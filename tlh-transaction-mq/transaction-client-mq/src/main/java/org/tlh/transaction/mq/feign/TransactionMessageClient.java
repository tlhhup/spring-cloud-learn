package org.tlh.transaction.mq.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.PageInfo;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.enums.MessageStatusEnum;
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
     * 事务发起方，确认发送消息,发送消息之后，通过返回的结果来确认消息已经发送成功
     * @param messageId 消息ID
     * @return
     */
    @PostMapping("/confirmSend/{messageId}")
    MessageRepDto confirmSendMessages(@PathVariable("messageId") Long messageId);

    /**
     * 事务被动方：确认消息被消费
     * @param messageId     消息ID
     * @param consumeSystem 被动方系统
     * @param consumeDate   消费时间
     * @return
     */
    @PostMapping("/confirmMessageConsume/{messageId}")
    MessageRepDto confirmMessageConsumed(@PathVariable("messageId")Long messageId,
                                         @RequestParam("consume_system") String consumeSystem,
                                         @RequestParam("consume_date")Date consumeDate);

    /**
     * 任务调度：确认消息死亡
     * @param messageId 消息ID
     * @param diedDate  死亡时间
     * @return
     */
    @PostMapping("/confirmMessageDied/{messageId}")
    MessageRepDto confirmMessageDied(@PathVariable("messageId") Long messageId,
                                     @RequestParam("died_date")Date diedDate);

    /**
     * 任务调度：增加消息重试次数
     * @param messageId 消息ID
     * @param sendDate  发送时间
     * @return
     */
    @PostMapping("/incMessageRetry/{messageId}")
    MessageRepDto incMessageRetry(@PathVariable("messageId") Long messageId,
                                  @RequestParam("send_date")Date sendDate);

    /**
     * 任务调度：重新发送死亡的消息
     * @return
     */
    @PostMapping("/reSendDiedMessage")
    MessageRepDto reSendDiedMessages();

    /**
     * 查询等待发送的消息
     * @param page 当前页，默认0
     * @param size 每页条数，默认10
     * @return
     */
    @GetMapping("/queryWaitingMessages")
    List<TransactionMessageDto> queryWaitingMessages(@RequestParam(value = "page",defaultValue = "0") int page,
                                                     @RequestParam(value = "size",defaultValue = "10") int size);

    /**
     * 查询指定状态的message
     * @param status  消息状态 {@link MessageStatusEnum}
     * @param page    当前页，默认0
     * @param size    每页条数，默认10
     * @return
     */
    @GetMapping("/queryMessages/{status}")
    PageInfo<TransactionMessageDto> queryMessages(@PathVariable("status")int status,
                                                  @RequestParam(value = "page",defaultValue = "0") int page,
                                                  @RequestParam(value = "size",defaultValue = "10") int size);

}
