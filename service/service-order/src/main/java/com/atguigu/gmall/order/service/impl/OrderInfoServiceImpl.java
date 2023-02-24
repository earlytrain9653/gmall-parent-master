package com.atguigu.gmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author 华为YL
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
* @createDate 2022-12-21 18:10:00
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{

    /**
     * 根据订单id查询订单
     * @param id
     * @param userId
     * @return
     */
    @Override
    public OrderInfo getOrderInfoById(Long id, Long userId) {
        OrderInfo orderInfo = lambdaQuery().eq(OrderInfo::getId, id)
                .eq(OrderInfo::getUserId, userId)
                .one();

        return orderInfo;
    }
}




