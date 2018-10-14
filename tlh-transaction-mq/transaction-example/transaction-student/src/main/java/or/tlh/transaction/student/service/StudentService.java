package or.tlh.transaction.student.service;

import lombok.extern.slf4j.Slf4j;
import or.tlh.transaction.student.entity.Student;
import or.tlh.transaction.student.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.dto.MessageRepDto;
import org.tlh.transaction.mq.dto.SendMessageReqDto;
import org.tlh.transaction.mq.feign.TransactionMessageClient;
import org.tlh.transaction.mq.utils.DateUtil;
import org.tlh.transaction.mq.utils.JsonUtil;

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

    @Autowired
    private TransactionMessageClient transactionMessageClient;

    @Transactional
    public boolean createStudent(Student student){
        boolean success=false;
        //1.保存本地事务
        this.studentRepository.save(student);

        //2.发送到消息系统
        MessageRepDto messageRepDto = this.transactionMessageClient.sendMessage(buildMessage(student,"student.create"));
        if(messageRepDto!=null&&messageRepDto.getSuccess()){
            success=true;
        }else{
            log.error("send create student error");
            //spring默认在出现runtime和error时会进行事务的回滚
            throw new RuntimeException("消息发送失败");
        }
        return success;
    }

    private SendMessageReqDto buildMessage(Student student,String routingKey) {
        SendMessageReqDto result=new SendMessageReqDto();
        result.setSendSystem("tlh-student");
        result.setCreateTime(DateUtil.now());
        result.setContent(JsonUtil.toJson(student));
        result.setExchange("student");
        result.setRoutingKey(routingKey);
        result.setRetryCount(10);
        return result;
    }

}
