package com.liren.judge.sandbox.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.model.*;
import com.liren.common.core.constant.Constants;
import com.liren.judge.sandbox.CodeSandbox;
import com.liren.judge.sandbox.model.ExecuteCodeRequest;
import com.liren.judge.sandbox.model.ExecuteCodeResponse;
import com.liren.judge.sandbox.model.JudgeInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JavaDockerCodeSandbox implements CodeSandbox {

    @Autowired
    private DockerClient dockerClient;

    @Override
    // TODO:将常数等都抽出来放到常量类中
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();
        String containerId = null;

        try {
            // 1. 拉取镜像 (建议手动拉取，这里作为兜底)
            // dockerClient.pullImageCmd(Constants.IMAGE).exec(new PullImageResultCallback()).awaitCompletion();

            // 2. 创建容器
            log.info("创建容器中...");
            HostConfig hostConfig = new HostConfig();
            hostConfig.withMemory(100 * 1000 * 1000L); // 限制内存 100MB
            hostConfig.withCpuCount(1L); // 限制 CPU 1核

            CreateContainerCmd containerCmd = dockerClient.createContainerCmd(Constants.IMAGE)
                    .withHostConfig(hostConfig)
                    .withAttachStdin(true)
                    .withAttachStderr(true)
                    .withAttachStdout(true)
                    .withTty(true); // 保持后台运行

            CreateContainerResponse createContainerResponse = containerCmd.exec();
            containerId = createContainerResponse.getId();

            // 3. 启动容器
            dockerClient.startContainerCmd(containerId).exec();
            log.info("容器已启动, ID: {}", containerId);

            // 4. 将用户代码上传到容器
            // 需要先把 String 存为 Main.java 字节数组，然后打成 tar 包上传
            // 这里我们用一个辅助方法处理
            uploadFileToContainer(containerId, "Main.java", code.getBytes(StandardCharsets.UTF_8));

            // 5. 编译代码 (javac -encoding utf-8 Main.java)
            String compileCmd = "javac -encoding utf-8 Main.java";
            ExecMessage compileMsg = execCmd(containerId, compileCmd.split(" "));
            if (compileMsg.getExitValue() != 0) {
                // 编译失败
                return ExecuteCodeResponse.builder()
                        .status(3) // 3-编译错误
                        .message("编译错误: " + compileMsg.getErrorMessage())
                        .build();
            }

            // 6. 执行代码 (针对每个输入用例)
            List<String> outputList = new ArrayList<>();
            long maxTime = 0;

            for (String input : inputList) {
                // 构造运行命令: java -cp . Main
                // 注意：这里输入是通过 stdin 传进去的，docker-java 的 exec 比较难处理 stdin
                // 简单起见，我们把输入写入临时文件，然后用重定向： java -cp . Main < input.txt

                // 6.1 上传输入数据
                uploadFileToContainer(containerId, "input.txt", input.getBytes(StandardCharsets.UTF_8));

                // 6.2 执行
                String runCmd = "sh -c java -cp . Main < input.txt"; // 使用 sh -c 支持重定向
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                ExecMessage runMsg = execCmd(containerId, new String[]{"sh", "-c", "java -cp . Main < input.txt"});

                stopWatch.stop();
                long time = stopWatch.getLastTaskTimeMillis();
                maxTime = Math.max(maxTime, time);

                if (runMsg.getExitValue() != 0) {
                    return ExecuteCodeResponse.builder()
                            .status(2) // 2-运行错误
                            .message("运行错误: " + runMsg.getErrorMessage())
                            .build();
                }
                outputList.add(runMsg.getMessage().trim()); // 收集输出
            }

            // 7. 封装结果
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setTime(maxTime);
            judgeInfo.setMemory(0L); // TODO:Docker 较难精确获取每次运行内存，暂存0，后续可优化

            return ExecuteCodeResponse.builder()
                    .status(1) // 1-正常
                    .outputList(outputList)
                    .judgeInfo(judgeInfo)
                    .build();

        } catch (Exception e) {
            log.error("沙箱执行异常", e);
            return ExecuteCodeResponse.builder().status(4).message(e.getMessage()).build();
        } finally {
            // 8. 销毁容器 (非常重要！否则服务器内存会炸)
            if (containerId != null) {
                try {
                    dockerClient.stopContainerCmd(containerId).exec();
                    dockerClient.removeContainerCmd(containerId).exec();
                    log.info("容器已销毁: {}", containerId);
                } catch (Exception e) {
                    log.error("销毁容器失败", e);
                }
            }
        }
    }

    // === 辅助方法 ===

    /**
     * 将文件内容上传到容器 (解决远程 Docker 文件传输问题)
     */
    private void uploadFileToContainer(String containerId, String fileName, byte[] content) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             TarArchiveOutputStream tar = new TarArchiveOutputStream(bos)) {

            TarArchiveEntry entry = new TarArchiveEntry(fileName);
            entry.setSize(content.length);
            tar.putArchiveEntry(entry);
            tar.write(content);
            tar.closeArchiveEntry();
            tar.finish(); // 必须 finish

            // 上传 tar 流
            dockerClient.copyArchiveToContainerCmd(containerId)
                    .withTarInputStream(new ByteArrayInputStream(bos.toByteArray()))
                    .withRemotePath("/") // 放到根目录
                    .exec();
        }
    }

    /**
     * 执行命令辅助类
     */
    @Data
    private static class ExecMessage {
        private int exitValue;
        private String message;
        private String errorMessage;
    }

    /**
     * 在容器内执行命令
     */
    private ExecMessage execCmd(String containerId, String[] cmd) {
        ExecMessage result = new ExecMessage();
        final StringBuilder stdout = new StringBuilder();
        final StringBuilder stderr = new StringBuilder();

        try {
            // 1. 创建 Exec
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd(cmd)
                    .exec();

            // 2. 启动执行并等待
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame frame) {
                            if (frame.getStreamType() == StreamType.STDERR) {
                                stderr.append(new String(frame.getPayload()));
                            } else {
                                stdout.append(new String(frame.getPayload()));
                            }
                        }
                    }).awaitCompletion(5, TimeUnit.SECONDS); // 5秒超时

            // 3. 获取退出码
            InspectExecResponse response = dockerClient.inspectExecCmd(execCreateCmdResponse.getId()).exec();
            result.setExitValue(response.getExitCodeLong().intValue());
            result.setMessage(stdout.toString());
            result.setErrorMessage(stderr.toString());

        } catch (Exception e) {
            result.setExitValue(-1);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }
}