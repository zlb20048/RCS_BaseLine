/*
 * 文件名: VoipNotificationEntity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Apr 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.notification;

import java.util.Date;
import java.util.Locale;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.notification.ViewNotificationEntity;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * Voip通知栏<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 16, 2012]
 */
public class VoipNotificationEntity extends ViewNotificationEntity
{
    /**
     * VOIP通知栏TAG
     */
    private static final String TAG = "VoipNotificationEntity";
    
    /**
     * 姓名的最大长度
     */
    private static final int MAX_NAME_LENGTH = 14;
    
    /**
     * 通话开始时间
     */
    private Date mStartTime;
    
    /**
     * 头像
     */
    private Bitmap mFaceBitmap;
    
    /**
     * 点击通知栏要跳转的intent
     */
    private Intent mIntent;
    
    /**
     * 上方通知栏要显示的内容
     */
    private CharSequence mTickerText;
    
    /**
     * 联系人的姓名
     */
    private String mContactName;
    
    /**
     * 计时flag
     */
    private boolean runFlag = true;
    
    /**
     * 头像改变标志
     */
    private boolean changeFace = false;
    
    /**
     * 姓名改变标志
     */
    private boolean changeName = false;
    
    /**
     * 构造方法
     * 
     * @param tickerText
     *            状态栏标题 显示为"与某某通话中......" + 通话时间
     * @param faceBitmap
     *            联系人头像
     * @param contactName
     *            联系人姓名
     * @param callStartTime
     *            电话开始时间
     * @param intent
     *            点击通知栏跳转页面
     */
    public VoipNotificationEntity(CharSequence tickerText, Bitmap faceBitmap,
            String contactName, Date callStartTime, Intent intent)
    {
        super(R.drawable.notification_logo, tickerText);
        this.mTickerText = tickerText;
        this.mFaceBitmap = faceBitmap;
        this.mContactName = contactName;
        this.mStartTime = callStartTime;
        this.mIntent = intent;
    }
    
    public boolean isRunFlag()
    {
        return runFlag;
    }
    
    public void setRunFlag(boolean runFlag)
    {
        this.runFlag = runFlag;
    }
    
    public Date getStartTime()
    {
        return mStartTime;
    }
    
    public void setStartTime(Date startTime)
    {
        this.mStartTime = startTime;
    }
    
    public Bitmap getFaceBitmap()
    {
        return mFaceBitmap;
    }
    
    /**
     * 设置头像
     * @param faceBitmap 头像图片对象
     */
    public void setFaceBitmap(Bitmap faceBitmap)
    {
        this.mFaceBitmap = faceBitmap;
        changeFace = true;
    }
    
    public CharSequence getTickerText()
    {
        return mTickerText;
    }
    
    public void setTickerText(CharSequence tickerText)
    {
        this.mTickerText = tickerText;
    }
    
    public String getContactName()
    {
        return mContactName;
    }
    
    /**
     * 设置名字<BR>
     * 主要用于本地通讯录信息改变的时候更新联系人姓名
     * @param contactName 联系人姓名
     */
    public void setContactName(String contactName)
    {
        this.mContactName = contactName;
        changeName = true;
    }
    
    /**
     * 获得通知栏布局<BR>
     * 
     * @return 通知栏布局ID
     * @see com.huawei.basic.android.im.component.notification.ViewNotificationEntity#getLayoutId()
     */
    
    @Override
    protected int getLayoutId()
    {
        return R.layout.notify_voip;
    }
    
    /**
     * 初始化通知栏布局的各元素的值<BR>
     * 
     * @param contentView
     *            RemoteViews对象
     * @see com.huawei.basic.android.im.component.notification.ViewNotificationEntity#initContentView(android.widget.RemoteViews)
     */
    
    @Override
    protected void initContentView(RemoteViews contentView)
    {
        // 开始时间为0
        String talkingTime = DateUtil.VOIP_TALKING_TIME.format(0);
        contentView.setTextViewText(R.id.notify_voip_connecttime, talkingTime);
        // 头像为空设置为默认头像
        if (null == mFaceBitmap)
        {
            contentView.setImageViewResource(R.id.notify_voip_face,
                    R.drawable.voip_comm_img_unknow);
        }
        else
        {
            contentView.setImageViewBitmap(R.id.notify_voip_face, mFaceBitmap);
        }
        contentView.setImageViewResource(R.id.notify_voip_phone_icon,
                R.drawable.icon_voip_notify_call);
        // 设置通知内容 "与某某通话"
        String contactNameFormat = getContext().getResources()
                .getString(R.string.notify_voip_connectcontent);
        if (!StringUtil.isNullOrEmpty(mContactName))
        {
            mContactName = StringUtil.trim(mContactName, MAX_NAME_LENGTH);
            //如果是英文环境，去掉拼接的省略号 界面显示 talking to...
            if ("en".equals(Locale.getDefault().getLanguage()))
            {
                mContactName = mContactName.replace("...", "");
            }
        }
        String contactName = String.format(contactNameFormat,
                mContactName == null ? "" : mContactName);
        contentView.setTextViewText(R.id.notify_voip_contactname, contactName);
        // 设置弹出标题
        getNotification().tickerText = contactName;
        new Thread()
        {
            @Override
            public void run()
            {
                while (runFlag)
                {
                    try
                    {
                        NotificationEntityManager.getInstance()
                                .updateNotification(getKey());
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e)
                    {
                        Logger.e(TAG, "fresh time ", e);
                    }
                }
            }
        } .start();
    }
    
    /**
     * 更新通知栏<BR>
     * 
     * @param contentView
     *            RemoteViews对象
     * @see com.huawei.basic.android.im.component.notification.ViewNotificationEntity#updateContentView(android.widget.RemoteViews)
     */
    
    @Override
    protected void updateContentView(RemoteViews contentView)
    {
        setActivityIntent(getActivityIntent());
        if (changeFace)
        {
            if (null == mFaceBitmap)
            {
                contentView.setImageViewResource(R.id.notify_voip_face,
                        R.drawable.voip_comm_img_unknow);
            }
            else
            {
                contentView.setImageViewBitmap(R.id.notify_voip_face,
                        mFaceBitmap);
            }
            changeFace = false;
        }
        if (changeName)
        {
            if (!StringUtil.isNullOrEmpty(mContactName))
            {
                mContactName = StringUtil.trim(mContactName, MAX_NAME_LENGTH);
            }
            String contactNameFormat = getContext().getResources()
                    .getString(R.string.notify_voip_connectcontent);
            String contactName = String.format(contactNameFormat,
                    mContactName == null ? "" : mContactName);
            contentView.setTextViewText(R.id.notify_voip_contactname,
                    contactName);
            changeName = false;
        }
        long now = System.currentTimeMillis();
        String talkingTime = DateUtil.VOIP_TALKING_TIME.format(now
                - mStartTime.getTime());
        contentView.setTextViewText(R.id.notify_voip_connecttime, talkingTime);
    }
    
    /**
     * 获得点击通知栏要跳转的Intent<BR>
     * 
     * @return 通知栏要跳转的Intent
     * @see com.huawei.basic.android.im.component.notification.BaseNotificationEntity#getActivityIntent()
     */
    
    protected Intent getActivityIntent()
    {
        return mIntent;
    }
    
    @Override
    protected int getNotificationFlags()
    {
        return Notification.FLAG_NO_CLEAR;
    }
    
    @Override
    protected int getNotificationSound()
    {
        return -1;
    }
    
    /**
     * 
     * 通知栏销毁前<BR>
     * 如果在通知栏销毁前需要做处理调用这个方法
     * 
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#onBeforeDestoryed()
     */
    @Override
    protected void onBeforeDestoryed()
    {
        runFlag = false;
    }
    
}
