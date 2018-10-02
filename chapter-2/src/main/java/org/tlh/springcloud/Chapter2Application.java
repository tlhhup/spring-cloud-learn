package org.tlh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.tlh.springcloud.command.StartCommand;

/**
 * @author huping
 * @desc
 * @date 18/10/1
 */
@EnableAsync
@SpringBootApplication
public class Chapter2Application {

    public static void main(String[] args) {
        new StartCommand(args);
        SpringApplication.run(Chapter2Application.class,args);
    }

}
