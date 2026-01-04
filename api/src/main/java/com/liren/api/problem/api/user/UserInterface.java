package com.liren.api.problem.api.user;

import com.liren.api.problem.dto.user.UserBasicInfoDTO;
import com.liren.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "UserInterface", value = "user-service", path = "/user/inner")
public interface UserInterface {

    /**
     * 批量获取用户基本信息
     * @param userIds 用户ID列表
     */
    @GetMapping("/getBatchBasicInfo")
    Result<List<UserBasicInfoDTO>> getBatchUserBasicInfo(@RequestParam("userIds") List<Long> userIds);
}
