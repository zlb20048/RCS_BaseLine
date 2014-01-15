/*
 * 文件名: BootReceiver.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Mar 9, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.login.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huawei.basic.android.im.common.FusionAction.ServiceAction;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.log.Logger;

/**
 * 监听系统开关机
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Mar 9, 2012]
 */
public class BootReceiver extends BroadcastReceiver
{
    /**
     * TAG
     */
    public static final String TAG = "BootReceiver";
    
    /**
     * 构造
     * 
     * @param context
     *            上下文
     * @param intent
     *            intent
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     *      android.content.Intent)
     */
    
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        //        Logger.i(TAG, null == intent ? "intent is null" : intent.getAction());
        //        if (null != intent)
        //        {
        //系统开机
        if (null != intent
                && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Logger.i(TAG, "sendBootMessage!");
            Intent serivceIntent = new Intent(ServiceAction.SERVICE_ACTION);
            serivceIntent.putExtra(ServiceAction.CONNECTIVITY,
                    LoginMessageType.STATUS_BOOT);
            context.getApplicationContext().startService(serivceIntent);
        }
        //            
        //        }
    }
    
}
