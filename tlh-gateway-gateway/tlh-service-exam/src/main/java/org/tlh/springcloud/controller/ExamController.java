package org.tlh.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 离歌笑tlh/hu ping on 18/10/20
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@RestController
@RequestMapping("/exam")
public class ExamController {

    @GetMapping("/start")
    public String start(){
        log.info(this.getClass().getSimpleName()+"--->start");
        return "start exam";
    }

}
