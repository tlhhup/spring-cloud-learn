package org.tlh.hmily.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.hmily.dto.TeacherDto;
import org.tlh.hmily.service.TeacherService;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@RestController
@RequestMapping(value = "/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping(value = "/add")
    public boolean addTeacher(@RequestBody TeacherDto teacherDto){
        return this.teacherService.addTeacher(teacherDto);
    }

}
