package com.atguigu.gmall.starter.cache;

import com.atguigu.gmall.starter.cache.annotation.EnableRedission;
import com.atguigu.gmall.starter.cache.aspect.CacheAspect;
import com.atguigu.gmall.starter.cache.redission.RedissionAutoConfiguration;
import com.atguigu.gmall.starter.cache.service.CacheService;
import com.atguigu.gmall.starter.cache.service.impl.CacheServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 杨林
 * @create 2022-12-10 16:02 星期六
 * description:
 */
@EnableAspectJAutoProxy
@EnableRedission
@AutoConfigureAfter({RedisAutoConfiguration.class, RedissionAutoConfiguration.class})
@Configuration
public class CacheAutoConfiguration {

    @Bean
    public CacheAspect cacheAspect(){
        return new CacheAspect();
    }

    @Bean
    public CacheService cacheService(){
        return new CacheServiceImpl();
    }


}
