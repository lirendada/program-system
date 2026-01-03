package com.liren.judge.strategy;

import com.liren.api.problem.dto.TestCaseDTO;
import com.liren.judge.sandbox.model.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 判题上下文：用于在策略中传递信息（这个袋子里装着：沙箱跑完的结果 + 题目的标准答案 + 提交记录ID）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeContext {
    private Long submitId;

    /**
     * 沙箱运行的结果 (Output, Status, Message, JudgeInfo)
     */
    private ExecuteCodeResponse executeCodeResponse;

    /**
     * 题目的标准测试用例 (Input, Output)
     */
    private List<TestCaseDTO> testCases;

    /**
     * 提交的语言 (未来可能根据语言选策略)
     */
    private String language;
}
