package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author 杨林
 * @create 2022-12-06 10:01 星期二
 * description: 测试redis配置文件
 */
@SpringBootTest
public class TestRedis {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void test1(){
        redisTemplate.opsForValue().set("aa","商品信息");
    }

}
