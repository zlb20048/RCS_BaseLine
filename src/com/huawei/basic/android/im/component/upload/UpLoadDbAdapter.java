/*
 * 文件名: upLoadDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.upload;

import java.util.LinkedList;

import com.huawei.basic.android.im.component.load.adapter.ITaskDataAdapter;
import com.huawei.basic.android.im.component.load.task.TaskOperation;

/**
 * 上传任务数据库的具体实现
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-19] 
 */
public class UpLoadDbAdapter implements ITaskDataAdapter
{
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public LinkedList<TaskOperation> getAllTask()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public int addTask(TaskOperation task)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public void updateTaskStatus(int id, int status, Class<?> taskClass)
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public TaskOperation getTask(int id, Class<?> taskClass)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public void updateTotalSize(int id, long size, Class<?> taskClass)
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public Class<?> getTaskClass()
    {
        // TODO Auto-generated method stub
        return UploadTask.class;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public void deleteTask(int id, Class<?> taskClass)
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public int getMaxTaskId()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
