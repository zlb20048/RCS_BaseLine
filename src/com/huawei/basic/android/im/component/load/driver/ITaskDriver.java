/*
 * 文件名: ITaskDriver.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述:  适配各种网络驱动,创建各种类型的连接。
 * 创建人: deanye
 * 创建时间:2012-4-17
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.driver;

import com.huawei.basic.android.im.component.load.task.TaskException;

/**
 * 适配各种网络驱动,创建各种类型的连接。
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-17] 
 */
public interface ITaskDriver
{
    
    /**
     * 创建连接
     * @throws TaskException 自定义装载器异常
     */
    public void connect() throws TaskException;
    
    /**
     * 读取数据
     * @throws TaskException 自定义装载器异常
     */
    public void read() throws TaskException;
    
    /**
     * 关闭连接
     */
    public void close();
    
}
