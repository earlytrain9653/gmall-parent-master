package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.entity.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.vo.LoginSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨林
 * @create 2022-12-15 10:38 星期四
 * description:
 */
@RestController
@RequestMapping("/api/user")
public class LoginController {

    @Autowired
    UserInfoService userInfoService;

    /**
     * 登录
     * @param userInfo
     * @return
     */
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo userInfo){
        LoginSuccessVo vo = userInfoService.login(userInfo);
        return Result.ok(vo);
    }

    /**
     * 退出登录
     *      删除用户在Redis中的登录信息
     * @return
     */
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token,HttpServletRequest request){

        //删除redis中的数据
        userInfoService.logout(token);

        return Result.ok();
    }

}
