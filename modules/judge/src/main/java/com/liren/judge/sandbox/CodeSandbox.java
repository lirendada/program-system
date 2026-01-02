package com.liren.judge.sandbox;

import com.liren.judge.sandbox.model.ExecuteCodeRequest;
import com.liren.judge.sandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 */
public interface CodeSandbox {
    /**
     * 执行代码
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}