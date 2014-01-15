/*
 * 文件名: RegisterXmppMng.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl.xmpp;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.RegisterData;
import com.huawei.basic.android.im.component.net.xmpp.data.RegisterNotification;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.service.core.ILoginXmppListener;
import com.huawei.basic.android.im.component.service.impl.IObserver;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.utils.UriUtil;
import com.huawei.fast.IEngineBridge;

/**
 * XMPP注册相关服务管理类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-11]
 */
public class RegisterXmppMng extends XmppMng
{
    private ILoginXmppListener mLoginXmppListener;
    
    /**
     * XMPP注册模块管理类构造函数
     * @param engineBridge .so bridge
     * @param observer  IObserver
     * @param loginXmppListener  ILoginXmppListener
     */
    public RegisterXmppMng(IEngineBridge engineBridge, IObserver observer,
            ILoginXmppListener loginXmppListener)
    {
        super(engineBridge, observer);
        mLoginXmppListener = loginXmppListener;
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.impl.xmpp.XmppMng#handleNotification(java.lang.String, int, java.lang.String)
     */
    @Override
    public void handleNotification(String componentID, int notifyID, String data)
    {
        RegisterNotification regNtf = parseData(RegisterNotification.class,
                data);
        if (regNtf != null)
        {
            switch (notifyID)
            {
            
            // 注册消息
                case BaseParams.RegisterParams.FAST_REGISTER_NTF_REGISTER:
                    handleRegisterNtf(regNtf);
                    break;
                
                // 注销消息
                case BaseParams.RegisterParams.FAST_REGISTER_NTF_DEREGISTER:
                    handleDeRegisterNtf(regNtf);
                    break;
                default:
                    Logger.e(TAG, "unknown notify id, notifyID=" + notifyID);
                    break;
            }
        }
    }
    
    /**
     * <BR>
     * 
     * @return String
     * @see com.huawei.basic.android.im.service.impl.xmpp.XmppMng#getComponentId()
     */
    
    @Override
    protected String getComponentId()
    {
        return BaseParams.RegisterParams.FAST_COM_REGISTER_ID;
    }
    
    /**
     * 订阅XMPP注册/注销<BR>
     * 
     * @see com.huawei.basic.android.im.service.impl.xmpp.XmppMng#subNotify()
     */
    
    @Override
    protected void subNotify()
    {
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.RegisterParams.FAST_REGISTER_NTF_REGISTER);
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.RegisterParams.FAST_REGISTER_NTF_DEREGISTER);
    }
    
    /**
     * 
     * 登录后注册XMPP<BR>
     * [功能详细描述]
     * @param aasResult  AASResult
     */
    public void registerXmpp(AASResult aasResult)
    {
        String token = aasResult.getToken();
        // 理论上是使用WoYouID @ chinaunicom 直接登陆的
        String jid = UriUtil.buildXmppJid(aasResult.getUserID());
        RegisterData.RegisterCmdData rd = new RegisterData.RegisterCmdData();
        
        rd.setClientType(RegisterData.RegisterCmdData.ClientType.MOBILE);
        rd.setJid(jid);
        rd.setMaxHeartbeatCount("3");
        String xmppUrl = "http://" + aasResult.getXmppaddr();
        
        String[] strs = UriUtil.resolveHttpUrl(xmppUrl);
        
        rd.setServerIp(strs[0]);
        rd.setServerPort(strs[1]);
        rd.setAuthToken(token);
        rd.setUseTls(false);
        rd.setUseZlib(false);
        
        String cmdData = rd.makeCmdData();
        getEngineBridge().executeCommand(BaseParams.RegisterParams.FAST_COM_REGISTER_ID,
                BaseParams.RegisterParams.FAST_REGISTER_CMD_REGISTER,
                cmdData);
    }
    
    /**
     * 
     * 注销XMPP<BR>
     * [功能详细描述]
     * @param userID 用户业务ID
     */
    public void deregister(String userID)
    {
        RegisterData.DeregisterCmdData dcd = new RegisterData.DeregisterCmdData();
        dcd.setJid(UriUtil.buildXmppJid(userID));
        
        String cmdData = dcd.makeCmdData();
        
        getEngineBridge().executeCommand(BaseParams.RegisterParams.FAST_COM_REGISTER_ID,
                BaseParams.RegisterParams.FAST_REGISTER_CMD_DEREGISTER,
                cmdData);
    }
    
    /**
     * 处理注册通知消息
     * <BR>
     * 
     * @param regNtf RegisterNotification
     */
    private void handleRegisterNtf(RegisterNotification regNtf)
    {
        int iErrorCode = regNtf.getErrorCode();
        // 做必要的处理，并回调到IXmpp.
        if (!handleCommonNotification(regNtf))
        {
            // XMPP服务器返回码
            Logger.i(TAG, "XMPP register result code : " + iErrorCode);
            
            switch (iErrorCode)
            {
            // 鉴权失败，将AASResult中TOKEN置为空   // TODO:
                case XmppResultCode.Register.FAST_ERR_NOT_AUTHORIZED:
                    break;
                case XmppResultCode.Register.FAST_ERR_HEARTBEAT_TIMEOUT:
                    break;
                case XmppResultCode.Register.FAST_ERR_NOT_REGISTERED:
                    break;
                default:
                    Logger.e(TAG, "xmpp register unknown error code : "
                            + iErrorCode);
                    break;
            }
        }
        mLoginXmppListener.xmppRegister(iErrorCode);
    }
    
    /**
     * 处理注销、下线等de-register通知消息
     * <BR>
     * 
     * @param regNtf RegisterNotification
     */
    private void handleDeRegisterNtf(RegisterNotification regNtf)
    {
        int iErrorCode = regNtf.getErrorCode();
        // 做必要的处理，并回调到IXmpp.
        if (!handleCommonNotification(regNtf))
        {
            switch (iErrorCode)
            {
                case XmppResultCode.Register.FAST_ERR_NOT_REGISTERED:
                    break;
                case XmppResultCode.Register.FAST_ERR_NOT_AUTHORIZED:
                    break;
                case XmppResultCode.Register.FAST_ERR_CONFLICT:
                    break;
                case XmppResultCode.Register.FAST_ERR_HEARTBEAT_TIMEOUT:
                    break;
                default:
                    Logger.e(TAG, "xmpp deregister unknown error code : "
                            + iErrorCode);
                    break;
            }
        }
        mLoginXmppListener.xmppDeregister(iErrorCode);
    }
}
