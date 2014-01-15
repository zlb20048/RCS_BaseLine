/*
 * 文件名: BindVoip.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 王媛媛
 * 创建时间:2012-3-15
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.voip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.VoipAction;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.VOIPMessageType;
import com.huawei.basic.android.im.component.database.voip.VoipURIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.voip.AnswerAdapterListener;
import com.huawei.basic.android.im.component.voip.CallAdapterListener;
import com.huawei.basic.android.im.component.voip.CallBackEventListener;
import com.huawei.basic.android.im.component.voip.CallManager;
import com.huawei.basic.android.im.component.voip.CallSession;
import com.huawei.basic.android.im.component.voip.CloseAdapterListener;
import com.huawei.basic.android.im.component.voip.ICallLoginListener;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.voip.CommunicationLogDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.voip.VoipAccountDbAdapter;
import com.huawei.basic.android.im.logic.login.receiver.ConnectionChangedReceiver;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.voip.CommunicationLog;
import com.huawei.basic.android.im.logic.model.voip.VoipAccount;
import com.huawei.basic.android.im.logic.notification.VoipNotificationEntity;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.fast.voip.FastVoIPConstant;
import com.huawei.fast.voip.bean.AlertingNotifyBean;
import com.huawei.fast.voip.bean.TalkingNotifyBean;

/**
 * 创建voip绑定、解绑、打电话、接听电话、挂断电话逻辑处理类
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-15]
 */
public class VoipLogic extends BaseLogic implements IVoipLogic
{
    /**
     * Debug Tag
     */    
    private static final String TAG = "VoipLogic";
    /**
     * voip通話管理类对象
     */
    private CallManager mCallManager;
    
    /**
     * Voip账号数据操作适配器
     * 
     */
    private VoipAccountDbAdapter mVoipAccountDbAdapter;
    
    /**
     * 个人/好友信息表数据库操作 适配器
     */
    private ContactInfoDbAdapter mContactInfoDbAdapter;
    
    /**
     * 通话记录数据操作适配器
     */
    private CommunicationLogDbAdapter mCommunicationLogDbAdapter;
    
    /**
     * 头像数据操作适配器
     */
    private FaceThumbnailDbAdapter mFaceThumbnailDbAdapter;
    
    /**
     * 上下文
     */
    private Context mContext;
    
    /**
     * 通知栏Key
     */
    private String mNotificationKey;
    
    /**
     * 通话被动回调事件监听器
     */
    private CallBackEventListener mCallBackEventListener = new CallBackEventListener()
    {
        
        @Override
        protected void onClosed(int callId, String reason, int repCode)
        {
            //向Ui发送通知
            sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_CLOSE);
            //保存数据库 结束时间
            String ownerUserId = FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId();
            mCommunicationLogDbAdapter.updateEndTimeByLastCallTimeAndCallId(new Date(
                    System.currentTimeMillis()),
                    ownerUserId,
                    callId,
                    CommunicationLog.TYPE_VOIP_CALL_IN_MISSED);
        }
        
        @Override
        protected void onAlerting(AlertingNotifyBean alertingNotifyBean)
        {
            //当前用户ID
            final String ownerUserId = FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId();
            //当前时间
            long currentTime = System.currentTimeMillis();
            //呼叫类型
            int sort = alertingNotifyBean.getSession()
                    .getContent()
                    .equals(FastVoIPConstant.AUDIOTYPE) ? CommunicationLog.SORT_AUDIO_CALL
                    : CommunicationLog.SORT_VIDEO_CALL;
            
            int callId = alertingNotifyBean.getId();
            
            //创建通话记录对象
            CommunicationLog communicationLog = new CommunicationLog();
            //用户Id
            communicationLog.setOwnerUserId(ownerUserId);
            //通话ID
            communicationLog.setCallId(callId);
            //被叫昵称
            communicationLog.setRemoteDisplayName(alertingNotifyBean.getRemoteDisplayname());
            //被叫电话
            communicationLog.setRemotePhoneNum(alertingNotifyBean.getRemoteNumber());
            //主叫
            communicationLog.setType(CommunicationLog.TYPE_VOIP_CALL_IN_MISSED);
            //呼叫类型
            communicationLog.setSort(sort);
            //呼叫日期
            communicationLog.setCallDate(new Date(currentTime).toString());
            //是否已读
            communicationLog.setIsUnread(false);
            //开始时间
            communicationLog.setCallTime(new Date(currentTime));
            //            if (null != faceThumbnailModel)
            //            {
            //                communicationLog.setFaceUrl(faceThumbnailModel.getFaceUrl());
            //                communicationLog.setFaceData(faceThumbnailModel.getFaceBytes());
            //            }
            //通话记录插入数据库
            mCommunicationLogDbAdapter.addCommunicationLog(communicationLog);
            //启动接听电话界面
            Intent intent = new Intent(VoipAction.ACTION_VOIP_CALLING);
            intent.putExtra(VoipAction.EXTRA_IS_CALL_OUT, false);
            intent.putExtra(VoipAction.EXTRA_PHONE_NUMBER,
                    alertingNotifyBean.getRemoteNumber());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
        
        @Override
        protected void onQueue(AlertingNotifyBean alertingNotifyBean)
        {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        protected void onHeld(int callId)
        {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        protected void onRegState(int regId, String regState, String event)
        {
            // TODO Auto-generated method stub
            
        }
        
    };
    
    /**
     * 构造方法:得到单例的 Voip账号数据操作适配器和个人/好友信息表数据库操作适配器
     * 
     * @param context
     *            Context
     */
    public VoipLogic(Context context)
    {
        mContext = context;
        mCallManager = CallManager.getInstance();
        mVoipAccountDbAdapter = VoipAccountDbAdapter.getInstance(context);
        mContactInfoDbAdapter = ContactInfoDbAdapter.getInstance(context);
        mCommunicationLogDbAdapter = CommunicationLogDbAdapter.getInstance(context);
        mFaceThumbnailDbAdapter = FaceThumbnailDbAdapter.getInstance(context);
    }
    
    /**
     * 
     * 初始化VOIP
     * 
     * @return
     */
    public boolean init()
    {
        //创建并返回 铃音绝对文件路径
        String ringFile = getRingFilePath(mContext);
        
        //初始化VOIP
        Logger.i(TAG, "FusionConfig.voipDomain: "
                + FusionConfig.getInstance().getVoipDomain()
                + "FusionConfig.voipServer"
                + FusionConfig.getInstance().getVoipServer()
                + "FusionConfig.voipPort"
                + FusionConfig.getInstance().getVoipPort());
        
        return CallManager.getInstance().init(FusionConfig.getInstance()
                .getVoipDomain(),
                FusionConfig.getInstance().getVoipServer(),
                FusionConfig.getInstance().getVoipPort(),
                mContext,
                ringFile);
    }
    
    /**
     * 
     * 创建并返回 铃音绝对文件路径
     *
     * @param context
     * @return 返回铃音文件绝对路径
     */
    private String getRingFilePath(Context context)
    {
        
        boolean exists = context.getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE)
                .getBoolean(Common.KEY_EXISTS_VOIP_RING_FILE, false);
        
        //判断是否已经创建了铃音文件
        if (!exists)
        {
            InputStream is = null;
            OutputStream os = null;
            try
            {
                is = context.getResources().openRawResource(R.raw.voip_ring);
                os = context.openFileOutput("voip_ring.wav",
                        Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                //将raw文件copy到/data/data/应用包名/files/下
                while (is.read(buffer) != -1)
                {
                    os.write(buffer);
                }
                //设置铃音文件创建成功标志
                context.getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(Common.KEY_EXISTS_VOIP_RING_FILE, true)
                        .commit();
                
            }
            catch (FileNotFoundException e)
            {
                Logger.d(TAG, "ring file not exists");
            }
            catch (IOException e)
            {
                Logger.d(TAG, e.getMessage());
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        Logger.d(TAG, "raw voip_ring.wav close exception");
                    }
                }
                if (os != null)
                {
                    try
                    {
                        os.close();
                    }
                    catch (IOException e)
                    {
                        Logger.d(TAG, "data voip_ring.wav close exception");
                    }
                }
            }
            
        }
        //定义铃音文件路径
        String ringFilePath = "/data/data/" + context.getPackageName()
                + "/files/voip_ring.wav";
        Logger.d(TAG, ringFilePath);
        return ringFilePath;     
    }
    
    /**
     * 
     *  检测是否登录VOIP
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#checkVoipLogin()
     */
    public void checkVoipLogin()
    {
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        VoipAccount voipAccount = mVoipAccountDbAdapter.findVoipAccountByOwnerUserId(ownerUserId);
        if (voipAccount != null)
        {
            sendMessage(FusionMessageType.VOIPMessageType.VOIP_SHOW_UNBIND_BUTTON,
                    voipAccount.getAccount());
        }
        else
        {
            sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_SHOW_BIND_BUTTON);
        }
    }
    
    /**
     * 如果上次已经登录，则自动登录
     * 
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#autoLoginVoip()
     */
    public void autoLoginVoip()
    {
        if (CallManager.getInstance().isLogin())
        {
            return;
        }
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        VoipAccount voipAccount = mVoipAccountDbAdapter.findVoipAccountByOwnerUserId(ownerUserId);
        if (null != voipAccount)
        {
            ContactInfoModel contactInfoModel = mContactInfoDbAdapter.queryByFriendSysIdNoUnion(ownerUserId,
                    ownerUserId);
            String displayName = null;
            if (null != contactInfoModel)
            {
                displayName = contactInfoModel.getDisplayName();
            }
            
            final String aor = voipAccount.getAccount();
            //final  String aor =  DecodeUtil.decrypt(voipAccount.getPassword(),voipAccount.getAccount());
            String password = DecodeUtil.decrypt(aor, voipAccount.getPassword());
            CallManager.getInstance().login(displayName,
                    aor,
                    password,
                    new ICallLoginListener()
                    {
                        @Override
                        public void onLoginSuccessful(int regId, String uri)
                        {
                            mCallManager.setCallBackEventListener(mCallBackEventListener);
                        }
                        
                        @Override
                        public void onLoginFailure(int regId, int errorCode)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                    });
        }
    }
    
    /**
     *查询数据库中的VoipAccount
     * 
     * @return VoipAccount
     */
    public VoipAccount queryVoipAccount()
    {
        String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        return mVoipAccountDbAdapter.findVoipAccountByOwnerUserId(ownerUserId);
    }
    
    /**
     * 获得好友头像
     * 
     * @param friendUserId
     *            好友UserID
     * @return 头像
     */
    public FaceThumbnailModel getFaceThumbnailModel(String friendUserId)
    {
        return mFaceThumbnailDbAdapter.queryByFaceId(friendUserId);
    }
    
    /**
     * 根据电话号码得到好友信息
     * 
     * @param phoneNum
     *            手机号或是voip账号
     * @return ContactInfoModel
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#getContactInfoModelByPhone(java.lang.String)
     */
    public ContactInfoModel getContactInfoModelByPhone(String phoneNum)
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        return mContactInfoDbAdapter.queryContactInfoByPhone(userSysId,
                phoneNum);
    }
    
    /**
     * 绑定voip账号，绑定成功后，在本地数据库保存该账号信息，并登录sip服务器
     * 
     * @param aor
     *            voip账号
     * @param password
     *            voip密码
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#bindVoip(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void bindVoip(final String aor, final String password)
    {
        if (!CallManager.getInstance().isInit())
        {
            sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_UNINIT_SDK);
            return;
        }
        if (CallManager.getInstance().isLogin())
        {
            return;
        }
        final String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        ContactInfoModel contactInfoModel = mContactInfoDbAdapter.queryByFriendSysIdNoUnion(ownerUserId,
                ownerUserId);
        String displayName = null;
        if (null != contactInfoModel)
        {
            contactInfoModel.getDisplayName();
        }
        
        int regId = mCallManager.login(displayName,
                aor,
                password,
                new ICallLoginListener()
                {
                    //登录成功
                    @Override
                    public void onLoginSuccessful(int regId, String uri)
                    {
                        VoipAccount voipAccount = new VoipAccount();
                        //使用加密后的密码为voip账号加密：加密后的密码为DecodeUtil.encrypt(aor,
                        //password)
                        voipAccount.setAccount(DecodeUtil.encrypt(DecodeUtil.encrypt(aor,
                                password),
                                aor));
                        voipAccount.setPassword(DecodeUtil.encrypt(aor,
                                password));
                        voipAccount.setOwnerUserId(ownerUserId);
                        voipAccount.setCreatedDate(new Date());
                        mVoipAccountDbAdapter.save(voipAccount);
                        sendMessage(FusionMessageType.VOIPMessageType.VOIP_BIND_SUCCESS,
                                aor);
                        mCallManager.setCallBackEventListener(mCallBackEventListener);
                    }
                    
                    //登录失败
                    @Override
                    public void onLoginFailure(int regId, int errorCode)
                    {
                        if (errorCode == 404 || errorCode == 401)
                        {
                            
                            //密码错误
                            sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_ACCOUNT_PS_ERROR);
                        }
                        else
                        {
                            sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_BIND_FAILED);
                        }
                        mCallManager.logout(null);
                    }
                    
                });
        
        if (regId == -1)
        {
            int netStatus = ConnectionChangedReceiver.checkNet(mContext);
            if (LoginMessageType.NET_STATUS_WAP == netStatus
                    || LoginMessageType.NET_STATUS_DISABLE == netStatus)
            {
                
                sendEmptyMessage(FusionMessageType.VOIPMessageType.NTE_ERROR_VOIP_BIND_FAILED);
            }
        }
        
    }
    
    /**
     * 删除并取消voip绑定 登出sip服务器，并删除本地数据库中的信息
     * 
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#logout(int)
     */
    @Override
    public void unbindVOIP()
    {
        final String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        //删除绑定VOIP的数据
        mVoipAccountDbAdapter.deleteVoipAccountByOwnerUserId(ownerUserId);
        //发送成功解绑Message到UI层
        sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_UNBIND_SUCCESS);
        mCallManager.logout(null);
        
    }
    
    /**
     * 退出HITalk时登出voip账号,此时并没有解除绑定，数据库中仍然保存有VOIP账号信息
     * 
     */
    public void logout()
    {
        //如果不在登录状态，就不需要执行登出
        if (!CallManager.getInstance().isLogin())
        {
            return;
        }
        mCallManager.logout(null);
    }
    
    /**
     * 拨打电话流程
     * 
     * @param displayName
     *            displayName
     * @param phoneNum
     *            phoneNum
     * @param type
     *            type
     * @param faceThumbnailModel
     *            FaceThumbnailModel
     */
    @Override
    public void callVoip(final String displayName, final String phoneNum,
            final String type, final FaceThumbnailModel faceThumbnailModel)
    {
        //当前用户ID
        final String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        //当前时间
        long currentTime = System.currentTimeMillis();
        //呼叫类型
        int sort = type.equals(FastVoIPConstant.AUDIOTYPE) ? CommunicationLog.SORT_AUDIO_CALL
                : CommunicationLog.SORT_VIDEO_CALL;
        
        //创建通话记录对象
        CommunicationLog communicationLog = new CommunicationLog();
        //用户Id
        communicationLog.setOwnerUserId(ownerUserId);
        //被叫昵称
        communicationLog.setRemoteDisplayName(displayName);
        //被叫电话
        communicationLog.setRemotePhoneNum(phoneNum);
        //主叫
        communicationLog.setType(CommunicationLog.TYPE_VOIP_CALL_OUT);
        //呼叫类型
        communicationLog.setSort(sort);
        //呼叫日期
        communicationLog.setCallDate(new Date(currentTime).toString());
        //是否已读
        communicationLog.setIsUnread(false);
        //开始时间
        communicationLog.setCallTime(new Date(currentTime));
//        if (null != faceThumbnailModel)
//        {
//            communicationLog.setFaceUrl(faceThumbnailModel.getFaceUrl());
//            communicationLog.setFaceData(faceThumbnailModel.getFaceBytes());
//        }
        //通话记录插入数据库
        
        int callId = mCallManager.call(displayName,
                phoneNum,
                type,
                new CallAdapterListener()
                {
                    
                    @Override
                    public void onClosed(int callId, String reason, int repCode)
                    {
                        mCommunicationLogDbAdapter.updateEndTimeByLastCallTimeAndCallId(new Date(
                                System.currentTimeMillis()),
                                ownerUserId,
                                callId,
                                CommunicationLog.TYPE_VOIP_CALL_OUT);
                        //向UI发送通知 通话结束
                        sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_CLOSE);
                    }
                    
                    @Override
                    public void onTalking(TalkingNotifyBean talkingNotifyBean)
                    {
                        //向UI发送通知
                        sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_TALKING);
                        //把开始通话时间插入数据库
                        int callId = talkingNotifyBean.getId();
                        mCommunicationLogDbAdapter.updateStartTimeByLastCallTimeAndCallId(new Date(
                                System.currentTimeMillis()),
                                ownerUserId,
                                callId,
                                CommunicationLog.TYPE_VOIP_CALL_OUT);
                    }
                    
                    @Override
                    public void onRinging(int callId, String remoteUri)
                    {
                        //向UI发送通知
                        sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_RINGING);
                    }
                    
                    @Override
                    public void onQueued(int callId, String remoteUri)
                    {
                        // TODO Auto-generated method stub
                    }
                });
        //通话ID
        communicationLog.setCallId(callId);
        mCommunicationLogDbAdapter.addCommunicationLog(communicationLog);
    }
    
    /**
     * 来电接听
     */
    @Override
    public void answerVoip()
    {
        mCallManager.answer(new AnswerAdapterListener()
        {
            
            @Override
            public void onTalking(TalkingNotifyBean talkingNotifyBean)
            {
                sendEmptyMessage(VOIPMessageType.VOIP_IN_CALL_STATE_TALKING);
                //当前用户ID
                final String ownerUserId = FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId();
                //当前时间
                long currentTime = System.currentTimeMillis();
                mCommunicationLogDbAdapter.updateStartTimeByLastCallTimeAndCallId(new Date(
                        currentTime),
                        ownerUserId,
                        talkingNotifyBean.getId(),
                        CommunicationLog.TYPE_VOIP_CALL_IN_ALREADY);
            }
            
            @Override
            public void onClosed(int callId, String reason, int repCode)
            {
                String ownerUserId = FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId();
                mCommunicationLogDbAdapter.updateEndTimeByLastCallTimeAndCallId(new Date(
                        System.currentTimeMillis()),
                        ownerUserId,
                        callId,
                        CommunicationLog.TYPE_VOIP_CALL_IN_ALREADY);
                sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_CLOSE);
            }
        });
    }
    
    /**
     * 挂断电话
     * @param isRefuse  boolean
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#closeVoip(boolean)
     */
    public void closeVoip(final boolean isRefuse)
    {
        CallSession callSession = mCallManager.getCurrentCallSession();
        if (callSession == null)
        {
            //主动挂断电话时，如果callSession是空时，也可以挂断，防止界面卡死
            sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_CLOSE);
            return;
        }
        boolean isClose = mCallManager.close(callSession.getCallId(),
                new CloseAdapterListener()
                {
                    @Override
                    public void onClosed(int callId, String reason, int repCode)
                    {
                        String ownerUserId = FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId();
                        //拒绝接听
                        if (isRefuse)
                        {
                            mCommunicationLogDbAdapter.updateEndTimeByLastCallTimeAndCallId(new Date(
                                    System.currentTimeMillis()),
                                    ownerUserId,
                                    callId,
                                    CommunicationLog.TYPE_VOIP_CALL_IN_REFUSED);
                        }
                        else
                        {
                            //挂断
                            mCommunicationLogDbAdapter.updateEndTimeByLastCallTimeAndCallId(new Date(
                                    System.currentTimeMillis()),
                                    ownerUserId,
                                    callId);
                        }
                        sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_CLOSE);
                    }
                });
        if (!isClose)
        {
            sendEmptyMessage(VOIPMessageType.VOIP_CALL_STATE_CLOSE);
            return;
        }
        
    }
    
    /**
     * 判断Voip是否登录
     * 
     * @return 登陆
     */
    @Override
    public boolean isLogin()
    {
        return mCallManager.isLogin();
    }
    
    /**
     * 校验填写的密码和数据库中查询的密码是否相同
     * 
     * @param ps
     *            填写的密码
     */
    @Override
    public void checkPS(String ps)
    {
        VoipAccount voipAccount = queryVoipAccount();
        String voipPs = null;
        //使用加密后的密码解密
        //查询数据库中的密码 并解密
        if (null != voipAccount)
        {
            voipPs = DecodeUtil.decrypt(voipAccount.getAccount(),
                    voipAccount.getPassword());
        }
        
        //如果密码和数据库中的匹配则校验成功,否则密码错误,解绑失败
        if (StringUtil.equals(ps, voipPs))
        {
            unbindVOIP();
        }
        else
        {
            sendEmptyMessage(FusionMessageType.VOIPMessageType.VOIP_PS_ERROR);
        }
        
    }
    
    /**
     * 免提是否打开
     * 
     * @return 免提是否打开
     */
    public boolean isOpenSpeaker()
    {
        return mCallManager.isOpenSpeaker();
    }
    
    /**
     * 打开免提
     */
    public void openSpeaker()
    {
        mCallManager.openSpeaker();
        
    }
    
    /**
     * 关闭免提
     */
    public void closeSpeaker()
    {
        mCallManager.closeSpeaker();
    }
    
    /**
     * 是否静音
     * 
     * @return 是否静音
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#isMute()
     */
    public boolean isMute()
    {
        return mCallManager.isMute();
    }
    
    /**
     * 打开静音
     * 
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#openMute()
     */
    public void openMute()
    {
        mCallManager.openMute();
    }
    
    /**
     * 关闭静音
     * 
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#closeMute()
     */
    public void closeMute()
    {
        mCallManager.closeMute();
    }
    
    /**
     * 二次拨号
     * 
     * @param code
     *            二次拨号输入的号码
     */
    @Override
    public void redial(String code)
    {
        if (null != mCallManager.getCurrentCallSession())
        {
            mCallManager.redial(mCallManager.getCurrentCallSession()
                    .getCallId(), code);
        }
    }
    
    /**
     * 添加原生呼出记录
     * 
     * @param phoneNum
     *            号码
     */
    @Override
    public void addLocalContactCommunicationLog(String phoneNum)
    {
        //当前用户ID
        final String ownerUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        //当前时间
        long currentTime = System.currentTimeMillis();
        //创建通话记录对象
        CommunicationLog communicationLog = new CommunicationLog();
        //用户Id
        communicationLog.setOwnerUserId(ownerUserId);
        //被叫昵称
        communicationLog.setRemoteDisplayName(phoneNum);
        //被叫电话
        communicationLog.setRemotePhoneNum(phoneNum);
        //主叫
        communicationLog.setType(CommunicationLog.TYPE_ORDINARY_CALL_OUT);
        //呼叫类型
        communicationLog.setSort(CommunicationLog.SORT_AUDIO_CALL);
        //呼叫日期
        communicationLog.setCallDate(new Date(currentTime).toString());
        //是否已读
        communicationLog.setIsUnread(false);
        //开始时间
        communicationLog.setCallTime(new Date(currentTime));
        //通话ID
        communicationLog.setCallId(0);
        mCommunicationLogDbAdapter.addCommunicationLog(communicationLog);
    }
    
    /**
     * 取消通知
     */
    @Override
    public void destroyNotification()
    {
        if (!StringUtil.isNullOrEmpty(mNotificationKey))
        {
            NotificationEntityManager.getInstance()
                    .destroyNotification(mNotificationKey);
        }
    }
    
    /**
     * 添加通知
     * @param tickerText 状态栏标题 显示为"与某某通话中..." + 通话时间
     * @param faceBitmap 联系人头像
     * @param contactName 联系人姓名
     * @param callStartTime 电话开始时间
     * @param intent 点击通知栏跳转页面
     * @return string 新的通知消息
     */
    @Override
    public String showNewNotification(CharSequence tickerText,
            Bitmap faceBitmap, String contactName, Date callStartTime,
            Intent intent)
    {
        mNotificationKey = NotificationEntityManager.getInstance()
                .showNewNotification(new VoipNotificationEntity(tickerText,
                        faceBitmap, contactName, callStartTime, intent));
        return mNotificationKey;
    }
    
    /**
     * 对 该logic对象定义被监听的Uri
     * @return  短信数据库的uri
     * @see com.huawei.basic.android.im.framework.logic.BaseLogic#getObserverUris()
     */
    @Override
    protected Uri[] getObserverUris()
    {
        return new Uri[] { VoipURIField.VOIP_ACCOUNT_URI };
    }
    
    /**
     * 传入Uri数组来监听该logic所要监听的数据库对象
     * @param selfChange 是否改变
     * @param uri   logic所要监听的数据库对象的uri
     * @see com.huawei.basic.android.im.framework.logic.BaseLogic#onChangeByUri(boolean, android.net.Uri)
     */
    @Override
    protected void onChangeByUri(boolean selfChange, Uri uri)
    {
        
        if (uri == VoipURIField.VOIP_ACCOUNT_URI)
        {
            sendEmptyMessageDelayed(FusionMessageType.VOIPMessageType.VOIP_BIND_UNBIND,
                    2000);
        }
    }
    
    /**
     * 更新通知栏头像 
     * @param faceBitmap 头像数据
     * @param name  姓名
     * @see com.huawei.basic.android.im.logic.voip.IVoipLogic#updateNotificationData(android.graphics.Bitmap, java.lang.String)
     */
    @Override
    public void updateNotificationData(Bitmap faceBitmap, String name)
    {
        if (StringUtil.isNullOrEmpty(mNotificationKey))
        {
            return;
        }
        
        if (null != NotificationEntityManager.getInstance()
                && null != NotificationEntityManager.getInstance()
                        .getNotificationEntity(mNotificationKey))
        {
            ((VoipNotificationEntity) NotificationEntityManager.getInstance()
                    .getNotificationEntity(mNotificationKey)).setFaceBitmap(faceBitmap);
            ((VoipNotificationEntity) NotificationEntityManager.getInstance()
                    .getNotificationEntity(mNotificationKey)).setContactName(name);
        }
    }
    
}
