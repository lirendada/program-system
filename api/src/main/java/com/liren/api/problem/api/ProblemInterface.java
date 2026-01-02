package com.liren.api.problem.api;

import com.liren.api.problem.dto.ProblemSubmitUpdateDTO;
import com.liren.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * contextId: 用于区分同一个服务名的不同 Client (防止Bean冲突)
 * name: 目标服务名 (oj-problem-service)
 * path: 统一前缀 (建议内部接口加 /inner 区分)
 */
@FeignClient(contextId = "ProblemInterface",name = "problem-service", path = "/problem/inner")
public interface ProblemInterface {

    @PostMapping("/submit/update")
    Result<Boolean> updateSubmitResult(@RequestBody ProblemSubmitUpdateDTO problemSubmitUpdateDTO);

}
