package com.atguigu.gmall.item.service;

/**
 * @author 杨林
 * @create 2022-12-08 13:11 星期四
 * description:
 */
public interface LockService {
    String lock() throws InterruptedException;

    void unlock(String uuid);
}
