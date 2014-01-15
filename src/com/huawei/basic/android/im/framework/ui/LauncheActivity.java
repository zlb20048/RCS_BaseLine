/*
 * 文件名: LauncheActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.framework.ui;

import android.content.Context;
import android.os.Bundle;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.framework.logic.BaseLogicBuilder;

/**系统的Activity的启动类<BR>
 * 第一个启动的Activity必须继承，而且其他Activity不要继承
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012] 
 */
public abstract class LauncheActivity extends BaseActivity
{
    private static final String TAG = "LauncheActivity";
    
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
            BaseLogicBuilder logicBuilder = createLogicBuilder(this.getApplicationContext());
            super.setLogicBuilder(logicBuilder);
            initSystem(LauncheActivity.this);
            Logger.i(TAG, "Load logic builder successful");
        }
        super.onCreate(savedInstanceState);
    }
    
    /**
     * 系统的初始化方法<BR>
     * @param context
     *      系统的context对象
     */
    protected abstract void initSystem(Context context);
    
    /**
     * Logic建造管理类需要创建的接口<BR>
     * 需要子类继承后，指定Logic建造管理类具体实例
     * @param context
     *      系统的context对象
     * @return
     *      Logic建造管理类具体实例
     */
    protected abstract BaseLogicBuilder createLogicBuilder(Context context);
    
}