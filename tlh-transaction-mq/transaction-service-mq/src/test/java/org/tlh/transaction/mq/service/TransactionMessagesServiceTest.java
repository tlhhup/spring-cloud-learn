package org.tlh.transaction.mq.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.dto.SendMessageReqDto;

import java.util.Date;

/**
 * @author huping
 * @desc
 * @date 18/10/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TransactionMessagesServiceTest {

    @Autowired
    private TransactionMessagesService transactionMessagesService;

    @Test
    public void saveMessage() throws Exception {
        SendMessageReqDto send=new SendMessageReqDto();
        send.setContent("hh");
        send.setCreateTime(new Date());
        send.setRetryCount(10);
        send.setRoutingKey("fff");
        send.setSendSystem("fasdf");
        this.transactionMessagesService.saveMessage(send);
    }

    @Test
    public void confirmSendMessage() throws Exception {
    }

    @Test
    public void confirmMessageConsumed() throws Exception {
    }

    @Test
    public void confirmMessageDied() throws Exception {
        this.transactionMessagesService.confirmMessageDied(1l, new Date());
    }

    @Test
    public void incrementMessageRetry() throws Exception {
        this.transactionMessagesService.incrementMessageRetry(1l,new Date());
    }

    @Test
    public void reSendDiedMessages() throws Exception {
    }

    @Test
    public void findWaitingMessages() throws Exception {
    }

    @Test
    public void findMessagesByStatus() throws Exception {
    }

}