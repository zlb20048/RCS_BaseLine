/*
 * 文件名: DownloadTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.download;

import java.io.File;
import java.io.IOException;

import com.huawei.basic.android.im.component.load.task.ITask;
import com.huawei.basic.android.im.component.load.task.TaskOperation;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.utils.FileUtil;

/**
 * 具体的某个任务实现类
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-19] 
 */
public abstract class DownloadTask extends TaskOperation
{
    
    /**
     * TAG
     */
    private static final String TAG = "DownloadTask";
    
    /**
     * 缓冲字节
     */
    private byte[] bytesBuf = null;
    
    /**
     * 下载地址
     */
    private String downloadUrl;
    
    /**
     * 是否保存数据库
     */
    private boolean isSave = true;
    
    /**
     * 保存路径
     */
    private String storePath;
    
    /**
     * 超时时间
     */
    private int timeout = 20000;
    
    /**
     * 删除任务是否删除文件
     */
    private boolean isDeleteFile = false;
    
    /**
     * 获取文件保存路径
     * @return String 保存路径
     */
    public String getStorePath()
    {
        return storePath;
    }
    
    /**
     * 设置文件保存路径
     * @param path String
     */
    public void setStorePath(String path)
    {
        this.storePath = path;
    }
    
    /**
     * 获取下载地址
     * @return String 下载地址
     */
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
    
    /**
     * 设置现在地址
     * @param  downloadUrl 下载地址
     */
    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
    
    /**
     * 获取缓冲字节
     * @return byte[] bytesBuf
     */
    public byte[] getBytesBuf()
    {
        return bytesBuf;
    }
    
    /**
     * 设置缓冲字节
     * @param bytesBuf  byte[] 
     */
    protected void setBytesBuf(byte[] bytesBuf)
    {
        this.bytesBuf = bytesBuf;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    public boolean isResumeTask()
    {
        return true;
    }
    
    /**
     * {@inheritDoc}  
     */
    public boolean equals(TaskOperation task)
    {
        if (task.getClass() == this.getClass())
        {
            return getDownloadUrl().equals(((DownloadTask) task).getDownloadUrl());
        }
        return false;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected boolean isSave()
    {
        return isSave;
    }
    
    /**
     * 设置是否保存数据库  
     * @param isSave boolean
     */
    public void setIsSave(boolean isSave)
    {
        this.isSave = isSave;
    }
    
    /**
     * 获取是否删除文件
     * @return boolean
     */
    public boolean isDeleteFile()
    {
        return isDeleteFile;
    }
    
    /**
     * 设置是否删除文件
     * @param isDeleteFile boolean
     */
    public void setIsDeleteFile(boolean isDeleteFile)
    {
        this.isDeleteFile = isDeleteFile;
    }
    
    /**
     * 获取超时时间
     * @return int getTimeout
     */
    public int getTimeout()
    {
        return timeout;
    }
    
    /**
     * 设置超时时间
     * @param timeout int 
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnCreated()
    {
        String path = getStorePath();
        if (null != path)
        {
            try
            {
                File file = FileUtil.createFile(path);
                this.setStorePath(file.getPath());
                Logger.i(TAG, "getStorePath:" + path);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnStart()
    {
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnRestart()
    {
        String path = getStorePath();
        if (null != path)
        {
            FileUtil.deleteFile(path);
            setCurrentSize(0L);
        }
        else
        {
            this.bytesBuf = null;
        }
    }
    
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnDeleted()
    {
        if (this.getStatus() != ITask.TASK_STATUS_FINISHED
                || (this.isDeleteFile && this.getStatus() == ITask.TASK_STATUS_FINISHED))
        {
            String path = getStorePath();
            if (null != path)
            {
                FileUtil.deleteFile(getStorePath());
                setCurrentSize(0L);
            }
            else
            {
                this.bytesBuf = null;
            }
        }
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected void resetOnReload()
    {
        int status = this.getStatus();
        String filePath = this.getStorePath();
        if (status == TASK_STATUS_FINISHED)
        {
            setCurrentSize(getTotalSize());
            return;
        }
        if (status == TASK_STATUS_DELETED || status == TASK_STATUS_NEW)
        {
            setCurrentSize(0);
            if (null != filePath)
            {
                FileUtil.deleteFile(filePath);
            }
            return;
        }
        if (null != filePath)
        {
            File file = FileUtil.getFileByPath(filePath);
            if (file.exists() && file.isFile())
            {
                setCurrentSize(file.length());
            }
        }
    }
    
}