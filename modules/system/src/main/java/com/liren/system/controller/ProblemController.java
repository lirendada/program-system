package com.liren.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liren.common.core.result.Result;
import com.liren.system.dto.ProblemAddDTO;
import com.liren.system.dto.ProblemQueryRequest;
import com.liren.system.service.IProblemService;
import com.liren.system.vo.ProblemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/problem")
@Tag(name = "题目管理API")
public class ProblemController {
    @Autowired
    private IProblemService problemService;

    @PostMapping("/add")
    @Operation(summary = "新增题目")
    public Result<Boolean> addProblem(@Validated @RequestBody ProblemAddDTO problemAddDTO) {
        return Result.success(problemService.addProblem(problemAddDTO));
    }

    @PostMapping("/list/page")
    @Operation(summary = "分页获取题目列表", description = "支持搜索，返回脱敏数据")
    public Result<Page<ProblemVO>> listProblemVOByPage(@RequestBody ProblemQueryRequest queryRequest) {
        // 1. 限制爬虫/恶意请求
        long size = queryRequest.getPageSize();
        if (size > 20) {
            queryRequest.setPageSize(20);
        }

        // 2. 调用 Service
        Page<ProblemVO> page = problemService.getProblemList(queryRequest);

        return Result.success(page);
    }
}
