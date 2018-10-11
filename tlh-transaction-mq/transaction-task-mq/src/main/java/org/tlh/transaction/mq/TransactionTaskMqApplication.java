package org.tlh.transaction.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@EnableEurekaClient
@SpringBootApplication
public class TransactionTaskMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionTaskMqApplication.class,args);
    }

}