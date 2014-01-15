/*
 * 文件名: DeferredHandler.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import java.util.LinkedList;

import com.huawei.basic.android.im.component.log.Logger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;

/**
 * Queue of things to run on a looper thread. Items posted with {@link #post}
 * will not be actually enqued on the handler until after the last one has run,
 * to keep from starving the thread. This class is fifo.
 * 
 * @author deanye
 * @version [RCS Client V100R001C03, 2011-11-16]
 */
public class DeferredHandler
{
    private static final String TAG = "DeferredHandler";
    
    private LinkedList<Runnable> mQueue = new LinkedList<Runnable>();
    
    private MessageQueue mMessageQueue = Looper.myQueue();
    
    private Impl mHandler = new Impl();
    
    /**
     * 
     * a looper thread.
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2011-11-16]
     */
    private class Impl extends Handler implements MessageQueue.IdleHandler
    {
        public void handleMessage(Message msg)
        {
            Runnable r;
            synchronized (mQueue)
            {
                if (mQueue.size() == 0)
                {
                    return;
                }
                r = mQueue.removeFirst();
            }
            r.run();
            synchronized (mQueue)
            {
                scheduleNextLocked();
            }
        }
        
        public boolean queueIdle()
        {
            handleMessage(null);
            return false;
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2011-11-16]
     */
    private static class FirstRunnable implements Runnable
    {
        private Runnable mRunnable;
        
        private FirstRunnable(Runnable r)
        {
            mRunnable = r;
        }
        
        public void run()
        {
            mRunnable.run();
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2011-11-16]
     */
    private static class IdleRunnable implements Runnable
    {
        private Runnable mRunnable;
        
        private IdleRunnable(Runnable r)
        {
            mRunnable = r;
        }
        
        public void run()
        {
            mRunnable.run();
        }
    }
    
    /**
     * [构造简要说明]
     */
    public DeferredHandler()
    {
    }
    
    /**
     * Schedule runnable to run after everything that's on the queue right now.
     * 
     * @param runnable Runnable
     */
    public void post(Runnable runnable)
    {
        synchronized (mQueue)
        {
            mQueue.add(runnable);
            if (mQueue.size() == 1)
            {
                scheduleNextLocked();
            }
        }
    }
    
    /**
     * Schedule runnable to run before everything that's on the queue right now.
     * 
     * @param runnable Runnable
     */
    public void postFirst(Runnable runnable)
    {
        FirstRunnable fRun = new FirstRunnable(runnable);
        synchronized (mQueue)
        {
            if (mQueue.isEmpty())
            {
                mQueue.add(fRun);
                scheduleNextLocked();
            }
            else
            {
                for (int i = 0; i < mQueue.size(); i++)
                {
                    if (!(mQueue.get(i) instanceof FirstRunnable))
                    {
                        mQueue.add(i, fRun);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Schedule runnable to run when the queue goes idle.
     * 
     * @param runnable Runnable
     */
    public void postIdle(final Runnable runnable)
    {
        post(new IdleRunnable(runnable));
    }
    
    /**
     * 
     * cancelRunnable
     * 
     * @param runnable Runnable
     */
    public void cancelRunnable(Runnable runnable)
    {
        synchronized (mQueue)
        {
            while (mQueue.remove(runnable))
            {
                Logger.d(TAG, "remove runnable from mQueue");
            }
        }
    }
    
    /**
     * clear LinkedList<Runnable>
     */
    public void cancel()
    {
        synchronized (mQueue)
        {
            mQueue.clear();
        }
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     */
    void scheduleNextLocked()
    {
        if (mQueue.size() > 0)
        {
            Runnable peek = mQueue.getFirst();
            if (peek instanceof IdleRunnable)
            {
                mMessageQueue.addIdleHandler(mHandler);
            }
            else
            {
                mHandler.sendEmptyMessage(1);
            }
        }
    }
}
