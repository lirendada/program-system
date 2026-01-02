package com.liren.judge.mq;

import com.liren.api.problem.api.ProblemInterface;
import com.liren.api.problem.dto.ProblemSubmitUpdateDTO;
import com.liren.common.core.constant.Constants;
import com.liren.common.core.result.Result;
import com.liren.common.core.result.ResultCode;
import com.liren.judge.exception.JudgeException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JudgeReceiver {
    @Autowired
    private ProblemInterface problemService;

    @RabbitListener(queues = Constants.JUDGE_QUEUE, ackMode = "MANUAL")
    public void receiveJudgeMessage(Long submitId, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("接收到判题任务, submitId: {}", submitId);

        try {
            // === 模拟判题过程 ===
            // 1. 根据 submitId 去查 db (这一步需要 Feign 调用 oj-problem，目前还没写，先 Mock)
            log.info("正在调用沙箱进行判题...");
            Thread.sleep(2000); // 模拟耗时

            // 2. 得到结果: AC
            log.info("判题完成，结果: AC");

            // 3. 更新数据库状态 (也需要调 Feign，先 Mock)
            log.info("开始回写判题结果...");
            ProblemSubmitUpdateDTO updateDTO = new ProblemSubmitUpdateDTO();
            updateDTO.setSubmitId(submitId);
            updateDTO.setStatus(30); // 30-结束
            updateDTO.setJudgeResult(1); // 1-AC
            updateDTO.setTimeCost(10); // 10ms
            updateDTO.setMemoryCost(1024); // 1MB

            Result<Boolean> result = problemService.updateSubmitResult(updateDTO);
            if(result.getData() != null && result.getData() == true) {
                log.info("回写判题结果成功, submitId: {}, result: {}", submitId, result);
            } else {log.error("回写判题结果失败, submitId: {}, result: {}", submitId, result);
                throw new JudgeException(ResultCode.UPDATE_PROBLEM_ERROR);
            }

            // 4. 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("接收到判题任务失败, submitId: {}", submitId, e);
            try {
                // 失败重试或丢弃 (根据业务需求，这里先 requeue=false 丢弃防止死循环)
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
