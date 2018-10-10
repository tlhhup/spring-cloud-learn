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
public class SendMessageRepDto implements Serializable {

    private Boolean success;

    @JsonProperty("message_id")
    private Long messageId;

}
