/*
 * 文件名: BaseDbAdaper.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 数据库操作基类
 * 创建人: 刘鲁宁
 * 创建时间:Mar 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * 数据库操作基类
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 15, 2012] 
 */
public abstract class TaskDbAdapter
{
    /**
     * dateformat对象
     */
    private static final SimpleDateFormat TIME_FORAMT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    
    /**
     * 数据库操作接收器
     */
    private ContentResolver mContentResolver;
    
    /**
     * 构造方法
     * @param context
     *      Context对象
     */
    protected TaskDbAdapter(Context context)
    {
        mContentResolver = context.getContentResolver();
    }
    
    /**
     * 删除数据方法
     * @param where
     *      条件字段
     * @param selectionArgs
     *      条件参数
     */
    protected final void delete(String where, String[] selectionArgs)
    {
        mContentResolver.delete(getUri(), where, selectionArgs);
    }
    
    /**
     * 插入数据方法
     * @param values
     *      数据封装对象
     */
    protected final void insert(ContentValues values)
    {
        mContentResolver.insert(getUri(), values);
    }
    
    /**
     * 更新数据方法
     * @param values
     *      数据封装对象
     * @param where
     *      更新条件
     * @param selectionArgs
     *      更新参数
     */
    protected final void update(ContentValues values, String where,
            String[] selectionArgs)
    {
        mContentResolver.update(getUri(), values, where, selectionArgs);
    }
    
    /**
     * 查询数据方法
     * @param sql
     *      查询sql
     * @param selectionArgs
     *      条件参数
     * @return
     *      游标对象
     */
    protected final Cursor query(String sql, String[] selectionArgs)
    {
        return mContentResolver.query(TaskURIField.QUERY_SQL_URI,
                null,
                sql,
                selectionArgs,
                null);
    }
    
    /**
     * 查询数据方法
     * @param projection
     *      游标读取的数据库字段
     * @param selection
     *      查询条件
     * @param selectionArgs
     *      查询参数
     * @param sortOrder
     *      排序条件
     * @return
     *      游标对象
     */
    protected final Cursor query(String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        return mContentResolver.query(getUri(),
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }
    
    /**
     * 执行SQL方法
     * @param sql
     *      执行的sql
     * @param selectionArgs
     *      条件字段
     * @param tableName
     *      表名
     */
    protected final void execSQL(String sql, String[] selectionArgs,
            String tableName)
    {
        mContentResolver.query(TaskURIField.EXECUTE_SQL_URI,
                null,
                sql,
                selectionArgs,
                tableName);
    }
    
    /**
     * 获取URI
     * @return
     *      URI
     */
    private final Uri getUri()
    {
        return Uri.parse("content://" + TaskURIField.AUTHORITY + "/"
                + getTableName());
    }
    
    /**
     * 获取表名
     * @return 表名
     */
    protected abstract String getTableName();
    
    /**
     * 读取游标中的日期类型数据
     * @param cursor
     *      游标对象
     * @param cloumnName
     *      列名
     * @return
     *      日期对象
     */
    protected static synchronized Date getDateFormCursor(Cursor cursor,
            String cloumnName)
    {
        try
        {
            String dateString = cursor.getString(cursor.getColumnIndex(cloumnName));
            return dateString == null ? null : TIME_FORAMT.parse(dateString);
        }
        catch (ParseException e)
        {
            return null;
        }
    }
    
    /**
     * 转换日期为字符串
     * @param date
     *      日期对象
     * @return
     *      日期字符串
     */
    protected static synchronized String changeDateToString(Date date)
    {
        return date == null ? null : TIME_FORAMT.format(date);
    }
    
    /**
     * 根据异常控制开关打印异常<BR>
     *
     * @param e 异常
     */
    public static void printException(Exception e)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        
        try
        {
            baos.close();
        }
        catch (IOException e1)
        {
            printException(e1);
        }
    }
    
    /**
     * 关闭游标<BR>
     *
     * @param cursor 要关闭的游标对象
     */
    public static void closeCursor(Cursor cursor)
    {
        if (cursor != null)
        {
            cursor.close();
        }
    }
}
