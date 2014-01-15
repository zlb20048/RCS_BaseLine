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
import com.huawei.basic.android.im.component.database.DatabaseHelper.MyAppColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.SysAppInfoColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.logic.model.SysAppInfoModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 我的应用数据库操作适配器<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Apr 11, 2012]
 */
public class MyAppDbAdapter
{
    /**
     * MyAppDbAdapter对象
     */
    private static MyAppDbAdapter instance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver cr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private MyAppDbAdapter(Context context)
    {
        cr = context.getContentResolver();
    }
    
    /**
     * 获取MyAppDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return MyAppDbAdapter
     */
    public static synchronized MyAppDbAdapter getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new MyAppDbAdapter(context);
        }
        return instance;
    }
    
    /**
     * 插入新的"我的应用"<BR>
     * 
     * @param userSysId 用户系统标识
     * @param appId 应用ID
     * @return 成功：插入的条数 <br>
     *         失败：-1
     */
    public long insert(String userSysId, String appId)
    {
        long result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(appId))
            {
                Uri uri = URIField.MY_APP_URI;
                ContentValues cv = new ContentValues();
                cv.put(MyAppColumns.USER_SYSID, userSysId);
                cv.put(MyAppColumns.APP_ID, appId);
                Uri resultUri = cr.insert(uri, cv);
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
     * 删除"我的应用"<BR>
     * 
     * @param userSysId 用户系统标识
     * @param appId 应用ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByAppId(String userSysId, String appId)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(appId))
            {
                Uri uri = URIField.MY_APP_URI;
                result = cr.delete(uri, MyAppColumns.USER_SYSID + "=? AND "
                        + MyAppColumns.APP_ID + "=?", new String[] { userSysId,
                        appId });
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 查询所有"我的应用"<BR>
     * 
     * @param userSysId 用户系统标识
     * @return 成功：Cursor <br>
     *         失败：null
     */
    public Cursor queryAllWithCursor(String userSysId)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = Uri.withAppendedPath(URIField.MY_APP_ALL_URI,
                        userSysId);
                cursor = cr.query(uri, null, null, null, null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 查询所有"我的应用"<BR>
     * 
     * @param userSysId 用户系统标识
     * @return 成功："我的应用"列表 <br>
     *         失败：null
     */
    public List<SysAppInfoModel> queryAll(String userSysId)
    {
        List<SysAppInfoModel> list = null;
        SysAppInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = this.queryAllWithCursor(userSysId);
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
     * 查询所有"我的应用"ID.<BR>
     * 
     * @param userSysId 用户系统标识
     * @return 成功："我的应用"ID集合 <br>
     *         失败：null
     */
    public List<String> queryAllAppIds(String userSysId)
    {
        Cursor cursor = null;
        List<String> appIds = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = URIField.MY_APP_URI;
                cursor = cr.query(uri,
                        null,
                        MyAppColumns.USER_SYSID + "=?",
                        new String[] { userSysId },
                        MyAppColumns.APP_ID);
                if (cursor != null && cursor.moveToFirst())
                {
                    appIds = new ArrayList<String>();
                    while (!cursor.isAfterLast())
                    {
                        appIds.add(cursor.getString(cursor.getColumnIndex(MyAppColumns.APP_ID)));
                        cursor.moveToNext();
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
        return appIds;
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
    
}
