/*
 * 文件名: RetrieveAdapterListener.java
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
 * 恢复通话监听器
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public interface RetrieveAdapterListener extends BaseAdapterListener
{
    /**
     * 恢复通话失败回调
     * @param callId
     *      通话id
     */
    void onRetrieveFailure(int callId);
    
    /**
     * 恢复通话成功回调
     * @param talkingNotifyBean
     *      解析封装的对象
     */
    void onRetrieved(TalkingNotifyBean talkingNotifyBean);
    
}
