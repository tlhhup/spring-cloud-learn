package org.tlh.gateway.swagger;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SwaggerButlerProperties.class)
public class SwaggerButlerAutoConfig {

    @Bean
    @Primary
    public SwaggerResourcesProcessor swaggerResourcesProcessor() {
        return new SwaggerResourcesProcessor();
    }

}
