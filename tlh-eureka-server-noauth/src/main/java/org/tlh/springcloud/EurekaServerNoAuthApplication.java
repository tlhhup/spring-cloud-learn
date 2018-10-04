package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author huping
 * @desc
 * @date 18/10/5
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerNoAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerNoAuthApplication.class,args);
    }

}
