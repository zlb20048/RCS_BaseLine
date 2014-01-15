/*
 * 文件名: SysAppInfoAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
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
import com.huawei.basic.android.im.component.database.DatabaseHelper.SysAppInfoColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.logic.model.SysAppInfoModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 系统应用操作数据适配器<BR>
 * 
 * @author 张仙
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 16, 2012] 
 */
public class SysAppInfoAdapter
{
    /**
     * SysAppInfoAdapter对象
     */
    private static SysAppInfoAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private SysAppInfoAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取SysAppInfoAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return SysAppInfoAdapter
     */
    public static synchronized SysAppInfoAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new SysAppInfoAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入一个系统应用<BR>
     * 
     * @param info 系统应用对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insert(SysAppInfoModel info)
    {
        long result = -1;
        if (null != info)
        {
            Uri uri = URIField.SYSAPPINFO_URI;
            ContentValues cv = this.setValues(info);
            
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
            }
        }
        return result;
    }
    
    /**
     * 插入一个系统应用<BR>
     * 
     * @param userSysId 用户系统标识
     * @param info SysAppInfoModel
     * @return 成功：插入的条数 <br>
     *         失败：-1
     */
    public long insert(String userSysId, SysAppInfoModel info)
    {
        long result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId) && info != null)
            {
                Uri uri = URIField.SYSAPPINFO_URI;
                ContentValues cv = this.setValues(userSysId, info);
                
                Uri resultUri = mCr.insert(uri, cv);
                if (resultUri != null)
                {
                    result = ContentUris.parseId(resultUri);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 根据应用ID删除应用<BR>
     * 
     * @param appId 应用ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByAppId(String appId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(appId))
        {
            Uri uri = URIField.SYSAPPINFO_URI;
            result = mCr.delete(uri,
                    SysAppInfoColumns.APP_ID + "=?",
                    new String[] { appId });
        }
        return result;
    }
    
    /**
     * 删除所有应用<BR>
     * 
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteAll()
    {
        int result = -1;
        Uri uri = URIField.SYSAPPINFO_URI;
        result = mCr.delete(uri, null, null);
        return result;
    }
    
    /**
     * 删除应用<BR>
     * 
     * @param userSysId 用户系统标识
     * @param type 应用类型
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteAll(String userSysId, int type)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = URIField.SYSAPPINFO_URI;
                result = mCr.delete(uri,
                        SysAppInfoColumns.USER_SYSID + "=? AND "
                                + SysAppInfoColumns.TYPE + "=?",
                        new String[] { userSysId, String.valueOf(type) });
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 根据应用ID修改系统应用信息<BR>
     * [全量修改]
     * 
     * @param appId 应用ID
     * @param info 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByAppId(String appId, SysAppInfoModel info)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(appId) && null != info)
        {
            Uri uri = URIField.SYSAPPINFO_URI;
            ContentValues cv = this.setValues(info);
            result = mCr.update(uri,
                    cv,
                    SysAppInfoColumns.APP_ID + "=?",
                    new String[] { appId });
        }
        return result;
    }
    
    /**
     * 根据应用ID修改系统应用部分字段<BR>
     * [通用方法]
     * 
     * @param appId 应用ID
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByAppId(String appId, ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(appId) && null != cv && 0 < cv.size())
        {
            Uri uri = URIField.SYSAPPINFO_URI;
            result = mCr.update(uri,
                    cv,
                    SysAppInfoColumns.APP_ID + "=?",
                    new String[] { appId });
        }
        return result;
    }
    
    /**
     * 根据应用ID修改系统应用信息<BR>
     * [全量修改]
     * 
     * @param userSysId 用户系统标识
     * @param appId 应用ID
     * @param info 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByAppId(String userSysId, String appId,
            SysAppInfoModel info)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(appId) && info != null)
            {
                Uri uri = URIField.SYSAPPINFO_URI;
                ContentValues cv = this.setValues(userSysId, info);
                cv.remove(SysAppInfoColumns.APP_ID);
                result = mCr.update(uri,
                        cv,
                        SysAppInfoColumns.USER_SYSID + "=? AND "
                                + SysAppInfoColumns.APP_ID + "=?",
                        new String[] { userSysId, appId });
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 根据appId查询系统应用<BR>
     * 
     * @param appId 应用ID
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByAppIdWithCursor(String appId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(appId))
        {
            Uri uri = URIField.SYSAPPINFO_URI;
            cursor = mCr.query(uri,
                    null,
                    SysAppInfoColumns.APP_ID + "=?",
                    new String[] { appId },
                    null);
        }
        return cursor;
    }
    
    /**
     * 根据appId查询系统应用<BR>
     * 
     * @param userSysId 用户系统标识
     * @param appId 应用ID
     * @param type 应用类型
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByAppIdWithCursor(String userSysId, String appId,
            int type)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(appId))
            {
                Uri uri = Uri.withAppendedPath(URIField.SYSAPPINFO_APPID_URI,
                        userSysId + "/" + appId + "/" + type);
                cursor = mCr.query(uri, null, null, null, null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 
     * 根据appId查询系统应用<BR>
     * @param userSyId 用户系统ID
     * @param appId 应用 ID
     * @param type 应用类型
     * @return SysAppInfoModel对象
     */
    public SysAppInfoModel queryByAppId(String userSyId, String appId, int type)
    {
        SysAppInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = this.queryByAppIdWithCursor(userSyId, appId, type);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToSysAppInfoModel(cursor);
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
     * 查询所有系统应用<BR>
     * 
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryAllWithCursor()
    {
        return mCr.query(URIField.SYSAPPINFO_URI, null, null, null, null);
    }
    
    /**
     * 查询所有系统应用<BR>
     * 
     * @return 成功：SysAppInfoModel对象<br>
     *         失败：null
     */
    public List<SysAppInfoModel> queryAll()
    {
        List<SysAppInfoModel> list = null;
        Cursor cursor = null;
        try
        {
            SysAppInfoModel info = null;
            cursor = this.queryAllWithCursor();
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<SysAppInfoModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToSysAppInfoModel(cursor);
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
     * 查询所有系统应用<BR>
     * 
     * @param userSysId 用户系统标识
     * @param type 应用类型
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryAllWithCursor(String userSysId, int type)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = Uri.withAppendedPath(URIField.SYSAPPINFO_ALL_URI,
                        userSysId + "/" + type);
                cursor = mCr.query(uri, null, null, null, null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 查询所有系统应用<BR>
     * 
     * @param userSysId 用户系统标识
     * @param type 应用类型
     * @return 成功：SysAppInfoModel对象<br>
     *         失败：null
     */
    public List<SysAppInfoModel> queryAll(String userSysId, int type)
    {
        List<SysAppInfoModel> list = null;
        Cursor cursor = null;
        try
        {
            SysAppInfoModel info = null;
            cursor = this.queryAllWithCursor(userSysId, type);
            if (cursor != null && cursor.moveToFirst())
            {
                list = new ArrayList<SysAppInfoModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToSysAppInfoModel(cursor);
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
     * 根据游标解析系统应用信息<BR>
     * 
     * @param cursor 游标对象
     * @return SysAppInfoModel
     */
    private SysAppInfoModel parseCursorToSysAppInfoModel(Cursor cursor)
    {
        SysAppInfoModel info = new SysAppInfoModel();
        info.setAppId(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.APP_ID)));
        info.setName(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.NAME)));
        info.setType(cursor.getInt(cursor.getColumnIndex(SysAppInfoColumns.TYPE)));
        info.setDesc(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.DESC)));
        info.setIconName(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.ICON_NAME)));
        info.setIconUrl(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.ICON_URL)));
        info.setAppUrl(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.APP_URL)));
        info.setUpdateTime(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.UPDATE_TIME)));
        info.setSso(cursor.getString(cursor.getColumnIndex(SysAppInfoColumns.SSO)));
        return info;
    }
    
    /**
     * 把SysAppInfoModel对象解析成ContentValues对象<BR>
     * 
     * 
     * @param info SysAppInfoModel 系统应用信息对象
     * @return ContentValues 解析系统后获得的ContentValues对象
     */
    private ContentValues setValues(SysAppInfoModel info)
    {
        ContentValues cv = new ContentValues();
        cv.put(SysAppInfoColumns.APP_ID, info.getAppId());
        cv.put(SysAppInfoColumns.NAME, info.getName());
        cv.put(SysAppInfoColumns.TYPE, info.getType());
        cv.put(SysAppInfoColumns.DESC, info.getDesc());
        cv.put(SysAppInfoColumns.ICON_NAME, info.getIconName());
        cv.put(SysAppInfoColumns.ICON_URL, info.getIconUrl());
        cv.put(SysAppInfoColumns.APP_URL, info.getAppUrl());
        cv.put(SysAppInfoColumns.UPDATE_TIME, info.getUpdateTime());
        cv.put(SysAppInfoColumns.SSO, info.getSso());
        return cv;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param info SysAppInfoModel
     * @return ContentValues
     */
    private ContentValues setValues(String userSysId, SysAppInfoModel info)
    {
        ContentValues cv = new ContentValues();
        cv.put(SysAppInfoColumns.USER_SYSID, userSysId);
        cv.put(SysAppInfoColumns.APP_ID, info.getAppId());
        cv.put(SysAppInfoColumns.NAME, info.getName());
        cv.put(SysAppInfoColumns.TYPE, info.getType());
        cv.put(SysAppInfoColumns.DESC, info.getDesc());
        cv.put(SysAppInfoColumns.ICON_NAME, info.getIconName());
        cv.put(SysAppInfoColumns.APP_URL, info.getAppUrl());
        cv.put(SysAppInfoColumns.ICON_URL, info.getIconUrl());
        cv.put(SysAppInfoColumns.UPDATE_TIME, info.getUpdateTime());
        cv.put(SysAppInfoColumns.SSO, info.getSso());
        return cv;
    }
    
}
