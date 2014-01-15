/*
 * 文件名: AASResult.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 登录model类，实现Parcelable，用于aidl间的通信<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-11]
 */
public class AASResult implements Parcelable
{
    
    /**
     * CREATOR
     */
    public static final Parcelable.Creator<AASResult> CREATOR = new Parcelable.Creator<AASResult>()
    {
        
        @Override
        public AASResult createFromParcel(Parcel source)
        {
            return new AASResult(source);
        }
        
        @Override
        public AASResult[] newArray(int size)
        {
            return new AASResult[size];
        }
    };
    
    /*********************用户信息************************/
    /**
     * return 返回码
     */
    private String result;
    
    /**
     * desc 对返回码的一些说明
     */
    private String desc;
    
    /**
     * 即时通信平台分配给用户的系统ID，对用户不可见
     */
    private String userSysId;
    
    /**
     * 本次登录ID，注销使用，由服务器分配
     */
    private String loginid;
    
    /**
     * 本次登录分配的token信息，由服务器分配
     */
    private String token;
    
    /**
     * 超时时间，相对当前时间的秒数；客户端需要在该时间前重新发起令牌更新消息
     */
    private String expiretime;
    
    /**
     * 用户账号信息，即时通信用户业务ID
     */
    private String userID;
    
    /**
     * 网盘Uid，由服务器分配（可能为空）
     */
    private String nduid;
    
    /**
     * 语言返回用户的语言类型。
     * zh_CN:中文
     * en_US:英文
     * es_ES:西语
     */
    private String language;
    
    /**
     * 第一次登录标识： 0：第一次登录  1：非第一次登录
     */
    private String loginfirsttime;
    
    /***********************  server info  服务器信息 ************************/
    
    /**
     * XMPP接入地址
     */
    private String xmppaddr = "http://192.168.9.104:5222";
    
    /**
     * svn地址和端口，格式如下：221.4.104.98:6060,211.4.104.98:7060
     */
    private String svnlist;
    
    /**
     * SVN用户名，明文
     */
    private String svnuser;
    
    /**
     * SVN密码，明文
     */
    private String svnpwd;
    
    /**
     * SyncML地址本同步接入地址，用于用户手机通讯录备份、恢复。
     */
    private String cabsyncmlurl;
    
    /**
     * 用户个人资料、好友、联系人、应用管理接入地址。主要是6.6 UE与平台基于REST的联系人管理接口接入地址。
     */
    private String cabgroupurl;
    
    /**
     * Web Portal服务器网盘首页URL 门户URL地址，对应于6.2章“用户管理接口”。
     */
    private String portalurl;
    
    /**
     * 升级服务器URL，对应“客户端版本检测接口”，用于客户端升级检测和接入。
     */
    private String liveupdateurl;
    
    /**
     * OSE服务器URL（手机客户端业务管理，上传头像、多媒体消息文件等）。
     */
    private String rifurl;
    
    /**
     * BOSH协议接入地址，可能存在多个，中间以半角逗号隔开，参考示例。对应于接口规范XMPP分册中BOSH接口接入
     */
    private String boshurl;
    
    /**
     * 邮箱接入地址，客户端通过该url接入集成邮箱系统，比如云邮；（目前支持的对接邮箱系统）
     */
    private String emailurl;
    
    /**
     * 微博门户接入url地址，PC客户端可以链接到该微博门户。
     */
    private String mbportalurl;
    
    /**
     * 微博平台url，手机客户端微博操作接入地址。
     */
    private String mbplaturl;
    
    /**
     * 是否弹出客户端迷你窗口  0：不弹出，默认值    1：弹出
     */
    private String popupminiwindow;
    
    /**
     * 构造函数
     */
    public AASResult()
    {
        // 默认地址
        xmppaddr = "http://192.168.9.104:5222";
        
        // 深圳hosting环境
        // portalurl = "http://119.145.9.215:18080/Portal/servlet/";
        // liveupdateurl = "http://119.145.9.215:28080";
        
        // 南京N5环境
        //        portalurl = "http://221.226.48.130:2137/Portal/servlet/";
        //        liveupdateurl = "http://192.168.1.1:5010/";
        
        // 北京公网环境
        portalurl = "http://123.125.97.217:18095/Portal/servlet/";
        liveupdateurl = "http://123.125.97.217:8080";
        
        // 香港Hosting环境
        //        portalurl = "http://202.55.9.41:8095/Portal/servlet/";
        //        liveupdateurl = "http://202.55.9.41:8084/";
    }
    
    private AASResult(Parcel source)
    {
        readFromParcel(source);
    }
    
    /**
     * 
     * <BR>
     * 
     * @return int
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents()
    {
        return 0;
    }
    
    /**
     * 
     * <BR>
     * 
     * @param dest Parcel
     * @param arg1 int
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        
        dest.writeString(result);
        dest.writeString(desc);
        dest.writeString(userSysId);
        dest.writeString(loginid);
        dest.writeString(token);
        dest.writeString(expiretime);
        dest.writeString(userID);
        dest.writeString(nduid);
        dest.writeString(language);
        dest.writeString(loginfirsttime);
        dest.writeString(xmppaddr);
        dest.writeString(svnlist);
        dest.writeString(svnuser);
        dest.writeString(svnpwd);
        dest.writeString(cabsyncmlurl);
        dest.writeString(cabgroupurl);
        dest.writeString(portalurl);
        dest.writeString(liveupdateurl);
        dest.writeString(rifurl);
        dest.writeString(boshurl);
        dest.writeString(emailurl);
        dest.writeString(mbportalurl);
        dest.writeString(mbplaturl);
        dest.writeString(popupminiwindow);
    }
    
    /**
     * 
     * 从Parcel读取数据<BR>
     * 
     * @param source Parcel
     */
    public void readFromParcel(Parcel source)
    {
        result = source.readString();
        desc = source.readString();
        userSysId = source.readString();
        loginid = source.readString();
        token = source.readString();
        expiretime = source.readString();
        userID = source.readString();
        nduid = source.readString();
        language = source.readString();
        loginfirsttime = source.readString();
        xmppaddr = source.readString();
        svnlist = source.readString();
        svnuser = source.readString();
        svnpwd = source.readString();
        cabsyncmlurl = source.readString();
        cabgroupurl = source.readString();
        portalurl = source.readString();
        liveupdateurl = source.readString();
        rifurl = source.readString();
        boshurl = source.readString();
        emailurl = source.readString();
        mbportalurl = source.readString();
        mbplaturl = source.readString();
        popupminiwindow = source.readString();
    }
    
    /**
     * get result
     * @return the result
     */
    public String getResult()
    {
        return result;
    }
    
    /**
     * set result
     * @param result the result to set
     */
    public void setResult(String result)
    {
        this.result = result;
    }
    
    /**
     * get desc
     * @return the desc
     */
    public String getDesc()
    {
        return desc;
    }
    
    /**
     * set desc
     * @param desc the desc to set
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    
    /**
     * get userSysId
     * @return the userSysId
     */
    public String getUserSysId()
    {
        return userSysId;
    }
    
    /**
     * set userSysId
     * @param userSysId the userSysId to set
     */
    public void setUserSysId(String userSysId)
    {
        this.userSysId = userSysId;
    }
    
    /**
     * get loginid
     * @return the loginid
     */
    public String getLoginid()
    {
        return loginid;
    }
    
    /**
     * set loginid
     * @param loginid the loginid to set
     */
    public void setLoginid(String loginid)
    {
        this.loginid = loginid;
    }
    
    /**
     * get token
     * @return the token
     */
    public String getToken()
    {
        return token;
    }
    
    /**
     * set token
     * @param token the token to set
     */
    public void setToken(String token)
    {
        this.token = token;
    }
    
    /**
     * get expiretime
     * @return the expiretime
     */
    public String getExpiretime()
    {
        return expiretime;
    }
    
    /**
     * set expiretime
     * @param expiretime the expiretime to set
     */
    public void setExpiretime(String expiretime)
    {
        this.expiretime = expiretime;
    }
    
    /**
     * get userID 用户业务ID
     * @return the userID
     */
    public String getUserID()
    {
        return userID;
    }
    
    /**
     * set userID
     * @param userID the userID to set
     */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    
    /**
     * get nduid
     * @return the nduid
     */
    public String getNduid()
    {
        return nduid;
    }
    
    /**
     * set nduid
     * @param nduid the nduid to set
     */
    public void setNduid(String nduid)
    {
        this.nduid = nduid;
    }
    
    /**
     * get language
     * @return the language
     */
    public String getLanguage()
    {
        return language;
    }
    
    /**
     * set language
     * @param language the language to set
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }
    
    /**
     * get loginfirsttime
     * @return the loginfirsttime
     */
    public String getLoginfirsttime()
    {
        return loginfirsttime;
    }
    
    /**
     * set loginfirsttime
     * @param loginfirsttime the loginfirsttime to set
     */
    public void setLoginfirsttime(String loginfirsttime)
    {
        this.loginfirsttime = loginfirsttime;
    }
    
    /**
     * get xmppaddr
     * @return the xmppaddr
     */
    public String getXmppaddr()
    {
        return xmppaddr;
    }
    
    /**
     * set xmppaddr
     * @param xmppaddr the xmppaddr to set
     */
    public void setXmppaddr(String xmppaddr)
    {
        this.xmppaddr = xmppaddr;
    }
    
    /**
     * get svnlist
     * @return the svnlist
     */
    public String getSvnlist()
    {
        return svnlist;
    }
    
    /**
     * set svnlist
     * @param svnlist the svnlist to set
     */
    public void setSvnlist(String svnlist)
    {
        this.svnlist = svnlist;
    }
    
    /**
     * get svnuser
     * @return the svnuser
     */
    public String getSvnuser()
    {
        return svnuser;
    }
    
    /**
     * set svnuser
     * @param svnuser the svnuser to set
     */
    public void setSvnuser(String svnuser)
    {
        this.svnuser = svnuser;
    }
    
    /**
     * get svnpwd
     * @return the svnpwd
     */
    public String getSvnpwd()
    {
        return svnpwd;
    }
    
    /**
     * set svnpwd
     * @param svnpwd the svnpwd to set
     */
    public void setSvnpwd(String svnpwd)
    {
        this.svnpwd = svnpwd;
    }
    
    /**
     * get cabsyncmlurl
     * @return the cabsyncmlurl
     */
    public String getCabsyncmlurl()
    {
        return cabsyncmlurl;
    }
    
    /**
     * set cabsyncmlurl
     * @param cabsyncmlurl the cabsyncmlurl to set
     */
    public void setCabsyncmlurl(String cabsyncmlurl)
    {
        this.cabsyncmlurl = cabsyncmlurl;
    }
    
    /**
     * get cabgroupurl
     * @return the cabgroupurl
     */
    public String getCabgroupurl()
    {
        return cabgroupurl;
    }
    
    /**
     * set cabgroupurl
     * @param cabgroupurl the cabgroupurl to set
     */
    public void setCabgroupurl(String cabgroupurl)
    {
        this.cabgroupurl = cabgroupurl;
    }
    
    //    /**
    //     * set appupdateurl
    //     * @param appupdateurl the appupdateurl to set
    //     */
    //    public void setAppupdateurl(String appupdateurl)
    //    {
    //        this.appupdateurl = appupdateurl;
    //    }
    //    
    //    /**
    //     * get userSysId
    //     * @return the userSysId
    //     */
    //    public String getAppupdateurl()
    //    {
    //        return appupdateurl;
    //    }
    
    /**
     * get portalurl
     * @return the portalurl
     */
    public String getPortalurl()
    {
        return portalurl;
    }
    
    /**
     * set portalurl
     * @param portalurl the portalurl to set
     */
    public void setPortalurl(String portalurl)
    {
        this.portalurl = portalurl;
    }
    
    /**
     * get liveupdateurl
     * @return the liveupdateurl
     */
    public String getLiveupdateurl()
    {
        return liveupdateurl;
    }
    
    /**
     * set liveupdateurl
     * @param liveupdateurl the liveupdateurl to set
     */
    public void setLiveupdateurl(String liveupdateurl)
    {
        this.liveupdateurl = liveupdateurl;
    }
    
    /**
     * get rifurl
     * @return the rifurl
     */
    public String getRifurl()
    {
        return rifurl;
    }
    
    /**
     * set rifurl
     * @param rifurl the rifurl to set
     */
    public void setRifurl(String rifurl)
    {
        this.rifurl = rifurl;
    }
    
    /**
     * get boshurl
     * @return the boshurl
     */
    public String getBoshurl()
    {
        return boshurl;
    }
    
    /**
     * set boshurl
     * @param boshurl the boshurl to set
     */
    public void setBoshurl(String boshurl)
    {
        this.boshurl = boshurl;
    }
    
    /**
     * get emailurl
     * @return the emailurl
     */
    public String getEmailurl()
    {
        return emailurl;
    }
    
    /**
     * set emailurl
     * @param emailurl the emailurl to set
     */
    public void setEmailurl(String emailurl)
    {
        this.emailurl = emailurl;
    }
    
    /**
     * get mbportalurl
     * @return the mbportalurl
     */
    public String getMbportalurl()
    {
        return mbportalurl;
    }
    
    /**
     * set mbportalurl
     * @param mbportalurl the mbportalurl to set
     */
    public void setMbportalurl(String mbportalurl)
    {
        this.mbportalurl = mbportalurl;
    }
    
    /**
     * get mbplaturl
     * @return the mbplaturl
     */
    public String getMbplaturl()
    {
        return mbplaturl;
    }
    
    /**
     * set mbplaturl
     * @param mbplaturl the mbplaturl to set
     */
    public void setMbplaturl(String mbplaturl)
    {
        this.mbplaturl = mbplaturl;
    }
    
    /**
     * get popupminiwindow
     * @return the popupminiwindow
     */
    public String getPopupminiwindow()
    {
        return popupminiwindow;
    }
    
    /**
     * set popupminiwindow
     * @param popupminiwindow the popupminiwindow to set
     */
    public void setPopupminiwindow(String popupminiwindow)
    {
        this.popupminiwindow = popupminiwindow;
    }
    
    /**
     * 
     * [编码字符串]<BR>
     * [功能详细描述]
     * 
     * @return 编码字符串
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "Userid[" + this.userSysId + "]userID[" + userID + "]loginid["
                + loginid + "]token[" + token + "]";
    }
    
}
