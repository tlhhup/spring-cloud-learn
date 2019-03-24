package otg.tlh.hmily.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@Data
@Entity
@Table(name = "auth_user")
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;
    private String password;
    private Integer type;
    private Integer status = 0;

    @Column(updatable = false)
    private Date createTime = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

}
