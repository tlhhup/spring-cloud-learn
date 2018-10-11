package org.tlh.transaction.mq.service;

import org.springframework.data.domain.Pageable;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.enums.MessageStatusEnum;

import java.util.Date;
import java.util.List;

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
    MessageRepDto saveMessage(SendMessageReqDto sendMessageReqDto);

    /**
     * 事务发起方，确认发送消息
     * @param messageId
     * @return
     */
    MessageRepDto confirmSendMessage(Long messageId);

    /***
     * 事务被动方：确认消息被消费
     * @param messageId
     * @param consumeSystem
     * @return
     */
    MessageRepDto confirmMessageConsumed(Long messageId,String consumeSystem);

    /**
     * 任务调度：确认消息死亡
     * @param messageId
     * @return
     */
    MessageRepDto confirmMessageDied(Long messageId);

    /**
     * 任务调度：增加消息重试次数:
     * @param messageId
     * @param sendDate
     * @return
     */
    MessageRepDto incrementMessageRetry(Long messageId, Date sendDate);

    /**
     * 任务调度：重新发送死亡的消息
     * @return
     */
    MessageRepDto reSendDiedMessages();

    /**
     * 查询等待发送的消息
     * @param pageable
     * @return
     */
    List<TransactionMessageDto> findWaitingMessages(Pageable pageable);

    /**
     * 查询指定状态的message
     * @param status
     * @param pageable
     * @return
     */
    List<TransactionMessageDto> findMessagesByStatus(MessageStatusEnum status,Pageable pageable);

}
