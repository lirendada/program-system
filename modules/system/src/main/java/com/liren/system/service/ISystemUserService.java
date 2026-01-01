package com.liren.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liren.system.dto.SystemUserLoginDTO;
import com.liren.system.entity.SystemUserEntity;
import com.liren.system.vo.SystemUserLoginVO;


public interface ISystemUserService extends IService<SystemUserEntity> {

    SystemUserLoginVO login(SystemUserLoginDTO systemUserLoginDTO);
}
