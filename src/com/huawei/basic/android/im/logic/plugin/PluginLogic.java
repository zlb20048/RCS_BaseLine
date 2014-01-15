/*
 * 文件名: PluginLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 23, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.plugin;

import java.util.List;

import android.content.Context;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.PluginManager;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.UserConfigDbAdapter;
import com.huawei.basic.android.im.logic.model.UserConfigModel;

/**
 * 插件的logic<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 23, 2012] 
 */
public class PluginLogic extends BaseLogic implements IPluginLogic
{
    
    private UserConfigDbAdapter mUserConfigAdapter;
    
    private PluginManager mPluginManager;
    
    /**
     * 构造方法
     * @param context context上下文
     */
    public PluginLogic(Context context)
    {
        mUserConfigAdapter = UserConfigDbAdapter.getInstance(context);
        mPluginManager = PluginManager.getInstance();
    }
    
    /**
     *{@inheritDoc} 
     */
    @Override
    public void setShowPluginsOnContacts(boolean flag)
    {
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        String flagString = flag ? UserConfigModel.IS_UPLOAD_PHONE_CONTACT_YES
                : UserConfigModel.IS_UPLOAD_PHONE_CONTACT_NO;
        UserConfigModel model = mUserConfigAdapter.queryByKey(sysId,
                UserConfigModel.IS_SHOW_PLUGINS_ON_CONTACTS);
        if (null == model)
        {
            model = new UserConfigModel();
            model.setKey(UserConfigModel.IS_SHOW_PLUGINS_ON_CONTACTS);
            model.setValue(flagString);
            mUserConfigAdapter.insertUserConfig(sysId, model);
        }
        else
        {
            mUserConfigAdapter.updateByKey(sysId,
                    UserConfigModel.IS_SHOW_PLUGINS_ON_CONTACTS,
                    flagString);
        }
        sendMessage(FriendMessageType.SHOW_ON_CONTACTS_LIST, flag);
    }
    
    /**
     *{@inheritDoc} 
     */
    public boolean getShowPluginsOnContacts()
    {
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        UserConfigModel model = mUserConfigAdapter.queryByKey(sysId,
                UserConfigModel.IS_SHOW_PLUGINS_ON_CONTACTS);
        if (null == model)
        {
            return true;
        }
        else
        {
            return model.getValue()
                    .equals(UserConfigModel.IS_SHOW_PLUGINS_ON_CONTACTS_YES);
        }
    }
    
    /**
     *{@inheritDoc} 
     */
    @Override
    public List<BasePlugin> getPluginList()
    {
        return mPluginManager.getAllPluins();
    }
}
