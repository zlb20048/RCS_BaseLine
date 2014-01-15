/*
 * 文件名: IPluginInit.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 21, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core;

import android.content.Context;

import com.huawei.basic.android.im.component.plugin.core.xml.PluginConfigXmlParser.PluginConfigModel;

/**
 * 初始化接口<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 21, 2012] 
 */
public interface IPluginInit
{
    /**
     * init<BR>
     * @param context context上下文
     * @param model 配置对象model
     */
    void init(Context context, PluginConfigModel model);
}
