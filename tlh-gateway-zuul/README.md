1. zuul是通过过滤器来实现请求的拦截，其过滤器有四种类型，其执行顺序ZuulServlet中可以查看到

		try {
            preRoute();
        } catch (ZuulException e) {
            error(e);
            postRoute();
            return;
        }
        try {
            route();
        } catch (ZuulException e) {
            error(e);
            postRoute();
            return;
        }
        try {
            postRoute();
        } catch (ZuulException e) {
            error(e);
            return;
        }
2. 过滤器类型，在FilterConstants中有常量定义
	1. 	pre：在路由之前
	2. route：路由到后端服务
	3. post：路由结束
	4. error：出现异常