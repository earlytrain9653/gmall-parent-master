package com.atguigu.gmall.pay;

import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.common.constant.MqConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 杨林
 * @create 2022-12-27 9:51 星期二
 * description:
 */
@SpringBootTest
public class Test {

    @Autowired
    MQService mqService;

    @org.junit.jupiter.api.Test
    public void test(){
        mqService.send("hhh", MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_PAYED_RK);
    }
}
