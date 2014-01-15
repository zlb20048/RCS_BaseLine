/*
 * 文件名: IObserver.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-2-23
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl;

import android.content.Context;
import android.os.Handler;

import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.utils.DeferredHandler;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-23] 
 */
public interface IObserver
{
    
    /**
     * 
     * 发送XMPP消息<BR>
     * 
     * @param messageType 消息类型
     * @param result 数据
     */
    void sendXmppMessage(int messageType, String result);
    
    /**
     * 
     * 发送LOGIN消息<BR>
     * 
     * @param messageType 消息类型
     * @param result 数据
     */
    void sendLoginMessage(int messageType, String result);
    
    /**
     * 
     * 登录模块的回调函数<BR>
     * 
     * @param aasResult 错误码
     */
    void loginSuccessCallback(AASResult aasResult);
    
    /**
     * XMPP的回调
     * <BR>
     * 
     * @param componentID 组件ID
     * @param notifyID 订阅ID
     * @param data 返回的数据
     */
    void xmppCallback(String componentID, int notifyID, String data);
    
    /**
     * 获取Context上下文对象
     * <BR>
     * 
     * @return Context
     */
    Context getContext();
    
    /**
     * 获取Handler对象
     * <BR>
     * 
     * @return Handler
     */
    Handler getHandler();
    
    /**
     * 
     * 从资源文件获取指定id的字符串值<BR>
     * 
     * @param resID 资源id
     * @return 对应的字符串对象
     */
    String getString(int resID);
    
    /**
     * 获取当前登录用户的系统id
     * <BR>
     * 
     * @return 当前用户系统id
     */
    String getUserSysID();
    
    /**
     * 获取当前登录用户账号id
     * <BR>
     * 
     * @return 当前用户账号id
     */
    String getUserID();
    
    /**
     * 显示状态栏提示
     * <BR>
     * 
     * @param message 显示的消息
     */
    void showNotification(String message);
    
    /**
     * 
     * 显示提示信息<BR>
     * 
     * @param message 显示消息
     */
    void showPrompt(String message);
    
    /**
     * 
     *获取DeferredHandler<BR>
     * @return DeferredHandler
     */
    DeferredHandler getDeferredHandler();

    /**
     * 
     * 注册xmpp<BR>
     * 
     * @param aasResult AASResult
     */
    void registerXmpp(AASResult aasResult);
    
    /**
     * 
     * 注销xmpp<BR>
     * 
     * @param userID 用户业务ID
     */
    void deRegisterXmpp(String userID);
    
    /**
     * 执行命令 <BR>
     * 
     * @param comId 组件id
     * @param cmdId 命令id
     * @param data 执行命令需上传的数据
     * @return 执行命令的结果，“0”为成功
     */
    String executeCommand(String comId, int cmdId, String data);
    
    /**
     * 是否开启声音 <BR>
     * 
     * @return boolean 是否开启声音
     */
    boolean isSoundOpen();
}
