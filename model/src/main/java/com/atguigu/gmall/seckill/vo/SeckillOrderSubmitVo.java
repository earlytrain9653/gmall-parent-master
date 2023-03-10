package com.atguigu.gmall.seckill.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-28 21:36 星期三
 * description:
 */
@NoArgsConstructor
@Data
public class SeckillOrderSubmitVo {


    @JsonProperty("consignee")
    private String consignee;
    @JsonProperty("consigneeTel")
    private String consigneeTel;
    @JsonProperty("deliveryAddress")
    private String deliveryAddress;
    @JsonProperty("orderComment")
    private String orderComment;

    @JsonProperty("code")
    private String code;

    @JsonProperty("orderDetailList")
    private List<OrderDetailListDTO> orderDetailList;

    @NoArgsConstructor
    @Data
    public static class OrderDetailListDTO {
        @JsonProperty("id")
        private Object id;
        @JsonProperty("userId")
        private Integer userId;
        @JsonProperty("orderId")
        private Object orderId;
        @JsonProperty("skuId")
        private Integer skuId;
        @JsonProperty("skuName")
        private String skuName;
        @JsonProperty("imgUrl")
        private String imgUrl;
        @JsonProperty("orderPrice")
        private Integer orderPrice;
        @JsonProperty("skuNum")
        private Integer skuNum;
        @JsonProperty("createTime")
        private String createTime;
        @JsonProperty("splitTotalAmount")
        private Integer splitTotalAmount;
        @JsonProperty("splitActivityAmount")
        private Integer splitActivityAmount;
        @JsonProperty("splitCouponAmount")
        private Integer splitCouponAmount;
    }
}
