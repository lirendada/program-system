package com.liren.judge.mq;

import com.liren.api.problem.api.ProblemInterface;
import com.liren.api.problem.dto.ProblemSubmitUpdateDTO;
import com.liren.api.problem.dto.SubmitRecordDTO;
import com.liren.api.problem.dto.TestCaseDTO;
import com.liren.common.core.constant.Constants;
import com.liren.common.core.result.Result;
import com.liren.common.core.result.ResultCode;
import com.liren.judge.exception.JudgeException;
import com.liren.judge.sandbox.CodeSandbox;
import com.liren.judge.sandbox.model.ExecuteCodeRequest;
import com.liren.judge.sandbox.model.ExecuteCodeResponse;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JudgeReceiver {
    @Autowired
    private CodeSandbox codeSandbox;

    @Autowired
    private ProblemInterface problemService;

    @RabbitListener(queues = Constants.JUDGE_QUEUE, ackMode = "MANUAL")
    public void receiveJudgeMessage(Long submitId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("接收到判题任务, submitId: {}", submitId);

        try {
            // ------------------------------------------
            // 1. 准备数据: 查提交记录(代码)、查测试用例
            // ------------------------------------------
            Result<SubmitRecordDTO> submitRecord = problemService.getSubmitRecord(submitId);
            if(submitRecord == null || submitRecord.getData() == null) {
                throw new JudgeException(ResultCode.SUBMIT_RECORD_NOT_FOUND);
            }
            String userCode = submitRecord.getData().getCode();
            String language = submitRecord.getData().getLanguage();
            Long problemId = submitRecord.getData().getProblemId();

            Result<List<TestCaseDTO>> testCaseResult = problemService.getTestCases(problemId); // 拿到测试用例
            List<TestCaseDTO> testCases = testCaseResult.getData();

            if (testCases == null || testCases.isEmpty()) {
                throw new JudgeException(ResultCode.TEST_CASE_NOT_FOUND);
            }

            // 提取输入列表给沙箱
            List<String> inputList = testCases.stream().map(TestCaseDTO::getInput).collect(Collectors.toList());

            // ------------------------------------------
            // 2. 调用沙箱执行
            // ------------------------------------------
            ExecuteCodeRequest executeRequest = ExecuteCodeRequest.builder()
                    .code(userCode)
                    .language(language)
                    .inputList(inputList)
                    .build();

            log.info("调用 Docker 沙箱...");
            ExecuteCodeResponse executeResponse = codeSandbox.executeCode(executeRequest);
            log.info("沙箱执行结束, 状态: {}", executeResponse.getStatus());

            // ------------------------------------------
            // 3. 结果比对 (Judge Logic)
            // ------------------------------------------
            int finalStatus = 30; // 默认成功
            int judgeResult = 0;  // 0-AC

            // 如果沙箱本身报错 (编译失败/运行错误)
            if (executeResponse.getStatus() != 1) {
                judgeResult = (executeResponse.getStatus() == 3) ? 5 : 2; // 5-CE, 2-RE (假设)
                // 如果是编译错误，把错误信息回写
            } else {
                // 逐个比对输出
                List<String> outputList = executeResponse.getOutputList();
                // 确保输出数量一致
                if (outputList.size() != testCases.size()) {
                    judgeResult = 2; // WA (输出数量不对)
                } else {
                    for (int i = 0; i < testCases.size(); i++) {
                        String sandboxOutput = outputList.get(i).trim(); // 去掉末尾换行
                        String standardOutput = testCases.get(i).getOutput().trim();
                        if (!sandboxOutput.equals(standardOutput)) {
                            judgeResult = 1; // 1-WA (答案错误)
                            log.info("WA: 预期[{}], 实际[{}]", standardOutput, sandboxOutput);
                            break;
                        }
                    }
                }
            }

            // ------------------------------------------
            // 4. 回写数据库
            // ------------------------------------------
            ProblemSubmitUpdateDTO updateDTO = new ProblemSubmitUpdateDTO();
            updateDTO.setSubmitId(submitId);
            updateDTO.setStatus(finalStatus);
            updateDTO.setJudgeResult(judgeResult);
            // 回写沙箱返回的时间/内存
            if (executeResponse.getJudgeInfo() != null) {
                updateDTO.setTimeCost(Math.toIntExact(executeResponse.getJudgeInfo().getTime()));
                // memory...
            }
            updateDTO.setErrorMessage(executeResponse.getMessage());

            Result<Boolean> updateResult = problemService.updateSubmitResult(updateDTO);
            if (Boolean.TRUE.equals(updateResult.getData())) {
                log.info("判题完成并回写成功!");
            } else {
                log.error("回写失败");
            }

            // 4. 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("接收到判题任务失败, submitId: {}", submitId, e);
            try {
                // 失败重试或丢弃 (根据业务需求，这里先 requeue=false 丢弃防止死循环)
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
