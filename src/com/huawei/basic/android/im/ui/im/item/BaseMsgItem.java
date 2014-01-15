/*
 * 文件名: BaseMsgHolder.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im.item;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.ui.im.ProgressModel;
import com.huawei.basic.android.im.utils.DateUtil;

/**
 * 消息展示组件Item<BR>
 * 此类相当于在BaseAdapter的实现过程中定义Holder的原理，所以在此类中的操作有一定限制。
 * 本类只能定义与视图显示相关组件对象作为成员变量，无法保存与消息相关的属性，否则会导致消息的混淆展示
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public abstract class BaseMsgItem
{
    /**
     * 消息的类型个数
     */
    public static final int SEND_OR_RECV_TYPE_COUNT = 2;
    
    /**
     * 消息内容的类型个数，与MsgType中定义的个数一致
     */
    public static final int MSG_TYPE_COUNT = 7;
    
    /**
     * DEBUG TAG.
     */
    private static final String TAG = "BaseMsgItem";
    
    /**
     * 消息接收/发送UTC时间戳，对应Message及GroupMessage中的MSG_TIME字段(两个表中该字段需保持一致，否则出错)
     */
    private static final String COLUMN_NAME_MSG_TIME = "msgTime";
    
    /**
     * 时间线显示时间差(毫秒数):5min
     */
    private static final int TIMELINE_DIFFER_MILLIS = 5 * 60 * 1000;
    
    /**
     * 
     * 消息的类型定义，发送/接收<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-13]
     */
    public interface SendOrReceive
    {
        /**
         * 消息类型：发送
         */
        int SEND = 0;
        
        /**
         * 消息类型：接收
         */
        int RECV = 1;
    }
    
    /**
     * 消息内容的类型，文本/多媒体/...
     * <BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-13]
     */
    public interface MsgType
    {
        /**
         * 消息内容的类型：文本
         */
        int TEXT = 0;
        
        /**
         * 消息内容的类型：系统事件
         */
        int SYSTEM = 1;
        
        /**
         * 消息内容的类型：多媒体消息-图片
         */
        int IMG = 2;
        
        /**
         * 消息内容的类型：多媒体消息-音频
         */
        int AUDIO = 3;
        
        /**
         * 消息内容的类型：多媒体消息-视频
         */
        int VIDEO = 4;
        
        /**
         * 消息内容的类型：多媒体消息-贴图
         */
        int EMOJI = 5;
        
        /**
         * 消息内容的类型：多媒体消息-地址位置
         */
        int LOCATION = 6;
    }
    
    /**
     * 包含消息内容的容器
     */
    private LinearLayout msgContainer;
    
    /**
     * 包含时间线的View容器，用于设置时间线可见性
     */
    private View timeLineV;
    
    /**
     * 时间线TextView
     */
    private TextView timeLineTV;
    
    /**
     * 是发送的消息还是接收的消息，
     * 参考
     * {@link #SendOrReceive} 
     */
    private int typeSendOrReceive;
    
    /**
     * holder内组件事件监听器
     */
    private HolderEventListener holderEventListener;
    
    /**
     * 头像ImageView
     */
    private ImageView faceIV;
    
    /**
     * 昵称TextView(暂不显示昵称，所以XML中设置为gone)
     */
    private TextView nickTV;
    
    /**
     * 消息已读，未读 ImageView
     */
    private ImageView readFlagIV;
    
    /**
     * 重发消息按钮
     */
    private ImageButton resendBtn;
    
    /**
     * 发送状态文本显示TextView(上传进度)
     */
    private TextView msgStatusTV;
    
    /**
     * 音频上传的progressBar(指上传、正在发送状态)
     */
    private ProgressBar mAudioPB;
    
    /**
     * Constructor
     * 
     * @param holderEventListener HolderEventListener
     */
    public BaseMsgItem(HolderEventListener holderEventListener)
    {
        this.holderEventListener = holderEventListener;
    }
    
    /**
     * 
     * 设置重发按钮灰化效果<BR>
     */
    public void setResendUnable()
    {
        if (resendBtn != null)
        {
            resendBtn.setEnabled(false);
        }
    }
    
    /**
     * get holderEventListener
     * @return the holderEventListener
     */
    public HolderEventListener getHolderEventListener()
    {
        return holderEventListener;
    }
    
    /**
     * set holderEventListener
     * @param holderEventListener the holderEventListener to set
     */
    public void setHolderEventListener(HolderEventListener holderEventListener)
    {
        this.holderEventListener = holderEventListener;
    }
    
    /**
     * 获取VIEW对象<BR>
     * getView方法中要实现如下功能：<br>
     * 1、根据{@link #getTypeSendOrReceive()}来决定生成发送还是接收的view；
     * 2、初始化各Item中的组件对象
     * 说明：所有的item配置文件中公共部分组件的ID定义应该与本类中获取时的id一致
     * @param context Context
     * @param cursor Cursor
     * @return VIEW
     */
    public View getView(Context context, Cursor cursor)
    {
        // 1 获取view对象
        View view = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // 1.1 获取接收类型消息展示view
        if (typeSendOrReceive == BaseMsgItem.SendOrReceive.RECV)
        {
            //接收，初始化接收View           
            view = inflater.inflate(R.layout.im_msg_received, null);
            
            // 接收类型消息展示包含阅读状态标识（已读/未读）
            readFlagIV = (ImageView) view.findViewById(R.id.unread_flag);
        }
        
        // 1.2 获取发送类型消息展示view
        else
        {
            //TODO 发送，初始化发送View
            view = inflater.inflate(R.layout.im_msg_send, null);
            
            // 发送类型消息展示包含重发按钮及发送状态显示
            resendBtn = (ImageButton) view.findViewById(R.id.resend);
            msgStatusTV = (TextView) view.findViewById(R.id.msg_status);
            mAudioPB = (ProgressBar) view.findViewById(R.id.audio_progress);
        }
        
        // 2 公共部分组件对象初始化
        faceIV = (ImageView) view.findViewById(R.id.im_head_icon);
        nickTV = (TextView) view.findViewById(R.id.im_nick);
        timeLineV = view.findViewById(R.id.time_line);
        timeLineTV = (TextView) view.findViewById(R.id.time_line_text);
        msgContainer = (LinearLayout) view.findViewById(R.id.paopao);
        msgContainer.addView(getSubView(context));
        
        holderEventListener.registerContextMenu(msgContainer);
        msgContainer.setTag(R.string.im_msg_menu_copy, msgCanBeCopy());
        msgContainer.setTag(R.string.im_msg_menu_delete, msgCanBeDelete());
        msgContainer.setTag(R.string.im_msg_menu_share, msgCanBeShare());
        
        // 3 各item的组件对象初始化
        initSubView(context, view);
        return view;
    }
    
    /**
     * Bind an existing view to the data pointed to by cursor<BR>
     * 在此方法中实现共有的视图展示：1、头像显示；2、时间线 ；3、消息状态
     * 另，设置各组件监听器及实现
     * @param nickName 昵称
     * @param face 头像
     * @param msg BaseMessageModel
     * @param context Context
     * @param cursor Cursor
     * @param progress ProgressModel
     * @param isPlaying boolean
     */
    public void bindView(String nickName, Drawable face, BaseMessageModel msg,
            Context context, final Cursor cursor, ProgressModel progress,
            boolean isPlaying)
    {
        
        showFace(face, msg);
        
        showNickName(nickName);
        
        showTimeline(msg, context, cursor);
        
        showMsgStatus(context, msg, progress);
        if (msgContainer != null)
        {
            msgContainer.setTag(msg);
        }
        
        updateView(msg, context, cursor, progress, isPlaying);
    }
    
    /**
     * get typeSendOrReceive
     * @return the typeSendOrReceive
     */
    public int getTypeSendOrReceive()
    {
        return typeSendOrReceive;
    }
    
    /**
     * set typeSendOrReceive
     * @param typeSendOrReceive the typeSendOrReceive to set
     */
    public void setTypeSendOrReceive(int typeSendOrReceive)
    {
        this.typeSendOrReceive = typeSendOrReceive;
    }
    
    /**
     * 
     * 根据消息类型 获取view对象<BR>
     * 
     * @param context Context
     * @return View
     */
    protected abstract View getSubView(Context context);
    
    /**
     * 
     * 初始化各item自己的组件对象<BR>
     * @param view view
     * @param context context
     */
    protected abstract void initSubView(Context context, View view);
    
    /**
     * 更新视图
     * <BR>
     * 根据cursor直接从数据库查询数据进行展示
     * @param msg BaseMessageModel
     * @param context context
     * @param cursor Cursor
     * @param progress ProgressModel
     * @param isPlaying boolean
     */
    protected abstract void updateView(final BaseMessageModel msg,
            Context context, Cursor cursor, ProgressModel progress,
            boolean isPlaying);
    
    /**
     * get msgContainer
     * @return the msgContainer
     */
    protected LinearLayout getMsgContainer()
    {
        return msgContainer;
    }
    
    /**
     * set timeLineV
     * @param timeLineV the timeLineV to set
     */
    protected void setTimeLineV(View timeLineV)
    {
        this.timeLineV = timeLineV;
    }
    
    /**
     * set timeLineTV
     * @param timeLineTV the timeLineTV to set
     */
    protected void setTimeLineTV(TextView timeLineTV)
    {
        this.timeLineTV = timeLineTV;
    }
    
    /**
     * 
     * 消息能否被删除<BR>
     * 
     * @return 默认可删除
     */
    protected boolean msgCanBeDelete()
    {
        return true;
    }
    
    /**
     * 消息能否复制
     * <BR>
     * 
     * @return 默认不可复制
     */
    protected boolean msgCanBeCopy()
    {
        return false;
    }
    
    /**
     * 消息能否转发
     * <BR>
     * 
     * @return 默认不可转发
     */
    protected boolean msgCanBeTransfer()
    {
        return false;
    }
    
    /**
     * 消息能否分享
     * <BR>
     * 
     * @return 默认不可分享
     */
    protected boolean msgCanBeShare()
    {
        return false;
    }
    
    /**
     * 显示消息状态<BR>
     * 
     * @param context Context
     * @param msg BaseMessageModel
     */
    private void showMsgStatus(Context context, final BaseMessageModel msg,
            final ProgressModel progress)
    {
        int msgStatus = msg.getMsgStatus();
        // 接收到的消息状态展示
        if (getTypeSendOrReceive() == BaseMsgItem.SendOrReceive.RECV)
        {
            if (readFlagIV != null)
            {
                if (msgStatus == BaseMessageModel.MSGSTATUS_UNREAD_NEED_REPORT
                        || msgStatus == BaseMessageModel.MSGSTATUS_UNREAD_NO_REPORT)
                {
                    readFlagIV.setVisibility(View.VISIBLE);
                    readFlagIV.setImageResource(R.drawable.unread_flag);
                }
                else
                {
                    readFlagIV.setVisibility(View.GONE);
                }
            }
            
        }
        
        // 发送出去的消息状态展示
        else if (getTypeSendOrReceive() == BaseMsgItem.SendOrReceive.SEND)
        {
            Logger.d(TAG, "progress = " + (progress == null));
            // 显示进度   如果是音频显示进度条
            if (progress != null)
            {
                resendBtn.setVisibility(View.GONE);
                //2.上传进度的显示
                //如果是音频显示进度条
                if (msg.getMediaIndex().getMediaType() == MediaIndexModel.MEDIATYPE_AUDIO)
                {
                    //进度文本显示隐藏
                    msgStatusTV.setVisibility(View.GONE);
                    return;
                }
                //地图的气泡左边不显示进度
                else if (msg.getMediaIndex().getMediaType() == MediaIndexModel.MEDIATYPE_LOCATION)
                {
                    //显示消息状态
                    mAudioPB.setVisibility(View.GONE);
                    msgStatusTV.setVisibility(View.GONE);
                }
                else
                {
                    //显示消息状态
                    mAudioPB.setVisibility(View.GONE);
                    msgStatusTV.setVisibility(View.VISIBLE);
                    msgStatusTV.setText(progress.getFinished() / 1024 + "K/"
                            + progress.getTotal() / 1024 + "K");
                    return;
                }
            }
            else
            {
                mAudioPB.setVisibility(View.GONE);
            }
            if (resendBtn != null && msgStatusTV != null)
            {
                if (msgStatus == BaseMessageModel.MSGSTATUS_SEND_FAIL)
                {
                    resendBtn.setVisibility(View.VISIBLE);
                    msgStatusTV.setVisibility(View.GONE);
                    msgContainer.setTag(R.string.im_msg_menu_resend, true);
                }
                else
                {
                    msgContainer.setTag(R.string.im_msg_menu_resend, false);
                    resendBtn.setVisibility(View.GONE);
                    msgStatusTV.setVisibility(View.VISIBLE);
                    Logger.d(TAG, "Message status is : " + msgStatus);
                    //非发送失败的其他状态
                    //1、如果是音频
                    if (msg.getMediaIndex() != null
                            && msg.getMediaIndex().getMediaType() == MediaIndexModel.MEDIATYPE_AUDIO)
                    {
                        //正在发送和阻塞状态显示进度条
                        if (msgStatus == BaseMessageModel.MSGSTATUS_PREPARE_SEND
                                || msgStatus == BaseMessageModel.MSGSTATUS_BLOCK)
                        {
                            mAudioPB.setVisibility(View.VISIBLE);
                            msgStatusTV.setVisibility(View.GONE);
                        }
                        else
                        {
                            //否则进度条隐藏，文本显示
                            mAudioPB.setVisibility(View.GONE);
                            msgStatusTV.setVisibility(View.VISIBLE);
                            setStatus(msgStatus, context, msgStatusTV);
                        }
                    }
                    else
                    {
                        //2、非音频，消息状态显示
                        mAudioPB.setVisibility(View.GONE);
                        msgStatusTV.setVisibility(View.VISIBLE);
                        setStatus(msgStatus, context, msgStatusTV);
                    }
                }
            }
        }
    }
    
    /**
     * 显示时间线<BR>
     * 
     * @param  msg BaseMessageModel
     * @param context Context
     * @param cursor Cursor
     */
    private void showTimeline(final BaseMessageModel msg, Context context,
            final Cursor cursor)
    {
        // 更新时间线显示
        if (timeLineV != null && timeLineTV != null)
        {
            // 获取时间字符串，如果为null，说明不需要显示时间线，则隐藏该视图
            String strTimeLine = timeLineShouldShown(msg, context, cursor);
            
            if (strTimeLine == null)
            {
                timeLineV.setVisibility(View.GONE);
            }
            else
            {
                timeLineV.setVisibility(View.VISIBLE);
                timeLineTV.setText(strTimeLine);
            }
        }
    }
    
    /**
     * 显示昵称<BR>
     */
    private void showNickName(String nickName)
    {
        // 更新昵称
        if (nickTV != null)
        {
            if (nickName != null)
            {
                nickTV.setText(nickName);
            }
            else
            {
                nickTV.setText("");
            }
        }
    }
    
    /**
     * 显示头像并设置头像点击监听器<BR>
     * 
     */
    private void showFace(final Drawable face, final BaseMessageModel msg)
    {
        // 更新头像显示
        if (faceIV != null)
        {
            
            // 设置头像
            if (face != null)
            {
                faceIV.setImageDrawable(face);
            }
            else
            {
                faceIV.setImageResource(R.drawable.default_contact_icon);
            }
            
            // 设置头像的点击监听器
            faceIV.setOnClickListener(new View.OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    getHolderEventListener().onUserPhotoClick(msg);
                }
            });
        }
    }
    
    /**
     * 
     * 判断是否需要显示时间线<BR>
     * 
     * @param msg BaseMessageModel
     * @param context Context
     * @param cursor Cursor
     * @return 如果需要显示时间线，则返回用于显示的时间字符串；否则返回null
     */
    private String timeLineShouldShown(final BaseMessageModel msg,
            Context context, Cursor cursor)
    {
        String currentTimestampStr = msg.getMsgTime();
        Date currentTimeStamp = null;
        try
        {
            currentTimeStamp = DateUtil.TIMESTAMP_DF.parse(currentTimestampStr);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "Parse current msgTime failed", e);
            return null;
        }
        if (currentTimeStamp == null)
        {
            return null;
        }
        Date prevTimeStamp = null;
        boolean hasPrevRecord = cursor.moveToPrevious();
        if (hasPrevRecord)
        {
            String prevTimestampStr = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MSG_TIME));
            if (prevTimestampStr != null)
            {
                try
                {
                    prevTimeStamp = DateUtil.TIMESTAMP_DF.parse(prevTimestampStr);
                }
                catch (Exception e)
                {
                    Logger.e(TAG, "Parse prev msgTime failed", e);
                }
            }
        }
        cursor.moveToNext();
        long differ = (prevTimeStamp != null) ? currentTimeStamp.getTime()
                - prevTimeStamp.getTime() : currentTimeStamp.getTime();
        if (differ > TIMELINE_DIFFER_MILLIS)
        {
            return DateUtil.getFormatTimeString(context, currentTimestampStr);
        }
        return null;
    }
    
    /**
     * 设置消息状态
     * @param msgStatus 消息状态值
     * @param context Context
     * @param msgStatusTextView 用于显示当前状态的TextView
     */
    private void setStatus(int msgStatus, Context context,
            TextView msgStatusTextView)
    {
        String strStatus = null;
        switch (msgStatus)
        {
            // 正在发送
            case BaseMessageModel.MSGSTATUS_PREPARE_SEND:
            case BaseMessageModel.MSGSTATUS_BLOCK:
                strStatus = context.getResources()
                        .getString(R.string.message_sending);
                break;
            // 已发送
            case BaseMessageModel.MSGSTATUS_SENDED:
                strStatus = context.getResources()
                        .getString(R.string.message_sent);
                break;
            // 已送达
            case BaseMessageModel.MSGSTATUS_SEND_UNREAD:
                strStatus = context.getResources()
                        .getString(R.string.message_sent_unread);
                break;
            // 对方已读
            case BaseMessageModel.MSGSTATUS_READED:
                strStatus = context.getResources()
                        .getString(R.string.message_sent_read);
                break;
            default:
                break;
        }
        if (strStatus != null)
        {
            Logger.i(TAG, strStatus);
            msgStatusTextView.setText(strStatus);
        }
    }
}
