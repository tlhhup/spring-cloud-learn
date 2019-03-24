package org.tlh.hmily.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.hmily.dto.AuthUserDto;
import org.tlh.hmily.dto.AuthUserRepDto;
import org.tlh.hmily.dto.TeacherDto;
import org.tlh.hmily.entity.Teacher;
import org.tlh.hmily.feign.AuthUserService;
import org.tlh.hmily.repositories.TeacherRepository;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class TeacherService {

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Hmily
    @Transactional
    public boolean addTeacher(TeacherDto teacherDto){
        try {
            //1.添加数据到认证中心
            AuthUserDto authUserDto=new AuthUserDto();
            authUserDto.setPassword(teacherDto.getPassword());
            authUserDto.setUserName(teacherDto.getUserName());
            authUserDto.setType(1);
            AuthUserRepDto user = this.authUserService.createUser(authUserDto);
            //2.保存数据到本地
            Teacher teacher=new Teacher();
            BeanUtils.copyProperties(teacherDto,teacher);
            teacher.setAuthId(user.getId());
            this.teacherRepository.save(teacher);
            return true;
        } catch (Exception e) {
            log.error("add teacher error",e);
        }
        return false;
    }

    public boolean confirmAddTeacher(TeacherDto teacherDto){
        return true;
    }

    @Transactional
    public boolean cancelAddTeacher(TeacherDto teacherDto){
        return this.teacherRepository.deleteTeacherByName(teacherDto.getName());
    }

}
