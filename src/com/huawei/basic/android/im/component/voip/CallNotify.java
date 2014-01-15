/*
 * 文件名: CallNotify.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

import java.util.Hashtable;
import java.util.Map;

import android.util.Log;

import com.huawei.fast.voip.FastVoIPNotify;
import com.huawei.fast.voip.bean.AlertingNotifyBean;
import com.huawei.fast.voip.bean.RegBean;
import com.huawei.fast.voip.bean.TalkingNotifyBean;

/**
 * Fast组建的监听回调对象
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public final class CallNotify extends FastVoIPNotify
{
    /**
     * TAG
     */
    private static final String TAG = "CallNotify";
    
    /**
     * 登录监听器
     */
    private ICallLoginListener mCallLoginListener;
    
    /**
     * 登出监听器
     */
    private ICallLogoutListener mCallLogoutListener;
    
    /**
     * 被动回调监听器
     */
    private CallBackEventListener mCallBackEventListener;
    
    /**
     * 拨打电话监听器集合
     */
    private Map<Integer, CallAdapterListener> mCallAdapterListeners = new Hashtable<Integer, CallAdapterListener>();
    
    /**
     * 保持通话监听器集合
     */
    private Map<Integer, HoldAdapterListener> mHoldAdapterListeners = new Hashtable<Integer, HoldAdapterListener>();
    
    /**
     * 接听通话监听器集合
     */
    private Map<Integer, AnswerAdapterListener> mAnswerAdapterListeners = new Hashtable<Integer, AnswerAdapterListener>();
    
    /**
     * 恢复通话监听器集合
     */
    private Map<Integer, RetrieveAdapterListener> mRetrieveAdapterListeners = new Hashtable<Integer, RetrieveAdapterListener>();
    
    /**
     * 设置被动回调监听器
     * @param callBackEventListener
     *      被动回调监听器
     */
    protected void setCallBackEventListener(
            CallBackEventListener callBackEventListener)
    {
        this.mCallBackEventListener = callBackEventListener;
    }
    
    /**
     * 获取被动回调监听器
     * @return
     *      被动回调监听器
     */
    protected ICallLoginListener getCallLoginListener()
    {
        return mCallLoginListener;
    }
    
    /**
     * 设置登录监听器
     * @param callLoginListener
     *      登录监听器
     */
    protected void setCallLoginListener(ICallLoginListener callLoginListener)
    {
        this.mCallLoginListener = callLoginListener;
    }
    
    /**
     * 获取登出监听器
     * @return
     *      登出监听器
     */
    protected ICallLogoutListener getCallLogoutListener()
    {
        return mCallLogoutListener;
    }
    
    /**
     * 设置登出监听器
     * @param callLogoutListener
     *      登出监听器
     */
    protected void setCallLogoutListener(ICallLogoutListener callLogoutListener)
    {
        this.mCallLogoutListener = callLogoutListener;
    }
    
    /**
     * 增加拨打电话监听器
     * @param callId
     *      通话id
     * @param callAdapterListener
     *      拨打电话监听器
     */
    protected void addCallAdapterListener(int callId,
            CallAdapterListener callAdapterListener)
    {
        this.mCallAdapterListeners.put(callId, callAdapterListener);
    }
    
    /**
     * 移除拨打电话监听器
     * @param callId
     *      通话id
     */
    protected void removeCallAdapterListener(int callId)
    {
        this.mCallAdapterListeners.remove(callId);
    }
    
    /**
     * 增加保持通话监听器
     * @param callId
     *      通话id
     * @param holdAdapterListener
     *      保持通话监听器
     */
    protected void addHoldAdapterListener(int callId,
            HoldAdapterListener holdAdapterListener)
    {
        this.mHoldAdapterListeners.put(callId, holdAdapterListener);
    }
    
    /**
     * 移除保持通话监听器
     * @param callId
     *      通话id
     */
    protected void removeHoldAdapterListener(int callId)
    {
        this.mHoldAdapterListeners.remove(callId);
    }
    
    /**
     * 增加恢复通话监听器
     * @param callId
     *      通话id
     * @param retrieveAdapterListener
     *      恢复通话监听器
     */
    protected void addRetrieveAdapterListener(int callId,
            RetrieveAdapterListener retrieveAdapterListener)
    {
        this.mRetrieveAdapterListeners.put(callId, retrieveAdapterListener);
    }
    
    /**
     * 移除恢复通话监听器
     * @param callId
     *      通话id
     */
    protected void removeRetrieveAdapterListener(int callId)
    {
        this.mRetrieveAdapterListeners.remove(callId);
    }
    
    /**
     * 增加接听通话监听器
     * @param callId
     *      通话id
     * @param answerAdapterListener
     *      接听通话监听器
     */
    protected void addAnswerAdapterListener(int callId,
            AnswerAdapterListener answerAdapterListener)
    {
        this.mAnswerAdapterListeners.put(callId, answerAdapterListener);
    }
    
    /**
     * 移除接听通话监听器
     * @param callId
     *      通话id
     */
    protected void removeAnswerAdapterListener(int callId)
    {
        this.mAnswerAdapterListeners.remove(callId);
    }
    
    /**
     * 登录回调
     * @param regBean
     *      解析封装的对象
     * @see com.huawei.fast.voip.FastVoIPNotify#onLogin(com.huawei.fast.voip.bean.RegBean)
     */
    @Override
    public void onLogin(RegBean regBean)
    {
        Log.d(TAG, "CallNotify.onLogin regId=" + regBean.getId());
        if (mCallLoginListener == null)
        {
            return;
        }
        int regId = regBean.getId();
        if (regBean.getResult() == 0)
        {
            mCallLoginListener.onLoginSuccessful(regId, regBean.getUri());
        }
        else
        {
            mCallLoginListener.onLoginFailure(regId, regBean.getErrorcode());
        }
        mCallLoginListener = null;
    }
    
    /**
     * 登出回调
     * @param regBean
     *      封装解析后的通知
     * @see com.huawei.fast.voip.FastVoIPNotify#onLogout(com.huawei.fast.voip.bean.RegBean)
     */
    @Override
    public void onLogout(RegBean regBean)
    {
        Log.d(TAG, "CallNotify.onLogout regId=" + regBean.getId());
        if (mCallLogoutListener == null)
        {
            return;
        }
        int regId = regBean.getId();
        if (regBean.getResult() == 0)
        {
            mCallLogoutListener.onLogoutSuccessful(regId, regBean.getUri());
        }
        else
        {
            mCallLogoutListener.onLogoutFailure(regId, regBean.getErrorcode());
        }
        mCallLogoutListener = null;
        
    }
    
    /**
     * 保持通话失败回调
     * @param callId
     *      通话id
     * @see com.huawei.fast.voip.FastVoIPNotify#onHoldFailure(int)
     */
    @Override
    public void onHoldFailure(int callId)
    {
        Log.d(TAG, "CallNotify.onHoldFailure callId=" + callId);
        HoldAdapterListener listener = mHoldAdapterListeners.get(callId);
        if (null != listener)
        {
            listener.onHoldFailure(callId);
            mHoldAdapterListeners.remove(callId);
        }
    }
    
    /**
     * 保持通话中回调
     * @param callId
     *      通话id
     * @see com.huawei.fast.voip.FastVoIPNotify#onHolding(int)
     */
    @Override
    public void onHolding(int callId)
    {
        Log.d(TAG, "CallNotify.onHolding callId=" + callId);
        HoldAdapterListener listener = mHoldAdapterListeners.get(callId);
        if (null != listener)
        {
            listener.onHolding(callId);
            mHoldAdapterListeners.remove(callId);
        }
    }
    
    /**
     * 呼叫被等待通知回调
     * @param callId
     *      通话id
     * @param remoteUri
     *      对方的号码
     * @param withSdp
     *      标识收到的临时响应中是否带sdp. 1－带sdp; 0－不带sdp
     * @see com.huawei.fast.voip.FastVoIPNotify#onQueued(int, java.lang.String, int)
     */
    @Override
    public void onQueued(int callId, String remoteUri, int withSdp)
    {
        Log.d(TAG, "CallNotify.onQueued callId=" + callId);
        CallAdapterListener listener = mCallAdapterListeners.get(callId);
        if (null != listener)
        {
            listener.onQueued(callId, remoteUri);
        }
    }
    
    /**
     * 恢复通话失败回调
     * @param callId
     *      通话id
     * @see com.huawei.fast.voip.FastVoIPNotify#onRetrieveFailure(int)
     */
    @Override
    public void onRetrieveFailure(int callId)
    {
        Log.d(TAG, "CallNotify.onRetrieveFailure callId=" + callId);
        RetrieveAdapterListener listener = mRetrieveAdapterListeners.get(callId);
        if (null != listener)
        {
            listener.onRetrieveFailure(callId);
            mRetrieveAdapterListeners.remove(callId);
        }
    }
    
    /**
     * 呼叫恢复回调
     * @param talkingNotifyBean
     *      封装解析后的通知
     * @see com.huawei.fast.voip.FastVoIPNotify#onRetrieved(com.huawei.fast.voip.bean.TalkingNotifyBean)
     */
    @Override
    public void onRetrieved(TalkingNotifyBean talkingNotifyBean)
    {
        Log.d(TAG, "CallNotify.onRetrieved callId=" + talkingNotifyBean.getId());
        int callId = talkingNotifyBean.getId();
        RetrieveAdapterListener listener = mRetrieveAdapterListeners.get(callId);
        if (null != listener)
        {
            listener.onRetrieved(talkingNotifyBean);
            mRetrieveAdapterListeners.remove(callId);
        }
    }
    
    /**
     * 主叫振铃通知回调
     * @param callId
     *      通话id
     * @param remoteUri
     *      对方号码
     * @param withSdp
     *      标识收到的临时响应中是否带sdp. 0－带sdp; 1－不带sdp
     * @see com.huawei.fast.voip.FastVoIPNotify#onRinging(int, java.lang.String, int)
     */
    @Override
    public void onRinging(int callId, String remoteUri, int withSdp)
    {
        Log.d(TAG, "CallNotify.onRinging callId=" + callId);
        CallAdapterListener listener = mCallAdapterListeners.get(callId);
        if (null != listener)
        {
            listener.onRinging(callId, remoteUri);
        }
    }
    
    /**
     * 开始通话通知回调
     * @param talkingNotifyBean
     *      封装解析后的通知。
     * @see com.huawei.fast.voip.FastVoIPNotify#onTalking(com.huawei.fast.voip.bean.TalkingNotifyBean)
     */
    @Override
    public void onTalking(TalkingNotifyBean talkingNotifyBean)
    {
        Log.d(TAG, "CallNotify.onTalking callId=" + talkingNotifyBean.getId());
        int callId = talkingNotifyBean.getId();
        CallAdapterListener callAdapterListener = mCallAdapterListeners.get(callId);
        if (null != callAdapterListener)
        {
            callAdapterListener.onTalking(talkingNotifyBean);
  //          mCallAdapterListeners.remove(callId);
        }
        
        AnswerAdapterListener answerAdapterListener = mAnswerAdapterListeners.get(callId);
        if (null != answerAdapterListener)
        {
            answerAdapterListener.onTalking(talkingNotifyBean);
//            mAnswerAdapterListeners.remove(callId);
        }
    }
    
    /**
     * 结束通话通知回调
     * @param callId
     *      结束通话通知
     * @param reason
     *      通话结束的原因
     * @param repCode
     *      invitey请求的非2XX响应码
     * @see com.huawei.fast.voip.FastVoIPNotify#onClosed(int, java.lang.String, int)
     */
    @Override
    public void onClosed(int callId, String reason, int repCode)
    {
        Log.d(TAG, "CallNotify.onClosed callId=" + callId);
        boolean isCallBack = true;
        CallAdapterListener callAdapterListener = mCallAdapterListeners.get(callId);
        if (null != callAdapterListener)
        {
            callAdapterListener.onClosed(callId, reason, repCode);
            mCallAdapterListeners.remove(callId);
            isCallBack = false;
        }
        
        AnswerAdapterListener answerAdapterListener = mAnswerAdapterListeners.get(callId);
        if (null != answerAdapterListener)
        {
            answerAdapterListener.onClosed(callId, reason, repCode);
            mAnswerAdapterListeners.remove(callId);
            isCallBack = false;
        }
        
        RetrieveAdapterListener retrieveAdapterListener = mRetrieveAdapterListeners.get(callId);
        if (null != retrieveAdapterListener)
        {
            retrieveAdapterListener.onClosed(callId, reason, repCode);
            mRetrieveAdapterListeners.remove(callId);
            isCallBack = false;
        }
        
        HoldAdapterListener holdAdapterListener = mHoldAdapterListeners.get(callId);
        if (null != holdAdapterListener)
        {
            holdAdapterListener.onClosed(callId, reason, repCode);
            mHoldAdapterListeners.remove(callId);
            isCallBack = false;
        }
        
        if (isCallBack && null != mCallBackEventListener)
        {
            mCallBackEventListener.onClosed(callId, reason, repCode);
        }
    }
    
    /**
     * 被叫振铃回调
     * @param alertingNotifyBean
     *      封装解析后的通知
     * @see com.huawei.fast.voip.FastVoIPNotify#onAlerting(com.huawei.fast.voip.bean.AlertingNotifyBean)
     */
    @Override
    public void onAlerting(AlertingNotifyBean alertingNotifyBean)
    {
        Log.d(TAG, "CallNotify.onAlerting callId=" + alertingNotifyBean.getId());
        if (null == mCallBackEventListener)
        {
            return;
        }
        mCallBackEventListener.onAlerting(alertingNotifyBean);
    }
    
    /**
     * 呼叫被保持回调
     * @param callId
     *      通话id
     * @see com.huawei.fast.voip.FastVoIPNotify#onHeld(int)
     */
    @Override
    public void onHeld(int callId)
    {
        Log.d(TAG, "CallNotify.onHeld callId=" + callId);
        if (null == mCallBackEventListener)
        {
            return;
        }
        mCallBackEventListener.onHeld(callId);
    }
    
    /**
     * 被叫方等待通知回调
     * @param alertingNotifyBean
     *      封装解析后的通知
     * @see com.huawei.fast.voip.FastVoIPNotify#onQueue(com.huawei.fast.voip.bean.AlertingNotifyBean)
     */
    @Override
    public void onQueue(AlertingNotifyBean alertingNotifyBean)
    {
        Log.d(TAG, "CallNotify.onQueue callId=" + alertingNotifyBean.getId());
        if (null == mCallBackEventListener)
        {
            return;
        }
        mCallBackEventListener.onQueue(alertingNotifyBean);
    }
    
    /**
     * 注册状态回调
     * @param regId
     *      分配的注册id
     * @param regState
     *      当前的注册状态, {"Active","Terminate"} 
     * @param event
     *      注册状态改变的原因
     * @param retryAfter
     *      服务器重连的时间
     * @see com.huawei.fast.voip.FastVoIPNotify#onRegState(int, java.lang.String, java.lang.String, int)
     */
    @Override
    public void onRegState(int regId, String regState, String event,
            int retryAfter)
    {
        Log.d(TAG, "CallNotify.onRegState regId=" + regId);
        if (null == mCallBackEventListener)
        {
            return;
        }
        mCallBackEventListener.onRegState(regId, regState, event, retryAfter);
    }
    
    /**
     * 根据通话id移除所有相应的监听
     * @param callId
     *      通话id
     */
    public void removeListenersByCallId(int callId)
    {
        mCallAdapterListeners.remove(callId);
        mAnswerAdapterListeners.remove(callId);
        mHoldAdapterListeners.remove(callId);
        mRetrieveAdapterListeners.remove(callId);
    }
    
    /**
     * 移除所有的监听器
     */
    public void removeAllListener()
    {
        mCallLoginListener = null;
        mCallLogoutListener = null;
        mCallBackEventListener = null;
        mCallAdapterListeners.clear();
        mAnswerAdapterListeners.clear();
        mHoldAdapterListeners.clear();
        mRetrieveAdapterListeners.clear();
    }
}
