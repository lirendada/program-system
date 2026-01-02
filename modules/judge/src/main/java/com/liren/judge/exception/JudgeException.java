package com.liren.judge.exception;

import com.liren.common.core.exception.BizException;
import com.liren.common.core.result.ResultCode;

public class JudgeException extends BizException {
    public JudgeException(ResultCode resultCode) {
        super(resultCode);
    }
}
