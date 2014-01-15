/*
 * 文件名: LoginLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.login;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceData;
import com.huawei.basic.android.im.component.service.app.ILoginServiceListener;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.framework.logic.ILogic;
import com.huawei.basic.android.im.logic.adapter.db.AccountDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.adapter.http.LoginHttpManager;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.im.IImLogic;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.logic.model.AccountModel;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.CountryItemModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.HanziToPinyin;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.SystemFacesUtil;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * 登录模块逻辑处理实现类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-6]
 */
public class LoginLogic extends BaseLogic implements ILoginLogic
{
    
    /**
     * TAG
     */
    private static final String TAG = "LoginLogic";
    
    /**
     * 服务发送对象
     */
    private IServiceSender mServiceSender;
    
    /**
     * 系统 Context对象,用于对数据库操作
     */
    private Context mContext;
    
    /**
     * 刷新TOKEN的IServiceListener
     */
    private ILoginServiceListener mRefreshTokenListener;
    
    /**
     * 国家代码表
     */
    private List<CountryItemModel> mCountryLists;
    
    /**
     * 设置Logic对象
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 群组Logic对象
     */
    private IGroupLogic mGroupLogic;
    
    /**
     * 好友Logic对象
     */
    private IFriendLogic mFriendLogic;
    
    /**
     * 联系人Logic对象
     */
    private IContactLogic mContactLogic;
    
    /**
     * Im Logic对象
     */
    private IImLogic mImLogic;
    
    /**
     * 
     * 构造函数
     * 
     * @param context
     *            上下文
     * @param serviceSender
     *            serviceSender
     */
    public LoginLogic(Context context, IServiceSender serviceSender)
    {
        this.mContext = context;
        this.mServiceSender = serviceSender;
        //监听server
        mServiceSender.addLoginServiceListener(new ILoginServiceListener()
        {
            
            @Override
            public void sendLoginMessage(int messageType, String result)
            {
                switch (messageType)
                {
                    case LoginMessageType.LOGIN_SUCCESS:
                        Logger.i(TAG, "login");
                        sendEmptyMessage(messageType);
                        break;
                    case LoginMessageType.REFRESH_TOKEN:
                        FusionConfig.getInstance().setToken(result);
                        break;
                    default:
                        sendMessage(messageType, result);
                        break;
                }
            }
            
            @Override
            public void loginSuccessCallback(AASResult aasResult)
            {
            }
        });
        
    }
    
    /**
     * 获取上次登录的用户信息<BR>
     * 
     * @param context
     *            Context
     * @return 用户信息
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#getLastLoginAccountModel()
     */
    @Override
    public AccountModel getLastLoginAccountModel(Context context)
    {
        AccountModel model = null;
        // 上次登录的用户系统ID
        SharedPreferences sp = mContext.getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        String userSysId = sp.getString(Common.KEY_USER_SYSID, null);
        String password = sp.getString(Common.KEY_USER_PASSWORD, null);
        String passPort = sp.getString(Common.KEY_USER_PASSPORT, "");
        String userSysID = context.getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE)
                .getString(Common.KEY_USER_SYSID, null);
        if (userSysID != null)
        {
            model = new AccountModel();
            model.setUserSysId(userSysId);
            model.setPassword(StringUtil.isNullOrEmpty(password) ? null
                    : password);
            model.setLoginAccount(passPort);
            return model;
        }
        return null;
    }
    
    /**
     * 登录<BR>
     * 
     * @param userAccount
     *            账号
     * @param passwd
     *            密码
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#login(java.lang.String,
     *      java.lang.String)
     */
    
    @Override
    public void login(String userAccount, String passwd)
    {
        login(userAccount, passwd, null);
    }
    
    /**
     * 登录<BR>
     * 登录时先进行HTPP的AAS鉴权登录(回调loginSuccessCallback())，成功后再进行XMPP注册(
     * sendLoginMessage())。
     * AAS登录后返回AASResult，保存到FusionConfig中；XMPP注册成功才算成功的走完登录流程。
     * 
     * @param userAccount
     *            账号
     * @param passwd
     *            密码
     * @param verifyCode
     *            验证码
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#login(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    
    @Override
    public void login(final String userAccount, final String passwd,
            String verifyCode)
    {
        mServiceSender.login(userAccount,
                passwd,
                verifyCode,
                FusionConfig.getInstance().getClientVersion());
        
    }
    
    /**
     * 登出<BR>
     * 1、登出时，调用登出接口及XMPP注销操作；2、停止刷新TOKEN；
     * 
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#logout()
     */
    
    @Override
    public void logout()
    {
        mServiceSender.addLoginServiceListener(new ILoginServiceListener()
        {
            
            @Override
            public void sendLoginMessage(int messageType, String result)
            {
                switch (messageType)
                {
                    case LoginMessageType.LOGOUT:
                        sendMessage(messageType, result);
                        break;
                    case LoginMessageType.LOGOUT_FAIL:
                        sendMessage(messageType, result);
                        break;
                    default:
                        break;
                }
                mServiceSender.removeLoginServiceListener(this);
                
            }
            
            @Override
            public void loginSuccessCallback(AASResult aasResult)
            {
                
            }
            
        });
        AASResult aasResult = FusionConfig.getInstance().getAasResult();
        mServiceSender.logout(aasResult.getUserID(),
                aasResult.getUserSysId(),
                aasResult.getToken(),
                aasResult.getLoginid());
        stopRefreshToken();
    }
    
    /**
     * 获取验证码<BR>
     * 
     * @param account
     *            账号
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#getVerifyCode(java.lang.String)
     */
    @Override
    public void getVerifyCode(String account)
    {
        new LoginHttpManager().getVerifyCode(account, new IHttpListener()
        {
            
            @Override
            public void onResult(int action, Response response)
            {
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    byte[] bytes = response.getByteData();
                    sendMessage(LoginMessageType.GET_VERIFY_CODE_IMAGE_SUCCESS,
                            bytes);
                }
                else
                {
                    sendMessage(LoginMessageType.GET_VERIFY_CODE_IMAGE_FAILED,
                            null);
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                
            }
        });
    }
    
    /**
     * 校验验证码<BR>
     * 
     * @param userAccount
     *            账号
     * @param verifyCode
     *            验证码
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#login(java.lang.String,
     *      java.lang.String)
     */
    
    @Override
    public void sendVerifyCodeImage(String userAccount, String verifyCode)
    {
        new LoginHttpManager().sendVerifyCode(userAccount,
                verifyCode,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            if (response.getResultCode() == 0)
                            {
                                sendMessage(LoginMessageType.SEND_VERIFY_CODE_IMAGE_SUCCESS,
                                        null);
                            }
                            else
                            {
                                sendMessage(LoginMessageType.SEND_VERIFY_CODE_IMAGE_FAILED,
                                        response.getResultCode());
                            }
                        }
                        else
                        {
                            sendMessage(LoginMessageType.SEND_VERIFY_CODE_IMAGE_FAILED,
                                    response.getResultCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * 登录成功保存信息<BR>
     * 
     * @param autoLogin
     *            是否自动登录
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#setLoginMode(boolean)
     */
    
    @Override
    public void setLoginMode(boolean autoLogin)
    {
        AccountModel accountModel = AccountDbAdapter.getInstance(mContext)
                .queryByUserSysId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId());
        if (accountModel != null)
        {
            accountModel.setAutoLogin(autoLogin);
            AccountDbAdapter.getInstance(mContext)
                    .updateByLoginAccount(accountModel);
        }
    }
    
    /**
     * 停止刷新TOKEN，并移除刷新TOKEN的监听器 <BR>
     * 
     */
    private void stopRefreshToken()
    {
        mServiceSender.stopRefreshToken();
        mServiceSender.removeLoginServiceListener(mRefreshTokenListener);
        mRefreshTokenListener = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveAccount(Context context, String userSysID,
            String userID, String account, String passwd)
    {
        Editor edit = context.getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE)
                .edit();
        edit.putString(Common.KEY_USER_SYSID, userSysID);
        edit.putString(Common.KEY_USER_ID, userID);
        edit.putString(Common.KEY_USER_PASSPORT, account);
        
        edit.putString(Common.KEY_USER_PASSWORD,
                DecodeUtil.encrypt(account, passwd));
        edit.putBoolean(Common.KEY_ISLOGIN, true);
        edit.commit();
        AccountModel accountModel = AccountDbAdapter.getInstance(mContext)
                .queryByUserSysId(userSysID);
        if (accountModel == null)
        {
            accountModel = new AccountModel();
            //            setAccountAttribute(accountModel, userSysID, account, passwd);
            //用户信息设置
            accountModel.setUserSysId(userSysID);
            accountModel.setLoginAccount(account);
            passwd = DecodeUtil.encrypt(account, passwd);
            accountModel.setPassword(passwd);
            accountModel.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            
            AccountDbAdapter.getInstance(mContext).insertAccount(accountModel);
            return true;
        }
        else
        {
            //            setAccountAttribute(accountModel, userSysID, account, passwd);
            //用户信息设置
            accountModel.setUserSysId(userSysID);
            accountModel.setLoginAccount(account);
            passwd = DecodeUtil.encrypt(account, passwd);
            accountModel.setPassword(passwd);
            accountModel.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            AccountDbAdapter.getInstance(mContext).updateByUserSysId(userSysID,
                    accountModel);
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void requestLoginMessage()
    {
        mServiceSender.requestLoginMessage();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterLoginSuccessed(boolean isFirst)
    {
        Logger.i(TAG, "afterLogin" + isFirst);
        new AfterLoginHandler().start(isFirst);
    }
    
    /**
     * {@inheritDoc}
     */
    public class AfterLoginHandler extends Handler
    {
        /**
         * 最大重试次数
         */
        private static final int MAX_RETRY_TIMES = 2;
        
        /**
         * 是否首次登录
         */
        private boolean mFirstLogin;
        
        /**
         * 重试次数
         */
        private int retry;
        
        /**
         * 成功事件记录器
         */
        private HashSet<Integer> mCompletedStep;
        
        /**
         * 
         * 登录启动
         * 
         * @param firstLogin
         *            是否第一次登录
         */
        public void start(boolean firstLogin)
        {
            mFirstLogin = firstLogin;
            mCompletedStep = new HashSet<Integer>();
            ((ILogic) mSettingsLogic).addHandler(this);
            ((ILogic) mGroupLogic).addHandler(this);
            ((ILogic) mFriendLogic).addHandler(this);
            ((ILogic) mContactLogic).addHandler(this);
            ((ILogic) mImLogic).addHandler(this);
            sendEmptyMessage(LoginMessageType.BEGIN_AFTER_LOGIN);
        }
        
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg)
        {
            final int action = msg.what;
            Logger.i(TAG, "action: " + action + ", retry: " + retry);
            if (retry < MAX_RETRY_TIMES)
            {
                mCompletedStep.add(action);
                switch (action)
                {
                    case LoginMessageType.BEGIN_AFTER_LOGIN:
                        if (!mCompletedStep.contains(SettingsMessageType.GET_MYPROFILE_SUCCEED))
                        {
                            requesrProfile();
                            break;
                        }
                    case SettingsMessageType.GET_MYPROFILE_SUCCEED:
                    case SettingsMessageType.GET_MYPROFILE_FAILED:
                        if (!mCompletedStep.contains(GroupMessageType.GET_GROUP_LIST_SUCCESS))
                        {
                            requestGroupList();
                            break;
                        }
                    case GroupMessageType.GET_GROUP_LIST_SUCCESS:
                    case GroupMessageType.GET_GROUP_LIST_FAILED:
                        if (!mCompletedStep.contains(FriendMessageType.REQUEST_FOR_CONTACT_LIST))
                        {
                            requestFriendList();
                            break;
                        }
                    case FriendMessageType.REQUEST_FOR_CONTACT_LIST:
                    case FriendMessageType.RESPONSE_ERROR:
                        if (0 == retry)
                        {
                            if (mFirstLogin)
                            {
                                //向小秘书发送消息
                                sendSecretaryMessage();
                            }
                            sendOnlinePresence();
                        }
                        if (FriendMessageType.REQUEST_FOR_CONTACT_LIST == action)
                        {
                            if (null == msg.obj)
                            {
                                mCompletedStep.remove(FriendMessageType.REQUEST_FOR_CONTACT_LIST);
                            }
                            else
                            {
                                requestPhoto((ArrayList<ContactInfoModel>) msg.obj);
                            }
                        }
                        retry++;
                        if (retry < MAX_RETRY_TIMES)
                        {
                            sendEmptyMessage(LoginMessageType.BEGIN_AFTER_LOGIN);
                        }
                        else
                        {
                            ((ILogic) mSettingsLogic).removeHandler(this);
                            ((ILogic) mGroupLogic).removeHandler(this);
                            ((ILogic) mFriendLogic).removeHandler(this);
                            ((ILogic) mContactLogic).removeHandler(this);
                            ((ILogic) mImLogic).removeHandler(this);
                        }
                        break;
                }
            }
        }
    }
    
    /**
     * 
     * 获取个人信息
     */
    private void requesrProfile()
    {
        Logger.i(TAG, "1.获取个人资料");
        mSettingsLogic.sendRequestPrivateProfile(null, true);
    }
    
    /**
     * 
     * 获取群信息
     */
    private void requestGroupList()
    {
        Logger.i(TAG, "2.获取群信息");
        mGroupLogic.getGroupListFromXmpp();
    }
    
    /**
     * 
     * 获取好友列表
     */
    private void requestFriendList()
    {
        Logger.i(TAG, "3.获取好友列表");
        mFriendLogic.getAllContactList(true, null);
    }
    
    /**
     * 
     * 给小秘书发送首次登入消息<BR>
     * 
     */
    private void sendSecretaryMessage()
    {
        Logger.i(TAG, "4.给小秘书发送首次登入消息");
        mImLogic.sendToSecretary();
    }
    
    /**
     * 设置设置模块的logic实现类
     * 
     * @param settingsLogic
     *            设置模块的logic实现类
     */
    public void setSettingsLogic(ISettingsLogic settingsLogic)
    {
        mSettingsLogic = settingsLogic;
    }
    
    /**
     * 
     * 设置群组logic
     * 
     * @param groupLogic
     *            群组logic
     */
    public void setGroupLogic(IGroupLogic groupLogic)
    {
        mGroupLogic = groupLogic;
    }
    
    /**
     * 
     * 设置好友模块的logic实现类
     * 
     * @param friendLogic
     *            好友模块的logic实现类
     */
    public void setFriendLogic(IFriendLogic friendLogic)
    {
        mFriendLogic = friendLogic;
    }
    
    /**
     * 
     * 设置通讯录模块的logic实现类
     * 
     * @param contactLogic
     *            通讯录模块的logic实现类
     */
    public void setContactLogic(IContactLogic contactLogic)
    {
        mContactLogic = contactLogic;
    }
    
    /**
     * 
     * 设置Im模块的logic实现类<BR>
     * 
     * @param imLogic
     *            ImLogic实现类
     */
    public void setImLogic(IImLogic imLogic)
    {
        mImLogic = imLogic;
    }
    
    //    /**
    //     * 
    //     * [一句话功能简述]<BR>
    //     * 第一次进入系统，默认插入会话信息到系统数据库MessageSession表中
    //     */
    //    private void addDefaultSessionData()
    //    {
    //        //插入如何建立圈子
    //        insertMessageToConversation(ConversationModel.CONVERSATIONTYPE_INIT_TIPS,
    //                ConversationModel.ID_VALUE_BE_FOUND,
    //                mContext.getString(R.string.init_tips_be_found_content));
    //        //插入如何找到好友
    //        insertMessageToConversation(ConversationModel.CONVERSATIONTYPE_INIT_TIPS,
    //                ConversationModel.ID_VALUE_FIND_FRIEND,
    //                mContext.getString(R.string.init_tips_find_friend_content));
    //        //插入如何找到群
    //        insertMessageToConversation(ConversationModel.CONVERSATIONTYPE_INIT_TIPS,
    //                ConversationModel.ID_VALUE_FIND_GROUP,
    //                mContext.getString(R.string.init_tips_find_group_content));
    //    }
    
    //    /**
    //     * 
    //     * [一句话功能简述]<BR>
    //     * 把信息插入到数据库的MessageSession表中
    //     * 
    //     * @param msgType
    //     * @param msgId
    //     * @param msgContent
    //     */
    //    private void insertMessageToConversation(int msgType, String msgId,
    //            String msgContent)
    //    {
    //        ConversationDbAdapter conversationDbAdapter = ConversationDbAdapter.getInstance(mContext);
    //        //获取当前的用户系统id
    //        String userSysId = FusionConfig.getInstance()
    //                .getAasResult()
    //                .getUserSysId();
    //        ConversationModel conversationModel = new ConversationModel();
    //        conversationModel.setLastMsgId(generateMsgId());
    //        conversationModel.setConversationId(msgId);
    //        conversationModel.setConversationType(msgType);
    //        //插入的消息类别为文本消息
    //        conversationModel.setLastMsgType(BaseMessageModel.MSGTYPE_TEXT);
    //        conversationModel.setLastMsgContent(msgContent);
    //        //插入消息的时间格式
    //        conversationModel.setLastTime(DateUtil.getCurrentDateString());
    //        conversationDbAdapter.insertConversation(userSysId, conversationModel);
    //    }
    
    //    /**
    //     * 
    //     * [一句话功能简述]<BR>
    //     * 生成唯一的消息id
    //     * 
    //     * @return
    //     */
    //    private String generateMsgId()
    //    {
    //        return UUID.randomUUID().toString().replaceAll("-", "");
    //    }
    
    /**
     * 发布在线状态<BR>
     * 发送在线状态后，Xmpp服务器开始推送离线消息
     */
    private void sendOnlinePresence()
    {
        PresenceData.PublishCmdData cmdData = new PresenceData.PublishCmdData();
        String jid = FusionConfig.getInstance().getAasResult().getUserID();
        cmdData.setFrom(UriUtil.buildXmppJid(jid));
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
        String publishResult = mServiceSender.executeCommand(BaseParams.PresenceParams.FAST_COM_PRESENCE_ID,
                BaseParams.PresenceParams.FAST_PRESENCE_CMD_PUBLISH,
                cmdDataString);
        Logger.i("LoginLogic", "publish online cmd xml:" + publishResult);
        
    }
    
    /**
     * 
     * 获取好友头像<BR>
     * [功能详细描述]
     */
    private void requestPhoto(ArrayList<ContactInfoModel> contactInfoList)
    {
        Logger.i(TAG, "6.获取好友头像");
        final FaceThumbnailDbAdapter faceThumbnailDbAdapter = FaceThumbnailDbAdapter.getInstance(mContext);
        if (contactInfoList != null)
        {
            for (ContactInfoModel contactInfo : contactInfoList)
            {
                final String faceUrl = contactInfo.getFaceUrl();
                final String faceId = contactInfo.getFriendUserId();
                // URL不为空 && 不是系统头像 && byte[]为空，则从服务器拉取
                if (!StringUtil.isNullOrEmpty(faceUrl)
                        && !SystemFacesUtil.isSystemFaceUrl(faceUrl)
                        && null == contactInfo.getFaceBytes())
                {
                    new FaceManager().loadFaceIcon(faceId,
                            faceUrl,
                            new IHttpListener()
                            {
                                /**
                                 * 网络交互监听的回调方法<BR
                                 * 
                                 * @param action
                                 *            监听动作
                                 * @param response
                                 *            响应的Response
                                 * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onResult
                                 *      (int,
                                 *      com.huawei.basic.android.im.component.net.http.Response)
                                 */
                                @Override
                                public void onResult(int action,
                                        Response response)
                                {
                                    if (ResponseCode.Succeed == response.getResponseCode()
                                            && null != response.getByteData()
                                            && 0 < response.getByteData().length)
                                    {
                                        
                                        if (faceThumbnailDbAdapter.queryByFaceId(faceId) != null)
                                        {
                                            faceThumbnailDbAdapter.updateByFaceId(faceId,
                                                    new FaceThumbnailModel(
                                                            faceId,
                                                            faceUrl,
                                                            response.getByteData()));
                                        }
                                        else
                                        {
                                            faceThumbnailDbAdapter.insertFaceThumbnail(new FaceThumbnailModel(
                                                    faceId, faceUrl,
                                                    response.getByteData()));
                                        }
                                        
                                    }
                                }
                                
                                /**
                                 * 进度变化回调<BR>
                                 * 
                                 * @param isInProgress
                                 *            true为进度开始，false为进度结束
                                 * @see com.huawei.basic.android.im.component.net.http.IHttpListener#onProgress(boolean)
                                 */
                                @Override
                                public void onProgress(boolean isInProgress)
                                {
                                    
                                }
                            });
                }
            }
        }
    }
    
    /**
     * 重新登录
     * 
     * @param delay
     *            延时重新登录
     * @see com.huawei.basic.android.im.component.service.core.ILogin#reLogin(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void reLogin(long delay)
    {
        mServiceSender.login(delay);
    }
    
    /**
     * 
     * 载入国家文件，解析XML
     * 
     * @param context
     *            上下文
     * @return 国家码列表
     * @see com.huawei.basic.android.im.logic.login.ILoginLogic#getCountryCode(android.content.Context)
     */
    
    @Override
    public List<CountryItemModel> getCountryCode(Context context)
    {
        mCountryLists = new ArrayList<CountryItemModel>();
        //所有国家码
        String countryCode = context.getResources()
                .getString(R.string.country_code);
        //分割所有的国家码
        String[] singleCodeAll = countryCode.trim().split(",");
        for (int i = 0; i < singleCodeAll.length; i++)
        {
            CountryItemModel mCountryItemModel;
            //分割单个的国家码获取具体的名称
            String[] singleCode = singleCodeAll[i].split(":");
            
            mCountryItemModel = new CountryItemModel();
            //获取国家码
            mCountryItemModel.setCountryCode(singleCode[0]);
            //获取国家名字
            mCountryItemModel.setChName(singleCode[1]);
            //获取国家拼音
            mCountryItemModel.setSimplePinyin(singleCode[2]);
            
            mCountryItemModel.setInitialName(HanziToPinyin.getInstance()
                    .getSimpleSortKey(singleCode[1])
                    .replaceAll(" ", ""));
            mCountryLists.add(mCountryItemModel);
        }
        return mCountryLists;
    }
}
