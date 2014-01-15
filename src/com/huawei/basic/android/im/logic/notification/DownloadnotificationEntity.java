/*
 * 文件名: DownloadnotificationEntity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2012-4-16
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.notification;

import android.widget.RemoteViews;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.notification.ViewNotificationEntity;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-4-16] 
 */
public class DownloadnotificationEntity extends ViewNotificationEntity
{
    /**
     * 下载完成大小
     */
    private int finishedSize;
    
    /**
     * 下载文件名称
     */
    private String mFileName;
    
    /**
     * 下载文件总大小
     */
    private int mTotalSize;
    
    /**
     * 进程百分比
     */
    private double processPercent;
    
    /**
     * 下载图标
     */
    private int mIcon;
    
    /**
     * 构造方法
     * @param fileName 文件名称
     * @param icon 通知栏图标
     * @param tickerText 通知栏弹出信息
     * @param totalSize 文件总大小
     */
    public DownloadnotificationEntity(String fileName, int icon,
            CharSequence tickerText, int totalSize)
    {
        super(icon, tickerText);
        mFileName = fileName;
        mTotalSize = totalSize;
        mIcon = icon;
    }

    public void setProcessPercent(double processPercent)
    {
        this.processPercent = processPercent;
    }
    
    public void setFinishedSize(int finishedSize)
    {
        this.finishedSize = finishedSize;
    }
    
    /**
     * 获得布局文件<BR>
     * @return 布局文件ID
     * @see com.huawei.basic.android.im.component.notification.ViewNotificationEntity#getLayoutId()
     */
    
    @Override
    protected int getLayoutId()
    {
        return R.layout.notify_download;
    }
    
    /**
     * 初始化布局文件各元素<BR>
     * @param contentView RemoteViews对象
     * @see com.huawei.basic.android.im.component.notification.ViewNotificationEntity#initContentView(android.widget.RemoteViews)
     */
    
    @Override
    protected void initContentView(RemoteViews contentView)
    {
        //显示下载标题
        String textContent = getContext().getResources()
                .getString(R.string.notify_download_downloadcontent);
        String formatTextContent = String.format(textContent, mFileName);
        contentView.setTextViewText(R.id.notify_download_content,
                formatTextContent);
        
        contentView.setImageViewResource(R.id.notify_download_icon, mIcon);
        contentView.setTextViewText(R.id.notify_download_size, "0MB/"
                + mTotalSize + "MB");
    }
    
    /**
     * 更新通知栏<BR>
     * @param contentView RemoteViews对象
     * @see com.huawei.basic.android.im.component.notification.ViewNotificationEntity#updateContentView(android.widget.RemoteViews)
     */
    @Override
    protected void updateContentView(RemoteViews contentView)
    {
        contentView.setTextViewText(R.id.notify_download_size, finishedSize
                + "MB/" + mTotalSize + "MB");
        contentView.setProgressBar(R.id.notify_download_bar,
                100,
                (int) (100 * processPercent),
                false);
    }
    
    @Override
    protected int getProgressBarId()
    {
        return R.id.notify_download_bar;
    }
    
    /**
     * 
     * 更新操作完成后调用<BR>
     * @see com.huawei.basic.android.im.component.notification.NotificationEntity#onAfterUpdated()
     */
    @Override
    protected void onAfterUpdated()
    {
        //下载完成要跳转到系统的安装界面
        if (finishedSize >= mTotalSize)
        {
            //TODO: 跳转到系统应用安装界面
            
            NotificationEntityManager.getInstance()
                    .destroyNotification(getKey());
        }
    }
    
}
