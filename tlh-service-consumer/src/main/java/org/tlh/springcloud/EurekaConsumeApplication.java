package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.tlh.springcloud.config.RestTemplateAuthInterceptor;

import java.util.List;

/**
 * @author huping
 * @desc
 * @date 18/10/2
 */
@EnableHystrix
@EnableEurekaClient
@SpringBootApplication
public class EurekaConsumeApplication {

    @Bean
    @Primary
    public RestTemplate normalRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateAuthInterceptor restTemplateAuthInterceptor){
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(restTemplateAuthInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumeApplication.class,args);
    }

}
