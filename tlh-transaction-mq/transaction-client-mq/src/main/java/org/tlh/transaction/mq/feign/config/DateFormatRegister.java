package org.tlh.transaction.mq.feign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;

/**
 * @author huping
 * @desc
 * @date 18/10/14
 */
@Component
public class DateFormatRegister implements FeignFormatterRegistrar {

    @Value("${spring.mvc.date-format}")
    private String pattern;

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter(this.pattern));
    }
}
