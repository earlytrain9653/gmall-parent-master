package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.config.thread.annotation.EnableAppThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 杨林
 * @create 2022-12-16 13:57 星期五
 * description:
 */
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@SpringCloudApplication
@EnableAppThreadPool
public class CartServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class);
    }
}
