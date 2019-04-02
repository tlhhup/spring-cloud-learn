package otg.tlh.hmily.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import otg.tlh.hmily.dto.AuthUserDto;
import otg.tlh.hmily.dto.AuthUserRepDto;
import otg.tlh.hmily.entity.AuthUser;
import otg.tlh.hmily.repositories.AuthUserRepository;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthUserService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Hmily(confirmMethod = "confirmCreateUser", cancelMethod = "cancelCreateUser")
    @Transactional
    public AuthUserRepDto createUser(AuthUserDto userDto) {
        try {
            AuthUser authUser = new AuthUser();
            BeanUtils.copyProperties(userDto, authUser);
            authUser = this.authUserRepository.save(authUser);

            AuthUserRepDto result = new AuthUserRepDto();
            BeanUtils.copyProperties(authUser, result);
            throw new RuntimeException("create user error");//模拟被动方出错，事务应该回滚
            //return result;
        } catch (BeansException e) {
            log.error("create user error", e);
        }
        throw new RuntimeException("create user error");
    }

    @Transactional
    public boolean confirmCreateUser(AuthUserDto userDto) {
        System.out.println("被动方 confirm");
        return this.authUserRepository.updateUserStatus(userDto.getUserName(), userDto.getType(), 1)>0;
    }

    @Transactional
    public boolean cancelCreateUser(AuthUserDto userDto) {
        System.out.println("被动方 cancel");
        return this.authUserRepository.deleteAuthUserByUserNameAndType(userDto.getUserName(), userDto.getType())>0;
    }

}
