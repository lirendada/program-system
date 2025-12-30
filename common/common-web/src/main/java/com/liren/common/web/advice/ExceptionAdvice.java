package com.liren.common.web.advice;

import com.liren.common.core.exception.BizException;
import com.liren.common.core.result.Result;
import com.liren.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * ==================== 业务异常 ====================
     */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生业务异常：{}.", requestURI, e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * ==================== 参数异常 ====================
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生参数不合法异常.", requestURI, e);
        return Result.fail(
                ResultCode.PARAM_ILLEGAL.getCode(),
                ResultCode.PARAM_ILLEGAL.getMessage()
        );
    }

    @ExceptionHandler(NumberFormatException.class)
    public Result<?> handleNumberFormatException(NumberFormatException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生参数格式错误.", requestURI, e);
        return Result.fail(
                ResultCode.PARAM_FORMAT_ERROR.getCode(),
                ResultCode.PARAM_FORMAT_ERROR.getMessage()
        );
    }

    /**
     * 处理 JSON 解析错误（如参数类型不匹配、JSON 格式错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}', 参数格式错误.", requestURI, e);
        return Result.fail(ResultCode.PARAM_FORMAT_ERROR.getCode(), "参数格式错误：请检查数值范围或类型");
    }

    /**
     * ==================== 程序缺陷类异常 ====================
     */

    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生空指针异常.", requestURI, e);
        return Result.fail(
                ResultCode.NULL_POINTER.getCode(),
                ResultCode.NULL_POINTER.getMessage()
        );
    }

    @ExceptionHandler(ClassCastException.class)
    public Result<?> handleClassCastException(ClassCastException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生类型转换异常.", requestURI, e);
        return Result.fail(
                ResultCode.CLASS_CAST.getCode(),
                ResultCode.CLASS_CAST.getMessage()
        );
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public Result<?> handleIndexOutOfBoundsException(IndexOutOfBoundsException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生数据索引越界异常.", requestURI, e);
        return Result.fail(
                ResultCode.INDEX_OUT_OF_BOUNDS.getCode(),
                ResultCode.INDEX_OUT_OF_BOUNDS.getMessage()
        );
    }

    /**
     * ==================== IO / 外部资源异常 ====================
     */

    @ExceptionHandler(IOException.class)
    public Result<?> handleIOException(IOException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生IO 操作异常.", requestURI, e);
        return Result.fail(
                ResultCode.IO_ERROR.getCode(),
                ResultCode.IO_ERROR.getMessage()
        );
    }

    /**
     * ==================== 运行时异常兜底 ====================
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生运行时异常.", requestURI, e);
        return Result.fail(
                ResultCode.RUNTIME_ERROR.getCode(),
                ResultCode.RUNTIME_ERROR.getMessage()
        );
    }

    /**
     * ==================== 最终兜底异常 ====================
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统内部错误.", requestURI, e);
        return Result.fail(
                ResultCode.SYSTEM_ERROR.getCode(),
                ResultCode.SYSTEM_ERROR.getMessage()
        );
    }
}