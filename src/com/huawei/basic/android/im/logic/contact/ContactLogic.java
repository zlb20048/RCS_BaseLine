/*
 * 文件名: ContactLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Adler32;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContactsEntity;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ContactListAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.ContactMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.ConversationMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.PhoneContactIndexColumns;
import com.huawei.basic.android.im.component.load.TaskManagerFactory;
import com.huawei.basic.android.im.component.load.task.ITask;
import com.huawei.basic.android.im.component.load.task.ITaskManager;
import com.huawei.basic.android.im.component.load.task.ITaskStatusListener;
import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.PhoneContactIndexDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.UserConfigDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.ContactManager;
import com.huawei.basic.android.im.logic.contact.upload.ContactUploadTask;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.utils.HanziToPinyin;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 通讯录模块的logic实现类<BR>
 * 
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012]
 */
public class ContactLogic extends BaseLogic implements IContactLogic
{
    
    /**
     * debug TAG
     */
    private static final String TAG = "ContactLogic";
    
    /**
     * 需要提示的联系人数量
     */
    private static final int MAX_CONTACTS = 1000;
    
    /**
     * 同步通讯录等待时间
     */
    private static final int UPLOAD_MODIFY_CONTACTS_DELAY = 6000;
    
    /**
     * 成功标志
     */
    private static final String SUCCESS_FLAG = "0";
    
    /**
     * 手机通讯录信息uri
     */
    private static final Uri[] URIS = new Uri[] { ContactsContract.Contacts.CONTENT_URI };
    
    /**
     * 允许所有用户经我确认后加我为好友
     */
    private static final int CONTACT_USER_FRIEND_PRIVACY_ALLOW_ALL = 1;
    
    /**
     * 仅允许通讯录中的用户经我确认后加我为好友
     */
    private static final int CONTACT_USER_FRIEND_PRIVACY_ALLOW_BUT_PHONE = 2;
    
    /**
     * 不允许任何人加我为好友
     */
    private static final int CONTACT_USER_FRIEND_PRIVACY_NOT_ALLOW = 3;
    
    /**
     * 允许绑定了手机号码的用户经我确认后加我为好友
     */
    private static final int CONTACT_USER_FRIEND_PRIVACY_ALLOW_BUT_BINDED = 4;
    
    /**
     * 需要确认信息，由用户手工确认是否同意被加好友
     */
    private static final int CONTACT_USER_AUTO_CONFIRM_NO = 1;
    
    /**
     * 上传通讯录每个号码的最大长度
     */
    private static final int MAX_PHONE_NUMBER_LENGTH = 20;
    
    /**
     * 上传通讯录每个邮箱地址的最大长度
     */
    private static final int MAX_EMAIL_ADDRESS_LENGTH = 50;
    
    /**
     * 接收Logic传过来的mContext
     */
    private Context mContext;
    
    /**
     * 计时器
     */
    private Timer mTimer = null;
    
    /**
     * 通讯录上传管理
     */
    private ITaskManager mUploadContactsManager;
    
    /**
     * 数据库操作类
     */
    private PhoneContactIndexDbAdapter mPhoneContactIndexDbAdapter;
    
    /**
     * 通讯录上传监听
     */
    private ITaskStatusListener mContactListener = new ITaskStatusListener()
    {
        @Override
        public void onChangeStatus(ITask task)
        {
            switch (task.getStatus())
            {
                case ITask.TASK_STATUS_NEW:
                    sendMessage(ConversationMessageType.UPLOAD_CONTACTS_RUNNING,
                            task.getPercent());
                    Logger.d(TAG,
                            "[Upload Contacts]NEW TASK, NAME:" + task.getName());
                    break;
                
                case ITask.TASK_STATUS_RUNNING:
                    Logger.d(TAG, "[Upload Contacts]TASK IS RUNNING, NAME:"
                            + task.getName());
                    break;
                
                case ITask.TASK_STATUS_PROCESS:
                    sendMessage(ConversationMessageType.UPLOAD_CONTACTS_RUNNING,
                            task.getPercent());
                    Logger.d(TAG, "[Upload Contacts]RETURN TASK PROCESS, NAME:"
                            + task.getName());
                    break;
                
                case ITask.TASK_STATUS_WARTING:
                    Logger.d(TAG, "[Upload Contacts]TASK IS WAITING, NAME:"
                            + task.getName());
                    break;
                
                case ITask.TASK_STATUS_FINISHED:
                    sendEmptyMessage(ConversationMessageType.UPLOAD_CONTACTS_FINISH);
                    // 服务器获取当前用户所有HiTalk联系人
                    getContactUserIds();
                    Logger.d(TAG, "[Upload Contacts]TASK IS FINISHED, NAME:"
                            + task.getName());
                    break;
                
                case ITask.TASK_STATUS_DELETED:
                    Logger.d(TAG, "[Upload Contacts]TASK IS DELETED, NAME:"
                            + task.getName());
                    break;
                
                case ITask.TASK_STATUS_ERROR:
                    Logger.d(TAG, "[Upload Contacts]TASK MET ERROR, NAME:"
                            + task.getName());
                    sendEmptyMessage(ConversationMessageType.UPLOAD_PROGRESS_FAIL);
                    break;
                case ITask.TASK_STATUS_STOPPED:
                    Logger.d(TAG, "[Upload Contacts]TASK IS STOPED, NAME:"
                            + task.getName());
                    break;
                default:
                    break;
            }
        }
    };
    
    /**
     * 构造函数，并获得serviceSender [构造简要说明]
     * 
     * @param context
     *            context
     * @param serviceSender
     *            serviceSender
     */
    public ContactLogic(Context context, IServiceSender serviceSender)
    {
        mContext = context;
        mPhoneContactIndexDbAdapter = PhoneContactIndexDbAdapter.getInstance(mContext);
        mUploadContactsManager = TaskManagerFactory.getUploadContactManager();
        mUploadContactsManager.addTaskStatusListener(mContactListener);
    }
    
    /**
     * 初始化获取手机通讯录里的联系人<BR>
     * [功能详细描述]
     */
    @Override
    public synchronized void getPhoneContactData()
    {
        new Thread()
        {
            private ArrayList<PhoneContactIndexModel> contacts;
            
            private HashMap<String, PhoneContactIndexModel> dbContactMap;
            
            public void run()
            {
                Logger.i(TAG, "GET CONTACTS BEGIN");
                // 获取手机通讯录数据
                contacts = getContacts();
                // 通讯录有数据
                if (null != contacts && contacts.size() > 0)
                {
                    dbContactMap = buildDbContactsInfoMap();
                    contacts = addStatusToContacts(contacts, dbContactMap);
                }
                sendMessage(ContactMessageType.GET_ALL_CONTACT_LIST, contacts);
                Logger.i(TAG, "GET CONTACTS END");
            }
        }.start();
    }
    
    /**
     * 获取手机联系人数据<BR>
     * 主要用于联系人列表展示,该方法仅查询出联系人及其名下一个电话号码
     * 
     * @return 手机联系人数据
     */
    private ArrayList<PhoneContactIndexModel> getContacts()
    {
        ArrayList<PhoneContactIndexModel> phoneContacts = new ArrayList<PhoneContactIndexModel>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/data/phones");
        
        String[] projection = { Phone.CONTACT_ID, Phone.DISPLAY_NAME,
                Phone.DATA1, Phone.PHOTO_ID };
        Cursor cursor = null;
        try
        {
            cursor = resolver.query(uri, projection, null, null, null);
            // Calling getCount() causes the cursor window to be filled,
            // which will make the first access on the main thread a lot faster.
            if (cursor != null)
            {
                cursor.getCount();
            }
        }
        catch (Exception e)
        {
            Logger.w(TAG, e.toString());
            cursor = null;
        }
        
        if (cursor != null && cursor.getCount() > 0)
        {
            String contactId = null;
            String number, displayName, photoId;
            PhoneContactIndexModel contact = null;
            Map<String, PhoneContactIndexModel> contactMap = new HashMap<String, PhoneContactIndexModel>();
            
            // 装载联系人model
            while (cursor.moveToNext())
            {
                // 联系人ContactId
                contactId = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID)));
                // 判断当前ContactID是否加载,
                if (!contactMap.containsKey(contactId))
                {
                    contact = new PhoneContactIndexModel();
                    // 联系人ID
                    contact.setContactLUID(contactId);
                    // 数据获取途径:手机
                    contact.setContactType(PhoneContactIndexModel.CONTACT_TYPE_PHONE);
                    // 将联系人名称存入
                    displayName = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));
                    contact.setDisplayName(null == displayName ? ""
                            : displayName);
                    // 将头像编号存入
                    photoId = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Phone.PHOTO_ID)));
                    contact.setPhotoId(photoId);
                    // 将电话号码存入
                    number = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Phone.DATA1)))
                            .replaceAll("(-|^\\+86)", "");
                    ArrayList<String> phoneNum = new ArrayList<String>();
                    phoneNum.add(null == number ? "" : number);
                    contact.addPhoneNumber(phoneNum);
                    // 已获取过的Contact信息存入
                    contactMap.put(contactId, contact);
                    phoneContacts.add(contact);
                }
            }
        }
        cursor.close();
        
        return phoneContacts;
    }
    
    /**
     * 
     * 把联系人加上状态（是好友还是HiTalk）<BR>
     * 
     * @param contactsList
     *            联系人列表
     * @param dbContactMap
     *            数据库中HiTalk联系人
     * @return contactsList 联系人列表
     */
    private ArrayList<PhoneContactIndexModel> addStatusToContacts(
            ArrayList<PhoneContactIndexModel> contactsList,
            HashMap<String, PhoneContactIndexModel> dbContactMap)
    {
        for (PhoneContactIndexModel contact : contactsList)
        {
            if (null != dbContactMap
                    && dbContactMap.containsKey(contact.getContactLUID()))
            {
                PhoneContactIndexModel contactInfo = dbContactMap.get(contact.getContactLUID());
                contact.setContactSysId(contactInfo.getContactSysId());
                contact.setContactUserId(contactInfo.getContactUserId());
                contact.setAddFriendPrivacy(contactInfo.getAddFriendPrivacy());
                contact.setHiTalk(true);
            }
        }
        return contactsList;
    }
    
    /**
     * 
     * 插入SpellName<BR>
     * @param phoneContact
     */
    private void generateSpellNameAndInitialName(
            PhoneContactIndexModel phoneContact)
    {
        HanziToPinyin htp = HanziToPinyin.getInstance();
        String splitName = null;
        // 计算显示名和转化成拼音的字符串
        if (!StringUtil.isNullOrEmpty(phoneContact.getDisplayName()))
        {
            splitName = htp.getPinyinWithWhitespace(phoneContact.getDisplayName());
            phoneContact.setSpellName(htp.getPinyin(phoneContact.getDisplayName()));
        }
        if (!StringUtil.isNullOrEmpty(splitName))
        {
            StringBuffer sb = new StringBuffer();
            String[] str = splitName.split(" ");
            if (str != null)
            {
                for (int i = 0; i < str.length; i++)
                {
                    if (str[i].length() >= 1)
                    {
                        sb.append(str[i].charAt(0));
                    }
                }
            }
            phoneContact.setInitialName(sb.toString());
        }
    }
    
    /**
     * 
     * 构建联系人信息，找出有多少HiTalk用户<BR>
     * 
     * @return dbContactMap
     */
    private HashMap<String, PhoneContactIndexModel> buildDbContactsInfoMap()
    {
        HashMap<String, PhoneContactIndexModel> dbContactMap = new HashMap<String, PhoneContactIndexModel>();
        
        List<PhoneContactIndexModel> dbContacts = getDbContacts();
        
        if (dbContacts != null && !dbContacts.isEmpty())
        {
            for (PhoneContactIndexModel dbContact : dbContacts)
            {
                if (!StringUtil.isNullOrEmpty(dbContact.getContactSysId()))
                {
                    PhoneContactIndexModel phoneContactIndexModel = new PhoneContactIndexModel();
                    
                    phoneContactIndexModel.setContactUserId(dbContact.getContactUserId());
                    phoneContactIndexModel.setContactSysId(dbContact.getContactSysId());
                    phoneContactIndexModel.setAddFriendPrivacy(dbContact.getAddFriendPrivacy());
                    
                    dbContactMap.put(dbContact.getContactLUID(),
                            phoneContactIndexModel);
                }
            }
        }
        Logger.d(TAG, "Found " + dbContactMap.size() + " HiTalk users");
        return dbContactMap;
    }
    
    /**
     * 
     * 监听本地通讯录<BR>
     * 
     */
    @Override
    public void registerContactsObserver()
    {
        ContentObserver contentObserver = new ContentObserver(new Handler())
        {
            @Override
            public void onChange(boolean selfChange)
            {
                if (null != mTimer)
                {
                    mTimer.cancel();
                }
                mTimer = new Timer(true);
                mTimer.schedule(new TimerTask()
                {
                    public void run()
                    {
                        Logger.i(TAG, " ===手机通讯录变化 " + URIS[0]);
                        mTimer = null;
                        // 数据库数据有变化，需判断是否需要变量上传通讯录
                        if (needUploadContacts(FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId()))
                        {
                            beginUpload(false);
                        }
                        getPhoneContactData();
                    }
                }, UPLOAD_MODIFY_CONTACTS_DELAY);
            }
        };
        // 调用父类方法，将当前uri注册到监听中
        registerObserver(URIS[0], contentObserver);
    }
    
    /**
     * 
     * 移除本地通讯录监听<BR>
     * 
     */
    @Override
    public void unRegisterContactsObserver()
    {
        // 调用父类方法，移除uri
        unRegisterObserver(URIS[0]);
    }
    
    /**
     * 
     * 上传通讯录<BR>
     * 
     * @param isFullUpload
     *            全量/增量标志
     */
    @Override
    public void beginUpload(final boolean isFullUpload)
    {
        new Thread()
        {
            public void run()
            {
                ITask task = buildUploadContactsTask(isFullUpload);
                try
                {
                    TaskManagerFactory.getUploadContactManager()
                            .createTask(task);
                    TaskManagerFactory.getUploadContactManager()
                            .startTask(task.getId());
                }
                catch (TaskException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    /**
     * 暂停上传<BR>
     */
    @Override
    public void paushUpload()
    {
        try
        {
            mUploadContactsManager.stopAllTask();
        }
        catch (TaskException e)
        {
            e.printStackTrace();
            Logger.e(TAG, "暂停上传失败");
        }
    }
    
    /**
     * 继续上传<BR>
     */
    @Override
    public void resumUpload()
    {
        try
        {
            mUploadContactsManager.startAllTask();
        }
        catch (TaskException e)
        {
            e.printStackTrace();
            Logger.e(TAG, "继续上传失败");
        }
    }
    
    /**
     * 取消上传<BR>
     */
    @Override
    public void cancelUpload()
    {
        try
        {
            mUploadContactsManager.deleteAllTask();
        }
        catch (TaskException e)
        {
            e.printStackTrace();
            Logger.e(TAG, "取消上传失败");
        }
    }
    
    /**
     * 获取本地联系人详情<BR>
     * [功能详细描述]
     * 
     * @param id
     *            id
     * @return PhoneContactIndexModel 联系人详情
     */
    public PhoneContactIndexModel getLocalContactProfile(String id)
    {
        
        PhoneContactIndexModel phoneContactIndexModel = new PhoneContactIndexModel();
        
        queryContactByID(id, phoneContactIndexModel);
        queryLocalContactByID(id, phoneContactIndexModel);
        
        return phoneContactIndexModel;
    }
    
    /**
     * 
     * 根据id 查询表PhoneContactIndex数据<BR>
     * 
     * @param id
     *            ContactID
     * @param phoneContactIndexModel
     *            phoneContactIndexModel
     */
    private void queryContactByID(String contactId,
            PhoneContactIndexModel phoneContactIndexModel)
    {
        if (null == phoneContactIndexModel && null == contactId)
        {
            return;
        }
        Cursor dataCursor = null;
        Uri uri = Uri.parse("content://com.android.contacts/contacts/"
                + contactId + "/data");
        
        // 添加异常处理,针对一些定制android系统(Coolpad 8870)可以禁止访问系统权限
        try
        {
            dataCursor = mContext.getContentResolver().query(uri,
                    new String[] { Data.DATA1, Data.DATA2, Data.MIMETYPE },
                    null,
                    null,
                    null);
        }
        catch (SecurityException ex)
        {
            Logger.d(TAG, "getPhoneContacts", ex);
        }
        
        if (null != dataCursor)
        {
            phoneContactIndexModel.setContactLUID(contactId);
            // 查询联系人表中的
            while (dataCursor.moveToNext())
            {
                String data = dataCursor.getString(0);
                String dataType = dataCursor.getString(1);
                String mimeType = dataCursor.getString(2);
                if (StructuredName.CONTENT_ITEM_TYPE.equals(mimeType))
                {
                    phoneContactIndexModel.setDisplayName(data);
                }
                else if (Phone.CONTENT_ITEM_TYPE.equals(mimeType))
                {
                    ArrayList<String> phoneNum = new ArrayList<String>();
                    phoneNum.add(null == data ? "" : data);
                    phoneNum.add(null == dataType ? "" : dataType);
                    phoneContactIndexModel.addPhoneNumber(phoneNum);
                }
                else if (Email.CONTENT_ITEM_TYPE.equals(mimeType))
                {
                    ArrayList<String> phoneEmail = new ArrayList<String>();
                    phoneEmail.add(null == data ? "" : data);
                    phoneEmail.add(null == dataType ? "" : dataType);
                    phoneContactIndexModel.addEmailAddr(phoneEmail);
                    
                }
            }
            dataCursor.close();
        }
        
    }
    
    /**
     * 
     * 根据id查询手机通讯录数据<BR>
     * 
     * @param id
     *            ContactID
     * @param phoneContactIndexModel
     *            phoneContactIndexModel
     */
    private void queryLocalContactByID(String id,
            PhoneContactIndexModel phoneContactIndexModel)
    {
        
        String selection1 = "_id = " + id;
        Cursor cur1 = mContext.getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI,
                        new String[] { ContactsContract.Contacts.PHOTO_ID },
                        selection1,
                        null,
                        null);
        if (cur1 != null)
        {
            if (cur1.getCount() > 0)
            {
                cur1.moveToFirst();
                phoneContactIndexModel.setPhotoId(cur1.getString(0));
                Cursor curphoto = mContext.getContentResolver()
                        .query(Data.CONTENT_URI,
                                new String[] { ContactsContract.Contacts.Photo.DATA15 },
                                "_id = " + phoneContactIndexModel.getPhotoId(),
                                null,
                                null);
                if (null != curphoto)
                {
                    if (curphoto.moveToFirst())
                    {
                        phoneContactIndexModel.setHeadBytes(curphoto.getBlob(0));
                    }
                    curphoto.close();
                }
            }
            cur1.close();
        }
    }
    
    /**
     * 
     * 将数据插入UserConfig表<BR>
     * [功能详细描述]
     * 
     * @param userSysID
     *            用户的唯一标识
     * @param colName
     *            键对应字段
     * @param colValue
     *            键对应的值
     * 
     * @return 成功：插入后记录的行数<BR>
     *         失败：-1
     * @see com.huawei.basic.android.im.logic.contact.IContactLogic#insertUploadFlag(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public long insertUploadFlag(String userSysID, String colName,
            String colValue)
    {
        UserConfigDbAdapter configAdapter = UserConfigDbAdapter.getInstance(mContext);
        
        // 调用数据库封装方法，实现插入操作
        return configAdapter.insertUserConfig(userSysID,
                UserConfigModel.IS_UPLOAD_CONTACTS,
                colValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long insertOrUpdateUploadFlag(boolean flag)
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        if (null != queryUserConfig(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), UserConfigModel.IS_UPLOAD_CONTACTS))
        {
            return updateUploadFlag(flag);
        }
        return insertUploadFlag(userSysId,
                UserConfigModel.IS_UPLOAD_CONTACTS,
                flag ? UserConfigModel.IS_UPLOAD_CONTACTS_YES
                        : UserConfigModel.IS_UPLOAD_CONTACTS_NO);
    }
    
    /**
     * 
     * 查询数据库配置信息<BR>
     * 
     * @param userSysID
     *            userSysID
     * @param colName
     *            colName
     * @return UserConfigModel
     * @see com.huawei.basic.android.im.logic.contact.IContactLogic#queryUserConfig(java.lang.String,
     *      java.lang.String)
     */
    public UserConfigModel queryUserConfig(String userSysID, String colName)
    {
        UserConfigDbAdapter configAdapter = UserConfigDbAdapter.getInstance(mContext);
        
        // 调用数据库封装方法
        return configAdapter.queryByKey(userSysID, colName);
    }
    
    /**
     * 判断是否需要上传联系人<BR>
     * 默认设置为上传，除非用户手动设置为不上传
     * 
     * @param userSysId
     *            用户sysId
     * @return boolean 是否需要上传
     * @see com.huawei.basic.android.im.logic.contact.IContactLogic#needUploadContacts(java.lang.String)
     */
    @Override
    public boolean needUploadContacts(String userSysId)
    {
        UserConfigDbAdapter configAdapter = UserConfigDbAdapter.getInstance(mContext);
        UserConfigModel uploadContactsSet = configAdapter.queryByKey(userSysId,
                UserConfigModel.IS_UPLOAD_CONTACTS);
        if (null != uploadContactsSet
                && ContactListAction.DISAGREE_UPLOAD_CONTACTS.equals(uploadContactsSet.getValue()))
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     * 是否需要显示太多联系人的提示<BR>
     * [功能详细描述]
     * 
     * @return boolean
     * @see com.huawei.basic.android.im.logic.contact.IContactLogic#needShowManyContactsTips()
     */
    @Override
    public boolean needShowManyContactsTips()
    {
        int contactNumber = 0;
        Cursor cursor = mContext.getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        if (cursor != null)
        {
            contactNumber = cursor.getCount();
            cursor.close();
        }
        Logger.d(TAG, "contacts total number = " + contactNumber);
        return contactNumber > MAX_CONTACTS ? true : false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void inviteFriend(Map<String, Object> sendData)
    {
        // 发送服务器
        new ContactManager().inviteFriend(sendData, new IHttpListener()
        {
            @Override
            public void onProgress(boolean isInProgress)
            {
            }
            
            @Override
            public void onResult(int action, Response response)
            {
                String messageString = null;
                if (null != response.getObj())
                {
                    String code = (String) response.getObj();
                    
                    if (SUCCESS_FLAG.equals(response.getObj()))
                    {
                        messageString = mContext.getResources()
                                .getString(R.string.chatbar_invite_friend);
                    }
                    else
                    {
                        messageString = FusionErrorInfo.getErrorInfo(mContext,
                                String.valueOf(code));
                    }
                }
                
                if (null == messageString)
                {
                    messageString = mContext.getResources()
                            .getString(R.string.xmpp_error_code_unknown);
                }
                sendMessage(FriendMessageType.INVITE_FRIEND_MESSAGE,
                        messageString);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUploaded()
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        UserConfigModel config = UserConfigDbAdapter.getInstance(mContext)
                .queryByKey(userSysId, UserConfigModel.IS_UPLOAD_CONTACTS);
        if (null == config)
        {
            return false;
        }
        return UserConfigModel.IS_UPLOAD_CONTACTS_YES.equals(config.getValue());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUploadedContacts()
    {
        new ContactManager().deleteUploadedContacts(new IHttpListener()
        {
            @Override
            public void onResult(int action, Response response)
            {
                mPhoneContactIndexDbAdapter.deleteByUsersysId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId());
                getPhoneContactData();
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PhoneContactIndexModel> getAddressBookContactsNotFriends()
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int updateUploadFlag(boolean flag)
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        return UserConfigDbAdapter.getInstance(mContext).updateByKey(userSysId,
                UserConfigModel.IS_UPLOAD_CONTACTS,
                flag ? UserConfigModel.IS_UPLOAD_CONTACTS_YES
                        : UserConfigModel.IS_UPLOAD_CONTACTS_NO);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContactById(String contactId)
    {
        if (null == contactId)
        {
            return;
        }
        
        String where = ContactsContract.Data.CONTACT_ID + " = ? ";
        String[] params = new String[] { contactId };
        
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(where, params)
                .build());
        try
        {
            mContext.getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);
            mPhoneContactIndexDbAdapter.deleteByGUID(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(), contactId);
            // 展示toast
            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
            Logger.e(TAG, "删除通讯录失败 ");
        }
    }
    
    private ITask buildUploadContactsTask(boolean isFullUpload)
    {
        ContactUploadTask contactUploadTask = new ContactUploadTask();
        contactUploadTask.setName("Upload Contacts");
        contactUploadTask.setCreatedTime(new Date());
        contactUploadTask.setContacts(getContactsNeedUpload());
        contactUploadTask.setIsFullUpload(isFullUpload);
        contactUploadTask.setUserSysId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        return contactUploadTask;
    }
    
    /**
     * 获取需要上传的联系人数据
     * 
     * @return 需要上传的联系人数据
     */
    @SuppressWarnings("unchecked")
    protected List<PhoneContactIndexModel>[] getContactsNeedUpload()
    {
        // 取出手机里的联系人
        ArrayList<PhoneContactIndexModel> phoneContacts = getPhoneContacts();
        
        //        PhoneContactIndexModel[] allContacts = getPhoneContacts();
        
        // 读取上次数据库表中的联系人信息
        List<PhoneContactIndexModel> lastUploadContacts = getDbContacts();
        
        // 便于清晰划分出新增、修改以及删除的联系人
        List<PhoneContactIndexModel> addedContacts = null;
        List<PhoneContactIndexModel> updatedContacts = null;
        List<PhoneContactIndexModel> deleteContacts = null;
        
        // 对联系人通过adler32进行指纹采集
        if (null != phoneContacts && phoneContacts.size() > 0)
        {
            StringBuilder sb = new StringBuilder();
            List<List<String>> numbers = null;
            List<List<String>> emails = null;
            Adler32 adler32 = new Adler32();
            
            for (PhoneContactIndexModel contact : phoneContacts)
            {
                sb.append("^");
                sb.append(StringUtil.getString(contact.getDisplayName()));
                sb.append(StringUtil.getString(contact.getDisplayName()));
                sb.append("\r\n");
                
                // 获取电话信息
                numbers = contact.getPhoneNumbers();
                if (numbers != null && !numbers.isEmpty())
                {
                    sb.append("^N");
                    for (List<String> number : numbers)
                    {
                        // number中包含手机号码、号码类型
                        sb.append(number.get(0));
                        sb.append("\r\n");
                    }
                }
                // 获取邮件信息
                emails = contact.getEmailAddrs();
                if (emails != null && !emails.isEmpty())
                {
                    sb.append("^E");
                    for (List<String> email : emails)
                    {
                        // email中保存的邮件地址、邮件类型
                        sb.append(email);
                        sb.append("\r\n");
                    }
                }
                
                adler32.reset();
                adler32.update(sb.toString().getBytes(), 0, sb.length());
                String crcValue = String.valueOf(adler32.getValue());
                contact.setContactCrcValue(crcValue);
                sb.delete(0, sb.length());
            }
        }
        
        // 比对指纹,归类出新增与更改的通讯录记录
        if (null != phoneContacts && phoneContacts.size() > 0)
        {
            addedContacts = new ArrayList<PhoneContactIndexModel>();
            if (null != lastUploadContacts && !lastUploadContacts.isEmpty())
            {
                updatedContacts = new ArrayList<PhoneContactIndexModel>();
                for (PhoneContactIndexModel contactForUpload : phoneContacts)
                {
                    for (PhoneContactIndexModel lastUploadContact : lastUploadContacts)
                    {
                        if (!StringUtil.equals(lastUploadContact.getContactLUID(),
                                contactForUpload.getContactLUID()))
                        {
                            continue;
                        }
                        if (StringUtil.equals(lastUploadContact.getContactCrcValue(),
                                contactForUpload.getContactCrcValue()))
                        {
                            // 如果CRC值相同, 则是没修改过
                            contactForUpload.setContactModifyFlag(PhoneContactIndexModel.CONTACT_MODIFY_FLAG_NORMAL);
                        }
                        else
                        {
                            // 如果CRC值不相同, 则是被修改了
                            contactForUpload.setContactModifyFlag(PhoneContactIndexModel.CONTACT_MODIFY_FLAG_UPDATE);
                            updatedContacts.add(contactForUpload);
                        }
                        contactForUpload.setContactGUID(lastUploadContact.getContactGUID());
                        lastUploadContacts.remove(lastUploadContact);
                        break;
                    }
                    if (contactForUpload.getContactModifyFlag() == PhoneContactIndexModel.CONTACT_MODIFY_FLAG_ADD)
                    {
                        addedContacts.add(contactForUpload);
                    }
                }
            }
            else
            {
                addedContacts.addAll(phoneContacts);
            }
        }
        // 查询出需上传得删除记录
        if (null != lastUploadContacts)
        {
            deleteContacts = new ArrayList<PhoneContactIndexModel>();
            for (PhoneContactIndexModel lastUploadContact : lastUploadContacts)
            {
                lastUploadContact.setContactModifyFlag(PhoneContactIndexModel.CONTACT_MODIFY_FLAG_DELETE);
                deleteContacts.add(lastUploadContact);
            }
        }
        
        Logger.d(TAG, "Added contacts count: "
                + (addedContacts == null ? 0 : addedContacts.size()));
        Logger.d(TAG, "Updated contacts count: "
                + (updatedContacts == null ? 0 : updatedContacts.size()));
        Logger.d(TAG, "Delete contacts count: "
                + (deleteContacts == null ? 0 : deleteContacts.size()));
        
        return new List[] { addedContacts, updatedContacts, deleteContacts };
    }
    
    /**
     * 
     * 获取数据库中的联系人
     */
    private List<PhoneContactIndexModel> getDbContacts()
    {
        List<PhoneContactIndexModel> lastUploadContacts = mPhoneContactIndexDbAdapter.queryAllContact(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        return lastUploadContacts;
    }
    
    /**
     * 获取手机联系人数据
     * 
     * @return 手机联系人数据
     */
    private ArrayList<PhoneContactIndexModel> getPhoneContacts()
    {
        ContentResolver resolver = mContext.getContentResolver();
        ArrayList<PhoneContactIndexModel> phoneContacts = new ArrayList<PhoneContactIndexModel>();
        Cursor cursor = null;
        try
        {
            // 读取手机联系人
            cursor = resolver.query(ContactsContract.Data.CONTENT_URI,
                    null,
                    RawContactsEntity.MIMETYPE + " IN ('"
                            + Phone.CONTENT_ITEM_TYPE + "','"
                            + Email.CONTENT_ITEM_TYPE + "','"
                            + StructuredName.CONTENT_ITEM_TYPE + "')",
                    null,
                    null);
        }
        catch (SecurityException ex)
        {
            Logger.d(TAG, "getPhoneContacts", ex);
        }
        
        if (cursor != null)
        {
            if (cursor.getCount() > 0)
            {
                String contactId = null;
                String mimeType = null;
                String number, email, displayName;
                String numType, emailType;
                PhoneContactIndexModel contact = null;
                
                Map<String, PhoneContactIndexModel> contactMap = new HashMap<String, PhoneContactIndexModel>();
                
                while (cursor.moveToNext())
                {
                    contactId = String.valueOf(cursor.getLong(cursor.getColumnIndex(Data.CONTACT_ID)));
                    if (!contactMap.containsKey(contactId))
                    {
                        contact = new PhoneContactIndexModel();
                        contact.setContactLUID(contactId);
                        contact.setContactType(PhoneContactIndexModel.CONTACT_TYPE_PHONE);
                        contactMap.put(contactId, contact);
                    }
                    else
                    {
                        contact = contactMap.get(contactId);
                    }
                    mimeType = cursor.getString(cursor.getColumnIndex(RawContactsEntity.MIMETYPE));
                    // 类型为电话号码
                    if (Phone.CONTENT_ITEM_TYPE.equals(mimeType))
                    {
                        // 电话号码
                        number = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)))
                                .replaceAll("(-|^\\+86)", "")
                                .trim();
                        // 号码类型:手机、住宅等
                        numType = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Phone.DATA2)))
                                .trim();
                        if (number.length() > MAX_PHONE_NUMBER_LENGTH)
                        {
                            // 对号码进行截取
                            number = number.substring(0,
                                    MAX_PHONE_NUMBER_LENGTH);
                        }
                        // 将电话号码存入contact
                        ArrayList<String> phoneNum = new ArrayList<String>();
                        phoneNum.add(null == number ? "" : number);
                        phoneNum.add(null == numType ? "" : numType);
                        
                        contact.addPhoneNumber(phoneNum);
                    }
                    // 类型为邮件地址
                    else if (Email.CONTENT_ITEM_TYPE.equals(mimeType))
                    {
                        // 邮件地址
                        email = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Email.DATA1)))
                                .trim();
                        // 邮件类型：
                        emailType = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Email.DATA2)))
                                .trim();
                        if (email.length() > MAX_EMAIL_ADDRESS_LENGTH)
                        {
                            // 对Email进行截取
                            email = email.substring(0, MAX_EMAIL_ADDRESS_LENGTH);
                        }
                        
                        // 将电话号码存入contact
                        ArrayList<String> phoneEmail = new ArrayList<String>();
                        phoneEmail.add(null == email ? "" : email);
                        phoneEmail.add(null == emailType ? "" : emailType);
                        
                        contact.addEmailAddr(phoneEmail);
                    }
                    // 类型为联系人名称
                    else if (StructuredName.CONTENT_ITEM_TYPE.equals(mimeType))
                    {
                        displayName = StringUtil.getString(cursor.getString(cursor.getColumnIndex(Email.DATA1)))
                                .trim();
                        if (!StringUtil.isNullOrEmpty(displayName))
                        {
                            contact.setDisplayName(displayName);
                        }
                    }
                }
                
                // 获取通讯录数据,转换为ArrayList
                PhoneContactIndexModel[] contacts = contactMap.values()
                        .toArray(new PhoneContactIndexModel[contactMap.size()]);
                for (PhoneContactIndexModel phoneContact : contacts)
                {
                    generateSpellNameAndInitialName(phoneContact);
                    phoneContacts.add(phoneContact);
                }
            }
            cursor.close();
            
        }
        return phoneContacts;
    }
    
    /**
     * 
     * 获取所有联系人在HiTalk系统中的SysId<BR>
     */
    private void getContactUserIds()
    {
        // 在此处理向服务器请求的东西
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        new ContactManager().sendForContact(sendData, new IHttpListener()
        {
            @Override
            public void onProgress(boolean isInProgress)
            {
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(int action, Response response)
            {
                Logger.d(TAG, "getContactUserIds:" + response.getObj());
                saveContactUserID((List<PhoneContactIndexModel>) response.getObj());
                // 在此返回ContactUserIds
                getPhoneContactData();
            }
        });
    }
    
    /**
     * 
     * 将服务器返回的userId存入数据库中<BR>
     * [功能详细描述]
     * 
     * @param contacts
     *            传入PhoneContactBean
     */
    
    private void saveContactUserID(List<PhoneContactIndexModel> contacts)
    {
        // 关于非UI的逻辑可以在此实现
        if (contacts != null && !contacts.isEmpty())
        {
            try
            {
                String userSysId = FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId();
                for (PhoneContactIndexModel contact : contacts)
                {
                    // 判断PhoneContactIndexColumns.ADDFRIEND_PRIVACY，默认为允许任何人
                    int addFriendPrivacy = PhoneContactIndexModel.ADDFRIENDPRIVACY_ALLOW_ALL;
                    int fp = CONTACT_USER_FRIEND_PRIVACY_ALLOW_ALL;
                    int acf = CONTACT_USER_AUTO_CONFIRM_NO;
                    try
                    {
                        fp = Integer.parseInt(contact.getFp());
                        acf = Integer.parseInt(contact.getAcf());
                    }
                    catch (Exception e)
                    {
                        Logger.e(TAG, "Invalid value: " + e.toString());
                    }
                    
                    if (fp == CONTACT_USER_FRIEND_PRIVACY_NOT_ALLOW)
                    {
                        // 不允许任何人加我
                        addFriendPrivacy = PhoneContactIndexModel.ADDFRIENDPRIVACY_NO_ALLOW;
                    }
                    else if (acf == CONTACT_USER_AUTO_CONFIRM_NO)
                    {
                        // 需要确认
                        addFriendPrivacy = PhoneContactIndexModel.ADDFRIENDPRIVACY_NEED_CONFIRM;
                    }
                    else if (fp == CONTACT_USER_FRIEND_PRIVACY_ALLOW_ALL)
                    {
                        // 允许所有人
                        addFriendPrivacy = PhoneContactIndexModel.ADDFRIENDPRIVACY_ALLOW_ALL;
                    }
                    else if (fp == CONTACT_USER_FRIEND_PRIVACY_ALLOW_BUT_BINDED)
                    {
                        // 允许绑定了手机号码的HiTalk用户
                        addFriendPrivacy = PhoneContactIndexModel.ADDFRIENDPRIVACY_ALLOW_BIND;
                    }
                    else if (fp == CONTACT_USER_FRIEND_PRIVACY_ALLOW_BUT_PHONE)
                    {
                        // 允许通讯录里的联系人
                        addFriendPrivacy = PhoneContactIndexModel.ADDFRIENDPRIVACY_ALLOW_CONTACT;
                    }
                    
                    // 更新数据库
                    ContentValues cv = new ContentValues();
                    cv.put(PhoneContactIndexColumns.ADDFRIEND_PRIVACY,
                            addFriendPrivacy);
                    cv.put(PhoneContactIndexColumns.CONTACT_SYSID,
                            contact.getContactSysId());
                    cv.put(PhoneContactIndexColumns.CONTACT_USERID,
                            contact.getContactUserId());
                    mPhoneContactIndexDbAdapter.updateByContactId(userSysId,
                            contact.getContactGUID(),
                            cv);
                    cv.clear();
                }
            }
            catch (Exception e)
            {
                Logger.e(TAG, "Save contact user id error", e);
                return;
            }
        }
    }
}
