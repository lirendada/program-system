package com.liren.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.liren.common.core.base.BaseTimeEntity;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("tb_test_case")
public class TestCaseEntity extends BaseTimeEntity {
    @TableId(value = "case_id", type = IdType.ASSIGN_ID)
    private Long caseId;

    private Long problemId;

    private String input; // 输入数据

    private String output; // 期望输出
}