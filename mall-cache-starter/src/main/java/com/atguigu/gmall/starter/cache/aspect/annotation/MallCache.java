package com.atguigu.gmall.starter.cache.aspect.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨林
 * @create 2022-12-09 19:10 星期五
 * description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MallCache {

    /**
     * 指定缓存用的key  支持一个动态表达式
     *      1.#{} 内部的东西会被计算
     *      2.#变量名  #{args}代表取出所有方法参数
     * @return
     */
    String cacheKey() default "";

    /**
     * 指定位图的名字  位图中的数据都在Redis中 所以名字很重要  要用这个名字先找到位图
     * @return
     */
    String bitMapName() default "";

    /**
     * 指定位图中需要判定的值  支持表达式
     *      如果不写位图  默认就不用位图
     * @return
     */
    String bitMapKey() default "";

    /**
     * 指定一个锁用的key  支持动态表达式
     * @return
     */
    String lockKey() default "";

    /**
     * 过期时间
     * @return
     */
    long ttl() default 300L;

    /**
     * 指定过期时间的时间单位
     * @return
     */
    TimeUnit unit() default TimeUnit.SECONDS;

}
