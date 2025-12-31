package com.liren.common.web.config;

import com.liren.common.web.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 UserInterceptor，拦截所有路径
        // 这里的 /** 意味着所有进入 Controller 的请求都会先经过这个拦截器
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/**");
    }
}