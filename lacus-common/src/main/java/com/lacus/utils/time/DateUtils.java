package com.lacus.utils.time;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
@Slf4j
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public static Long dateString2TimeStamp(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = format.parse(timeStamp);
            return parse.getTime();
        } catch (ParseException e) {
            log.error("转换错误：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static Date getDate(Long mills){
        return new Date(mills);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String dateTime(final String format, final Date date) {
        return parseDateToStr(format, date);
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 将数字转换成时分秒，
     * 例如： 5261 -> 5秒261毫秒， 3602118 -> 1小时2秒118毫秒
     *
     * @param n 数字
     */
    public static String convertNumber2DateString(Long n) {
        String result = "";
        long hh = n / 3600000;
        long mm = (n - hh * 3600000) / 60000;
        long ss = (n - hh * 3600000 - mm * 60000) / 1000;
        long ms = (n - hh * 3600000 - mm * 60000 - ss * 1000);

        if (n > 0) {
            if (hh > 0) {
                result += hh + "小时";
            }
            if (mm > 0) {
                result += mm + "分";
            }
            if (ss > 0) {
                result += ss + "秒";
            }
            if (ms > 0) {
                result += ms + "毫秒";
            }
        } else {
            result = "NaN";
        }
        return result;
    }

    /**
     * 计算两个日期相差天数
     */
    public static int getDatePoorDay(Date endDate, Date startDate) {
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startDate.getTime();
        // 计算差多少天
        return (int) (diff / (1000 * 24 * 60 * 60));
    }

    /**
     * date to local datetime
     *
     * @param date date
     * @return local datetime
     */
    private static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * local datetime to date
     *
     * @param localDateTime local datetime
     * @return date
     */
    private static Date localDateTime2Date(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * get current date str
     *
     * @return date string
     */
    public static String getCurrentTime() {
        return getCurrentTime(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * get the date string in the specified format of the current time
     *
     * @param format date format
     * @return date string
     */
    public static String getCurrentTime(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * get the formatted date string
     *
     * @param date   date
     * @param format e.g. yyyy-MM-dd HH:mm:ss
     * @return date string
     */
    public static String format(Date date, String format) {
        return format(date2LocalDateTime(date), format);
    }

    /**
     * get the formatted date string
     *
     * @param localDateTime local data time
     * @param format        yyyy-MM-dd HH:mm:ss
     * @return date string
     */
    public static String format(LocalDateTime localDateTime, String format) {
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * convert time to yyyy-MM-dd HH:mm:ss format
     *
     * @param date date
     * @return date string
     */
    public static String dateToString(Date date) {
        return format(date, YYYY_MM_DD_HH_MM_SS);
    }


    /**
     * convert string to date and time
     *
     * @param date   date
     * @param format format
     * @return date
     */
    public static Date parse(String date, String format) {
        try {
            LocalDateTime ldt = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
            return localDateTime2Date(ldt);
        } catch (Exception e) {
            log.error("error while parse date:" + date, e);
        }
        return null;
    }


    /**
     * convert date str to yyyy-MM-dd HH:mm:ss format
     *
     * @param str date string
     * @return yyyy-MM-dd HH:mm:ss format
     */
    public static Date stringToDate(String str) {
        return parse(str, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * get seconds between two dates
     *
     * @param d1 date1
     * @param d2 date2
     * @return differ seconds
     */
    public static long differSec(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return 0;
        }
        return (long) Math.ceil(differMs(d1, d2) / 1000.0);
    }

    /**
     * get ms between two dates
     *
     * @param d1 date1
     * @param d2 date2
     * @return differ ms
     */
    public static long differMs(Date d1, Date d2) {
        return Math.abs(d1.getTime() - d2.getTime());
    }


    /**
     * get hours between two dates
     *
     * @param d1 date1
     * @param d2 date2
     * @return differ hours
     */
    public static long diffHours(Date d1, Date d2) {
        return (long) Math.ceil(diffMin(d1, d2) / 60.0);
    }

    /**
     * get minutes between two dates
     *
     * @param d1 date1
     * @param d2 date2
     * @return differ minutes
     */
    public static long diffMin(Date d1, Date d2) {
        return (long) Math.ceil(differSec(d1, d2) / 60.0);
    }


    /**
     * get the date of the specified date in the days before and after
     *
     * @param date date
     * @param day  day
     * @return the date of the specified date in the days before and after
     */
    public static Date getSomeDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * get the hour of day.
     *
     * @param date date
     * @return hour of day
     */
    public static int getHourIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * compare two dates
     *
     * @param future future date
     * @param old    old date
     * @return true if future time greater than old time
     */
    public static boolean compare(Date future, Date old) {
        return future.getTime() > old.getTime();
    }

    /**
     * convert schedule string to date
     *
     * @param schedule schedule
     * @return convert schedule string to date
     */
    public static Date getScheduleDate(String schedule) {
        return stringToDate(schedule);
    }

    /**
     * format time to readable
     *
     * @param ms ms
     * @return format time
     */
    public static String format2Readable(long ms) {

        long days = MILLISECONDS.toDays(ms);
        long hours = MILLISECONDS.toDurationHours(ms);
        long minutes = MILLISECONDS.toDurationMinutes(ms);
        long seconds = MILLISECONDS.toDurationSeconds(ms);

        return String.format("%02d %02d:%02d:%02d", days, hours, minutes, seconds);

    }

    /**
     * format time to duration
     *
     * @param d1 d1
     * @param d2 d2
     * @return format time
     */
    public static String format2Duration(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        return format2Duration(differMs(d1, d2));
    }

    /**
     * format time to duration
     *
     * @param ms ms
     * @return format time
     */
    public static String format2Duration(long ms) {

        long days = MILLISECONDS.toDays(ms);
        long hours = MILLISECONDS.toDurationHours(ms);
        long minutes = MILLISECONDS.toDurationMinutes(ms);
        long seconds = MILLISECONDS.toDurationSeconds(ms);

        StringBuilder strBuilder = new StringBuilder();
        if (days > 0) {
            strBuilder.append(days).append("d").append(" ");
        }
        if (hours > 0) {
            strBuilder.append(hours).append("h").append(" ");
        }
        if (minutes > 0) {
            strBuilder.append(minutes).append("m").append(" ");
        }
        if (seconds > 0) {
            strBuilder.append(seconds).append("s");
        }
        return strBuilder.toString();

    }

    /**
     * get monday
     * <p>
     * note: Set the first day of the week to Monday, the default is Sunday
     *
     * @param date date
     * @return get monday
     */
    public static Date getMonday(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        return cal.getTime();
    }

    /**
     * get sunday
     * <p>
     * note: Set the first day of the week to Monday, the default is Sunday
     *
     * @param date date
     * @return get sunday
     */
    public static Date getSunday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return cal.getTime();
    }

    /**
     * get first day of month
     *
     * @param date date
     * @return first day of month
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    /**
     * get some hour of day
     *
     * @param date       date
     * @param offsetHour hours
     * @return some hour of day
     */
    public static Date getSomeHourOfDay(Date date, int offsetHour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + offsetHour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * get last day of month
     *
     * @param date date
     * @return get last day of month
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 00:00:00
     *
     * @param inputDay date
     * @return start day
     */
    public static Date getStartOfDay(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 23:59:59
     *
     * @param inputDay day
     * @return end of day
     */
    public static Date getEndOfDay(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 00:00:00
     *
     * @param inputDay day
     * @return start of hour
     */
    public static Date getStartOfHour(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 23:59:59
     *
     * @param inputDay day
     * @return end of hour
     */
    public static Date getEndOfHour(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * get current date
     *
     * @return current date
     */
    public static Date getCurrentDate() {
        return DateUtils.parse(DateUtils.getCurrentTime(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * get date
     *
     * @param date          date
     * @param calendarField calendarField
     * @param amount        amount
     * @return date
     */
    public static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * get current time stamp : yyyyMMddHHmmssSSS
     *
     * @return date string
     */
    public static String getCurrentTimeStamp() {
        return getCurrentTime(YYYYMMDDHHMMSSSSS);
    }

    public static Long getTimeStamp(final String format, Long timestamp) {
        String dateString = dateTime(format, new Timestamp(timestamp * 1000));
        Date fullDate = dateTime(format, dateString);
        String realDateString = dateTime(YYYY_MM_DD_HH_MM_SS, fullDate);
        return Timestamp.valueOf(realDateString).getTime() / 1000;
    }


    static final long C0 = 1L;
    static final long C1 = C0 * 1000L;
    static final long C2 = C1 * 1000L;
    static final long C3 = C2 * 1000L;
    static final long C4 = C3 * 60L;
    static final long C5 = C4 * 60L;
    static final long C6 = C5 * 24L;

    public static String getDatetimeStr(Date date){
        return DateFormatUtils.format(date,YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * Time unit representing one thousandth of a second
     */
    public static class MILLISECONDS {

        public static long toSeconds(long d) {
            return d / (C3 / C2);
        }

        public static long toMinutes(long d) {
            return d / (C4 / C2);
        }

        public static long toHours(long d) {
            return d / (C5 / C2);
        }

        public static long toDays(long d) {
            return d / (C6 / C2);
        }

        public static long toDurationSeconds(long d) {
            return (d % (C4 / C2)) / (C3 / C2);
        }

        public static long toDurationMinutes(long d) {
            return (d % (C5 / C2)) / (C4 / C2);
        }

        public static long toDurationHours(long d) {
            return (d % (C6 / C2)) / (C5 / C2);
        }

    }


    public static String timeStrBeforeNow(int hours) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd");
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime localDate = timeNow.minusHours(hours);
        return localDate.format(fmt);
    }

    public static String dateStrBeforeNow(int months) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate = LocalDate.now();
        LocalDate localDate = endDate.minusMonths(months).plusDays(1);
        return localDate.format(fmt);
    }

    /**
     * 时间戳转日期时间
     */
    public static String stampToDateTime(Long timeStamp, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(timeStamp);
        return dateFormat.format(date);
    }
}
