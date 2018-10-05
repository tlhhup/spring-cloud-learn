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