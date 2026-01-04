package com.liren.user.service;

import com.liren.api.problem.dto.user.UserBasicInfoDTO;
import com.liren.common.core.result.Result;
import com.liren.user.dto.UserLoginDTO;

import java.util.List;

public interface IUserService {
    String login(UserLoginDTO userLoginDTO);

    /**
     * 批量获取用户信息
     */
    List<UserBasicInfoDTO> getBatchUserBasicInfo(List<Long> userIds);
}
