/*
 * 文件名: ViewNotificationEntity.java
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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * 自定义视图通知栏实体类<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 16, 2012] 
 */
public abstract class ViewNotificationEntity extends NotificationEntity
{
    /**
     * 构造方法<BR>
     * @param icon 通知栏图标
     * @param tickerText 通知栏弹出信息
     */
    public ViewNotificationEntity(int icon, CharSequence tickerText)
    {
        super(icon, tickerText);
    }
    
    /**
     * 初始化<BR>
     * @param context 上下文
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#init(android.content.Context)
     */
    protected void init(Context context)
    {
        super.init(context);
        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                getLayoutId());
        int progressBarId = getProgressBarId();
        if (progressBarId > -1)
        {
            contentView.setProgressBar(progressBarId, 100, 1, false);
        }
        getNotification().contentView = contentView;
    }
    
    /**
     * 添加view的点击监听<BR>
     * 如果通知栏有需要监听的元素，通过此方法添加监听
     * @param viewId 响应点击事件的布局中的元素ID
     */
    protected final void addClickEvent(int viewId)
    {
        Intent intent = new Intent(FLAG_INTENT_ACTION);
        intent.putExtra(FLAG_NOTIFICATION_KEY, getKey());
        intent.putExtra(FLAG_NOTIFICATION_TYPE, TYPE_NOTIFICATION_CLICK);
        intent.putExtra(FLAG_NOTIFICATION_ACTION, viewId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(super.getContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        getNotification().contentView.setOnClickPendingIntent(viewId,
                pendingIntent);
    }
    
    /**
     * 
     * 通知栏点击跳转intent<BR>
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#setActivityPendingIntent()
     */
    protected final void setActivityPendingIntent()
    {
        super.setActivityPendingIntent();
    }
    
    /**
     * 初始化通知栏布局元素<BR>
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#initContentInfo()
     */
    protected final void initContentInfo()
    {
        initContentView(getNotification().contentView);
    }
    
    /**
     * 更新通知栏<BR>
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#updateContentInfo()
     */
    protected final void updateContentInfo()
    {
        updateContentView(getNotification().contentView);
    }
    
    /**
     * 获得通知栏的状态标志<BR>
     * @return 返回通知栏的状态<BR>
     * FLAG_AUTO_CANCEL:点击通知栏后清除通知
     */
    protected int getFlags()
    {
        return Notification.FLAG_AUTO_CANCEL;
    }
    
    /**
     * 获得通知栏布局中的进度条ID<BR>
     * @return 通知栏布局中的进度条ID
     */
    protected int getProgressBarId()
    {
        return -1;
    }
    
    /**
     * 设置通知栏的布局文件ID<BR>
     * @return 通知栏的布局文件ID
     */
    protected abstract int getLayoutId();
    
    /**
     * 初始化通知栏布局<BR>
     * @param contentView RemoteViews
     */
    protected abstract void initContentView(RemoteViews contentView);
    
    /**
     * 
     * 更新通知栏<BR>
     * @param contentView RemoteViews对象
     */
    protected abstract void updateContentView(RemoteViews contentView);
    
}
