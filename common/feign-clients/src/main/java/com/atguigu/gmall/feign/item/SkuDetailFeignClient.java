package com.atguigu.gmall.feign.item;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author 杨林
 * @create 2022-12-03 21:38 星期六
 * description:
 */
@RequestMapping("/api/inner/rpc/item")
@FeignClient("service-item") //调用详情服务
public interface SkuDetailFeignClient {

    /**
     * 获取商品详情数据
     * @param skuId
     * @return
     */
    @GetMapping("/sku/detail/{skuId}")
    Result<SkuDetailVo> getSkuDetails(@PathVariable("skuId") Long skuId);
}
