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
import com.huawei.basic.android.im.component.database.DatabaseHelper.FaceThumbnailColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 用户头像表数据库操作类适配器<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-16]
 */
public class FaceThumbnailDbAdapter
{
    /**
     * TAG
     */
    private static final String TAG = "FaceThumbnailDbAdapter";
    
    /**
     * FaceThumbnailDbAdapter对象
     */
    private static FaceThumbnailDbAdapter sInstance;
    
    /**
     * 数据库表内容解释器对象
     */
    private ContentResolver mCr;
    
    /**
     * 构造方法
     * 
     * @param context 上下文
     */
    private FaceThumbnailDbAdapter(Context context)
    {
        mCr = context.getContentResolver();
    }
    
    /**
     * 获取FaceThumbnailDbAdapter对象<BR>
     * 单例
     * 
     * @param context 上下文
     * @return FaceThumbnailDbAdapter
     */
    public static synchronized FaceThumbnailDbAdapter getInstance(
            Context context)
    {
        if (null == sInstance)
        {
            sInstance = new FaceThumbnailDbAdapter(context);
        }
        return sInstance;
    }
    
    /**
     * 插入头像<BR>
     * 
     * @param ft 需要插入的头像对象
     * @return 成功：插入后记录的条数<br>
     *         失败：-1
     */
    public long insertFaceThumbnail(FaceThumbnailModel ft)
    {
        long result = -1;
        if (null != ft && !StringUtil.isNullOrEmpty(ft.getFaceId()))
        {
            Uri uri = URIField.FACETHUMBNAIL_URI;
            
            ContentValues cv = setValues(ft);
            if (null == cv)
            {
                return result;
            }
            
            Uri resultUri = mCr.insert(uri, cv);
            
            if (null != resultUri)
            {
                result = ContentUris.parseId(resultUri);
            }
        }
        else
        {
            Logger.w(TAG, "insertFaceThumbnail fail, ft is null...");
        }
        return result;
    }
    
    /**
     * 删除头像<BR>
     * 
     * @param faceId 像ID，可以是sysID和群组ID
     * @return 成功：删除的条数<br>
     *         失败：-1
     */
    public int deleteByFaceId(String faceId)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(faceId))
        {
            Uri uri = URIField.FACETHUMBNAIL_URI;
            
            result = mCr.delete(uri,
                    FaceThumbnailColumns.FACE_ID + "=? ",
                    new String[] { faceId });
            
            Logger.i(TAG, "deleteByFaceId, result = " + result);
        }
        return result;
    }
    
    /**
     * 根据头像ID修改头像。<BR>
     * [全量修改] 会修改传入参数ft的所有属性
     * 
     * @param faceId 头像ID
     * @param ft 需要修改的头像对象
     * @return 成功：修改的条数<br>
     *         失败：-1
     */
    public int updateByFaceId(String faceId, FaceThumbnailModel ft)
    {
        int result = -1;
        if (!StringUtil.isNullOrEmpty(faceId) && null != ft)
        {
            Uri uri = URIField.FACETHUMBNAIL_URI;
            ContentValues cv = this.setValues(ft);
            if (null == cv)
            {
                return result;
            }
            
            result = mCr.update(uri,
                    cv,
                    FaceThumbnailColumns.FACE_ID + "=?",
                    new String[] { faceId });
            
            Logger.i(TAG, "updateByFaceId, result = " + result);
        }
        else
        {
            Logger.w(TAG, "updateByFaceId, faceId or ft is null...");
        }
        return result;
    }
    
    /**
     * 根据faceId和faceType查询头像<BR>
     * 
     * @param faceId 头像ID，可以是hitalk id和群组ID
     * @return 成功：头像对象 <br>
     *         失败：null
     */
    public FaceThumbnailModel queryByFaceId(String faceId)
    {
        FaceThumbnailModel ft = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(faceId))
            {
                cursor = queryByFaceIdWithCursor(faceId);
                
                if (null != cursor && cursor.moveToFirst())
                {
                    ft = parseCursorToFaceThumbnail(cursor);
                }
                else
                {
                    Logger.i(TAG, "query, cursor is null...");
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
        return ft;
    }
    
    /**
     * 根据faceID 更新或者插入数据（如果数据不存在的话,如果数据库中存在数据，
     * faceurl没有变化，就不用操作，直接返回1，如果数据faceurl发生变化，
     * 需要把bytes数据同时更新为空<BR>
     * @param model model
     * @return 插入或者更新的条数
     */
    public int updateOrInsert(FaceThumbnailModel model)
    {
        FaceThumbnailModel savedModel = queryByFaceId(model.getFaceId());
        //数据库中已存在并且记录没有变动
        if (null != savedModel)
        {
            if (savedModel.getFaceUrl() != null
                    && savedModel.getFaceUrl().equals(model.getFaceUrl()))
            {
                return 1;
            }
            else
            {
                return updateByFaceId(model.getFaceId(), model);
            }
        }
        else
        {
            if (insertFaceThumbnail(model) > 0)
            {
                return 1;
            }
        }
        
        return -1;
    }
    
    /**
     * 根据faceUrl查询头像<BR>
     * 
     * @param faceUrl 头像URL
     * @return FaceThumbnailModel
     */
    public FaceThumbnailModel queryByFaceUrl(String faceUrl)
    {
        FaceThumbnailModel ft = null;
        Cursor cursor = null;
        try
        {
            if (!StringUtil.isNullOrEmpty(faceUrl))
            {
                Uri uri = URIField.FACETHUMBNAIL_URI;
                cursor = mCr.query(uri, null, FaceThumbnailColumns.FACE_URL
                        + "=? ", new String[] { faceUrl }, null);
                
                if (null != cursor && cursor.moveToFirst())
                {
                    ft = parseCursorToFaceThumbnail(cursor);
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
        return ft;
    }
    
    /**
     * 查询所有人的头像<BR>
     * 
     * @return 成功：头像对象list <br>
     *         失败：null
     */
    public List<FaceThumbnailModel> queryAll()
    {
        List<FaceThumbnailModel> list = null;
        Cursor cursor = null;
        try
        {
            FaceThumbnailModel ft = null;
            Uri uri = URIField.FACETHUMBNAIL_URI;
            cursor = mCr.query(uri, null, null, null, null);
            
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<FaceThumbnailModel>();
                while (cursor.moveToNext())
                {
                    ft = parseCursorToFaceThumbnail(cursor);
                    list.add(ft);
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
     * 根据faceId和faceType查询头像<BR>
     * 
     * @param faceId 头像ID，可以是hitalk id 和 群组ID
     * @return 成功：Cursor <br>
     *         失败：null
     */
    private Cursor queryByFaceIdWithCursor(String faceId)
    {
        Cursor cursor = null;
        if (!StringUtil.isNullOrEmpty(faceId))
        {
            Uri uri = URIField.FACETHUMBNAIL_URI;
            cursor = mCr.query(uri,
                    null,
                    FaceThumbnailColumns.FACE_ID + "=? ",
                    new String[] { faceId },
                    null);
        }
        else
        {
            Logger.w(TAG, "queryByFaceId, faceId is null...");
        }
        return cursor;
    }
    
    /**
     * [一句话功能简述]<BR>
     * 
     * @param ft FaceThumbnailModel
     * @return ContentValues
     */
    private ContentValues setValues(FaceThumbnailModel ft)
    {
        if (StringUtil.isNullOrEmpty(ft.getFaceUrl())
                && null == ft.getFaceBytes())
        {
            return null;
        }
        ContentValues cv = new ContentValues();
        cv.put(FaceThumbnailColumns.FACE_ID, ft.getFaceId());
        cv.put(FaceThumbnailColumns.FACE_BYTES, ft.getFaceBytes());
        cv.put(FaceThumbnailColumns.FACE_URL, ft.getFaceUrl());
        cv.put(FaceThumbnailColumns.FACE_FILE_PATH , ft.getFaceFilePath());
        return cv;
    }
    
    /**
     * 根据游标解析用户头像信息<BR>
     * 
     * @param cursor 游标对象
     * @return FaceThumbnailModel
     */
    private FaceThumbnailModel parseCursorToFaceThumbnail(Cursor cursor)
    {
        FaceThumbnailModel ft = new FaceThumbnailModel();
        ft.setFaceId(cursor.getString(cursor.getColumnIndex(FaceThumbnailColumns.FACE_ID)));
        ft.setFaceBytes(cursor.getBlob(cursor.getColumnIndex(FaceThumbnailColumns.FACE_BYTES)));
        ft.setFaceUrl(cursor.getString(cursor.getColumnIndex(FaceThumbnailColumns.FACE_URL)));
        ft.setFaceFilePath(cursor.getString(cursor.getColumnIndex(FaceThumbnailColumns.FACE_FILE_PATH)));
        return ft;
    }
    
}
