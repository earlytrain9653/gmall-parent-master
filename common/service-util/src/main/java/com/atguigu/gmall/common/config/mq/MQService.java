package com.atguigu.gmall.common.config.mq;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.util.MD5;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author 杨林
 * @create 2022-12-24 11:31 星期六
 * description:
 */
@Slf4j
@Service
public class MQService {


    private StringRedisTemplate redisTemplate;

    private RabbitTemplate rabbitTemplate;

    public MQService(RabbitTemplate rabbitTemplate, StringRedisTemplate redisTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
        initTemplate();
    }


    public void send(Object message, String exchange, String routingKey) {
        rabbitTemplate.setRetryTemplate(new RetryTemplate());
        rabbitTemplate.convertAndSend(exchange, routingKey, JSON.toJSONString(message));
    }


    public void retry(Channel channel, long tag, String content, Integer retryCount) throws IOException {
        //只要消息的MD5相同 就是同一个消息
        String md5 = MD5.encrypt(content);
        //同一个消息最多重试5次
        Long increment = redisTemplate.opsForValue().increment("msg:count:" + md5);
        if (increment <= retryCount) {
            channel.basicNack(tag, false, true);
        } else {
            redisTemplate.delete("msg:count:" + md5);
            channel.basicAck(tag, false);
        }
    }

    private void initTemplate() {
        this.rabbitTemplate.setConfirmCallback((CorrelationData correlationData,
                                                boolean ack,
                                                String cause) -> {
            log.info("confirm回调：data:{},ack:{},cause:{}", correlationData, ack, cause);
        });

        this.rabbitTemplate.setReturnCallback((Message message,
                                               int replyCode,
                                               String replyText,
                                               String exchange,
                                               String routingKey) -> {
            log.info("return回调：message:{},replyCode:{},replyText:{},exchange:{} routingKey:{}",
                    message, replyCode, replyText, exchange, routingKey);
        });
    }
}
