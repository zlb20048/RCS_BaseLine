/*
 * 文件名: SysAppInfoModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;

/**
 * 放系统应用信息<BR>
 * 
 * @author RaulXiao
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 16, 2012] 
 */
public class SysAppInfoModel implements Serializable
{
    
    /**
     * 应用的类型: 缺省应用   
     */
    public static final int TYPE_DEFAULT = 1;
    
    /**
     * 应用的类型: 推荐扩展应用
     */
    public static final int TYPE_EXPAND = 2;
    
    /**
     * 应用的类型: 热门应用
     */
    public static final int TYPE_HOT = 3;
    
    //    /**
    //     * 应用的类型: 热门应用
    //     */
    //    public static final int TYPE_HOT = 4;
    
    /**
     * 应用支持的单点登录类型 ：不支持单点登录   
     */
    public static final String SSO_NO_POINT = "0";
    
    /**
     * 应用支持的单点登录类型 ：支持Token方式单点登录
     */
    public static final String SSO_TOKEN = "1";
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 应用名称
     */
    private String name;
    
    /**
     * 应用的类型: <br>
     * 1：缺省应用   <br>
     * 2：扩展应用
     */
    private int type;
    
    /**
     * 应用描述
     */
    private String desc;
    
    /**
     * 应用图标本地存储的名字： "APPID.扩展名"
     */
    private String iconName;
    
    /**
     * 应用展示图标的URL
     */
    private String iconUrl;
    
    /**
     * 应用的访问URL
     */
    private String appUrl;
    
    /**
     * 更新时间  <br>
     * 格式yyyyMMddHHmmss
     */
    private String updateTime;
    
    /**
     * 应用支持的单点登录类型  <br>
     * 0：不支持单点登录   <br>
     * 1：支持Token方式单点登录
     */
    private String sso;
    
    /**
     * 构造方法
     */
    public SysAppInfoModel()
    {
        super();
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public String getDesc()
    {
        return desc;
    }
    
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    
    public String getIconName()
    {
        return iconName;
    }
    
    public void setIconName(String iconName)
    {
        this.iconName = iconName;
    }
    
    public String getIconUrl()
    {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }
    
    public String getAppUrl()
    {
        return appUrl;
    }
    
    public void setAppUrl(String appUrl)
    {
        this.appUrl = appUrl;
    }
    
    public String getUpdateTime()
    {
        return updateTime;
    }
    
    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }
    
    public String getSso()
    {
        return sso;
    }
    
    public void setSso(String sso)
    {
        this.sso = sso;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" appId:").append(appId);
        sb.append(" name:").append(name);
        sb.append(" type:").append(type);
        sb.append(" desc:").append(desc);
        sb.append(" iconName ").append(iconName);
        sb.append(" iconUrl:").append(iconUrl);
        sb.append(" appUrl:").append(appUrl);
        sb.append(" updateTime:").append(updateTime);
        sb.append(" sso:").append(sso);
        return sb.toString();
    }
    
}
