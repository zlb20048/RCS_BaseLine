/*
 * 文件名: FriendHelperLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 22, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.friend;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.FriendHelperMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FriendManagerColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.ConversationDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FriendManagerDbAdapter;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 找朋友小助手记录<BR>
 * 
 * @author qlzhou
 * @version [RCS Client V100R001C03, Feb 22, 2012]
 */
public class FriendHelperLogic extends BaseLogic implements IFriendHelperLogic
{
    /**
     * DEBUG TAG
     */
    private static final String TAG = "FriendHelperLogic";
    
    /**
     * 默认分页大小
     */
    private static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 上下文
     */
    private Context mContext;
    
    /**
     * {@link PresenceXmppSender}
     */
    private FriendManagerDbAdapter mFriendManagerAdapter;
    
    /**
     * {@link ContactInfoDbAdapter}
     */
    private ContactInfoDbAdapter mContactInfoDbAdapter;
    
    /**
     * {@link ContactInfoDbAdapter}
     */
    private FaceThumbnailDbAdapter mFaceThumbnailDbAdapter;
    
    /**
     * {@link PresenceXmppSender}
     */
    private PresenceXmppSender mPresenceXmppSender;
    
    /**
     * {@link ConversationDbAdapter}
     */
    private ConversationDbAdapter mConversationDbAdapter;
    
    /**
     * 找朋友小助手逻辑构造方法
     * 
     * @param context
     *            context
     * @param serviceSender
     *            serviceSender
     */
    public FriendHelperLogic(Context context, IServiceSender serviceSender)
    {
        this.mContext = context;
        mFriendManagerAdapter = FriendManagerDbAdapter.getInstance(context);
        mContactInfoDbAdapter = ContactInfoDbAdapter.getInstance(context);
        mFaceThumbnailDbAdapter = FaceThumbnailDbAdapter.getInstance(context);
        mPresenceXmppSender = new PresenceXmppSender(serviceSender);
        mConversationDbAdapter = ConversationDbAdapter.getInstance(context);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Uri[] getObserverUris()
    {
        return new Uri[] { URIField.FRIENDMANAGER_URI };
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onChangeByUri(boolean selfChange, Uri uri)
    {
        sendMessage(FriendHelperMessageType.FRIENDHELPER_LIST_CHANGED, null);
        super.onChangeByUri(selfChange, uri);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doAuth(String friendUserId, boolean isAgree)
    {
        // 当期登录用户的userID 和 sysID
        String userId = FusionConfig.getInstance().getAasResult().getUserID();
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        
        // 执行结果
        int executeResultCode = mPresenceXmppSender.doAuth(userId,
                friendUserId,
                isAgree);
        
        Logger.d(TAG, "doAuth --------> executeResultCode:" + executeResultCode);
        
        if (handleXmppResultCode(executeResultCode,
                mContext.getString(R.string.friendmanager_operation_confirm_friend)))
        {
            Logger.d(TAG, "doAuth -------> 执行命令成功");
            // 操作找朋友小助手数据库
            ContentValues cv = new ContentValues();
            if (isAgree)
            {
                cv.put(FriendManagerColumns.STATUS,
                        FriendManagerModel.STATUS_AGREE_SEND_SENDDING);
            }
            else
            {
                cv.put(FriendManagerColumns.STATUS,
                        FriendManagerModel.STATUS_REFUSE_SEND_SENDDING);
            }
            
            int[] subServices = new int[] {
                    FriendManagerModel.SUBSERVICE_ADD_FRIEND,
                    FriendManagerModel.SUBSERVICE_BE_ADD,
                    FriendManagerModel.SUBSERVICE_FRIEND_COMMON };
            
            mFriendManagerAdapter.updateByFriendUserIdAndSubservices(sysId,
                    friendUserId,
                    subServices,
                    cv,
                    null,
                    false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public FriendManagerModel getFriendManagerFromDB(String userSysId,
            int subService, String friendUserId, String groupId)
    {
        FriendManagerModel friendManagerModel = null;
        
        if (null == groupId)
        {
            friendManagerModel = mFriendManagerAdapter.queryBySubServiceAndFriendUserId(userSysId,
                    subService,
                    friendUserId);
        }
        else
        {
            friendManagerModel = mFriendManagerAdapter.queryByFriendUserIdAndGroupId(userSysId,
                    subService,
                    friendUserId,
                    groupId);
        }
        return friendManagerModel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addFriend(String friendUserId, String nickName, String reason,
            String faceUrl)
    {
        Logger.d(TAG, "addFriend -------->发出加好友申请!");
        
        // 获取当前登录用户userId和sysId
        String userId = FusionConfig.getInstance().getAasResult().getUserID();
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        
        // 查询个人资料
        ContactInfoModel info = mContactInfoDbAdapter.queryMyProfile(sysId);
        
        if (info == null)
        {
            Logger.e(TAG, "数据库中不存在当前用户的信息，无法发送请求！");
            return;
        }
        
        if (null != mContactInfoDbAdapter.queryByFriendUserIdNoUnion(sysId,
                friendUserId))
        {
            handleXmppResultCode(FusionCode.XmppConfig.FRIEND_LIST_EXISTED,
                    mContext.getString(R.string.friendmanager_operation_addfriend));
            return;
        }
        
        int result = mPresenceXmppSender.addFriend(userId,
                info.getFaceUrl(),
                info.getNickName(),
                info.getSignature(),
                friendUserId,
                reason);
        
        Logger.d(TAG, "addFriend -----> executeResult:" + result);
        
        if (handleXmppResultCode(result,
                mContext.getString(R.string.friendmanager_operation_addfriend)))
        {
            newFreindHelperRecord(friendUserId, nickName, reason, faceUrl);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFriend(String friendUserId)
    {
        // 当前登录用户的userId
        String userId = FusionConfig.getInstance().getAasResult().getUserID();
        
        int executeResultCode = mPresenceXmppSender.deleteFriend(userId,
                friendUserId);
        
        Logger.d(TAG, "deleteFriend -----> executeResult:" + executeResultCode);
        
        // 处理错误信息
        handleXmppResultCode(executeResultCode,
                mContext.getString(R.string.friendmanager_operation_deletefriend));
    }
    
    /**
     * 插入新的一条找朋友数据<BR>
     * @param toUserId friendUserId
     * @param nickName 昵称
     * @param reason 验证信息
     * @param faceUrl 头像url
     */
    private void newFreindHelperRecord(String toUserId, String nickName,
            String reason, String faceUrl)
    {
        String mSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        // 关联插入头像表数据
        FaceThumbnailModel faceThumbnailModel = new FaceThumbnailModel();
        faceThumbnailModel.setFaceUrl(faceUrl);
        faceThumbnailModel.setFaceId(toUserId);
        mFaceThumbnailDbAdapter.updateOrInsert(faceThumbnailModel);
        
        int[] subservices = new int[] {
                FriendManagerModel.SUBSERVICE_ADD_FRIEND,
                FriendManagerModel.SUBSERVICE_BE_ADD,
                FriendManagerModel.SUBSERVICE_FRIEND_COMMON };
        
        FriendManagerModel model = mFriendManagerAdapter.queryBySubServiceAndFriendUserId(mSysId,
                toUserId,
                subservices);
        
        // 数据库中是否存在该条记录
        boolean isExist = null != model;
        
        if (null == model)
        {
            model = new FriendManagerModel();
        }
        
        model.setFriendUserId(toUserId);
        model.setSubService(FriendManagerModel.SUBSERVICE_ADD_FRIEND);
        model.setStatus(FriendManagerModel.STATUS_SENDDING);
        model.setReason(reason);
        model.setNickName(nickName);
        model.setFaceUrl(faceUrl);
        model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(null));
        
        if (!isExist)
        {
            mFriendManagerAdapter.insert(mSysId, model, null, false);
        }
        else
        {
            mFriendManagerAdapter.updateByFriendUserIdAndSubservices(mSysId,
                    toUserId,
                    subservices,
                    model,
                    generateConversationString(mContext, model),
                    false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFriendManagerByFriendUserId(String friendUserId,
            int subservice, String groupJid)
    {
        String sysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        
        if (groupJid != null)
        {
            return mFriendManagerAdapter.deleteByFriendUserIdAndGroupId(sysId,
                    friendUserId,
                    groupJid) >= 1;
        }
        else
        {
            return mFriendManagerAdapter.deleteByFriendUserIdAndSubservices(sysId,
                    new int[] { subservice },
                    friendUserId) >= 1;
        }
    }
    
    /**
     * 
     * 通过hiTalkID监听数据库单条好友信息<BR>
     * 
     * @param hiTalkID
     *            hiTalkID
     */
    @Override
    public void registerObserverByID(final String hiTalkID)
    {
        // 空指针保护
        if (null == hiTalkID)
        {
            return;
        }
        
        final Uri uri = Uri.withAppendedPath(URIField.FRIENDMANAGER_WITH_FRIEND_USER_ID_URI,
                hiTalkID);
        // 调用父类方法，将当前uri注册到监听中
        registerObserver(uri, new ContentObserver(new Handler())
        {
            public void onChange(boolean selfChange)
            {
                sendMessage(FriendHelperMessageType.FRIENDHELPER_CHANGED,
                        hiTalkID);
            }
        });
    }
    
    /**
     * 
     * 通过hiTalkID移除数据库单条好友信息监听<BR>
     * 
     * @param hiTalkID
     *            hiTalkID
     */
    @Override
    public void unRegisterObserverByID(String hiTalkID)
    {
        // 空指针保护
        if (null == hiTalkID)
        {
            return;
        }
        
        Uri uri = Uri.withAppendedPath(URIField.FRIENDMANAGER_WITH_FRIEND_USER_ID_URI,
                hiTalkID);
        // 调用父类方法，移除uri
        unRegisterObserver(uri);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<FriendManagerModel> queryByPage(int page)
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        ArrayList<FriendManagerModel> list = mFriendManagerAdapter.queryListByPage(userSysId,
                (page - 1) * DEFAULT_PAGE_SIZE,
                DEFAULT_PAGE_SIZE);
        
        // 处理关联的头像，不同的数据，关联的头像是不一样的，
        // 例如 申请加入群 展示的 群头像 ，好友相关的则展示好友的头像
        if (null != list)
        {
            for (FriendManagerModel friendManagerModel : list)
            {
                String faceKey = null;
                switch (friendManagerModel.getSubService())
                {
                    case FriendManagerModel.SUBSERVICE_BE_ADD:
                    case FriendManagerModel.SUBSERVICE_ADD_FRIEND:
                    case FriendManagerModel.SUBSERVICE_FRIEND_COMMON:
                    case FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER:
                    case FriendManagerModel.SUBSERVICE_GROUP_WAITTING:
                        // 展示好友的头像
                        faceKey = friendManagerModel.getFriendUserId();
                        break;
                    case FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY:
                    case FriendManagerModel.SUBSERVICE_GROUP_APPLY:
                    case FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF:
                        // 展示群头像
                        faceKey = friendManagerModel.getGroupId();
                        break;
                    default:
                        break;
                }
                if (null != faceKey)
                {
                    FaceThumbnailModel model = mFaceThumbnailDbAdapter.queryByFaceId(faceKey);
                    if (null != model)
                    {
                        friendManagerModel.setFaceUrl(model.getFaceUrl());
                    }
                }
            }
        }
        
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clearFriendManagerUnreadMessages()
    {
        // 获取当前用户SysId
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        return mConversationDbAdapter.clearFriendUnreadMsgInConver(userSysId) >= 1;
        
    }
    
    /**
     * 处理Xmpp超时错误，失败必然展示toast<BR>
     * 
     * @param code
     *            错误码
     * @param operation
     *            操作
     * @return 是否继续处理（错误的情况下只显示toast）
     */
    private boolean handleXmppResultCode(int code, String operation)
    {
        String messageString = FusionErrorInfo.getXmppErrInfo(mContext,
                String.valueOf(code));
        if (XmppResultCode.Base.FAST_ERR_SUCCESS == code)
        {
            return true;
        }
        else
        {
            if (null == messageString)
            {
                messageString = mContext.getResources()
                        .getString(R.string.xmpp_error_code_unknown);
            }
            // 展示toast
            Toast.makeText(mContext, messageString, Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    
    /**
     * 生成会话展示
     * 
     * @param friendManagerModel
     *            friendManagerModel
     * @param context
     *            上下文对象
     * @return 生成的字符串
     */
    private String generateConversationString(Context context,
            FriendManagerModel friendManagerModel)
    {
        int subservice = friendManagerModel.getSubService();
        int status = friendManagerModel.getStatus();
        
        Logger.d(TAG, "generateConversationString -------> subservice:"
                + subservice);
        Logger.d(TAG, "generateConversationString -------> status:" + status);
        
        Resources rcs = context.getResources();
        String lastConversationMsg = null;
        
        // 会话中展示的好友名称，如果为空 则显示 好友的WoYou ID
        String displayFriendName = null;
        // 会话中展示的群组名称 ，如果为空 则显示 群组的 ID
        String displayGroupName = null;
        
        displayGroupName = StringUtil.isNullOrEmpty(friendManagerModel.getGroupName()) ? friendManagerModel.getGroupId()
                : friendManagerModel.getGroupName();
        displayFriendName = StringUtil.isNullOrEmpty(friendManagerModel.getNickName()) ? friendManagerModel.getFriendUserId()
                : friendManagerModel.getNickName();
        
        switch (subservice)
        {
            case FriendManagerModel.SUBSERVICE_ADD_FRIEND:
            {
                // 申请加别人好友
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_result),
                            displayFriendName,
                            rcs.getString(R.string.friendmanager_message_pass));
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_wait),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_result),
                            displayFriendName,
                            rcs.getString(R.string.friendmanager_message_decline));
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_BE_ADD:
            {
                // 被加好友
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_result),
                            rcs.getString(R.string.friendmanager_message_pass),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_wait),
                            displayFriendName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayFriendName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY:
            {
                
                // 接收到群邀请
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_inviting_agree),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_inviting),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.firendmanager_message_inviting_refuse),
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GROUP_WAITTING:
            {
                // 群主受理待加入成员
                
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_result),
                            rcs.getString(R.string.friendmanager_message_agree),
                            displayFriendName,
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_wait),
                            displayFriendName,
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_be_add_group_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayFriendName,
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_GROUP_APPLY:
            {
                // 申请加入群
                if (FriendManagerModel.STATUS_AGREE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_result),
                            rcs.getString(R.string.friendmanager_message_agree),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_WAITTING == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_wait),
                            displayGroupName);
                }
                else if (FriendManagerModel.STATUS_REFUSE == status)
                {
                    lastConversationMsg = String.format(rcs.getString(R.string.friendmanager_message_add_group_result),
                            rcs.getString(R.string.friendmanager_message_decline),
                            displayGroupName);
                }
                break;
            }
            case FriendManagerModel.SUBSERVICE_INVITE_REGISTER:
            case FriendManagerModel.SUBSERVICE_SYSTEM_MATCH:
                break;
            case FriendManagerModel.SUBSERVICE_FRIEND_COMMON:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_friend_success),
                        displayFriendName);
                break;
            case FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_group_success_self),
                        displayGroupName);
                break;
            case FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER:
                lastConversationMsg = String.format(context.getResources()
                        .getString(R.string.friendmanager_message_group_success_owner),
                        displayFriendName,
                        displayGroupName);
                break;
        }
        Logger.d(TAG,
                "generateConversationString -------> 生成的字符串  lastConversationMsg: "
                        + lastConversationMsg);
        return lastConversationMsg;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clearFriendManagerMessages()
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        mFriendManagerAdapter.deleteByFriendSysId(userSysId);
        return true;
    }
}
