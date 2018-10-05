package org.tlh.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huping
 * @desc
 * @date 18/10/5
 */
@RestController
@RequestMapping("/config")
public class SccConfigClientController {

    @Value("${name}")
    private String name;

    @GetMapping("/name")
    public String name(){
        return name;
    }

}
