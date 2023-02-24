package com.atguigu.gmall.mq.logistic;

import lombok.Data;

/**
 * @author 杨林
 * @create 2022-12-27 15:21 星期二
 * description:
 */
@Data
public class OrderLogisticMsg {
    private Long id;
    private Long userId;
}
