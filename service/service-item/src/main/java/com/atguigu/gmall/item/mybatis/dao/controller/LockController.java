package com.atguigu.gmall.item.mybatis.dao.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Service;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 杨林
 * @create 2022-12-08 8:54 星期四
 * description: 测试锁
 */
@RestController
@Slf4j
public class LockController {

    @Value("${server.port}")
    String port;

    @Autowired
    StringRedisTemplate redisTemplate;

    ReentrantLock lock = new ReentrantLock();

    @Autowired
    LockService lockService;

    /**
     * 并发情况操作共享组件会出现线程安全问题：
     * 无锁 （单节点）：压测1w请求  实际值：359
     * 加锁 本地锁 （单节点）：压测1w请求  实际值：1000
     * 加锁 本地锁  （集群模式）：压测1w请求  实际值4916
     *      结论：本地锁对于集群模式来说：每个节点的锁都是该节点独有的锁 是锁不住所有机器的 因此：需要引入分布式锁
     * 分布式锁实现思路：将锁的状态存入公共的地方（redis），让所有的节点都修改同一把锁
     * 加锁  分布式锁  （集群模式）：压测1w请求  实际值：10000
     *      效果：分布式锁在分布式情况下 能锁住所有
     *          锁越大性能越差
     * @return
     */
    //分布式锁的实现
    @GetMapping("/incr")
    public Result distributedLock() throws InterruptedException {

        //打印当前节点端口号
        log.info("当前线程的端口号为：{}",port);

        //上锁
        String uuid = lockService.lock();  //自旋锁
        //读取远程的数据（redis中）
        String num = redisTemplate.opsForValue().get("num");
        //对数据进行加1操作
        num = Integer.parseInt(num) + 1 + "";
        //将修改后的数据放回redis中
        redisTemplate.opsForValue().set("num",num);

        //解锁
       lockService.unlock(uuid);

        return Result.ok();
    }

//    //本地锁
//    public Result incrLocalLock(){
//
//        //打印当前节点端口号
//        log.info("当前线程的端口号为：{}",port);
//
//        //加锁
//        lock.lock();
//
//        //读取远程的数据（redis中）
//        String num = redisTemplate.opsForValue().get("num");
//        //对数据进行加1操作
//        num = Integer.parseInt(num) + 1 + "";
//        //将修改后的数据放回redis中
//        redisTemplate.opsForValue().set("num",num);
//
//        //解锁
//        lock.unlock();
//        return Result.ok();
//    }
}
