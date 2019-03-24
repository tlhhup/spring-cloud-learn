package org.tlh.hmily.feign;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.tlh.hmily.dto.AuthUserDto;
import org.tlh.hmily.dto.AuthUserRepDto;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@FeignClient(name = "tlh-hmily-provider",path = "/user")
public interface AuthUserService {

    @Hmily
    @PostMapping(value = "/createUser")
    AuthUserRepDto createUser(@RequestBody AuthUserDto authUserDto);

}
