/*
 * 文件名: ConnectionChangedReceiver.java
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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.huawei.basic.android.im.common.FusionAction.ServiceAction;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 监听系统网络
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Mar 9, 2012]
 */
public class ConnectionChangedReceiver extends BroadcastReceiver
{
    /**
     * TAG
     */
    public static final String TAG = "ConnectionChangedReceiver";
    
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
        if (null != intent
                && ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
        {
            Logger.i(TAG, "sendNetMessage!");
            Logger.d(TAG, "start service");
            Intent serivceIntent = new Intent(ServiceAction.SERVICE_ACTION);
            serivceIntent.putExtra(ServiceAction.CONNECTIVITY,
                    checkNet(context));
            context.getApplicationContext().startService(serivceIntent);
        }
    }
    
    /**
     * 修改系统指定的接入点为net接入点
     * 
     * @param context
     *            Context上下文
     */
    public static void switchOverAPN(Context context)
    {
        Cursor cursor = null;
        try
        {
            cursor = context.getContentResolver()
                    .query(Uri.parse("content://telephony/carriers"),
                            null,
                            null,
                            null,
                            null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                Logger.i(TAG, "serch net");
                String string = cursor.getString(cursor.getColumnIndex("apn"));
                if (StringUtil.isNullOrEmpty(string))
                {
                    string = cursor.getString(cursor.getColumnIndex("user"));
                }
                if (!StringUtil.isNullOrEmpty(string))
                {
                    string = string.toLowerCase();
                    if (string.contains("net"))
                    {
                        ContentValues cv = new ContentValues();
                        // cv.put("apn",
                        // cursor.getString(cursor.getColumnIndex("apn")));
                        // cv.put("type",
                        // cursor.getString(cursor.getColumnIndex("type")));
                        cv.put("apn_id",
                                cursor.getString(cursor.getColumnIndex("_id")));
                        Logger.i(TAG, "change net");
                        context.getContentResolver()
                                .update(Uri.parse("content://telephony/carriers/preferapn"),
                                        cv,
                                        null,
                                        null);
                        break;
                    }
                }
                cursor.moveToNext();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DatabaseHelper.closeCursor(cursor);
        }
    }
    
    /**
     * 
     * 检查网络状态
     * 
     * @param context
     *            context
     * 
     * @return netStatus
     */
    public static int checkNet(Context context)
    {
        
        // 获取网络服务对象
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()
                && activeNetworkInfo.isConnected())
        {
            //有网络
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                Cursor cursor = null;
                try
                {
                    cursor = context.getContentResolver()
                            .query(Uri.parse("content://telephony/carriers/preferapn"),
                                    null,
                                    null,
                                    null,
                                    null);
                    if (cursor != null && cursor.getCount() > 0)
                    {
                        cursor.moveToFirst();
                        String string = cursor.getString(cursor.getColumnIndex("apn"));
                        if (StringUtil.isNullOrEmpty(string))
                        {
                            string = cursor.getString(cursor.getColumnIndex("user"));
                        }
                        if (!StringUtil.isNullOrEmpty(string))
                        {
                            string = string.toLowerCase();
                            if (string.contains("wap"))
                            {
                                return LoginMessageType.NET_STATUS_WAP;
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    DatabaseHelper.closeCursor(cursor);
                }
            }
            return LoginMessageType.NET_STATUS_ENABLE;
        }
        return LoginMessageType.NET_STATUS_DISABLE;
    }
    
}
