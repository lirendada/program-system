package com.liren.contest;

import com.liren.api.problem.api.problem.ProblemInterface;
import com.liren.api.problem.api.user.UserInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(clients = {ProblemInterface.class, UserInterface.class})
@SpringBootApplication
public class ContestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContestApplication.class, args);
    }
}