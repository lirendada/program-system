package com.liren.system.controller;

import com.liren.common.core.result.Result;
import com.liren.system.dto.LoginRequestDTO;
import com.liren.system.service.ISystemUserService;
import com.liren.system.vo.LoginResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/user")
public class SystemUserController {
    @Autowired
    private ISystemUserService systemUserService;

    @PostMapping("/login")
    public Result<LoginResponseVO> login(@RequestBody LoginRequestDTO loginDTO) {
        return Result.success(systemUserService.login(loginDTO));
    }


}
