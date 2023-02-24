package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.search.Goods;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-12 20:14 星期一
 * description:
 */
@FeignClient("service-search")
@RequestMapping("/api/inner/rpc/search")
public interface SearchFeignClient {

    /**
     * 检索商品
     * @param paramVo
     * @return
     */
    @PostMapping("/searchgoods")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo);

    /**
     * 商品上架
     * @return
     */
    @PostMapping("/up/goods")
    Result up(@RequestBody Goods goods);

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping("/down/goods/{skuId}")
    Result down(@PathVariable("skuId") Long skuId);



    /**
     * 增加热度分
     * @param skuId
     * @param score
     * @return
     */
    @GetMapping("/hotscore/{skuId}/{score}")
    public Result updateHotScore(@PathVariable("skuId") Long skuId,
                                 @PathVariable("score") Long score);

}
