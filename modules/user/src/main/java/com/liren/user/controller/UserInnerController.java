package com.liren.user.controller;

import com.liren.api.problem.api.user.UserInterface;
import com.liren.api.problem.dto.user.UserBasicInfoDTO;
import com.liren.common.core.result.Result;
import com.liren.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/inner")
@Tag(name = "用户内部接口")
public class UserInnerController implements UserInterface {
    @Autowired
    private IUserService userService;

    @Override
    @Operation(summary = "批量获取用户基本信息")
    public Result<List<UserBasicInfoDTO>> getBatchUserBasicInfo(List<Long> userIds) {
        return Result.success(userService.getBatchUserBasicInfo(userIds));
    }
}
