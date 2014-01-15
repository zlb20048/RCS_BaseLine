/*
 * 文件名: ContactInfoDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 8, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FaceThumbnailColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.Tables;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 个人/好友信息表数据库操作 适配器<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class ContactInfoDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "ContactInfoDbAdapter";
    
    /**
     * ContactInfoDbAdapter对象
     */
    private static ContactInfoDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private ContactInfoDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取ContactInfoDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return ContactInfoDbAdapter
     */
    public static synchronized ContactInfoDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new ContactInfoDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入 个人/好友信息
     * 
     * @param userSysId 用户的唯一标识
     * @param info 插入对象<br>
     *            ( 注：插入个人信息时，info对象中，必须 friendSysId = userSysId, friendUserId =
     *            userId )
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertContactInfo(String userSysId, ContactInfoModel info)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != info)
        {
            Uri uri = URIField.CONTACTINFO_URI;
            ContentValues cv = setValues(userSysId, info);
            
            Uri resultUri = mCr.insert(uri, cv);
            if (resultUri != null)
            {
                result = ContentUris.parseId(resultUri);
            }
            
            Logger.i(TAG, "insertContactInfo, result = " + result);
        }
        else
        {
            Logger.w(TAG, "insertContactInfo fail, info is null...");
        }
        return result;
    }
    
    /**
     * 批量插入 个人/好友信息
     * 
     * @param userSysId 用户的唯一标识
     * @param list 插入对象的集合<br>
     *            ( 注：插入个人信息时，info对象中，必须 friendSysId = userSysId, friendUserId =
     *            userId )
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public int insertContactInfo(String userSysId, List<ContactInfoModel> list)
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
            Uri uri = URIField.CONTACTINFO_URI;
            result = mCr.bulkInsert(uri, values);
            Logger.i(TAG, "insertContactInfo, result = " + result);
        }
        else
        {
            Logger.w(TAG, "insertContactInfo fail, info is null...");
        }
        return result;
    }
    
    /**
     * 根据friendUserId删除好友<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param friendUserId ID，也是JID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByFriendUserId(String userSysId, String friendUserId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.CONTACTINFO_URI;
            result = mCr.delete(uri,
                    ContactInfoColumns.FRIEND_USERID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=? ",
                    new String[] { friendUserId, userSysId });
            
            Logger.i(TAG, "deleteByFriendUserId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByFriendUserId fail, friendUserId is null...");
        }
        return result;
    }
    
    /**
     * 根据userSysId删除好友<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByUserSysId(String userSysId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.CONTACTINFO_URI;
            result = mCr.delete(uri,
                    ContactInfoColumns.USER_SYSID + "=? ",
                    new String[] { userSysId });
            
            Logger.i(TAG, "deleteByUserSysId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByUserSysId fail, userSysId is null...");
        }
        return result;
    }
    
    /**
     * 根据friendSysId修改好友信息<BR>
     * [通用方法]
     * 
     * @param userSysId 用户的唯一标识
     * @param friendSysId 好友ID
     * @param params 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByFriendSysId(String userSysId, String friendSysId,
            ContentValues params)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendSysId) && null != params
                && params.size() > 0)
        {
            Uri uri = URIField.CONTACTINFO_URI;
            
            result = mCr.update(uri,
                    params,
                    ContactInfoColumns.FRIEND_SYSID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=? ",
                    new String[] { friendSysId, userSysId });
            
            Logger.i(TAG, "updateFriendByFriendSysId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateFriendByFriendSysId fail, friendSysId or params is null...");
        }
        return result;
    }
    
    /**
     * 根据friendSysId修改好友信息<BR>
     * [全量修改] 会修改传入参数info的所有属性
     * 
     * @param userSysId 用户的唯一标识
     * @param friendSysId 好友ID
     * @param info 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByFriendSysId(String userSysId, String friendSysId,
            ContactInfoModel info)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendSysId) && null != info)
        {
            Uri uri = URIField.CONTACTINFO_URI;
            ContentValues cv = setValues(userSysId, info);
            
            result = mCr.update(uri,
                    cv,
                    ContactInfoColumns.FRIEND_SYSID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=? ",
                    new String[] { friendSysId, userSysId });
            
            Logger.i(TAG, "updateFriendByFriendSysId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateFriendByFriendSysId fail, friendSysId or params is null...");
        }
        return result;
    }
    
    /**
     * 根据friendUserId修改好友信息<BR>
     * [通用方法]
     * 
     * @param userSysId 用户的唯一标识
     * @param friendUserId 好友JID
     * @param params 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByFriendUserId(String userSysId, String friendUserId,
            ContentValues params)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId) && null != params
                && params.size() > 0)
        {
            Uri uri = URIField.CONTACTINFO_URI;
            
            result = mCr.update(uri,
                    params,
                    ContactInfoColumns.FRIEND_USERID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=? ",
                    new String[] { friendUserId, userSysId });
            
            Logger.i(TAG, "updateByFriendUserId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateByFriendUserId fail, friendSysId or params is null...");
        }
        return result;
    }
    
    /**
     * 根据friendUserId修改好友信息<BR>
     * [全量修改] 会修改传入参数info的所有属性
     * 
     * @param userSysId 用户的唯一标识
     * @param friendUserId 好友JID
     * @param info 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByFriendUserId(String userSysId, String friendUserId,
            ContactInfoModel info)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId) && null != info)
        {
            Uri uri = URIField.CONTACTINFO_URI;
            ContentValues cv = setValues(userSysId, info);
            
            result = mCr.update(uri,
                    cv,
                    ContactInfoColumns.FRIEND_USERID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=? ",
                    new String[] { friendUserId, userSysId });
            
            Logger.i(TAG, "updateFriendByFriendSysId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "updateByFriendUserId fail, friendSysId or params is null...");
        }
        return result;
    }
    
    /**
     * 修改个人信息<BR>
     * [通用方法]
     * 
     * @param userSysId 用户的唯一标识
     * @param params 修改字段集合
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateMyProfile(String userSysId, Map<String, Object> params)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != params
                && params.size() > 0)
        {
            Uri uri = URIField.CONTACTINFO_URI;
            
            ContentValues cv = setValues(params);
            
            result = mCr.update(uri,
                    cv,
                    ContactInfoColumns.FRIEND_SYSID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=? ",
                    new String[] { userSysId, userSysId });
            
            Logger.i(TAG, "updateMyProfile, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateMyProfile fail, params is null...");
        }
        return result;
    }
    
    /**
     * 获取个人信息<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功：个人信息<br>
     *         失败：null
     */
    public ContactInfoModel queryMyProfile(String userSysId)
    {
        return queryByFriendSysIdWithPrivate(userSysId, userSysId);
    }
    
    /**
     * 获取个人信息<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryMyProfileWithCursor(String userSysId)
    {
        return queryByFriendUserIdWithPrivateCursor(userSysId, userSysId);
    }
    
    /**
     * OK 根据friendSysId 关联查询个人/好友详细信息，包括头像和分组信息。<BR>
     * 
     * @param userSysId 当前登录用户系统标识
     * @param friendSysId 好友ID，如果是获取个人信息的话，friendSysId = userSysId
     * @return 成功：个人/好友对象<br>
     *         失败：null
     */
    public ContactInfoModel queryByFriendSysIdWithPrivate(String userSysId,
            String friendSysId)
    {
        ContactInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByFriendSysIdWithPrivateCursor(userSysId, friendSysId);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToContactInfoModel(cursor);
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
        return info;
    }
    
    /**
     * OK 根据friendUserId 关联查询个人/好友详细信息，包括头像和分组信息。<BR>
     * 
     * @param userSysId 当前登录用户系统标识
     * @param friendUserId 好友ID，如果是获取个人信息的话，friendSysId = userSysId
     * @return 成功：个人/好友对象<br>
     *         失败：null
     */
    public ContactInfoModel queryByFriendUserIdWithPrivate(String userSysId,
            String friendUserId)
    {
        ContactInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByFriendUserIdWithPrivateCursor(userSysId,
                    friendUserId);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToContactInfoModel(cursor);
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
        return info;
    }
    
    /**
     * OK 根据friendSysId 关联查询好友详细信息，包括头像和分组信息。<BR>
     * 
     * @param userSysId 当前登录用户的系统标识
     * @param friendUserId 好友ID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByFriendUserIdWithPrivateCursor(String userSysId,
            String friendUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION_URI;
            cursor = mCr.query(uri, null, Tables.CONTACTINFO + "."
                    + ContactInfoColumns.FRIEND_USERID + "=? AND "
                    + Tables.CONTACTINFO + "." + ContactInfoColumns.USER_SYSID
                    + "=?", new String[] { friendUserId, userSysId }, null);
        }
        else
        {
            Logger.w(TAG,
                    "queryByFriendSysIdWithPrivateCursor fail, friendSysId is null...");
        }
        return cursor;
    }
    
    /**
     * OK 根据friendSysId 关联查询好友详细信息，包括头像和分组信息。<BR>
     * 
     * @param userSysId 当前登录用户的系统标识
     * @param friendSysId 好友ID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByFriendSysIdWithPrivateCursor(String userSysId,
            String friendSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendSysId))
        {
            Uri uri = URIField.CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION_URI;
            cursor = mCr.query(uri, null, Tables.CONTACTINFO + "."
                    + ContactInfoColumns.FRIEND_SYSID + "=? AND "
                    + Tables.CONTACTINFO + "." + ContactInfoColumns.USER_SYSID
                    + "=?", new String[] { friendSysId, userSysId }, null);
        }
        else
        {
            Logger.w(TAG,
                    "queryByFriendSysIdWithPrivateCursor fail, friendSysId is null...");
        }
        return cursor;
    }
    
    /**
     * 根据friendSysId 查询好友信息(不作关联查询)，不包括头像和分组信息。<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param friendSysId 好友ID
     * @return 成功：好友对象<br>
     *         失败：null
     */
    public ContactInfoModel queryByFriendSysIdNoUnion(String userSysId,
            String friendSysId)
    {
        ContactInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByFriendSysIdNoUnionWithCursor(userSysId, friendSysId);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToContactInfoModel(cursor);
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
        return info;
    }
    
    /**
     * OK 根据friendSysId 查询好友信息(不作关联查询)，不包括头像和分组信息。<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param friendSysId 好友ID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByFriendSysIdNoUnionWithCursor(String userSysId,
            String friendSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendSysId))
        {
            Uri uri = URIField.CONTACTINFO_URI;
            cursor = mCr.query(uri,
                    null,
                    ContactInfoColumns.FRIEND_SYSID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=?",
                    new String[] { friendSysId, userSysId },
                    null);
        }
        else
        {
            Logger.w(TAG,
                    "queryByFriendSysIdWithPublicCursor fail, friendSysId is null...");
        }
        return cursor;
    }
    
    /**
     * OK 根据friendUserId 查询好友信息(不作关联查询)，不包括头像和分组信息。<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param friendUserId 好友JID
     * @return 成功：好友对象<br>
     *         失败：null
     */
    public ContactInfoModel queryByFriendUserIdNoUnion(String userSysId,
            String friendUserId)
    {
        ContactInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByFriendUserIdNoUnionWithCursor(userSysId,
                    friendUserId);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToContactInfoModel(cursor);
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
        return info;
    }
    
    /**
     *根据电话号码查询ContactInfoModel
     * @param userSysId 用户ID
     * @param phone 电话号码
     * @return ContactInfoModel对象
     */
    public ContactInfoModel queryContactInfoByPhone(String userSysId,
            String phone)
    {
        ContactInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByPhone(userSysId, phone);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToContactInfoModel(cursor);
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
        return info;
    }
    
    /**
     *  根据电话号码查询
     * @param userSysId
     * @param phone
     * @return
     */
    private Cursor queryByPhone(String userSysId, String phone)
    {
        Cursor cursor = null;
        if (null != userSysId && null != phone)
        {
            Uri uri = URIField.CONTACTINFO_URI;
            cursor = mCr.query(uri, null, " PHONE_NUMBERS_EQUAL("
                    + ContactInfoColumns.PRIMARY_MOBILE + ", ?,'0') AND "
                    + ContactInfoColumns.USER_SYSID + "=?", new String[] {
                    phone, userSysId },

            null);
        }
        else
        {
            Logger.w(TAG, "queryByPhone fail, phone is null...");
        }
        return cursor;
    }
    
    /**
     * OK 根据friendUserId 查询好友信息(不作关联查询)，不包括头像和分组信息。<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param friendUserId 好友JID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByFriendUserIdNoUnionWithCursor(String userSysId,
            String friendUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.CONTACTINFO_URI;
            cursor = mCr.query(uri,
                    null,
                    ContactInfoColumns.FRIEND_USERID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=?",
                    new String[] { friendUserId, userSysId },
                    null);
        }
        else
        {
            Logger.w(TAG,
                    "queryByFriendUserIdWithPublicCursor fail, friendSysId is null...");
        }
        return cursor;
    }
    
    /**
     * 按 A到Z排序 查询所有好友列表<BR>
     * 
     * @param userSysId 当前登录用户系统标识
     * @return 成功：好友列表<br>
     *         失败：null
     */
    public ArrayList<ContactInfoModel> queryAllWithAZ(String userSysId)
    {
        ArrayList<ContactInfoModel> list = null;
        Cursor cursor = null;
        try
        {
            ContactInfoModel info = null;
            cursor = queryAllWithAZCursor(userSysId);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<ContactInfoModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToContactInfoModel(cursor);
                    list.add(info);
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
     * 按 A到Z排序 查询所有好友列表<BR>
     * 
     * @param userSysId 当前登录用户系统标识
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryAllWithAZCursor(String userSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.CONTACTINFO_URI;
            cursor = mCr.query(uri,
                    null,
                    ContactInfoColumns.USER_SYSID + "=? AND "
                            + ContactInfoColumns.FRIEND_SYSID + "!=?",
                    new String[] { userSysId, userSysId },
                    ContactInfoColumns.DISPLAY_SPELLNAME);
        }
        return cursor;
    }
    
    /**
     * 关联查询好友的头像的url（不包含好友的头像数据）<BR>
     * @param userSysId 当前用户的sysId
     * @return 好友列表
     */
    public ArrayList<ContactInfoModel> queryAllWithFaceUrl(String userSysId)
    {
        
        ArrayList<ContactInfoModel> list = new ArrayList<ContactInfoModel>();
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = URIField.CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_URI;
                cursor = mCr.query(uri,
                        null,
                        ContactInfoColumns.USER_SYSID + "=? AND "
                                + ContactInfoColumns.FRIEND_SYSID + "!=?",
                        new String[] { userSysId, userSysId },
                        null);
            }
            if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    ContactInfoModel model = parseCursorToContactInfoModel(cursor);
                    if (null != model)
                    {
                        list.add(model);
                    }
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            if (cursor != null)
            {
                DatabaseHelper.closeCursor(cursor);
            }
        }
        return list;
    }
    
    /**
     * 查询当前用户好友的总数量(包括自己)<BR>
     * 
     * @param userSysId 用户系统标识
     * @return 成功：好友的数量<br>
     *         失败：0
     */
    public int queryCountWithSelf(String userSysId)
    {
        int result = 0;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = URIField.CONTACTINFO_URI;
                cursor = mCr.query(uri, null, ContactInfoColumns.USER_SYSID
                        + "=?", new String[] { userSysId }, null);
                if (null != cursor)
                {
                    result = cursor.getCount();
                    if (result > 0)
                    {
                        result = result - 1;
                    }
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
        return result;
    }
    
    /**
     * 根据分组ID查询好友列表<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionId 分组ID
     * @return 成功：好友列表<br>
     *         失败：null
     */
    public ArrayList<ContactInfoModel> queryByContactSectionId(
            String userSysId, String contactSectionId)
    {
        ArrayList<ContactInfoModel> list = null;
        Cursor cursor = null;
        try
        {
            ContactInfoModel info = null;
            cursor = queryByContactSectionIdWithCursor(userSysId,
                    contactSectionId);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<ContactInfoModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToContactInfoModel(cursor);
                    list.add(info);
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
     * 根据分组ID查询好友列表<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionId 分组ID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByContactSectionIdWithCursor(String userSysId,
            String contactSectionId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(contactSectionId))
        {
            Uri uri = URIField.CONTACTINFO_URI;
            cursor = mCr.query(uri,
                    null,
                    ContactInfoColumns.CONTACT_SECTIONID + "=? AND "
                            + ContactInfoColumns.USER_SYSID + "=? AND "
                            + ContactInfoColumns.FRIEND_SYSID + "!= ?",
                    new String[] { contactSectionId, userSysId, userSysId },
                    ContactInfoColumns.DISPLAY_SPELLNAME);
        }
        else
        {
            Logger.w(TAG,
                    "queryByContactSectionId fail, contactSectionId is null...");
        }
        return cursor;
    }
    
    /**
     * 根据分组ID 批量 查询好友列表, 不关联头像<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionIds 分组ID集合
     * @return 成功：好友列表<br>
     *         失败：null
     */
    public List<ContactInfoModel> queryByContactSectionIds(String userSysId,
            List<String> contactSectionIds)
    {
        List<ContactInfoModel> list = null;
        Cursor cursor = null;
        try
        {
            ContactInfoModel info = null;
            cursor = queryByContactSectionIdsWithCursor(userSysId,
                    contactSectionIds);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<ContactInfoModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToContactInfoModel(cursor);
                    list.add(info);
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
     * 根据分组ID 批量 查询好友列表 <BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param contactSectionIds 分组ID集合
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByContactSectionIdsWithCursor(String userSysId,
            List<String> contactSectionIds)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != contactSectionIds
                && 0 < contactSectionIds.size())
        {
            Uri uri = URIField.CONTACTINFO_URI;
            String selectionWhere = ContactInfoColumns.CONTACT_SECTIONID
                    + " IN(";
            String[] selectionArgs = new String[contactSectionIds.size() + 1];
            
            for (int i = 0; i < contactSectionIds.size(); i++)
            {
                if (i == (contactSectionIds.size() - 1))
                {
                    selectionWhere += "?) ";
                }
                else
                {
                    selectionWhere += "?, ";
                }
                selectionArgs[i] = contactSectionIds.get(i);
            }
            
            selectionWhere += " AND " + ContactInfoColumns.USER_SYSID + "=?";
            selectionArgs[contactSectionIds.size()] = userSysId;
            
            cursor = mCr.query(uri,
                    null,
                    selectionWhere,
                    selectionArgs,
                    ContactInfoColumns.DISPLAY_SPELLNAME);
        }
        else
        {
            Logger.w(TAG,
                    "queryByContactSectionIds fail, contactSectionIds is null...");
        }
        return cursor;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param userSysId 用户系统标识
     * @param info ContactInfoModel
     * @return ContentValues
     */
    private ContentValues setValues(String userSysId, ContactInfoModel info)
    {
        ContentValues cv = new ContentValues();
        cv.put(ContactInfoColumns.USER_SYSID, userSysId);
        cv.put(ContactInfoColumns.FRIEND_SYSID, info.getFriendSysId());
        cv.put(ContactInfoColumns.FRIEND_USERID, info.getFriendUserId());
        cv.put(ContactInfoColumns.MIDDLE_NAME, info.getMiddleName());
        cv.put(ContactInfoColumns.LAST_NAME, info.getLastName());
        cv.put(ContactInfoColumns.NICK_NAME, info.getNickName());
        if (null != info.getPrimaryMobile())
        {
            cv.put(ContactInfoColumns.PRIMARY_MOBILE, info.getPrimaryMobile());
        }
        if (null != info.getPrimaryEmail())
        {
            cv.put(ContactInfoColumns.PRIMARY_EMAIL, info.getPrimaryEmail());
        }
        cv.put(ContactInfoColumns.SIGNATURE, info.getSignature());
        cv.put(ContactInfoColumns.DESCRIPTION, info.getDescription());
        cv.put(ContactInfoColumns.TOBE_BIND_PRIMARYMOBILE,
                info.getToBeBindPrimaryMobile());
        cv.put(ContactInfoColumns.TOBE_BIND_EMAIL,
                info.getToBeBindPrimaryEmail());
        cv.put(ContactInfoColumns.LEVEL, info.getLevel());
        cv.put(ContactInfoColumns.MEMO_NAME, info.getMemoName());
        cv.put(ContactInfoColumns.MEMO_PHONE,
                StringUtil.listToString(info.getMemoPhones(), null));
        cv.put(ContactInfoColumns.MEMO_EMAIL,
                StringUtil.listToString(info.getMemoEmails(), null));
        cv.put(ContactInfoColumns.GENDER, info.getGender());
        cv.put(ContactInfoColumns.BIRTHDAY, info.getBirthday());
        cv.put(ContactInfoColumns.MARRIAGE_STATUS, info.getMarriageStatus());
        cv.put(ContactInfoColumns.AGE, info.getAge());
        cv.put(ContactInfoColumns.ZODIAC, info.getZodiac());
        cv.put(ContactInfoColumns.ASTRO, info.getAstro());
        cv.put(ContactInfoColumns.BLOOD, info.getBlood());
        cv.put(ContactInfoColumns.HOBBY, info.getHobby());
        cv.put(ContactInfoColumns.COMPANY, info.getCompany());
        cv.put(ContactInfoColumns.DEPARTMENT, info.getDeparment());
        cv.put(ContactInfoColumns.TITLE, info.getTitle());
        cv.put(ContactInfoColumns.SCHOOL, info.getSchool());
        cv.put(ContactInfoColumns.COURSE, info.getCourse());
        cv.put(ContactInfoColumns.BATCH, info.getBatch());
        if (!StringUtil.isNullOrEmpty(info.getCountry()))
        {
            cv.put(ContactInfoColumns.COUNTRY, info.getCountry());
        }
        cv.put(ContactInfoColumns.PROVINCE, info.getProvince());
        cv.put(ContactInfoColumns.CITY, info.getCity());
        cv.put(ContactInfoColumns.STREET, info.getStreet());
        cv.put(ContactInfoColumns.ADDRESS, info.getAddress());
        cv.put(ContactInfoColumns.POSTALCODE, info.getPostalCode());
        cv.put(ContactInfoColumns.BUILDING, info.getBuilding());
        cv.put(ContactInfoColumns.LAST_UPDATE, info.getLastUpdate());
        cv.put(ContactInfoColumns.HOME_LOCATION, info.getHomeLocation());
        if (null != info.getContactSectionId())
        {
            cv.put(ContactInfoColumns.CONTACT_SECTIONID,
                    info.getContactSectionId());
        }
        return cv;
    }
    
    /**
     * 
     * 把map转成contentvalues
     * @param params map
     * @return contentvalues
     */
    private ContentValues setValues(Map<String, Object> params)
    {
        ContentValues cv = new ContentValues();
        
        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (ContactInfoColumns.MEMO_PHONE.equals(key)
                    || ContactInfoColumns.MEMO_EMAIL.equals(key))
            {
                // 处理value为List的情况
                @SuppressWarnings("unchecked")
                List<String> lists = (List<String>) value;
                if (null != lists && lists.size() > 0)
                {
                    cv.put(key, StringUtil.listToString(lists, null));
                }
                else
                {
                    cv.put(key, "");
                }
            }
            else
            {
                if (null == value)
                {
                    cv.put(key, "");
                }
                else
                {
                    cv.put(key, value + "");
                }
            }
        }
        return cv;
    }
    
    /**
     * 根据游标解析个人/好友信息<BR>
     * 
     * @param cursor 游标
     * @return 个人/好友对象
     */
    private ContactInfoModel parseCursorToContactInfoModel(Cursor cursor)
    {
        ContactInfoModel info = new ContactInfoModel();
        info.setFriendSysId(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.FRIEND_SYSID)));
        info.setFriendUserId(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.FRIEND_USERID)));
        // info.setUserName(cursor.getString(cursor
        // .getColumnIndex(ContactInfoColumns.USER_NAME)));
        info.setMiddleName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.MIDDLE_NAME)));
        info.setLastName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.LAST_NAME)));
        // info.setFace(cursor.getString(cursor
        // .getColumnIndex(ContactInfoColumns.FACE)));
        info.setDisplayName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.DISPLAY_NAME)));
        info.setDisplaySpellName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.DISPLAY_SPELLNAME)));
        info.setNickName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.NICK_NAME)));
        info.setPrimaryMobile(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.PRIMARY_MOBILE)));
        info.setPrimaryEmail(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.PRIMARY_EMAIL)));
        info.setSignature(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.SIGNATURE)));
        info.setDescription(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.DESCRIPTION)));
        info.setSpellName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.SPELLNAME)));
        info.setInitialName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.INITIALNAME)));
        info.setGender(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.GENDER)));
        info.setBirthday(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.BIRTHDAY)));
        info.setMarriageStatus(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.MARRIAGE_STATUS)));
        info.setAge(String.valueOf(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.AGE))));
        info.setZodiac(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.ZODIAC)));
        info.setAstro(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.ASTRO)));
        info.setBlood(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.BLOOD)));
        info.setHobby(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.HOBBY)));
        info.setCompany(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.COMPANY)));
        info.setDeparment(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.DEPARTMENT)));
        info.setTitle(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.TITLE)));
        info.setSchool(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.SCHOOL)));
        info.setCourse(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.COURSE)));
        info.setBatch(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.BATCH)));
        info.setCountry(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.COUNTRY)));
        info.setProvince(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.PROVINCE)));
        info.setCity(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.CITY)));
        info.setStreet(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.STREET)));
        info.setAddress(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.ADDRESS)));
        info.setPostalCode(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.POSTALCODE)));
        info.setBuilding(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.BUILDING)));
        info.setLevel(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.LEVEL)));
        info.setMemoName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.MEMO_NAME)));
        info.setMemoPhones(StringUtil.parseStringToList(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.MEMO_PHONE)),
                null));
        info.setMemoEmails(StringUtil.parseStringToList(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.MEMO_EMAIL)),
                null));
        info.setLastUpdate(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.LAST_UPDATE)));
        info.setToBeBindPrimaryMobile(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.TOBE_BIND_PRIMARYMOBILE)));
        info.setToBeBindPrimaryEmail(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.TOBE_BIND_EMAIL)));
        info.setHomeLocation(cursor.getInt(cursor.getColumnIndex(ContactInfoColumns.HOME_LOCATION)));
        
        int faceUrlColumn = cursor.getColumnIndex(FaceThumbnailColumns.FACE_URL);
        if (-1 != faceUrlColumn)
        {
            info.setFaceUrl(cursor.getString(faceUrlColumn));
        }
        int groupIdColumn = cursor.getColumnIndex(ContactInfoColumns.QUERY_CONTACT_SECTIONID);
        if (-1 != groupIdColumn)
        {
            info.setContactSectionId(cursor.getString(groupIdColumn));
        }
        else
        {
            info.setContactSectionId(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.CONTACT_SECTIONID)));
        }
        
        int groupNameColumn = cursor.getColumnIndex(ContactInfoColumns.QUERY_CONTACT_SECTIONNAME);
        if (-1 != groupNameColumn)
        {
            info.setContactSectionName(cursor.getString(groupNameColumn));
        }
        
        int groupNotesColumn = cursor.getColumnIndex(ContactInfoColumns.QUERY_CONTACT_SECTIONNOTES);
        if (-1 != groupNotesColumn)
        {
            info.setContactSectionNotes(cursor.getString(groupNotesColumn));
        }
        
        return info;
    }
    
}
