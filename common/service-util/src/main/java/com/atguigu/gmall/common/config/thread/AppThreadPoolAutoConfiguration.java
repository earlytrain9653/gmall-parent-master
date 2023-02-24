package com.atguigu.gmall.common.config.thread;

import com.atguigu.gmall.common.config.thread.properties.AppThreadProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-05 21:28 星期一
 * description:自定义一个线程池
 */
@EnableConfigurationProperties(AppThreadProperties.class)
@Configuration
@Slf4j
public class AppThreadPoolAutoConfiguration {

    /**
     * int corePoolSize,  核心线程数
     * int maximumPoolSize,   最大线程数  阻塞队列满  开到最大  max-core：弹性线程
     * long keepAliveTime,   弹性线程的存活时间  指多久不干活就会释放掉
     * TimeUnit unit,       时间单位
     * BlockingQueue<Runnable> workQueue,阻塞队列
     *                  任务过来 先开启核心线程进行处理 如果核心线程都忙 则进入阻塞队列
     * ThreadFactory threadFactory,  线程工厂  创建新线程   new Thread()
     * RejectedExecutionHandler handler  拒绝策略
     *                                     核心  队列  最大 都满了时 再有任务过来 启用拒绝策略
     *
     *
     *   队列大小的两个决定因素：
     *          （1）：压测： 峰值 * 1.5
     *          （2）：内存： 未来微服务部署到哪种机器上
     * @return
     */
    @Bean
    public ThreadPoolExecutor executor(AppThreadProperties properties){
        ThreadPoolExecutor myThreadPool = new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(properties.getWorkQueueSize()),  //可以利用碎片化空间
                new ThreadFactory() {
                    int i = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        log.info("线程池：准备新线程，老线程会复用");  //最多打印24次
                        Thread thread = new Thread(r);  //创建线程  执行任务
                        //给每个线程设置一个名字
                        thread.setName("线程池核心线程：" + i++);
                        //设置优先级 0~10  10优先级最高
                        thread.setPriority(10);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return myThreadPool;
    }
}
