package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author huping
 * @desc
 * @date 18/10/5
 */
@EnableEurekaClient
@SpringBootApplication
public class DBSccConfigAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DBSccConfigAdminApplication.class,args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
