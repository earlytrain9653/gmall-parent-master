package com.atguigu.gmall.item.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-03 22:41 星期六
 * description:
 */
public interface SkuDetailService {
    /**
     * 商品详情
     * @param skuId
     * @return
     */
    SkuDetailVo getDetailData(Long skuId);


    /**
     * 增加商品热度
     * @param skuId
     */
    void incrHotScore(Long skuId);
}
