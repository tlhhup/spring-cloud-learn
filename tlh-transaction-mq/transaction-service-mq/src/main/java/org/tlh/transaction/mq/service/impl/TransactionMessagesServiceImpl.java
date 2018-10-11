package org.tlh.transaction.mq.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.entity.TransactionMessage;
import org.tlh.transaction.mq.enums.MessageStatusEnum;
import org.tlh.transaction.mq.repositories.TransactionMessagesRepository;
import org.tlh.transaction.mq.service.TransactionMessagesService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public MessageRepDto saveMessage(SendMessageReqDto sendMessageReqDto) {
        MessageRepDto result=new MessageRepDto();

        TransactionMessage messages=new TransactionMessage();
        messages.setSendSystem(sendMessageReqDto.getSendSystem());
        messages.setDieCount(sendMessageReqDto.getRetryCount());
        messages.setRoutingKey(sendMessageReqDto.getRoutingKey());
        messages.setMessage(sendMessageReqDto.getContent());
        messages.setCreateTime(sendMessageReqDto.getCreateTime());
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
    public MessageRepDto confirmSendMessage(Long messageId) {
        MessageRepDto result=new MessageRepDto();
        result.setMessageId(messageId);
        Integer count = this.transactionMessagesRepository.updateMessageStatus(messageId, MessageStatusEnum.CONFORM_SEND.getCode());
        if(count!=null&&count>0){
            result.setSuccess(true);
        }else{
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public MessageRepDto confirmMessageConsumed(Long messageId, String consumeSystem) {
        Optional<TransactionMessage> messages = this.transactionMessagesRepository.findById(messageId);
        messages.ifPresent(transactionMessage -> {
            transactionMessage.setConsumptionSystem(consumeSystem);
            transactionMessage.setConsumptionDate(new Date());
            transactionMessage.setStatus(MessageStatusEnum.CONSUMED.getCode());
        });
        return new MessageRepDto(messages.isPresent(),messageId);
    }

    @Override
    public MessageRepDto confirmMessageDied(Long messageId) {
        Optional<TransactionMessage> messages = this.transactionMessagesRepository.findById(messageId);
        //有值则执行，无者do nothing
        messages.ifPresent(transactionMessage -> {
            transactionMessage.setDieDate(new Date());
            transactionMessage.setStatus(MessageStatusEnum.DIE.getCode());
        });
        return new MessageRepDto(messages.isPresent(),messageId);
    }

    @Override
    public MessageRepDto incrementMessageRetry(Long messageId,Date sendDate) {
        Optional<TransactionMessage> messages = this.transactionMessagesRepository.findById(messageId);
        messages.ifPresent(transactionMessage -> {
            transactionMessage.setReSendCount(transactionMessage.getReSendCount()+1);
            transactionMessage.setSendTime(sendDate);
        });
        return new MessageRepDto(messages.isPresent(),messageId);
    }

    @Override
    public MessageRepDto reSendDiedMessages() {
        return new MessageRepDto(this.transactionMessagesRepository.cleanDiedMessageStatus().orElse(0)>0,null);
    }

    @Override
    public List<TransactionMessageDto> findWaitingMessages(Pageable pageable) {
        return this.findMessagesByStatus(MessageStatusEnum.WAIT_CONSUMPTION,pageable);
    }

    @Override
    public List<TransactionMessageDto> findMessagesByStatus(MessageStatusEnum status, Pageable pageable) {
        List<TransactionMessage> messages = this.transactionMessagesRepository.findTransactionMessagesByStatus(status.getCode(), pageable);
        if(messages!=null){
            return messages.parallelStream().map(message->{
                TransactionMessageDto item=new TransactionMessageDto();

                item.setConsumptionDate(message.getConsumptionDate());
                item.setConsumptionSystem(message.getConsumptionSystem());
                item.setDieDate(message.getDieDate());
                item.setId(message.getId());
                item.setSendTime(message.getSendTime());
                item.setStatus(message.getStatus());
                item.setContent(message.getMessage());
                item.setCreateTime(message.getCreateTime());
                item.setRetryCount(message.getReSendCount());
                item.setRoutingKey(message.getRoutingKey());
                item.setSendSystem(message.getSendSystem());

                return item;
            }).collect(Collectors.toList());
        }
        return null;
    }

}
