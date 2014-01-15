/*
 * 文件名: ApkPlugin.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.plugins.apk;

import java.io.Serializable;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import android.content.Context;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.ICallBack;

/**
 * Apk插件<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
@Root(name = "plugin", strict = false)
public class ApkPlugin extends BasePlugin implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Element(name = "package-name", required = false)
    private String packageName;
    
    @Element(name = "archive-file-path", required = false)
    private String archiveFilePath;
    
    @Element(name = "url", required = false)
    private String url;
    
    @Element(name = "action", required = false)
    private String startAction;
    
    @ElementMap(inline = true, entry = "extra", key = "key", attribute = true, required = false)
    private Map<String, String> intentExtras;
    
    private Context context;
    
    public void setContext(Context context)
    {
        this.context = context;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstall(ICallBack listener)
    {
        ApkOperator.getInstance(context).uninstallApk(packageName, listener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start()
    {
        //TODO 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doInstall(ICallBack listener)
    {
        ApkOperator.getInstance(context).installApk(archiveFilePath, listener);
    }
    
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }
    
    public String getPackageName()
    {
        return packageName;
    }
    
    public void setArchiveFilePath(String archiveFilePath)
    {
        this.archiveFilePath = archiveFilePath;
    }
    
    public String getArchiveFilePath()
    {
        return archiveFilePath;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setStartAction(String startAction)
    {
        this.startAction = startAction;
    }
    
    public String getStartAction()
    {
        return startAction;
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
