package com.atguigu.gmall.product.vo;

/**
 * @author 杨林
 * @create 2022-12-03 21:04 星期六
 * description:
 */
// 封装页面详情用到的所有数据

import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuDetailVo {

    //分类信息
    private CategoryViewDTO categoryView;

    //sku信息
    private SkuInfo skuInfo;

    //实时价格
    private BigDecimal price;

    //销售属性集合
    private List<SpuSaleAttr> spuSaleAttrList;

    //valuesSkuJson
    private String valuesSkuJson;

    @Data
    public static class CategoryViewDTO{
        private Long category1Id;
        private Long category2Id;
        private Long category3Id;
        private String category1Name;
        private String category2Name;
        private String category3Name;
    }

}
