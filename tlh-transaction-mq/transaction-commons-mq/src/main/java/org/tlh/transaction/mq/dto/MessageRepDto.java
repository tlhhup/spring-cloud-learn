package org.tlh.transaction.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.tracing.dtrace.ArgsAttributes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRepDto implements Serializable {

    private Boolean success;

    @JsonProperty("message_id")
    private Long messageId;

}
