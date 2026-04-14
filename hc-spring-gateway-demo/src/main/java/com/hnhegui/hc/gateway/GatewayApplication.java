package com.hnhegui.hc.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 网关服务启动类
 */
@Slf4j
@SpringBootApplication
@EnableCaching
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        log.info("====== 网关服务启动成功 ======");
    }
}
