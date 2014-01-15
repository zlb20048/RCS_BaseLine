/*
 * 文件名: CommunicationLogDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 14, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.db.voip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.voip.VoipDatabaseHelper.CommunicationLogColumns;
import com.huawei.basic.android.im.component.database.voip.VoipDatabaseHelper.Tables;
import com.huawei.basic.android.im.logic.model.voip.CommunicationLog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 14, 2012] 
 */
public class CommunicationLogDbAdapter extends BaseDbAdapter
{
    /**
     * 当前对象唯一实例
     */
    private static CommunicationLogDbAdapter mInstance;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private CommunicationLogDbAdapter(Context context)
    {
        super(context);
    }
    
    /**
     * 获取UserConfigDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return UserConfigDbAdapter
     */
    public static synchronized CommunicationLogDbAdapter getInstance(
            Context context)
    {
        if (null == mInstance)
        {
            mInstance = new CommunicationLogDbAdapter(context);
        }
        return mInstance;
    }
    
    /**
     * 得到表名
     * @return
     *      表名
     * @see com.huawei.basic.android.im.logic.adapter.db.voip.BaseDbAdapter#getTableName()
     */
    @Override
    protected String getTableName()
    {
        return Tables.COMMUNICATION_LOG;
    }
    
    /**
     * 新增一个通话记录
     * @param communicationLog
     *      通话记录对象
     */
    public void addCommunicationLog(CommunicationLog communicationLog)
    {
        super.insert(getContentValues(communicationLog));
    }
    
    /**
     * 更新通话时间根据所属用户id和callId
     * @param startTime
     *      通话开始时间
     * @param ownerUserId
     *      所属用户ID
     * @param callId
     *      通话ID
     * @param type
     *      通话类型
     */
    public void updateStartTimeByLastCallTimeAndCallId(Date startTime,
            String ownerUserId, int callId, int type)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ")
                .append(getTableName())
                .append(" SET startTime= ?,type= ?");
        if (type != CommunicationLog.TYPE_VOIP_CALL_IN_MISSED)
        {
            sql.append(",isUnread = '0'");
        }
        sql.append(" WHERE startTime is null AND ")
                .append("_ID =(SELECT _ID FROM ")
                .append(getTableName())
                .append(" WHERE ownerUserId = ? AND ")
                .append("callId = ? ORDER BY callTime DESC LIMIT 0,1)");
        String startTimeString = changeDateToString(startTime);
        String[] selectionArgs = new String[] { startTimeString,
                String.valueOf(type), ownerUserId, String.valueOf(callId) };
        super.execSQL(sql.toString(), selectionArgs, getTableName());
    }
    
    /**
     * 更新结束时间根据所属用户id和callId
     * @param endTime
     *      结束时间
     * @param ownerUserId
     *      所属用户ID
     * @param callId
     *      通话ID
     */
    public void updateEndTimeByLastCallTimeAndCallId(Date endTime,
            String ownerUserId, int callId)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ")
                .append(getTableName())
                .append(" SET endTime = ? WHERE ")
                .append("_ID =(SELECT _ID FROM ")
                .append(getTableName())
                .append(" WHERE endTime is null AND ownerUserId = ? AND ")
                .append("callId = ? ORDER BY callTime DESC LIMIT 0,1)");
        String endTimeString = changeDateToString(endTime);
        String[] selectionArgs = new String[] { endTimeString, ownerUserId,
                String.valueOf(callId) };
        super.execSQL(sql.toString(), selectionArgs, getTableName());
    }
    
    /**
     * 更新结束时间根据所属用户id和callId
     * @param endTime
     *      结束时间
     * @param ownerUserId
     *      所属用户ID
     * @param callId
     *      通话ID
     * @param type
     *      通话类型
     */
    public void updateEndTimeByLastCallTimeAndCallId(Date endTime,
            String ownerUserId, int callId, int type)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ")
                .append(getTableName())
                .append(" SET endTime = ?, type = ?");
        if (type != CommunicationLog.TYPE_VOIP_CALL_IN_MISSED)
        {
            sql.append(",isUnread='0'");
        }
        else
        {
            sql.append(",isUnread='1'");
        }
        sql.append(" WHERE endTime is null AND ")
                .append("_ID =(SELECT _ID FROM ")
                .append(getTableName())
                .append(" WHERE ownerUserId = ? AND ")
                .append("callId = ? ORDER BY callTime DESC LIMIT 0,1)");
        String endTimeString = changeDateToString(endTime);
        String[] selectionArgs = new String[] { endTimeString,
                String.valueOf(type), ownerUserId, String.valueOf(callId) };
        super.execSQL(sql.toString(), selectionArgs, getTableName());
    }
    
    /**
     * 删除所属用户ID需要显示通话记录意外的通话记录
     * @param ownerUserId
     *      所属用户ID
     * @param startIndex
     *     开始删除条数下标
     */
    public void deleteCommunicationLogGroupByOwnerSysId(String ownerUserId,
            int startIndex)
    {
        //查询出ownerUserId用户不需要删除的RemoteUri或者RemotePhoneNum
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t2.remoteUri remoteUri,t2.remotePhoneNum remotePhoneNum FROM (SELECT t1.remoteUri remoteUri,");
        sql.append("t1.remotePhoneNum remotePhoneNum,MAX(t1.callTime) callTime FROM ");
        sql.append(getTableName())
                .append(" t1 WHERE t1.ownerUserId = ? GROUP BY t1.remoteUri,t1.remotePhoneNum) t2 ");
        sql.append(" ORDER BY t2.callTime DESC LIMIT ?,?");
        StringBuilder remoteUriString = new StringBuilder();
        StringBuilder remotePhoneNumString = new StringBuilder();
        Cursor cursor = super.query(sql.toString(), new String[] { ownerUserId,
                "0", String.valueOf(startIndex) });
        try
        {
            if (null != cursor && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    String remoteUri = cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_URI));
                    String remotePhoneNum = cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_PHONE_NUM));
                    if (remoteUri != null)
                    {
                        remoteUriString.append(",'")
                                .append(remoteUri)
                                .append("'");
                    }
                    else if (remotePhoneNum != null)
                    {
                        remotePhoneNumString.append(",'")
                                .append(remotePhoneNum)
                                .append("'");
                    }
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
        //删除查询到ownerUserId用户的RemoteUri或者RemotePhoneNum以外的通话记录
        if (remoteUriString.toString().length() > 0
                || remotePhoneNumString.toString().length() > 0)
        {
            StringBuilder where = new StringBuilder();
            where.append("ownerUserId = ? AND (");
            if (remoteUriString.toString().length() > 0)
            {
                where.append(" remoteUri NOT IN ( ")
                        .append(remoteUriString.toString().substring(1))
                        .append(")");
                if (remotePhoneNumString.toString().length() > 0)
                {
                    where.append(" OR ");
                }
            }
            if (remotePhoneNumString.toString().length() > 0)
            {
                where.append(" remotePhoneNum NOT IN ( ")
                        .append(remotePhoneNumString.toString().substring(1))
                        .append(")");
            }
            where.append(")");
            super.delete(where.toString(), new String[] { ownerUserId });
        }
    }
    
    /**
     * 获取当前用户的未读记录数
     * @param ownerUserId
     *      当前用户的id
     * @return
     *      未读记录数
     */
    public int getUnreadTotal(String ownerUserId)
    {
        Cursor cursor = null;
        try
        {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(_ID) ctn FROM ")
                    .append(getTableName())
                    .append(" WHERE ownerUserId = ? AND isUnread = ?");
            cursor = super.query(sql.toString(), new String[] { ownerUserId,
                    "1" });
            if (null != cursor && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    return cursor.getInt(0);
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
        return 0;
    }
    
    /**
     * 根据对方用户id分组查询通讯记录
     * @param ownerUserId
     *      所属用户id
     * @param startIndex
     *      开始查询下标
     * @param recordNum
     *      查询返回记录数
     * @return
     *      通话记录集合
     */
    public List<CommunicationLog> findCommunicationLogGroupByOwnerSysId(
            String ownerUserId, int startIndex, int recordNum)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (SELECT t4._ID _ID,t4.remoteUserId remoteUserId,t3.remoteUri remoteUri,t3.remotePhoneNum remotePhoneNum,t4.remoteDisplayName remoteDisplayName,t4.faceUri faceUri,t4.faceData faceData,");
        sql.append("t4.type type,t4.sort sort,t4.callTime callTime,t3.unreadAmout unreadAmout FROM (SELECT t2.remoteUri remoteUri,");
        sql.append("t2.remotePhoneNum remotePhoneNum,MAX(t2.callTime) callTime,SUM(t2.isUnread) unreadAmout ");
        sql.append("FROM (SELECT t1.isUnread isUnread,t1.remoteUri remoteUri,t1.remotePhoneNum remotePhoneNum,t1.callTime callTime ");
        sql.append("FROM ")
                .append(getTableName())
                .append(" t1 WHERE t1.ownerUserId = ?) t2 GROUP BY t2.remoteUri,t2.remotePhoneNum) ");
        sql.append("t3 LEFT JOIN ")
                .append(getTableName())
                .append(" t4 ON ( t3.remoteUri = t4.remoteUri OR t3.remotePhoneNum = t4.remotePhoneNum) ");
        sql.append("AND t3.callTime = t4.callTime WHERE t4.ownerUserId = ? LIMIT ?,? ) ORDER BY callTime DESC");
        Cursor cursor = super.query(sql.toString(),
                new String[] { ownerUserId, ownerUserId,
                        String.valueOf(startIndex), String.valueOf(recordNum) });
        List<CommunicationLog> list = new ArrayList<CommunicationLog>();
        try
        {
            if (null != cursor && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    CommunicationLog communicationLog = new CommunicationLog();
                    communicationLog.setId(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.ID)));
                    communicationLog.setRemoteUserId(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_USER_ID)));
                    communicationLog.setRemoteUri(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_URI)));
                    communicationLog.setRemotePhoneNum(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_PHONE_NUM)));
                    communicationLog.setRemoteDisplayName(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_DISPLAY_NAME)));
                    communicationLog.setFaceUrl(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.FACE_URI)));
                    communicationLog.setFaceData(cursor.getBlob(cursor.getColumnIndex(CommunicationLogColumns.FACE_DATA)));
                    communicationLog.setType(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.TYPE)));
                    communicationLog.setSort(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.SORT)));
                    communicationLog.setCallTime(getDateFormCursor(cursor,
                            CommunicationLogColumns.CALL_TIME));
                    communicationLog.setUnreadAmout(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.UNREAD_AMOUT)));
                    list.add(communicationLog);
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
     * 根据ownerUserId删除所有的通话记录
     * @param ownerUserId
     *      所属用户ID
     */
    public void deleteCommunicationLogByOwnerUserId(String ownerUserId)
    {
        super.delete("ownerUserId = ?", new String[] { ownerUserId });
    }
    
    /**
     * 根据对方用户ID或者对方电话号码删除通话记录
     * @param ownerUserId
     *      所属用户id
     * @param remoteUri
     *      对方用户Uri
     * @param remotePhoneNum
     *      对方电话号码
     * @param startIndex
     *      开始删除下标
     */
    public void deleteCommunicationLogByRemoteUriOrRemotePhoneNum(
            String ownerUserId, String remoteUri, String remotePhoneNum,
            int startIndex)
    {
        if (remoteUri == null && remotePhoneNum == null)
        {
            return;
        }
        StringBuilder where = new StringBuilder();
        String[] selectionArgs = null;
        where.append("_ID IN (SELECT _ID FROM ")
                .append(getTableName())
                .append(" WHERE ownerUserId = ? AND ");
        if (remoteUri != null)
        {
            where.append("remoteUri = ? ");
            selectionArgs = new String[] { ownerUserId, remoteUri,
                    String.valueOf(startIndex) };
        }
        else if (remotePhoneNum != null)
        {
            where.append("remotePhoneNum = ?");
            selectionArgs = new String[] { ownerUserId, remotePhoneNum,
                    String.valueOf(startIndex) };
        }
        where.append(" ORDER BY callTime DESC LIMIT ?)");
        super.delete(where.toString(), selectionArgs);
    }
    
    /**
     * 根据对方用户ID或者对方电话号码查询所有的通话记录
     * @param ownerUserId
     *      所属用户id
     * @param remoteUri
     *      对方用户Uri
     * @param remotePhoneNum
     *      对方电话号码
     * @param startIndex
     *      开始查询下标
     * @param recordNum
     *      查询返回记录数
     * @return
     *      通话记录集合
     */
    public List<CommunicationLog> findCommunicationLogByRemoteUriOrRemotePhoneNum(
            String ownerUserId, String remoteUri, String remotePhoneNum,
            int startIndex, int recordNum)
    {
        if (remoteUri == null && remotePhoneNum == null)
        {
            return new ArrayList<CommunicationLog>();
        }
        StringBuilder sql = new StringBuilder();
        String[] selectionArgs = null;
        sql.append("SELECT * FROM ")
                .append(getTableName())
                .append(" t WHERE t.ownerUserId = ? AND ");
        if (remoteUri != null)
        {
            sql.append("t.remoteUri = ? ");
            selectionArgs = new String[] { ownerUserId, remoteUri,
                    String.valueOf(startIndex), String.valueOf(recordNum) };
        }
        else if (remotePhoneNum != null)
        {
            sql.append("t.remotePhoneNum = ?");
            selectionArgs = new String[] { ownerUserId, remotePhoneNum,
                    String.valueOf(startIndex), String.valueOf(recordNum) };
        }
        sql.append(" ORDER BY t.callTime DESC LIMIT ?,?");
        Cursor cursor = super.query(sql.toString(), selectionArgs);
        try
        {
            List<CommunicationLog> list = new ArrayList<CommunicationLog>();
            if (null != cursor && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    list.add(parseCursorToCommunicationLog(cursor));
                    cursor.moveToNext();
                }
                
            }
            return list;
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
            return new ArrayList<CommunicationLog>();
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
            
        }
    }
    
    /**
     * 根据所属用户ID删除通讯记录
     * @param ownerUserId
     *      所属用户id
     * @param remoteUri
     *      对方用户Uri
     * @param remotePhoneNum
     *      对方电话号码
     */
    public void deleteByRemoteUriOrRemotePhoneNum(String ownerUserId,
            String remoteUri, String remotePhoneNum)
    {
        StringBuilder where = new StringBuilder();
        String[] selectionArgs = null;
        where.append("ownerUserId=? AND ");
        if (remoteUri != null)
        {
            where.append("remoteUri=?");
            selectionArgs = new String[] { ownerUserId, remoteUri };
        }
        else if (remotePhoneNum != null)
        {
            where.append("remotePhoneNum=?");
            selectionArgs = new String[] { ownerUserId, remotePhoneNum };
        }
        super.delete(where.toString(), selectionArgs);
        
    }
    
    /**
     * 根据所属用户来电的电话或URI更新为已读
     * @param ownerUserId
     *      所属用户ID
     * @param remoteUri
     *      对方的URI
     * @param remotePhoneNum
     *      对方的号码
     */
    public void updateToIsReadByOwnerUserId(String ownerUserId,
            String remoteUri, String remotePhoneNum)
    {
        ContentValues values = new ContentValues();
        values.put(CommunicationLogColumns.IS_UNREAD, false);
        StringBuilder where = new StringBuilder();
        String[] selectionArgs = null;
        where.append("ownerUserId=? AND isUnread=? AND ");
        if (remoteUri != null)
        {
            where.append("remoteUri=?");
            selectionArgs = new String[] { ownerUserId, remoteUri };
        }
        else if (remotePhoneNum != null)
        {
            where.append("remotePhoneNum=?");
            selectionArgs = new String[] { ownerUserId, "1", remotePhoneNum };
        }
        super.update(values, where.toString(), selectionArgs);
    }
    
    /**
     * 读取游标的数据封装成VOIP账号对象
     * @param cursor
     *      游标对象
     * @return
     *      VOIP账号对象
     */
    private static CommunicationLog parseCursorToCommunicationLog(Cursor cursor)
    {
        CommunicationLog communicationLog = new CommunicationLog();
        communicationLog.setId(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.ID)));
        communicationLog.setCallId(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.CALL_ID)));
        communicationLog.setRemoteUserId(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_USER_ID)));
        communicationLog.setRemoteUri(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_URI)));
        communicationLog.setRemoteDisplayName(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_DISPLAY_NAME)));
        communicationLog.setRemotePhoneNum(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.REMOTE_PHONE_NUM)));
        communicationLog.setType(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.TYPE)));
        communicationLog.setSort(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.SORT)));
        communicationLog.setFaceUrl(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.FACE_URI)));
        communicationLog.setFaceData(cursor.getBlob(cursor.getColumnIndex(CommunicationLogColumns.FACE_DATA)));
        communicationLog.setCallDate(cursor.getString(cursor.getColumnIndex(CommunicationLogColumns.CALL_DATE)));
        communicationLog.setIsUnread(cursor.getInt(cursor.getColumnIndex(CommunicationLogColumns.IS_UNREAD)) == 1);
        communicationLog.setCallTime(getDateFormCursor(cursor,
                CommunicationLogColumns.CALL_TIME));
        communicationLog.setStartTime(getDateFormCursor(cursor,
                CommunicationLogColumns.START_TIME));
        communicationLog.setEndTime(getDateFormCursor(cursor,
                CommunicationLogColumns.END_TIME));
        return communicationLog;
    }
    
    /**
     * 封装VoipAccount对象为ContentValues
     * @param voipAccount
     *       VOIP账号对象
     * @return
     *       ContentValues对象
     */
    private static ContentValues getContentValues(
            CommunicationLog communicationLog)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CommunicationLogColumns.CALL_DATE,
                communicationLog.getCallDate());
        contentValues.put(CommunicationLogColumns.CALL_ID,
                communicationLog.getCallId());
        contentValues.put(CommunicationLogColumns.FACE_DATA,
                communicationLog.getFaceData());
        contentValues.put(CommunicationLogColumns.FACE_URI,
                communicationLog.getFaceUrl());
        contentValues.put(CommunicationLogColumns.OWNER_USER_ID,
                communicationLog.getOwnerUserId());
        contentValues.put(CommunicationLogColumns.REMOTE_PHONE_NUM,
                communicationLog.getRemotePhoneNum());
        contentValues.put(CommunicationLogColumns.REMOTE_DISPLAY_NAME,
                communicationLog.getRemoteDisplayName());
        contentValues.put(CommunicationLogColumns.REMOTE_URI,
                communicationLog.getRemoteUri());
        contentValues.put(CommunicationLogColumns.REMOTE_USER_ID,
                communicationLog.getRemoteUserId());
        contentValues.put(CommunicationLogColumns.IS_UNREAD,
                communicationLog.getIsUnread());
        contentValues.put(CommunicationLogColumns.SORT,
                communicationLog.getSort());
        contentValues.put(CommunicationLogColumns.TYPE,
                communicationLog.getType());
        contentValues.put(CommunicationLogColumns.CALL_TIME,
                changeDateToString(communicationLog.getCallTime()));
        contentValues.put(CommunicationLogColumns.START_TIME,
                changeDateToString(communicationLog.getStartTime()));
        contentValues.put(CommunicationLogColumns.END_TIME,
                changeDateToString(communicationLog.getEndTime()));
        return contentValues;
    }
    
}
