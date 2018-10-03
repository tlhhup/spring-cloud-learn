1. 通过hystrix-dashboard来监控hystrix(服务降级、失败、熔断)信息
	1. 添加依赖
	
			<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
	2. 配置类添加EnableHystrixDashboard注解
	3. 需要监控的服务体检actuator依赖并开启hystrix.stream的web接口
	
			management:
			  endpoints:
			    web:
			      exposure:
			        include: hystrix.stream # 开启stream 的web接口
2. 通过turbine来聚合集群的hystrix信息，将注册中心中同一服务的所有节点的hystrix.stream接口的数据聚合之后通过dashboard进行展示
	1. 在以上配置的基础上添加turbine的依赖
	
			<dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	        </dependency>
	2. 启动类添加注解
		1. EnableEurekaClient：把自己注册到注册中心，目的拉取注册中心的service信息
		2. EnableTurbine：开启turbine
	3. 	配置文件添加需要监控的集群信息
		
			turbine:
			  aggregator:
			    clusterConfig: default,EUREKA-COMSUMER,CONSUME-FEIGN # 需要监控的集群信息
			  appConfig: eureka-comsumer,consume-feign # 需要聚合的服务的
	4. 通过dashboard来监控集群信息
			
			# clusterName 必须匹配配置文件中的clusterConfig属性中的任何一个
			http://turbine-hostname:port/turbine.stream?cluster=[clusterName]	