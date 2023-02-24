package com.atguigu.gmall.seckill.biz;

import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.seckill.vo.SeckillOrderConfirmVo;
import com.atguigu.gmall.seckill.vo.SeckillOrderSubmitVo;

/**
 * @author 杨林
 * @create 2022-12-27 22:46 星期二
 * description:
 */
public interface SeckillBizService {

    /**
     * 生成秒杀码
     * @param skuId
     * @return
     */
    String generateSeckillCode(Long skuId);

    /**
     * 秒杀下单
     * @param skuId
     * @param skuIdStr
     */
    void seckillOrder(Long skuId, String skuIdStr);

    /**
     * 检查秒杀单状态
     * @param skuId
     * @return
     */
    ResultCodeEnum checkOrder(Long skuId);

    /**
     * 获取秒杀单数据
     * @param code
     * @return
     */
    SeckillOrderConfirmVo getSeckillOrderInfo(String code);

    /**
     * 提交秒杀单
     * @param submitVo
     * @return
     */
    Long submitOrder(SeckillOrderSubmitVo submitVo);
}
