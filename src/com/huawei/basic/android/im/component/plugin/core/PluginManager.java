/*
 * 文件名: PluginManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.plugin.core.db.BasePluginDbAdapter;
import com.huawei.basic.android.im.component.plugin.core.db.CommonPluginDbAdapter;
import com.huawei.basic.android.im.component.plugin.core.xml.PluginConfigXmlParser.PluginConfigModel;
import com.huawei.basic.android.im.component.plugin.plugins.apk.ApkPlugin;
import com.huawei.basic.android.im.component.plugin.plugins.internal.InternalPlugin;
import com.huawei.basic.android.im.component.plugin.util.XmlParser;

/**
 * [一句话功能简述]<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
public class PluginManager implements IPluginManager
{
    /**
     * TAG
     */
    private static final String TAG = "PluginManager";
    
    /**
     * PluginManager
     */
    private static PluginManager sPluginManager;
    
    /**
     * PluginDbAdapter
     */
    private BasePluginDbAdapter mPluginDbAdapter;
    
    private PluginManager()
    {
    }
    
    /**
     * 获取实例对象
     * @return PluginManager
     */
    public static synchronized PluginManager getInstance()
    {
        if (null == sPluginManager)
        {
            sPluginManager = new PluginManager();
        }
        return sPluginManager;
    }
    
    /**
     * 初始化<BR>
     * @param context context上下文
     * @param initArrays 各种插件的初始化方法
     */
    public void init(Context context, IPluginInit... initArrays)
    {
        Log.d(TAG, "init.....");
        
        //初始化数据库操作
        mPluginDbAdapter = CommonPluginDbAdapter.getInstance();
        CommonPluginDbAdapter.getInstance().init(context);
        
        PluginConfigModel configModel = null;
        List<BasePlugin> configPluginList = null;
        try
        {
            configModel = new XmlParser().parseXmlInputStream(PluginConfigModel.class,
                    context.getResources().openRawResource(R.raw.plugin_config));
        }
        catch (Exception e)
        {
            Logger.e(TAG, "插件配置文件格式错误,请检查文件格式 ", e);
            return;
        }
        if (null == configModel)
        {
            Log.e(TAG, "parse config fail");
        }
        else
        {
            configPluginList = configModel.getPluginList();
        }
        if (null == configPluginList)
        {
            //配置文件上没有获取到插件列表
            return;
        }
        else
        {
            //method容器初始化
            MethodContainer.init(context);
            
            //缓存插件的method 列表
            for (BasePlugin basePlugin : configPluginList)
            {
                if (null != basePlugin.getMethods())
                {
                    MethodContainer.put(basePlugin.getPluginId(),
                            basePlugin.getMethods());
                }
            }
        }
        
        if (null != initArrays)
        {
            for (int i = 0; i < initArrays.length; i++)
            {
                initArrays[i].init(context, configModel);
            }
        }
        
    }
    
    @Override
    public ArrayList<ApkPlugin> getApkPluginList()
    {
        return mPluginDbAdapter.queryAllApkPlugins();
    }
    
    @Override
    public ArrayList<InternalPlugin> getInternalPluginList()
    {
        return mPluginDbAdapter.queryAllInternalPlugins();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void install(BasePlugin plugin, ICallBack listener)
    {
        plugin.install(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstall(BasePlugin plugin, ICallBack listener)
    {
        plugin.uninstall(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BasePlugin plugin)
    {
        plugin.start();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(BasePlugin plugin, ICallBack listener)
    {
        plugin.update(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BasePlugin getPluginByPluginId(String pluginId)
    {
        BasePlugin basePlugin = mPluginDbAdapter.queryPluginByPluginId(pluginId);
        if (null != basePlugin)
        {
            basePlugin.setMethods(MethodContainer.get(pluginId));
        }
        return basePlugin;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<BasePlugin> getAllPluins()
    {
        List<InternalPlugin> internalList = getInternalPluginList();
        List<ApkPlugin> apkList = getApkPluginList();
        ArrayList<BasePlugin> baseList = new ArrayList<BasePlugin>();
        
        if (null != internalList)
        {
            baseList.addAll(internalList);
        }
        if (null != apkList)
        {
            baseList.addAll(apkList);
        }
        return baseList;
    }
}
