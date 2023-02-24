package com.atguigu.gmall.mq.seckill;

import lombok.Data;

/**
 * @author 杨林
 * @create 2022-12-28 18:37 星期三
 * description:
 */
@Data
public class SeckillOrderMsg {

    private Long userId;
    private String code;
    private Long skuId;
    private String date;
}
