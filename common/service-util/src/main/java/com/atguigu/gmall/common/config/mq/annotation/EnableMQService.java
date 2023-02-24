package com.atguigu.gmall.common.config.mq.annotation;

import com.atguigu.gmall.common.config.mq.MQService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-24 11:48 星期六
 * description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MQService.class)
@EnableRabbit
public @interface EnableMQService {
}
