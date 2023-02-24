package com.atguigu.gmall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-27 13:41 星期二
 * description:
 */
@Data
public class SkuWare {

    private Long wareId;
    private List<Long> skuIds;

}
