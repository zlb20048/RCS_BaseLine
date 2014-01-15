package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.PhoneContactIndexColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 手机联系人信息表数据库操作 适配器<BR>
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-18]
 */
public class PhoneContactIndexDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "PhoneContactIndexDbAdapter";
    
    /**
     * PhoneContactIndexDbAdapter对象
     */
    private static PhoneContactIndexDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private PhoneContactIndexDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取PhoneContactIndexDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return PhoneContactIndexDbAdapter
     */
    public static synchronized PhoneContactIndexDbAdapter getInstance(
            Context context)
    {
        if (null == sInstance)
        {
            sInstance = new PhoneContactIndexDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入电话联系人信息<BR>
     * [功能详细描述]
     * 
     * @param userSysId 用户的唯一标识
     * @param contact 插入对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertPhoneContactIndex(String userSysId,
            PhoneContactIndexModel contact)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != contact)
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            ContentValues cv = setValues(userSysId, contact);
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
                Logger.i(TAG, "insertPhoneContactIndex, result = " + result);
            }
        }
        else
        {
            Logger.w(TAG, "insertPhoneContactIndex fail, account is null...");
        }
        return result;
    }
    
    /**
     * 插入电话联系人信息<BR>
     * @param userSysId 用户的唯一标识
     * @param cv 插入字段
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertPhoneContactIndex(String userSysId, ContentValues cv)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != cv)
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
                Logger.i(TAG, "insertPhoneContactIndex, result = " + result);
            }
        }
        else
        {
            Logger.w(TAG, "insertPhoneContactIndex fail, account is null...");
        }
        return result;
    }
    
    /**
     * 批量插入联系人信息<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param list 装联系人对象的集合
     * @return 是否插入成功
     */
    public int insertPhoneContactIndex(String userSysId,
            List<PhoneContactIndexModel> list)
    {
        if (!StringUtil.isNullOrEmpty(userSysId) && null != list
                && 0 < list.size())
        {
            int size = list.size();
            ContentValues[] values = new ContentValues[size];
            for (int i = 0; i < size; i++)
            {
                values[i] = setValues(userSysId, list.get(i));
            }
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            return mCr.bulkInsert(uri, values);
        }
        else
        {
            Logger.w(TAG, "insertPhoneContactIndex  fail, list is null...");
            return -1;
        }
    }
    
    /**
     * 根据 contactGUID 删除 当前登录用户的联系人<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactGUID 联系人的本地通讯录ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByGUID(String userSysId, String contactGUID)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactGUID))
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            result = mCr.delete(uri,
                    PhoneContactIndexColumns.USER_SYSID + "=? AND "
                            + PhoneContactIndexColumns.CONTACT_GUID + "=? ",
                    new String[] { userSysId, contactGUID });
            
            Logger.i(TAG, "deleteBy, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteBy fail, contactLUID is null...");
        }
        return result;
    }
    
    /**
     * 根据 userSysId 删除 记录<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByUsersysId(String userSysId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            result = mCr.delete(uri, PhoneContactIndexColumns.USER_SYSID
                    + "=? ", new String[] { userSysId });
            
            Logger.i(TAG, "deleteBy, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteBy fail, userSysId is null...");
        }
        return result;
    }
    
    /**
     * 根据contactLUID 和 contactType 修改其它字段信息<BR>
     * 
     * @param userSysId 的唯一标识
     * @param contactLUID 联系人的本地通讯录ID
     * @param contactType 类型
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateBy(String userSysId, String contactLUID, int contactType,
            ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactLUID) && null != cv)
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            
            result = mCr.update(uri,
                    cv,
                    PhoneContactIndexColumns.USER_SYSID + "=? AND "
                            + PhoneContactIndexColumns.CONTACT_LUID + "=? AND "
                            + PhoneContactIndexColumns.CONTACT_TYPE + "=?",
                    new String[] { userSysId, contactLUID,
                            Integer.toString(contactType) });
            
            Logger.i(TAG, "updateBy, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateBy fail, contactLUID or params is null...");
        }
        return result;
    }
    
    /**
     * 更新所有联系人的系统id<BR>
     * 根据userSysId和contactGUID 更新 contactSysId
     * 
     * @param userSysId 用户的唯一标识
     * @param contacts 要更新的联系人
     */
    public void updateContactUserSysIds(String userSysId,
            List<PhoneContactIndexModel> contacts)
    {
        if (!StringUtil.isNullOrEmpty(userSysId) && null != contacts)
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            ContentValues cv = new ContentValues();
            for (PhoneContactIndexModel contact : contacts)
            {
                cv.clear();
                cv.put(PhoneContactIndexColumns.CONTACT_SYSID,
                        contact.getContactSysId());
                mCr.update(uri,
                        cv,
                        PhoneContactIndexColumns.USER_SYSID + "=? AND "
                                + PhoneContactIndexColumns.CONTACT_GUID + "=?",
                        new String[] { userSysId, contact.getContactGUID() });
            }
        }
        else
        {
            Logger.w(TAG, "updateContactUserSysIds fail, contacts is null...");
        }
    }
    
    /**
     * 根据ContactGUID修改通讯录表<BR>
     * [通用方法]
     * 
     * @param userSysId 用户的唯一标识
     * @param contactGUID 联系人GUID
     * @param params 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByContactId(String userSysId, String contactGUID,
            ContentValues params)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactGUID) && null != params
                && params.size() > 0)
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            
            result = mCr.update(uri,
                    params,
                    PhoneContactIndexColumns.USER_SYSID + "=? AND "
                            + PhoneContactIndexColumns.CONTACT_GUID + "=? ",
                    new String[] { userSysId, contactGUID });
            
            Logger.i(TAG, "updateByContactId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateByContactId fail, contactGUID or params is null...");
        }
        return result;
    }
    
    /**
     * 根据 contactLUID 和 contactType 查询联系人信息
     * 
     * @param userSysId 用户的唯一标识
     * @param contactLUID 联系人的本地通讯录ID
     * @param contactType 联系人类型
     * @return 成功：联系人对象 <br>
     *         失败：null
     */
    public PhoneContactIndexModel queryBy(String userSysId, String contactLUID,
            int contactType)
    {
        PhoneContactIndexModel contact = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByWithCursor(userSysId, contactLUID, contactType);
            if (null != cursor && cursor.moveToFirst())
            {
                contact = parseCursorToPhoneContactIndex(cursor);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return contact;
    }
    
    /**
     * 根据 contactLUID 和 contactType 查询联系人信息
     * 
     * @param userSysId 用户的唯一标识
     * @param friendUserId 好友会话ID
     * @return 成功：联系人对象 <br>
     *         失败：null
     */
    public PhoneContactIndexModel queryByFriendUserId(String userSysId,
            String friendUserId)
    {
        PhoneContactIndexModel contact = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByWithCursor(userSysId, friendUserId);
            if (null != cursor && cursor.moveToFirst())
            {
                contact = parseCursorToPhoneContactIndex(cursor);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return contact;
    }
    
    /**
     * 根据 contactLUID 和 contactType 查询联系人信息
     * 
     * @param userSysId 用户的唯一标识
     * @param contactLUID 联系人的本地通讯录ID
     * @param contactType 联系人类型
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByWithCursor(String userSysId, String contactLUID,
            int contactType)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactLUID))
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            cursor = mCr.query(uri,
                    null,
                    PhoneContactIndexColumns.USER_SYSID + "=? AND "
                            + PhoneContactIndexColumns.CONTACT_LUID + "=? AND "
                            + PhoneContactIndexColumns.CONTACT_TYPE + "=?",
                    new String[] { userSysId, contactLUID,
                            Integer.toString(contactType) },
                    null);
        }
        else
        {
            Logger.i(TAG, "queryBy is fail, contactLUID is null...");
        }
        return cursor;
    }
    
    /**
     * 根据 contactLUID 和 contactType 查询联系人信息
     * 
     * @param userSysId 用户的唯一标识
     * @param friendUserId 好友会话ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByWithCursor(String userSysId, String friendUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.PHONECONTACTINDEX_URI;
            cursor = mCr.query(uri, null, PhoneContactIndexColumns.USER_SYSID
                    + "=? AND " + PhoneContactIndexColumns.CONTACT_USERID
                    + "=? ", new String[] { userSysId, friendUserId }, null);
        }
        else
        {
            Logger.i(TAG, "queryBy is fail, friendUserId is null...");
        }
        return cursor;
    }
    
    /**
     * 查询所有联系人的信息<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功：联系人信息列表 <br>
     *         失败：null
     */
    public List<PhoneContactIndexModel> queryAllContact(String userSysId)
    {
        List<PhoneContactIndexModel> list = null;
        Cursor cursor = null;
        try
        {
            PhoneContactIndexModel contact = null;
            cursor = queryAllContactWithCursor(userSysId);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<PhoneContactIndexModel>();
                while (!cursor.isAfterLast())
                {
                    contact = parseCursorToPhoneContactIndex(cursor);
                    list.add(contact);
                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return list;
    }
    
    /**
     * 查询所有联系人的信息<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryAllContactWithCursor(String userSysId)
    {
        Cursor cursor = null;
        Uri uri = URIField.PHONECONTACTINDEX_URI;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            cursor = mCr.query(uri, null, PhoneContactIndexColumns.USER_SYSID
                    + "=?", new String[] { userSysId }, null);
        }
        return cursor;
    }
    
    /**
     * 根据游标解析联系人信息<BR>
     * 
     * @param cursor 游标对象
     * @return 联系人对象
     */
    private PhoneContactIndexModel parseCursorToPhoneContactIndex(Cursor cursor)
    {
        PhoneContactIndexModel contact = new PhoneContactIndexModel();
        contact.setContactType(cursor.getInt(cursor.getColumnIndex(PhoneContactIndexColumns.CONTACT_TYPE)));
        contact.setContactLUID(cursor.getString(cursor.getColumnIndex(PhoneContactIndexColumns.CONTACT_LUID)));
        contact.setContactGUID(cursor.getString(cursor.getColumnIndex(PhoneContactIndexColumns.CONTACT_GUID)));
        contact.setContactSysId(cursor.getString(cursor.getColumnIndex(PhoneContactIndexColumns.CONTACT_SYSID)));
        contact.setContactCrcValue(cursor.getString(cursor.getColumnIndex(PhoneContactIndexColumns.CONTACT_CRCVALUE)));
        contact.setContactUserId(cursor.getString(cursor.getColumnIndex(PhoneContactIndexColumns.CONTACT_USERID)));
        contact.setAddFriendPrivacy(cursor.getInt(cursor.getColumnIndex(PhoneContactIndexColumns.ADDFRIEND_PRIVACY)));
        return contact;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param userSysId 用户ID
     * @param contact 联系人对象
     * @return ContentValues
     */
    private ContentValues setValues(String userSysId,
            PhoneContactIndexModel contact)
    {
        ContentValues cv = new ContentValues();
        cv.put(PhoneContactIndexColumns.USER_SYSID, userSysId);
        cv.put(PhoneContactIndexColumns.CONTACT_TYPE, contact.getContactType());
        cv.put(PhoneContactIndexColumns.CONTACT_LUID, contact.getContactLUID());
        cv.put(PhoneContactIndexColumns.CONTACT_GUID, contact.getContactGUID());
        cv.put(PhoneContactIndexColumns.CONTACT_SYSID,
                contact.getContactSysId());
        cv.put(PhoneContactIndexColumns.CONTACT_CRCVALUE,
                contact.getContactCrcValue());
        cv.put(PhoneContactIndexColumns.CONTACT_SYSID,
                contact.getContactSysId());
        cv.put(PhoneContactIndexColumns.CONTACT_USERID,
                contact.getContactUserId());
        cv.put(PhoneContactIndexColumns.ADDFRIEND_PRIVACY,
                contact.getAddFriendPrivacy());
        return cv;
    }
}
