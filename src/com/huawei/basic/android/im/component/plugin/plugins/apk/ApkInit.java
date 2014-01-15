/*
 * 文件名: ApkInit.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 21, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.plugins.apk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.IPluginInit;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.component.plugin.core.xml.PluginConfigXmlParser.PluginConfigModel;
import com.huawei.basic.android.im.component.plugin.util.ApkUtil;

/**
 * apk插件初始化<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 21, 2012] 
 */
public class ApkInit implements IPluginInit
{
    
    private ApkPluginDbAdapter mApkPluginDbAdapter;
    
    /**
     * {@inheritDoc}
    */
    @Override
    public void init(Context context, PluginConfigModel model)
    {
        ApkPluginDbAdapter.getInstance().init(context);
        mApkPluginDbAdapter = ApkPluginDbAdapter.getInstance();
        
        ArrayList<ApkPlugin> configList = new ArrayList<ApkPlugin>();
        
        if (null != model.getPluginList())
        {
            for (BasePlugin basePlugin : model.getPluginList())
            {
                if (basePlugin instanceof ApkPlugin)
                {
                    configList.add((ApkPlugin) basePlugin);
                }
            }
        }
        
        //删除配置文件中没有的记录
        deletePluginsByConfig(configList,
                mApkPluginDbAdapter.queryAllApkPlugins());
        
        //同步配置文件和数据库
        synApkPluginByConfig(configList,
                mApkPluginDbAdapter.queryAllApkPlugins());
        
        //同步数据库和本地安装包
        synApkPluginWithLoacalInstall(context);
    }
    
    private void setApkPluginContentValues(ContentValues cv,
            ApkPlugin configApkPlugin)
    {
        if (null == cv || null == configApkPlugin)
        {
            return;
        }
        cv.put(PluginColumns.DESC, configApkPlugin.getDesc());
        cv.put(PluginColumns.NAME, configApkPlugin.getName());
        cv.put(PluginColumns.ICON_URL, configApkPlugin.getIconUrl());
        cv.put(PluginColumns.PACKAGE_NAME, configApkPlugin.getPackageName());
        cv.put(PluginColumns.PUB_TIME, configApkPlugin.getPubTime());
        cv.put(PluginColumns.START_ACTION, configApkPlugin.getStartAction());
        cv.put(PluginColumns.URL, configApkPlugin.getUrl());
        cv.put(PluginColumns.INTENT_EXTRA,
                convertExtrasMapToString(configApkPlugin.getIntentExtras()));
    }
    
    /**
     * 同步配置文件中插件和数据库中的插件<BR>
     * @param configList 配置文件中的APK列表
     * @param dbList 数据库中APK列表
     */
    private void synApkPluginByConfig(List<ApkPlugin> configList,
            List<ApkPlugin> dbList)
    {
        for (ApkPlugin configApkPlugin : configList)
        {
            //exist
            boolean flag = false;
            if (null != dbList)
            {
                for (ApkPlugin dbApkPlugin : dbList)
                {
                    if (configApkPlugin.getPluginId()
                            .equals(dbApkPlugin.getPluginId()))
                    {
                        ContentValues cv = new ContentValues();
                        if (configApkPlugin.getVersion() > dbApkPlugin.getVersion())
                        {
                            cv.put(PluginColumns.NEED_UPDATE, 1);
                        }
                        setApkPluginContentValues(cv, configApkPlugin);
                        mApkPluginDbAdapter.updateByPluginId(cv,
                                configApkPlugin.getPluginId());
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag)
            {
                mApkPluginDbAdapter.insertApk(configApkPlugin);
            }
        }
    }
    
    /**
     * 同步数据库和本地安装包<BR>
     * @param context context上下文
     */
    private void synApkPluginWithLoacalInstall(Context context)
    {
        //3.获取已安装插件的packname的列表
        List<PackageInfo> installedPackageList = ApkUtil.getInstalledPackages(context);
        
        //获取新的数据库 apk 数据
        List<ApkPlugin> newDbApkPluginList = mApkPluginDbAdapter.queryAllApkPlugins();
        if (null == newDbApkPluginList)
        {
            return;
        }
        for (ApkPlugin apkPlugin : newDbApkPluginList)
        {
            //是否已安装
            boolean flag = false;
            for (PackageInfo info : installedPackageList)
            {
                if (info.packageName.equals(apkPlugin.getPackageName()))
                {
                    if (apkPlugin.getStatus() != BasePlugin.STATUS_INSTALLED)
                    {
                        ContentValues cv = new ContentValues();
                        cv.put(PluginColumns.STATUS,
                                BasePlugin.STATUS_INSTALLED);
                        mApkPluginDbAdapter.updateByPackageName(cv,
                                apkPlugin.getPackageName());
                    }
                    flag = true;
                }
            }
            //没有安装，更新状态为未安装
            if (!flag)
            {
                if (apkPlugin.getStatus() != BasePlugin.STATUS_UNINSTALLED)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(PluginColumns.STATUS, BasePlugin.STATUS_UNINSTALLED);
                    mApkPluginDbAdapter.updateByPackageName(cv,
                            apkPlugin.getPackageName());
                }
            }
        }
    }
    
    /**
     * 删除配置文件中不存在的列表<BR>
     * @param configList 配置文件中的列表
     * @param dbList 数据库中列表
     */
    private void deletePluginsByConfig(List<? extends BasePlugin> configList,
            List<? extends BasePlugin> dbList)
    {
        if (null != dbList)
        {
            for (BasePlugin dbPlugin : dbList)
            {
                //config是否存在
                boolean flag = false;
                if (null != configList)
                {
                    for (BasePlugin configPlugin : configList)
                    {
                        if (dbPlugin.getPluginId()
                                .equals(configPlugin.getPluginId()))
                        {
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag)
                {
                    mApkPluginDbAdapter.deleteByPluginId(dbPlugin.getPluginId());
                }
            }
        }
    }
    
    /**
     * map生成String{name1,value1;name2,value2;}<BR>
     * @param map intent传递的参数
     * @return extras的string形式
     */
    private String convertExtrasMapToString(Map<String, String> map)
    {
        if (null == map)
        {
            return null;
        }
        if (null != map.keySet())
        {
            StringBuffer sb = new StringBuffer();
            for (String key : map.keySet())
            {
                sb.append(key).append(",").append(map.get(key)).append(";");
            }
            return sb.toString();
        }
        return null;
    }
}
