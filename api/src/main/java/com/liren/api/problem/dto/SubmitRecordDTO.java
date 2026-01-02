package com.liren.api.problem.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubmitRecordDTO implements Serializable {
    private Long submitId;
    private Long problemId;
    private Long userId;
    private String language;
    private String code;
}
