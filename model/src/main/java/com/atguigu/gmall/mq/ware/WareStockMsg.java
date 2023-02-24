package com.atguigu.gmall.mq.ware;

import lombok.Data;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-26 17:20 星期一
 * description:
 */
@Data
public class WareStockMsg {

    private Long orderId;
    private Long userId;
    private String consignee;
    private String consigneeTel;
    private String orderComment;
    private String orderBody;
    private String deliveryAddress;
    private String paymentWay;
    private List<Sku> details;
    private Long wareId;


    @Data
    public static class Sku{

        private Long skuId;
        private Integer skuNum;
        private String skuName;
    }

}
