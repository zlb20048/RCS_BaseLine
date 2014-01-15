/*
 * 文件名: BaseAdapterListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 监听器基类
 * 创建人: 刘鲁宁
 * 创建时间:Mar 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

/**
 * 监听器基类
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public abstract interface BaseAdapterListener
{
    /**
     * 结束通话通知回调方法
     * @param callId
     *      通话id
     * @param reason
     *      通话结束的原因
     * @param repCode
     *      invitey请求的非2XX响应码
     */
    void onClosed(int callId, String reason, int repCode);
}
