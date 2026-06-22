package com.hnhegui.hc.entity.mq;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQ 事务消息日志实体
 *
 * <p>用于事务消息的本地持久化，配合通用 Checker 判断本地事务是否已提交。</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("mq_transaction_log")
public class MqTransactionLog extends BaseEntity {

    /**
     * 消息 ID（唯一约束，用于回查判断）
     */
    private String msgId;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息标签
     */
    private String tag;

    /**
     * 业务数据 JSON
     */
    private String dataJson;

    /**
     * 状态：PENDING / COMMITTED / ROLLED_BACK
     */
    private String status;
}
