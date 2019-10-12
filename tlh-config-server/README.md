使用配置中心来外部化配置信息，及部分配置信息通过配置中心动态获取
###server端
1. 添加依赖

		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
2. 配置类添加注解EnableConfigServer
3. 所有的http的API都可以在EnvironmentController和ResourceController中获取

	                 
		/encrypt/{name}/{profiles}    
		/decrypt/{name}/{profiles}    
		/decrypt],methods=[POST]}
		/encrypt/status],methods=[GET]}
		/key],methods=[GET]}
		/key/{name}/{profiles}
		/{name}-{profiles}.yml || /{name}-{profiles}.yaml],methods=[GET]
		/{name}-{profiles}.properties],methods=[GET]
		/{name}/{profiles}/{label:.*}],methods=[GET]}
		/{label}/{name}-{profiles}.properties],methods=[GET]}
		/{name}-{profiles}.json],methods=[GET]}
		/{label}/{name}-{profiles}.json],methods=[GET]}
		/{label}/{name}-{profiles}.yml || /{label}/{name}-{profiles}.yaml]
		/{name}/{profiles:.*[^-].*}],methods=[GET]}
		/{name}/{profile}/{label}/**],methods=[GET],produces=[application/octet-stream]}
		/{name}/{profile}/{label}/**],methods=[GET]}
		/{name}/{profile}/**],methods=[GET],params=[useDefaultLabel]}
4. 说明
	1. name：为config-client配置文件中的spring.application.name属性
	2. profiles：为config-client配置文件中的spring.profiles.active的属性(及环境)
	3. label：git的label

###client
1. 添加依赖

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
2. 编写bootstrap.yml文件添加上述第4种的信息(**注意必须在这个文件中**)

		spring:
		  cloud:
		    config:
		      uri: http://localhost:8086 # 配置服务器的地址
3. 客户端通过服务发现来拉取配置信息
	1. 添加依赖
	
			<dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	        </dependency>
	2. 在bootstrap.yml文件中添加注册中心，及配置中心的serviceID

			spring:
			  cloud:
			    config:
			      profile: dev				#来取的配置环境
			      label: master				#拉取的label
			      discovery:
			        enabled: true			#开启服务发现
			        service-id: tlh-config-db # 配置中心在注册中心的ID
			eureka:
			  instance:
			    appname: scc-config-client
			    lease-renewal-interval-in-seconds: 5
			    lease-expiration-duration-in-seconds: 5
			  client:
			    service-url:
			      defaultZone: http://localhost:8089/eureka/
	3. 说明
		1. 好处：适用于配置中心地址会变的场景
		2. 坏处：增加了网络流量，需要先从注册中心获取配置中心的地址

###server端该用db来存储数据
 通过JdbcEnvironmentRepository来获取数据库中的配置信息

1. 添加依赖

		<dependencies>
	        <dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-config-server</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-jdbc</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>mysql</groupId>
	            <artifactId>mysql-connector-java</artifactId>
	        </dependency>
    	</dependencies>
2. 编写配置文件

		server:
		  port: 8087
		spring:
		  datasource:
		    url: jdbc:mysql:///config_server
		    driver-class-name: org.gjt.mm.mysql.Driver
		    username: 用户名
		    password: 密码
		    # 不能使用默认的数据源(估计是该版springcloud的bug)
		    type: org.springframework.jdbc.datasource.DriverManagerDataSource
		  profiles:
		    active: jdbc # 激活config-DB方式
		  cloud:
		    config:
		      server:
		        jdbc:
		          sql: SELECT `KEY`, `VALUE` from PROPERTIES where APPLICATION=? and PROFILE=? and LABEL=?
		          order: 0
3. 初始化数据库表

		create database config_server;
		use config_server;
		create table PROPERTIES(
			APPLICATION varchar(200),
		    `PROFILE` varchar(50),
		    LABEL varchar(50),
		    `KEY` varchar(100),
		    `VALUE` varchar(500)
		);
		
		select * from PROPERTIES;
		
		insert into PROPERTIES values('consume-feign','dev',null,'name','name-dev');
		SET SQL_SAFE_UPDATES = 0;
		-- label必须要有值，其默认为master
		update PROPERTIES set label='master' where APPLICATION='consume-feign';

###属性client的配置信息
1. 手动刷新
	1. 添加依赖

			<dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-actuator</artifactId>
	        </dependency>
	2. 在需要刷新属性的地方添加RefreshScope注解

			@RefreshScope
			@RestController
			@RequestMapping("/config")
			public class SccConfigClientController {
			
			    @Value("${name}")
			    private String name;
			
			    @GetMapping("/name")
			    public String name(){
			        return name;
			    }
			
			}
	3. 开启refresh端点(http协议默认关闭)

			management:
			  endpoints:
			    web:
			      exposure:
			        include: refresh
	4. 触发刷新 
	
			POST http://待刷新的服务地址/actuator/refresh
	5. RefreshScope说明
		1. 当该注解添加在使用了Configuration注解的配置类时，并不意味着该类中的所有的bean都会重新被加载，除非该bean也添加了RefreshScope注解
2. 集成消息总线自动刷新(config-server端)
	1. 添加依赖(在server和client端都需要添加)
	
			<!-- spring cloud bus -->
	        <dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
	            <version>2.0.0.RELEASE</version>
	        </dependency>
	2. 增加配置(在server端配置)
	
			management:
			  endpoints:
			    web:
			      exposure:
			        include: bus-refresh  # 开放bus-refresh端点
	3. 触发端点 
		1. 所有节点：[/actuator/bus-refresh],methods=[POST]
		2. 指定节点：[/actuator/bus-refresh/{destination}],methods=[POST] 
			1. destination：由app:index:id构成，可参考官网稳定说明
			2. destination：如果是服务名称，则会触发所有实例的配置刷新
			3. destination:port(及index)可以刷新特定实例的配置信息 	
	4. 说明
		1. 采用的是本地的rabbitmq，所以才用默认配置即可 
		2. 在sever端出发bus-refresh之后，会通过消息总线将事件发送出去，从而调用client的refresh来达到配置的自动刷新
		3. bus-refresh可以使得加了RefreshScope注解和ConfigurationProperties注解的数据从新加载
3. 加载配置中心的数据在client端通过对象来接收(通过**ConfigurationProperties**实现)
	1. 定义client端对象封装配置中数据
	
			@Data
			@RefreshScope
			@ConfigurationProperties(prefix = NestingProperty.SCC_PREFIX)
			public class NestingProperty {
			
			    public static final String SCC_PREFIX="scc.config.client";
			
			    private String secret;
			
			    private Long expire;
			
			    private RateLimit rateLimit;
			
			    private Map<String,Item> docket;
			
			    @Data
			    static class RateLimit{
			        private Long rate;
			        private Long remaining;
			
			        List<Item> items;
			    }
			
			    @Data
			    static class Item{
			        private String name;
			    }
	
			}
	2. 启用自动配置属性  

			@EnableEurekaClient
			@SpringBootApplication
			@EnableConfigurationProperties(value = {NestingProperty.class})
			public class SccConfigClientApplication {
			
			    public static void main(String[] args) {
			        SpringApplication.run(SccConfigClientApplication.class,args);
			    }
			
			}
	3. 配置中心返回的原始数据(config-server使用db模式)

			name=hello-world-100
			scc.config.client.rate-limit.remaining=100
			scc.config.client.rate-limit.rate=1000
			scc.config.client.docket.first.name=lisi    #map类型数据
			scc.config.client.rate-limit.items[0].name=zhansan  #list类型数据
			scc.config.client.secret=12345
			scc.config.client.expire=24

	4. 注意：只能结合EnableConfigurationProperties或者单独定义一个bean来完成属性的自动配置
		1. EnableConfigurationProperties(方便)

				@EnableConfigurationProperties(value = {NestingProperty.class})
		2. 定义bean 

				@Bean
			    public NestingProperty nestingProperty(){//2.使用定义bean的方式
			        return new NestingProperty();
			    }

###client端拉取配置的原理
1. 如果配置的是URL地址，直接通过bootstrap.yaml文件中配置的uri、profile、application.name和label信息**通过RestTemplate**发送请求(**注意config-server端暴露的地址**)拉取配置信息
2. 如果配置的是通过服务发现的方式，首先从通过bootstrap.yaml文件中配置service-id从服务中心获取配置中心的地址，再通过profile、application.name和label信息通过RestTemplate发送请求(**注意config-server端暴露的地址**)拉取配置信息
3. 关键类和代码
	1. ConfigClientAutoConfiguration：基础配置信息，bootstrap.yaml文件中的内容

			@Bean
			public ConfigClientProperties configClientProperties(Environment environment,
					ApplicationContext context) {
				if (context.getParent() != null
						&& BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
								context.getParent(), ConfigClientProperties.class).length > 0) {
					return BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(),
							ConfigClientProperties.class);
				}
				ConfigClientProperties client = new ConfigClientProperties(environment);
				return client;
			}
	2. DiscoveryClientConfigServiceBootstrapConfiguration：基于服务发现

			private void refresh() {
				try {
					//获取配置中心的ServiceID
					String serviceId = this.config.getDiscovery().getServiceId();
					List<String> listOfUrls = new ArrayList<>();
					//通过服务发现，获取所有的配置中心的实例
					List<ServiceInstance> serviceInstances = this.instanceProvider
							.getConfigServerInstances(serviceId);
					//遍历所有的配置中心，将其对应的真实地址记录到集合中
					for (int i = 0; i < serviceInstances.size(); i++) {
		
						ServiceInstance server = serviceInstances.get(i);
						String url = getHomePage(server);
		
						if (server.getMetadata().containsKey("password")) {
							String user = server.getMetadata().get("user");
							user = user == null ? "user" : user;
							this.config.setUsername(user);
							String password = server.getMetadata().get("password");
							this.config.setPassword(password);
						}
		
						if (server.getMetadata().containsKey("configPath")) {
							String path = server.getMetadata().get("configPath");
							if (url.endsWith("/") && path.startsWith("/")) {
								url = url.substring(0, url.length() - 1);
							}
							url = url + path;
						}
		
						listOfUrls.add(url);
					}
		
					String[] uri = new String[listOfUrls.size()];
					uri = listOfUrls.toArray(uri);
					//设置到ConfigClientProperties对象中
					this.config.setUri(uri);
			.......
	3. ConfigServiceBootstrapConfiguration 

			//创建配置定位器，有他去拉取配置
			@Bean
			@ConditionalOnMissingBean(ConfigServicePropertySourceLocator.class)
			@ConditionalOnProperty(value = "spring.cloud.config.enabled", matchIfMissing = true)
			public ConfigServicePropertySourceLocator configServicePropertySource(ConfigClientProperties properties) {
				ConfigServicePropertySourceLocator locator = new ConfigServicePropertySourceLocator(
						properties);
				return locator;
			}  
			
			
			
			@Override
			@Retryable(interceptor = "configServerRetryInterceptor")
			public org.springframework.core.env.PropertySource<?> locate(
					org.springframework.core.env.Environment environment) {
				ConfigClientProperties properties = this.defaultProperties.override(environment);
				CompositePropertySource composite = new CompositePropertySource("configService");
				//获取RestTemplate对象
				RestTemplate restTemplate = this.restTemplate == null
						? getSecureRestTemplate(properties)
						: this.restTemplate;
				Exception error = null;
				String errorBody = null;
				try {
					String[] labels = new String[] { "" };
					if (StringUtils.hasText(properties.getLabel())) {
						labels = StringUtils
								.commaDelimitedListToStringArray(properties.getLabel());
					}
					String state = ConfigClientStateHolder.getState();
					// Try all the labels until one works
					for (String label : labels) {
						//拉取配置信息
						Environment result = getRemoteEnvironment(restTemplate, properties,
								label.trim(), state);
								
								
			private Environment getRemoteEnvironment(RestTemplate restTemplate,
			ConfigClientProperties properties, String label, String state) {
				//构建请求的URL地址，参考config-server端暴露的地址
				String path = "/{name}/{profile}";
				String name = properties.getName();
				String profile = properties.getProfile();
				String token = properties.getToken();
				int noOfUrls = properties.getUri().length;
				if (noOfUrls > 1) {
					logger.info("Multiple Config Server Urls found listed.");
				}
		
				Object[] args = new String[] { name, profile };
				if (StringUtils.hasText(label)) {
					if (label.contains("/")) {
						label = label.replace("/", "(_)");
					}
					args = new String[] { name, profile, label };
					path = path + "/{label}";
				}
				ResponseEntity<Environment> response = null;
		
				for (int i = 0; i < noOfUrls; i++) {
					Credentials credentials = properties.getCredentials(i);
					String uri = credentials.getUri();
					String username = credentials.getUsername();
					String password = credentials.getPassword();
		
					logger.info("Fetching config from server at : " + uri);
		
					try {
						HttpHeaders headers = new HttpHeaders();
						addAuthorizationToken(properties, headers, username, password);
						if (StringUtils.hasText(token)) {
							headers.add(TOKEN_HEADER, token);
						}
						if (StringUtils.hasText(state) && properties.isSendState()) {
							headers.add(STATE_HEADER, state);
						}
		
						final HttpEntity<Void> entity = new HttpEntity<>((Void) null, headers);
						// 发送请求
						response = restTemplate.exchange(uri + path, HttpMethod.GET, entity,
								Environment.class, args);
					}
					.......
		
					if (response == null || response.getStatusCode() != HttpStatus.OK) {
						return null;
					}
					// 解析响应，即获取到的配置信息
					Environment result = response.getBody();
					return result;
				}
		
				return null;
			}