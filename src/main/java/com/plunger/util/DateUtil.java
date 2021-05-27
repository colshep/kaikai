package com.plunger.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public final static String datePattern = "yyyy-MM-dd HH:mm:ss";
    public final static String datePattern10 = "yyyy-MM-dd";
    public final static SimpleDateFormat sf = new SimpleDateFormat(datePattern);
    public final static SimpleDateFormat sf10 = new SimpleDateFormat(datePattern10);
    public final static Calendar cal = Calendar.getInstance();

    /**
     * 取回系统当前时间 时间格式yyyy-MM-dd hh:mm:ss
     *
     * @return yyyy-MM-dd hh:mm:ss格式的时间字符串
     */
    public static String getNowTime() {
        return sf.format(new Date());
    }

    public static String format10(String date) {
        Date d = new Date();
        if (!StringUtils.isEmpty(date)) {
            try {
                d = sf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
                d = new Date();
            }
        }
        return sf10.format(d);
    }

    public static Date formatStrToDate(String dateStr) {
        try {
            if (dateStr.length() == datePattern.length()) {
                return sf.parse(dateStr);
            } else if (dateStr.length() == datePattern10.length()) {
                return sf10.parse(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String formatDateToStr(Date date) {
        return sf.format(date);
    }

    public static Date addDay(Date date, int offset) {
        cal.setTime(date);//设置起时间
        cal.add(Calendar.DATE, offset);
        return cal.getTime();
    }

    public static Date addMonth(Date date, int offset) {
        cal.setTime(date);//设置起时间
        cal.add(Calendar.MONTH, offset);
        return cal.getTime();
    }

    public static Date addYear(Date date, int offset) {
        cal.setTime(date);//设置起时间
        cal.add(Calendar.YEAR, offset);
        return cal.getTime();
    }

    public static String addDay(String dateStr, int offset) {
        Date date = formatStrToDate(dateStr);
        Date result = addDay(date, offset);
        return formatDateToStr(result);
    }

    public static String addMonth(String dateStr, int offset) {
        Date date = formatStrToDate(dateStr);
        Date result = addMonth(date, offset);
        return formatDateToStr(result);
    }

    public static String addYear(String dateStr, int offset) {
        Date date = formatStrToDate(dateStr);
        Date result = addYear(date, offset);
        return formatDateToStr(result);
    }
}
