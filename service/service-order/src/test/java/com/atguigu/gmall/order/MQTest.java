package com.atguigu.gmall.order;

import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.order.entity.OrderInfo;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 杨林
 * @create 2022-12-24 11:10 星期六
 * description:
 */
@SpringBootTest
public class MQTest {

    //无论成功失败，confirm回调都会触发，如果消息不能抵达给Queue Return回调就回触发

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MQService mqService;


    @Test
    public void test(){
        OrderInfo orderInfo = new OrderInfo();
        mqService.send(orderInfo,"hello","a");
    }

    @Test
    void testSend(){
        rabbitTemplate.convertAndSend("hello","a","哈啊哈");
        System.out.println("发送完成");
    }
}
