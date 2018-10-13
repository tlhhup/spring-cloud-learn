package org.tlh.transaction.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tlh.transaction.mq.jobs.ProcessMessagesTask;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@EnableScheduling
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class TransactionTaskMqApplication implements CommandLineRunner{

    public static void main(String[] args) {
        SpringApplication.run(TransactionTaskMqApplication.class,args);
    }

    @Autowired
    private ProcessMessagesTask processMessagesTask;

    @Override
    public void run(String... args) throws Exception {
        this.processMessagesTask.processWaitingMessages();
    }
}
