package org.tlh.springcloud.config;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author huping
 * @desc  初始化HystrixRequestContext对象，不然在使用请求合并是会报空指针异常
 * @date 18/10/5
 */
public class HystrixContextInitInterceptor implements HandlerInterceptor {

    private HystrixRequestContext hystrixRequestContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        hystrixRequestContext=HystrixRequestContext.initializeContext();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        hystrixRequestContext.shutdown();
    }
}
