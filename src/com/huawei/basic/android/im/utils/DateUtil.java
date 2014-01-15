/*
 * 文件名: DateUtil.java
 * 版    权：Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 根据时间格式生成时间字符串
 * 创建人: 周雪松
 * 创建时间:2012-2-23
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 根据时间格式，获取时间字符串
 * 
 * @author 周雪松
 * @version [RCS Client V100R001C03, 2012-2-23]
 */
public class DateUtil
{
    /**
     * TAG:用于打印日志的
     */
    public static final String TAG = "DateUtil";
    
    /**
     * 日期formatter
     */
    public static final SimpleDateFormat FRIEND_MANAGER_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    
    /**
     * 通话时间
     */
    public static final SimpleDateFormat VOIP_TALKING_TIME = new SimpleDateFormat(
            "mm:ss");
    
    /**
     * 时间戳格式
     */
    public static final SimpleDateFormat TIMESTAMP_DF = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    
    /**
     * 一秒
     */
    private static final long SECOND = 1000;
    
    /**
     * 一分钟
     */
    private static final long ONE_MINUTE = 60 * SECOND;
    
    /**
     * 一小时
     */
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    
    /**
     * 
     * [一句话功能简述]<BR>
     * 根据默认的格式获得当前时间字符串
     * 
     * @return 当前时间
     */
    public static String getCurrentDateString()
    {
        return TIMESTAMP_DF.format(new Date());
    }
    
    /**
     * 转化格式化的日期字符串<BR>
     * 
     * @param date
     *            日期
     * @return 格式化的日期字符串
     */
    public static String getDateString(Date date)
    {
        if (null == date)
        {
            return getCurrentDateString();
        }
        return TIMESTAMP_DF.format(date);
    }
    
    /**
     * 
     * 
     * @param diffTime
     *            通话时长
     * @return 通话时长
     */
    /**
     * 
     * 设置通话时长格式
     * 
     * @param diffTime
     *            long
     * @param hh
     *            时
     * @param mm
     *            分
     * @param ss
     *            秒
     * @return 通话时长格式
     */
    public static String getDiffTime(long diffTime, String hh, String mm,
            String ss)
    {
        //小时常数 
        long hourMarker = 60 * 60;
        
        // 分钟常数
        long minuteMarker = 60;
        
        //秒常数 
        long secondMarker = 1;
        
        DecimalFormat decfmt = new DecimalFormat();
        //小时
        long hour = diffTime / hourMarker;
        //分钟
        long minute = (diffTime - hour * hourMarker) / minuteMarker;
        //秒
        long second = (diffTime - hour * hourMarker - minute * minuteMarker)
                / secondMarker;
        
        if (hour == 0 && minute == 0)
        {
            return decfmt.format(second) + ss;
        }
        if (hour == 0 && minute != 0)
        {
            return decfmt.format(minute) + mm + decfmt.format(second) + ss;
        }
        else
        {
            return decfmt.format(hour) + hh + decfmt.format(minute) + mm
                    + decfmt.format(second) + ss;
        }
    }
    
    /**
     * 通话详情通话时长 设置通话时长格式
     * 
     * @param diffTime
     *            long
     * @return 通话时长格式
     */
    public static String getDiffTime2(long diffTime)
    {
        //        //小时常数 
        //        long hourMarker = 60 * 60;
        
        // 分钟常数
        long minuteMarker = 60;
        
        //秒常数 
        long secondMarker = 1;
        
        DecimalFormat decfmt = new DecimalFormat();
        //小时
        // long hour = diffTime / hourMarker;
        //分钟
        long minute = diffTime / minuteMarker;
        //秒
        long second = (diffTime - minute * minuteMarker) / secondMarker;
        
        if (minute == 0)
        {
            return decfmt.format(second);
        }
        else
        {
            return decfmt.format(minute) + ":" + decfmt.format(second);
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * 根据默认的格式获得生日时间字符串
     * 
     * @param date
     *            Date
     * @return 生日格式时间
     */
    public static String getBirthdayDateString(Date date)
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
    /**
     * 找朋友小助手时间转换<BR>
     * 
     * @param date
     *            date
     * @return 日期的时间串
     */
    public static String getFormatTimeStringForFriendManager(Date date)
    {
        return null == date ? FRIEND_MANAGER_FORMATTER.format(new Date())
                : FRIEND_MANAGER_FORMATTER.format(date);
    }
    
    /**
     * 找朋友小助手时间串转Date<BR>
     * 
     * @param friendManagerTimeString
     *            找朋友小助手时间串
     * @return 日期对象
     * 
     * @throws ParseException
     *             解析发生异常
     */
    public static Date getDateFromFriendManageTimeString(
            String friendManagerTimeString) throws ParseException
    {
        return FRIEND_MANAGER_FORMATTER.parse(friendManagerTimeString);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param context
     *            Context
     * @param lastTime
     *            String
     * @return String
     */
    public static String getFormatTimeString(Context context, String lastTime)
    {
        if (lastTime == null)
        {
            return null;
        }
        Date lastDate = null;
        try
        {
            lastDate = TIMESTAMP_DF.parse(lastTime);
        }
        catch (ParseException e)
        {
            Logger.e(TAG, "ParseException:" + e.getMessage());
        }
        if (lastDate == null)
        {
            return null;
        }
        return getFormatTimeByDate(context, lastDate);
    }
    
    /**
     * 通话详情通话时间点的格式 获取具体时间 12小时制
     * 
     * @param context
     *            Context
     * @param lastDate
     *            Date
     * @return 具体时间
     */
    public static String getFormatClearTimeByDate(Context context, Date lastDate)
    {
        //将Date转化为Calendar
        Calendar cal = new GregorianCalendar();
        cal.setTime(lastDate);
        String formatTime = null;
        
        //取出具体时间
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = df.format(lastDate);
        if (time.charAt(0) == '0')
        {
            time = time.substring(1);
        }
        // 再把时间的年月日取出来
        df = new SimpleDateFormat("yyyy-MM-dd");
        // 取出时间是上午(0-12)\下午(12-18)\晚上(18-24)
        int chatHour = cal.get(Calendar.HOUR_OF_DAY);
        int chatMinute = cal.get(Calendar.MINUTE);
        //如果分钟小于10分，加0
        String chatMinuteStr = ""
                + (chatMinute < 10 ? ("0" + chatMinute) : chatMinute);
        if (chatHour == 0 && chatMinute >= 0)//(0:00-0:59)
        {
            //上午
            formatTime = context.getResources()
                    .getString(R.string.voip_am_string, "12:" + chatMinuteStr);
        }
        if (0 < chatHour && chatHour <= 11) //(00:00-12：00)
        {
            //上午
            formatTime = context.getResources()
                    .getString(R.string.voip_am_string, time);
        }
        if (chatHour == 12 && chatMinute == 0)//(12:00)
        {
            //上午
            formatTime = context.getResources()
                    .getString(R.string.voip_am_string, time);
        }
        if (chatHour == 12 && chatMinute > 0)//(12:01-12:59)
        {
            //下午
            formatTime = context.getResources()
                    .getString(R.string.voip_af_string, time);
        }
        int hour = chatHour - 12;
        String detailTime = context.getResources()
                .getString(R.string.voip_detail_time, hour, chatMinuteStr);
        if (12 < chatHour && chatHour <= 17)//(13:00-17:00)
        {
            //下午
            formatTime = context.getResources()
                    .getString(R.string.voip_af_string, detailTime);
        }
        if (chatHour == 18 && chatMinute == 0)//(18:00)
        {
            //下午
            formatTime = context.getResources()
                    .getString(R.string.voip_af_string, detailTime);
        }
        if (chatHour == 18 && chatMinute > 0)//(18:01-18:59)
        {
            //下午
            formatTime = context.getResources()
                    .getString(R.string.voip_af_string, detailTime);
        }
        if (18 < chatHour && chatHour <= 23)//(19:00-23:59)
        {
            //下午
            formatTime = context.getResources()
                    .getString(R.string.voip_af_string, detailTime);
        }
        
        return formatTime;
        
    }
    
    /**
     * 获取具体时间 12小时制
     * 
     * @param context
     *            Context
     * @param lastDate
     *            Date
     * @return 具体时间
     */
    public static String getFormatClearTimeByDate2(Context context,
            Date lastDate)
    {
        //取出具体时间
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        String time = df.format(lastDate);
        if (time.charAt(0) == '0')
        {
            time = time.substring(1);
        }
        String formatTime = null;
        Calendar cal = new GregorianCalendar();
        cal.setTime(lastDate);
        // 取出时间是上午(0-12)\下午(12-24)
        int chatHour = cal.get(Calendar.HOUR_OF_DAY);
        if (0 <= chatHour && chatHour <= 12)
        {
            //上午
            formatTime = context.getResources()
                    .getString(R.string.voip_am_string, time);
        }
        else
        {
            //下午
            formatTime = context.getResources()
                    .getString(R.string.voip_af_string, time);
        }
        return formatTime;
    }
    
    /**
     * 
     * 通话记录详情年-月-日
     * 
     * @param context
     *            Context
     * @param lastDate
     *            Date
     * @return 通话记录详情年-月-日
     */
    public static String getCommunicationLogDetailTimeByDate(Context context,
            Date lastDate)
    {
        //取出具体时间
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        String time = df.format(lastDate);
        if (time.charAt(0) == '0')
        {
            time = time.substring(1);
        }
        // 再把时间的年月日取出来
        df = new SimpleDateFormat("yyyy-MM-dd");
        String lastTimeDate = df.format(lastDate);
        return lastTimeDate;
    }
    
    /**
     * 通话记录列表时间显示
     * 
     * @param context
     *            Context
     * @param lastDate
     *            Date
     * @return String
     */
    
    public static String getCommunicationTimeByDate(Context context,
            Date lastDate)
    {
        
        Calendar c = Calendar.getInstance();
        //当前年份
        int nowYear = c.get(Calendar.YEAR);
        //将Date转化为Calendar
        Calendar cal = new GregorianCalendar();
        cal.setTime(lastDate);
        //聊天的时间
        int chatYear = cal.get(Calendar.YEAR);
        //Calendar 转为 Date:
        Date nowDate = c.getTime();
        
        String formatTime = null;
        
        //取出具体时间
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = df.format(lastDate);
        if (time.charAt(0) == '0')
        {
            time = time.substring(1);
        }
        // 再把时间的年月日取出来
        df = new SimpleDateFormat("yyyy-MM-dd");
        String lastTimeDate = df.format(lastDate);
        //判断是否是当今年
        if (nowYear != chatYear)
        {
            formatTime = context.getResources().getString(R.string.year_string,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
        }
        // 判断是否是今天
        else if (df.format(nowDate).equals(lastTimeDate))
        {
            // 取出时间是上午(0-12)\下午(12-18)\晚上(18-24)
            int chatHour = cal.get(Calendar.HOUR_OF_DAY);
            int chatMinute = cal.get(Calendar.MINUTE);
            //如果分钟小于10分，加0
            String chatMinuteStr = ""
                    + (chatMinute < 10 ? ("0" + chatMinute) : chatMinute);
            if (chatHour == 0 && chatMinute >= 0)//(0:00-0:59)
            {
                //上午
                formatTime = context.getResources()
                        .getString(R.string.voip_am_string,
                                "12:" + chatMinuteStr);
            }
            if (0 < chatHour && chatHour <= 11) //(1:00-11：59)
            {
                //上午
                formatTime = context.getResources()
                        .getString(R.string.voip_am_string, time);
            }
            if (chatHour == 12 && chatMinute == 0)//(12:00)
            {
                //上午
                formatTime = context.getResources()
                        .getString(R.string.voip_am_string, time);
            }
            if (chatHour == 12 && chatMinute > 0)//(12:01-12:59)
            {
                //下午
                formatTime = context.getResources()
                        .getString(R.string.voip_af_string, time);
            }
            int hour = chatHour - 12;
            String detailTime = context.getResources()
                    .getString(R.string.voip_detail_time, hour, chatMinuteStr);
            if (12 < chatHour && chatHour <= 17)//(13:00-17:00)
            {
                //下午
                formatTime = context.getResources()
                        .getString(R.string.voip_af_string, detailTime);
            }
            if (chatHour == 18 && chatMinute == 0)//(18:00)
            {
                //下午
                formatTime = context.getResources()
                        .getString(R.string.voip_af_string, detailTime);
            }
            if (chatHour == 18 && chatMinute > 0)//(18:01-18:59)
            {
                //下午
                formatTime = context.getResources()
                        .getString(R.string.voip_af_string, detailTime);
            }
            if (18 < chatHour && chatHour <= 23)//(19:00-23:59)
            {
                //下午
                formatTime = context.getResources()
                        .getString(R.string.voip_af_string, detailTime);
            }
        }
        else
        {
            // 昨天
            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
            if (df.format(c.getTime()).equals(lastTimeDate))
            {
                formatTime = context.getResources()
                        .getString(R.string.voip_yesterday, time);
            }
            else
            {
                c.setTime(lastDate);
                formatTime = context.getResources()
                        .getString(R.string.voip_year_string,
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH) + 1,
                                c.get(Calendar.DAY_OF_MONTH));
            }
        }
        return formatTime;
    }
    
    /**
     * 
     * 聊天界面时间格式转化<BR>
     * 
     * @param context
     *            Context
     * @param lastDate
     *            Date
     * @return 时间
     */
    public static String getFormatTimeByDate(Context context, Date lastDate)
    {
        long before = lastDate.getTime();
        Calendar c = Calendar.getInstance();
        //当前年份
        int nowYear = c.get(Calendar.YEAR);
        //将Date转化为Calendar
        Calendar cal = new GregorianCalendar();
        cal.setTime(lastDate);
        //聊天的时间
        int chatYear = cal.get(Calendar.YEAR);
        //Calendar 转为 Date:
        Date nowDate = c.getTime();
        long now = nowDate.getTime();
        long diff = now - before;
        String formatTime = null;
        if (diff > 0 && diff < ONE_MINUTE)
        {
            if (diff / SECOND == 0 || diff / SECOND == 1)
            {
                formatTime = context.getResources()
                        .getString(R.string.one_second, diff / SECOND);
            }
            else
            {
                formatTime = context.getResources()
                        .getString(R.string.before_second, diff / SECOND);
            }
        }
        else if (diff >= ONE_MINUTE && diff < ONE_HOUR)
        {
            if (diff / ONE_MINUTE == 1)
            {
                formatTime = context.getResources()
                        .getString(R.string.one_minute, diff / ONE_MINUTE);
            }
            else
            {
                formatTime = context.getResources()
                        .getString(R.string.before_minute, diff / ONE_MINUTE);
            }
        }
        else
        {
            //取出具体时间
            SimpleDateFormat df = new SimpleDateFormat("HH:mm",
                    Locale.getDefault());
            String time = df.format(lastDate);
            // 再把时间的年月日取出来
            df = new SimpleDateFormat("yyyy-MM-dd");
            String lastTimeDate = df.format(lastDate);
            //判断是否是当今年
            if (nowYear != chatYear)
            {
                formatTime = context.getResources()
                        .getString(R.string.year_string,
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.DAY_OF_MONTH));
            }
            // 判断是否是今天
            else if (df.format(nowDate).equals(lastTimeDate) && diff > ONE_HOUR)
            {
                // 取出时间是上午(0-12)\下午(12-18)\晚上(18-24)
                int chatHour = cal.get(Calendar.HOUR_OF_DAY);
                int chatMinute = cal.get(Calendar.MINUTE);
                if (0 <= chatHour && chatHour <= 11) //(00:00-12：00)
                {
                    //上午
                    formatTime = context.getResources()
                            .getString(R.string.am_string, time);
                }
                else if (chatHour == 12 && chatMinute == 0)//(12:00)
                {
                    //上午
                    formatTime = context.getResources()
                            .getString(R.string.am_string, time);
                }
                else if (chatHour == 12 && chatMinute > 0)//(12:01-12:59)
                {
                    //下午
                    formatTime = context.getResources()
                            .getString(R.string.af_string, time);
                }
                else if (12 < chatHour && chatHour <= 17)//(13:00-17:00)
                {
                    //下午
                    formatTime = context.getResources()
                            .getString(R.string.af_string, time);
                }
                else if (chatHour == 18 && chatMinute == 0)//(18:00)
                {
                    //下午
                    formatTime = context.getResources()
                            .getString(R.string.af_string, time);
                }
                else if (chatHour == 18 && chatMinute > 0)//(18:01-23:59)
                {
                    //晚上
                    formatTime = context.getResources()
                            .getString(R.string.ev_string, time);
                }
                else if (18 < chatHour && chatHour < 23)
                {
                    //晚上
                    formatTime = context.getResources()
                            .getString(R.string.ev_string, time);
                }
            }
            else
            {
                // 昨天
                c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
                if (df.format(c.getTime()).equals(lastTimeDate))
                {
                    formatTime = context.getResources()
                            .getString(R.string.before_one_day, time);
                }
                else
                {
                    c.setTime(lastDate);
                    formatTime = context.getResources()
                            .getString(R.string.date_string,
                                    c.get(Calendar.MONTH) + 1,
                                    c.get(Calendar.DATE));
                }
            }
        }
        
        return formatTime;
    }
    
    /**
     * 服务器返回的离线消息时间为UTC时间，需要转换为本地时间
     * 
     * @author 李颖00124251
     * @version [RCS Client V100R001C03, 2012-3-16]
     * @param dateStr
     *            String 解析前的字符串时间对象
     * @return Date 经过解析后的时间对象
     * @exception ParseException
     *                解析错误
     */
    public static Date getDelayTime(String dateStr) throws ParseException
    {
        
        SimpleDateFormat imDelayFormatter = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        imDelayFormatter.setTimeZone(timeZone);
        
        return imDelayFormatter.parse(dateStr);
    }
    
}
