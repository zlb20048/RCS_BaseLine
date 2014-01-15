/*
 * 文件名: LoadTaskManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 任务管理器
 * 创建人: deanye
 * 创建时间:2012-4-18
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.task;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Handler;

import com.huawei.basic.android.im.component.load.adapter.ITaskDataAdapter;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 任务管理器
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-18] 
 */
public class TaskManager implements ITaskManager
{
    
    /**
     * TAG
     */
    private static final String TAG = "LoadTaskManager";
    
    /**
     * 默认任务管理是可以自动重新启动上次的任务的
     */
    private boolean isResumeTasks = true;
    
    /**
     * 任务列表
     */
    private List<TaskOperation> mLoadTasks = null;
    
    /**
     * 数据库适配操作
     */
    private ITaskDataAdapter mTaskDataAdapter = null;
    
    /**
     * 消息机制
     */
    private Set<Handler> mHandlers = new LinkedHashSet<Handler>();
    
    /**
     * 监听
     */
    private Set<ITaskStatusListener> mTaskStatusListeners = new LinkedHashSet<ITaskStatusListener>();
    
    /**
     * 线程池
     */
    private Executor fixedThreadPoolExecutor = null;
    
    /**
     * 线程池
     */
    private Executor cacheThreadPoolExecutor = Executors.newCachedThreadPool();
    
    /**
     * 当前最大的任务id
     */
    private int mNextTaskId = -1;
    
    /**
     * 创建任务管理器
     * @param taskDataAdapter 数据库操作
     * @param taskStatusListener ITaskStatusListener
     * @param maxTaskNumber int
     */
    public TaskManager(ITaskDataAdapter taskDataAdapter,
            ITaskStatusListener taskStatusListener, int maxTaskNumber)
    {
        fixedThreadPoolExecutor = Executors.newFixedThreadPool(maxTaskNumber);
        this.mTaskDataAdapter = taskDataAdapter;
        if (null != taskStatusListener)
        {
            mTaskStatusListeners.add(taskStatusListener);
        }
        mTaskStatusListeners.add(new ITaskStatusListener()
        {
            public void onChangeStatus(ITask task)
            {
                TaskOperation taskOperation = (TaskOperation) task;
                int id = taskOperation.getId();
                int status = taskOperation.getStatus();
                switch (status)
                {
                    case ITask.TASK_STATUS_NEW:
                        Logger.i(TAG, "download status is NEW !");
                        if (taskOperation.isSave() && null != mTaskDataAdapter)
                        {
                            mTaskDataAdapter.addTask(taskOperation);
                        }
                        getAllTask().add(0, taskOperation);
                        break;
                    case ITask.TASK_STATUS_WARTING:
                        Logger.i(TAG, "download status is WARTING !");
                        if (taskOperation.isSave() && null != mTaskDataAdapter)
                        {
                            mTaskDataAdapter.updateTaskStatus(id,
                                    status,
                                    taskOperation.getClass());
                        }
                        break;
                    case ITask.TASK_STATUS_RUNNING:
                        Logger.i(TAG, "download status is RUNNING !");
                        if (taskOperation.isSave() && null != mTaskDataAdapter)
                        {
                            mTaskDataAdapter.updateTaskStatus(id,
                                    status,
                                    taskOperation.getClass());
                         
                        }
                        break;
                    case ITask.TASK_STATUS_DELETED:
                        Logger.i(TAG, "download status is DELETED !");
                        if (taskOperation.isSave() && null != mTaskDataAdapter)
                        {
                            mTaskDataAdapter.deleteTask(id,
                                    taskOperation.getClass());
                        }
                        getAllTask().remove(taskOperation);
                        break;
                    case ITask.TASK_STATUS_PROCESS:
                        if (!taskOperation.isUpdatedTotal())
                        {
                            mTaskDataAdapter.updateTotalSize(id,
                                    taskOperation.getTotalSize(),
                                    taskOperation.getClass());
                            taskOperation.setIsUpdatedTotal(true);
                        }
                        Logger.i(TAG, "download status is PROCESS !");
                        break;
                    case ITask.TASK_STATUS_STOPPED:
                        Logger.i(TAG, "download status is STOPPED !");
                        if (taskOperation.isSave() && null != mTaskDataAdapter)
                        {
                            mTaskDataAdapter.updateTaskStatus(id,
                                    status,
                                    taskOperation.getClass());
                        }
                        break;
                    case ITask.TASK_STATUS_FINISHED:
                        Logger.i(TAG, "download status is FINISHED !");
                        if (taskOperation.isSave() && null != mTaskDataAdapter)
                        {
                            mTaskDataAdapter.updateTaskStatus(id,
                                    status,
                                    taskOperation.getClass());
                        }
                        break;
                    case ITask.TASK_STATUS_ERROR:
                        Logger.i(TAG, "download status is ERROR !");
                        if (taskOperation.isSave() && null != mTaskDataAdapter)
                        {
                            mTaskDataAdapter.updateTaskStatus(id,
                                    status,
                                    taskOperation.getClass());
                        }
                        break;
                }
            }
        });
        
    }
    
    /**
     * 
     * 任务是否已经创建
     * @param task ITask 任务对象
     * @return 是否已经创建任务
     */
    public boolean isExist(ITask task)
    {
        TaskOperation t = (TaskOperation) task;
        for (TaskOperation taskOperation : getAllTask())
        {
            if (taskOperation.equals(t))
            {
                t.setId(taskOperation.getId());
                return true;
            }
        }
        return false;
    }
    
    /**
     * 创建任务
     * @param task ITask
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#createTask(com.huawei.basic.android.im.component.load.task.ITask)
     */
    @Override
    public void createTask(ITask task) throws TaskException
    {
        if (isExist(task))
        {
            throw new TaskException(TaskException.TASK_IS_EXIST);
        }
        try
        {
            ((TaskOperation) task).setId(getNextTaskId());
            ((TaskOperation) task).setTaskDataAdapter(mTaskDataAdapter);
            Logger.i(TAG, "TaskManager.createTask:taskId=" + task.getId());
            Executor executor = task.isBackground() ? cacheThreadPoolExecutor
                    : fixedThreadPoolExecutor;
            ((TaskOperation) task).create(executor,
                    mTaskStatusListeners,
                    mHandlers);
        }
        catch (Exception ex)
        {
            Logger.i(TAG, "create task failed", ex);
            throw new TaskException(TaskException.CREATE_TASK_FAILED);
        }
        
    }
    
    /**
     * 根据任务id 开始任务
     * @param id 任务id
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#startTask(int)
     */
    @Override
    public void startTask(int id) throws TaskException
    {
        Logger.i(TAG, "TaskManager.startTask:taskId=" + id);
        
        TaskOperation taskOperation = findTaskById(id);
        if (taskOperation == null)
        {
            Logger.e(TAG, "Task not found : Task id is " + id);
            throw new TaskException(TaskException.TASK_NOT_FOUND);
        }
        try
        {
            taskOperation.start();
        }
        catch (Exception ex)
        {
            Logger.e(TAG, "start task failed", ex);
            throw new TaskException(TaskException.START_TASK_FAILED);
        }
    }
    
    /**
     * 重新开始任务
     * @param id 任务id
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#restartTask(int)
     */
    @Override
    public void restartTask(int id) throws TaskException
    {
        TaskOperation task = findTaskById(id);
        if (task == null)
        {
            Logger.e(TAG, "Task not found : Id is " + id);
            throw new TaskException(TaskException.TASK_NOT_FOUND);
        }
        try
        {
            task.restart();
        }
        catch (Exception ex)
        {
            Logger.e(TAG, "Restart task failed : Id is " + id, ex);
            throw new TaskException(TaskException.RESTART_TASK_FAILED);
        }
    }
    
    /**
     * 根据任务id停止任务
     * @param id int
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#stopTask(int)
     */
    @Override
    public void stopTask(int id) throws TaskException
    {
        TaskOperation task = findTaskById(id);
        if (task == null)
        {
            Logger.e(TAG, "Task not found : Id is " + id);
            throw new TaskException(TaskException.TASK_NOT_FOUND);
        }
        try
        {
            task.stop();
        }
        catch (Exception ex)
        {
            throw new TaskException(TaskException.STOP_TASK_FAILED);
        }
    }
    
    /**
     * 根据任务id删除任务
     * @param id 任务id 
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#deleteTask(int)
     */
    @Override
    public void deleteTask(int id) throws TaskException
    {
        TaskOperation task = findTaskById(id);
        if (task == null)
        {
            Logger.e(TAG, "Task not found : Id is " + id);
            throw new TaskException(TaskException.TASK_NOT_FOUND);
        }
        try
        {
            task.delete();
        }
        catch (Exception ex)
        {
            Logger.e(TAG, "Delete task failed : Id is " + id, ex);
            throw new TaskException(TaskException.DELETE_TASK_FAILED);
        }
    }
    
    /**
     * 根据任务id查找某个任务
     * @param id 任务id 
     * @return TaskOperation
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#findLoadTaskById(int)
     */
    @Override
    public TaskOperation findTaskById(int id)
    {
        List<TaskOperation> tasks = getAllTask();
        synchronized (tasks)
        {
            for (TaskOperation task : tasks)
            {
                if (task.getId() == id)
                {
                    return task;
                }
            }
        }
        return null;
    }
    
    /**
     * 开始所有任务
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#startAllTask()
     */
    @Override
    public void startAllTask() throws TaskException
    {
        TaskException taskException = null;
        List<TaskOperation> tasks = getAllTask();
        synchronized (tasks)
        {
            for (int i = tasks.size() - 1; i >= 0; i--)
            {
                TaskOperation task = tasks.get(i);
                if (!task.isBackground())
                {
                    try
                    {
                        // 根据任务id开始任务
                        startTask(task.getId());
                    }
                    catch (Exception ex)
                    {
                        Logger.e(TAG,
                                "Start all task failed : Id is " + task.getId(),
                                ex);
                        if (null == taskException)
                        {
                            taskException = new TaskException(
                                    TaskException.START_TASK_FAILED);
                        }
                    }
                }
            }
        }
        
        //如果有异常抛出异常
        if (null != taskException)
        {
            throw taskException;
        }
    }
    
    /**
     * 停止所有任务
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#stopAllTask()
     */
    @Override
    public void stopAllTask() throws TaskException
    {
        TaskException taskException = null;
        List<TaskOperation> tasks = getAllTask();
        synchronized (tasks)
        {
            for (TaskOperation task : tasks)
            {
                if (!task.isBackground())
                {
                    try
                    {
                        stopTask(task.getId());
                    }
                    catch (Exception ex)
                    {
                        Logger.e(TAG,
                                "Stop all task failed : Id is " + task.getId(),
                                ex);
                        if (null == taskException)
                        {
                            taskException = new TaskException(
                                    TaskException.STOP_TASK_FAILED);
                        }
                    }
                }
            }
        }
        
        //如果有异常抛出异常
        if (null != taskException)
        {
            throw taskException;
        }
        
    }
    
    /**
     * 删除所有任务
     * @throws TaskException 自定义装载器异常
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#deleteAllTask()
     */
    @Override
    public void deleteAllTask() throws TaskException
    {
        TaskException taskException = null;
        List<TaskOperation> tasks = getAllTask();
        synchronized (tasks)
        {
            //倒着remove这样做不会越界
            for (int i = tasks.size() - 1; i >= 0; i--)
            {
                ITask task = tasks.get(i);
                if (!task.isBackground())
                {
                    try
                    {
                        deleteTask(task.getId());
                    }
                    catch (Exception ex)
                    {
                        Logger.e(TAG,
                                "Delete all task failed : Id is "
                                        + task.getId(),
                                ex);
                        if (null == taskException)
                        {
                            taskException = new TaskException(
                                    TaskException.DELETE_TASK_FAILED);
                        }
                    }
                }
            }
        }
        
        //如果有异常抛出异常
        if (null != taskException)
        {
            throw taskException;
        }
    }
    
    /**
     * 添加消息句柄
     * @param handler Handler
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#addHandler(android.os.Handler)
     */
    @Override
    public void addHandler(Handler handler)
    {
        mHandlers.add(handler);
        
    }
    
    /**
     * 移除消息句柄
     * @param handler Handler
     * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#removeHandler(android.os.Handler)
     */
    @Override
    public void removeHandler(Handler handler)
    {
        mHandlers.remove(handler);
    }
    
    /**
     * 给任务加入一些特定的监听者
     * @param taskStatusListener ITaskStatusListener
     * @see com.huawei.basic.android.im.component.load.task.ITaskManager#addTaskStatusListener(com.huawei.basic.android.im.component.load.task.ITaskStatusListener)
     */
    @Override
    public void addTaskStatusListener(ITaskStatusListener taskStatusListener)
    {
        mTaskStatusListeners.add(taskStatusListener);
    }
    
    /**
     * 
     * 移除加入的一些监听者
     * @param taskStatusListener ITaskStatusListener
     * @see com.huawei.basic.android.im.component.load.task.ITaskManager#removeTaskStatusListener(com.huawei.basic.android.im.component.load.task.ITaskStatusListener)
     */
    @Override
    public void removeTaskStatusListener(ITaskStatusListener taskStatusListener)
    {
        mTaskStatusListeners.remove(taskStatusListener);
    }
    
    /**
     * 重新恢复下次没做完的任务
     * @see com.huawei.basic.android.im.component.load.task.ITaskManager#startLastTasks()
     */
    @Override
    public void startLastTasks()
    {
        Logger.i(TAG, "startService STATUS_PROCESS");
        List<Integer> status = new ArrayList<Integer>();
        status.add(ITask.TASK_STATUS_NEW);
        status.add(ITask.TASK_STATUS_RUNNING);
        status.add(ITask.TASK_STATUS_WARTING);
        status.add(ITask.TASK_STATUS_PROCESS);
        startUpByStatus(status);
    }
    
    /**
     * 获取可见的任务列表
     * @return 任务列表
     * @see com.huawei.basic.android.im.component.load.task.ITaskManager#getDisplayTasks()
     */
    @Override
    public List<ITask> getDisplayTasks()
    {
        List<ITask> tasklist = new LinkedList<ITask>();
        List<TaskOperation> taskOperationTasks = getAllTask();
        synchronized (tasklist)
        {
            for (TaskOperation task : taskOperationTasks)
            {
                if (!task.isBackground())
                {
                    tasklist.add(task);
                }
            }
        }
        return tasklist;
    }
    
    /**
     * 获取可见的后台线程的个数
     * @return int 后台线程的个数
     */
    @Override
    public int getDisplayTaskAmount()
    {
        return getDisplayTasks().size();
    }
    
    /**
     * 获取所有任务
     * @return List<TaskOperation> 所有任务
     */
    private List<TaskOperation> getAllTask()
    {
        if (null == mLoadTasks && null != mTaskDataAdapter)
        {
            mLoadTasks = new Vector<TaskOperation>();
            for (TaskOperation task : mTaskDataAdapter.getAllTask())
            {
                Executor executor = task.isBackground() ? cacheThreadPoolExecutor
                        : fixedThreadPoolExecutor;
                task.reload(executor, mTaskStatusListeners, mHandlers);
                task.setIsUpdatedTotal(true);
                mLoadTasks.add(task);
            }
        }
        return mLoadTasks;
    }
    
    /**
     * 根据当前任务状态开始任务
     * @param targetStatus List<Integer> 任务状态
     */
    private void startUpByStatus(List<Integer> targetStatus)
    {
        if (!isResumeTasks)
        {
            return;
        }
        List<TaskOperation> tasks = getAllTask();
        synchronized (tasks)
        {
            for (TaskOperation task : tasks)
            {
                task.resetOnReload();
                int status = task.getStatus();
                int id = task.getId();
                try
                {
                    if (targetStatus.contains(status))
                    {
                        if (task.isResumeTask())
                        {
                            task.setStatus(ITask.TASK_STATUS_NEW);
                            startTask(id);
                        }
                        else
                        {
                            task.setStatus(ITask.TASK_STATUS_STOPPED);
                        }
                    }
                }
                catch (Exception ex)
                {
                    Logger.e(TAG, ex.getMessage(), ex);
                }
            }
        }
    }
    
    /**
     * 算最大ID号 
     * @return 最大id号 
     */
    private int getNextTaskId()
    {
        if (mNextTaskId == -1 && null != mTaskDataAdapter)
        {
            mNextTaskId = mTaskDataAdapter.getMaxTaskId();
        }
        return ++mNextTaskId;
    }
    
}
