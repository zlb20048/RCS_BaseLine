/*
 * 文件名: PluginContentProvider.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 10, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.Tables;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.UriStrings;

/**
 * 插件的contentProvider<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 10, 2012] 
 */
public class PluginContentProvider extends ContentProvider
{
    
    private static final int PLUGIN = 0x00000001;
    
    /**
     * URI键值队
     */
    private static final UriMatcher URIMATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);
    
    static
    {
        URIMATCHER.addURI(UriStrings.AUTHORITY, "plugin", PLUGIN);
    }
    
    private SQLiteOpenHelper mOpenHelper;
    
    private Context mContext;
    
    private ContentResolver mContentResolver;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(Uri uri, String selection, String[] args)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int result = db.delete(Tables.PLUGIN, selection, args);
        if (result >= 1)
        {
            mContentResolver.notifyChange(uri, null);
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(Uri uri)
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        if (null != values)
        {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            long result = db.insert(Tables.PLUGIN, null, values);
            if (result != -1)
            {
                Uri resultUri = ContentUris.withAppendedId(PluginURIField.PLUGIN_INSERT_WITH_ID,
                        result);
                if (null != values.get(PluginColumns.PLUGIN_ID))
                {
                    mContentResolver.notifyChange(Uri.withAppendedPath(PluginURIField.PLUGIN_INSERT_WITH_ID,
                            (String) values.get(PluginColumns.PLUGIN_ID)),
                            null);
                }
                
                return resultUri;
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate()
    {
        mContext = getContext();
        mOpenHelper = PluginDbHelper.getInstance(mContext);
        mContentResolver = mContext.getContentResolver();
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * FROM ").append(Tables.PLUGIN);
        if (null != selection)
        {
            sb.append(" WHERE ");
            sb.append(selection);
        }
        return db.rawQuery(sb.toString(), selectionArgs);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int result = db.update(Tables.PLUGIN, values, selection, selectionArgs);
        if (result >= 1)
        {
            mContentResolver.notifyChange(uri, null);
        }
        return result;
    }
}
