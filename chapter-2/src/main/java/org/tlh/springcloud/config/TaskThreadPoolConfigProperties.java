package org.tlh.springcloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author huping
 * @desc
 * @date 18/10/1
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.task.pool")
public class TaskThreadPoolConfigProperties {

    private int corePoolSize=5;

    private int maxPoolSize=50;

    private int keepAliveSeconds=60;

    private int queueCapacity=1000;

    private String threadNamePrefix="TLH-AsyncTask-1";

}
