/*
 * 文件名: LoginImpl.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl;

import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceData;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.notification.TextNotificationEntity;
import com.huawei.basic.android.im.component.service.core.ILogin;
import com.huawei.basic.android.im.logic.adapter.db.AccountDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.LoginHttpManager;
import com.huawei.basic.android.im.logic.login.receiver.ConnectionChangedReceiver;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.logic.model.AccountModel;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * Service层登录模块实现类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-6]
 */
public class LoginImpl implements ILogin
{
    
    /**
     * 刷新TOKEN的间隔，为30分钟
     */
    private static final int INTERVAL_REFRESH_TOKEN = 30 * 60 * 1000;
    
    /**
     * 重试刷新TOKEN的间隔，为5分钟
     */
    private static final int INTERVAL_RETRY_REFRESH_TOKEN = 5 * 60 * 1000;
    
    private static final String TAG = "LoginImpl";
    
    private IObserver mObserver;
    
    private AASResult mAASResult;
    
    private String mAccount;
    
    private String mPassword;
    
    private String mVersionName;
    
    private int retryTimes;
    
    private CountDownLatch mAccessLatch;
    
    private boolean register;
    
    private TextNotificationEntity mNotification;
    
    private Handler aasHandler = new AASHandler();
    
    /**
     * 处理aas登录的handler类
     * 
     * @author qinyangwang
     * @version [RCS Client V100R001C03, 2012-3-27]
     */
    private class AASHandler extends Handler
    {
        private static final int LOGIN_AAS = 0;
        
        private static final int LOGIN_XMPP = 1;
        
        private static final int REFRESH_TOKEN = 2;
        
        private static final int LOGIN_TOKEN = 3;
        
        @Override
        public void handleMessage(Message msg)
        {
            if (LoginMessageType.NET_STATUS_DISABLE != ConnectionChangedReceiver.checkNet(mObserver.getContext()))
            {
                removeMessages(msg.what);
                AASResult aasResult = mAASResult;
                switch (msg.what)
                {
                    case LOGIN_XMPP:
                        if (null != aasResult)
                        {
                            if (LoginMessageType.NET_STATUS_ENABLE == ConnectionChangedReceiver.checkNet(mObserver.getContext()))
                            {
                                registerXmpp(aasResult);
                            }
                            break;
                        }
                    case LOGIN_AAS:
                        checkAccountInfo();
                        aasLogin(null);
                        break;
                    
                    case REFRESH_TOKEN:
                        if (null != aasResult)
                        {
                            refreshToken(aasResult);
                            break;
                        }
                    case LOGIN_TOKEN:
                        loginToken();
                        break;
                }
            }
        }
        
    }
    
    /**
     * 构造函数
     * 
     * @param observer
     *            ILoginObserver
     */
    public LoginImpl(IObserver observer)
    {
        mObserver = observer;
    }
    
    /**
     * 
     * 注册XMPP
     * 
     * @param aasResult
     *            AASResult
     */
    private void registerXmpp(AASResult aasResult)
    {
        mObserver.sendLoginMessage(LoginMessageType.STATUS_LOGINING, null);
        CountDownLatch latch = mAccessLatch;
        Logger.i(TAG, "before sync registerXmpp");
        if (latch != null)
        {
            try
            {
                latch.await();
                mAccessLatch = null;
            }
            catch (InterruptedException e)
            {
                Logger.e(TAG, "latch interrupted registerXmpp", e);
            }
        }
        Logger.i(TAG, "after sync registerXmpp");
        mObserver.registerXmpp(aasResult);
    }
    
    /**
     * 
     * 注销XMPP
     * 
     * @param userID
     *            用户id
     */
    private void deRegisterXmpp(String userID)
    {
        if (register)
        {
            
            if (null == mAccessLatch)
            {
                mAccessLatch = new CountDownLatch(1);
            }
            
            mObserver.deRegisterXmpp(userID);
        }
        else
        {
            CountDownLatch latch = mAccessLatch;
            if (latch != null)
            {
                latch.countDown();
            }
        }
    }
    
    /**
     * 
     * Token 登录
     */
    private void loginToken()
    {
        checkAccountInfo();
        new LoginHttpManager().login(mAccount,
                mPassword,
                null,
                mVersionName,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        AASResult aasResult = (AASResult) response.getObj();
                        if (ResponseCode.Succeed == response.getResponseCode())
                        {
                            if ("0".equals(aasResult.getResult()))
                            {
                                mAASResult = aasResult;
                                aasHandler.sendEmptyMessageDelayed(AASHandler.REFRESH_TOKEN,
                                        INTERVAL_REFRESH_TOKEN);
                                mObserver.loginSuccessCallback(aasResult);
                            }
                            else if (FusionErrorInfo.isRetryCode(aasResult.getResult()))
                            {
                                aasHandler.sendEmptyMessageDelayed(AASHandler.LOGIN_TOKEN,
                                        INTERVAL_RETRY_REFRESH_TOKEN);
                            }
                            else
                            {
                                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                        aasResult.getResult());
                            }
                        }
                        else
                        {
                            aasHandler.sendEmptyMessageDelayed(AASHandler.LOGIN_TOKEN,
                                    INTERVAL_RETRY_REFRESH_TOKEN);
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 
     * aas登录
     * 
     * @param verifyCode
     *            验证码
     */
    private void aasLogin(String verifyCode)
    {
        if (StringUtil.isNullOrEmpty(mAccount)
                || StringUtil.isNullOrEmpty(mPassword))
        {
            FusionConfig.getInstance()
                    .setUserStatus(LoginMessageType.LOGIN_ERROR);
            mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR, null);
            return;
        }
        FusionConfig.getInstance()
                .setUserStatus(LoginMessageType.STATUS_LOGINING);
        mObserver.sendLoginMessage(LoginMessageType.STATUS_LOGINING, null);
        new LoginHttpManager().login(mAccount,
                mPassword,
                verifyCode,
                mVersionName,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        Logger.i(TAG,
                                "aasLogin, code:" + response.getResponseCode());
                        FusionConfig.getInstance()
                                .setUserStatus(LoginMessageType.LOGIN_ERROR);
                        switch (response.getResponseCode())
                        {
                            case Succeed:
                                AASResult aasResult = (AASResult) response.getObj();
                                if (!aasResult.getResult().equals("0"))
                                {
                                    if (hasLogined()
                                            && FusionErrorInfo.isRetryCode(aasResult.getResult()))
                                    {
                                        FusionConfig.getInstance()
                                                .setUserStatus(LoginMessageType.STATUS_LOGINING);
                                        reLogin(AASHandler.LOGIN_AAS);
                                    }
                                    else
                                    {
                                        mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                                aasResult.getResult());
                                    }
                                }
                                else
                                {
                                    retryTimes = 0;
                                    FusionConfig.getInstance()
                                            .setUserStatus(LoginMessageType.STATUS_LOGINING);
                                    mAASResult = aasResult;
                                    aasHandler.removeMessages(AASHandler.LOGIN_TOKEN);
                                    aasHandler.sendEmptyMessageDelayed(AASHandler.REFRESH_TOKEN,
                                            INTERVAL_REFRESH_TOKEN);
                                    mObserver.loginSuccessCallback(aasResult);
                                    registerXmpp(aasResult);
                                }
                                break;
                            case Failed:
                                if (hasLogined()
                                        && FusionErrorInfo.isRetryHttpCode(response.getData()))
                                {
                                    FusionConfig.getInstance()
                                            .setUserStatus(LoginMessageType.STATUS_LOGINING);
                                    reLogin(AASHandler.LOGIN_AAS);
                                }
                                else
                                {
                                    mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                            FusionCode.Common.KEY_FAILED);
                                }
                                break;
                            case AuthError:
                                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                        FusionCode.Common.KEY_AUTHERROR);
                                break;
                            case NetworkError:
                                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                        FusionCode.Common.KEY_NETWORKERROR);
                                break;
                            case ParamError:
                                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                        FusionCode.Common.KEY_PARAMERROR);
                                break;
                            case Timeout:
                                if (hasLogined())
                                {
                                    FusionConfig.getInstance()
                                            .setUserStatus(LoginMessageType.STATUS_LOGINING);
                                    reLogin(AASHandler.LOGIN_AAS);
                                }
                                else
                                {
                                    mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                            FusionCode.Common.KEY_TIMEOUT);
                                }
                                break;
                            default:
                                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                                        FusionCode.Common.KEY_FAILED);
                                break;
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 
     * XMPP注册
     * 
     * @param errorCode
     *            注册返回码
     * @see com.huawei.basic.android.im.component.service.core.ILoginXmppListener#xmppRegister(int)
     */
    @Override
    public void xmppRegister(int errorCode)
    {
        Logger.i(TAG, "xmppRegister, code:" + errorCode);
        FusionConfig.getInstance().setUserStatus(LoginMessageType.LOGIN_ERROR);
        mObserver.sendLoginMessage(LoginMessageType.STATUS_LOGINING, null);
        switch (errorCode)
        {
            case XmppResultCode.Base.FAST_ERR_SUCCESS:
                register = true;
                retryTimes = 0;
                FusionConfig.getInstance()
                        .setUserStatus(LoginMessageType.STATUS_ONLINE);
                mObserver.sendLoginMessage(LoginMessageType.LOGIN_SUCCESS, null);
                NotificationEntityManager.getInstance()
                        .cancelNotification(mNotification);
                break;
            
            case XmppResultCode.Base.FAST_ERR_HOST_UNREACHABLE:
                if (hasLogined())
                {
                    FusionConfig.getInstance()
                            .setUserStatus(LoginMessageType.STATUS_BREAK);
                    mObserver.sendLoginMessage(LoginMessageType.STATUS_BREAK,
                            String.valueOf(errorCode));
                }
                else
                {
                    mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                            String.valueOf(errorCode));
                }
                break;
            
            case XmppResultCode.Register.FAST_ERR_NOT_AUTHORIZED:
                retryTimes = 0;
                reLogin(AASHandler.LOGIN_AAS);
            case XmppResultCode.Base.FAST_ERR_TIMEOUT:
            case XmppResultCode.Base.FAST_ERR_SEND:
            case XmppResultCode.Base.FAST_ERR_NET_ERROR:
            case XmppResultCode.Base.FAST_ERR_SERVER_CONNECTION_CLOSED:
                if (!hasLogined())
                {
                    mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                            String.valueOf(errorCode));
                    break;
                }
            case XmppResultCode.Register.FAST_ERR_HEARTBEAT_TIMEOUT:
            case XmppResultCode.Register.FAST_ERR_NOT_REGISTERED:
                FusionConfig.getInstance()
                        .setUserStatus(LoginMessageType.STATUS_LOGINING);
                reLogin(AASHandler.LOGIN_XMPP);
                break;
            default:
                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                        String.valueOf(errorCode));
                break;
        }
    }
    
    /**
     * 
     * XMPP注销
     * 
     * @param errorCode
     *            注销返回码
     * @see com.huawei.basic.android.im.component.service.core.ILoginXmppListener#xmppDeregister(int)
     */
    @Override
    public void xmppDeregister(int errorCode)
    {
        register = false;
        FusionConfig.getInstance().setUserStatus(LoginMessageType.LOGIN_ERROR);
        String codeStr = String.valueOf(errorCode);
        switch (errorCode)
        {
        // 注销成功、未注册、未登录都做注销动作
            case XmppResultCode.Base.FAST_ERR_SUCCESS:
                
            case XmppResultCode.Register.FAST_ERR_NOT_REGISTERED:
                CountDownLatch latch = mAccessLatch;
                if (latch != null)
                {
                    latch.countDown();
                }
            case XmppResultCode.Register.FAST_ERR_NOT_AUTHORIZED:
                // 做注销动作，然后直接退出
                break;
            case XmppResultCode.Base.FAST_ERR_HOST_UNREACHABLE:
                if (hasLogined())
                {
                    FusionConfig.getInstance()
                            .setUserStatus(LoginMessageType.STATUS_BREAK);
                    mObserver.sendLoginMessage(LoginMessageType.STATUS_BREAK,
                            codeStr);
                    break;
                }
            case XmppResultCode.Register.FAST_ERR_CONFLICT:
                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                        codeStr);
                break;
            case XmppResultCode.Base.FAST_ERR_TIMEOUT:
            case XmppResultCode.Base.FAST_ERR_SEND:
            case XmppResultCode.Base.FAST_ERR_NET_ERROR:
            case XmppResultCode.Base.FAST_ERR_SERVER_CONNECTION_CLOSED:
            case XmppResultCode.Register.FAST_ERR_HEARTBEAT_TIMEOUT:
                if (hasLogined())
                {
                    FusionConfig.getInstance()
                            .setUserStatus(LoginMessageType.STATUS_LOGINING);
                    reLogin(AASHandler.LOGIN_XMPP);
                }
                else
                {
                    mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                            codeStr);
                }
                break;
            default:
                mObserver.sendLoginMessage(LoginMessageType.LOGIN_ERROR,
                        codeStr);
                break;
        }
        if (XmppResultCode.Base.FAST_ERR_SUCCESS != errorCode
                && XmppResultCode.Register.FAST_ERR_NOT_REGISTERED != errorCode
                && XmppResultCode.Register.FAST_ERR_NOT_AUTHORIZED != errorCode)
        {
            showNotification(FusionErrorInfo.getLoginErrorInfo(mObserver.getContext(),
                    codeStr));
        }
        //        if (errorCode == XmppResultCode.Base.FAST_ERR_SUCCESS)
        //        {
        //            mObserver.sendLoginMessage(LoginMessageType.LOGIN_SUCCESS, null);
        //        }
        //        else
        //        {
        //            mObserver.sendLoginMessage(LoginMessageType.LOGIN_XMPP_FAILED,
        //                    String.valueOf(errorCode));
        //        }
    }
    
    /**
     * 
     * 登录初始化
     * 
     * @return 是否初始化
     * @see com.huawei.basic.android.im.component.service.core.ILogin#init()
     */
    @Override
    public boolean init()
    {
        return true;
    }
    
    /**
     * 
     * 登录AAS
     * 
     * @param account
     *            账号
     * @param password
     *            密码
     * @param verifyCode
     *            验证码
     * @param clientVersion
     *            版本
     * @see com.huawei.basic.android.im.component.service.core.ILogin#login(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void login(final String account, final String password,
            final String verifyCode, String clientVersion)
    {
        mAASResult = null;
        retryTimes = 0;
        mAccount = account;
        mPassword = password;
        mVersionName = clientVersion;
        aasLogin(verifyCode);
    }
    
    /**
     * 
     * AAS登出
     * 
     * @param userID
     *            用户id
     * @param userSysID
     *            系统id
     * @param token
     *            token
     * @param loginID
     *            登录id
     * @see com.huawei.basic.android.im.component.service.core.ILogin#logout(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void logout(String userID, String userSysID, String token,
            String loginID)
    {
        FusionConfig.getInstance().setUserStatus(LoginMessageType.LOGIN_ERROR);
        mAASResult = null;
        deRegisterXmpp(userID);
        new LoginHttpManager().logout(userID,
                userSysID,
                token,
                loginID,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        mObserver.sendLoginMessage(LoginMessageType.LOGOUT,
                                null);
                        //清除通知栏
                        NotificationEntityManager.getInstance()
                                .destroyAllNotification();
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
        mObserver.getDeferredHandler().cancel();
    }
    
    /**
     * 
     * 刷新TOKEN
     * 
     * @param aasResult
     *            AASResult
     * 
     * @see com.huawei.basic.android.im.component.service.core.ILogin#refreshToken(java.lang.String,
     *      java.lang.String)
     */
    private void refreshToken(AASResult aasResult)
    {
        new LoginHttpManager().refreshToken(aasResult.getUserSysId(),
                aasResult.getToken(),
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        String result = (String) response.getObj();
                        if (Response.ResponseCode.Succeed == response.getResponseCode()
                                && !StringUtil.isNullOrEmpty(result)
                                && !StringUtil.isNumeric(result))
                        {
                            aasHandler.sendEmptyMessageDelayed(AASHandler.REFRESH_TOKEN,
                                    INTERVAL_REFRESH_TOKEN);
                            AASResult aasResult = mAASResult;
                            if (null != aasResult)
                            {
                                aasResult.setToken(result);
                            }
                            mObserver.sendLoginMessage(LoginMessageType.REFRESH_TOKEN,
                                    result);
                        }
                        else if ("200044006".equals(result))
                        {
                            loginToken();
                        }
                        else
                        {
                            aasHandler.sendEmptyMessageDelayed(AASHandler.REFRESH_TOKEN,
                                    INTERVAL_RETRY_REFRESH_TOKEN);
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 请求登录信息 <BR>
     * 
     * 
     */
    @Override
    public void requestLoginMessage()
    {
        AASResult aasResult = mAASResult;
        if (null != aasResult)
        {
            mObserver.loginSuccessCallback(aasResult);
            refreshToken(aasResult);
        }
        int status = ConnectionChangedReceiver.checkNet(mObserver.getContext());
        if (LoginMessageType.NET_STATUS_ENABLE == status)
        {
            mObserver.sendLoginMessage(FusionConfig.getInstance()
                    .getUserStatus(), null);
        }
        else
        {
            mObserver.sendLoginMessage(status, null);
        }
    }
    
    /**
     * 
     * 登录AAS
     * 
     * @param delay
     *            登录请求的延时
     * @see com.huawei.basic.android.im.component.service.core.ILogin#login(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void login(long delay)
    {
        if (0 > delay)
        {
            delay = 0;
        }
        else
        {
            retryTimes = 0;
        }
        AASResult aasResult = mAASResult;
        if (null != aasResult)
        {
            mObserver.loginSuccessCallback(aasResult);
            refreshToken(aasResult);
        }
        aasHandler.removeMessages(AASHandler.LOGIN_XMPP);
        aasHandler.sendEmptyMessageDelayed(AASHandler.LOGIN_XMPP, delay);
    }
    
    /**
     * 
     * 重新登录
     * 
     * @param msdId
     *            状态标示
     */
    private void reLogin(int msdId)
    {
        retryTimes++;
        long delay = 0;
        switch (retryTimes)
        {
            case 4:
                delay = 5000;
                break;
            case 5:
                delay = 15000;
                break;
            case 6:
                delay = 30000;
                break;
            case 7:
                delay = 60000;
                break;
            case 8:
                delay = 120000;
                break;
            default:
                if (retryTimes > 8)
                {
                    delay = 300000;
                }
                break;
        }
        
        if (delay > 0)
        {
            FusionConfig.getInstance()
                    .setUserStatus(LoginMessageType.STATUS_BREAK);
            mObserver.sendLoginMessage(LoginMessageType.STATUS_BREAK, null);
        }
        else
        {
            FusionConfig.getInstance()
                    .setUserStatus(LoginMessageType.STATUS_LOGINING);
        }
        aasHandler.sendEmptyMessageDelayed(msdId, delay);
    }
    
    /**
     * 
     * 检查用户信息
     */
    private void checkAccountInfo()
    {
        if (null == mAccount)
        {
            SharedPreferences sp = mObserver.getContext()
                    .getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                            Context.MODE_PRIVATE);
            //            if (null != userSysID)
            //            {
            //                AccountModel account = AccountDbAdapter.getInstance(mObserver.getContext())
            //                        .queryByUserSysId(userSysID);
            //                if (null != account)
            //                {
            //                    mAccount = account.getLoginAccount();
            //                    mPassword = DecodeUtil.decrypt(mAccount,
            //                            account.getPassword());
            //                }
            //            }
            mAccount = sp.getString(Common.KEY_USER_PASSPORT, "");
            mPassword = DecodeUtil.decrypt(mAccount,
                    sp.getString(Common.KEY_USER_PASSWORD, ""));
        }
        
        if (null == mVersionName)
        {
            mVersionName = FusionConfig.getInstance().getClientVersion();
            if (null == mVersionName)
            {
                try
                {
                    mVersionName = mObserver.getContext()
                            .getPackageManager()
                            .getPackageInfo(mObserver.getContext()
                                    .getPackageName(),
                                    0).versionName;
                    FusionConfig.getInstance().setClientVersion(mVersionName);
                }
                catch (NameNotFoundException e)
                {
                    Logger.e(TAG, "mVersionName error", e);
                }
            }
        }
    }
    
    private boolean hasLogined()
    {
        return mObserver.getContext()
                .getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE)
                .getBoolean(Common.KEY_ISLOGIN, false);
    };
    
    private void setLogined(boolean logined)
    {
        SharedPreferences preferences = mObserver.getContext()
                .getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
        if (preferences.getBoolean(Common.KEY_ISLOGIN, false) != logined)
        {
            Editor editor = mObserver.getContext()
                    .getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                            Context.MODE_PRIVATE)
                    .edit();
            editor.putBoolean(Common.KEY_ISLOGIN, logined);
            rePutShare(preferences, editor, Common.KEY_USER_SYSID);
            rePutShare(preferences, editor, Common.KEY_USER_ID);
            rePutShare(preferences, editor, Common.KEY_USER_PASSPORT);
            rePutShare(preferences, editor, Common.KEY_USER_PASSWORD);
            editor.commit();
        }
    }
    
    private void rePutShare(SharedPreferences preferences, Editor editor,
            String key)
    {
        if (preferences.contains(key))
        {
            editor.putString(key, preferences.getString(key, null));
        }
    }
    
    /**
     * 
     * 刷新TOKEN
     * 
     * @param userSysID
     *            系统id
     * @param token
     *            TOKEN
     * @see com.huawei.basic.android.im.component.service.core.ILogin#refreshToken(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void refreshToken(String userSysID, String token)
    {
        
    }
    
    /**
     * 
     * 停止刷新TOKEN
     * 
     * @see com.huawei.basic.android.im.component.service.core.ILogin#stopRefreshToken()
     */
    @Override
    public void stopRefreshToken()
    {
        
    }
    
    /**
     * 发送网络消息
     * 
     * @param status
     *            网络状态
     * @see com.huawei.basic.android.im.component.service.core.ILogin#sendNoNetMessage()
     */
    @Override
    public void sendNetMessage(int status)
    {
        if (hasLogined())
        {
            AASResult aasResult = mAASResult;
            switch (status)
            {
                case LoginMessageType.NET_STATUS_DISABLE:
                case LoginMessageType.NET_STATUS_WAP:
                    mObserver.sendLoginMessage(status, null);
                    if (null != aasResult)
                    {
                        deRegisterXmpp(aasResult.getUserID());
                    }
                    showNotification(mObserver.getContext()
                            .getString(LoginMessageType.NET_STATUS_DISABLE == status ? R.string.check_network
                                    : R.string.check_wap_apn));
                    break;
                case LoginMessageType.NET_STATUS_ENABLE:
                    login(1000);
                    NotificationEntityManager.getInstance()
                            .cancelNotification(mNotification);
                    break;
            }
        }
        //        if (LoginMessageType.NET_STATUS_DISABLE != stauts)
        //        {
        //            if (!aasHandler.hasMessages(AASHandler.REFRESH_TOKEN)
        //                    && hasLogined())
        //            {
        //                aasHandler.sendEmptyMessage(AASHandler.REFRESH_TOKEN);
        //            }
        //            if (LoginMessageType.NET_STATUS_ENABLE == stauts)
        //            {
        //                aasHandler.sendEmptyMessage(AASHandler.LOGIN_XMPP);
        //            }
        //            else
        //            {
        //                mObserver.sendLoginMessage(stauts, null);
        //            }
        //        }
        //        else
        //        {
        //            AASResult aasResult = mAASResult;
        //            if (null != aasResult)
        //            {
        //                mObserver.deRegisterXmpp(aasResult.getUserID());
        //            }
        //            mObserver.sendLoginMessage(stauts, null);
        //        }
    }
    
    /**
     * 
     * Service启动通知
     * 
     *
     */
    @Override
    public void serviceStart()
    {
        if (hasLogined()
                && LoginMessageType.STATUS_OFFLINE == FusionConfig.getInstance()
                        .getUserStatus()
                && LoginMessageType.NET_STATUS_DISABLE != ConnectionChangedReceiver.checkNet(mObserver.getContext()))
        {
            checkAccountInfo();
            aasLogin(null);
        }
    }
    
    /**
     * 
     * Service独立运行时需要处理的事件
     * 
     * @param messageType 消息类型
     * @param result 数据
     */
    @Override
    public void serviceProcess(int messageType, final String result)
    {
        AASResult aasResult = mAASResult;
        switch (messageType)
        {
            case LoginMessageType.LOGIN_SUCCESS:
                setLogined(true);
                if (null != aasResult)
                {
                    PresenceData.PublishCmdData cmdData = new PresenceData.PublishCmdData();
                    cmdData.setFrom(UriUtil.buildXmppJid(aasResult.getUserID()));
                    cmdData.setPriority("0");
                    
                    //设置支持语音，视频能力
                    PresenceCommonClass.DeviceData device = new PresenceCommonClass.DeviceData();
                    device.setAudio("no");
                    device.setVideo("no");
                    
                    //
                    device.setType(PresenceCommonClass.DeviceData.Type.ANDROID.getValue());
                    cmdData.setDevice(device);
                    
                    String cmdDataString = cmdData.makeCmdData();
                    // 执行命令
                    String publishResult = mObserver.executeCommand(BaseParams.PresenceParams.FAST_COM_PRESENCE_ID,
                            BaseParams.PresenceParams.FAST_PRESENCE_CMD_PUBLISH,
                            cmdDataString);
                    Logger.i(TAG, "publish online cmd xml:" + publishResult);
                }
                break;
            case LoginMessageType.LOGIN_ERROR:
                setLogined(false);
                if (null != aasResult)
                {
                    //从数据库查找账号信息
                    AccountModel model = AccountDbAdapter.getInstance(mObserver.getContext())
                            .queryByUserSysId(aasResult.getUserSysId());
                    //密码置空
                    model.setPassword(null);
                    //更新数据库
                    AccountDbAdapter.getInstance(mObserver.getContext())
                            .updateByLoginAccount(model);
                    logout(aasResult.getUserID(),
                            aasResult.getUserSysId(),
                            aasResult.getToken(),
                            aasResult.getLoginid());
                }
                mObserver.getHandler().post(new Runnable()
                {
                    
                    @Override
                    public void run()
                    {
                        Toast.makeText(mObserver.getContext(),
                                FusionErrorInfo.getLoginErrorInfo(mObserver.getContext(),
                                        result),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                break;
        }
    }
    
    private void showNotification(String content)
    {
        //        if (!StringUtil.equals(((ActivityManager) mObserver.getContext()
        //                .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1)
        //                .get(0).baseActivity.getPackageName(), mObserver.getContext()
        //                .getPackageName()))
        //        {
        //        
        //            NotificationEntityManager.getInstance()
        //                    .cancelNotification(mNotification);
        //            mNotification = new TextNotificationEntity(
        //                    R.drawable.sys_notify_logo, content, null, content);
        //            NotificationEntityManager.getInstance()
        //                    .showNewNotification(mNotification);
        //            int ringerMode = ((AudioManager) mObserver.getContext()
        //                    .getSystemService(Context.AUDIO_SERVICE)).getRingerMode();
        //            if (AudioManager.RINGER_MODE_NORMAL == ringerMode
        //                    || AudioManager.RINGER_MODE_VIBRATE == ringerMode)
        //            {
        //                if (AudioManager.RINGER_MODE_NORMAL == ringerMode
        //                        && mObserver.isSoundOpen())
        //                {
        //                    Ringtone ringtone = RingtoneManager.getRingtone(mObserver.getContext(),
        //                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        //                    if (null != ringtone)
        //                    {
        //                        ringtone.play();
        //                    }
        //                }
        //                ((Vibrator) mObserver.getContext()
        //                        .getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
        //            }
        //        
        //        }
    }
}
