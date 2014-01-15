/*
 * 文件名: ContactUploadTask.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:May 8, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.contact.upload;

import java.util.List;
import com.huawei.basic.android.im.component.load.task.TaskOperation;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;

/**
 * 通讯录上传任务类<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, May 8, 2012] 
 */
public class ContactUploadTask extends TaskOperation
{
    
    /**
     * 通讯录数据列表
     */
    private List<PhoneContactIndexModel>[] mContacts;
    
    /**
     * 全量上传标识
     */
    private boolean isFullUpload = false;
    
    /**
     * 用户标识sysId
     */
    private String userSysId;
    
    /**
     * 通讯录上传任务对象
     */
    private ContactUploadTaskDriver mContactUploadTaskDriver;
    
    /**
     * 是否自动重新启动上次的任务
     * @return boolean
     */
    @Override
    public boolean isResumeTask()
    {
        return false;
    }
    
    /**
     * 
     *开始任务前的初始化数据<BR>
     * @see com.huawei.basic.android.im.component.load.task.TaskOperation#resetOnCreated()
     */
    @Override
    protected void resetOnCreated()
    {
        if (null == mContacts)
        {
            return;
        }
        
        int total = 0;
        for (List<PhoneContactIndexModel> list : mContacts)
        {
            if (null != list)
            {
                total += list.size();
            }
        }
        setTotalSize(total);
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
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetOnReload()
    {
        
    }
    
    /**
     * 删除任务前的初始化数据<BR>
     * @see com.huawei.basic.android.im.component.load.task.TaskOperation#resetOnDeleted()
     */
    @Override
    protected void resetOnDeleted()
    {
        getTaskDriver().deleteUploadedContacts();
    }
    
    /**
     * 判断当前任务是否正在运行<BR>
     * @param task task
     * @return boolean
     * @see com.huawei.basic.android.im.component.load.task.TaskOperation#equals(com.huawei.basic.android.im.component.load.task.TaskOperation)
     */
    @Override
    protected boolean equals(TaskOperation task)
    {
        return this == task;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSave()
    {
        return false;
    }
    
    /**
     * 获取<BR>
     * [功能详细描述]
     * @return mContactUploadTaskDriver
     * @see com.huawei.basic.android.im.component.load.task.TaskOperation#getTaskDriver()
     */
    @Override
    protected ContactUploadTaskDriver getTaskDriver()
    {
        if (mContactUploadTaskDriver == null)
        {
            mContactUploadTaskDriver = new ContactUploadTaskDriver(this);
        }
        return mContactUploadTaskDriver;
    }
    
    /**
     * 
     * 获取上传数据<BR>
     * @return mContacts
     */
    public List<PhoneContactIndexModel>[] getContacts()
    {
        return mContacts;
    }
    
    /**
     * 
     * 赋值任务数据<BR>
     * @param contacts contacts
     */
    public void setContacts(List<PhoneContactIndexModel>[] contacts)
    {
        this.mContacts = contacts;
    }
    
    /**
     * 
     * 全量上传标志<BR>
     * @return boolean
     */
    public boolean isFullUpload()
    {
        return isFullUpload;
    }
    
    /**
     * 
     * 赋值全量上传标志<BR>
     * @param isFullUpload isFullUpload
     */
    public void setIsFullUpload(boolean isFullUpload)
    {
        this.isFullUpload = isFullUpload;
    }
    
    /**
     * 
     * 获取当前用户ID<BR>
     * @return userSysId
     */
    public String getUserSysId()
    {
        return userSysId;
    }
    
    /**
     * 
     * 赋值当前用户ID<BR>
     * @param userSysId userSysId
     */
    public void setUserSysId(String userSysId)
    {
        this.userSysId = userSysId;
    }
    
}
