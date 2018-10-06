###基于JWT的认证中心
1. 添加依赖

		<dependencies>
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-web</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-data-jpa</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>mysql</groupId>
	            <artifactId>mysql-connector-java</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-actuator</artifactId>
	        </dependency>
	        <!-- validator -->
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-validation</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>org.projectlombok</groupId>
	            <artifactId>lombok</artifactId>
	        </dependency>
	        <!-- jwt -->
	        <dependency>
	            <groupId>com.auth0</groupId>
	            <artifactId>java-jwt</artifactId>
	            <version>3.4.0</version>
	        </dependency>
	        <!-- swagger2 -->
	        <dependency>
	            <groupId>com.spring4all</groupId>
	            <artifactId>swagger-spring-boot-starter</artifactId>
	            <version>1.8.0.RELEASE</version>
	        </dependency>
	        <!-- test-->
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-test</artifactId>
	        </dependency>
	    </dependencies>

2. 编写jwt工具类(签发、校验)

		@Slf4j
		public class JWTUtil {
		
		    private static volatile JWTUtil instance;
		    private static Algorithm algorithm;
		
		    private JWTUtil(){
		        String secret="123456";
		        algorithm = Algorithm.HMAC256(secret);
		    }
		
		    public static JWTUtil getInstance() {
		        if(instance==null){
		            synchronized (JWTUtil.class){
		                if (instance==null){
		                    instance=new JWTUtil();
		                }
		            }
		        }
		        return instance;
		    }
		
		    /**
		     * 生成token
		     * @param rawJson
		     * @return
		     * @throws TokenSignException
		     */
		    public String signToken(String rawJson) throws TokenSignException {
		        try {
		            String token = JWT.create().withIssuer("auth0")//
		                                    .withSubject(rawJson)//
		                                    .withExpiresAt(DateUtils.addDays(new Date(),1))//
		                                    .sign(algorithm);
		            return token;
		        } catch (IllegalArgumentException e) {
		            log.error("jwt",e);
		            throw new TokenSignException("参数错误");
		        } catch (JWTCreationException e) {
		            log.error("jwt",e);
		            throw new TokenSignException("生成Token失败");
		        }
		    }
		
		    /**
		     * 校验token
		     * @param token
		     * @return
		     * @throws TokenSignException
		     */
		    public String verifyToken(String token) throws TokenSignException {
		        try {
		            JWTVerifier build = JWT.require(algorithm).withIssuer("auth0").build();
		            DecodedJWT verify = build.verify(token);
		            return verify.getSubject();
		        }catch (TokenExpiredException e){
		            log.error("jwt",e);
		            throw new TokenSignException("token过期");
		        }catch (JWTVerificationException e) {
		            log.error("jwt",e);
		            throw new TokenSignException("校验token失败");
		        }
		    }
		
		    public static void main(String[] args) throws JsonProcessingException, TokenSignException {
		        Map<String,Object> data=new HashMap<>();
		        data.put("userName","tlh");
		        data.put("age","10");
		        data.put("id",1);
		        String json = JsonUtils.toJson(data);
		        System.out.println(json);
		        String token = JWTUtil.getInstance().signToken(json);
		        System.out.println(token);
		        System.out.println("**********");
		        System.out.println(JWTUtil.getInstance().verifyToken(token));
		    }
	
		
		}
3. 编写配置文件

		server:
		  port: 8092
		eureka:
		  client:
		    healthcheck:
		      enabled: true
		    service-url:
		      defaultZone: http://localhost:8089/eureka/
		  instance:
		    appname: tlh-auth
		    lease-renewal-interval-in-seconds: 5
		    lease-expiration-duration-in-seconds: 5
		    status-page-url: http://localhost:${server.port}/swagger-ui.html
		spring:
		  application:
		    name: tlh-auth
		  datasource:
		    driver-class-name: org.gjt.mm.mysql.Driver
		    username: 数据库用户名
		    password: 数据库密码
		    url: jdbc:mysql:///tlh_auth
		    type: org.springframework.jdbc.datasource.DriverManagerDataSource
		  jpa:
		    hibernate:
		      ddl-auto: update
		  messages:
		    basename: error
		swagger:
		  enabled: true
		  title: 认证中心
		  apply-default-response-messages: false
		  global-response-message:
		    head[0]:
		      code: 200
4. 主类


		@EnableSwagger2Doc
		@EnableEurekaClient
		@SpringBootApplication
		@EnableTransactionManagement
		public class TlhAuthApplication {
		
		    public static void main(String[] args) {
		        SpringApplication.run(TlhAuthApplication.class,args);
		    }
		
		}  
		
###服务提供方校验token
通过在服务提供方定义spring mvc的拦截器处理

1. 编写拦截器

		public class AuthInterceptor implements HandlerInterceptor {
	
		    private RestTemplate restTemplate;
		
		    public AuthInterceptor(RestTemplate restTemplate) {
		        this.restTemplate=restTemplate;
		    }
		
		    @Override
		    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		        response.setContentType("application/json;charset=utf-8");
		        String authorization = request.getHeader("Authorization");
		        if(StringUtils.isEmpty(authorization)){
		            PrintWriter writer = response.getWriter();
		            writer.write(JsonUtils.toJson(new ResponseDto<>(ResponseDto.NO_AUTH_CODE,null,"请求头缺少Authorization")));
		            return false;
		        }else{
		            Map<String,String> data=new HashMap<>();
		            data.put("token",authorization);
		            Map<String,Object> map = restTemplate.postForObject("http://tlh-auth/userAuth/verify", data, Map.class);
		            if(map==null){
		                PrintWriter writer = response.getWriter();
		                writer.write(JsonUtils.toJson(new ResponseDto<>(ResponseDto.NO_AUTH_CODE,null,"token过期")));
		                return false;
		            }
		            String userInfo = map.get("data").toString();
		            if(StringUtils.hasText(userInfo)){
		                return true;
		            }else{
		                PrintWriter writer = response.getWriter();
		                writer.write(JsonUtils.toJson(new ResponseDto<>(ResponseDto.NO_AUTH_CODE,null,"token验证失败")));
		                return false;
		            }
		        }
		    }
		}
2. 配置拦截器	

		@Configuration
		public class SecurityConfig implements WebMvcConfigurer{
		
		    @Autowired
		    private RestTemplate restTemplate;
		
		    @Override
		    public void addInterceptors(InterceptorRegistry registry) {
		        registry.addInterceptor(new AuthInterceptor(restTemplate));
		    }
		}
		
###服务消费端通过resttemplate拦截器添加认证头信息
1. 定义拦截器
	
		@Component
		public class RestTemplateAuthInterceptor implements ClientHttpRequestInterceptor {
		
		    @Override
		    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		        // todo token从缓存中获取
		        String token = "";
		        request.getHeaders().add("Authorization", token);
		        return execution.execute(request, body);
		    }
		
		}
	
2. 注入拦截器

		@Bean
	    @Primary //多个同一类型的bean时需要通过该注解定义primary，在通过类型注入时，首先注入该bean
	    public RestTemplate normalRestTemplate(){
	        return new RestTemplate();
	    }
	
	    @Bean
	    @LoadBalanced
	    public RestTemplate restTemplate(RestTemplateAuthInterceptor restTemplateAuthInterceptor){
	        RestTemplate restTemplate = new RestTemplate();
	        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
	        //添加拦截器
	        interceptors.add(restTemplateAuthInterceptor);
	        restTemplate.setInterceptors(interceptors);
	        return restTemplate;
	    }
	    
###服务消费端通过feign拦截器添加认证头信息
1. 编写拦截器
	
		public class FeignAuthRequestInterceptor implements RequestInterceptor {
	
		    @Override
		    public void apply(RequestTemplate template) {
		        String token = "";
		        template.header("Authorization", token);
		    }
		}

2. 编写feign的配置类

		@Configuration
		public class FeignConfiguration {
		
		    @Bean
		    Logger.Level feignLoggerLevel(){
		        return Logger.Level.FULL;
		    }
		
		    @Bean
		    public FeignAuthRequestInterceptor feignAuthRequestInterceptor(){
		        return new FeignAuthRequestInterceptor();
		    }
		
		}
3. 在feign的client中使用配置

		@FeignClient(name = "user-service",path = "/UserController",fallback = UserFeignClientFallBack.class,configuration = FeignConfiguration.class)
		public interface UserFeignClient {
		
		    @PostMapping("/add")
		    String register();
		
		    @DeleteMapping("/delete/{id}")
		    String delete(@PathVariable("id") int id);
		
		}

###zuul网关中传递token到后端服务
1. 通过pre类型的过滤器处理

		@Component
		public class AuthHeaderFilter extends ZuulFilter {
		
		    @Override
		    public String filterType() {
		        return FilterConstants.PRE_TYPE;
		    }
		
		    @Override
		    public int filterOrder() {
		        return 0;
		    }
		
		    @Override
		    public boolean shouldFilter() {
		        RequestContext ctx = RequestContext.getCurrentContext();
		        Object isSuccess = ctx.get("isSuccess");
		        return isSuccess==null?true:Boolean.valueOf(isSuccess.toString());
		    }
		
		    @Override
		    public Object run() throws ZuulException {
		        RequestContext ctx = RequestContext.getCurrentContext();
		        String token = "";
		        ctx.addZuulRequestHeader("Authorization", token);
		        return null;
		    }
		}
		
###token的获取
通过定时任务调用认证中心获取token并缓存到本地	


