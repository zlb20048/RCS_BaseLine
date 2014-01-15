/*
 * 文件名: FusionConfig.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-2-15
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.common;

import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.utils.Base64Util;

/**
 * 全局配置类，单例模式<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-15] 
 */
public class FusionConfig
{
    /**
     * TAG
     */
    private static final String TAG = "FusionConfig";
    
    /**
     *  单例对象
     */
    private static FusionConfig sMe = new FusionConfig();
    
    private AASResult aasResult;
    
    /**
     * 用户状态
     */
    private int userStatus = LoginMessageType.STATUS_OFFLINE;
    
    /**
     * CAB请求时鉴权字符串
     */
    private String cabReqAuthorization;
    
    /**
     * OSE请求时鉴权字符串
     */
    private String oseReqAuthorization;
    
    /**
     * 客户端版本号
     */
    private String clientVersion;
    
    /**
     * sip服务器默认值
     */
    private String voipServer = "203.86.17.44";
    
    /**
     * sip域默认值
     */
    private String voipDomain = "203.86.17.44";
    
    /**
     * sip端口默认值
     */
    private int voipPort = 5060;
    
    /**
     * 私有化构造器
     */
    private FusionConfig()
    {
    }
    
    /**
     * sip服务器默认值
     * @return the voipServer
     */
    public String getVoipServer()
    {
        return voipServer;
    }
    
    /**
     * sip服务器默认值
     * @param voipserver the voipServer to set
     */
    public void setVoipServer(String voipserver)
    {
        voipServer = voipserver;
    }
    
    /**
     * sip域默认值
     * @return the voipDomain
     */
    public String getVoipDomain()
    {
        return voipDomain;
    }
    
    /**
     * sip域默认值
     * @param voipdomain the voipDomain to set
     */
    public void setVoipDomain(String voipdomain)
    {
        voipDomain = voipdomain;
    }
    
    /**
     * sip端口默认值
     * @return the voipPort
     */
    public int getVoipPort()
    {
        return voipPort;
    }
    
    /**
     * sip端口默认值
     * @param voipport the voipPort to set
     */
    public void setVoipPort(int voipport)
    {
        voipPort = voipport;
    }
    
    /**
     *  获取单例的 FusionConfig对象
     * @return FusionConfig对象
     */
    public static FusionConfig getInstance()
    {
        return sMe;
    }
    
    /**
     * AAs登陆以后返回数据
     * @return the aasResult
     */
    public AASResult getAasResult()
    {
        if (aasResult == null)
        {
            Logger.e(TAG,
                    "getAasResult() -> aasResult is null, create a new AASResult instance() "
                            + "to avoid NullPointerException.");
            aasResult = new AASResult();
        }
        return aasResult;
    }
    
    /**
     * AAs登陆以后返回数据 生成HTTP请求的鉴权字段
     * @param aasResult the aasResult to set
     */
    public void setAasResult(AASResult aasResult)
    {
        this.aasResult = aasResult;
        refreshCabOseToken();
    }
    
    private void refreshCabOseToken()
    {
        if (aasResult != null)
        {
            cabReqAuthorization = "Basic "
                    + Base64Util.encode((aasResult.getUserSysId() + ":" + aasResult.getToken()).getBytes());
            oseReqAuthorization = "Basic "
                    + Base64Util.encode((aasResult.getUserID() + ":" + aasResult.getToken()).getBytes());
        }
        else
        {
            Logger.e(TAG, "setAasResult() -> aasResult is null");
        }
    }
    
    /**
     * set token
     * @param token the token to set
     */
    public void setToken(String token)
    {
        getAasResult().setToken(token);
        refreshCabOseToken();
    }
    
    /**
     * 获取用户状态
     * @return the userStatus
     */
    public int getUserStatus()
    {
        return userStatus;
    }
    
    /**
     * 修改用户状态
     * @param userStatus the userStatus to set
     */
    public void setUserStatus(int userStatus)
    {
        this.userStatus = userStatus;
    }
    
    /**
     * CAB请求时鉴权字符串
     * @return the cabReqAuthorization
     */
    public String getCabReqAuthorization()
    {
        return cabReqAuthorization;
    }
    
    /**
     * OSE请求时鉴权字符串
     * @return the oseReqAuthorization
     */
    public String getOseReqAuthorization()
    {
        return oseReqAuthorization;
    }
    
    /**
     * get clientVersion
     * @return the clientVersion
     */
    public String getClientVersion()
    {
        return clientVersion;
    }
    
    /**
     * set clientVersion
     * @param clientVersion the clientVersion to set
     */
    public void setClientVersion(String clientVersion)
    {
        this.clientVersion = clientVersion;
    }
    
}
