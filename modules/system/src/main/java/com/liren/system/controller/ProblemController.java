package com.liren.system.controller;

import com.liren.common.core.result.Result;
import com.liren.system.dto.ProblemDTO;
import com.liren.system.service.IProblemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/problem")
public class ProblemController {
    @Autowired
    private IProblemService problemService;

    @PostMapping("/add")
    @Operation(summary = "新增题目")
    public Result<Boolean> addProblem(@Validated @RequestBody ProblemDTO problemDTO) {
        return Result.success(problemService.addProblem(problemDTO));
    }


}
