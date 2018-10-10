package org.tlh.transaction.mq.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.entity.TransactionMessages;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Repository
public interface TransactionMessagesRepository extends JpaRepository<TransactionMessages,Long> {

    @Modifying
    @Transactional
    @Query("update TransactionMessages set status=?2 where id=?1")
    Integer updateMessageStatus(Long messageId, int code);
}
