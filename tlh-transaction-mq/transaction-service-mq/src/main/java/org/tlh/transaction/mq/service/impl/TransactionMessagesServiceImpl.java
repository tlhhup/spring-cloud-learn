package org.tlh.transaction.mq.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.dto.SendMessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.entity.TransactionMessages;
import org.tlh.transaction.mq.enums.MessageStatusEnum;
import org.tlh.transaction.mq.repositories.TransactionMessagesRepository;
import org.tlh.transaction.mq.service.TransactionMessagesService;

import java.util.Date;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Service
@Transactional(readOnly = true)
public class TransactionMessagesServiceImpl implements TransactionMessagesService{


    @Autowired
    private TransactionMessagesRepository transactionMessagesRepository;

    @Transactional
    @Override
    public SendMessageRepDto saveMessage(SendMessageReqDto sendMessageReqDto) {
        SendMessageRepDto result=new SendMessageRepDto();

        TransactionMessages messages=new TransactionMessages();
        messages.setSendSystem(sendMessageReqDto.getSendSystem());
        messages.setDieCount(sendMessageReqDto.getRetryCount());
        messages.setRoutingKey(sendMessageReqDto.getRoutingKey());
        messages.setMessage(sendMessageReqDto.getContent());
        messages.setCreateTime(new Date());
        messages.setStatus(MessageStatusEnum.WAIT_CONSUMPTION.getCode());

        try {
            this.transactionMessagesRepository.save(messages);
            result.setMessageId(messages.getId());
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return result;
    }

    @Transactional
    @Override
    public SendMessageRepDto conformSendMessage(Long messageId) {
        SendMessageRepDto result=new SendMessageRepDto();
        result.setMessageId(messageId);
        Integer count = this.transactionMessagesRepository.updateMessageStatus(messageId, MessageStatusEnum.CONFORM_SEND.getCode());
        if(count!=null&&count>0){
            result.setSuccess(true);
        }else{
            result.setSuccess(false);
        }
        return result;
    }
}
