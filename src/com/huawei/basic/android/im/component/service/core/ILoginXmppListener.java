/*
 * 文件名: ILoginXmppListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.core;

/**
 * 登录模块的XMPP注册/注销服务的监听器定义<BR>
 * 用户登录成功后需要注册XMPP服务，XMPP服务器推送消息后，<BR>
 * 登录模块需要对注册成功与否等情况进行其他处理，可以在本接口定义的方法中进行实现。
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-14] 
 */
public interface ILoginXmppListener
{
    
    /**
     * 
     * XMPP注册回调
     * @param errorCode Error Code
     */
    void xmppRegister(int errorCode);
    
    /**
     * XMPP注销回调
     *@param errorCode Error Code
     */
    void xmppDeregister(int errorCode);
}
