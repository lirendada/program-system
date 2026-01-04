package com.liren.problem;

import com.liren.api.problem.api.contest.ContestInterface;
import com.liren.api.problem.api.user.UserInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(clients = {ContestInterface.class, UserInterface.class})
@SpringBootApplication
public class ProblemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProblemApplication.class, args);
    }
}