package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.gateway.properties.AuthUrlProperties;
import com.atguigu.gmall.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.RequestBuilder;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-15 14:15 星期四
 * description:拦截每个请求  透传用户id
 */
@Slf4j
@Component
public class UserAuthGatewayFilter implements GlobalFilter {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AuthUrlProperties properties;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     *
     * @param exchange  包括这次的请求和响应
     * @param chain  filter链
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        log.info("请求开始  拦截到请求：{}",path);

        //预先判断
        //1.如果是静态资源  直接放行 无需查看用户身份信息
        //静态资源  .css  .js  .png  .gif  .jpg
        //if (path.endsWith())
        List<String> urlList = properties.getAnyoneUrl();
//        for (String pattern : urlList) {
//            if (antPathMatcher.match(pattern,path)){
//                log.info("这是一个静态资源 直接放行");
//                //放行
//                return chain.filter(exchange);
//            }
//        }
        long count = urlList.stream()
                .filter(pattern -> antPathMatcher.match(pattern, path))
                .count();
        if (count > 0){
            log.info("这是一个静态资源 直接放行");
            //放行
            return chain.filter(exchange);
        }

        //2.浏览器尝试访问内部路径 直接打回
        List<String> denyUrl = properties.getDenyUrl();
        long countDeny = denyUrl.stream()
                .filter(pattern -> antPathMatcher.match(pattern, path))
                .count();
        if (countDeny > 0){
            log.warn("浏览器尝试访问内部路径 疑似攻击请求 直接打回");
            Result<String> build = Result.build("", ResultCodeEnum.PERMISSION);
            //返回错误Json
            return responseJson(exchange,build);
        }

        //3.有限权限访问   必须登录才能访问
        List<String> authUrl = properties.getAuthUrl();
        long authCount = authUrl
                .stream()
                .filter(pattern -> antPathMatcher.match(pattern, path))
                .count();
        if (authCount > 0){
            //必须登录才能访问
            //判断时候能拿到令牌   拿到令牌且令牌为真说明登录了  否则没登录
            String token = getToken(exchange);
            UserInfo userInfo = getUserInfo(token);
            if (StringUtils.isEmpty(token) || userInfo == null){  //没登陆或者令牌为假令牌
                //浏览器重定向到登录页 去登录
                return redirectToPage(exchange,properties.getLoginPage());
            }
        }

        //所有请求都是正常情况下的统一功能

        //获取请求头或cookie中携带的用户令牌
        String token = getToken(exchange);
//        if(StringUtils.isEmpty(token)){
//            return null;
//        }

        //根据token获取Redis中用户登录的信息
        UserInfo userInfo = getUserInfo(token);
        //如果能拿到用户信息  就透传id
        //透传id
        return userIdThrough(chain,exchange,userInfo);


        //响应式编程的filter放行
//        Mono<Void> filter = chain
//                .filter(exchange)
//                .doFinally((item) -> {
//                    log.info("请求结束：" + request.getURI().toString());  //这个方法式非阻塞的
//                });

//        return filter;
    }

    /**
     * 透传用户id
     * @param chain
     * @param exchange
     * @param userInfo
     * @return
     */
    private Mono<Void> userIdThrough(GatewayFilterChain chain, ServerWebExchange exchange, UserInfo userInfo) {

        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();

        if (userInfo != null){
            //用户信息放在头中 往下透传
            //请求数据都是只读的  不能修改
            //request.getHeaders().set("UserId",userInfo.getId().toString());
            //只要构造完成，老的exchange里面的request也会跟着变
            builder.header("UserId", userInfo.getId().toString()).build();
        }

        //如果携带临时id也需要透传
        String tempId = getTempId(exchange);
        //临时id不为空  透传
        if (!StringUtils.isEmpty(tempId)){
            //只要构建完成，老的exchange里面的request也会跟着变
            builder.header("TempId",tempId).build();
        }

        return chain.filter(exchange);
    }


    /**
     * 获取请求中的临时id
     * @param exchange
     * @return
     */
    private String getTempId(ServerWebExchange exchange) {
        //1.拿到请求
        ServerHttpRequest request = exchange.getRequest();

        //先看请求头
        String userTempId = request.getHeaders().getFirst("userTempId");
        if (!StringUtils.isEmpty(userTempId)){
            return userTempId;
        }

        //如果请求头中没有  就看cookie中
        HttpCookie first = request.getCookies().getFirst("userTempId");
        if (first != null){
            return first.getValue();
        }

        //如果头和cookie中都没有  返回null
        return null;

    }


    /**
     * 重定向到登录页
     * @param exchange
     * @param loginPage
     * @return
     */
    private Mono<Void> redirectToPage(ServerWebExchange exchange, String loginPage) {
        String uri = exchange.getRequest().getURI().toString();
        ServerHttpResponse response = exchange.getResponse();
        //1.重定向：设置状态码：302
        response.setStatusCode(HttpStatus.FOUND);
        //设置要跳转的新地址
        loginPage += "?originUrl=" + uri;
        //2.设置响应头  Location：新位置
        response.getHeaders().set("Location",loginPage);
        //防止前台使用假cookie一直重定向，将假cookie删除
        ResponseCookie cookie = ResponseCookie.from("token", "1")
                .domain("gmall.com")
                .maxAge(0)
                .build();
        response.addCookie(cookie);
        //响应结束  返回
        return response.setComplete();
    }

    /**
     * 响应一个json
     * @param exchange
     * @param build
     * @return
     */
    private Mono<Void> responseJson(ServerWebExchange exchange, Result<String> build) {
        //1.得到响应对象
        ServerHttpResponse response = exchange.getResponse();

        //2.得到数据的DataBuffer
        String json = JSON.toJSONString(build);
        DataBuffer dataBuffer = response.bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return response.writeWith(Mono.just(dataBuffer));
    }

    /**
     * 根据token获取Redis中用户登录的信息
     * @param token
     * @return
     */
    private UserInfo getUserInfo(String token) {
        String s = redisTemplate.opsForValue().get("login:user:" + token);
        UserInfo info = JSON.parseObject(s, UserInfo.class);
        if (info == null){
            return null;
        }
        return info;
    }

    /**
     * 获取请求头或cookie中的用户令牌
     * @param exchange
     * @return
     */
    private String getToken(ServerWebExchange exchange) {
        //1.拿到请求
        ServerHttpRequest request = exchange.getRequest();

        //2.获取所有的请求头
        String token = request.getHeaders().getFirst("token");
        if (!StringUtils.isEmpty(token)){
            return token;
        }

        //如果请求头中没有 就看cookie中是否含有  但是有肯能连这个cookie都没有
        HttpCookie first = request.getCookies().getFirst("token");

        if (first != null){
            return first.getValue();
        }
        return null;
    }
}
