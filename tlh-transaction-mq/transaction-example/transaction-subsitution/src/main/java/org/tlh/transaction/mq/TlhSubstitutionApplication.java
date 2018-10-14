package org.tlh.transaction.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class TlhSubstitutionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlhSubstitutionApplication.class,args);
    }

}
