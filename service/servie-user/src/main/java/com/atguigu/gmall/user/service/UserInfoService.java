package com.atguigu.gmall.user.service;

import com.atguigu.gmall.user.entity.UserInfo;
import com.atguigu.gmall.user.vo.LoginSuccessVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

/**
* @author 华为YL
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-12-15 16:37:27
*/
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户登录
     * @param userInfo
     * @return
     */
    LoginSuccessVo login(UserInfo userInfo);

    /**
     * 用户退出
     * @param token
     */
    void logout(String token);
}
