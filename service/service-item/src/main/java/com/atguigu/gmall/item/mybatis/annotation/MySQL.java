package com.atguigu.gmall.item.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author 杨林
 * @create 2022-12-14 15:15 星期三
 * description:
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MySQL {
    String value();
}
