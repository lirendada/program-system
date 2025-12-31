package com.liren.common.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 用户上下文工具类
 * 基于 Alibaba TransmittableThreadLocal，实现全链路/异步线程的用户ID传递
 */
public class UserContext {

    // 核心：使用 TransmittableThreadLocal 而不是普通的 ThreadLocal
    private static final ThreadLocal<Long> USER_ID = new TransmittableThreadLocal<>();

    /**
     * 设置当前登录用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 清除上下文
     * 【非常重要】在请求结束时(拦截器的 afterCompletion)必须调用，防止内存泄漏
     */
    public static void remove() {
        USER_ID.remove();
    }
}