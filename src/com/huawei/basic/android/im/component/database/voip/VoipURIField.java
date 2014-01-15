/*
 * 文件名: VoipURIField.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: Voip字段定义
 * 创建人: 刘鲁宁
 * 创建时间:Mar 14, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.database.voip;

import android.net.Uri;
import com.huawei.basic.android.im.component.database.voip.VoipDatabaseHelper.Tables;

/**
 * Voip字段定义
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 14, 2012] 
 */
public class VoipURIField
{
    /**
     * The authority for the contacts provider
     */
    public static final String AUTHORITY = "com.huawei.rcsbaseline.database.voip";
    
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
     * Voip账号表URI
     */
    public static final Uri VOIP_ACCOUNT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.VOIP_ACCOUNT);
    
    /**
     * 通话记录表URI
     */
    public static final Uri COMMUNICATION_LOG_URI = Uri.parse("content://"
            + AUTHORITY + "/" + Tables.COMMUNICATION_LOG);
    
}
