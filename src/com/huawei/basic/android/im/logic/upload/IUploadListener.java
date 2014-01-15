/*
 * 文件名: IUploadListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 业务处用于处理上传文件状态  包括 进度，成功与失败
 * 创建人: s00193607
 * 创建时间:Mar 26, 2012
 * 
 */
package com.huawei.basic.android.im.logic.upload;

/**
 * 
 * 上传文件过程监听器<BR>
 * @author s00193607
 * @version [RCS Client V100R001C03, 2012-3-28]
 */
public interface IUploadListener
{
    /**
     * 上传完成，并且成功时<BR>
     * @param response  UploadFileForURLResponse
     */
    void onUploadFinish(UploadFileForURLResponse response);
    
    /**
     * 上传开始 <BR>
     * 
     */
    void onUploadStart();
    
    /**
     * 上传已暂停 <BR>
     * 
     */
    void onUploadPause();
    
    /**
     * 上传已停止 <BR>
     * 
     */
    void onUploadStop();
    
    /**
     * 上传失败 <BR>
     * @param errorInfo 错误信息
     */
    void onUploadFail(String errorInfo);
    
    /**
     * 
     * 更新进度实现方法<BR>
     * 此方法可以用于显示具体的进度百分比
     * 
     * @param finishedSize 已完成的大小
     * @param totalSize 上传的总大小
     * @param progressPercent 进度百分比
     */
    void onUploadProgress(int finishedSize, int totalSize,
            double progressPercent);
}
