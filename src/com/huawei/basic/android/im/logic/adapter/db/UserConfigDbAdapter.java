/*
 * 文件名: UserConfigDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.db;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.UserConfigColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.utils.StringUtil;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 *  用户全局配置表数据库操作类<BR>
 * [功能详细描述]
 * @author 张仙
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 15, 2012] 
 */
public class UserConfigDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "UserConfigDbAdapter";
    
    /**
     * UserConfigDbAdapter对象
     */
    private static UserConfigDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private UserConfigDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取UserConfigDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return UserConfigDbAdapter
     */
    public static synchronized UserConfigDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new UserConfigDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入配置项<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param config 配置项对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertUserConfig(String userSysId, UserConfigModel config)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != config)
        {
            Uri uri = URIField.USERCONFIG_URI;
            ContentValues cv = new ContentValues();
            cv.put(UserConfigColumns.USER_SYSID, userSysId);
            cv.put(UserConfigColumns.KEY, config.getKey());
            cv.put(UserConfigColumns.VALUE, config.getValue());
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
                Logger.i(TAG, "insertUserConfig, result = " + result);
            }
        }
        else
        {
            Logger.w(TAG, "insertUserConfig fail, config is null...");
        }
        return result;
    }
    
    /**
     * 插入配置项<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param key 键
     * @param value 键对应的值
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertUserConfig(String userSysId, String key, String value)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != key)
        {
            Uri uri = URIField.USERCONFIG_URI;
            ContentValues cv = new ContentValues();
            cv.put(UserConfigColumns.USER_SYSID, userSysId);
            cv.put(UserConfigColumns.KEY, key);
            cv.put(UserConfigColumns.VALUE, null == value ? "" : value);
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
                Logger.i(TAG, "insertUserConfig, result = " + result);
            }
        }
        else
        {
            Logger.w(TAG, "insertUserConfig fail, config is null...");
        }
        return result;
    }
    
    /**
     * 根据名称删除配置项<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param key 配置项名称
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByKey(String userSysId, String key)
    {
        int result = -1;
        Uri uri = URIField.USERCONFIG_URI;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(key))
        {
            result = mCr.delete(uri, UserConfigColumns.KEY + "=? AND "
                    + UserConfigColumns.USER_SYSID + "=?", new String[] { key,
                    userSysId });
            Logger.i(TAG, "deleteByKey, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByKey fail, key is null...");
        }
        return result;
    }
    
    /**
     * 根据配置项名称修改配置项值<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param key 新的配置项值
     * @param config 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByKey(String userSysId, String key, UserConfigModel config)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(key) && null != config)
        {
            Uri uri = URIField.USERCONFIG_URI;
            ContentValues cv = new ContentValues();
            cv.put(UserConfigColumns.KEY, config.getKey());
            cv.put(UserConfigColumns.VALUE, config.getValue());
            result = mCr.update(uri, cv, UserConfigColumns.KEY + "=? AND "
                    + UserConfigColumns.USER_SYSID + "=?", new String[] { key,
                    userSysId });
            Logger.i(TAG, "updateByKey, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateByKey fail, value is null...");
        }
        return result;
    }
    
    /**
     * 根据配置项名称修改配置项值<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param key 新的配置项值
     * @param value 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByKey(String userSysId, String key, String value)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(key))
        {
            Uri uri = URIField.USERCONFIG_URI;
            ContentValues cv = new ContentValues();
            cv.put(UserConfigColumns.KEY, key);
            cv.put(UserConfigColumns.VALUE, value == null ? "" : value);
            result = mCr.update(uri, cv, UserConfigColumns.KEY + "=? AND "
                    + UserConfigColumns.USER_SYSID + "=?", new String[] { key,
                    userSysId });
            Logger.i(TAG, "updateByKey, result = " + result);
        }
        return result;
    }
    
    /**
     * 根据配置项名称 查询配置项对象<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param key 配置项名称
     * @return 成功：配置项对象<br>
     *         失败：null
     */
    public UserConfigModel queryByKey(String userSysId, String key)
    {
        Logger.i(TAG, userSysId + "," + key);
        UserConfigModel config = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByKeyWithCursor(userSysId, key);
            if (null != cursor && cursor.moveToFirst())
            {
                Logger.i(TAG,
                        "queryByKeyWithCursor, value= "
                                + cursor.getString(cursor.getColumnIndex(UserConfigColumns.VALUE)));
                config = parseCursorToUserConfig(cursor);
                Logger.i(TAG, "queryByKey, value= " + config.getValue());
            }
            else
            {
                Logger.w(TAG, "queryByKey fail, value is null...");
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
        return config;
    }
    
    /**
     * 根据配置项名称 查询配置项对象<BR>
     * 
     * @param userSysId 用户的唯一标识
     * @param key 配置项名称
     * @return 成功：配置项对象<br>
     *         失败：null
     */
    public Cursor queryByKeyWithCursor(String userSysId, String key)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(key))
        {
            
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(key))
            {
                Uri uri = URIField.USERCONFIG_URI;
                cursor = mCr.query(uri, null, UserConfigColumns.KEY + "=? AND "
                        + UserConfigColumns.USER_SYSID + "=?", new String[] {
                        key, userSysId }, null);
            }
            else
            {
                Logger.w(TAG, "queryByKeyWithCursor fail, value is null...");
            }
        }
        else
        {
            Logger.w(TAG, "queryByKeyWithCursor fail, value is null...");
        }
        return cursor;
    }
    
    /**
     * 根据游标解析配置项信息<BR>
     * 
     * @param cursor 游标对象
     * @return 配置项对象
     */
    private UserConfigModel parseCursorToUserConfig(Cursor cursor)
    {
        UserConfigModel config = new UserConfigModel();
        config.setKey(cursor.getString(cursor.getColumnIndex(UserConfigColumns.KEY)));
        config.setValue(cursor.getString(cursor.getColumnIndex(UserConfigColumns.VALUE)));
        Logger.i(TAG, "parseCursorToUserConfig, value= " + config.getValue());
        return config;
    }
    
}
