package com.liren.system.dto;

import com.liren.common.core.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 题目查询请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProblemQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目 ID (精确搜索)
     */
    private Long problemId;

    /**
     * 标题/内容关键词 (模糊搜索)
     */
    private String keyword;

    /**
     * 难度 (精确搜索: 1-简单 2-中等 3-困难)
     */
    private Integer difficulty;

    /**
     * 标签
     * 例如: ["数组", "动态规划"]
     */
    private List<String> tags;
}