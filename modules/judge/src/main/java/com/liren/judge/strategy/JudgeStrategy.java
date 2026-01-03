package com.liren.judge.strategy;

import com.liren.api.problem.dto.ProblemSubmitUpdateDTO;

public interface JudgeStrategy {

    /**
     * 执行判题（所有裁判都必须遵守的规则：给素材，判结果）
     * @param judgeContext 上下文素材
     * @return 判题结果 (用于更新数据库)
     */
    ProblemSubmitUpdateDTO doJudge(JudgeContext judgeContext);
}
