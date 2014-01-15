/*
 * 文件名: VoipDataBaseHelper.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: VOIP数据库DatabaseHelper
 * 创建人: 刘鲁宁
 * 创建时间:Mar 14, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.database.voip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.log.Logger;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * VOIP数据库DatabaseHelper
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 14, 2012] 
 */
public class VoipDatabaseHelper extends SQLiteOpenHelper
{
    /**
     * 数据库操作异常控制开关。开发调试阶段打开该开关，正式上线须关闭。
     */
    public static final boolean IS_PRINT_EXCEPTION = true;
    
    /**
     * TAG
     */
    private static final String TAG = "VoipDataBaseHelper";
    
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "rcsbaseline_voip.db";
    
    /**
     * 数据库的版本号
     */
    private static final int DATABASE_VERSION = 1;
    
    /**
     * 数据库表操作对象
     */
    private static VoipDatabaseHelper sSingleton = null;
    
    /**
    *
    * 构造器创建数据库
    *
    * @param context context
    */
    private VoipDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Logger.i(TAG, "init VoipDataBaseHelper()");
    }
    
    /**
     * 获取DatabaseHelper操作助手类
     * @param context
     *      Context对象
     * @return DatabaseHelper
     *      数据库操作助手类
     */
    public static synchronized VoipDatabaseHelper getInstance(Context context)
    {
        if (sSingleton == null)
        {
            sSingleton = new VoipDatabaseHelper(context);
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
            createTableVoipAccount(db);
            
            // 1 创建"通话记录"数据表
            createTableCommunicationLog(db);
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
    private void createTableVoipAccount(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.VOIP_ACCOUNT);
        sql.append(" ( ");
        sql.append(VoipAccountColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(VoipAccountColumns.OWNER_USER_ID).append(" TEXT NOT NULL, ");
        sql.append(VoipAccountColumns.ACCOUNT).append(" TEXT NOT NULL, ");
        sql.append(VoipAccountColumns.PASSWORD).append(" TEXT NOT NULL, ");
        sql.append(VoipAccountColumns.CREATED_DATE).append(" DATETIME ");
        sql.append(" );");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.VOIP_ACCOUNT + " success!");
        
    }
    
    /**
     * 创建CommunicationLog表操作
     * @param db
     *      数据库操作对象
     */
    private void createTableCommunicationLog(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.COMMUNICATION_LOG);
        sql.append(" ( ");
        sql.append(CommunicationLogColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(CommunicationLogColumns.OWNER_USER_ID).append(" TEXT NOT NULL,");
        sql.append(CommunicationLogColumns.CALL_ID).append(" INTEGER NOT NULL,");
        sql.append(CommunicationLogColumns.REMOTE_USER_ID).append(" TEXT,");
        sql.append(CommunicationLogColumns.REMOTE_URI).append(" TEXT,");
        sql.append(CommunicationLogColumns.REMOTE_DISPLAY_NAME).append(" TEXT,");
        sql.append(CommunicationLogColumns.REMOTE_PHONE_NUM).append(" TEXT,");
        sql.append(CommunicationLogColumns.TYPE).append(" INTEGER NOT NULL,");
        sql.append(CommunicationLogColumns.SORT).append(" INTEGER NOT NULL,");
        sql.append(CommunicationLogColumns.FACE_URI).append(" TEXT,");
        sql.append(CommunicationLogColumns.FACE_DATA).append(" BOLB,");
        sql.append(CommunicationLogColumns.CALL_DATE).append(" TEXT NOT NULL,");
        sql.append(CommunicationLogColumns.IS_UNREAD).append(" INTEGER NOT NULL,");
        sql.append(CommunicationLogColumns.CALL_TIME).append(" DATETIME NOT NULL,");
        sql.append(CommunicationLogColumns.START_TIME).append(" DATETIME,");
        sql.append(CommunicationLogColumns.END_TIME).append(" DATETIME ");
        sql.append(" );");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.VOIP_ACCOUNT + " success!");
        
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
        String VOIP_ACCOUNT = "voip_account";
        /**
         * 通话记录表
         */
        String COMMUNICATION_LOG = "communication_log";
    }
    
    /**
     * voip账号表字段常量定义
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Mar 16, 2012]
     */
    public interface VoipAccountColumns
    {
        /**
         * 非业务ID,自增长
         */
        public static final String ID = "_ID";
        
        /**
         * 所属用户ID
         */
        public static final String OWNER_USER_ID = "ownerUserId";
        
        /**
         * VOIP账号
         */
        
        public static final String ACCOUNT = "account";
        
        /**
         * 密码
         */
        public static final String PASSWORD = "password";
        
        /**
         * 创建时间
         */
        public static final String CREATED_DATE = "createdDate";
    }
    
    /**
     * 通话记录表字段常量定义
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Mar 16, 2012]
     */
    public interface CommunicationLogColumns
    {

        /**
         * 非业务ID,自增长
         */
        public static final String ID = "_ID";
        
        /**
         *所属用户ID(userSysId) 
         */
        public static final String OWNER_USER_ID = "ownerUserId";
        
        /**
         * 通话id
         */
        public static final String CALL_ID = "callId";
        
        /**
         * 对方用户ID
         */
        public static final String REMOTE_USER_ID = "remoteUserId";
        
        /**
         * 对方用户Uri
         */
        public static final String REMOTE_URI = "remoteUri";
        
        /**
         * 对方用户显示名称
         */
        public static final String REMOTE_DISPLAY_NAME = "remoteDisplayName";
        
        /**
         * 对方用户的电话号码
         */
        public static final String REMOTE_PHONE_NUM = "remotePhoneNum";
        
        /**
         * 类型
         */
        public static final String TYPE = "type";
        
        /**
         * 种类
         */
        public static final String SORT = "sort";
        
        /**
         * 头像的Uri
         */
        public static final String FACE_URI = "faceUri";
        
        /**
         * 头像的数据
         */
        public static final String FACE_DATA = "faceData";
        
        /**
         * 通话日期 yyyyMMdd
         */
        public static final String CALL_DATE = "callDate";
        
        /**
         * 是否未读
         */
        public static final String IS_UNREAD = "isUnread";
        /**
         * 呼叫接通时间
         */
        public static final String CALL_TIME = "callTime";
        
        /**
         * 开始通话时间
         */
        public static final String START_TIME = "startTime";
        
        /**
         * 结束通话时间
         */
        public static final String END_TIME = "endTime";

        /**
         * 未读消息数量
         */
        public static final String UNREAD_AMOUT = "unreadAmout";
        
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
                DatabaseHelper.printException(e1);
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
