package com.atguigu.gmall.order.biz.impl;
import com.atguigu.gmall.mq.logistic.OrderLogisticMsg;
import com.google.common.collect.Lists;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gmall.cart.entity.CartInfo;
import com.atguigu.gmall.common.config.mq.MQService;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.PaymentWay;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.ProductSkuDetailFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.mq.ware.WareStockResultMsg;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.vo.OrderSplitResp;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.order.vo.SkuWare;
import com.atguigu.gmall.user.entity.UserAddress;

import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.vo.OrderConfirmRespVo;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 杨林
 * @create 2022-12-21 22:07 星期三
 * description:
 */
@Slf4j
@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductSkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    MQService mqService;

    /**
     * 获取订单确认数据
     * @return
     */

    @Override
    public OrderConfirmRespVo getConfirmData() {
        OrderConfirmRespVo respVo = new OrderConfirmRespVo();
        //1.商品列表
        //远程找购物车要到所有选中的商品
        List<CartInfo> data = cartFeignClient.getChecked().getData();
        List<OrderConfirmRespVo.SkuDetail> detailList = data.stream()
                .map((item) -> {
                    //商品列表
                    OrderConfirmRespVo.SkuDetail skuDetail = new OrderConfirmRespVo.SkuDetail();
                    skuDetail.setSkuId(item.getSkuId());
                    skuDetail.setImgUrl(item.getImgUrl());
                    skuDetail.setSkuName(item.getSkuName());
                    //商品的实时价格
                    BigDecimal price = skuDetailFeignClient.getPrice(item.getSkuId()).getData();
                    skuDetail.setOrderPrice(price);
                    skuDetail.setSkuNum(item.getSkuNum());
                    //查询这个商品的库存状态
                    String hasStock = wareFeignClient.hasStock(item.getSkuId(), item.getSkuNum());
                    skuDetail.setHasStock(hasStock);
                    return skuDetail;
                }).collect(Collectors.toList());
        respVo.setDetailArrayList(detailList);

        //2商品的总数量
        Integer totalNum = detailList.stream()
                .map(OrderConfirmRespVo.SkuDetail::getSkuNum)
                .reduce((o1, o2) -> o1 + o2)
                .get();
        respVo.setTotalNum(totalNum);

        //总金额
        BigDecimal bigDecimal = detailList.stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();
        respVo.setTotalAmount(bigDecimal);

        //获取收获地址列表
        Long userId = UserAuthUtils.getUserId();
        List<UserAddress> userAddresses = userFeignClient.getUserAddress(userId).getData();
        respVo.setUserAddressList(userAddresses);

        String tradeNo = "ATGUIGU-" + System.currentTimeMillis() + "-" + userId;
        respVo.setTradeNo(tradeNo);
        //Redis中放一份tradeNo  防止重复提交
        redisTemplate.opsForValue().set(RedisConst.REPEAT_TOKEN +tradeNo,"1",2, TimeUnit.MINUTES);
        return respVo;
    }

    /**
     * 提交订单
     * @param submitVo
     * @param tradeNo
     * @return
     */
    @Transactional
    @Override
    public Long submitOrder(OrderSubmitVo submitVo, String tradeNo) {

        //校验：后端永远不要相信前端带来的数据    能校验  则校验
        //参数校验：交给jsr303校验注解来做
        //业务校验：
        // - 令牌校验
        // - 校验库存
        // - 校验价格

        //1.校验令牌
        Boolean delete = redisTemplate.delete(RedisConst.REPEAT_TOKEN + tradeNo);
        if (!delete){
            throw new GmallException(ResultCodeEnum.REPEAT_REQUEST);
        }

        //2.校验库存
        List<OrderSubmitVo.OrderDetailListDTO> noStockSku = submitVo.getOrderDetailList()
                .stream()
                .filter((item) -> "0".equals(wareFeignClient.hasStock(item.getSkuId(), item.getSkuNum())))
                .collect(Collectors.toList());
        if (noStockSku != null && noStockSku.size() > 0){
            String skuNames = noStockSku.stream()
                    .map(OrderSubmitVo.OrderDetailListDTO::getSkuName)
                    .reduce((o1, o2) -> o1 + ";" + o2)
                    .get();
            GmallException gmallException = new GmallException(skuNames + "; 没库存", ResultCodeEnum.NO_STOCK.getCode());
            throw gmallException;
        }

        //3.校验价格
        //获取改变价格的商品信息列表
        List<OrderSubmitVo.OrderDetailListDTO> priceChangeSkus = submitVo.getOrderDetailList()
                .stream()
                .filter((item) -> {
                    BigDecimal orderPrice = item.getOrderPrice();
                    //获取实时价格
                    BigDecimal price = skuDetailFeignClient.getPrice(item.getSkuId()).getData();
                    return Math.abs(orderPrice.subtract(price).doubleValue()) >= 0.00001;
                }).collect(Collectors.toList());//得到价格变化的商品
        if (priceChangeSkus != null && priceChangeSkus.size() > 0){
            String skuNames = priceChangeSkus.stream().map(OrderSubmitVo.OrderDetailListDTO::getSkuName)
                    .reduce((o1, o2) -> o1 + ";" + o2)
                    .get();//价格改变的商品名

            GmallException gmallException = new GmallException(skuNames + "; 价格变话，请刷新页面重新确认",
                    ResultCodeEnum.PRICE_CHANGE.getCode());
            throw gmallException;
        }

        //1.给OrderInfo保存订单基本信息
        OrderInfo orderInfo = prepareOrderInfo(submitVo,tradeNo);
        orderInfoService.save(orderInfo);

        //订单用雪花算法生成的id
        Long orderId = orderInfo.getId();

        //30min以后关闭
//        ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
//        service.schedule(schedule -> {
//            closeOrder(orderInfo);
//        },30,TimeUnit.MINUTES);

        //2.给order_detail保存订单明细信息
        List<OrderDetail> orderDetails = prepareOrderDetails(submitVo,orderInfo);
        orderDetailService.saveBatch(orderDetails);

        //发送订单创建成功消息
        mqService.send(orderInfo, MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_CREATE_RK);

        //删除购物车选中的商品
        cartFeignClient.deleteChecked();
        return orderId;
    }

    /**
     * 关闭订单
     * @param id
     * @param userId
     */
    @Override
    public void closeOrder(Long id, Long userId) {

        ProcessStatus closed = ProcessStatus.CLOSED;

        boolean update = orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus, closed.getOrderStatus().name())
                .set(OrderInfo::getProcessStatus, closed)
                .eq(OrderInfo::getId, id)
                .eq(OrderInfo::getUserId, userId)
                //以下两个eq保证关单的幂等性
                .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.name())
                .eq(OrderInfo::getProcessStatus, ProcessStatus.UNPAID.name())
                .update();
        log.info("订单关闭");
    }

    /**
     * 订单修改为已支付
     * @param out_trade_no
     * @param userId
     */
    @Override
    public void payedOrder(String out_trade_no, Long userId) {
        //关单消息和支付消息如果同时抵达，无论谁先执行，最终结果都应该以支付状态为准
        //1.关单先运行，改成以关闭    支付后运行  就应该改回来已支付
        //2.支付先运行 改为以支付   关单后运行就什么也不做

        ProcessStatus payed = ProcessStatus.PAID;

        //修改订单为已支付状态
        boolean update = orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus, payed.getOrderStatus().name())
                .set(OrderInfo::getProcessStatus, payed.name())
                .eq(OrderInfo::getUserId, userId)
                .eq(OrderInfo::getOutTradeNo, out_trade_no)
                .in(OrderInfo::getOrderStatus, OrderStatus.UNPAID.name(), OrderStatus.CLOSED.name())
                .in(OrderInfo::getProcessStatus, ProcessStatus.UNPAID.name(), ProcessStatus.CLOSED.name())
                .update();
        log.info("修改订单为已支付状态");
    }

    @Override
    public void updateOrderStockStatus(WareStockResultMsg result) {
        //1.最终订单要修改成的状态
        ProcessStatus status = ProcessStatus.WAITING_DELEVER;
        switch (result.getStatus()) {
            case "DEDUCTED":
                status = ProcessStatus.WAITING_DELEVER;  //扣减成功  等待发货
                break;
            case "OUT_OF_STOCK":
                status = ProcessStatus.STOCK_EXCEPTION;  //扣减失败  等待调货
                break;
        }

        OrderInfo info = orderInfoService.getById(result.getOrderId());

        //注意：一旦使用消息队列  就和Http没有任何关系  以前透传的所有东西都不能用
        //如果想要后来用的字段  发消息的时候就一定带上
        //2.修改订单状态
        boolean update = orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus, status.getOrderStatus().name())
                .set(OrderInfo::getProcessStatus, status.name())
                .eq(OrderInfo::getId, info.getId())
                .eq(OrderInfo::getUserId, info.getUserId())
                .eq(OrderInfo::getOrderStatus, OrderStatus.PAID.name())
                .eq(OrderInfo::getProcessStatus, ProcessStatus.PAID.name())
                .update();
        log.info("订单库存状态更新完成");

        //下电子面单  进行发货
        if ("DEDUCTED".equals(result.getStatus())){
            OrderLogisticMsg msg = new OrderLogisticMsg();
            msg.setId(info.getId());
            msg.setUserId(info.getUserId());
            //给等待物流配送的订单队列发送消息
            mqService.send(msg,MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_LOGISTIC_RK);
        }

    }

    /**
     * 拆单
     * @param orderId
     * @param json
     * @return
     */
    @Override
    public List<OrderSplitResp> orderSplit(Long orderId, String json) {
        //大订单(orderId)拆分成 子订单（根据大订单中所有商品的库存分布，拆分成子订单  把子订单都存到数据库）
        //得到大订单中所有商品的库存分布
        List<SkuWare> skuWares = JSON.parseObject(json, new TypeReference<List<SkuWare>>(){
        });

        //从数据库中查出大订单
        OrderInfo parentOrder = orderInfoService.getById(orderId);

        //拿到大订单中所有商品
        List<OrderDetail> orderDetails = orderDetailService.lambdaQuery()
                .eq(OrderDetail::getOrderId, parentOrder.getId())
                .eq(OrderDetail::getUserId, parentOrder.getUserId())
                .list();

        AtomicInteger i = new AtomicInteger();

        //拆分子订单
        List<OrderInfo> orderInfos = skuWares.stream()
                .map(item -> {
                    OrderInfo childOrder = new OrderInfo();
                    childOrder.setConsignee(parentOrder.getConsignee());
                    childOrder.setConsigneeTel(parentOrder.getConsigneeTel());
                    //子订单总额：子订单负责的商品的总额
                    List<Long> skuIds = item.getSkuIds();  //当前子订单负责所有商品的id

                    //拿到子订单负责的所有商品
                    List<OrderDetail> childDetails = orderDetails.stream()
                            .filter(obj -> skuIds.contains(obj.getSkuId()))
                            .collect(Collectors.toList());
                    childOrder.setOrderDetails(childDetails);

                    //拿到子订单负责所有商品的总价
                    BigDecimal totalAmount = childDetails.stream()
                            .map(o1 -> o1.getOrderPrice().multiply(new BigDecimal(o1.getSkuNum())))
                            .reduce((o1, o2) -> o1.add(o2))
                            .get();
                    childOrder.setTotalAmount(totalAmount);

                    childOrder.setOrderStatus(parentOrder.getOrderStatus());
                    childOrder.setUserId(parentOrder.getUserId());
                    childOrder.setPaymentWay(parentOrder.getPaymentWay());
                    childOrder.setDeliveryAddress(parentOrder.getDeliveryAddress());
                    childOrder.setOrderComment(parentOrder.getOrderComment());
                    childOrder.setOutTradeNo(i.getAndIncrement() + "_"+parentOrder.getOutTradeNo());

                    childOrder.setTradeBody(childDetails.get(0).getSkuName());
                    childOrder.setCreateTime(new Date());
                    childOrder.setExpireTime(parentOrder.getExpireTime());
                    childOrder.setProcessStatus(parentOrder.getProcessStatus());
                    childOrder.setTrackingNo("");
                    childOrder.setParentOrderId(parentOrder.getId());
                    childOrder.setImgUrl(childDetails.get(0).getImgUrl());
                    childOrder.setProvinceId(0L);
                    childOrder.setOperateTime(new Date());
                    childOrder.setActivityReduceAmount(new BigDecimal("0"));
                    childOrder.setCouponAmount(new BigDecimal("0"));
                    childOrder.setOriginalTotalAmount(totalAmount);
                    childOrder.setFeightFee(new BigDecimal("0"));
                    childOrder.setRefundableTime(new Date());
                    childOrder.setWareId(item.getWareId());

                    return childOrder;

                }).collect(Collectors.toList());

        for (OrderInfo orderInfo : orderInfos) {
            //保存子订单
            boolean save = orderInfoService.save(orderInfo);
            Long id = orderInfo.getId();

            //保存子订单的明细
            List<OrderDetail> details = orderInfo.getOrderDetails().stream()
                    .map(item -> {
                        item.setOrderId(id);  //回填子订单id
                        return item;
                    }).collect(Collectors.toList());
            orderDetailService.saveBatch(details);
        }

        //把父订单状态改为已拆分
        boolean update = orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus, OrderStatus.SPLIT.name())
                .set(OrderInfo::getProcessStatus, ProcessStatus.SPLIT.name())
                .eq(OrderInfo::getId, parentOrder.getId())
                .eq(OrderInfo::getUserId, parentOrder.getUserId())
                .update();

        List<Long> ids = orderInfos.stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        log.info("拆单完成  大订单：{},拆解为：{}",parentOrder.getId(),ids);

        //准备响应结果
        List<OrderSplitResp> collect = orderInfos.stream()
                .map(item -> {
                    OrderSplitResp splitResp = new OrderSplitResp();
                    splitResp.setOrderId(item.getId());
                    splitResp.setUserId(item.getUserId());
                    splitResp.setConsignee(item.getConsignee());
                    splitResp.setConsigneeTel(item.getConsigneeTel());
                    splitResp.setOrderComment(item.getOrderComment());
                    splitResp.setOrderBody(item.getTradeBody());
                    splitResp.setDeliveryAddress(item.getDeliveryAddress());
                    splitResp.setPaymentWay("2");
                    splitResp.setWareId(item.getWareId());

                    //订单明细数据
                    List<OrderDetail> details = item.getOrderDetails();

                    List<OrderSplitResp.Sku> skuList = details.stream()
                            .map(o1 -> {
                                OrderSplitResp.Sku sku = new OrderSplitResp.Sku();
                                sku.setSkuId(o1.getSkuId());
                                sku.setSkuNum(o1.getSkuNum());
                                sku.setSkuName(o1.getSkuName());
                                return sku;
                            }).collect(Collectors.toList());

                    splitResp.setDetails(skuList);

                    return splitResp;
                }).collect(Collectors.toList());

        return collect;
    }

    /**
     * 保存秒杀单
     * @param info
     * @return
     */
    @Override
    public Long saveSeckillOrder(OrderInfo info) {
        //1.保存订单数据
        boolean save = orderInfoService.save(info);
        Long orderId = info.getId();
        //2.保存订单明细
        List<OrderDetail> details = info.getOrderDetails()
                .stream()
                .map(item -> {
                    item.setOrderId(info.getId());  //回填id
                    return item;
                }).collect(Collectors.toList());

        orderDetailService.saveBatch(details);

        return orderId;
    }


    /**
     * 准备order_detail数据
     * @param submitVo
     * @param orderInfo
     * @return
     */
    private List<OrderDetail> prepareOrderDetails(OrderSubmitVo submitVo, OrderInfo orderInfo) {
        List<OrderDetail> details = submitVo.getOrderDetailList()
                .stream()
                .map(item -> {
                    OrderDetail orderDetail = new OrderDetail();

                    orderDetail.setUserId(orderInfo.getUserId());
                    orderDetail.setOrderId(orderInfo.getId());
                    orderDetail.setSkuId(item.getSkuId());
                    orderDetail.setSkuName(item.getSkuName());
                    orderDetail.setImgUrl(item.getImgUrl());
                    orderDetail.setOrderPrice(item.getOrderPrice());
                    orderDetail.setSkuNum(item.getSkuNum());
                    orderDetail.setCreateTime(new Date());
                    orderDetail.setSplitTotalAmount(item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())));
                    orderDetail.setSplitActivityAmount(new BigDecimal("0"));
                    orderDetail.setSplitCouponAmount(new BigDecimal("0"));


                    return orderDetail;
                }).collect(Collectors.toList());

        return details;
    }

    /**
     * 根据前端带来的vo数据  得到order_info数据
     * @param submitVo
     * @param tradeNo
     * @return
     */
    private OrderInfo prepareOrderInfo(OrderSubmitVo submitVo, String tradeNo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setConsignee(submitVo.getConsignee());
        orderInfo.setConsigneeTel(submitVo.getConsigneeTel());
        orderInfo.setDeliveryAddress(submitVo.getDeliveryAddress());
        orderInfo.setOrderComment(submitVo.getOrderComment());

        //计算订单总额
        BigDecimal totalAmount = submitVo.getOrderDetailList()
                .stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();
        orderInfo.setTotalAmount(totalAmount);

        //订单状态
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());

        //用户id
        Long userId = UserAuthUtils.getUserId();
        orderInfo.setUserId(userId);

        //支付方式
        orderInfo.setPaymentWay(PaymentWay.ONLINE.name());

        //对外流水号
        orderInfo.setOutTradeNo(tradeNo);

        //交易体
        String skuName = submitVo.getOrderDetailList().get(0).getSkuName();
        orderInfo.setTradeBody(skuName);

        //创建时间
        orderInfo.setCreateTime(new Date());
        //失效时间  30min不支付  订单失效
        Date date = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        orderInfo.setExpireTime(date);

        //处理状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());

        //物流单编号
        orderInfo.setTrackingNo("");
        //父订单编号
        orderInfo.setParentOrderId(null);

        //图片路径
        String imgUrl = submitVo.getOrderDetailList().get(0).getImgUrl();
        orderInfo.setImgUrl(imgUrl);

        //省id
        orderInfo.setProvinceId(0L);
        //操作时间
        orderInfo.setOperateTime(new Date());
        //促销金额
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        //优惠券金额
        orderInfo.setCouponAmount(new BigDecimal("0"));

        //原价总额
        orderInfo.setOriginalTotalAmount(totalAmount);

        return orderInfo;
    }
}
