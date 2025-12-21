package com.liren.system.service.impl;

import com.liren.system.dto.LoginRequestDTO;
import com.liren.system.mapper.SystemUserMapper;
import com.liren.system.service.ISystemUserService;
import com.liren.system.vo.LoginResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemUserServiceImpl implements ISystemUserService {
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public LoginResponseVO login(LoginRequestDTO loginDTO) {
        return null;
    }

}
