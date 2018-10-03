package org.tlh.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.springcloud.feign.UserFeignClient;

/**
 * @author huping
 * @desc
 * @date 18/10/3
 */
@RestController
@RequestMapping("/user")
public class UserAuthController {

    @Autowired
    private UserFeignClient userFeignClient;

    @PostMapping("/register")
    public String register(){
        return this.userFeignClient.register();
    }

}
