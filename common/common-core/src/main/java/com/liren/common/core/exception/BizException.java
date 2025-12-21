package com.liren.common.core.exception;

import com.liren.common.core.result.ResultCode;

public class BizException extends BaseException {

    public BizException(ResultCode resultCode) {
        super(resultCode);
    }

    public BizException(int code, String message) {
        super(code, message);
    }
}
