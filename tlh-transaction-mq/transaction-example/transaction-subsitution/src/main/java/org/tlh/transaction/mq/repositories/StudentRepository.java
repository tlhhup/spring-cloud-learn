package org.tlh.transaction.mq.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tlh.transaction.mq.entity.Student;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@Repository
public interface StudentRepository extends JpaRepository<Student,Integer> {
}
