/*
 * 文件名: AccountModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周雪松
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;

/**
 * 客户端上登录过的帐号信息<BR>
 * [功能详细描述]
 * @author 周雪松
 * @version [RCS Client V100R001C03, Feb 15, 2012]
 */
public class AccountModel implements Serializable
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 登录帐号（可以是即时通信业务ID，邮箱或者手机号码）
     * 
     */
    private String loginAccount;
    
    /**
     * 上次登录的时间戳；格式：YYYYMMDDHHMMSS
     * 
     */
    private String timestamp;
    
    /**
     * 登录密码， 使用SHA256算法加密的串:
     * SHA256（user+密码），SHA256要求最后转成16进制大写字符串
     * 
     */
    private String password;
    
    /**
     * 自动登录: false： 不自动登录 true： 自动登录
     */
    private boolean autoLogin;
    
    /**
     * 上一次登录的状态： <br>
     * 0：登录成功 <br>
     * 1：登录失败，用户名/密码鉴权不通过 <br>
     * 2：登录失败，服务端响应超时 <br>
     * 注：状态可根据实际需求，进行扩展
     * 
     */
    private int lastStatus;
    
    /**
     * 用户在沃友系统的唯一标识，服务器返回
     * 
     */
    private String userSysId;
    
    /**
     * 注册时服务器分配的用户ID，即业务ID， USER_ID也是用户账号（userAccount）的一种格式
     * 
     */
    private String userId;
    
    /**
     * 用户登录后呈现的状态<br>
     * online:在线 <br>
     * invisible:隐身 <br>
     * chat:希望进行聊天 <br>
     * away:临时离开<br>
     * xa:离开一段时间 <br>
     * dnd:免打扰 <br>
     * offline:离线<br>
     * (请参考文档)
     * 
     */
    private String userStatus;
    
    /**
     * 用户绑定的手机号
     */
    private String bindMobile;
    
    /**
     * 用户绑定的Email地址
     */
    private String bindEmail;
    
    /**
     * 默认构造方法
     */
    public AccountModel()
    {
    }
    
    public String getLoginAccount()
    {
        return loginAccount;
    }
    
    public void setLoginAccount(String loginAccount)
    {
        this.loginAccount = loginAccount;
    }
    
    public String getTimestamp()
    {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public boolean getAutoLogin()
    {
        return autoLogin;
    }
    
    public void setAutoLogin(boolean autoLogin)
    {
        this.autoLogin = autoLogin;
    }
    
    public int getLastStatus()
    {
        return lastStatus;
    }
    
    public void setLastStatus(int lastStatus)
    {
        this.lastStatus = lastStatus;
    }
    
    public String getUserSysId()
    {
        return userSysId;
    }
    
    public void setUserSysId(String userSysId)
    {
        this.userSysId = userSysId;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getUserStatus()
    {
        return userStatus;
    }
    
    public void setUserStatus(String userStatus)
    {
        this.userStatus = userStatus;
    }
    
    public String getBindMobile()
    {
        return bindMobile;
    }
    
    public void setBindMobile(String bindMobile)
    {
        this.bindMobile = bindMobile;
    }
    
    public String getBindEmail()
    {
        return bindEmail;
    }
    
    public void setBindEmail(String bindEmail)
    {
        this.bindEmail = bindEmail;
    }
    
    /**
     * 
     * 重写toString方法
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuffer("AccountModel [loginAccount=").append(loginAccount)
                .append(", timestamp=")
                .append(timestamp)
                .append(", password=")
                .append(password)
                .append(", autoLogin=")
                .append(autoLogin)
                .append(", lastStatus=")
                .append(lastStatus)
                .append(", userSysId=")
                .append(userSysId)
                .append(", userId=")
                .append(userId)
                .append(", userStatus=")
                .append(userStatus)
                .append(", bindMobile=")
                .append(bindMobile)
                .append(", bindEmail=")
                .append(bindEmail)
                .append("]")
                .toString();
    }
}
