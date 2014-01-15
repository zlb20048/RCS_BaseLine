/*
 * 文件名: TextNotificationEntity.java
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

import android.content.Context;

/**
 * 文本通知栏实体类<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 16, 2012] 
 */
public class TextNotificationEntity extends NotificationEntity
{
    /**
     * 通知栏广播类型 找朋友小助手
     */
    public static final String NOTIFICAITON_ACTION_IM_FRIENDHELPER = "com.huawei.basic.notification.friendhelper";
    
    /**
     * 两次消息的时间间隔
     */
    public static final long TIME_SPACE = 3000L;
    
    private String mContentTitle;
    
    private String mContentText;
    
    private CharSequence mTickerText;
    
    /**
     * 无参构造方法
     */
    public TextNotificationEntity()
    {
        super();
    }
    
    /**
     * 构造方法
     * @param icon 通知栏图标
     * @param tickerText 通知栏弹出标题
     * @param contentTitle 下拉通知栏标题
     * @param contentText 下拉通知栏内容
     */
    public TextNotificationEntity(int icon, CharSequence tickerText,
            String contentTitle, String contentText)
    {
        super(icon, tickerText);
        this.mContentTitle = contentTitle;
        this.mContentText = contentText;
        mTickerText = tickerText;
    }
    
    /**
     * 
     * 带有声音提示的构造方法
     * @param icon 通知栏图标
     * @param tickerText 通知栏弹出标题
     * @param contentTitle 下拉通知栏标题
     * @param contentText 下拉通知栏内容
     * @param haveSound 是否有提示音
     */
    public TextNotificationEntity(int icon, CharSequence tickerText,
            String contentTitle, String contentText, boolean haveSound)
    {
        super(icon, tickerText, haveSound);
        this.mContentTitle = contentTitle;
        this.mContentText = contentText;
        this.mTickerText = tickerText;
    }
    
    /**
     * 
     * 初始化<BR>
     * @param context 上下文
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#init(android.content.Context)
     */
    @Override
    protected void init(Context context)
    {
        super.init(context);
    }
    
    /**
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#initContentInfo()
     */
    
    @Override
    protected void initContentInfo()
    {
        getNotification().setLatestEventInfo(getContext(),
                mContentTitle,
                mContentText,
                getActivityPendingIntent());
        
    }
    
    @Override
    protected int getPentingIntentFlags()
    {
        return super.getPentingIntentFlags();
    }
    
    /**
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#updateContentInfo()
     */
    
    @Override
    protected void updateContentInfo()
    {
        getNotification().setLatestEventInfo(getContext(),
                mContentTitle,
                mContentText,
                getActivityPendingIntent());
    }
    
    public String getContentTitle()
    {
        return mContentTitle;
    }
    
    public void setContentTitle(String contentTitle)
    {
        this.mContentTitle = contentTitle;
    }
    
    public String getContentText()
    {
        return mContentText;
    }
    
    public void setContentText(String contentText)
    {
        this.mContentText = contentText;
    }
    
    public CharSequence getTickerText()
    {
        return mTickerText;
    }
    
    public void setTickerText(CharSequence tickerText)
    {
        this.mTickerText = tickerText;
    }
}
