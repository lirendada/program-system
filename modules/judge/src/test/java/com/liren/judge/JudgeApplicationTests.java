package com.liren.judge;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PingCmd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JudgeApplicationTests {
    @Autowired
    private DockerClient dockerClient;

    @Test
    public void testDockerPing() {
        PingCmd pingCmd = dockerClient.pingCmd();
        pingCmd.exec();
        System.out.println("Docker is running.");
    }
}
