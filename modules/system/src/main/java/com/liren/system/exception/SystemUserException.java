package com.liren.system.exception;

import com.liren.common.core.exception.BizException;
import com.liren.common.core.result.ResultCode;

public class SystemUserException extends BizException {
    public SystemUserException(ResultCode resultCode) {
        super(resultCode);
    }
}
