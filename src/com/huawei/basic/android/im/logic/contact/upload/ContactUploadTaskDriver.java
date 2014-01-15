/*
 * 文件名: ContactUploadTaskDriver.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import com.huawei.basic.android.im.component.load.driver.ITaskDriver;
import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.component.load.task.TaskOperation;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.logic.adapter.http.ContactManager;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;

/**
 * 通讯录上传功能<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, May 8, 2012] 
 */
public class ContactUploadTaskDriver implements ITaskDriver
{
    
    /**
     * 定义TAG
     */
    private static final String TAG = "ContactUploadTaskDriver";
    
    /**
     * 进度更新时间
     */
    private static final int REPOR_TTIME = 500;
    
    /**
     * 批量上传通讯录时，单次上传最大数量
     */
    private static final int MAX_COUNT_PERTIME = 50;
    
    /**
     * 新增联系人标识
     */
    private static final int ACTION_UPLOAD_ADD_CONTACTS = 0x00000000;
    
    /**
     * 更改联系人标识
     */
    private static final int ACTION_UPLOAD_UPDATE_CONTACTS = 0x00000001;
    
    /**
     * 删除联系人标识
     */
    private static final int ACTION_UPLOAD_DELETE_CONTACTS = 0x00000002;
    
    /**
     * 数据库对象
     */
    private IContactUploadDbAdapter mContactUploadDbAdapter;
    
    /**
     * 上传任务类
     */
    private ContactUploadTask mContactUploadTask;
    
    /**
     * 上传通讯录
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return 上传是否成功
     */
    private BlockingQueue<List<PhoneContactIndexModel>> queue = new ArrayBlockingQueue<List<PhoneContactIndexModel>>(
            1);
    
    /**
     * 
     * 构造器
     * @param contactUploadTask contactUploadTask
     */
    public ContactUploadTaskDriver(ContactUploadTask contactUploadTask)
    {
        this.mContactUploadTask = contactUploadTask;
        this.mContactUploadDbAdapter = (IContactUploadDbAdapter) mContactUploadTask.getTaskDataAdapter();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws TaskException
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void read() throws TaskException
    {
        uploadContactsToCAB(mContactUploadTask.getContacts());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
    {
        
    }
    
    /**
     * 
     * 上传通讯录操作<BR>
     * @param contactsForUpload contactsForUpload
     * @throws TaskException TaskException
     */
    private void uploadContactsToCAB(
            List<PhoneContactIndexModel>[] contactsForUpload)
            throws TaskException
    {
        long time = 0;
        // 每次最大操作的条数
        // 当前处理数据集索引
        int position = 0;
        // 当前处理的数据集
        List<PhoneContactIndexModel> contactsToHandle = null;
        // 数据处理类型
        int action = ACTION_UPLOAD_ADD_CONTACTS;
        
        int contactModifyFlag = PhoneContactIndexModel.CONTACT_MODIFY_FLAG_ADD;
        
        // contactsForUpload包含新增、更改、删除通讯录同步数据
        for (position = 0; position < 3; position++)
        {
            contactsToHandle = contactsForUpload[position];
            if (contactsToHandle == null)
            {
                continue;
            }
            List<PhoneContactIndexModel> contactArray = new ArrayList<PhoneContactIndexModel>(
                    contactsToHandle);
            // 新增通讯录
            if (position == 0)
            {
                action = ACTION_UPLOAD_ADD_CONTACTS;
                contactModifyFlag = PhoneContactIndexModel.CONTACT_MODIFY_FLAG_ADD;
            }
            // 更改通讯录
            else if (position == 1)
            {
                action = ACTION_UPLOAD_UPDATE_CONTACTS;
                contactModifyFlag = PhoneContactIndexModel.CONTACT_MODIFY_FLAG_UPDATE;
            }
            // 需上传的删除通讯录
            else
            {
                action = ACTION_UPLOAD_DELETE_CONTACTS;
                contactModifyFlag = PhoneContactIndexModel.CONTACT_MODIFY_FLAG_DELETE;
            }
            
            int size = contactsToHandle == null ? 0 : contactsToHandle.size();
            if (size < 1)
            {
                continue;
            }
            // 最大循环次数
            int times = (int) Math.ceil(size / (double) MAX_COUNT_PERTIME);
            for (int i = 0; i < times; i++)
            {
                if (mContactUploadTask.getAction() == TaskOperation.ACTION_STOP
                        || mContactUploadTask.getAction() == TaskOperation.ACTION_DELETE)
                {
                    return;
                }
                // 暂停全量上传
                int start = i * MAX_COUNT_PERTIME;
                int end = MAX_COUNT_PERTIME * (i + 1);
                
                if (end > size)
                {
                    end = size;
                }
                final List<PhoneContactIndexModel> contacts = contactArray.subList(start,
                        end);
                // 按批次上传通讯录
                new ContactManager().uploadContactsToCAB(action,
                        contacts,
                        new IHttpListener()
                        {
                            @Override
                            public void onProgress(boolean isInProgress)
                            {
                            }
                            
                            @SuppressWarnings("unchecked")
                            @Override
                            public void onResult(int action, Response response)
                            {
                                if (null != response.getObj())
                                {
                                    try
                                    {
                                        queue.put((List<PhoneContactIndexModel>) response.getObj());
                                    }
                                    catch (InterruptedException e)
                                    {
                                        Logger.d(TAG, "queue put error ");
                                    }
                                }
                            }
                        });
                
                List<PhoneContactIndexModel> list = null;
                try
                {
                    list = queue.poll(5000L, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e1)
                {
                    throw new TaskException(TaskException.SERVER_CONNECT_FAILED);
                }
                if (null == list)
                {
                    throw new TaskException(TaskException.SERVER_CONNECT_FAILED);
                }
                else
                {
                    for (PhoneContactIndexModel contact : list)
                    {
                        contact.setContactModifyFlag(contactModifyFlag);
                    }
                }
                
                mContactUploadDbAdapter.saveSucceedContacts(mContactUploadTask.getUserSysId(),
                        list);
                
                for (PhoneContactIndexModel phoneContactIndexModel : contacts)
                {
                    contactsToHandle.remove(phoneContactIndexModel);
                }
                long currentTime = System.currentTimeMillis();
                if (time == 0 || (currentTime - time > REPOR_TTIME))
                {
                    try
                    {
                        mContactUploadTask.onProgress();
                    }
                    catch (TaskException e)
                    {
                        e.printStackTrace();
                    }
                    time = currentTime;
                }
                mContactUploadTask.setCurrentSize(mContactUploadTask.getCurrentSize()
                        + contacts.size());
                
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void deleteUploadedContacts()
    {
        
    }
    
}
