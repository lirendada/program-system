package com.liren.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liren.system.dto.ProblemAddDTO;
import com.liren.system.dto.ProblemQueryRequest;
import com.liren.system.vo.ProblemVO;

public interface IProblemService {
    // 添加题目
    boolean addProblem(ProblemAddDTO problemAddDTO);

    // 查看题目列表
    Page<ProblemVO> getProblemList(ProblemQueryRequest queryRequest);
}
