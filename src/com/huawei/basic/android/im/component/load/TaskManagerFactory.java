/*
 * 文件名: TaskManagerFactory.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 工厂类产生任务管理类的实例
 * 创建人: deanye
 * 创建时间:2012-4-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load;

import android.content.Context;

import com.huawei.basic.android.im.component.download.DownloadDbAdapter;
import com.huawei.basic.android.im.component.load.adapter.ITaskDataAdapter;
import com.huawei.basic.android.im.component.load.adapter.TaskDataProxy;
import com.huawei.basic.android.im.component.load.task.ITaskManager;
import com.huawei.basic.android.im.component.load.task.TaskManager;
import com.huawei.basic.android.im.component.upload.UpLoadDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.PhoneContactIndexDbAdapter;
import com.huawei.basic.android.im.logic.contact.upload.ContactUploadDbAdapter;

/**
 * 工厂类产生任务管理类的实例（可能有多个任务管理实例所以TaskManager不做为单例）
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-19] 
 */
public class TaskManagerFactory
{
    /**
     * 任务管理器对象
     */
    private static ITaskManager taskmanager;
    
    /**
     * 任务管理器对象
     */
    private static ITaskManager uploadContactsManager;
    
    /**
     * 最大任务数
     */
    private static final int MAX_TASK_NUMBER = 3;
    
    private static Context mContext;
    
    /**
     * 初始化
     * @param context Context
     */
    public static void init(Context context)
    {
        TaskManagerFactory.mContext = context;
    }
    
    /**
     * 产生任务管理器对象的对外方法
     * @return ITaskManager 任务管理器对象
     */
    public static ITaskManager getTaskManager()
    {
        if (null == taskmanager)
        {
            //注入可能的数据操作实例
            ITaskDataAdapter taskDataAdapter = new TaskDataProxy(
                    new DownloadDbAdapter(mContext), new UpLoadDbAdapter());
            taskmanager = new TaskManager(taskDataAdapter, null,
                    MAX_TASK_NUMBER);
        }
        return taskmanager;
    }
    
    /**
     * 产生任务管理器对象的对外方法
     * @return ITaskManager 任务管理器对象
     */
    public static ITaskManager getUploadContactManager()
    {
        if (null == uploadContactsManager)
        {
            //注入可能的数据操作实例
            uploadContactsManager = new TaskManager(new ContactUploadDbAdapter(
                    PhoneContactIndexDbAdapter.getInstance(mContext)), null,
                    MAX_TASK_NUMBER);
        }
        return uploadContactsManager;
    }
}
