###spring cloud gateway应用
1. spring cloud Gateway
	1. 架构
	
		![](pic/spring_cloud_gateway_diagram.png)
	2. 术语 
		1. route：路由，通过ID的唯一标示，定义转发的URL地址和断言和过滤器集合
		2. Predicate：断言，用于对请求的URL地址进行匹配判断
		3. Filter：各种Gatewayfilter的实例，如限流过滤器
	3. 核心组件(gateway依赖于netty和Spring Webflux)
		1. DispatcherHandler：入口
		2. HandlerMapping：处理器映射器
		3. HandlerAdapter：处理器适配器
	4. 执行流程
		1. 由处理器适配器handlermapping通过断言对client的请求进行匹配得到handler，在交由handleradapter进行执行，经过一些列的过滤器最终得到一个proxy request对象，最后在发送给后端真实的service   	