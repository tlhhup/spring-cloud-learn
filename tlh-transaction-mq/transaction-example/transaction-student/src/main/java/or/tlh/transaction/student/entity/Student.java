package or.tlh.transaction.student.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@Data
@Entity
@Table(name = "tlh_students")
public class Student implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String address;

    private Integer age;

}
