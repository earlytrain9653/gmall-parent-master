package com.atguigu.gmall.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.user.vo.LoginSuccessVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.entity.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author 华为YL
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-12-15 16:37:27
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 用户登录
     * @param userInfo
     * @return
     */
    @Override
    public LoginSuccessVo login(UserInfo userInfo) {
        String loginName = userInfo.getLoginName();
        String passwd = userInfo.getPasswd();
        //对前端传入的passwd进行MD5加密
        String encrypt = MD5.encrypt(passwd);

        //判断数据库中是否存在登录用户
        UserInfo info = lambdaQuery().eq(UserInfo::getLoginName, loginName)
                .eq(UserInfo::getPasswd, encrypt)
                .one();
        //任何业务不预期的行为  由全局异常捕获 进行统一异常处理
        if (info == null){
            throw new GmallException(ResultCodeEnum.LOGIN_ERROR);
        }

        //响应方式 以json方式交给前端  前端自动处理（放大域名  设置7天有效）
        LoginSuccessVo vo = new LoginSuccessVo();
        //生成一个token信息
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        vo.setToken(token);
        vo.setUserId(info.getId());
        vo.setNickName(info.getNickName());

        //服务端要共享token与用户信息对应信息
        //保存在Redis中
        redisTemplate.opsForValue()
                .set(RedisConst.LOGIN_USER + token, JSON.toJSONString(info),7, TimeUnit.DAYS);

        //给前端返回数据
        return vo;
    }

    /**
     * 用户退出
     * @param token
     */
    @Override
    public void logout(String token) {
        redisTemplate.delete(RedisConst.LOGIN_USER + token);
    }
}




