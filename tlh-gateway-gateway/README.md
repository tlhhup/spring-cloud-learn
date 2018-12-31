### spring cloud gateway应用
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
	4. **执行流程**
		1. 由处理器适配器handlermapping通过断言对client的请求进行匹配得到handler，在交由handleradapter进行执行，经过一些列的**过滤器**(按过滤器的顺序执行)最终得到一个proxy request对象，最后在发送给后端真实的service(及uri定义的目的地址)   	
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
5. **限流过滤器原理**，通过执行lua脚本来实现

		local tokens_key = KEYS[1]			# 存令牌数的key
		local timestamp_key = KEYS[2]		# 存最后写入时间key
		
		
		local rate = tonumber(ARGV[1])		# 生成令牌的速度
		local capacity = tonumber(ARGV[2])  # 令牌桶容量
		local now = tonumber(ARGV[3])		# 当前时间 秒
		local requested = tonumber(ARGV[4]) # 这次请求需要的令牌数
		
		local fill_time = capacity/rate		# 桶满时间 秒
		local ttl = math.floor(fill_time*2) # 向下取整
		
		
		
		local last_tokens = tonumber(redis.call("get", tokens_key)) # 获取当前的令牌数
		if last_tokens == nil then
		  last_tokens = capacity
		end
		
		
		local last_refreshed = tonumber(redis.call("get", timestamp_key)) ＃最后写入时间 秒
		if last_refreshed == nil then
		  last_refreshed = 0
		end
		
		local delta = math.max(0, now-last_refreshed)						# 上次获取token和这次的时间差，用于计算生成了多少新流入桶中的token
		local filled_tokens = math.min(capacity, last_tokens+(delta*rate))  # 取最大容量和(存量＋新增)的最小值   --->当前桶的容量
		local allowed = filled_tokens >= requested							# 是否允许反问
		local new_tokens = filled_tokens
		local allowed_num = 0
		if allowed then
		  new_tokens = filled_tokens - requested
		  allowed_num = 1
		end
		
		# 原子性操作
		redis.call("setex", tokens_key, ttl, new_tokens)  					＃ 现在桶中存在的token，桶满就回到初始状态
		redis.call("setex", timestamp_key, ttl, now)						# 最后写入时间
		
		return { allowed_num, new_tokens }
	1. tokens_key用于模拟令牌桶，其存储桶中的可用令牌数
	2. 通过计算上次和这次的写入时间差*速率=新流入桶中的令牌
	3. 当前桶中的令牌大于请求需要的令牌则表示allowed，允许请求到达后端服务
6. 自定义前置和后置过滤器
	1. 前置过滤器

			public class PreGatewayFilterFactory extends AbstractGatewayFilterFactory<PreGatewayFilterFactory.Config> {
			
				public PreGatewayFilterFactory() {
					// 必须强制注入配置类的class对象，否则是否的是默认的object作为配置类
					super(Config.class);
				}
			
				@Override
				public GatewayFilter apply(Config config) {
					// grab configuration from Config object
					return (exchange, chain) -> {
			            //If you want to build a "pre" filter you need to manipulate the
			            //request before calling change.filter
			            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
			            //use builder to manipulate the request
			            return chain.filter(exchange.mutate().request(request).build());
					};
				}
			
				public static class Config {
			        //Put the configuration properties for your filter here
				}
	
			}
	2. 后置过滤器 	

			public class PostGatewayFilterFactory extends AbstractGatewayFilterFactory<PostGatewayFilterFactory.Config> {
			
				public PostGatewayFilterFactory() {
					super(Config.class);
				}
			
				@Override
				public GatewayFilter apply(Config config) {
					// grab configuration from Config object
					return (exchange, chain) -> {
						return chain.filter(exchange).then(Mono.fromRunnable(() -> {
							ServerHttpResponse response = exchange.getResponse();
							//Manipulate the response in some way
						}));
					};
				}
			
				public static class Config {
			        //Put the configuration properties for your filter here
				}
	
			}
7. 获取后端服务的响应，通过定义全局过滤器实现，order的值越小对应pre类型就越先执行，同时对应post类型就越后执行

		@Slf4j
		@Component
		public class WrapperResponseGlobalFilter implements GlobalFilter, Ordered {
		
		    @Override
		    public int getOrder() {
		        // -1 is response write filter, must be called before that
		        return -2;
		    }
		
		    @Override
		    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		        ServerHttpResponse originalResponse = exchange.getResponse();
		        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
		        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
		            @Override
		            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
		                if (body instanceof Flux) {
		                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
		                    return super.writeWith(fluxBody.map(dataBuffer -> {
		                        // probably should reuse buffers
		                        byte[] content = new byte[dataBuffer.readableByteCount()];
		                        dataBuffer.read(content);
		                        //释放掉内存
		                        DataBufferUtils.release(dataBuffer);
		                        String s = new String(content, Charset.forName("UTF-8"));
		                        log.info(s);
		                        //TODO，s就是response的值，想修改、查看就随意而为了
		                        byte[] uppedContent = new String(content, Charset.forName("UTF-8")).getBytes();
		                        return bufferFactory.wrap(uppedContent);
		                    }));
		                }
		                // if body is not a flux. never got there.
		                return super.writeWith(body);
		            }
		        };
		        // replace response with decorator
		        return chain.filter(exchange.mutate().response(decoratedResponse).build());
		    }
		
		}
8. 跨域处理

	在前后分离开发中，前端主要透过官网来访问后端服务，而网关也一般部署在一台或多台不同于前端的物理机中，这个时候就会出现跨域的问题。在gateway中提供了两种方式来处理改问题，一个基础yml配置的方式(方便、灵活)，一种是基于WebFilter代码的方式来处理。
	1. 配置文件处理(该配置方式**对路由后端服务的跨域有效**，对gateway本身的controller无效)

		通过GlobalCorsProperties配置属性来完成跨域的配置，处理一个URL地址和一个CorsConfiguration之间的映射。其逻辑处理在org.springframework.web.cors.reactive.DefaultCorsProcessor#handleInternal该方法中完成。
	
			spring:
			  cloud:
			    gateway:
			      globalcors:
			        cors-configurations:
			          '[/**]':
			            allowedOrigins: "http://localhost:9527" # 前端服务器域名地址
			            allowedHeaders: "*"
			            allowedMethods:
			              - GET
			              - DELETE
			              - POST
			              - PUT
			              - OPTIONS		# 该请求用于获取服务器支持的HTTP请求方式，为跨域请求的预检请求，其目的是为了判断实际发送的请求是否是安全的
	2. 配置类处理(过滤器) :对路由到后端的服务和gateway
本身的controller都有效
			@Configuration
			public class GatewayConfig {
			
			    private static final String ALL = "*";
			    private static final String MAX_AGE = "18000L";
			
			
			    @Bean
			    public WebFilter corsFilter() {
			        return (ServerWebExchange ctx, WebFilterChain chain) -> {
			            ServerHttpRequest request = ctx.getRequest();
			            if (!CorsUtils.isCorsRequest(request)) {
			                return chain.filter(ctx);
			            }
			            HttpHeaders requestHeaders = request.getHeaders();
			            ServerHttpResponse response = ctx.getResponse();
			            HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
			            HttpHeaders headers = response.getHeaders();
			            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
			            headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
			            if (requestMethod != null) {
			                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
			            }
			            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
			            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
			            headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
			            if (request.getMethod() == HttpMethod.OPTIONS) {
			                response.setStatusCode(HttpStatus.OK);
			                return Mono.empty();
			            }
			            return chain.filter(ctx);
			        };
			    }
	
			}
	3. gateway本服务的跨域配置(参照WebFlux的官方文档):解决在网关处处理登出的逻辑

			@Configuration
		    public class WebConfig implements WebFluxConfigurer {
		
		        @Override
		        public void addCorsMappings(CorsRegistry registry) {
		
		            registry.addMapping("/**")
		                    .allowedOrigins("http://localhost:9527")
		                    .allowedHeaders("*")
		                    .allowedMethods("PUT", "DELETE","DELETE","OPTIONS","POST");
		        }
		    }
	4. **综上**：如果需要实现网关跨域的完全配置，需要1+3组合或者直接采用2的方式(推荐)
9. 全局异常处理及**gateway执行流程**
	1. spring cloud gateway主要通过webfulex和netty构成，其入口为**DispatcherHandler**对象的handle方法，其核心的组件有
		1. handlermapping：类似Spring mvc中的处理器映射器
		2. handleradapter：类似Spring mvc中的处理器适配器
		3. HandlerResultHandler：处理器执行的结果，类似Spring mvc中的mv对象 
		4. WebExceptionHandler：异常处理器，类似Spring mvc中的HandlerExceptionResolver对象
	2. 执行流程(和Springmvc的执行流程相似)及源码分析
		1. handle方法
		
				public Mono<Void> handle(ServerWebExchange exchange) {
					if (logger.isDebugEnabled()) {
						ServerHttpRequest request = exchange.getRequest();
						logger.debug("Processing " + request.getMethodValue() + " request for [" + request.getURI() + "]");
					}
					if (this.handlerMappings == null) {
						return Mono.error(HANDLER_NOT_FOUND_EXCEPTION);
					}
					return Flux.fromIterable(this.handlerMappings)
							//1.由处理器映射器得到处理器
							.concatMap(mapping -> mapping.getHandler(exchange))
							.next()
							.switchIfEmpty(Mono.error(HANDLER_NOT_FOUND_EXCEPTION))
							//2.通过处理器适配器调用处理器
							.flatMap(handler -> invokeHandler(exchange, handler))
							//3.结果处理，及响应
							.flatMap(result -> handleResult(exchange, result));
				} 		 	
		2. 处理器调用：invokeHandler方法

				private Mono<HandlerResult> invokeHandler(ServerWebExchange exchange, Object handler) {
					if (this.handlerAdapters != null) {
						for (HandlerAdapter handlerAdapter : this.handlerAdapters) {
							//1.遍历得到处理器适配器
							if (handlerAdapter.supports(handler)) {
								//2.完成处理器的调用
								return handlerAdapter.handle(exchange, handler);
							}
						}
					}
					return Mono.error(new IllegalStateException("No HandlerAdapter: " + handler));
				}
		3. 如果调用的是后端的服务(及包装为org.springframework.cloud.gateway.handler.FilteringWebHandler对象)将交由SimpleHandlerAdapter处理

				@Override
				public Mono<HandlerResult> handle(ServerWebExchange exchange, Object handler) {
					WebHandler webHandler = (WebHandler) handler;
					Mono<Void> mono = webHandler.handle(exchange);
					return mono.then(Mono.empty());
				}
		4. FilteringWebHandler对象的handle方法

				@Override
				public Mono<Void> handle(ServerWebExchange exchange) {
					//1.获取在调用后端的路由
					Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
					List<GatewayFilter> gatewayFilters = route.getFilters();
					//2.注入过滤器，其请求和各种预处理都是在过滤器中完成的
					List<GatewayFilter> combined = new ArrayList<>(this.globalFilters);
					combined.addAll(gatewayFilters);
					//TODO: needed or cached?
					AnnotationAwareOrderComparator.sort(combined);
			
					logger.debug("Sorted gatewayFilterFactories: "+ combined);
					//
					return new DefaultGatewayFilterChain(combined).filter(exchange);
				}
		5. DefaultGatewayFilterChain(FilteringWebHandler中的内部类)的方法filter方法

				@Override
				public Mono<Void> filter(ServerWebExchange exchange) {
					return Mono.defer(() -> {
						if (this.index < filters.size()) {
							//1.遍历过滤器
							GatewayFilter filter = filters.get(this.index);
							DefaultGatewayFilterChain chain = new DefaultGatewayFilterChain(this, this.index + 1);
							//2.调用过滤器方法，在LoadBalancerClientFilter完成负载均衡处理，在NettyRoutingFilter中完成请求的发送
							return filter.filter(exchange, chain);
						} else {
							return Mono.empty(); // complete
						}
					});
				}
		6. LoadBalancerClientFilter的filter方法

				@Override
				public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
					//1.得到请求路径
					URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
					String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
					if (url == null || (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix))) {
						return chain.filter(exchange);
					}
					//preserve the original url
					addOriginalRequestUrl(exchange, url);
			
					log.trace("LoadBalancerClientFilter url before: " + url);
					//2.得到真实访问的后端服务的实例
					final ServiceInstance instance = loadBalancer.choose(url.getHost());
			
					if (instance == null) {
						throw new NotFoundException("Unable to find instance for " + url.getHost());
					}
			
					URI uri = exchange.getRequest().getURI();
			
					// if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
					// if the loadbalancer doesn't provide one.
					String overrideScheme = null;
					if (schemePrefix != null) {
						overrideScheme = url.getScheme();
					}
					//3.构建真实的请求地址
					URI requestUrl = loadBalancer.reconstructURI(new DelegatingServiceInstance(instance, overrideScheme), uri);
			
					log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
					exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
					//3.继续执行，将发送请求
					return chain.filter(exchange);
				}
		7. NettyRoutingFilter的filter方法

				@Override
				public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
					....
					//发送请求
					Mono<HttpClientResponse> responseMono = this.httpClient.request(method, url, req -> {
						final HttpClientRequest proxyRequest = req.options(NettyPipeline.SendOptions::flushOnEach)
								.headers(httpHeaders)
								.chunkedTransfer(chunkedTransfer)
								.failOnServerError(false)
								.failOnClientError(false);
			
						if (preserveHost) {
							String host = request.getHeaders().getFirst(HttpHeaders.HOST);
							proxyRequest.header(HttpHeaders.HOST, host);
						}
			
						return proxyRequest.sendHeaders() //I shouldn't need this
								.send(request.getBody().map(dataBuffer ->
										((NettyDataBuffer) dataBuffer).getNativeBuffer()));
					});
			
					if (properties.getResponseTimeout() != null) {
						responseMono.timeout(properties.getResponseTimeout(),
								Mono.error(new TimeoutException("Response took longer than timeout: " +
										properties.getResponseTimeout())));
					}
					//处理响应，设置响应头和状态信息
					return responseMono.doOnNext(res -> {
						ServerHttpResponse response = exchange.getResponse();
						// put headers and status so filters can modify the response
						HttpHeaders headers = new HttpHeaders();
			
						res.responseHeaders().forEach(entry -> headers.add(entry.getKey(), entry.getValue()));
			
						if (headers.getContentType() != null) {
							exchange.getAttributes().put(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR, headers.getContentType());
						}
			
						HttpHeaders filteredResponseHeaders = HttpHeadersFilter.filter(
								this.headersFilters.getIfAvailable(), headers, exchange, Type.RESPONSE);
						
						response.getHeaders().putAll(filteredResponseHeaders);
						HttpStatus status = HttpStatus.resolve(res.status().code());
						if (status != null) {
							response.setStatusCode(status);
						} else if (response instanceof AbstractServerHttpResponse) {
							// https://jira.spring.io/browse/SPR-16748
							((AbstractServerHttpResponse) response).setStatusCodeValue(res.status().code());
						} else {
							throw new IllegalStateException("Unable to set status code on response: " +res.status().code()+", "+response.getClass());
						}
			
						// Defer committing the response until all route filters have run
						// Put client response as ServerWebExchange attribute and write response later NettyWriteResponseFilter
						exchange.getAttributes().put(CLIENT_RESPONSE_ATTR, res);
					}).then(chain.filter(exchange));
		}
	3. [异常处理](https://juejin.im/post/5bbad1405188255c4a7137e1)
		1. 异常处理的配置类为ErrorWebFluxAutoConfiguration，从其配置类可以看到默认使用的是DefaultErrorWebExceptionHandler异常处理器(采用html展现异常)，如果需要自定义则继承该类重写特定的方法即可

				@Bean
				@ConditionalOnMissingBean(value = ErrorWebExceptionHandler.class, search = SearchStrategy.CURRENT)
				@Order(-1)
				public ErrorWebExceptionHandler errorWebExceptionHandler(
						ErrorAttributes errorAttributes) {
					DefaultErrorWebExceptionHandler exceptionHandler = new DefaultErrorWebExceptionHandler(
							errorAttributes, this.resourceProperties,
							this.serverProperties.getError(), this.applicationContext);
					exceptionHandler.setViewResolvers(this.viewResolvers);
					exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
					exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
					return exceptionHandler;
				}
		2. 自定义返回json数据格式的异常处理器，重写一下三个方法即可

				/**
			     * 获取异常属性
			     */
			    @Override
			    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
			        int code = 500;
			        Throwable error = super.getError(request);
			        if (error instanceof NotFoundException) {
			            code = 404;
			        }
			        if (error instanceof IllegalRequestException){
			            code=((IllegalRequestException) error).getCode();
			        }
			        return response(code, this.buildMessage(request, error));
			    }
			
			    /**
			     * 指定响应处理方法为JSON处理的方法
			     * @param errorAttributes
			     */
			    @Override
			    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
			        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
			    }
			
			    /**
			     * 根据code获取对应的HttpStatus
			     * @param errorAttributes
			     */
			    @Override
			    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
			        int statusCode = (int) errorAttributes.get("code");
			        return HttpStatus.valueOf(statusCode);
			    }
	    3. 配置异常处理器，完全模仿ErrorWebFluxAutoConfiguration异常处理器的配置即可

### 自定义API级别限流
1. 思路
	1. 限流的处理延用lua的处理方式
	2. 通过aop拦截所有添加了限流注解的请求，将注解的信息第一次写入到redis中(key、rate、capacity、requested(需要的令牌数))，后面都从redis中动态获取
	3. 通过config消息总线实现客户端配置刷新的原理，实现限流配置的动态刷新	
 