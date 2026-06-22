package com.hnhegui.hc.mq.config;

import com.hc.framework.common.util.JsonUtils;
import com.hc.framework.rocketmq.core.BaseMqMessage;
import com.hc.framework.rocketmq.core.TransactionLogStore;
import com.hnhegui.hc.entity.mq.MqTransactionLog;
import com.hnhegui.hc.mapper.mq.MqTransactionLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * MQ 事务消息日志存储实现。
 *
 * <p>在本地 DB 事务内持久化 {@link BaseMqMessage}，供通用 Checker 回查时判断
 * 本地事务是否已提交。</p>
 *
 * <p><b>使用前提</b>：save() 调用必须与业务操作在同一个数据库事务内。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqTransactionLogStore implements TransactionLogStore {

    private final MqTransactionLogMapper mapper;

    @Override
    public void save(BaseMqMessage message) {
        MqTransactionLog entity = new MqTransactionLog();
        entity.setMsgId(message.getMsgId());
        entity.setTopic(message.getTopic());
        entity.setTag(message.getTag());
        entity.setDataJson(JsonUtils.toJson(message.getData()));
        entity.setStatus("PENDING");
        mapper.insert(entity);
        log.debug("[MQ事务日志] 消息日志已写入 msgId={} topic={} tag={}",
                message.getMsgId(), message.getTopic(), message.getTag());
    }

    @Override
    public boolean existsByMsgId(String msgId) {
        return mapper.existsByMsgId(msgId);
    }
}
