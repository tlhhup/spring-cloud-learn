package org.tlh.springcloud;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@EnableSwagger2Doc
@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement
public class TlhAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlhAuthApplication.class,args);
    }

}
