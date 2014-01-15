/*
 * 文件名: ILogin.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.core;

/**
 * 存在于服务中的登录处理接口<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-10]
 */
public interface ILogin extends ILoginXmppListener
{
    
    /**
     * 
     * 登录服务的初始化<BR>
     * 
     * @return 初始化是否成功
     */
    boolean init();
    
    /**
     * 
     * 登录<BR>
     * 登录成功后通过loginSuccessCallback返回AASResult对象
     * 
     * @param account
     *            账号
     * @param password
     *            密码
     * @param verifyCode
     *            验证码
     * @param clientVersion
     *            客户端版本号
     */
    void login(String account, String password, String verifyCode,
            String clientVersion);
    
    /**
     * 
     * 登出、注销<BR>
     * 
     * @param userID
     *            用户ID
     * @param userSysID
     *            用户系统ID
     * @param token
     *            TOKEN
     * @param loginID
     *            本次登录ID
     */
    void logout(String userID, String userSysID, String token, String loginID);
    
    /**
     * 刷新TOKEN <BR>
     * 刷新成功后返回TOKEN字符串
     * 
     * @param userSysID
     *            用户系统ID
     * @param token
     *            之前的TOKEN
     */
    void refreshToken(String userSysID, String token);
    
    /**
     * 停止刷新TOKEN <BR>
     * 
     */
    void stopRefreshToken();
    
    /**
     * 请求登录信息 <BR>
     * 
     * 
     */
    void requestLoginMessage();
    
    /**
     * 
     * 登录<BR>
     * 
     * 
     * @param delay
     *            延时后登录 登录成功后通过loginSuccessCallback返回AASResult对象
     */
    void login(long delay);
    
    /**
     * 
     * 发送网络状态
     * 
     * @param status
     *            网络状态
     */
    void sendNetMessage(int status);
    
    /**
     * 
     * Service启动通知
     * 
     *
     */
    void serviceStart();
    
    /**
     * 
     * Service独立运行时需要处理的事件
     * 
     * 
     * @param messageType 消息类型
     * @param result 数据
     */
    void serviceProcess(int messageType, String result);
}
