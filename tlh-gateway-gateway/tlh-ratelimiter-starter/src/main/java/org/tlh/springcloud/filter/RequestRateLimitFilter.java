package org.tlh.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.tlh.springcloud.RequestRateLimitProperties;
import org.tlh.springcloud.entity.RateLimit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/23
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
public class RequestRateLimitFilter implements Filter {

    private RateKeyResolver defaultKeyResolver;
    private RateKeyResolver keyResolver;
    private ValueOperations<String, RateLimit> rateLimitOperations;
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private RedisScript<List<Long>> script;

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
            RateLimit rateLimit = this.rateLimitOperations.get(tokenKey);
            if(rateLimit!=null) {

                List<String> keys = getKeys(tokenKey);


                List<String> scriptArgs = Arrays.asList(rateLimit.getReplenishRate() + "", rateLimit.getBurstCapacity() + "",
                        Instant.now().getEpochSecond() + "", rateLimit.getRequested() + "");
                Flux<List<Long>> flux = this.reactiveRedisTemplate.execute(this.script, keys, scriptArgs);
                Mono<Boolean> result = flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
                        .reduce(new ArrayList<Long>(), (longs, l) -> {
                            longs.addAll(l);
                            return longs;
                        }).map(results -> {
                            boolean allowed = results.get(0) == 1L;
                            Long tokensLeft = results.get(1);

                            log.debug("RequestRateLimitFilter--->tokens left:{}", tokensLeft);
                            return allowed;
                        });
                if (!result.block()) {
                    ((HttpServletResponse) servletResponse).setStatus(RequestRateLimitProperties.TO_MANY_REQUEST);
                    return;
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);

        log.debug("RequestRateLimitFilter--->end rate limit");
    }

    @Override
    public void destroy() {
        log.info("RequestRateLimitFilter--->destroy");
    }

    private List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    public void setReactiveRedisTemplate(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public void setScript(RedisScript<List<Long>> script) {
        this.script = script;
    }

    public void setRateLimitOperations(ValueOperations<String, RateLimit> rateLimitOperations) {
        this.rateLimitOperations = rateLimitOperations;
    }

    public void setKeyResolver(RateKeyResolver keyResolver) {
        this.keyResolver = keyResolver;
    }
}
