package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.search.SearchAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service
* @createDate 2022-11-29 11:42:45
*/
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    /**
     * 获取某个skuId的所有平台属性名和值
     * @param skuId
     * @return
     */
    List<SearchAttr> getSkuAttrsAndValue(Long skuId);
}
