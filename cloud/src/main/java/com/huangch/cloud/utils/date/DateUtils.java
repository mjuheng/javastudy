package com.huangch.cloud.utils.date;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangch
 * @since 2023-12-21
 */
public class DateUtils extends LocalDateTimeUtil {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    public static LocalDate toLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), DEFAULT_ZONE_ID);
    }

    /**
     * 新增日期的天数
     *
     * @param date 时间
     * @param day 新增的天数
     * @return 处理后日期
     */
    public static Date plusDay(Date date, Integer day) {
        if (date == null || day == null) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, day);
        return cal.getTime();
    }

    public static Date getMonthLastDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
}
