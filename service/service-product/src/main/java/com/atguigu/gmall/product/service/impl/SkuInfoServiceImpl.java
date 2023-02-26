package com.atguigu.gmall.product.service.impl;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.*;
import com.atguigu.gmall.product.service.*;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import com.atguigu.gmall.search.SearchAttr;
import com.atguigu.gmall.starter.cache.service.CacheService;
import com.google.common.collect.Lists;
import java.util.Date;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.product.vo.SkuSaveVo;
import com.atguigu.gmall.search.Goods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-11-29 11:42:45
*/
@Slf4j
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    CacheService cacheService;


    @Transactional
    @Override
    public void saveSkuInfoData(SkuSaveVo vo) {
        //1.把基本信息保存到 sku_info
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(vo,skuInfo);
        boolean save = save(skuInfo);
        if (!save) {
            log.debug("sku：{}基本信息保存失败", vo);
        }
        //获取skuId
        Long skuId = skuInfo.getId();

        //2.图片信息  sku_image
        List<SkuImage> imageList = vo.getSkuImageList().stream()
                .map(item -> {
                    SkuImage skuImage = new SkuImage();
                    BeanUtils.copyProperties(item, skuImage);
                    //id回填
                    skuImage.setSkuId(skuId);

                    return skuImage;
                })
                .collect(Collectors.toList());
        //skuImage批量入库
        boolean b = skuImageService.saveBatch(imageList);
        if (!b) {
            log.debug("sku：{}图片信息保存失败", vo.toString());
        }

        //3.平台属性
        List<SkuAttrValue> attrValueList = vo.getSkuAttrValueList().stream()
                .map(item -> {
                    SkuAttrValue skuAttrValue = new SkuAttrValue();
                    BeanUtils.copyProperties(item, skuAttrValue);
                    //回填skuId
                    skuAttrValue.setSkuId(skuId);
                    return skuAttrValue;
                }).collect(Collectors.toList());
        skuAttrValueService.saveBatch(attrValueList);

        //销售属性
        List<SkuSaleAttrValue> saleAttrValueList = vo.getSkuSaleAttrValueList().stream()
                .map(item -> {
                    SkuSaleAttrValue saleAttrValue = new SkuSaleAttrValue();
                    saleAttrValue.setSpuId(skuInfo.getSpuId());
                    saleAttrValue.setSaleAttrValueId(item.getSaleAttrValueId());
                    saleAttrValue.setSkuId(skuId);
                    return saleAttrValue;
                }).collect(Collectors.toList());
        skuSaleAttrValueService.saveBatch(saleAttrValueList);

        //同步bitmap
        redisTemplate.opsForValue().setBit(RedisConst.SKUID_BITMAP,skuId,true);
    }

    /**
     * 商品上架
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        //1.修改数据库的状态为上架
        SkuInfo skuInfo = getById(skuId);
        skuInfo.setIsSale(1);
        boolean update = updateById(skuInfo);
        if (update){
            Goods goods = prepareGoods(skuId);
            searchFeignClient.up(goods);

            //将商品信息加入到位图中
            boolean bitmap = cacheService.updateBitmap(RedisConst.SKUID_BITMAP, skuId, true);

            log.info("商品{}上架完成,并将商品信息添加到位图中",skuInfo);
        }
    }


    //根据skuId准备一个要上架的Goods数据
    private Goods prepareGoods(Long skuId) {
        SkuInfo info = getById(skuId);
        //2.保存在es中
        Goods goods = new Goods();
        goods.setId(info.getId());
        goods.setDefaultImg(info.getSkuDefaultImg());
        goods.setTitle(info.getSkuName());
        goods.setPrice(info.getPrice().doubleValue());
        goods.setCreateTime(new Date());

        BaseTrademark baseTrademark = baseTrademarkService.getById(info.getTmId());
        //查询品牌
        goods.setTmId(info.getTmId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());

        //精确的三级分类信息
        CategoryTreeVo treeVo = baseCategory2Service.getCategoryTreeVoByC3Id(info.getCategory3Id());
        goods.setCategory1Id(treeVo.getCategoryId());
        goods.setCategory1Name(treeVo.getCategoryName());
        CategoryTreeVo child = treeVo.getCategoryChild().get(0);
        goods.setCategory2Id(child.getCategoryId());
        goods.setCategory2Name(child.getCategoryName());
        CategoryTreeVo child2 = child.getCategoryChild().get(0);
        goods.setCategory3Id(child2.getCategoryId());
        goods.setCategory3Name(child2.getCategoryName());

        goods.setHotScore(0L);

        //所有的平台属性
        List<SearchAttr> list = skuAttrValueService.getSkuAttrsAndValue(skuId);
        goods.setAttrs(Lists.newArrayList(list));
        return goods;
    }

    /**
     * 商品下架
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        //1.修改数据库状态
        SkuInfo skuInfo = getById(skuId);
        skuInfo.setIsSale(0);
        boolean update = updateById(skuInfo);

        //2.删除es中的商品
        if (update){
            searchFeignClient.down(skuId);

            //下架 清除redis中的相关的商品信息
            cacheService.delayDoubleDel(RedisConst.SKUKEY_PREFIX + skuId);

            //下架 清除bitmap中的该商品的相关新消息
            boolean bitmap = cacheService.updateBitmap(RedisConst.SKUID_BITMAP, skuId, false);

            log.info("商品{}下架完成,并且已经清除缓存与位图中的信息",skuId);
        }

    }
}




