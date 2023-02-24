package com.atguigu.gmall.common.config.thread.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 杨林
 * @create 2022-12-05 22:46 星期一
 * description:
 */
@ConfigurationProperties(prefix = "app.threadpool")
@Data
public class AppThreadProperties {

    private Integer corePoolSize = 4;
    private Integer maximumPoolSize = 8;
    private Long keepAliveTime = 5L;
    private Integer workQueueSize = 1000;

}
