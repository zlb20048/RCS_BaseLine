/*
 * 文件名: ILoadStatusListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-18
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.task;

/**
 * 回调的监听
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-18] 
 */
public interface ITaskStatusListener
{
    /**
     * 任务改变通知监听
     * @param loadTask 任务详情
     */
    public void onChangeStatus(ITask loadTask);
}
