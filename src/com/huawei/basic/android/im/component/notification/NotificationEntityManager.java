/*
 * 文件名: NotificationEntityManager.java
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

import java.util.Hashtable;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

/**
 * 通知栏管理类<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 16, 2012] 
 */
public class NotificationEntityManager
{
    /**
     * NotificationEntityManager的单例对象
     */
    private static NotificationEntityManager mInstance = new NotificationEntityManager();
    
    /**
     * 通知栏ID 自增
     */
    private int mNotificationEntityId = 0;
    
    /**
     * NotificationManager对象
     */
    private NotificationManager mNotificationManager = null;
    
    /**
     * 保存notification对象的集合
     */
    private Map<String, NotificationEntity> mNotificationEntitys = new Hashtable<String, NotificationEntity>();
    
    /**
     * 上下文对象
     */
    private Context mContext;
    
    /**
     * 处理通知栏点击事件的广播
     */
    //    private BroadcastReceiver mNotificationBroadcastReceiver = new BroadcastReceiver()
    //    {
    //        public void onReceive(Context context, Intent intent)
    //        {
    //            String key = intent.getExtras()
    //                    .getString(NotificationEntity.FLAG_NOTIFICATION_KEY);
    //            NotificationEntity notificationEntity = mNotificationEntitys.get(key);
    //            if (null == notificationEntity)
    //            {
    //                return;
    //            }
    //            int type = intent.getExtras()
    //                    .getInt(NotificationEntity.FLAG_NOTIFICATION_TYPE);
    //            switch (type)
    //            {
    //                case NotificationEntity.TYPE_NOTIFICATION_CLICK:
    //                    int viewId = intent.getExtras()
    //                            .getInt(NotificationEntity.FLAG_NOTIFICATION_ACTION);
    //                    if ((notificationEntity.getNotification().flags & Notification.FLAG_AUTO_CANCEL) > 0)
    //                    {
    //                        notificationEntity.beforeDestoryed();
    //                        mNotificationEntitys.remove(key);
    //                        notificationEntity.onAfterDestoryed();
    //                    }
    //                    INotificationEntityListener notificationEntityListener = notificationEntity.getNotificationEntityListener();
    //                    if (null != notificationEntityListener)
    //                    {
    //                        notificationEntityListener.onClick(viewId,
    //                                notificationEntity);
    //                    }
    //                    break;
    //                case NotificationEntity.TYPE_NOTIFICATION_DELETE:
    //                    notificationEntity.beforeDestoryed();
    //                    mNotificationEntitys.remove(key);
    //                    notificationEntity.onAfterDestoryed();
    //                    break;
    //                default:
    //                    break;
    //                
    //            }
    //        }
    //    };
    /**
     * 获得NotificationEntityManager对象<BR>
     * @return NotificationEntityManager对象
     */
    public static NotificationEntityManager getInstance()
    {
        return mInstance;
    }
    
    /**
     * 获得notfication对象ID<BR>
     * @return notfiication对象ID
     */
    private int getNextNotificationEntityId()
    {
        return ++mNotificationEntityId;
    }
    
    /**
     * 初始化通知栏基本组件<BR>
     * @param context 上下文
     */
    public void init(Context context)
    {
        this.mContext = context;
        if (null == mNotificationManager)
        {
            this.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //            IntentFilter filter = new IntentFilter();
            //            filter.addAction(NotificationEntity.FlAG_INTENT_ACTION);
            //            this.mContext.registerReceiver(mNotificationBroadcastReceiver,
            //                    filter);
        }
    }
    
    /**
     * 从缓存中取得通知栏对象<BR>
     * @param key 通知栏的存储时的键
     * @return 通知栏对象
     */
    public NotificationEntity getNotificationEntity(String key)
    {
        return key == null ? null : mNotificationEntitys.get(key);
    }
    
    /**
     * 展示通知栏<BR>
     * @param 通知栏对象
     * @param notificationEntity NotificationEntity
     * @return 缓存通知栏的键
     */
    public final String showNewNotification(
            NotificationEntity notificationEntity)
    {
        int notificationEntityId = getNextNotificationEntityId();
        String key = null != notificationEntity.getKey() ? notificationEntity.getKey()
                : String.valueOf(getNextNotificationEntityId());
        notificationEntity.setKey(key);
        notificationEntity.init(mContext);
        if ((notificationEntity.getNotificationFlags() & Notification.FLAG_AUTO_CANCEL) != Notification.FLAG_AUTO_CANCEL)
        {
            mNotificationEntitys.put(key, notificationEntity);
        }
        notificationEntity.setId(notificationEntityId);
        notificationEntity.breforeCreated();
        notificationEntity.initContentInfo();
        //        notificationEntity.setDeletePendingIntent();
        notificationEntity.setActivityPendingIntent();
        mNotificationManager.notify(notificationEntityId,
                notificationEntity.getNotification());
        notificationEntity.afterCreated();
        return key;
    }
    
    /**
     * 更新通知栏<BR>
     * @param key 缓存通知栏集合中的键
     */
    public final void updateNotification(String key)
    {
        NotificationEntity notificationEntity = mNotificationEntitys.get(key);
        if (null != notificationEntity)
        {
            notificationEntity.beforeUpdated();
            notificationEntity.updateContentInfo();
            notificationEntity.setActivityPendingIntent();
            mNotificationManager.notify(notificationEntity.getId(),
                    notificationEntity.getNotification());
            notificationEntity.afterUpdated();
        }
    }
    
    /**
     * 更新通知栏<BR>
     * @param notificationEntity 通知栏对象
     */
    public final void updateNotification(NotificationEntity notificationEntity)
    {
        notificationEntity.beforeUpdated();
        notificationEntity.updateContentInfo();
        notificationEntity.setActivityPendingIntent();
        mNotificationManager.notify(notificationEntity.getId(),
                notificationEntity.getNotification());
        notificationEntity.afterUpdated();
    }
    
    /**
     * 销毁通知栏<BR>
     * @param key 缓存通知栏集合中的键
     */
    public void destroyNotification(String key)
    {
        NotificationEntity notificationEntity = mNotificationEntitys.get(key);
        if (null != notificationEntity)
        {
            notificationEntity.beforeDestoryed();
            notificationEntity.updateContentInfo();
            mNotificationManager.cancel(notificationEntity.getId());
            mNotificationEntitys.remove(key);
            notificationEntity.afterDestoryed();
        }
    }
    
    /**
     * 消除通知栏<BR>
     * @param entity NotificationEntity对象
     */
    public void cancelNotification(NotificationEntity entity)
    {
        if (null != entity)
        {
            mNotificationManager.cancel(entity.getId());
        }
    }
    
    /**
     * 清除所有通知栏<BR>
     */
    public void destroyAllNotification()
    {
        if (null != mNotificationManager)
        {
            mNotificationManager.cancelAll();
            mNotificationEntitys.clear();
        }
    }
    
    /**
     * 移除通知栏<BR>
     * 移除通知栏
     * @param key 通知栏缓存时候的key
     */
    protected void removeNotificationEntity(String key)
    {
        mNotificationEntitys.remove(key);
        
    }
}
