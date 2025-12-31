package com.liren.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@RefreshScope // 更新配置后自动刷新
@ConfigurationProperties("gateway.auth")  // 指定属性
public class AuthWhiteList {
    private List<String> whitelist = new ArrayList<>();
}
