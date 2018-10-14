package org.tlh.transaction.mq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.entity.Student;
import org.tlh.transaction.mq.repositories.StudentRepository;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public boolean saveStudent(Student student){
        try {
            this.studentRepository.save(student);
            return true;
        } catch (Exception e) {
            log.error("save student error",e);
        }
        return false;
    }

}
