package com.hnhegui.hc.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 网关服务启动类
 */
@SpringBootApplication
@EnableCaching
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("=== 网关服务启动成功 ===");
    }
}
