/*
 * 文件名: ApkDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 21, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.plugins.apk;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.db.BasePluginDbAdapter;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.component.plugin.core.db.PluginURIField;

/**
 * Apk插件数据库操作<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 21, 2012] 
 */
public class ApkPluginDbAdapter extends BasePluginDbAdapter
{
    private static ApkPluginDbAdapter sPluginDbAdapter;
    
    /**
     * 构造方法私有化
     */
    private ApkPluginDbAdapter()
    {
        
    }
    
    /**
     * 获取实例<BR>
     * @return 实例对象
     */
    public static synchronized ApkPluginDbAdapter getInstance()
    {
        if (null == sPluginDbAdapter)
        {
            sPluginDbAdapter = new ApkPluginDbAdapter();
        }
        return sPluginDbAdapter;
    }
    
    /**
     * 初始化<BR>
     * @param context context上下文
     */
    public void init(Context context)
    {
        this.setmContext(context);
        this.setmCr(context.getContentResolver());
    }
    
    /**
     * 插入数据<BR>
     * @param apkPlugin apk插件对象
     * @return id
     */
    public long insertApk(ApkPlugin apkPlugin)
    {
        long result = -1;
        Uri uri = PluginURIField.PLUGIN_URI;
        Uri resultUri = getmCr().insert(uri, setApkPluginValues(apkPlugin));
        if (null != resultUri)
        {
            result = ContentUris.parseId(resultUri);
        }
        return result;
    }
    
    /**
     * 根据插件PackageName更新插件<BR>
     * @param cv ContentValues
     * @param packageName 插件的packageName
     * @return 更新记录的条数
     */
    public int updateByPackageName(ContentValues cv, String packageName)
    {
        int result = -1;
        Uri uri = PluginURIField.PLUGIN_URI;
        StringBuffer sb = new StringBuffer();
        sb.append(PluginColumns.PACKAGE_NAME).append(" =? ");
        result = getmCr().update(uri,
                cv,
                sb.toString(),
                new String[] { packageName });
        return result;
    }
    
    private ContentValues setApkPluginValues(ApkPlugin model)
    {
        if (null == model)
        {
            return null;
        }
        ContentValues cv = new ContentValues();
        cv.put(PluginColumns.TYPE, BasePlugin.TYPE_APK);
        cv.put(PluginColumns.ARCHIVE_FILE_PATH, model.getArchiveFilePath());
        cv.put(PluginColumns.DESC, model.getDesc());
        cv.put(PluginColumns.ICON, model.getIcon());
        cv.put(PluginColumns.ICON_URL, model.getIconUrl());
        cv.put(PluginColumns.NAME, model.getName());
        cv.put(PluginColumns.PUB_TIME, model.getPubTime());
        cv.put(PluginColumns.PLUGIN_ID, model.getPluginId());
        cv.put(PluginColumns.SHOW_IN_CONTACTLIST,
                model.isShowInContactList() ? 1 : 0);
        cv.put(PluginColumns.PACKAGE_NAME, model.getPackageName());
        cv.put(PluginColumns.STATUS, model.getStatus());
        cv.put(PluginColumns.VERSION, model.getVersion());
        cv.put(PluginColumns.START_ACTION, model.getStartAction());
        cv.put(PluginColumns.INTENT_EXTRA,
                convertExtrasMapToString(model.getIntentExtras()));
        return cv;
    }
}
