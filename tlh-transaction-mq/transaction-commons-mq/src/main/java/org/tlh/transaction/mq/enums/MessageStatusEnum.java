package org.tlh.transaction.mq.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 消息状态枚举，必须按照以下顺序进行切换<br>
 *     0-1-2<br>
 *     0-1-3-1
 * @author huping
 * @desc
 * @date 18/10/10
 */
public enum MessageStatusEnum {

    /**
     * 等待确认,如果消息始终处于该状态，则为废消息
     */
    WAIT_CONFIRM(0, "wait_confirm"),

    /**
     * 确认发送,可以将消息投递到实时消息系统
     */
    CONFIRM_SEND(1,"confirm_send"),

    /**
     * 已经消费
     */
    CONSUMED(2, "consumed"),

    /**
     * 死亡，等待人工干预
     */
    DIE(3,"die");

    private int code;
    private String value;


    MessageStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static MessageStatusEnum getMessageStatusByCode(int code){
        Optional<MessageStatusEnum> messageStatusEnum = Arrays.stream(values()).filter(statusEnum -> statusEnum.code == code).findFirst();
        return messageStatusEnum.orElse(WAIT_CONFIRM);
    }
}
