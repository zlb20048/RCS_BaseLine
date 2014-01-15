/*
 * 文件名: TaskDatabaseHelper.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Apr 26, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 26, 2012] 
 */
public class TaskDatabaseHelper extends SQLiteOpenHelper
{
    /**
     * 数据库操作异常控制开关。开发调试阶段打开该开关，正式上线须关闭。
     */
    public static final boolean IS_PRINT_EXCEPTION = true;
    
    /**
     * TAG
     */
    private static final String TAG = "TaskDataBaseHelper";
    
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "Task_Demo.db";
    
    /**
     * 数据库的版本号
     */
    private static final int DATABASE_VERSION = 1;
    
    /**
     * 数据库表操作对象
     */
    private static TaskDatabaseHelper sSingleton = null;
    
    /**
    *
    * 构造器创建数据库
    *
    * @param context context
    */
    private TaskDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Logger.i(TAG, "init TaskdatabaseHelper()");
    }
    
    /**
     * 获取DatabaseHelper操作助手类
     * @param context
     *      Context对象
     * @return DatabaseHelper
     *      数据库操作助手类
     */
    public static synchronized TaskDatabaseHelper getInstance(Context context)
    {
        if (sSingleton == null)
        {
            sSingleton = new TaskDatabaseHelper(context);
        }
        return sSingleton;
    }
    
    /**
     * 数据库在创建时的操作
     * @param db
     *      数据库操作对象
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Logger.d(TAG, "DatabaseHelper on Create()");
        
        createTable(db);
        
    }
    
    /**
     * 创建表操作
     * @param db
     *      数据库操作对象
     */
    private void createTable(SQLiteDatabase db)
    {
        try
        {
            // 1 创建"Voip账号"数据表
            createTableDownloadTask(db);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "createTable() exception:", e);
        }
    }
    
    /**
     * 创建VoipAccount表操作
     * @param db
     *      数据库操作对象
     */
    private void createTableDownloadTask(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.DOWNLOAD_TASK);
        sql.append(" ( ");
        sql.append(DownloadTaskColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sql.append(DownloadTaskColumns.DOWNLOAD_TASK_ID)
                .append(" NUMERIC NOT NULL,");
        sql.append(DownloadTaskColumns.NAME).append(" TEXT NOT NULL,");
        sql.append(DownloadTaskColumns.DOWNLOAD_URL).append(" TEXT NOT NULL,");
        sql.append(DownloadTaskColumns.STORE_PATH).append(" TEXT NOT NULL,");
        sql.append(DownloadTaskColumns.TOTAL_SIZE).append(" NUMERIC NOT NULL,");
        sql.append(DownloadTaskColumns.STATUS).append(" INTEGER NOT NULL,");
        sql.append(DownloadTaskColumns.IS_BACKGROUD)
                .append(" INTEGER NOT NULL,");
        sql.append(DownloadTaskColumns.IS_DELETE_FILE)
                .append(" INTEGER NOT NULL,");
        sql.append(DownloadTaskColumns.IS_POST).append(" INTEGER NOT NULL,");
        sql.append(DownloadTaskColumns.IS_PROXY).append(" INTEGER NOT NULL,");
        sql.append(DownloadTaskColumns.PROXY_HOST).append(" TEXT,");
        sql.append(DownloadTaskColumns.PROXY_PROT).append(" INTEGER,");
        sql.append(DownloadTaskColumns.CREATED_TIME)
                .append(" DATETIME NOT NULL);");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.DOWNLOAD_TASK + " successful!");
    }
    
    /**
     * 数据库版本升级操作
     * @param db
     *      数据库操作对象
     * @param oldVersion
     *      老版本号
     * @param newVersion
     *      新版本号
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * 表常量定义接口
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Mar 16, 2012]
     */
    public interface Tables
    {
        /**
         * Voip账号表
         */
        String DOWNLOAD_TASK = "download_task";
        
    }
    
    /**
     * voip账号表字段常量定义
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Mar 16, 2012]
     */
    public interface DownloadTaskColumns
    {
        /**
         * 非业务ID,自增长
         */
        public static final String ID = "_ID";
        
        /**
         * 任务下载ID
         */
        public static final String DOWNLOAD_TASK_ID = "downloadTaskId";
        
        /**
         * 任务名称
         */
        
        public static final String NAME = "name";
        
        /**
         * 下载地址
         */
        public static final String DOWNLOAD_URL = "downloadUrl";
        
        /**
         * 保存路径
         */
        public static final String STORE_PATH = "storePath";
        
        /**
         * 总大小
         */
        public static final String TOTAL_SIZE = "totalSize";
        
        /**
         * 状态
         */
        public static final String STATUS = "status";
        
        /**
         * 是否后台下载
         */
        public static final String IS_BACKGROUD = "isBackgroud";
        
        /**
         * 任务完成后删除任务是否删除文件
         */
        public static final String IS_DELETE_FILE = "isDeleteFile";
        
        /**
         * POST类型
         */
        public static final String IS_POST = "isPost";
        
        /**
         * 代理key
         */
        public static final String IS_PROXY = "isProxy";
        
        /**
         * 代理IP
         */
        public static final String PROXY_HOST = "proxyHost";
        
        /**
         * 代理端口
         */
        public static final String PROXY_PROT = "proxyPory";
        
        /**
         * 创建时间
         */
        public static final String CREATED_TIME = "createdTime";
        
    }
    
    /**
     * 根据异常控制开关打印异常<BR>
     * @param e 
     *      异常对象
     */
    public static void printException(Exception e)
    {
        if (IS_PRINT_EXCEPTION)
        {
            throw new RuntimeException(e);
        }
        else
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(ps);
            Logger.e(TAG, new String(baos.toByteArray()));
            
            try
            {
                baos.close();
            }
            catch (IOException e1)
            {
                Log.e(TAG, e1.getMessage(), e1);
            }
        }
    }
    
    /**
     * 关闭游标<BR>
     * @param cursor 游标对象
     */
    public static void closeCursor(Cursor cursor)
    {
        if (cursor != null)
        {
            cursor.close();
        }
    }
}
