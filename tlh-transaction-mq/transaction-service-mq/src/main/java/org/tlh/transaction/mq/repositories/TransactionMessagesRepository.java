package org.tlh.transaction.mq.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tlh.transaction.mq.entity.TransactionMessage;

import java.util.Optional;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Repository
public interface TransactionMessagesRepository extends JpaRepository<TransactionMessage,Long> {

    @Modifying
    @Transactional
    @Query("update TransactionMessage set status=?2 where id=?1")
    Integer updateMessageStatus(Long messageId, int status);

    Page<TransactionMessage> findTransactionMessagesByStatus(int status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update TransactionMessage set status=1,reSendCount=0 where status=2")
    Optional<Integer> cleanDiedMessageStatus();
}
