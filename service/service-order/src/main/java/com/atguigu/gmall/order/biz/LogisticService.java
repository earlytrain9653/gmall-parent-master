package com.atguigu.gmall.order.biz;

import com.alibaba.fastjson.JSONObject;

/**
 * @author 杨林
 * @create 2022-12-27 15:41 星期二
 * description:物流服务
 */
public interface LogisticService {
    /**
     * 生成电子面单
     * @param orderId
     * @param userId
     * @return
     */
    JSONObject generateEOrder(Long orderId, Long userId) throws Exception;
}
