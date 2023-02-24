package com.atguigu.gmall.starter.cache.aspect;

/**
 * @author 杨林
 * @create 2022-12-09 13:51 星期五
 * description:
 */

import com.atguigu.gmall.starter.cache.aspect.annotation.MallCache;

import com.atguigu.gmall.starter.cache.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 商品详情页面优化：引入AOP机制  实现缓存
 */
@Slf4j
//@Component  //加入容器中  才能生效  //利用自动配置类将其放如到容器中
@Aspect  //声明这是一个切面  保证切面的通用性
public class CacheAspect {

    @Autowired
    CacheService cacheService;

    @Autowired
    RedissonClient redissonClient;

    //1.创建一个表达式解析器
    SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 切点表达式
     */
   // @Pointcut("execution(public * com.atguigu.gmall.item.service.SkuDetailService.getDetailData(Long))")
    @Pointcut("@annotation(com.atguigu.gmall.starter.cache.aspect.annotation.MallCache)")
    public void pc(){}

    /**
     * 环绕通知：能够拦截目标方法的执行
     * @param pjp：可推进的连接点 封装了目标方法和当前切面信息
     * @return
     */
    @Around(value = "pc()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.info("缓存切面介入。。。。");
        RLock lock = null;
        Boolean tryLock = false;
        try {
            //拿到目标方法的所有参数
            //不能将这些参数写死 需要动态获取
//            Object[] args = pjp.getArgs();
//            Long skuId = (Long)args[0];

            //先查缓存  动态的 查各种数据 数据缓存用的key都不一样
            //SkuDetailVo cache = cacheService.getRedisCache(skuId);

            //获取精确泛型类型的返回值类型
            Type returnType = getMethodReturnType(pjp);

            //获取MallCache注解
            MallCache mallCache = getMethodAnnotation(pjp,MallCache.class);
            //获取缓存中定义的缓存key
            String expr = mallCache.cacheKey();

            //计算表达式的值
            String cacheEval = null;
            if (StringUtils.isEmpty(expr)){
                //如果expr为空  使用默认值
                cacheEval = getCacheKeyDefault(expr,pjp);
            }else{
                //如果expr不为空 才需要动态计算
                cacheEval = evalExpression(expr,pjp,String.class);
            }


            //获取缓存中的数据
            Object cache = cacheService.getCacheData(cacheEval,returnType);

            //2.如果缓存中有 直接返回
            if (cache != null){
                log.info("缓存命中");
                return cache;
            }

            //3.如果缓存中没有  问位图（bitmap）
            log.info("缓存未命中");
            //每种业务都有自己的bitmap  或者可以不用 动态开启
            //bitmap 不一样  bitmap要判定的值也不一样
//            Boolean aBoolean = cacheService.mightContain(skuId);
            String bitMapName = mallCache.bitMapName();
            String bitMapKey = mallCache.bitMapKey();  //key可以自己指定
            //判断是否使用位图
            if (!StringUtils.isEmpty(bitMapKey)){
                //断言机制：如果某些参数不符合预期 自动抛出IllegalStateException(message)
                Assert.hasLength(bitMapKey,"bitmap索引位置必须给定");
                Long bitmapIndex = evalExpression(bitMapKey, pjp, Long.class); //值可以表达式计算
                boolean contain = cacheService.mightContain(bitMapName,bitmapIndex);
                //4.如果位图中没有 直接返回空  结束方法
                if (!contain){
                    log.info("位图为空  疑似攻击请求");
                    return null;
                }
            }
            //如果为空  位图不用 直接走后面的流程

            //5.如果位图中有 准备回源查找  回源前先上锁
            //锁需要和业务保持一致
            String lockKey = mallCache.lockKey();
            //如果不给定锁 则使用默认锁
            if (StringUtils.isEmpty(lockKey)){
                lockKey = "lock:" + cacheEval;
            }else{
                lockKey = evalExpression(lockKey, pjp, String.class);
            }
            log.info("位图中有 准备回源");
            lock = redissonClient.getLock(lockKey);
            tryLock = lock.tryLock();

            if (tryLock){
                //极端情况下：
                //A: 缓存？ == 位图？== 得锁 == 缓存？ == 回源 == 放缓存 == 释放锁
                //B: 缓存？========================================== 位图 == 得锁 == 得锁 == 回源
                log.info("上锁成功");
                //双重检测机制  【动态：缓存中的数据类型】
                //SkuDetailVo cache1 = cacheService.getRedisCache(skuId);
                //Object cache1 = cacheService.getCacheData(cacheEval, returnType);
                cache = cacheService.getCacheData(cacheEval,returnType);
                if (cache != null){
                    return cache;
                }

                //说明拿到了锁  回源查找
                //6.执行目标方法
                log.info("正在回源查找");
                Object proceed = pjp.proceed();
                //7.保存到缓存中
                // 每种业务存的东西不一样  key也不一样  过期时间也不一样
                long ttl = mallCache.ttl();
                TimeUnit unit = mallCache.unit();
//                cacheService.saveCacheData(proceed,cacheEval);
               cacheService.saveCacheData(proceed,cacheEval,ttl,unit);
                //8.返回数据
                return proceed;
            }

            //9.走到这里 没抢到锁 等待一段之间 直接查询缓存
            log.info("获取锁失败  等待300ms后 直接从缓存中获取");
            TimeUnit.MILLISECONDS.sleep(300);
//            cache = cacheService.getRedisCache(skuId);
            cache = cacheService.getCacheData(cacheEval,returnType);
            return cache;
        }finally {
            if (tryLock){
                //表示获取锁成功 需要释放锁
                lock.unlock();
            }
        }
    }

    /**
     * 设置缓存key的默认值
     * @param expr
     * @param pjp
     * @return
     */
    private String getCacheKeyDefault(String expr, ProceedingJoinPoint pjp) {

        //没传表达式 默认值：方法名全签名 + 参数列表
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String methodName = signature.getMethod().getName();
        String className = signature.getMethod().getDeclaringClass().toString().replace("class ", "");
        String argsName = Arrays.stream(pjp.getArgs())
                .reduce((o1, o2) -> o1.toString() + "_" + o2.toString()).get().toString();

        //缓存key：类名：方法名：参数列表
        return (className + ":" + methodName + ":" + argsName);
    }

    /**
     * 计算指定表达式的值
     * @param expr
     * @param pjp
     * @return
     */
    private<T> T evalExpression(String expr, ProceedingJoinPoint pjp,Class<T> tetType) {

        //把创建表达式的的代码 提取到公共位置

        //2.解析表达式
        Expression expression = parser.parseExpression(expr, ParserContext.TEMPLATE_EXPRESSION);
        //3.得到值
        EvaluationContext ec = new StandardEvaluationContext();
        Object[] args = pjp.getArgs();
        ec.setVariable("args",args);
        T value = expression.getValue(ec, tetType);
        return value;
    }

    /**
     * 获取方法上的指定注解
     * @param pjp
     * @return
     */
    private<T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint pjp, Class<T> clz) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        T annotation = method.getDeclaredAnnotation(clz);
        return annotation;
    }

    /**
     * 获取目标方法的带泛型的返回值类型
     * @param pjp
     * @return
     */
    private Type getMethodReturnType(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Type returnType = signature.getMethod().getGenericReturnType();
        return returnType;
    }
}
