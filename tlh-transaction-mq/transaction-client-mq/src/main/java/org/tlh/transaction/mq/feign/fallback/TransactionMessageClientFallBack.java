package org.tlh.transaction.mq.feign.fallback;

import com.google.common.collect.Lists;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.dto.TransactionMessageDto;
import org.tlh.transaction.mq.feign.TransactionMessageClient;

import java.util.Date;
import java.util.List;

/**
 * @author huping
 * @desc
 * @date 18/10/11
 */
public class TransactionMessageClientFallBack implements TransactionMessageClient {

    @Override
    public MessageRepDto sendMessage(SendMessageReqDto sendMessageReqDto) {
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto confirmSendMessages(Long messageId) {
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto confirmMessageConsumed(Long messageId, String consumeSystem) {
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto confirmMessageDied(Long messageId) {
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto incMessageRetry(Long messageId, Date sendDate) {
        return new MessageRepDto(false,0l);
    }

    @Override
    public MessageRepDto reSendDiedMessages() {
        return new MessageRepDto(false,0l);
    }

    @Override
    public List<TransactionMessageDto> queryWaitingMessages(int page, int size) {
        return Lists.newArrayList();
    }

    @Override
    public List<TransactionMessageDto> queryMessages(int status, int page, int size) {
        return Lists.newArrayList();
    }
}
