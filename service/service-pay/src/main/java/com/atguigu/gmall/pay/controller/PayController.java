package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨林
 * @create 2022-12-24 19:54 星期六
 * description:
 */
@RequestMapping("/api/payment")
@RestController
public class PayController {

    @Autowired
    PayService payService;

    /**
     * 请求二维码收银台页面
     * @param orderId
     * @return
     * @throws AlipayApiException
     */
    @GetMapping("/alipay/submit/{orderId}")
    public String alipay(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        Long userId = UserAuthUtils.getUserId();
        String page = payService.generatePayPage(orderId,userId);
        return page;
    }
}
