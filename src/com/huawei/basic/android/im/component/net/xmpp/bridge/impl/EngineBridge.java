/*
 * 文件名: EngineBridge.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: admin
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.bridge.impl;


import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.fast.IEngineBridge;
import com.huawei.fast.IEngineListener;
import com.huawei.fast.UniSwitchJni;

/**
 * 桥接的实现类<BR>
 * 通过桥实现对对底层调用的封装
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-13]
 */
public class EngineBridge implements IEngineBridge
{

    /**
     * TAG
     */
    public static final String TAG = "EngineBridge";
    // 加载动态链接库
    {
        Logger.i(TAG,
            "loading  component xmppservice");
        System.loadLibrary("xmppservice");
        Logger.i(TAG,
            "load xmppservice success!");
    }

    /**
     * 实例对象
     */
    private static IEngineBridge engineBridge;

    /**
     * uniSwitch组件调用入口
     */
    private UniSwitchJni uniSwitchEntry;

    private IEngineListener mEngineListener;

    /**
     * 初始化uniSwitch,xmlParser,classHashMap,listenerHashMap
     */
    private EngineBridge()
    {
        uniSwitchEntry = new UniSwitchJni(this);
    }

    /**
     * 返回EngineBridge的一个实例
     * <p>
     * 单例模式
     * </p>
     * 
     * @return EngineBridge的一个实例
     */
    public static synchronized IEngineBridge getInstance()
    {
        if (engineBridge == null)
        {
            engineBridge = new EngineBridge();
        }
        return engineBridge;
    }

    public void setListener(IEngineListener engineListener)
    {
        mEngineListener = engineListener;
    }

    /**
     * 
     * 初始化框架<BR>
     * 
     * @return 初始化结果
     * @see com.chinaunicom.woyou.xmppengine.bridge.IEngineBridge#initializeFrame()
     */
    @Override
    public int initializeFrame()
    {
        Logger.i(TAG,
            "initializeFrame");
        return uniSwitchEntry.initializeFrame();
    }

    /**
     * 去初始化框架<BR>
     * 
     * @see com.chinaunicom.woyou.xmppengine.bridge.IEngineBridge#uninitializeFrame()
     */
    @Override
    public void uninitializeFrame()
    {
        Logger.i(TAG,
            "uninitializeFrame");
        uniSwitchEntry.uninitializeFrame();
    }

    /**
     * 加载组件<BR>
     * 
     * @param comId 组件id
     * @return 加载组件结果
     * @see com.chinaunicom.woyou.xmppengine.bridge.IEngineBridge#loadComponent(java.lang.String)
     */
    @Override
    public int loadComponent(String comId)
    {
        Logger.i(TAG,
            "loadComponent");
        return uniSwitchEntry.loadComponent(comId);
    }

    /**
     * 
     * 卸载组件<BR>
     * 
     * @param comId 组件id
     * @return 卸载组件结果
     * @see com.chinaunicom.woyou.xmppengine.bridge.IEngineBridge#unloadComponent(java.lang.String)
     */
    @Override
    public int unloadComponent(String comId)
    {
        Logger.i(TAG,
            "unloadComponent");
        return uniSwitchEntry.unloadComponent(comId);
    }

    /**
     * 执行命令<BR>
     * 
     * @param comId 命令组件唯一标识符
     * @param cmdId 命令ID
     * @param data 执行参数
     * @return 执行命令结果
     */
    @Override
    public String executeCommand(String comId, int cmdId, String data)
    {
        Logger.i(TAG,
            "executeCommand");
        return uniSwitchEntry.executeCommand(comId,
            cmdId,
            data);
    }

    /**
     * 订阅通知<BR>
     * @param comId 组件ID
     * @param ntyId 通知ID
     * @return 订阅结果
     * @see com.chinaunicom.woyou.xmppengine.bridge.IEngineBridge#subNotify
     *      (com.chinaunicom.woyou.xmppengine.data.CombineIdentify,
     *      com.huawei.fast.chinaunicom.woyou.xmppengine.bridge.IEngineListener,
     *      java.lang.Class)
     */
    @Override
    public int subNotify(String comId, int ntyId)
    {
        Logger.i(TAG,
            "subNotify");
        int status = uniSwitchEntry.subNotify(comId,
            ntyId);
        return status;
    }

    /**
     * 取消订阅<BR>
     * @param comId 组件ID
     * @param ntfId 命令ID
     * @return 取消订阅结果
     * @see com.chinaunicom.woyou.xmppengine.bridge.IEngineBridge#unSubNotify
     *      (com.chinaunicom.woyou.xmppengine.data.CombineIdentify)
     */
    @Override
    public int unSubNotify(String comId, int ntfId)
    {
        Logger.i(TAG,
            "unSubNotify");
        return uniSwitchEntry.unSubNotify(comId,
            ntfId);
    }

    /**
     * 回调接口<BR>
     * @param comId 组件ID
     * @param ntfId 命令ID
     * @param param 回调参数
     * @see com.chinaunicom.woyou.xmppengine.bridge.IEngineBridge#notifyCallback
     *      (com.chinaunicom.woyou.xmppengine.data.CombineIdentify,
     *      java.lang.String)
     */
    @Override
    public void notifyCallback(String comId, int ntfId, String param)
    {
        Logger.i(TAG,
            "notifyCallback");
        mEngineListener.notifyCallback(comId,
            ntfId,
            param);
    }
}
