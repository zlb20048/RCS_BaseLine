package com.huawei.basic.android.im.ui.im.item;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.ui.im.ProgressModel;

/**
 * 
 * 媒体类型消息展示Holder<BR>
 * @author lidan
 * @version [RCS Client V100R001C03, 2012-3-29]
 */
public class AudioItem extends BaseMsgItem
{
    
    /**
     * TAG
     */
    private static final String TAG = "AudioItem";
    
    /**
     * 音频最大显示宽度
     */
    private static final int MAX_WIDTH = 210;
    
    /**
     * 音频最小显示宽度
     */
    private static final int MIN_WIDTH = 120;
    
    /**
     * 音频显示图标
     */
    private ImageView mAudioIV;
    
    /**
     * 音频大小
     */
    private TextView audioSizeTV;
    
    /**
     * 构造方法
     * @param holderEventListener HolderEventListener
     */
    public AudioItem(HolderEventListener holderEventListener)
    {
        super(holderEventListener);
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#updateView(com.huawei.basic.android.im.logic.model.BaseMessageModel, android.content.Context, android.database.Cursor)
     */
    @Override
    public void updateView(final BaseMessageModel msg, final Context context,
            Cursor cursor, ProgressModel progress, final boolean isPlaying)
    {
        // 设置泡泡显示宽度
        int width = (msg.getMediaIndex().getPlayTime())
                * (MAX_WIDTH - MIN_WIDTH) / 60 + MIN_WIDTH;
        ViewGroup.LayoutParams params = getMsgContainer().getLayoutParams();
        params.width = width;
        getMsgContainer().setLayoutParams(params);
        
        // 设置泡泡的Gravity.
        getMsgContainer().setGravity(getTypeSendOrReceive() == BaseMsgItem.SendOrReceive.SEND ? (Gravity.RIGHT | Gravity.CENTER_VERTICAL)
                : (Gravity.LEFT | Gravity.CENTER_VERTICAL));
        
        // 设置消息内容(音频长度，图标等)
        audioSizeTV.setVisibility(View.VISIBLE);
        Logger.i(TAG, "play time==" + msg.getMediaIndex().getPlayTime());
        audioSizeTV.setText(msg.getMediaIndex().getPlayTime() + "''");
        Logger.d(TAG, "isPlaying ? " + isPlaying);
        if (msg.getMsgStatus() == BaseMessageModel.MSGSTATUS_PREPARE_SEND)
        {
            mAudioIV.setBackgroundDrawable(null);
            return;
        }
        
        // 未播放时
        if (!isPlaying)
        {
            Drawable backgd = mAudioIV.getBackground();
            if (backgd instanceof AnimationDrawable)
            {
                ((AnimationDrawable) backgd).stop();
            }
            mAudioIV.setBackgroundResource(msg.getMsgSendOrRecv() == BaseMessageModel.MSGSENDORRECV_SEND ? R.drawable.audio_send_normal
                    : R.drawable.audio_rec_normal);
        }
        else
        {
            Drawable backgd = mAudioIV.getBackground();
            if (backgd == null || !(backgd instanceof AnimationDrawable))
            {
                mAudioIV.setBackgroundResource(msg.getMsgSendOrRecv() == BaseMessageModel.MSGSENDORRECV_SEND ? R.anim.voice_send_from_icon_anim
                        : R.anim.voice_recv_from_icon_anim);
            }
            AnimationDrawable ad = (AnimationDrawable) mAudioIV.getBackground();
            ad.start();
        }
        
        getMsgContainer().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isPlaying)
                {
                    Logger.d(TAG,
                            "playing right now, stop play& show start button");
                    getHolderEventListener().stopPlayAudio();
                    //                    mAudioIV.setImageResource(R.drawable.audio_play);
                }
                else
                {
                    Logger.d(TAG,
                            "is not playing right now, start play& show stop button");
                    if (msg.getMediaIndex().getMediaPath() != null)
                    {
                        getHolderEventListener().startPlayAudio(msg);
                        getHolderEventListener().setMsgAsReaded(msg);
                        mAudioIV.setBackgroundResource(msg.getMsgSendOrRecv() == BaseMessageModel.MSGSENDORRECV_SEND ? R.anim.voice_send_from_icon_anim
                                : R.anim.voice_recv_from_icon_anim);
                        AnimationDrawable ad = (AnimationDrawable) mAudioIV.getBackground();
                        ad.start();
                    }
                    else
                    {
                        Logger.e(TAG, "audio path is null!");
                        Toast.makeText(context,
                                R.string.download_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    
    /**
     * 获取子视图<BR>
     * @param context Context
     * @return 子视图
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#getSubView(android.content.Context)
     */
    @Override
    protected View getSubView(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.im_msg_item_audio_paopao, null);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#initSubView(android.view.View)
     */
    
    @Override
    protected void initSubView(Context context, View view)
    {
        mAudioIV = (ImageView) view.findViewById(R.id.audio_image);
        audioSizeTV = (TextView) view.findViewById(R.id.audio_size);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#msgCanBeCopy()
     */
    
    @Override
    protected boolean msgCanBeCopy()
    {
        return false;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#msgCanBeTransfer()
     */
    
    @Override
    protected boolean msgCanBeTransfer()
    {
        return true;
    }
}
