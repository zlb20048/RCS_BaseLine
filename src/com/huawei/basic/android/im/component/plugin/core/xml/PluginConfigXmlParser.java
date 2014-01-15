/*
 * 文件名: PluginConfigXmlParser.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 14, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core.xml;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.content.Context;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.util.XmlParser;

/**
 * 解析插件的配置文件类<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 20, 2012]
 */
public class PluginConfigXmlParser
{
    /**
     * 解析插件文件<BR>
     * @param context context 上下文
     * @return 插件配置model
     */
    public static PluginConfigModel parsePluginConfig(Context context)
    {
        try
        {
            PluginConfigModel model = new XmlParser().parseXmlInputStream(PluginConfigModel.class,
                    context.getResources().openRawResource(R.raw.plugin_config));
            if (null != model)
            {
                return model;
            }
        }
        catch (Exception e)
        {
            Logger.e("TAG", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 插件的配置model<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 20, 2012]
     */
    @Root(name = "plugin-list")
    public static class PluginConfigModel
    {
        @Element(name = "server-ip", required = false)
        private String server;
        
        @Element(name = "save-apk-path", required = false)
        private String saveApkPath;
        
        @ElementList(inline = true)
        private List<BasePlugin> pluginList;
        
        public List<BasePlugin> getPluginList()
        {
            return pluginList;
        }
        
        public void setPluginList(List<BasePlugin> pluginList)
        {
            this.pluginList = pluginList;
        }
        
        public void setServer(String server)
        {
            this.server = server;
        }
        
        public String getServer()
        {
            return server;
        }
        
        public void setSaveApkPath(String saveApkPath)
        {
            this.saveApkPath = saveApkPath;
        }
        
        public String getSaveApkPath()
        {
            return saveApkPath;
        }
        
    }
}
