package com.hnhegui.hc.mapper.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.mq.MqTransactionLog;

/**
 * MQ 事务消息日志 Mapper
 */
public interface MqTransactionLogMapper extends BaseMapper<MqTransactionLog> {

    /**
     * 根据 msgId 判断消息日志是否存在（用于事务回查）。
     *
     * @param msgId 消息 ID
     * @return true 表示消息日志存在
     */
    default boolean existsByMsgId(String msgId) {
        return selectCount(new LambdaQueryWrapper<MqTransactionLog>()
                .eq(MqTransactionLog::getMsgId, msgId)) > 0;
    }
}
