/*
 * 文件名: ContactCodeUtil.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 马波
 * 创建时间:Feb 24, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import java.util.HashMap;
import java.util.Map;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;

import android.content.Context;

/**
 * 
 * 将邮箱及号码类型转换成对应的描述字符串<BR>
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-5-24]
 */
public class ContactCodeUtil
{
    /**
     * 定义TAG
     */
    private static final String TAG = "ContactCodeUtil";
    
    /**
     * 号码类型Map
     */
    private static Map<String, Integer> mPhoneCodeCodeMap = null;
    
    /**
     * 邮箱类型Map
     */
    private static Map<String, Integer> mEmailCodeCodeMap = null;
    
    /**
     * 初始化号码类型Map
     */
    private static void initPhoneCodeMap()
    {
        mPhoneCodeCodeMap = new HashMap<String, Integer>();
        
        mPhoneCodeCodeMap.put("1", R.string.phone_1);
        mPhoneCodeCodeMap.put("2", R.string.phone_2);
        mPhoneCodeCodeMap.put("3", R.string.phone_3);
        mPhoneCodeCodeMap.put("4", R.string.phone_4);
        mPhoneCodeCodeMap.put("5", R.string.phone_5);
        mPhoneCodeCodeMap.put("6", R.string.phone_6);
        mPhoneCodeCodeMap.put("7", R.string.phone_7);
        mPhoneCodeCodeMap.put("8", R.string.phone_8);
        mPhoneCodeCodeMap.put("9", R.string.phone_9);
        mPhoneCodeCodeMap.put("10", R.string.phone_10);
        mPhoneCodeCodeMap.put("11", R.string.phone_11);
        mPhoneCodeCodeMap.put("12", R.string.phone_12);
        mPhoneCodeCodeMap.put("13", R.string.phone_13);
        mPhoneCodeCodeMap.put("14", R.string.phone_14);
        mPhoneCodeCodeMap.put("15", R.string.phone_15);
        mPhoneCodeCodeMap.put("16", R.string.phone_16);
        mPhoneCodeCodeMap.put("17", R.string.phone_17);
        mPhoneCodeCodeMap.put("18", R.string.phone_18);
        mPhoneCodeCodeMap.put("19", R.string.phone_19);
        mPhoneCodeCodeMap.put("20", R.string.phone_20);
        mPhoneCodeCodeMap.put("21", R.string.phone_21);
    }
    
    /**
     * 初始化邮件类型Map
     */
    private static void initEmailCodeMap()
    {
        mEmailCodeCodeMap = new HashMap<String, Integer>();
        
        mEmailCodeCodeMap.put("1", R.string.email_1);
        mEmailCodeCodeMap.put("2", R.string.email_2);
        mEmailCodeCodeMap.put("3", R.string.email_3);
        mEmailCodeCodeMap.put("4", R.string.email_4);
        mEmailCodeCodeMap.put("5", R.string.email_5);
    }
    
    /**
     * 通过错误码获取错误信息文字字符串
     * 
     * @param phoneType
     *            号码类型
     * @param context
     *            程序Context
     * @return 根据电话类型获取到的对应的字符串， 如果类型有问题或对应信息字符串未找到，则返回null
     */
    public static String getPhoneInfo(Context context, String phoneType)
    {
        if (!StringUtil.isNullOrEmpty(phoneType))
        {
            if (mPhoneCodeCodeMap == null)
            {
                initPhoneCodeMap();
            }
            
            String retmsg = getCodeText(context, mPhoneCodeCodeMap, phoneType);
            if (retmsg != null)
            {
                return retmsg;
            }
        }
        
        Logger.e(TAG, "未知错误(" + phoneType + ")");
        return null;
    }
    
    /**
     * 通过错误码获取错误信息文字字符串
     * 
     * @param emailType
     *            邮箱类型
     * @param context
     *            程序Context
     * @return 根据邮箱类型获取到的对应的字符串， 如果类型有问题或对应信息字符串未找到，则返回null
     */
    public static String getEmailInfo(Context context, String emailType)
    {
        if (!StringUtil.isNullOrEmpty(emailType))
        {
            if (mEmailCodeCodeMap == null)
            {
                initEmailCodeMap();
            }
            
            String retmsg = getCodeText(context, mEmailCodeCodeMap, emailType);
            if (retmsg != null)
            {
                return retmsg;
            }
        }
        
        Logger.e(TAG, "未知错误(" + emailType + ")");
        return null;
    }
    
    /**
     * 
     * 获取对应错误码的文字描述
     * @param context 上下文
     * @param infoMap 数据集合
     * @param code 错误码
     * @return 文字描述
     */
    private static String getCodeText(Context context,
            Map<String, Integer> infoMap, String code)
    {
        if (infoMap.containsKey(code))
        {
            try
            {
                return context.getResources()
                        .getString(infoMap.get(code));
            }
            catch (Exception e)
            {
                return null;
            }
        }
        
        return null;
    }
}