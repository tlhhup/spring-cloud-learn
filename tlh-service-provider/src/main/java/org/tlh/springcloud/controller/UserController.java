package org.tlh.springcloud.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author huping
 * @desc
 * @date 18/10/2
 */
@RestController
@RequestMapping("/UserController")
public class UserController {

    @PostMapping("/add")
    public String add(){
        return "add";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        return "delete";
    }

}
