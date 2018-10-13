package org.tlh.transaction.mq.utils;

import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * @author huping
 * @desc
 * @date 18/10/11
 */
public class DateUtil {

    public static final String ISO_DATETIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String ISO_DATETIME_TIME_ZONE_FORMAT="yyyy-MM-dd'T'HH:mm:ssZZ";

    public static final String ISO_DATE_FORMAT="yyyy-MM-dd";

    public static final String ISO_DATE_TIME_ZONE_FORMAT="yyyy-MM-ddZZ";

    public static final String ISO_TIME_FORMAT="'T'HH:mm:ss";

    public static final String ISO_TIME_TIME_ZONE_FORMAT="'T'HH:mm:ssZZ";

    public static final String ISO_TIME_NO_T_FORMAT="HH:mm:ss";

    public static final String ISO_TIME_NO_T_TIME_ZONE_FORMAT="HH:mm:ssZZ";

    public static final String SMTP_DATETIME_FORMAT="EEE, dd MMM yyyy HH:mm:ss Z";

    /**
     * 获取当前时间
     * @return
     */
    public static Date now(){
        return new Date();
    }

    /**
     * 格式化时间日期
     * @param dateTime
     * @return
     */
    public static String formatDateTime(Date dateTime){
        return DateFormatUtils.format(DateUtil.now(),ISO_DATETIME_FORMAT);
    }

}
