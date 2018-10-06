package org.tlh.springcloud.dto;

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
public class BaseUser implements Serializable {

    @NotNull
    @ApiModelProperty("用户名")
    private String userName;

    @NotNull
    @ApiModelProperty("密码")
    private String password;

}
