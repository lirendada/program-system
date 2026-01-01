package com.liren.system.vo;

import lombok.Data;

import java.io.Serializable;

//TODO:完善swagger信息
@Data
public class ProblemTagVO implements Serializable {
    private Long tagId;
    private String tagName;
    private String tagColor;
}

