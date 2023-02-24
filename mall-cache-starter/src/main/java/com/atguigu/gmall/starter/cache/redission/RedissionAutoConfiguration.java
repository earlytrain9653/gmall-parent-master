package com.atguigu.gmall.starter.cache.redission;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author 杨林
 * @create 2022-12-08 19:51 星期四
 * description:
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
@SpringBootConfiguration
public class RedissionAutoConfiguration {

    @Bean
    public RedissonClient redissionClient(RedisProperties properties){
        //创建Redission配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword());

        //创建Redission的客户端
        RedissonClient client = Redisson.create(config);
        return client;
    }

}
