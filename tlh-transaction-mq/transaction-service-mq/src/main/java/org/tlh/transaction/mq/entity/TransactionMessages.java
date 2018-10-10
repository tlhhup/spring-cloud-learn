package org.tlh.transaction.mq.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Data
@Table(name = "transaction_messages")
public class TransactionMessages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
