package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author huping
 * @desc
 * @date 18/10/5
 */
@EnableEurekaClient
@SpringBootApplication
public class SccConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SccConfigClientApplication.class,args);
    }

}
