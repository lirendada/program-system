package com.liren.api.problem.dto.user;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserBasicInfoDTO implements Serializable {
    private Long id;
    private String nickname;
    private String avatar;
}