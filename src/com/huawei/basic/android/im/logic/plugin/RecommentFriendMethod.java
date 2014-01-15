/*
 * 文件名: RecommentFriendMethod.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 25, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.plugin;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.plugin.core.SwitchMethod;
import com.huawei.basic.android.im.logic.adapter.db.UserConfigDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.ContactManager;
import com.huawei.basic.android.im.logic.contact.ContactLogic;
import com.huawei.basic.android.im.logic.model.UserConfigModel;

/**
 * 向我推荐通讯录好友<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 25, 2012] 
 */
public class RecommentFriendMethod extends SwitchMethod
{
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getStatus()
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        UserConfigModel config = UserConfigDbAdapter.getInstance(getContext())
                .queryByKey(userSysId, UserConfigModel.IS_UPLOAD_CONTACTS);
        if (null == config)
        {
            return false;
        }
        return UserConfigModel.IS_UPLOAD_CONTACTS_YES.equals(config.getValue());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void invoke(final boolean flag)
    {
        //logic无法注入到这个里面，只能用这种方式
        final ContactLogic logic = new ContactLogic(getContext(), null);
        if (flag)
        {
            //上传通讯录
            logic.beginUpload(true);
            logic.updateUploadFlag(flag);
        }
        else
        {
            new ContactManager().deleteUploadedContacts(new IHttpListener()
            {
                @Override
                public void onResult(int action, Response response)
                {
                    logic.updateUploadFlag(flag);
                }
                
                @Override
                public void onProgress(boolean isInProgress)
                {
                }
            });
            
        }
    }
    
}
