###spring cloud gateway应用
1. spring cloud Gateway(对外服务的统一入口)
	1. 架构
	
		![](pic/spring_cloud_gateway_diagram.png)
	2. 术语 
		1. route：路由，通过ID的唯一标示，定义转发的URL地址和断言和过滤器集合
		2. Predicate：断言，用于对请求的URI地址进行匹配判断，看是否满足设置的某种条件
		3. Filter：各种Gatewayfilter的实例，如限流过滤器
	3. 核心组件(gateway依赖于netty和Spring Webflux)
		1. DispatcherHandler：入口
		2. HandlerMapping：处理器映射器
		3. HandlerAdapter：处理器适配器
	4. 执行流程
		1. 由处理器适配器handlermapping通过断言对client的请求进行匹配得到handler，在交由handleradapter进行执行，经过一些列的过滤器最终得到一个proxy request对象，最后在发送给后端真实的service(及uri定义的目的地址)   	
2. Predicate(多个断言可以组合使用，其逻辑关系为and)
	1. After

			predicates:
	        - After=2017-01-20T17:42:47.789-07:00[America/Denver]
	2. Before

			predicates:
       	  	- Before=2017-01-20T17:42:47.789-07:00[America/Denver]
	3. Between	--->对时间进行断言(和当前时间进行对比)

			predicates:
	        - Between=2017-01-20T17:42:47.789-07:00[America/Denver], 2017-01-21T17:42:47.789-07:00[America/Denver]
	4. Cookie：标示cookie满足name和value的匹配时返回true

			predicates:
        	- Cookie=chocolate, ch.p
	5. Header：标示header中有指定的name和value时返回true

			predicates:
        	- Header=X-Request-Id, \d+
	6. Host：标示请求的主机满足某种个是时返回true

			predicates:
        	- Host=**.somehost.org
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
  9. Query：标示请求中包含指定的参数时返回true，多个参数采用","隔开

	  		predicates:
	        - Query=baz
  10. RemoteAddr
3. 过滤器工厂
	1. AddRequestHeader：路由到下游的请求添加请求头(这个位置可以考虑添加后端服务认证所需要的token)

			filters:
        	- AddRequestHeader=X-Request-Foo, Bar
	2. AddRequestParameter： 路由到下游的请求添加请求参数

			filters:
        	- AddRequestParameter=foo, bar
	3. AddResponseHeader：路由到下游的响应添加头信息

			filters:
	        - AddResponseHeader=X-Response-Foo, Bar
	4. Hystrix：Hystrix处理服务降级、熔断处理
	5. PrefixPath：对匹配的所有的请求，添加指定的前缀

			filters:
	        - PrefixPath=/mypath
	6. **RequestRateLimiter**：限流过滤器
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
				              args:	#这个参数不能写错
				                redis-rate-limiter.replenishRate: 10  # 每秒生成的令牌
				                redis-rate-limiter.burstCapacity: 20  # 桶大小
				                key-resolver: "#{@hostNameKeyResolver}" #指定限流的key,通过spring的el表达式，指定bean
    7. RedirectTo：重定向，标示如果出现3XX的状态码，重定向的界面

	    	filters:
	        - RedirectTo=302, http://acme.org
    8. RemoveRequestHeader：移除请求头，标示路由到下游的请求头被移除指定的头信息

    		filters:
        	- RemoveRequestHeader=X-Request-Foo
    9. RemoveResponseHeader：移除响应头，标示路由到下游的响应头被移除指定的头信息

    		filters:
        	- RemoveResponseHeader=X-Response-Foo
4. 全局过滤器(GlobalFilter)
	1. **Forward**：如果uri指定的地址是以forward开头的，将被直接路由到本地的handler进行处理，不会路由到后端的service(可用于登陆逻辑的处理)
	
			- id: local_route     # 路由到但前网关的handler
	          uri: forward:/auth/login
	          predicates:
	          - Path=/login
	          - Method=GET
	2. LoadBalancerClient：如果uri指定的地址是以lb开头的，将使用LoadBalancerClient将服务名解析为真实的主机和端口号
	3. Websocket：如果uri指定的地址是以ws或者wss开头的，将使用spring web socket注解转发到下游的websocket 