package com.atguigu.gmall.common.util;

import com.atguigu.gmall.user.vo.UserAuthInfoVo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨林
 * @create 2022-12-17 20:49 星期六
 * description:用户信息工具类
 */
public class UserAuthUtils {

    public static UserAuthInfoVo getUserAuthInfo(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader("UserId");
        Long uid = null;
        try {
            uid = Long.parseLong(userId);
        }catch (Exception e){
        }
        String tempId = request.getHeader("TempId");
        UserAuthInfoVo userAuthInfoVo = new UserAuthInfoVo();
        userAuthInfoVo.setUserId(uid);
        userAuthInfoVo.setTempId(tempId);
        return userAuthInfoVo;
    }

    /**
     * 获取老请求
     *
     * @return
     */
    public static HttpServletRequest request() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    public static Long getUserId() {
        HttpServletRequest request = request();
        String userId = request.getHeader("UserId");
        if (StringUtils.isEmpty(userId)){
            return null;
        }
        return Long.parseLong(userId);
    }
}
