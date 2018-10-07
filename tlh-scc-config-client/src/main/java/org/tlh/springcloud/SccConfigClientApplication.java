package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.tlh.springcloud.config.properties.NestingProperty;

/**
 * @author huping
 * @desc
 * @date 18/10/5
 */
@EnableEurekaClient
@SpringBootApplication
@EnableConfigurationProperties(value = {NestingProperty.class})
public class SccConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SccConfigClientApplication.class,args);
    }

}
