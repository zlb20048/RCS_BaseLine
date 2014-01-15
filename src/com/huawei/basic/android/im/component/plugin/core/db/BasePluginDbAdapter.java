/*
 * 文件名: PluginDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.component.plugin.plugins.apk.ApkPlugin;
import com.huawei.basic.android.im.component.plugin.plugins.internal.InternalPlugin;

/**
 * 插件数据库操作adapter<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 11, 2012] 
 */
public abstract class BasePluginDbAdapter
{
    
    private Context mContext;
    
    private ContentResolver mCr;
    
    /**
     * 初始化<BR>
     * @param context context上下文
     */
    public abstract void init(Context context);
    
    /**
     * 获取context<BR>
     * @return context
     */
    public Context getmContext()
    {
        return mContext;
    }
    
    /**
     * 设置context<BR>
     * @param context context
     */
    public void setmContext(Context context)
    {
        this.mContext = context;
    }
    
    /**
     * 获取mCr<BR>
     * @return ContentResolver
     */
    public ContentResolver getmCr()
    {
        return mCr;
    }
    
    /**
     * 设置ContentResolver<BR>
     * @param cr ContentResolver
     */
    public void setmCr(ContentResolver cr)
    {
        this.mCr = cr;
    }
    
    /**
     * 根据插件ID查询apk插件<BR>
     * @param pluginId 插件ID
     * @return ApkPlugin
     */
    public ApkPlugin queryApkPluginByPluginId(String pluginId)
    {
        Cursor cursor = null;
        try
        {
            ApkPlugin pluginModel = null;
            Uri uri = PluginURIField.PLUGIN_URI;
            StringBuffer sb = new StringBuffer();
            sb.append(PluginColumns.PLUGIN_ID).append(" =? ");
            sb.append(" AND ")
                    .append(PluginColumns.TYPE)
                    .append(" = ")
                    .append(BasePlugin.TYPE_APK);
            cursor = mCr.query(uri,
                    null,
                    sb.toString(),
                    new String[] { pluginId },
                    null);
            if (null != cursor && cursor.moveToFirst())
            {
                pluginModel = parseApkPluginCursor(cursor);
            }
            return pluginModel;
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    /**
     * 根据插件ID查询Internal插件<BR>
     * @param pluginId 插件ID
     * @return InternalPlugin
     */
    public InternalPlugin queryInternalPluginByPluginId(String pluginId)
    {
        Cursor cursor = null;
        try
        {
            InternalPlugin pluginModel = null;
            Uri uri = PluginURIField.PLUGIN_URI;
            StringBuffer sb = new StringBuffer();
            sb.append(PluginColumns.PLUGIN_ID).append(" =? ");
            cursor = mCr.query(uri,
                    null,
                    sb.toString(),
                    new String[] { pluginId },
                    null);
            if (null != cursor && cursor.moveToFirst())
            {
                pluginModel = parseInternalPluginCursor(cursor);
            }
            return pluginModel;
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    /**
     * 根据插件ID更新插件<BR>
     * @param cv ContentValues
     * @param pluginId 插件ID
     * @return 更新记录的条数
     */
    public int updateByPluginId(ContentValues cv, String pluginId)
    {
        int result = -1;
        Uri uri = Uri.withAppendedPath(PluginURIField.PLUGIN_UPDATE_WITH_ID,
                String.valueOf(pluginId));
        StringBuffer sb = new StringBuffer();
        sb.append(PluginColumns.PLUGIN_ID).append(" =? ");
        result = mCr.update(uri, cv, sb.toString(), new String[] { pluginId });
        return result;
    }
    
    /**
     * 根据插件ID删除一条插件记录<BR>
     * @param pluginId 插件ID
     * @return 删除的条数
     */
    public int deleteByPluginId(String pluginId)
    {
        int result = -1;
        Uri uri = Uri.withAppendedPath(PluginURIField.PLUGIN_DELETE_WITH_ID,
                pluginId);
        StringBuffer sb = new StringBuffer();
        sb.append(PluginColumns.PLUGIN_ID).append(" =? ");
        result = mCr.delete(uri, sb.toString(), new String[] { pluginId });
        return result;
    }
    
    /**
     * 根据插件id查询插件<BR>
     * @param pluginId 插件ID
     * @return BasePlugin
     */
    public BasePlugin queryPluginByPluginId(String pluginId)
    {
        Cursor cursor = null;
        try
        {
            Uri uri = PluginURIField.PLUGIN_URI;
            StringBuffer sb = new StringBuffer();
            sb.append(PluginColumns.PLUGIN_ID).append(" =? ");
            cursor = mCr.query(uri,
                    null,
                    sb.toString(),
                    new String[] { pluginId },
                    null);
            if (null != cursor && cursor.moveToFirst())
            {
                if (BasePlugin.TYPE_APK == cursor.getInt(cursor.getColumnIndex(PluginColumns.TYPE)))
                {
                    return parseApkPluginCursor(cursor);
                }
                else if (BasePlugin.TYPE_INTERNAL == cursor.getInt(cursor.getColumnIndex(PluginColumns.TYPE)))
                {
                    return parseInternalPluginCursor(cursor);
                }
            }
            return null;
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    /**
     * 查询所有的Apk插件<BR>
     * @return ArrayList<ApkPlugin>
     */
    public ArrayList<ApkPlugin> queryAllApkPlugins()
    {
        Cursor cursor = null;
        try
        {
            ArrayList<ApkPlugin> list = null;
            Uri uri = PluginURIField.PLUGIN_URI;
            StringBuffer sb = new StringBuffer();
            sb.append(PluginColumns.TYPE)
                    .append(" = ")
                    .append(BasePlugin.TYPE_APK);
            cursor = mCr.query(uri, null, sb.toString(), null, null);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<ApkPlugin>();
                do
                {
                    list.add(parseApkPluginCursor(cursor));
                } while (cursor.moveToNext());
            }
            return list;
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    /**
     * 查询所有内部插件<BR>
     * @return ArrayList<InternalPlugin>
     */
    public ArrayList<InternalPlugin> queryAllInternalPlugins()
    {
        Cursor cursor = null;
        try
        {
            ArrayList<InternalPlugin> list = null;
            Uri uri = PluginURIField.PLUGIN_URI;
            StringBuffer sb = new StringBuffer();
            sb.append(PluginColumns.TYPE)
                    .append(" = ")
                    .append(BasePlugin.TYPE_INTERNAL);
            cursor = mCr.query(uri, null, sb.toString(), null, null);
            if (null != cursor && cursor.moveToFirst())
            {
                list = new ArrayList<InternalPlugin>();
                do
                {
                    list.add(parseInternalPluginCursor(cursor));
                } while (cursor.moveToNext());
            }
            return list;
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
                cursor = null;
            }
        }
    }
    
    private InternalPlugin parseInternalPluginCursor(Cursor cursor)
    {
        if (null == cursor)
        {
            return null;
        }
        InternalPlugin pluginModel = new InternalPlugin();
        pluginModel.setContext(mContext);
        pluginModel.setPluginId(cursor.getString(cursor.getColumnIndex(PluginColumns.PLUGIN_ID)));
        pluginModel.setDesc(cursor.getString(cursor.getColumnIndex(PluginColumns.DESC)));
        pluginModel.setIcon(cursor.getBlob(cursor.getColumnIndex(PluginColumns.ICON)));
        pluginModel.setIconUrl(cursor.getString(cursor.getColumnIndex(PluginColumns.ICON_URL)));
        pluginModel.setName(cursor.getString(cursor.getColumnIndex(PluginColumns.NAME)));
        pluginModel.setPubTime(cursor.getString(cursor.getColumnIndex(PluginColumns.PUB_TIME)));
        pluginModel.setVersion(cursor.getInt(cursor.getColumnIndex(PluginColumns.VERSION)));
        pluginModel.setShowInContactList(cursor.getInt(cursor.getColumnIndex(PluginColumns.SHOW_IN_CONTACTLIST)) == 1 ? true
                : false);
        pluginModel.setStatus(cursor.getInt(cursor.getColumnIndex(PluginColumns.STATUS)));
        pluginModel.setStartAction(cursor.getString(cursor.getColumnIndex(PluginColumns.START_ACTION)));
        pluginModel.setIntentExtras(convertStringExtrasToMap(cursor.getString(cursor.getColumnIndex(PluginColumns.INTENT_EXTRA))));
        return pluginModel;
    }
    
    private ApkPlugin parseApkPluginCursor(Cursor cursor)
    {
        if (null == cursor)
        {
            return null;
        }
        ApkPlugin pluginModel = new ApkPlugin();
        pluginModel.setContext(mContext);
        pluginModel.setArchiveFilePath(cursor.getString(cursor.getColumnIndex(PluginColumns.ARCHIVE_FILE_PATH)));
        pluginModel.setPluginId(cursor.getString(cursor.getColumnIndex(PluginColumns.PLUGIN_ID)));
        pluginModel.setDesc(cursor.getString(cursor.getColumnIndex(PluginColumns.DESC)));
        pluginModel.setIcon(cursor.getBlob(cursor.getColumnIndex(PluginColumns.ICON)));
        pluginModel.setIconUrl(cursor.getString(cursor.getColumnIndex(PluginColumns.ICON_URL)));
        pluginModel.setPackageName(cursor.getString(cursor.getColumnIndex(PluginColumns.PACKAGE_NAME)));
        pluginModel.setName(cursor.getString(cursor.getColumnIndex(PluginColumns.NAME)));
        pluginModel.setPubTime(cursor.getString(cursor.getColumnIndex(PluginColumns.PUB_TIME)));
        pluginModel.setVersion(cursor.getInt(cursor.getColumnIndex(PluginColumns.VERSION)));
        pluginModel.setShowInContactList(cursor.getInt(cursor.getColumnIndex(PluginColumns.SHOW_IN_CONTACTLIST)) == 1 ? true
                : false);
        pluginModel.setStatus(cursor.getInt(cursor.getColumnIndex(PluginColumns.STATUS)));
        pluginModel.setStartAction(cursor.getString(cursor.getColumnIndex(PluginColumns.START_ACTION)));
        pluginModel.setIntentExtras(convertStringExtrasToMap(cursor.getString(cursor.getColumnIndex(PluginColumns.INTENT_EXTRA))));
        return pluginModel;
    }
    
    /**
     * 把intent extras转成 string<BR>
     * @param extrasString string 
     * @return extras的Map格式
     */
    private Map<String, String> convertStringExtrasToMap(String extrasString)
    {
        if (null == extrasString)
        {
            return null;
        }
        String[] entryStrings = extrasString.split(";");
        Map<String, String> map = null;
        if (null != entryStrings)
        {
            map = new HashMap<String, String>();
            for (String entry : entryStrings)
            {
                String[] keyValue = entry.split(",");
                if (null != keyValue && keyValue.length == 2)
                {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return map;
    }
    
    /**
     * map生成String{name1,value1;name2,value2;}<BR>
     * @param map intent传递的参数
     * @return extras的string形式
     */
    public String convertExtrasMapToString(Map<String, String> map)
    {
        if (null == map)
        {
            return null;
        }
        if (null != map.keySet())
        {
            StringBuffer sb = new StringBuffer();
            for (String key : map.keySet())
            {
                sb.append(key).append(",").append(map.get(key)).append(";");
            }
            return sb.toString();
        }
        return null;
    }
}
