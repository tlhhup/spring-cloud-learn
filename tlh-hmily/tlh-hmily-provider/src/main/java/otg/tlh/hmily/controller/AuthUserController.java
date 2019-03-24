package otg.tlh.hmily.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import otg.tlh.hmily.dto.AuthUserDto;
import otg.tlh.hmily.dto.AuthUserRepDto;
import otg.tlh.hmily.service.AuthUserService;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@RestController
@RequestMapping("/user")
public class AuthUserController {

    @Autowired
    private AuthUserService authUserService;

    @PostMapping(value = "/createUser")
    public AuthUserRepDto createUser(@RequestBody AuthUserDto authUserDto){
        return this.authUserService.createUser(authUserDto);
    }


}
