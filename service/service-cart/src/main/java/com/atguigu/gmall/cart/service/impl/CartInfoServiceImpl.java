package com.atguigu.gmall.cart.service.impl;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.entity.CartInfo;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.feign.item.SkuDetailFeignClient;
import com.atguigu.gmall.feign.product.ProductSkuDetailFeignClient;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.user.vo.UserAuthInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨林
 * @create 2022-12-17 17:19 星期六
 * description:
 */
@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductSkuDetailFeignClient productSkuDetailFeignClient;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public AddCartSuccessVo addToCart(Long skuId, Integer num) {

        //1.得到操作购物车用的key: cart:info:用户标识
        String cartKey = determinCartKey();

        //2.给购物车中添加商品
        SkuInfo skuInfo = addCartItem(cartKey,skuId,num);

        //3.返回前端需要的数据
        AddCartSuccessVo cartSuccessVo = new AddCartSuccessVo();
        cartSuccessVo.setSkuInfo(skuInfo);
        cartSuccessVo.setSkuNum(num);


        return cartSuccessVo;
    }

    /**
     * 给购车中添加商品
     * @param cartKey
     * @param skuId
     * @param num
     * @return
     */
    public SkuInfo addCartItem(String cartKey, Long skuId, Integer num) {

        SkuInfo result = null;
        //1.先看购物车中有无此商品
        BoundHashOperations<String, String, String> cart = getCart(cartKey);
        if (cart.hasKey(skuId.toString())) {
            //1.1有 修改数量
            String json = cart.get(skuId.toString());
            //获取到原来的购物车信息
            CartInfo cartInfo = getCartInfo(cartKey,skuId);
            //原来的数量加上新加入的数量
            cartInfo.setSkuNum(cartInfo.getSkuNum() + num);
            //修改购物车中的实时价格
            BigDecimal price = productSkuDetailFeignClient.getPrice(skuId).getData();
            cartInfo.setSkuPrice(price);
            //保存到Redis
            cart.put(skuId.toString(),JSON.toJSONString(cartInfo));
            SkuInfo skuInfo = convertCartInfo2SkuInfo(cartInfo);
            result = skuInfo;
        }else{
            //1.2没有  新增
            Result<SkuInfo> skuInfo = productSkuDetailFeignClient.getSkuInfo(skuId);
            //保存到购物车
            CartInfo cartInfo = convertSkuInfo2CartInfo(skuInfo.getData(),num);
            cart.put(skuId.toString(), JSON.toJSONString(cartInfo));
            result = skuInfo.getData();
        }

        return result;
    }

    /**
     * 将CartIntem转换成SkuInfo
     * @param cartInfo
     * @return
     */
    private SkuInfo convertCartInfo2SkuInfo(CartInfo cartInfo) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(cartInfo.getSkuId());
        skuInfo.setPrice(cartInfo.getSkuPrice());
        skuInfo.setSkuName(cartInfo.getSkuName());
        skuInfo.setSkuDefaultImg(cartInfo.getImgUrl());
        return skuInfo;
    }

    /**
     * 从指定购物车中得到一个商品信息
     * @param cartKey
     * @param skuId
     * @return
     */
    public CartInfo getCartInfo(String cartKey, Long skuId) {
        //从Redis拿到这个商品
        String json = redisTemplate.opsForHash().get(cartKey, skuId.toString()).toString();
        if (!StringUtils.isEmpty(json)){
            CartInfo cartInfo = JSON.parseObject(json, CartInfo.class);
            return cartInfo;
        }
        return null;
    }

    /**
     * 返回购物车中所有商品的列表
     * @param cartKey
     * @return
     */
    @Override
    public List<CartInfo> getCartInfoList(String cartKey) {
        //得到购物车
        BoundHashOperations<String, String, String> cart = getCart(cartKey);

        //拿到购物车中的所有值 key不用管
        //数据需要按照商品加入购物车的时间有序排列  最后一个加入购物车在最上面显示
        List<CartInfo> cartInfos = cart.values().stream().map((str) -> {
            CartInfo cartInfo = JSON.parseObject(str, CartInfo.class);
            return cartInfo;
        }).sorted((o1,o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());

        //同步最新价格
        CompletableFuture.runAsync(() -> {
            syncPrice(cartKey, cartInfos);
        },threadPoolExecutor);

        return cartInfos;
    }

    private void syncPrice(String cartKey, List<CartInfo> cartInfos) {
        cartInfos
                .stream()
                .forEach((item) -> {
            //查询价格
            BigDecimal realPrice = productSkuDetailFeignClient.getPrice(item.getSkuId()).getData();
            if (Math.abs(item.getSkuPrice().doubleValue() - realPrice.doubleValue()) >= 0.00001) {
                //价格发生了变化
                item.setSkuPrice(realPrice);
                save(cartKey,item);
            }
        });
    }


    /**
     * 修改购物车商品数量
     * @param cartKey
     * @param skuId
     * @param num
     */
    @Override
    public void addToCartList(String cartKey, Long skuId, Integer num) {
        CartInfo cartInfo = getCartInfo(cartKey, skuId);
        if (num == 1 || num == -1){
            Integer skuNum = cartInfo.getSkuNum();
            skuNum = skuNum + num;
            cartInfo.setSkuNum(skuNum);
            save(cartKey,cartInfo);
        }else{
            //直接修改数量
            cartInfo.setSkuNum(num);
            save(cartKey,cartInfo);
        }

    }

    /**
     * 修改选中状态  0：未选中  1：选中
     * @param cartKey
     * @param skuId
     * @param isChecked
     */
    @Override
    public void checkCart(String cartKey, Long skuId, Integer isChecked) {
        if (!(isChecked == 0 || isChecked == 1)){
            throw new GmallException(ResultCodeEnum.INVALID_PARAM);
        }
        CartInfo cartInfo = getCartInfo(cartKey, skuId);
        cartInfo.setIsChecked(isChecked);
        save(cartKey,cartInfo);
    }

    /**
     * 删除购物车中的某个商品
     * @param cartKey
     * @param skuId
     */
    @Override
    public void delete(String cartKey, Long skuId) {
        redisTemplate.opsForHash().delete(cartKey,skuId.toString());
    }

    /**
     * 删除购物车中选中的商品
     * @param cartKey
     */
    @Override
    public void deleteChecked(String cartKey) {
        List<CartInfo> checked = getChecked(cartKey);

        List<Long> checkedIds = checked
                                .stream()
                                .map((item) -> item.getSkuId())
                                .collect(Collectors.toList());
        redisTemplate.opsForHash().delete(cartKey,checkedIds.toArray());
    }

    @Override
    public List<CartInfo> getChecked(String cartKey) {
        //List<CartInfo> cartInfoList = getCartInfoList(cartKey);

        //得到购物车
        List<CartInfo> collect = redisTemplate.opsForHash()
                .values(cartKey)
                .stream()
                .map(item -> JSON.parseObject(item.toString(), CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .filter((o1) -> o1.getIsChecked() == 1)
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * 专供购物车列表使用  展示购物车中所有商品
     * @return
     */
    @Override
    public List<CartInfo> displayItems() {
        //1.先判断用户是否登录了 且临时购物车有数据

        //得到临时购物车的key
        String tempCartKey = getCustomCartKey("TempId");
        //得到用户购物车的key
        String userCartKey = getCustomCartKey("UserId");
        //用户没登录 直接返货临时购物车的所有数据
        if (userCartKey == null){

            //给临时购物车设置过期时间
            Long expire = redisTemplate.getExpire(tempCartKey);
            if (expire < 0) {
                redisTemplate.expire(tempCartKey,365, TimeUnit.DAYS);
            }
            //redisTemplate.expire(tempCartKey,365, TimeUnit.DAYS);  //这样设置过期时间  会自动续期

            List<CartInfo> cartInfoList = getCartInfoList(tempCartKey);
            return cartInfoList;
        }

        //如果用户登录 判断是否需要合并
        try {
            Long tempSize = redisTemplate.opsForHash().size(tempCartKey);
            if (tempSize > 0){
                //合并：把临时购物车的每一个商品拿出来放到用户购物车
                List<CartInfo> tempItems = getCartInfoList(tempCartKey);
                for (CartInfo tempItem : tempItems) {
                    addCartItem(userCartKey,tempItem.getSkuId(),tempItem.getSkuNum());
                }
                //合并结束  删除临时购物车
                redisTemplate.delete(tempCartKey);

            }
        }catch (Exception e){
            //说明合并期间出错  为了展示依然能进行  必须处理调异常
        }

        List<CartInfo> cartInfoList = getCartInfoList(userCartKey);
        return cartInfoList;
    }


    /**
     * 根据传入的标识  获取相应的key
     * @param flag
     */
    private String getCustomCartKey(String flag) {
        HttpServletRequest request = UserAuthUtils.request();
        String header = request.getHeader(flag);
        if (StringUtils.isEmpty(header)){
            return null;
        }

        return RedisConst.CART_INFO + header;
    }

    /**
     * 保存商品到
     * @param cartKey
     * @param cartInfo
     */
    private void save(String cartKey, CartInfo cartInfo) {

        //购物车中单个商品不超过200
        if (cartInfo.getSkuNum() >= RedisConst.CART_ITEM_NUM_LIMIT) {
            throw new GmallException(ResultCodeEnum.CART_ITEM_NUM_OVERFLOW);
        }

        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        hashOps.put(cartInfo.getSkuId().toString(),JSON.toJSONString(cartInfo));

        //购物车总商品量不超过200
        Long size = redisTemplate.opsForHash().size(cartKey);
        if (size >= 200){
            redisTemplate.opsForHash().delete(cartKey,cartInfo.getSkuId().toString());
            throw new GmallException(ResultCodeEnum.CART_ITEM_COUNT_OVERFLOW);
        }

    }

    /**
     * 将skuInfo 转换成 CartInfo
     * @param skuInfo
     * @return
     */
    private CartInfo convertSkuInfo2CartInfo(SkuInfo skuInfo,Integer num) {
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        Long userId = userAuthInfo.getUserId();
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuInfo.getId());
        cartInfo.setCartPrice(skuInfo.getPrice());  //购物车价格
        cartInfo.setSkuPrice(skuInfo.getPrice());  //实时价格
        cartInfo.setSkuNum(num);
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);  //1代表选中状态   0代表不选
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());

        return cartInfo;
    }

    /**
     * 决定购物车用哪个键
     * @return
     */
    public String determinCartKey() {
        //1.获取到当前用户信息  工具类方法调用也是同一个线程
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        String tempId = userAuthInfo.getTempId();
        Long userId = userAuthInfo.getUserId();
        String cartKey = RedisConst.CART_INFO;
        if (userId != null){
            return cartKey = cartKey + userId;
        }else {
            return cartKey = cartKey + tempId;
        }
    }

    /**
     * 获取当前购物车
     * @param cartKey
     * @return
     */
    private BoundHashOperations<String,String,String> getCart(String cartKey){
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        return cart;
    }
}