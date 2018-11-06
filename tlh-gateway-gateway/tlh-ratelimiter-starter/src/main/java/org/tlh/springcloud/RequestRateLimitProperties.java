package org.tlh.springcloud;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/21
 * <p>
 * Github: https://github.com/tlhhup
 */
@Data
@ConfigurationProperties(prefix = "tlh.rate")
public class RequestRateLimitProperties {

    public static final int TO_MANY_REQUEST = 429;
    public static final String REDIS_SCRIPT_NAME = "requestRateLimiterScript";
    public static final String DEFAULT_PATTERN = "/**";//多级路径
    private static final String RATE_PREFIX = "rate";


    private String basePackage;
    private String limitPattern = DEFAULT_PATTERN;
    private String excludePathPatterns;
    private String redisKey = RATE_PREFIX;

}
