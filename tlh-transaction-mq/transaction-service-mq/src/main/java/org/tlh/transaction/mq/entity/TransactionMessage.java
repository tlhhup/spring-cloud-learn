package org.tlh.transaction.mq.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huping
 * @desc
 * @date 18/10/10
 */
@Data
@Entity
@Table(name = "transaction_messages")
public class TransactionMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*********** 主动方 ************/
    private String message;// 消息内容,json数据，业务数据

    private String exchange;//交换器

    private String routingKey;//路由key

    private String sendSystem;//发送消息的系统，主动方

    private int dieCount;//死亡次数条件，由主动方觉得，超过后消息变为死亡状态，需人工干预来重新投递

    private Date createTime;//创建时间

    /************* task ***************/
    private int reSendCount;//重复发送次数

    private Date sendTime;//最新发送时间

    private Date dieDate;//死亡时间

    // init
    private int status;//消息状态

    /********** 被动方更新 ****************/
    private Date consumptionDate;//消费时间

    private String consumptionSystem;//消费消息的系统，被动方


}
