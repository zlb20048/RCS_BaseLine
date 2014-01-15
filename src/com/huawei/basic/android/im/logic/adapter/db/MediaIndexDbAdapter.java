/*
 * 文件名: MediaIndexDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 27, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.utils.FileUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 多媒体文件索引数据操作适配器<BR>
 * 
 * @author 张仙
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 27, 2012] 
 */
public class MediaIndexDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "MediaIndexAdapter";
    
    private static final int MEDIA_AUTO_DOWNLOAD_TRY_MAX_TIMES = 3;
    
    /**
     * MediaIndexAdapter对象
     */
    private static MediaIndexDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    private MediaIndexDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取MediaIndexAdapter对象<BR>
     * 单例
     *
     * @param context 上下文
     * @return MediaIndexAdapter
     */
    public static synchronized MediaIndexDbAdapter getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new MediaIndexDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入多媒体文件<BR>
     *
     * @param media 插入对象
     * @return 成功：插入后记录的行数<br>
     *         失败：-1
     */
    public long insertMediaIndex(MediaIndexModel media)
    {
        long result = -1;
        try
        {
            if (null != media)
            {
                Uri uri = null;
                int mediaType = media.getMediaType();
                uri = URIField.MEDIAINDEX_URI;
                ContentValues cv = new ContentValues();
                cv.put(MediaIndexColumns.MSG_ID, media.getMsgId());
                if (MediaIndexModel.MEDIATYPE_UNKNOWN != mediaType)
                {
                    cv.put(MediaIndexColumns.MEDIA_TYPE, mediaType);
                }
                cv.put(MediaIndexColumns.MEDIA_ALT, media.getMediaAlt());
                cv.put(MediaIndexColumns.MEDIA_SIZE, media.getMediaSize());
                cv.put(MediaIndexColumns.MEDIA_PATH, media.getMediaPath());
                cv.put(MediaIndexColumns.MEDIA_SMALL_PATH,
                        media.getMediaSmallPath());
                cv.put(MediaIndexColumns.MEDIA_URL, media.getMediaURL());
                cv.put(MediaIndexColumns.MEDIA_SMALL_URL,
                        media.getMediaSmallURL());
                cv.put(MediaIndexColumns.PLAY_TIME, media.getPlayTime());
                cv.put(MediaIndexColumns.LOCATION_LAT, media.getLocationLat());
                cv.put(MediaIndexColumns.LOCATION_LON, media.getLocationLon());
                Uri resultUri = mCr.insert(uri, cv);
                
                if (null != resultUri)
                {
                    result = ContentUris.parseId(resultUri);
                }
                Logger.i(TAG, "insertMediaIndex, result = " + result);
            }
            else
            {
                Logger.w(TAG, "insertMediaIndex fail, media is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 删除一物条消息所对应的多媒体信息，同时删除存储的多媒体文件.<BR>
     *
     * @param msgId 消息ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public synchronized int deleteByMsgId(String msgId)
    {
        int result = -1;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(msgId))
            {
                Uri uri = URIField.MEDIAINDEX_URI;
                
                cursor = this.queryByMsgIdWithCursor(msgId);
                if (null == cursor || !cursor.moveToFirst())
                {
                    return result;
                }
                //用于传递多媒体文件路径信息的map
                HashMap<String, String> mapPath = new HashMap<String, String>();
                //根据cursor获得原始文件路径
                String tempFilePath = cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_PATH));
                //获得缩略图路径
                String tempFileSmallPath = cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_PATH));
                //起线程删除存储在本地的多媒体文件
                mapPath.put(tempFilePath, tempFileSmallPath);
                //起线程删除存储在本地的多媒体文件
                new DeleteMediaFileThread(mapPath).start();
                
                result = mCr.delete(uri,
                        MediaIndexColumns.MSG_ID + "=? ",
                        new String[] { msgId });
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
            DatabaseHelper.closeCursor(cursor);
        }
        return result;
    }
    
    /**
     * 删除一个会话中的所有多媒体信息
     * @param userSysId 用户系统ID
     * @param conversationId 会话ID
     * @return
     */
    public synchronized void deleteAllMediaByConversationId(String userSysId,
            String conversationId)
    {
        Cursor mediaCursor = null;
        if (StringUtil.isNullOrEmpty(userSysId)
                || StringUtil.isNullOrEmpty(conversationId))
        {
            return;
        }
        
        try
        {
            // 先查询所有多媒体消息Cursor
            Uri queryMediaUri = Uri.withAppendedPath(URIField.MESSAGE_QUERY_MEDIAINDEX_URI,
                    userSysId + "/" + conversationId);
            mediaCursor = mCr.query(queryMediaUri, null, null, null, null);
            if (mediaCursor == null || !mediaCursor.moveToFirst())
            {
                return;
            }
            
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            
            //存储多媒体信息路径路径 前面为原始文件路径。后面为缩略文件路径
            HashMap<String, String> mapFilePath = new HashMap<String, String>();
            while (!mediaCursor.isAfterLast())
            {
                String msgId = mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MSG_ID));
                String tempFilePath = mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_PATH));
                String tempFileSmallPath = mediaCursor.getString(mediaCursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_PATH));
                ContentProviderOperation operation = ContentProviderOperation.newDelete(URIField.MEDIAINDEX_URI)
                        .withSelection(MediaIndexColumns.MSG_ID + " =? ",
                                new String[] { msgId })
                        .build();
                operations.add(operation);
                mapFilePath.put(tempFilePath, tempFileSmallPath);
                mediaCursor.moveToNext();
            }
            //删除信息中存储在本地的多媒体文件
            new DeleteMediaFileThread(mapFilePath).start();
            
            // 批量删除多媒体记录
            mCr.applyBatch(URIField.AUTHORITY, operations);
            
        }
        catch (RemoteException e)
        {
            DatabaseHelper.printException(e);
        }
        catch (OperationApplicationException e)
        {
            DatabaseHelper.printException(e);
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(mediaCursor);
        }
        
    }
    
    /**
     * 删除一物条消息所对应的多媒体信息<BR>
     *
     * @param msgId 消息ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByMsgIdNoDelFile(String msgId)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(msgId))
            {
                Uri uri = URIField.MEDIAINDEX_URI;
                
                result = mCr.delete(uri,
                        MediaIndexColumns.MSG_ID + "=? ",
                        new String[] { msgId });
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
        return result;
    }
    
    /**
     * 通过msgId和mediaUrl删除多媒体信息<BR>
     *
     * @param msgId 消息ID
     * @param mediaUrl 多媒体URL
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByUrl(String msgId, String mediaUrl)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(msgId))
            {
                Uri uri = URIField.MEDIAINDEX_URI;
                result = mCr.delete(uri, MediaIndexColumns.MSG_ID + "=? AND "
                        + MediaIndexColumns.MEDIA_URL + "=?", new String[] {
                        msgId, mediaUrl });
                Logger.i(TAG, "deleteBy, result = " + result);
                
            }
            else
            {
                Logger.w(TAG, "deleteBy fail, msgId is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 根据MsgId更新多媒体操作表<BR>
     * [通用方法]
     *
     * @param msgId 消息ID
     * @param contentValues 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int update(String msgId, ContentValues contentValues)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(msgId) && null != contentValues
                    && 0 < contentValues.size())
            {
                Uri uri = URIField.MEDIAINDEX_URI;
                result = mCr.update(uri,
                        contentValues,
                        MediaIndexColumns.MSG_ID + "=?",
                        new String[] { msgId });
                Logger.i(TAG, "update, result = " + result);
            }
            else
            {
                Logger.w(TAG, "update fail, msgId is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 根据msgId和mediaUrl修改多媒体信息<BR>
     * [通用方法]
     *
     * @param conversationId 会话ID
     * @param msgId 消息ID
     * @param contentValues 需要修改的字段
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int update(String conversationId, String msgId,
            ContentValues contentValues)
    {
        int result = -1;
        try
        {
            if (!StringUtil.isNullOrEmpty(conversationId)
                    && !StringUtil.isNullOrEmpty(msgId)
                    && null != contentValues && 0 < contentValues.size())
            {
                
                Uri uri = Uri.withAppendedPath(URIField.MEDIAINDEX_CONVERSATIONID_URI,
                        conversationId);
                
                result = mCr.update(uri,
                        contentValues,
                        MediaIndexColumns.MSG_ID + "=?",
                        new String[] { msgId });
                Logger.i(TAG, "update, result = " + result);
            }
            else
            {
                Logger.w(TAG, "update fail, msgId is null...");
            }
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        return result;
    }
    
    /**
     * 获得所有类型为audio或者video的消息ID<BR>
     * @return 所有类型为audio或者video的消息ID的字符串
     */
    public String getAllAudioAndVideoIds()
    {
        String retStr = null;
        Cursor cursor = null;
        try
        {
            StringBuffer sb = new StringBuffer();
            Uri uri = URIField.MEDIAINDEX_URI;
            String[] projection = new String[] { MediaIndexColumns.MSG_ID };
            String selection = MediaIndexColumns.MEDIA_TYPE + " =? OR "
                    + MediaIndexColumns.MEDIA_TYPE + " =? OR "
                    + MediaIndexColumns.MEDIA_TYPE + " =? ";
            String[] selectionArgs = new String[] {
                    String.valueOf(MediaIndexModel.MEDIATYPE_AUDIO),
                    String.valueOf(MediaIndexModel.MEDIATYPE_VIDEO),
                    String.valueOf(MediaIndexModel.MEDIATYPE_IMG) };
            cursor = mCr.query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    if (cursor.isFirst())
                    {
                        sb.append("(");
                    }
                    sb.append("'").append(cursor.getString(0)).append("'");
                    if (cursor.isLast())
                    {
                        sb.append(")");
                    }
                    else
                    {
                        sb.append(",");
                    }
                    cursor.moveToNext();
                }
            }
            retStr = sb.toString();
        }
        catch (Exception e)
        {
            DatabaseHelper.printException(e);
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
        
        return retStr;
    }
    
    /**
     * 根据msgId和mediaUrl查询多媒体信息<BR>
     *
     * @param msgId 消息ID
     * @param mediaUrl 多媒体URL
     * @return 成功：多媒体对象;<br>
     *         失败：null
     */
    public MediaIndexModel queryByMsgIdAndUrl(String msgId, String mediaUrl)
    {
        MediaIndexModel media = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(msgId)
                    && !StringUtil.isNullOrEmpty(mediaUrl))
            {
                Uri uri = URIField.MEDIAINDEX_URI;
                cursor = mCr.query(uri,
                        null,
                        MediaIndexColumns.MSG_ID + "=? AND "
                                + MediaIndexColumns.MEDIA_URL + "=?",
                        new String[] { msgId, mediaUrl },
                        null);
                if (null != cursor && cursor.moveToFirst())
                {
                    media = parseCursorToMediaIndexModel(cursor);
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
        return media;
    }
    
    /**
     * 根据msgId查询多媒体信息<BR>
     *
     * @param msgId 消息ID
     * @return 成功：Cursor;<br>
     *         失败：null
     */
    public Cursor queryByMsgIdWithCursor(String msgId)
    {
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(msgId))
            {
                Uri uri = URIField.MEDIAINDEX_URI;
                cursor = mCr.query(uri,
                        null,
                        MediaIndexColumns.MSG_ID + "=? ",
                        new String[] { msgId },
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
     * 根据msgId查询多媒体信息<BR>
     *
     * @param msgId 消息ID
     * @return 成功：多媒体列表;<br>
     *         失败：null
     */
    public MediaIndexModel queryByMsgId(String msgId)
    {
        MediaIndexModel mediaModel = null;
        Cursor cursor = null;
        try
        {
            
            cursor = queryByMsgIdWithCursor(msgId);
            if (null != cursor && cursor.moveToFirst())
            {
                mediaModel = parseCursorToMediaIndexModel(cursor);
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
        return mediaModel;
    }
    
    /**
     * 根据游标解析多媒体信息<BR>
     *
     * @param cursor 游标对象
     * @return 多媒体信息
     */
    private MediaIndexModel parseCursorToMediaIndexModel(Cursor cursor)
    {
        MediaIndexModel media = new MediaIndexModel();
        media.setMsgId(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MSG_ID)));
        media.setMediaType(cursor.getInt(cursor.getColumnIndex(MediaIndexColumns.MEDIA_TYPE)));
        media.setMediaSize(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_SIZE)));
        media.setMediaPath(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_PATH)));
        media.setMediaSmallPath(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_PATH)));
        media.setMediaURL(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_URL)));
        media.setMediaSmallURL(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_SMALL_URL)));
        media.setMediaAlt(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.MEDIA_ALT)));
        media.setPlayTime(cursor.getInt(cursor.getColumnIndex(MediaIndexColumns.PLAY_TIME)));
        media.setDownloadTryTimes(cursor.getInt(cursor.getColumnIndex(MediaIndexColumns.DOWNLOAD_TRY_TIMES)));
        media.setLocationLat(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.LOCATION_LAT)));
        media.setLocationLon(cursor.getString(cursor.getColumnIndex(MediaIndexColumns.LOCATION_LON)));
        return media;
    }
    
    /**
     * 标志下载失败的音频消息
     */
    public void setAudioMediaToDownloadFailed()
    {
        ContentValues values = new ContentValues(1);
        values.put(MediaIndexColumns.DOWNLOAD_TRY_TIMES,
                MEDIA_AUTO_DOWNLOAD_TRY_MAX_TIMES + 1);
        StringBuilder buf = new StringBuilder();
        buf.append(MediaIndexColumns.DOWNLOAD_TRY_TIMES);
        buf.append("=? AND ");
        buf.append(MediaIndexColumns.MEDIA_TYPE);
        buf.append("=? AND (");
        buf.append(MediaIndexColumns.MEDIA_PATH);
        buf.append(" IS NULL OR ");
        buf.append(MediaIndexColumns.MEDIA_PATH);
        buf.append("='')");
        mCr.update(URIField.MEDIAINDEX_URI,
                values,
                buf.toString(),
                new String[] {
                        String.valueOf(MEDIA_AUTO_DOWNLOAD_TRY_MAX_TIMES),
                        String.valueOf(MediaIndexModel.MEDIATYPE_AUDIO) });
    }
    
    /**
     * 删除多媒体文件线程<BR>
     * 
     * @author zhaozeyang
     */
    class DeleteMediaFileThread extends Thread
    {
        //要删除的多媒体记录
        private HashMap<String, String> mPathMap;
        
        /**
         * 构造函数
         * @param pathMap key和value分别存储原文件路径和缩略文件路径
         */
        public DeleteMediaFileThread(HashMap<String, String> pathMap)
        {
            this.mPathMap = pathMap;
        }
        
        @Override
        public void run()
        {
            try
            {
                if (mPathMap != null)
                {
                    Iterator<Entry<String, String>> it = mPathMap.entrySet()
                            .iterator();
                    while (it.hasNext())
                    {
                        Map.Entry<String, String> entry = (Entry<String, String>) it.next();
                        String tempFilepath = entry.getKey();
                        String tempFileSmallpath = entry.getValue();
                        if (!StringUtil.isNullOrEmpty(tempFilepath))
                        {
                            FileUtil.deleteFile(tempFilepath);
                        }
                        if (!StringUtil.isNullOrEmpty(tempFileSmallpath))
                        {
                            FileUtil.deleteFile(tempFileSmallpath);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Logger.e(TAG, "MediaIndexDbAdapter : DeleteMediaFileThread", e);
            }
        }
    }
}
