/*
 * 文件名: NotificationEntity.java
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

/**
 * NotificationEntity对象类<BR>
 * 定义通知栏对象的属性设置方法
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 16, 2012] 
 */
public abstract class NotificationEntity
{
    /**
     * 标志通知栏操作类型的key
     */
    public static final String FLAG_NOTIFICATION_TYPE = "flag_notification_type";
    
    /**
     * 处理通知栏按钮点击事件的广播需要注册的action字符串
     */
    protected static final String FLAG_INTENT_ACTION = "com.huawei.basic.android.notification";
    
    /**
     * 通知栏缓存集合的键
     */
    protected static final String FLAG_NOTIFICATION_KEY = "flag_notification_key";
    
    /**
     * 标志被点击view的 viewId 的 key
     */
    protected static final String FLAG_NOTIFICATION_ACTION = "flag_notification_action";
    
    /**
     * 通知栏 按钮点击标志
     */
    protected static final int TYPE_NOTIFICATION_CLICK = 1;
    
    /**
     * 通知栏 删除 标志
     */
    protected static final int TYPE_NOTIFICATION_DELETE = 2;
    
    /**
     * 通知栏ID 用于notify通知栏 通知栏唯一标识
     */
    private Integer id;
    
    /**
     * 通知栏的key 用于缓存和获取通知栏
     */
    private String key;
    
    /**
     * 上下文对象
     */
    private Context mContext;
    
    /**
     * Notification 对象
     */
    private Notification mNotification;
    
    /**
     * 通知栏按钮事件监听
     */
    private INotificationEntityListener mNotificationEntityListener;
    
    /**
     * 点击通知栏要跳转的intent
     */
    private Intent mActivityIntent = null;
    
    /**
     * 构造方法
     */
    public NotificationEntity()
    {
        mNotification = new Notification();
        mNotification.flags = getNotificationFlags();
        mNotification.when = System.currentTimeMillis();
        if (getNotificationSound() != -1)
        {
            mNotification.defaults |= getNotificationSound();
        }
    }
    
    /**
     * 初始化通知栏基本对象
     * @param icon 通知栏图标
     * @param tickerText 通知栏弹出标题
     */
    public NotificationEntity(int icon, CharSequence tickerText)
    {
        mNotification = new Notification(icon, tickerText,
                System.currentTimeMillis());
        mNotification.flags = getNotificationFlags();
        if (getNotificationSound() != -1)
        {
            mNotification.defaults |= getNotificationSound();
        }
    }
    
    /**
     * 初始化通知栏基本对象
     * @param icon 通知栏图标
     * @param tickerText 通知栏弹出标题
     * @param haveSound 是否有提示音
     */
    public NotificationEntity(int icon, CharSequence tickerText,
            boolean haveSound)
    {
        mNotification = new Notification(icon, tickerText,
                System.currentTimeMillis());
        mNotification.flags = getNotificationFlags();
        if (getNotificationSound() != -1 && haveSound)
        {
            mNotification.defaults |= getNotificationSound();
        }
    }
    
    /**
     * 获得NotificationEntityListener实例<BR>
     * @return NotificationEntityListener实例
     */
    public INotificationEntityListener getNotificationEntityListener()
    {
        return mNotificationEntityListener;
    }
    
    /**
     * 设置NotificationEntityListener
     * @param notificationEntityListener NotificationEntityListener实例
     */
    public void setNotificationEntityListener(
            INotificationEntityListener notificationEntityListener)
    {
        this.mNotificationEntityListener = notificationEntityListener;
    }
    
    /**
     * 初始化<BR>
     * @param context 上下文
     */
    protected void init(Context context)
    {
        this.mContext = context.getApplicationContext();
    }
    
    public final int getId()
    {
        return id;
    }
    
    protected final void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    protected int getNotificationFlags()
    {
        return Notification.FLAG_AUTO_CANCEL;
    }
    
    protected final Context getContext()
    {
        return mContext;
    }
    
    protected final Notification getNotification()
    {
        return mNotification;
    }
    
    /**
     * 设置通知栏点击跳转Intent<BR>
     */
    protected void setDeletePendingIntent()
    {
        Intent intent = new Intent(FLAG_INTENT_ACTION);
        intent.putExtra(FLAG_NOTIFICATION_KEY, getKey());
        intent.putExtra(FLAG_NOTIFICATION_TYPE, TYPE_NOTIFICATION_DELETE);
        mNotification.deleteIntent = PendingIntent.getBroadcast(mContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
    /**
     * 设置点击通知栏事件<BR>
     * 设置点击通知栏之后要跳转的Intent
     */
    protected void setActivityPendingIntent()
    {
        mNotification.contentIntent = getActivityPendingIntent();
    }
    
    /**
     * 获得通知栏点击时候的pendingIntent<BR>
     * remoteView 中对应的contenIntent
     * @return PendingIntent对象
     */
    protected final PendingIntent getActivityPendingIntent()
    {
        PendingIntent intent = PendingIntent.getActivity(mContext,
                0,
                getActivityIntent(),
                getPentingIntentFlags());
        return intent;
    }
    
    /**
     * 
     *  获得PendingIntent的类型<BR>
     * [功能详细描述]
     * @return PendingIntent的类型
     */
    protected int getPentingIntentFlags()
    {
        return PendingIntent.FLAG_UPDATE_CURRENT;
    }
    
    /**
     * 
     * 获得通知栏的声音<BR>
     * @return 通知栏声音ID
     */
    protected int getNotificationSound()
    {
        return Notification.DEFAULT_SOUND;
    }
    
    /**
     * 通知栏创建之前调用方法<BR>
     */
    protected final void breforeCreated()
    {
        onBreforeCreated();
    }
    
    /**
     * 通知栏创建之后调用<BR>
     */
    protected final void afterCreated()
    {
        onAfterCreated();
        
    }
    
    /**
     * 通知栏更新之前调用<BR>
     */
    protected final void beforeUpdated()
    {
        onBeforeUpdated();
        
    }
    
    /**
     * 通知栏更新之后调用<BR>
     */
    protected final void afterUpdated()
    {
        onAfterUpdated();
        
    }
    
    /**
     * 通知栏销毁之前调用<BR>
     */
    protected final void beforeDestoryed()
    {
        onBeforeDestoryed();
        
    }
    
    /**
     * 通知栏销毁之后调用<BR>
     */
    protected final void afterDestoryed()
    {
        onAfterDestoryed();
        
    }
    
    /**
     * 通知栏创建之前调用方法<BR>
     */
    protected void onBreforeCreated()
    {
        
    }
    
    /**
     * 通知栏创建之后调用<BR>
     */
    protected void onAfterCreated()
    {
        
    }
    
    /**
     * 通知栏更新之前调用<BR>
     */
    protected void onBeforeUpdated()
    {
        
    }
    
    /**
     * 通知栏更新之后调用<BR>
     */
    protected void onAfterUpdated()
    {
        
    }
    
    /**
     * 通知栏销毁之前调用<BR>
     */
    protected void onBeforeDestoryed()
    {
        
    }
    
    /**
     * 通知栏销毁之后调用<BR>
     */
    protected void onAfterDestoryed()
    {
        
    }
    
    /**
     * 初始化通知栏信息<BR>
     */
    protected abstract void initContentInfo();
    
    /**
     * 更新通知栏信息<BR>
     */
    protected abstract void updateContentInfo();
    
    /**
     * 更新通知栏信息<BR>
     * @param notificationEntity NotificationEntity对象
     */
    protected void updateContentInfo(NotificationEntity notificationEntity)
    {
        
    }
    
    /**
     * 设置通知栏跳转Intent<BR>
     * @param activityIntent 通知栏跳转的Intent
     */
    public void setActivityIntent(Intent activityIntent)
    {
        this.mActivityIntent = activityIntent;
    }
    
    /**
     * 获得通知栏跳转Intent<BR>
     * @return 通知栏跳转的Intent
     */
    protected Intent getActivityIntent()
    {
        return null == mActivityIntent ? new Intent() : mActivityIntent;
    }
}
