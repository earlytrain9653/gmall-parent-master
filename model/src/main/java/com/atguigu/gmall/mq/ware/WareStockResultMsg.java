package com.atguigu.gmall.mq.ware;

import lombok.Data;

/**
 * @author 杨林
 * @create 2022-12-26 21:39 星期一
 * description:
 */
@Data
public class WareStockResultMsg {

    private Long orderId;
    private String status;

}
