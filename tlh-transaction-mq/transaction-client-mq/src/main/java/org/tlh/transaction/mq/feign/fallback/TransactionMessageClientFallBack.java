package org.tlh.transaction.mq.feign.fallback;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.PageInfo;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.feign.TransactionMessageClient;

import java.util.List;

/**
 * @author huping
 * @desc
 * @date 18/10/11
 */
@Slf4j
@Component
public class TransactionMessageClientFallBack implements TransactionMessageClient {

    @Override
    public MessageRepDto sendMessage(SendMessageReqDto sendMessageReqDto) {
        log.info("client sendMessage fail");
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto confirmSendMessages(Long messageId) {
        log.info("client confirmSendMessage fail");
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto confirmMessageConsumed(Long messageId, String consumeSystem,String consumeDate) {
        log.info("client confirmMessageConsumed fail");
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto confirmMessageDied(Long messageId,String diedDate) {
        log.info("client confirmMessageDied fail");
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto incMessageRetry(Long messageId, String sendDate) {
        log.info("client incMessageRetry fail");
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto reSendDiedMessages() {
        log.info("client reSendDiedMessages fail");
        return new MessageRepDto(false,0l);
    }

    @Override
    public List<TransactionMessageDto> queryWaitingMessages(int page, int size) {
        log.info("client queryWaitingMessages fail");
        return Lists.newArrayList();
    }

    @Override
    public PageInfo<TransactionMessageDto> queryMessages(int status, int page, int size) {
        log.info("client queryMessages fail");
        return new PageInfo<>();
    }
}
