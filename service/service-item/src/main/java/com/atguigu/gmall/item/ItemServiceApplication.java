package com.atguigu.gmall.item;

import com.atguigu.gmall.starter.cache.annotation.EnableAppCache;
import com.atguigu.gmall.common.config.thread.annotation.EnableAppThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 杨林
 * @create 2022-12-03 20:40 星期六
 * description:
 */
//@EnableAppCache
//@EnableAspectJAutoProxy  //启用aspectJ自动代理   在mall-cache-starter的配置类中开启
@SpringBootApplication
//com.atguigu.gmall.feign   扫描包建议：用谁扫谁
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.product",
                                    "com.atguigu.gmall.feign.search"})  //开启feign的远程调用
@EnableAppThreadPool
public class ItemServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class,args);
    }
}
