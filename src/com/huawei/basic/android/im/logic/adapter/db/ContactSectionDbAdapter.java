package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactSectionColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ContactSectionModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 联系人分组数据库操作适配器<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public class ContactSectionDbAdapter
{
    
    /**
     * TAG
     */
    private static final String TAG = "ContactSectionDbAdapter";
    
    /**
     * ContactSectionDbAdapter对象
     */
    private static ContactSectionDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 上下文对象
     */
    private Context mContext;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private ContactSectionDbAdapter(Context context)
    {
        mContext = context;
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取ContactSectionDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return AccountAdapter
     */
    public static synchronized ContactSectionDbAdapter getInstance(
            Context context)
    {
        if (null == sInstance)
        {
            sInstance = new ContactSectionDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入分组<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param cs 分组对象
     * @return 成功：插入后记录的ID<br>
     *         失败：-1
     */
    public long insertContactSection(String userSysId, ContactSectionModel cs)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != cs
                && !StringUtil.isNullOrEmpty(cs.getName()))
        {
            Uri uri = URIField.CONTACTSECTION_URI;
            ContentValues cv = new ContentValues();
            cv.put(ContactSectionColumns.CONTACTSECTION_ID,
                    cs.getContactSectionId());
            cv.put(ContactSectionColumns.USER_SYSID, userSysId);
            cv.put(ContactSectionColumns.NAME, cs.getName());
            cv.put(ContactSectionColumns.NOTES, cs.getNotes());
            
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
                Logger.i(TAG, "insertContactSection, result = " + result);
            }
        }
        else
        {
            Logger.w(TAG, "insertContactSection fail, cs is null...");
        }
        return result;
    }
    
    /**
     * 根据分组ID删除分组<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionId 分组ID
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public int deleteByContactSectionId(String userSysId,
            String contactSectionId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactSectionId))
        {
            Uri uri = URIField.CONTACTSECTION_URI;
            result = mCr.delete(uri,
                    ContactSectionColumns.CONTACTSECTION_ID + "=? AND "
                            + ContactSectionColumns.USER_SYSID + "=?",
                    new String[] { contactSectionId, userSysId });
            
            Logger.i(TAG, "deleteByContactSectionId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "deleteByContactSectionId fail, contactSectionId is null...");
        }
        return result;
    }
    
    /**
     * 根据SysId删除分组信息
     * @param userSysId userSysId
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public int deleteBySysId(String userSysId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.CONTACTSECTION_URI;
            result = mCr.delete(uri,
                    ContactSectionColumns.USER_SYSID + "=?",
                    new String[] { userSysId });
            
            Logger.i(TAG, "deleteByContactSectionId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "deleteByContactSectionId fail, contactSectionId is null...");
        }
        return result;
    }
    
    /**
     * 修改分组信息<BR>
     * [全量修改] 会修改传入参数cs的所有属性
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionId 分组ID
     * @param cs 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByContactSectionId(String userSysId,
            String contactSectionId, ContactSectionModel cs)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactSectionId) && null != cs)
        {
            Uri uri = URIField.CONTACTSECTION_URI;
            ContentValues cv = new ContentValues();
            cv.put(ContactSectionColumns.NAME, cs.getName());
            cv.put(ContactSectionColumns.NOTES, cs.getNotes());
            
            result = mCr.update(uri,
                    cv,
                    ContactSectionColumns.CONTACTSECTION_ID + "=? AND "
                            + ContactSectionColumns.USER_SYSID + "=?",
                    new String[] { contactSectionId, userSysId });
            
            Logger.i(TAG, "updateContactSection, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateContactSection fail, contactSectionId or cs is null...");
        }
        return result;
    }
    
    /**
     * 根据contactSectionId修改账号信息部分字段<BR>
     * [通用方法]
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionId 分组ID
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateContactSection(String userSysId, String contactSectionId,
            ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactSectionId) && null != cv
                && 0 < cv.size())
        {
            Uri uri = URIField.CONTACTSECTION_URI;
            
            result = mCr.update(uri,
                    cv,
                    ContactSectionColumns.CONTACTSECTION_ID + "=? AND "
                            + ContactSectionColumns.USER_SYSID + "=?",
                    new String[] { contactSectionId, userSysId });
            Logger.i(TAG, "updateContactSection, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateContactSection fail, contactSectionId or params is null...");
        }
        return result;
    }
    
    /**
     * 根据分组ID查询分组信息,不包括组成员<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionId 分组ID
     * @return 成功：分组对象； <br>
     *         失败：null
     */
    public ContactSectionModel queryByContactSectionId(String userSysId,
            String contactSectionId)
    {
        ContactSectionModel contactSection = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(contactSectionId))
            {
                Uri uri = URIField.CONTACTSECTION_URI;
                cursor = mCr.query(uri,
                        null,
                        ContactSectionColumns.CONTACTSECTION_ID + "=? AND "
                                + ContactSectionColumns.USER_SYSID + "=?",
                        new String[] { contactSectionId, userSysId },
                        null);
                
                if (null != cursor && cursor.moveToFirst())
                {
                    contactSection = parseCursorToContactSection(cursor);
                }
            }
            else
            {
                Logger.w(TAG,
                        "queryByContactSectionId fail, contactSectionId is null...");
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
        return contactSection;
    }
    
    /**
     * 根据分组ID查询分组信息,不包括组成员<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionId 分组ID
     * @return 成功：Cursor对象； <br>
     *         失败：null
     */
    public Cursor queryByContactSectionIdWithCursor(String userSysId,
            String contactSectionId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactSectionId))
        {
            Uri uri = URIField.CONTACTSECTION_URI;
            cursor = mCr.query(uri,
                    null,
                    ContactSectionColumns.CONTACTSECTION_ID + "=? AND "
                            + ContactSectionColumns.USER_SYSID + "=?",
                    new String[] { contactSectionId, userSysId },
                    null);
        }
        else
        {
            Logger.w(TAG,
                    "queryByContactSectionId fail, contactSectionId is null...");
        }
        return cursor;
    }
    
    /**
     * 查询所有分组，纯粹的分组，不包括组成员<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功： 分组列表 <br>
     *         失败：null
     */
    public ArrayList<ContactSectionModel> queryAllContactSection(
            String userSysId)
    {
        ArrayList<ContactSectionModel> contactList = null;
        Cursor cursor = null;
        try
        {
            ContactSectionModel cl = null;
            
            cursor = this.queryAllContactSectionWithCursor(userSysId);
            
            if (null != cursor && cursor.moveToFirst())
            {
                contactList = new ArrayList<ContactSectionModel>();
                while (!cursor.isAfterLast())
                {
                    cl = parseCursorToContactSection(cursor);
                    contactList.add(cl);
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
        return contactList;
    }
    
    /**
     * 批量插入 好友分组信息
     * 
     * @param userSysId 用户的唯一标识
     * @param list 插入对象的集合<br>
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public int applyInsertContactSection(String userSysId,
            List<ContactSectionModel> list)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != list
                && list.size() > 0)
        {
            int size = list.size();
            ContentValues[] values = new ContentValues[size];
            for (int i = 0; i < size; i++)
            {
                values[i] = setValues(userSysId, list.get(i));
            }
            Uri uri = URIField.CONTACTSECTION_URI;
            result = mCr.bulkInsert(uri, values);
            Logger.i(TAG, "insertContactSection, result = " + result);
        }
        else
        {
            Logger.w(TAG, "insertContactSection fail, info is null...");
        }
        return result;
    }
    
    /**
     * 查询所有分组，纯粹的分组，不包括组成员<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功： Cursor对象； <br>
     *         失败：null
     */
    public Cursor queryAllContactSectionWithCursor(String userSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.CONTACTSECTION_URI;
            cursor = mCr.query(uri,
                    null,
                    ContactSectionColumns.USER_SYSID + "=?",
                    new String[] { userSysId },
                    ContactSectionColumns.NAME);
        }
        return cursor;
    }
    
    /**
     * 根据游标解析联系人分组信息<BR>
     * 
     * @param cursor 游标对象
     * @return 分组对象
     */
    private ContactSectionModel parseCursorToContactSection(Cursor cursor)
    {
        ContactSectionModel cs = new ContactSectionModel();
        String contactSectionIdString = cursor.getString(cursor.getColumnIndex(ContactSectionColumns.CONTACTSECTION_ID));
        if (ContactSectionModel.DEFAULT_SECTION_ID.equals(contactSectionIdString))
        {
            cs.setName(mContext.getResources()
                    .getString(R.string.default_section_name));
        }
        else
        {
            cs.setName(cursor.getString(cursor.getColumnIndex(ContactSectionColumns.NAME)));
        }
        cs.setContactSectionId(contactSectionIdString);
        cs.setNotes(cursor.getString(cursor.getColumnIndex(ContactSectionColumns.NOTES)));
        return cs;
    }
    
    /**
     * 
     * 将model中的数据放入到contentValues对象中<BR>
     * @param userSysId 用户系统id
     * @param model contactSection 对象
     * @return contentValues对象
     */
    private ContentValues setValues(String userSysId, ContactSectionModel model)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactSectionColumns.CONTACTSECTION_ID,
                model.getContactSectionId());
        contentValues.put(ContactSectionColumns.NAME, model.getName());
        contentValues.put(ContactSectionColumns.NOTES, model.getNotes());
        contentValues.put(ContactSectionColumns.USER_SYSID, userSysId);
        return contentValues;
    }
    
}
