/*
 * 文件名: ConfigManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-5-17
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 网络配置文件读取的管理类，对设置项进行初始化
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-5-17] 
 */
public class ConfigManager
{
    
    private static final String TAG = "ConfigManager";
    
    /**
     * SettingsManager实例
     */
    private static ConfigManager singleInstance;
    
    /**
     * 私有构造
     */
    private ConfigManager()
    {
    }
    
    /**
     * 提供给外部获取实例
     * 
     * @return SettingsManager实例
     */
    public static ConfigManager getInstance()
    {
        if (singleInstance == null)
        {
            singleInstance = new ConfigManager();
        }
        return singleInstance;
    }
    
    /**
     * 初始化内存中的设置项，项目启动时调用
     * 
     * @param context context
     */
    public void initConfig(Context context)
    {
        // sdcard存在，并且可以访问
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            File configFile = new File(
                    android.os.Environment.getExternalStorageDirectory()
                            + "/config.xml");
            if (configFile.exists())
            {
                // 有此文件就读取 .
                try
                {
                    InputStream input = new FileInputStream(
                            android.os.Environment.getExternalStorageDirectory()
                                    + "/config.xml");
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(input, "UTF-8");
                    
                    // 产生第一个事件
                    int event = parser.getEventType();
                    while (event != XmlPullParser.END_DOCUMENT)
                    {
                        switch (event)
                        {
                            case XmlPullParser.START_DOCUMENT:
                                // 判断当前事件是否是文档开始事件
                                break;
                            case XmlPullParser.START_TAG:
                                // 判断当前事件是否是标签元素开始事件
                                if ("aasurl".equals(parser.getName()))
                                {
                                    // 判断开始标签元素是否是aasurl
                                    String aasurl = parser.nextText();
                                    //TODO aas的配置地址
                                    Logger.i(TAG, "setAas_url: " + aasurl);
                                }
                                else if ("portalurl".equals(parser.getName()))
                                {
                                    // 判断开始标签元素是否是portalurl
                                    String portalurl = parser.nextText();
                                    //TODO portal的配置地址
                                    //                                    FusionConfig.getInstance()
                                    //                                            .getAasResult()
                                    //                                            .setPortalurl(portal_url);
                                    Logger.i(TAG, "setPortalurl: " + portalurl);
                                }
                                else if ("liveupdateurl".equals(parser.getName()))
                                {
                                    // 判断开始标签元素是否是liveupdateurl
                                    String liveupdateurl = parser.nextText();
                                    //TODO 升级服务器的配置地址
                                    //                                    FusionConfig.getInstance()
                                    //                                            .getAasResult()
                                    //                                            .setLiveupdateurl(liveupdate_url);
                                    Logger.i(TAG, "setLiveupdateurl: "
                                            + liveupdateurl);
                                }
                                else if ("voip_server".equals(parser.getName()))
                                {
                                    // 判断开始标签元素是否是portalurl
                                    String voipserver = parser.nextText();
                                    FusionConfig.getInstance()
                                            .setVoipServer(voipserver);
                                    Logger.i(TAG, "voip_server: " + voipserver);
                                }
                                else if ("voip_domain".equals(parser.getName()))
                                {
                                    // 判断开始标签元素是否是portalurl
                                    String voipdomain = parser.nextText();
                                    FusionConfig.getInstance()
                                            .setVoipDomain(voipdomain);
                                    Logger.i(TAG, "voip_domain: " + voipdomain);
                                }
                                else if ("voip_port".equals(parser.getName()))
                                {
                                    // 判断开始标签元素是否是portalurl
                                    int voipport = Integer.parseInt(parser.nextText());
                                    FusionConfig.getInstance()
                                            .setVoipPort(voipport);
                                    Logger.i(TAG, "voip_port: " + voipport);
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                // 判断当前事件是否是标签元素结束事件
                        }
                        // 进入下一个元素并触发相应事件
                        event = parser.next();
                    }
                    
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                    Logger.e(TAG, "config FileNotFoundException");
                }
                catch (XmlPullParserException e)
                {
                    e.printStackTrace();
                    Logger.e(TAG, "config XmlPullParserException");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Logger.e(TAG, "config IOException");
                }
            }
            else
            {
                // 没有此文件 .
                Logger.i(TAG, "没有网络配置文件");
                //                Toast.makeText(context,
                //                    "没有网络配置文件",
                //                    Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            // 没有此文件 .
            Logger.i(TAG, "没有sdcard");
            //            Toast.makeText(context,
            //                "没有sdcard",
            //                Toast.LENGTH_LONG).show();
        }
    }
}
