package org.tlh.hmily.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@Data
@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String address;
    private Integer age;
    private Integer authId;

}
