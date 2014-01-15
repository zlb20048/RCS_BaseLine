/*
 * 文件名: RcsDownloadHttpDriver.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-5-26
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.download.http;

import java.io.File;

import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.utils.FileUtil;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-5-26] 
 */
public class RcsDownloadHttpDriver extends DownloadHttpDriver
{
    
    private RcsDownloadHttpTask mDownloadTask;
    
    public RcsDownloadHttpDriver(RcsDownloadHttpTask downloadTask)
    {
        super(downloadTask);
        this.mDownloadTask = downloadTask;
    }
    
    @Override
    protected void handleStorePath() throws TaskException
    {
        if (mDownloadTask.getStorePath() == null)
        {
            if (mDownloadTask.getStoreFileName() == null)
            {
                mDownloadTask.setStoreFileName(getFileName());
            }
            
            String filePath = mDownloadTask.getStoreDir();
            if (!filePath.endsWith("/"))
            {
                filePath += "/";
            }
            filePath += mDownloadTask.getStoreFileName();
            mDownloadTask.setStorePath(filePath);
        }
        
        if (null != mDownloadTask.getStorePath())
        {
            File file = FileUtil.getFileByPath(mDownloadTask.getStorePath());
            if (file != null && file.exists()
                    && mDownloadTask.getCurrentSize() == 0)
            {
                FileUtil.deleteFile(file);
            }
        }
    }
}
