package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;

/**
 * 
 * 手机联系人操作 适配器<BR>
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-5-16]
 */
public class PhoneLocalAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "PhoneLocalAdapter";
    
    /**
     * PhoneLocalAdapter对象
     */
    private static PhoneLocalAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private PhoneLocalAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取PhoneLocalAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return PhoneLocalAdapter
     */
    public static synchronized PhoneLocalAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new PhoneLocalAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 
     * 根据PhoneContactIndexModel对象查询手机通讯录数据<BR>
     * 
     * @param contactId ContactId
     * @return PhoneContactIndexModel对象
     */
    public PhoneContactIndexModel queryContactByID(String contactId)
    {
        if (null == contactId)
        {
            return null;
        }
        Cursor dataCursor = null;
        PhoneContactIndexModel phoneContactIndexModel = null;
        Uri uri = Uri.parse("content://com.android.contacts/contacts/"
                + contactId + "/data");
        
        // 添加异常处理,针对一些定制android系统(Coolpad 8870)可以禁止访问系统权限
        try
        {
            dataCursor = mCr.query(uri, new String[] { Data.DATA1, Data.DATA2,
                    Data.MIMETYPE }, null, null, null);
        }
        catch (SecurityException ex)
        {
            Logger.d(TAG, "getPhoneContacts", ex);
        }
        
        if (null != dataCursor)
        {
            phoneContactIndexModel = new PhoneContactIndexModel();
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
        return phoneContactIndexModel;
    }
}
