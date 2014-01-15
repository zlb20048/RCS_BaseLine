/*
 * 文件名: InternalInit.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.IPluginInit;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.component.plugin.core.xml.PluginConfigXmlParser.PluginConfigModel;

/**
 * 内部插件初始化<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 21, 2012] 
 */
public class InternalInit implements IPluginInit
{
    
    private InternalDbAdapter mInternalDbAdapter;
    
    /**
     * {@inheritDoc}
    */
    @Override
    public void init(Context context, PluginConfigModel model)
    {
        //先初始化数据库操作
        mInternalDbAdapter = InternalDbAdapter.getInstance();
        InternalDbAdapter.getInstance().init(context);
        
        ArrayList<InternalPlugin> configList = new ArrayList<InternalPlugin>();
        if (null != model.getPluginList())
        {
            for (BasePlugin basePlugin : model.getPluginList())
            {
                if (basePlugin instanceof InternalPlugin)
                {
                    configList.add((InternalPlugin) basePlugin);
                }
            }
        }
        
        //删除配置文件中没有记录
        deletePluginsByConfig(configList,
                mInternalDbAdapter.queryAllInternalPlugins());
        
        //同步数据库和本地记录 
        synInternalPluginByConfig(configList,
                mInternalDbAdapter.queryAllInternalPlugins());
        
    }
    
    private void synInternalPluginByConfig(List<InternalPlugin> configList,
            List<InternalPlugin> dbList)
    {
        for (InternalPlugin configInternalPlugin : configList)
        {
            //exist
            boolean flag = false;
            if (null != dbList)
            {
                for (InternalPlugin dbInternalPlugin : dbList)
                {
                    if (configInternalPlugin.getPluginId()
                            .equals(dbInternalPlugin.getPluginId()))
                    {
                        flag = true;
                        ContentValues cv = new ContentValues();
                        setInternalPluginConventValues(cv, configInternalPlugin);
                        mInternalDbAdapter.updateByPluginId(cv,
                                configInternalPlugin.getPluginId());
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag)
            {
                mInternalDbAdapter.insertInternal(configInternalPlugin);
            }
        }
    }
    
    private void setInternalPluginConventValues(ContentValues cv,
            InternalPlugin dbInternalPlugin)
    {
        if (null == cv || null == dbInternalPlugin)
        {
            return;
        }
        cv.put(PluginColumns.DESC, dbInternalPlugin.getDesc());
        cv.put(PluginColumns.NAME, dbInternalPlugin.getName());
        cv.put(PluginColumns.ICON_URL, dbInternalPlugin.getIconUrl());
        cv.put(PluginColumns.PUB_TIME, dbInternalPlugin.getPubTime());
        cv.put(PluginColumns.START_ACTION, dbInternalPlugin.getStartAction());
        cv.put(PluginColumns.INTENT_EXTRA,
                convertExtrasMapToString(dbInternalPlugin.getIntentExtras()));
    }
    
    /**
     * 删除配置文件中不存在的列表<BR>
     * @param configList 配置文件中的列表
     * @param dbList 数据库中列表
     */
    private void deletePluginsByConfig(List<? extends BasePlugin> configList,
            List<? extends BasePlugin> dbList)
    {
        if (null != dbList)
        {
            for (BasePlugin dbPlugin : dbList)
            {
                //config是否存在
                boolean flag = false;
                if (null != configList)
                {
                    for (BasePlugin configPlugin : configList)
                    {
                        if (dbPlugin.getPluginId()
                                .equals(configPlugin.getPluginId()))
                        {
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag)
                {
                    mInternalDbAdapter.deleteByPluginId(dbPlugin.getPluginId());
                }
            }
        }
    }
    
    /**
     * map生成String{name1,value1;name2,value2;}<BR>
     * @param map intent传递的参数
     * @return extras的string形式
     */
    private String convertExtrasMapToString(Map<String, String> map)
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
