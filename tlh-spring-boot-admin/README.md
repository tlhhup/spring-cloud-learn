###集成spring-boot-admin

spring-boot-admin是用于收集spring-boot应用的actuator端点的信息，通过AngularJs进行界面展示。

1. server端(集成eureka、security)
	1. 添加依赖 
	
			<dependencies>
				 <!-- 核心配置 -->
		        <dependency>
		            <groupId>de.codecentric</groupId>
		            <artifactId>spring-boot-admin-starter-server</artifactId>
		            <version>2.0.3</version>
		        </dependency>
		        <dependency>
		            <groupId>org.springframework.boot</groupId>
		            <artifactId>spring-boot-starter-web</artifactId>
		        </dependency>
		        <!-- 集成eureka的服务发现 -->
		        <dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		        </dependency>
		        <dependency>
		            <groupId>org.springframework.boot</groupId>
		            <artifactId>spring-boot-starter-actuator</artifactId>
		        </dependency>
				 <!-- 开启admin的认证 -->
		        <dependency>
		            <groupId>org.springframework.boot</groupId>
		            <artifactId>spring-boot-starter-security</artifactId>
		        </dependency>
		    </dependencies>
	2. 编写配置类
		
			@EnableEurekaClient
			@EnableAdminServer
			@SpringBootApplication
			public class SpringBootAdminApplication {
			
			    public static void main(String[] args) {
			        SpringApplication.run(SpringBootAdminApplication.class,args);
			    }
			
			}
	3. 添加安全配置(认证规则和过滤静态资源)
	
			@Configuration
			public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
			
			    private final String adminContextPath;
			
			    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
			        this.adminContextPath = adminServerProperties.getContextPath();
			    }
			
			    @Override
			    protected void configure(HttpSecurity http) throws Exception {
			        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
			        successHandler.setTargetUrlParameter("redirectTo");
			        successHandler.setDefaultTargetUrl(adminContextPath + "/");
			
			        http.authorizeRequests()
			                .antMatchers(adminContextPath + "/assets/**").permitAll()
			                .antMatchers(adminContextPath + "/login").permitAll()
			                .anyRequest().authenticated()
			                .and()
			                .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and()
			                .logout().logoutUrl(adminContextPath + "/logout").and()
			                .httpBasic().and()
			                .csrf()
			                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			                .ignoringAntMatchers(
			                        adminContextPath + "/instances",
			                        adminContextPath + "/actuator/**"
			                );
			    }
	
			}
	4. 编写配置文件
	
			server:
			  port: 8091
			spring:
			  application:
			    name: tlh-admin
			  security:
			    user:
			      name: tlh
			      password: 123456
			  boot:
			    admin:
			      discovery:
			        ignored-services: tlh-admin # 将自己忽略掉，不然在admin中会看到
			eureka:
			  instance:
			    appname: tlh-admin
			    lease-expiration-duration-in-seconds: 5
			    lease-renewal-interval-in-seconds: 5
			  client:
			    healthcheck:
			      enabled: true
			    service-url:
			      defaultZone: http://localhost:8089/eureka/
2. client端
	1. 添加依赖

		
	        <!-- spring boot admin client -->
	        <dependency>
	            <groupId>de.codecentric</groupId>
	            <artifactId>spring-boot-admin-starter-client</artifactId>
	            <version>2.0.3</version>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-security</artifactId>
	        </dependency>
	2. 添加admin端的认证信息

			eureka:
			  client:
			    service-url:
			      defaultZone: http://localhost:8089/eureka/
			    healthcheck:
			      enabled: true
			  instance:
			    appname: tlh-config-db
			    lease-expiration-duration-in-seconds: 5
			    lease-renewal-interval-in-seconds: 5
			    metadata-map: # 设置spring boot admin 的认证信息
			      user.name: tlh
			      user.password: 123456
			management:
			  endpoints:
			    web:
			      exposure:
			        include: "*"  # 开启所有的actuator端点(http协议)
			  endpoint:
			    health:
			      show-details: always 
	3. 编写security配置
	
			@Configuration
			public class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
			
			    @Override
			    protected void configure(HttpSecurity http) throws Exception {
			        http.authorizeRequests().anyRequest().permitAll()
			                .and().csrf().disable();
			    }
			
			}
