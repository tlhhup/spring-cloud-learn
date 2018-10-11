package org.tlh.transaction.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Data
public class SendMessageReqDto implements Serializable {

    @NotEmpty
    private String content;

    @NotEmpty
    @JsonProperty("routing_key")
    private String routingKey;

    @NotEmpty
    @JsonProperty("send_system")
    private String sendSystem;

    @NotNull
    @JsonProperty("retry_count")
    private Integer retryCount;

    @NotNull
    @JsonProperty("create_time")
    private Date createTime;

}
