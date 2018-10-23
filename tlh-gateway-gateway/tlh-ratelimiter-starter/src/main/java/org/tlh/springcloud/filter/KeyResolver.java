package org.tlh.springcloud.filter;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/23
 * <p>
 * Github: https://github.com/tlhhup
 */
@FunctionalInterface
public interface KeyResolver {

    String resolve(String uri);
}
