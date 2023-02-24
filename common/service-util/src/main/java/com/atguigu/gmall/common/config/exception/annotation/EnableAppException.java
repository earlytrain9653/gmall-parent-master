package com.atguigu.gmall.common.config.exception.annotation;

import com.atguigu.gmall.common.config.exception.ExceptionAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-10 22:14 星期六
 * description:
 */
@Import(ExceptionAutoConfiguration.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAppException {
}
