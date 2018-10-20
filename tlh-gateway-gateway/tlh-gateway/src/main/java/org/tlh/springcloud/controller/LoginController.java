package org.tlh.springcloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/20
 * <p>
 * Github: https://github.com/tlhhup
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

}
