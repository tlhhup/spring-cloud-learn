package org.tlh.springcloud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Data
@NoArgsConstructor
public class ResponseDto<T> {

    private Integer code;
    private T data;
    private String message;

    public ResponseDto(Integer code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static ResponseDto getInstance(){
        return new ResponseDto();
    }

    public static ResponseDto success(){
        return new ResponseDto(200,null,null);
    }

    public static ResponseDto success(String data){
        return new ResponseDto(200,data,null);
    }

}
