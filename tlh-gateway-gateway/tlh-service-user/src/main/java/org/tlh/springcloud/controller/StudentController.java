package org.tlh.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/20
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController {

    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName, @RequestParam("password") String password){
        log.info(this.getClass().getSimpleName()+"--->login");
        return "login";
    }

}
