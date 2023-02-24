package com.atguigu.gmall.common.retryer;

import feign.RetryableException;
import feign.Retryer;

/**
 * @author 杨林
 * @create 2022-12-11 15:41 星期日
 * description:  自定义feign的重试次数
 */

/**
 * 保证业务幂等性
 */
public class NeverRetryer implements Retryer {
    /**
     * 继续还是传播
     *      重试器只要不抛出错误 就会继续重试一次远程调用
     * @param e
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
        //代表永不重试
        throw e;
    }

    @Override
    public Retryer clone() {
        return new NeverRetryer();
    }
}
