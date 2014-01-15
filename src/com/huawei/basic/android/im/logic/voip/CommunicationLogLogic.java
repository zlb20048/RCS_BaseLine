/*
 * 文件名: CommunicationLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 王媛媛
 * 创建时间:2012-3-15
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.voip;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.component.database.voip.VoipURIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.voip.CommunicationLogDbAdapter;
import com.huawei.basic.android.im.logic.model.voip.CommunicationLog;

/**
 *  通话记录及详情的逻辑处理类
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-15] 
 */
public class CommunicationLogLogic extends BaseLogic implements
        ICommunicationLogLogic
{
    /**
     * Debug Tag
     */
    private static final String TAG = "CommunicationLogLogic";
    
    /**
     * VOIP通话记录数据操作适配器
     *  
     */
    private CommunicationLogDbAdapter mCommLogDbAdapter;
    
    /**
     * 构造方法:得到单例的 Voip账号数据操作适配器和个人/好友信息表数据库操作适配器
     * @param context  Context
     */
    public CommunicationLogLogic(Context context)
    {
        super();
        mCommLogDbAdapter = CommunicationLogDbAdapter.getInstance(context);
    }
    
    /**
     * 
     * 发送获取通话记录消息
     * @see com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic#getAllCommunicationLogs()
     */
    public void getAllCommunicationLogs()
    {       
        //发送获取通话记录消息
        sendEmptyMessage(FusionMessageType.VOIPMessageType.COMM_GET_ALL_COMM_LOG);      
    }
    
    /**
     * 
     * 查询所有的通话记录
     * @return 查询的所有的CommunicationLog集合
     * @see com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic#getAllCommunicationLogsFromDB()
     */
    public List<CommunicationLog> getAllCommunicationLogsFromDB()
    {
        int startIndex = FusionCode.Common.PAGE_START_INDEX;
        int recordNum = FusionCode.Common.PAGE_RECORD_COUNT;
        //获取当前用户的id
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        mCommLogDbAdapter.deleteCommunicationLogGroupByOwnerSysId(ownerUserId,
                recordNum);
        List<CommunicationLog> list = mCommLogDbAdapter.findCommunicationLogGroupByOwnerSysId(ownerUserId,
                startIndex,
                recordNum);
        return list;       
    }
    
    
    
    /**
     * 根据来电或去电用户id查询该用户所有的通讯记录详情
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param remoteUri   对方的voip账号
     * @param remotePhoneNum  对方的手机号
     * @param startIndex 开始下标
     * @param recordNum  记录总数
     * @see com.huawei.basic.android.im.logic.voip.
     * ICommunicationLogLogic#loadCommunicationLogByRemoteUriOrRemotePhoneNum
     * (java.lang.String, java.lang.String, int, int)
     */
    @Override
    public void loadCommunicationLogByRemoteUriOrRemotePhoneNum(
            String remoteUri, String remotePhoneNum, int startIndex,
            int recordNum)
    {
        //获取当前用户的id
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        //多余10条通话记录就删除
        mCommLogDbAdapter.deleteCommunicationLogByRemoteUriOrRemotePhoneNum(ownerUserId,
                remoteUri,
                remotePhoneNum,
                startIndex);
        List<CommunicationLog> list = mCommLogDbAdapter.findCommunicationLogByRemoteUriOrRemotePhoneNum(ownerUserId,
                remoteUri,
                remotePhoneNum,
                startIndex,
                recordNum);
        sendMessage(FusionMessageType.VOIPMessageType.COMM_GET_COMM_LOG_DETAIL,
                list);
        
    }
    
    /**
     * 根据来电或去电用户remoteUri或remotePhoneNum删除该用户所有的通讯记录详情
     *  
     * @param remoteUri 来电或去电用户voip账号
     * @param remotePhoneNum 来电或去电用户电话号码
     */
    @Override
    public void deleteByRemoteUriOrRemotePhoneNum(String remoteUri,
            String remotePhoneNum)
    {
        
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        mCommLogDbAdapter.deleteByRemoteUriOrRemotePhoneNum(ownerUserId,
                remoteUri,
                remotePhoneNum);
        sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_DELETE_COMM_LOG);
    }
    
    /**
     *  删除所属用户ID需要显示通话记录以外的通话记录
     * 
     * @param startIndex 开始删除条数下标
     * @see com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic#deleteCommunicationLogGroupByOwnerSysId(int)
     */
    @Override
    public void deleteCommunicationLogGroupByOwnerSysId(int startIndex)
    {
        //所属用户ID
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        mCommLogDbAdapter.deleteCommunicationLogGroupByOwnerSysId(ownerUserId,
                startIndex);
    }
    
    /**
     *删除通话详情记录 10 条以外的记录
     * @param remoteUri
     * @param remotePhoneNum
     * @param startIndex
     */
    //    public void deleteCommunicationLogDetail(String remoteUri,String remotePhoneNum,int startIndex ){
    //        //获取当前用户的id
    //        String ownerUserId = FusionConfig.getInstance()
    //                .getAasResult()
    //                .getUserSysId();
    //        //多余10条通话记录就删除
    //        mCommLogDbAdapter.deleteCommunicationLogByRemoteUriOrRemotePhoneNum(ownerUserId,
    //                remoteUri,
    //                remotePhoneNum,
    //                startIndex);
    //    }
    
    /**
     * 获取未读电话数量
     *  
     * @return  未读电话数量
     * @see com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic#getUnreadTotal()
     */
    
    public int getUnreadTotal()
    {
        //所属用户ID
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        Log.d(TAG, "getUnreadTotal $ownerUserId" + ownerUserId);
        if (null != ownerUserId)
        {
            return mCommLogDbAdapter.getUnreadTotal(ownerUserId);
        }
        return 0;
    }
    
    /**
     * 
     * 根据所属用户ID更新通讯记录为已读
      * @param remoteUri voip号
       * @param remotePhoneNum 手机号
     */
    public void updateToIsReadByOwnerUserId(String remoteUri,
            String remotePhoneNum)
    {
        //所属用户ID
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        mCommLogDbAdapter.updateToIsReadByOwnerUserId(ownerUserId,
                remoteUri,
                remotePhoneNum);
    }
    
    /**
     * 
     *获取本地通讯录联系人
     * @param context context
     * @param phoneNumber 手机号或voip号
     * @return PhoneContact
     * @see com.huawei.basic.android.im.logic.voip.
     * ICommunicationLogLogic#getPhoneContacts
     * (android.content.Context, java.lang.String)
     */
    public PhoneContact getPhoneContacts(Context context, String phoneNumber)
    {
        ContentResolver resolver = context.getContentResolver();
        
        // 获取手机联系人  
        String[] projection = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER,
                Phone.PHOTO_ID, Phone.CONTACT_ID };
        Cursor phoneCursor = null;
        //添加异常处理,针对一些定制android系统(Coolpad 8870)可以禁止访问系统权限
        try
        {
            phoneCursor = resolver.query(Phone.CONTENT_URI,
                    projection,
                    " PHONE_NUMBERS_EQUAL(" + Phone.NUMBER + ", ?,'0')",
                    new String[] { phoneNumber },
                    null);
        }
        catch (SecurityException ex)
        {
            Logger.d(TAG, "getPhoneContacts", ex);
        }
        PhoneContact contact = null;
        if (phoneCursor != null)
        {
            while (phoneCursor.moveToNext())
            {
                //当手机号码为空的或者为空字段 跳过当前循环  
                if (TextUtils.isEmpty(phoneNumber))
                {
                    continue;
                }
                //得到联系人名称  
                String contactName = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.DISPLAY_NAME));
                //得到联系人ID  
                Long contactId = phoneCursor.getLong(phoneCursor.getColumnIndex(Phone.CONTACT_ID));
                //得到联系人头像ID  
                Long photoId = phoneCursor.getLong(phoneCursor.getColumnIndex(Phone.PHOTO_ID));
                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的  
                byte[] faceData = null;
                if (photoId > 0)
                {
                    
                    Cursor curphoto = resolver.query(Data.CONTENT_URI,
                            new String[] { ContactsContract.Contacts.Photo.DATA15 },
                            "_id = " + photoId,
                            null,
                            null);
                    if (null != curphoto)
                    {
                        if (curphoto.moveToFirst())
                        {
                            faceData = curphoto.getBlob(0);
                        }
                        curphoto.close();
                    }
                }
                contact = new PhoneContact();
                contact.setContactId(contactId);
                contact.setPhotoId(photoId);
                contact.setPhoneNumber(phoneNumber);
                contact.setContactName(contactName);
                contact.setFaceData(faceData);
            }
            phoneCursor.close();
        }
        return contact;
    }
    
    /**
     * 
     * 对 该logic对象 定义所有被监听的Uri 重载父类的方法，传入Uri数组来监听该logic所要监听的数据库对象
     * @return 返回为空将不监听任何数据库表变化
     * @see com.huawei.basic.android.im.framework.logic.BaseLogic#getObserverUris()
     */
    @Override
    protected Uri[] getObserverUris()
    {
        return new Uri[] { VoipURIField.COMMUNICATION_LOG_URI,
                Phone.CONTENT_URI };
    }
    
    /**
     * 数据表变化时被回调的方法
     * 
    * @param selfChange 如果是true，被监听是由于代码执行了commit造成的
     * @param uri 被监听的Uri
     * @see com.huawei.basic.android.im.framework.logic.BaseLogic#onChangeByUri(boolean, android.net.Uri)
     */
    @Override
    protected void onChangeByUri(boolean selfChange, Uri uri)
    {
        
        //延时发送监听到的变化
        if (uri == VoipURIField.COMMUNICATION_LOG_URI)
        {
            
            //发送获取通话记录消息
            sendEmptyMessageDelayed(FusionMessageType.VOIPMessageType.COMM_GET_ALL_COMM_LOG,
                    2000);
            
            //更新详情界面和未读记录的消息
            sendEmptyMessageDelayed(FusionMessageType.VOIPMessageType.VOIP_CALL_AGAIN,
                    2000);
            
            //发送更新未读数的消息
            sendEmptyMessageDelayed(FusionMessageType.VOIPMessageType.VOIP_CHANGE_COMM_LOG_UNREAD_TOTAL,
                    2000);
        }
        else if (uri == Phone.CONTENT_URI)
        {
            
            // 将陌生号添加到系统通讯录的消息
            sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_ADD_CANTACT);
        }
    }
    
    /**
     * 手机联系人通讯录
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-23]
     */
    public class PhoneContact
    {
        private long contactId;
        
        private long photoId;
        
        private String contactName;
        
        private String phoneNumber;
        
        private byte[] faceData;
        
        public long getContactId()
        {
            return contactId;
        }
        
        public void setContactId(long contactId)
        {
            this.contactId = contactId;
        }
        
        public long getPhotoId()
        {
            return photoId;
        }
        
        public void setPhotoId(long photoId)
        {
            this.photoId = photoId;
        }
        
        public String getContactName()
        {
            return contactName;
        }
        
        public void setContactName(String contactName)
        {
            this.contactName = contactName;
        }
        
        public String getPhoneNumber()
        {
            return phoneNumber;
        }
        
        public void setPhoneNumber(String phoneNumber)
        {
            this.phoneNumber = phoneNumber;
        }
        
        public byte[] getFaceData()
        {
            return faceData;
        }
        
        public void setFaceData(byte[] faceData)
        {
            this.faceData = faceData;
        }
        
    }
    
    /**
     * 删除所有的通话记录
     * @see com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic#deleteAllCommunicationLogs()
     */
    @Override
    public void deleteAllCommunicationLogs()
    {
        //所属用户ID
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        mCommLogDbAdapter.deleteCommunicationLogByOwnerUserId(ownerUserId);
    }
    
}
