package org.tlh.springcloud.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.tlh.springcloud.dto.ResponseDto;
import org.tlh.springcloud.exceptions.TokenSignException;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(TokenSignException.class)
    public ResponseDto handler(TokenSignException e){
        return new ResponseDto(400,null,e.getMessage());
    }

    // todo 校验异常的处理  响应处理

}
