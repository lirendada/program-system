package com.liren.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubmitStatusEnum {
    // 10-等待判题 (排队中)
    WAITING(10, "等待判题"),

    // 20-判题中 (正在跑代码)
    JUDGING(20, "判题中"),

    // 30-判题完成 (出结果了，无论是AC还是WA)
    SUCCEED(30, "判题完成"),

    // 40-判题失败 (系统崩了，或者无法运行)
    FAILED(40, "判题失败");

    private final Integer code;
    private final String message;
}