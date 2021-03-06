package org.tlh.springcloud.filter;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/23
 * <p>
 * Github: https://github.com/tlhhup
 */
public class DefaultRateKeyResolver implements RateKeyResolver {

    @Override
    public String resolve(String uri) {
        String[] split = uri.substring(1).split("/");
        StringBuilder builder=new StringBuilder();
        builder.append(split[0]).append(".").append(split[1]);
        return builder.toString();
    }

}
