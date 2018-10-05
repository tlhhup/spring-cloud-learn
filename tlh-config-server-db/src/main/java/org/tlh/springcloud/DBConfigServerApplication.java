package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author huping
 * @desc
 * @date 18/10/4
 */
@EnableEurekaClient
@EnableConfigServer
@SpringBootApplication
public class DBConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DBConfigServerApplication.class,args);
    }

}
