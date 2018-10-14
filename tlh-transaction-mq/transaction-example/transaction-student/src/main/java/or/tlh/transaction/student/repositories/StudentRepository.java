package or.tlh.transaction.student.repositories;

import or.tlh.transaction.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@Repository
public interface StudentRepository extends JpaRepository<Student,Integer> {
}
