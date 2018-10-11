package org.tlh.transaction.mq.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.feign.fallback.TransactionMessageClientFallBack;

import java.util.Date;
import java.util.List;

/**
 * @author huping
 * @desc
 * @date 18/10/11
 */
@FeignClient(name = "tlh-transaction-service-mq",path = "/txMessages",fallback = TransactionMessageClientFallBack.class)
public interface TransactionMessageClient {

    /**
     * 事务发起方，存储预发送消息
     * @param sendMessageReqDto
     * @return
     */
    @PostMapping("/sendMessage")
    MessageRepDto sendMessage(@RequestBody SendMessageReqDto sendMessageReqDto);

    /**
     * 事务发起方，确认发送消息
     * @param messageId
     * @return
     */
    @PostMapping("/confirmSend/{messageId}")
    MessageRepDto confirmSendMessages(@PathVariable("messageId") Long messageId);

    /**
     * 事务被动方：确认消息被消费
     * @param messageId
     * @param consumeSystem
     * @return
     */
    @PostMapping("/confirmMessageConsume/{messageId}")
    MessageRepDto confirmMessageConsumed(@PathVariable("messageId")Long messageId,
                                         @RequestParam("consumeSystem") String consumeSystem);

    /**
     * 任务调度：确认消息死亡
     * @param messageId
     * @return
     */
    @PostMapping("/confirmMessageDied/{messageId}")
    MessageRepDto confirmMessageDied(@PathVariable("messageId") Long messageId);

    /**
     * 任务调度：增加消息重试次数
     * @param messageId
     * @param sendDate
     * @return
     */
    @PostMapping("/incMessageRetry/{messageId}")
    MessageRepDto incMessageRetry(@PathVariable("messageId") Long messageId,
                                  @RequestParam("sendDate")Date sendDate);

    /**
     * 任务调度：重新发送死亡的消息
     * @return
     */
    @PostMapping("/reSendDiedMessage")
    MessageRepDto reSendDiedMessages();

    /**
     * 查询等待发送的消息
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/queryWaitingMessages")
    List<TransactionMessageDto> queryWaitingMessages(@RequestParam("page") int page,
                                                     @RequestParam("size") int size);

    /**
     * 查询指定状态的message
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/queryMessages/{status}")
    List<TransactionMessageDto> queryMessages(@PathVariable("status")int status,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size);

}
