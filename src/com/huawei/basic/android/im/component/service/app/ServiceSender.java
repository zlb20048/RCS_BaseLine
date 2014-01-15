/*
 * 文件名: XmppService.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.app;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.huawei.basic.android.im.common.FusionAction.ServiceAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.service.core.IServiceEntry;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.utils.DeferredHandler;

/**服务发送实现类<BR>
 * 负责启动SERVICE并且实现APP调用服务的接口方法
 * @author 刘鲁宁
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 13, 2012] 
 */
public class ServiceSender implements IServiceSender
{
    
    /**
     * debug tag
     */
    private static final String TAG = "ServiceSender";
    
    /**
     * IServiceSender实例
     */
    private static ServiceSender serviceSender;
    
    /**
     * 登录服务监听器列表
     */
    private final List<ILoginServiceListener> mLoginServiceListeners = new ArrayList<ILoginServiceListener>();
    
    /**
     * XMPP服务监听器列表
     */
    private final List<IXmppServiceListener> mXmppServiceListeners = new ArrayList<IXmppServiceListener>();
    
    private IServiceEntry mServiceEntry;
    
    private Context mApplicationContext;
    
    private int mPanding;
    
    private BroadcastReceiver mNewServiceReceiver = new BroadcastReceiver()
    {
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "receiver new service");
            if (null != serviceSender)
            {
                Log.i(TAG, "rebind new service");
                serviceSender.startService();
            }
        }
    };
    
    private DeferredHandler mDeferredHandler = new DeferredHandler();
    
    private IAppEntry.Stub mBinder = new IAppEntry.Stub()
    {
        
        /**
         * <BR>
         * 
         * @param messageType
         * @param result
         * @throws RemoteException
         * @see com.huawei.basic.android.im.component.service.app.IAppEntry#sendXmppMessage(int, java.lang.String)
         */
        
        @Override
        public void sendXmppMessage(final int messageType, final String result)
                throws RemoteException
        {
            mDeferredHandler.post(new Runnable()
            {
                
                @Override
                public void run()
                {
                    ServiceSender.this.sendXmppMessage(messageType, result);
                }
            });
        }
        
        /**
         * <BR>
         * 
         * @param messageType
         * @param result
         * @throws RemoteException
         * @see com.huawei.basic.android.im.component.service.app.IAppEntry#sendLoginMessage(int, java.lang.String)
         */
        
        @Override
        public void sendLoginMessage(final int messageType, final String result)
                throws RemoteException
        {
            mDeferredHandler.post(new Runnable()
            {
                
                @Override
                public void run()
                {
                    ServiceSender.this.sendLoginMessage(messageType, result);
                }
            });
        }
        
        @Override
        public void loginSuccessCallback(final AASResult aasResult)
                throws RemoteException
        {
            mDeferredHandler.post(new Runnable()
            {
                
                @Override
                public void run()
                {
                    ServiceSender.this.loginSuccessCallback(aasResult);
                }
            });
        }
        
        @Override
        public void xmppCallback(final String componentID, final int notifyID,
                final String data) throws RemoteException
        {
            mDeferredHandler.post(new Runnable()
            {
                
                @Override
                public void run()
                {
                    ServiceSender.this.xmppCallback(componentID, notifyID, data);
                }
            });
        }
        
    };
    
    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1)
        {
            Logger.i(TAG, "onServiceConnected");
            IServiceEntry serviceEntry = IServiceEntry.Stub.asInterface(arg1);
            try
            {
                serviceEntry.registerCallback(mBinder);
                mServiceEntry = serviceEntry;
                handlerPanding();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
            mApplicationContext.registerReceiver(mNewServiceReceiver,
                    new IntentFilter(ServiceAction.SERVICE_RECREATE));
        }
        
        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            
        }
        
    };
    
    /**
     * 私有构造方法
     */
    private ServiceSender(Context context)
    {
        mApplicationContext = context;
    }
    
    /**
     * 
     * 启动service<BR>
     * 
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#startService(android.content.Context)
     */
    private void startService()
    {
        Logger.d(TAG, "start service");
        Intent intent = new Intent(ServiceAction.SERVICE_ACTION);
        mApplicationContext.startService(intent);
        mApplicationContext.bindService(intent,
                mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }
    
    /**
     * 添加登录服务监听器<BR>
     * 
     * @param serviceListener ILoginServiceListener
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#
     * addServiceListener(com.huawei.basic.android.im.component.service.app.ILoginServiceListener)
     */
    @Override
    public void addLoginServiceListener(ILoginServiceListener serviceListener)
    {
        mLoginServiceListeners.add(serviceListener);
    }
    
    /**
     * 添加XMPP服务监听器<BR>
     * 
     * @param serviceListener IXmppServiceListener
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#
     * addServiceListener(com.huawei.basic.android.im.component.service.app.IXmppServiceListener)
     */
    
    @Override
    public void addXmppServiceListener(IXmppServiceListener serviceListener)
    {
        mXmppServiceListeners.add(serviceListener);
    }
    
    /**
     * 移除监听器<BR>
     * 
     * @param serviceListener ILoginServiceListener
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#
     * removeLoginServiceListener(com.huawei.basic.android.im.component.service.app.ILoginServiceListener)
     */
    
    @Override
    public void removeLoginServiceListener(ILoginServiceListener serviceListener)
    {
        mLoginServiceListeners.remove(serviceListener);
    }
    
    /**
     * <BR>
     * 
     * @param serviceListener IXmppServiceListener
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#
     * removeXmppServiceListener(com.huawei.basic.android.im.component.service.app.IXmppServiceListener)
     */
    
    @Override
    public void removeXmppServiceListener(IXmppServiceListener serviceListener)
    {
        mXmppServiceListeners.remove(serviceListener);
    }
    
    /**
     * <BR>
     * 
     * @param account String
     * @param password String
     * @param verifyCode String
     * @param clientVersion String
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#
     * login(java.lang.String, java.lang.String, java.lang.String)
     */
    
    @Override
    public void login(String account, String password, String verifyCode,
            String clientVersion)
    {
        try
        {
            mServiceEntry.login(account, password, verifyCode, clientVersion);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            mServiceEntry = null;
            setPanding(LoginMessageType.LOGIN);
            startService();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
            setPanding(LoginMessageType.LOGIN);
        }
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#logout(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void logout(String userID, String userSysID, String token,
            String loginID)
    {
        try
        {
            mServiceEntry.logout(userID, userSysID, token, loginID);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#refreshToken(java.lang.String, java.lang.String)
     */
    @Override
    public void refreshToken(String userSysID, String token)
    {
        try
        {
            mServiceEntry.refreshToken(userSysID, token);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#stopRefreshToken()
     */
    @Override
    public void stopRefreshToken()
    {
        try
        {
            mServiceEntry.stopRefreshToken();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
    }
    
    /**
     * <BR>
     * 
     * @param comId String
     * @param notifyId int
     * @return 订阅结果，0为成功
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#subNotify(java.lang.String, int)
     */
    
    @Override
    public int subNotify(String comId, int notifyId)
    {
        try
        {
            return mServiceEntry.subNotify(comId, notifyId);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
        return -1;
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#unSubNotify(java.lang.String, int)
     */
    @Override
    public int unSubNotify(String comId, int notifyId)
    {
        try
        {
            return mServiceEntry.unSubNotify(comId, notifyId);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
        return -1;
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#executeCommand(java.lang.String, int, java.lang.String)
     */
    @Override
    public String executeCommand(String comId, int cmdId, String data)
    {
        try
        {
            String result = mServiceEntry.executeCommand(comId, cmdId, data);
            
            return result;
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
        return null;
    }
    
    /**
     * 登录成功的回调
     * @param aasResult AASResult
     */
    private void loginSuccessCallback(AASResult aasResult)
    {
        FusionConfig.getInstance().setAasResult(aasResult);
        ILoginServiceListener[] listeners = new ILoginServiceListener[mLoginServiceListeners.size()];
        listeners = mLoginServiceListeners.toArray(listeners);
        for (ILoginServiceListener serviceListener : listeners)
        {
            serviceListener.loginSuccessCallback(aasResult);
        }
    }
    
    /**
     * 
     * XMPP回调<BR>
     * 
     * @param componentID 组件ID
     * @param notifyID 订阅ID
     * @param data 数据
     */
    private void xmppCallback(String componentID, int notifyID, String data)
    {
        IXmppServiceListener[] listeners = new IXmppServiceListener[mXmppServiceListeners.size()];
        listeners = mXmppServiceListeners.toArray(listeners);
        for (IXmppServiceListener serviceListener : listeners)
        {
            serviceListener.xmppCallback(componentID, notifyID, data);
        }
    }
    
    /**
     * 
     * XMPP消息回调<BR>
     * 
     * @param messageType 消息类型
     * @param result 返回结果
     */
    private void sendXmppMessage(int messageType, String result)
    {
        IXmppServiceListener[] listeners = new IXmppServiceListener[mXmppServiceListeners.size()];
        listeners = mXmppServiceListeners.toArray(listeners);
        for (IXmppServiceListener serviceListener : listeners)
        {
            serviceListener.sendXmppMessage(messageType, result);
        }
        
    }
    
    /**
     * 
     * 登录消息回调<BR>
     * 
     * @param messageType 消息类型
     * @param result 返回结果
     */
    private void sendLoginMessage(int messageType, String result)
    {
        
        ILoginServiceListener[] listeners = new ILoginServiceListener[mLoginServiceListeners.size()];
        listeners = mLoginServiceListeners.toArray(listeners);
        for (ILoginServiceListener serviceListener : listeners)
        {
            serviceListener.sendLoginMessage(messageType, result);
        }
    }
    
    /**
     * 获取一个IServiceSender实例
     * 
     * @param context Context
     * 
     * @return IServiceSender
     */
    public static synchronized IServiceSender getIServiceSender(Context context)
    {
        if (null == serviceSender)
        {
            serviceSender = new ServiceSender(context);
        }
        serviceSender.startService();
        return serviceSender;
    }
    
    /**
     * 自动登录
     * @param delay 延时
     * @see com.huawei.basic.android.im.component.service.app.IServiceSender#login(long)
     */
    
    @Override
    public void login(long delay)
    {
        try
        {
            mServiceEntry.connectionChangerLogin(delay);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
    }
    
    /**
     * 
     * 发送网络状态
     * 
     * @param stauts
     *            网络状态
     */
    @Override
    public void sendNetMessage(int stauts)
    {
        try
        {
            mServiceEntry.sendNetMessage(stauts);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
        }
    }
    
    private void setPanding(int panding)
    {
        mPanding |= panding;
    }
    
    private void handlerPanding() throws RemoteException
    {
        if (LoginMessageType.STATUS == (mPanding & LoginMessageType.STATUS))
        {
            mServiceEntry.requestLoginMessage();
        }
        if (LoginMessageType.LOGIN == (mPanding & LoginMessageType.LOGIN))
        {
            sendLoginMessage(LoginMessageType.LOGIN, null);
        }
        mPanding = 0;
    }
    
    /**
     * 请求登录信息 <BR>
     * 
     * 
     */
    @Override
    public void requestLoginMessage()
    {
        try
        {
            mServiceEntry.requestLoginMessage();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            mServiceEntry = null;
            setPanding(LoginMessageType.STATUS);
            startService();
        }
        catch (NullPointerException e)
        {
            Logger.i(TAG, "entry not ready");
            setPanding(LoginMessageType.STATUS);
        }
    }
}
