package com.atguigu.gmall.mq.logistic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-27 15:57 星期二
 * description:
 */
@Data
public class EOrderRequestData {
    @JsonProperty("OrderCode")
    private String OrderCode;  //订单号
    @JsonProperty("ShipperCode")
    private String ShipperCode;  //快递公司编号
    @JsonProperty("CustomerName")
    private String CustomerName;  //客户编号
    @JsonProperty("CustomerPwd")
    private String CustomerPwd;
    @JsonProperty("MonthCode")
    private String MonthCode;
    @JsonProperty("SendSite")
    private String SendSite;
    @JsonProperty("PayType")
    private Integer PayType;
    @JsonProperty("ExpType")
    private String ExpType;
    @JsonProperty("Cost")
    private BigDecimal Cost;   //快递运费
    @JsonProperty("OtherCost")
    private BigDecimal OtherCost; //其他费用
    @JsonProperty("sender")
    private Sender sender;  //发货人
    @JsonProperty("receiver")
    private Receiver receiver;  //收货人
    @JsonProperty("Commodity")
    private List<Commodity> Commodity; //商品
    @JsonProperty("AddService")
    private List<AddService> AddService;  //附加服务
    @JsonProperty("Weight")
    private BigDecimal Weight; //商品的重量
    @JsonProperty("Quantity")
    private Integer Quantity;  //数量
    @JsonProperty("Volume")
    private BigDecimal Volume;
    @JsonProperty("IsReturnPrintTemplate")
    private Integer IsReturnPrintTemplate;
    @JsonProperty("Remark")
    private String Remark; //备注



    @Data
    public static class Sender{
        @JsonProperty("Company")
        private String Company;
        @JsonProperty("Name")
        private String Name;
        @JsonProperty("Mobile")
        private String Mobile;
        @JsonProperty("ProvinceName")
        private String ProvinceName;
        @JsonProperty("CityName")
        private String CityName;
        @JsonProperty("ExpAreaName")
        private String ExpAreaName;
        @JsonProperty("Address")
        private String Address;
    }

    @Data
    public static class Receiver{
        @JsonProperty("Company")
        private String Company;
        @JsonProperty("Name")
        private String Name;
        @JsonProperty("Mobile")
        private String Mobile;
        @JsonProperty("ProvinceName")
        private String ProvinceName;
        @JsonProperty("CityName")
        private String CityName;
        @JsonProperty("ExpAreaName")
        private String ExpAreaName;
        @JsonProperty("Address")
        private String Address;
    }


    @Data
    public static class Commodity{
        @JsonProperty("GoodsName")
        private String GoodsName;
        @JsonProperty("Goodsquantity")
        private Integer Goodsquantity;
        @JsonProperty("GoodsWeight")
        private BigDecimal GoodsWeight;
    }


    @Data
    public static class AddService{
        @JsonProperty("Name")
        private String Name;
        @JsonProperty("Value")
        private String Value;
    }

}
