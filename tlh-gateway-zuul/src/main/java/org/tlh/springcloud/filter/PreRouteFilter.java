package org.tlh.springcloud.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huping
 * @desc
 * @date 18/10/3
 */
@Component
public class PreRouteFilter extends ZuulFilter {

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
        HttpServletRequest request = ctx.getRequest();

        ctx.setSendZuulResponse(false);
        ctx.setResponseBody("拦截请求");
        //后面的filter通过此判断是否继续执行
        ctx.set("isSuccess",false);
        return null;
    }
}
