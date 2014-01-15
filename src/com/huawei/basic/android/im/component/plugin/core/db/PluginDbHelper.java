/*
 * 文件名: PluginDbHelper.java
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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 插件的DbHelper<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 10, 2012] 
 */
public class PluginDbHelper extends SQLiteOpenHelper
{
    /**
     * TAG
     */
    private static final String TAG = "PluginDbHelper";
    
    /**
     * 插件的数据库名称
     */
    private static final String DB_NAME = "rcsbaseline_plugin.db";
    
    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;
    
    /**
     * 插件数据库操作
     */
    private static PluginDbHelper sPluginDbHelper;
    
    /**
     * {@inheritDoc}
     */
    private PluginDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    /**
     * 获取插件对象的实例
     * @param context context上下文
     * @return PluginDbHelper
     */
    public static synchronized PluginDbHelper getInstance(Context context)
    {
        if (null == sPluginDbHelper)
        {
            sPluginDbHelper = new PluginDbHelper(context);
        }
        return sPluginDbHelper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        createPluginTable(db);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.e(TAG, "database update ...");
    }
    
    /**
     * 表名称<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 20, 2012]
     */
    public interface Tables
    {
        
        /**
         * 插件表名称
         */
        String PLUGIN = "plugin";
    }
    
    /**
     * Uri串<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 11, 2012]
     */
    public interface UriStrings
    {
        /**
         * 鉴权串
         */
        String AUTHORITY = "com.huawei.rcsbaseline.database.plugin";
        
        /**
         * 删除Plugin记录的URI
         */
        String PLUGIN_DELETE_WITH_ID = "plugin_delete_with_id/";
        
        /**
         * 更新Plugin记录的URI
         */
        String PLUGIN_UPDATE_WITH_ID = "plugin_update_with_id/";
        
        /**
         * 插入Plugin记录的URI
         */
        String PLUGIN_INSERT_WITH_ID = "plugin_insert_with_id/";
    }
    
    /**
     * 创建插件表<BR>
     */
    private void createPluginTable(SQLiteDatabase db)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ").append(Tables.PLUGIN).append("(");
        sb.append(PluginColumns.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append(PluginColumns.ARCHIVE_FILE_PATH).append(" TEXT,");
        sb.append(PluginColumns.DESC).append(" TEXT,");
        sb.append(PluginColumns.ICON).append(" BLOB,");
        sb.append(PluginColumns.NAME).append(" TEXT,");
        sb.append(PluginColumns.PUB_TIME).append(" TEXT,");
        sb.append(PluginColumns.VERSION).append(" INTEGER,");
        sb.append(PluginColumns.PACKAGE_NAME).append(" TEXT,");
        sb.append(PluginColumns.START_ACTION).append(" TEXT,");
        sb.append(PluginColumns.TYPE).append(" INTEGER,");
        sb.append(PluginColumns.URL).append(" TEXT,");
        sb.append(PluginColumns.ICON_URL).append(" TEXT,");
        sb.append(PluginColumns.PLUGIN_ID).append(" TEXT,");
        sb.append(PluginColumns.STATUS).append(" INTEGER,");
        sb.append(PluginColumns.NEED_UPDATE).append(" INTEGER DEFAULT 0,");
        sb.append(PluginColumns.INTENT_EXTRA).append(" TEXT,");
        sb.append(PluginColumns.SHOW_IN_CONTACTLIST).append(" INTEGER)");
        db.execSQL(sb.toString());
    }
    
    /**
     * Columns<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 10, 2012]
     */
    public interface PluginColumns
    {
        /**
         * ID
         */
        String ID = "_id";
        
        /**
         * APK存储路径
         */
        String ARCHIVE_FILE_PATH = "archive_file_path";
        
        /**
         * 插件名称
         */
        String NAME = "name";
        
        /**
         * 插件的头像数据
         */
        String ICON = "icon";
       
        /**
         * 插件描述
         */
        String DESC = "desc";
        
        /**
         * 插件发布时间
         */
        String PUB_TIME = "pub_time";
        
        /**
         * 版本号
         */
        String VERSION = "version";
        
        /**
         * 是否在好友列表展现
         */
        String SHOW_IN_CONTACTLIST = "show_in_contactlist";
        
        /**
         * 包名
         */
        String PACKAGE_NAME = "package_name";
        
        /**
         * 插件的状态
         */
        String STATUS = "status";
        
        /**
         * 插件的ID
         */
        String PLUGIN_ID = "plugin_id";
        
        /**
         * 插件的下载地址
         */
        String URL = "url";
        
        /**
         * 插件启动的Action
         */
        String START_ACTION = "start_action";
        
        /**
         * 插件的类型
         */
        String TYPE = "type";
        
        /**
         * 是否需要更新
         */
        String NEED_UPDATE = "need_update";
        
        /**
         * 插件图标的URL
         */
        String ICON_URL = "icon_url";
        
        /**
         * intent传递的参数
         */
        String INTENT_EXTRA = "intent_extra";
    }
}
