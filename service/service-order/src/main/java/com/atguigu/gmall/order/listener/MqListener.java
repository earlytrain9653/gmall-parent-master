package com.atguigu.gmall.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author 杨林
 * @create 2022-12-24 13:27 星期六
 * description:
 */
@Service
@Slf4j
public class MqListener {

    //消费者开启了手动ack（手动确认模式）  必须完全给服务器回复ok  服务器才回删除消息
    @RabbitListener(queues = "haha")   //说明要监听消息
    public void listener(Message message, Channel channel) throws IOException {
        //消息标签
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String s = new String(message.getBody());
//        System.out.println("收到消息：" + s + "; 正在处理。。。。");
        log.info("收到消息：{}" + s + "; 正在处理。。。。");
        //回复ok
        try {
            channel.basicAck(deliveryTag,false);//是否批量回复：否
        }catch (Exception e){
            channel.basicNack(deliveryTag,false,true);//重新入队
        }
    }
}
