/*
 * 文件名: DownloadDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-4-18
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.download;

import java.util.Date;
import java.util.LinkedList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.huawei.basic.android.im.component.download.http.DownloadHttpTask;
import com.huawei.basic.android.im.component.load.adapter.ITaskDataAdapter;
import com.huawei.basic.android.im.component.load.database.TaskDatabaseHelper;
import com.huawei.basic.android.im.component.load.database.TaskDbAdapter;
import com.huawei.basic.android.im.component.load.database.TaskDatabaseHelper.DownloadTaskColumns;
import com.huawei.basic.android.im.component.load.task.TaskOperation;

/**
 * 下载任务数据的具体实现
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-4-18] 
 */
public class DownloadDbAdapter extends TaskDbAdapter implements
        ITaskDataAdapter
{
    /**
     *  构造器
     * @param context Context
     */
    public DownloadDbAdapter(Context context)
    {
        super(context);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LinkedList<TaskOperation> getAllTask()
    {
        Cursor cursor = super.query(null,
                null,
                null,
                DownloadTaskColumns.CREATED_TIME);
        LinkedList<TaskOperation> list = new LinkedList<TaskOperation>();
        try
        {
            if (null != cursor && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    DownloadHttpTask downloadHttpTask = parseCursorToDownloadHttpTask(cursor);
                    list.add(downloadHttpTask);
                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            printException(e);
        }
        finally
        {
            closeCursor(cursor);
        }
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int addTask(TaskOperation task)
    {
        if (task.getClass().isAssignableFrom(getTaskClass()))
        {
            DownloadHttpTask downloadHttpTask = (DownloadHttpTask) task;
            task.setCreatedTime(new Date());
            super.insert(getContentValues(downloadHttpTask));
            return downloadHttpTask.getId();
        }
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTaskStatus(int id, int status, Class<?> taskClass)
    {
        if (taskClass.isAssignableFrom(getTaskClass()))
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadTaskColumns.STATUS, status);
            super.update(contentValues, DownloadTaskColumns.DOWNLOAD_TASK_ID
                    + "=?", new String[] { String.valueOf(id) });
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TaskOperation getTask(int id, Class<?> taskClass)
    {
        if (taskClass.isAssignableFrom(getTaskClass()))
        {
            Cursor cursor = super.query(DownloadTaskColumns.DOWNLOAD_TASK_ID
                    + "=?", new String[] { String.valueOf(id) });
            try
            {
                if (null != cursor && cursor.moveToFirst())
                {
                    while (!cursor.isAfterLast())
                    {
                        return parseCursorToDownloadHttpTask(cursor);
                    }
                }
            }
            catch (Exception e)
            {
                printException(e);
            }
            finally
            {
                closeCursor(cursor);
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTotalSize(int id, long size, Class<?> taskClass)
    {
        if (taskClass.isAssignableFrom(getTaskClass()))
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadTaskColumns.TOTAL_SIZE, size);
            super.update(contentValues, DownloadTaskColumns.DOWNLOAD_TASK_ID
                    + "=?", new String[] { String.valueOf(id) });
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTask(int id, Class<?> taskClass)
    {
        if (taskClass.isAssignableFrom(getTaskClass()))
        {
            super.delete(DownloadTaskColumns.DOWNLOAD_TASK_ID + "=?",
                    new String[] { String.valueOf(id) });
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getTaskClass()
    {
        return DownloadHttpTask.class;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxTaskId()
    {
        Cursor cursor = null;
        try
        {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT MAX(")
                    .append(DownloadTaskColumns.DOWNLOAD_TASK_ID)
                    .append(") maxId FROM ")
                    .append(getTableName());
            cursor = super.query(sql.toString(), null);
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
            printException(e);
        }
        finally
        {
            closeCursor(cursor);
        }
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName()
    {
        // TODO Auto-generated method stub
        return TaskDatabaseHelper.Tables.DOWNLOAD_TASK;
    }
    
    /**
     * 读取游标的数据封装成VOIP账号对象
     * @param cursor
     *      游标对象
     * @return
     *      VOIP账号对象
     */
    private static DownloadHttpTask parseCursorToDownloadHttpTask(Cursor cursor)
    {
        DownloadHttpTask downloadHttpTask = new DownloadHttpTask();
        downloadHttpTask.setId(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.DOWNLOAD_TASK_ID)));
        downloadHttpTask.setName(cursor.getString(cursor.getColumnIndex(DownloadTaskColumns.NAME)));
        downloadHttpTask.setStatus(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.STATUS)));
        downloadHttpTask.setDownloadUrl(cursor.getString(cursor.getColumnIndex(DownloadTaskColumns.DOWNLOAD_URL)));
        downloadHttpTask.setStorePath(cursor.getString(cursor.getColumnIndex(DownloadTaskColumns.STORE_PATH)));
        downloadHttpTask.setTotalSize(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.TOTAL_SIZE)));
        downloadHttpTask.setBackground(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.IS_BACKGROUD)) == 1);
        downloadHttpTask.setIsDeleteFile(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.IS_DELETE_FILE)) == 1);
        downloadHttpTask.setIsPost(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.IS_POST)) == 1);
        downloadHttpTask.setIsProxy(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.IS_PROXY)) == 1);
        downloadHttpTask.setProxyHost(cursor.getString(cursor.getColumnIndex(DownloadTaskColumns.PROXY_HOST)));
        downloadHttpTask.setProxyPort(cursor.getInt(cursor.getColumnIndex(DownloadTaskColumns.PROXY_PROT)));
        downloadHttpTask.setCreatedTime(getDateFormCursor(cursor,
                DownloadTaskColumns.CREATED_TIME));
        return downloadHttpTask;
    }
    
    /**
     * 封装VoipAccount对象为ContentValues
     * @param voipAccount
     *       VOIP账号对象
     * @return
     *       ContentValues对象
     */
    private static ContentValues getContentValues(
            DownloadHttpTask downloadHttpTask)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadTaskColumns.DOWNLOAD_TASK_ID,
                downloadHttpTask.getId());
        contentValues.put(DownloadTaskColumns.NAME, downloadHttpTask.getName());
        contentValues.put(DownloadTaskColumns.STATUS,
                downloadHttpTask.getStatus());
        contentValues.put(DownloadTaskColumns.DOWNLOAD_URL,
                downloadHttpTask.getDownloadUrl());
        contentValues.put(DownloadTaskColumns.STORE_PATH,
                downloadHttpTask.getStorePath());
        contentValues.put(DownloadTaskColumns.TOTAL_SIZE,
                downloadHttpTask.getTotalSize());
        contentValues.put(DownloadTaskColumns.IS_BACKGROUD,
                downloadHttpTask.isBackground());
        contentValues.put(DownloadTaskColumns.IS_DELETE_FILE,
                downloadHttpTask.isDeleteFile());
        contentValues.put(DownloadTaskColumns.IS_POST,
                downloadHttpTask.isPost());
        contentValues.put(DownloadTaskColumns.IS_PROXY,
                downloadHttpTask.isProxy());
        contentValues.put(DownloadTaskColumns.PROXY_HOST,
                downloadHttpTask.getProxyHost());
        contentValues.put(DownloadTaskColumns.PROXY_PROT,
                downloadHttpTask.getProxyPort());
        contentValues.put(DownloadTaskColumns.CREATED_TIME,
                changeDateToString(downloadHttpTask.getCreatedTime()));
        return contentValues;
    }
}
