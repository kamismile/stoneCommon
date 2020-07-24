package com.github.kamismile.stone.commmon.util;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: lidong Date: 12-3-8 Time: 下午12:56 To change this template use File | Settings | File
 * Templates.
 */
public class DateUtil extends org.apache.commons.lang3.time.DateUtils {
    public static final String DATE_DF = "yyyy-MM-dd";
    public static final String TIMESTAMP_DF = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_HH = "HH";
    public static final String TIMESTAMP_DF1 = "yyyy-MM-dd'T'HH:mm:ssZ";// Facebook返回数据日期格式

    /**
     * @param strDate   待解析的字符串
     * @param strFomate 格式 数值为空时 ,使用默认的日期格式{@link #TIMESTAMP_DF}解析
     * @return Date对象
     */
    public static Date parseDate(String strDate, String strFomate) {
        if (strFomate == null)
            strFomate = TIMESTAMP_DF;
        SimpleDateFormat df = new SimpleDateFormat(strFomate);
        return df.parse(strDate, new ParsePosition(0));
    }

    public static String formatDate(Date strDate, String strFomate) {
        if (strFomate == null)
            strFomate = TIMESTAMP_DF;
        if (strDate == null)
            return null;
        SimpleDateFormat df = new SimpleDateFormat(strFomate);
        return df.format(strDate);
    }

    public static String formatDate(Date strDate) {
        return formatDate(strDate, DATE_DF);
    }

    /**
     * 是否为空如果不等于空并且是数值型是转换为Date型
     *
     * @param o 值
     * @return 返回空或转换后的值
     */
    public static Date isDateNull(Object o) {
        if (o != null && !"".equals(o.toString())) {
            Object obj = DateUtil.parseDate(o.toString(), TIMESTAMP_DF);
            if (obj == null) {
                obj = DateUtil.parseDate(o.toString(), DateUtil.DATE_DF);
            }
            return (Date) obj;
        }
        return null;
    }


    public static String getYesterday(Date date, String strFomate) {
        if (date == null) {
            date = new Date();
        }
        if (strFomate == null) {
            strFomate = DATE_DF;
        }
        Date yesterday = addDays(date, -1);
        return formatDate(yesterday, strFomate);
    }

    public static String getToday() {
        return formatDate(new Date(), DATE_DF);
    }


    public static Date getDateBeforeOrAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }


    public static Date makeZeroDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static String dateTimeTOISODateTime(String date,String strFomate) {
        if (ValueUtils.isNull(date)) {
            return null;
        }
        return ISO8601Utils.format(parseDate(date, strFomate));
    }

    /**
     *
     * @param date 日期
     * @param month 0当前一个月
     * @param day 0前一个月的最后一天
     * @return 日期
     */
    public static Date getMonthDay(Date date,int month,int day){
        Calendar cale = toCalendar(date);
        cale.add(Calendar.MONTH, month);
        cale.set(Calendar.DAY_OF_MONTH, day);
        return cale.getTime();
    }




}
