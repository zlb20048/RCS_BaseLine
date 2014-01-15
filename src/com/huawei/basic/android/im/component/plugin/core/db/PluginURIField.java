/*
 * 文件名: PluginUriField.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 10, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core.db;

import android.net.Uri;

import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.Tables;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.UriStrings;

/**
 * 插件的URI<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 10, 2012] 
 */
public interface PluginURIField
{
    /**
     * 插件 URI
     */
    Uri PLUGIN_URI = Uri.parse("content://" + UriStrings.AUTHORITY + "/"
            + Tables.PLUGIN);
    
    /**
     * 删除插件记录的带ID的URI
     */
    Uri PLUGIN_DELETE_WITH_ID = Uri.parse("content://" + UriStrings.AUTHORITY
            + "/" + UriStrings.PLUGIN_DELETE_WITH_ID);
    
    /**
     * 更新插件记录的带ID的URI
     */
    Uri PLUGIN_UPDATE_WITH_ID = Uri.parse("content://" + UriStrings.AUTHORITY
            + "/" + UriStrings.PLUGIN_UPDATE_WITH_ID);
    
    /**
     * 插入插件记录你的带ID的URI
     */
    Uri PLUGIN_INSERT_WITH_ID = Uri.parse("content://" + UriStrings.AUTHORITY
            + "/" + UriStrings.PLUGIN_INSERT_WITH_ID);
}
