/*
 * 文件名: URIField.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 定义系统要用到的URI
 * 创建人: qinyangwang
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.database;

import android.net.Uri;

import com.huawei.basic.android.im.component.database.DatabaseHelper.QueryCondition;
import com.huawei.basic.android.im.component.database.DatabaseHelper.Tables;

/**
 * Provieder中要用到的URI常量集合
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public class URIField
{
    /**
     * 系统数据库操作权限/provider权限，与AndroidManifest.xml中的provider中的配置一致
     */
    public static final String AUTHORITY = "com.huawei.rcsbaseline.database";
    
    /**
     * 系统数据库操作权限URI: 系统 provider URI
     */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    
    /*================================基本数据表操作URI定义开始========================================= */
    
    /**
     * 基本数据库表操作URI: 联系人分组表
     */
    public static final Uri CONTACTSECTION_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.CONTACTSECTION);
    
    /**
     * 基本数据库表操作URI: 帐号信息表
     */
    public static final Uri ACCOUNT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Tables.ACCOUNT);
    
    /**
     * 基本数据库表操作URI: 用户头像表
     */
    public static final Uri FACETHUMBNAIL_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.FACETHUMBNAIL);
    
    /**
     * 基本数据库表操作URI: 个人/好友信息表
     */
    public static final Uri CONTACTINFO_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.CONTACTINFO);
    
    /**
     * 基本数据库表操作URI: 手机联系人信息表
     */
    public static final Uri PHONECONTACTINDEX_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.PHONECONTACTINDEX);
    
    /**
     * 基本数据库表操作URI: 用户全局配置表
     */
    public static final Uri USERCONFIG_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Tables.USERCONFIG);
    
    /**
     * 基本数据库表操作URI: 系统应用
     */
    public static final Uri SYSAPPINFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Tables.SYSAPPINFO);
    
    /**
     * 查询所有系统应用 URI <br>
     * 格式： .../sysApp_all/[userSysId]/[type]
     */
    public static final Uri SYSAPPINFO_ALL_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.SYSAPPINFO_ALL);
    
    /**
     * 基本数据库表操作URI: 找朋友小助手信息表
     */
    public static final Uri FRIENDMANAGER_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.FRIEND_MANAGER);
    
    /**
     * 基本数据库表操作URI: 群组信息
     */
    public static final Uri GROUPINFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Tables.GROUPINFO);
    
    /**
     * 基本数据库表操作URI: 临时群/群成员表
     */
    public static final Uri GROUPMEMBER_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.GROUPMEMBER);
    
    /**
     * 基本数据库表操作URI: 消息会话表
     */
    public static final Uri CONVERSATION_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.CONVERSATION);
    
    /**
     * 基本数据库表操作URI: 单人聊天消息表
     */
    public static final Uri MESSAGE_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Tables.MESSAGE);
    
    /**
     * 基本数据库表操作URI: 多媒体文件索引表
     */
    public static final Uri MEDIAINDEX_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Tables.MEDIAINDEX);
    
    /**
     * 基本数据库表操作URI: 临时群/群 消息
     */
    public static final Uri GROUPMESSAGE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.GROUPMESSAGE);
    
    /*================================基本数据表操作URI定义结束========================================= */
    
    /*================================特殊数据表操作URI定义开始========================================= */
    
    /**
     * 特殊删除记录操作URI: 个人/好友信息表
     */
    public static final Uri CONTACTINFO_DELETE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.CONTATCINFO_DELETE);
    
    /**
     * 特殊数据库表操作URI: 包含friendUserId操作的联系人信息表URI
     */
    public static final Uri CONTACTINFO_WITH_FRIEND_USER_ID_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.CONTACTINFO_WITH_FRIEND_USER_ID);
    
    /**
     * 特殊数据库表操作URI: 联系人插入URI
     */
    public static final Uri CONTACTINFO_INSERT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.CONTACTINFO_INSERT);
    
    /**
     * 特殊数据库表操作URI: 根据friendSysId关联查询好友详细信息(联合查询)，包括头像和分组信息URI。
     */
    public static final Uri CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_AND_CONTACTSETION);
    
    /**
     * 特殊数据库表操作URI: 根据friendSysId 关联查询好友详细信息(联合查询)，包括头像。
     */
    public static final Uri CONTACTINFO_QUERY_WITH_FACETHUMBNAIL_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.CONTACTINFO_QUERY_WITH_FACETHUMBNAIL);
    
    /**
     * 特殊数据库表操作URI: 分页查询小助手数据
     */
    public static final Uri FRIENDMANAGER_QUERY_WITH_PAGE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.FRIENDMANAGER_QUERY_WITH_PAGE);
    
    /**
     * 特殊数据库表操作URI: 找朋友小助手信息表 包含hitalkId 的URI
     */
    public static final Uri FRIENDMANAGER_WITH_FRIEND_USER_ID_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.FRIENDMANAGER_WITH_FRIEND_USER_ID);
    
    /**
     * 特殊数据库表操作URI: 关联头像表查询小助手的URI
     */
    public static final Uri FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.FRIENDMANAGER_QUERY_WITH_FACETHUMBNAIL);
    
    /**
     * 特殊数据库表操作URI: 包含群组ID的群组信息操作URI
     */
    public static final Uri GROUPINFO_WITH_GROUPID_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.GROUPINFO_WITH_GROUP_ID);
    
    /**
     * 特殊数据库表操作URI: 分页查询群聊天消息URI <br>
     * 格式如： .../groupMessage_page/userSysId/[groupId]/[currentPage|perPageCount] <br>
     * currentPage: 当前页 <br>
     * perPageCount: 每页显示的记录数
     */
    public static final Uri GROUPMESSAGE_PAGE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.GROUP_MESSAGE_PAGE);
    
    /**
     * 特殊数据库表操作URI: 根据群组ID查询最后一条群组消息URI
     */
    public static final Uri GROUPMESSAGE_LAST_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.GROUP_MESSAGE_LAST);
    
    /**
     * 特殊数据库表操作URI: 关联头像和成员数临时群/群组信息查询URI
     */
    public static final Uri GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT);
    
    /**
     * 特殊数据库表操作URI: 包含群组ID的临时群/群员表操作URI
     */
    public static final Uri GROUPMEMBER_WITH_GROUPID_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.GROUPMEMBER_WITH_GROUP_ID);
    
    /**
     * 特殊数据库表操作URI: 包含成员的群组ID和成员的ID临时群/群员表操作操作URI
     */
    public static final Uri GROUPMEMBER_WITH_GROUPID_AND_MEMBERID_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.GROUPMEMBER_WITH_GROUPID_AND_MEMBERID);
    
    /**
     * 特殊数据库表操作URI: 关联头像的临时群/群成员查询URI
     */
    public static final Uri GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL);
    
    /**
     * 特殊数据库表操作URI: 包含friendUserId的单人聊天信息表操作URI<br>
     */
    public static final Uri MESSAGE_WITH_FRIENDID_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.MESSAGE_WITH_FRIENDID);
    
    /**
     * 特殊数据库表操作URI: 根据conversationId/friendUserId查询最后一条单人聊天信息URI <br>
     * 格式如： .../message_last/[conversationId]
     */
    public static final Uri MESSAGE_LAST_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.MESSAGE_LAST);
    
    /**
     * 特殊数据库表操作URI: 分页查询单人聊天消息 URI<br>
     * 格式如：
     * .../message_page/userSysId/[conversationId]/[currentPage|perPageCount] <br>
     * currentPage: 当前页 <br>
     * perPageCount: 每页显示的记录数
     */
    public static final Uri MESSAGE_PAGE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.MESSAGE_PAGE);
    
    /**
     * 特殊数据库表操作URI: 根据 conversationId/friendUserId查询所有多媒体消息 <br>
     * 格式如： .../message_by_query_media/[userSysId]/[conversationId]
     */
    public static final Uri MESSAGE_QUERY_MEDIAINDEX_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.MESSAGE_BY_QUERY_MEDIAINDEX);
    
    /**
     * 特殊数据库表操作URI: 更新单人消息状态操作URI
     */
    public static final Uri MESSAGE_UPDATE_STATE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.MESSAGE_UPDATE_STATE);
    
    /**
     * 特殊数据库表操作URI：查询指定类型的多媒体未读的消息
     */
    public static final Uri MESSGAE_UNREAD_SPECIAL_MEDIA_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.MESSGAE_UNREAD_SPECIAL_MEDIA);
    
    /**
     * 特殊数据库表操作URI: 包含conversationId/friendUserId的媒体索引表操作URI <br>
     * 格式如： .../mediaIndex/[conversationId]
     */
    public static final Uri MEDIAINDEX_CONVERSATIONID_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.MEDIAINDEX_CONVERSATIONID);
    
    /**
     * 特殊数据库表操作URI: 包含群组ID的群消息信息表URI
     */
    public static final Uri GROUPMESSAGE_WITH_GROUPID_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.GROUP_MESSAGE_WITH_GROUPID);
    
    /**
     * 特殊数据库表操作URI：查询指定类型的多媒体未读的群组消息
     */
    public static final Uri GROUPMESSGAE_UNREAD_SPECIAL_MEDIA_URI = Uri.parse("content://"
            + AUTHORITY
            + "/"
            + QueryCondition.GROUPMESSGAE_UNREAD_SPECIAL_MEDIA);
    
    /**
     * 特殊数据库表操作URI: 查询单人通话通知栏信息URI
     */
    public static final Uri NOTIFICATION_IM_SINGLE_MESSAGE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.NOTIFICATION_IM_SINGLE);
    
    /**
     * 特殊数据库表操作URI: 分页查询会话表信息URI
     */
    public static final Uri CONVERSATION_FOR_PAGE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.CONVERSATION_FOR_PAGE);
    
    /**
     * 特殊数据库表操作URI：找朋友小助手URI
     */
    public static final Uri CONVERSATION_FRIEND_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.CONVERSATION_FRIEND);
    
    /*================================特殊数据表操作URI定义结束========================================= */
    /**
     * 我的应用 URI
     */
    public static final Uri MY_APP_URI = Uri.parse("content://" + AUTHORITY
            + "/" + Tables.MY_APP);
    
    /**
     * 查询所有"我的应用" URI, 格式如： <br>
     * .../myApp_all/[userSysId]
     */
    public static final Uri MY_APP_ALL_URI = Uri.parse("content://" + AUTHORITY
            + "/" + QueryCondition.MY_APP_ALL);
    
    /**
     * 根据appId查询系统应用 URI <br>
     * 格式： .../sysApp_by_appId/[userSysId]/[appId]/[type]
     */
    public static final Uri SYSAPPINFO_APPID_URI = Uri.parse("content://"
            + AUTHORITY + "/" + QueryCondition.SYSAPPINFO_APPID);
    
    /**
     *  注册时，读取短信验证码 短信的URI
     */
    public static final Uri SMS_URI = Uri.parse("content://sms/");
}
