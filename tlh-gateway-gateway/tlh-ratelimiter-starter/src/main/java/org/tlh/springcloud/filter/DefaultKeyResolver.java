package org.tlh.springcloud.filter;

import org.tlh.springcloud.RequestRateLimitProperties;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/23
 * <p>
 * Github: https://github.com/tlhhup
 */
public class DefaultKeyResolver implements KeyResolver {

    @Override
    public String resolve(String uri) {
        String[] split = uri.substring(1).split("/");
        StringBuilder builder=new StringBuilder(RequestRateLimitProperties.RATE_PREFIX);
        builder.append(split[0]).append(".").append(split[1]);
        return builder.toString();
    }

}
