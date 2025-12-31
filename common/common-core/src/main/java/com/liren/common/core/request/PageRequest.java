package com.liren.common.core.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    private final static Long serialVersionUID = 1L;

    /**
     * 当前页号 (默认1)
     */
    private long current = 1;

    /**
     * 页面大小 (默认10)
     */
    private long pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序 (ascend / descend)
     */
    private String sortOrder;
}
