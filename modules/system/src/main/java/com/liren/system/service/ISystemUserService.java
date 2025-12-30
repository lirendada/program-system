package com.liren.system.service;

import com.liren.system.dto.LoginDTO;
import com.liren.system.vo.LoginVO;


public interface ISystemUserService {

    LoginVO login(LoginDTO loginDTO);
}
