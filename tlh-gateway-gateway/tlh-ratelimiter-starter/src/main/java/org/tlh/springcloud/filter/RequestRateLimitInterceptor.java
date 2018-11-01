package org.tlh.springcloud.filter;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.tlh.springcloud.RequestRateLimitProperties;
import org.tlh.springcloud.annotation.RequestRateLimit;
import org.tlh.springcloud.entity.RateLimit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestRateLimitInterceptor extends BaseRequestRateLimit implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            if (((HandlerMethod) handler).hasMethodAnnotation(RequestRateLimit.class)) {
                RequestRateLimit requestRateLimit = ((HandlerMethod) handler).getMethodAnnotation(RequestRateLimit.class);
                //包装为实体类对象
                RateLimit rateLimit=new RateLimit();
                rateLimit.setTokenKey(requestRateLimit.tokenKey());
                rateLimit.setReplenishRate(requestRateLimit.replenishRate());
                rateLimit.setBurstCapacity(requestRateLimit.burstCapacity());
                rateLimit.setRequested(requestRateLimit.requested());
                rateLimit.setEnable(requestRateLimit.enable());
                if(!isAllowed(rateLimit)){//限流
                    response.setStatus(RequestRateLimitProperties.TO_MANY_REQUEST);
                    return false;
                }
            }
        }

        return true;
    }
}
