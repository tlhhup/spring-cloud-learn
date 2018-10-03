package org.tlh.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @HystrixCommand(fallbackMethod = "registerFail")
    public String register(){
        return this.restTemplate.postForEntity("http://user-service/UserController/add",null,String.class).getBody();
    }

    public String registerFail(){
        return "fail";
    }

    @DeleteMapping("/delete/{id}")
    @HystrixCollapser(batchMethod = "batchDelete",collapserProperties = {
            @HystrixProperty(name="timerDelayInMilliseconds", value = "100")
    })
    public String delete(@PathVariable("id")Integer id){
        this.restTemplate.delete("http://user-service/UserController/delete/{id}",id);
        return null;
    }

    @HystrixCommand(fallbackMethod = "deleteFail")
    public List<String> batchDelete(List<Integer> ids){
        System.out.println("batchDelete");
        Map<String,List<Integer>> param=new HashMap<>();
        param.put("ids",ids);
        this.restTemplate.delete("http://user-service/UserController/delete",param);
        return Arrays.asList("user");
    }

    public List<String> deleteFail(List<Integer> ids){
        System.out.println("deleteFail");
        return Arrays.asList("user");
    }

}
