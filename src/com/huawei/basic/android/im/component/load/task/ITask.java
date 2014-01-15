/*
 * 文件名: ILoadTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-17
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.task;

import java.util.Date;

import android.os.Handler;

/**
 * 任务抽象接口
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-17] 
 */
public interface ITask
{
    
    /**
     * 新任务
     */
    int TASK_STATUS_NEW = 0;
    
    /**
     * 运行中任务
     */
    int TASK_STATUS_RUNNING = 1;
    
    /**
     * 任务进度
     */
    int TASK_STATUS_PROCESS = 2;
    
    /**
     * 停止任务
     */
    int TASK_STATUS_STOPPED = 3;
    
    /**
     * 删除任务
     */
    int TASK_STATUS_DELETED = 4;
    
    /**
     * 关闭任务
     */
    int TASK_STATUS_FINISHED = 5;
    
    /**
     * 等待任务
     */
    int TASK_STATUS_WARTING = 6;
    
    /**
     * 出错任务
     */
    int TASK_STATUS_ERROR = 7;
    
    /**
     * 任务id
     * @return 任务id
     */
    public int getId();
    
    /**
     * 任务名
     * @return 任务名
     */
    String getName();
    
    /**
     * 是否在后台运行
     * @return 是否在后台运行 boolean
     */
    public boolean isBackground();
    
    /**
     * 任务状态
     * @return  任务状态
     */
    public int getStatus();
    
    /**
     * 完成进度
     * @return 完成进度
     */
    public int getPercent();
    
    /**
     * 任务大小
     * @return 任务大小
     */
    public long getTotalSize();
    
    /**
     * 创建任务时间
     * @return Date
     */
    public Date getCreatedTime();
    
    /**
     * 是可以自动重新启动上次的任务的
     * @return boolean
     */
    public boolean isResumeTask();
    
    /**
     * 添加一个handler消息句柄
     * @param handler Handler
     */
    public void addOwnerHandler(Handler handler);
    
    /**
     * 移除某个handler消息句柄
     * @param handler Handler
     */
    public void removeOwnerHandler(Handler handler);
    
    /**
     * 添加一个监听
     * @param taskStatusListener ITaskStatusListener
     */
    public void addOwnerStatusListener(ITaskStatusListener taskStatusListener);
    
    /**
     * 移除某个监听 
     * @param taskStatusListener ITaskStatusListener
     */
    public void removeOwnerStatusListener(ITaskStatusListener taskStatusListener);
    
    /**
     * 获取任务当前完成大小
     * @return long
     */
    public long getCurrentSize();
    
    /**
     * 获取任务异常
     * @return TaskException
     */
    public TaskException getTaskException();
    
}
