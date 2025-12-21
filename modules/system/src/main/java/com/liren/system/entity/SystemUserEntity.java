package com.liren.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liren.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_system_user")
public class SystemUserEntity extends BaseEntity {
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId; // 主键ID，使用雪花算法

    private String userAccount;
    private String password;
    private String nickName;
}