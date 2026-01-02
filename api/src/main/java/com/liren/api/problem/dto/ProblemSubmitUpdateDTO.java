package com.liren.api.problem.dto;

import lombok.Data;

/**
 * 题目提交后交给判题机的更新信息
 */
@Data
public class ProblemSubmitUpdateDTO {
    private Long submitId;

    /**
     * 状态: 20-判题中, 30-成功, 40-失败
     */
    private Integer status;

    /**
     * 结果: 1-AC, 2-WA, 3-TLE, 4-MLE, 5-RE, 6-CE
     */
    private Integer judgeResult;

    private Integer timeCost; // 耗时 (ms)
    private Integer memoryCost; // 内存消耗 (KB)
    private String errorMessage; // 错误信息 / 判题详情 JSON
}
