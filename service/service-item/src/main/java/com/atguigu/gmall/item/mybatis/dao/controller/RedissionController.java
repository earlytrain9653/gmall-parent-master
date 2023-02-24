package com.atguigu.gmall.item.mybatis.dao.controller;

import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-08 19:56 星期四
 * description:
 */
@Slf4j
@RestController
public class RedissionController {

    @Autowired
    RedissonClient client;

    @GetMapping("/redisson/lock")
    public Result lock() throws InterruptedException {
        //获取一把锁
        RLock lock = client.getLock("haha-lock");

        try {  //加解锁总是成对出现
            //尝试加锁 成功返回 true 失败返回 false  也会自动续期
            //boolean b = lock.tryLock();
            //加锁  自动续期
            lock.lock();  //阻塞式加锁  一定要等到锁
            //lock.lock(10,TimeUnit.SECONDS);  //锁的默认时长10s
            log.info("业务正在运行");
            TimeUnit.SECONDS.sleep(60);
            log.info("业务运行完成");
        }catch (Exception e){

        }finally {
            try {
                //            lock.unlockAsync();  //异步解锁  在后台解锁 继续执行下面的代码
                lock.unlock();   //同步解锁
            }catch (Exception e){}
        }


        return Result.ok();
    }


    @GetMapping("/redisson")
    public Result redisson(){
      log.info("客户端：{}",client);
      return Result.ok();
    }
}
