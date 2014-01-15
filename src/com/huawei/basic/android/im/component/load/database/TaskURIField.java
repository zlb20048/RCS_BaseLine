/*
 * 文件名: TaskURIField.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Apr 26, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.load.database;

import com.huawei.basic.android.im.component.load.database.TaskDatabaseHelper.Tables;

import android.net.Uri;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 26, 2012] 
 */
public interface TaskURIField
{
    /**
     * The authority for the contacts provider
     */
    public static final String AUTHORITY = "com.huawei.rcsbaseline.database.task";
    
    /**
     * A content:// style uri to the authority for the rcsbaseline provider
     */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    
    /**
     * 查询SQL操作保留字
     */
    public static final String QUERY = "querySql";
    
    /**
     * 执行SQL操作保留字
     */
    public static final String EXECUTE = "executeSql";
    
    /**
     * 查询URI
     */
    public static final Uri QUERY_SQL_URI = Uri.parse("content://" + AUTHORITY
            + "/" + QUERY);
    
    /**
     * 执行URI
     */
    public static final Uri EXECUTE_SQL_URI = Uri.parse("content://"
            + AUTHORITY + "/" + EXECUTE);
    
    /**
     * 下载任务表URI
     */
    public static final Uri DOWNLOAD_TASK_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.DOWNLOAD_TASK);
    
}
