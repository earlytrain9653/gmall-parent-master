package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨林
 * @create 2022-12-03 21:33 星期六
 * description:sku详情
 */
@RestController
@RequestMapping("/api/inner/rpc/item")
public class SkuDetailRpcController {

    @Autowired
    SkuDetailService skuDetailService;

    /**
     * 获取商品详情数据
     * @param skuId
     * @return
     */
    @GetMapping("/sku/detail/{skuId}")
    public Result<SkuDetailVo> getSkuDetails(@PathVariable("skuId") Long skuId) throws InterruptedException {
        //1.切面拦截：如果缓存中有真正的方法都不用调
        SkuDetailVo skuDetailVo = skuDetailService.getDetailData(skuId);

        //2.给这个商品增加热度
        skuDetailService.incrHotScore(skuId);


        return Result.ok(skuDetailVo);
    }

}
