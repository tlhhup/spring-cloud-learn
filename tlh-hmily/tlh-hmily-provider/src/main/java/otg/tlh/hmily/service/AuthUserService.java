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
            return result;
        } catch (BeansException e) {
            log.error("create user error", e);
        }
        throw new RuntimeException("create user error");
    }

    @Transactional
    public boolean confirmCreateUser(AuthUserDto userDto) {
        return this.authUserRepository.updateUserStatus(userDto.getUserName(), userDto.getType(), 1)>0;
    }

    @Transactional
    public boolean cancelCreateUser(AuthUserDto userDto) {
        return this.authUserRepository.deleteAuthUserByUserNameAndType(userDto.getUserName(), userDto.getType());
    }

}
