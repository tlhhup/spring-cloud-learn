package org.tlh.springcloud.config;


import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Component
public class RestTemplateAuthInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // todo token从缓存中获取
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJpZFwiOjIsXCJ1c2VyTmFtZVwiOlwidGxoXCIsXCJwYXNzd29yZFwiOm51bGwsXCJhZ2VcIjpudWxsLFwicmVhbE5hbWVcIjpudWxsLFwiYWRkcmVzc1wiOm51bGx9IiwiaXNzIjoiYXV0aDAiLCJleHAiOjE1Mzg5MjA2ODF9.qzC2wzO5SsliTWxbOQH2iah57lR2oi5VHiAM12kawgA";
        request.getHeaders().add("Authorization", token);
        return execution.execute(request, body);
    }

}
