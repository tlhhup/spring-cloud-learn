package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Created by 离歌笑tlh/hu ping on 2019/10/13
 * <p>
 * Github: https://github.com/tlhhup
 */
@EnableConfigServer
@SpringBootApplication
public class TlhAloneConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlhAloneConfigServerApplication.class,args);
    }

}
