/*
 * 文件名: TimeThread.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-8
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import android.os.Handler;
import android.os.Message;

/**
 * UI计时器线程<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-8] 
 */
public class TimeThread extends Thread
{
    /**
     * 计时器间隔，默认为1000ms
     */
    private int mInterval;
    
    /**
     * Handler对象
     */
    private Handler mHandler;
    
    /**
     * 消息ID
     */
    private int mMessageID;
    
    /***
     * 计时器总时间，单位为s
     */
    private int mTotalSecond;
    
    /**
     * 线程是否正在运行
     */
    private boolean mRunning;
    
    /**
     * 
     * 构造函数，默认间隔为1s
     * @param handler  Handler对象
     * @param messageID 消息ID
     * @param totalSecond  计时器总时间，单位为s
     */
    public TimeThread(Handler handler, int messageID, int totalSecond)
    {
        this(handler, messageID, totalSecond, 1);
    }
    
    /**
     * 
     * 构造函数
     * @param handler Handler对象
     * @param messageID 消息ID
     * @param totalSecond 计时器总时间，单位为s
     * @param interval 计时器间隔，单位为s
     */
    public TimeThread(Handler handler, int messageID, int totalSecond,
            int interval)
    {
        super();
        this.mInterval = interval;
        this.mHandler = handler;
        this.mMessageID = messageID;
        this.mTotalSecond = totalSecond;
    }
    
    /**
     * 取消计时
     * <BR>
     *
     */
    public void cancel()
    {
        mRunning = false;
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {
        mRunning = true;
        int remainTime = mTotalSecond;
        do
        {
            Message msg = new Message();
            msg.what = mMessageID;
            msg.obj = remainTime;
            mHandler.sendMessage(msg);
            try
            {
                Thread.sleep(mInterval * 1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        } while (remainTime-- > 0 && mRunning);
    }
}
