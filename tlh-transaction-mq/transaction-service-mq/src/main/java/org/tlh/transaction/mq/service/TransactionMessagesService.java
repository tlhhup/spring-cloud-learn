package org.tlh.transaction.mq.service;

import org.tlh.transaction.mq.dto.SendMessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
public interface TransactionMessagesService {

    /**
     * 事务发起方，存储预发送消息
     * @param sendMessageReqDto
     * @return
     */
    SendMessageRepDto saveMessage(SendMessageReqDto sendMessageReqDto);

    /**
     * 事务发起方，确认发送消息
     * @param messageId
     * @return
     */
    SendMessageRepDto conformSendMessage(Long messageId);


}
