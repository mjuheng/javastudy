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

    /**
     * 去除 Date 对象的时分秒和毫秒部分，将其设置为零
     *
     * @param date 要处理的 Date 对象
     * @return 处理后的 Date 对象，不包含时分秒和毫秒
     */
    public static Date truncateTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 计算两个时间相差天数
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 相差天数，begin > endDate.正数，反之负数
     */
    public static int getDifferenceInDays(Date beginDate, Date endDate) {
        beginDate = truncateTime(beginDate);
        endDate = truncateTime(endDate);
        return (int) ((beginDate.getTime() - endDate.getTime()) / (24 * 60 * 60 * 1000));
    }
}
