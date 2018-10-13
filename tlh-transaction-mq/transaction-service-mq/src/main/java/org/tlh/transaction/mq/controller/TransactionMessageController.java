package org.tlh.transaction.mq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.PageInfo;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.enums.MessageStatusEnum;
import org.tlh.transaction.mq.service.TransactionMessagesService;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@RestController
@RequestMapping("/txMessages")
public class TransactionMessageController {

    @Autowired
    private TransactionMessagesService transactionMessagesService;

    @PostMapping("/sendMessage")
    public MessageRepDto sendMessage(@Valid @RequestBody SendMessageReqDto sendMessageReqDto){
        return this.transactionMessagesService.saveMessage(sendMessageReqDto);
    }

    @PostMapping("/confirmSend/{messageId}")
    public MessageRepDto confirmSendMessages(@PathVariable("messageId") Long messageId){
        return this.transactionMessagesService.confirmSendMessage(messageId);
    }

    @PostMapping("/confirmMessageConsume/{messageId}")
    public MessageRepDto confirmMessageConsumed(@PathVariable("messageId")Long messageId,
                                                @RequestParam("consume_system") String consumeSystem,
                                                @RequestParam("consume_date")Date consumeDate){
        return this.transactionMessagesService.confirmMessageConsumed(messageId,consumeSystem,consumeDate);
    }

    @PostMapping("/confirmMessageDied/{messageId}")
    public MessageRepDto confirmMessageDied(@PathVariable("messageId") Long messageId,
                                            @RequestParam("died_date")Date diedDate){
        return this.transactionMessagesService.confirmMessageDied(messageId,diedDate);
    }

    @PostMapping("/incMessageRetry/{messageId}")
    public MessageRepDto incMessageRetry(@PathVariable("messageId") Long messageId,
                                         @RequestParam("send_date")Date sendDate){
        return this.transactionMessagesService.incrementMessageRetry(messageId,sendDate);
    }

    @PostMapping("/reSendDiedMessage")
    public MessageRepDto reSendDiedMessages(){
        return this.transactionMessagesService.reSendDiedMessages();
    }

    @GetMapping("/queryWaitingMessages")
    public List<TransactionMessageDto> queryWaitingMessages(Pageable pageable){
        return this.transactionMessagesService.findWaitingMessages(pageable);
    }

    @GetMapping("/queryMessages/{status}")
    public PageInfo<TransactionMessageDto> queryMessages(@PathVariable("status")int status,Pageable pageable){
        return this.transactionMessagesService.findMessagesByStatus(MessageStatusEnum.getMessageStatusByCode(status),pageable);
    }

}
