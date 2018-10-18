package org.tlh.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/18
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@RestController
@RequestMapping("/sms")
public class SmsController {

    @GetMapping("/send")
    public String sendVerifyCode(){
        log.info("send");
        return "success";
    }

}
