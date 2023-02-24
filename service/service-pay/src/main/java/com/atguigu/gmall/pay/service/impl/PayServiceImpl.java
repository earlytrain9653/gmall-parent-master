package com.atguigu.gmall.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.pay.config.AlipayConfig;
import com.atguigu.gmall.pay.config.properties.AlipayProperties;
import com.atguigu.gmall.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨林
 * @create 2022-12-25 16:32 星期日
 * description:
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    OrderFeignClient orderFeignClient;

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    AlipayProperties alipayProperties;

    /**
     * 生成支付页
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public String generatePayPage(Long orderId, Long userId) throws AlipayApiException {
        //1.创建一个AliPayClient

        //2.创建一个支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        //3.设置参数
        alipayRequest.setReturnUrl(alipayProperties.getReturn_url());  //同步回调  支付成功以后 浏览器要跳转到的页面
        alipayRequest.setNotifyUrl(alipayProperties.getNotify_url());  //通知回调  支付成功后 支付消息会通知给这个地址

        //4.准备待支付的订单数据
        //远程调用订单服务，获取订单的基本数据  基于此数据构造一个支付页
        OrderInfo orderInfo = orderFeignClient.grtOrderInfoById(orderId).getData();

        //商户订单号
        String outTradeNo = orderInfo.getOutTradeNo();
        //付款金额
        BigDecimal totalAmount = orderInfo.getTotalAmount();
        //订单名称
        String tradeName = "尚品汇-订单-" + outTradeNo;
        //商品名称
        String tradeBody = orderInfo.getTradeBody();

        Map<String,Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no",outTradeNo);
        bizContent.put("total_amount",totalAmount);
        bizContent.put("subject",tradeName);
        bizContent.put("body",tradeBody);
        bizContent.put("product_code","FAST_INSTANT_TRADE_PAY");
        //自动关单
        String date = DateUtil.formatDate(orderInfo.getExpireTime(), "yyyy-MM-dd HH:mm:ss");
        bizContent.put("time_expire",date);
        alipayRequest.setBizContent(JSON.toJSONString(bizContent));

        String page = alipayClient.pageExecute(alipayRequest).getBody();

        return page;
    }
}
