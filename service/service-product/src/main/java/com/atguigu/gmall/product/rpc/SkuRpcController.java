package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-03 23:22 星期六
 * description:
 */
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuRpcController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 获取skuInfo
     * @return
     */
    @GetMapping("/sku/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId)  {

//        //测试feign连接失败后的重试
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        SkuInfo skuInfo = skuInfoService.getById(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * 获取sku的图片信息
     * @param skuId
     * @return
     */
    @GetMapping("/skuImage/{skuId}")
    public Result<List<SkuImage>> getSkuInages(@PathVariable("skuId") Long skuId){
        List<SkuImage> list = skuImageService.lambdaQuery()
                .eq(SkuImage::getSkuId, skuId)
                .list();
        return Result.ok(list);
    }

    /**
     * 获取商品的实时价格
     * @param skuId
     * @return
     */
    @GetMapping("/price/{skuId}")
    public Result<BigDecimal> getPrice(@PathVariable("skuId") Long skuId){

        //该方法虽然返回的是一个对象 但对象中的其他字段均为null
//        SkuInfo one = skuInfoService.lambdaQuery()
//                .select(SkuInfo::getPrice)  //只查找指定的列
//                .eq(SkuInfo::getId,skuId)
//                .one();
        //BigDecimal price = one.getPrice();
        //下面的语句等同于 select * from sku_info where sku_id = ?
        //该方法查询所有的列 数据量庞大  影响网络的传输  并发请求量就会减少 因此我们只需要查询我们需要的列
        SkuInfo byId = skuInfoService.getById(skuId);
        BigDecimal price = byId.getPrice();
        return Result.ok(price);
    }

    /**
     * 获取spu销售属性名和值
     * @param spuId
     * @return
     */
    @GetMapping("/spusaleattr/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttr(@PathVariable("spuId") Long spuId,
                                                    @PathVariable("skuId") Long skuId){
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrListBySpuId(spuId,skuId);
        return Result.ok(list);
    }

    /**
     * 获取spu的json字符串
     * @return
     */
    @GetMapping("/valuespujson/{spuId}")
    public Result<String> getValueSpuJson(@PathVariable("spuId") Long spuId){

        String json = spuSaleAttrService.getValueSpuJson(spuId);

        return Result.ok(json);
    }


}
