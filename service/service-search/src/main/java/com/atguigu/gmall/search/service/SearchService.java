package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.Goods;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;

/**
 * @author 杨林
 * @create 2022-12-12 20:08 星期一
 * description:
 */
public interface SearchService {
    /**
     * 检索商品
     * @param paramVo
     * @return
     */
    SearchResponseVo search(SearchParamVo paramVo);

    /**
     * 上架  将商品保存到es
     * @param goods
     */
    void up(Goods goods);

    /**
     * 商品下架
     * @param skuId
     */
    void down(Long skuId);

    /**
     * 增加热度分
     * @param skuId
     * @param score
     */
    void updateHotScore(Long skuId, Long score);
}
