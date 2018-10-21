package org.tlh.springcloud.listener;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.tlh.springcloud.TlhApiRateLimitProperties;
import org.tlh.springcloud.annotation.RequestRateLimit;
import org.tlh.springcloud.entity.RateLimit;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
public class InitApiRateLimitListener implements ApplicationListener<ContextRefreshedEvent> {

    private AtomicBoolean initialized=new AtomicBoolean(false);

    private TlhApiRateLimitProperties tlhApiRateLimitProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            this.tlhApiRateLimitProperties = event.getApplicationContext().getBean(TlhApiRateLimitProperties.class);
            this.initRateLimitApi();
        } catch (Exception e) {
            log.error("scanning rate api error");
        }
    }

    private void initRateLimitApi(){
        log.info("InitApiRateLimitListener---->scanning rateLimit API");
        List<RateLimit> rateLimits=Lists.newArrayList();
        RateLimit rateLimit=null;
        //构建配置
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()//
                                                .setUrls(ClasspathHelper.forPackage(this.tlhApiRateLimitProperties.getBasePackage()))//设置扫描的包
                                                .setScanners(new MethodAnnotationsScanner());//设置方法注解扫描
        //初始化
        Reflections reflections = new Reflections(configurationBuilder);
        //获取添加了ApiRateLimit注解的所有方法
        Set<Method> methods = reflections.getMethodsAnnotatedWith(RequestRateLimit.class);
        if(methods!=null&&!methods.isEmpty()){
            for (Method method:methods){
                RequestRateLimit annotation = method.getAnnotation(RequestRateLimit.class);
                if (annotation!=null){
                    rateLimit=new RateLimit(annotation.tokenKey(),annotation.replenishRate(),annotation.burstCapacity(),annotation.requested());
                    rateLimits.add(rateLimit);
                    rateLimit=null;
                }
            }
        }
        log.info("InitApiRateLimitListener---->end;Api:{}", rateLimits.size());
    }

}
