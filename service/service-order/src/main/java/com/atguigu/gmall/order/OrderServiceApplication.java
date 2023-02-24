package com.atguigu.gmall.order;

import com.atguigu.gmall.common.config.mq.annotation.EnableMQService;
import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 杨林
 * @create 2022-12-21 17:58 星期三
 * description:
 */
@EnableMQService
@EnableTransactionManagement
@SpringCloudApplication
@MapperScan(basePackages = "com.atguigu.gmall.order.mapper")
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.cart",
                                    "com.atguigu.gmall.feign.product",
                                    "com.atguigu.gmall.feign.user",
                                    "com.atguigu.gmall.feign.ware"})
@EnableUserAuthFeignInterceptor
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class,args);
    }
}
