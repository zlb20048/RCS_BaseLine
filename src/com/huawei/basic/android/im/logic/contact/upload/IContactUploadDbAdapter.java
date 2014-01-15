/*
 * 文件名: IContactUploadDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:May 8, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.contact.upload;

import java.util.List;

import com.huawei.basic.android.im.component.load.adapter.ITaskDataAdapter;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, May 8, 2012] 
 */
public interface IContactUploadDbAdapter extends ITaskDataAdapter
{
    /**
     *  更改数据库通讯录数据<BR>
     * 上传通讯录信息后同步更改本地数据
     * @param userSysId 用户sysid
     * @param contacts 更改数据
     */
    public void saveSucceedContacts(String userSysId,
            List<PhoneContactIndexModel> contacts);
}
