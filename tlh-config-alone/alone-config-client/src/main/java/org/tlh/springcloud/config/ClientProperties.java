package org.tlh.springcloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by 离歌笑tlh/hu ping on 2019/10/13
 * <p>
 * Github: https://github.com/tlhhup
 */
@Data
@ConfigurationProperties(prefix = "alone.client")
public class ClientProperties {

    private String sysId;
    private String name;

    private Wechat wechat;

    @Data
    public static class Wechat{
        private String appId;
        private String secret;
    }

}
