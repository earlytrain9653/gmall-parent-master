package com.atguigu.gmall.order.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-23 17:35 星期五
 * description:
 */
@NoArgsConstructor
@Data
public class  OrderSubmitVo {
    @NotEmpty(message = "收货人名字必须填写")
    @JsonProperty("consignee")
    private String consignee;

    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$",
            message = "必须填写合法的11位手机号")
    @JsonProperty("consigneeTel")
    private String consigneeTel;

    @NotEmpty(message = "收获地址不能为空")
    @JsonProperty("deliveryAddress")
    private String deliveryAddress;
    @JsonProperty("paymentWay")
    private String paymentWay;
    @JsonProperty("orderComment")
    private String orderComment;

    @JsonProperty("orderDetailList")
    private List<OrderDetailListDTO> orderDetailList;

    @NoArgsConstructor
    @Data
    public static class OrderDetailListDTO {
        @NotNull
        @JsonProperty("skuId")
        private Long skuId;
        @JsonProperty("imgUrl")
        private String imgUrl;
        @JsonProperty("skuName")
        private String skuName;
        @JsonProperty("orderPrice")
        private BigDecimal orderPrice;
        @JsonProperty("skuNum")
        private Integer skuNum;
        @JsonProperty("hasStock")
        private String hasStock;
    }
}
