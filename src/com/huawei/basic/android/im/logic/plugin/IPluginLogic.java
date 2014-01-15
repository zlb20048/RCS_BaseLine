/*
 * 文件名: IPluginLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 21, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.plugin;

import java.util.List;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;

/**
 * 插件ilogic<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 21, 2012] 
 */
public interface IPluginLogic
{
    /**
     * 设置是否在好友列表界面展示通讯录<BR>
     * @param flag true:展示 false:不展示
     */
    void setShowPluginsOnContacts(boolean flag);
    
    /**
     * 获取是否在好友列表显示插件<BR>
     * @return 是否在好友列表展示插件
     */
    boolean getShowPluginsOnContacts();
    
    /**
     * 获取需要展示的插件列表<BR>
     * @return  插件列表
     */
    List<BasePlugin> getPluginList();
}
