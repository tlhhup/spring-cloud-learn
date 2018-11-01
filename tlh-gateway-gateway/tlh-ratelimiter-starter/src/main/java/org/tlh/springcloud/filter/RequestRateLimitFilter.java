package org.tlh.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.tlh.springcloud.RequestRateLimitProperties;
import org.tlh.springcloud.entity.RateLimit;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/23
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
public class RequestRateLimitFilter extends BaseRequestRateLimit implements Filter {

    private RateKeyResolver defaultKeyResolver;
    private RateKeyResolver keyResolver;
    private ValueOperations<String, RateLimit> rateLimitOperations;

    public RequestRateLimitFilter() {
        this.defaultKeyResolver = new DefaultRateKeyResolver();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RequestRateLimitFilter--->init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.debug("RequestRateLimitFilter--->start rate limit");

        if(servletRequest instanceof HttpServletRequest){
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String servletPath = request.getServletPath();
            String uri = request.getRequestURI().substring(servletPath.length());
            String tokenKey=this.keyResolver!=null?keyResolver.resolve(uri):this.defaultKeyResolver.resolve(uri);
            //获取限流配置
            RateLimit rateLimit = this.rateLimitOperations.get(tokenKey);
            rateLimit.setTokenKey(tokenKey);
            //判断是否限流
            if(!isAllowed(rateLimit)){
                ((HttpServletResponse) servletResponse).setStatus(RequestRateLimitProperties.TO_MANY_REQUEST);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);

        log.debug("RequestRateLimitFilter--->end rate limit");
    }

    @Override
    public void destroy() {
        log.info("RequestRateLimitFilter--->destroy");
    }

    public void setRateLimitOperations(ValueOperations<String, RateLimit> rateLimitOperations) {
        this.rateLimitOperations = rateLimitOperations;
    }

    public void setKeyResolver(RateKeyResolver keyResolver) {
        this.keyResolver = keyResolver;
    }
}
