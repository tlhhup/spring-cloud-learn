package org.tlh.springcloud;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.tlh.springcloud.entity.RateLimit;
import org.tlh.springcloud.listener.InitRequestRateLimitListener;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RequestRateLimitProperties.class)
public class RequestRateLimitAutoConfiguration {

    @Bean
    public InitRequestRateLimitListener initApiRateLimitListener(RedisTemplate<String,RateLimit> rateLimitRedisTemplate){
        InitRequestRateLimitListener rateLimitListener = new InitRequestRateLimitListener();
        rateLimitListener.setRateLimitRedisTemplate(rateLimitRedisTemplate);
        return rateLimitListener;
    }

    @Bean
    public RedisTemplate<String,RateLimit> rateLimitRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String,RateLimit> rateLimitRedisTemplate=new RedisTemplate<>();
        rateLimitRedisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置序列化
        StringRedisSerializer keySerializer=new StringRedisSerializer();
        rateLimitRedisTemplate.setKeySerializer(keySerializer);
        rateLimitRedisTemplate.setHashKeySerializer(keySerializer);

        Jackson2JsonRedisSerializer<RateLimit> jsonRedisSerializer=new Jackson2JsonRedisSerializer<RateLimit>(RateLimit.class);
        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED,true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN,true);
        jsonRedisSerializer.setObjectMapper(mapper);

        rateLimitRedisTemplate.setValueSerializer(jsonRedisSerializer);
        rateLimitRedisTemplate.setHashValueSerializer(jsonRedisSerializer);

        return rateLimitRedisTemplate;
    }

}
