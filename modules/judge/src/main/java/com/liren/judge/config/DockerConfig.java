package com.liren.judge.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DockerConfig {
    @Value("${oj.judge.docker.host}")
    private String dockerHost;

    @Bean
    public DockerClient dockerClient() {
        // 1. 配置 Docker 连接信息
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();

        // 2. 显式配置 HTTP 客户端 (使用我们引入的 httpclient5，避免使用默认的 Jersey)
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100) // 最大连接数
                .connectionTimeout(Duration.ofSeconds(30)) // 连接超时
                .responseTimeout(Duration.ofSeconds(45)) // 响应超时
                .build();

        // 3. 创建 DockerClient
        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient) // <--- 关键点：指定 HttpClient
                .build();
    }
}
