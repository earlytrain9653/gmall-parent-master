package com.atguigu.gmall.common.interceptors.annotation;

import com.atguigu.gmall.common.interceptors.UserHeaderFeignInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-17 21:21 星期六
 * description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(UserHeaderFeignInterceptor.class)
public @interface EnableUserAuthFeignInterceptor {
}
