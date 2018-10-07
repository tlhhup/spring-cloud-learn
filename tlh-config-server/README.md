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