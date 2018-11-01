package org.tlh.springcloud.annotation;

import java.lang.annotation.*;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestRateLimit {

    /**
     * 存储在redis中的key
     * @return
     */
    String tokenKey() default "";

    /**
     * 生成令牌的速率
     * @return
     */
    int replenishRate() default 10;

    /**
     * 令牌桶容量
     * @return
     */
    int burstCapacity() default 20;

    /**
     * 需要的令牌数
     * @return
     */
    int requested() default 1;

    /**
     * 是否启用限流
     * @return
     */
    boolean enable() default true;

}
