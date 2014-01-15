/*
 * 文件名: InternalPlugin.java
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

import java.io.Serializable;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import android.content.Context;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.ICallBack;

/**
 * 内部插件<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
@Root(name = "plugin", strict = false)
public class InternalPlugin extends BasePlugin  implements Serializable
{
    
    private static final String TAG = "InternalPlugin";
    
    private static final long serialVersionUID = 1L;
    
    @Element(name = "action")
    private String startAction;
    
    @ElementMap(inline = true, entry = "extra", key = "key", attribute = true, required = false)
    private Map<String, String> intentExtras;
    
    private Context context;
    
    /**
     *{@inheritDoc} 
     */
    @Override
    public void uninstall(ICallBack listener)
    {
        
    }
    
    /**
     *{@inheritDoc} 
     */
    @Override
    public void start()
    {
        if (null == startAction)
        {
            Logger.e(TAG, "startAction is null,cannot start plugin ..");
            return;
        }
        InternalOperator.getInstance(getContext()).start(startAction,
                intentExtras);
    }
    
    /**
     *{@inheritDoc} 
     */
    @Override
    public void doInstall(ICallBack listener)
    {
        
    }
    
    public void setStartAction(String startAction)
    {
        this.startAction = startAction;
    }
    
    public String getStartAction()
    {
        return startAction;
    }
    
    public void setContext(Context context)
    {
        this.context = context;
    }
    
    public Context getContext()
    {
        return context;
    }
    
    public void setIntentExtras(Map<String, String> intentExtras)
    {
        this.intentExtras = intentExtras;
    }
    
    public Map<String, String> getIntentExtras()
    {
        return intentExtras;
    }
    
}
