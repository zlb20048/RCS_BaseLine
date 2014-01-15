package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FaceThumbnailColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.Tables;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 临时群/群组信息数据操作适配器<BR>
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-21]
 */
public class GroupInfoDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "GroupInfoDbAdapter";
    
    /**
     * GoupInfoDbAdapter对象
     */
    private static GroupInfoDbAdapter sInstance;
    
    /**
     * GroupMemberDbAdapter 对象
     */
    private static GroupMemberDbAdapter mMemberAdapter;
    
    /**
     * ConversationDbAdapter对象
     */
    private static ConversationDbAdapter mConversationAdapter;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private GroupInfoDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取GoupInfoDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return GoupInfoDbAdapter
     */
    public static synchronized GroupInfoDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new GroupInfoDbAdapter(context);
            mMemberAdapter = GroupMemberDbAdapter.getInstance(context);
            mConversationAdapter = ConversationDbAdapter.getInstance(context);
        }
        return sInstance;
    }
    
    /**
     * 插入群组信息<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param group 需要插入的对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertGroupInfo(String userSysId, GroupInfoModel group)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != group)
        {
            Uri uri = Uri.withAppendedPath(URIField.GROUPINFO_WITH_GROUPID_URI,
                    group.getGroupId());
            ContentValues cv = setValues(userSysId, group);
            Uri resultUri = mCr.insert(uri, cv);
            result = ContentUris.parseId(resultUri);
            Logger.d(TAG, "insertGroupInfo ----> uri: " + uri);
        }
        return result;
    }
    
    /**
     * 根据群组ID删除群组信息<BR>
     * 删除群组的同时，删除群组头像信息。
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupJid 群组Jid
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByGroupJid(String userSysId, String groupJid)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid))
        {
            Uri uri = Uri.withAppendedPath(URIField.GROUPINFO_WITH_GROUPID_URI,
                    groupJid);
            result = mCr.delete(uri, GroupInfoColumns.USER_SYSID + "=? AND "
                    + GroupInfoColumns.GROUP_ID + "=?", new String[] {
                    userSysId, groupJid });
            //删除群组的同时级联删除群组成员表，群聊天消息表，会话表中的群聊天消息的记录
            if (result > 0)
            {
                mConversationAdapter.deleteByConversationId(userSysId,
                        groupJid,
                        ConversationModel.CONVERSATIONTYPE_GROUP);
                mMemberAdapter.deleteMemberByGroupId(userSysId, groupJid);
            }
        }
        return result;
    }
    
    /**
     * 根据groupJid修改群组信息<BR>
     * [通用方法]
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupJid 群组ID
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByGroupJid(String userSysId, String groupJid,
            ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid) && null != cv
                && 0 < cv.size())
        {
            Uri uri = Uri.withAppendedPath(URIField.GROUPINFO_WITH_GROUPID_URI,
                    groupJid);
            result = mCr.update(uri,
                    cv,
                    GroupInfoColumns.USER_SYSID + "=? AND "
                            + GroupInfoColumns.GROUP_ID + "=?",
                    new String[] { userSysId, groupJid });
        }
        return result;
    }
    
    /**
     * 根据groupJid修改群组信息<BR>
     * [全量修改]
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupJid 群组ID
     * @param info 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByGroupJid(String userSysId, String groupJid,
            GroupInfoModel info)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid))
        {
            Uri uri = Uri.withAppendedPath(URIField.GROUPINFO_WITH_GROUPID_URI,
                    groupJid);
            ContentValues cv = setValues(userSysId, info);
            result = mCr.update(uri,
                    cv,
                    GroupInfoColumns.USER_SYSID + "=? AND "
                            + GroupInfoColumns.GROUP_ID + "=?",
                    new String[] { userSysId, groupJid });
        }
        return result;
    }
    
    /**
     * 根据群组JID查询群组信息<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupJid 群组ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByGroupIdWithCursor(String userSysId, String groupJid)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid))
        {
            Uri uri = Uri.withAppendedPath(URIField.GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT_URI,
                    userSysId + "/" + groupJid);
            cursor = mCr.query(uri,
                    null,
                    Tables.GROUPINFO + "." + GroupInfoColumns.USER_SYSID
                            + "=? AND " + Tables.GROUPINFO + "."
                            + GroupInfoColumns.GROUP_ID + "=?",
                    new String[] { userSysId, groupJid },
                    null);
        }
        return cursor;
    }
    
    /**
     * 根据群组JID查询群组信息<BR>
     * 
     * @param userSysId 当前登录用户系统标识
     * @param groupJid 群组ID
     * @return 成功：群组对象 <br>
     *         失败：null
     */
    public GroupInfoModel queryByGroupJid(String userSysId, String groupJid)
    {
        GroupInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByGroupIdWithCursor(userSysId, groupJid);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToGroupInfo(cursor);
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
     * 根据群组JID查询群组信息，不关联头像、角色、人数、未读消息。<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupJid 群组ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByGroupJidNoUnionWithCursor(String userSysId,
            String groupJid)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid))
        {
            Uri uri = URIField.GROUPINFO_URI;
            cursor = mCr.query(uri, null, GroupInfoColumns.GROUP_ID + "=? AND "
                    + GroupInfoColumns.USER_SYSID + "=?", new String[] {
                    groupJid, userSysId }, null);
        }
        return cursor;
    }
    
    /**
     * 根据群组JID查询群组信息，不关联头像、角色、人数、未读消息<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupJid 群组ID
     * @return 成功：群组对象 <br>
     *         失败：null
     */
    public GroupInfoModel queryByGroupJidNoUnion(String userSysId,
            String groupJid)
    {
        GroupInfoModel info = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByGroupJidNoUnionWithCursor(userSysId, groupJid);
            if (null != cursor && cursor.moveToFirst())
            {
                info = parseCursorToGroupInfo(cursor);
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
     * 查询所有群组<BR>
     * 
     * @param userSysId 用户系统标识
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryAllWithCursor(String userSysId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId))
        {
            Uri uri = Uri.withAppendedPath(URIField.GROUPINFO_QUERY_WITH_FACETHUMBNAIL_AND_MEMBER_COUNT_URI,
                    userSysId + "/" + "-1");
            cursor = mCr.query(uri,
                    null,
                    Tables.GROUPINFO + "." + GroupInfoColumns.USER_SYSID
                            + "=? ORDER BY " + Tables.GROUPINFO + "."
                            + GroupInfoColumns.GROUP_NAME,
                    new String[] { userSysId },
                    null);
        }
        return cursor;
    }
    
    /**
     * 查询所有群组<BR>
     * 
     * @param userSysId 用户系统标识
     * @return 成功：群组列表 <br>
     *         失败：null
     */
    public List<GroupInfoModel> queryAll(String userSysId)
    {
        ArrayList<GroupInfoModel> list = null;
        Cursor cursor = null;
        try
        {
            GroupInfoModel info = null;
            cursor = queryAllWithCursor(userSysId);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<GroupInfoModel>();
                while (!cursor.isAfterLast())
                {
                    info = parseCursorToGroupInfo(cursor);
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
     * 得到群组中未读消息数<BR>
     * 
     * @param userSysId 用户系统标识
     * @param groupJid 群组ID
     * @return 成功：未读消息数 <br>
     *         失败：0
     */
    public int getUnReadMsgCount(String userSysId, String groupJid)
    {
        int result = 0;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupJid))
            {
                Uri uri = URIField.GROUPINFO_URI;
                cursor = mCr.query(uri,
                        new String[] { GroupInfoColumns.UNREAD_MSG },
                        GroupInfoColumns.USER_SYSID + "=? AND "
                                + GroupInfoColumns.GROUP_ID + "=?",
                        new String[] { userSysId, groupJid },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    result = cursor.getInt(cursor.getColumnIndex(GroupInfoColumns.UNREAD_MSG));
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
     * 根据群组JID，将该群组的未读消息清空。<BR>
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupJid 群组ID
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int clearUnReadMsg(String userSysId, String groupJid)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupJid))
        {
            Uri uri = URIField.GROUPINFO_URI;
            ContentValues cv = new ContentValues();
            cv.put(GroupInfoColumns.UNREAD_MSG, 0);
            result = mCr.update(uri,
                    cv,
                    GroupInfoColumns.USER_SYSID + "=? AND "
                            + GroupInfoColumns.GROUP_ID + "=?",
                    new String[] { userSysId, groupJid });
        }
        return result;
    }
    
    /**
     * 
     * 批量插入群信息记录<BR>
     * 有批量插入操作的时候执行此方法
     * @param userSysId 用户系统ID
     * @param datas 要插入的对象集合
     * @return 插入的条数
     */
    public long applyInsertGroupInfo(String userSysId,
            List<GroupInfoModel> datas)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && datas.size() > 0)
        {
            Uri uri = URIField.GROUPINFO_URI;
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            
            for (int i = 0; i < datas.size(); i++)
            {
                ContentValues values = this.setValues(userSysId, datas.get(i));
                ContentProviderOperation operation = ContentProviderOperation.newInsert(uri)
                        .withValues(values)
                        .build();
                operationList.add(operation);
            }
            try
            {
                ContentProviderResult[] results = mCr.applyBatch(URIField.AUTHORITY,
                        operationList);
                result = results.length;
            }
            catch (RemoteException e)
            {
                DatabaseHelper.printException(e);
            }
            catch (OperationApplicationException e)
            {
                DatabaseHelper.printException(e);
            }
        }
        return result;
    }
    
    /**
     * 批量删除群信息<BR>
     * 有批量删除群信息调用此方法
     * @param userSysId 用户系统ID
     * @param datas 要删除的成员信息集合
     * @return 删除的条数
     */
    public long applyDeleteGroupInfo(String userSysId,
            List<GroupInfoModel> datas)
    {
        long result = -1;
        GroupInfoModel model = null;
        String selection = new StringBuilder().append(GroupInfoColumns.GROUP_ID)
                .append(" =? ")
                .toString();
        String[] selectionArgs = new String[1];
        if (!StringUtil.isNullOrEmpty(userSysId) && datas.size() > 0)
        {
            Uri uri = URIField.GROUPINFO_URI;
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            
            for (int i = 0; i < datas.size(); i++)
            {
                model = datas.get(i);
                ContentValues values = this.setValues(userSysId, datas.get(i));
                selectionArgs[0] = model.getGroupId();
                ContentProviderOperation operation = ContentProviderOperation.newDelete(uri)
                        .withSelection(selection, selectionArgs)
                        .withValues(values)
                        .build();
                operationList.add(operation);
            }
            try
            {
                ContentProviderResult[] results = mCr.applyBatch(URIField.AUTHORITY,
                        operationList);
                result = results.length;
            }
            catch (RemoteException e)
            {
                DatabaseHelper.printException(e);
            }
            catch (OperationApplicationException e)
            {
                DatabaseHelper.printException(e);
            }
        }
        return result;
    }
    
    /**
     * 批量更新群信息<BR>
     * 有批量更新群信息调用此方法
     * @param userSysId 用户系统ID
     * @param datas 要更新的群信息集合
     * @return 更新的条数
     */
    public long applyUpdateGroupMember(String userSysId,
            List<GroupInfoModel> datas)
    {
        long result = -1;
        GroupInfoModel model = null;
        String selection = new StringBuilder().append(GroupInfoColumns.GROUP_ID)
                .append(" =? ")
                .toString();
        String[] selectionArgs = new String[2];
        if (!StringUtil.isNullOrEmpty(userSysId) && datas.size() > 0)
        {
            Uri uri = URIField.GROUPINFO_URI;
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            
            for (int i = 0; i < datas.size(); i++)
            {
                model = datas.get(i);
                ContentValues values = this.setValues(userSysId, model);
                selectionArgs[0] = model.getGroupId();
                ContentProviderOperation operation = ContentProviderOperation.newUpdate(uri)
                        .withSelection(selection, selectionArgs)
                        .withValues(values)
                        .build();
                operationList.add(operation);
            }
            try
            {
                ContentProviderResult[] results = mCr.applyBatch(URIField.AUTHORITY,
                        operationList);
                result = results.length;
            }
            catch (RemoteException e)
            {
                DatabaseHelper.printException(e);
            }
            catch (OperationApplicationException e)
            {
                DatabaseHelper.printException(e);
            }
        }
        return result;
    }
    
    /**
     * 查询当前登录用户创建的群/临时群(聊吧)的个数<BR>
     * @param userSysId 用户系统ID
     * @param groupType 群组类型
     * @return 当前登录用户创建的群的个数
     */
    public int getOwnerCreateGroup(String userSysId, int groupType)
    {
        int count = -1;
        Cursor cursor = null;
        try
        {
            if (StringUtil.isNullOrEmpty(userSysId))
            {
                return count;
            }
            Uri uri = URIField.GROUPINFO_URI;
            String[] projection = new String[] { " COUNT(1) " };
            StringBuffer selection = new StringBuffer().append(GroupInfoColumns.AFFILICATION)
                    .append(" =owner AND ")
                    .append(GroupInfoColumns.GROUP_TYPE);
            if (groupType > GroupInfoModel.GROUPTYPE_NVN)
            {
                selection.append(" > ").append(GroupInfoModel.GROUPTYPE_NVN);
            }
            else
            {
                selection.append(" = ").append(groupType);
            }
            cursor = mCr.query(uri,
                    projection,
                    selection.toString(),
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst())
            {
                count = cursor.getInt(0);
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
        return count;
    }
    
    /**
     * 
     * 把群更新成无效状态<BR>
     * 被提出群 后不能再接受和发送群消息
     * @param userSysId 用户系统ID
     * @param groupId 群组ID
     * @return 更新的条数
     */
    public int updateGroupInfoToInvalid(String userSysId, String groupId)
    {
        int result = -1;
        if (StringUtil.isNullOrEmpty(userSysId)
                || StringUtil.isNullOrEmpty(groupId))
        {
            return -1;
        }
        Uri uri = URIField.GROUPINFO_URI;
        String selection = new StringBuffer().append(GroupInfoColumns.USER_SYSID)
                .append(" =? AND ")
                .append(GroupInfoColumns.GROUP_ID)
                .append(" =? ")
                .toString();
        String[] selectionArgs = new String[] { userSysId, groupId };
        ContentValues cv = new ContentValues();
        cv.put(GroupInfoColumns.AFFILICATION, "none");
        result = this.mCr.update(uri, cv, selection, selectionArgs);
        return result;
        
    }
    
    /**
     * 根据游标解析群组信息<BR>
     * 
     * @param cursor 游标对象
     * @return 群组信息对象
     */
    private GroupInfoModel parseCursorToGroupInfo(Cursor cursor)
    {
        GroupInfoModel info = new GroupInfoModel();
        info.setGroupId(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.GROUP_ID)));
        info.setGroupName(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.GROUP_NAME)));
        info.setGroupDesc(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.GROUP_DESC)));
        // info.setFace(cursor.getString(cursor
        // .getColumnIndex(GroupInfoColumns.FACE)));
        info.setGroupLabel(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.GROUP_LABEL)));
        info.setChatType(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.CHATTYPE)));
        info.setGroupSort(cursor.getInt(cursor.getColumnIndex(GroupInfoColumns.GROUP_SORT)));
        info.setGroupType(cursor.getInt(cursor.getColumnIndex(GroupInfoColumns.GROUP_TYPE)));
        info.setGroupBulletin(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.GROUP_BULLETIN)));
        info.setRecvRolicy(cursor.getInt(cursor.getColumnIndex(GroupInfoColumns.RECV_POLICY)));
        info.setMaxMembers(cursor.getInt(cursor.getColumnIndex(GroupInfoColumns.MAXMEMBERS)));
        info.setLastUpdate(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.LASTUPDATE)));
        info.setDelFlag(cursor.getInt(cursor.getColumnIndex(GroupInfoColumns.DELFLAG)));
        info.setUnReadMsg(cursor.getInt(cursor.getColumnIndex(GroupInfoColumns.UNREAD_MSG)));
        info.setAffiliation(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.AFFILICATION)));
        info.setGroupOwnerNick(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.GROUP_OWNERNICK)));
        info.setGroupOwnerUserId(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.GROUP_OWNER_USERID)));
        // info.setGroupOwnerFace(cursor.getString(cursor
        // .getColumnIndex(GroupInfoColumns.GROUP_OWNERFACE)));
        info.setProceeding(cursor.getString(cursor.getColumnIndex(GroupInfoColumns.PROCEEDING)));
        
        int memberCountColumn = cursor.getColumnIndex(GroupInfoColumns.QUERY_NUMBER_COUNT);
        if (-1 != memberCountColumn)
        {
            info.setMemberCount(cursor.getInt(memberCountColumn));
        }
        
        int faceUrlColumn = cursor.getColumnIndex(FaceThumbnailColumns.FACE_URL);
        if (-1 != faceUrlColumn)
        {
            info.setFaceUrl(cursor.getString(faceUrlColumn));
        }
        
        int faceBytesColumn = cursor.getColumnIndex(FaceThumbnailColumns.FACE_BYTES);
        if (-1 != faceBytesColumn)
        {
            info.setFaceBytes(cursor.getBlob(faceBytesColumn));
        }
        
        return info;
    }
    
    /**
     * 将groupInfo对象数据放入contentValues中<BR>
     * 
     * @param userSysId 用户系统标识
     * @param group 群组对象
     * @return ContentValues
     */
    private ContentValues setValues(String userSysId, GroupInfoModel group)
    {
        ContentValues cv = new ContentValues();
        cv.put(GroupInfoColumns.USER_SYSID, userSysId);
        cv.put(GroupInfoColumns.GROUP_ID, group.getGroupId());
        cv.put(GroupInfoColumns.GROUP_NAME, group.getGroupName());
        cv.put(GroupInfoColumns.GROUP_DESC, group.getGroupDesc());
        // String face = group.getFace();
        // cv.put(GroupInfoColumns.FACE,
        // face);
        cv.put(GroupInfoColumns.GROUP_LABEL, group.getGroupLabel());
        cv.put(GroupInfoColumns.GROUP_SORT, group.getGroupSort());
        // 对预置群组该配置项的取值根据业务场景的要求进行变化；
        //对于其他群组的取值固定为both
        String chatType = GroupInfoModel.CHATTYPE_BOTH;
        int groupType = group.getGroupType();
        cv.put(GroupInfoColumns.GROUP_TYPE, groupType);
        if (groupType == GroupInfoModel.GROUPTYPE_SYSTEM)
        {
            chatType = group.getChatType();
        }
        cv.put(GroupInfoColumns.CHATTYPE, chatType);
        cv.put(GroupInfoColumns.GROUP_BULLETIN, group.getGroupBulletin());
        cv.put(GroupInfoColumns.PROCEEDING, group.getProceeding());
        cv.put(GroupInfoColumns.RECV_POLICY, group.getRecvRolicy());
        cv.put(GroupInfoColumns.MAXMEMBERS, group.getMaxMembers());
        cv.put(GroupInfoColumns.LASTUPDATE, group.getLastUpdate());
        cv.put(GroupInfoColumns.DELFLAG, group.getDelFlag());
        cv.put(GroupInfoColumns.AFFILICATION, group.getAffiliation());
        cv.put(GroupInfoColumns.GROUP_OWNERNICK, group.getGroupOwnerNick());
        cv.put(GroupInfoColumns.GROUP_OWNER_USERID, group.getGroupOwnerUserId());
        // cv.put(GroupInfoColumns.GROUP_OWNERFACE,
        // group.getGroupOwnerFace());
        // cv.put(FaceThumbnailColumns.FACE_URL, group.getFaceUrl());
        // cv.put(FaceThumbnailColumns.FACE_BYTES, group.getFaceBytes());
        
        return cv;
    }
    
}
