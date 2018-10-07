package org.tlh.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.springcloud.config.properties.NestingProperty;

/**
 * @author huping
 * @desc
 * @date 18/10/7
 */
@RestController
@RequestMapping("/Nesting")
public class NestingPropertyController {

    @Autowired
    private NestingProperty nestingProperty;

    @GetMapping("/all")
    public NestingProperty all() {
        return nestingProperty;
    }
}
