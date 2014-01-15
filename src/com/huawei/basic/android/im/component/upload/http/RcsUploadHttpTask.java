/*
 * 文件名: RcsUploadHttpTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-5-7
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.upload.http;

import java.util.ArrayList;
import java.util.List;

import com.huawei.basic.android.im.component.load.driver.ITaskDriver;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-5-7] 
 */
public class RcsUploadHttpTask extends UploadHttpTask
{
    /**
     * 上传的内容列表
     */
    private List<UploadContentInfo> uploadContentInfoList;
    
    private int uploadType;
    
    /**
     * 
     * 上传类型<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-5-14]
     */
    public interface UploadType
    {
        /**
         * 直接上传，携带request body & 文件实体
         */
        int DIRECT_UPLOAD = 1;
        
        /**
         * 分两次上传，第一阶段：仅携带request body
         */
        int REDIRECT_UPLOAD_FIRST_PHASE = 2;
        
        /**
         * 分两次上传，第二阶段：仅上传文件实体
         */
        int REDIRECT_UPLOAD_SECOND_PHASE = 3;
    }
    
    /**
     * 
     * 获取上传列表<BR>
     * @return 上传列表
     */
    public List<UploadContentInfo> getUploadContentInfoList()
    {
        return uploadContentInfoList;
    }
    
    /**
     * 
     * 设置上传列表<BR>
     * @param  list 上传列表
     */
    public void setUploadContentInfoList(List<UploadContentInfo> list)
    {
        this.uploadContentInfoList = list;
    }
    
    /**
     * 
     * 添加上传列表<BR>
     * @param uploadContentInfo UploadContentInfo
     */
    public void addUploadContentInfo(UploadContentInfo uploadContentInfo)
    {
        if (this.uploadContentInfoList == null)
        {
            this.uploadContentInfoList = new ArrayList<UploadContentInfo>();
        }
        this.uploadContentInfoList.add(uploadContentInfo);
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
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.upload.UploadTask#getTaskDriver()
     */
    
    @Override
    protected ITaskDriver getTaskDriver()
    {
        // TODO Auto-generated method stub
        return new RcsUploadHttpDriver(this);
    }
    
}
