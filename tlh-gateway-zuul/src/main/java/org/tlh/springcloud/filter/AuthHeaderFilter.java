package org.tlh.springcloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Component
public class AuthHeaderFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Object isSuccess = ctx.get("isSuccess");
        return isSuccess==null?true:Boolean.valueOf(isSuccess.toString());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJpZFwiOjIsXCJ1c2VyTmFtZVwiOlwidGxoXCIsXCJwYXNzd29yZFwiOm51bGwsXCJhZ2VcIjpudWxsLFwicmVhbE5hbWVcIjpudWxsLFwiYWRkcmVzc1wiOm51bGx9IiwiaXNzIjoiYXV0aDAiLCJleHAiOjE1Mzg5MjA2ODF9.qzC2wzO5SsliTWxbOQH2iah57lR2oi5VHiAM12kawgA";
        ctx.addZuulRequestHeader("Authorization", token);
        return null;
    }
}
