package org.tlh.springcloud.listener;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.tlh.springcloud.RequestRateLimitProperties;
import org.tlh.springcloud.annotation.RequestRateLimit;
import org.tlh.springcloud.entity.RateLimit;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
public class InitRequestRateLimitListener implements ApplicationListener<ContextRefreshedEvent> {

    private RequestRateLimitProperties requestRateLimitProperties;

    private RedisTemplate<String,RateLimit> rateLimitRedisTemplate;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.requestRateLimitProperties = event.getApplicationContext().getBean(RequestRateLimitProperties.class);
        this.initRateLimitApi();
    }

    private void initRateLimitApi() {
        log.info("InitRequestRateLimitListener---->scanning rateLimit API");
        Map<String,RateLimit> rateLimits = Maps.newHashMap();
        RateLimit rateLimit = null;
        //构建配置
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()//
                .setUrls(ClasspathHelper.forPackage(this.requestRateLimitProperties.getBasePackage()))//设置扫描的包
                .setScanners(new MethodAnnotationsScanner());//设置方法注解扫描
        //初始化
        Reflections reflections = new Reflections(configurationBuilder);
        //获取添加了ApiRateLimit注解的所有方法
        Set<Method> methods = reflections.getMethodsAnnotatedWith(RequestRateLimit.class);
        if (methods != null && !methods.isEmpty()) {
            for (Method method : methods) {
                RequestRateLimit annotation = method.getAnnotation(RequestRateLimit.class);
                if (annotation != null) {
                    if(StringUtils.hasText(annotation.tokenKey())) {
                        rateLimit = new RateLimit(annotation.tokenKey(), annotation.replenishRate(), //
                                annotation.burstCapacity(), annotation.requested(),annotation.enable());
                        rateLimits.put(rateLimit.getTokenKey(),rateLimit);
                        rateLimit = null;
                    }
                }
            }
        }
        if(!rateLimits.isEmpty()){
            this.rateLimitRedisTemplate.opsForHash().putAll(requestRateLimitProperties.getRedisKey(),rateLimits);
        }
        log.info("InitRequestRateLimitListener---->end;Api:{}", rateLimits.size());
    }

    public void setRateLimitRedisTemplate(RedisTemplate<String, RateLimit> rateLimitRedisTemplate) {
        this.rateLimitRedisTemplate = rateLimitRedisTemplate;
    }
}
