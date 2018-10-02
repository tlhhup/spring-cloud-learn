package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author huping
 * @desc
 * @date 18/10/2
 */
@EnableEurekaServer
@SpringBootApplication
public class TlhEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlhEurekaServerApplication.class,args);
    }

}
