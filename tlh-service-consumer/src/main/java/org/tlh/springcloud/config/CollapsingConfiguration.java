package org.tlh.springcloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author huping
 * @desc
 * @date 18/10/5
 */
@Configuration
public class CollapsingConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HystrixContextInitInterceptor());
    }
}
