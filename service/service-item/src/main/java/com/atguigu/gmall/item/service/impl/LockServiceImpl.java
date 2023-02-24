package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-08 13:12 星期四
 * description: 实现自定义上锁和解锁的操作
 */
@Service
public class LockServiceImpl implements LockService {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 上锁操作：阻塞式加锁
     * 1：如果获取锁失败  就一直等待锁  直到获取成功
     */
    @Override
    public String lock() throws InterruptedException {

//        //加锁失败后 不跳出循环 一直尝试加锁   自旋锁
//        while (!redisTemplate.opsForValue().setIfAbsent("lock","1")){
//            //加锁成功后 跳出循环
//
//            //加锁失败后 20毫秒后重试
//            TimeUnit.MILLISECONDS.sleep(10);
//        }

        //加入UUID 防止删除别人的锁
        String uuid = UUID.randomUUID().toString();

        //为保证Redis操作的原子性 防止一些物理故障造成的损失  一次性完成赋值和设置过去时间
        while (!redisTemplate.opsForValue().setIfAbsent("lock",uuid,30,TimeUnit.SECONDS)){
            //加锁失败后 20毫秒后重试
            TimeUnit.MILLISECONDS.sleep(10);
        }

        return uuid;

        //走到这里 说明加锁成功  为Redis设置过期时间
        //redisTemplate.expire("lock",10,TimeUnit.SECONDS);
    }

    /**
     * 解锁操作
     * @param uuid
     */
    @Override
    public void unlock(String uuid) {

//        //判断这个值是否等于加锁时的uuid (锁过期  Redis会自动删除 别人会占上这个锁)
//        //如果相等 说明是自己的锁 在执行解锁操作
//        //如果不相等  说明不是自己的锁  则不能执行解锁操作
//        String lock = redisTemplate.opsForValue().get("lock");
//
//        if (uuid.equals(lock)){
//            //锁未发生变化
//            redisTemplate.delete("lock");
//
//            //问题：获取锁值的时候确实没过期 但在查询完成之后 返回数据的过程中 过期了  又会造成误删
//        }

        //删除锁 获取值 + 删除锁 = 原子操作  因此需要脚本支持
        //脚本无论多长  都是原子操作
        //lua脚本只能保证原子性 但不能保证事务
        //lua脚本不能写太长 还加各种修改
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        //返回0：删锁失败，有可能是别人的锁
        //返回1：删锁成功
        redisTemplate.execute(new DefaultRedisScript<>(script,Long.class),
                Arrays.asList("lock"),
                uuid
                );
    }
}
