package org.tlh.springcloud.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;
import java.util.Map;

/**
 * @author huping
 * @desc
 * @date 18/10/7
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = NestingProperty.SCC_PREFIX)
public class NestingProperty {

    public static final String SCC_PREFIX="scc.config.client";

    private String secret;

    private Long expire;

    private RateLimit rateLimit;

    private Map<String,Item> docket;

    @Data
    static class RateLimit{
        private Long rate;
        private Long remaining;

        List<Item> items;
    }

    @Data
    static class Item{
        private String name;
    }

}
