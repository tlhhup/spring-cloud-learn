package org.tlh.transaction.mq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tlh.transaction.mq.dto.SendMessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.service.TransactionMessagesService;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@RestController
@RequestMapping("/TxMessages")
public class TransactionMessageController {

    @Autowired
    private TransactionMessagesService transactionMessagesService;

    @PostMapping("/sendMessage")
    public SendMessageRepDto sendMessage(@RequestBody SendMessageReqDto sendMessageReqDto){
        return this.transactionMessagesService.saveMessage(sendMessageReqDto);
    }

    @PostMapping("/conformSend/{messageId}")
    public SendMessageRepDto conformSendMessages(@PathVariable("messageId") Long messageId){
        return this.transactionMessagesService.conformSendMessage(messageId);
    }


}
