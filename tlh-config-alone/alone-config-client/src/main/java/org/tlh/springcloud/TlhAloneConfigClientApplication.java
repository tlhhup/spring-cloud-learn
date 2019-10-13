package org.tlh.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.tlh.springcloud.config.ClientProperties;

/**
 * Created by 离歌笑tlh/hu ping on 2019/10/13
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(ClientProperties.class)
public class TlhAloneConfigClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TlhAloneConfigClientApplication.class,args);
    }

    @Autowired
    private ClientProperties clientProperties;

    @Override
    public void run(String... args) throws Exception {
      log.info(clientProperties.getName());
    }
}
