package com.hnhegui.hc.publisher;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @author hecong
 * @since 2026/4/14 14:26
 */
@Component
@RequiredArgsConstructor
public class MessagePublisher {


    private final RedissonClient redissonClient;

    /**
     * 发布消息到指定主题
     * @param topicName 主题名（频道名）
     * @param message 消息内容（支持对象/字符串）
     * @return 接收到消息的客户端数量
     */
    public long publish(String topicName, Object message) {
        RTopic topic = redissonClient.getTopic(topicName);
        // 发布消息，返回订阅者数量
        return topic.publish(message);
    }
}
