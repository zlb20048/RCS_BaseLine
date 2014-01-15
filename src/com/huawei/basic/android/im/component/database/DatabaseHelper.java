/*
 * 文件名: DatabaseHelper.java
 * 版    权:  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 对每个数据库表定义了对应的数据表字段接口,获得数据库帮助累的实例,特殊URI的字符串定义接口
 * 创建人: qinyangwang
 * 创建时间:2012-2-22
 * 
 * 修改人:
 * 修改时间:
 * 修改内容:
 */
package com.huawei.basic.android.im.component.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 
 * 数据库操作帮助类<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    
    /**
     * 数据库操作异常控制开关。开发调试阶段打开该开关,正式上线须关闭。
     */
    public static final boolean IS_PRINT_EXCEPTION = true;
    
    /**
     * 打印log信息时传入的标志
     */
    private static final String TAG = "DatabaseHelper";
    
    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME_STR = "rcsbaseline";
    
    private static final String DATABASE_NAME_SUFFIX = ".db";
    
    /**
     * 数据库的版本号
     */
    private static final int DATABASE_VERSION = 1;
    
    /**
     * 数据库表操作对象
     */
    private static DatabaseHelper sSingleton = null;
    
    /**
     *
     * 构造器创建数据库
     * @param context 上下文
     */
    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME_STR + DATABASE_NAME_SUFFIX, null,
                DATABASE_VERSION);
        Logger.i(TAG, "init DatabaseHelper()");
    }
    
    /**
     * 
     * 带有UserId的DatabaseHelper构造方法
     * @param context Context对象
     * @param userId 用户ID
     */
    private DatabaseHelper(Context context, String userId)
    {
        super(context, DATABASE_NAME_STR + "_" + userId + DATABASE_NAME_SUFFIX,
                null, DATABASE_VERSION);
        Logger.i(TAG, "init DatabaseHelper()  userSysId : " + userId);
    }
    
    /**
     * 获取DatabaseHelper对象
     *
     * @param context 上下文
     * @return DatabaseHelper对象
     */
    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if (sSingleton == null)
        {
            sSingleton = new DatabaseHelper(context);
        }
        return sSingleton;
    }
    
    /**
     * 
     * 带有userId的databaseHelper对象的创建<BR>
     * 分库用到的databaseHelper
     * @param context Context
     * @param userId 用户ID
     * @return DatabaseHelper对象
     */
    public static DatabaseHelper getInstance(Context context, String userId)
    {
        Logger.d(TAG, "new DatabaseHelper");
        sSingleton = new DatabaseHelper(context, userId);
        return sSingleton;
    }
    
    /**
     *
     * 创建数据库
     *
     * @param db SQLiteDatabase对象
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    public void onCreate(SQLiteDatabase db)
    {
        Logger.d(TAG, "DatabaseHelper on Create()");
        createTable(db);
    }
    
    /**
     * 版本更新
     *
     * @param db SQLiteDatabase对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
     *      int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        
    }
    
    /**
     * 创建数据库中表格
     *
     * @param db SQLiteDatabase对象
     */
    private void createTable(SQLiteDatabase db)
    {
        try
        {
            // 1 创建"好友分组"数据表
            createTableContactSection(db);
            // 2 创建"帐号信息"数据表
            createTableAccount(db);
            // 3 创建"用户头像"数据表
            createTableFaceThumbnail(db);
            // 4 创建"联系人"数据表
            createTablePhoneContactIndex(db);
            // 5 创建"个人/好友"数据表
            createTableContactInfo(db);
            // 6 创建"全局配置项"数据表
            createTableUserConfig(db);
            // 7 系统应用表
            createTableSysAppInfo(db);
            // 8 找朋友小助手
            createTableFriendManager(db);
            // 9 创建"临时群/群组"信息
            createTableGroupInfo(db);
            // 10 创建"临时群/群成员"表
            createTableGroupMember(db);
            // 11 创建"消息会话"数据表
            createTableConversation(db);
            // 12 创建信息表
            createTableMessage(db);
            // 13 创建"多媒体文件索引"表
            createTableMediaIndex(db);
            // 14 创建"临时群/群消息"表
            createTableGroupMessage(db);
            
            //15 创建消息表触发器
            createMessageTrigger(db);
            //16 创建群组消息表触发器
            createGroupMessageTrigger(db);
            
            //17 创建我的应用表
            createTableMyApp(db);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "createTable() exception:", e);
        }
    }
    
    /**
     * 创建"好友分组"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableContactSection(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.CONTACTSECTION);
        sql.append(" ( ");
        sql.append(ContactSectionColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(ContactSectionColumns.CONTACTSECTION_ID).append(" TEXT, ");
        sql.append(ContactSectionColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(ContactSectionColumns.NAME).append(" TEXT NOT NULL, ");
        sql.append(ContactSectionColumns.NOTES).append(" TEXT ");
        sql.append(" );");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.CONTACTSECTION + " success!");
    }
    
    /**
     * 创建"账号"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableAccount(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.ACCOUNT);
        sql.append(" ( ");
        sql.append(AccountColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(AccountColumns.LOGIN_ACCOUNT).append(" TEXT NOT NULL, ");
        sql.append(AccountColumns.TIMESTAMP).append(" TEXT, ");
        sql.append(AccountColumns.PASSWORD).append(" TEXT, ");
        sql.append(AccountColumns.AUTOLOGIN)
                .append(" INTEGER NOT NULL DEFAULT 0, ");
        sql.append(AccountColumns.LAST_STATUS).append(" INTEGER, ");
        sql.append(AccountColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(AccountColumns.USER_ID).append(" TEXT, ");
        sql.append(AccountColumns.BIND_MOBILE).append(" TEXT, ");
        sql.append(AccountColumns.BIND_EMAIL).append(" TEXT, ");
        sql.append(AccountColumns.USER_STATUS).append(" TEXT ");
        sql.append(" );");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.ACCOUNT + " success!");
    }
    
    /**
     * 创建"用户头像"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableFaceThumbnail(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.FACETHUMBNAIL);
        sql.append(" ( ");
        sql.append(FaceThumbnailColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(FaceThumbnailColumns.FACE_ID)
                .append(" TEXT UNIQUE NOT NULL, ");
        sql.append(FaceThumbnailColumns.FACE_BYTES).append(" BLOB, ");
        sql.append(FaceThumbnailColumns.FACE_URL).append(" TEXT, ");
        sql.append(FaceThumbnailColumns.FACE_FILE_PATH).append(" TEXT ");
        sql.append(" );");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.FACETHUMBNAIL + " success!");
    }
    
    /**
     * 创建"手机联系人"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTablePhoneContactIndex(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.PHONECONTACTINDEX);
        sql.append(" ( ");
        sql.append(PhoneContactIndexColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(PhoneContactIndexColumns.USER_SYSID)
                .append(" TEXT NOT NULL, ");
        sql.append(PhoneContactIndexColumns.CONTACT_TYPE)
                .append(" INTEGER NOT NULL DEFAULT 0, ");
        sql.append(PhoneContactIndexColumns.CONTACT_LUID)
                .append(" TEXT NOT NULL, ");
        sql.append(PhoneContactIndexColumns.CONTACT_GUID).append(" TEXT , ");
        sql.append(PhoneContactIndexColumns.CONTACT_CRCVALUE)
                .append(" TEXT NOT NULL, ");
        sql.append(PhoneContactIndexColumns.CONTACT_SYSID).append(" TEXT, ");
        sql.append(PhoneContactIndexColumns.CONTACT_USERID).append(" TEXT, ");
        sql.append(PhoneContactIndexColumns.ADDFRIEND_PRIVACY)
                .append(" INTEGER ");
        sql.append(" );");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.PHONECONTACTINDEX + " success!");
    }
    
    /**
     * 创建"个人/好友信息"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableContactInfo(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.CONTACTINFO);
        sql.append(" (");
        sql.append(ContactInfoColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(ContactInfoColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(ContactInfoColumns.FRIEND_SYSID).append(" TEXT, ");
        sql.append(ContactInfoColumns.FRIEND_USERID).append(" TEXT, ");
        sql.append(ContactInfoColumns.USER_NAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.MIDDLE_NAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.LAST_NAME).append(" TEXT, ");
        //sql.append(ContactInfoColumns.FACE).append(" TEXT, ");
        sql.append(ContactInfoColumns.NICK_NAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.PRIMARY_MOBILE).append(" TEXT, ");
        sql.append(ContactInfoColumns.PRIMARY_EMAIL).append(" TEXT, ");
        sql.append(ContactInfoColumns.SIGNATURE).append(" TEXT, ");
        sql.append(ContactInfoColumns.DESCRIPTION).append(" TEXT, ");
        sql.append(ContactInfoColumns.MEMO_NAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.MEMO_PHONE).append(" TEXT, ");
        sql.append(ContactInfoColumns.MEMO_EMAIL).append(" TEXT, ");
        sql.append(ContactInfoColumns.TOBE_BIND_PRIMARYMOBILE)
                .append(" TEXT, ");
        sql.append(ContactInfoColumns.TOBE_BIND_EMAIL).append(" TEXT, ");
        sql.append(ContactInfoColumns.LEVEL).append(" INTEGER DEFAULT 0, ");
        sql.append(ContactInfoColumns.BIRTHDAY).append(" TEXT, ");
        sql.append(ContactInfoColumns.GENDER).append(" INTEGER DEFAULT 0, ");
        sql.append(ContactInfoColumns.MARRIAGE_STATUS)
                .append(" INTEGER DEFAULT 0, ");
        sql.append(ContactInfoColumns.AGE).append(" INTEGER, ");
        sql.append(ContactInfoColumns.ZODIAC).append(" INTEGER DEFAULT 0, ");
        sql.append(ContactInfoColumns.ASTRO).append(" INTEGER DEFAULT 0, ");
        sql.append(ContactInfoColumns.BLOOD).append(" INTEGER DEFAULT 0, ");
        sql.append(ContactInfoColumns.HOBBY).append(" TEXT, ");
        sql.append(ContactInfoColumns.COMPANY).append(" TEXT, ");
        sql.append(ContactInfoColumns.DEPARTMENT).append(" TEXT, ");
        sql.append(ContactInfoColumns.TITLE).append(" TEXT, ");
        sql.append(ContactInfoColumns.SCHOOL).append(" TEXT, ");
        sql.append(ContactInfoColumns.COURSE).append(" TEXT, ");
        sql.append(ContactInfoColumns.BATCH).append(" TEXT, ");
        sql.append(ContactInfoColumns.COUNTRY).append(" TEXT , ");
        sql.append(ContactInfoColumns.PROVINCE).append(" TEXT, ");
        sql.append(ContactInfoColumns.CITY).append(" TEXT, ");
        sql.append(ContactInfoColumns.STREET).append(" TEXT, ");
        sql.append(ContactInfoColumns.ADDRESS).append(" TEXT, ");
        sql.append(ContactInfoColumns.POSTALCODE).append(" TEXT, ");
        sql.append(ContactInfoColumns.BUILDING).append(" TEXT, ");
        sql.append(ContactInfoColumns.LAST_UPDATE).append(" TEXT,");
        sql.append(ContactInfoColumns.DISPLAY_NAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.DISPLAY_SPELLNAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.SPELLNAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.INITIALNAME).append(" TEXT, ");
        sql.append(ContactInfoColumns.HOME_LOCATION).append(" INTEGER, ");
        sql.append(ContactInfoColumns.CONTACT_SECTIONID)
                .append(" TEXT DEFAULT '0', ");
        sql.append(" UNIQUE( ");
        sql.append(ContactInfoColumns.USER_SYSID).append(", ");
        sql.append(ContactInfoColumns.FRIEND_SYSID);
        sql.append(" )");
        sql.append(" );");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.CONTACTINFO + " success!");
    }
    
    /**
     * 创建"用户全局配置"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableUserConfig(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.USERCONFIG);
        sql.append(" ( ");
        sql.append(UserConfigColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(UserConfigColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(UserConfigColumns.KEY).append(" TEXT NOT NULL, ");
        sql.append(UserConfigColumns.VALUE).append(" TEXT NOT NULL, ");
        sql.append(" UNIQUE( ");
        sql.append(UserConfigColumns.USER_SYSID).append(", ");
        sql.append(UserConfigColumns.KEY);
        sql.append(" )");
        sql.append(" ); ");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.USERCONFIG + " success!");
    }
    
    /**
     * 创建"系统应用"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableSysAppInfo(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TABLE ").append(Tables.SYSAPPINFO);
        sql.append(" ( ");
        sql.append(SysAppInfoColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(SysAppInfoColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(SysAppInfoColumns.APP_ID).append(" TEXT NOT NULL, ");
        sql.append(SysAppInfoColumns.NAME).append(" TEXT, ");
        sql.append(SysAppInfoColumns.TYPE).append(" INTEGER, ");
        sql.append(SysAppInfoColumns.DESC).append(" TEXT, ");
        sql.append(SysAppInfoColumns.ICON_NAME).append(" TEXT, ");
        sql.append(SysAppInfoColumns.ICON_URL).append(" TEXT, ");
        sql.append(SysAppInfoColumns.APP_URL).append(" TEXT, ");
        sql.append(SysAppInfoColumns.UPDATE_TIME).append(" TEXT, ");
        sql.append(SysAppInfoColumns.SSO).append(" TEXT ");
        sql.append(" ); ");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.SYSAPPINFO + " success!");
    }
    
    /**
     * 创建"找朋友小助手"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableFriendManager(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(Tables.FRIEND_MANAGER).append("(");
        sql.append(FriendManagerColumns.ID);
        sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(FriendManagerColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(FriendManagerColumns.SUBSERVICE).append(" TEXT, ");
        sql.append(FriendManagerColumns.FRIEND_SYSID).append(" TEXT, ");
        sql.append(FriendManagerColumns.MSG_ID).append(" TEXT, ");
        sql.append(FriendManagerColumns.FRIEND_USERID).append(" TEXT, ");
        sql.append(FriendManagerColumns.FIRSTNAME).append(" TEXT, ");
        sql.append(FriendManagerColumns.MIDDLENAME).append(" TEXT, ");
        sql.append(FriendManagerColumns.LASTNAME).append(" TEXT, ");
        sql.append(FriendManagerColumns.SIGNATURE).append(" TEXT, ");
        sql.append(FriendManagerColumns.NICKNAME).append(" TEXT, ");
        sql.append(FriendManagerColumns.STATUS).append(" INTEGER, ");
        sql.append(FriendManagerColumns.REASON).append(" TEXT, ");
        sql.append(FriendManagerColumns.OPERATE_TIME).append(" TEXT, ");
        sql.append(FriendManagerColumns.GROUP_ID).append(" TEXT, ");
        sql.append(FriendManagerColumns.GROUP_NAME).append(" TEXT); ");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.FRIEND_MANAGER + " success!");
    }
    
    /**
     * 创建"临时群/群组"信息表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableGroupInfo(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ");
        sql.append(Tables.GROUPINFO);
        sql.append("(");
        sql.append(GroupInfoColumns.ID);
        sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(GroupInfoColumns.USER_SYSID);
        sql.append(" TEXT NOT NULL, ");
        sql.append(GroupInfoColumns.GROUP_ID);
        sql.append(" TEXT, ");
        sql.append(GroupInfoColumns.GROUP_NAME);
        sql.append(" TEXT, ");
        sql.append(GroupInfoColumns.GROUP_DESC);
        sql.append(" TEXT, ");
        sql.append(GroupInfoColumns.GROUP_LABEL).append(" TEXT, ");
        sql.append(GroupInfoColumns.CHATTYPE).append(" TEXT, ");
        sql.append(GroupInfoColumns.GROUP_SORT).append(" INTEGER, ");
        sql.append(GroupInfoColumns.GROUP_TYPE).append(" INTEGER, ");
        sql.append(GroupInfoColumns.GROUP_BULLETIN).append(" TEXT, ");
        sql.append(GroupInfoColumns.PROCEEDING).append(" TEXT, ");
        sql.append(GroupInfoColumns.RECV_POLICY).append(" INTEGER, ");
        sql.append(GroupInfoColumns.MAXMEMBERS).append(" INTEGER, ");
        sql.append(GroupInfoColumns.LASTUPDATE).append(" TEXT, ");
        sql.append(GroupInfoColumns.UNREAD_MSG).append(" INTEGER, ");
        sql.append(GroupInfoColumns.AFFILICATION).append(" TEXT, ");
        sql.append(GroupInfoColumns.GROUP_OWNER_USERID).append(" TEXT, ");
        sql.append(GroupInfoColumns.GROUP_OWNERNICK).append(" TEXT, ");
        sql.append(GroupInfoColumns.GROUP_OWNERFACE).append(" TEXT, ");
        sql.append(GroupInfoColumns.DELFLAG).append(" INTEGER);");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.GROUPINFO + " success!");
    }
    
    /**
     * 创建"临时群/群成员"信息表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableGroupMember(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(Tables.GROUPMEMBER).append("(");
        sql.append(GroupMemberColumns.ID);
        sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(GroupMemberColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(GroupMemberColumns.GROUP_ID).append(" TEXT, ");
        sql.append(GroupMemberColumns.MEMBER_USERID).append(" TEXT, ");
        sql.append(GroupMemberColumns.MEMBER_ID).append(" TEXT, ");
        sql.append(GroupMemberColumns.AFFILIATION).append(" TEXT, ");
        sql.append(GroupMemberColumns.MEMBER_NICK).append(" TEXT, ");
        sql.append(GroupMemberColumns.MEMBER_DESC).append(" TEXT, ");
        sql.append(GroupMemberColumns.STATUS).append(" TEXT);");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.GROUPMEMBER + " success!");
    }
    
    /**
     * 创建"消息会话"数据表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableConversation(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(Tables.CONVERSATION).append("(");
        sql.append(ConversationColumns.ID);
        sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(ConversationColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(ConversationColumns.CONVERSATIONID)
                .append(" TEXT NOT NULL, ");
        sql.append(ConversationColumns.CONVERSATIONTYPE)
                .append(" INTEGER NOT NULL, ");
        sql.append(ConversationColumns.GROUPID).append(" TEXT, ");
        sql.append(ConversationColumns.LASTTIME).append(" TEXT, ");
        sql.append(ConversationColumns.LAST_MSG_ID).append(" TEXT, ");
        sql.append(ConversationColumns.LAST_MSG_SEQUENCE).append(" TEXT, ");
        sql.append(ConversationColumns.LAST_MSG_TYPE).append(" INTEGER, ");
        sql.append(ConversationColumns.LAST_MSG_CONTENT).append(" TEXT, ");
        sql.append(ConversationColumns.LAST_MSG_STATUS).append(" INTEGER, ");
        sql.append(ConversationColumns.UNREAD_MSG).append(" INTEGER, ");
        sql.append(ConversationColumns.RECEIVERS_NAME).append(" TEXT, ");
        sql.append(" UNIQUE( ");
        sql.append(ConversationColumns.USER_SYSID).append(", ");
        sql.append(ConversationColumns.CONVERSATIONID);
        sql.append(" )");
        sql.append(" ); ");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.CONVERSATION + " success!");
    }
    
    /**
     * 
     * 创建多媒体文件表<BR>
     * @param db SQLiteDatabase数据库
     */
    private void createTableMediaIndex(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(Tables.MEDIAINDEX).append("(");
        sql.append(MediaIndexColumns.ID);
        sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(MediaIndexColumns.MSG_ID).append(" TEXT NOT NULL, ");
        sql.append(MediaIndexColumns.MEDIA_TYPE).append(" INTEGER, ");
        sql.append(MediaIndexColumns.MEDIA_SIZE).append(" TEXT, ");
        sql.append(MediaIndexColumns.MEDIA_PATH).append(" TEXT, ");
        sql.append(MediaIndexColumns.MEDIA_SMALL_PATH).append(" TEXT, ");
        sql.append(MediaIndexColumns.MEDIA_URL).append(" TEXT, ");
        sql.append(MediaIndexColumns.MEDIA_SMALL_URL).append(" TEXT, ");
        sql.append(MediaIndexColumns.MEDIA_ALT).append(" TEXT, ");
        sql.append(MediaIndexColumns.LOCATION_LAT).append(" TEXT, ");
        sql.append(MediaIndexColumns.LOCATION_LON).append(" TEXT, ");
        sql.append(MediaIndexColumns.PLAY_TIME).append(" INTEGER, ");
        sql.append(MediaIndexColumns.DOWNLOAD_TRY_TIMES)
                .append(" INTEGER DEFAULT 0); ");
        db.execSQL(sql.toString());
        Logger.d(TAG, "create " + Tables.MEDIAINDEX + " success!");
    }
    
    /**
     * 创建信息表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableMessage(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(Tables.MESSAGE);
        sql.append("(");
        sql.append(MessageColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sql.append(MessageColumns.USER_SYSID).append(" TEXT NOT NULL,");
        sql.append(MessageColumns.MSG_ID).append(" TEXT NOT NULL,");
        sql.append(MessageColumns.MSG_SEQUENCE).append(" TEXT,");
        sql.append(MessageColumns.FRIEND_USERID).append(" TEXT,");
        sql.append(MessageColumns.MSG_SENDORRECV).append(" INTEGER,");
        sql.append(MessageColumns.MSG_TIME).append(" TEXT,");
        sql.append(MessageColumns.MSG_STATUS).append(" INTEGER,");
        sql.append(MessageColumns.MSG_TYPE).append(" INTEGER, ");
        sql.append(MessageColumns.MSG_CONTENT).append(" TEXT); ");
        db.execSQL(sql.toString());
    }
    
    /**
     * 
     * 创建临时群/群消息表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableGroupMessage(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ").append(Tables.GROUPMESSAGE).append("(");
        sql.append(GroupMessageColumns.ID);
        sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sql.append(GroupMessageColumns.USER_SYSID).append(" TEXT NOT NULL, ");
        sql.append(GroupMessageColumns.GROUP_ID).append(" TEXT, ");
        sql.append(GroupMessageColumns.MSG_ID).append(" TEXT, ");
        sql.append(GroupMessageColumns.MSG_SEQUENCE).append(" TEXT, ");
        sql.append(GroupMessageColumns.MEMBER_USERID).append(" TEXT, ");
        sql.append(GroupMessageColumns.MEMBER_NAME).append(" TEXT, ");
        sql.append(GroupMessageColumns.MSG_TIME).append(" TEXT, ");
        sql.append(GroupMessageColumns.MSG_TYPE).append(" INTEGER, ");
        sql.append(GroupMessageColumns.MSG_CONTENT).append(" TEXT, ");
        sql.append(GroupMessageColumns.MSG_STATUS).append(" INTEGER, ");
        sql.append(GroupMessageColumns.MSG_SENDORRECV).append(" INTEGER);");
        db.execSQL(sql.toString());
    }
    
    /**
     * 
     * 创建消息表的触发器<BR>
     * @param db SQLiteDatabase对象
     */
    private void createMessageTrigger(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TRIGGER ")
                .append(Triggers.MESSAGE_TRIGEER)
                .append(" AFTER INSERT ON ")
                .append(Tables.MESSAGE)
                .append(" FOR EACH ROW ")
                .append(" BEGIN ")
                .append(" DELETE FROM ")
                .append(Tables.MESSAGE)
                .append(" WHERE ")
                .append(MessageColumns.ID)
                .append(" NOT IN ")
                .append(" (SELECT ")
                .append(MessageColumns.ID)
                .append(" FROM ")
                .append(Tables.MESSAGE)
                .append(" ORDER BY ")
                .append(MessageColumns.ID)
                .append(" DESC LIMIT ")
                .append(" ( MIN( ")
                .append(Triggers.MAX_MESSAGE_RECORD_COUNT)
                .append(", (SELECT COUNT(")
                .append(MessageColumns.ID)
                .append(") AS totalCount FROM ")
                .append(Tables.MESSAGE)
                .append("))));")
                .append("END");
        Logger.d(TAG, " 创建单人消息触发器 " + sql.toString());
        db.execSQL(sql.toString());
    }
    
    /**
     * 
     * 创建消息表的触发器<BR>
     * @param db SQLiteDatabase对象
     */
    private void createGroupMessageTrigger(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append(" CREATE TRIGGER ")
                .append(Triggers.GROUP_MESSAGE_TRIGGER)
                .append(" AFTER INSERT ON ")
                .append(Tables.GROUPMESSAGE)
                .append(" FOR EACH ROW ")
                .append(" BEGIN ")
                .append(" DELETE FROM ")
                .append(Tables.GROUPMESSAGE)
                .append(" WHERE ")
                .append(GroupMessageColumns.ID)
                .append(" NOT IN ")
                .append(" (SELECT ")
                .append(GroupMessageColumns.ID)
                .append(" FROM ")
                .append(Tables.GROUPMESSAGE)
                .append(" ORDER BY ")
                .append(GroupMessageColumns.ID)
                .append(" DESC LIMIT ")
                .append(" ( MIN( ")
                .append(Triggers.MAX_MESSAGE_RECORD_COUNT)
                .append(", (SELECT COUNT(")
                .append(GroupMessageColumns.ID)
                .append(") AS totalCount FROM ")
                .append(Tables.GROUPMESSAGE)
                .append("))));")
                .append("END");
        Logger.d(TAG, " 创建多人消息触发器 " + sql.toString());
        db.execSQL(sql.toString());
    }
    
    /**
     * 
     * 创建我的应用表<BR>
     * @param db SQLiteDatabase对象
     */
    private void createTableMyApp(SQLiteDatabase db)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE ")
                .append(Tables.MY_APP)
                .append(" (")
                .append(MyAppColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(MyAppColumns.USER_SYSID)
                .append(" TEXT NOT NULL, ")
                .append(MyAppColumns.APP_ID)
                .append(" TEXT);");
        Logger.d(TAG, "create my_app : " + sql.toString());
        db.execSQL(sql.toString());
    }
    
    /**
     * 
     * 数据库表名定义接口<BR>
     * 定义系统中用到的数据库表名的接口
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface Tables
    {
        /**
         * 数据库表名:  联系人分组表
         */
        public static final String CONTACTSECTION = "ContactSection";
        
        /**
         * 数据库表名:  帐号信息表
         */
        public static final String ACCOUNT = "Accout";
        
        /**
         * 数据库表名:  用户头像表
         */
        public static final String FACETHUMBNAIL = "FaceThumbnail";
        
        /**
         * 数据库表名:  手机联系人信息表
         */
        public static final String PHONECONTACTINDEX = "PhoneContactIndex";
        
        /**
         * 数据库表名:  个人/好友信息表
         */
        public static final String CONTACTINFO = "ContactInfo";
        
        /**
         * 数据库表名:  用户全局配置表
         */
        public static final String USERCONFIG = "UserConfig";
        
        /**
         * 数据库表名:  找朋友小助手信息表
         */
        public static final String FRIEND_MANAGER = "FriendManager";
        
        /**
         * 数据库表名:  临时群/群组信息表
         */
        public static final String GROUPINFO = "GroupInfo";
        
        /**
         * 数据库表名:  临时群/群成员表
         */
        public static final String GROUPMEMBER = "GroupMember";
        
        /**
         * 数据库表名:  消息会话表
         */
        public static final String CONVERSATION = "MessageSession";
        
        /**
         * 数据库表名:  聊天信息表
         */
        public static final String MESSAGE = "Message";
        
        /**
         * 数据库表名:  多媒体文件索引表
         */
        public static final String MEDIAINDEX = "MediaIndex";
        
        /**
         * 数据库表名:  临时群/群 消息表
         */
        public static final String GROUPMESSAGE = "GroupMessage";
        
        /**
         * 通话记录表
         */
        public static final String COMMUNICATIONLOG = "communication_log";
        
        /**
         * 数据库表名:  系统应用表
         */
        public static final String SYSAPPINFO = "SysAppInfo";
        
        /**
         * 我的应用表
         */
        public static final String MY_APP = "MyApp";
        
    }
    
    /**
     * 
     * 触发器名称定义接口<BR>
     * 定义系统中用到的触发器
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-29]
     */
    public interface Triggers
    {
        
        /**
         * 数据库中信息表最大记录数为1000
         */
        public static final String MAX_MESSAGE_RECORD_COUNT = "1000";
        
        /**
         * 触发器: Message触发器   控制message表最大数目为1000行
         */
        public static final String MESSAGE_TRIGEER = "message_trigger";
        
        /**
         * 触发器: GroupMessage触发器   控制groupMessage表最大数目为1000行
         */
        public static final String GROUP_MESSAGE_TRIGGER = "group_message_trigger";
    }
    
    /**
     * 
     * 数据特殊操作条件定义接口<BR>
     * 定义特殊操作URI中用到的字符串标志
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface QueryCondition
    {
        /**
         * 自定义联系人删除操作的URI:<BR>
         */
        public static final String CONTATCINFO_DELETE = "contatcinfo_delete";
        
        /**
         * 自定义的插入联系人的uri
         */
        public static final String CONTACTINFO_INSERT = "contactinfo_insert";
        
        /**
         * 自定义联系人信息数据操作URI:<BR>
         * 带有friendUserId参数的URI
         */
        public static final String CONTACTINFO_WITH_FRIEND_USER_ID = "Contactinfo_With_Friend_User_Id/";
        
        /**
         * 自定义联系人信息数据操作URI:<BR>
         * 根据friendSysId 关联查询好友详细信息(联合查询),包括头像和分组信息的URI。
         */
        public static final String CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION = "ContactInfo_With_FaceThumbnail_And_ContactSection";
        
        /**
         * 自定义联系人信息数据操作URI:<BR>
         * 根据friendSysId 关联查询好友详细信息(联合查询),包括头像的URI
         */
        public static final String CONTACTINFO_QUERY_WITH_FACETHUMBNAIL = "ContactInfo_With_FaceThumbnail";
        
        /**
         * 自定义小助手URI:<BR>
         * 关联头像表查询好友助手的URI
         */
        public static final String FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL = "FriendManager_With_FaceThumbnail";
        
        /**
         * 自定义小助手URI:<BR>
         * 包含好友friendUserId的URI
         */
        public static final String FRIENDMANAGER_WITH_FRIEND_USER_ID = "Friendmanager_With_Friend_User_Id/";
        
        /**
         * 自定义小助手URI:<BR>
         * 分页查询小助手数据的URI
         */
        public static final String FRIENDMANAGER_QUERY_WITH_PAGE = "friendmanager_query_with_page";
        
        /**
         * 自定义群组信息URI:<BR>
         * 关联头像表和成员数查询群组信息URI
         */
        public static final String GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT = "GroupInfo_With_FaceThumbnail_And_Member_Count/";
        
        /**
         * 自定义群组信息URI:<BR>
         * 包含groupId(群组ID)的群组信息表操作URI
         */
        public static final String GROUPINFO_WITH_GROUP_ID = "groupinfo_with_group_id/";
        
        /**
         * 自定义群组成员URI:<BR>
         * 关联头像表查询组员信息的URI
         */
        public static final String GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL = "GroupMember_With_FaceThumbnail";
        
        /**
         * 自定义群组成员URI:<BR>
         * 包含groupId的群组成员操作URI
         */
        public static final String GROUPMEMBER_WITH_GROUP_ID = "GroupMember_With_Group_Id/";
        
        /**
         * 自定义群组成员URI:<BR>
         * 包含groupId(群组ID)和memberId(成员ID)的群组成员操作URI
         */
        public static final String GROUPMEMBER_WITH_GROUPID_AND_MEMBERID = "groupmember_with_groupid_and_memberid/";
        
        /**
         * 自定义多媒体信息URI:<BR>
         * 查询多媒体类型为auto(音频类型)的URI
         */
        public static final String MEDIAINDEX_AUTO = "mediaIndex_auto";
        
        /**
         * 自定义多媒体信息URI:<BR>
         * 带有conversationId/friendUserId(会话ID)的URI
         */
        public static final String MEDIAINDEX_CONVERSATIONID = "mediaIndex/";
        
        /**
         * 自定义群聊天消息URI:<BR>
         * 新建群组消息URI
         */
        public static final String GROUP_MESSAGE_NEW_CREATE = "groupMessage_new_create/";
        
        /**
         * 自定义群聊天消息URI:<BR>
         * 根据msgId(消息ID)进行指定消息查询的URI
         */
        public static final String GROUP_MESSAGE_MSGID = "groupMessage_msgId/";
        
        /**
         * 自定义群聊天消息URI:<BR>
         * 查询群组最后一条消息的操作URI
         */
        public static final String GROUP_MESSAGE_LAST = "groupMessage_last/";
        
        /**
         * 自定义群聊天消息URI:<BR>
         * 查询群组消息中所有的多媒体的操作URI
         */
        public static final String GROUPMESSAGE_BY_QUERY_MEDIAINDEX = "groupMessage_by_query_media/";
        
        /**
         * 自定义群聊天消息URI:<BR>
         * 删除群组消息中所有的多媒体U操作RI
         */
        public static final String GROUPMESSAGE_BY_DELETE_MEDIAINDEX = "groupMessage_by_delete_media/";
        
        /**
         * 自定义群聊天消息URI:<BR>
         * 分页查询群消息操作URI
         */
        public static final String GROUP_MESSAGE_PAGE = "groupMessage_page/";
        
        /**
         * 自定义群聊天消息URI:<BR>
         * 包含groupId(群ID)的群聊天消息操作URI
         */
        public static final String GROUP_MESSAGE_WITH_GROUPID = "group_message_with_groupid/";
        
        /**
         * 自定义单人聊天消息URI:<BR>
         * 根据msgId查询指定消息的URI
         */
        public static final String MESSAGE_WITH_FRIENDID = "message_with_friendid/";
        
        /**
         * 自定义单人聊天消息URI:<BR>
         * 查询消息记录相关的多媒体信息的URI
         */
        public static final String MESSAGE_BY_QUERY_MEDIAINDEX = "message_by_query_media/";
        
        /**
         * 自定义单人聊天消息URI:<BR>
         * 分页查询单人聊天消息操作URI
         */
        public static final String MESSAGE_PAGE = "message_page/";
        
        /**
         * 自定义单人聊天消息URI:<BR>
         * 查询最后一条消息URI
         */
        public static final String MESSAGE_LAST = "message_last/";
        
        /**
         * 自定义单人聊天信息URI:<BR>
         * 更新消息状态URI
         */
        public static final String MESSAGE_UPDATE_STATE = "message_update_state/";
        
        /**
         * 根据appId查询系统应用
         */
        public static final String SYSAPPINFO_APPID = "sysApp_by_appId/";
        
        /**
         * 查询所有系统应用
         */
        public static final String SYSAPPINFO_ALL = "sysApp_all/";
        
        /**
         * 查询所有"我的应用"
         */
        public static final String MY_APP_ALL = "myApp_all/";
        
        /**
         * 单人聊天通知栏URI
         */
        public static final String NOTIFICATION_IM_SINGLE = "notification_im_single/";
        
        /**
         * 会话表分页查询URI
         */
        public static final String CONVERSATION_FOR_PAGE = "converstation_for_page/";
        
        /**
         *  查询指定类型的多媒体未读的消息URI
         */
        public static final String MESSGAE_UNREAD_SPECIAL_MEDIA = "messgae_unread_special_media";
        
        /**
         *  查询指定类型的多媒体未读的群组消息URI
         */
        public static final String GROUPMESSGAE_UNREAD_SPECIAL_MEDIA = "groupmessgae_unread_special_media";
        
        /**
         * 找朋友小助手在会话表中操作的URI
         */
        public static final String CONVERSATION_FRIEND = "conversation_friend";
    }
    
    /**
     * 
     * 联系人分组数据库字段<BR>
     * 定义联系人分组数据库中的各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface ContactSectionColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 分组所属用户ID
         */
        public static final String USER_SYSID = "userSysId";
        
        /**
         * 表字段: 分组ID
         */
        public static final String CONTACTSECTION_ID = "contactSectionId";
        
        /**
         * 表字段: 分组名称
         */
        public static final String NAME = "name";
        
        /**
         * 表字段: 分组说明
         */
        public static final String NOTES = "notes";
    }
    
    /**
     * 
     * 帐号信息数据库字段<BR>
     * 用户账号有三种格式:业务ID(对应表中的USER_ID字段)、
     * 手机号(对应表中的BIND_MOBILEZ字段)、邮箱(对应表中的BIND_EMAIL字段)
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface AccountColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 登录帐号<BR>
         * 对应userAccount(可以是即时通信业务ID,邮箱或者手机号码)
         */
        public static final String LOGIN_ACCOUNT = "loginAccount";
        
        /**
         * 表字段: 上次登录的时间戳<BR>
         * 格式:YYYYMMDDHHMMSS
         */
        public static final String TIMESTAMP = "timestamp";
        
        /**
         * 表字段: 登录密码 <BR>
         * 使用SHA256算法加密的串:SHA256(user+密码)<BR>
         * SHA256要求最后转成16进制大写字符串
         */
        public static final String PASSWORD = "password";
        
        /**
         * 表字段: 自动登录 0: 不自动登录 1:
         */
        public static final String AUTOLOGIN = "autoLogin";
        
        /**
         * 表字段: 上一次登录的状态:<BR>
         *  0: 登录成功 <BR>
         *  1: 登录失败,用户名/密码鉴权不通过<BR>
         *  2: 登录失败,服务端响应超时<BR>
         *  注: 状态可根据实际需求,进行扩展<BR>
         */
        public static final String LAST_STATUS = "lastStatus";
        
        /**
         * 表字段: 用户的唯一标识,服务器返回
         */
        public static final String USER_SYSID = "userSysId";
        
        /**
         * 表字段: 注册时服务器分配的用户ID,即业务ID<BR>
         * USER_ID也是用户账号(userAccount)的一种格式
         */
        public static final String USER_ID = "userId";
        
        /**
         * 表字段: 用户登录后呈现的状态:<BR>
         * online:   在线 <BR>
         * invisible:隐身 <BR>
         * dnd:      免打扰<BR>
         * offline:  离线<BR>
         * Xa:       离开 忙碌
         */
        public static final String USER_STATUS = "userStatus";
        
        /**
         * 表字段: 用户绑定的手机号
         */
        public static final String BIND_MOBILE = "bindMobile";
        
        /**
         * 表字段: 用户绑定的Email地址
         */
        public static final String BIND_EMAIL = "bindEmail";
    }
    
    /**
     * 
     * 用户头像表字段<BR>
     * 定义用户头像表中的各字段名称
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface FaceThumbnailColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 头像ID<BR>
         * 可以是JID和群组ID
         */
        public static final String FACE_ID = "faceId";
        
        /**
         * 表字段: 头像缩略图数据
         */
        public static final String FACE_BYTES = "faceBytes";
        
        /**
         * 表字段:  用户头像URL地址<BR>
         * 注: 服务器更新后,需要及时更新到本地
         */
        public static final String FACE_URL = "faceUrl";
        
        /**
         * 表字段：头像路径<BR>
         */
        public static final String FACE_FILE_PATH = "face_file_path";
    }
    
    /**
     * 
     * 手机联系人信息表数据库字段<BR>
     * 定义手机联系人信息表各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface PhoneContactIndexColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 当前登录用户的唯一标识
         */
        public static final String USER_SYSID = "userSysId";
        
        /**
         * 表字段: 联系人类型:<BR> 
         * 0: 手机上的联系人 <BR>
         * 1: SIM卡1上的联系人<BR>
         * 2: SIM卡2上的联系人
         */
        public static final String CONTACT_TYPE = "contactType";
        
        /**
         * 表字段: 联系人的本地通讯录ID
         */
        public static final String CONTACT_LUID = "contactLUID";
        
        /**
         * 表字段: 联系人上的唯一标识
         */
        public static final String CONTACT_SYSID = "contactSysId";
        
        /**
         * 表字段: 联系人在服务器上的ID
         */
        public static final String CONTACT_GUID = "contactGUID";
        
        /**
         * 表字段: 比对是否变动字段
         */
        public static final String CONTACT_CRCVALUE = "contactCrcValue";
        
        /**
         * 表字段: 联系人ID
         */
        public static final String CONTACT_USERID = "contactUserId";
        
        /**
         * 表字段: 用户被加好友的验证方式说明:<br>
         * 1: 允许任何人<br>
         * 2: 需要验证信息<br>
         * 3: 允许通讯录的用户(客户端无特殊操作,同赋值1) <br>
         * 4: 不允许任何人<br>
         * 5: 允许绑定了手机号码的用户经我确认后加我为好友(暂未使用)
         */
        public static final String ADDFRIEND_PRIVACY = "addFriendPrivacy";
    }
    
    /**
     * 
     * 个人/好友信息表数据库字段<BR>
     * 定义个人/好友信息表各字段
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface ContactInfoColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户的唯一标识
         */
        public static final String USER_SYSID = "userSysId";
        
        /**
         *表字段: 好友的唯一标识
         */
        public static final String FRIEND_SYSID = "friendSysId";
        
        /**
         * 表字段: 好友的IM通信JID
         */
        public static final String FRIEND_USERID = "friendUserId";
        
        /**
         * 表字段: 好友的名字
         */
        public static final String USER_NAME = "userName";
        
        /**
         * 表字段: 好友的中间名<BR>
         * 暂不使用
         */
        public static final String MIDDLE_NAME = "middleName";
        
        /**
         * 表字段: 好友的姓<BR>
         * 暂不使用
         */
        public static final String LAST_NAME = "lastName";
        
        /**
         * 表字段: 昵称
         */
        public static final String NICK_NAME = "nickName";
        
        /**
         * 表字段: 好友绑定的手机号
         */
        public static final String PRIMARY_MOBILE = "bindedMobile";
        
        /**
         * 表字段: 好友绑定的邮箱
         */
        public static final String PRIMARY_EMAIL = "bindedEmail";
        
        /**
         * 表字段: 好友的签名
         */
        public static final String SIGNATURE = "signature";
        
        /**
         * 表字段: 好友的自我描述<BR>
         * 注: 服务器还没此字段名,后续可进行修正
         */
        public static final String DESCRIPTION = "description";
        
        /**
         * 表字段: 用户设置的好友昵称
         * 用户设置的,比好友自己设置的昵称优先展现
         */
        public static final String MEMO_NAME = "memoName";
        
        /**
         * 表字段: 用户设置的多个好友手机
         * 注: 用户设置的,优先展现,格式:Phone1|phone2|phone3
         */
        public static final String MEMO_PHONE = "memoPhone";
        
        /**
         * 表字段: 用户设置的多个好友邮箱
         * 注: 用户设置的,优先展现,格式:email1|email2|email3
         */
        public static final String MEMO_EMAIL = "memoEmail";
        
        /**
         * 表字段: 要绑定,但是还未验证的手机号码
         */
        public static final String TOBE_BIND_PRIMARYMOBILE = "toBeBindPrimaryMobile";
        
        /**
         * 表字段: 要绑定,但是还未验证的eMail
         */
        public static final String TOBE_BIND_EMAIL = "toBeBindEmail";
        
        /**
         * 表字段: 用户等级
         * 由系统根据用户在线时长、
         * 好友个数等相关数据自动计算得出
         * ,默认为0级。
         */
        public static final String LEVEL = "level";
        
        /**
         * 表字段: 生日<BR>
         * 格式为YYYYMMDD。例如19800127。
         */
        public static final String BIRTHDAY = "birthday";
        
        /**
         * 表字段: 性别 <BR>
         * 0:未设置 
         * 1:女
         *  2:男
         */
        public static final String GENDER = "gender";
        
        /**
         * 表字段: 婚姻状况<BR>
         * 0:未知<BR>
         * 1:单身<BR>
         * 2:恋爱中<BR>
         * 3:订婚<BR>
         * 4:已婚<BR>
         */
        public static final String MARRIAGE_STATUS = "marriageStatus";
        
        /**
         * 表字段: 好友年龄<BR>
         * 注:若没有缺省值
         * ,则根据生日计算出该值
         * ,存放。
         * 用户可以自主修改为其他值
         */
        public static final String AGE = "age";
        
        /**
         * 表字段:  好友生肖<BR> 
         * 0:没有设置<BR>
         * 1:鼠  2:牛 3:虎 4:兔 5:龙 6:蛇 <BR>
         * 7:马 8:羊 9:猴 10:鸡 11:狗 12:猪
         */
        public static final String ZODIAC = "zodiac";
        
        /**
         * 表字段: 星座 <BR>
         * 0:没有设置<BR>
         * 1:白羊座 2:金牛座3:双子座 <BR>
         * 4:巨蟹座 5:狮子座 6:处女座 <BR>
         * 7:天秤座 8:天蝎座 9:射手座<BR>
         * 10:摩羯座 11:水瓶座 12:双鱼座
         */
        public static final String ASTRO = "astro";
        
        /**
         * 表字段: 血型<BR> 
         * 0:没有设置 <BR>
         * 1:A型血 2:B型血 3:AB型血<BR> 
         * 4:O型血 5:Rh型血 6:MN型血 <BR>
         * 7:HLA型血
         */
        public static final String BLOOD = "blood";
        
        /**
         * 表字段: 爱好
         */
        public static final String HOBBY = "hobby";
        
        /**
         * 表字段: 公司
         */
        public static final String COMPANY = "company";
        
        /**
         * 表字段: 部门
         */
        public static final String DEPARTMENT = "department";
        
        /**
         * 表字段: 职位
         */
        public static final String TITLE = "title";
        
        /**
         * 表字段: 学校
         */
        public static final String SCHOOL = "school";
        
        /**
         * 表字段: 课程
         */
        public static final String COURSE = "course";
        
        /**
         * 表字段: 入学时间、级、届
         */
        public static final String BATCH = "batch";
        
        /**
         * 表字段: 国家
         */
        public static final String COUNTRY = "country";
        
        /**
         * 表字段: 省份
         */
        public static final String PROVINCE = "province";
        
        /**
         * 表字段: 城市
         */
        public static final String CITY = "city";
        
        /**
         * 表字段: 街道
         */
        public static final String STREET = "street";
        
        /**
         * 表字段: 详细地址
         */
        public static final String ADDRESS = "address";
        
        /**
         * 表字段: 邮政编码
         */
        public static final String POSTALCODE = "postalCode";
        
        /**
         * 表字段: 邮政信箱
         */
        public static final String BUILDING = "building";
        
        /**
         * 表字段: 最后一次更新的时间戳<BR>
         * ZZZZ表示为时区,格式:YYYYMMDDHHMMSSZZZZ
         */
        public static final String LAST_UPDATE = "lastUpdate";
        
        /**
         * 表字段: 展示名称
         */
        public static final String DISPLAY_NAME = "displayName";
        
        /**
         * 表字段: 显示名称拼音,用来做排序
         */
        public static final String DISPLAY_SPELLNAME = "displaySpellName";
        
        /**
         * 表字段: 好友各种名字的拼音串<BR>
         * 注: 便于本地好友的快速搜索<BR>
         * 例如: xiaoli,lisi,ligege 注:小李,李四,李哥哥
         */
        public static final String SPELLNAME = "spellName";
        
        /**
         * 表字段: 好友的各种名字的拼音首字母串<BR>
         * 注: 便于本地好友的快速搜索<BR>
         * 例如: xl,ls,lgg
         */
        public static final String INITIALNAME = "initialName";
        
        /**
         * 表字段: 分组ID
         */
        public static final String CONTACT_SECTIONID = "contactSectionId";
        
        /**
         * 表字段: 归属地
         */
        public static final String HOME_LOCATION = "homeLocation";
        
        /**
         * 表字段: 查询用分组id
         */
        public static final String QUERY_CONTACT_SECTIONID = "groupId";
        
        /**
         * 表字段: 查询用分组名
         */
        public static final String QUERY_CONTACT_SECTIONNAME = "groupName";
        
        /**
         * 表字段: 查询用分组说明
         */
        public static final String QUERY_CONTACT_SECTIONNOTES = "groupNotes";
    }
    
    /**
     *
     * 用户配置表字段<BR>
     * 定义用户配置表各字段名
     * @author qinyangwang
     * @version [RCS Client V100R001C03, 2012-2-14]
     */
    public interface UserConfigColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户的唯一标识
         */
        public static final String USER_SYSID = "userSysId";
        
        /**
         * 表字段: 配置项的名称<BR>
         * 例如:addrbookver
         */
        public static final String KEY = "key";
        
        /**
         * 表字段: 配置项的值<BR>
         * 例如:V1.2
         */
        public static final String VALUE = "value";
    }
    
    /**
     * 
     * 系统应用信息 表字段<BR>
     * 定义系统应用信息表各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface SysAppInfoColumns
    {
        /**
         * 用户在HiTalk系统的唯一标识
         */
        public static final String USER_SYSID = "userSysID";
        
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 应用ID
         */
        public static final String APP_ID = "appId";
        
        /**
         * 表字段: 应用名称
         */
        public static final String NAME = "name";
        
        /**
         * 表字段: 应用的类型: <br>
         * 1:缺省应用 <br>
         * 2:扩展应用
         */
        public static final String TYPE = "type";
        
        /**
         * 表字段: 应用描述
         */
        public static final String DESC = "desc";
        
        /**
         * 表字段: 应用图标本地存储的名字<BR>
         * 例如: "APPID.扩展名"
         */
        public static final String ICON_NAME = "iconName";
        
        /**
         * 表字段: 应用展示图标的URL
         */
        public static final String ICON_URL = "iconUrl";
        
        /**
         * 表字段: 应用的访问URL
         */
        public static final String APP_URL = "appUrl";
        
        /**
         * 表字段: 更新时间 <br>
         * 注: 格式yyyyMMddHHmmss
         */
        public static final String UPDATE_TIME = "updateTime";
        
        /**
         * 表字段: 应用支持的单点登录类型 <br>
         * 0: 不支持单点登录 <br>
         * 1: 支持Token方式单点登录
         */
        public static final String SSO = "sso";
    }
    
    /**
     * 
     * 正在加的好友(找朋友小助手)信息 表字段<BR>
     * 定义正在加的好友(找朋友小助手)信息 表各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface FriendManagerColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户在系统的唯一标识
         */
        public static final String USER_SYSID = "userSysID";
        
        /**
         * 表字段: 子业务类型:<br>
         * 1:  系统自动匹配好友(双方号簿均有联系方式) <br>
         * 2:  邀请方注册自动成为好友的 <br>
         * 3:  用户手动加的好友 <br>
         * 4:  用户加我为好友 <br>
         * 10: 群成员申请加入群 <br>
         * 11: 群主受理待加入成员
         *
         */
        public static final String SUBSERVICE = "subService";
        
        /**
         * 表字段: 好友系统标识
         */
        public static final String FRIEND_SYSID = "friendSysId";
        
        /**
         * 表字段: 消息ID<BR>
         * 注: 客户端存储时生成的唯一标识:UUID; 为了找朋友小助手的信息和会话表进行关联
         */
        public static final String MSG_ID = "msgId";
        
        /**
         * 表字段: 好友JID
         */
        public static final String FRIEND_USERID = "friendUserId";
        
        /**
         * 表字段: 好友的真实姓名
         */
        public static final String FIRSTNAME = "firstName";
        
        /**
         * 表字段: 好友的中间名
         */
        public static final String MIDDLENAME = "middleName";
        
        /**
         * 表字段: 好友的姓
         */
        public static final String LASTNAME = "lastName";
        
        /**
         * 表字段: 好友昵称
         */
        public static final String NICKNAME = "nickName";
        
        /**
         * 表字段: 好友签名
         */
        public static final String SIGNATURE = "signature";
        
        /**
         * 表字段: 业务状态<BR>
         * 注: 加好友、加群的过程态,状态可迁移<BR>
         * 1: 同意<BR>
         * 2: 拒绝 <BR>
         * 3: 等待对方处理 <BR>
         * 4: 正在发送 <BR>
         * 5: 发送失败
         */
        public static final String STATUS = "status";
        
        /**
         * 表字段: 操作的理由
         */
        public static final String REASON = "reason";
        
        /**
         * 表字段: 操作时间<BR>
         * 注: 毫秒级别UTC时间戳
         */
        public static final String OPERATE_TIME = "operateTime";
        
        /**
         * 表字段: 群组ID
         */
        public static final String GROUP_ID = "groupId";
        
        /**
         * 表字段: 群组名称
         */
        public static final String GROUP_NAME = "groupName";
        
    }
    
    /**
     * 
     * 临时群/群组信息 表字段<BR>
     * 定义临时群/群组信息表各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface GroupInfoColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户在系统的唯一标识
         */
        public static final String USER_SYSID = "userSysID";
        
        /**
         * 表字段: 群组ID
         */
        public static final String GROUP_ID = "groupId";
        
        /**
         * 表字段: 群组名称
         */
        public static final String GROUP_NAME = "groupName";
        
        /**
         * 表字段: 群组描述
         */
        public static final String GROUP_DESC = "groupDesc";
        
        /**
         * 表字段: 群组标签<BR>
         * 注: 内部按逗号分离,长度受限为300字节
         */
        public static final String GROUP_LABEL = "groupLabel";
        
        /**
         * 表字段: 群组成员(非Owner、Admin)的群组内消息的控制开关: <br>
         * 注: 预留字段,暂未使用<BR>
         * none: 不允许任何群组内消息,包括一对一和一对多 <br>
         * chat: 仅允许一对一群组内消息 <br>
         * groupchat: 仅允许一对多群组内消息 <br>
         * both: 同时允许一对一和一对多群组内消息 <br>
         * 对预置群组该配置项的取值根据业务场景的要求进行变化 <br>
         * 对于其他群组的取值固定为both
         */
        public static final String CHATTYPE = "chatType";
        
        /**
         * 表字段: 群组的分类索引<BR>
         * 注: 当前的索引取值范围如下:<BR>
         * 0-20,其中0表示未分类,当用户创建群组不指定分类时取值为0
         */
        public static final String GROUP_SORT = "groupSort";
        
        /**
         * 表字段: 群组类型 
         * 0: 多人会话 <BR>
         * 1: 固定群(受限)<BR>
         * 2: 固定群(开放) <BR>
         * 3: 预置群(成员不能退出)
         */
        public static final String GROUP_TYPE = "groupType";
        
        /**
         * 表字段: 群组公告
         */
        public static final String GROUP_BULLETIN = "groupBulletin";
        
        /**
         * 表字段: 群消息接收策略:<br>
         * 1: 自动弹出消息 <br>
         * 2: 接收并提示消息 <br>
         * 3: 接收不提示消息<br>
         * 4: 不提示消息只显示数目(屏蔽但是显示消息数目) <br>
         * 5: 完全屏蔽群内消息 <br>
         * 6: 屏蔽群内图片;手机简化实现,只支持1 4两个值
         */
        public static final String RECV_POLICY = "recvPolicy";
        
        /**
         * 表字段: 群组最大成员数
         */
        public static final String MAXMEMBERS = "maxMembers";
        
        /**
         * 表字段: 最后一次更新的UTC时间戳
         */
        public static final String LASTUPDATE = "lastUpdate";
        
        /**
         * 表字段: 删除标记 <br>
         * 0: 正常 <br>
         * 1: 服务器侧该群已删除 
         */
        public static final String DELFLAG = "delFlag";
        
        /**
         * 表字段: 当前群组未读消息数
         */
        public static final String UNREAD_MSG = "unreadMsg";
        
        /**
         * 表字段: 自己的岗位
         */
        public static final String AFFILICATION = "affilication";
        
        /**
         * 表字段: 当affiliation取值为none时,该字段指明该Pending状态的成员是处于邀请还是申请的过程中<br>
         * invite: 该成员处于已邀请但未确认过程中<br>
         * apply:  该成员处于已申请但未批准过程中
         */
        public static final String PROCEEDING = "proceeding";
        
        /**
         * 表字段: 群主ID
         */
        public static final String GROUP_OWNER_USERID = "ownerUserId";
        
        /**
         * 表字段: 群主昵称
         */
        public static final String GROUP_OWNERNICK = "ownerNick";
        
        /**
         * 表字段: 群主头像<BR>
         * (暂时没处理)
         */
        public static final String GROUP_OWNERFACE = "ownerFace";
        
        /**
         * 表字段: 最大成员数目
         */
        public static final String QUERY_NUMBER_COUNT = "memberCount";
        
    }
    
    /**
     * 
     * 临时群/群组成员 表字段<BR>
     * 定义临时群/群组成员表各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface GroupMemberColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户在系统的唯一标识
         */
        public static final String USER_SYSID = "userSysID";
        
        /**
         * 表字段: 群组ID
         */
        public static final String GROUP_ID = "groupId";
        
        /**
         * 表字段: 群成员在系统的IM通信ID(即ID)
         */
        public static final String MEMBER_USERID = "memberUserId";
        
        /**
         * 表字段: 群组分配的成员ID
         */
        public static final String MEMBER_ID = "memberId";
        
        /**
         * 表字段: 群组成员岗位 <br>
         * owner:  群组创建者 <br>
         * admin:  群组管理员 <br>
         * member: 群组普通正式成员(Active状态) <br>
         * none:   处于Pending状态的非正式群组成员
         */
        public static final String AFFILIATION = "affiliation";
        
        /**
         * 表字段: 用户在群组中的昵称
         */
        public static final String MEMBER_NICK = "memberNick";
        
        /**
         * 表字段: 用户在群组中的个人描述
         */
        public static final String MEMBER_DESC = "memberDesc";
        
        /**
         * 表字段: 当affiliation取值为none时,该属性指明显该Pending状态的成员是处于邀请还是申请的过程中<BR>
         * invite: 该成员处于已邀请但未确认过程中 <br>
         * apply:  该成员处于已申请但未批准过程中
         */
        public static final String STATUS = "status";
    }
    
    /**
     * 
     * 消息会话表字段<BR>
     * 定义消息会话表各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface ConversationColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户在系统的唯一标识
         */
        public static final String USER_SYSID = "userSysId";
        
        /**
         * 表字段: 会话ID<BR>
         * 可以是如下ID:<BR>
         * 好友/群成员JID<br>
         * 群发消ID(短信和IM都可以,迭代三设计)<br>
         * 临时组JID 群组JID<br>
         * 小秘书JID:(待定,10010,系统公告的id不需要在此表展现)<br>
         * 找朋友小助手JID:(待定)
         */
        public static final String CONVERSATIONID = "sessionId";
        
        /**
         * 表字段: id类型<BR>
         * 1:单人会话(对应好友ID) <br>
         * 2:群组会话(对应群组ID) <br>
         * 3:群发消息(混合消息或短信) <br>
         * 4:群内私聊(对应群成员ID)
         */
        public static final String CONVERSATIONTYPE = "idType";
        
        /**
         * 表字段: 群组ID<BR>
         * 注: 当IDType=4时, 有效
         */
        public static final String GROUPID = "groupId";
        
        /**
         * 表字段: 最近发送/接收消息时间<BR>
         * 注: 毫秒级别UTC时间戳
         */
        public static final String LASTTIME = "msgTime";
        
        /**
         * 表字段: 发送接收消息时<BR>
         * 注: 客户端存储时生成的唯一标识:UUID;用于与媒体资源表的对应
         */
        public static final String LAST_MSG_ID = "msgId";
        
        /**
         * 表字段: 消息序号<BR>
         * 注: 发送消息时由FAST生成, 接收时为发送方生成的
         */
        public static final String LAST_MSG_SEQUENCE = "msgSequence";
        
        /**
         * 表字段: 最后一次,最近发送/接收消息内容类型: <br>
         * 1: 文本(含图片表情符号) <br>
         * 2: 多媒体
         */
        public static final String LAST_MSG_TYPE = "msgType";
        
        /**
         * 表字段: 最后一次文本消息内容<BR>
         * 注: 图文混排的文本也存放在这里
         * 注: 如果是多媒体消息,需要在多媒体消息表内查询详情
         */
        public static final String LAST_MSG_CONTENT = "msgContent";
        
        /**
         * 表字段: 当发送/接收时,发送/接收的消息状态 <br>
         * 发送时发送的消息状态:<BR>
         * 1: 待发送 <br>
         * 2: 已发送 <br>
         * 3: 已送达未读 <br>
         * 4: 已读 <br>
         * 接收时收到的消息状态 <br>
         * 5:   已接收未读 <br>
         * 100: 阻塞状态(多媒体消息正在上传附件,不处理) <br>
         * 101: 发送失败
         */
        public static final String LAST_MSG_STATUS = "lastMsgStatus";
        
        /**
         * 表字段: 当前会话未读消息条数
         */
        public static final String UNREAD_MSG = "unreadMsg";
        
        /**
         * 表字段: 群发短信,接收方所有人的昵称聚合<BR>
         * <BR>例如:小李|王大伟|李刚
         */
        public static final String RECEIVERS_NAME = "receiversName";
    }
    
    /**
     * 
     * 聊天信息表<BR>
     * 定义聊天信息表各字段名
     * @author zhaozeyang
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface MessageColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户在系统的唯一标识
         */
        public static final String USER_SYSID = "userSysId";
        
        /**
         * 表字段: 消息ID<BR>
         * 注: 发送/接收消息时,客户端存储时生成的唯一标识:UUID;用于与媒体资源表的关联
         */
        public static final String MSG_ID = "msgId";
        
        /**
         * 表字段: 消息序号<BR>
         * 注: 发送消息时由FAST生成;接收时为发送方生成的
         */
        public static final String MSG_SEQUENCE = "msgSequence";
        
        /**
         * 表字段: 好友系统ID
         */
        public static final String FRIEND_USERID = "friendUserId";
        
        /**
         * 表字段: 消息方向 <br>
         * 1: 发送出去的消息 <br>
         * 2: 接收到的消息 <br>
         */
        public static final String MSG_SENDORRECV = "msgSendOrRecv";
        
        /**
         * 表字段: 消息发送/接收时间<BR>
         * 注: 毫秒级别UTC时间戳
         */
        public static final String MSG_TIME = "msgTime";
        
        /**
         * 表字段: 发送/接收时,发送/接收的消息状态<br>
         * 发送时, 发送消息的状态值:
         * 1: 待发送 <br>
         * 2: 已发送 <br>
         * 3: 已送达未读 <br>
         * 4: 已读 <br>
         * 接收时,接收的消息状态 <br>
         * 10:  未读,需发送阅读报告<br>
         * 11:  未读,无需发送阅读报告 <br>
         * 12:  已读,需发送阅读报告<br>
         * 13:  已读,无需发送阅读报告<br>
         * 100: 阻塞状态(多媒体消息正在上传附件,不处理) <br>
         * 101: 发送失败
         */
        public static final String MSG_STATUS = "msgStatus";
        
        /**
         * 表字段: 消息内容类型<br>
         * 1: 文本(含图片表情符号) <br>
         * 2: 多媒体 <br>
         */
        public static final String MSG_TYPE = "msgType";
        
        /**
         * 表字段: 文本消息内容<BR>
         * 注: 图文混排的文本也存放在这里
         * 注: 如果是多媒体消息,需要在多媒体消息表内查询详情
         */
        public static final String MSG_CONTENT = "msgContent";
        
    }
    
    /**
     * 多媒体文件索引表字段<BR>
     * 定义多媒体文件索引表各字段名
     * @author 张仙
     * @version [RCS Client V100R001C03, 2012-2-27]
     */
    public interface MediaIndexColumns
    {
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 消息ID<BR>
         * 注: 与聊天纪录表内的msgID一致,通过此ID与消息表建立关联
         */
        public static final String MSG_ID = "msgId";
        
        /**
         * 表字段: 多媒体消息类型<br>
         * 1: 图片 <br>
         * 2: 语音 <br>
         * 3: 视频
         */
        public static final String MEDIA_TYPE = "mediaType";
        
        /**
         * 表字段: 图片/音频/视频大小
         * 注: 单位:KB <br>
         * 注: 图文混排只统计图片大小,文字大小忽略不计,小于1KB,统一显示1KB
         */
        public static final String MEDIA_SIZE = "mediaSize";
        
        /**
         * 表字段: 多媒体文件原始图存放路径
         */
        public static final String MEDIA_PATH = "mediaFilePath";
        
        /**
         * 表字段: 多媒体文件缩放图存放路径
         */
        public static final String MEDIA_SMALL_PATH = "mediaSmallFilePath";
        
        /**
         * 表字段: 多媒体在文件服务器URL
         */
        public static final String MEDIA_URL = "mediaURL";
        
        /**
         * 表字段: 多媒体的缩略图在文件服务器URL
         */
        public static final String MEDIA_SMALL_URL = "mediaSmallURL";
        
        /**
         * 表字段: 音频/视频媒体文件播放时长<BR>
         * 注: 单位:秒, 取整数,四舍五入
         */
        public static final String PLAY_TIME = "playTime";
        
        /**
         * 表字段: 尝试下载次数
         */
        public static final String DOWNLOAD_TRY_TIMES = "downloadTryTimes";
        
        /**
         * 表字段：多媒体文件的描述
         */
        public static final String MEDIA_ALT = "mediaAlt";
        
        /**
         * 表字段：定位 经度
         */
        public static final String LOCATION_LAT = "lat";
        
        /**
         * 表字段：定位 纬度
         */
        public static final String LOCATION_LON = "lon";
        
    }
    
    /**
     * 临时群/群组消息 表字段<BR>
     * 定义 临时群/群组消息各表名
     * @author 张仙
     * @version [RCS Client V100R001C03, 2012-3-2]
     */
    public interface GroupMessageColumns
    {
        
        /**
         * 表字段: 非业务ID,自增长
         */
        public static final String ID = "_id";
        
        /**
         * 表字段: 用户系统ID
         */
        public static final String USER_SYSID = "userSysID";
        
        /**
         * 表字段: 群组ID
         */
        public static final String GROUP_ID = "groupId";
        
        /**
         * 表字段: 发送接收消息时<BR>
         * 注: 客户端存储时生成的唯一标识:UUID;用于与媒体资源表的关联
         */
        public static final String MSG_ID = "msgId";
        
        /**
         * 表字段: 消息序号<BR>
         * 注: 发送消息时由FAST生成,接收时为发送方生成的
         */
        public static final String MSG_SEQUENCE = "msgSequence";
        
        /**
         * 表字段: 系统成员ID,消息发送者的系统ID <br>
         * 0: 表示群内的通知类型消息(加入群,离开群等)
         */
        public static final String MEMBER_USERID = "memberUserId";
        
        /**
         * 表字段: 发送者的名字
         */
        public static final String MEMBER_NAME = "memberName";
        
        /**
         * 表字段: 消息接收/发送UTC时间戳
         */
        public static final String MSG_TIME = "msgTime";
        
        /**
         * 表字段: 消息内容类型<br>
         * 1: 文本(含图片表情符号) <br>
         * 2: 多媒体 <br>
         */
        public static final String MSG_TYPE = "msgType";
        
        /**
         * 表字段: 文本消息内容
         * 注: 图文混排的文本也存放在这里 <br>
         * 注: 多媒体消息,需要在多媒体文件索引表内查询 <br>
         * 注:(临时)群成员加入和退出的文本提示信息(包含用户昵称和具体行为等关键信息) <br>
         */
        public static final String MSG_CONTENT = "msgContent";
        
        /**
         * 表字段: 消息阅读状态<br>
         * 1: 未读 <br>
         * 2: 已读
         */
        public static final String MSG_STATUS = "msgStatus";
        
        /**
         * 表字段: 消息方向<br>
         * 1: 发送出去的消息 <br>
         * 2: 接收到的消息
         */
        public static final String MSG_SENDORRECV = "msgSendOrRecv";
    }
    
    /**
     * 
     * 我的应用表 字段<BR>
     * [功能详细描述]
     * @author raulxiao
     * @version [RCS Client V100R001C03, Apr 5, 2012]
     */
    public interface MyAppColumns
    {
        /**
         * 非业务ID，自增长
         */
        public static final String ID = "_ID";
        
        /**
         * 用户在沃友系统的唯一标识
         */
        public static final String USER_SYSID = "userSysID";
        
        /**
         * 应用ID
         */
        public static final String APP_ID = "appId";
    }
    
    /**
     * 根据异常控制开关打印异常<BR>
     *
     * @param e 异常
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