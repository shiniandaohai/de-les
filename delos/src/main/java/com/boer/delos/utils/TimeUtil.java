package com.boer.delos.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.lidroid.xutils.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间工具
 */
public class TimeUtil {
    public final static String FORMAT_DATE = "yyyy-MM-dd";
    public final static String FORMAT_DATE2 = "yyyyMMdd";
    public final static String FORMAT_TIME = "hh:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 HH:mm";
    public final static String FORMAT_PICTURE = "yyyyMMdd_hhmm_ss";
    public final static String FORMAT_YEAR_MONTH_DAY = "yyyy年MM月dd日";
    public final static String FORMAT_Y_M_D_H_M_2 = "yyyy年MM月dd日 HH:mm";
    public final static String FORMAT_DATE_TIME2 = "yyyy-MM-dd HH:mm:ss +0000";
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat sdf = new SimpleDateFormat();
    private static final int YEAR = 365 * 24 * 60 * 60;// 年
    private static final int MONTH = 30 * 24 * 60 * 60;// 月
    private static final int DAY = 24 * 60 * 60;// 天
    private static final int HOUR = 60 * 60;// 小时
    private static final int MINUTE = 60;// 分钟

    private static final int THREEDAYS = DAY * 3;

    /**
     * 根据时间戳获取描述性时间，如3分钟前，1天前
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 时间字符串
     */
    public static String getDescriptionTimeFromTimestamp(long timestamp) {
        try {
            long currentTime = System.currentTimeMillis();
            long timeGap = (currentTime - timestamp * 1000) / 1000;// 与现在时间相差秒数
            String timeStr = null;
            if (timeGap > YEAR) {// 去年及以前：显示x年X月X日 X:X
                // timeStr = timeGap / YEAR + "年前";
                timeStr = formatStamp2Time(timestamp, FORMAT_DATE_TIME);
            } else if (timeGap > THREEDAYS && timeGap < YEAR) {// 3天以上显示X月X日 X:X
                // timeStr = timeGap
                // / MONTH + "个月前";
                timeStr = formatStamp2Time(timestamp, FORMAT_MONTH_DAY_TIME);
            } else if (timeGap > DAY && timeGap < THREEDAYS) {// 1天以上3天以下
                timeStr = timeGap / DAY + "天前";
            } else if (timeGap > HOUR) {// 1小时-24小时
                timeStr = timeGap / HOUR + "小时前";
            } else if (timeGap > MINUTE) {// 1分钟-59分钟
                timeStr = timeGap / MINUTE + "分钟前";
            } else {// 1秒钟-59秒钟
                timeStr = "刚刚";
            }
            return timeStr;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据时间戳获取指定格式的时间，如2011-11-30 08:40
     *
     * @param timestamp 时间戳 单位为秒
     * @param format    指定格式 如果为null或空串则使用默认格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String formatStamp2Time(long timestamp, String format) {
        if (String.valueOf(timestamp).length() > 10) {
            timestamp /= 1000;
        }
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int year = Integer.valueOf(sdf.format(new Date(timestamp * 1000)).substring(0, 4));
            if (currentYear == year) {// 如果为今年则不显示年份
                sdf.applyPattern(FORMAT_MONTH_DAY_TIME);
            } else {
                sdf.applyPattern(FORMAT_DATE_TIME);
            }
        } else {
            sdf.applyPattern(format);
        }
        Date date = new Date(timestamp * 1000);
        return sdf.format(date);
    }

    /**
     * 根据时间戳获取时间字符串，并根据指定的时间分割数partionSeconds来自动判断返回描述性时间还是指定格式的时间
     *
     * @param timestamp      时间戳 单位是秒
     * @param partionSeconds 时间分割线，当现在时间与指定的时间戳的秒数差大于这个分割线时则返回指定格式时间，否则返回描述性时间
     * @param format
     * @return
     */
    public static String getMixTimeFromTimestamp(long timestamp, long partionSeconds, String format) {
        long currentTime = System.currentTimeMillis() / 1000;
        long timeGap = (currentTime - timestamp);// 与现在时间相差秒数
        if (timeGap <= partionSeconds) {
            return getDescriptionTimeFromTimestamp(timestamp);
        } else {
            return formatStamp2Time(timestamp, format);
        }
    }

    /**
     * 获取当前日期的指定格式的字符串
     *
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getCurrentTime(String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(new Date());
    }

    /**
     * 将日期字符串以指定格式转换为Date
     *
     * @param timeStr 日期字符串
     * @param format  指定的日期格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static Date getTimeFromString(String timeStr, String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        try {
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 获取指定时间的毫秒值
     *
     * @param date 指定时间
     * @return 指定时间的毫秒值
     */
    private static Long getTimeMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定时间的时间戳
     *
     * @param date 指定时间
     * @return 指定时间的时间戳
     */
    public static long getTargetTimeStamp(Date date) {
        return getTimeMillis(date) / 1000;
    }

    public static long getCurrentstamp() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    /**
     * 获取一天中每半个小时的时间点，从0:00开始
     *
     * @param count 时间点的个数
     * @return 时间点集合
     */
    public static List<String> getHalfHourList(int count) {
        List<String> timeList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i % 2 == 1) {
                timeList.add(i / 2 + ":30");
            } else {
                timeList.add(i / 2 + ":00");
            }
        }
        return timeList;
    }

    /**
     * 将Date以指定格式转换为日期时间字符串
     *
     * @param time   日期
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getStringFromTime(Date time, String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(time);
    }

    //获得当天0点时间
    public static int getTimesmorning(int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.roll(Calendar.DAY_OF_YEAR, day);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    //获得当天24点时间
    public static int getTimesnight(int day) {
        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, day);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    //获得本月第一天0点时间
    public static int getTimesMonthmorning(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.roll(Calendar.MONTH, month);

        return (int) (cal.getTimeInMillis() / 1000);
    }

    //获得本月最后一天24点时间
    public static int getTimesMonthnight(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.roll(Calendar.MONTH, month);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    public static int getTimesYearstart(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        cal.roll(Calendar.YEAR, year);
        cal.roll(Calendar.DAY_OF_YEAR, 1);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    public static int getTimesYearLast(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.roll(Calendar.YEAR, year);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    /**
     * 获取目标时间的时间戳
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @param sec
     * @return
     */
    public static long getTargetTimeStamp(int year, int month, int day, int hour, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, min, sec);
        return cal.getTimeInMillis() / 1000;
    }

    public static int getTargetTimeStamp2(String year, String month, String day, String hour,
                                          String min, String sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(year), Integer.valueOf(month) - 1,
                Integer.valueOf(day), Integer.valueOf(hour),
                Integer.valueOf(min), Integer.valueOf(sec));
        return (int) (cal.getTimeInMillis() / 1000);
    }

    /**
     * 获取目标时间前后的天
     *
     * @param d
     * @param day
     * @return
     */
    public static long getDateBeforeOrAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime().getTime();
    }


    /**
     * 判断选择的日期是否是本周
     *
     * @param time
     * @return
     */
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    /**
     * 判断选择的日期是否是今天
     */
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    /**
     * 判断选择的日期是否是本月
     */
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    public static boolean isThisYear(long time) {
        return isThisTime(time, "yyyy");
    }


    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }


    public static int getDaysByYearMonth1(int year, int _month) {
        int tempMonth=_month-1;
        int maxDate;
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        int month=a.get(Calendar.MONTH);
        if(tempMonth<month){
            a.set(Calendar.MONTH, tempMonth);
            maxDate=a.getActualMaximum(Calendar.DATE);
        }
        else if(month==tempMonth){
            maxDate=a.get(Calendar.DATE);
        }
        else{
            maxDate=0;
        }
        Log.d("getDaysByYearMonth1","maxDate="+maxDate);
        return maxDate;
    }

    public static long getTargetTimeStamp1(int year, int month, int day, int hour, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, min, sec);
        return cal.getTimeInMillis();
    }

}
