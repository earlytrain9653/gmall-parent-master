package com.atguigu.gmall.common.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 杨林
 * @create 2022-12-17 16:32 星期六
 * description: 透传用户头信息  把老请求头（网关传下来的请求头）的用户id或者临时id请求头中的东西  放到新请求中
 */
@Slf4j
@Component
public class UserHeaderFeignInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.info("user信息 feign拦截器开始工作");
        //从cartController中拿到静态变量requestMap
        //HttpServletRequest request = CartController.requestMap.get(Thread.currentThread());

        //springMVC  只要开始处理请求  就自动会把当前请求绑定共享到当前线程
        //获取到当前线程绑定的老请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        if (request != null){
            String userId = request.getHeader("UserId");
            if (!StringUtils.isEmpty(userId)){
                requestTemplate.header("UserId",userId);
            }

            String tempId = request.getHeader("TempId");

            if (!StringUtils.isEmpty(tempId)){
                requestTemplate.header("TempId",tempId);
            }
        }

        //用完之后删除requestMap中的数据  防止oom
        //CartController.requestMap.remove(Thread.currentThread());

    }
}
