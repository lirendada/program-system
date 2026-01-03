package com.liren.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SandboxRunStatusEnum {
    // 1-程序正常运行完毕 (可能结果不对，但程序没崩)
    NORMAL(1, "正常运行"),

    // 2-程序自己崩了 (数组越界、除零异常等)
    RUNTIME_ERROR(2, "运行错误"),

    // 3-编译都没过 (javac 报错)
    COMPILE_ERROR(3, "编译错误"),

    // 4-沙箱自己崩了 (Docker连接失败等)
    SYSTEM_ERROR(4, "系统错误");

    private final Integer code;
    private final String message;

    public static SandboxRunStatusEnum getByCode(Integer code) {
        for (SandboxRunStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}