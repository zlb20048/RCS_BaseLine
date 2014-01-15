/*
 * 文件名: LoadException.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 
 * 创建人: deanye
 * 创建时间:2012-4-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.task;

/**
 * 自定义装载器异常
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-14] 
 */
public class TaskException extends Exception
{
    
    /**
     * 创建任务失败
     */
    public static final int CREATE_TASK_FAILED = 1;
    
    /**
     * 开始任务失败
     */
    public static final int START_TASK_FAILED = 2;
    
    /**
     * 重新启动任务失败
     */
    public static final int RESTART_TASK_FAILED = 3;
    
    /**
     * 停止任务失败
     */
    public static final int STOP_TASK_FAILED = 4;
    
    /**
     * 删除任务失败
     */
    public static final int DELETE_TASK_FAILED = 5;
    
    /**
     * 任务已经创建
     */
    public static final int TASK_IS_EXIST = 6;
    
    /**
     * 任务没有发现
     */
    public static final int TASK_NOT_FOUND = 7;
    
    /**
     * 服务器连接失败
     */
    public static final int SERVER_CONNECT_FAILED = 8;
    
    /**
     * 拷贝文件失败
     */
    public static final int WRITE_FILE_FAILED = 9;
    
    /**
     * 序列ID 
     */
    private static final long serialVersionUID = 7513550920244438732L;
    
    /**
     * 异常类型
     */
    private int code;
    
    /**
     * 
     * 构造传入异常信息和类型
     * @param detailMessage 异常信息
     * @param code 类型
     */
    public TaskException(String detailMessage, int code)
    {
        super(detailMessage);
        this.code = code;
    }
    
    /**
     * 
     * 构造传入类型
     * @param code 类型
     */
    public TaskException(int code)
    {
        this.code = code;
    }
    
    /**
     * 获取当前异常类型
     * @return int 异常类型
     */
    public int getCode()
    {
        return code;
    }
    
}
