package com.liren.judge.strategy;

import com.liren.api.problem.dto.ProblemSubmitUpdateDTO;
import com.liren.judge.strategy.impl.DefaultJudgeStrategy;
import org.springframework.stereotype.Component;

@Component
public class JudgeManager {

    /**
     * 智能选策略并执行
     */
    public ProblemSubmitUpdateDTO doJudge(JudgeContext judgeContext) {
        String language = judgeContext.getLanguage();

        // 【扩展点】未来可以在这里根据 language 选择不同的策略
        // if ("java".equals(language)) { return new JavaJudgeStrategy().doJudge(judgeContext); }

        // 目前默认都走 Default
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();

        return judgeStrategy.doJudge(judgeContext);
    }
}
