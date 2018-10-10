package org.tlh.transaction.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@EnableEurekaClient
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class TransactionMqServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionMqServiceApplication.class,args);
    }

}
