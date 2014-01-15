/*
 * 文件名: GroupMessageDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 28, 2012
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
import com.huawei.basic.android.im.component.database.DatabaseHelper.ConversationColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupMessageColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MessageColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 群组消息数据操作适配器<BR>
 * 
 * @author 张仙
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 28, 2012] 
 */
public class GroupMessageDbAdapter
{
    
    /**
     * TAG
     */
    private static final String TAG = "GroupMessageDbAdapter";
    
    /**
     * GroupMessageDbAdapter对象
     */
    private static GroupMessageDbAdapter sInstance;
    
    /**
     * 会话表操作Adapter
     */
    private ConversationDbAdapter conversationAdapter;
    
    /**
     * 多媒体索引表操作Adapter
     */
    private MediaIndexDbAdapter mediaIndexAdapter;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mContentResolver;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private GroupMessageDbAdapter(Context context)
    {
        mContentResolver = context.getContentResolver();
    }
    
    /**
     * 获取GroupMessageAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return GroupMessageAdapter
     */
    public static synchronized GroupMessageDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new GroupMessageDbAdapter(context);
            sInstance.conversationAdapter = ConversationDbAdapter.getInstance(context);
            sInstance.mediaIndexAdapter = MediaIndexDbAdapter.getInstance(context);
        }
        return sInstance;
    }
    
    /**
     * 插入群组消息<BR>
     * 插入群组消息时，如果没有对应的会话，则会先建立会话。
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param conversationType 会话类型
     * @param msg 群组消息对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertGroupMsg(String userSysId, int conversationType,
            GroupMessageModel msg)
    {
        long result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId) && null != msg)
            {
                String groupId = msg.getGroupId();
                if (StringUtil.isNullOrEmpty(groupId))
                {
                    return result;
                }
                //分为发送和接收
                int sendOrRecv = msg.getMsgSendOrRecv();
                msg.setUserSysId(userSysId);
                result = doInsert(conversationType, sendOrRecv, msg);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 删除群组中的某条消息<BR>
     * 如果是多媒体类型的，会同时删除多媒体信息。
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param msgId 群组消息ID
     * @param msgType 消息类型
     * @param groupId 群组ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public synchronized int deleteByMsgId(String userSysId, String msgId,
            int msgType, String groupId)
    {
        int result = -1;
        Cursor lastMsgCursor = null;
        Cursor newLastMsgCursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(msgId) && msgType != 0)
            {
                Uri messageUri = Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                        groupId);
                GroupMessageModel oldMsg = this.queryByMsgId(userSysId, msgId);
                if (null == oldMsg)
                {
                    return -1;
                }
                // 先查询一下，看消息是否是最后一条，如果是最后一条，则还要更新会话表
                GroupMessageModel lastMsg = null;
                lastMsgCursor = this.queryLastMsgByGroupIdWithCursor(userSysId,
                        groupId);
                if (null != lastMsgCursor && lastMsgCursor.moveToFirst())
                {
                    lastMsg = this.parseCursorToGroupMessageModel(lastMsgCursor);
                }
                
                // 如果是多媒体类型的，则先删除多媒体文件。
                if (MessageModel.MSGTYPE_MEDIA == msgType)
                {
                    mediaIndexAdapter.deleteByMsgId(msgId);
                }
                
                result = mContentResolver.delete(messageUri,
                        GroupMessageColumns.USER_SYSID + "=? AND "
                                + GroupMessageColumns.MSG_ID + "=?",
                        new String[] { userSysId, msgId });
                
                // 如果删除的消息是 会话中的最后一条，则更新会话表中的最后一条消息记录
                if (null != lastMsg && msgId.equals(lastMsg.getMsgId()))
                {
                    newLastMsgCursor = queryLastMsgByGroupIdWithCursor(userSysId,
                            groupId);
                    if (null != newLastMsgCursor
                            && newLastMsgCursor.moveToFirst())
                    {
                        // 更新会话表
                        GroupMessageModel newLastMsg = parseCursorToGroupMessageModel(newLastMsgCursor);
                        ContentValues params = new ContentValues();
                        params.put(ConversationColumns.LAST_MSG_ID,
                                newLastMsg.getMsgId());
                        params.put(ConversationColumns.LAST_MSG_TYPE,
                                newLastMsg.getMsgType());
                        params.put(ConversationColumns.LAST_MSG_CONTENT,
                                newLastMsg.getMsgContent());
                        params.put(ConversationColumns.LAST_MSG_STATUS,
                                newLastMsg.getMsgStatus());
                    }
                    else
                    {
                        conversationAdapter.deleteByConversationId(userSysId,
                                groupId,
                                ConversationModel.CONVERSATIONTYPE_GROUP);
                    }
                }
                int unreadMsg = getUnreadMsgCount(userSysId, groupId);
                if (-1 != unreadMsg)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(ConversationColumns.UNREAD_MSG, unreadMsg);
                    conversationAdapter.updateByConversationId(userSysId,
                            groupId,
                            cv);
                }
                
                lastMsgCursor.close();
                
                Logger.d(TAG, "deleteByMsgId, result = " + result);
            }
            else
            {
                Logger.w(TAG, "deleteByMsgId fail, msgId is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(lastMsgCursor);
            DatabaseHelper.closeCursor(newLastMsgCursor);
        }
        
        return result;
    }
    
    /**
     * 根据会话ID删除群信息<BR>
     * @param sysId 当前用户的唯一标示符
     * @param conversationId conversationId
     * @return 删除的条数
     */
    public int deleteByConversationId(String sysId, String conversationId)
    {
        int result = -1;
        if (StringUtil.isNullOrEmpty(sysId)
                || StringUtil.isNullOrEmpty(conversationId))
        {
            return result;
        }
        String where = new StringBuffer().append(GroupMessageColumns.USER_SYSID)
                .append(" = ?")
                .append(" AND ")
                .append(GroupMessageColumns.GROUP_ID)
                .append(" = ?")
                .toString();
        result = mContentResolver.delete(Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                conversationId),
                where,
                new String[] { sysId, conversationId });
        if (result > 0)
        {
            //清空记录的时候将会话消息中的记录设为默认状态，在会话列表不显示消息内容和状态
            ContentValues contentValues = new ContentValues();
            contentValues.put(ConversationColumns.LAST_MSG_ID, "");
            contentValues.put(ConversationColumns.LAST_MSG_TYPE, 1);
            contentValues.put(ConversationColumns.LAST_MSG_CONTENT, "");
            //将状态设为数据库中没有的状态,会话列表界面将不显示状态
            contentValues.put(ConversationColumns.LAST_MSG_STATUS, -1);
            //将未读消息数设置为0
            contentValues.put(ConversationColumns.UNREAD_MSG, 0);
            this.conversationAdapter.updateByConversationId(sysId,
                    conversationId,
                    contentValues);
        }
        return result;
    }
    
    /**
     * 根据会话ID删除群信息以及群信息会话记录<BR>
     * @param sysId 当前用户的唯一标示符
     * @param conversationId conversationId
     * @return 删除的条数
     */
    public int deleteGroupMessageAndSationInfo(String sysId,
            String conversationId)
    {
        int result = -1;
        if (StringUtil.isNullOrEmpty(sysId)
                || StringUtil.isNullOrEmpty(conversationId))
        {
            return result;
        }
        String where = new StringBuffer().append(GroupMessageColumns.USER_SYSID)
                .append(" = ?")
                .append(" AND ")
                .append(GroupMessageColumns.GROUP_ID)
                .append(" = ?")
                .toString();
        result = mContentResolver.delete(Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                conversationId),
                where,
                new String[] { sysId, conversationId });
        this.conversationAdapter.deleteByConversationId(sysId,
                conversationId,
                ConversationModel.CONVERSATIONTYPE_GROUP);
        return result;
    }
    
    /**
     * 根据msgId查询消息<BR>
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param msgId 消息ID
     * @return 成功：GroupMessageModel对象 <br>
     *         失败：null
     */
    public GroupMessageModel queryByMsgId(String userSysId, String msgId)
    {
        GroupMessageModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByMsgIdWithCursor(userSysId, msgId);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToGroupMessageModel(cursor);
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
     * 根据groupId查询所有群消息记录
     * @param userSysId 用户ID
     * @param groupId 群组ID
     * @return 群消息记录的cursor
     */
    public Cursor queryAllMessageByGroupId(String userSysId, String groupId)
    {
        return queryForPageWithCursor(userSysId, groupId, 1, -1);
    }
    
    /**
     * 根据groupId分页查询消息记录，关联多媒体。<BR>
     * 
     * @param userSysId 用户系统标识
     * @param groupId 群组ID
     * @param currentPage 当前页
     * @param perPageCount 每页显示的记录数。为 -1 时，表示不分页，查询全部记录；
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryForPageWithCursor(String userSysId, String groupId,
            int currentPage, int perPageCount)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                // .../groupMessage_page/userSysId/[groupId]/[currentPage|perPageCount]
                Uri uri = Uri.withAppendedPath(URIField.GROUPMESSAGE_PAGE_URI,
                        userSysId + "/" + groupId + "/" + currentPage + "|"
                                + perPageCount);
                cursor = mContentResolver.query(uri, null, null, null, null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 根据msgId查询消息<BR>
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param msgId 消息ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByMsgIdWithCursor(String userSysId, String msgId)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(msgId))
            {
                Uri uri = URIField.GROUPMESSAGE_URI;
                String where = new StringBuffer().append(GroupMessageColumns.USER_SYSID)
                        .append("=? AND ")
                        .append(GroupMessageColumns.MSG_ID)
                        .append("=?")
                        .toString();
                cursor = mContentResolver.query(uri, null, where, new String[] {
                        userSysId, msgId }, null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 根据msgId修改消息<BR>
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param groupId 群组ID
     * @param msgId 消息ID
     * @param contentValues 需要修改的参数
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByMsgId(String userSysId, String groupId, String msgId,
            ContentValues contentValues)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId)
                    && !StringUtil.isNullOrEmpty(msgId)
                    && contentValues != null && contentValues.size() > 0)
            {
                Uri uri = Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                        groupId);
                
                result = this.mContentResolver.update(uri,
                        contentValues,
                        GroupMessageColumns.USER_SYSID + "=? AND "
                                + GroupMessageColumns.MSG_ID + "=?",
                        new String[] { userSysId, msgId });
                
                Logger.d(TAG, "updateByMsgId, result = " + result);
            }
            else
            {
                Logger.d(TAG, "updateByMsgId fail, msgId or params is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 更新群组消息记录<BR>
     * @param model GroupMessageModel 对象
     * @return 更新的条数
     */
    public int updateGroupMessageRecord(GroupMessageModel model)
    {
        
        int result = -1;
        Cursor lastMsgCursor = null;
        if (null == model || StringUtil.isNullOrEmpty(model.getMsgId())
                || StringUtil.isNullOrEmpty(model.getGroupId()))
        {
            return result;
        }
        try
        {
            ContentValues values = parseModelToContentValues(model);
            Uri uri = Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                    model.getGroupId());
            result = this.mContentResolver.update(uri,
                    values,
                    GroupMessageColumns.MSG_ID + " =? ",
                    new String[] { model.getMsgId() });
            lastMsgCursor = this.queryLastMsgByGroupIdWithCursor(model.getUserSysId(),
                    model.getGroupId());
            if (null != lastMsgCursor && lastMsgCursor.moveToFirst())
            {
                GroupMessageModel lastMsg = this.parseCursorToGroupMessageModel(lastMsgCursor);
                if (result > 0
                        && StringUtil.equals(model.getMsgId(),
                                lastMsg.getMsgId()))
                {
                    ContentValues cv = new ContentValues();
                    cv.put(ConversationColumns.LAST_MSG_SEQUENCE,
                            model.getMsgSequence());
                    cv.put(ConversationColumns.LAST_MSG_STATUS,
                            model.getMsgStatus());
                    this.conversationAdapter.updateByConversationId(model.getUserSysId(),
                            model.getGroupId(),
                            cv);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(lastMsgCursor);
        }
        return result;
    }
    
    /**
     * 根据msgSequence修改消息<BR>
     * [通用方法]
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param groupId 群组ID
     * @param msgSequence 消息序列
     * @param state 需要改变的状态值
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByMsgSequence(String userSysId, String groupId,
            String msgSequence, int state)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId)
                    && !StringUtil.isNullOrEmpty(msgSequence))
            {
                Uri uri = Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                        groupId);
                ContentValues cv = new ContentValues();
                cv.put(MessageColumns.MSG_STATUS, state);
                //更新多人消息表
                result = this.mContentResolver.update(uri,
                        cv,
                        GroupMessageColumns.USER_SYSID + "=? AND "
                                + GroupMessageColumns.MSG_SEQUENCE + "=? ",
                        new String[] { userSysId, msgSequence });
                //获得会话表中此多人会话记录
                ConversationModel model = conversationAdapter.queryByConversationId(userSysId,
                        groupId,
                        ConversationModel.CONVERSATIONTYPE_GROUP);
                //如果会话表中存在此多人会话记录，那么更新会话表状态
                if (model != null)
                {
                    
                    cv.clear();
                    cv.put(ConversationColumns.LAST_MSG_STATUS, state);
                    if (state == GroupMessageModel.MSGSTATUS_READED
                            || state == GroupMessageModel.MSGSTATUS_READED_NO_REPORT
                            || state == GroupMessageModel.MSGSTATUS_READED_NEED_REPORT)
                    {
                        cv.put(ConversationColumns.UNREAD_MSG,
                                model.getUnReadmsg() > 0 ? model.getUnReadmsg()
                                        - result : model.getUnReadmsg());
                    }
                    conversationAdapter.updateByConversationId(userSysId,
                            groupId,
                            cv);
                }
                Logger.d(TAG, "updateByMsgSequence, result = " + result);
            }
            else
            {
                Logger.d(TAG,
                        "updateByMsgSequence fail, msgSequence or params is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 更改所有消息状态为已读<BR>
     * @param userSysId 用戶系統ID
     * @param groupId 群组ID
     * @return 改变的条数
     */
    public int changeAllMsgToReaded(String userSysId, String groupId)
    {
        int count = 0;
        ConversationModel model = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                
                Uri uri = Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                        groupId);
                String ids = mediaIndexAdapter.getAllAudioAndVideoIds();
                ContentValues contentValues = new ContentValues();
                // 条件：接收 状态为未读不需要报告
                String whereClauseNoReport = GroupMessageColumns.MSG_SENDORRECV
                        + " =? AND " + GroupMessageColumns.MSG_STATUS
                        + " =? AND " + GroupMessageColumns.USER_SYSID + " =? ";
                if (!StringUtil.isNullOrEmpty(ids))
                {
                    whereClauseNoReport += " AND " + GroupMessageColumns.MSG_ID
                            + " NOT IN  " + ids;
                }
                // 参数：接收 状态为未读不需要报告
                String[] argsNoReport = new String[] {
                        "" + GroupMessageModel.MSGSENDORRECV_RECV,
                        "" + GroupMessageModel.MSGSTATUS_UNREAD_NO_REPORT,
                        userSysId };
                contentValues.put(GroupMessageColumns.MSG_STATUS,
                        GroupMessageModel.MSGSTATUS_READED_NO_REPORT);
                count = this.mContentResolver.update(uri,
                        contentValues,
                        whereClauseNoReport,
                        argsNoReport);
                
                ContentValues cv = new ContentValues();
                //更新会话表中未读消息数
                int unreadMsg = this.getUnreadMsgCount(userSysId, groupId);
                cv.put(ConversationColumns.UNREAD_MSG, unreadMsg);
                //查询会话表中群组消息记录,如果msgStatus是已读,则不更新该字段，反之更新该字段为已读
                model = conversationAdapter.queryByConversationId(userSysId,
                        groupId,
                        ConversationModel.CONVERSATIONTYPE_GROUP);
                //如果会话表中有相应记录 
                if (model != null)
                {
                    //如果会话表中的记录信息为未读
                    if (model.getLastMsgStatus() == GroupMessageModel.MSGSTATUS_UNREAD_NO_REPORT)
                    {
                        cv.put(ConversationColumns.LAST_MSG_STATUS,
                                GroupMessageModel.MSGSTATUS_READED_NO_REPORT);
                    }
                    this.conversationAdapter.updateByConversationId(userSysId,
                            groupId,
                            cv);
                }
            }
            else
            {
                Logger.d(TAG, "changeAllMsgToReaded fail....");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return count;
    }
    
    /**
     * 
     * 获取指定群组的未读音频消息id集合<BR>
     * 
     * @param userSysId 用户系统id
     * @param groupId 群组id
     * @return 指定群组的未读音频消息id集合
     */
    public List<String> getUnreadAudioMsgIds(String userSysId, String groupId)
    {
        Cursor cursor = null;
        List<String> retList = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = URIField.GROUPMESSGAE_UNREAD_SPECIAL_MEDIA_URI;
                String[] selectionArgs = new String[] {
                        String.valueOf(MediaIndexModel.MEDIATYPE_AUDIO),
                        groupId };
                cursor = this.mContentResolver.query(uri,
                        null,
                        null,
                        selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst())
                {
                    retList = new ArrayList<String>();
                    while (!cursor.isAfterLast())
                    {
                        String id = cursor.getString(cursor.getColumnIndex(GroupMessageColumns.MSG_ID));
                        retList.add(id);
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
        return retList;
    }
    
    /**
     * 获得群组消息未读消息数<BR>
     * @param userSysId 用户系统ID
     * @param groupId 群组ID
     * @return 群组消息未读消息数
     */
    private int getUnreadMsgCount(String userSysId, String groupId)
    {
        int result = 0;
        Cursor cursor = null;
        try
        {
            Uri uri = URIField.GROUPMESSAGE_URI;
            String[] projection = new String[] { "COUNT(1)" };
            String selection = GroupMessageColumns.USER_SYSID + " =? AND "
                    + GroupMessageColumns.GROUP_ID + " =? AND "
                    + GroupMessageColumns.MSG_STATUS + " =? ";
            String[] selectionArgs = new String[] { userSysId, groupId,
                    "" + GroupMessageModel.MSGSTATUS_UNREAD_NO_REPORT };
            cursor = this.mContentResolver.query(uri,
                    projection,
                    selection,
                    selectionArgs,
                    null);
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
     * 
     * 具体执行插入操作代码<BR>
     * 具体执行插入操作
     * @param conversationType 会话表保存的消息类型
     * @param recOrSend 消息方向：发送或者接受
     * @param msg GroupMessageModel对象
     * @return 插入成功条数
     */
    private long doInsert(int conversationType, int recOrSend,
            GroupMessageModel msg)
    {
        long result = -1;
        String groupId = msg.getGroupId();
        String userSysId = msg.getUserSysId();
        Uri uri = Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                groupId);
        ContentValues cv = new ContentValues();
        cv.put(GroupMessageColumns.USER_SYSID, userSysId);
        cv.put(GroupMessageColumns.MSG_ID, msg.getMsgId());
        cv.put(GroupMessageColumns.MSG_SEQUENCE, msg.getMsgSequence());
        cv.put(GroupMessageColumns.GROUP_ID, msg.getGroupId());
        cv.put(GroupMessageColumns.MEMBER_USERID, msg.getMemberUserId());
        cv.put(GroupMessageColumns.MEMBER_NAME, msg.getMemberNick());
        cv.put(GroupMessageColumns.MSG_SENDORRECV, recOrSend);
        String timeStamp = msg.getMsgTime();
        if (StringUtil.isNullOrEmpty(timeStamp))
        {
            timeStamp = DateUtil.getCurrentDateString();
        }
        cv.put(GroupMessageColumns.MSG_TIME, timeStamp);
        cv.put(GroupMessageColumns.MSG_CONTENT, msg.getMsgContent());
        
        int msgType = msg.getMsgType();
        if (MessageModel.MSGTYPE_MEDIA == msgType)
        {
            // 关联插入多媒体
            MediaIndexModel mediaIndex = msg.getMediaIndex();
            if (null != mediaIndex)
            {
                mediaIndexAdapter.insertMediaIndex(mediaIndex);
            }
        }
        
        cv.put(GroupMessageColumns.MSG_TYPE, msgType);
        cv.put(GroupMessageColumns.MSG_STATUS, msg.getMsgStatus());
        
        Uri resultUri = mContentResolver.insert(uri, cv);
        if (null != resultUri)
        {
            result = ContentUris.parseId(resultUri);
        }
        //如果插入记录成功，则更新会话表相关记录
        if (-1 != result)
        {
            ConversationModel conversation = conversationAdapter.queryByConversationId(userSysId,
                    groupId,
                    ConversationModel.CONVERSATIONTYPE_GROUP);
            if (null == conversation)
            {
                // 如果会话不存在，则先建一条会话。
                ConversationModel cm = new ConversationModel();
                cm.setConversationId(groupId);
                cm.setConversationType(conversationType);
                timeStamp = msg.getMsgTime();
                if (StringUtil.isNullOrEmpty(timeStamp))
                {
                    timeStamp = DateUtil.getCurrentDateString();
                }
                cm.setLastTime(timeStamp);
                cm.setLastMsgId(msg.getMsgId());
                cm.setLastMsgSequence(msg.getMsgSequence());
                msgType = msg.getMsgType();
                cm.setLastMsgType(msgType);
                cm.setLastMsgStatus(msg.getMsgStatus());
                cm.setLastMsgContent(msg.getMsgContent());
                // 未读消息数为1
                if (GroupMessageModel.MSGSENDORRECV_RECV == recOrSend)
                {
                    cm.setUnReadmsg(1);
                }
                
                conversationAdapter.insertConversation(userSysId, cm);
            }
            else
            {
                // 如果会话已存在，则更新会话中最后一条消息的记录
                int unread = conversation.getUnReadmsg();
                
                ContentValues params = new ContentValues();
                params.put(ConversationColumns.LAST_MSG_ID, msg.getMsgId());
                params.put(ConversationColumns.LAST_MSG_SEQUENCE,
                        msg.getMsgSequence());
                msgType = msg.getMsgType();
                params.put(ConversationColumns.LAST_MSG_TYPE, msgType);
                params.put(ConversationColumns.LAST_MSG_STATUS,
                        msg.getMsgStatus());
                params.put(ConversationColumns.LAST_MSG_CONTENT,
                        msg.getMsgContent());
                timeStamp = msg.getMsgTime();
                if (StringUtil.isNullOrEmpty(timeStamp))
                {
                    timeStamp = DateUtil.getCurrentDateString();
                }
                params.put(ConversationColumns.LASTTIME, timeStamp);
                // 未读消息数 +1
                if (GroupMessageModel.MSGSENDORRECV_RECV == recOrSend)
                {
                    params.put(ConversationColumns.UNREAD_MSG, unread + 1);
                }
                conversationAdapter.updateByConversationId(userSysId,
                        conversation.getConversationId(),
                        params);
            }
        }
        return result;
    }
    
    /**
     * 根据游标解析群组消息信息<BR>
     * 
     * @param cursor 游标对象
     * @return 群组消息对象
     */
    private GroupMessageModel parseCursorToGroupMessageModel(Cursor cursor)
    {
        GroupMessageModel info = new GroupMessageModel();
        info.setGroupId(cursor.getString(cursor.getColumnIndex(GroupMessageColumns.GROUP_ID)));
        info.setMsgId(cursor.getString(cursor.getColumnIndex(GroupMessageColumns.MSG_ID)));
        info.setMsgSequence(cursor.getString(cursor.getColumnIndex(GroupMessageColumns.MSG_SEQUENCE)));
        info.setMemberUserId(cursor.getString(cursor.getColumnIndex(GroupMessageColumns.MEMBER_USERID)));
        info.setMemberNick(cursor.getString(cursor.getColumnIndex(GroupMessageColumns.MEMBER_NAME)));
        info.setMsgTime(cursor.getString(cursor.getColumnIndex(GroupMessageColumns.MSG_TIME)));
        info.setMsgType(cursor.getInt(cursor.getColumnIndex(GroupMessageColumns.MSG_TYPE)));
        info.setMsgContent(cursor.getString(cursor.getColumnIndex(GroupMessageColumns.MSG_CONTENT)));
        info.setMsgStatus(cursor.getInt(cursor.getColumnIndex(GroupMessageColumns.MSG_STATUS)));
        info.setMsgSendOrRecv(cursor.getInt(cursor.getColumnIndex(GroupMessageColumns.MSG_SENDORRECV)));
        
        MediaIndexModel media = null;
        int mediaIdColumn = cursor.getColumnIndex("mediaId");
        if (-1 != mediaIdColumn
                && !StringUtil.isNullOrEmpty(cursor.getString(mediaIdColumn)))
        {
            media = new MediaIndexModel();
            media.setMediaType(cursor.getInt(cursor.getColumnIndex(MediaIndexColumns.MEDIA_TYPE)));
            media.setMediaSize(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_SIZE)));
            media.setMediaPath(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_PATH)));
            media.setMediaSmallPath(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_PATH)));
            media.setMediaURL(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_URL)));
            media.setMediaSmallURL(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_URL)));
            media.setPlayTime(cursor.getInt(cursor.getColumnIndex(MediaIndexColumns.PLAY_TIME)));
            
            info.setMediaIndex(media);
        }
        
        return info;
    }
    
    /**
     * 
     * 将groupMessageModel对象转换成contentValues<BR>
     * @param model GroupMessageModel 对象
     * @return ContentValues 对象
     */
    private ContentValues parseModelToContentValues(GroupMessageModel model)
    {
        ContentValues contentValues = null;
        if (null != model)
        {
            contentValues = new ContentValues();
            if (!StringUtil.isNullOrEmpty(model.getGroupId()))
            {
                contentValues.put(GroupMessageColumns.GROUP_ID,
                        model.getGroupId());
            }
            if (!StringUtil.isNullOrEmpty(model.getMemberNick()))
            {
                contentValues.put(GroupMessageColumns.MEMBER_NAME,
                        model.getMemberNick());
            }
            if (!StringUtil.isNullOrEmpty(model.getMemberUserId()))
            {
                contentValues.put(GroupMessageColumns.MEMBER_USERID,
                        model.getMemberUserId());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgContent()))
            {
                contentValues.put(GroupMessageColumns.MSG_CONTENT,
                        model.getMsgContent());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgId()))
            {
                contentValues.put(GroupMessageColumns.MSG_ID, model.getMsgId());
            }
            if (0 != model.getMsgSendOrRecv())
            {
                contentValues.put(GroupMessageColumns.MSG_SENDORRECV,
                        model.getMsgSendOrRecv());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgSequence()))
            {
                contentValues.put(GroupMessageColumns.MSG_SEQUENCE,
                        model.getMsgSequence());
            }
            if (0 != model.getMsgStatus())
            {
                contentValues.put(GroupMessageColumns.MSG_STATUS,
                        model.getMsgStatus());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgTime()))
            {
                contentValues.put(GroupMessageColumns.MSG_TIME,
                        model.getMsgTime());
            }
            if (0 != model.getMsgType())
            {
                contentValues.put(GroupMessageColumns.MSG_TYPE,
                        model.getMsgType());
            }
            if (!StringUtil.isNullOrEmpty(model.getUserSysId()))
            {
                contentValues.put(GroupMessageColumns.USER_SYSID,
                        model.getUserSysId());
            }
        }
        return contentValues;
    }
    
    /**
     * 根据groupId查询会话中的最后一条消息消息<BR>
     * 
     * @param userSysId 用户系统标识
     * @param groupId 消息ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    private Cursor queryLastMsgByGroupIdWithCursor(String userSysId,
            String groupId)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = Uri.withAppendedPath(URIField.GROUPMESSAGE_LAST_URI,
                        userSysId + "|" + groupId);
                
                cursor = mContentResolver.query(uri, null, null, null, null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
}
