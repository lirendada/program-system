package com.liren.common.core.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BaseEntity extends BaseTimeEntity {
    /** 创建者 */
    private Long createBy;

    /** 更新者 */
    private Long updateBy;
}