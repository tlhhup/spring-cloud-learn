package org.tlh.springcloud.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tlh.springcloud.dto.UserRegDto;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void createToken() throws Exception {
    }

    @Test
    public void verifyToken() throws Exception {
    }

    @Test
    public void createUser() throws Exception {
        UserRegDto userRegDto=new UserRegDto();
        userRegDto.setUserName("tlh");
        userRegDto.setPassword("123456");

        this.userService.createUser(userRegDto);
    }

}