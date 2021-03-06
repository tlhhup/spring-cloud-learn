package org.tlh.transaction.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
@EnableJpaRepositories
@EnableSpringDataWebSupport
@EnableTransactionManagement
public class TlhSubstitutionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlhSubstitutionApplication.class,args);
    }

}
