package org.tlh.transaction.mq.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author huping
 * @desc
 * @date 18/10/11
 */
public class JsonUtil {

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
        }
        return null;
    }

    public static <T> T toBean(Class<T> clazz,String json){
        try {
            return mapper.readValue(json,clazz);
        } catch (IOException e) {
        }
        return null;
    }

}
