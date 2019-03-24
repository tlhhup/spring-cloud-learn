package org.tlh.hmily.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@Data
public class AuthUserDto implements Serializable{

    private String userName;
    private String password;
    private Integer type;
}
