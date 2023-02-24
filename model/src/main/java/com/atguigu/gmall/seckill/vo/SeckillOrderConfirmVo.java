package com.atguigu.gmall.seckill.vo;

import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.user.entity.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-28 21:05 星期三
 * description:
 */
@Data
public class SeckillOrderConfirmVo {

    private List<OrderDetail> detailArrayList;
    private Integer totalNum;
    private BigDecimal totalAmount;
    private List<UserAddress> userAddressList;
}
