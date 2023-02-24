package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.vo.ValueSkuJsonVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-11-29 11:42:45
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    /**
     * 查出spu销售属性名和值的集合
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        List<SpuSaleAttr> saleAttrs = baseMapper.getSpuSaleAttrList(spuId);
        return saleAttrs;
    }

    /**
     * 根据spuid查询销售属性集合
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuId,Long skuId) {
        List<SpuSaleAttr> list = baseMapper.getSpuSaleAttrListBySpuId(spuId,skuId);
        return list;
    }


    /**
     * 根据spuid得到所有sku的销售属性组合
     * @param spuId
     * @return
     */
    @Override
    public String getValueSpuJson(Long spuId) {
        List<ValueSkuJsonVo> list = baseMapper.getValueSpuJson(spuId);
        Map<String, Long> map = list.stream()
                .collect(Collectors.toMap((t) -> t.getValueJson(), (t) -> t.getSkuId()));
        String jsonString = JSON.toJSONString(map);
        return jsonString;
    }
}




