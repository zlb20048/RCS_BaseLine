/*
 * 文件名: TaskDataProxy.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述:  代理分发数据适配的调用
 * 创建人: deanye
 * 创建时间:2012-4-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.adapter;

import java.util.LinkedList;
import java.util.List;

import com.huawei.basic.android.im.component.load.task.TaskOperation;

/**
 * 代理分发数据适配的调用
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-19] 
 */
public class TaskDataProxy implements ITaskDataAdapter
{
    
    /**
     * 当前系统可能适配的任务数据操作的对象的数组
     */
    private ITaskDataAdapter[] mTaskDataAdapters;
    
    /**
     * 构造器
     * @param loadTaskDataAdapter 当前系统可能适配的任务数据操作的对象
     */
    public TaskDataProxy(ITaskDataAdapter... loadTaskDataAdapter)
    {
        mTaskDataAdapters = loadTaskDataAdapter;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public LinkedList<TaskOperation> getAllTask()
    {
        LinkedList<TaskOperation> loadTaskOperationlists = new LinkedList<TaskOperation>();
        for (ITaskDataAdapter taskDataAdapter : mTaskDataAdapters)
        {
            List<TaskOperation> taskOperations = taskDataAdapter.getAllTask();
            if (taskOperations != null)
            {
                loadTaskOperationlists.addAll(taskOperations);
            }
        }
        return loadTaskOperationlists;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public int addTask(TaskOperation task)
    {
        for (ITaskDataAdapter taskDataAdapter : mTaskDataAdapters)
        {
            if (task.getClass()
                    .isAssignableFrom(taskDataAdapter.getTaskClass()))
            {
                return taskDataAdapter.addTask(task);
            }
        }
        return 0;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public void deleteTask(int id, Class<?> taskClass)
    {
        for (ITaskDataAdapter taskDataAdapter : mTaskDataAdapters)
        {
            if (taskClass.isAssignableFrom(taskDataAdapter.getTaskClass()))
            {
                taskDataAdapter.deleteTask(id, taskClass);
            }
        }
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public void updateTaskStatus(int id, int status, Class<?> taskClass)
    {
        for (ITaskDataAdapter taskDataAdapter : mTaskDataAdapters)
        {
            if (taskClass.isAssignableFrom(taskDataAdapter.getTaskClass()))
            {
                taskDataAdapter.updateTaskStatus(id, status, taskClass);
            }
        }
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public void updateTotalSize(int id, long fileSize, Class<?> taskClass)
    {
        for (ITaskDataAdapter taskDataAdapter : mTaskDataAdapters)
        {
            if (taskClass.isAssignableFrom(taskDataAdapter.getTaskClass()))
            {
                taskDataAdapter.updateTotalSize(id, fileSize, taskClass);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TaskOperation getTask(int id, Class<?> taskClass)
    {
        for (ITaskDataAdapter taskDataAdapter : mTaskDataAdapters)
        {
            if (taskClass.isAssignableFrom(taskDataAdapter.getTaskClass()))
            {
                return taskDataAdapter.getTask(id, taskClass);
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTaskClass()
    {
        //这个类是个代理类不需要知道类型 所以返回空
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxTaskId()
    {
        //获取多种task的最终ID
        int maxId = -1;
        for (ITaskDataAdapter taskDataAdapter : mTaskDataAdapters)
        {
            int taskid = taskDataAdapter.getMaxTaskId();
            maxId = maxId > taskid ? maxId : taskid;
        }
        return maxId;
    }
}
