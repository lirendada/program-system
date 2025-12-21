package com.liren.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "用户名不能为空")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    private String password;
}
