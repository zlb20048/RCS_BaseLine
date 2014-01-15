/*
 * 文件名: ContactUploadDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:May 8, 2012
 * 
 * 修改人：马波
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.contact.upload;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;

import com.huawei.basic.android.im.component.database.DatabaseHelper.PhoneContactIndexColumns;
import com.huawei.basic.android.im.component.load.task.TaskOperation;
import com.huawei.basic.android.im.logic.adapter.db.PhoneContactIndexDbAdapter;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;

/**
 * 上传通讯录数据库操作类<BR>
 * 
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, May 8, 2012] 
 */
public class ContactUploadDbAdapter implements IContactUploadDbAdapter
{
    
    /**
     * 通讯录表PhoneContactIndex操作类
     */
    private PhoneContactIndexDbAdapter mPhoneContactIndexDbAdapter;
    
    /**
     * 初始化页面信息<BR>
     * @param phoneContactIndexDbAdapter phoneContactIndexDbAdapter
     */
    public ContactUploadDbAdapter(
            PhoneContactIndexDbAdapter phoneContactIndexDbAdapter)
    {
        this.mPhoneContactIndexDbAdapter = phoneContactIndexDbAdapter;
    }
    
    /**
     * 
     * {@inheritDoc}
     * 
     */
    @Override
    public LinkedList<TaskOperation> getAllTask()
    {
        return new LinkedList<TaskOperation>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int addTask(TaskOperation task)
    {
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTaskStatus(int id, int status, Class<?> taskClass)
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TaskOperation getTask(int id, Class<?> taskClass)
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTotalSize(int id, long size, Class<?> taskClass)
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTaskClass()
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTask(int id, Class<?> taskClass)
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxTaskId()
    {
        return 0;
    }
    
    /**
     * 
     * 更改数据库通讯录数据<BR>
     * 上传通讯录信息后同步更改本地数据
     * @param userSysId 用户sysid
     * @param contacts 更改数据
     * @see com.huawei.basic.android.im.component.contact.IContactUploadDbAdapter#saveSucceedContacts(java.lang.String, java.util.List)
     */
    @Override
    public void saveSucceedContacts(String userSysId,
            List<PhoneContactIndexModel> contacts)
    {
        for (PhoneContactIndexModel contact : contacts)
        {
            int modifyFlag = contact.getContactModifyFlag();
            // 添加联系人
            if (modifyFlag == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_ADD)
            {
                mPhoneContactIndexDbAdapter.insertPhoneContactIndex(userSysId,
                        contact);
            }
            // 修改联系人
            else if (modifyFlag == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_UPDATE)
            {
                ContentValues cValues = new ContentValues();
                cValues.put(PhoneContactIndexColumns.CONTACT_USERID, "");
                cValues.put(PhoneContactIndexColumns.CONTACT_CRCVALUE,
                        contact.getContactCrcValue());
                cValues.put(PhoneContactIndexColumns.ADDFRIEND_PRIVACY, "");
                cValues.put(PhoneContactIndexColumns.CONTACT_SYSID, "");
                
                mPhoneContactIndexDbAdapter.updateByContactId(userSysId,
                        contact.getContactGUID(),
                        cValues);
            }
            // 删除联系人
            else if (modifyFlag == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_DELETE)
            {
                mPhoneContactIndexDbAdapter.deleteByGUID(userSysId,
                        contact.getContactGUID());
            }
        }
    }
}
