package com.liren.problem.controller;

import com.liren.api.problem.api.ProblemInterface;
import com.liren.api.problem.dto.ProblemSubmitUpdateDTO;
import com.liren.common.core.result.Result;
import com.liren.problem.dto.ProblemAddDTO;
import com.liren.problem.service.IProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/problem/inner") // 和 FeignClient 的 path 对应
@Tag(name = "题目微服务内部接口")
public class ProblemInnerController implements ProblemInterface {
    @Autowired
    private IProblemService problemService;

    @Override
    @Operation(
            summary = "更新提交结果",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "更新提交结果的请求体信息",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemSubmitUpdateDTO.class)
                    )
            )
    )
    public Result<Boolean> updateSubmitResult(@RequestBody ProblemSubmitUpdateDTO problemSubmitUpdateDTO) {
        return Result.success(problemService.updateSubmitResult(problemSubmitUpdateDTO));
    }
}
