/*
 * 文件名: RingtonePlayer.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 26, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

/**
 * 铃声播放器
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 26, 2012] 
 */
public class RingtonePlayer
{
    /**
     * TAG
     */
    private static final String TAG = "RingtonePlayer";
    
    /**
     * 震动时间
     */
    private static final long VIBERATOR_TIME = 1000L;
    
    /**
     * 媒体播放对象
     */
    private MediaPlayer mMediaPlayer = null;
    
    /**
     * 音频管理器
     */
    private AudioManager mAudioManager = null;
    
    /**
     * 播放线程
     */
    private Thread mThread = null;
    
    /**
     * 震动操作对象
     */
    private Vibrator mVibrator = null;
    
    /**
     * context对象
     */
    private Context mContext;
    
    /**
     * 构造方法
     * @param context
     *      context对象
     */
    public RingtonePlayer(Context context)
    {
        this.mContext = context;
        this.mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    
    /**
     * 开始播放铃声
     */
    public void start()
    {
        if (mThread == null || !mThread.isAlive())
        {
            mThread = new InnerThread();
            mThread.start();
        }
        
    }
    
    /**
     * 开始停止铃声
     */
    public void stop()
    {
        if (null != mMediaPlayer && mMediaPlayer.isPlaying())
        {
            mMediaPlayer.stop();
        }
        mMediaPlayer = null;
        if (null != mVibrator)
        {
            mVibrator.cancel();
            mVibrator = null;
        }
    }
    
    /**
     * 播放器内部播放线程
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Mar 26, 2012]
     */
    private class InnerThread extends Thread
    {
        /**
         * 线程运行方法
         * @see java.lang.Thread#run()
         */
        public void run()
        {
            try
            {
                int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                if ((null == mMediaPlayer || !mMediaPlayer.isPlaying())
                        && (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL))
                {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                            currentVolume,
                            AudioManager.FLAG_ALLOW_RINGER_MODES);
                    mMediaPlayer = new MediaPlayer();
                    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    mMediaPlayer.setDataSource(mContext, alert);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                }
                int ringerMode = mAudioManager.getRingerMode();
                if (ringerMode == AudioManager.RINGER_MODE_NORMAL
                        || ringerMode == AudioManager.RINGER_MODE_VIBRATE)
                {
                    mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = { 500, 100, 500, 100 };
                    mVibrator.vibrate(pattern, 1);
                }
            }
            catch (IllegalStateException e)
            {
                Log.e(TAG, "MediaPlayer run error.", e);
            }
            catch (IOException e)
            {
                Log.e(TAG, "MediaPlayer run error.", e);
            }
        }
    }
    
    /**
     * 开始振铃
     */
    public void startOnceVibrator()
    {
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(VIBERATOR_TIME);
    }
}
