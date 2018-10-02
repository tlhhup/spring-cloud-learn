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