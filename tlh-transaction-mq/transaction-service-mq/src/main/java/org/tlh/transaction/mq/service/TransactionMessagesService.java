package org.tlh.transaction.mq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.repositories.TransactionMessagesRepository;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Service
@Transactional
public class TransactionMessagesService {

    @Autowired
    private TransactionMessagesRepository transactionMessagesRepository;

}
