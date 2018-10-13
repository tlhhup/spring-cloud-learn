package org.tlh.transaction.mq.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.feign.TransactionMessageClient;
import org.tlh.transaction.mq.utils.DateUtil;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author huping
 * @desc
 * @date 18/10/13
 */
@Slf4j
@Component
public class ProcessMessagesTask {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TransactionMessageClient transactionMessageClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private ExecutorService sendExecutorService;
    private Semaphore semaphore;

    @PostConstruct
    public void init(){
        this.sendExecutorService = Executors.newFixedThreadPool(10);
        this.semaphore=new Semaphore(20);//20的并发
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void processWaitingMessages(){
        log.info("start process waiting message");
        //获取锁
        RLock lock = redissonClient.getLock("transaction-mq");
        try {
            //锁
            lock.lock();
            long sleepTime = processPreUnit();
            if(sleepTime>0){
                Thread.sleep(sleepTime);
            }
        } catch (Exception e) {
            log.error("process waiting message error",e);
        }finally {
            lock.unlock();
        }
    }

    private long processPreUnit() throws Exception{
        log.info("process pre unit end");
        long sleepTime=10000;
        //每次取头上没有被消费的， TODO 此处合理否?
        List<TransactionMessageDto> messages = this.transactionMessageClient.queryWaitingMessages(1, 5000);
        if(messages!=null&&!messages.isEmpty()){
            if(messages.size()==5000){
                sleepTime=0;
            }
            CountDownLatch countDownLatch=new CountDownLatch(messages.size());
            for(TransactionMessageDto message:messages){
                //获取资源
                this.semaphore.acquire();
                this.sendExecutorService.submit(()->{
                    try {
                        pushMessage(message);
                    } catch (Exception e) {
                        log.error("push waiting message error",e);
                    }finally {
                        semaphore.release();
                        countDownLatch.countDown();
                    }
                });
            }
            //等待一次发送执行完
            countDownLatch.await();
        }
        log.info("process pre unit end");
        return sleepTime;
    }

    private void pushMessage(TransactionMessageDto message){
        //1.判断消息是否达到死亡条件
        if(message.isDied()){
            this.transactionMessageClient.confirmMessageDied(message.getId(), DateUtil.now());
            return;
        }
        //2.发送消息
        long currentTimeMillis = System.currentTimeMillis();
        long sendTime=0;
        if(message.getSendTime()!=null){
            sendTime=message.getSendTime().getTime();
        }
        //发送间隔在一分钟
        if(currentTimeMillis-sendTime>60*1000){
            try {
                log.info("push message start");
                //3.将消息推送到消息队列
                this.rabbitTemplate.convertAndSend(message.getExchange(),message.getRoutingKey(),message.getContent());
                //4.增加发送次数
                this.transactionMessageClient.incMessageRetry(message.getId(),DateUtil.now());
                log.info("push message success");
            } catch (AmqpException e) {
                log.info("push message error");
            }
        }
    }

}
