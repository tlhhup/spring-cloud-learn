###eureka客服端配置

1. 自定义示例ID
   
        instance-id: ${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}
2. 心跳频率    
    
        lease-renewal-interval-in-seconds: 5 # 发送给注册中心的心跳频率
3. 失效时间
    
        lease-expiration-duration-in-seconds: 5 # 心跳间隔的超时时间，如果没有收到则移除该instance
4. 采用IP注册, 如果hostname不能被解析时使用IP向eureka发送请求   
    
        prefer-ip-address: true
5. Ribbon客户端**负载均衡原理**(通过对resttemplate的请求进行拦截实现)
	1. 通过在resttemplate添加LoadBalanced注解，可以使得LoadBalancerClient对该resttemplate进行包装
	2. Ribbon的核心自动装配类为LoadBalancerAutoConfiguration

			@Configuration
			@ConditionalOnClass(RestTemplate.class)
			@ConditionalOnBean(LoadBalancerClient.class)
			@EnableConfigurationProperties(LoadBalancerRetryProperties.class)
			public class LoadBalancerAutoConfiguration {
			
				@LoadBalanced
				@Autowired(required = false)
				private List<RestTemplate> restTemplates = Collections.emptyList();
			
				@Bean
				public SmartInitializingSingleton loadBalancedRestTemplateInitializerDeprecated(
						final ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {
					return () -> restTemplateCustomizers.ifAvailable(customizers -> {
			            for (RestTemplate restTemplate : LoadBalancerAutoConfiguration.this.restTemplates) {
			                for (RestTemplateCustomizer customizer : customizers) {							
			                	//1.添加拦截器--->2
			                    customizer.customize(restTemplate);
			                }
			            }
			        });
				}
			
				//2.拦截器配置
				@Configuration
				@ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
				static class LoadBalancerInterceptorConfig {
				
				   // 创建拦截器
					@Bean
					public LoadBalancerInterceptor ribbonInterceptor(
							LoadBalancerClient loadBalancerClient,
							LoadBalancerRequestFactory requestFactory) {
						return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
					}
			
					@Bean
					@ConditionalOnMissingBean
					public RestTemplateCustomizer restTemplateCustomizer(
							final LoadBalancerInterceptor loadBalancerInterceptor) {
						return restTemplate -> {
			                List<ClientHttpRequestInterceptor> list = new ArrayList<>(
			                        restTemplate.getInterceptors());
			                //3.注入拦截器
			                list.add(loadBalancerInterceptor);
			                restTemplate.setInterceptors(list);
			            };
					}
				}
			
				....
			}
	3. 拦截器实现LoadBalancerInterceptor

			public class LoadBalancerInterceptor implements ClientHttpRequestInterceptor {

				.....
			
				@Override
				public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
						final ClientHttpRequestExecution execution) throws IOException {
					final URI originalUri = request.getURI();
					String serviceName = originalUri.getHost();
					Assert.state(serviceName != null, "Request URI does not contain a valid hostname: " + originalUri);
					# 4.执行请求调用
					return this.loadBalancer.execute(serviceName, requestFactory.createRequest(request, body, execution));
				}
			}
			
			# 调用链
			private class InterceptingRequestExecution implements ClientHttpRequestExecution {

		
				@Override
				public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
					.... 
					//5.获取请求的真实地址
						ClientHttpRequest delegate = requestFactory.createRequest(request.getURI(), method);
						....
				}
			}
			
			public class ServiceRequestWrapper extends HttpRequestWrapper {
				.....
				@Override
				public URI getURI() {
				   //6. 该方法将instanceID更具配置IRule(负载均衡规则顶层借口)规则转换为真实的IP＋端口号，并最终生成请求的URL地址
					URI uri = this.loadBalancer.reconstructURI(
							this.instance, getRequest().getURI());
					return uri;
				}
			}
	4. 可以通过拦截来扩展resttemplate的功能(ClientHttpRequestInterceptor)
		1. 服务间采用jtw认证的时候，可以通过定义拦截器来，在发送请求前将token动态添加到请求头中 

###集成hystrix
1. 添加依赖

		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
2. 开启hystrix

		@EnableHystrix //开启hystrix支持
		@EnableEurekaClient
		@SpringBootApplication
		public class EurekaConsumeApplication {
		
		    @Bean
		    @LoadBalanced
		    public RestTemplate restTemplate(){
		        return new RestTemplate();
		    }
		
		    public static void main(String[] args) {
		        SpringApplication.run(EurekaConsumeApplication.class,args);
		    }
		
		}
3. 使用hystrix提供的服务降级、请求合并

		@RestController
		@RequestMapping("/userAuth")
		public class UserAuthController {
		
		    @Autowired
		    private RestTemplate restTemplate;
		
		    @PostMapping("/register")
		    @HystrixCommand(fallbackMethod = "registerFail")//定义hystrixcommand,设置失败回调
		    public String register(){
		        return this.restTemplate.postForEntity("http://user-service/UserController/add",null,String.class).getBody();
		    }
		
		    public String registerFail(){
		        return "fail";
		    }
		
		    @DeleteMapping("/delete/{id}")
		    @HystrixCollapser(batchMethod = "batchDelete",collapserProperties = {
		            @HystrixProperty(name="timerDelayInMilliseconds", value = "100")
		    })//请求合并
		    public String delete(@PathVariable("id")Integer id){
		        this.restTemplate.delete("http://user-service/UserController/delete/{id}",id);
		        return null;
		    }
		
		    @HystrixCommand(fallbackMethod = "deleteFail")
		    public List<String> batchDelete(List<Integer> ids){
		        System.out.println("batchDelete");
		        Map<String,List<Integer>> param=new HashMap<>();
		        param.put("ids",ids);
		        this.restTemplate.delete("http://user-service/UserController/delete",param);
		        return Arrays.asList("user");
		    }
		
		    public List<String> deleteFail(List<Integer> ids){
		        System.out.println("deleteFail");
		        return Arrays.asList("user");
		    }
	
		}	
4. 配置hystrix收集web接口(需要actuator)

		management:
		  endpoints:
		    web:
		      exposure:
		        include: hystrix.stream # 开启stream 的web接口
5. 处理请求合并报空指针问题
	1. 原因HystrixRequestContext在请求合并调用时没有初始化
	2. 处理：通过spring mvc的拦截器处理
		1. 定义拦截器
		
				public class HystrixContextInitInterceptor implements HandlerInterceptor {
				
				    private HystrixRequestContext hystrixRequestContext;
				
				    @Override
				    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
				        hystrixRequestContext=HystrixRequestContext.initializeContext();
				        return true;
				    }
				
				    @Override
				    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
				        hystrixRequestContext.shutdown();
				    }
				}
		2. 注入拦截器
		
				@Configuration
				public class CollapsingConfiguration implements WebMvcConfigurer {
				
				    @Override
				    public void addInterceptors(InterceptorRegistry registry) {
				        registry.addInterceptor(new HystrixContextInitInterceptor());
				    }
				}  	