/*
 * 文件名: ITaskManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 任务管理器接口
 * 创建人: deanye
 * 创建时间:2012-4-17
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.task;

import java.util.List;

import android.os.Handler;

/**
 * 任务管理器接口
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-17] 
 */
public interface ITaskManager
{
    
    /**
     * 创建任务
     * @param task 任务对象
     * @throws TaskException 自定义装载器异常
     */
    public void createTask(ITask task) throws TaskException;
    
    /**
     * 通过ID开始某个任务
     * @param id 任务id
     * @throws TaskException 自定义装载器异常
     */
    public void startTask(int id) throws TaskException;
    
    /**
     * 通过ID重新开始某个任务
     * @param id 任务id
     * @throws TaskException 自定义装载器异常
     */
    public void restartTask(int id) throws TaskException;
    
    /**
     * 通过ID停止某个任务
     * @param id 任务id
     * @throws TaskException 自定义装载器异常
     */
    public void stopTask(int id) throws TaskException;
    
    /**
     * 通过ID删除某个任务
     * @param id 任务id
     * @throws TaskException 自定义装载器异常
     */
    public void deleteTask(int id) throws TaskException;
    
    /**
     * 通过ID寻找某个任务
     * @param id 任务id
     * @return 任务对象
     */
    public ITask findTaskById(int id);
    
    /**
     * 开始所有任务
     * @throws TaskException 自定义装载器异常
     */
    public void startAllTask() throws TaskException;
    
    /**
     * 停止所有任务
     * @throws TaskException 自定义装载器异常
     */
    public void stopAllTask() throws TaskException;
    
    /**
     * 删除所有任务
     * @throws TaskException 自定义装载器异常
     */
    public void deleteAllTask() throws TaskException;
    
    /**
     * 重新恢复下次没做完的任务
     */
    public void startLastTasks();
    
    /**
     * 获取可见的后台线程
     * @return List<ITask> 获取可见的后台线程
     */
    public List<ITask> getDisplayTasks();
    
    /**
     * 获取可见的后台线程的个数
     * @return int
     */
    public int getDisplayTaskAmount();
    
    /**
     * 添加消息句柄
     * @param handler Handler 消息句柄
     */
    public void addHandler(Handler handler);
    
    /**
     * 移除消息句柄
     * @param handler Handler 消息句柄
     */
    public void removeHandler(Handler handler);
    
    /**
     * 给任务加入一些特定的监听者
     * @param taskStatusListener ITaskStatusListener 监听者
     */
    public void addTaskStatusListener(ITaskStatusListener taskStatusListener);
    
    /**
     * 移除加入的一些监听者
     * @param taskStatusListener ITaskStatusListener 监听者
     */
    public void removeTaskStatusListener(ITaskStatusListener taskStatusListener);
    
}
