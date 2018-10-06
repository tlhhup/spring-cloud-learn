package org.tlh.springcloud.reposities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tlh.springcloud.entity.User;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findUserByUserNameAndPassword(String userName,String password);

}
