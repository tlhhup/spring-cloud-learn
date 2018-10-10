package org.tlh.transaction.mq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
