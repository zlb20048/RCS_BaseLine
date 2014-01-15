/*
 * 文件名: MessageDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 27, 2012
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
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MessageColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 1v1聊天信息处理Adapter<BR>
 * 
 * @author qlzhou
 * @version [RCS Client V100R001C03, Feb 27, 2012]
 */
public class MessageDbAdapter
{
    /**
     * 分页查询时候，每页显示的记录数 如果为QUERY_ALL_RECORD_NUM 则查询全部记录
     */
    public static final int PER_PAGE_COUNT = 50;
    
    /**
     * 分页查询时，perPageCount传入此值为查询全部记录
     */
    public static final int QUERY_ALL_RECORD_NUM = -1;
    
    /**
     * Debug TAG
     */
    private static final String TAG = "MessageDbAdapter";
    
    /**
     * MessageDbAdapter对象
     */
    private static MessageDbAdapter sInstance;
    
    /**
     * 会话表操作adapter
     */
    private ConversationDbAdapter mConversationDbAdapter;
    
    /**
     * 多媒体索引表操作adapter
     */
    private MediaIndexDbAdapter mediaDbIndexAdapter;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mResolver;
    
    /**
     * 构造器
     */
    private MessageDbAdapter(Context context)
    {
        mResolver = context.getContentResolver();
    }
    
    /**
     * 1v1聊天信息Adapter<BR>
     * 
     * @param context
     *            context
     * @return MessageDbAdapter
     */
    public static synchronized MessageDbAdapter getInstance(Context context)
    {
        if (sInstance == null)
        {
            sInstance = new MessageDbAdapter(context);
            sInstance.mConversationDbAdapter = ConversationDbAdapter.getInstance(context);
            sInstance.mediaDbIndexAdapter = MediaIndexDbAdapter.getInstance(context);
        }
        return sInstance;
    }
    
    /**
     * 查询所有的信息<BR>
     * 
     * @param userSysId
     *            sysId
     * @param friendUserId
     *            friendUserId
     * @return 1v1聊天信息列表
     */
    public ArrayList<MessageModel> queryAllByFriendUserId(String userSysId,
            String friendUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(friendUserId))
        {
            Uri uri = URIField.MESSAGE_URI;
            cursor = mResolver.query(uri,
                    null,
                    MessageColumns.USER_SYSID + "=? AND "
                            + MessageColumns.FRIEND_USERID + " =?",
                    new String[] { userSysId, friendUserId },
                    MessageColumns.ID);
        }
        if (null != cursor && cursor.moveToFirst())
        {
            ArrayList<MessageModel> list = new ArrayList<MessageModel>();
            do
            {
                MessageModel model = parseCursorToMessageModel(cursor);
                list.add(model);
            } while (cursor.moveToNext());
            // 关闭cursor
            cursor.close();
            return list;
        }
        return null;
    }
    
    /**
     * 查询消息表<BR>
     * @param userSysId userSysId
     * @param friendUserId friendUserId
     * @return cursor游标
     */
    public Cursor queryAllByFriendUserIdWithCursor(String userSysId,
            String friendUserId)
    {
        Cursor cursor = queryForPageWithCursor(userSysId, friendUserId, 1, -1);
        return cursor;
    }
    
    /**
     * 插入一条记录<BR>
     * 
     * @param conversationType
     *            会话类型:<BR>
     *            ConversationModel.CONVERSATIONTYPE_1VN<BR>
     *            ConversationModel.CONVERSATIONTYPE_1V1<BR>
     * @param model
     *            插入的消息对象 实体 model实体需要包含：<BR>
     *            friengUserId的值 <BR>
     *            消息方向的值:MessageModel.MSGSENDORRECV_RECV<BR>
     *            MessageModel.MSGSENDORRECV_SEND
     * @return 成功：插入记录的ID<BR>
     *         失败：-1
     */
    public long insert(int conversationType, MessageModel model)
    {
        long result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(model.getUserSysId())
                    && null != model)
            {
                
                String conversationId = model.getFriendUserId();
                // conversationId为空 或 conversationType未赋值 时直接返回 -1
                if (StringUtil.isNullOrEmpty(conversationId))
                {
                    return result;
                }
                int sendOrRecv = model.getMsgSendOrRecv();
                //把msgSequence的值赋给msgId,msgId字段现在没必要用,为了不改变别前台接口调用
                //这里进行处理
                //                model.setMsgId(model.getMsgSequence() != null ? model.getMsgSequence()
                //                        : model.getMsgId());
                result = doInsert(conversationType,
                        model,
                        conversationId,
                        sendOrRecv);
                if (result == -1)
                {
                    return result;
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
     * 根据msgId删除消息<BR>
     * 如果是多媒体类型的，会同时删除多媒体信息。
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param msgId
     *            消息ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public synchronized int deleteByMsgId(String userSysId, String msgId)
    {
        int result = -1;
        Cursor newLastMsgCursor = null;
        //会话ID
        String friendUserId = null;
        //是否更新会话表标志
        boolean updateConverOrNot = false;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(msgId))
            {
                friendUserId = getFriendUserIdByMsgId(userSysId, msgId);
                //如果查询不到friendUserId直接返回
                if (StringUtil.isNullOrEmpty(friendUserId))
                {
                    Logger.d(TAG, "Can not query friendUserId ");
                    return result;
                }
                updateConverOrNot = mConversationDbAdapter.isLastRecordInConversation(msgId);
                Uri messageUri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                        friendUserId);
                // 根据消息ID查询，如果该消息不存在直接返回
                Cursor cursor = mResolver.query(messageUri,
                        null,
                        MessageColumns.MSG_ID + " =? ",
                        new String[] { msgId },
                        null);
                if (cursor == null || !cursor.moveToFirst())
                {
                    return -1;
                }
                MessageModel oldMsg = parseCursorToMessageModel(cursor);
                
                // 如果是多媒体类型的，则先删除多媒体文件。
                int mMsgType = oldMsg.getMsgType();
                if (mMsgType == MessageModel.MSGTYPE_MEDIA)
                {
                    this.mediaDbIndexAdapter.deleteByMsgId(msgId);
                }
                
                result = mResolver.delete(messageUri,
                        MessageColumns.USER_SYSID + "=? AND "
                                + MessageColumns.MSG_ID + "=?",
                        new String[] { userSysId, msgId });
                int unreadMsg = getUnreadMsgCount(userSysId, friendUserId);
                
                if (-1 != unreadMsg)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(ConversationColumns.UNREAD_MSG, unreadMsg);
                    mConversationDbAdapter.updateByConversationId(userSysId,
                            friendUserId,
                            cv);
                }
                // 需要更新会话表
                if (updateConverOrNot)
                {
                    newLastMsgCursor = queryLastMsgByConversationIdWithCursor(userSysId,
                            friendUserId);
                    if (newLastMsgCursor != null
                            && newLastMsgCursor.moveToFirst())
                    {
                        // 更新会话表
                        MessageModel newLastMsg = parseCursorToMessageModel(newLastMsgCursor);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ConversationColumns.LAST_MSG_ID,
                                newLastMsg.getMsgId());
                        contentValues.put(ConversationColumns.LAST_MSG_TYPE,
                                newLastMsg.getMsgType());
                        contentValues.put(ConversationColumns.LAST_MSG_CONTENT,
                                newLastMsg.getMsgContent());
                        contentValues.put(ConversationColumns.LAST_MSG_STATUS,
                                newLastMsg.getMsgStatus());
                        mConversationDbAdapter.updateByConversationId(userSysId,
                                friendUserId,
                                contentValues);
                    }
                    else
                    {
                        // 如果消息是最后一条记录，并且是仅有的一条，则同时删除会话表中的对应信息
                        mConversationDbAdapter.deleteByConversationId(userSysId,
                                friendUserId,
                                ConversationModel.CONVERSATIONTYPE_1V1);
                    }
                }
                
                Logger.d(TAG, "deleteByMsgId, result = " + result);
            }
            else
            {
                Logger.d(TAG, "delete fail, msgId or userSysId is null");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(newLastMsgCursor);
        }
        return result;
    }
    
    /**
     * 根据会话/好友ID删除消息<BR>
     * @param userSysId sysId
     * @param friendUserId 好友ID 
     * @return 删除的条数
     */
    public int deleteByFriendUserId(String userSysId, String friendUserId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                    friendUserId);
            String where = new StringBuffer().append(MessageColumns.USER_SYSID)
                    .append(" = ? AND ")
                    .append(MessageColumns.FRIEND_USERID)
                    .append(" = ?")
                    .toString();
            //删除所有多媒体文件信息
            mediaDbIndexAdapter.deleteAllMediaByConversationId(userSysId,
                    friendUserId);
            result = mResolver.delete(uri, where, new String[] { userSysId,
                    friendUserId });
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
                mConversationDbAdapter.updateByConversationId(userSysId,
                        friendUserId,
                        contentValues);
            }
        }
        return result;
    }
    
    /**
     * 删除好友相关的消息和会话记录<BR>
     * @param userSysId sysId
     * @param friendUserId 好友ID 
     * @return 删除的条数
     */
    public int deleteFriendMessage(String userSysId, String friendUserId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                    friendUserId);
            String where = new StringBuffer().append(MessageColumns.USER_SYSID)
                    .append(" = ? AND ")
                    .append(MessageColumns.FRIEND_USERID)
                    .append(" = ?")
                    .toString();
            //删除所有多媒体文件信息
            mediaDbIndexAdapter.deleteAllMediaByConversationId(userSysId,
                    friendUserId);
            result = mResolver.delete(uri, where, new String[] { userSysId,
                    friendUserId });
            mConversationDbAdapter.deleteByConversationId(userSysId,
                    friendUserId,
                    ConversationModel.CONVERSATIONTYPE_1V1);
        }
        return result;
    }
    
    /**
     * 更新所有消息记录为已读<BR>
     * @param userSysId userSysId
     * @param friendUserId friendUserId
     * @return 改变的条数
     */
    public int changeAllMsgToReaded(String userSysId, String friendUserId)
    {
        int count = 0;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                
                Uri uri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                        friendUserId);
                String ids = mediaDbIndexAdapter.getAllAudioAndVideoIds();
                ContentValues contentValues = new ContentValues();
                // 条件：接收 状态为未读不需要报告
                String whereClauseNoReport = MessageColumns.MSG_SENDORRECV
                        + " =? and " + MessageColumns.MSG_STATUS + " =? and "
                        + MessageColumns.USER_SYSID + " =? ";
                if (!StringUtil.isNullOrEmpty(ids))
                {
                    whereClauseNoReport += " AND " + MessageColumns.MSG_ID
                            + " NOT IN " + ids;
                }
                // 参数：接收 状态为未读不需要报告
                String[] argsNoReport = new String[] {
                        "" + MessageModel.MSGSENDORRECV_RECV,
                        "" + MessageModel.MSGSTATUS_UNREAD_NO_REPORT, userSysId };
                contentValues.put(MessageColumns.MSG_STATUS,
                        MessageModel.MSGSTATUS_READED_NO_REPORT);
                int countNoReport = mResolver.update(uri,
                        contentValues,
                        whereClauseNoReport,
                        argsNoReport);
                
                // 条件：接收 状态为未读需要报告
                String whereCluaseNeedReport = MessageColumns.MSG_SENDORRECV
                        + " =? and " + MessageColumns.MSG_STATUS + " =? and "
                        + MessageColumns.USER_SYSID + " =? ";
                if (!StringUtil.isNullOrEmpty(ids))
                {
                    whereCluaseNeedReport += " AND " + MessageColumns.MSG_ID
                            + " NOT IN " + ids;
                }
                // 参数：接收 状态为已读需要报告
                String[] argsNeedReport = new String[] {
                        "" + MessageModel.MSGSENDORRECV_RECV,
                        "" + MessageModel.MSGSTATUS_UNREAD_NEED_REPORT,
                        userSysId };
                contentValues.put(MessageColumns.MSG_STATUS,
                        MessageModel.MSGSTATUS_READED_NEED_REPORT);
                int countNeedReport = mResolver.update(uri,
                        contentValues,
                        whereCluaseNeedReport,
                        argsNeedReport);
                count = countNoReport + countNeedReport;
                // 如果是最后一条记录，更新会话表中的相关信息
                ContentValues cv = new ContentValues();
                int unreadMsg = getUnreadMsgCount(userSysId, friendUserId);
                if (unreadMsg >= 0)
                {
                    cv.put(ConversationColumns.UNREAD_MSG, unreadMsg);
                }
                this.mConversationDbAdapter.updateByConversationId(userSysId,
                        friendUserId,
                        cv);
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
     * 将"待发送"或"发送中"的消息 变 "发送失败".<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param conversationIdList
     *            会话列表
     */
    public void changeAllMsgStatusToFail(String userSysId,
            List<String> conversationIdList)
    {
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && conversationIdList != null
                    && conversationIdList.size() > 0)
            {
                Uri uri = null;
                ContentValues cv = new ContentValues();
                cv.put(MessageColumns.MSG_STATUS,
                        MessageModel.MSGSTATUS_SEND_FAIL);
                
                for (String conversationId : conversationIdList)
                {
                    
                    uri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                            conversationId);
                    
                    mResolver.update(uri,
                            cv,
                            MessageColumns.USER_SYSID + "=? AND ("
                                    + MessageColumns.MSG_STATUS + "=? OR "
                                    + MessageColumns.MSG_STATUS + "=?) AND "
                                    + MessageColumns.FRIEND_USERID + "=?",
                            new String[] {
                                    userSysId,
                                    String.valueOf(MessageModel.MSGSTATUS_PREPARE_SEND),
                                    String.valueOf(MessageModel.MSGSTATUS_BLOCK),
                                    conversationId });
                    
                    // 更新会话中最后一条消息的状态
                    this.changeAllMsgToReaded(userSysId, conversationId);
                }
                
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
    }
    
    /**
     * 根据sequenceId获得消息状态
     * @param userSysId 用户系统ID
     * @param sequenceId 序列ID
     * @return 消息状态
     */
    public int getMessageStateBySequenceId(String userSysId, String sequenceId)
    {
        Cursor cursor = null;
        int state = -1;
        try
        {
            if (StringUtil.isNullOrEmpty(userSysId)
                    || StringUtil.isNullOrEmpty(sequenceId))
            {
                return state;
            }
            Uri uri = URIField.MESSAGE_URI;
            String[] projection = new String[] { MessageColumns.MSG_STATUS };
            String whereClause = MessageColumns.USER_SYSID + " =? and "
                    + MessageColumns.MSG_SEQUENCE + " =? ";
            String[] selectionArgs = new String[] { userSysId, sequenceId };
            cursor = mResolver.query(uri,
                    projection,
                    whereClause,
                    selectionArgs,
                    null);
            if (cursor == null || !cursor.moveToFirst())
            {
                return state;
            }
            state = cursor.getInt(cursor.getColumnIndex(MessageColumns.MSG_STATUS));
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        return state;
    }
    
    /**
     * 将一个会话中的发送或者发送未读的消息变成传要修改的状态<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param conversationId
     *            会话类型
     * @param toStatus
     *            修改后的状态
     * @return 成功：修改的行数 <br>
     *         失败：-1
     */
    public int changeMsgStatusByConversationId(String userSysId,
            String conversationId, int toStatus)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(conversationId))
            {
                Uri uri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                        conversationId);
                
                ContentValues cv = new ContentValues();
                cv.put(MessageColumns.MSG_STATUS, toStatus);
                
                result = mResolver.update(uri,
                        cv,
                        MessageColumns.USER_SYSID + "=? AND "
                                + MessageColumns.FRIEND_USERID + "=? AND "
                                + MessageColumns.MSG_SENDORRECV + "=? AND "
                                + MessageColumns.MSG_STATUS + " IN (?,?)",
                        new String[] {
                                userSysId,
                                conversationId,
                                String.valueOf(MessageModel.MSGSENDORRECV_SEND),
                                String.valueOf(MessageModel.MSGSTATUS_SENDED),
                                String.valueOf(MessageModel.MSGSTATUS_SEND_UNREAD) });
                
                // 同时修改会话中的最后一条消息的状态
                this.changeLastMsgStatusInConversation(userSysId,
                        conversationId,
                        false);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 查询会话中的消息数量。<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param conversationId
     *            会话ID
     * @return 成功：总条数<br>
     *         失败：0
     */
    public int queryCountInConversation(String userSysId, String conversationId)
    {
        Cursor cursor = null;
        int result = 0;
        try
        {
            if (!StringUtil.isNullOrEmpty(conversationId))
            {
                Uri uri = URIField.MESSAGE_URI;
                cursor = mResolver.query(uri,
                        new String[] { MessageColumns.ID },
                        MessageColumns.USER_SYSID + "=? AND "
                                + MessageColumns.FRIEND_USERID + "=?",
                        new String[] { userSysId, conversationId },
                        null);
                
                if (cursor != null)
                {
                    result = cursor.getCount();
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
     * 根据userSysId查询所有消息记录(暂未包括多媒体信息)
     * 
     * @param userSysId
     *            用户ID
     * @param friendUserId
     *            会话ID
     * @return 数据的cursor
     */
    public Cursor queryMessageByUserId(String userSysId, String friendUserId)
    {
        return queryForPageWithCursor(userSysId,
                friendUserId,
                1,
                QUERY_ALL_RECORD_NUM);
    }
    
    /**
     * 根据msgId查询消息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param msgId
     *            消息ID
     * @return 成功：消息对象 <br>
     *         失败：null
     */
    public MessageModel queryByMsgId(String userSysId, String msgId)
    {
        MessageModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByMsgIdWithCursor(userSysId, msgId);
            if (cursor != null && cursor.moveToFirst())
            {
                info = parseCursorToMessageModel(cursor);
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
     * 根据msgId分页查询消息记录，关联多媒体。<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param conversationId
     *            会话ID
     * @param currentPage
     *            当前页
     * @param perPageCount
     *            每页显示的记录数。为 -1 或者 QUERY_ALL_RECORD_NUM时，表示不分页，查询全部记录；
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryForPageWithCursor(String userSysId,
            String conversationId, int currentPage, int perPageCount)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(conversationId))
            {
                // .../message_page/userSysId/[conversationId]/[currentPage|perPageCount]
                Uri uri = Uri.withAppendedPath(URIField.MESSAGE_PAGE_URI,
                        userSysId + "/" + conversationId + "/" + currentPage
                                + "|" + perPageCount);
                cursor = mResolver.query(uri, null, null, null, null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 根据msgId分页查询消息记录，关联多媒体。<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param conversationId
     *            会话ID
     * @param currentPage
     *            当前页
     * @param perPageCount
     *            每页显示的记录数。为 -1 或者 QUERY_ALL_RECORD_NUM时，表示不分页，查询全部记录；
     * @return 成功：MessageModel集合 <br>
     *         失败：null
     */
    public List<MessageModel> queryForPage(String userSysId,
            String conversationId, int currentPage, int perPageCount)
    {
        ArrayList<MessageModel> list = null;
        Cursor cursor = null;
        try
        {
            MessageModel info = null;
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(conversationId))
            {
                cursor = this.queryForPageWithCursor(userSysId,
                        conversationId,
                        currentPage,
                        perPageCount);
                if (cursor != null && cursor.moveToFirst())
                {
                    list = new ArrayList<MessageModel>();
                    while (!cursor.isAfterLast())
                    {
                        info = this.parseCursorToMessageModel(cursor);
                        list.add(info);
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
        return list;
    }
    
    /**
     * 
     * 查询该msgStatus下的所有消息<BR>
     * @param userSysID 用户系统ID
     * @param msgStatus 消息状态
     * @param friendUserId 好友id
     * @return messageModel集合
     */
    public List<MessageModel> queryAllMessageByMsgStatus(String userSysID,
            int msgStatus, String friendUserId)
    {
        Cursor cursor = queryAllMessageByMsgStatusWithCursor(userSysID,
                msgStatus,
                friendUserId);
        if (null != cursor && cursor.moveToFirst())
        {
            ArrayList<MessageModel> list = new ArrayList<MessageModel>();
            do
            {
                MessageModel model = parseCursorToMessageModel(cursor);
                list.add(model);
            } while (cursor.moveToNext());
            // 关闭cursor
            cursor.close();
            return list;
        }
        return null;
    }
    
    /**
     * 
     * 根据对象更新消息的值<BR>
     * @param model 消息对象
     * @return 返回更新的条数
     */
    public int updateMessageRecord(MessageModel model)
    {
        int result = -1;
        if (model == null || StringUtil.isNullOrEmpty(model.getMsgId()))
        {
            Logger.d(TAG, "MessageModel object is null or msgId is null");
            return result;
        }
        //        ConversationModel conModel = null;
        Uri uri = Uri.withAppendedPath(URIField.MESSAGE_UPDATE_STATE_URI,
                model.getFriendUserId());
        ContentValues values = this.parseModelToContentValue(model);
        result = this.mResolver.update(uri, values, MessageColumns.MSG_ID
                + " =? ", new String[] { model.getMsgId() });
        //如果更新成功，则更新会话表相关信息
        if (result > 0)
        {
            ContentValues mCv = new ContentValues();
            mCv.put(ConversationColumns.LAST_MSG_STATUS, model.getMsgStatus());
            mCv.put(ConversationColumns.LAST_MSG_SEQUENCE,
                    model.getMsgSequence());
            this.mConversationDbAdapter.updateByMsgId(model.getUserSysId(),
                    model.getMsgId(),
                    mCv);
        }
        return result;
        
    }
    
    /**
     * 根据msgSequence修改消息<BR>
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param msgSequence 消息ID
     * @param friendUserId 用户好友ID
     * @param newStatus 新的状态
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByMsgSequence(String userSysId, String msgSequence,
            String friendUserId, int newStatus)
    {
        int result = -1;
        ConversationModel model = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(msgSequence)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                Uri uri = Uri.withAppendedPath(URIField.MESSAGE_UPDATE_STATE_URI,
                        friendUserId);
                ContentValues cv = new ContentValues();
                cv.put(MessageColumns.MSG_STATUS, newStatus);
                result = mResolver.update(uri,
                        cv,
                        MessageColumns.USER_SYSID + "=? AND "
                                + MessageColumns.MSG_SEQUENCE + "=?",
                        new String[] { userSysId, msgSequence });
                //如果消息状态变为已读状态，更新会话表中记录的未读消息数
                if (newStatus == MessageModel.MSGSTATUS_READED_NEED_REPORT
                        || newStatus == MessageModel.MSGSTATUS_READED_NO_REPORT)
                {
                    //查询会话表,获取会话表中单人聊天信息记录
                    model = this.mConversationDbAdapter.queryByConversationId(userSysId,
                            friendUserId,
                            ConversationModel.CONVERSATIONTYPE_1V1);
                    //如果为null 再次查询是不是小秘书信息
                    if (model == null)
                    {
                        model = this.mConversationDbAdapter.queryByConversationId(userSysId,
                                friendUserId,
                                ConversationModel.CONVERSATIONTYPE_SECRET);
                    }
                    if (model != null)
                    {
                        int unreadMsgNum = model.getUnReadmsg();
                        //如果会话表中未读消息数数目大于0
                        if (unreadMsgNum > 0)
                        {
                            unreadMsgNum = unreadMsgNum - result;
                        }
                        //更新会话表中的未读消息数
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ConversationColumns.UNREAD_MSG,
                                unreadMsgNum);
                        this.mConversationDbAdapter.updateByConversationId(userSysId,
                                friendUserId,
                                contentValues);
                    }
                }
                
                //如果消息表更新成功, 更新会话表中的最后一条消息的消息状态
                if (result > 0)
                {
                    ContentValues mCv = new ContentValues();
                    mCv.put(ConversationColumns.LAST_MSG_STATUS, newStatus);
                    this.mConversationDbAdapter.updateByMsgSequence(userSysId,
                            msgSequence,
                            mCv);
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
     * 根据msgId修改消息<BR>
     * [通用方法]
     * 
     * @param userSysId 用户在沃友系统的唯一标识
     * @param friendUserId 会话ID
     * @param msgId 消息ID
     * @param newStatus 需要修改的状态
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByMsgId(String userSysId, String friendUserId,
            String msgId, int newStatus)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(msgId)
                    && !StringUtil.isNullOrEmpty(friendUserId)
                    && !StringUtil.isNullOrEmpty(msgId))
            {
                Uri uri = Uri.withAppendedPath(URIField.MESSAGE_UPDATE_STATE_URI,
                        friendUserId);
                ContentValues cv = new ContentValues();
                cv.put(MessageColumns.MSG_STATUS, newStatus);
                
                result = mResolver.update(uri,
                        cv,
                        MessageColumns.USER_SYSID + "=? AND "
                                + MessageColumns.MSG_ID + "=?",
                        new String[] { userSysId, msgId });
                //如果消息状态变为已读状态，更新会话表中记录的未读消息数
                if (newStatus == MessageModel.MSGSTATUS_READED_NEED_REPORT
                        || newStatus == MessageModel.MSGSTATUS_READED_NO_REPORT)
                {
                    ConversationModel model = this.mConversationDbAdapter.queryByConversationId(userSysId,
                            friendUserId,
                            ConversationModel.CONVERSATIONTYPE_1V1);
                    int unreadMsgNum = model.getUnReadmsg();
                    //如果会话表中未读消息数数目大于0
                    if (unreadMsgNum > 0)
                    {
                        unreadMsgNum = unreadMsgNum - result;
                        ContentValues contentValues = new ContentValues();
                        cv.put(ConversationColumns.UNREAD_MSG, unreadMsgNum);
                        this.mConversationDbAdapter.updateByConversationId(userSysId,
                                friendUserId,
                                contentValues);
                    }
                }
                Logger.d(TAG, "updateMsg, result = " + result);
            }
            else
            {
                Logger.d(TAG, "updateMsg fail, msgId or params is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 
     * 获取指定好友的未读音频消息id集合<BR>
     * 
     * @param userSysId 用户系统id
     * @param friendUserId 好友用户id
     * @return 指定好友的未读音频消息id集合
     */
    public List<String> getUnreadAudioMsgIds(String userSysId,
            String friendUserId)
    {
        Cursor cursor = null;
        List<String> retList = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                Uri uri = URIField.MESSGAE_UNREAD_SPECIAL_MEDIA_URI;
                String[] selectionArgs = new String[] {
                        String.valueOf(MediaIndexModel.MEDIATYPE_AUDIO),
                        friendUserId };
                cursor = this.mResolver.query(uri,
                        null,
                        null,
                        selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst())
                {
                    retList = new ArrayList<String>();
                    while (!cursor.isAfterLast())
                    {
                        String id = cursor.getString(cursor.getColumnIndex(MessageColumns.MSG_ID));
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
     * doInsert 插入数据
     * @param conversationType
     *            会话类型<BR>
     * @param model
     *            消息实体
     * @param friendUserId
     *            好友ID
     * @param recOrSend
     *            发送或者接收: MessageModel.MSGSENDORRECV_RECV<BR>
     *            MessageModel.MSGSENDORRECV_SEND
     * @return 插入的条数
     */
    private long doInsert(int conversationType, MessageModel model,
            String friendUserId, int recOrSend)
    {
        long retValue = -1;
        Uri uri = null;
        uri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                friendUserId);
        
        if (conversationType != ConversationModel.CONVERSATIONTYPE_1V1
                && conversationType != ConversationModel.CONVERSATIONTYPE_SECRET)
        {
            return retValue;
        }
        ContentValues cv = new ContentValues();
        cv.put(MessageColumns.USER_SYSID, model.getUserSysId());
        cv.put(MessageColumns.MSG_ID, model.getMsgId());
        cv.put(MessageColumns.MSG_SEQUENCE, model.getMsgSequence());
        cv.put(MessageColumns.FRIEND_USERID, model.getFriendUserId());
        // 接收或者发送
        cv.put(MessageColumns.MSG_SENDORRECV, recOrSend);
        String timeStamp = model.getMsgTime();
        if (StringUtil.isNullOrEmpty(timeStamp))
        {
            timeStamp = DateUtil.getCurrentDateString();
        }
        cv.put(MessageColumns.MSG_TIME, timeStamp);
        cv.put(MessageColumns.MSG_CONTENT, model.getMsgContent());
        int msgType = model.getMsgType();
        
        if (MessageModel.MSGTYPE_MEDIA == msgType)
        {
            // 关联插入多媒体
            MediaIndexModel mediaIndex = model.getMediaIndex();
            if (mediaIndex != null)
            {
                mediaDbIndexAdapter.insertMediaIndex(mediaIndex);
            }
        }
        cv.put(MessageColumns.MSG_TYPE, msgType);
        cv.put(MessageColumns.MSG_STATUS, model.getMsgStatus());
        Uri resultUri = mResolver.insert(uri, cv);
        if (resultUri != null)
        {
            retValue = ContentUris.parseId(resultUri);
        }
        
        //如果消息表插入成功了，则进行会话表的相关操作
        if (-1 != retValue)
        {
            ConversationModel conversation = mConversationDbAdapter.queryByConversationId(model.getUserSysId(),
                    friendUserId,
                    conversationType);
            if (conversation == null)
            {
                // 如果会话不存在，则先建一条会话。
                ConversationModel cm = new ConversationModel();
                cm.setConversationId(friendUserId);
                cm.setLastMsgId(model.getMsgId());
                //msgSequence
                cm.setLastMsgSequence(model.getMsgSequence());
                cm.setConversationType(conversationType);
                timeStamp = model.getMsgTime();
                if (StringUtil.isNullOrEmpty(timeStamp))
                {
                    timeStamp = DateUtil.getCurrentDateString();
                }
                cm.setLastTime(timeStamp);
                msgType = model.getMsgType();
                cm.setLastMsgType(msgType);
                cm.setLastMsgStatus(model.getMsgStatus());
                cm.setLastMsgContent(model.getMsgContent());
                
                // 如果是接收消息 未读消息数为1
                if (recOrSend == MessageModel.MSGSENDORRECV_RECV)
                {
                    cm.setUnReadmsg(1);
                }
                
                long isInserConversationOk = mConversationDbAdapter.insertConversation(model.getUserSysId(),
                        cm);
                // 创建会话失败也直接返回.
                if (isInserConversationOk == -1)
                {
                    return retValue;
                }
            }
            else
            {
                // 如果会话已存在，则更新会话中最后一条消息的记录
                int unread = conversation.getUnReadmsg();
                ContentValues contentValues = new ContentValues();
                contentValues.put(ConversationColumns.LAST_MSG_ID,
                        model.getMsgId());
                //msgSequence
                contentValues.put(ConversationColumns.LAST_MSG_SEQUENCE,
                        model.getMsgSequence());
                msgType = model.getMsgType();
                contentValues.put(ConversationColumns.LAST_MSG_TYPE, msgType);
                contentValues.put(ConversationColumns.LAST_MSG_STATUS,
                        model.getMsgStatus());
                contentValues.put(ConversationColumns.LAST_MSG_CONTENT,
                        model.getMsgContent());
                timeStamp = model.getMsgTime();
                if (StringUtil.isNullOrEmpty(timeStamp))
                {
                    timeStamp = DateUtil.getCurrentDateString();
                }
                contentValues.put(ConversationColumns.LASTTIME, timeStamp);
                // 如果是接收消息 未读消息数 +1
                if (recOrSend == MessageModel.MSGSENDORRECV_RECV)
                {
                    contentValues.put(ConversationColumns.UNREAD_MSG,
                            unread + 1);
                }
                mConversationDbAdapter.updateByConversationId(model.getUserSysId(),
                        conversation.getConversationId(),
                        contentValues);
            }
        }
        return retValue;
    }
    
    /**
     * 根据msgId查询消息<BR>
     * 
     * @param userSysId
     *            用户在沃友系统的唯一标识
     * @param msgId
     *            消息ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    private Cursor queryByMsgIdWithCursor(String userSysId, String msgId)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(msgId))
            {
                Uri uri = URIField.MESSAGE_URI;
                String selection = new StringBuilder().append(MessageColumns.USER_SYSID)
                        .append(" =? and ")
                        .append(MessageColumns.MSG_ID)
                        .append(" =? ")
                        .toString();
                String[] selectionArgs = new String[] { userSysId, msgId };
                cursor = mResolver.query(uri,
                        null,
                        selection,
                        selectionArgs,
                        null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 根据conversationId查询会话中的最后一条消息消息<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param conversationId
     *            消息ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    private Cursor queryLastMsgByConversationIdWithCursor(String userSysId,
            String conversationId)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(conversationId))
            {
                if (!StringUtil.isNullOrEmpty(userSysId)
                        && !StringUtil.isNullOrEmpty(conversationId))
                {
                    Uri uri = Uri.withAppendedPath(URIField.MESSAGE_LAST_URI,
                            userSysId + "|" + conversationId);
                    
                    cursor = mResolver.query(uri, null, null, null, null);
                }
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 根据conversationId查询会话中的最后一条消息消息<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param conversationId
     *            消息ID
     * @return 成功：MessageModel对象 <br>
     *         失败：null
     */
    private MessageModel queryLastMsgByConversationId(String userSysId,
            String conversationId)
    {
        MessageModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryLastMsgByConversationIdWithCursor(userSysId,
                    conversationId);
            if (cursor != null && cursor.moveToFirst())
            {
                info = parseCursorToMessageModel(cursor);
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
     * 
     * 根据 msgId查询好友ID<BR>
     * @param userSysId 用户系统ID
     * @param msgId 消息ID
     * @return 返回好友ID(friendUserId)
     */
    private String getFriendUserIdByMsgId(String userSysId, String msgId)
    {
        String friendUserId = null;
        MessageModel model = this.queryByMsgId(userSysId, msgId);
        if (model == null)
        {
            return friendUserId;
        }
        return model.getFriendUserId();
    }
    
    /**
     * 查询一个会话中的最后一条消息记录，判断其它方向，更新会话表中最后一条消息的状态。<BR>
     * 
     * @param userSysId
     *            用户系统标识
     * @param conversationId
     *            会话ID
     * @param isClearUnReadMsg
     *            是否清空会话中未读消息数
     */
    private void changeLastMsgStatusInConversation(String userSysId,
            String conversationId, boolean isClearUnReadMsg)
    {
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(conversationId))
        {
            MessageModel lastMsg = this.queryLastMsgByConversationId(userSysId,
                    conversationId);
            if (lastMsg != null)
            {
                ContentValues contentValues = new ContentValues();
                int sendOrRecv = lastMsg.getMsgSendOrRecv();
                if (sendOrRecv == MessageModel.MSGSENDORRECV_RECV)
                {
                    // 接收: 无状态
                    if (lastMsg.getMsgStatus() != MessageModel.MSGSTATUS_READED_NO_REPORT
                            && lastMsg.getMsgStatus() != MessageModel.MSGSTATUS_READED_NEED_REPORT)
                    {
                        contentValues.put(ConversationColumns.LAST_MSG_STATUS,
                                MessageModel.MSGSTATUS_UNREAD_NO_REPORT);
                    }
                    else
                    {
                        contentValues.put(ConversationColumns.LAST_MSG_STATUS,
                                MessageModel.MSGSTATUS_NO_STATUS);
                    }
                }
                else
                {
                    // 发送： 变为 最后一条消息的状态
                    contentValues.put(ConversationColumns.LAST_MSG_STATUS,
                            lastMsg.getMsgStatus());
                }
                if (isClearUnReadMsg)
                {
                    // 清空"未读消息数"
                    contentValues.put(ConversationColumns.UNREAD_MSG, 0);
                }
                this.mConversationDbAdapter.updateByConversationId(userSysId,
                        conversationId,
                        contentValues);
            }
        }
    }
    
    /**
     * 
     * 根据状态查询消息记录<BR>
     * [功能详细描述]
     * @param userSysID 用户系统ID
     * @param msgStatus 消息状态
     * @return 返回查询的结果集
     */
    private Cursor queryAllMessageByMsgStatusWithCursor(String userSysID,
            int msgStatus, String friendUserId)
    {
        Logger.d(TAG, "query all message by msgStatus:" + msgStatus);
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysID))
            {
                Uri uri = URIField.MESSAGE_URI;
                String selection = new StringBuilder().append(MessageColumns.USER_SYSID)
                        .append(" =? and ")
                        .append(MessageColumns.MSG_STATUS)
                        .append(" =? and ")
                        .append(MessageColumns.FRIEND_USERID)
                        .append(" =? ")
                        .toString();
                String[] selectionArgs = new String[] { userSysID,
                        String.valueOf(msgStatus), friendUserId };
                cursor = mResolver.query(uri,
                        null,
                        selection,
                        selectionArgs,
                        null);
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return cursor;
    }
    
    /**
     * 获得消息表中未读消息数<BR>
     * @param userSysId 用户系统ID
     * @param friendUserId 好友ID
     * @return 未读消息数
     */
    private int getUnreadMsgCount(String userSysId, String friendUserId)
    {
        int result = -1;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(friendUserId))
            {
                Uri uri = URIField.MESSAGE_URI;
                String[] projection = new String[] { "COUNT(1)" };
                String selection = MessageColumns.USER_SYSID + " =? AND "
                        + MessageColumns.FRIEND_USERID + " =? AND ("
                        + MessageColumns.MSG_STATUS + " =? OR "
                        + MessageColumns.MSG_STATUS + " =? )";
                String[] selectionArgs = new String[] { userSysId,
                        friendUserId,
                        MessageModel.MSGSTATUS_UNREAD_NEED_REPORT + "",
                        MessageModel.MSGSTATUS_UNREAD_NO_REPORT + "" };
                cursor = this.mResolver.query(uri,
                        projection,
                        selection,
                        selectionArgs,
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
     * 根据游标解析消息信息<BR>
     * 
     * @param cursor
     *            游标对象
     * @return 消息对象
     */
    private MessageModel parseCursorToMessageModel(Cursor cursor)
    {
        MessageModel info = new MessageModel();
        String msgId = cursor.getString(cursor.getColumnIndex(MessageColumns.MSG_ID));
        info.setMsgId(msgId);
        info.setMsgSequence(cursor.getString(cursor.getColumnIndex(MessageColumns.MSG_SEQUENCE)));
        info.setFriendUserId(cursor.getString(cursor.getColumnIndex(MessageColumns.FRIEND_USERID)));
        info.setMsgSendOrRecv(cursor.getInt(cursor.getColumnIndex(MessageColumns.MSG_SENDORRECV)));
        info.setMsgTime(cursor.getString(cursor.getColumnIndex(MessageColumns.MSG_TIME)));
        info.setMsgStatus(cursor.getInt(cursor.getColumnIndex(MessageColumns.MSG_STATUS)));
        int msgType = cursor.getInt(cursor.getColumnIndex(MessageColumns.MSG_TYPE));
        info.setMsgType(msgType);
        info.setMsgContent(cursor.getString(cursor.getColumnIndex(MessageColumns.MSG_CONTENT)));
        //         如果消息类型是多媒体，则对多媒体信息表进行查询，查找相应记录
        if (MessageModel.MSGTYPE_MEDIA == msgType)
        {
            MediaIndexModel media = new MediaIndexModel();
            Cursor mediaCursor = mediaDbIndexAdapter.queryByMsgIdWithCursor(msgId);
            // 判断多媒体信息在多媒体信息表中是否存在
            if (mediaCursor != null && mediaCursor.moveToFirst())
            {
                media.setMsgId(msgId);
                media.setMediaType(mediaCursor.getInt(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_TYPE)));
                media.setMediaAlt(mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_ALT)));
                media.setMediaSize(mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_SIZE)));
                media.setMediaPath(mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_PATH)));
                media.setMediaSmallPath(mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_PATH)));
                media.setMediaURL(mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_URL)));
                media.setMediaSmallURL(mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_URL)));
                media.setPlayTime(mediaCursor.getInt(mediaCursor.getColumnIndex(MediaIndexColumns.PLAY_TIME)));
                
                info.setMediaIndex(media);
            }
        }
        
        return info;
    }
    
    /**
     * 
     * 将model转化为contentValues对象<BR>
     * @param model MessageModel对象
     * @return ContentValues 对象
     */
    private ContentValues parseModelToContentValue(MessageModel model)
    {
        ContentValues contentValues = null;
        if (null != model)
        {
            contentValues = new ContentValues();
            if (!StringUtil.isNullOrEmpty(model.getFriendUserId()))
            {
                contentValues.put(MessageColumns.FRIEND_USERID,
                        model.getFriendUserId());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgContent()))
            {
                contentValues.put(MessageColumns.MSG_CONTENT,
                        model.getMsgContent());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgId()))
            {
                contentValues.put(MessageColumns.MSG_ID, model.getMsgId());
            }
            if (0 != model.getMsgSendOrRecv())
            {
                contentValues.put(MessageColumns.MSG_SENDORRECV,
                        model.getMsgSendOrRecv());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgSequence()))
            {
                contentValues.put(MessageColumns.MSG_SEQUENCE,
                        model.getMsgSequence());
            }
            if (0 != model.getMsgStatus())
            {
                contentValues.put(MessageColumns.MSG_STATUS,
                        model.getMsgStatus());
            }
            if (!StringUtil.isNullOrEmpty(model.getMsgTime()))
            {
                contentValues.put(MessageColumns.MSG_TIME, model.getMsgTime());
            }
            if (0 != model.getMsgType())
            {
                contentValues.put(MessageColumns.MSG_TYPE, model.getMsgType());
            }
        }
        
        return contentValues;
    }
}
