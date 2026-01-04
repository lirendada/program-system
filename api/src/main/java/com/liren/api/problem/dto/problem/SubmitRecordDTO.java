package com.liren.api.problem.dto.problem;

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
