/*
 * 文件名: ApkOperator.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.plugins.apk;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.component.plugin.core.ICallBack;
import com.huawei.basic.android.im.component.plugin.core.common.Config.ErrorCode;
import com.huawei.basic.android.im.component.plugin.core.db.PluginDbHelper.PluginColumns;
import com.huawei.basic.android.im.component.plugin.util.ApkUtil;

/**
 * Apk操作类<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 13, 2012] 
 */
public class ApkOperator
{
    
    private static final String TAG = "ApkOperator";
    
    private static ApkOperator mApkOperator;
    
    /**
     * 一个插件的一个操作只用一个监听监听事件，简化处理
     */
    private Map<String, ICallBack> mInstallListenerMap;
    
    private Map<String, ICallBack> mUninstallListenerMap;
    
    private Map<String, ICallBack> mUpdateListenerMap;
    
    private Context mContext;
    
    private ApkPluginDbAdapter mPluginDbAdapter;
    
    private ApkOperator(Context context)
    {
        Log.e(TAG, "ApkOperator Constructor...");
        mContext = context;
        new ApkReceiver(mContext).registerReceiver();
        mPluginDbAdapter = ApkPluginDbAdapter.getInstance();
        ApkPluginDbAdapter.getInstance().init(mContext);
        init();
    }
    
    /**
     * 获取插件的实例对象<BR>
     * @param context context上下文
     * @return ApkOperator的实例对象
     */
    public static synchronized ApkOperator getInstance(Context context)
    {
        if (null == mApkOperator)
        {
            mApkOperator = new ApkOperator(context);
        }
        return mApkOperator;
    }
    
    private void init()
    {
        Log.e(TAG, "init ...");
        mInstallListenerMap = new HashMap<String, ICallBack>();
        mUninstallListenerMap = new HashMap<String, ICallBack>();
        mUpdateListenerMap = new HashMap<String, ICallBack>();
    }
    
    /**
     * 安装插件<BR>
     * @param path apk路径
     * @param installListener 回调监听器
     */
    public void installApk(String path, ICallBack installListener)
    {
        Log.e(TAG, "installApk....");
        String packageName = ApkUtil.getPackageInfo(mContext, path).packageName;
        Log.e(TAG, "installApk packageName:" + packageName);
        mInstallListenerMap.put(packageName, installListener);
        ApkUtil.installApk(mContext, path);
    }
    
    /**
     * 卸载插件<BR>
     * @param packageName apk插件报名
     * @param uninstallListener 回调监听器
     */
    public void uninstallApk(String packageName, ICallBack uninstallListener)
    {
        mUninstallListenerMap.put(packageName, uninstallListener);
        ApkUtil.unInstallApk(mContext, packageName);
    }
    
    /**
     * apk安装卸载更新广播接收器<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 20, 2012]
     */
    public class ApkReceiver extends BroadcastReceiver
    {
        
        private Context mContext;
        
        /**
         * 构造方法
         * @param context context上下文
         */
        public ApkReceiver(Context context)
        {
            Log.e(TAG, "ApkReceiver init ...");
            mContext = context;
        }
        
        /**
         * 注册广播<BR>
         */
        public void registerReceiver()
        {
            Log.e(TAG, "registerReceiver  ...");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addDataScheme("package");
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            mContext.registerReceiver(this, intentFilter);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String receivePackageName = intent.getDataString();
            
            String packageName = null;
            if (null != receivePackageName)
            {
                packageName = receivePackageName.split(":")[1];
            }
            
            Log.e(TAG, "onReceive packageName:" + packageName);
            Log.e(TAG, "onReceive action:" + intent.getAction());
            Log.e(TAG, "Intent.ACTION_PACKAGE_ADDED :"
                    + Intent.ACTION_PACKAGE_ADDED);
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED))
            {
                ICallBack installListener = mInstallListenerMap.get(packageName);
                Log.e(TAG, "installListener ... " + installListener);
                if (null != installListener)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(PluginColumns.STATUS, BasePlugin.STATUS_INSTALLED);
                    if (mPluginDbAdapter.updateByPackageName(cv, packageName) >= 1)
                    {
                        installListener.callBack(ErrorCode.SUCCESS);
                    }
                    mInstallListenerMap.remove(packageName);
                }
            }
            else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED))
            {
                ICallBack uninstallListener = mUninstallListenerMap.get(packageName);
                if (null != uninstallListener)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(PluginColumns.STATUS, BasePlugin.STATUS_UNINSTALLED);
                    if (mPluginDbAdapter.updateByPackageName(cv, packageName) >= 1)
                    {
                        uninstallListener.callBack(ErrorCode.SUCCESS);
                    }
                    mUninstallListenerMap.remove(packageName);
                }
            }
            else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED))
            {
                ICallBack updateListener = mUpdateListenerMap.get(packageName);
                if (null != updateListener)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(PluginColumns.NEED_UPDATE, 0);
                    if (mPluginDbAdapter.updateByPackageName(cv, packageName) >= 1)
                    {
                        updateListener.callBack(ErrorCode.SUCCESS);
                    }
                    mUpdateListenerMap.remove(packageName);
                }
            }
        }
        
    }
    
}
