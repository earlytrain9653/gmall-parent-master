package com.atguigu.gmall.cart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 购物车表 用户登录系统时更新冗余
 * @TableName cart_info
 */
@TableName(value ="cart_info")
@Data
public class CartInfo implements Serializable {


    /**
     * 用户id
     */
    private String userId;

    /**
     * skuid
     */
    private Long skuId;

    /**
     * 商品第一次放入购物车时价格
     */
    private BigDecimal cartPrice;

    /**
     * 商品的实时价格
     */
    private BigDecimal skuPrice;

    /**
     * 数量
     */
    private Integer skuNum;

    /**
     * 图片文件
     */
    private String imgUrl;

    /**
     * sku名称 (冗余)
     */
    private String skuName;

    /**
     * 
     */
    private Integer isChecked;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;


}