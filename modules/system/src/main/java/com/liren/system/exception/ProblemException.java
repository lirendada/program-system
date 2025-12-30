package com.liren.system.exception;

import com.liren.common.core.exception.BizException;
import com.liren.common.core.result.ResultCode;

public class ProblemException extends BizException {
    public ProblemException(ResultCode resultCode) {
        super(resultCode);
    }
}