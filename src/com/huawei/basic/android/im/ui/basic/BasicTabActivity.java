/*
 * 文件名: BasicTabActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Apr 6, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import android.content.Context;
import android.os.Bundle;

import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.framework.logic.BaseLogicBuilder;
import com.huawei.basic.android.im.framework.ui.BaseTabActivity;
import com.huawei.basic.android.im.logic.LogicBuilder;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * tab activity 基类
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Apr 6, 2012] 
 */
public abstract class BasicTabActivity extends BaseTabActivity
{
    
    private static final String TAG = "BasicTabActivity";
    
    /**
     * Activity的初始化方法<BR>
     * @param savedInstanceState
     *     传入的Bundle对象
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        if (!isInit())
        {
            if (StringUtil.isNullOrEmpty(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserID()))
            {
                FusionConfig.getInstance()
                        .getAasResult()
                        .setUserSysId(getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                                MODE_PRIVATE).getString(Common.KEY_USER_SYSID,
                                null));
                FusionConfig.getInstance()
                        .getAasResult()
                        .setUserID(getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                                MODE_PRIVATE).getString(Common.KEY_USER_ID,
                                null));
            }
            BaseLogicBuilder logicBuilder = createLogicBuilder(this.getApplicationContext());
            super.setLogicBuilder(logicBuilder);
            Logger.i(TAG, "Load logic builder successful");
        }
        super.onCreate(savedInstanceState);
    }
    
    /**
     * 初始化logic的方法，由子类实现<BR>
     * 在该方法里通过getLogicByInterfaceClass获取logic对象
     * @see com.huawei.basic.android.im.framework.ui.BaseTabActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        // TODO Auto-generated method stub
    }
    
    /**
     * Logic建造管理类需要创建的接口<BR>
     * 需要子类继承后，指定Logic建造管理类具体实例
     * @param context
     *      系统的context对象
     * @return
     *      Logic建造管理类具体实例
     * @see com.huawei.basic.android.im.framework.ui.BaseTabActivity#createLogicBuilder(android.content.Context)
     */
    protected BaseLogicBuilder createLogicBuilder(Context context)
    {
        return LogicBuilder.getInstance(context);
    }
    
}
