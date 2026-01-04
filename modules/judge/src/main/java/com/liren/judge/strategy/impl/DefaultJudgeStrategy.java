package com.liren.judge.strategy.impl;

import com.liren.api.problem.dto.problem.ProblemSubmitUpdateDTO;
import com.liren.api.problem.dto.problem.TestCaseDTO;
import com.liren.common.core.enums.JudgeResultEnum;
import com.liren.common.core.enums.SandboxRunStatusEnum;
import com.liren.common.core.enums.SubmitStatusEnum;
import com.liren.judge.sandbox.model.ExecuteCodeResponse;
import com.liren.judge.sandbox.model.JudgeInfo;
import com.liren.judge.strategy.JudgeContext;
import com.liren.judge.strategy.JudgeStrategy;

import java.util.List;

public class DefaultJudgeStrategy implements JudgeStrategy {

    @Override
    public ProblemSubmitUpdateDTO doJudge(JudgeContext judgeContext) {
        ProblemSubmitUpdateDTO updateDTO = new ProblemSubmitUpdateDTO();
        Long submitId = judgeContext.getSubmitId();
        ExecuteCodeResponse executeResponse = judgeContext.getExecuteCodeResponse();
        List<TestCaseDTO> testCases = judgeContext.getTestCases();

        updateDTO.setSubmitId(submitId);
        updateDTO.setStatus(SubmitStatusEnum.SUCCEED.getCode()); // 只要沙箱跑完了，对于提交记录的生命周期来说就是“完成”了

        // 将沙箱的 status 转换为枚举，来判断判题结果
        SandboxRunStatusEnum runStatus = SandboxRunStatusEnum.getByCode(executeResponse.getStatus());

        if (runStatus == null) {
            // 防御性编程
            updateDTO.setJudgeResult(JudgeResultEnum.SYSTEM_ERROR.getCode());
            updateDTO.setErrorMessage("沙箱返回了未知的状态码: " + executeResponse.getStatus());
        }

        // 情况 A: 编译错误（CE）
        else if (runStatus == SandboxRunStatusEnum.COMPILE_ERROR) {
            updateDTO.setJudgeResult(JudgeResultEnum.COMPILE_ERROR.getCode());
            updateDTO.setErrorMessage(executeResponse.getMessage()); // 编译报错详情
        }

        // 情况 B: 运行错误（RE）
        else if (runStatus == SandboxRunStatusEnum.RUNTIME_ERROR) {
            updateDTO.setJudgeResult(JudgeResultEnum.RUNTIME_ERROR.getCode());
            updateDTO.setErrorMessage(executeResponse.getMessage()); // 运行报错详情
        }

        // 情况 C: 正常运行 (可能是 AC, WA, TLE, MLE)
        else if (runStatus == SandboxRunStatusEnum.NORMAL) {
            // 此时代码跑通了，需要检查：输出对不对、是否超时超内存
            processNormalResult(executeResponse, updateDTO, testCases);
        }

        // 情况 D: 沙箱系统错误
        else {
            updateDTO.setJudgeResult(JudgeResultEnum.SYSTEM_ERROR.getCode());
            updateDTO.setErrorMessage("沙箱系统异常: " + executeResponse.getMessage());
        }

        // ------------------------------------------
        // 4. 填充性能数据 (Time/Memory)
        // ------------------------------------------
        JudgeInfo judgeInfo = executeResponse.getJudgeInfo();
        if (judgeInfo != null) {
            updateDTO.setTimeCost(judgeInfo.getTime() != null ? judgeInfo.getTime().intValue() : 0);
            updateDTO.setMemoryCost(judgeInfo.getMemory() != null ? judgeInfo.getMemory().intValue() : 0);
        }

        return updateDTO;
    }

    /**
     * 处理沙箱正常运行后的结果比对
     * @param response 沙箱返回结果
     * @param updateDTO 待更新对象
     * @param testCases 标准测试用例列表 (这里直接用 List，不用 ProblemDTO)
     */
    private void processNormalResult(ExecuteCodeResponse response,
                                     ProblemSubmitUpdateDTO updateDTO,
                                     List<TestCaseDTO> testCases) {
        List<String> userOutputs = response.getOutputList();

        // 防御性检查：如果没有输出或者输出数量不对
        if (userOutputs == null || userOutputs.size() != testCases.size()) {
            updateDTO.setJudgeResult(JudgeResultEnum.WRONG_ANSWER.getCode());
            updateDTO.setErrorMessage("用户输出数量与用例数量不一致");
            return;
        }

        // 逐个比对
        for (int i = 0; i < testCases.size(); i++) {
            // 注意：通常 OJ 需要去除行末空格和换行符来比较
            // Hutool 的 StrUtil.trim() 或 String.trim() 都可以
            String userOut = userOutputs.get(i) == null ? "" : userOutputs.get(i).trim();
            String stdOut = testCases.get(i).getOutput() == null ? "" : testCases.get(i).getOutput().trim();

            if (!userOut.equals(stdOut)) {
                // 只要有一个对不上，就是 WA
                updateDTO.setJudgeResult(JudgeResultEnum.WRONG_ANSWER.getCode());
                // 可以在 errorMessage 里记录具体的 diff 信息，方便前端展示
                // updateDTO.setErrorMessage("Case " + (i+1) + " failed.");
                return;
            }
        }

        // 全部通过 -> AC
        updateDTO.setJudgeResult(JudgeResultEnum.ACCEPTED.getCode());
    }
}
