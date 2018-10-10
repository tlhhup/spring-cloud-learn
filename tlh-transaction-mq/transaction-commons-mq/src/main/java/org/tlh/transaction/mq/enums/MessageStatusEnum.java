package org.tlh.transaction.mq.enums;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
public enum MessageStatusEnum {

    /**
     * 等待消费
     */
    WAIT_CONSUMPTION(0, "wait"),

    /**
     * 已经消费
     */
    CONSUMPTIONED(1, "consumptioned"),

    /**
     * 死亡
     */
    DIE(2,"die"),

    /**
     * 确认发送
     */
    CONFORM_SEND(3,"conform_send");

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
}
