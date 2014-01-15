/*
 * 文件名: AnswerAdapterListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 回答操作适配监听器
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
 * 应答操作适配监听器<BR>
 * 用于监听应答操作时，回调事件的处理
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public interface AnswerAdapterListener extends BaseAdapterListener
{
    /**
     * 开始通话通知
     * @param talkingNotifyBean
     *      封装解析后的通知
     */
    void onTalking(TalkingNotifyBean talkingNotifyBean);
    
}
