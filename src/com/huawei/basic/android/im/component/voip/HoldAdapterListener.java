/*
 * 文件名: ICallSessionListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 12, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

/**
 * 保持通话监听器
 */
public interface HoldAdapterListener extends BaseAdapterListener
{
    /**
     * 保持通话成功回调
     * @param callId
     *      通话id
     */
    void onHolding(int callId);
    
    /**
     * 保持通话失败回调
     * @param callId
     *      通话id
     */
    void onHoldFailure(int callId);
    
}
