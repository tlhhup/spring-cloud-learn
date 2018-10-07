###消费端声明式服务调用(feign)
1. 添加依赖

		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
2. 主类添加注解

		@EnableHystrix
		@EnableEurekaClient
		@EnableFeignClients
		@SpringBootApplication
		public class FeignClientApplication {
		
		    public static void main(String[] args) {
		        SpringApplication.run(FeignClientApplication.class,args);
		    }
		
		}
3. 服务端接口调用声明

        //name：服务端实例(eureka中的ID
        //path：根路径
        //fallback：服务调用失败回调
		@FeignClient(name = "user-service",path = "/UserController",fallback = UserFeignClientFallBack.class)
		public interface UserFeignClient {
		
		    //服务端接口
		    @PostMapping("/add")
		    String register();
		
		    @DeleteMapping("/delete/{id}")
		    String delete(@PathVariable("id") int id);
		
		}
4. hystrix支持
	1. 添加依赖
	
			<dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-actuator</artifactId>
	        </dependency>
	2. 修改配置文件 
	
			feign:
			  hystrix:
			    enabled: true # 开启hystrix支持
			  client:
			    config:
			      default:
			        connectTimeout: 5000
			        readTimeout: 5000
			        loggerLevel: basic
			  compression: # 开启GZIP压缩
			    request:
			      enabled: true
			    response:
			      enabled: true
			 management:
			  endpoints:
			    web:
			      exposure:
			        include: hystrix.stream # 开启stream 的web接口
5. 自定义feign配置
	1. 添加请求拦截器：在发送请求前处理通用逻辑
		1. 编写拦截器：实现RequestInterceptor
		2. 编写feign的配置类

				@Configuration
				public class FooConfiguration {
				    @Bean
				    public Contract feignContract() {
				        return new feign.Contract.Default();
				    }
				
				    @Bean
				    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
				        return new BasicAuthRequestInterceptor("user", "password");
				    }
				}     
		3. 在feignclient中使用自定义配置
		
				@FeignClient(name = "stores", configuration = FooConfiguration.class)
				public interface StoreClient {
				    //..
				}
6. 获取导致fallback的原因
	1. 实现FallbackFactory接口
	
			@Component
			static class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient> {
				@Override
				public HystrixClient create(Throwable cause) {
					return new HystrixClient() {
						@Override
						public Hello iFailSometimes() {
							return new Hello("fallback; reason was: " + cause.getMessage());
						}
					};
				}
			}
	2. 在feignclient中通过fallbackFactory属性引用 

			@FeignClient(name = "hello", fallbackFactory = HystrixClientFallbackFactory.class)
			protected interface HystrixClient {
				@RequestMapping(method = RequestMethod.GET, value = "/hello")
				Hello iFailSometimes();
			}	