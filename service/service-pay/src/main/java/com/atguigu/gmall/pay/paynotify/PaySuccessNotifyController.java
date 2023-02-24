package com.atguigu.gmall.pay.paynotify;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.pay.config.properties.AlipayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 杨林
 * @create 2022-12-25 19:05 星期日
 * description:
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaySuccessNotifyController {

    @Autowired
    AlipayProperties alipayProperties;

    @Autowired
    MQService mqService;

    /**
     * 支付宝支付成功以后 支付宝会给这里发送请求  通知我们支付结果
     * @param params
     * @return
     */
    @PostMapping("/notify/success")
    public String paySuccessNotify(@RequestParam Map<String,String> params) throws AlipayApiException {
        log.info("收到支付宝支付消息通知：{}", JSON.toJSONString(params));

        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                alipayProperties.getAlipay_public_key(), alipayProperties.getCharset(),
                alipayProperties.getSign_type()); //调用SDK验证签名
        if (signVerified){
            log.info("验签通过  准备修改订单状态");
            String trade_status = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(trade_status)){
                //修改订单状态   通过消息传递机制
                mqService.send(params, MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_PAYED_RK);
            }
        }else {
            log.error("错误的支付宝数据  疑似攻击");
        }

        //什么时候给支付宝返回success
        return "success";
    }
}
