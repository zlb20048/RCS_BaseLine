/*
 * 文件名: RcsBaseLineProvider.java
 * 版    权:  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 数据库uri的注册，数据库操作的实现
 * 创建人: qinyangwang
 * 创建时间:2012-2-14
 * 
 * 修改人:
 * 修改时间:
 * 修改内容:
 */
package com.huawei.basic.android.im.component.database;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactSectionColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ConversationColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FaceThumbnailColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FriendManagerColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupMemberColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupMessageColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MessageColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MyAppColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.QueryCondition;
import com.huawei.basic.android.im.component.database.DatabaseHelper.SysAppInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.Tables;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.utils.HanziToPinyin;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * SQLiteContentProvider子类，具体实现查询语句的类。<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public class RcsBaseLineProvider extends SQLiteContentProvider implements
        OnSharedPreferenceChangeListener
{
    /**
     * URI匹配值: 帐号信息表URI
     */
    public static final int ACCOUNT = 0x00000010;
    
    /**
     * URI匹配值: 用户头像表URI
     */
    public static final int FACETHUMBNAIL = 0x00000020;
    
    /**
     * URI匹配值: 手机联系人信息表URI
     */
    public static final int PHONECONTACTINDEX = 0x00000030;
    
    /**
     * URI匹配值: 联系人分组列表URI
     */
    public static final int CONTACTSECTION = 0x00000040;
    
    /**
     * URI匹配值: 个人/好友信息表URI
     */
    public static final int CONTACTINFO = 0x00000050;
    
    /**
     * URI匹配值: 根据friendSysId关联查询好友详细信息(联合查询)，包括头像和分组信息URI。
     */
    public static final int CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION = 0x00000051;
    
    /**
     * URI匹配值: 包含friendUserId联系人信息表URI
     */
    public static final int CONTACTINFO_WITH_FRIEND_USER_ID = 0x00000052;
    
    /**
     * URI匹配值: 关联查询联系人信息头像表URI
     */
    public static final int CONTACTINFO_QUERY_WITH_FACETHUMBNAIL = 0x00000053;
    
    /**
     * URI匹配值: 用户全局配置表URI
     */
    public static final int USERCONFIG = 0x00000060;
    
    /**
     * URI匹配值: 系统应用URI
     */
    public static final int SYSAPPINFO = 0x00000070;
    
    /**
     * URI匹配值: 系统应用查询所有应用URI
     */
    public static final int SYSAPPINOF_ALL = 0x00000071;
    
    /**
     * 系统应用 appId
     */
    public static final int SYSAPPINFO_APPID = 0x00000072;
    
    /**
     * 我的应用
     */
    public static final int MY_APP = 0x00000073;
    
    /**
     * 查询我的所有应用
     */
    public static final int MY_APP_ALL = 0x00000074;
    
    /**
     * 应用图标
     */
    public static final int SYSAPP_INCON = 0x00000075;
    
    /**
     * URI匹配值: 小助手信息表URI
     */
    public static final int FRIENDMANAGER = 0x00000080;
    
    /**
     * URI匹配值: 根据好友JID查询 正在加的好友 信息表URI
     */
    public static final int FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL = 0x00000081;
    
    /**
     * URI匹配值: 包含friendUserId的小助手表URI
     */
    public static final int FRIENDMANAGER_WITH_FRIEND_USER_ID = 0x00000082;
    
    /**
     * URI匹配值: 分页查询找朋友小助手记录URI
     */
    public static final int FRIENDMANAGER_QUERY_WITH_PAGE = 0x00000083;
    
    /**
     * URI匹配值: 临时群/群组 信息表URI
     */
    public static final int GROUPINFO = 0x00000090;
    
    /**
     * URI匹配值: 查询 包括头像和成员数量群/群组操作URI
     */
    public static final int GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT = 0x00000091;
    
    /**
     * URI匹配值: 包含群组ID 的群组操作URI
     */
    public static final int GROUPINFO_WITH_GROUP_ID = 0x00000092;
    
    /**
     * URI匹配值: 临时群/群组 成员表URI
     */
    public static final int GROUPMEMBER = 0x000000a0;
    
    /**
     * URI匹配值: 查询包括头像群/群组成员操作URI
     */
    public static final int GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL = 0x000000a1;
    
    /**
     * URI匹配值: 包含群组ID的群组成员 操作URI
     */
    public static final int GROUPMEMBER_WITH_GROUP_ID = 0x000000a2;
    
    /**
     * URI匹配值: 包含群组的ID和群组成员的ID 群组成员操作URI
     */
    public static final int GROUPMEMBER_WITH_GROUPID_AND_MEMBERID = 0x000000a3;
    
    /**
     * URI匹配值: 消息会话表URI
     */
    public static final int CONVERSATION = 0x000000b0;
    
    /**
     * URI匹配值：消息会话表分页查询
     */
    public static final int CONVERSATION_FOR_PAGE = 0x000000b1;
    
    /**
     * URI匹配值:找朋友小助手回话表操作URI
     */
    public static final int CONVERSATION_FRIEND = 0x000000b2;
    
    /**
     * URI匹配值: 多媒体文件索引表URI
     */
    public static final int MEDIAINDEX = 0x000000c0;
    
    /**
     * URI匹配值: 包含conversationId/friendUserId的多媒体信息表操作URI
     */
    public static final int MEDIAINDEX_CONVERSATION = 0x000000c1;
    
    /**
     * URI匹配值: 类型为音频的多媒体信息表操作URI
     */
    public static final int MEDIAINDEX_AUTO = 0x000000c2;
    
    /**
     * URI匹配值: 群消息表URI
     */
    public static final int GROUPMESSAGE = 0x000000d0;
    
    /**
     * URI匹配值: 查询临时群/群组 聊天纪录(消息)最后一条消息URI
     */
    public static final int GROUPMESSAGE_LAST = 0x000000D2;
    
    /**
     * URI匹配值: 根据msgId查询群组消息 URI
     */
    public static final int GROUPMESSAGE_MSGID = 0x000000d4;
    
    /**
     * URI匹配值: 根据groupId 查询多媒体URI
     */
    public static final int GROUPMESSAGE_BY_QUERY_MEDIAINDEX = 0x000000d6;
    
    /**
     * URI匹配值: 分页查询临时群/群组 聊天纪录(消息)URI
     */
    public static final int GROUPMESSAGE_PAGE = 0x000000D7;
    
    /**
     * URI匹配值: 包含群组ID临时群/群组聊天记录操作URI 
     */
    public static final int GROUP_MESSAGE_WITH_GROUPID = 0x000000D8;
    
    /**
     * URI匹配值: 群组未读音频消息查询的URI 
     */
    public static final int GROUPMESSGAE_UNREAD_SPECIAL_MEDIA = 0x000000D9;
    
    /**
     * URI匹配值: 消息表URI
     */
    public static final int MESSAGE = 0x000000e0;
    
    /**
     * URI匹配值: 分页查询单人聊天纪录(消息) URI
     */
    public static final int MESSAGE_PAGE = 0x000000e1;
    
    /**
     * URI匹配值: 根据msgId查询消息URI
     */
    public static final int MESSAGE_WITH_FRIENDID = 0x000000e2;
    
    /**
     * URI匹配值: 根据conversationId 查询多媒体URI
     */
    public static final int MESSAGE_BY_QUERY_MEDIAINDEX = 0x000000e3;
    
    /**
     * URI匹配值: 查询最后一条单人聊天信息记录URI
     */
    public static final int MESSAGE_LAST = 0x000000e4;
    
    /**
     * URI匹配值: 更新单人消息状态URI
     */
    public static final int MESSAGE_UPDATE_STATE = 0x000000e5;
    
    /**
     * URI匹配值: 未读音频消息查询的URI 
     */
    public static final int MESSGAE_UNREAD_SPECIAL_MEDIA = 0x000000e6;
    
    /**
     * URI匹配值：查询单人会话通知栏信息
     */
    public static final int NOTIFICATION_IM_SINGLE = 0x000000f1;
    
    /**
     * 打印log信息时传入的标志
     */
    private static final String TAG = "RcsBaseLineProvider";
    
    /**
     * URI键值队
     */
    private static final UriMatcher URIMATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);
    
    private String userId;
    
    private SharedPreferences sp;
    
    private Map<String, DatabaseHelper> dbHelperPool = new Hashtable<String, DatabaseHelper>();
    
    static
    {
        // 账号
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.ACCOUNT, ACCOUNT);
        
        // 分组
        URIMATCHER.addURI(URIField.AUTHORITY,
                Tables.CONTACTSECTION,
                CONTACTSECTION);
        
        // 头像
        URIMATCHER.addURI(URIField.AUTHORITY,
                Tables.FACETHUMBNAIL,
                FACETHUMBNAIL);
        
        // 通讯录联系人
        URIMATCHER.addURI(URIField.AUTHORITY,
                Tables.PHONECONTACTINDEX,
                PHONECONTACTINDEX);
        
        // 好友
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.CONTACTINFO, CONTACTINFO);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION,
                CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.CONTACTINFO_WITH_FRIEND_USER_ID,
                CONTACTINFO_WITH_FRIEND_USER_ID);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.CONTACTINFO_QUERY_WITH_FACETHUMBNAIL,
                CONTACTINFO_QUERY_WITH_FACETHUMBNAIL);
        
        // 全局配置
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.USERCONFIG, USERCONFIG);
        
        // 系统应用
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.SYSAPPINFO, SYSAPPINFO);
        URIMATCHER.addURI(URIField.AUTHORITY, QueryCondition.SYSAPPINFO_APPID
                + "*/*/*", SYSAPPINFO_APPID);
        URIMATCHER.addURI(URIField.AUTHORITY, QueryCondition.SYSAPPINFO_ALL
                + "*/*", SYSAPPINOF_ALL);
        
        // 我的应用
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.MY_APP, MY_APP);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MY_APP_ALL + "*",
                MY_APP_ALL);
        
        // 找朋友小助手
        URIMATCHER.addURI(URIField.AUTHORITY,
                Tables.FRIEND_MANAGER,
                FRIENDMANAGER);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL,
                FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.FRIENDMANAGER_WITH_FRIEND_USER_ID + "*",
                FRIENDMANAGER_WITH_FRIEND_USER_ID);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.FRIENDMANAGER_QUERY_WITH_PAGE,
                FRIENDMANAGER_QUERY_WITH_PAGE);
        
        // 群信息
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.GROUPINFO, GROUPINFO);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUPINFO_WITH_GROUP_ID + "*",
                GROUPINFO_WITH_GROUP_ID);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT
                        + "*/*",
                GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT);
        
        // 群组成员
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.GROUPMEMBER, GROUPMEMBER);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL,
                GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUPMEMBER_WITH_GROUP_ID + "*",
                GROUPMEMBER_WITH_GROUP_ID);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUPMEMBER_WITH_GROUPID_AND_MEMBERID + "*/*",
                GROUPMEMBER_WITH_GROUPID_AND_MEMBERID);
        
        // 会话
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.CONVERSATION, CONVERSATION);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.CONVERSATION_FOR_PAGE + "*/*",
                CONVERSATION_FOR_PAGE);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.CONVERSATION_FRIEND,
                CONVERSATION_FRIEND);
        
        //多媒体
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.MEDIAINDEX, MEDIAINDEX);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MEDIAINDEX_CONVERSATIONID + "*",
                MEDIAINDEX_CONVERSATION);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MEDIAINDEX_AUTO,
                MEDIAINDEX_AUTO);
        //群组消息
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.GROUPMESSAGE, GROUPMESSAGE);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUP_MESSAGE_MSGID + "*",
                GROUPMESSAGE_MSGID);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUPMESSAGE_BY_QUERY_MEDIAINDEX + "*/*",
                GROUPMESSAGE_BY_QUERY_MEDIAINDEX);
        URIMATCHER.addURI(URIField.AUTHORITY, QueryCondition.GROUP_MESSAGE_PAGE
                + "*/*/*", GROUPMESSAGE_PAGE);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUP_MESSAGE_WITH_GROUPID + "*",
                GROUP_MESSAGE_WITH_GROUPID);
        URIMATCHER.addURI(URIField.AUTHORITY, QueryCondition.GROUP_MESSAGE_LAST
                + "*", GROUPMESSAGE_LAST);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.GROUPMESSGAE_UNREAD_SPECIAL_MEDIA,
                GROUPMESSGAE_UNREAD_SPECIAL_MEDIA);
        //1v1聊天消息
        URIMATCHER.addURI(URIField.AUTHORITY, Tables.MESSAGE, MESSAGE);
        URIMATCHER.addURI(URIField.AUTHORITY, QueryCondition.MESSAGE_PAGE
                + "*/*/*", MESSAGE_PAGE);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MESSAGE_WITH_FRIENDID + "*",
                MESSAGE_WITH_FRIENDID);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MESSAGE_BY_QUERY_MEDIAINDEX + "*/*",
                MESSAGE_BY_QUERY_MEDIAINDEX);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MESSAGE_LAST + "*",
                MESSAGE_LAST);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MESSAGE_UPDATE_STATE + "*",
                MESSAGE_UPDATE_STATE);
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.MESSGAE_UNREAD_SPECIAL_MEDIA,
                MESSGAE_UNREAD_SPECIAL_MEDIA);
        
        //单人通知栏查询
        URIMATCHER.addURI(URIField.AUTHORITY,
                QueryCondition.NOTIFICATION_IM_SINGLE + "*/*",
                NOTIFICATION_IM_SINGLE);
    }
    
    /**
     * 处理具体操作的集合
     */
    private HashMap<String, TableHandler> mDataRowHandlers;
    
    /**
     * 需要通知的uri集合
     */
    private Vector<Uri> mNotifyChangeUri;
    
    /**
     * 
     * 具体处理数据库操作的类
     * 
     * @author qinyangwang
     * @version [RCS Client V100R001C03, 2012-2-15]
     */
    private class TableHandler
    {
        /**
         * 数据表名称
         */
        protected String mTableName;
        
        /**
         * 
         * 构造方法，传入表名。<BR>
         * @param tableName 表名
         */
        public TableHandler(String tableName)
        {
            mTableName = tableName;
        }
        
        /**
         * 向指定的数据库中插入数据<BR>
         * @param db 数据库
         * @param nullColumnHack 默认空值列
         * @param values 插入的值
         * @return 新插入数据的rowId
         */
        public long insert(SQLiteDatabase db, String nullColumnHack,
                ContentValues values)
        {
            return db.insert(mTableName, nullColumnHack, values);
        }
        
        /**
         * 删除指定数据库中的数据<BR>
         * 
         * @param db 数据库
         * @param whereClause 条件语句
         * @param whereArgs 条件语句的值
         * @return 删除的行数
         */
        public int delete(SQLiteDatabase db, String whereClause,
                String[] whereArgs)
        {
            return db.delete(mTableName, whereClause, whereArgs);
        }
        
        /**
         * 
         * 更新数据库中的数据<BR>
         * @param db 数据库
         * @param values 需要更新的数据
         * @param whereClause 条件语句
         * @param whereArgs 条件
         * @return 更改的行数
         */
        public int update(SQLiteDatabase db, ContentValues values,
                String whereClause, String[] whereArgs)
        {
            return db.update(mTableName, values, whereClause, whereArgs);
        }
        
    }
    
    /**
     * 
     * 好友信息数据表操作的类<BR>
     * 
     * @author qinyangwang
     * @version [RCS Client V100R001C03, 2012-2-15]
     */
    private class ContactInfoTableHandler extends TableHandler
    {
        
        /**
         * 
         * 构造方法，传入表名。<BR>
         * @param tableName 表名
         */
        public ContactInfoTableHandler(String tableName)
        {
            super(tableName);
        }
        
        /**
         * 向指定的数据库中插入数据<BR>
         * @param db 数据库
         * @param nullColumnHack 默认空值列
         * @param values 插入的值
         * @return 新插入数据的rowId
         */
        public long insert(SQLiteDatabase db, String nullColumnHack,
                ContentValues values)
        {
            fixContactInfo(values, null, null);
            long result = db.insert(mTableName, nullColumnHack, values);
            //            if (-1 < result)
            //            {
            //开始事务
            //                newTransaction();
            //TODO 为什么插入联系人数据时，如果联系人没有手机号
            //，删除UserConfig表中对应的短信群发设置能力数据
            //                deleteInTransaction(URIField.USERCONFIG_URI,
            //                        UserConfigColumns.KEY + "=? " + " AND "
            //                                + UserConfigColumns.USER_SYSID + " IN(SELECT "
            //                                + ContactInfoColumns.USER_SYSID + " FROM "
            //                                + mTableName + " WHERE "
            //                                + ContactInfoColumns.USER_SYSID + "="
            //                                + ContactInfoColumns.FRIEND_SYSID + " AND "
            //                                + ContactInfoColumns.PRIMARY_MOBILE
            //                                + " IS NULL)",
            //                        new String[] { UserConfigModel.TOGETHER_SEND_SM_ABILITY });
            //            }
            return result;
        }
        
        /**
         * 
         * 更新数据库中的数据<BR>
         * @param db 数据库
         * @param values 需要更新的数据
         * @param whereClause 条件语句
         * @param whereArgs 条件
         * @return 更改的行数
         */
        public int update(SQLiteDatabase db, ContentValues values,
                String whereClause, String[] whereArgs)
        {
            fixContactInfo(values, whereClause, whereArgs);
            return db.update(mTableName, values, whereClause, whereArgs);
            
            //            int result = updateWithFaceThumbnail(db,
            //                    values,
            //                    whereClause,
            //                    whereArgs);
            //            if (0 < result)
            //            {
            //                newTransaction();
            //                deleteInTransaction(URIField.USERCONFIG_URI,
            //                        UserConfigColumns.KEY + "=? " + " AND "
            //                                + UserConfigColumns.USER_SYSID + " IN(SELECT "
            //                                + ContactInfoColumns.USER_SYSID + " FROM "
            //                                + mTableName + " WHERE "
            //                                + ContactInfoColumns.USER_SYSID + "="
            //                                + ContactInfoColumns.FRIEND_SYSID + " AND "
            //                                + ContactInfoColumns.PRIMARY_MOBILE
            //                                + " IS NULL)",
            //                        new String[] { UserConfigModel.TOGETHER_SEND_SM_ABILITY });
            ////            }
            //            return result;
        }
        
        /**
         * 补全用户信息
         * 在操作数据库前计算用户的显示名和生日
         * @param values 需要更新的数据
         * @param whereClause 条件语句
         * @param whereArgs 条件
         */
        private void fixContactInfo(ContentValues values, String whereClause,
                String[] whereArgs)
        {
            // 显示名可能需要重新计算，先获取更新字段
            String memoName = values.getAsString(ContactInfoColumns.MEMO_NAME);
            String nickName = values.getAsString(ContactInfoColumns.NICK_NAME);
            String friendUserId = values.getAsString(ContactInfoColumns.FRIEND_USERID);
            if (!StringUtil.isNullOrEmpty(memoName)
                    || !StringUtil.isNullOrEmpty(nickName)
                    || !StringUtil.isNullOrEmpty(friendUserId)
                    && null == whereArgs
                    && StringUtil.isNullOrEmpty(whereClause))
            {
                if (null != whereArgs && !StringUtil.isNullOrEmpty(whereClause))
                {
                    // 如果是更新语句，获取以前的字段加入计算
                    Cursor cursor = query(URIField.CONTACTINFO_URI,
                            new String[] { ContactInfoColumns.MEMO_NAME,
                                    ContactInfoColumns.NICK_NAME,
                                    ContactInfoColumns.FRIEND_USERID },
                            whereClause,
                            whereArgs,
                            null);
                    try
                    {
                        if (cursor != null && cursor.moveToFirst())
                        {
                            if (StringUtil.isNullOrEmpty(memoName))
                            {
                                memoName = cursor.getString(cursor.getColumnIndex(ContactInfoColumns.MEMO_NAME));
                            }
                            if (StringUtil.isNullOrEmpty(nickName))
                            {
                                nickName = cursor.getString(cursor.getColumnIndex(ContactInfoColumns.NICK_NAME));
                            }
                            if (StringUtil.isNullOrEmpty(friendUserId))
                            {
                                friendUserId = cursor.getString(cursor.getColumnIndex(ContactInfoColumns.FRIEND_USERID));
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
                }
                HanziToPinyin htp = HanziToPinyin.getInstance();
                String displayName = "";
                String spellName = "";
                String splitMemo = null;
                String splitNickName = null;
                // 计算显示名和转化成拼音的字符串
                if (!StringUtil.isNullOrEmpty(memoName))
                {
                    splitMemo = htp.getPinyinWithWhitespace(memoName);
                    spellName += htp.getPinyin(memoName);
                    spellName += " ";
                    if (StringUtil.isNullOrEmpty(displayName))
                    {
                        displayName = memoName;
                    }
                }
                if (!StringUtil.isNullOrEmpty(nickName))
                {
                    splitNickName = htp.getPinyinWithWhitespace(nickName);
                    spellName += htp.getPinyin(nickName);
                    spellName += " ";
                    if (StringUtil.isNullOrEmpty(displayName))
                    {
                        displayName = nickName;
                    }
                }
                if (!StringUtil.isNullOrEmpty(friendUserId))
                {
                    if (StringUtil.isNullOrEmpty(displayName))
                    {
                        displayName = friendUserId;
                    }
                }
                if (!StringUtil.isNullOrEmpty(spellName))
                {
                    String userSysId = values.getAsString(ContactInfoColumns.USER_SYSID);
                    // 如果是用户自己则不加入拼音
                    if (StringUtil.isNullOrEmpty(userSysId)
                            || !StringUtil.equals(userSysId,
                                    values.getAsString(ContactInfoColumns.FRIEND_SYSID)))
                    {
                        //带空格的拼音
                        values.put(ContactInfoColumns.SPELLNAME, spellName);
                        StringBuilder sb = new StringBuilder();
                        
                        //获得备注名的首字母
                        if (splitMemo != null)
                        {
                            String[] str = splitMemo.split(" ");
                            if (str != null)
                            {
                                for (int i = 0; i < str.length; i++)
                                {
                                    sb.append(str[i].charAt(0));
                                }
                                sb.append(" ");
                            }
                        }
                        
                        //获得姓名的首字母
                        if (splitNickName != null)
                        {
                            String[] str = splitNickName.split(" ");
                            if (str != null)
                            {
                                for (int i = 0; i < str.length; i++)
                                {
                                    sb.append(str[i].charAt(0));
                                }
                            }
                        }
                        
                        values.put(ContactInfoColumns.INITIALNAME,
                                sb.toString());
                    }
                }
                if (!StringUtil.isNullOrEmpty(displayName))
                {
                    // 加入显示名称和拼音
                    values.put(ContactInfoColumns.DISPLAY_NAME, displayName);
                    values.put(ContactInfoColumns.DISPLAY_SPELLNAME,
                            HanziToPinyin.getInstance()
                                    .getPinyinWithWhitespace(displayName));
                }
            }
            // 计算年龄
            String age = values.getAsString(ContactInfoColumns.AGE);
            if (StringUtil.isNullOrEmpty(age))
            {
                String birthday = values.getAsString(ContactInfoColumns.BIRTHDAY);
                if (!StringUtil.isNullOrEmpty(birthday))
                {
                    int year = Integer.parseInt(birthday.substring(0, 4));
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    age = Integer.toString(currentYear - year);
                }
                else
                {
                    age = "0";
                }
                values.put(ContactInfoColumns.AGE, age);
            }
        }
    }
    
    /**
     * 创建关联 provider对象入口
     * 
     * @return boolean
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate()
    {
        Logger.d(TAG, "RcsBaseLineProvider--->onCreate()");
        sp = getContext().getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onCreate();
        try
        {
            return initialize();
            
        }
        catch (RuntimeException e)
        {
            Logger.e(TAG, "RcsBaseLineProvider--->onCreate() exception:", e);
            return false;
        }
    }
    
    /**
     * 初始化<BR>
     * 初始化URI集合，初始化各处理具体业务的handler
     * @return 是否成功 
     */
    private boolean initialize()
    {
        if (null != getDatabaseHelper())
        {
            mDb = getDatabaseHelper().getWritableDatabase();
            initNotifyChangeUri();
            //注册tableHandler
            registerDataRowHandlers();
        }
        return null != mDb;
    }
    
    /**
     * 初始化DataRowHandlers
     * @author 周雪松
     */
    private void registerDataRowHandlers()
    {
        mDataRowHandlers = new HashMap<String, TableHandler>();
        mDataRowHandlers.put(Tables.CONTACTINFO, new ContactInfoTableHandler(
                Tables.CONTACTINFO));
    }
    
    /**
     * 初始化NotifyChangeUri
     * @author zhouxuesong
     */
    private void initNotifyChangeUri()
    {
        mNotifyChangeUri = new Vector<Uri>();
    }
    
    /**
     * 返回一个DatabaseHelper实例
     * 
     * 子类重写方法
     * 
     * @param context Context
     * @return DatabaseHelper
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider
     *      #getDatabaseHelper(android.content.Context)
     */
    @Override
    protected DatabaseHelper getDatabaseHelper(final Context context)
    {
        userId = sp.getString(Common.KEY_USER_ID, "");
        if (StringUtil.isNullOrEmpty(userId))
        {
            return null;
        }
        if (null != dbHelperPool.get(userId))
        {
            setDatabaseHelper(dbHelperPool.get(userId));
        }
        else
        {
            DatabaseHelper helper = DatabaseHelper.getInstance(getContext(),
                    userId);
            dbHelperPool.put(userId, helper);
            setDatabaseHelper(helper);
        }
        return dbHelperPool.get(userId);
    }
    
    /**
     * 获得TYPE
     * 
     * @param uri Uri
     * @return Uri
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri)
    {
        return null;
    }
    
    /**
     * 删除接口
     * 
     * @param uri Uri
     * @param whereClause String
     * @param whereArgs String[]
     * @return int
     * @see android.content.ContentProvider#delete(android.net.Uri,
     *      java.lang.String, java.lang.String[])
     */
    @Override
    protected int deleteInTransaction(Uri uri, String whereClause,
            String[] whereArgs)
    {
        Logger.i(TAG, "deleteInTransaction--->begin");
        
        int count = 0;
        
        TableHandler tableHandler = getTableHandlerByUri(uri);
        
        if (null != tableHandler)
        {
            Logger.i(TAG, "delete tableName, Uri: " + uri);
            
            count = tableHandler.delete(mDb, whereClause, whereArgs);
            if (count > 0)
            {
                switch (URIMATCHER.match(uri))
                {
                    case GROUPMEMBER_WITH_GROUPID_AND_MEMBERID:
                    {
                        String msgSendPath = uri.getPath();
                        String[] msgSendTemp = msgSendPath.split("/");
                        String groupId = msgSendTemp[2];
                        
                        if (null != groupId)
                        {
                            notifyChange(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                                    groupId));
                        }
                        
                        notifyChange(URIField.GROUPMEMBER_URI);
                        notifyChange(uri);
                        break;
                    }
                    case GROUPINFO_WITH_GROUP_ID:
                    {
                        notifyChange(URIField.GROUPINFO_URI);
                        break;
                    }
                    case CONTACTINFO:
                    {
                        notifyChange(URIField.CONTACTINFO_DELETE_URI);
                        break;
                    }
                }
                // 通知数据变更
                notifyChange(uri);
            }
        }
        
        return count;
    }
    
    /**
     * 修改接口<BR>
     * 
     * @param uri Uri
     * @param values ContentValues 需要更新的数据
     * @param whereClause String 条件
     * @param whereArgs String[] 条件值
     * @return count 更新的数目
     * @see android.content.ContentProvider#update(android.net.Uri,
     *      android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    protected int updateInTransaction(Uri uri, ContentValues values,
            String whereClause, String[] whereArgs)
    {
        userId = sp.getString(Common.KEY_USER_ID, "");
        //        if(dbHelperPool != null) {
        //            getHelperInitial();
        //        }
        Logger.i(TAG, "updateInTransaction()--->begin,uri: " + uri);
        int count = 0;
        TableHandler tableHandler = getTableHandlerByUri(uri);
        if (null != tableHandler && null != values)
        {
            count = tableHandler.update(mDb, values, whereClause, whereArgs);
        }
        Logger.i(TAG, "updateInTransaction()uri: " + uri);
        
        if (count > 0)
        {
            switch (URIMATCHER.match(uri))
            {
                case FRIENDMANAGER_WITH_FRIEND_USER_ID:
                {
                    //触发监听
                    notifyChange(URIField.FRIENDMANAGER_URI);
                    notifyChange(uri);
                    break;
                }
                case GROUPMEMBER_WITH_GROUPID_AND_MEMBERID:
                {
                    String msgSendPath = uri.getPath();
                    String[] msgSendTemp = msgSendPath.split("/");
                    String groupId = msgSendTemp[2];
                    
                    if (null != groupId)
                    {
                        notifyChange(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                                groupId));
                    }
                    
                    notifyChange(URIField.GROUPMEMBER_URI);
                    notifyChange(uri);
                    break;
                }
                case MESSAGE_UPDATE_STATE:
                {
                    List<String> pathSegment = uri.getPathSegments();
                    Uri messageTable = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                            pathSegment.get(1));
                    notifyChange(messageTable);
                    notifyChange(uri);
                    break;
                }
                case GROUPINFO_WITH_GROUP_ID:
                {
                    notifyChange(URIField.GROUPINFO_URI);
                    notifyChange(uri);
                    break;
                }
                default:
                    // 通知数据变更
                    notifyChange(uri);
                    break;
            }
            
        }
        
        return count;
    }
    
    /**
     * 查询接口<BR>
     * 
     * @param uri Uri
     * @param projection String[] 查询要返回的列
     * @param selection String   条件
     * @param selectionArgs String[] 条件值
     * @param orderBy String 排序字段
     * @return Cursor 返回数据cursor
     * @see android.content.ContentProvider#query(android.net.Uri,
     *      java.lang.String[], java.lang.String, java.lang.String[],
     *      java.lang.String)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String orderBy)
    {
        userId = sp.getString(Common.KEY_USER_ID, "");
        //        if(dbHelperPool != null) {
        //            getHelperInitial();
        //        }
        Logger.i(TAG, "begin query...   uri==> " + uri);
        
        Cursor cursor = null;
        int match = URIMATCHER.match(uri);
        switch (match)
        {
            case CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION:
            {
                // 关联分组表 和 头像表
                String sql = new StringBuilder("SELECT c.*, f.*, cs.").append(ContactSectionColumns.CONTACTSECTION_ID)
                        .append(" ")
                        .append(ContactInfoColumns.QUERY_CONTACT_SECTIONID)
                        .append(", cs.")
                        .append(ContactSectionColumns.NAME)
                        .append(" ")
                        .append(ContactInfoColumns.QUERY_CONTACT_SECTIONNAME)
                        .append(", cs.")
                        .append(ContactSectionColumns.NOTES)
                        .append(" ")
                        .append(ContactInfoColumns.QUERY_CONTACT_SECTIONNOTES)
                        .append(" FROM ")
                        .append(Tables.CONTACTINFO)
                        .append(" c LEFT JOIN ")
                        .append(Tables.FACETHUMBNAIL)
                        .append(" f ON c.")
                        .append(ContactInfoColumns.FRIEND_USERID)
                        .append("= f.")
                        .append(FaceThumbnailColumns.FACE_ID)
                        .append(" LEFT JOIN ")
                        .append(Tables.CONTACTSECTION)
                        .append(" cs ON c.")
                        .append(ContactInfoColumns.CONTACT_SECTIONID)
                        .append("= cs.")
                        .append(ContactSectionColumns.CONTACTSECTION_ID)
                        .append(" AND c.")
                        .append(ContactSectionColumns.USER_SYSID)
                        .append(" = cs.")
                        .append(ContactInfoColumns.USER_SYSID)
                        .append(" WHERE ")
                        .append(selection.replaceAll(Tables.CONTACTINFO + ".",
                                "c."))
                        .append(" ORDER BY c.")
                        .append(ContactInfoColumns.DISPLAY_SPELLNAME)
                        .append(" ASC")
                        .toString();
                
                Logger.d(TAG, "sql:" + sql);
                
                cursor = mDb.rawQuery(sql, selectionArgs);
                break;
            }
            //关联查询头像的url，头像的数据一般不放在contactInfoModel里面，因为数据量很大的时候，会导致OOM
            case CONTACTINFO_QUERY_WITH_FACETHUMBNAIL:
            {
                String sql = new StringBuffer("SELECT c.*,f.").append(FaceThumbnailColumns.FACE_URL)
                        .append(" FROM ")
                        .append(Tables.CONTACTINFO)
                        .append(" c LEFT JOIN ")
                        .append(Tables.FACETHUMBNAIL)
                        .append(" f ON c.")
                        .append(ContactInfoColumns.FRIEND_USERID)
                        .append(" = f.")
                        .append(FaceThumbnailColumns.FACE_ID)
                        .append(" WHERE ")
                        .append(selection.replaceAll(Tables.CONTACTINFO + ".",
                                "c."))
                        .append(" ORDER BY ")
                        .append(ContactInfoColumns.INITIALNAME)
                        .append(" ASC")
                        .toString();
                
                Logger.d(TAG, "sql:" + sql);
                
                cursor = mDb.rawQuery(sql, selectionArgs);
                break;
            }
            case FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL:
            {
                String sql = new StringBuilder("SELECT u.*,f.* FROM ").append(Tables.FRIEND_MANAGER)
                        .append(" u LEFT JOIN ")
                        .append(Tables.FACETHUMBNAIL)
                        .append(" f ON u.")
                        .append(FriendManagerColumns.FRIEND_USERID)
                        .append("= f.")
                        .append(FaceThumbnailColumns.FACE_ID)
                        .append(" WHERE ")
                        .append(selection.replaceAll(Tables.FRIEND_MANAGER
                                + ".", "u."))
                        .toString();
                
                Logger.d(TAG, "sql:" + sql);
                
                cursor = mDb.rawQuery(sql, selectionArgs);
                break;
            }
            case FRIENDMANAGER_QUERY_WITH_PAGE:
            {
                String sql = new StringBuilder("SELECT u.* FROM ").append(Tables.FRIEND_MANAGER)
                        .append(" u ")
                        .append(" WHERE ")
                        .append(selection.replaceAll(Tables.FRIEND_MANAGER
                                + ".", "u."))
                        .append(" ORDER BY ")
                        .append(FriendManagerColumns.OPERATE_TIME)
                        .append(" DESC ")
                        .append(" LIMIT ")
                        .append(selectionArgs[1])
                        .append(" OFFSET ")
                        .append(selectionArgs[2])
                        .toString();
                
                Logger.d(TAG, "sql: " + sql);
                
                cursor = mDb.rawQuery(sql, new String[] { selectionArgs[0] });
                break;
            }
            case GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT:
            {
                
                String msgSendPath = uri.getPath();
                String[] msgSendTemp = msgSendPath.split("/");
                String userSysId = msgSendTemp[2];
                String groupId = msgSendTemp[3];
                StringBuffer sql = new StringBuffer(
                        "SELECT g.*, f.*, (SELECT COUNT(me._ID) FROM ").append(Tables.GROUPMEMBER)
                        .append(" me WHERE me.")
                        .append(GroupMemberColumns.GROUP_ID)
                        .append("=g.")
                        .append(GroupInfoColumns.GROUP_ID)
                        .append(" AND me.")
                        .append(GroupMemberColumns.USER_SYSID)
                        .append(" = '")
                        .append(userSysId);
                if (!StringUtil.equals(groupId, "-1"))
                {
                    sql.append("' AND me.")
                            .append(GroupMemberColumns.GROUP_ID)
                            .append(" = '")
                            .append(groupId);
                }
                ;
                
                sql.append("' AND me.")
                        .append(GroupMemberColumns.AFFILIATION)
                        .append(" <> '")
                        .append(GroupInfoModel.CHATTYPE_NONE)
                        .append("' ) ")
                        .append(GroupInfoColumns.QUERY_NUMBER_COUNT)
                        .append(" FROM ")
                        .append(Tables.GROUPINFO)
                        .append(" g LEFT JOIN ")
                        .append(Tables.FACETHUMBNAIL)
                        .append(" f ON g.")
                        .append(GroupInfoColumns.GROUP_ID)
                        .append("= f.")
                        .append(FaceThumbnailColumns.FACE_ID)
                        .append(" WHERE ")
                        .append(selection.replaceAll(Tables.GROUPINFO + ".",
                                "g."));
                
                Logger.d(TAG, "sql:" + sql);
                
                cursor = mDb.rawQuery(sql.toString(), selectionArgs);
                break;
            }
            case GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL:
            {
                String sql = new StringBuilder("SELECT g.*, f.* FROM ").append(Tables.GROUPMEMBER)
                        .append(" g LEFT JOIN ")
                        .append(Tables.FACETHUMBNAIL)
                        .append(" f ON g.")
                        .append(GroupMemberColumns.MEMBER_USERID)
                        .append(" = f.")
                        .append(FaceThumbnailColumns.FACE_ID)
                        .append(" WHERE ")
                        .append(selection.replaceAll(Tables.GROUPMEMBER + ".",
                                "g."))
                        .toString();
                
                Logger.d(TAG, "sql:" + sql);
                
                cursor = mDb.rawQuery(sql, selectionArgs);
                break;
            }
            case GROUPMESSAGE_MSGID:
            {
                String tableName = Tables.GROUPMESSAGE;
                String mParams = uri.getPath();
                String[] mTemp = mParams.split("/");
                String mUserSysId = mTemp[2];
                String mMsgId = mTemp[3];
                String msgMediaColumnNames = new StringBuilder(" media.").append(MediaIndexColumns.MEDIA_PATH)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SIZE)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SMALL_PATH)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_TYPE)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_URL)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SMALL_URL)
                        .append(", media.")
                        .append(MediaIndexColumns.PLAY_TIME)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_ALT)
                        .append(", media.")
                        .append(MediaIndexColumns.LOCATION_LAT)
                        .append(", media.")
                        .append(MediaIndexColumns.LOCATION_LON)
                        .toString();
                String mMsgSql = "SELECT msg.*, media."
                        + MediaIndexColumns.MSG_ID + " mediaId, "
                        + msgMediaColumnNames + " FROM  " + tableName
                        + " msg LEFT JOIN " + Tables.MEDIAINDEX
                        + " media ON msg." + GroupMessageColumns.MSG_ID
                        + "=media." + MediaIndexColumns.MSG_ID + " WHERE msg."
                        + GroupMessageColumns.USER_SYSID + "=? AND msg."
                        + GroupMessageColumns.MSG_ID + "=?";
                
                Logger.d(TAG, "MESSAGE_MSGID, sql: " + mMsgSql + " :: "
                        + mUserSysId + ", " + mMsgId);
                
                cursor = mDb.rawQuery(mMsgSql, new String[] { mUserSysId,
                        mMsgId });
                break;
            }
            case GROUPMESSAGE_LAST:
            {
                String tableName = Tables.GROUPMESSAGE;
                String lastParams = uri.getLastPathSegment();
                String[] lastTemp = lastParams.split("\\|");
                String userSysId = lastTemp[0];
                String mGroupId = lastTemp[1];
                if (StringUtil.isNullOrEmpty(userSysId)
                        && StringUtil.isNullOrEmpty(mGroupId))
                {
                    break;
                }
                
                String queryLastGroupMessageSql = "SELECT m1.* FROM "
                        + tableName + " m1 " + "WHERE m1."
                        + GroupMessageColumns.MSG_TIME + " = "
                        + "(SELECT MAX(m2." + GroupMessageColumns.MSG_TIME
                        + ") FROM " + tableName + " m2 " + " WHERE "
                        + GroupMessageColumns.GROUP_ID + "=? AND "
                        + GroupMessageColumns.USER_SYSID + "=?) ORDER BY m1."
                        + GroupMessageColumns.ID + " DESC";
                
                Logger.d(TAG, "GROUPMESSAGE_LAST, sql: "
                        + queryLastGroupMessageSql);
                
                cursor = mDb.rawQuery(queryLastGroupMessageSql, new String[] {
                        mGroupId, userSysId });
                
                break;
            }
            case GROUPMESSAGE_BY_QUERY_MEDIAINDEX:
            {
                String params = uri.getPath();
                String[] tempStr = params.split("/");
                String userSysId = tempStr[2];
                String groupId = tempStr[3];
                
                String queryMediaSql = "SELECT me." + MediaIndexColumns.MSG_ID
                        + " msgId, me." + MediaIndexColumns.MEDIA_PATH
                        + " mediaFilePath, me."
                        + MediaIndexColumns.MEDIA_SMALL_PATH
                        + " mediaSmallFilePath FROM " + "(SELECT m.* FROM "
                        + Tables.GROUPMESSAGE + " m WHERE m."
                        + GroupMessageColumns.GROUP_ID + "=? AND m."
                        + GroupMessageColumns.USER_SYSID + "=? AND m."
                        + GroupMessageColumns.MSG_TYPE + "=?) msg LEFT JOIN "
                        + Tables.MEDIAINDEX + " me ON msg."
                        + GroupMessageColumns.MSG_ID + "=me."
                        + MediaIndexColumns.MSG_ID;
                
                cursor = mDb.rawQuery(queryMediaSql,
                        new String[] { groupId, userSysId,
                                String.valueOf(GroupMessageModel.MSGTYPE_MEDIA) });
                
                Logger.d(TAG,
                        "GROUPMESSAGE_BY_QUERY_MEDIAINDEX, sql: "
                                + queryMediaSql
                                + " :: "
                                + groupId
                                + ", "
                                + userSysId
                                + ", "
                                + String.valueOf(GroupMessageModel.MSGTYPE_MEDIA));
                
                break;
            }
            case GROUPMESSAGE_PAGE:
            {
                String gMsgMediaColumnNames = new StringBuilder(" media.").append(MediaIndexColumns.MEDIA_PATH)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SIZE)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SMALL_PATH)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_TYPE)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_URL)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SMALL_URL)
                        .append(", media.")
                        .append(MediaIndexColumns.PLAY_TIME)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_ALT)
                        .append(", media.")
                        .append(MediaIndexColumns.LOCATION_LAT)
                        .append(", media.")
                        .append(MediaIndexColumns.LOCATION_LON)
                        .toString();
                String groupMsgPageParams = uri.getPath();
                String[] tempGroupMsgPage = groupMsgPageParams.split("/");
                String userSysID = tempGroupMsgPage[2];
                String mGroupId = tempGroupMsgPage[3];
                String groupMsgPage = tempGroupMsgPage[4];
                tempGroupMsgPage = groupMsgPage.split("\\|");
                int mCurrentPage = Integer.valueOf(tempGroupMsgPage[0]);
                int mPerPageCount = Integer.valueOf(tempGroupMsgPage[1]);
                
                String groupMsgPageSql = "";
                String tempGroupMsgPageSql = "SELECT msg.*, media."
                        + MediaIndexColumns.MSG_ID + " mediaId, "
                        + gMsgMediaColumnNames + " FROM " + Tables.GROUPMESSAGE
                        + " msg LEFT JOIN " + Tables.MEDIAINDEX
                        + " media ON msg." + GroupMessageColumns.MSG_ID
                        + "=media." + MediaIndexColumns.MSG_ID + " WHERE msg."
                        + GroupMessageColumns.USER_SYSID + "=? AND msg."
                        + GroupMessageColumns.GROUP_ID + "=? ORDER BY msg."
                        + GroupMessageColumns.MSG_TIME;
                
                if (mPerPageCount == -1)
                {
                    // 不分页，查询全部
                    groupMsgPageSql = tempGroupMsgPageSql;
                }
                else
                {
                    if (mCurrentPage < 1)
                    {
                        mCurrentPage = 1;
                    }
                    int start = (mCurrentPage - 1) * mPerPageCount;
                    int end = mPerPageCount;
                    groupMsgPageSql = tempGroupMsgPageSql + " LIMIT " + start
                            + ", " + end;
                }
                
                cursor = mDb.rawQuery(groupMsgPageSql, new String[] {
                        userSysID, mGroupId });
                
                Logger.d(TAG, "MESSAGE_PAGE, sql: " + groupMsgPageSql + " :: "
                        + userSysID + ", " + mGroupId);
                
                break;
            }
            case MESSAGE_PAGE:
            {
                String messageMediaColumnNames = new StringBuilder(" media.").append(MediaIndexColumns.MEDIA_PATH)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SIZE)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SMALL_PATH)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_TYPE)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_URL)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_SMALL_URL)
                        .append(", media.")
                        .append(MediaIndexColumns.PLAY_TIME)
                        .append(", media.")
                        .append(MediaIndexColumns.MEDIA_ALT)
                        .append(", media.")
                        .append(MediaIndexColumns.LOCATION_LAT)
                        .append(", media.")
                        .append(MediaIndexColumns.LOCATION_LON)
                        .toString();
                String msgParams = uri.getPath();
                String[] tempMsg = msgParams.split("/");
                String userSysId = tempMsg[2];
                String mConversationId = tempMsg[3];
                String mPage = tempMsg[4];
                tempMsg = mPage.split("\\|");
                int currentPage = Integer.valueOf(tempMsg[0]);
                int perPageCount = Integer.valueOf(tempMsg[1]);
                
                String msgPageSql = "";
                String tempMsgPageSql = "SELECT msg.*, media."
                        + MediaIndexColumns.MSG_ID + " mediaId, "
                        + messageMediaColumnNames + " FROM " + Tables.MESSAGE
                        + " msg LEFT JOIN " + Tables.MEDIAINDEX
                        + " media ON msg." + MessageColumns.MSG_ID + "=media."
                        + MediaIndexColumns.MSG_ID + " WHERE msg."
                        + MessageColumns.USER_SYSID + "=? AND msg."
                        + MessageColumns.FRIEND_USERID + "=? ORDER BY msg."
                        + MessageColumns.MSG_TIME;
                
                if (perPageCount == -1)
                {
                    // 不分页，查询全部
                    msgPageSql = tempMsgPageSql;
                }
                else
                {
                    if (currentPage < 1)
                    {
                        currentPage = 1;
                    }
                    int start = (currentPage - 1) * perPageCount;
                    int end = perPageCount;
                    msgPageSql = tempMsgPageSql + " LIMIT " + start + ", "
                            + end;
                }
                
                cursor = mDb.rawQuery(msgPageSql, new String[] { userSysId,
                        mConversationId });
                
                Logger.d(TAG, "MESSAGE_PAGE, sql: " + msgPageSql + " :: "
                        + userSysId + ", " + mConversationId);
                
                break;
            }
            case MESSAGE_BY_QUERY_MEDIAINDEX:
            {
                String params = uri.getPath();
                String[] tempStr = params.split("/");
                String userSysId = tempStr[2];
                String conversationId = tempStr[3];
                
                String queryMediaSql = "SELECT me." + MediaIndexColumns.MSG_ID
                        + " msgId, me." + MediaIndexColumns.MEDIA_PATH
                        + " mediaFilePath, me."
                        + MediaIndexColumns.MEDIA_SMALL_PATH
                        + " mediaSmallFilePath FROM " + "(SELECT m.* FROM "
                        + Tables.MESSAGE + " m WHERE m."
                        + MessageColumns.FRIEND_USERID + "=? AND m."
                        + MessageColumns.USER_SYSID + "=? AND m."
                        + MessageColumns.MSG_TYPE + "=?) msg LEFT JOIN "
                        + Tables.MEDIAINDEX + " me ON msg."
                        + MessageColumns.MSG_ID + "=me."
                        + MediaIndexColumns.MSG_ID;
                
                cursor = mDb.rawQuery(queryMediaSql,
                        new String[] { conversationId, userSysId,
                                String.valueOf(MessageModel.MSGTYPE_MEDIA) });
                
                Logger.d(TAG,
                        "MESSAGE_BY_QUERY_MEDIAINDEX, sql: " + queryMediaSql
                                + " :: " + conversationId + ", " + userSysId
                                + ", "
                                + String.valueOf(MessageModel.MSGTYPE_MEDIA));
                
                break;
            }
            case MESSAGE_LAST:
            {
                String tableName = Tables.MESSAGE;
                String msgConverParams = uri.getLastPathSegment();
                String[] tempConver = msgConverParams.split("\\|");
                String userSysId = tempConver[0];
                String mConversationId = tempConver[1];
                if (StringUtil.isNullOrEmpty(mConversationId))
                {
                    break;
                }
                
                String queryLastMessageSql = "SELECT m1.* FROM " + tableName
                        + " m1 " + "WHERE m1." + MessageColumns.MSG_TIME
                        + " = " + "(SELECT MAX(m2." + MessageColumns.MSG_TIME
                        + ") FROM " + tableName + " m2 " + " WHERE "
                        + MessageColumns.FRIEND_USERID + "=? AND "
                        + MessageColumns.USER_SYSID + "=?) AND " + " m1. "
                        + MessageColumns.FRIEND_USERID + " =?  ORDER BY m1."
                        + MessageColumns.ID + " DESC";
                
                Logger.d(TAG, "MESSAGE_LAST, sql: " + queryLastMessageSql);
                
                cursor = mDb.rawQuery(queryLastMessageSql, new String[] {
                        mConversationId, userSysId, mConversationId });
                
                break;
            }
            
            case SYSAPPINFO_APPID:
            {
                String tableName = Tables.SYSAPPINFO;
                String params = uri.getPath();
                String[] tempStr = params.split("/");
                String userSysId = tempStr[2];
                String appId = tempStr[3];
                String appType = tempStr[4];
                String iconColumnNames = " icon."
                        + FaceThumbnailColumns.FACE_URL + ", icon."
                        + FaceThumbnailColumns.FACE_BYTES;
                String querySysAppByAppId = "SELECT s.*," + iconColumnNames
                        + " FROM " + tableName + " s LEFT JOIN "
                        + Tables.FACETHUMBNAIL + " icon ON s."
                        + SysAppInfoColumns.APP_ID + "=icon."
                        + FaceThumbnailColumns.FACE_ID + " WHERE s."
                        + SysAppInfoColumns.USER_SYSID + "=? AND s."
                        + SysAppInfoColumns.APP_ID + "=? AND s."
                        + SysAppInfoColumns.TYPE + "=?";
                
                Logger.d(TAG, "SYSAPPINFO_APPID, sql: " + querySysAppByAppId);
                
                cursor = mDb.rawQuery(querySysAppByAppId, new String[] {
                        userSysId, appId, appType });
                break;
            }
            
            case SYSAPPINOF_ALL:
            {
                String tableName = Tables.SYSAPPINFO;
                String params = uri.getPath();
                String[] tempStr = params.split("/");
                String userSysId = tempStr[2];
                String appType = tempStr[3];
                String iconColumnNames = " icon."
                        + FaceThumbnailColumns.FACE_URL + ", icon."
                        + FaceThumbnailColumns.FACE_BYTES;
                String querySysAppAll = "SELECT s.*," + iconColumnNames
                        + " FROM " + tableName + " s LEFT JOIN "
                        + Tables.FACETHUMBNAIL + " icon ON s."
                        + SysAppInfoColumns.APP_ID + "=icon."
                        + FaceThumbnailColumns.FACE_ID + " WHERE s."
                        + SysAppInfoColumns.USER_SYSID + "=? AND s."
                        + SysAppInfoColumns.TYPE + "=?";
                
                Logger.d(TAG, "SYSAPPINOF_ALL, sql:" + querySysAppAll);
                
                cursor = mDb.rawQuery(querySysAppAll, new String[] { userSysId,
                        appType });
                
                break;
            }
            case MY_APP_ALL:
            {
                String tableName = Tables.MY_APP;
                String userSysId = uri.getLastPathSegment();
                String iconColumnNames = " icon."
                        + FaceThumbnailColumns.FACE_URL + ", icon."
                        + FaceThumbnailColumns.FACE_BYTES;
                
                String queryMyAppAllSql = "SELECT s.*," + iconColumnNames
                        + " FROM " + tableName + " m LEFT JOIN "
                        + Tables.SYSAPPINFO + " s ON m." + MyAppColumns.APP_ID
                        + "=s." + SysAppInfoColumns.APP_ID + " LEFT JOIN "
                        + Tables.FACETHUMBNAIL + " icon ON s."
                        + SysAppInfoColumns.APP_ID + "=icon."
                        + FaceThumbnailColumns.FACE_ID + " WHERE m."
                        + MyAppColumns.USER_SYSID + "=? AND s."
                        + SysAppInfoColumns.USER_SYSID + "=?";
                
                Logger.d(TAG, "MY_APP_ALL, sql:" + queryMyAppAllSql);
                
                //                cursor = mDb.rawQuery(queryMyAppAllSql,
                //                        new String[] { userSysId, userSysId,
                //                                String.valueOf(SysAppInfoModel.TYPE_RECOMMEND) });
                
                cursor = mDb.rawQuery(queryMyAppAllSql, new String[] {
                        userSysId, userSysId });
                break;
            }
            case CONVERSATION_FOR_PAGE:
                String msgParams = uri.getPath();
                String[] tempMsg = msgParams.split("/");
                String userSysId = tempMsg[2];
                int perPageCount = Integer.valueOf(tempMsg[3]);
                String converSql = "SELECT * FROM " + Tables.CONVERSATION
                        + " WHERE " + ConversationColumns.USER_SYSID + " = "
                        + userSysId + " AND "
                        + ConversationColumns.CONVERSATIONTYPE + " <> "
                        + ConversationModel.CONVERSATIONTYPE_1VN;
                converSql += " ORDER BY " + ConversationColumns.LASTTIME
                        + " DESC";
                if (perPageCount != -1)
                {
                    converSql += " LIMIT " + perPageCount;
                }
                Logger.d(TAG, "CONVERSATION_FOR_PAGE, sql:" + converSql);
                cursor = mDb.rawQuery(converSql, null);
                break;
            case NOTIFICATION_IM_SINGLE:
                //TODO:sql语句优化，以及代码重构
                String params = uri.getPath();
                String[] temp = params.split("/");
                String curFriendUserId = temp[2];
                String mUserSysId = temp[3];
                String hitalkSecretary = getContext().getString(R.string.hitalk_secretary);
                StringBuffer sqlBuffer = new StringBuffer().append("SELECT ")
                        .append(" c.*, m.")
                        .append(MediaIndexColumns.MEDIA_TYPE)
                        .append(" AS ")
                        .append(MediaIndexColumns.MEDIA_TYPE)
                        .append(", CASE WHEN (")
                        .append(" c. ")
                        .append(ConversationColumns.CONVERSATIONID)
                        .append(" = '")
                        .append(FusionCode.XmppConfig.SECRETARY_ID)
                        .append("' ) THEN '")
                        .append(hitalkSecretary)
                        .append("' ELSE (SELECT j.")
                        .append(ContactInfoColumns.NICK_NAME)
                        .append(" FROM ")
                        .append(Tables.CONTACTINFO)
                        .append(" j ")
                        .append(" WHERE c.")
                        .append(ConversationColumns.CONVERSATIONID)
                        .append(" = j.")
                        .append(ContactInfoColumns.FRIEND_USERID)
                        .append(") END AS ")
                        .append(ContactInfoColumns.NICK_NAME)
                        .append(" FROM (")
                        .append(" SELECT a.*, b.sumUnreadMsg AS sumUnreadMsg FROM ")
                        .append("( SELECT ")
                        .append(ConversationColumns.USER_SYSID)
                        .append(",")
                        .append(ConversationColumns.CONVERSATIONID)
                        .append(",")
                        .append(ConversationColumns.LAST_MSG_ID)
                        .append(",")
                        .append(ConversationColumns.LAST_MSG_CONTENT)
                        .append(",")
                        .append(ConversationColumns.LASTTIME)
                        .append(",")
                        .append(ConversationColumns.LAST_MSG_TYPE)
                        .append(" FROM ")
                        .append(Tables.CONVERSATION)
                        .append(" WHERE ")
                        .append(ConversationColumns.UNREAD_MSG)
                        .append(" >0 AND (")
                        .append(ConversationColumns.CONVERSATIONTYPE)
                        .append(" = '")
                        .append("1' OR ")
                        .append(ConversationColumns.CONVERSATIONTYPE)
                        .append("  = '5' AND ")
                        .append(ConversationColumns.USER_SYSID)
                        .append(" = '")
                        .append(mUserSysId)
                        .append("') ) a ")
                        .append(" LEFT JOIN ")
                        .append("( SELECT ")
                        .append(ConversationColumns.USER_SYSID)
                        .append(",")
                        .append("SUM(")
                        .append(ConversationColumns.UNREAD_MSG)
                        .append(") AS sumUnreadMsg FROM ")
                        .append(Tables.CONVERSATION)
                        .append(" WHERE (")
                        .append(ConversationColumns.CONVERSATIONTYPE)
                        .append(" = '")
                        .append("1' OR ")
                        .append(ConversationColumns.CONVERSATIONTYPE)
                        .append("  = '5') AND ")
                        .append(ConversationColumns.CONVERSATIONID)
                        .append(" <> '")
                        .append(curFriendUserId)
                        .append("' AND ")
                        .append(ConversationColumns.USER_SYSID)
                        .append(" = '")
                        .append(mUserSysId)
                        .append("' ) b")
                        .append(" WHERE a.")
                        .append(ConversationColumns.USER_SYSID)
                        .append(" =  '")
                        .append(mUserSysId)
                        .append("' AND a.")
                        .append(ConversationColumns.CONVERSATIONID)
                        .append(" <> '")
                        .append(curFriendUserId)
                        .append("' ) c LEFT JOIN ")
                        .append(Tables.MEDIAINDEX)
                        .append(" m ON c.")
                        .append(ConversationColumns.LAST_MSG_ID)
                        .append(" = m.")
                        .append(MediaIndexColumns.MSG_ID)
                        .append(" ORDER BY ")
                        .append(ConversationColumns.LASTTIME)
                        .append(" DESC ");
                Logger.d(TAG, "NOTIFICATION_SINGLE_IM stringBuffer, SQL:"
                        + sqlBuffer.toString());
                cursor = mDb.rawQuery(sqlBuffer.toString(), selectionArgs);
                break;
            case MESSGAE_UNREAD_SPECIAL_MEDIA:
                String sql1 = "SELECT a.* FROM " + Tables.MESSAGE + " a, "
                        + Tables.MEDIAINDEX + " b WHERE a."
                        + MessageColumns.MSG_ID + " = b."
                        + MediaIndexColumns.MSG_ID + " AND b."
                        + MediaIndexColumns.MEDIA_TYPE + " =? " + " AND a."
                        + MessageColumns.FRIEND_USERID + " =? AND (a."
                        + MessageColumns.MSG_STATUS + " = '"
                        + MessageModel.MSGSTATUS_UNREAD_NEED_REPORT + "' OR a."
                        + MessageColumns.MSG_STATUS + " = '"
                        + MessageModel.MSGSTATUS_UNREAD_NO_REPORT + "')";
                Logger.d(TAG, "MESSGAE_UNREAD_SPECIAL_MEDIA, sql:" + sql1);
                cursor = mDb.rawQuery(sql1, selectionArgs);
                break;
            case GROUPMESSGAE_UNREAD_SPECIAL_MEDIA:
                String sql2 = "SELECT a.* FROM " + Tables.GROUPMESSAGE + " a, "
                        + Tables.MEDIAINDEX + " b WHERE a."
                        + GroupMessageColumns.MSG_ID + " = b."
                        + MediaIndexColumns.MSG_ID + " AND b."
                        + MediaIndexColumns.MEDIA_TYPE + " =? " + " AND a."
                        + GroupMessageColumns.GROUP_ID + " =? AND (a."
                        + GroupMessageColumns.MSG_STATUS + " = '"
                        + GroupMessageModel.MSGSTATUS_UNREAD_NEED_REPORT
                        + "' OR a." + GroupMessageColumns.MSG_STATUS + " = '"
                        + GroupMessageModel.MSGSTATUS_UNREAD_NO_REPORT + "')";
                Logger.d(TAG, "GROUPMESSGAE_UNREAD_SPECIAL_MEDIA, sql:" + sql2);
                cursor = mDb.rawQuery(sql2, selectionArgs);
                break;
            default:
                SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                qb.setTables(getTableNameByMatchCode(match));
                cursor = qb.query(mDb,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy,
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
     * 
     * 事务中对insert的处理
     * 子类重写方法
     * @param uri 插入的地址描述
     * @param values 插入的值
     * @return 插入后的uri描述
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider
     *      #insertInTransaction(android.net.Uri, android.content.ContentValues)
     */
    @Override
    protected Uri insertInTransaction(Uri uri, ContentValues values)
    {
        userId = sp.getString(Common.KEY_USER_ID, "");
        //        if(dbHelperPool != null) {
        //            getHelperInitial();
        //        }
        Uri resultUri = null;
        TableHandler tableHandler = getTableHandlerByUri(uri);
        
        if (null != tableHandler && null != values)
        {
            long rowId = tableHandler.insert(mDb, null, values);
            
            if (rowId > 0)
            {
                resultUri = ContentUris.withAppendedId(uri, rowId);
                switch (URIMATCHER.match(uri))
                {
                    case CONTACTINFO:
                    {
                        String friendUserId = (String) values.get(ContactInfoColumns.FRIEND_USERID);
                        if (friendUserId != null)
                        {
                            notifyChange(Uri.withAppendedPath(URIField.CONTACTINFO_WITH_FRIEND_USER_ID_URI,
                                    friendUserId));
                        }
                        notifyChange(URIField.CONTACTINFO_INSERT_URI);
                        notifyChange(uri);
                        break;
                    }
                    case FRIENDMANAGER_WITH_FRIEND_USER_ID:
                    {
                        notifyChange(URIField.FRIENDMANAGER_URI);
                        notifyChange(uri);
                        break;
                    }
                    case GROUPINFO_WITH_GROUP_ID:
                    {
                        notifyChange(URIField.GROUPINFO_URI);
                        notifyChange(uri);
                        break;
                    }
                    case GROUPMEMBER_WITH_GROUPID_AND_MEMBERID:
                    {
                        String msgSendPath = uri.getPath();
                        String[] msgSendTemp = msgSendPath.split("/");
                        String groupId = msgSendTemp[2];
                        
                        if (null != groupId)
                        {
                            notifyChange(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                                    groupId));
                        }
                        
                        notifyChange(URIField.GROUPMEMBER_URI);
                        notifyChange(uri);
                        break;
                    }
                    default:
                        notifyChange(uri);
                        break;
                }
                
            }
        }
        else
        {
            // throw new IllegalArgumentException("Unkown uri:" + uri);
            Logger.w(TAG, "insertInTransaction()  fail, uri: " + uri
                    + ", uri or tableName is null...");
        }
        
        return resultUri;
    }
    
    /**
     * 根据Uri匹配出数据库表名<BR>
     * 
     * @param uri Uri
     * @return 数据库表名
     */
    private String getTableNameByUri(Uri uri)
    {
        if (uri != null)
        {
            final int match = URIMATCHER.match(uri);
            return getTableNameByMatchCode(match);
        }
        return null;
    }
    
    /**
     * 根据match值匹配出数据库表名
     * @param match match 解析URI获得的对应的整数值
     * @return String 数据库表名
     */
    private String getTableNameByMatchCode(int match)
    {
        switch (match)
        {
            case ACCOUNT:
                return Tables.ACCOUNT;
            case FACETHUMBNAIL:
                return Tables.FACETHUMBNAIL;
            case PHONECONTACTINDEX:
                return Tables.PHONECONTACTINDEX;
            case CONTACTSECTION:
                return Tables.CONTACTSECTION;
            case CONTACTINFO:
            case CONTACTINFO_WITH_FRIEND_USER_ID:
                return Tables.CONTACTINFO;
            case USERCONFIG:
                return Tables.USERCONFIG;
                
            case SYSAPPINFO:
            case SYSAPPINOF_ALL:
                return Tables.SYSAPPINFO;
            case MY_APP:
                return Tables.MY_APP;
                
            case FRIENDMANAGER:
            case FRIENDMANAGER_WITH_FRIEND_USER_ID:
                return Tables.FRIEND_MANAGER;
            case GROUPINFO:
            case GROUPINFO_WITH_GROUP_ID:
                return Tables.GROUPINFO;
            case GROUPMEMBER:
            case GROUPMEMBER_WITH_GROUP_ID:
            case GROUPMEMBER_WITH_GROUPID_AND_MEMBERID:
                return Tables.GROUPMEMBER;
            case CONVERSATION:
            case CONVERSATION_FRIEND:
                return Tables.CONVERSATION;
            case MEDIAINDEX_AUTO:
            case MEDIAINDEX_CONVERSATION:
            case MEDIAINDEX:
                return Tables.MEDIAINDEX;
            case GROUPMESSAGE:
            case GROUP_MESSAGE_WITH_GROUPID:
            case GROUPMESSAGE_MSGID:
                return Tables.GROUPMESSAGE;
            case MESSAGE:
            case MESSAGE_WITH_FRIENDID:
            case MESSAGE_UPDATE_STATE:
                return Tables.MESSAGE;
            default:
                return null;
        }
    }
    
    /**
     * 
     * 通过Uri获得处理目的数据库表的对象<BR>
     * 
     * @param uri 数据表所在的ContentProvider地址
     * @return 具体处理业务逻辑的handler对象
     */
    private TableHandler getTableHandlerByUri(Uri uri)
    {
        String tableName = getTableNameByUri(uri);
        TableHandler tableHandler = null;
        if (null != tableName)
        {
            tableHandler = mDataRowHandlers.get(tableName);
            if (null == tableHandler)
            {
                tableHandler = new TableHandler(tableName);
            }
        }
        return tableHandler;
    }
    
    /**
     * 通知数据变更<BR>
     * 
     * @param uri Uri
     */
    private void notifyChange(Uri uri)
    {
        mNotifyChangeUri.add(uri);
    }
    
    /**
     * 
     * 当有数据内容变动时，通知变动的抽象方法
     * 子类继承方法
     * @see com.huawei.basic.android.im.component.database.SQLiteContentProvider#notifyChange()
     */
    @Override
    protected void notifyChange()
    {
        synchronized (mNotifyChangeUri)
        {
            ContentResolver contentResolver = getContext().getContentResolver();
            for (Uri uri : mNotifyChangeUri)
            {
                Logger.d(TAG, "监听到数据变化 " + uri);
                contentResolver.notifyChange(uri, null);
            }
            mNotifyChangeUri.clear();
        }
    }
    
    /**
     * 当配置文件发生改变时候触发此监听方法<BR>
     * @param sharedPreferences 监听的配置文件对象
     * @param key 监听的key
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
     */
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key)
    {
        Logger.d(TAG, "onSharedPreferenceChanged ------");
        if (StringUtil.equals(key, Common.KEY_USER_ID))
        {
            userId = sp.getString(Common.KEY_USER_ID, "");
            Logger.d(TAG, "onSharedPreferenceChanged  userSysId : " + userId);
            getHelperInitial();
        }
        else if (StringUtil.equals(key, Common.KEY_ISLOGIN))
        {
            boolean isLogin = sp.getBoolean(Common.KEY_ISLOGIN, false);
            Logger.d(TAG, "onSharedPreferenceChanged isLogin : " + isLogin);
            if (isLogin)
            {
                getHelperInitial();
            }
        }
    }
    
    /**
     * 
     * 获得databseHelper对象，并初始化相关初始值<BR>
     */
    private synchronized void getHelperInitial()
    {
        Logger.d(TAG, "getHelperInitial  " + dbHelperPool.get(userId));
        if (null != dbHelperPool.get(userId))
        {
            setDatabaseHelper(dbHelperPool.get(userId));
        }
        else
        {
            DatabaseHelper helper = DatabaseHelper.getInstance(getContext(),
                    userId);
            dbHelperPool.put(userId, helper);
            setDatabaseHelper(helper);
        }
        initialize();
    }
    
}
