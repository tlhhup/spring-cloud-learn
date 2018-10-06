package org.tlh.springcloud.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Data
@ApiModel("用户注册")
public class UserRegDto extends BaseUser{

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("地址")
    private String address;

}
