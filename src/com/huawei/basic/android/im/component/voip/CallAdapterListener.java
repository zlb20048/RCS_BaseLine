/*
 * 文件名: CallAdapterListener.java
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

import com.huawei.fast.voip.bean.TalkingNotifyBean;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public interface CallAdapterListener extends BaseAdapterListener
{
    /**
     * 呼叫被等待回调方法
     * @param callId
     *      通话id
     * @param remoteUri
     *      对方的通话Uri  
     */
    void onQueued(int callId, String remoteUri);
    
    /**
     * 被叫振铃通知回调方法
     * @param callId
     *      通话id
     * @param remoteUri
     *      对方的通话Uri  
     */
    void onRinging(int callId, String remoteUri);
    
    /**
     * 开始通话通知回调方法
     * @param talkingNotifyBean
     *      封装解析后的通知
     */
    void onTalking(TalkingNotifyBean talkingNotifyBean);
    
}
