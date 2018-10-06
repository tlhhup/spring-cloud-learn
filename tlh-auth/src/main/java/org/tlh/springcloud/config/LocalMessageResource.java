package org.tlh.springcloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Component
public class LocalMessageResource {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String code){
        return this.messageSource.getMessage(code,null, Locale.getDefault());
    }

    public String getMessage(String code, Object[] args){
        return this.messageSource.getMessage(code,args, Locale.getDefault());
    }

    public String getMessage(String code,String defaultValue){
        return this.messageSource.getMessage(code,null,defaultValue,Locale.getDefault());
    }

    public String getMessage(String code,Object[] args,String defaultValue){
        return this.messageSource.getMessage(code,args,defaultValue,Locale.getDefault());
    }

}
