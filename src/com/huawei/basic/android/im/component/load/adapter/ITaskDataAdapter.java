/*
 * 文件名: ITaskDataAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述:  数据库处理适配接口
 * 创建人: deanye
 * 创建时间:2012-4-18
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.adapter;

import java.util.LinkedList;

import com.huawei.basic.android.im.component.load.task.TaskOperation;

/**
 * 数据库操作 <BR>
 * 数据库处理适配接口
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-18] 
 */
public interface ITaskDataAdapter
{
    
    /**
     * 获取所有任务
     * @return 所有任务集合
     */
    public LinkedList<TaskOperation> getAllTask();
    
    /**
     * 添加任务
     * @param task 被添加的任务
     * @return 任务ID
     */
    public int addTask(TaskOperation task);
    
    /**
     * 更新任务状态 
     * @param id 任务id 
     * @param status 任务状态
     * @param taskClass Class<?> 
     */
    public void updateTaskStatus(int id, int status, Class<?> taskClass);
    
    /**
     * 根据id获取任务信息
     * @param id 任务id 
     * @param taskClass Class<?> 
     * @return 返回任务对象
     */
    public TaskOperation getTask(int id, Class<?> taskClass);
    
    /**
     * 更新完成的任务大小
     * @param id  任务id
     * @param size 已经完成的任务大小
     * @param taskClass Class<?> 
     */
    public void updateTotalSize(int id, long size, Class<?> taskClass);
    
    /**
     * 判断是否是对应的任务类型
     * @return boolean是否是对应的任务类型
     */
    public Class<?> getTaskClass();
    
    /**
     * 删除任务
     * @param id 任务ID
     * @param taskClass Class<?> 
     */
    void deleteTask(int id, Class<?> taskClass);
    
    /**
     * 获取最大的ID已生成下一个ID
     * @return int ID 
     */
    int getMaxTaskId();
}
