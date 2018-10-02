package org.tlh.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author huping
 * @desc
 * @date 18/10/2
 */
@RestController
@RequestMapping("/userAuth")
public class UserAuthController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/register")
    public String register(){
        return this.restTemplate.postForEntity("http://user-service/UserController/add",null,String.class).getBody();
    }

}
