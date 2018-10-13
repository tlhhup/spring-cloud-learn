package org.tlh.transaction.mq.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.PageInfo;
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
        messages.setExchange(sendMessageReqDto.getExchange());
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

    @Transactional
    @Override
    public MessageRepDto confirmMessageConsumed(Long messageId, String consumeSystem, Date consumeDate) {
        Optional<TransactionMessage> messages = this.transactionMessagesRepository.findById(messageId);
        messages.ifPresent(transactionMessage -> {
            transactionMessage.setConsumptionSystem(consumeSystem);
            transactionMessage.setConsumptionDate(consumeDate);
            transactionMessage.setStatus(MessageStatusEnum.CONSUMED.getCode());
        });
        return new MessageRepDto(messages.isPresent(),messageId);
    }

    @Transactional
    @Override
    public MessageRepDto confirmMessageDied(Long messageId, Date diedDate) {
        Optional<TransactionMessage> messages = this.transactionMessagesRepository.findById(messageId);
        //有值则执行，无者do nothing
        messages.ifPresent(transactionMessage -> {
            transactionMessage.setDieDate(diedDate);
            transactionMessage.setStatus(MessageStatusEnum.DIE.getCode());
        });
        return new MessageRepDto(messages.isPresent(),messageId);
    }

    @Transactional
    @Override
    public MessageRepDto incrementMessageRetry(Long messageId,Date sendDate) {
        Optional<TransactionMessage> messages = this.transactionMessagesRepository.findById(messageId);
        messages.ifPresent(transactionMessage -> {
            transactionMessage.setReSendCount(transactionMessage.getReSendCount()+1);
            transactionMessage.setSendTime(sendDate);
        });
        return new MessageRepDto(messages.isPresent(),messageId);
    }

    @Transactional
    @Override
    public MessageRepDto reSendDiedMessages() {
        return new MessageRepDto(this.transactionMessagesRepository.cleanDiedMessageStatus().orElse(0)>0,null);
    }

    @Override
    public List<TransactionMessageDto> findWaitingMessages(Pageable pageable) {
        return this.findMessagesByStatus(MessageStatusEnum.WAIT_CONSUMPTION,pageable).getData();
    }

    @Override
    public PageInfo<TransactionMessageDto> findMessagesByStatus(MessageStatusEnum status, Pageable pageable) {
        PageInfo<TransactionMessageDto> result=new PageInfo<>();
        Page<TransactionMessage> page = this.transactionMessagesRepository.findTransactionMessagesByStatus(status.getCode(), pageable);
        if(page!=null) {
            result.setCurrentPage(page.getNumber());
            result.setTotal(page.getTotalElements());
            result.setTotalPage(page.getTotalPages());

            List<TransactionMessage> messages =page.getContent();
            if (messages != null) {
                List<TransactionMessageDto> collect = messages.parallelStream().map(message -> {
                    TransactionMessageDto item = new TransactionMessageDto();

                    item.setConsumptionDate(message.getConsumptionDate());
                    item.setConsumptionSystem(message.getConsumptionSystem());
                    item.setDieDate(message.getDieDate());
                    item.setDiedCount(message.getDieCount());
                    item.setId(message.getId());
                    item.setSendTime(message.getSendTime());
                    item.setStatus(message.getStatus());
                    item.setContent(message.getMessage());
                    item.setCreateTime(message.getCreateTime());
                    item.setRetryCount(message.getReSendCount());
                    item.setExchange(message.getExchange());
                    item.setRoutingKey(message.getRoutingKey());
                    item.setSendSystem(message.getSendSystem());

                    return item;
                }).collect(Collectors.toList());
                result.setData(collect);
            }
        }
        return result;
    }

}
