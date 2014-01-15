/*
 * 文件名: IXmppServiceListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.app;

/**
 * XMPP模块service监听器<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-14] 
 */
public interface IXmppServiceListener
{
    
    /**
     * 
     * XMPP发送消息<BR>
     * 
     * @param messageType 消息类型
     * @param result 结果
     */
    void sendXmppMessage(int messageType, String result);
    
    /**
     * 
     * XMPP服务回调方法<BR>
     * //TODO:是否需要该接口，一般APP层不会直接处理XMPP回调消息
     *
     * @param componentID 组件id
     * @param notifyID   订阅ID
     * @param data 数据
     */
    void xmppCallback(String componentID, int notifyID, String data);
}
