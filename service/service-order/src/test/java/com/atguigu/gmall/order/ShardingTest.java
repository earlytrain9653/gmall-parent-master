package com.atguigu.gmall.order;
import java.math.BigDecimal;
import java.util.Date;

import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 杨林
 * @create 2022-12-21 19:57 星期三
 * description:
 */
@SpringBootTest
public class ShardingTest {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Test
    public void testInsert(){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setConsignee("11");
        orderInfo.setConsigneeTel("11");
        orderInfo.setTotalAmount(new BigDecimal("11"));
        orderInfo.setOrderStatus("11");
        orderInfo.setUserId(3L);
        orderInfo.setPaymentWay("11");
        orderInfo.setDeliveryAddress("11");
        orderInfo.setOrderComment("11");
        orderInfo.setOutTradeNo("11");
        orderInfo.setTradeBody("11");
        orderInfo.setCreateTime(new Date());
        orderInfo.setExpireTime(new Date());
        orderInfo.setProcessStatus("11");
        orderInfo.setTrackingNo("11");
        orderInfo.setParentOrderId(0L);
        orderInfo.setImgUrl("");
        orderInfo.setProvinceId(0L);
        orderInfo.setOperateTime(new Date());
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(new BigDecimal("0"));
        orderInfo.setOriginalTotalAmount(new BigDecimal("0"));
        orderInfo.setFeightFee(new BigDecimal("0"));
        orderInfo.setRefundableTime(new Date());

        orderInfoMapper.insert(orderInfo);
        System.out.println("插入完成");

    }
}
