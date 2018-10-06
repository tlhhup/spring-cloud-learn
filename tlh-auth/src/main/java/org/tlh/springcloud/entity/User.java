package org.tlh.springcloud.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Entity
@Data
@Table(name = "tlh_users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;

    private String password;

    private Integer age;

    private String realName;

    private String address;

}
