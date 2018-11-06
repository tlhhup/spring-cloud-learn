package org.tlh.springcloud;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.tlh.springcloud.entity.RateLimit;
import org.tlh.springcloud.filter.RateKeyResolver;
import org.tlh.springcloud.filter.RequestRateLimitFilter;
import org.tlh.springcloud.filter.RequestRateLimitInterceptor;
import org.tlh.springcloud.listener.InitRequestRateLimitListener;

import javax.servlet.Filter;
import java.util.List;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisOperations.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RequestRateLimitProperties.class)
public class RequestRateLimitAutoConfiguration {

    @Autowired
    private RequestRateLimitProperties requestRateLimitProperties;

    /************ filter request rate limit start **************/

    @Bean
    @ConditionalOnClass(Filter.class)
    @ConditionalOnProperty(prefix = "tlh",name = "filter.enable",havingValue = "true")
    public InitRequestRateLimitListener initApiRateLimitListener(RedisTemplate<String,RateLimit> rateLimitRedisTemplate){
        InitRequestRateLimitListener rateLimitListener = new InitRequestRateLimitListener();
        rateLimitListener.setRateLimitRedisTemplate(rateLimitRedisTemplate);
        return rateLimitListener;
    }

    @Bean
    @ConditionalOnClass(Filter.class)
    @ConditionalOnProperty(prefix = "tlh",name = "filter.enable",havingValue = "true")
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

    @Bean
    @ConditionalOnClass(Filter.class)
    @ConditionalOnProperty(prefix = "tlh",name = "filter.enable",havingValue = "true")
    public FilterRegistrationBean<RequestRateLimitFilter> rateLimitFilterFilterRegistration(ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
                                                                                            @Qualifier(RequestRateLimitProperties.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
                                                                                            @Autowired(required = false)RateKeyResolver rateKeyResolver,
                                                                                            RedisTemplate<String,RateLimit> rateLimitRedisTemplate){
        RequestRateLimitFilter requestRateLimitFilter=new RequestRateLimitFilter();
        requestRateLimitFilter.setRateLimitOperations(rateLimitRedisTemplate.opsForHash());
        requestRateLimitFilter.setReactiveRedisTemplate(reactiveRedisTemplate);
        requestRateLimitFilter.setScript(redisScript);
        requestRateLimitFilter.setKeyResolver(rateKeyResolver);
        requestRateLimitFilter.setRedisKey(this.requestRateLimitProperties.getRedisKey());

        //register filter
        FilterRegistrationBean<RequestRateLimitFilter> registrationBean=new FilterRegistrationBean<>();

        registrationBean.setFilter(requestRateLimitFilter);
        //过滤默认的Spring mvc的多级路径
        if(!RequestRateLimitProperties.DEFAULT_PATTERN.equals(requestRateLimitProperties.getLimitPattern())) {
            registrationBean.addUrlPatterns(requestRateLimitProperties.getLimitPattern());
        }
        return registrationBean;
    }

    /************ filter request rate limit end **************/

    @Bean
    @SuppressWarnings("unchecked")
    public RedisScript requestRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        RedisSerializer<String> serializer = new StringRedisSerializer();
        RedisSerializationContext<String , String> serializationContext = RedisSerializationContext
                .<String, String>newSerializationContext()
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory,
                serializationContext);
    }

    @Configuration
    @ConditionalOnClass(DispatcherServlet.class)
    @ConditionalOnProperty(prefix = "tlh",name = "filter.enable",matchIfMissing = true,havingValue = "false")
    public class RequestLimitMvcConfig implements WebMvcConfigurer{

        @Autowired
        private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

        @Autowired
        @Qualifier(RequestRateLimitProperties.REDIS_SCRIPT_NAME)
        private RedisScript<List<Long>> script;

        @Autowired
        private RequestRateLimitProperties requestRateLimitProperties;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            RequestRateLimitInterceptor requestRateLimitInterceptor=new RequestRateLimitInterceptor();
            requestRateLimitInterceptor.setReactiveRedisTemplate(reactiveRedisTemplate);
            requestRateLimitInterceptor.setScript(script);

            //register interceptor
            InterceptorRegistration interceptorRegistration = registry.addInterceptor(requestRateLimitInterceptor);
            interceptorRegistration.addPathPatterns(requestRateLimitProperties.getLimitPattern());
            if(StringUtils.hasText(requestRateLimitProperties.getExcludePathPatterns())){
                interceptorRegistration.excludePathPatterns(requestRateLimitProperties.getExcludePathPatterns());
            }
        }

    }

}
