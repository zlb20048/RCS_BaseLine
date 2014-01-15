/*
 * 文件名: PresenceXmppMng.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl.xmpp;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.FriendHelperMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.ContactInfoColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FriendManagerColumns;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceNotification;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.notification.TextNotificationEntity;
import com.huawei.basic.android.im.component.service.impl.IObserver;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.ConversationDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FriendManagerDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.MessageDbAdapter;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.ui.friend.FindFriendHelperActivity;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;
import com.huawei.fast.IEngineBridge;

/**
 * XMPP状态服务管理类<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-11]
 */
public class PresenceXmppMng extends XmppMng
{
    /**
     * 离线类型
     */
    private static final String UNAVAILABLE = "uavailable";
    
    /**
     * 相关的subservices
     */
    private int[] subServices = new int[] {
            FriendManagerModel.SUBSERVICE_ADD_FRIEND,
            FriendManagerModel.SUBSERVICE_BE_ADD,
            FriendManagerModel.SUBSERVICE_FRIEND_COMMON };
    
    /**
     * 好友管理adapter
     */
    private FriendManagerDbAdapter mFriendManagerDbAdapter;
    
    /**
     * 联系人数据库操作 
     */
    private ContactInfoDbAdapter mContactDbAdapter;
    
    /**
     * 头像表
     */
    private FaceThumbnailDbAdapter mFaceThumbnailDbAdapter;
    
    private TextNotificationEntity mEntity;
    
    /**
     * 构造函数
     * @param engineBridge IEngineBridge
     * @param observer IObserver
     */
    public PresenceXmppMng(IEngineBridge engineBridge, IObserver observer)
    {
        super(engineBridge, observer);
        mFriendManagerDbAdapter = FriendManagerDbAdapter.getInstance(getObserver().getContext());
        mContactDbAdapter = ContactInfoDbAdapter.getInstance(getObserver().getContext());
        mFaceThumbnailDbAdapter = FaceThumbnailDbAdapter.getInstance(getObserver().getContext());
        
        //注册广播监听，主要用于通知栏清除
        registIMNotificationReceiver();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handleNotification(String componentID, int notifyId, String data)
    {
        switch (notifyId)
        {
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADDING:
                handleFriendAdding(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADD:
                handleFriendAdd(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_REMOVE:
                handleFriendRemove(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADDED:
                handleFriendAdded(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_REMOVED:
                handleFriendRemoved(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADD_DECLINED:
                handleFriendAddDeclined(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADDING_CONFIRM:
                handleFriendAddConfirm(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_PRESENCE:
                handlePresence(data);
                break;
            case BaseParams.PresenceParams.FAST_PRESENCE_NTF_UNAVAILABLE:
                // 好友离线消息，当前手机客户端不做处理
                break;
            default:
                break;
        }
    }
    
    /**
     * <BR>
     * 
     * @return 该组件id
     * @see com.huawei.basic.android.im.service.impl.xmpp.XmppMng#getComponentId()
     */
    @Override
    protected String getComponentId()
    {
        return BaseParams.PresenceParams.FAST_COM_PRESENCE_ID;
    }
    
    /**
     * <BR>
     * 
     * @see com.huawei.basic.android.im.service.impl.xmpp.XmppMng#subNotify()
     */
    
    @Override
    protected void subNotify()
    {
        // 添加好友请求通知
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADDING);
        
        // 添加好友响应
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADD);
        
        // 添加好友成功
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADDED);
        
        // 添加好友被拒绝
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADD_DECLINED);
        
        // 确认是否同意被加好友响应
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_ADDING_CONFIRM);
        
        // 删除好友请求响应
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_REMOVE);
        
        // 被删除好友
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_FRIEND_REMOVED);
        
        // 好友发布状态
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_PRESENCE);
        
        // 好友离线通知
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.PresenceParams.FAST_PRESENCE_NTF_UNAVAILABLE);
    }
    
    /**
     * 其他用户发过来，用户收到加好友请求通知<BR>
     * 
     * @param content pushContent
     */
    private void handleFriendAdding(String content)
    {
        Logger.i(TAG, "接收到添加好友的请求." + content);
        
        // 1.解析 收到的加好友请求XML
        PresenceNotification.FriendAddingNtf pn = parseData(PresenceNotification.FriendAddingNtf.class,
                content);
        if (pn == null)
        {
            return;
        }
        
        // 2.获取该用户的相关信息
        String logo = null;
        String nickName = null;
        
        PresenceCommonClass.PersonData person = pn.getPresence().getPerson();
        
        if (null != person)
        {
            logo = person.getLogo();
            nickName = person.getNick();
        }
        
        String friendUserId = UriUtil.getHitalkIdFromJid(pn.getFrom());
        String operateDate = DateUtil.getFormatTimeStringForFriendManager(null);
        String reason = pn.getPresence().getReason();
        
        // 3.生成在会话表中显示的字符串信息
        String lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_be_add_wait),
                StringUtil.isNullOrEmpty(nickName) ? friendUserId : nickName);
        
        // 4.保存信息到数据库
        FriendManagerModel fhm = new FriendManagerModel();
        fhm.setFaceUrl(logo);
        fhm.setFriendUserId(friendUserId);
        fhm.setNickName(nickName);
        fhm.setOperateTime(operateDate);
        fhm.setReason(reason);
        
        // 类型为被加好友
        fhm.setSubService(FriendManagerModel.SUBSERVICE_BE_ADD);
        
        // 状态为等待处理
        fhm.setStatus(FriendManagerModel.STATUS_WAITTING);
        
        //插入或者全量更新
        if (mFriendManagerDbAdapter.updateOrInsert(getObserver().getUserSysID(),
                friendUserId,
                subServices,
                fhm,
                lastMsgContent,
                true) > 0)
        {
            //通知栏提示            
            showNotification(lastMsgContent);
        }
    }
    
    /**
     * 自己发出去的加好友请求通知<BR>
     * 
     * @param content 服务器push过来的内容
     */
    private void handleFriendAdd(String content)
    {
        
        Logger.i(TAG, "发送添加好友请求的响应.");
        // 1.解析数据
        PresenceNotification.FriendAddNtf fa = null;
        fa = parseData(PresenceNotification.FriendAddNtf.class, content);
        
        if (null == fa || !handleResult(fa.getErrorCode(), true))
        {
            return;
        }
        
        // 2.获取该用户的信息
        String fromJid = fa.getFrom();
        String friendUserId = UriUtil.getHitalkIdFromJid(fromJid);
        
        //当前用户的sysid
        String userSysId = getObserver().getUserSysID();
        
        FriendManagerModel model = mFriendManagerDbAdapter.queryBySubServiceAndFriendUserId(userSysId,
                friendUserId,
                subServices);
        
        //昵称
        String nickName = model == null ? friendUserId : model.getNickName();
        
        // 3.生成在会话表中显示的字符串信息
        String lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_add),
                StringUtil.isNullOrEmpty(nickName) ? friendUserId : nickName);
        
        // 4.更新数据库
        ContentValues cv = new ContentValues();
        cv.put(FriendManagerColumns.STATUS, FriendManagerModel.STATUS_WAITTING);
        mFriendManagerDbAdapter.updateByFriendUserIdAndSubservices(userSysId,
                friendUserId,
                subServices,
                cv,
                lastMsgContent,
                false);
    }
    
    /**
     * 删除好友响应<BR>
     * 
     * @param content push Content
     */
    private void handleFriendRemove(String content)
    {
        
        Logger.i(TAG, "handleFriendRemove ----> 收到删除好友请求的响应.");
        
        // 删除好友请求响应（结果）
        PresenceNotification.FriendRemoveNtf fr = parseData(PresenceNotification.FriendRemoveNtf.class,
                content);
        
        if (fr == null || !handleResult(fr.getErrorCode(), false))
        {
            return;
        }
        
        // 成功删除好友
        // 1.获取该好友的信息
        String friendUserId = UriUtil.getHitalkIdFromJid(fr.getFrom());
        
        // 2.从数据库删除该好友
        Logger.i(TAG, "从数据库删除该好友， friend user Id : " + friendUserId);
        mContactDbAdapter.deleteByFriendUserId(getObserver().getUserSysID(),
                friendUserId);
        
        // 删除找朋友小助手相关的记录
        mFriendManagerDbAdapter.deleteByFriendUserIdAndSubservices(getObserver().getUserSysID(),
                subServices,
                friendUserId);
        
        // 清除1V1消息
        MessageDbAdapter.getInstance(getObserver().getContext())
                .deleteFriendMessage(getObserver().getUserSysID(), friendUserId);
        
        // 发送消息给好友详情处理
        getObserver().sendXmppMessage(FriendHelperMessageType.DELETE_FRIEND_SUCCESS,
                friendUserId);
    }
    
    /**
     * 
     * 收到被删除的通知<BR>
     * 
     * @param content push Content
     */
    private void handleFriendRemoved(String content)
    {
        
        Logger.i(TAG, "收到被删除的通知");
        PresenceNotification.FriendRemovedNtf frn = parseData(PresenceNotification.FriendRemovedNtf.class,
                content);
        if (frn == null)
        {
            return;
        }
        String fromJid = frn.getFrom();
        String fromUserId = UriUtil.getHitalkIdFromJid(fromJid);
        
        // 删除好友信息表记录
        mContactDbAdapter.deleteByFriendUserId(getObserver().getUserSysID(),
                fromUserId);
        
        // 删除找朋友小助手记录
        mFriendManagerDbAdapter.deleteByFriendUserIdAndSubservices(getObserver().getUserSysID(),
                subServices,
                fromUserId);
        
        // 清除1V1消息，按照新的需求被动删除不需要删除聊天记录
        //        MessageDbAdapter.getInstance(getObserver().getContext())
        //                .deleteFriendMessage(getObserver().getUserSysID(), fromUserId);
        
        //发送消息通知logic
        getObserver().sendXmppMessage(FriendHelperMessageType.BE_DELETED,
                fromUserId);
    }
    
    /**
     * 添加好友成功<BR>
     * 收到已经被添加为好友通知（1.自动匹配好友向双方发送 2.默认同意添加好友，向被添加方发送）
     * 
     * @param content 服务器push过来的内容
     */
    private void handleFriendAdded(String content)
    {
        Logger.i(TAG, "添加好友成功的通知:" + content);
        
        // 添加好友成功，更新请求表为同意 
        PresenceNotification.FriendAddedNtf fan = parseData(PresenceNotification.FriendAddedNtf.class,
                content);
        
        if (fan == null)
        {
            return;
        }
        
        String fromJid = fan.getFrom();
        String friendUserId = UriUtil.getHitalkIdFromJid(fromJid);
        String nickName = null;
        String faceUrl = null;
        String lastMsgContent = null;
        String operateTime = DateUtil.getFormatTimeStringForFriendManager(null);
        
        FriendManagerModel savedFriendManagerModel = mFriendManagerDbAdapter.queryBySubServiceAndFriendUserId(getObserver().getUserSysID(),
                friendUserId,
                subServices);
        // 在if else 都操作会话表，当本地数据库中存在数据时，以本地数据库为主
        if (null != savedFriendManagerModel)
        {
            // 更新数据库已同意
            savedFriendManagerModel.setOperateTime(operateTime);
            
            savedFriendManagerModel.setStatus(FriendManagerModel.STATUS_AGREE);
            
            // 如果为空的话，就显示数据库中的数据，优先显示网络最新数据
            nickName = nickName == null ? savedFriendManagerModel.getNickName()
                    : nickName;
            
            // 生成会话
            lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_add_result_success),
                    StringUtil.isNullOrEmpty(nickName) ? friendUserId
                            : nickName);
            
            //更新数据库
            mFriendManagerDbAdapter.updateByFriendUserIdAndSubservices(getObserver().getUserSysID(),
                    friendUserId,
                    subServices,
                    savedFriendManagerModel,
                    lastMsgContent,
                    true);
        }
        else
        {
            // 找朋友小助手中没有数据，插入新的一条记录
            lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_friend_success),
                    StringUtil.isNullOrEmpty(nickName) ? friendUserId
                            : nickName);
            
            FriendManagerModel fmm = new FriendManagerModel();
            fmm.setFaceUrl(faceUrl);
            fmm.setNickName(nickName);
            fmm.setOperateTime(operateTime);
            fmm.setFriendUserId(friendUserId);
            fmm.setSubService(FriendManagerModel.SUBSERVICE_FRIEND_COMMON);
            fmm.setStatus(FriendManagerModel.STATUS_AGREE);
            mFriendManagerDbAdapter.insert(getObserver().getUserSysID(),
                    fmm,
                    lastMsgContent,
                    true);
        }
        //通知栏展示
        showNotification(lastMsgContent);
        getObserver().sendXmppMessage(FriendHelperMessageType.NEW_FRIEND_ADDED,
                friendUserId);
        
    }
    
    /**
     * 加好友被拒绝通知<BR>
     * 
     * @param content push Content
     */
    private void handleFriendAddDeclined(String content)
    {
        
        Logger.d(TAG, "收到加好友被拒绝通知");
        // 收到添加好友被拒绝通知。仅通知订阅发起方
        
        PresenceNotification.FriendAddDeclinedNtf fad = parseData(PresenceNotification.FriendAddDeclinedNtf.class,
                content);
        
        if (fad == null)
        {
            return;
        }
        
        String fromJid = fad.getFrom();
        String friendUserId = UriUtil.getHitalkIdFromJid(fromJid);
        String nickName = null;
        String operateTime = DateUtil.getFormatTimeStringForFriendManager(null);
        
        FriendManagerModel savedFriendManagerModel = mFriendManagerDbAdapter.queryBySubServiceAndFriendUserId(getObserver().getUserSysID(),
                friendUserId,
                subServices);
        String lastMsgContent = null;
        
        if (null != savedFriendManagerModel)
        {
            // 更新数据库已拒绝
            savedFriendManagerModel.setStatus(FriendManagerModel.STATUS_REFUSE);
            savedFriendManagerModel.setOperateTime(operateTime);
            
            nickName = savedFriendManagerModel.getNickName();
            
            // 生成会话
            lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_add_result_declined),
                    StringUtil.isNullOrEmpty(nickName) ? friendUserId
                            : nickName);
            
            //更新小助手数据库
            mFriendManagerDbAdapter.updateByFriendUserIdAndSubservices(getObserver().getUserSysID(),
                    friendUserId,
                    subServices,
                    savedFriendManagerModel,
                    lastMsgContent,
                    true);
        }
        else
        {
            
            // 生成会话
            lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_add_result_declined),
                    StringUtil.isNullOrEmpty(nickName) ? friendUserId
                            : nickName);
            
            FriendManagerModel fmm = new FriendManagerModel();
            fmm.setNickName(nickName);
            fmm.setOperateTime(operateTime);
            fmm.setFriendUserId(friendUserId);
            fmm.setSubService(FriendManagerModel.SUBSERVICE_ADD_FRIEND);
            fmm.setStatus(FriendManagerModel.STATUS_REFUSE);
            mFriendManagerDbAdapter.insert(getObserver().getUserSysID(),
                    fmm,
                    lastMsgContent,
                    true);
        }
        
        //通知栏展示
        showNotification(lastMsgContent);
        
    }
    
    /**
     * 加好友确认通知<BR>
     * 
     * @param content pushContent
     */
    private void handleFriendAddConfirm(String content)
    {
        
        // 确认是否同意被加为好友响应
        PresenceNotification.FriendAddingConfirmNtf fac = parseData(PresenceNotification.FriendAddingConfirmNtf.class,
                content);
        
        //是否通知logic重新获取数据
        boolean isNeed = false;
        
        if (fac == null || !handleResult(fac.getErrorCode(), true))
        {
            return;
        }
        
        String friendUserId = UriUtil.getHitalkIdFromJid(fac.getFrom());
        String operateTime = DateUtil.getFormatTimeStringForFriendManager(null);
        
        // 查询数据库
        FriendManagerModel savedFriendManagerModel = mFriendManagerDbAdapter.queryBySubServiceAndFriendUserId(getObserver().getUserSysID(),
                friendUserId,
                subServices);
        
        if (null != savedFriendManagerModel)
        {
            savedFriendManagerModel.setOperateTime(operateTime);
            String lastMsgContent = null;
            
            // 存在数据
            
            switch (savedFriendManagerModel.getStatus())
            {
                case FriendManagerModel.STATUS_AGREE_SEND_SENDDING:
                {
                    savedFriendManagerModel.setStatus(FriendManagerModel.STATUS_AGREE);
                    
                    // 更新会话表
                    
                    String nickName = savedFriendManagerModel.getNickName();
                    
                    // 生成会话
                    lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_be_add),
                            getObserver().getString(R.string.friendmanager_agree),
                            StringUtil.isNullOrEmpty(nickName) ? friendUserId
                                    : nickName);
                    
                    isNeed = true;
                    break;
                }
                case FriendManagerModel.STATUS_REFUSE_SEND_SENDDING:
                {
                    savedFriendManagerModel.setStatus(FriendManagerModel.STATUS_REFUSE);
                    // 更新会话表
                    String nickName = savedFriendManagerModel.getNickName();
                    
                    // 生成会话
                    lastMsgContent = String.format(getObserver().getString(R.string.friendmanager_message_be_add),
                            getObserver().getString(R.string.friendmanager_refuse),
                            StringUtil.isNullOrEmpty(nickName) ? friendUserId
                                    : nickName);
                    Logger.d(TAG,
                            "handleFriendAddConfirmNtf ------------> 生成的会话："
                                    + lastMsgContent);
                    isNeed = false;
                    break;
                }
            }
            
            // 更新找朋友小助手数据库
            mFriendManagerDbAdapter.updateByFriendUserIdAndSubservices(getObserver().getUserSysID(),
                    friendUserId,
                    subServices,
                    savedFriendManagerModel,
                    lastMsgContent,
                    false);
            
            if (isNeed)
            {
                getObserver().sendXmppMessage(FriendHelperMessageType.REQUEST_DO_AUTH_SUCCESS,
                        null);
            }
        }
        
    }
    
    /**
     * 好友状态更新<BR>
     * 
     * @param content push Content
     */
    private void handlePresence(String content)
    {
        // 接收到好友个人资料更新的推送消息，刷新好友界面
        Logger.d(TAG, "收到好友状态更新，包括签名变更等");
        PresenceNotification.PresenceNtf pn = parseData(PresenceNotification.PresenceNtf.class,
                content);
        if (pn == null
                || UNAVAILABLE.equalsIgnoreCase(pn.getPresence().getType()))
        {
            return;
        }
        else
        {
            PresenceCommonClass.PersonData personData = pn.getPresence()
                    .getPerson();
            if (null == personData)
            {
                return;
            }
            String nickName = personData.getNick();
            String logo = personData.getLogo();
            String sign = personData.getSign();
            String fromUserId = UriUtil.getHitalkIdFromJid(pn.getFrom());
            String sysId = getObserver().getUserSysID();
            
            Logger.e(TAG, "nickName:" + nickName + ",logo:" + logo + ",sign:"
                    + sign + ",fromUserId:" + fromUserId + ",sysId:" + sysId);
            
            ContactInfoModel contactModel = mContactDbAdapter.queryByFriendUserIdNoUnion(sysId,
                    fromUserId);
            
            if (null == contactModel)
            {
                contactModel = new ContactInfoModel();
                contactModel.setNickName(nickName);
                contactModel.setFaceUrl(logo);
                contactModel.setFriendUserId(fromUserId);
                long id = mContactDbAdapter.insertContactInfo(sysId,
                        contactModel);
                Logger.d(TAG, "插入数据的 id:" + id);
            }
            else
            {
                //更新关联的头像
                if (null != logo)
                {
                    FaceThumbnailModel faceModel = new FaceThumbnailModel();
                    faceModel.setFaceId(fromUserId);
                    faceModel.setFaceUrl(logo);
                    mFaceThumbnailDbAdapter.updateOrInsert(faceModel);
                }
                
                //先更新头像然后更新好友
                ContentValues cv = new ContentValues();
                cv.put(ContactInfoColumns.NICK_NAME, nickName);
                cv.put(ContactInfoColumns.SIGNATURE, sign);
                mContactDbAdapter.updateByFriendUserId(sysId, fromUserId, cv);
            }
            
            //发送消息到UI
            getObserver().sendXmppMessage(FriendHelperMessageType.FRIENDHELPER_PRESENCE,
                    null);
        }
    }
    
    /**
     * 处理错误码<BR>
     * @param errorCode 错误码
     * @param needShowToast 成功是否需要显示toast
     * @return 是否继续处理
     */
    private boolean handleResult(int errorCode, boolean needShowToast)
    {
        if (XmppResultCode.Base.FAST_ERR_SUCCESS == errorCode)
        {
            if (needShowToast)
            {
                getObserver().showPrompt(getObserver().getContext()
                        .getString(R.string.request_sended));
            }
            return true;
        }
        else
        {
            String messageString = FusionErrorInfo.getXmppErrInfo(getObserver().getContext(),
                    String.valueOf(errorCode));
            if (null == messageString)
            {
                messageString = getObserver().getContext()
                        .getString(R.string.request_fail);
            }
            getObserver().showPrompt(messageString);
            return false;
        }
    }
    
    private int getUnreadMessageCount()
    {
        return ConversationDbAdapter.getInstance(getObserver().getContext())
                .queryFriendManagerUnreadCount(getObserver().getUserSysID());
    }
    
    private void showNotification(String content)
    {
        ActivityManager am = (ActivityManager) getObserver().getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager mKeyguardManager = (KeyguardManager) getObserver().getContext()
                .getSystemService(Context.KEYGUARD_SERVICE);
        ComponentName name = am.getRunningTasks(1).get(0).topActivity;
        //锁屏情况下也要提示通知栏
        if (mKeyguardManager.inKeyguardRestrictedInputMode()
                || !StringUtil.equals(name.getClassName(),
                        FindFriendHelperActivity.class.getName()))
        {
            String title = String.format(getObserver().getContext()
                    .getResources()
                    .getString(R.string.notify_friendmanager_title),
                    getUnreadMessageCount());
            if (null != mEntity)
            {
                NotificationEntityManager.getInstance()
                        .cancelNotification(mEntity);
            }
            mEntity = new TextNotificationEntity(R.drawable.notification_logo,
                    content, title, content, getObserver().isSoundOpen());
            mEntity.setActivityIntent(new Intent(
                    FusionAction.FindFriendHelperAction.ACTION));
            NotificationEntityManager.getInstance()
                    .showNewNotification(mEntity);
        }
    }
    
    /**
     * 注册notification的广播监听<BR>
     * 主要为了实现，当进入到会话详情页面的时候清除通知栏
     */
    private void registIMNotificationReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TextNotificationEntity.NOTIFICAITON_ACTION_IM_FRIENDHELPER);
        getObserver().getContext().registerReceiver(new BroadcastReceiver()
        {
            
            @Override
            public void onReceive(Context context, Intent intent)
            {
                NotificationEntityManager.getInstance()
                        .cancelNotification(mEntity);
            }
        },
                filter);
    }
    
}
