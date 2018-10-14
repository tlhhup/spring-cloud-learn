package org.tlh.transaction.mq.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tlh.transaction.mq.feign.config.DateFormatRegister;
import org.tlh.transaction.mq.feign.fallback.TransactionMessageClientFallBack;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@Configuration
@EnableFeignClients
@ConditionalOnClass(TransactionMessageClient.class)
public class TransactionClientAutoConfiguration {

    @Bean
    public FeignFormatterRegistrar dateFormatRegister(){
        return new DateFormatRegister();
    }

    @Bean
    public TransactionMessageClientFallBack transactionMessageClientFallBack(){
        return new TransactionMessageClientFallBack();
    }

}
