/*
 * 文件名: IMNotificaitonEntity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2012-4-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.notification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.notification.TextNotificationEntity;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 单人会话通知栏<BR>
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-4-20] 
 */
public class IMNotificationEntity extends TextNotificationEntity
{
    
    /**
     * 通知栏类型:单人会话通知栏
     */
    public static final String NOTIFICATION_IM_SINGLE = "notification_im_single";
    
    /**
     * 通知栏广播action
     */
    public static final String NOTIFICAITON_ACTION_IM_SINGLE = "com.huawei.basic.notification.imnotification";
    
    /**
     * 广播中标志当前好友id的key字符串
     */
    public static final String NOTIFICATION_CURRENT_FRIENDUSERID = "notification_current_frienduserid";
    
    /**
     * TAG
     */
    private static final String TAG = "IMNotificaitonEntity";
    
    /**
     * 姓名显示的最大长度
     */
    private static final int MAX_NAME_LENGTH = 7;
    
    /**
     * 通知栏显示标题
     */
    private String mContentTitle;
    
    /**
     * 通知栏显示内容
     */
    private CharSequence mContentText;
    
    /**
     * 联系人姓名
     */
    private String mName;
    
    /**
     * 下拉通知栏显示内容
     */
    private String mMsgContent;
    
    /**
     * 发来消息的好友数量
     */
    private int mFriendCount;
    
    /**
     * 发来的消息总数
     */
    private int mMsgCount;
    
    /**
     * 消息类型
     */
    private int mMsgType;
    
    /**
     * 媒体类型
     */
    private int mMediaType;
    
    /**
     * 要跳转的activity
     */
    private Intent mIntent;
    
    /**
     * 构造方法
     * @param name 发来消息的人的姓名
     * @param msgContent 消息内容
     * @param friendCount 好友数量
     * @param msgCount 消息数量
     * @param msgType 消息类型
     * @param mediaType 媒体类型
     * @param intent 要跳转的activity
     * @param haveSound 是否有提示音
     */
    public IMNotificationEntity(String name, String msgContent,
            int friendCount, int msgCount, int msgType, int mediaType,
            Intent intent, boolean haveSound)
    {
        super(R.drawable.notification_logo, null, null, null, haveSound);
        this.mName = name;
        this.mMsgContent = msgContent;
        this.mFriendCount = friendCount;
        this.mMsgCount = msgCount;
        this.mMsgType = msgType;
        this.mMediaType = mediaType;
        this.mIntent = intent;
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
     * 初始化通知栏<BR>
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#initContentInfo()
     */
    
    @Override
    protected final void initContentInfo()
    {
        generateMsg(mName,
                mMsgContent,
                mFriendCount,
                mMsgCount,
                mMsgType,
                mMediaType,
                mIntent);
        getNotification().setLatestEventInfo(getContext(),
                mContentTitle,
                mContentText,
                getActivityPendingIntent());
        
    }
    
    /**
     * 更新通知栏<BR>
     * @see com.huawei.basic.android.im.component.notification.TextNotificationEntity#updateContentInfo()
     */
    @Override
    protected void updateContentInfo()
    {
        getNotification().setLatestEventInfo(getContext(),
                mContentTitle,
                mContentText,
                getActivityPendingIntent());
    }
    
    /**
     * 获得通知栏跳转intent<BR>
     * @return Intent对象
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#getActivityIntent()
     */
    @Override
    protected Intent getActivityIntent()
    {
        return mIntent == null ? super.getActivityIntent() : mIntent;
    }
    
    /**
     * 获得通知栏flag<BR>
     * @return Notification标志
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#getNotificationFlags()
     */
    @Override
    protected int getNotificationFlags()
    {
        return Notification.FLAG_AUTO_CANCEL;
    }
    
    /**
     * 
     * 生成通知栏相关信息<BR>
     * @param name 发来消息的人的姓名
     * @param content 消息内容
     * @param friendCount 好友数量
     * @param msgCount 消息数量
     * @param msgType 消息类型
     * @param mediaType 媒体类型
     * @param intent 要跳转的activity
     */
    private void generateMsg(String name, String content, int friendCount,
            int msgCount, int msgType, int mediaType, Intent intent)
    {
        //弹出通知栏的的消息
        Logger.d(TAG, "IMNotificationEntity : " + "[" + name + " : " + content
                + " : " + friendCount + " : " + msgCount + " : " + msgType
                + " : " + mediaType + "]");
        String tickerTextFormat = null;
        if (msgType == MessageModel.MSGTYPE_TEXT)
        {
            tickerTextFormat = getContext().getString(R.string.notify_im_ticker_text);
        }
        else if (msgType == MessageModel.MSGTYPE_MEDIA)
        {
            switch (mediaType)
            {
                case MediaIndexModel.MEDIATYPE_IMG:
                case MediaIndexModel.MEDIATYPE_EMOJI:
                    tickerTextFormat = getContext().getString(R.string.notify_im_ticker_img);
                    break;
                case MediaIndexModel.MEDIATYPE_LOCATION:
                    tickerTextFormat = getContext().getString(R.string.mediatype_location);
                    break;
                case MediaIndexModel.MEDIATYPE_AUDIO:
                    tickerTextFormat = getContext().getString(R.string.notify_im_ticker_audio);
                    break;
                case MediaIndexModel.MEDIATYPE_VIDEO:
                    tickerTextFormat = getContext().getString(R.string.notify_im_ticker_vido);
                    break;
            }
        }
        String tickerText = String.format(tickerTextFormat,
                StringUtil.isNullOrEmpty(name) ? "" : name);
        getNotification().tickerText = tickerText;
        //如果是同一个好友发送的消息
        if (friendCount == 1)
        {
            if (msgCount == 1)
            {
                mContentTitle = tickerText;
                if (msgType == MessageModel.MSGTYPE_TEXT)
                {
                    mContentText = content;
                }
                else if (msgType == MessageModel.MSGTYPE_MEDIA)
                {
                    switch (mediaType)
                    {
                        case MediaIndexModel.MEDIATYPE_IMG:
                            mContentText = getContext().getString(R.string.mediatype_img);
                            break;
                        case MediaIndexModel.MEDIATYPE_EMOJI:
                            mContentText = getContext().getString(R.string.mediatype_emoji);
                            break;
                        case MediaIndexModel.MEDIATYPE_LOCATION:
                            mContentText = getContext().getString(R.string.mediatype_location);
                            break;
                        case MediaIndexModel.MEDIATYPE_AUDIO:
                            mContentText = getContext().getString(R.string.mediatype_audio);
                            break;
                        case MediaIndexModel.MEDIATYPE_VIDEO:
                            mContentText = getContext().getString(R.string.mediatype_video);
                            break;
                    }
                }
            }
            else if (msgCount > 1)
            {
                //截取姓名长度最大为7字符串
                if (!StringUtil.isNullOrEmpty(name))
                {
                    name = StringUtil.trim(name, MAX_NAME_LENGTH);
                }
                mContentTitle = name;
                String contentFormat = getContext().getString(R.string.notify_im_multimsg);
                mContentText = String.format(contentFormat, msgCount);
            }
        }
        else
        {
            String contentTitleFormat = getContext().getString(R.string.notify_im_title_multi);
            mContentTitle = String.format(contentTitleFormat, friendCount);
            String contentFormat = getContext().getString(R.string.notify_im_multimsg);
            mContentText = String.format(contentFormat, msgCount);
            setActivityIntent(intent);
        }
    }
    
}
