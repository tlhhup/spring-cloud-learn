package org.tlh.transaction.mq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huping
 * @desc 投递到消息系统的数据结构
 * @date 18/10/14
 */
@Data
public class DeliveryMessageDto<T,ID> implements Serializable {

    private T rawData;
    private ID messageId;

}
