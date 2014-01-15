/*
 * 文件名: BaseService.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

import com.huawei.basic.android.im.common.FusionAction.ServiceAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.service.app.IAppEntry;
import com.huawei.basic.android.im.component.service.impl.IObserver;
import com.huawei.basic.android.im.component.service.impl.LoginImpl;
import com.huawei.basic.android.im.component.service.impl.XmppImpl;
import com.huawei.basic.android.im.logic.adapter.db.UserConfigDbAdapter;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.utils.DeferredHandler;
import com.huawei.basic.android.im.utils.GlobalExceptionHandler;

/**
 * 沃友项目提供的service，包含登录及XMPP服务<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-10]
 */
public class BaseService extends Service
{
    /**
     * debug tag
     */
    private static final String TAG = "BaseService";
    
    /**
     * 登录接口对象
     */
    private ILogin mLogin;
    
    /**
     * XMPP接口对象
     */
    private IXmpp mXmpp;
    
    /**
     * WoApp
     */
    private IAppEntry mAppEntry;
    
    /**
     * 当前登录用户的系统id
     */
    private String mUserSysID;
    
    /**
     * 当前登录用户的账号id
     */
    private String mUserID;
    
    private Handler mHandler;
    
    /*
     * 线程队列handler
     */
    private DeferredHandler mDeferredHandler;
    
    private IServiceEntry.Stub mBinder = new IServiceEntry.Stub()
    {
        
        @Override
        public int unSubNotify(String comId, int cmdId) throws RemoteException
        {
            return mXmpp.unSubNotify(comId, cmdId);
        }
        
        @Override
        public int subNotify(String comId, int cmdId) throws RemoteException
        {
            return mXmpp.subNotify(comId, cmdId);
        }
        
        @Override
        public void logout(String userID, String userSysID, String token,
                String loginID) throws RemoteException
        {
            // 注销时同时进行HTTP注销和XMPP注销
            mLogin.logout(userID, userSysID, token, loginID);
            //            mXmpp.deregister(userID);
        }
        
        @Override
        public void login(String account, String password, String verifyCode,
                String clientVersion) throws RemoteException
        {
            mLogin.login(account, password, verifyCode, clientVersion);
        }
        
        @Override
        public void reLogin() throws RemoteException
        {
            mLogin.login(0);
        }
        
        /**
         * <BR>
         * 
         * @param userSysID
         * @param token
         * @throws RemoteException
         * @see com.huawei.basic.android.im.component.service.core.IServiceEntry#refreshToken(java.lang.String, java.lang.String)
         */
        
        @Override
        public void refreshToken(String userSysID, String token)
                throws RemoteException
        {
            mLogin.refreshToken(userSysID, token);
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.component.service.core.IServiceEntry#stopRefreshToken()
         */
        
        @Override
        public void stopRefreshToken() throws RemoteException
        {
            mLogin.stopRefreshToken();
        }
        
        @Override
        public String executeCommand(String comId, int cmdId, String data)
                throws RemoteException
        {
            
            return mXmpp.executeCommand(comId, cmdId, data);
        }
        
        @Override
        public void registerCallback(IAppEntry appEntry) throws RemoteException
        {
            BaseService.this.mAppEntry = appEntry;
            
        }
        
        @Override
        public void requestLoginMessage()
        {
            mLogin.requestLoginMessage();
        }
        
        @Override
        public void connectionChangerLogin(long delay) throws RemoteException
        {
            mLogin.login(delay);
        }
        
        @Override
        public void sendNetMessage(int stauts) throws RemoteException
        {
            mLogin.sendNetMessage(stauts);
        }
        
    };
    
    /**
     * 监听器对象
     */
    private IObserver mObserver = new IObserver()
    {
        
        /**
         * <BR>
         * 
         * @param messageType
         * @param result
         * @see com.huawei.basic.android.im.component.service.impl.IObserver#sendXmppMessage(int, java.lang.String)
         */
        
        @Override
        public void sendXmppMessage(int messageType, String result)
        {
            try
            {
                mAppEntry.sendXmppMessage(messageType, result);
            }
            catch (RemoteException e)
            {
                Logger.e(TAG, "sendXmppMessage error", e);
            }
            catch (NullPointerException e)
            {
                Logger.i(TAG, "entry not ready");
            }
        }
        
        /**
         * <BR>
         * 
         * @param messageType
         * @param result
         * @see com.huawei.basic.android.im.component.service.impl.IObserver#sendLoginMessage(int, java.lang.String)
         */
        
        @Override
        public void sendLoginMessage(int messageType, String result)
        {
            Logger.i(TAG, "sendLoginMessage, code:" + messageType);
            try
            {
                mAppEntry.sendLoginMessage(messageType, result);
            }
            catch (RemoteException e)
            {
                Logger.e(TAG, "sendLoginMessage error", e);
                mLogin.serviceProcess(messageType, result);
            }
            catch (NullPointerException e)
            {
                Logger.i(TAG, "entry not ready");
                mLogin.serviceProcess(messageType, result);
            }
        }
        
        @Override
        public void loginSuccessCallback(AASResult aasResult)
        {
            //先将AAS结果返回，再进行XMPP注册
            try
            {
                mAppEntry.loginSuccessCallback(aasResult);
            }
            catch (RemoteException e)
            {
                Logger.e(TAG, "loginSuccessCallback error", e);
            }
            catch (NullPointerException e)
            {
                Logger.i(TAG, "entry not ready");
            }
            mUserSysID = aasResult.getUserSysId();
            mUserID = aasResult.getUserID();
            FusionConfig.getInstance().setAasResult(aasResult);
        }
        
        @Override
        public void registerXmpp(AASResult aasResult)
        {
            mXmpp.register(aasResult);
        }
        
        /**
         * 
         * 注销xmpp<BR>
         * 
         * @param userID 用户业务ID
         */
        @Override
        public void deRegisterXmpp(String userID)
        {
            mXmpp.deregister(userID);
        }
        
        @Override
        public String executeCommand(String comId, int cmdId, String data)
        {
            
            return mXmpp.executeCommand(comId, cmdId, data);
        }
        
        @Override
        public void showNotification(String message)
        {
            
        }
        
        @Override
        public void showPrompt(final String message)
        {
            // 回调UI
            new Handler(Looper.getMainLooper(), new Handler.Callback()
            {
                @Override
                public boolean handleMessage(Message msg)
                {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG)
                            .show();
                    return true;
                }
            }).sendEmptyMessage(0);
        }
        
        @Override
        public String getString(int resID)
        {
            return getContext().getString(resID);
        }
        
        @Override
        public String getUserSysID()
        {
            return mUserSysID;
        }
        
        @Override
        public Context getContext()
        {
            return BaseService.this.getApplicationContext();
        }
        
        @Override
        public Handler getHandler()
        {
            return mHandler;
        }
        
        /**
         * 获取DeferredHandler
         */
        public DeferredHandler getDeferredHandler()
        {
            return mDeferredHandler;
        }
        
        @Override
        public void xmppCallback(String componentID, int notifyID, String data)
        {
            try
            {
                mAppEntry.xmppCallback(componentID, notifyID, data);
            }
            catch (RemoteException e)
            {
                Logger.e(TAG, "xmppCallback error", e);
            }
            catch (NullPointerException e)
            {
                Logger.i(TAG, "entry not ready");
            }
        }
        
        @Override
        public String getUserID()
        {
            return mUserID;
        }
        
        @Override
        public boolean isSoundOpen()
        {
            UserConfigModel config = UserConfigDbAdapter.getInstance(getContext())
                    .queryByKey(getUserSysID(), UserConfigModel.VOICE_TIPS);
            return config == null
                    || !UserConfigModel.CLOSE_VOICE.equals(config.getValue());
        }
    };
    
    /**
     * onCreate()<BR>
     * 实例化及初始化mLogin及mXmpp
     * 
     * @see android.app.Service#onCreate()
     */
    
    @Override
    public void onCreate()
    {
        Logger.i(TAG, "onCreate");
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        try
        {
            FusionConfig.getInstance()
                    .setClientVersion(mObserver.getContext()
                            .getPackageManager()
                            .getPackageInfo(mObserver.getContext()
                                    .getPackageName(),
                                    0).versionName);
        }
        catch (NameNotFoundException e)
        {
            Logger.e(TAG, "service get verion name fail.", e);
        }
        mHandler = new Handler(getMainLooper());
        mDeferredHandler = new DeferredHandler();
        
        mLogin = buildLogin();
        if (mLogin == null)
        {
            Logger.e(TAG, "mLogin is NULL!");
        }
        else
        {
            if (!mLogin.init())
            {
                Logger.e(TAG, "mLogin initialized failure!");
            }
        }
        
        mXmpp = buildXmpp();
        if (mXmpp == null)
        {
            Logger.e(TAG, "mXmpp is NULL!");
        }
        else
        {
            if (!mXmpp.init())
            {
                Logger.e(TAG, "mXmpp initialized failure!");
            }
        }
        //初始化通知栏管理类
        NotificationEntityManager.getInstance().init(getApplicationContext());
        mLogin.serviceStart();
        sendBroadcast(new Intent(ServiceAction.SERVICE_RECREATE));
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Logger.i(TAG, "onStartCommand");
        int status = -1;
        if (null != intent)
        {
            status = intent.getIntExtra(ServiceAction.CONNECTIVITY, status);
        }
        if (LoginMessageType.STATUS_BOOT == status || -1 == status)
        {
            mLogin.serviceStart();
        }
        else
        {
            mLogin.sendNetMessage(status);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    
    /**
     * 
     * <BR>
     * 
     * @param intent Intent intent
     * @return IBinder
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        Logger.i(TAG, "onBind");
        return mBinder;
    }
    
    /**
     * 生成ILogin的具体实现类对象
     * @return ILogin
     */
    protected ILogin buildLogin()
    {
        return new LoginImpl(mObserver);
    }
    
    /**
     * 
     * 生成IXmpp的具体实现类对象<BR>
     * 
     * @return IXmpp
     */
    protected IXmpp buildXmpp()
    {
        return new XmppImpl(mObserver, mLogin);
    }
}
