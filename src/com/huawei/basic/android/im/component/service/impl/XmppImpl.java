/*
 * 文件名: XmppImpl.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.bridge.impl.EngineBridge;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.service.core.ILoginXmppListener;
import com.huawei.basic.android.im.component.service.core.IXmpp;
import com.huawei.basic.android.im.component.service.impl.xmpp.GroupXmppMng;
import com.huawei.basic.android.im.component.service.impl.xmpp.MsgXmppMng;
import com.huawei.basic.android.im.component.service.impl.xmpp.PresenceXmppMng;
import com.huawei.basic.android.im.component.service.impl.xmpp.RegisterXmppMng;
import com.huawei.basic.android.im.component.service.impl.xmpp.XmppMng;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.utils.DeferredHandler;
import com.huawei.fast.IEngineBridge;
import com.huawei.fast.IEngineListener;

/**
 * XMPP逻辑实现类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-10]
 */
public class XmppImpl implements IXmpp
{
    
    /**
     * debug tag
     */
    private static final String TAG = "XmppImpl";
    
    private IObserver mObserver;
    
    private ILoginXmppListener mLoginXmppListener;
    
    /**
     * XMPP组件管理类集合
     */
    private List<XmppMng> mXmppMngList;
    
    /**
     * XMPP注册类
     */
    private RegisterXmppMng regXmppMng;
    
    /**
     * XMPP连接桥，通过调用该实例的若干方法进行XMPP通信
     */
    private IEngineBridge mEngineBridge;
    
    /**
     * XMPP连接桥监听器定义
     */
    private IEngineListener mEngineListener = new IEngineListener()
    {
        
        @Override
        public void notifyCallback(final String componentID,
                final int notifyID, final String data)
        {
            DeferredHandler handler = mObserver.getDeferredHandler();
            Runnable runnable = new Runnable()
            {
                
                @Override
                public void run()
                {
                    Logger.i(TAG, "componentID[" + componentID + "] notifyID["
                            + notifyID + "] data[" + data + "]");
                    // 根据componentId获取对应的XmppMng对象进行处理
                    XmppMng xmppMng = getCorrespondingXmppMng(componentID,
                            notifyID);
                    
                    if (xmppMng != null)
                    {
                        xmppMng.handleNotification(componentID, notifyID, data);
                    }
                }
            };
            if (BaseParams.RegisterParams.FAST_COM_REGISTER_ID.equals(componentID))
            {
                handler.postFirst(runnable);
            }
            else
            {
                handler.post(runnable);
            }
        }
        
    };
    
    /**
     * 构造函数 
     * 
     * @param observer IObserver
     * @param loginXmppListener ILoginXmppListener
     */
    public XmppImpl(IObserver observer, ILoginXmppListener loginXmppListener)
    {
        mObserver = observer;
        mLoginXmppListener = loginXmppListener;
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.core.IXmpp#init()
     */
    @Override
    public boolean init()
    {
        
        // 初始化EngineBridge，设置回调监听器
        mEngineBridge = EngineBridge.getInstance();
        mEngineBridge.setListener(mEngineListener);
        
        // 初始化框架.结果为0表示成功
        int iRet = mEngineBridge.initializeFrame();
        Logger.d(TAG, "Result of initialize XMPP frame : " + iRet);
        
        // 开始加载XMPP动态库
        iRet = mEngineBridge.loadComponent("/data/data/com.uim/lib/libxmppservice.so");
        Logger.d(TAG, "Result of load component : " + iRet);
        // 1.xmpp相关服务初始化及业务订阅
        mXmppMngList = new ArrayList<XmppMng>();
        // 订阅连接状态通知
        regXmppMng = new RegisterXmppMng(mEngineBridge, mObserver,
                mLoginXmppListener);
        
        mXmppMngList.add(regXmppMng);
        // presence相关的注册
        mXmppMngList.add(new PresenceXmppMng(mEngineBridge, mObserver));
        // 群组相关的注册
        mXmppMngList.add(new GroupXmppMng(mEngineBridge, mObserver));
        mXmppMngList.add(new MsgXmppMng(mEngineBridge, mObserver));
        return true;
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.core.IXmpp#register(com.huawei.basic.android.im.logic.model.AASResult, com.huawei.basic.android.im.component.service.core.ILoginXmppListener)
     */
    @Override
    public void register(AASResult aasResult)
    {
        //将所有的注册加入List后，xmpp请求
        regXmppMng.registerXmpp(aasResult);
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.core.IXmpp#deregister(java.lang.String)
     */
    @Override
    public void deregister(String userID)
    {
        regXmppMng.deregister(userID);
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.core.IXmpp#subNotify(java.lang.String, int)
     */
    @Override
    public int subNotify(String comId, int notifyId)
    {
        return mEngineBridge.subNotify(comId, notifyId);
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.core.IXmpp#unSubNotify(java.lang.String, int)
     */
    @Override
    public int unSubNotify(String comId, int notifyId)
    {
        return mEngineBridge.unSubNotify(comId, notifyId);
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.core.IXmpp#executeCommand(java.lang.String, int, java.lang.String)
     */
    @Override
    public String executeCommand(String comId, int cmdId, String data)
    {
        return mEngineBridge.executeCommand(comId, cmdId, data);
    }
    
    /**
     * 根据组件id获取对应的组件管理类对象
     * <BR>
     * 
     * @param componentId 组件id
     * @param notifyID 订阅ID
     * @return XmppMng具体的XMPP管理类对象
     */
    private XmppMng getCorrespondingXmppMng(String componentId, int notifyID)
    {
        for (XmppMng xmppMng : mXmppMngList)
        {
            if (xmppMng.matched(componentId, notifyID))
            {
                return xmppMng;
            }
        }
        return null;
    }
    
}
