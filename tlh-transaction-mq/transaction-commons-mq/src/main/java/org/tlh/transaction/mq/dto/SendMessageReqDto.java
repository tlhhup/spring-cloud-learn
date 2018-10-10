package org.tlh.transaction.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Data
public class SendMessageReqDto implements Serializable {

    private String content;

    @JsonProperty("routing_ke")
    private String routingKey;

    @JsonProperty("send_system")
    private String sendSystem;

    @JsonProperty("retry_count")
    private int retryCount;

}
