package org.tlh.transaction.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author huping
 * @desc
 * @date 18/10/11
 */
@Data
public class TransactionMessageDto extends SendMessageReqDto {

    private Long id;

    @JsonProperty("send_time")
    private Date sendTime;

    @JsonProperty("died_date")
    private Date dieDate;

    private int status;

    @JsonProperty("consumption_date")
    private Date consumptionDate;

    @JsonProperty("consumption_system")
    private String consumptionSystem;

}
