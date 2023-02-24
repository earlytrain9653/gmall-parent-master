package com.atguigu.gmall.web;

import com.atguigu.gmall.common.interceptors.annotation.EnableUserAuthFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {
                                    "com.atguigu.gmall.feign"
                                    })
@EnableUserAuthFeignInterceptor
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class,args);
    }
}
