package com.atguigu.gmall.user.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.entity.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-22 14:42 星期四
 * description:
 */
@RestController
@RequestMapping("/api/inner/rpc/user")
public class UserRpcController {

    @Autowired
    UserAddressService userAddressService;

    /**
     * 获取用户的地址列表
     * @param userId
     * @return
     */
    @GetMapping("/addresses/{userId}")
    public Result<List<UserAddress>> getUserAddress(@PathVariable("userId") Long userId){

        List<UserAddress> list = userAddressService.lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .list();
        return Result.ok(list);
    }
}
