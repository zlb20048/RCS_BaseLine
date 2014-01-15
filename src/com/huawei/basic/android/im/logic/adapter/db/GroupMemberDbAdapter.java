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
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupMemberColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.Tables;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 群组成员数据操作适配器<BR>
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-21]
 */
public class GroupMemberDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "GroupMemberDbAdapter";
    
    /**
     * GroupMemberAdapter对象
     */
    private static GroupMemberDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private GroupMemberDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取GroupMemberAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return GroupMemberAdapter
     */
    public static synchronized GroupMemberDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new GroupMemberDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入分组成员。<BR>
     * 如果头像为自定义，则会同时插入到头像表。
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param member 需插入的分组成员对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertGroupMember(String userSysId, GroupMemberModel member)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId) && null != member)
        {
            Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_AND_MEMBERID_URI,
                    member.getGroupId()),
                    member.getMemberUserId());
            
            ContentValues cv = setValues(userSysId, member);
            
            Uri resultUri = mCr.insert(uri, cv);
            result = ContentUris.parseId(resultUri);
        }
        return result;
    }
    
    /**
     * 
     * 批量插入群成员消息记录<BR>
     * 有批量插入操作的时候执行此方法
     * @param userSysId 用户系统ID
     * @param groupId 群组id
     * @param datas 要插入的对象集合
     * @return 插入的条数
     */
    public long applyInsertGroupMember(String userSysId, String groupId,
            List<GroupMemberModel> datas)
    {
        long result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId) && datas.size() > 0)
        {
            int size = datas.size();
            ContentValues []values = new ContentValues[size];
            Uri uri = Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                    groupId);
            
            for (int i = 0; i < size; i++)
            {
                values[i] = this.setValues(userSysId, datas.get(i));
            }
            result = mCr.bulkInsert(uri, values);
        }
        Logger.d(TAG, "apply insert groupMember:  " + result);
        return result;
    }
    
    /**
     * 批量删除成员信息<BR>
     * 有批量删除成员信息调用此方法
     * @param userSysId 用户系统ID
     * @param datas 要删除的成员信息集合
     * @return 要删除的
     */
    public long applyDeleteGroupMember(String userSysId, List<GroupMemberModel> datas)
    {
        long result = -1;
        GroupMemberModel model = null;
        String selection = new StringBuilder().append(GroupMemberColumns.GROUP_ID)
                .append(" =? AND ")
                .append(GroupMemberColumns.MEMBER_ID)
                .append(" =? ")
                .toString();
        String[] selectionArgs = new String[2];
        if (!StringUtil.isNullOrEmpty(userSysId) && datas.size() > 0)
        {
            Uri uri = URIField.GROUPMEMBER_URI;
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            
            for (int i = 0; i < datas.size(); i++)
            {
                model = datas.get(i);
                ContentValues values = this.setValues(userSysId, datas.get(i));
                selectionArgs[0] = model.getGroupId();
                selectionArgs[1] = model.getMemberId();
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
     * 批量更新成员信息<BR>
     * 有批量更新成员信息调用此方法
     * @param userSysId 用户系统ID
     * @param datas 要更新的成员信息集合
     * @return 更新的条数
     */
    public long applyUpdateGroupMember(String userSysId,
            List<GroupMemberModel> datas)
    {
        long result = -1;
        GroupMemberModel model = null;
        String selection = new StringBuilder().append(GroupMemberColumns.GROUP_ID)
                .append(" =? AND ")
                .append(GroupMemberColumns.MEMBER_ID)
                .append(" =? ")
                .toString();
        String[] selectionArgs = new String[2];
        if (!StringUtil.isNullOrEmpty(userSysId) && datas.size() > 0)
        {
            Uri uri = URIField.GROUPMEMBER_URI;
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            
            for (int i = 0; i < datas.size(); i++)
            {
                model = datas.get(i);
                ContentValues values = this.setValues(userSysId, model);
                selectionArgs[0] = model.getGroupId();
                selectionArgs[1] = model.getMemberId();
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
     * 根据memberUserId删除某个分组内的成员。<BR>
     * 删除成员的同时，删除头像信息。
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupId 群组ID
     * @param memberUserId 群成员的JID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteMemberByMemberUserId(String userSysId, String groupId,
            String memberUserId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId)
                && !StringUtil.isNullOrEmpty(memberUserId))
        {
            Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_AND_MEMBERID_URI,
                    groupId),
                    memberUserId);
            result = mCr.delete(uri, GroupMemberColumns.USER_SYSID + "=? AND "
                    + GroupMemberColumns.GROUP_ID + "=? AND "
                    + GroupMemberColumns.MEMBER_USERID + "=?", new String[] {
                    userSysId, groupId, memberUserId });
        }
        return result;
    }
    
    /**
     * 根据groupId删除某个分组内的所有成员<BR>
     * 删除成员的同时，删除头像信息。
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupId 群组ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteMemberByGroupId(String userSysId, String groupId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId))
        {
            Uri uri = Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                    groupId);
            result = mCr.delete(uri, GroupMemberColumns.USER_SYSID + "=? AND "
                    + GroupMemberColumns.GROUP_ID + "=?", new String[] {
                    userSysId, groupId });
        }
        return result;
    }
    
    /**
     * 修改群组成员信息<BR>
     * [通用方法]
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupId 群组ID
     * @param memberUserId 群成员的JID
     * @param cv 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByGroupIdAndMemberUserId(String userSysId, String groupId,
            String memberUserId, ContentValues cv)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId)
                && !StringUtil.isNullOrEmpty(memberUserId) && null != cv
                && 0 < cv.size())
        {
            Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_AND_MEMBERID_URI,
                    groupId),
                    memberUserId);
            result = mCr.update(uri, cv, GroupMemberColumns.USER_SYSID
                    + "=? AND " + GroupMemberColumns.GROUP_ID + "=? AND "
                    + GroupMemberColumns.MEMBER_USERID + "=?", new String[] {
                    userSysId, groupId, memberUserId });
        }
        return result;
    }
    
    /**
     * 修改群组成员信息<BR>
     * [全量修改]
     * 
     * @param userSysId 用户在系统的唯一标识
     * @param groupId 群组ID
     * @param memberUserId 群成员的JID
     * @param member 需要修改的对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByGroupIdAndMemberUserId(String userSysId, String groupId, String memberUserId,
            GroupMemberModel member)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId)
                && !StringUtil.isNullOrEmpty(memberUserId))
        {
            Uri uri = Uri.withAppendedPath(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_AND_MEMBERID_URI,
                    groupId),
                    memberUserId);
            ContentValues cv = setValues(userSysId, member);
            result = mCr.update(uri, cv, GroupMemberColumns.USER_SYSID
                    + "=? AND " + GroupMemberColumns.GROUP_ID + "=? AND "
                    + GroupMemberColumns.MEMBER_USERID + "=?", new String[] {
                    userSysId, groupId, memberUserId });
        }
        return result;
    }
    
    /**
     * 查询分组内的所有成员<BR>
     * 
     * @param userSysId 当前用户的系统标识
     * @param groupId 群组ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByGroupIdWithCursor(String userSysId, String groupId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId))
        {
            Uri uri = URIField.GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mCr.query(uri, null, Tables.GROUPMEMBER + "."
                    + GroupMemberColumns.USER_SYSID + "=? AND "
                    + Tables.GROUPMEMBER + "." + GroupMemberColumns.GROUP_ID
                    + "=? ORDER BY " + Tables.GROUPMEMBER + "."
                    + GroupMemberColumns.MEMBER_NICK, new String[] { userSysId,
                    groupId }, null);
        }
        return cursor;
    }
    
    /**
     * 查询分组内的所有成员<BR>
     * 
     * @param userSysId 当前用户的系统标识
     * @param groupId 群组ID
     * @return 成功：成员列表 <br>
     *         失败：null
     */
    public List<GroupMemberModel> queryByGroupId(String userSysId,
            String groupId)
    {
        ArrayList<GroupMemberModel> list = null;
        Cursor cursor = null;
        try
        {
            GroupMemberModel member = null;
            cursor = queryByGroupIdWithCursor(userSysId, groupId);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<GroupMemberModel>();
                while (!cursor.isAfterLast())
                {
                    member = parseCursorToGroupMember(cursor);
                    list.add(member);
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
     * 查询分组内的所有成员<BR>
     * 
     * @param userSysId 用户系统标识
     * @param groupId 群组ID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByGroupIdNoUnionWithCursor(String userSysId,
            String groupId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId))
        {
            Uri uri = URIField.GROUPMEMBER_URI;
            cursor = mCr.query(uri,
                    null,
                    GroupMemberColumns.USER_SYSID + "=? AND "
                            + GroupMemberColumns.GROUP_ID + "=?",
                    new String[] { userSysId, groupId },
                    GroupMemberColumns.MEMBER_NICK);
        }
        return cursor;
    }
    
    /**
     * 查询分组内的所有成员<BR>
     * 
     * @param userSysId 用户系统标识
     * @param groupId 群组ID
     * @return 成功：成员列表 <br>
     *         失败：null
     */
    public List<GroupMemberModel> queryByGroupIdNoUnion(String userSysId,
            String groupId)
    {
        ArrayList<GroupMemberModel> list = null;
        Cursor cursor = null;
        try
        {
            GroupMemberModel member = null;
            cursor = queryByGroupIdNoUnionWithCursor(userSysId, groupId);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<GroupMemberModel>();
                while (!cursor.isAfterLast())
                {
                    member = parseCursorToGroupMember(cursor);
                    list.add(member);
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
     * 查询分组内某个成员的信息<BR>
     * 
     * @param userSysId 用户系统标识
     * @param groupId 群组ID
     * @param memberUserId 成员JID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByMemberUserIdWithCursor(String userSysId,
            String groupId, String memberUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId)
                && !StringUtil.isNullOrEmpty(memberUserId))
        {
            Uri uri = URIField.GROUPMEMBER_QUERY_WITH_FACETHUMBNAIL_URI;
            cursor = mCr.query(uri, null, Tables.GROUPMEMBER + "."
                    + GroupMemberColumns.USER_SYSID + "=? AND "
                    + Tables.GROUPMEMBER + "." + GroupMemberColumns.GROUP_ID
                    + "=? AND " + Tables.GROUPMEMBER + "."
                    + GroupMemberColumns.MEMBER_USERID + "=?", new String[] {
                    userSysId, groupId, memberUserId }, null);
            ;
        }
        return cursor;
    }
    
    /**
     * 查询分组内某个成员的信息<BR>
     * 
     * @param userSysId 用户系统标识
     * @param groupId 群组ID
     * @param memberUserId 群成员的JID
     * @return 成功：成员信息 <br>
     *         失败：null
     */
    public GroupMemberModel queryByMemberUserId(String userSysId,
            String groupId, String memberUserId)
    {
        GroupMemberModel member = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId)
                    && !StringUtil.isNullOrEmpty(memberUserId))
            {
                cursor = queryByMemberUserIdWithCursor(userSysId,
                        groupId,
                        memberUserId);
                if (null != cursor && cursor.moveToFirst())
                {
                    member = parseCursorToGroupMember(cursor);
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
        return member;
    }
    
    /**
     * 查询分组内某个成员的信息<BR>
     * 
     * @param userSysId 当前登录用户标识
     * @param groupId 群组ID
     * @param memberUserId 成员JID
     * @return 成功：Cursor对象 <br>
     *         失败：null
     */
    public Cursor queryByMemberUserIdNoUnionWithCursor(String userSysId,
            String groupId, String memberUserId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(userSysId)
                && !StringUtil.isNullOrEmpty(groupId)
                && !StringUtil.isNullOrEmpty(memberUserId))
        {
            Uri uri = URIField.GROUPMEMBER_URI;
            cursor = mCr.query(uri, null, GroupMemberColumns.USER_SYSID
                    + "=? AND " + GroupMemberColumns.GROUP_ID + "=? AND "
                    + GroupMemberColumns.MEMBER_USERID + "=?", new String[] {
                    userSysId, groupId, memberUserId }, null);
        }
        return cursor;
    }
    
    /**
     * 查询分组内某个成员的信息<BR>
     * 
     * @param userSysId 当前登录用户标识
     * @param groupId 群组ID
     * @param memberUserId 群成员的JID
     * @return 成功：成员信息 <br>
     *         失败：null
     */
    public GroupMemberModel queryByMemberUserIdNoUnion(String userSysId,
            String groupId, String memberUserId)
    {
        GroupMemberModel member = null;
        Cursor cursor = null;
        try
        {
            cursor = queryByMemberUserIdNoUnionWithCursor(userSysId,
                    groupId,
                    memberUserId);
            if (null != cursor && cursor.moveToFirst())
            {
                member = parseCursorToGroupMember(cursor);
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
        return member;
    }
    
    /**
     * 
     * 获得群组成员个数<BR>
     * @param userSysId 用户系统ID
     * @param groupId 群组id
     * @return 群成员个数
     */
    public int getMemberCount(String userSysId, String groupId)
    {
        Cursor cursor = null;
        int result = 0;
        try
        {
            if (!StringUtil.isNullOrEmpty(userSysId)
                    && !StringUtil.isNullOrEmpty(groupId))
            {
                Uri uri = URIField.GROUPMEMBER_URI;
                String[] projection = new String[] { "COUNT(1)" };
                String selection = new StringBuffer().append(GroupMemberColumns.AFFILIATION)
                        .append(" <>? AND ")
                        .append(GroupMemberColumns.USER_SYSID)
                        .append(" =? AND ")
                        .append(GroupMemberColumns.GROUP_ID)
                        .append(" =? ")
                        .toString();
                String[] selectionArgs = new String[] {
                        GroupMemberModel.AFFILIATION_NONE, userSysId, groupId };
                cursor = this.mCr.query(uri,
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
     * 根据游标解析群组成员信息<BR>
     * 
     * @param cursor 游标对象
     * @return 群组成员对象
     */
    private GroupMemberModel parseCursorToGroupMember(Cursor cursor)
    {
        GroupMemberModel member = new GroupMemberModel();
        member.setGroupId(cursor.getString(cursor.getColumnIndex(GroupMemberColumns.GROUP_ID)));
        member.setMemberUserId(cursor.getString(cursor.getColumnIndex(GroupMemberColumns.MEMBER_USERID)));
        member.setMemberId(cursor.getString(cursor.getColumnIndex(GroupMemberColumns.MEMBER_ID)));
        member.setAffiliation(cursor.getString(cursor.getColumnIndex(GroupMemberColumns.AFFILIATION)));
        member.setMemberNick(cursor.getString(cursor.getColumnIndex(GroupMemberColumns.MEMBER_NICK)));
        member.setMemberDesc(cursor.getString(cursor.getColumnIndex(GroupMemberColumns.MEMBER_DESC)));
        // member.setMemberFace(cursor.getString(cursor
        // .getColumnIndex(GroupMemberColumns.MEMEBER_FACE)));
        member.setStatus(cursor.getString(cursor.getColumnIndex(GroupMemberColumns.STATUS)));
        
        int faceUrlColumn = cursor.getColumnIndex(FaceThumbnailColumns.FACE_URL);
        if (-1 != faceUrlColumn)
        {
            member.setMemberFaceUrl(cursor.getString(faceUrlColumn));
        }
        
        int faceBytesColumn = cursor.getColumnIndex(FaceThumbnailColumns.FACE_BYTES);
        if (-1 != faceBytesColumn)
        {
            member.setMemberFaceBytes(cursor.getBlob(faceBytesColumn));
        }
        
        return member;
    }
    
    /**
     * 将群成员对象封装成 ContentValues<BR>
     * 
     * @param userSysId 用户系统标识
     * @param member 群成员对象
     * @return ContentValues
     */
    private ContentValues setValues(String userSysId, GroupMemberModel member)
    {
        ContentValues cv = new ContentValues();
        cv.put(GroupMemberColumns.USER_SYSID, userSysId);
        cv.put(GroupMemberColumns.GROUP_ID, member.getGroupId());
        cv.put(GroupMemberColumns.MEMBER_USERID, member.getMemberUserId());
        cv.put(GroupMemberColumns.MEMBER_ID, member.getMemberId());
        cv.put(GroupMemberColumns.AFFILIATION, member.getAffiliation());
        cv.put(GroupMemberColumns.MEMBER_NICK, member.getMemberNick());
        cv.put(GroupMemberColumns.MEMBER_DESC, member.getMemberDesc());
        // String memberFace = member.getMemberFace();
        // cv.put(GroupMemberColumns.MEMEBER_FACE,
        // memberFace);
        cv.put(GroupMemberColumns.STATUS, member.getStatus());
        
        return cv;
    }
    
}
