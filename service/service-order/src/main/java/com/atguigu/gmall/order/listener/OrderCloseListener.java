package com.atguigu.gmall.order.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author 杨林
 * @create 2022-12-24 14:25 星期六
 * description:
 */
@Slf4j
@Service
public class OrderCloseListener {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MQService mqService;

    /**
     * 监听死信队列中所有待关闭的订单
     * @param message
     * @param channel
     */
    @RabbitListener(queues = MqConst.ORDER_DEAD_QUEUE)
    public void listener(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String content = new String(message.getBody());
        try {
            OrderInfo orderInfo = JSON.parseObject(content, OrderInfo.class);
            orderBizService.closeOrder(orderInfo.getId(),orderInfo.getUserId());
            log.info("收到需要关单的消息：{}",content);
            channel.basicAck(tag,false);
        }catch (Exception e){
            mqService.retry(channel, tag, content,5);

        }

    }

}
