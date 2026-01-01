package com.liren.system.vo;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.liren.system.entity.ProblemEntity;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

//TODO：补充swagger信息
@Data
public class ProblemVO implements Serializable {
    // 必须转 String，防止前端精度丢失
    @JsonSerialize(using = ToStringSerializer.class)
    private Long problemId;

    private String title;

    /**
     * 难度：1-简单 2-中等 3-困难
     * 前端拿到数字后，自己映射成文字或颜色
     */
    private Integer difficulty;

    /**
     * 标签列表
     */
    private List<ProblemTagVO> tags;

    /**
     * 提交数
     */
    private Integer submitNum;

    /**
     * 通过数
     */
    private Integer acceptedNum;

    /**
     * 状态：0-隐藏 1-正常
     */
    private Integer status;

    // 列表页通常需要展示创建时间
    private Date createTime;

    /**
     * 静态转换方法
     */
    public static ProblemVO objToVo(ProblemEntity problemEntity) {
        if (problemEntity == null) {
            return null;
        }
        ProblemVO problemVO = new ProblemVO();
        // BeanUtils 会自动匹配字段名：problemId -> problemId, title -> title
        BeanUtil.copyProperties(problemEntity, problemVO);
        return problemVO;
    }
}