###spring cloud gateway应用
1. spring cloud Gateway(对外服务的统一入口)
	1. 架构
	
		![](pic/spring_cloud_gateway_diagram.png)
	2. 术语 
		1. route：路由，通过ID的唯一标示，定义转发的URL地址和断言和过滤器集合
		2. Predicate：断言，用于对请求的URI地址进行匹配判断
		3. Filter：各种Gatewayfilter的实例，如限流过滤器
	3. 核心组件(gateway依赖于netty和Spring Webflux)
		1. DispatcherHandler：入口
		2. HandlerMapping：处理器映射器
		3. HandlerAdapter：处理器适配器
	4. 执行流程
		1. 由处理器适配器handlermapping通过断言对client的请求进行匹配得到handler，在交由handleradapter进行执行，经过一些列的过滤器最终得到一个proxy request对象，最后在发送给后端真实的service(及uri定义的目的地址)   	
2. Predicate(多个断言可以组合使用，其逻辑关系为and)
	1. After
	2. Before
	3. Between	--->对时间进行断言(和当前时间进行对比)
	4. Cookie
	5. Header
	6. Host
	7. Method：对请求的方法
	
		请求路径匹配tlh-service-exam中的所有的路径，并且请求方法为GET的请求都最终被路由到服务名称为tlh-service-exam的后端服务。
		
			cloud:
		    gateway:
		      routes:
		        - id: method_route		# 路由唯一标示
		          uri: lb://tlh-service-exam # 使用lb前缀，通过ribbon进行负载
		          predicates:			# 断言 
		          - Method=GET
	8. Path：请求的路径，通过spring的pathmatcher进行对比匹配

		请求路径为/exam开头的所有的请求都最终被路由到服务名称为tlh-service-exam的后端服务
	
			cloud:
		    gateway:
		      routes:
		        - id: path_route
		          uri: lb://tlh-service-exam # 通过ribbon进行负载
		          predicates:
		          - Path=/exam/**
  9. Query：查询参数
  10. RemoteAddr
3. 过滤器
	1. AddRequestHeader：路由到下游的请求添加请求头(这个位置可以考虑添加后端服务认证所需要的token)
	2. AddRequestParameter： 路由到下游的请求添加请求参数
	3. AddResponseHeader：路由到下游的响应添加头信息
	4. Hystrix：Hystrix处理服务降级、熔断处理
	5. PrefixPath：对匹配的所有的请求，添加指定的前缀
	6. RequestRateLimiter：限流过滤器
		1. 使用令牌桶算法来实现，主要的核心接口为RateLimiter接口
		2. 使用redis-reactive来处理限流
			1. 添加依赖

					<dependency>
			            <groupId>org.springframework.boot</groupId>
			            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
			        </dependency>
			2. 编写KeyResolver用于设置限流的key(默认使用的PrincipalNameKeyResolver没有任何效果)
			
					@Bean
				    KeyResolver hostNameKeyResolver() {
				        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
				    }
			3. 编写限流配置  

					cloud:
				    gateway:
				      routes:
				        - id: exam_route
				          uri: lb://tlh-service-exam # 通过ribbon进行负载
				          predicates:
				          - Path=/exam/**
				          filters:
				            - name: RequestRateLimiter
				              args:
				                redis-rate-limiter.replenishRate: 10  # 每秒生成的令牌
				                redis-rate-limiter.burstCapacity: 20  # 桶大小
				                key-resolver: "#{@hostNameKeyResolver}" #指定限流的key