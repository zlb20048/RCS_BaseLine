/*
 * 文件名: BasePlugin.java
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

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.util.Log;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
@Root(name = "plugin")
public abstract class BasePlugin implements IPlugin, Serializable
{
    /**
     * 未安装
     */
    public static final int STATUS_UNINSTALLED = 1;
    
    /**
     * 已安装
     */
    public static final int STATUS_INSTALLED = 2;
    
    /**
     * 正在安装
     */
    public static final int STATUS_INSTALLING = 3;
    
    /**
     * 内部插件
     */
    public static final int TYPE_INTERNAL = 1;
    
    /**
     * APK插件
     */
    public static final int TYPE_APK = 2;
    
    private static final long serialVersionUID = 1L;
    
    private static final String TAG = "BasePlugin";
    
    @Element(name = "plugin-id", required = false)
    private String pluginId;
    
    @Element(name = "name", required = false)
    private String name;
    
    @Element(name = "version", required = false)
    private int version;
    
    @Element(name = "desc", required = false)
    private String desc;
    
    @Element(name = "pub-time", required = false)
    private String pubTime;
    
    @Element(name = "show-in-contactlist", required = false)
    private boolean showInContactList;
    
    @Element(name = "status", required = false)
    private int status;
    
    @Element(name = "icon", required = false)
    private byte[] icon;
    
    @Element(name = "icon-url", required = false)
    private String iconUrl;
    
    @ElementList(inline = true, required = false)
    private List<BaseMethod> methods;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void install(ICallBack listener)
    {
        Log.d(TAG, "preInstall ....");
        //安装之前
        preInstall();
        
        Log.d(TAG, "installing ....");
        //执行安装
        doInstall(listener);
        
        Log.d(TAG, "afterInstall ....");
        //安装之后
        afterInstall();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ICallBack listener)
    {
        
    }
    
    /**
     * 安装动作<BR>
     * @param listener listener
     */
    public abstract void doInstall(ICallBack listener);
    
    /**
     * 安装之前
     */
    public void preInstall()
    {
    }
    
    /**
     * 安装之后的操作
     */
    public void afterInstall()
    {
    }
    
    public void setShowInContactList(boolean showInContactList)
    {
        this.showInContactList = showInContactList;
    }
    
    public boolean isShowInContactList()
    {
        return showInContactList;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setIcon(byte[] icon)
    {
        this.icon = icon;
    }
    
    public byte[] getIcon()
    {
        return icon;
    }
    
    public void setPubTime(String pubTime)
    {
        this.pubTime = pubTime;
    }
    
    public String getPubTime()
    {
        return pubTime;
    }
    
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    
    public String getDesc()
    {
        return desc;
    }
    
    public void setVersion(int version)
    {
        this.version = version;
    }
    
    public int getVersion()
    {
        return version;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
    }
    
    public String getPluginId()
    {
        return pluginId;
    }
    
    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }
    
    public String getIconUrl()
    {
        return iconUrl;
    }
    
    public void setMethods(List<BaseMethod> methods)
    {
        this.methods = methods;
    }
    
    public List<BaseMethod> getMethods()
    {
        return methods;
    }
}
