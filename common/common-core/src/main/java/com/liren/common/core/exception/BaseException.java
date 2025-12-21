package com.liren.common.core.exception;

import com.liren.common.core.result.ResultCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    /**
     * 业务错误码
     */
    protected final int code;

    protected BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    protected BaseException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}