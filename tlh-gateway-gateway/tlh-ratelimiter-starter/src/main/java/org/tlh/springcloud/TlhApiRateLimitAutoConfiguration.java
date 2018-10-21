package org.tlh.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tlh.springcloud.listener.InitApiRateLimitListener;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(TlhApiRateLimitProperties.class)
public class TlhApiRateLimitAutoConfiguration {

    @Bean
    public InitApiRateLimitListener initApiRateLimitListener(){
        return new InitApiRateLimitListener();
    }

}
