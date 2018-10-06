package org.tlh.springcloud.config;

import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.tlh.springcloud.dto.ResponseDto;
import org.tlh.springcloud.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
public class AuthInterceptor implements HandlerInterceptor {

    private RestTemplate restTemplate;

    public AuthInterceptor(RestTemplate restTemplate) {
        this.restTemplate=restTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            PrintWriter writer = response.getWriter();
            writer.write(JsonUtils.toJson(new ResponseDto<>(ResponseDto.NO_AUTH_CODE,null,"请求头缺少Authorization")));
            return false;
        }else{
            Map<String,String> data=new HashMap<>();
            data.put("token",authorization);
            Map<String,Object> map = restTemplate.postForObject("http://tlh-auth/userAuth/verify", data, Map.class);
            if(map==null){
                PrintWriter writer = response.getWriter();
                writer.write(JsonUtils.toJson(new ResponseDto<>(ResponseDto.NO_AUTH_CODE,null,"token过期")));
                return false;
            }
            String userInfo = map.get("data").toString();
            if(StringUtils.hasText(userInfo)){
                return true;
            }else{
                PrintWriter writer = response.getWriter();
                writer.write(JsonUtils.toJson(new ResponseDto<>(ResponseDto.NO_AUTH_CODE,null,"token验证失败")));
                return false;
            }
        }
    }
}
