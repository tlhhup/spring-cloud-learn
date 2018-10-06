package org.tlh.springcloud.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author huping
 * @desc
 * @date 18/10/6
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper mapper;

    static {
        mapper=new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN,true);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED,true);
    }

    public static String toJson(Object data){
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("toJson",e);
        }
        return null;
    }

    public static <T> T toBean(Class<T> clazz,String json){
        try {
            return mapper.readValue(json,clazz);
        } catch (IOException e) {
            log.error("toBean",e);
        }
        return null;
    }

}
