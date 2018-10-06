package org.tlh.springcloud.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tlh.springcloud.dto.ResponseDto;
import org.tlh.springcloud.dto.UserAuthReqDto;
import org.tlh.springcloud.dto.VerifyTokenDto;
import org.tlh.springcloud.exceptions.TokenSignException;
import org.tlh.springcloud.service.UserService;

import javax.validation.Valid;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Api
@RestController
@RequestMapping("/userAuth")
public class UserAuthController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户认证",notes = "返回用户认证的token")
    @PostMapping("/token")
    public ResponseDto<String> token(@RequestBody @Valid UserAuthReqDto userAuthReqDto) throws TokenSignException {
        String token = this.userService.createToken(userAuthReqDto);
        return ResponseDto.success(token);
    }

    @PostMapping("/verify")
    @ApiOperation(value = "token校验")
    public ResponseDto<String> verify(@RequestBody @Valid VerifyTokenDto verifyToken) throws TokenSignException {
        String data = this.userService.verifyToken(verifyToken.getToken());
        return ResponseDto.success(data);
    }

}
