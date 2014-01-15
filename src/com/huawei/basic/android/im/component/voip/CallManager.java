/*
 * 文件名: CallManager.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 12, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

import android.content.Context;
import android.util.Log;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.fast.voip.FastVoIP;
import com.huawei.fast.voip.FastVoIPConfig;
import com.huawei.fast.voip.bean.AlertingNotifyBean;
import com.huawei.fast.voip.bean.TalkingNotifyBean;

/**
 * 通话管理类
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 12, 2012] 
 */
public class CallManager
{
    /**
     * TAG
     */
    private static final String TAG = "CallManager";
    
    /**
     * 最大通话的线路数
     */
    private static final int MAX_CALL_AMOUNT = 1;

    /**
     * 是否接收订阅通知标志,需要服务器支持，false不接收订阅，true接收订阅，默认false
     * 订阅：当前VOIP账号在他处登录时,可以通过onRegState接收到被KICK的通知,
     */
    private static final boolean SUBSCRIBE = false;
    
    /**
     * 单例模式静态成员对象
     */
    private static CallManager instance = new CallManager();
    
    /**
     * 铃声播放器
     */
    private RingtonePlayer mRingtonePlayer;
    
    /**
     * 扬声器
     */
    private Speaker mSpeaker;
    
    /**
     * 当前会话对象
     */
    private CallSession mCurrentCallSession;
    
    /**
     * 等待会话的对象
     */
    private CallSession mWaitCallSession;
    
    /**
     * Fast参数配置对象
     */
    private FastVoIPConfig mFastVoIPConfig;
    
    /**
     * Fast组件操作类
     */
    private FastVoIP mFastVoIP;
    
    /**
     * SIP代理服务器域名
     */
    private String mProxyDomain;
    
    /**
     * SIP代理服务器IP
     */
    private String mProxyIp;
    
    /**
     * SIP代理服务器端口
     */
    private int mProxyPort;
    
    /**
     * voip账号
     */
    private String mAor;
    
    /**
     * 当前用户名称
     */
    private String mDisplayName;
    
    /**
     * 注册ID
     */
    private int mRegId = -1;
    
    /**
     * Fast组件回调对象
     */
    private CallNotify mCallNotify = new CallNotify();
    
    /**
     * 私有的默认构造方法
     */
    private CallManager()
    {
    }
    
    /**
     * 获取单例的唯一实例
     * @return
     *      唯一实例
     */
    public static CallManager getInstance()
    {
        return instance;
    }
    
    /**
     * 初始化方法
     * @param proxyDomain
     *      SIP代理服务器域名
     * @param proxyIp
     *      SIP代理服务器IP
     * @param proxyPort
     *      SIP代理服务器端口
     * @param context
     *      Context对象
     * @param ringFilePath
     *      铃音文件全路径   
     * @return
     *      是否初始化成功
     */
    public synchronized boolean init(String proxyDomain, String proxyIp,
            int proxyPort, Context context, String ringFilePath)
    {
        Logger.d(TAG, "CallManager.init");
        this.mProxyDomain = proxyDomain;
        this.mProxyIp = proxyIp;
        this.mProxyPort = proxyPort;
        this.mFastVoIP = FastVoIP.getInstance();
        //设置回铃音、呼叫等待 铃音文件
        if (null != ringFilePath)
        {
            //设置回铃音文件路径
            FastVoIPConfig.getInstance().setSipRingtone(ringFilePath);
            //设置呼叫等待铃音文件路径
            FastVoIPConfig.getInstance().setSipQueuedtone(ringFilePath);
        }
        this.mRingtonePlayer = new RingtonePlayer(context);
        this.mSpeaker = new Speaker(context);
        boolean isInit = this.mFastVoIP.init(context.getApplicationContext(),
                mCallNotify,
                true);
        return isInit;
    }
    
    /**
     * 设置被动通话操作监听器
     * @param callBackEventListener
     *      通话响应事件监听器
     */
    public synchronized void setCallBackEventListener(
            final CallBackEventListener callBackEventListener)
    {
        Logger.d(TAG, "CallManager.setCallBackEventListener");
        final CallBackEventListener listener = new CallBackEventListener()
        {
            @Override
            public void onClosed(int callId, String reason, int repCode)
            {
                Logger.d(TAG,
                        "CallManager.setCallBackEventListener$onClosed callId="
                                + callId);
                mSpeaker.close();
                if (null != mRingtonePlayer)
                {
                    mRingtonePlayer.stop();
                    mRingtonePlayer.startOnceVibrator();
                }
                CallSession callSession = getCallSessionByCallId(callId);
                if (CallManager.this.mCurrentCallSession == callSession)
                {
                    CallManager.this.mCurrentCallSession = null;
                }
                if (CallManager.this.mWaitCallSession == callSession)
                {
                    CallManager.this.mWaitCallSession = null;
                }
                if (null != callSession)
                {
                    callSession.close();
                }
                mCallNotify.removeListenersByCallId(callId);
                if (null != callBackEventListener)
                {
                    callBackEventListener.onClosed(callId, reason, repCode);
                }
            }
            
            @Override
            public void onAlerting(AlertingNotifyBean alertingNotifyBean)
            {
                int callId = alertingNotifyBean.getId();
                Logger.d(TAG,
                        "CallManager.setCallBackEventListener$onAlerting callId="
                                + callId);
                if (null != mRingtonePlayer)
                {
                    mRingtonePlayer.start();
                }
                String type = alertingNotifyBean.getSession().getContent();
                CallSession callSession = new CallSession(type, false,
                        CallManager.this.mFastVoIP);
                callSession.setCallId(callId);
                CallInfo caller = new CallInfo();
                caller.setDisplayName(alertingNotifyBean.getRemoteDisplayname());
                caller.setPhoneNum(alertingNotifyBean.getRemoteNumber());
                caller.setUri(alertingNotifyBean.getRemoteUri());
                callSession.setCaller(caller);
                CallInfo callee = new CallInfo();
                callee.setDisplayName(CallManager.this.mDisplayName);
                callee.setUri(CallManager.this.mAor);
                callSession.addCallee(callee);
                if (null == CallManager.this.mCurrentCallSession)
                {
                    CallManager.this.mCurrentCallSession = callSession;
                }
                else if (null == CallManager.this.mWaitCallSession)
                {
                    CallManager.this.mWaitCallSession = callSession;
                }
                else
                {
                    return;
                }
                if (null != callBackEventListener)
                {
                    callBackEventListener.onAlerting(alertingNotifyBean);
                }
            }
            
            @Override
            public void onQueue(AlertingNotifyBean alertingNotifyBean)
            {
                Logger.d(TAG,
                        "CallManager.setCallBackEventListener$onQueue callId="
                                + alertingNotifyBean.getId());
                if (null != callBackEventListener)
                {
                    callBackEventListener.onQueue(alertingNotifyBean);
                }
            }
            
            @Override
            public void onHeld(int callId)
            {
                Logger.d(TAG,
                        "CallManager.setCallBackEventListener$onHeld callId="
                                + callId);
                if (null != callBackEventListener)
                {
                    callBackEventListener.onHeld(callId);
                }
            }
            
            @Override
            public void onRegState(int regId, String regState, String event,
                    int retryAfter)
            {
                Logger.d(TAG,
                        "CallManager.setCallBackEventListener$onRegState regId="
                                + regId);
                onRegState(regId, regState, event);
                if (retryAfter == 0 && "Terminate".equals(regState))
                {
                    CallManager.this.mRegId = -1;
                    CallManager.this.mFastVoIPConfig = null;
                    CallManager.this.mCurrentCallSession = null;
                    CallManager.this.mWaitCallSession = null;
                    CallManager.this.mCallNotify.removeAllListener();
                }
            }
            
            @Override
            protected void onRegState(int regId, String regState, String event)
            {
                if (null != callBackEventListener)
                {
                    callBackEventListener.onRegState(regId, regState, event);
                }
                
            }
        };
        if (null != mCallNotify)
        {
            mCallNotify.setCallBackEventListener(listener);
        }
    }
    
    /**
     * 登录SIP服务器
     * @param displayName
     *      显示名称
     * @param aor
     *      账号
     * @param password
     *      密码
     * @param callLoginListener
     *      登录监听器
     * @return
     *      注册ID
     */
    public synchronized int login(final String displayName, final String aor,
            final String password, final ICallLoginListener callLoginListener)
    {
        Logger.d(TAG, "CallManager.login");
        this.mDisplayName = displayName;
        this.mAor = aor;
        final FastVoIPConfig fastVoIPConfig = FastVoIPConfig.getInstance();
        fastVoIPConfig.setAor(aor);
        fastVoIPConfig.setPassword(password);
        fastVoIPConfig.setProxyDomain(mProxyDomain);
        fastVoIPConfig.setProxyIp(mProxyIp);
        fastVoIPConfig.setProxyPort(mProxyPort);
        fastVoIPConfig.setMaxCallNum(MAX_CALL_AMOUNT);
        
        //设置是否启动订阅注册
        fastVoIPConfig.setSubscribe(SUBSCRIBE);
        this.mCallNotify.setCallLoginListener(new ICallLoginListener()
        {
            @Override
            public void onLoginSuccessful(int regId, String uri)
            {
                Logger.d(TAG, "CallManager.login$onLoginSuccessful regId="
                        + regId);
                CallManager.this.mRegId = regId;
                CallManager.this.mFastVoIPConfig = fastVoIPConfig;
                if (null != callLoginListener)
                {
                    callLoginListener.onLoginSuccessful(regId, uri);
                }
            }
            
            @Override
            public void onLoginFailure(int regId, int errorCode)
            {
                Logger.d(TAG, "CallManager.login$onLoginFailure regId=" + regId);
                if (null != callLoginListener)
                {
                    callLoginListener.onLoginFailure(regId, errorCode);
                }
            }
        });
        mRegId = mFastVoIP.login();
        return mRegId;
    }
    
    /**
     * 登出SIP服务器
     * @param callLogoutListener
     *      登出监听器
     * @return
     *      是否登出操作成功
     */
    public synchronized boolean logout(
            final ICallLogoutListener callLogoutListener)
    {
        Logger.d(TAG, "logout");
        this.mCallNotify.setCallLogoutListener(new ICallLogoutListener()
        {
            @Override
            public void onLogoutSuccessful(int regId, String remoteUri)
            {
                Logger.d(TAG, "CallManager.logout$onLogoutSuccessful regId="
                        + regId);
                if (null != callLogoutListener)
                {
                    callLogoutListener.onLogoutSuccessful(regId, remoteUri);
                }
            }
            
            @Override
            public void onLogoutFailure(int regId, int errorcode)
            {
                Logger.d(TAG, "CallManager.logout$onLogoutFailure regId="
                        + regId);
                if (null != callLogoutListener)
                {
                    callLogoutListener.onLogoutFailure(regId, errorcode);
                }
            }
        });
        boolean isLogout = mFastVoIP.logout(mRegId);
        CallManager.this.mRegId = -1;
        CallManager.this.mFastVoIPConfig = null;
        CallManager.this.mCurrentCallSession = null;
        CallManager.this.mWaitCallSession = null;
        CallManager.this.mCallNotify.removeAllListener();
        return isLogout;
    }
    
    /**
     * 卸载Fast模块
     */
    public synchronized void unInit()
    {
        Logger.d(TAG, "CallManager.unInit");
        if (null != mFastVoIP)
        {
            mFastVoIP.unInit();
            mCallNotify.removeAllListener();
            mFastVoIP = null;
            mRingtonePlayer = null;
            mSpeaker = null;
        }
    }
    
    /**
     * 呼叫通话
     * @param displayName
     *      显示名称
     * @param phoneNum
     *      对方电话号码
     * @param type
     *      通话类型 音频：FastVoIPConstant.AUDIOTYPE,视频：FastVoIPConstant.VIDEOTYPE
     * @param callAdapterListener
     *      呼叫通话监听器
     * @return INT 通话操作
     */
    public synchronized int call(String displayName, final String phoneNum,
            final String type, final CallAdapterListener callAdapterListener)
    {
        Logger.d(TAG, "CallManager.call");
        if (null == mFastVoIPConfig || null == mFastVoIP)
        {
            return -1;
        }
        if (null != mCurrentCallSession && null != mWaitCallSession)
        {
            return -1;
        }
        if (null != mCurrentCallSession)
        {
            
            //            HoldAdapterListener holdAdapterListener = new HoldAdapterListener()
            //            {
            //                @Override
            //                public void onHolding(int callId)
            //                {
            //                    Logger.d(TAG, "CallManager.call$onHolding callId=" + callId);
            //                    mWaitCallSession = mCurrentCallSession;
            //                    mCurrentCallSession = new CallSession(type, true,
            //                            mFastVoIP, mCallNotify);
            //                    mCurrentCallSession.getCall().call(phoneNum);
            //                }
            //                
            //                @Override
            //                public void onHoldFailure(int callId)
            //                {
            //                    Logger.d(TAG, "CallManager.call$onHoldFailure callId="
            //                            + callId);
            //                }
            //                
            //                @Override
            //                protected void onClosed(int callId, String reason, int repCode)
            //                {
            //                    Logger.d(TAG, "CallManager.call$onClosed callId=" + callId);
            //                    
            //                }
            //            };
            //            holdAdapterListener.setCallSession(mCurrentCallSession);
            //            mCallNotify.addHoldAdapterListener(holdAdapterListener);
            //            mCurrentCallSession.getCall().hold();
            return -1;
        }
        else
        {
            mCurrentCallSession = new CallSession(type, true, mFastVoIP);
            
            CallInfo caller = new CallInfo();
            caller.setDisplayName(this.mDisplayName);
            caller.setUri(this.mAor);
            mCurrentCallSession.setCaller(caller);
            
            CallInfo callee = new CallInfo();
            callee.setDisplayName(displayName);
            if (phoneNum.matches("\\+?\\d*"))
            {
                callee.setPhoneNum(phoneNum);
            }
            else
            {
                callee.setUri(phoneNum);
            }
            mCurrentCallSession.addCallee(callee);
            CallAdapterListener listener = new CallAdapterListener()
            {
                @Override
                public void onQueued(int callId, String remoteUri)
                {
                    Logger.d(TAG, "CallManager.call$onQueued callId=" + callId);
                    if (null != callAdapterListener)
                    {
                        callAdapterListener.onQueued(callId, remoteUri);
                    }
                }
                
                @Override
                public void onRinging(int callId, String remoteUri)
                {
                    Logger.d(TAG, "CallManager.call$onRinging callId=" + callId);
                    
                    if (null != callAdapterListener)
                    {
                        callAdapterListener.onRinging(callId, remoteUri);
                    }
                }
                
                @Override
                public void onTalking(TalkingNotifyBean talkingNotifyBean)
                {
                    Logger.d(TAG, "CallManager.call$onTalking callId="
                            + talkingNotifyBean.getId());
                    if (null != mRingtonePlayer)
                    {
                        mRingtonePlayer.startOnceVibrator();
                    }
                    mCurrentCallSession.setStartTime();
                    if (null != callAdapterListener)
                    {
                        callAdapterListener.onTalking(talkingNotifyBean);
                    }
                }
                
                @Override
                public void onClosed(int callId, String reason, int repCode)
                {
                    Logger.d(TAG, "CallManager.call$onClosed callId=" + callId);
                    mSpeaker.close();
                    if (null != mRingtonePlayer)
                    {
                        mRingtonePlayer.startOnceVibrator();
                    }
                    if (null != CallManager.this.mCurrentCallSession)
                    {
                        CallManager.this.mCurrentCallSession.close();
                        CallManager.this.mCurrentCallSession = null;
                    }
                    mCallNotify.removeListenersByCallId(callId);
                    if (null != callAdapterListener)
                    {
                        callAdapterListener.onClosed(callId, reason, repCode);
                    }
                }
            };
            int callId = mCurrentCallSession.getCall().call(phoneNum);
            Log.d(TAG, "call:call--->callId=" + callId);
            mCallNotify.addCallAdapterListener(callId, listener);
            return callId;
        }
    }
    
    /**
     * 接听通话
     * @param answerAdapterListener
     *      接听电话监听器
     * @return 
     *      是否应答成功
     */
    public synchronized boolean answer(
            final AnswerAdapterListener answerAdapterListener)
    {
        Logger.d(TAG, "CallManager.answer");
        mSpeaker.close();
        if (null != mRingtonePlayer)
        {
            mRingtonePlayer.stop();
        }
        if (null == mFastVoIPConfig || null == mFastVoIP)
        {
            return false;
        }
        final CallSession callSession = null != mWaitCallSession ? mWaitCallSession
                : mCurrentCallSession;
        ICall callee = null != callSession ? callSession.getCall() : null;
        if (null == callee)
        {
            return false;
        }
        AnswerAdapterListener listener = new AnswerAdapterListener()
        {
            @Override
            public void onTalking(TalkingNotifyBean talkingNotifyBean)
            {
                Logger.d(TAG, "CallManager.answer$onTalking");
                callSession.setStartTime();
                if (null != answerAdapterListener)
                {
                    answerAdapterListener.onTalking(talkingNotifyBean);
                }
            }
            
            @Override
            public void onClosed(int callId, String reason, int repCode)
            {
                Logger.d(TAG, "CallManager.answer$onClosed");
                if (null != mRingtonePlayer)
                {
                    mRingtonePlayer.startOnceVibrator();
                }
                if (CallManager.this.mCurrentCallSession == callSession)
                {
                    CallManager.this.mCurrentCallSession = null;
                }
                if (CallManager.this.mWaitCallSession == callSession)
                {
                    CallManager.this.mWaitCallSession = null;
                }
                if (null != callSession)
                {
                    callSession.close();
                }
                mCallNotify.removeListenersByCallId(callId);
                if (null != answerAdapterListener)
                {
                    answerAdapterListener.onClosed(callId, reason, repCode);
                }
            }
        };
        mCallNotify.addAnswerAdapterListener(callSession.getCallId(), listener);
        Log.d(TAG, "call:call--->callId=" + callSession.getCallId());
        return callee.answer();
    }
    
    /**
     * 二次拨号输入
     * @param callId
     *      通话id
     * @param code
     *      输入号码
     * @return
     *      是否拨号操作成功
     */
    public synchronized boolean redial(int callId, String code)
    {
        return this.mFastVoIP.redial(callId, code);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param callId
     *      通话id
     * @param closeAdapterListener
     *      挂断电话监听器
     * @return 
     *      是否挂断操作成功
     */
    public synchronized boolean close(int callId,
            final CloseAdapterListener closeAdapterListener)
    {
        Logger.d(TAG, "CallManager.close_1 callId=" + callId);
        mSpeaker.close();
        if (null != mRingtonePlayer)
        {
            mRingtonePlayer.stop();
        }
        final CallSession callSession = getCallSessionByCallId(callId);
        boolean isClosed = false;
        if (null != callSession)
        {
            Logger.d(TAG, "CallManager.close_2 callId=" + callId);
            mCallNotify.removeListenersByCallId(callId);
            isClosed = callSession.getCall().close();
            //        if (!isClosed)
            //        {
            //            return false;
            //        }
            if (CallManager.this.mCurrentCallSession == callSession)
            {
                CallManager.this.mCurrentCallSession = null;
            }
            if (CallManager.this.mWaitCallSession == callSession)
            {
                CallManager.this.mWaitCallSession = null;
            }
            if (null != callSession)
            {
                callSession.close();
            }
        }
        if (null != closeAdapterListener)
        {
            closeAdapterListener.onClosed(callId, null, 0);
        }
        return isClosed;
    }
    
    /**
     * 接听通话
     * @param callId
     *      通话id
     * @param holdAdapterListener
     *      保持通话监听器
     * @return 
     *      是否保持操作成功
     */
    public synchronized boolean hold(int callId,
            final HoldAdapterListener holdAdapterListener)
    {
        Logger.d(TAG, "CallManager.hold callId=" + callId);
        final CallSession callSession = getCallSessionByCallId(callId);
        if (callSession == null)
        {
            return false;
        }
        HoldAdapterListener listener = new HoldAdapterListener()
        {
            @Override
            public void onHolding(int callId)
            {
                Logger.d(TAG, "CallManager.hold$onHolding callId=" + callId);
                if (null != holdAdapterListener)
                {
                    holdAdapterListener.onHolding(callId);
                }
            }
            
            @Override
            public void onHoldFailure(int callId)
            {
                Logger.d(TAG, "CallManager.hold$onHoldFailure callId=" + callId);
                if (null != holdAdapterListener)
                {
                    holdAdapterListener.onHoldFailure(callId);
                }
                
            }
            
            @Override
            public void onClosed(int callId, String reason, int repCode)
            {
                Logger.d(TAG, "CallManager.hold$onClosed callId=" + callId);
                mSpeaker.close();
                if (null != mRingtonePlayer)
                {
                    mRingtonePlayer.startOnceVibrator();
                }
                if (CallManager.this.mCurrentCallSession == callSession)
                {
                    CallManager.this.mCurrentCallSession = null;
                }
                if (CallManager.this.mWaitCallSession == callSession)
                {
                    CallManager.this.mWaitCallSession = null;
                }
                if (null != callSession)
                {
                    callSession.close();
                }
                mCallNotify.removeListenersByCallId(callId);
                if (null != holdAdapterListener)
                {
                    holdAdapterListener.onClosed(callId, reason, repCode);
                }
            }
        };
        mCallNotify.removeListenersByCallId(callId);
        mCallNotify.addHoldAdapterListener(callId, listener);
        return callSession.getCall().hold();
    }
    
    /**
     * 恢复通话
     * @param callId
     *      通话id
     * @param retrieveAdapterListener
     *      恢复通话监听器
     * @return
     *      是否恢复操作成功
     */
    public synchronized boolean retrieve(int callId,
            final RetrieveAdapterListener retrieveAdapterListener)
    {
        Logger.d(TAG, "CallManager.retrieve callId=" + callId);
        final CallSession callSession = getCallSessionByCallId(callId);
        if (callSession == null)
        {
            return false;
        }
        RetrieveAdapterListener listener = new RetrieveAdapterListener()
        {
            @Override
            public void onRetrieved(TalkingNotifyBean talkingNotifyBean)
            {
                Logger.d(TAG, "retrieve.onRetrieved callId="
                        + talkingNotifyBean.getId());
                if (null != retrieveAdapterListener)
                {
                    retrieveAdapterListener.onRetrieved(talkingNotifyBean);
                }
            }
            
            @Override
            public void onRetrieveFailure(int callId)
            {
                Logger.d(TAG, "retrieve.onRetrieveFailure callId=" + callId);
                if (null != retrieveAdapterListener)
                {
                    retrieveAdapterListener.onRetrieveFailure(callId);
                }
            }
            
            @Override
            public void onClosed(int callId, String reason, int repCode)
            {
                Logger.d(TAG, "retrieve.onClosed callId=" + callId);
                mSpeaker.close();
                if (null != mRingtonePlayer)
                {
                    mRingtonePlayer.startOnceVibrator();
                }
                if (CallManager.this.mCurrentCallSession == callSession)
                {
                    CallManager.this.mCurrentCallSession = null;
                }
                if (CallManager.this.mWaitCallSession == callSession)
                {
                    CallManager.this.mWaitCallSession = null;
                }
                if (null != callSession)
                {
                    callSession.close();
                }
                mCallNotify.removeListenersByCallId(callId);
                if (null != retrieveAdapterListener)
                {
                    retrieveAdapterListener.onClosed(callId, reason, repCode);
                }
            }
        };
        mCallNotify.removeListenersByCallId(callId);
        mCallNotify.addRetrieveAdapterListener(callId, listener);
        return callSession.getCall().retrieve();
    }
    
    /**
     * 
     * 扬声器是否打开
     * @return
     *      操作是否成功
     */
    public boolean isOpenSpeaker()
    {
        if (null == mSpeaker)
        {
            return false;
        }
        return mSpeaker.isOpen();
    }
    
    /**
     * 打开扬声器
     */
    public void openSpeaker()
    {
        if (null != mSpeaker)
        {
            mSpeaker.open();
        }
    }
    
    /**
     * 关闭扬声器
     */
    public void closeSpeaker()
    {
        if (null != mSpeaker)
        {
            mSpeaker.close();
        }
    }
    
    /**
     * 是否是静音
     * @return
     *      是否是静音
     */
    public boolean isMute()
    {
        return mFastVoIPConfig.isOption_dvi() == 0;
    }
    
    /**
     * 打开静音
     */
    public void openMute()
    {
        mFastVoIPConfig.setSessionId(mCurrentCallSession.getCallId());
        mFastVoIPConfig.setOption_dvi(0);
        mFastVoIP.setAudioCaps();
    }
    
    /**
     * 关闭静音
     */
    public void closeMute()
    {
        mFastVoIPConfig.setSessionId(mCurrentCallSession.getCallId());
        mFastVoIPConfig.setOption_dvi(1);
        mFastVoIP.setAudioCaps();
    }
    
    /**
     * 是否已经登录
     * @return
     *      是否登录成功
     */
    public boolean isLogin()
    {
        return null != mFastVoIPConfig && null != mFastVoIP;
    }
    
    /**
     * 是否已经初始化
     * @return
     *     是否初始化成功
     */
    public boolean isInit()
    {
        return mFastVoIP != null;
    }
    
    /**
     * 得到当前通话会话
     * @return
     *      通话会话
     */
    public CallSession getCurrentCallSession()
    {
        return mCurrentCallSession;
    }
    
    /**
     * 得到等待的通话会话
     * @return
     *      通话会话
     */
    public CallSession getWaitCallSession()
    {
        return mWaitCallSession;
    }
    
    /**
     * 通过callId获取响应的会话
     * @param callId
     *      通话ID
     * @return
     *      通话会话
     */
    public CallSession getCallSessionByCallId(int callId)
    {
        if (null != mCurrentCallSession
                && mCurrentCallSession.getCallId() == callId)
        {
            return mCurrentCallSession;
        }
        if (null != mWaitCallSession && mWaitCallSession.getCallId() == callId)
        {
            return mWaitCallSession;
        }
        return null;
    }
    
    /**
     * 得到注册ID
     * @return
     *      注册ID
     */
    public int getRegId()
    {
        return mRegId;
    }
    
}
