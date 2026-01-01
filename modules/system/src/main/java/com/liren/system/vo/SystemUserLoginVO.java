package com.liren.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO：补充swagger信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemUserLoginVO {
    private Long userId;
    private String userAccount;
    private String nickName;
}
