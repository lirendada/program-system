package com.liren.judge.sandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    /**
     * 输出用例列表 (对应 inputList)
     */
    private List<String> outputList;

    /**
     * 执行信息 (如: 编译错误信息)
     */
    private String message;

    /**
     * 执行状态 (对应SandboxRunStatusEnum，1-正常, 2-运行错误, 3-编译错误, 4-系统错误)
     */
    private Integer status;

    /**
     * 判题信息 (时间、内存)
     */
    private JudgeInfo judgeInfo;
}