package or.tlh.transaction.student.controller;

import or.tlh.transaction.student.entity.Student;
import or.tlh.transaction.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/create")
    public boolean createStudent(@RequestBody Student student){
        return this.studentService.createStudent(student);
    }

}
