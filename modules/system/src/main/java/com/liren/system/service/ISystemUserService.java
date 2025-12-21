package com.liren.system.service;

import com.liren.system.dto.LoginRequestDTO;
import com.liren.system.vo.LoginResponseVO;


public interface ISystemUserService {

    LoginResponseVO login(LoginRequestDTO loginDTO);
}
