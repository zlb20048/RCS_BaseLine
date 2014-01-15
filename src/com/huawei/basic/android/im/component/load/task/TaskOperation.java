/*
 * 文件名: ILoadTaskOperation.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 任务操作接口
 * 创建人: deanye
 * 创建时间:2012-4-17
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.task;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import android.os.Handler;

import com.huawei.basic.android.im.component.load.adapter.ITaskDataAdapter;
import com.huawei.basic.android.im.component.load.driver.ITaskDriver;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 任务操作接口
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-17] 
 */
public abstract class TaskOperation implements ITask, Runnable
{
    
    /**
     * 通常行为
     */
    public static final int ACTION_NONE = 0;
    
    /**
     * 正在创建
     */
    public static final int ACTION_CREATE = 1;
    
    /**
     * 正在开始任务
     */
    public static final int ACTION_START = 2;
    
    /**
     * 正在重新开始任务
     */
    public static final int ACTION_RESTART = 3;
    
    /**
     * 正在停止任务
     */
    public static final int ACTION_STOP = 4;
    
    /**
     * 正在删除任务
     */
    public static final int ACTION_DELETE = 5;
    
    /**
     * 正在删除任务
     */
    public static final int RECONNECT_NUM = 5;
    
    /**
     * TAG
     */
    private static final String TAG = "TaskOperation";
    
    /**
     * 任务ID
     */
    private int mId;
    
    /**
     * 任务名 
     */
    private String mName;
    
    /**
     * 任务名 
     */
    private Date createdTime;
    
    /**
     * 任务异常定义
     */
    private TaskException mTaskException;
    
    /**
     * 数据保存操作接口
     */
    private ITaskDataAdapter mTaskDataAdapter;
    
    /**
     * 任务管理器产生的消息句柄
     */
    private Set<Handler> mShareHandlers = null;
    
    /**
     * 提供一个单独的任务的消息句柄。可以单独指向某个特定任务对象
     */
    private Set<Handler> mOwnerHandlers = new HashSet<Handler>();
    
    /**
     * 任务管理器产生的任务监听对象
     */
    private Set<ITaskStatusListener> shareTaskStatusListeners = null;
    
    /**
     * 提供一个单独的任务监听对象。可以单独指向某个特定任务对象
     */
    private Set<ITaskStatusListener> ownerTaskStatusListeners = new HashSet<ITaskStatusListener>();
    
    /**
     * 线程池
     */
    private Executor mExecutor = null;
    
    /**
     * 任务状态
     */
    private int mStatus;
    
    /**
     * 任务当前正在处理的行为
     */
    private Integer mAction = ACTION_NONE;
    
    /**
     * 是否对外可见
     */
    private boolean isBackground = false;
    
    /**
     * 当前完成大小
     */
    private long mCurrentSize;
    
    /**
     * 任务大小
     */
    private long mSize = -1L;
    
    /**
     * 是否更新进度条
     */
    private boolean isUpdatedTotal = false;
    
    /**
     * 开始任务前的初始化数据
     */
    protected abstract void resetOnCreated();
    
    /**
     * 重新开始任务前的初始化数据
     */
    protected abstract void resetOnStart();
    
    /**
     * 重新开始任务前的初始化数据
     */
    protected abstract void resetOnRestart();
    
    /**
     * 重新加载任务前的初始化数据
     */
    protected abstract void resetOnReload();
    
    /**
     * 删除任务前的初始化数据
     */
    protected abstract void resetOnDeleted();
    
    /**
     * 查看任务是否是同一任务
     * @param task TaskOperation
     * @return boolean 
     */
    protected abstract boolean equals(TaskOperation task);
    
    /**
     * 是否需要保存数据库 
     * @return boolean
     */
    protected abstract boolean isSave();
    
    /**
     * 任务是否完成
     * @return boolean
     * @throws TaskException 自定义装载器异常
     */
    protected boolean isFinished() throws TaskException
    {
        return (getTotalSize() != -1) && (getCurrentSize() >= getTotalSize());
    }
    
    /**
     * 适配各种网络驱动,创建任务对应的连接对象
     * @return ITaskDriver
     */
    protected abstract ITaskDriver getTaskDriver();
    
    /**
     * 添加数据保存操作接口
     * @param taskDataAdapter ITaskDataAdapter
     */
    protected void setTaskDataAdapter(ITaskDataAdapter taskDataAdapter)
    {
        this.mTaskDataAdapter = taskDataAdapter;
    }
    
    /**
     * 
     * 获取数据保存操作接口
     * @return ITaskDataAdapter
     */
    public ITaskDataAdapter getTaskDataAdapter()
    {
        return this.mTaskDataAdapter;
    }
    
    /**
     * 创建任务
     * @param executor 线程池对象
     * @param taskStatusListeners 监听对象
     * @param handlers 消息句柄
     * @throws Exception 自定义装载器异常
     */
    protected final void create(Executor executor,
            Set<ITaskStatusListener> taskStatusListeners, Set<Handler> handlers)
            throws Exception
    {
        if (mAction.intValue() != ACTION_NONE)
        {
            return;
        }
        if (!onPreCreate())
        {
            return;
        }
        synchronized (mAction)
        {
            this.mAction = ACTION_CREATE;
            this.mExecutor = executor;
            if (null != handlers)
            {
                this.mShareHandlers = handlers;
            }
            if (null != taskStatusListeners)
            {
                this.shareTaskStatusListeners = taskStatusListeners;
            }
            resetOnCreated();
            onCreated();
        }
    }
    
    /**
     * 重新加载 任务
     * @param executor 线程池对象
     * @param taskStatusListeners 监听对象
     * @param handlers 消息句柄
     */
    protected final void reload(Executor executor,
            Set<ITaskStatusListener> taskStatusListeners, Set<Handler> handlers)
    {
        this.mExecutor = executor;
        if (null != handlers)
        {
            this.mShareHandlers = handlers;
        }
        if (null != taskStatusListeners)
        {
            this.shareTaskStatusListeners = taskStatusListeners;
        }
        
    }
    
    /**
     * 开始任务
     * @throws Exception 自定义装载器异常
     */
    protected final void start() throws Exception
    {
        Logger.i(TAG, "TaskOperation.start:taskId=" + getId());
        if (mAction.intValue() != ACTION_NONE)
        {
            return;
        }
        int status = getStatus();
        if (status != TASK_STATUS_NEW && status != TASK_STATUS_STOPPED
                && status != TASK_STATUS_ERROR)
        {
            return;
        }
        if (!onPreStart())
        {
            return;
        }
        synchronized (mAction)
        {
            this.mAction = ACTION_START;
            resetOnStart();
            if (!isBackground && mExecutor instanceof ThreadPoolExecutor)
            {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) mExecutor;
                Logger.i(TAG,
                        "Active size : " + threadPoolExecutor.getActiveCount()
                                + ", Pool size : "
                                + threadPoolExecutor.getCorePoolSize());
                if (threadPoolExecutor.getActiveCount() >= threadPoolExecutor.getCorePoolSize())
                {
                    onWaited();
                }
            }
            Logger.i(TAG, "TaskOperation.start --- executor.execute :taskId="
                    + getId());
            mExecutor.execute(this);
        }
        
    }
    
    /**
     * 暂停任务
     * @throws Exception 异常
     */
    protected final void stop() throws Exception
    {
        if (mAction.intValue() != ACTION_NONE)
        {
            return;
        }
        int status = getStatus();
        if (!onPreStop())
        {
            return;
        }
        if (status == TASK_STATUS_WARTING)
        {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) mExecutor;
            threadPoolExecutor.remove(this);
            onStopped();
            return;
        }
        if (status != TASK_STATUS_RUNNING && status != TASK_STATUS_PROCESS)
        {
            return;
        }
        this.mAction = ACTION_STOP;
    }
    
    /**
     * 重新开始任务
     * @throws Exception 异常
     */
    protected final void restart() throws Exception
    {
        if (mAction.intValue() != ACTION_NONE)
        {
            return;
        }
        int status = getStatus();
        if (status != TASK_STATUS_FINISHED && status != TASK_STATUS_ERROR)
        {
            return;
        }
        if (!onPreRestart())
        {
            return;
        }
        synchronized (mAction)
        {
            this.mAction = ACTION_RESTART;
            resetOnRestart();
            if (!isBackground() && mExecutor instanceof ThreadPoolExecutor)
            {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) mExecutor;
                Logger.i(TAG,
                        "Active size : " + threadPoolExecutor.getActiveCount()
                                + ", Pool size : "
                                + threadPoolExecutor.getLargestPoolSize());
                if (threadPoolExecutor.getActiveCount() >= threadPoolExecutor.getLargestPoolSize())
                {
                    onWaited();
                }
            }
            mExecutor.execute(this);
        }
    }
    
    /**
     * 删除任务
     * @throws Exception 异常
     */
    protected final void delete() throws Exception
    {
        if (mAction.intValue() != ACTION_NONE)
        {
            return;
        }
        if (!onPreDelete())
        {
            return;
        }
        this.mAction = ACTION_DELETE;
        int status = getStatus();
        if (status == TASK_STATUS_WARTING)
        {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) mExecutor;
            threadPoolExecutor.remove(this);
            onDeleted();
            return;
        }
        if (status != TASK_STATUS_RUNNING && status != TASK_STATUS_PROCESS)
        {
            onDeleted();
        }
    }
    
    /**
     * 获取当前任务行为
     * @return the action
     */
    public final Integer getAction()
    {
        return mAction;
    }
    
    /**
     * 赋予当前任务行为
     * @param action Integer
     */
    protected final void setAction(Integer action)
    {
        this.mAction = action;
    }
    
    /**
     * 线程实现
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        ITaskDriver driver = null;
        try
        {
            int action = getAction();
            if (action != ACTION_STOP && action != ACTION_DELETE)
            {
                driver = getTaskDriver();
                int reconnect = RECONNECT_NUM;
                onRunning();
                while (--reconnect >= 0 && action != ACTION_DELETE)
                {
                    try
                    {
                        driver.connect();
                        reconnect = RECONNECT_NUM;
                        driver.read();
                        break;
                    }
                    catch (TaskException ex)
                    {
                        Logger.w(TAG,
                                "task is running: download failed : id is "
                                        + mId + " ,reconnect is " + reconnect);
                        if (reconnect == 0
                                || ex.getCode() != TaskException.SERVER_CONNECT_FAILED)
                        {
                            throw ex;
                        }
                    }
                }
            }
            updateStatus();
        }
        catch (TaskException ex)
        {
            Logger.e(TAG, "Download failed : id is " + mId, ex);
            if (getAction() == ACTION_DELETE)
            {
                try
                {
                    onDeleted();
                }
                catch (TaskException e)
                {
                    onError(ex);
                }
            }
            else
            {
                onError(ex);
            }
        }
        finally
        {
            if (driver != null)
            {
                driver.close();
            }
        }
    }
    
    /**
     * 获取任务ID
     * @return int
     * @see com.huawei.basic.android.im.component.load.task.ITask#getId()
     */
    public final int getId()
    {
        return mId;
    }
    
    /**
     * 赋予任务ID
     * @param id int
     */
    public final void setId(int id)
    {
        this.mId = id;
    }
    
    /**
     * 获取任务名
     * @return String
     * @see com.huawei.basic.android.im.component.load.task.ITask#getName()
     */
    public final String getName()
    {
        return mName;
    }
    
    /**
     * 赋予任务名 
     * @param name String
     */
    public final void setName(String name)
    {
        this.mName = name;
    }
    
    /**
     * 任务创建时间
     * @return 创建时间
     * @see com.huawei.basic.android.im.component.load.task.ITask#getCreatedTime()
     */
    public Date getCreatedTime()
    {
        return createdTime;
    }
    
    /**
     * 任务创建时间
     * @param createdTime Date
     */
    public void setCreatedTime(Date createdTime)
    {
        this.createdTime = createdTime;
    }
    
    /**
     * 任务大小
     * @return 任务大小
     * @see com.huawei.basic.android.im.component.load.task.ITask#getTotalSize()
     */
    public final long getTotalSize()
    {
        return mSize > 0 ? mSize : 0;
    }
    
    /**
     * 任务大小
     * @param si long
     */
    public final void setTotalSize(long si)
    {
        this.mSize = si;
    }
    
    /**
     * 获取任务是否对外可见
     * @return boolean
     * @see com.huawei.basic.android.im.component.load.task.ITask#isBackground()
     */
    public final boolean isBackground()
    {
        return false;
    }
    
    /**
     * 设置任务是否对外可见
     * @param isbackground boolean
     */
    public final void setBackground(boolean isbackground)
    {
        this.isBackground = isbackground;
    }
    
    /**
     * 获取任务当前完成大小
     * @return long
     */
    public final long getCurrentSize()
    {
        return mCurrentSize;
    }
    
    /**
     * 赋予任务当前完成大小
     * @param currentSize long
     */
    public final void setCurrentSize(long currentSize)
    {
        this.mCurrentSize = currentSize;
    }
    
    /**
     * 获取任务状态
     * @return int
     * @see com.huawei.basic.android.im.component.load.task.ITask#getStatus()
     */
    public final int getStatus()
    {
        return mStatus;
    }
    
    /**
     * 设置任务状态
     * @param status int
     */
    public final void setStatus(int status)
    {
        this.mStatus = status;
    }
    
    /**
     * 获取是否更新进度
     * @return boolean
     */
    protected boolean isUpdatedTotal()
    {
        return isUpdatedTotal;
    }
    
    /**
     * 设置是否更新进度
     * @param isUpdatedTotal boolean
     */
    protected void setIsUpdatedTotal(boolean isUpdatedTotal)
    {
        this.isUpdatedTotal = isUpdatedTotal;
    }
    
    /**
     * 获取任务异常
     * @return TaskException
     * @see com.huawei.basic.android.im.component.load.task.ITask#getTaskException()
     */
    public TaskException getTaskException()
    {
        return mTaskException;
    }
    
    /**
     * 获取任务完成进度
     * @return 完成进度
     */
    public final int getPercent()
    {
        long totalSize = getTotalSize();
        long currentSize = getCurrentSize();
        Logger.i(TAG, "fileTotalSize=" + totalSize + ",fileCurrentSize="
                + currentSize);
        if (totalSize <= 0)
        {
            return 0;
        }
        if (currentSize != 0 && currentSize == totalSize)
        {
            return 100;
        }
        else
        {
            return (int) (currentSize * 100 / (totalSize + 1));
        }
    }
    
    /**
     * 创建任务前的初始化工作是否完成
     * @return boolean
     * @throws TaskException 自定义装载器异常
     */
    public boolean onPreCreate() throws TaskException
    {
        return true;
    }
    
    /**
     * 开始任务前的初始化工作是否完成
     * @return boolean
     * @throws TaskException 自定义装载器异常
     */
    public boolean onPreStart() throws TaskException
    {
        return true;
    }
    
    /**
     * 重新开始任务前的初始化工作是否完成
     * @return boolean
     * @throws TaskException 自定义装载器异常
     */
    public boolean onPreRestart() throws TaskException
    {
        return true;
    }
    
    /**
     * 停止任务前的初始化工作是否完成
     * @return boolean
     * @throws TaskException 自定义装载器异常
     */
    public boolean onPreStop() throws TaskException
    {
        return true;
    }
    
    /**
     * 删除任务前的初始化工作是否完成
     * @return boolean
     * @throws TaskException 自定义装载器异常
     */
    public boolean onPreDelete() throws TaskException
    {
        return true;
    }
    
    /**
     * 创建任务后的状态设置
     * @throws TaskException 自定义装载器异常
     */
    public final void onCreated() throws TaskException
    {
        setStatus(TASK_STATUS_NEW);
        notifyAllStatus();
        this.mAction = ACTION_NONE;
    }
    
    /**
     * 开始任务后的状态设置
     * @throws TaskException 自定义装载器异常
     */
    public final void onWaited() throws TaskException
    {
        setStatus(TASK_STATUS_WARTING);
        notifyAllStatus();
        this.mAction = ACTION_NONE;
    }
    
    /**
     * 运行任务后的状态设置
     * @throws TaskException 自定义装载器异常
     */
    public final void onRunning() throws TaskException
    {
        setStatus(TASK_STATUS_RUNNING);
        notifyAllStatus();
        this.mAction = ACTION_NONE;
    }
    
    /**
     * 任务进行中的状态通知 
     * @throws TaskException 自定义装载器异常
     */
    public final void onProgress() throws TaskException
    {
        setStatus(TASK_STATUS_PROCESS);
        notifyAllStatus();
    }
    
    /**
     * 暂停任务后的状态设置
     * @throws TaskException 自定义装载器异常
     */
    public final void onStopped() throws TaskException
    {
        setStatus(TASK_STATUS_STOPPED);
        notifyAllStatus();
        this.mAction = ACTION_NONE;
    }
    
    /**
     * 删除任务后的状态设置
     * @throws TaskException 自定义装载器异常
     */
    public final void onDeleted() throws TaskException
    {
        resetOnDeleted();
        setStatus(TASK_STATUS_DELETED);
        notifyAllStatus();
        this.mAction = ACTION_NONE;
    }
    
    /**
     * 任务完成后的状态设置
     * @throws TaskException 自定义装载器异常
     */
    public final void onFinish() throws TaskException
    {
        Logger.i(TAG, "TaskOperation.onFinish :taskId=" + getId());
        setStatus(TASK_STATUS_FINISHED);
        this.mAction = ACTION_NONE;
        notifyAllStatus();
    }
    
    /**
     * 任务过程中出错的状态设置
     * @param ex Exception 自定义装载器异常
     */
    public final void onError(TaskException ex)
    {
        Logger.i(TAG, "TaskOperation.onError :taskId=" + getId(), ex);
        setStatus(TASK_STATUS_ERROR);
        this.mTaskException = ex;
        notifyAllStatus();
        this.mAction = ACTION_NONE;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void addOwnerHandler(Handler handler)
    {
        mOwnerHandlers.add(handler);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void removeOwnerHandler(Handler handler)
    {
        mOwnerHandlers.remove(handler);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void addOwnerStatusListener(
            ITaskStatusListener downloadStatusListener)
    {
        this.ownerTaskStatusListeners.add(downloadStatusListener);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void removeOwnerStatusListener(
            ITaskStatusListener downloadStatusListener)
    {
        this.ownerTaskStatusListeners.remove(downloadStatusListener);
    }
    
    /**
     * 状态变化通知所有监听器和消息句柄 
     * @param status
     */
    private void notifyAllStatus()
    {
        ITaskStatusListener[] shareArray = new ITaskStatusListener[shareTaskStatusListeners.size()];
        shareTaskStatusListeners.toArray(shareArray);
        for (ITaskStatusListener downloadStatusListener : shareArray)
        {
            downloadStatusListener.onChangeStatus(this);
        }
        ITaskStatusListener[] downloadArray = new ITaskStatusListener[ownerTaskStatusListeners.size()];
        ownerTaskStatusListeners.toArray(downloadArray);
        for (ITaskStatusListener downloadStatusListener : downloadArray)
        {
            downloadStatusListener.onChangeStatus(this);
        }
        Handler[] shareHandlerArray = new Handler[mShareHandlers.size()];
        mShareHandlers.toArray(shareHandlerArray);
        for (Handler handler : shareHandlerArray)
        {
            handler.sendMessage(handler.obtainMessage(getStatus(), this));
        }
        Handler[] ownerHandlerArray = new Handler[mOwnerHandlers.size()];
        for (Handler handler : ownerHandlerArray)
        {
            handler.sendMessage(handler.obtainMessage(getStatus(), this));
        }
    }
    
    /**
     * 更新任务状态
     * @throws Exception
     */
    private void updateStatus() throws TaskException
    {
        switch (mAction.intValue())
        {
            case ACTION_DELETE:
                onDeleted();
                break;
            case ACTION_STOP:
                onStopped();
                break;
            default:
                onFinish();
                break;
        }
    }
}
