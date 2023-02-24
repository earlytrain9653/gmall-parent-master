package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

/**
 * @author 杨林
 * @create 2022-12-25 16:32 星期日
 * description:
 */
public interface PayService {

    /**
     * 生成支付页
     * @param orderId
     * @param userId
     * @return
     */
    String generatePayPage(Long orderId, Long userId) throws AlipayApiException;
}
