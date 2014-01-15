/*
 * 文件名: IPluginManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core;

import java.util.ArrayList;

import com.huawei.basic.android.im.component.plugin.plugins.apk.ApkPlugin;
import com.huawei.basic.android.im.component.plugin.plugins.internal.InternalPlugin;

/**
 * 插件管理接口<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
public interface IPluginManager
{
    /**
     * 安装插件<BR>
     * @param plugin 插件对象
     * @param listener 回调监听器
     */
    void install(BasePlugin plugin, ICallBack listener);
    
    /**
     * 安装插件<BR>
     * @param plugin 插件对象
     * @param listener 回调监听器
     */
    void uninstall(BasePlugin plugin, ICallBack listener);
    
    /**
     * 安装插件<BR>
     * @param plugin 插件对象
     * @param listener 回调监听器
     */
    void update(BasePlugin plugin, ICallBack listener);
    
    /**
     * 获取APK插件列表<BR>
     * @return  ArrayList<ApkPlugin>
     */
    ArrayList<ApkPlugin> getApkPluginList();
    
    /**
     * 获取内部插件列表<BR>
     * @return  ArrayList<InternalPlugin>
     */
    ArrayList<InternalPlugin> getInternalPluginList();
    
    /**
     * 更具插件ID获取插件<BR>
     * @param pluginId 插件ID
     * @return BasePlugin
     */
    BasePlugin getPluginByPluginId(String pluginId);
    
    /**
     * 启动插件<BR>
     * @param plugin 插件对象
     */
    void start(BasePlugin plugin);
    
    /**
     * 获取所有的插件列表<BR>
     * @return 插件列表
     */
    ArrayList<BasePlugin> getAllPluins();
}
