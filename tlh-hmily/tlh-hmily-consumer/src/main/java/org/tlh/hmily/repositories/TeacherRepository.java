package org.tlh.hmily.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tlh.hmily.entity.Teacher;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    int deleteTeacherByName(String name);

}
