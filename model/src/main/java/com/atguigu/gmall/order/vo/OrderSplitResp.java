package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.mq.ware.WareStockMsg;
import lombok.Data;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-27 13:26 星期二
 * description:
 */
@Data
public class OrderSplitResp {

    private Long orderId;
    private Long userId;
    private String consignee;
    private String consigneeTel;
    private String orderComment;
    private String orderBody;
    private String deliveryAddress;
    private String paymentWay;
    private List<OrderSplitResp.Sku> details;
    private Long wareId;


    @Data
    public static class Sku{

        private Long skuId;
        private Integer skuNum;
        private String skuName;
    }
}
