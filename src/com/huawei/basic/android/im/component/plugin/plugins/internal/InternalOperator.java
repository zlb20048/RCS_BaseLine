/*
 * 文件名: InternalOperator.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.plugins.internal;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.huawei.basic.android.im.component.plugin.core.ICallBack;
import com.huawei.basic.android.im.component.plugin.core.common.Config.ErrorCode;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.utils.PluginStringUtil;

/**
 * 内部插件操作类<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
public class InternalOperator
{
    private static InternalOperator sInstance;
    
    private Context mContext;
    
    private InternalDbAdapter mPluginDbAdapter;
    
    private InternalOperator(Context context)
    {
        this.mContext = context;
        mPluginDbAdapter = InternalDbAdapter.getInstance();
    }
    
    /**
     * 获取实例对象<BR>
     * @param context context上下文
     * @return InternalOperator
     */
    public static synchronized InternalOperator getInstance(Context context)
    {
        if (null == sInstance)
        {
            sInstance = new InternalOperator(context);
        }
        return sInstance;
    }
    
    /**
     * 安装插件<BR>
     * @param internalPlugin 内部插件
     * @param listener 回调监听器
     */
    public void install(InternalPlugin internalPlugin, ICallBack listener)
    {
        String pluginId = internalPlugin.getPluginId();
        InternalPlugin dbInternalPlugin = mPluginDbAdapter.queryInternalPluginByPluginId(pluginId);
        
        int errorCode = ErrorCode.BASE;
        
        if (null == dbInternalPlugin)
        {
            long result = mPluginDbAdapter.insertInternal(internalPlugin);
            if (result > -1)
            {
                errorCode = ErrorCode.SUCCESS;
            }
            else
            {
                errorCode = ErrorCode.FAILED;
            }
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put(PluginColumns.STATUS, InternalPlugin.STATUS_INSTALLED);
            int result = mPluginDbAdapter.updateByPluginId(cv, pluginId);
            if (result >= 1)
            {
                errorCode = ErrorCode.SUCCESS;
            }
            else
            {
                errorCode = ErrorCode.FAILED;
            }
        }
        
        listener.callBack(errorCode);
        
    }
    
    /**
     * 启动插件<BR>
     * @param action action
     * @param intentExtras intentExtras
     */
    public void start(String action, Map<String, String> intentExtras)
    {
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (null != intentExtras)
        {
            if (null != intentExtras.keySet())
            {
                for (String key : intentExtras.keySet())
                {
                    String value = intentExtras.get(key);
                    if (null != value && value.length() > 9
                            && "string_id".equals(value.substring(0, 9)))
                    {
                        intent.putExtra(key, mContext.getString(PluginStringUtil.getOtherId(value)));
                    }
                    else
                    {
                        intent.putExtra(key, value);
                    }
                }
            }
        }
        mContext.startActivity(intent);
    }
}
