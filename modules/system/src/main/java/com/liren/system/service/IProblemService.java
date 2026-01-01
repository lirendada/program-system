package com.liren.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liren.system.dto.ProblemAddDTO;
import com.liren.system.dto.ProblemQueryRequest;
import com.liren.system.entity.ProblemEntity;
import com.liren.system.vo.ProblemVO;

public interface IProblemService extends IService<ProblemEntity> {
    // 添加题目
    boolean addProblem(ProblemAddDTO problemAddDTO);

    // 查看题目列表
    Page<ProblemVO> getProblemList(ProblemQueryRequest queryRequest);
}
