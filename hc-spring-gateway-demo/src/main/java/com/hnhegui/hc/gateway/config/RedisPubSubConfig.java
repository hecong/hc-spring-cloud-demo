package com.hnhegui.hc.gateway.config;

import com.hnhegui.hc.gateway.listener.RouteRefreshListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis Pub/Sub 配置
 * 配置路由刷新消息监听
 */
@Configuration
public class RedisPubSubConfig {

    /**
     * 创建消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter routeRefreshListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅路由刷新频道
        container.addMessageListener(routeRefreshListenerAdapter,
                new ChannelTopic(RouteRefreshListener.ROUTE_REFRESH_CHANNEL));
        return container;
    }

    /**
     * 创建消息监听器适配器
     */
    @Bean
    public MessageListenerAdapter routeRefreshListenerAdapter(RouteRefreshListener listener) {
        return new MessageListenerAdapter(listener);
    }
}
