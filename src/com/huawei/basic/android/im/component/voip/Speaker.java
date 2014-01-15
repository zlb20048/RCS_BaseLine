/*
 * 文件名: Speaker.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Apr 9, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

import android.content.Context;
import android.media.AudioManager;

/**
 * 扬声器
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 9, 2012] 
 */
public class Speaker
{
    /**
     * Context对象
     */
    private Context mContext;
    
//    /**
//     * 当前声音
//     */
//    private int mCurrVolume;
    
    /**
     * 构造方法
     * @param context
     *      Context对象
     */
    public Speaker(Context context)
    {
        this.mContext = context;
    }
    
    /**
     * 打开扬声器
     */
    public void open()
    {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (null == audioManager)
        {
            return;
        }
//        audioManager.setMode(AudioManager.ROUTE_SPEAKER);
//        mCurrVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        if (!audioManager.isSpeakerphoneOn())
        {
            audioManager.setSpeakerphoneOn(true);
//            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
//                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
//                    AudioManager.STREAM_VOICE_CALL);
        }
    }
    
    /**
     * 
     * 扬声器是否打开
     * @return 扬声器是否打开
     */
    public boolean isOpen()
    {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (null == audioManager)
        {
            return false;
        }
        return audioManager.isSpeakerphoneOn();
    }
    
    /**
     * 关闭扬声器
     */
    public void close()
    {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (null == audioManager)
        {
            return;
        }
        if (audioManager.isSpeakerphoneOn())
        {
            audioManager.setSpeakerphoneOn(false);
//            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
//                    mCurrVolume,
//                    AudioManager.STREAM_VOICE_CALL);
        }
    }
}
