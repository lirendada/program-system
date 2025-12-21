package com.liren.common.web.config;

import com.liren.common.web.advice.ExceptionAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionConfig {
    @Bean
    @ConditionalOnMissingBean(ExceptionAdvice.class)
    public ExceptionAdvice exceptionAdvice() {
        return new ExceptionAdvice();
    }
}
