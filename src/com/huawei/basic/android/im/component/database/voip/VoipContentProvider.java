/*
 * 文件名: VoipContentProvider.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: VOIP数据库ContentProvider
 * 创建人: 刘鲁宁
 * 创建时间:Mar 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.database.voip;

import java.util.HashSet;

import com.huawei.basic.android.im.component.database.SQLiteContentProvider;
import com.huawei.basic.android.im.component.database.voip.VoipDatabaseHelper.Tables;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * VOIP数据库ContentProvider
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 15, 2012] 
 */
public class VoipContentProvider extends SQLiteContentProvider
{
    /**
     * URI键值队
     */
    private static final UriMatcher URIMATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);
    
    /**
     * 执行SQL操作的match
     */
    private static final int EXECUTE = 1;
    
    /**
     * 查询SQL操作的match
     */
    private static final int QUERY = 2;
    
    /**
     * 账号表的match
     */
    private static final int VOIP_ACCOUNT = 3;
    
    /**
     * 通话记录表的match
     */
    private static final int COMMUNICATION_LOG = 4;
    
    /**
     * 数据库通知对象map
     */
    private HashSet<Uri> mNotifyChangeUri;
    
    /**
     * 加载URI
     */
    static
    {
        URIMATCHER.addURI(VoipURIField.AUTHORITY, VoipURIField.EXECUTE, EXECUTE);
        
        URIMATCHER.addURI(VoipURIField.AUTHORITY, VoipURIField.QUERY, QUERY);
        
        // 账号
        URIMATCHER.addURI(VoipURIField.AUTHORITY,
                Tables.VOIP_ACCOUNT,
                VOIP_ACCOUNT);
        
        URIMATCHER.addURI(VoipURIField.AUTHORITY,
                Tables.COMMUNICATION_LOG,
                COMMUNICATION_LOG);
    }
    
    /**
     * 创建方法
     * @return
     *      是否创建成功
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider#onCreate()
     */
    @Override
    public boolean onCreate()
    {
        super.onCreate();
        mDb = getDatabaseHelper().getWritableDatabase();
        mNotifyChangeUri = new HashSet<Uri>();
        return mDb != null;
    }
    
    /**
     * 获取databaseHelper对象
     * @param context
     *      Context对象
     * @return
     *      SQLiteOpenHelper 对象
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider#getDatabaseHelper(android.content.Context)
     */
    
    @Override
    protected SQLiteOpenHelper getDatabaseHelper(Context context)
    {
        // TODO Auto-generated method stub
        return VoipDatabaseHelper.getInstance(context);
    }
    
    /**
     * 在事务中保存数据
     * @param uri
     *      数据库表的URI
     * @param values
     *      数据封装对象
     * @return
     *      数据库表的URI
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider#insertInTransaction(android.net.Uri, android.content.ContentValues)
     */
    
    @Override
    protected Uri insertInTransaction(Uri uri, ContentValues values)
    {
        String tableName = getTableNameByUri(uri);
        long rowId = mDb.insert(tableName, null, values);
        if (rowId > 0)
        {
            // 通知数据变更
            notifyChange(uri);
        }
        return ContentUris.withAppendedId(uri, rowId);
    }
    
    /**
     * 在事务中更新数据
     * @param uri
     *      数据库表的URI
     * @param values
     *      数据封装对象
     * @param selection
     *      查询条件语句
     * @param selectionArgs
     *      查询条件参数
     * @return
     *      更新数据数量
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider#updateInTransaction(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    
    @Override
    protected int updateInTransaction(Uri uri, ContentValues values,
            String selection, String[] selectionArgs)
    {
        String tableName = getTableNameByUri(uri);
        int count = mDb.update(tableName, values, selection, selectionArgs);
        if (count > 0)
        {
            // 通知数据变更
            notifyChange(uri);
        }
        return count;
    }
    
    /**
     * 在事务中删除数据
     * @param uri
     *      数据库表的URI
     * @param selection
     *      查询条件语句
     * @param selectionArgs
     *      查询条件参数
     * @return
     *      删除数据数量
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider#deleteInTransaction(android.net.Uri, java.lang.String, java.lang.String[])
     */
    
    @Override
    protected int deleteInTransaction(Uri uri, String selection,
            String[] selectionArgs)
    {
        String tableName = getTableNameByUri(uri);
        int count = mDb.delete(tableName, selection, selectionArgs);
        if (count > 0)
        {
            // 通知数据变更
            notifyChange(uri);
        }
        return count;
    }
    
    /**
     * 通知数据变更<BR>
     * @param uri 
     *      数据库表的URI
     */
    private void notifyChange(Uri uri)
    {
        mNotifyChangeUri.add(uri);
    }
    
    /**
     * 根据Uri匹配出数据库表名<BR>
     * @param uri 
     *      数据库表的URI
     * @return 
     *      数据库表名
     */
    private String getTableNameByUri(Uri uri)
    {
        if (uri == null)
        {
            return null;
        }
        int match = URIMATCHER.match(uri);
        switch (match)
        {
            case VOIP_ACCOUNT:
                return Tables.VOIP_ACCOUNT;
            case COMMUNICATION_LOG:
                return Tables.COMMUNICATION_LOG;
            default:
                return null;
        }
    }
    
    /**
     * 根据数据库表名匹配出Uri<BR>
     * @param tableName 
     *      数据库表名
     * @return 
     *      数据库表Uri
     */
    private Uri getUriByTableName(String tableName)
    {
        if (tableName == null)
        {
            return null;
        }
        if (tableName.equals(Tables.VOIP_ACCOUNT))
        {
            return VoipURIField.VOIP_ACCOUNT_URI;
        }
        else if (tableName.equals(Tables.COMMUNICATION_LOG))
        {
            return VoipURIField.COMMUNICATION_LOG_URI;
        }
        return null;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param uri
     *      数据库表的URI
     * @param projection
     *      游标读取列
     * @param selection
     *      查询条件语句
     * @param selectionArgs
     *      查询条件参数
     * @param sortOrder
     *      排序条件
     * @return
     *      游标对象
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        Cursor cursor = null;
        String tableName = this.getTableNameByUri(uri);
        int match = URIMATCHER.match(uri);
        switch (match)
        {
            case EXECUTE:
                tableName = sortOrder;
                Uri tempUri = getUriByTableName(tableName);
                mNotifyChangeUri.add(tempUri);
                mDb.execSQL(selection, selectionArgs);
                notifyChange();
                break;
            case QUERY:
                cursor = mDb.rawQuery(selection, selectionArgs);
                break;
            default:
                SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                qb.setTables(tableName);
                cursor = qb.query(mDb,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        null);
                break;
            
        }
        if (cursor != null)
        {
            // 监测数据变更
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }
    
    /**
     * 根据Uri获取类型
     * @param uri
     *     数据库表的Uri
     * @return
     *      类型字符串
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * 通知数据表发生变化
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider#notifyChange()
     */
    
    @Override
    protected void notifyChange()
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        for (Uri uri : mNotifyChangeUri)
        {
            contentResolver.notifyChange(uri, null);
        }
        mNotifyChangeUri.clear();
    }
    
}
