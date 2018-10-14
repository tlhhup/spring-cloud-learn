package org.tlh.transaction.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.transaction.mq.feign.TransactionMessageClient;
import org.tlh.transaction.mq.utils.DateUtil;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionClientTest {

    @Autowired
    private TransactionMessageClient transactionMessageClient;

    @Test
    public void confirmMessageDied(){
        this.transactionMessageClient.confirmMessageDied(1l, DateUtil.now());
    }

}
