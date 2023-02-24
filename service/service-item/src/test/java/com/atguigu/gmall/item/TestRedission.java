package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 杨林
 * @create 2022-12-08 14:00 星期四
 * description:
 */
@SpringBootTest
public class TestRedission {

    @Test
    public void test1(){
        //创建Redission配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.10.129:6379")
                .setPassword("yl123@!");

        //创建Redission的客户端
        RedissonClient client = Redisson.create(config);

        System.out.println(client);
    }
}
