package com.liren.api.problem.api;

import com.liren.api.problem.dto.ProblemSubmitUpdateDTO;
import com.liren.api.problem.dto.SubmitRecordDTO;
import com.liren.api.problem.dto.TestCaseDTO;
import com.liren.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * contextId: 用于区分同一个服务名的不同 Client (防止Bean冲突)
 * name: 目标服务名 (oj-problem-service)
 * path: 统一前缀 (建议内部接口加 /inner 区分)
 */
@FeignClient(contextId = "ProblemInterface",name = "problem-service", path = "/problem/inner")
public interface ProblemInterface {

    @PostMapping("/submit/update")
    Result<Boolean> updateSubmitResult(@RequestBody ProblemSubmitUpdateDTO problemSubmitUpdateDTO);

    @GetMapping("/test-case/{problemId}")
    Result<List<TestCaseDTO>> getTestCases(@PathVariable("problemId") Long problemId);

    @GetMapping("/submit/{submitId}")
    Result<SubmitRecordDTO> getSubmitRecord(@PathVariable("submitId") Long submitId);
}
