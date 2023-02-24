package com.atguigu.gmall.search.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.search.Goods;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-12 19:54 星期一
 * description:
 */
@RestController
@RequestMapping("/api/inner/rpc/search")
public class SearchRpcController {

    @Autowired
    SearchService searchService;


    /**
     * 增加热度分
     * @param skuId
     * @param score
     * @return
     */
    @GetMapping("/hotscore/{skuId}/{score}")
    public Result updateHotScore(@PathVariable("skuId") Long skuId,
                                 @PathVariable("score") Long score){
        searchService.updateHotScore(skuId,score);
        return Result.ok();
    }


    /**
     * 商品上架
     * @return
     */
    @PostMapping("/up/goods")
    public Result up(@RequestBody Goods goods){
        searchService.up(goods);
        return Result.ok();
    }

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping("/down/goods/{skuId}")
    public Result down(@PathVariable("skuId") Long skuId){
        searchService.down(skuId);
        return Result.ok();
    }

    /**
     * 检索商品
     * @param paramVo
     * @return
     */
    @PostMapping("/searchgoods")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo){

        // 检索
        SearchResponseVo responseVo = searchService.search(paramVo);
        return Result.ok(responseVo);
    }
}
