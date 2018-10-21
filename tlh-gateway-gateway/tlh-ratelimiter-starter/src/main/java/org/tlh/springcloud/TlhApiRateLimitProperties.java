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
public class TlhApiRateLimitProperties {

    private String basePackage;

}
