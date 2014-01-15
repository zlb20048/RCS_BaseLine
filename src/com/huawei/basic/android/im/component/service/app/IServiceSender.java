/*
 * 文件名: IServiceSender.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.app;

/**
 * 与service交互的发送接口定义<BR>
 * 
 * @author 刘鲁宁
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 13, 2012]
 */
public interface IServiceSender
{
    
    //    /**
    //     * 
    //     * 起service<BR>
    //     * 
    //     * @param context Context
    //     */
    //    void startService(Context context);
    
    /**
     * 添加service的登录模块监听器 <BR>
     * 
     * @param serviceListener
     *            ILoginServiceListener
     */
    void addLoginServiceListener(ILoginServiceListener serviceListener);
    
    /**
     * 
     * 添加service的XMPP模块监听器<BR>
     * 
     * @param serviceListener
     *            IXmppServiceListener
     */
    void addXmppServiceListener(IXmppServiceListener serviceListener);
    
    /**
     * 
     * 移除登录模块监听器<BR>
     * 
     * @param serviceListener
     *            ILoginServiceListener
     */
    void removeLoginServiceListener(ILoginServiceListener serviceListener);
    
    /**
     * 
     * 移除XMPP模块监听器<BR>
     * 
     * @param serviceListener
     *            IXmppServiceListener
     */
    void removeXmppServiceListener(IXmppServiceListener serviceListener);
    
    /**
     * 
     * 登录<BR>
     * 
     * @param account
     *            账号
     * @param password
     *            密码
     * @param verifyCode
     *            验证码，可为空
     * @param clientVersion
     *            客户端版本号
     */
    void login(String account, String password, String verifyCode,
            String clientVersion);
    
    /**
     * 
     * 用户登出<BR>
     * 
     * @param userID
     *            用户业务ID
     * @param userSysID
     *            用户系统ID
     * @param token
     *            TOKEN
     * @param loginID
     *            本次登录ID
     */
    void logout(String userID, String userSysID, String token, String loginID);
    
    /**
     * 
     * 定期刷新TOKEN<BR>
     * 
     * @param userSysID
     *            用户系统ID
     * @param token
     *            TOKEN
     */
    void refreshToken(String userSysID, String token);
    
    /**
     * 停止刷新TOKEN <BR>
     * 
     */
    void stopRefreshToken();
    
    /**
     * XMPP订阅服务 <BR>
     * 
     * @param comId
     *            组件id
     * @param notifyId
     *            服务id
     * @return 是否订阅成功，0为订阅成功
     */
    int subNotify(String comId, int notifyId);
    
    /**
     * XMPP注销服务 <BR>
     * 
     * @param comId
     *            组件id
     * @param notifyId
     *            服务id
     * @return 是否注销服务成功，0为成功
     */
    int unSubNotify(String comId, int notifyId);
    
    /**
     * 执行XMPP命令 <BR>
     * 
     * @param comId
     *            组件id
     * @param cmdId
     *            服务id
     * @param data
     *            执行参数
     * @return 执行命令的结果，“0”表示成功
     */
    String executeCommand(String comId, int cmdId, String data);
    
    /**
     * 
     * 自动登录
     * 
     * @param delay
     *            延时
     */
    void login(long delay);
    
    /**
     * 
     * 发送网络状态
     * 
     * @param stauts
     *            网络状态
     */
    void sendNetMessage(int stauts);
    
    /**
     * 请求登录信息 <BR>
     * 
     * 
     */
    void requestLoginMessage();
}
