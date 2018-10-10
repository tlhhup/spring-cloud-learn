package org.tlh.transaction.mq.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tlh.transaction.mq.entity.TransactionMessages;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Repository
public interface TransactionMessagesRepository extends JpaRepository<TransactionMessages,Long> {
}
