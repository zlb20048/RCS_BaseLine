/*
 * 文件名: UploadHttpTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-24
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 工具类 判断当前网络和截取网络数据中的size字段
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-24]
 */
public class Util
{
    
    /**
     * "3gwap"字符串常量
     */
    private static final String WAPSTR = "3gwap";
    
    /**
     * 保存APN信息的变量
     */
    private static String apn;
    
    /**
     * 判断apn是否3Gwap方式
     * @param context 浏览器对象上下文
     * @return 是否为3gwap代理(true:apn是3Gwap,false:不是)
     */
    public static boolean is3Gwap(Context context)
    {
        if (null == apn)
        {
            getAPN(context);
        }
        
        if (null != apn && apn.equals(WAPSTR))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 通过context取得ConnectivityManager中的NetworkInfo里关于apn的联网信息
     * @param context 浏览器对象上下文
     * @return 代理模式
     */
    private static String getAPN(Context context)
    {
        if (apn == null)
        {
            // 通过context得到ConnectivityManager连接管理
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 通过ConnectivityManager得到NetworkInfo网络信息
            NetworkInfo info = manager.getActiveNetworkInfo();
            // 获取NetworkInfo中的apn信息
            apn = info.getExtraInfo();
        }
        return apn;
    }
    
    /**
     * 返回当前时间
     * @return "yyyy-MM-dd HH:mm:ss"格式的时间字符串
     */
    public static String getTime()
    {
        // 使用默认时区和语言环境获得一个日历
        Calendar cale = Calendar.getInstance();
        // 将Calendar类型转换成Date类型
        Date tasktime = cale.getTime();
        // 设置日期输出的格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 格式化输出
        return df.format(tasktime);
    }
    
    /**
     * 将字符串str按子字符串separatorChars 分割成数组
     * @param str 要拆分的字符串
     * @param separatorChars 用来拆分的分割字符
     * @return 拆分后的字符串
     */
    public static String[] split2(String str, String separatorChars)
    {
        return splitWorker(str, separatorChars, -1, false);
    }
    
    /**
     * 拆分字符串
     * @param str 要拆分的字符串
     * @param separatorChars 用来拆分的分割字符
     * @param max 要拆分字符串的最大长度
     * @param preserveAllTokens
     * @return 拆分后的字符串
     */
    private static String[] splitWorker(String str, String separatorChars,
            int max, boolean preserveAllTokens)
    {
        if (str == null)
        {
            return null;
        }
        int len = str.length();
        if (len == 0)
        {
            return new String[] { "" };
        }
        Vector<String> vector = new Vector<String>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null)
        {
            while (i < len)
            {
                if (str.charAt(i) == '\r' || str.charAt(i) == '\n'
                        || str.charAt(i) == '\t')
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        vector.addElement(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                }
                else
                {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        }
        else if (separatorChars.length() == 1)
        {
            char sep = separatorChars.charAt(0);
            while (i < len)
            {
                if (str.charAt(i) == sep)
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        vector.addElement(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                }
                else
                {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        }
        else
        {
            while (i < len)
            {
                int id = i + separatorChars.length() < len ? i
                        + separatorChars.length() : len;
                if (separatorChars.indexOf(str.charAt(i)) >= 0
                        && separatorChars.equals(str.substring(i, id)))
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        vector.addElement(str.substring(start, i));
                        match = false;
                    }
                    i += separatorChars.length();
                    start = i;
                }
                else
                {
                    lastMatch = false;
                    match = true;
                    i++;
                }
            }
        }
        if (match || preserveAllTokens && lastMatch)
        {
            vector.addElement(str.substring(start, i));
        }
        String[] ret = new String[vector.size()];
        vector.copyInto(ret);
        return ret;
    }
    
}
