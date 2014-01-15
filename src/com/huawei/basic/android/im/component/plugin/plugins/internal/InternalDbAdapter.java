/*
 * 文件名: InternalDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 21, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.plugins.internal;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.db.BasePluginDbAdapter;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.component.plugin.core.db.PluginURIField;

/**
 * 内部插件数据库监听<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 21, 2012] 
 */
public class InternalDbAdapter extends BasePluginDbAdapter
{
    
    private static InternalDbAdapter sInternalDbAdapter;
    
    private InternalDbAdapter()
    {
        
    }
    
    /**
     * InternalDbAdapter获取实例对象<BR>
     * @return  InternalDbAdapter
     */
    public static InternalDbAdapter getInstance()
    {
        if (null == sInternalDbAdapter)
        {
            sInternalDbAdapter = new InternalDbAdapter();
        }
        return sInternalDbAdapter;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Context context)
    {
        this.setmContext(context);
        this.setmCr(context.getContentResolver());
    }
    
    /**
     * 插入数据<BR>
     * @param internalPlugin internal插件对象
     * @return id
     */
    public long insertInternal(InternalPlugin internalPlugin)
    {
        long result = -1;
        Uri uri = PluginURIField.PLUGIN_URI;
        Uri resultUri = getmCr().insert(uri,
                setInternalPluginValues(internalPlugin));
        if (null != resultUri)
        {
            result = ContentUris.parseId(resultUri);
        }
        return result;
    }
    
    private ContentValues setInternalPluginValues(InternalPlugin model)
    {
        if (null == model)
        {
            return null;
        }
        ContentValues cv = new ContentValues();
        cv.put(PluginColumns.TYPE, BasePlugin.TYPE_INTERNAL);
        cv.put(PluginColumns.DESC, model.getDesc());
        cv.put(PluginColumns.ICON, model.getIcon());
        cv.put(PluginColumns.ICON_URL, model.getIconUrl());
        cv.put(PluginColumns.NAME, model.getName());
        cv.put(PluginColumns.PUB_TIME, model.getPubTime());
        cv.put(PluginColumns.PLUGIN_ID, model.getPluginId());
        cv.put(PluginColumns.SHOW_IN_CONTACTLIST,
                model.isShowInContactList() ? 1 : 0);
        cv.put(PluginColumns.STATUS, model.getStatus());
        cv.put(PluginColumns.VERSION, model.getVersion());
        cv.put(PluginColumns.START_ACTION, model.getStartAction());
        cv.put(PluginColumns.INTENT_EXTRA,
                convertExtrasMapToString(model.getIntentExtras()));
        return cv;
    }
}
