/*
 * 文件名: RcsDownloadHttpTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-4-28
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.download.http;

import com.huawei.basic.android.im.component.load.driver.ITaskDriver;
import com.huawei.basic.android.im.component.load.task.TaskOperation;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-28] 
 */
public class RcsDownloadHttpTask extends DownloadHttpTask
{
    /**
     * 保存文件夹
     */
    private String storeDir;
    
    /**
     * 保存文件名称
     */
    private String storeFileName;
    
    /**
     * get storeDir
     * @return the storeDir
     */
    public String getStoreDir()
    {
        return storeDir;
    }
    
    /**
     * set storeDir
     * @param storeDir the storeDir to set
     */
    public void setStoreDir(String storeDir)
    {
        this.storeDir = storeDir;
    }
    
    /**
     * get storeFileName
     * @return the storeFileName
     */
    public String getStoreFileName()
    {
        return storeFileName;
    }
    
    /**
     * set storeFileName
     * @param storeFileName the storeFileName to set
     */
    public void setStoreFileName(String storeFileName)
    {
        this.storeFileName = storeFileName;
    }
    
    /**
     * {@inheritDoc}  
     */
    @Override
    protected ITaskDriver getTaskDriver()
    {
        return new RcsDownloadHttpDriver(this);
    }
    
    /**
     * 重载equels方法，判断方法为当下载url与下载后路径都相同时判定为任务相同
     * @param task TaskOperation 下载任务
     * @return boolean 是否为同一任务
     */
    public boolean equals(TaskOperation task)
    {
        
        if (task.getClass() == this.getClass())
        {
            return getDownloadUrl().equals(((RcsDownloadHttpTask) task).getDownloadUrl())
                    && getStoreDir() != null
                    && getStoreDir().equals(((RcsDownloadHttpTask) task).getStoreDir());
        }
        return false;
    }
}
