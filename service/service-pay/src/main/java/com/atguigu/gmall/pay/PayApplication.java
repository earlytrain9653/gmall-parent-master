package com.atguigu.gmall.pay;

import com.atguigu.gmall.common.config.mq.annotation.EnableMQService;
import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 杨林
 * @create 2022-12-24 19:48 星期六
 * description:
 */
@EnableMQService
@EnableUserAuthFeignInterceptor
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.order")
@SpringCloudApplication
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
    }
}
