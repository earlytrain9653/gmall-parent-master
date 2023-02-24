package com.atguigu.gmall.seckill;

import com.atguigu.gmall.common.config.mq.annotation.EnableMQService;
import org.mapstruct.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 杨林
 * @create 2022-12-27 19:51 星期二
 * description:
 */
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.user",
                                    "com.atguigu.gmall.feign.order"
                                    })
@EnableMQService
@EnableScheduling
@MapperScan(basePackages = "com.atguigu.gmall.seckill.mapper")
@SpringCloudApplication
public class SeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class,args);
    }
}
