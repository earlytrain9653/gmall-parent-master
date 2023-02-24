package com.atguigu.gmall.item.service.impl;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.feign.product.ProductSkuDetailFeignClient;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.starter.cache.aspect.annotation.MallCache;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.vo.SkuDetailVo.CategoryViewDTO;

import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.product.vo.CategoryTreeVo;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 杨林
 * @create 2022-12-03 22:41 星期六
 * description:
 */
@Service
@Slf4j
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    ProductSkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    ThreadPoolExecutor executor;


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient client;

    @Autowired
    SearchFeignClient searchFeignClient;

    //业务Service只关注业务  增强逻辑有切面负责
    @Override
    @MallCache(
                cacheKey = RedisConst.SKUKEY_PREFIX + "#{#args[0]}",
                bitMapName = RedisConst.SKUID_BITMAP,
                bitMapKey = "#{#args[0]}",
                lockKey = RedisConst.LOCK_SKU + "#{#args[0]}",
                ttl = 7,
                unit = TimeUnit.DAYS)
    public SkuDetailVo getDetailData(Long skuId)  {
        return this.getSkuDetailVo(skuId);
    }

    /**
     * 增加商品热度
     * @param skuId
     */
    @Override
    public void incrHotScore(Long skuId) {
        //1.累计热度  score 累计的值
        Long score = redisTemplate.opsForValue().increment("sku:hotscore:" + skuId);
        //2.同步给es
        if (score % 100 == 0){  //说明累计到了100
            //远程调用es增加热度
           searchFeignClient.updateHotScore(skuId,score);
        }

    }

    /**
     * 改造商品详情
     *      引入分布式锁 （Redission）
     * @param skuId
     * @return
     */
//    public SkuDetailVo getDetailDataWithDistLock(Long skuId) throws InterruptedException {
//
//        //1.从缓存中获取数据
//        SkuDetailVo cache = cacheService.getRedisCache(skuId);
//
//        //2.判断缓存中是否含有要查询的数据
//        if (cache != null){
//            //缓存中存在数据  直接返回
//            return cache;
//        }
//
//        //3.缓存中没有数据 先访问位图中是否存在
//        //防止Redis穿透攻击 （随机值穿透攻击）
//        Boolean aBoolean = cacheService.mightContain(skuId);
//
//        //4.位图中没有数据 代表数据库无此数据 直接返回
//        if (!aBoolean){
//            return null;
//        }
//
//        //5.位图中存在数据，准备回源查找
//        //加锁  防止缓存击穿  lock:sku:49
//        RLock lock = client.getLock(RedisConst.LOCK_SKU + skuId); //锁的粒度要小
//        boolean tryLock = false;
//        try {
//            //6.尝试竞争一下锁
//            tryLock = lock.tryLock();
//            if (tryLock){
//                //回源查找
//                cache = this.getSkuDetailVo(skuId);
//                //放入缓存中
//                cacheService.saveData(cache,skuId);
//                return cache;
//            }else {
//                //没抢到锁
//                TimeUnit.MILLISECONDS.sleep(300);
//                return cacheService.getRedisCache(skuId);
//            }
//        }finally {
//            //解锁  只有获取到锁才解锁
//            if (tryLock){
//                lock.unlock();
//            }
//        }
//    }


//    private ReentrantLock lock = new ReentrantLock();
//
//    //利用Map作为本地缓存
//    ConcurrentHashMap<String, SkuDetailVo> cache =  new ConcurrentHashMap<>();

    /**
     * 改造商品详情页面之引入bitmap   防止随机值穿透攻击
     * 加锁:防止缓存击穿
     * 存空值：防止缓存穿透
     * @param skuId
     * @return
     */
//    @Override
//    public SkuDetailVo getDetailDataWithLocalLock(Long skuId) {
//
//        SkuDetailVo returnValue = null;
//
//        log.info("商品详情查询开始");
//        //1.先在缓存中查询
//        returnValue = cacheService.getRedisCache(skuId);
//
//        //判断缓存中是否有数据
//        if (returnValue == null){  //在这里 一定要判断是真没有还是假没有  真没有 null  假没有  ”x“
//            log.info("缓存未命中  正在查询位图中是否含有数据");
//            //通过位图判断 数据库是否有数据
//            Boolean contain = cacheService.mightContain(skuId);
//            if (!contain){
//                //说明位图中没有相应的元素
//                log.info("位图中不含有相应的元素 疑似攻击请求 查询结束");
//                return null;
//            }
//
//            log.info("位图中存在数据  准备回源");
//
//            //准备拦截缓存击穿问题
//            //加锁
//
//            boolean tryLock = lock.tryLock();//尝试加锁
//            log.info("正在尝试加锁  防止缓存击穿");
//            if (tryLock){
//                //如果抢锁成功 执行回源查询
//                //3.表示缓存中没有数据 回源查询
//                log.info("加锁成功");
//                returnValue = this.getSkuDetailVo(skuId);
//
//                //4.将查询到的数据缓存在Redis中  null之缓存都可能不会被调用
//                //防止数据空中查到空值，所以直接传skuId  而不是从returnValue中获取skuId
//                cacheService.saveData(returnValue,skuId);
//
//                //解锁
//                lock.unlock();
//            }else{
//                //抢锁失败
//                log.info("加锁失败 0.5秒后直接从缓存中获取数据");
//                try {
//                    TimeUnit.MILLISECONDS.sleep(500);
//
//                    //再从缓存中获取
//                    returnValue = cacheService.getRedisCache(skuId);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }else{
//            log.info("缓存命中");
//        }
//
//        return returnValue;
//    }


    /**
     * 封装商品详情数据
     * @param skuId
     * @return
     */
//    public SkuDetailVo getDetailDataNullSave(Long skuId) {
//
//        /**
//         * 引入redis作为缓存:
//         *      缓存的使用场景：
//         *          热点数据
//         *          读多写少的数据（不经常修改）
//         */
//        //1.先从缓存中查询数据
//        String jsonString = redisTemplate.opsForValue().get(RedisConst.SKUKEY_PREFIX + skuId);
//
//        //2.判断缓存中是否有数据
//        if (StringUtils.isEmpty(jsonString)){
//            //表示缓存未命中
//            log.info("缓存未命中，需要回源查找数据");
//            SkuDetailVo skuDetailVo = this.getSkuDetailVo(skuId);
//
//            String jsonData = "x";
//            //3.放入缓存 即使是null值  也需要放入到Redis中
//            //防止缓存穿透攻击
//            //3.1判断数据库中是否查询到对应的数据
//            if (skuDetailVo != null){
//                //表示 在数据库中查询到了对应的值
//                jsonData = JSON.toJSONString(skuDetailVo);
//            }
//            //3.2 将数据放入到缓存中  设置缓存时间为7天
//            redisTemplate.opsForValue().set(RedisConst.SKUKEY_PREFIX + skuId,jsonData,7, TimeUnit.DAYS);
//
//            //3.3将查询到的数据返回
//            return skuDetailVo;
//        }
//        //4.表示缓存中存在该数据
//        /**
//         * 4.1 判断是真存在还是假存在
//         *      真存在：    json真数据
//         *      假存在     "x"
//         */
//        if ("x".equals(jsonString)){
//            //表示数据假存在
//            log.info("疑似数据攻击请求");
//            return null;
//        }
//
//        SkuDetailVo skuDetailVo = JSON.parseObject(jsonString, SkuDetailVo.class);
//        return skuDetailVo;
//    }

//    @Override
//    public SkuDetailVo getDetailData(Long skuId) {
//
//        /**
//         * 优化商品详情页面2：引入Map作为本地缓存  提高效率
//         *      缺点：
//         *          内存容量有限 并不能放下所有商品
//         *          微服务场景下  缓存命中率较低
//         *          数据修改不容易  一次修改必须要通知所有的持有副本的微服务
//         */
//        SkuDetailVo detailVo = cache.get("sku:info:" + skuId);
//        if (detailVo == null){
//
//            //未命中缓存
//            System.out.println("未命中缓存  正在回源查找");
//            //此时再回源查找
//            SkuDetailVo skuDetailVo = this.getSkuDetailVo(skuId);
//            //然后写入到map缓存中
//            cache.put("sku:info:" + skuId,skuDetailVo);
//            return skuDetailVo;
//        }else{
//            //命中缓存
//            System.out.println("命中缓存");
//            return detailVo;
//        }

        //商品详情（含图片）
//        SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
//        List<SkuImage> images = skuDetailFeignClient.getSkuInages(skuId).getData();
//        skuInfo.setSkuImageList(images);
//        skuDetailVo.setSkuInfo(skuInfo);
//
//        //当前商品精确的完整分类信息
//        //从skuInfo中获取c3Id
//        Long c3Id = skuInfo.getCategory3Id();
//        CategoryTreeVo categoryTreeVo = skuDetailFeignClient.getCategoryTreeVoByC3Id(c3Id).getData();
//        CategoryViewDTO viewDTO = this.getCategoryViewDTO(categoryTreeVo);
//        skuDetailVo.setCategoryView(viewDTO);
//
//        BigDecimal price = skuDetailFeignClient.getPrice(skuId).getData();
//        skuDetailVo.setPrice(price);
//
//
//        //根据spuId查询销售属性
//        List<SpuSaleAttr> spuSaleAttrs = skuDetailFeignClient.getSpuSaleAttr(skuInfo.getSpuId(),skuId).getData();
//        skuDetailVo.setSpuSaleAttrList(spuSaleAttrs);
//
//        //查询当前sku的所有兄弟们的所有组合可能性
//        String spuJson = skuDetailFeignClient.getValueSpuJson(skuInfo.getSpuId()).getData();
//        skuDetailVo.setValuesSkuJson(spuJson);

//    }

    //将远程调用的业务逻辑封装成方法
    public SkuDetailVo getSkuDetailVo(Long skuId){
        SkuDetailVo skuDetailVo = new SkuDetailVo();

        /**
         * 优化商品详情页面1：  引入异步编排
         */
        //异步查询商品详情
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //商品详情（含图片）
            SkuInfo skuInfo = skuDetailFeignClient.getSkuInfo(skuId).getData();
            return skuInfo;
        },executor);

        //异步查询商品图片
        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            if (res == null) return;
            List<SkuImage> images = skuDetailFeignClient.getSkuInages(skuId).getData();
            res.setSkuImageList(images);
            skuDetailVo.setSkuInfo(res);
        },executor);

        //异步查询：当前商品精确的完整分类信息
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            if (res == null) return;
            Long c3Id = res.getCategory3Id();
            CategoryTreeVo categoryTreeVo = skuDetailFeignClient.getCategoryTreeVoByC3Id(c3Id).getData();
            CategoryViewDTO viewDTO = this.getCategoryViewDTO(categoryTreeVo);
            skuDetailVo.setCategoryView(viewDTO);
        },executor);

        //异步查询商品价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
           try {
               BigDecimal price = skuDetailFeignClient.getPrice(skuId).getData();
               skuDetailVo.setPrice(price);
           }catch (Exception e){

           }
        },executor);

        //异步查询：根据spuId查询销售属性
        CompletableFuture<Void> spuSaleAttrFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            if (res == null) return;
            List<SpuSaleAttr> spuSaleAttrs = skuDetailFeignClient.getSpuSaleAttr(res.getSpuId(), skuId).getData();
            skuDetailVo.setSpuSaleAttrList(spuSaleAttrs);
        },executor);

        //异步查询：查询当前sku的所有兄弟们的所有组合可能性
        CompletableFuture<Void> jsonFuture = skuInfoFuture.thenAcceptAsync((res) -> {
            if (res == null) return;
            String spuJson = skuDetailFeignClient.getValueSpuJson(res.getSpuId()).getData();
            skuDetailVo.setValuesSkuJson(spuJson);
        },executor);

        //等所有异步任务执行完成之后 放行
        CompletableFuture.allOf(imageFuture,categoryFuture,priceFuture,spuSaleAttrFuture,jsonFuture).join();

        return skuDetailVo;
    }

    /**
     * 封装CategoryViewDTO
     * @param categoryTreeVo
     * @return
     */
    private CategoryViewDTO getCategoryViewDTO(CategoryTreeVo categoryTreeVo){
        CategoryViewDTO viewDTO = new CategoryViewDTO();
        viewDTO.setCategory1Id(categoryTreeVo.getCategoryId());
        viewDTO.setCategory1Name(categoryTreeVo.getCategoryName());
        //因为该list中肯定只有一个 所以获取第一个
        CategoryTreeVo child1 = categoryTreeVo.getCategoryChild().get(0);
        viewDTO.setCategory2Id(child1.getCategoryId());
        viewDTO.setCategory2Name(child1.getCategoryName());
        CategoryTreeVo child2 = child1.getCategoryChild().get(0);
        viewDTO.setCategory3Id(child2.getCategoryId());
        viewDTO.setCategory3Name(child2.getCategoryName());
       return viewDTO;
    }

}
