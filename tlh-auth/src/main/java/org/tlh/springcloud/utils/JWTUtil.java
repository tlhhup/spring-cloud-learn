package org.tlh.springcloud.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.tlh.springcloud.exceptions.TokenSignException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Slf4j
public class JWTUtil {

    private static volatile JWTUtil instance;
    private static Algorithm algorithm;

    private JWTUtil(){
        String secret="123456";
        algorithm = Algorithm.HMAC256(secret);
    }

    public static JWTUtil getInstance() {
        if(instance==null){
            synchronized (JWTUtil.class){
                if (instance==null){
                    instance=new JWTUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 生成token
     * @param rawJson
     * @return
     * @throws TokenSignException
     */
    public String signToken(String rawJson) throws TokenSignException {
        try {
            String token = JWT.create().withIssuer("auth0")//
                                    .withSubject(rawJson)//
                                    .withExpiresAt(DateUtils.addDays(new Date(),1))//
                                    .sign(algorithm);
            return token;
        } catch (IllegalArgumentException e) {
            log.error("jwt",e);
            throw new TokenSignException("参数错误");
        } catch (JWTCreationException e) {
            log.error("jwt",e);
            throw new TokenSignException("生成Token失败");
        }
    }

    /**
     * 校验token
     * @param token
     * @return
     * @throws TokenSignException
     */
    public String verifyToken(String token) throws TokenSignException {
        try {
            JWTVerifier build = JWT.require(algorithm).withIssuer("auth0").build();
            DecodedJWT verify = build.verify(token);
            return verify.getSubject();
        }catch (TokenExpiredException e){
            log.error("jwt",e);
            throw new TokenSignException("token过期");
        }catch (JWTVerificationException e) {
            log.error("jwt",e);
            throw new TokenSignException("校验token失败");
        }
    }

    public static void main(String[] args) throws JsonProcessingException, TokenSignException {
        Map<String,Object> data=new HashMap<>();
        data.put("userName","tlh");
        data.put("age","10");
        data.put("id",1);
        String json = JsonUtils.toJson(data);
        System.out.println(json);
        String token = JWTUtil.getInstance().signToken(json);
        System.out.println(token);
        System.out.println("**********");
        System.out.println(JWTUtil.getInstance().verifyToken(token));
    }


}
