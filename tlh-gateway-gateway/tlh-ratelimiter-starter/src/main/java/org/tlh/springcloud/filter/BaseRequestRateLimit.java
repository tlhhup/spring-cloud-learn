package org.tlh.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.tlh.springcloud.entity.RateLimit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class BaseRequestRateLimit {

    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private RedisScript<List<Long>> script;

    protected final boolean isAllowed(RateLimit rateLimit){
        //启用限流
        if(rateLimit!=null&&rateLimit.isEnable()) {

            List<String> keys = getKeys(rateLimit.getTokenKey());

            //构建lua执行参数
            List<String> scriptArgs = Arrays.asList(rateLimit.getReplenishRate() + "", rateLimit.getBurstCapacity() + "",
                    Instant.now().getEpochSecond() + "", rateLimit.getRequested() + "");
            //执行lua脚本 todo 优化，该依赖太重
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
            return result.block();
        }else {
            return true;
        }
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
}
