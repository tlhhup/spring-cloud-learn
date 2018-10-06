package org.tlh.springcloud.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Data
@ApiModel("token校验")
public class VerifyTokenDto implements Serializable {

    @NotNull
    @ApiModelProperty("token")
    private String token;

}
