package com.atguigu.gmall.feign.user;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.entity.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-22 14:49 星期四
 * description:
 */
@RequestMapping("/api/inner/rpc/user")
@FeignClient("service-user")
public interface UserFeignClient {
    /**
     * 获取用户的地址列表
     * @param userId
     * @return
     */
    @GetMapping("/addresses/{userId}")
    Result<List<UserAddress>> getUserAddress(@PathVariable("userId") Long userId);
}
