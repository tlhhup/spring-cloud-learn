###eureka服务端配置
在启用security的时候需要针对eureka的客户端配置csrf

    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //去掉eureka
            http.csrf().ignoringAntMatchers("/eureka/**");
            super.configure(http);
        }
    }

快速剔除down掉的client

    eureka:
      client:
        register-with-eureka: false # 去掉自我注册
        fetch-registry: false       # 不用拉取注册信息
        service-url:
          defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
      instance:
        appname: eureka-server
        hostname: localhost
      server:
        enable-self-preservation: false # 在开发阶段关闭保护模式
        eviction-interval-timer-in-ms: 5000 # 清理时间