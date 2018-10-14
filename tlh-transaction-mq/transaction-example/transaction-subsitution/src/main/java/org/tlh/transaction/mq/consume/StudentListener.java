package org.tlh.transaction.mq.consume;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.tlh.transaction.mq.dto.DeliveryMessageDto;
import org.tlh.transaction.mq.entity.Student;
import org.tlh.transaction.mq.feign.TransactionMessageClient;
import org.tlh.transaction.mq.service.StudentService;
import org.tlh.transaction.mq.utils.DateUtil;
import org.tlh.transaction.mq.utils.JsonUtil;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@Slf4j
@Component
public class StudentListener {

    private static final String SYSTEM_NAME = "substitution";

    @Autowired
    private StudentService studentService;

    @Autowired
    private TransactionMessageClient transactionMessageClient;

    @RabbitListener(queues = "studentCreate")
    public void handlerStudentCreate(DeliveryMessageDto<String, Long> deliveryMessage) {
        if (deliveryMessage != null) {
            String rawData = deliveryMessage.getRawData();
            if (StringUtils.hasText(rawData)) {
                Student student = JsonUtil.toBean(Student.class, rawData);
                boolean success = this.studentService.saveStudent(student);
                if (success) {
                    log.info("confirm message consumed");
                    //确认消息被消费
                    this.transactionMessageClient.confirmMessageConsumed(deliveryMessage.getMessageId(),
                            SYSTEM_NAME, DateUtil.now());
                }
            }
        }
    }

}
