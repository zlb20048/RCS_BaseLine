/*
 * 文件名: CallBackEventListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

import com.huawei.fast.voip.bean.AlertingNotifyBean;

/**
 * 通话被动回调事件监听器
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public abstract class CallBackEventListener
{
   /**
    * 
    *  onRegState
    * @param regId INT 
    * @param regState string 
    * @param event string 
    * @param retryAfter INT 
    */
    protected void onRegState(int regId, String regState, String event,
            int retryAfter)
    {
    }

    /**
     * 被叫方结束通话回调方法
     * @param callId
     *      通话id
     * @param reason
     *      通话结束的原因
     * @param repCode
     *      invitey请求的非2XX响应码
     */
    protected abstract void onClosed(int callId, String reason, int repCode);
    
    /**
     * 被叫振铃回调方法
     * @param alertingNotifyBean
     *      封装解析后的通知对象
     */
    protected abstract void onAlerting(AlertingNotifyBean alertingNotifyBean);
    
    /**
     *  被叫方等待通知回调方法
     * @param alertingNotifyBean
     *      封装解析后的通知对象
     */
    protected abstract void onQueue(AlertingNotifyBean alertingNotifyBean);
    
    /**
     * 被叫方被保持回调方法
     * @param callId
     *      通话Id
     */
    protected abstract void onHeld(int callId);
    
    /**
     * 注册状态回调
     * @param regId
     *      注册ID
     * @param regState
     *      注册状态
     * @param event
     *      注册状态改变的原因
     */
    protected abstract void onRegState(int regId, String regState, String event);
}
