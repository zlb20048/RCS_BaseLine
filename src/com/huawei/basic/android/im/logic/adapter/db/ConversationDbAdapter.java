/*
 * 文件名: ConversationDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qinyangwang
 * 创建时间:2012-2-22
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ConversationColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupMessageColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MessageColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.notification.bean.IMNotificationBean;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 消息会话数据操作适配器<BR>
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-22]
 */
public class ConversationDbAdapter
{
    private static final String TAG = "ConversationDbAdapter";
    
    /**
     * ConversationDbAdapter对象
     */
    private static ConversationDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 消息adapter
     */
    private MessageDbAdapter mMessageAdapter;
    
    /**
     * 群组信息adapter
     */
    private GroupMessageDbAdapter mGroupMessageAdapter;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private ConversationDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
        mMessageAdapter = MessageDbAdapter.getInstance(context);
        mGroupMessageAdapter = GroupMessageDbAdapter.getInstance(context);
    }
    
    /**
     * 获取ConversationDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return ConversationDbAdapter
     */
    public static synchronized ConversationDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new ConversationDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 清除历史聊天数据<BR>
     * 
     * @param userSysId 用户系统标识
     */
    public void clearAllData(String userSysId)
    {
        try
        {
            if (StringUtil.isNullOrEmpty(userSysId))
            {
                return;
            }
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation operation = null;
            
            // conversation
            operation = ContentProviderOperation.newDelete(URIField.CONVERSATION_URI)
                    .withSelection(ConversationColumns.USER_SYSID + "=?",
                            new String[] { userSysId })
                    .build();
            operations.add(operation);
            
            // 1v1
            operation = ContentProviderOperation.newDelete(URIField.MESSAGE_URI)
                    .withSelection(MessageColumns.USER_SYSID + "=?",
                            new String[] { userSysId })
                    .build();
            operations.add(operation);
            
            // group
            operation = ContentProviderOperation.newDelete(URIField.GROUPMESSAGE_URI)
                    .withSelection(GroupMessageColumns.USER_SYSID + "=?",
                            new String[] { userSysId })
                    .build();
            operations.add(operation);
            
            // 执行
            mCr.applyBatch(URIField.AUTHORITY, operations);
            
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
    }
    
    /**
     * 插入会话<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param info 会话对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertConversation(String userSysId, ConversationModel info)
    {
        Logger.i(TAG, "insertConversation -----------> 插入会话 ");
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != info)
        {
            Uri uri = URIField.CONVERSATION_URI;
            ContentValues cv = setValues(userSysId, info);
            
            Uri resultUri = mCr.insert(uri, cv);
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
                Logger.i(TAG, "insertConversation, result = " + result);
            }
        }
        else
        {
            Logger.w(TAG, "insertConversation fail, info is null...");
        }
        return result;
    }
    
    /**
     * 根据会话ID删除会话<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param conversationId 会话ID
     * @param conversationType 会话的类型
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByConversationId(String userSysId, String conversationId,
            int conversationType)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(conversationId))
        {
            
            Uri uri = URIField.CONVERSATION_URI;
            //TODO: 删除会话表不需要传递类型  （CONVERSATIONID AND USER_SYSID） 是唯一的
            result = mCr.delete(uri,
                    ConversationColumns.CONVERSATIONID + "=? AND "
                            + ConversationColumns.USER_SYSID + " =? ",
                    new String[] { conversationId, userSysId });
            
            // 再删除消息表中会话所对应的消息
            if (result > 0)
            {
                if (conversationType == ConversationModel.CONVERSATIONTYPE_1V1
                        || conversationType == ConversationModel.CONVERSATIONTYPE_SECRET)
                {
                    mMessageAdapter.deleteByFriendUserId(userSysId,
                            conversationId);
                }
                else if (conversationType == ConversationModel.CONVERSATIONTYPE_GROUP)
                {
                    mGroupMessageAdapter.deleteByConversationId(userSysId,
                            conversationId);
                }
            }
            
            Logger.i(TAG, "deleteByConversationId, result = " + result);
        }
        else
        {
            Logger.w(TAG,
                    "deleteByConversationId fail, conversationId is null...");
        }
        return result;
    }
    
    /**
     * 根据消息ID删除会话<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param msgId 消息ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByMsgId(String userSysId, String msgId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(msgId))
        {
            Uri uri = URIField.CONVERSATION_URI;
            
            // 先判断是否有多媒体，如果有则先删除多媒体
            // 这里不删多媒体，因为该方法主要用于删除"群发"内容，
            //而"群发"内容无法区分是分享的还是编辑的，
            //如果是分享内容删了多媒体的话，会导致源内容无效
            // this.mediaAdapter.deleteByMsgId(msgId);
            
            result = mCr.delete(uri,
                    ConversationColumns.LAST_MSG_ID + "=? AND "
                            + ConversationColumns.USER_SYSID + "=?",
                    new String[] { msgId, userSysId });
            
            Logger.i(TAG, "deleteByMsgId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "deleteByMsgId fail, conversationId is null...");
        }
        return result;
    }
    
    /**
     * 
     * 根据userSysId删除类型为小助手的会话记录<BR>
     * @param userSysId 用户系统ID
     * @return 删除的条数
     */
    public int deleteFriendManagerInfoByUserSysId(String userSysId)
    {
        int result = -1;
        if (StringUtil.isNullOrEmpty(userSysId))
        {
            return result;
        }
        Uri uri = URIField.CONVERSATION_URI;
        String selection = new StringBuilder().append(ConversationColumns.USER_SYSID)
                .append(" =? AND ")
                .append(ConversationColumns.CONVERSATIONTYPE)
                .append(" = ")
                .append(ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER)
                .toString();
        result = mCr.delete(uri, selection, new String[] { userSysId });
        return result;
    }
    
    /**
     * 根据会话ID修改会话信息部分字段<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param conversationId 会话ID
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByConversationId(String userSysId, String conversationId,
            ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(conversationId) && null != cv
                && 0 < cv.size())
        {
            Uri uri = URIField.CONVERSATION_URI;
            result = mCr.update(uri,
                    cv,
                    ConversationColumns.CONVERSATIONID + "=? AND "
                            + ConversationColumns.USER_SYSID + "=?",
                    new String[] { conversationId, userSysId });
            Logger.i(TAG, "updateByConversation, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateByConversation fail, params is null...");
        }
        return result;
    }
    
    /**
     * 根据 msgId 修改会话信息.<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param msgId 消息ID
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByMsgId(String userSysId, String msgId, ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(msgId) && null != cv
                && 0 < cv.size())
        {
            Uri uri = URIField.CONVERSATION_URI;
            result = mCr.update(uri,
                    cv,
                    ConversationColumns.LAST_MSG_ID + "=? AND "
                            + ConversationColumns.USER_SYSID + "=?",
                    new String[] { msgId, userSysId });
            Logger.i(TAG, "updateByConversation, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateByConversation fail, params is null...");
        }
        return result;
    }
    
    /**
     * 根据 msgSequence 修改会话信息.<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param msgSequence 消息sequence
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByMsgSequence(String userSysId, String msgSequence,
            ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(msgSequence) && null != cv
                && 0 < cv.size())
        {
            Uri uri = URIField.CONVERSATION_URI;
            result = mCr.update(uri,
                    cv,
                    ConversationColumns.LAST_MSG_SEQUENCE + "=? AND "
                            + ConversationColumns.USER_SYSID + "=?",
                    new String[] { msgSequence, userSysId });
            Logger.i(TAG, "updateByConversation, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateByConversation fail, params is null...");
        }
        return result;
    }
    
    /**
     * 根据userId更新会话表中的小助手记录
     * @param userSysId 用户系统ID
     * @param cv 需要改变的值
     * @return 更改的条数
     */
    public int updateFriendManagerRecordInConversation(String userSysId,
            ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.CONVERSATION_URI;
            String selection = new StringBuilder().append(ConversationColumns.USER_SYSID)
                    .append(" =? and ")
                    .append(ConversationColumns.CONVERSATIONTYPE)
                    .append(" =? ")
                    .toString();
            String[] selectionArgs = new String[] { userSysId,
                    ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER + "" };
            result = mCr.update(uri, cv, selection, selectionArgs);
        }
        return result;
    }
    
    /**
     * 将小助手信息在会话表中的未读消息数清零<BR>
     * 通知栏要监听这个特殊URI
     * @param userSysId 用户系统ID
     * @return 操作成功的条数
     */
    public int clearFriendUnreadMsgInConver(String userSysId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.CONVERSATION_FRIEND_URI;
            String selection = new StringBuilder().append(ConversationColumns.USER_SYSID)
                    .append(" =? and ")
                    .append(ConversationColumns.CONVERSATIONTYPE)
                    .append(" =? ")
                    .toString();
            String[] selectionArgs = new String[] { userSysId,
                    ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER + "" };
            ContentValues contentValues = new ContentValues();
            contentValues.put(ConversationColumns.UNREAD_MSG, 0);
            result = mCr.update(uri, contentValues, selection, selectionArgs);
        }
        return result;
    }
    
    /**
     * 根据会话ID查询会话信息<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param conversationId 会话ID
     * @param conversationType 会话类型
     * @return 成功：ConversationModel对象<br>
     *         失败：null
     */
    public ConversationModel queryByConversationId(String userSysId,
            String conversationId, int conversationType)
    {
        ConversationModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByConversationIdWithCursor(userSysId,
                    conversationId,
                    conversationType);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToConversationModel(cursor);
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
     * 根据会话ID查询会话信息<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param conversationId 会话ID
     * @param conversationType 会话类型
     * @return 成功：Cursor对象<br>
     *         失败：null
     */
    public Cursor queryByConversationIdWithCursor(String userSysId,
            String conversationId, int conversationType)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(conversationId))
        {
            Uri uri = URIField.CONVERSATION_URI;
            cursor = mCr.query(uri,
                    null,
                    ConversationColumns.CONVERSATIONID + "=? AND "
                            + ConversationColumns.USER_SYSID + "=? AND "
                            + ConversationColumns.CONVERSATIONTYPE + " =? ",
                    new String[] { conversationId, userSysId,
                            String.valueOf(conversationType) },
                    null);
        }
        return cursor;
    }
    
    /**
     * 查询所有会话<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @return 成功：会话列表 <br>
     *         失败：null
     */
    public List<ConversationModel> queryAllConversation(String userSysId)
    {
        ArrayList<ConversationModel> list = null;
        Cursor cursor = null;
        try
        {
            ConversationModel info = null;
            
            cursor = queryAllConversationWithCursor(userSysId);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<ConversationModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToConversationModel(cursor);
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
     * 分页查询会话表信息<BR>
     * @param userSysId 用户系统ID
     * @param queryCount 查询的个数
     * @return 会话表信息集合
     */
    public List<ConversationModel> queryAllConversationForPage(
            String userSysId, int queryCount)
    {
        ArrayList<ConversationModel> list = null;
        Cursor cursor = null;
        try
        {
            ConversationModel info = null;
            
            cursor = queryAllConversationWithCursorForPage(userSysId,
                    queryCount);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<ConversationModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToConversationModel(cursor);
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
     * 查询所有会话<BR>
     * 过滤掉"群发"
     * 
     * @param userSysId 用户在系统的唯一标识
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryAllConversationWithCursor(String userSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = URIField.CONVERSATION_URI;
            cursor = mCr.query(uri,
                    null,
                    ConversationColumns.USER_SYSID + "=? AND "
                            + ConversationColumns.CONVERSATIONTYPE + "<> ?",
                    new String[] {
                            userSysId,
                            String.valueOf(ConversationModel.CONVERSATIONTYPE_1VN) },
                    ConversationColumns.LASTTIME + " DESC");
        }
        return cursor;
    }
    
    /**
     * 分页查询会话表信息<BR>
     * @param userSysId 用户系统ID
     * @param queryCount 查询的个数
     * @return 会话表信息结果集
     */
    public Cursor queryAllConversationWithCursorForPage(String userSysId,
            int queryCount)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = Uri.withAppendedPath(URIField.CONVERSATION_FOR_PAGE_URI,
                    userSysId + "/" + queryCount);
            cursor = mCr.query(uri,
                    null,
                    null,
                    null,
                    ConversationColumns.LASTTIME + " DESC");
        }
        return cursor;
    }
    
    /**
     * 
     * 根据msgId查询会话信息<BR>
     * 如果可以根据msgId查询到会话记录，说明该记录为最新记录，
     * 对该msgId进行的操作需要更新会话表
     * @param msgId 消息ID
     * @return 会话表中的msgId
     */
    public boolean isLastRecordInConversation(String msgId)
    {
        boolean isExist = false;
        if (StringUtil.isNullOrEmpty(msgId))
        {
            return isExist;
        }
        Uri uri = URIField.CONVERSATION_URI;
        String[] projection = new String[] { ConversationColumns.LAST_MSG_ID,
                ConversationColumns.CONVERSATIONID };
        String selection = ConversationColumns.LAST_MSG_ID + " =? ";
        String[] selectionArgs = new String[] { msgId };
        Cursor cursor = mCr.query(uri,
                projection,
                selection,
                selectionArgs,
                null);
        if (cursor != null && cursor.moveToFirst())
        {
            isExist = true;
        }
        return isExist;
    }
    
    /**
     * 判断会话表中是否存在当前登录用户的小助手信息记录
     * @param userSysId 当前登录用户ID
     * @return 是否存在记录
     */
    public boolean isFriendHelperRecordExists(String userSysId)
    {
        ConversationModel model = null;
        boolean isExist = false;
        model = queryFriendManagerInConverByUserSysId(userSysId);
        if (model != null)
        {
            isExist = true;
        }
        return isExist;
    }
    
    /**
     * 
     * 根据userSysId查询小助手在会话表中的信息<BR>
     * @param userSysId 用户系统ID
     * @return ConversationModel对象
     */
    public ConversationModel queryFriendManagerInConverByUserSysId(
            String userSysId)
    {
        ConversationModel model = null;
        Cursor cursor = null;
        if (StringUtil.isNullOrEmpty(userSysId))
        {
            return model;
        }
        try
        {
            String selection = new StringBuilder().append(ConversationColumns.USER_SYSID)
                    .append(" =? AND ")
                    .append(ConversationColumns.CONVERSATIONTYPE)
                    .append(" =? ")
                    .toString();
            String[] selectionArgs = new String[] { userSysId,
                    ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER + "" };
            cursor = this.mCr.query(URIField.CONVERSATION_URI,
                    null,
                    selection,
                    selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst())
            {
                model = parseCursorToConversationModel(cursor);
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
        return model;
    }
    
    /**
     * 查询单人消息通知栏信息<BR>
     * @param userSysId 用户系统ID
     * @param currentFriendUserId 用户系统ID
     * @return 单人通知栏信息集合
     */
    public HashMap<String, IMNotificationBean> getImSingleNotification(
            String userSysId, String currentFriendUserId)
    {
        HashMap<String, IMNotificationBean> map = null;
        Cursor cursor = null;
        if (StringUtil.isNullOrEmpty(userSysId))
        {
            return map;
        }
        if (StringUtil.isNullOrEmpty(currentFriendUserId))
        {
            currentFriendUserId = "-10000";
        }
        try
        {
            Uri uri = Uri.withAppendedPath(URIField.NOTIFICATION_IM_SINGLE_MESSAGE_URI,
                    currentFriendUserId + "/" + userSysId);
            cursor = mCr.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst())
            {
                map = new HashMap<String, IMNotificationBean>();
                while (!cursor.isAfterLast())
                {
                    IMNotificationBean bean = this.parseCursorToIMNotification(cursor);
                    map.put(bean.getSessionId(), bean);
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
        return map;
    }
    
    /**
     * 
     * 根据userSysID查询当前登录用户未读消息总数<BR>
     * 
     * @param userSysId 用户系统ID
     * @return 未读消息总数
     */
    public int queryAllUnreadMsgNum(String userSysId)
    {
        
        int result = 0;
        Cursor cursor = null;
        try
        {
            if (StringUtil.isNullOrEmpty(userSysId))
            {
                return result;
            }
            String[] projection = new String[] { " SUM("
                    + ConversationColumns.UNREAD_MSG + ")" };
            String selection = ConversationColumns.USER_SYSID + " =? ";
            String[] selectionArgs = new String[] { userSysId };
            Uri uri = URIField.CONVERSATION_URI;
            cursor = mCr.query(uri, projection, selection, selectionArgs, null);
            if (null != cursor && cursor.moveToFirst())
            {
                result = cursor.getInt(0);
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
        return result;
    }
    
    /**
     * 查询某会话中未读消息数量<BR>
     * @param userSysId 用户系统ID
     * @param friendUserId 用户好友ID
     * @return 会话中未读消息数量
     */
    public int queryUnreadMsgNum(String userSysId, String friendUserId)
    {
        int result = 0;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                Uri uri = URIField.CONVERSATION_URI;
                String[] projection = new String[] { ConversationColumns.UNREAD_MSG };
                String selection = ConversationColumns.USER_SYSID + " =? AND "
                        + ConversationColumns.CONVERSATIONID + " =? ";
                String[] selectionArgs = new String[] { userSysId, friendUserId };
                cursor = mCr.query(uri,
                        projection,
                        selection,
                        selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst())
                {
                    result = cursor.getInt(0);
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
        return result;
    }
    
    /**
     * 
     * 查询找朋友小助手未读消息数<BR>
     * @param userSysId userSysId
     * @return 未读消息数
     */
    public int queryFriendManagerUnreadCount(String userSysId)
    {
        int result = 0;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId))
            {
                Uri uri = URIField.CONVERSATION_URI;
                StringBuffer sb = new StringBuffer();
                sb.append(ConversationColumns.CONVERSATIONTYPE)
                        .append(" = ")
                        .append(ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER)
                        .append(" AND ")
                        .append(ConversationColumns.USER_SYSID)
                        .append(" = ?");
                cursor = mCr.query(uri,
                        new String[] { ConversationColumns.UNREAD_MSG },
                        sb.toString(),
                        new String[] { userSysId },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    result = cursor.getInt(0);
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
        return result;
    }
    
    /**
     * 转化model为ContentValues
     * 
     * @param userSysId userSysId
     * @param info ConversationModel
     * @return ContentValues
     */
    private ContentValues setValues(String userSysId, ConversationModel info)
    {
        ContentValues cv = new ContentValues();
        cv.put(ConversationColumns.USER_SYSID, userSysId);
        cv.put(ConversationColumns.CONVERSATIONID, info.getConversationId());
        cv.put(ConversationColumns.UNREAD_MSG, info.getUnReadmsg());
        cv.put(ConversationColumns.CONVERSATIONTYPE, info.getConversationType());
        
        cv.put(ConversationColumns.GROUPID, info.getGroupId());
        String timeStamp = info.getLastTime();
        if (StringUtil.isNullOrEmpty(timeStamp))
        {
            timeStamp = DateUtil.getCurrentDateString();
        }
        cv.put(ConversationColumns.LASTTIME, timeStamp);
        cv.put(ConversationColumns.LAST_MSG_ID, info.getLastMsgId());
        cv.put(ConversationColumns.LAST_MSG_SEQUENCE, info.getLastMsgSequence());
        if (0 != info.getLastMsgType())
        {
            cv.put(ConversationColumns.LAST_MSG_TYPE, info.getLastMsgType());
        }
        
        cv.put(ConversationColumns.LAST_MSG_CONTENT, info.getLastMsgContent());
        if (0 != info.getLastMsgStatus())
        {
            cv.put(ConversationColumns.LAST_MSG_STATUS, info.getLastMsgStatus());
        }
        return cv;
    }
    
    /**
     * 根据游标解析会话消息<BR>
     * 
     * @param cursor 游标对象
     * @return 会话对象
     */
    private ConversationModel parseCursorToConversationModel(Cursor cursor)
    {
        ConversationModel info = new ConversationModel();
        info.setConversationId(cursor.getString(cursor.getColumnIndex(ConversationColumns.CONVERSATIONID)));
        info.setConversationType(cursor.getInt(cursor.getColumnIndex(ConversationColumns.CONVERSATIONTYPE)));
        info.setGroupId(cursor.getString(cursor.getColumnIndex(ConversationColumns.GROUPID)));
        info.setLastMsgId(cursor.getString(cursor.getColumnIndex(ConversationColumns.LAST_MSG_ID)));
        info.setLastMsgSequence(cursor.getString(cursor.getColumnIndex(ConversationColumns.LAST_MSG_SEQUENCE)));
        info.setLastMsgType(cursor.getInt(cursor.getColumnIndex(ConversationColumns.LAST_MSG_TYPE)));
        info.setLastMsgContent(cursor.getString(cursor.getColumnIndex(ConversationColumns.LAST_MSG_CONTENT)));
        info.setLastTime(cursor.getString(cursor.getColumnIndex(ConversationColumns.LASTTIME)));
        info.setUnReadmsg(cursor.getInt(cursor.getColumnIndex(ConversationColumns.UNREAD_MSG)));
        info.setLastMsgStatus(cursor.getInt(cursor.getColumnIndex(ConversationColumns.LAST_MSG_STATUS)));
        info.setReceiversName(StringUtil.parseStringToList(cursor.getString(cursor.getColumnIndex(ConversationColumns.RECEIVERS_NAME)),
                null));
        return info;
    }
    
    private IMNotificationBean parseCursorToIMNotification(Cursor cursor)
    {
        if (cursor == null)
        {
            return null;
        }
        IMNotificationBean bean = new IMNotificationBean();
        bean.setMediaType(cursor.getInt(cursor.getColumnIndex(MediaIndexColumns.MEDIA_TYPE)));
        bean.setMsgContent(cursor.getString(cursor.getColumnIndex(ConversationColumns.LAST_MSG_CONTENT)));
        bean.setMsgType(cursor.getInt(cursor.getColumnIndex(ConversationColumns.LAST_MSG_TYPE)));
        bean.setNickName(cursor.getString(cursor.getColumnIndex(ContactInfoColumns.NICK_NAME)));
        bean.setSessionId(cursor.getString(cursor.getColumnIndex(ConversationColumns.CONVERSATIONID)));
        bean.setUnreadMsgCount(cursor.getInt(cursor.getColumnIndex("sumUnreadMsg")));
        return bean;
    }
    
}