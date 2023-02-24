package com.atguigu.gmall.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.atguigu.gmall.pay.config.properties.AlipayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 杨林
 * @create 2022-12-25 16:48 星期日
 * description:
 */
@EnableConfigurationProperties(AlipayProperties.class)
@Configuration
public class AlipayConfig {

    @Bean
    AlipayClient alipayClient(AlipayProperties alipayProperties){
        return new DefaultAlipayClient(alipayProperties.getGatewayUrl(),
                                        alipayProperties.getApp_id(),
                                        alipayProperties.getMerchant_private_key(),
                                        "json",
                                        alipayProperties.getCharset(),
                                        alipayProperties.getAlipay_public_key(),
                                        alipayProperties.getSign_type());
    }


}
