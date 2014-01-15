/*
 * 文件名: INotificationEntityListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Apr 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.notification;

/**
 * 通知栏按钮事件监听<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 16, 2012] 
 */
public interface INotificationEntityListener
{
    
    /**
     * 通知栏按钮点击事件<BR>
     * @param viewId 布局文件中元素ID
     * @param notificationEntity NotificationEntity 对象
     */
    void onClick(int viewId, NotificationEntity notificationEntity);
    
}
