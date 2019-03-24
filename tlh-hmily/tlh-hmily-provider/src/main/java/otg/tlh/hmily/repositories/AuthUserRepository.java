package otg.tlh.hmily.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import otg.tlh.hmily.entity.AuthUser;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
public interface AuthUserRepository extends JpaRepository<AuthUser,Integer> {

    @Modifying
    @Transactional
    @Query(value = "update AuthUser set status=?3 where userName=?1 and type=?2")
    int updateUserStatus(String userName,int type,int status);

    boolean deleteAuthUserByUserNameAndType(String userName,int type);

}
