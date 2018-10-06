package org.tlh.springcloud.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.tlh.springcloud.config.LocalMessageResource;
import org.tlh.springcloud.dto.UserAuthReqDto;
import org.tlh.springcloud.dto.UserRegDto;
import org.tlh.springcloud.entity.User;
import org.tlh.springcloud.exceptions.TokenSignException;
import org.tlh.springcloud.reposities.UserRepository;
import org.tlh.springcloud.utils.JWTUtil;
import org.tlh.springcloud.utils.JsonUtils;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalMessageResource localMessageResource;

    public String createToken(UserAuthReqDto userAuthReqDto) throws TokenSignException {
        String userName = userAuthReqDto.getUserName();
        String password = userAuthReqDto.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        User user = this.userRepository.findUserByUserNameAndPassword(userName, password);
        if(user!=null){
            user.setPassword(null);
            String token = JWTUtil.getInstance().signToken(JsonUtils.toJson(user));
            if(StringUtils.isEmpty(token)){
                throw new TokenSignException(this.localMessageResource.getMessage("user.token.create"));
            }
            return token;
        }
        return null;
    }

    public String verifyToken(String token) throws TokenSignException {
        String data = JWTUtil.getInstance().verifyToken(token);
        if(StringUtils.isEmpty(data)){
            throw new TokenSignException(this.localMessageResource.getMessage("user.token.verify"));
        }
        return data;
    }

    @Transactional
    public String createUser(UserRegDto userReg) throws TokenSignException{
        User user=new User();
        BeanUtils.copyProperties(userReg,user);
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user=this.userRepository.save(user);
        //处理持久态修改字段自动update
        User temp=new User();
        BeanUtils.copyProperties(user,temp);
        temp.setPassword(null);
        String token = JWTUtil.getInstance().signToken(JsonUtils.toJson(user));
        if(StringUtils.isEmpty(token)){
            throw new TokenSignException(this.localMessageResource.getMessage("user.token.create"));
        }
        return token;
    }
}
