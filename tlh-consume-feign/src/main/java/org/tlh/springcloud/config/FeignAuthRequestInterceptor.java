package org.tlh.springcloud.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
public class FeignAuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJpZFwiOjIsXCJ1c2VyTmFtZVwiOlwidGxoXCIsXCJwYXNzd29yZFwiOm51bGwsXCJhZ2VcIjpudWxsLFwicmVhbE5hbWVcIjpudWxsLFwiYWRkcmVzc1wiOm51bGx9IiwiaXNzIjoiYXV0aDAiLCJleHAiOjE1Mzg5MjA2ODF9.qzC2wzO5SsliTWxbOQH2iah57lR2oi5VHiAM12kawgA";
        template.header("Authorization", token);
    }
}
