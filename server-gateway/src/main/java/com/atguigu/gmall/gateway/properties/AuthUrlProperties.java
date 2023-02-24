package com.atguigu.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-15 20:59 星期四
 * description:
 */
@Component
@ConfigurationProperties(prefix = "app.auth")
@Data
public class AuthUrlProperties {
    //直接放行的请求
    private List<String> anyoneUrl;

    //任何情况都拒绝的请求
    private List<String> denyUrl;

    //登录后才能访问的请求
    private List<String> authUrl;

    //配置登录页地址
    private String loginPage;
}
