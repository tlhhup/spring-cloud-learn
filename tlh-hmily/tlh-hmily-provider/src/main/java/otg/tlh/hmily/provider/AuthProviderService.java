package otg.tlh.hmily.provider;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import otg.tlh.hmily.dto.AuthUserDto;
import otg.tlh.hmily.dto.AuthUserRepDto;
import otg.tlh.hmily.service.AuthUserService;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/31
 * <p>
 * Github: https://github.com/tlhhup
 */
@Component
public class AuthProviderService {

    @Autowired
    private AuthUserService authUserService;

    @Hmily(confirmMethod = "createUser", cancelMethod = "createUser")
    public AuthUserRepDto createUser(AuthUserDto userDto) {
        return authUserService.createUser(userDto);
    }

}
