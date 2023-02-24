package com.atguigu.gmall.product.init;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @author 杨林
 * @create 2022-12-06 20:14 星期二
 * description: 项目启动就创建
 */
@Slf4j
public class InitListener implements SpringApplicationRunListener, ApplicationContextAware {



    ApplicationContext applicationContext;

    SpringApplication application;
    public InitListener(SpringApplication application, String[] args){
        log.info("监听器对象创建  appliaction:{}; args:{};",application,args);
        this.application = application;
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        log.info("监听到项目  started");
    }

    @Override
    public void starting() {
        log.info("监听到项目正在启动 starting");
//        SkuInfoService bean = applicationContext.getBean(SkuInfoService.class);
//        System.out.println(bean);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
