/*
 * 文件名: SettingsLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Adler32;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.os.Handler;

import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.RegisterMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactInfoColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.component.upload.http.UploadContentInfo;
import com.huawei.basic.android.im.component.upload.http.UploadContentInfo.MimeType;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.AccountDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.ConversationDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.MyAppDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.SysAppInfoAdapter;
import com.huawei.basic.android.im.logic.adapter.db.UserConfigDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.adapter.http.RegisterManager;
import com.huawei.basic.android.im.logic.adapter.http.UpdateManager;
import com.huawei.basic.android.im.logic.adapter.http.UserManager;
import com.huawei.basic.android.im.logic.friend.PresenceXmppSender;
import com.huawei.basic.android.im.logic.model.AccountModel;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.SysAppInfoModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.logic.upload.ContentUploader;
import com.huawei.basic.android.im.logic.upload.IUploadListener;
import com.huawei.basic.android.im.logic.upload.ReceiverType;
import com.huawei.basic.android.im.logic.upload.UploadFileForURLResponse;
import com.huawei.basic.android.im.logic.upload.UploadFileForURLResponse.UploadFileForURLResult;
import com.huawei.basic.android.im.logic.upload.UploadParam;
import com.huawei.basic.android.im.logic.upload.UploadType;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 设置模块的logic实现类<BR>
 * 
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012]
 */
public class SettingsLogic extends BaseLogic implements ISettingsLogic
{
    /**
     * 账号字段
     */
    public static final String PASSPORT = "passport";
    
    /**
     * Debug Tag
     */
    private static final String TAG = "SettingsLogic";
    
    /**
     * 我的应用最大数量
     */
    private static final int MAX_APP_NUM = 12;
    
    /**
     * 系统 Context 对象
     */
    private Context mContext;
    
    /**
     * 个人信息对象
     */
    private ContactInfoModel mUser;
    
    /**
     * 联系人实体对象
     */
    private ContactInfoModel mContactInfo;
    
    /**
     * {@link PresenceXmppSender}
     */
    private PresenceXmppSender mPresenceXmppSender;
    
    /**
     * 应用信息数据库管理。
     */
    private SysAppInfoAdapter mSysAppInfoAdapter;
    
    /**
     * 我的应用管理
     */
    private MyAppDbAdapter mMyAppDbAdapter;
    
    /**
     * 用户配置信息数据库管理。
     */
    private UserConfigDbAdapter mUserConfigDbAdapter;
    
    /**
     * 联系人信息数据库管理
     */
    private ContactInfoDbAdapter mContactInfoDbAdapter;
    
    /**
     * 构造方法
     * 
     * @param context
     *            context 系统 Context 对象
     * @param serviceSender
     *            serviceSender
     */
    public SettingsLogic(Context context, IServiceSender serviceSender)
    {
        this.mContext = context;
        mPresenceXmppSender = new PresenceXmppSender(serviceSender);
        mSysAppInfoAdapter = SysAppInfoAdapter.getInstance(context);
        mMyAppDbAdapter = MyAppDbAdapter.getInstance(context);
        mUserConfigDbAdapter = UserConfigDbAdapter.getInstance(context);
        mContactInfoDbAdapter = ContactInfoDbAdapter.getInstance(context);
    }
    
    /**
     * 查询个人资料
     * 
     * @param timeStamp
     *            发送的数据
     * @param isForceUpdate
     *            强制更新
     */
    @Override
    public void sendRequestPrivateProfile(String timeStamp,
            boolean isForceUpdate)
    {
        if (!isForceUpdate)
        {
            mContactInfo = queryMyProfile(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId());
        }
        
        if (null == mContactInfo || isForceUpdate)
        {
            mUser = new ContactInfoModel();
            new UserManager().requestPrivateProfile(timeStamp,
                    new IHttpListener()
                    {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void onResult(int action, Response response)
                        {
                            if (ResponseCode.Succeed == response.getResponseCode())
                            {
                                if (response.getResultCode() == 0)
                                {
                                    Map<String, Object> modelMap1 = new HashMap<String, Object>();
                                    modelMap1 = (Map<String, Object>) response.getObj();
                                    mUser = (ContactInfoModel) modelMap1.get("PrivateProfile");
                                    UserConfigModel friendPrivacy = (UserConfigModel) modelMap1.get("FriendPrivacy");
                                    UserConfigModel autoConfirmFriend = (UserConfigModel) modelMap1.get("AutoConfirmFriend");
                                    UserConfigModel privacy = (UserConfigModel) modelMap1.get("Privacy");
                                    //获取隐私设置配置信息保存到数据库中
                                    if (null == configQueryByKey(UserConfigModel.FRIEND_PRIVACY)
                                            && null == configQueryByKey(UserConfigModel.FRIEND_PRIVACY))
                                    {
                                        mUserConfigDbAdapter.insertUserConfig(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                friendPrivacy);
                                        mUserConfigDbAdapter.insertUserConfig(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                autoConfirmFriend);
                                    }
                                    else
                                    {
                                        mUserConfigDbAdapter.updateByKey(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                UserConfigModel.FRIEND_PRIVACY,
                                                friendPrivacy);
                                        mUserConfigDbAdapter.updateByKey(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                UserConfigModel.AUTO_CONFIRM_FRIEND,
                                                autoConfirmFriend);
                                    }
                                    //获取隐私资料配置信息保存到数据库中
                                    if (null == configQueryByKey(UserConfigModel.PRIVACY))
                                    {
                                        mUserConfigDbAdapter.insertUserConfig(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                privacy);
                                    }
                                    else
                                    {
                                        mUserConfigDbAdapter.updateByKey(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                UserConfigModel.PRIVACY,
                                                privacy);
                                    }
                                    
                                    if (null == queryMyProfile(FusionConfig.getInstance()
                                            .getAasResult()
                                            .getUserSysId()))
                                    {
                                        //将查询的个人资料插入数据库中
                                        mContactInfoDbAdapter.insertContactInfo(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                mUser);
                                        //头像
                                        updateUserHead(mUser);
                                    }
                                    else
                                    {
                                        //更新到本地数据库
                                        mContactInfoDbAdapter.updateByFriendSysId(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                FusionConfig.getInstance()
                                                        .getAasResult()
                                                        .getUserSysId(),
                                                mUser);
                                    }
                                    sendMessage(SettingsMessageType.GET_MYPROFILE_SUCCEED,
                                            mUser);
                                }
                                else
                                {
                                    sendMessage(SettingsMessageType.GET_MYPROFILE_FAILED,
                                            response.getResultCode());
                                }
                            }
                            else
                            {
                                sendMessage(SettingsMessageType.CONNECT_FAILED,
                                        response.getResponseCode());
                            }
                        }
                        
                        @Override
                        public void onProgress(boolean isInProgress)
                        {
                        }
                    });
        }
        else
        {
            //查询头像
            FaceThumbnailModel faceMode = FaceThumbnailDbAdapter.getInstance(mContext)
                    .queryByFaceId(FusionConfig.getInstance()
                            .getAasResult()
                            .getUserID());
            if (null != faceMode)
            {
                //赋值给Model对象
                mContactInfo.setFaceUrl(faceMode.getFaceUrl());
                mContactInfo.setFaceBytes(faceMode.getFaceBytes());
            }
            mUser = mContactInfo;
            //给各组件赋值
            sendMessage(SettingsMessageType.GET_MYPROFILE_SUCCEED, mUser);
        }
        
    }
    
    /**
     * 更新个人资料 发送的数据
     * 
     * @param contactInfoModel
     *            contactInfoModel
     */
    @Override
    public void sendUpdatePrivateProfile(final ContactInfoModel contactInfoModel)
    {
        new UserManager().updatePrivateProfile(contactInfoModel,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            if (Common.RESULT_CODE_SUCCESS == response.getResultCode())
                            {
                                //将查询的个人资料插入数据库中
                                String lastUpdate = (String) response.getObj();
                                contactInfoModel.setLastUpdate(lastUpdate);
                                //存储到本地数据库
                                ContactInfoDbAdapter.getInstance(mContext)
                                        .updateByFriendSysId(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                FusionConfig.getInstance()
                                                        .getAasResult()
                                                        .getUserSysId(),
                                                contactInfoModel);
                                
                                //头像
                                updateUserHead(contactInfoModel);
                                
                                String userId = FusionConfig.getInstance()
                                        .getAasResult()
                                        .getUserID();
                                
                                //更新个人资料成功后，向好友发送presence
                                mPresenceXmppSender.publishPresence(userId,
                                        contactInfoModel.getSignature(),
                                        contactInfoModel.getFaceUrl(),
                                        contactInfoModel.getNickName());
                                
                                sendEmptyMessage(SettingsMessageType.MSG_TYPE_UPDATE_MYPROFILE_SUCCEED);
                            }
                            else
                            {
                                sendMessage(SettingsMessageType.MSG_TYPE_UPDATE_MYPROFILE_FAILED,
                                        response.getResultCode());
                            }
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
        
    }
    
    /**
     * 更新个人头像
     * 
     * @param infoModel
     *            个人资料信息
     */
    private void updateUserHead(ContactInfoModel infoModel)
    {
        //头像
        FaceThumbnailModel faceModel = new FaceThumbnailModel();
        faceModel.setFaceUrl(infoModel.getFaceUrl());
        faceModel.setFaceBytes(infoModel.getFaceBytes());
        faceModel.setFaceId(FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        //更新头像数据
        FaceThumbnailDbAdapter.getInstance(mContext).updateOrInsert(faceModel);
    }
    
    /**
     * 更新个人资料 发送的数据
     * 
     * @param contactInfoModel
     *            contactInfoModel
     */
    @Override
    public void sendUpdateSignature(final ContactInfoModel contactInfoModel)
    {
        new UserManager().updatePrivateSignature(contactInfoModel,
                new IHttpListener()
                {
                    @Override
                    public void onResult(int action, Response response)
                    {
                        ResponseCode retCode = response.getResponseCode();
                        if (retCode == ResponseCode.Succeed)
                        {
                            if (response.getResultCode() == 0)
                            {
                                //将查询的个人资料插入数据库中
                                String lastUpdate = (String) response.getObj();
                                contactInfoModel.setLastUpdate(lastUpdate);
                                //存储到本地数据库
                                ContactInfoDbAdapter.getInstance(mContext)
                                        .updateByFriendSysId(FusionConfig.getInstance()
                                                .getAasResult()
                                                .getUserSysId(),
                                                FusionConfig.getInstance()
                                                        .getAasResult()
                                                        .getUserSysId(),
                                                contactInfoModel);
                                sendEmptyMessage(SettingsMessageType.MSG_TYPE_UPDATE_SIGNATURE_SUCCEED);
                            }
                            else
                            {
                                sendMessage(SettingsMessageType.MSG_TYPE_UPDATE_SIGNATURE_FAILED,
                                        response.getResultCode());
                            }
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
        
    }
    
    /**
     * 更新个人资料 发送的数据 updatePrivateProfilePrivacy [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param autoConfirmFriendStr
     *            autoConfirmFriendStr
     * @param friendPrivacyStr
     *            friendPrivacyStr
     * @see
     */
    @Override
    public void sendUpdateProfilePrivacy(final String autoConfirmFriendStr,
            final String friendPrivacyStr)
    {
        new UserManager().updatePrivateProfilePrivacy(autoConfirmFriendStr,
                friendPrivacyStr,
                new IHttpListener()
                {
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (ResponseCode.Succeed == response.getResponseCode())
                        {
                            if (Common.RESULT_CODE_SUCCESS == response.getResultCode())
                            {
                                UserConfigModel userConfigModel1 = configQueryByKey(UserConfigModel.FRIEND_PRIVACY);
                                UserConfigModel userConfigModel2 = configQueryByKey(UserConfigModel.AUTO_CONFIRM_FRIEND);
                                UserConfigModel config = new UserConfigModel();
                                UserConfigModel config1 = new UserConfigModel();
                                config.setKey(UserConfigModel.FRIEND_PRIVACY);
                                config.setValue(friendPrivacyStr);
                                config1.setKey(UserConfigModel.AUTO_CONFIRM_FRIEND);
                                config1.setValue(autoConfirmFriendStr);
                                //隐私设置更新成功，则将相应数据保存在数据库中
                                if (null == userConfigModel1)
                                {
                                    UserConfigDbAdapter.getInstance(mContext)
                                            .insertUserConfig(FusionConfig.getInstance()
                                                    .getAasResult()
                                                    .getUserSysId(),
                                                    config);
                                }
                                else
                                {
                                    UserConfigDbAdapter.getInstance(mContext)
                                            .updateByKey(FusionConfig.getInstance()
                                                    .getAasResult()
                                                    .getUserSysId(),
                                                    UserConfigModel.FRIEND_PRIVACY,
                                                    config);
                                    
                                }
                                if (null == userConfigModel2)
                                {
                                    UserConfigDbAdapter.getInstance(mContext)
                                            .insertUserConfig(FusionConfig.getInstance()
                                                    .getAasResult()
                                                    .getUserSysId(),
                                                    config1);
                                }
                                else
                                {
                                    UserConfigDbAdapter.getInstance(mContext)
                                            .updateByKey(FusionConfig.getInstance()
                                                    .getAasResult()
                                                    .getUserSysId(),
                                                    UserConfigModel.AUTO_CONFIRM_FRIEND,
                                                    config1);
                                }
                                sendEmptyMessage(SettingsMessageType.UPDATE_MYPROFILE_PRIVACY_SUCCEED);
                            }
                            else
                            {
                                sendMessage(SettingsMessageType.UPDATE_MYPROFILE_PRIVACY_FAILED,
                                        response.getResultCode());
                            }
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.ADD_FRIENDS_CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 更新个人资料 privacy
     * 
     * @param privacy
     *            数据
     */
    @Override
    public void sendUpdateProfilePrivacy(final String privacy)
    {
        HashMap<String, Object> sendData = new HashMap<String, Object>();
        sendData.put(UserConfigModel.PRIVACY, privacy);
        new UserManager().updateProfilePrivacyMaterial(privacy,
                new IHttpListener()
                {
                    @Override
                    public void onResult(int action, Response response)
                    {
                        // 接口调用成功返回
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            if (Common.RESULT_CODE_SUCCESS == response.getResultCode())
                            {
                                UserConfigModel userConfigModel = configQueryByKey(UserConfigModel.PRIVACY);
                                UserConfigModel config = new UserConfigModel();
                                config.setKey(UserConfigModel.PRIVACY);
                                config.setValue(privacy);
                                //如果数据库中数据为空则添加记录
                                if (null == userConfigModel)
                                {
                                    UserConfigDbAdapter.getInstance(mContext)
                                            .insertUserConfig(FusionConfig.getInstance()
                                                    .getAasResult()
                                                    .getUserSysId(),
                                                    config);
                                }
                                else
                                {
                                    UserConfigDbAdapter.getInstance(mContext)
                                            .updateByKey(FusionConfig.getInstance()
                                                    .getAasResult()
                                                    .getUserSysId(),
                                                    UserConfigModel.PRIVACY,
                                                    config);
                                }
                                sendEmptyMessage(SettingsMessageType.UPDATE_PRIVACY_MATERIAL_SUCCEED);
                            }
                            else
                            {
                                sendMessage(SettingsMessageType.UPDATE_PRIVACY_MATERIAL_FAILED,
                                        response.getResultCode());
                            }
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.OPEN_PROFILE_CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * sendModifyPassword [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param oldPassword
     *            oldPassword
     * @param newPassword
     *            newPassword
     * @see
     */
    @Override
    public void sendModifyPassword(String oldPassword, final String newPassword)
    {
        new UserManager().requestModifyPassword(oldPassword,
                newPassword,
                new IHttpListener()
                {
                    /**
                     * 
                     * 通过该方法获取response数据并反馈UI层<BR>
                     * [功能详细描述]
                     * 
                     * @param action
                     *            action
                     * @param response
                     *            response
                     * @see
                     */
                    @Override
                    public void onResult(int action, Response response)
                    {
                        ResponseCode retCode = response.getResponseCode();
                        if (retCode == ResponseCode.Succeed)
                        {
                            AccountModel account = AccountDbAdapter.getInstance(mContext)
                                    .queryByUserSysId(FusionConfig.getInstance()
                                            .getAasResult()
                                            .getUserSysId());
                            account.setPassword(DecodeUtil.encrypt(FusionConfig.getInstance()
                                    .getAasResult()
                                    .getUserID(),
                                    newPassword));
                            AccountDbAdapter.getInstance(mContext)
                                    .updateByLoginAccount(account);
                            sendMessage(SettingsMessageType.MODIFY_PASSWORD,
                                    response.getObj());
                            
                        }
                        
                        else if (retCode == ResponseCode.NetworkError
                                || retCode == ResponseCode.Timeout)
                        {
                            sendEmptyMessage(SettingsMessageType.MODIFY_PASSWORD);
                        }
                        
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
        
    }
    
    /**
     * 检查更新 发送的数据 RequestCheckUpdate [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param version
     *            version
     * @param width
     *            width
     * @param height
     *            height
     * @see
     */
    @Override
    public void sendRequestCheckUpdate(String version, String width,
            String height)
    {
        new UpdateManager().requestCheckUpdate(version,
                width,
                height,
                new IHttpListener()
                {
                    
                    @Override
                    public void onResult(int action, Response response)
                    {
                        ResponseCode retCode = response.getResponseCode();
                        if (retCode == ResponseCode.Succeed)
                        {
                            sendMessage(SettingsMessageType.MSG_TYPE_CHECK_UPDATE_VERSION_SUCCEED,
                                    response.getObj());
                        }
                        else if (response.getResponseCode() != Response.ResponseCode.Succeed
                                || response.getObj() == null)
                        {
                            sendEmptyMessage(SettingsMessageType.MSG_TYPE_CHECK_UPDATE_VERSION_FAILED);
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 绑定邮箱 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param emailAdrr
     *            emailAdrr
     * @param nickName
     *            nickName
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#bindMail(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void bindMail(String emailAdrr, String nickName)
    {
        new UserManager().sendRequestBindMail(emailAdrr,
                nickName,
                new IHttpListener()
                {
                    
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                            String retCode = (String) responseObj.get(UserManager.RET_CODE);
                            sendMessage(SettingsMessageType.BIND_EMAIL, retCode);
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 获得验证码 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param phoneNumber
     *            phoneNumber
     * @param operType
     *            operType
     */
    @Override
    public void getMsisdnVerifyCode(String phoneNumber, String operType)
    {
        new UserManager().sendRequestGetMsisdnVerifyCode(phoneNumber,
                operType,
                new IHttpListener()
                {
                    
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                            //演示版本从服务器返回中直接获取验证码 added by zhanggj 20120509
                            //                            String retCode = (String) responseObj.get(UserManager.RET_CODE);
                            //                            sendMessage(SettingsMessageType.GET_MSISDN_VERIFY_CODE,
                            //                                    retCode);
                            sendMessage(SettingsMessageType.GET_MSISDN_VERIFY_CODE,
                                    responseObj);
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 绑定手机 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param phoneNum
     *            phoneNum
     * @param verifyCode
     *            verifyCode
     */
    @Override
    public void bindPhone(String phoneNum, String verifyCode)
    {
        new UserManager().sendRequestBindPhone(phoneNum,
                verifyCode,
                new IHttpListener()
                {
                    
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                            String retCode = (String) responseObj.get(UserManager.RET_CODE);
                            sendMessage(SettingsMessageType.BIND_PHONE, retCode);
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
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
     * 解绑手机 [功能详细描述]
     * 
     * @param opreType
     *            opreType
     * @param verifyCode
     *            verifyCode
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#unBindPhone(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void unBindPhone(String opreType, String verifyCode)
    {
        new UserManager().sendRequestUnBindPhone(opreType,
                verifyCode,
                new IHttpListener()
                {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                            String retCode = (String) responseObj.get(UserManager.RET_CODE);
                            sendMessage(SettingsMessageType.UNBIND, retCode);
                        }
                        else
                        {
                            sendMessage(SettingsMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                    }
                });
    }
    
    /**
     * 解绑邮箱
     * 
     * @param operType
     *            operType
     */
    @Override
    public void unBindMail(String operType)
    {
        new UserManager().sendUnBindMail(operType, new IHttpListener()
        {
            
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(int action, Response response)
            {
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                    String retCode = (String) responseObj.get(UserManager.RET_CODE);
                    sendMessage(SettingsMessageType.UNBIND, retCode);
                }
                else
                {
                    sendMessage(SettingsMessageType.CONNECT_FAILED,
                            response.getResponseCode());
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
            }
        });
    }
    
    /**
     * 重置密码
     * 
     * @param number
     *            手机号码
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#resetPassword(java.lang.String)
     */
    
    @Override
    public void resetPasswordFromNumber(String number)
    {
        new UserManager().resetPasswordFromNumber(number, new IHttpListener()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(int action, Response response)
            {
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                    String retCode = (String) responseObj.get(UserManager.RET_CODE);
                    sendMessage(SettingsMessageType.CHECK_MOBILE_BIND, retCode);
                }
                else
                {
                    
                    //连接服务器失败
                    sendMessage(RegisterMessageType.CONNECT_FAILED,
                            response.getResponseCode());
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                
            }
            
        });
    }
    
    /**
     * 重置密码
     * 
     * @param email
     *            邮箱
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#resetPassword(java.lang.String)
     */
    
    @Override
    public void resetPasswordFromEmail(String email)
    {
        new UserManager().resetPasswordFromEmail(email,

        new IHttpListener()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(int action, Response response)
            {
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                    String retCode = (String) responseObj.get(UserManager.RET_CODE);
                    sendMessage(SettingsMessageType.RESET_PASSWORD, retCode);
                }
                else
                {
                    
                    //连接服务器失败
                    sendMessage(RegisterMessageType.CONNECT_FAILED,
                            response.getResponseCode());
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                
            }
        });
    }
    
    /**
     * 重置密码
     * 
     * @param number
     *            号码
     * @param phoneType
     *            号码类型
     * @param verifyCode
     *            验证码
     * @param password
     *            密码
     * @see
     */
    
    @Override
    public void resetPasswordFromNumberAndVerify(String number,
            String verifyCode, String password)
    {
        new UserManager().resetPasswordFromNumberAndVerify(number,

        verifyCode, password, new IHttpListener()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(int action, Response response)
            {
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                    String retCode = (String) responseObj.get(UserManager.RET_CODE);
                    sendMessage(SettingsMessageType.RESET_PASSWORD, retCode);
                }
                else
                {
                    
                    //连接服务器失败
                    sendMessage(RegisterMessageType.CONNECT_FAILED,
                            response.getResponseCode());
                }
            }
            
            @Override
            public void onProgress(boolean isInProgress)
            {
                
            }
        });
    }
    
    /**
     * 重置密码
     * 
     * @param number
     *            手机号
     * @param openType
     *            解绑类型
     * @see
     */
    
    @Override
    public void resetPasswordFromNumberWithType(String number, String openType)
    {
        new UserManager().resetPasswordFromNumberWithType(number,
                openType,
                new IHttpListener()
                {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(int action, Response response)
                    {
                        if (response.getResponseCode() == ResponseCode.Succeed)
                        {
                            HashMap<String, Object> responseObj = (HashMap<String, Object>) response.getObj();
                            //演示版本从服务器返回中直接获取验证码 added by zhanggj 20120509
                            //                            String retCode = (String) responseObj.get(UserManager.RET_CODE);
                            //                            sendMessage(SettingsMessageType.GET_MSISDN_VERIFY_CODE,
                            //                                    retCode);
                            sendMessage(SettingsMessageType.GET_MSISDN_VERIFY_CODE,
                                    responseObj);
                        }
                        else
                        {
                            
                            //连接服务器失败
                            sendMessage(RegisterMessageType.CONNECT_FAILED,
                                    response.getResponseCode());
                        }
                    }
                    
                    @Override
                    public void onProgress(boolean isInProgress)
                    {
                        
                    }
                });
    }
    
    /**
     * 查询个人资料信息
     * 
     * @param userSysId
     *            用户系统ID
     * @return 个人资料对象
     */
    @Override
    public ContactInfoModel queryMyProfile(String userSysId)
    {
        return mContactInfoDbAdapter.queryMyProfile(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
    }
    
    /**
     * 查询数据库操作
     * 
     * @param key
     *            key值
     * @return 个人配置对象
     */
    @Override
    public UserConfigModel configQueryByKey(String key)
    {
        UserConfigModel model = UserConfigDbAdapter.getInstance(mContext)
                .queryByKey(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        key);
        return model;
        
    }
    
    /**
     * 查询数据库有记录执行更新，没记录执行添加
     * 
     * @param userId
     *            userId
     * @param key
     *            key
     * @param value
     *            value
     */
    @Override
    public void addConfig(String userId, String key, String value)
    {
        UserConfigModel config = new UserConfigModel();
        config.setKey(key);
        config.setValue(value);
        UserConfigDbAdapter configDbAdapter = UserConfigDbAdapter.getInstance(mContext);
        if (configDbAdapter.queryByKey(userId, key) == null)
        {
            configDbAdapter.insertUserConfig(userId, config);
        }
        else
        {
            configDbAdapter.updateByKey(userId, key, config);
        }
    }
    
    /**
     * 检测邮箱是否绑定
     * 
     * @param email
     *            邮箱
     * @see com.huawei.basic.android.im.logic.login.IRegisterLogic#checkEmailBind(java.lang.String)
     */
    @Override
    public void checkEmailBind(final String email)
    {
        new RegisterManager().checkEmailBind(email, new IHttpListener()
        {
            @Override
            public void onResult(int action, Response response)
            {
                
                //联网成功
                if (response.getResponseCode() == ResponseCode.Succeed)
                {
                    
                    //如果retCode為0，表明邮箱未绑定,去获取验证码
                    if (response.getResultCode() == 0)
                    {
                        sendEmptyMessage(RegisterMessageType.CHECK_EMAIL_BIND_SUCCESS);
                    }
                    
                    //如果retCode不是0，表明邮箱已经被绑定
                    else
                    {
                        sendMessage(RegisterMessageType.CHECK_EMAIL_BIND_FAILED,
                                response.getResultCode());
                    }
                }
                else
                {
                    
                    //连接服务器失败
                    sendMessage(RegisterMessageType.CONNECT_FAILED,
                            response.getResponseCode());
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
     * 从服务器获取应用并且应用插入表中<BR>
     * 
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#getAppFromeServer()
     */
    @Override
    public void getAppFromeServer()
    {
        final String sysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        UserConfigModel userConfigModel = mUserConfigDbAdapter.queryByKey(sysId,
                UserConfigModel.SYS_APP_INFO_VERSION);
        final String version = null == userConfigModel ? null
                : userConfigModel.getValue();
        
        final String mTime = DateUtil.FRIEND_MANAGER_FORMATTER.format(new Date());
        UserConfigModel userConfigModel1 = mUserConfigDbAdapter.queryByKey(sysId,
                UserConfigModel.APP_TIMESTAMP);
        
        if (null == userConfigModel1
                || !mTime.equals(userConfigModel1.getValue()))
        {
            new UserManager().getApp(version, new IHttpListener()
            {
                @SuppressWarnings("unchecked")
                @Override
                public void onResult(int action, Response response)
                {
                    HashMap<String, Object> responseData = (HashMap<String, Object>) response.getObj();
                    String mUserSysId = FusionConfig.getInstance()
                            .getAasResult()
                            .getUserSysId();
                    if (response.getResponseCode() == ResponseCode.Succeed)
                    {
                        /**
                         * 先清空数据库
                         */
                        mSysAppInfoAdapter.deleteAll();
                        addAppInfo((ArrayList<SysAppInfoModel>) responseData.get(UserManager.SYSAPP));
                        addConfig(sysId,
                                UserConfigModel.SYS_APP_INFO_VERSION,
                                (String) responseData.get(UserConfigModel.SYS_APP_INFO_VERSION));
                        sendEmptyMessage(SettingsMessageType.GET_APP_SUCCESS);
                    }
                    else
                    {
                        addConfig(mUserSysId,
                                UserConfigModel.APP_TIMESTAMP,
                                mTime);
                    }
                }
                
                @Override
                public void onProgress(boolean isInProgress)
                {
                    
                }
            });
        }
        
    }
    
    /**
     * 
     * 将获取的应用插入系统应用表中<BR>
     * 
     * @param appList获取的应用列表
     */
    private void addAppInfo(ArrayList<SysAppInfoModel> appList)
    {
        String mUserSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        if (null != appList)
        {
            Logger.d("XMM", "SysAppInfoModel datas = " + appList.size());
            for (SysAppInfoModel sysAppInfoModel : appList)
            {
                SysAppInfoModel info = mSysAppInfoAdapter.queryByAppId(mUserSysId,
                        sysAppInfoModel.getAppId(),
                        sysAppInfoModel.getType());
                FaceManager.updateFace(mContext,
                        sysAppInfoModel.getAppId(),
                        sysAppInfoModel.getIconUrl());
                if (info != null)
                {
                    mSysAppInfoAdapter.updateByAppId(mUserSysId,
                            sysAppInfoModel.getAppId(),
                            sysAppInfoModel);
                }
                else
                {
                    mSysAppInfoAdapter.insert(mUserSysId, sysAppInfoModel);
                    Logger.d("XMM",
                            "insert appInfo = " + sysAppInfoModel.getIconUrl());
                }
            }
        }
    }
    
    /**
     * 
     * 根据应用类型从数据库获取系统应用<BR>
     * [功能详细描述]
     * 
     * @param type
     *            应用类型
     * @return SysAppInfoModel列表
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#getSysAppInfoByType(int)
     */
    public ArrayList<SysAppInfoModel> getSysAppInfoByType(int type)
    {
        return (ArrayList<SysAppInfoModel>) mSysAppInfoAdapter.queryAll(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                type);
    }
    
    /**
     * 
     * 从数据库获取所有系统应用<BR>
     * [功能详细描述]
     * 
     * @return SysAppInfoModel列表
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#getAllSysAppInfo()
     */
    public ArrayList<SysAppInfoModel> getAllSysAppInfo()
    {
        return (ArrayList<SysAppInfoModel>) mSysAppInfoAdapter.queryAll();
    }
    
    /**
     * 
     * 从数据库获取我的应用<BR>
     * 
     * @param userSysId系统用户Id
     * @return 应用列表
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#getMyAppInfoFromDB(java.lang.String)
     */
    public ArrayList<SysAppInfoModel> getMyAppInfoFromDB()
    {
        return (ArrayList<SysAppInfoModel>) mMyAppDbAdapter.queryAll(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
    }
    
    /**
     * 
     * 删除应用<BR>
     * 
     * @param appId
     *            应用Id
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#deleteByAppId(java.lang.String)
     */
    public void deleteByAppId(String appId)
    {
        int result = mMyAppDbAdapter.deleteByAppId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), appId);
        if (result == -1)
        {
            sendEmptyMessage(SettingsMessageType.DELETE_APP_FAILED);
        }
        else
        {
            sendEmptyMessage(SettingsMessageType.DELETE_APP_SUCCESS);
        }
    }
    
    /**
     * 
     * 获取到待添加应用列表数据<BR>
     * [功能详细描述]
     * 
     * @return SysAppInfoModel列表
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#getAddedData()
     */
    public ArrayList<SysAppInfoModel> getAddedData()
    {
        ArrayList<SysAppInfoModel> addedAppInfoModelList = getMyAppInfoFromDB();
        ArrayList<SysAppInfoModel> allAppInfoModelList = getAllSysAppInfo();
        if (allAppInfoModelList != null)
        {
            if (addedAppInfoModelList != null)
            {
                for (SysAppInfoModel model : addedAppInfoModelList)
                {
                    for (int i = allAppInfoModelList.size() - 1; i >= 0; i--)
                    {
                        Logger.d(TAG, "" + i + " " + i);
                        if (StringUtil.equals(allAppInfoModelList.get(i)
                                .getAppId(), String.valueOf(model.getAppId())))
                        {
                            Logger.d(TAG, "---------------->>>Remove!!!");
                            allAppInfoModelList.remove(i);
                            break;
                        }
                    }
                }
                
            }
        }
        
        return allAppInfoModelList;
    }
    
    /**
     * 
     * 插入我的应用表<BR>
     * [功能详细描述]
     * 
     * @param appId
     *            应用Id
     * @return long 结果值
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#insertMyApp(java.lang.String)
     */
    public long insertMyApp(String appId)
    {
        return mMyAppDbAdapter.insert(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), appId);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param mIds
     *            id
     * @see com.huawei.basic.android.im.logic.settings.ISettingsLogic#addAppToDB(java.util.ArrayList)
     */
    public void addAppToDB(ArrayList<Integer> mIds)
    {
        // 发送添加应用的请求
        // 发送请求前，先判断是否已超数额（超过则提示）
        ArrayList<SysAppInfoModel> addedAppInfoModelList = getMyAppInfoFromDB();
        int size = 0;
        if (addedAppInfoModelList == null)
        {
            size = 0;
        }
        else
        {
            size = addedAppInfoModelList.size();
        }
        if (MAX_APP_NUM < size + mIds.size())// 超额判断
        {
            sendEmptyMessage(SettingsMessageType.EXCESS_MAXNUM);
        }
        else if (mIds.size() == 0)
        {
            sendEmptyMessage(SettingsMessageType.NO_APP);
        }
        else
        // 未超额则发送请求
        {
            Logger.d(TAG, "size = " + mIds.size());
            // 添加应用
            for (int i = mIds.size() - 1; i >= 0; i--)
            {
                Logger.d(TAG, "---------------->inser ; i = " + i);
                int id = mIds.get(i);
                // 添加到我的应用表中去
                insertMyApp("" + id);
            }
            sendEmptyMessage(SettingsMessageType.CAN_INSERT);
        }
    }
    
    /**
     * 上传用户头像<BR>
     * 
     * @param fileName
     *            文件名称
     * @param photoBytes
     *            头像的byte数组
     */
    public void uploadUserFace(String fileName, byte[] photoBytes)
    {
        Logger.d(TAG, "uploadUserFace:fileName" + fileName
                + ",photoBytes.length:" + (float) photoBytes.length / 1024
                + "[KB]");
        UploadParam uploadParam = new UploadParam();
        uploadParam.setUserAccount(FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        uploadParam.setUploadType(UploadType.FACE);
        uploadParam.setReceiverType(ReceiverType.SINGLE_FRIEND);
        uploadParam.setReceiver(FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        
        //添加上传监听器
        IUploadListener uploadListener = new IUploadListener()
        {
            
            @Override
            public void onUploadFinish(UploadFileForURLResponse response)
            {
                List<UploadFileForURLResult> list = response.getUploadFileForURLResultList();
                UploadFileForURLResult uploadFileForURLResult = null;
                uploadFileForURLResult = list.get(0);
                
                //发送消息到UI,告知UI头像上传成功
                sendMessage(SettingsMessageType.FACE_UPLOAD_SUCCESS,
                        uploadFileForURLResult.getDownloadUrl());
            }
            
            @Override
            public void onUploadStart()
            {
            }
            
            @Override
            public void onUploadPause()
            {
            }
            
            @Override
            public void onUploadStop()
            {
            }
            
            @Override
            public void onUploadFail(String errorInfo)
            {
                //向UI发送文件上传失败消息
                Logger.d(TAG, "onUploadFail()  errorInfo:" + errorInfo);
                sendEmptyMessage(SettingsMessageType.FACE_UPLOAD_FAILED);
            }
            
            @Override
            public void onUploadProgress(int finishedSize, int totalSize,
                    double progressPercent)
            {
            }
        };
        UploadContentInfo uploadFileInfo = new UploadContentInfo();
        uploadFileInfo.setContentName(fileName);
        uploadFileInfo.setContentDesc("upload face");
        uploadFileInfo.setData(photoBytes);
        uploadFileInfo.setMimeType(MimeType.IMG);
        uploadParam.addUploadContentInfoList(uploadFileInfo);
        new ContentUploader().upload(uploadParam, uploadListener);
    }
    
    /**
     * 
     * 根据给定的路径，把bytes中的数据写入改路径下临时文件中<BR>
     * basepath为Activity.getFilesDir()方法得到的
     * 
     * @param baseDir
     * @param bytes
     * @return 临时文件的全路径
     */
    //    private String getFilePath(File baseDir, byte[] bytes)
    //    {
    //        File tempFile;
    //        FileOutputStream fos = null;
    //        String tempFileName = "tmp_head_image.jpg";
    //        String path;
    //        if (null == bytes)
    //        {
    //            Logger.d(TAG, "getFilePath2() null == bytes 返回null");
    //            return null;
    //        }
    //        tempFile = new File(baseDir.getPath(), tempFileName);
    //        if (tempFile.exists())
    //        {
    //            tempFile.delete();
    //        }
    //        try
    //        {
    //            fos = new FileOutputStream(tempFile);
    //            fos.write(bytes);
    //            fos.close();
    //            fos = null;
    //        }
    //        catch (Exception e)
    //        {
    //            Logger.e(TAG, "getFilePath()" + e.toString());
    //            return null;
    //        }
    //        path = tempFile.getPath();
    //        return path;
    //    }
    
    /**
     * {@inheritDoc}
     */
    public String getCrcValue(ContactInfoModel user)
    {
        StringBuilder sb = new StringBuilder();
        Adler32 adler32 = new Adler32();
        String name = user.getNickName();
        if (!StringUtil.isNullOrEmpty(name))
        {
            sb.append("@N");
            sb.append(name);
            sb.append("\r\n");
        }
        int gender = user.getGender();
        if (!StringUtil.isNullOrEmpty(String.valueOf(gender)))
        {
            sb.append("@G");
            sb.append(String.valueOf(gender));
            sb.append("\r\n");
        }
        String province = user.getProvince();
        if (!StringUtil.isNullOrEmpty(province))
        {
            sb.append("@P");
            sb.append(province);
            sb.append("\r\n");
        }
        String city = user.getCity();
        if (!StringUtil.isNullOrEmpty(city))
        {
            sb.append("@C");
            sb.append(city);
            sb.append("\r\n");
        }
        String company = user.getCompany();
        if (!StringUtil.isNullOrEmpty(company))
        {
            sb.append("@C");
            sb.append(company);
            sb.append("\r\n");
        }
        String school = user.getSchool();
        if (!StringUtil.isNullOrEmpty(school))
        {
            sb.append("@S");
            sb.append(school);
            sb.append("\r\n");
        }
        adler32.reset();
        adler32.update(sb.toString().getBytes(), 0, sb.length());
        String crcValue = String.valueOf(adler32.getValue());
        sb.delete(0, sb.length());
        return crcValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerContactInfoObserver()
    {
        registerObserver(URIField.CONTACTINFO_URI, new ContentObserver(
                new Handler())
        {
            public void onChange(boolean selfChange)
            {
                Logger.d(TAG, "=====好友信息表发生变化=====");
                sendEmptyMessage(SettingsMessageType.CONTACTINFO_DB_CHANGED);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterContactInfoObserver()
    {
        unRegisterObserver(URIField.CONTACTINFO_URI);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContactEmail(String email)
    {
        ContentValues cv = new ContentValues();
        cv.put(ContactInfoColumns.PRIMARY_EMAIL, email);
        mContactInfoDbAdapter.updateByFriendSysId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), cv);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContactPhone(String phone)
    {
        ContentValues cv = new ContentValues();
        cv.put(ContactInfoColumns.PRIMARY_MOBILE, phone);
        mContactInfoDbAdapter.updateByFriendSysId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), cv);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unbindAccount(String unbindInfo)
    {
        String userId = FusionConfig.getInstance().getAasResult().getUserID();
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        //读取配置文件中的内容
        SharedPreferences sp = mContext.getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        String account = sp.getString(Common.KEY_USER_PASSPORT, "");
        String password = null;
        //如果要解绑的信息与配置文件中的账号相同，则更新配置文件账号信息和密码信息
        if (StringUtil.equals(unbindInfo, account))
        {
            password = sp.getString(Common.KEY_USER_PASSWORD, "");
            password = DecodeUtil.decrypt(account, password);
            Editor edit = sp.edit();
            edit.putString(Common.KEY_USER_PASSPORT, userId);
            edit.putString(Common.KEY_USER_PASSWORD,
                    DecodeUtil.encrypt(userId, password));
            edit.commit();
        }
        return AccountDbAdapter.getInstance(mContext).unbindAccount(unbindInfo,
                userId,
                userSysId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAllData()
    {
        ConversationDbAdapter.getInstance(mContext)
                .clearAllData(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId());
    }
}
