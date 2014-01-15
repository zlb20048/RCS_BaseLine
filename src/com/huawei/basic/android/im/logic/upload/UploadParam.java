/*
 * 文件名: UploadParam.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-4-11
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.upload;

import java.util.ArrayList;
import java.util.List;

import com.huawei.basic.android.im.component.upload.http.UploadContentInfo;

/**
 * 上传参数model定义<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-11] 
 */
public class UploadParam
{
    /**
     * User account, according to HiTalk ID.
     */
    private String userAccount;
    
    /**
     * Upload type.
     * @see UploadType
     */
    private int uploadType;
    
    /**
     * Receiver type.
     * @see ReceiverType
     */
    private int receiverType;
    
    /**
     * 接收消息的用户账号，该字段仅用于广播消息附件上传。取值如下：<BR>
     * 1.  receiverType=1时，填为接收方的沃友ID<BR>
     * 2.  receiverType=2时，填为多人会话及群组消息填为会话/群组ID<BR>
     * 3.  receiverType=3时，填为一个特殊值”all-friends”（该字段预留，目前暂未使用。）<BR>
     */
    private String receiver;
    
    /**
     * Count of files that be uploaded
     */
    private int fileCount;
    
    /**
     * The total size of all the files.
     */
    private int totalSize;
    
    /**
     * The list of the files information that be uploaded.
     */
    private List<UploadContentInfo> uploadFileInfoList;
    
    /**
     * get userAccount
     * @return the userAccount
     */
    public String getUserAccount()
    {
        return userAccount;
    }
    
    /**
     * set userAccount
     * @param userAccount the userAccount to set
     */
    public void setUserAccount(String userAccount)
    {
        this.userAccount = userAccount;
    }
    
    /**
     * get uploadType
     * @return the uploadType
     */
    public int getUploadType()
    {
        return uploadType;
    }
    
    /**
     * set uploadType
     * @param uploadType the uploadType to set
     */
    public void setUploadType(int uploadType)
    {
        this.uploadType = uploadType;
    }
    
    /**
     * get receiverType
     * @return the receiverType
     */
    public int getReceiverType()
    {
        return receiverType;
    }
    
    /**
     * set receiverType
     * @param receiverType the receiverType to set
     */
    public void setReceiverType(int receiverType)
    {
        this.receiverType = receiverType;
    }
    
    /**
     * get receiver
     * @return the receiver
     */
    public String getReceiver()
    {
        return receiver;
    }
    
    /**
     * set receiver
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }
    
    /**
     * get fileCount
     * @return the fileCount
     */
    public int getFileCount()
    {
        if (fileCount == 0 && uploadFileInfoList != null)
        {
            fileCount = uploadFileInfoList.size();
        }
        return fileCount;
    }
    
    /**
     * get totalSize
     * @return the totalSize
     */
    public int getTotalSize()
    {
        if (totalSize == 0 && uploadFileInfoList != null)
        {
            for (UploadContentInfo uploadFileInfo : uploadFileInfoList)
            {
                totalSize += uploadFileInfo.getContentSize();
            }
        }
        return totalSize;
    }
    
    /**
     * get uploadFileInfoList
     * @return the uploadFileInfoList
     */
    public List<UploadContentInfo> getUploadContentInfoList()
    {
        return uploadFileInfoList;
    }
    
    /**
     * set uploadFileInfoList
     * @param aUploadFileInfoList the uploadFileInfoList to set
     */
    public void setUploadContentInfoList(
            List<UploadContentInfo> aUploadFileInfoList)
    {
        this.uploadFileInfoList = aUploadFileInfoList;
    }
    
    /**
     * 
     * add an upload file info<BR>
     * 
     * @param uploadFileInfo 上传的文件信息对象
     */
    public void addUploadContentInfoList(UploadContentInfo uploadFileInfo)
    {
        if (this.uploadFileInfoList == null)
        {
            uploadFileInfoList = new ArrayList<UploadContentInfo>();
        }
        uploadFileInfoList.add(uploadFileInfo);
        fileCount++;
    }
}
