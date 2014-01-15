/*
 * 文件名: GroupXmppMng.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 群组相关消息的处理
 * 创建人: tjzhang
 * 创建时间:Mar 2, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl.xmpp;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.FriendManagerColumns;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupInfoColumns;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams.GroupParams;
import com.huawei.basic.android.im.component.net.xmpp.data.CommonXmppCmdGenerator.GroupConfigFieldData;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification.MemberInfoNtfData.ItemNtf;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.component.service.impl.IObserver;
import com.huawei.basic.android.im.logic.adapter.db.FriendManagerDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMemberDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMessageDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.MessageUtils;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;
import com.huawei.fast.IEngineBridge;

/**
 * 处理各种群组相关的消息<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, Mar 2, 2012] 
 */
public class GroupXmppMng extends XmppMng
{
    private static final int MEMBER_CHANGE_NICK_CODE = 303;
    
    private GroupInfoDbAdapter mGroupInfoDbAdapter;
    
    private GroupMemberDbAdapter mGroupMemberDbAdapter;
    
    private GroupMessageDbAdapter mGroupMessageDbAdapter;
    
    /**
     * 找朋友小助手数据操作适配器
     */
    private FriendManagerDbAdapter mFriendManagerDbAdapter;
    
    private Context mContext;
    
    /**
     * [构造简要说明]
     * @param engineBridge engineBridge
     * @param observer observer
     */
    public GroupXmppMng(IEngineBridge engineBridge, IObserver observer)
    {
        super(engineBridge, observer);
        
        mContext = getObserver().getContext();
        mGroupInfoDbAdapter = GroupInfoDbAdapter.getInstance(mContext);
        mGroupMemberDbAdapter = GroupMemberDbAdapter.getInstance(mContext);
        mGroupMessageDbAdapter = GroupMessageDbAdapter.getInstance(mContext);
        mFriendManagerDbAdapter = FriendManagerDbAdapter.getInstance(mContext);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.component.service.impl.xmpp.XmppMng#matched(java.lang.String, int)
     */
    
    @Override
    public boolean matched(String componentId, int notifyID)
    {
        // 如果是群组的消息订阅回调，使用MsgXmppMng来处理，本类不处理
        if (GroupParams.FAST_COM_GROUP_ID.equals(componentId))
        {
            if (notifyID == GroupParams.FAST_GROUP_NTF_MESSAGE_RECEIVED
                    || notifyID == GroupParams.FAST_GROUP_NTF_MESSAGE_SEND_ERROR
                    || notifyID == GroupParams.FAST_GROUP_NTF_MESSAGE_SEND)
            {
                return false;
            }
        }
        return super.matched(componentId, notifyID);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handleNotification(String componentID, int notifyId, String data)
    {
        switch (notifyId)
        {
        // 获取群组响应
            case GroupParams.FAST_GROUP_NTF_GET_GOURP_LIST:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            // 获取群信息
            case GroupParams.FAST_GROUP_NTF_GET_CONFIG_INFO:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //获取群成员列表响应
            case GroupParams.FAST_GROUP_NTF_MEMBER_GET_MEMBER_LIST:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //创建群组响应
            case GroupParams.FAST_GROUP_NTF_CREATE:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //邀请别人加入群组响应：成功不返回
            case GroupParams.FAST_GROUP_NTF_MEMBER_INVITE:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //收到邀请加入群组通知
            case GroupParams.FAST_GROUP_NTF_MEMBER_INVITING:
                handleMemberInviting(data);
                break;
            //删除群成员响应，仅删人者收到
            case GroupParams.FAST_GROUP_NTF_MEMBER_REMOVE:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            case GroupParams.FAST_GROUP_NTF_SUBMIT_CONFIG_INFO:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            case GroupParams.FAST_GROUP_NTF_CONFIG_INFO:
                handleConfigInfo(data);
                break;
            case GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_APPLY:
                handleJoinApply(data);
                break;
            //收到申请加入群组通知
            case GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_APPLING:
                handleJoinApplying(data);
                break;
            //搜索群组请求响应，需要放在logic中处理
            case GroupParams.FAST_GROUP_NTF_SEARCH_GROUP:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //成员退出通知（成员退出，所有者退出，成员被踢出）
            case GroupParams.FAST_GROUP_NTF_MEMBER_REMOVED:
                handleMemberRemoved(data);
                break;
            //通知被提出者（仅被踢出者收到）
            case GroupParams.FAST_GROUP_NTF_MEMBER_KICKED:
                handleMemberKicked(data);
                break;
            //成员信息通知（新成员加入，指派管理员，所有者变更、修改了成员信息）
            case GroupParams.FAST_GROUP_NTF_MEMBER_INFO:
                handleMemberInfo(data);
                break;
            //更改接受群消息设置
            case GroupParams.FAST_GROUP_NTF_MEMBER_CHANGE_INFO:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //修改群昵称
            case GroupParams.FAST_GROUP_NTF_MEMBER_CHANGE_NICK:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //管理员同意成员加入服务器返回的信息
            case GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_ACCEPT:
                handleMemberJoinAccept(data);
                break;
            case GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_DECLINE:
                handleMemberJoinDecline(data);
                break;
            //删除群组
            case GroupParams.FAST_GROUP_NTF_DESTROY:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            case GroupParams.FAST_GROUP_NTF_MEMBER_QUIT:
                getObserver().xmppCallback(getComponentId(), notifyId, data);
                break;
            //被邀请者，拒绝接受加入群组邀请(通知管理员)
            case GroupParams.FAST_GROUP_NTF_MEMBER_INVITE_DECLINED:
                Logger.d(TAG, "拒绝接受加入群组邀请:" + data);
                break;
            //申请加入群被拒绝
            case GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_DECLINED:
                Logger.d(TAG, "申请加入群被拒绝:" + data);
                handleMemberJoinDeclined(data);
                break;
            //群成员所在的群被删除(通知成员)
            case GroupParams.FAST_GROUP_NTF_DESTROYED:
                Logger.d(TAG, "群成员所在的群被删除" + data);
                handleGroupDestroyed(data);
                break;
            default:
                break;
        }
    }
    
    /**
     * 群被删除（通知群成员）<BR>
     * @param data data
     */
    private void handleGroupDestroyed(String data)
    {
        try
        {
            GroupNotification.DestroyedNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.DestroyedNtfData.class,
                    data);
            if (null != ntfData)
            {
                String groupJid = UriUtil.getGroupJidFromJid(ntfData.getFrom());
                //删除小助手数据
                mFriendManagerDbAdapter.deleteByGroupId(getObserver().getUserSysID(),
                        groupJid);
                //                mGroupInfoDbAdapter.deleteByGroupJid(getObserver().getUserSysID(),
                //                        groupJid);
                
                //将这个群的affiliation置为none
                mGroupInfoDbAdapter.updateGroupInfoToInvalid(getObserver().getUserSysID(),
                        groupJid);
                getObserver().sendXmppMessage(GroupMessageType.GROUP_DESTROYED_SUCCESS,
                        groupJid);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理申请加入群被拒绝通知<BR>
     * @param data data
     */
    private void handleMemberJoinDeclined(String data)
    {
        //               <group>
        //               <from>1032301@group.im.wo.com.cn</from>
        //               <to>10142@im.wo.com.cn/woclient</to>
        //              <decline action="apply" from="10144@im.wo.com.cn/woclient"><reason/></decline>
        //              </group>
        
        try
        {
            GroupNotification.MemberJoinDeclinedNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.MemberJoinDeclinedNtfData.class,
                    data);
            
            if (null != ntfData)
            {
                //当前登录用户的id
                String friendUserId = getObserver().getUserID();
                String userSysId = getObserver().getUserSysID();
                
                int[] subServices = new int[] { FriendManagerModel.SUBSERVICE_GROUP_APPLY };
                
                //群的jid
                String groupJid = UriUtil.getGroupJidFromJid(ntfData.getFrom());
                
                FriendManagerModel model = mFriendManagerDbAdapter.queryByFriendUserIdAndGroupId(userSysId,
                        subServices,
                        friendUserId,
                        groupJid);
                
                if (null == model)
                {
                    model = new FriendManagerModel();
                    GroupInfoModel groupInfoModel = mGroupInfoDbAdapter.queryByGroupJid(userSysId,
                            groupJid);
                    model.setGroupName(UriUtil.getHitalkIdFromJid(groupJid));
                    if (null != groupInfoModel)
                    {
                        if (groupInfoModel.getGroupName() != null)
                        {
                            model.setGroupName(groupInfoModel.getGroupName());
                        }
                    }
                    model.setSubService(FriendManagerModel.SUBSERVICE_GROUP_APPLY);
                    model.setStatus(FriendManagerModel.STATUS_REFUSE);
                    model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(null));
                    model.setFriendUserId(friendUserId);
                    model.setGroupId(groupJid);
                    mFriendManagerDbAdapter.insert(userSysId,
                            model,
                            generateConversationString(mContext, model),
                            true);
                }
                else
                {
                    model.setStatus(FriendManagerModel.STATUS_REFUSE);
                    model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(null));
                    mFriendManagerDbAdapter.updateByFriendUserIdAndGroupId(friendUserId,
                            userSysId,
                            subServices,
                            groupJid,
                            model,
                            generateConversationString(mContext, model),
                            true);
                }
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理管理员拒绝别人申请加入群的通知<BR>
     * （to管理员）
     * @param data xmpp回调的数据
     */
    private void handleMemberJoinDecline(String data)
    {
        Logger.d(TAG, "handleMemberJoinDecline ---------> data:" + data);
        Logger.d(TAG, "服务器推送的信息应该包含我所同意的那个成员的ID，这边客户端不作规避!");
    }
    
    /**
     * 处理管理员同意别人申请加入群的通知<BR>
     * （to管理员）
     * @param data xmpp回调的数据
     */
    private void handleMemberJoinAccept(String data)
    {
        try
        {
            Logger.d(TAG, "handleMemberJoinAccept ---------> data:" + data);
            
            getObserver().showPrompt(getObserver().getString(R.string.friendmanager_message_add_group_success));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理群成员邀请（被邀请者）<BR>
     * @param data 服务器返回的数据
     */
    private void handleMemberInviting(String data)
    {
        //相关的subservices
        int[] subServices = new int[] {
                FriendManagerModel.SUBSERVICE_GROUP_APPLY,
                FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY };
        
        Logger.d(TAG, "handleMemberInviting ------> 邀请加入群组通知");
        
        try
        {
            // 1.解析数据
            GroupNotification.MemberInvitingNtfData ntfData = parseData(GroupNotification.MemberInvitingNtfData.class,
                    data);
            
            if (ntfData == null || ntfData.getErrorCode() != 0)
            {
                Logger.e(TAG, "handleMemberInviting 解析返回的xml字符串, 字符串为: " + data);
                return;
            }
            
            // 2.获取该群组的相关信息
            String logo = ntfData.getInvite().getGrouplogo();
            
            // 2.1 群组JID
            String groupJid = UriUtil.getGroupJidFromJid(ntfData.getFrom());
            ;
            // 2.2 群组名称
            String groupName = ntfData.getInvite().getGroupname();
            // 2.3 更新数据时间
            String operateTime = DateUtil.getFormatTimeStringForFriendManager(null);
            // 2.4 邀请附带信息
            String reason = ntfData.getInvite().getReason();
            // 2.5 获取当前用户的id
            String friendUserId = getObserver().getUserID();
            
            //是否需要插入数据
            boolean needInsert = false;
            String userSysId = getObserver().getUserSysID();
            
            //查询出数据
            FriendManagerModel model = mFriendManagerDbAdapter.queryByFriendUserIdAndGroupId(userSysId,
                    subServices,
                    friendUserId,
                    groupJid);
            
            if (null == model)
            {
                model = new FriendManagerModel();
                //需要插入
                needInsert = true;
            }
            
            model.setFaceUrl(logo);
            model.setGroupName(groupName);
            model.setOperateTime(operateTime);
            model.setReason(reason);
            model.setGroupId(groupJid);
            model.setFriendUserId(friendUserId);
            model.setSubService(FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY);
            model.setStatus(FriendManagerModel.STATUS_WAITTING);
            FaceManager.updateFace(mContext, groupJid, logo);
            if (needInsert)
            {
                mFriendManagerDbAdapter.insert(userSysId,
                        model,
                        generateConversationString(mContext, model),
                        true);
            }
            else
            {
                mFriendManagerDbAdapter.updateByFriendUserIdAndGroupId(friendUserId,
                        userSysId,
                        subServices,
                        groupJid,
                        model,
                        generateConversationString(mContext, model),
                        true);
            }
            
            //在被邀请人的数据库插入群信息
            needInsert = false;
            GroupInfoModel gim = mGroupInfoDbAdapter.queryByGroupJid(userSysId,
                    groupJid);
            if (gim == null)
            {
                gim = new GroupInfoModel();
                gim.setGroupTypeStr(ntfData.getInvite().getGrouptype());
                needInsert = true;
            }
            // 群组jid
            gim.setGroupId(groupJid);
            // 群描述 可选
            gim.setGroupDesc(ntfData.getInvite().getGroupdesc());
            // 群头像
            gim.setFaceUrl(ntfData.getInvite().getGrouplogo());
            // 群名称
            gim.setGroupName(ntfData.getInvite().getGroupname());
            // 群主昵称
            //          gim.setGroupOwnerNick(ntfData.getInvite().getPerson().getNick());
            gim.setAffiliation(GroupMemberModel.AFFILIATION_NONE);
            gim.setGroupOwnerUserId(UriUtil.getHitalkIdFromJid(ntfData.getInvite()
                    .getFrom()));
            
            if (needInsert)
            {
                //没有该群信息，则插入
                mGroupInfoDbAdapter.insertGroupInfo(userSysId, gim);
            }
            else
            {
                //有，则更新
                mGroupInfoDbAdapter.updateByGroupJid(userSysId,
                        gim.getGroupId(),
                        gim);
            }
        }
        catch (Exception e)
        {
            Logger.e(TAG, e.toString());
        }
        
    }
    
    /**
     * 
     * 处理被踢消息（仅被踢者）<BR>
     * [功能详细描述]
     * @param data xmpp服务器返回的数据
     */
    private void handleMemberKicked(String data)
    {
        try
        {
            GroupNotification.MemberKickedNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.MemberKickedNtfData.class,
                    data);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + data);
                return;
            }
            if (ntfData.getErrorCode() != XmppResultCode.Base.FAST_ERR_SUCCESS)
            {
                Logger.d(TAG,
                        "handleMemberLKicked, 错误码: " + ntfData.getErrorCode());
                return;
            }
            String from = ntfData.getFrom();
            // 群ID
            String groupJid = UriUtil.getGroupJidFromJid(from);
            //            mGroupInfoDbAdapter.deleteByGroupJid(getObserver().getUserSysID(),
            //                    groupJid);
            //            
            //删除小助手相关的信息
            mFriendManagerDbAdapter.deleteByGroupId(getObserver().getUserSysID(),
                    groupJid);
            
            //将这个群的affiliation置为none
            mGroupInfoDbAdapter.updateGroupInfoToInvalid(getObserver().getUserSysID(),
                    groupJid);
            
            getObserver().sendXmppMessage(GroupMessageType.MEMBER_KICKED_FROM_GROUP,
                    groupJid);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理成员退出消息（成员退出，所有者退出，成员被踢出）<BR>
     * [功能详细描述]
     * @param data xmpp服务器返回的数据
     */
    private void handleMemberRemoved(String data)
    {
        try
        {
            GroupNotification.MemberRemovedNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.MemberRemovedNtfData.class,
                    data);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + data);
                return;
            }
            if (ntfData.getErrorCode() != XmppResultCode.Base.FAST_ERR_SUCCESS)
            {
                Logger.d(TAG,
                        "handleMemberRemoved, 错误码: " + ntfData.getErrorCode());
                return;
            }
            
            //当前用户的userSysId
            String userSysId = getObserver().getUserSysID();
            //当前用户的userId
            String userId = getObserver().getUserID();
            
            String from = ntfData.getFrom();
            // 群ID
            String groupJid = UriUtil.getGroupJidFromJid(from);
            // 退出成员的userId
            String memberUserId = UriUtil.getGroupMemberIdFromJid(from);
            // 群成员退出提示内容，优先显示 nickName
            String msgContent = null;
            
            // 查询本地群信息
            GroupInfoModel gim = mGroupInfoDbAdapter.queryByGroupJid(userSysId,
                    groupJid);
            // 如果被踢者id与当前用户id相同，说明当前用户退出
            if (userId != null && userId.equals(memberUserId))
            {
                Logger.d(TAG, "handleMemberRemoved --------> 当前用户退出群,删除群组 ");
                if (gim.getGroupType() == GroupInfoModel.GROUPTYPE_NVN)
                {
                    msgContent = mContext.getResources()
                            .getString(R.string.chatbar_inviter_you)
                            + mContext.getResources()
                                    .getString(R.string.chatbar_msg_quit);
                }
                else
                {
                    msgContent = mContext.getResources()
                            .getString(R.string.chatbar_inviter_you)
                            + mContext.getResources()
                                    .getString(R.string.group_msg_quit);
                }
                Logger.d(TAG,
                        "handleMemberRemoved --------> 往群信息表中插入一条提示消息 --> "
                                + msgContent);
                //TODO userId
                insertSystemMsg(userSysId, groupJid, userId, msgContent);
                //                mGroupInfoDbAdapter.deleteByGroupJid(userSysId, groupJid);
                mFriendManagerDbAdapter.deleteByGroupId(userSysId, groupJid);
                
                //将这个群的affiliation置为none
                mGroupInfoDbAdapter.updateGroupInfoToInvalid(getObserver().getUserSysID(),
                        groupJid);
            }
            else
            {
                // 往群信息表中插入一条提示消息：xxx退出了群
                GroupMemberModel gmm = mGroupMemberDbAdapter.queryByMemberUserIdNoUnion(userSysId,
                        groupJid,
                        memberUserId);
                String msgHead = memberUserId;
                if (gmm != null)
                {
                    msgHead = gmm.getMemberNick();
                }
                if (gim.getGroupType() == GroupInfoModel.GROUPTYPE_NVN)
                {
                    msgContent = msgHead
                            + mContext.getResources()
                                    .getString(R.string.chatbar_msg_quit);
                }
                else
                {
                    msgContent = msgHead
                            + mContext.getResources()
                                    .getString(R.string.group_msg_quit);
                }
                Logger.d(TAG,
                        "handleMemberRemoved --------> 往群信息表中插入一条提示消息 --> "
                                + msgContent);
                //TODO userId
                insertSystemMsg(userSysId, groupJid, userId, msgContent);
                
                Logger.d(TAG, "handleMemberRemoved --------> 其它群成员退出群，删除该成员 ");
                mGroupMemberDbAdapter.deleteMemberByMemberUserId(userSysId,
                        groupJid,
                        memberUserId);
                
                //删除找朋友小助手相关的信息
                mFriendManagerDbAdapter.deleteByFriendUserIdAndGroupId(userSysId,
                        memberUserId,
                        groupJid);
            }
            
            getObserver().sendXmppMessage(GroupMessageType.MEMBER_REMOVED_FROM_GROUP,
                    memberUserId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理执行更新配置信息命令后返回的数据
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param pushContent 服务器返回的数据
     */
    private void handleConfigInfo(String pushContent)
    {
        try
        {
            GroupNotification.ConfigInfoNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.ConfigInfoNtfData.class,
                    pushContent);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + pushContent);
                return;
            }
            if (ntfData.getErrorCode() != 0)
            {
                Logger.d(TAG,
                        "handleConfigInfo, 错误码: " + ntfData.getErrorCode());
                return;
            }
            GroupInfoModel mGroupInfoModel = mGroupInfoDbAdapter.queryByGroupJid(getObserver().getUserSysID(),
                    ntfData.getFrom());
            if (null != mGroupInfoModel
                    && !GroupMemberModel.AFFILIATION_OWNER.equals(mGroupInfoModel.getAffiliation()))
            {
                GroupInfoModel group = new GroupInfoModel();
                List<GroupConfigFieldData> fieldNtfList = ntfData.getX()
                        .getFieldList();
                String groupType = null;
                String groupFixtype = null;
                for (GroupConfigFieldData fieldNtf : fieldNtfList)
                {
                    Logger.d(TAG,
                            fieldNtf.getLabel() + "][" + fieldNtf.getType()
                                    + "][" + fieldNtf.getValue() + "]["
                                    + fieldNtf.getVar());
                    if ("group#groupconfig_groupid".equals(fieldNtf.getVar()))
                    {
                        group.setGroupId(ntfData.getFrom());
                    }
                    else if ("group#groupconfig_grouptype".equals(fieldNtf.getVar()))
                    {
                        groupType = fieldNtf.getValue();
                    }
                    else if ("group#groupconfig_fixtype".equals(fieldNtf.getVar()))
                    {
                        groupFixtype = fieldNtf.getValue();
                    }
                    else if ("group#groupconfig_groupbulletin".equals(fieldNtf.getVar()))
                    {
                        group.setGroupBulletin(fieldNtf.getValue());
                    }
                    else if ("group#groupconfig_chattype".equals(fieldNtf.getVar()))
                    {
                        group.setChatType(fieldNtf.getValue());
                    }
                    else if ("group#groupconfig_groupdesc".equals(fieldNtf.getVar()))
                    {
                        group.setGroupDesc(fieldNtf.getValue());
                    }
                    else if ("group#groupconfig_grouplabel".equals(fieldNtf.getVar()))
                    {
                        group.setGroupLabel(fieldNtf.getValue());
                    }
                    else if ("group#groupconfig_grouplogo".equals(fieldNtf.getVar()))
                    {
                        group.setFaceUrl(fieldNtf.getValue());
                    }
                    else if ("group#groupconfig_groupname".equals(fieldNtf.getVar()))
                    {
                        group.setGroupName(fieldNtf.getValue());
                    }
                    else if ("group#groupconfig_groupsort".equals(fieldNtf.getVar()))
                    {
                        if (fieldNtf.getValue() != null)
                        {
                            group.setGroupSort(Integer.parseInt(fieldNtf.getValue()));
                        }
                    }
                }
                // 如果是固定类型群，则从fixtype中获取内容
                if ("fixed".equals(groupType))
                {
                    group.setGroupTypeStr(groupFixtype);
                }
                else
                {
                    group.setGroupTypeStr(groupType);
                }
                
                if (mGroupInfoDbAdapter.queryByGroupJid(getObserver().getUserSysID(),
                        group.getGroupId()) != null)
                {
                    // 目前只修改群组名称，标签，类型，简介，权限
                    ContentValues values = new ContentValues();
                    values.put(GroupInfoColumns.GROUP_NAME,
                            group.getGroupName());
                    values.put(GroupInfoColumns.GROUP_LABEL,
                            group.getGroupLabel());
                    values.put(GroupInfoColumns.GROUP_DESC,
                            group.getGroupDesc());
                    values.put(GroupInfoColumns.GROUP_SORT,
                            group.getGroupSort());
                    values.put(GroupInfoColumns.GROUP_TYPE,
                            group.getGroupType());
                    // 更新头像
                    FaceManager.updateFace(getObserver().getContext(),
                            group.getGroupId(),
                            group.getFaceUrl());
                    mGroupInfoDbAdapter.updateByGroupJid(getObserver().getUserSysID(),
                            group.getGroupId(),
                            values);
                }
                else
                {
                    mGroupInfoDbAdapter.insertGroupInfo(getObserver().getUserSysID(),
                            group);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * 处理成员信息通知<BR>
     * (1)成员修改了信息(修改描述或者消息接收策略)
     * (2)成员修改了昵称
     * (3)权限变更(暂时不做处理)
     * (4)新成员加入(自己新加入群/聊吧或者其他成员新加入群)
     * @param data xmpp下发的数据
     */
    private void handleMemberInfo(String data)
    {
        Logger.d(TAG, "=====处理成员信息通知=====");
        GroupNotification.MemberInfoNtfData ntfData = parseData(GroupNotification.MemberInfoNtfData.class,
                data);
        
        if (ntfData == null || ntfData.getErrorCode() != 0)
        {
            Logger.e(TAG, "处理成员信息通知解析失败或者错误码不为0" + data);
            return;
        }
        
        String userSysId = getObserver().getUserSysID();
        String userId = getObserver().getUserID();
        
        // 先取出群组JID，成员的userID
        String groupJid = UriUtil.getGroupJidFromJid(ntfData.getFrom());
        ItemNtf item = ntfData.getX().getItemList().get(0);
        String memberUserId = UriUtil.getHitalkIdFromJid(item != null ? item.getJid()
                : null);
        
        //是否是群主，根据本地数据判断
        boolean isOwner = false;
        
        //是否是自己
        boolean isSelf = false;
        
        // 在这里处理一下群成员信息改变的通知消息处理, 包括群成员个人描述和头像,及群消息策略修改时,会有此通知
        // 此通知中, Item中没有成员id, 需要从From字段中获取
        
        //先判断memberUserId是否为空，用来区分是否是成员修改了描述或者群消息接收策略
        if (StringUtil.isNullOrEmpty(memberUserId))
        {
            Logger.d(TAG, "=====有群成员修改了描述或者群消息接收策略=====");
            //此时需要从From字段中取得memberUserId
            memberUserId = UriUtil.getGroupMemberIdFromJid(ntfData.getFrom());
            GroupMemberModel member = mGroupMemberDbAdapter.queryByMemberUserIdNoUnion(userSysId,
                    groupJid,
                    memberUserId);
            
            // 如果数据库中已经有这个成员, 则检查一下是否需要更新成员的信息
            //没有，则不做处理
            if (null != member)
            {
                String desc = item.getDesc();
                if (!StringUtil.equals(desc, member.getMemberDesc()))
                {
                    member.setMemberDesc(desc);
                    mGroupMemberDbAdapter.updateByGroupIdAndMemberUserId(userSysId,
                            groupJid,
                            memberUserId,
                            member);
                }
            }
            return;
        }
        else
        {
            //此时需要判断是群成员修改了昵称还是新成员加入或者是权限发生变更(权限处理暂未处理)
            //先判断是否是有成员修改昵称的
            if (ntfData.getX().getStatusCode() == MEMBER_CHANGE_NICK_CODE)
            {
                Logger.d(TAG, "=====有群成员修改昵称通知=====");
                GroupMemberModel memberModel = mGroupMemberDbAdapter.queryByMemberUserIdNoUnion(userSysId,
                        groupJid,
                        memberUserId);
                //判断本地是否有这个成员，有则更新，没有，则不做处理
                if (null != memberModel)
                {
                    Logger.d(TAG, "=============更新群昵称=============");
                    Logger.d(TAG,
                            "更改前群昵称--------------->"
                                    + memberModel.getMemberNick());
                    memberModel.setMemberNick(ntfData.getX().getGroupNick());
                    mGroupMemberDbAdapter.updateByGroupIdAndMemberUserId(userSysId,
                            groupJid,
                            memberUserId,
                            memberModel);
                    //判断该成员是否是群主，如果是群主，需要修改创建者昵称
                    if (GroupMemberModel.AFFILIATION_OWNER.equals(memberModel.getAffiliation()))
                    {
                        ContentValues cv = new ContentValues();
                        cv.put(GroupInfoColumns.GROUP_OWNERNICK,
                                memberModel.getMemberNick());
                        mGroupInfoDbAdapter.updateByGroupJid(userSysId,
                                groupJid,
                                cv);
                    }
                    
                    getObserver().sendXmppMessage(GroupMessageType.MEMBER_NICKNAME_CHANGED,
                            groupJid);
                }
                
            }
            //下面则为处理有新成员加入的通知
            else
            {
                Logger.d(TAG, "=====有成员加入群=====");
                
                //先判断是否有grouptype字段，
                //如果有，说明是新加入群或者聊吧，需要把群组信息加到数据库中
                String groupType = item.getGroupType();
                
                //头像
                String faceUrl = null;
                
                // 查询本地群信息
                GroupInfoModel gim = mGroupInfoDbAdapter.queryByGroupJid(userSysId,
                        groupJid);
                
                if (!StringUtil.isNullOrEmpty(groupType))
                {
                    boolean hasGroupInfo = true;
                    if (null == gim)
                    {
                        hasGroupInfo = false;
                        gim = new GroupInfoModel();
                        gim.setGroupId(groupJid);
                    }
                    gim.setGroupName(item.getGroupName());
                    gim.setGroupTypeStr(groupType);
                    // 修改群成员的权限属性
                    //这边处理不存在群组的信息的就当成成员的身份去处理
                    gim.setAffiliation(GroupMemberModel.AFFILIATION_MEMBER);
                    gim.setRecvRolicy(GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT);
                    if (hasGroupInfo)
                    {
                        mGroupInfoDbAdapter.updateByGroupJid(userSysId,
                                groupJid,
                                gim);
                    }
                    else
                    {
                        mGroupInfoDbAdapter.insertGroupInfo(userSysId, gim);
                    }
                }
                
                // 先取ItemList, 如果List大于1, 则说明是自己加入群或群中加入了多个的新成员
                // 不论是自己新加入群, 还是有新成员加入群, 之前的群成员中都没有这些人,所以此时先把所有成员都加入到群成员中
                //先取ItemList,判断size大小
                List<ItemNtf> itemList = ntfData.getX().getItemList();
                if (null != itemList && itemList.size() > 0)
                {
                    //先判断size是否为1，如果为1，再判断先加入的是否是自己
                    if (itemList.size() == 1)
                    {
                        String msgContent = null;
                        String memberNick = null;
                        //新加入的群成员是自己
                        if (userId.equals(memberUserId))
                        {
                            Logger.d(TAG, "=====新加入的群成员是自己=====");
                            //获取群成员列表
                            memberNick = mContext.getResources()
                                    .getString(R.string.group_member_you);
                            //群和聊吧插入消息内容有区别
                            if (gim.getGroupType() == GroupInfoModel.GROUPTYPE_NVN)
                            {
                                msgContent = gim.getGroupOwnerNick()
                                        + mContext.getResources()
                                                .getString(R.string.group_msg_invite)
                                        + memberNick
                                        + mContext.getResources()
                                                .getString(R.string.chatbar_msg_add);
                            }
                            else
                            {
                                msgContent = memberNick
                                        + mContext.getResources()
                                                .getString(R.string.group_msg_add);
                            }
                            insertSystemMsg(userSysId,
                                    groupJid,
                                    memberUserId,
                                    msgContent);
                            getObserver().sendXmppMessage(GroupMessageType.NEED_GET_MEMBER_LIST,
                                    groupJid);
                            isSelf = true;
                        }
                        //不是自己，则插入或者更新群成员表
                        else
                        {
                            Logger.d(TAG, "=====新加入的群成员不是自己=====");
                            boolean hasBefore = true;
                            GroupMemberModel gmm = mGroupMemberDbAdapter.queryByMemberUserId(userSysId,
                                    groupJid,
                                    memberUserId);
                            if (gmm == null)
                            {
                                gmm = new GroupMemberModel();
                                gmm.setGroupId(groupJid);
                                gmm.setMemberUserId(memberUserId);
                                gmm.setMemberId(UriUtil.buildMyXmppGroupJid(groupJid,
                                        memberUserId));
                                hasBefore = false;
                            }
                            else
                            {
                                faceUrl = gmm.getMemberFaceUrl();
                            }
                            gmm.setAffiliation(item.getAffiliation());
                            gmm.setMemberNick(item.getGroupnick());
                            gmm.setMemberDesc(item.getDesc());
                            if (hasBefore)
                            {
                                mGroupMemberDbAdapter.updateByGroupIdAndMemberUserId(userSysId,
                                        gmm.getGroupId(),
                                        gmm.getMemberUserId(),
                                        gmm);
                            }
                            else
                            {
                                mGroupMemberDbAdapter.insertGroupMember(userSysId,
                                        gmm);
                            }
                            memberNick = gmm.getMemberNick();
                            if (gim.getGroupType() == GroupInfoModel.GROUPTYPE_NVN)
                            {
                                msgContent = gim.getGroupOwnerNick()
                                        + mContext.getResources()
                                                .getString(R.string.group_msg_invite)
                                        + memberNick
                                        + mContext.getResources()
                                                .getString(R.string.chatbar_msg_add);
                            }
                            else
                            {
                                msgContent = memberNick
                                        + mContext.getResources()
                                                .getString(R.string.group_msg_add);
                            }
                            insertSystemMsg(userSysId,
                                    groupJid,
                                    memberUserId,
                                    msgContent);
                        }
                        getObserver().sendXmppMessage(GroupMessageType.JOIN_GROUP_SUCCESS,
                                memberUserId);
                    }
                    //size 不为1,则把所有的数据插入到群成员表中
                    //这种只会是聊吧，以后可以会为提示语专门做处理
                    else
                    {
                        Logger.d(TAG, "=====新加入N个群成员（聊吧）=====");
                        StringBuilder sbMemberNick = new StringBuilder();
                        String ownerNick = null;
                        for (ItemNtf groupMember : itemList)
                        {
                            
                            // 1. 拼接消息加入到群会话中
                            if (groupMember.getJid() == null)
                            {
                                // 跳过JID为空的元素
                                continue;
                            }
                            String memberNick = groupMember.getGroupnick();
                            String memberJid = UriUtil.getHitalkIdFromJid(groupMember.getJid());
                            String affiliation = groupMember.getAffiliation();
                            String memberID = UriUtil.buildMyXmppGroupJid(groupJid,
                                    memberJid);
                            
                            // 2. 取出所有成员加入到群成员数据库中
                            GroupMemberModel newMember = new GroupMemberModel();
                            newMember.setGroupId(groupJid);
                            newMember.setMemberUserId(memberJid);
                            newMember.setMemberId(memberID);
                            newMember.setMemberNick(memberNick);
                            newMember.setAffiliation(affiliation);
                            
                            if (GroupMemberModel.AFFILIATION_OWNER.equals(affiliation))
                            {
                                // 此说明包含了吧主在列表中，说明是自己第一次被邀请进来
                                if (StringUtil.equals(memberJid, userId))
                                {
                                    //说明创建者是自己，将昵称改成“你”
                                    ownerNick = mContext.getResources()
                                            .getString(R.string.chatbar_inviter_you);
                                }
                                else
                                {
                                    ownerNick = memberNick;
                                }
                            }
                            else if (StringUtil.equals(memberJid, userId))
                            {
                                // 当前成员是自己，说明自己在被邀请之列
                                sbMemberNick.append(mContext.getResources()
                                        .getString(R.string.chatbar_member_you));
                            }
                            else
                            {
                                sbMemberNick.append(newMember.getMemberNick())
                                        .append(",");
                            }
                            
                            // 群成员写入数据库前检查一下是否已经存在，此情况出现在离线时被邀请加入聊吧，上线后先获取群组和成员，再收到加入聊吧的消息
                            if (null == mGroupMemberDbAdapter.queryByMemberUserIdNoUnion(userSysId,
                                    groupJid,
                                    memberJid))
                            {
                                mGroupMemberDbAdapter.insertGroupMember(userSysId,
                                        newMember);
                            }
                            
                        }
                        String nicks = sbMemberNick.substring(0,
                                sbMemberNick.length() - 1).toString();
                        // 往群信息表中插入一条提示消息：xxx邀请xxx，xxx加入聊吧
                        String msgContent = (null != ownerNick ? ownerNick
                                : mContext.getResources()
                                        .getString(R.string.chatbar_inviter_you_regard))
                                + (null != ownerNick ? mContext.getResources()
                                        .getString(R.string.group_msg_invite)
                                        : mContext.getResources()
                                                .getString(R.string.group_msg_invite_))
                                + nicks
                                + mContext.getString(R.string.chatbar_msg_add_);
                        insertSystemMsg(userSysId, groupJid, userId, msgContent);
                        Logger.d(TAG,
                                "handleMemberInfo--------->--在消息 表中插入显示的消息:"
                                        + msgContent);
                    }
                    getObserver().sendXmppMessage(GroupMessageType.MEMBER_ADDED_TO_GROUP,
                            groupJid);
                }
                
                //找朋友小助手
                //处理找朋友小助手信息
                
                //判断该条信息是聊吧相关的，小助手不展示，直接return
                if (gim.getGroupType() == GroupInfoModel.GROUPTYPE_NVN)
                {
                    return;
                }
                
                GroupInfoModel groupModel = mGroupInfoDbAdapter.queryByGroupJid(userSysId,
                        groupJid);
                
                isOwner = groupModel != null
                        && GroupMemberModel.AFFILIATION_OWNER.equalsIgnoreCase(groupModel.getAffiliation());
                
                //是否需要处理小助手记录
                boolean needHandle = false;
                
                //当前用户是群主
                if (isOwner)
                {
                    //处理不是自己的信息
                    if (!isSelf)
                    {
                        //这边要分数据库中的数据 1.申请加入我的群  2.无法区分
                        needHandle = true;
                    }
                }
                else
                {
                    //自己加入群信息
                    if (isSelf)
                    {
                        //这边数据库区分 1.我申请加入群，群主同意  2.接收到群邀请后加入群 3.无法区分
                        needHandle = true;
                    }
                }
                
                if (!needHandle)
                {
                    Logger.d(TAG, "自己不是群主,加入的人又不是自己,小助手不处理");
                    return;
                }
                
                //subservies，区分如何操作数据库
                int[] subServices = new int[] {
                        FriendManagerModel.SUBSERVICE_GROUP_APPLY,
                        FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY,
                        FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF,
                        FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER,
                        FriendManagerModel.SUBSERVICE_GROUP_WAITTING };
                
                FriendManagerModel model = mFriendManagerDbAdapter.queryByFriendUserIdAndGroupId(userSysId,
                        subServices,
                        memberUserId,
                        groupJid);
                
                if (null != model)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(FriendManagerColumns.STATUS,
                            FriendManagerModel.STATUS_AGREE);
                    cv.put(FriendManagerColumns.OPERATE_TIME,
                            DateUtil.getFormatTimeStringForFriendManager(null));
                    model.setStatus(FriendManagerModel.STATUS_AGREE);
                    
                    mFriendManagerDbAdapter.updateByFriendUserIdAndGroupId(memberUserId,
                            userSysId,
                            subServices,
                            groupJid,
                            cv,
                            generateConversationString(mContext, model),
                            true);
                }
                else
                {
                    model = new FriendManagerModel();
                    if (isOwner)
                    {
                        model.setSubService(FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER);
                    }
                    else
                    {
                        model.setSubService(FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF);
                    }
                    model.setStatus(FriendManagerModel.STATUS_AGREE);
                    model.setFriendUserId(memberUserId);
                    model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(null));
                    model.setGroupId(groupJid);
                    model.setFaceUrl(faceUrl);
                    if (null != gim.getGroupName())
                    {
                        model.setGroupName(gim.getGroupName());
                    }
                    else
                    {
                        model.setGroupName(item.getGroupName() == null ? UriUtil.getHitalkIdFromJid(groupJid)
                                : item.getGroupName());
                    }
                    model.setNickName(item.getGroupnick() == null ? memberUserId
                            : item.getGroupnick());
                    mFriendManagerDbAdapter.insert(userSysId,
                            model,
                            generateConversationString(mContext, model),
                            true);
                }
            }
        }
    }
    
    private void handleJoinApplying(String data)
    {
        Logger.d(TAG, "=====被申请者 收到申请加入群组的请求通知=====");
        GroupNotification.MemberJoinApplingNtfData ntfData = parseData(GroupNotification.MemberJoinApplingNtfData.class,
                data);
        
        if (ntfData == null || ntfData.getErrorCode() != 0)
        {
            Logger.e(TAG, "handleJoinApplying 解析返回的xml字符串, 字符串为: " + data);
            return;
        }
        
        String friendUserId = UriUtil.getHitalkIdFromJid(ntfData.getApply()
                .getFrom());
        
        //群的jid
        String groupJid = UriUtil.getGroupJidFromJid(ntfData.getFrom());
        ;
        
        //当前登录用户的SysID
        String userSysId = getObserver().getUserSysID();
        
        //申请加入者的昵称
        String friendNickName = ntfData.getApply().getNick();
        
        //获取群jid的前半部分
        String groupId = UriUtil.getHitalkIdFromJid(groupJid);
        
        //验证信息
        String reason = ntfData.getApply().getReason();
        
        //生成会话的时候，需要展示群的名称
        GroupInfoModel info = mGroupInfoDbAdapter.queryByGroupJid(userSysId,
                groupJid);
        
        //优先显示组名
        String groupName = info == null ? groupId
                : (StringUtil.isNullOrEmpty(info.getGroupName()) ? groupId
                        : info.getGroupName());
        
        FriendManagerModel friendManagerModel = new FriendManagerModel();
        friendManagerModel.setFriendUserId(friendUserId);
        friendManagerModel.setNickName(friendNickName);
        friendManagerModel.setGroupName(groupName);
        friendManagerModel.setReason(reason);
        friendManagerModel.setGroupId(groupJid);
        friendManagerModel.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(null));
        friendManagerModel.setStatus(FriendManagerModel.STATUS_WAITTING);
        friendManagerModel.setSubService(FriendManagerModel.SUBSERVICE_GROUP_WAITTING);
        
        int[] subServices = new int[] {
                FriendManagerModel.SUBSERVICE_GROUP_WAITTING,
                FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY };
        
        FriendManagerModel model = mFriendManagerDbAdapter.queryByFriendUserIdAndGroupId(userSysId,
                subServices,
                friendUserId,
                groupJid);
        
        if (model == null)
        {
            mFriendManagerDbAdapter.insert(userSysId,
                    friendManagerModel,
                    generateConversationString(mContext, friendManagerModel),
                    true);
        }
        else
        {
            mFriendManagerDbAdapter.updateByFriendUserIdAndGroupId(friendUserId,
                    userSysId,
                    subServices,
                    groupJid,
                    friendManagerModel,
                    generateConversationString(mContext, friendManagerModel),
                    true);
        }
    }
    
    private void handleJoinApply(String data)
    {
        Logger.d(TAG, "=====申请者 收到申请加入群响应=====");
        GroupNotification.MemberJoinApplyNtfData ntfData = parseData(GroupNotification.MemberJoinApplyNtfData.class,
                data);
        if (ntfData == null)
        {
            getObserver().sendXmppMessage(GroupMessageType.JOIN_GROUP_FAILED, null);
            return;
        }
        
        // 先获取返回错误码
        int errorCode = ntfData.getErrorCode();
        // 如果没有错误，则解析服务器返回内容
        if (errorCode != XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            getObserver().sendXmppMessage(GroupMessageType.JOIN_GROUP_FAILED,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(errorCode)));
            return;
        }
    }
    
    /**
     * 
     * 插入系统消息<BR>
     * [功能详细描述]
     * @param groupId groupId
     * @param memberUserId memberUserId
     * @param msgContent msgContent
     */
    private void insertSystemMsg(String userSysId, String groupId,
            String memberUserId, String msgContent)
    {
        GroupMessageModel msgModel = new GroupMessageModel();
        msgModel.setMsgId(MessageUtils.generateMsgId());
        msgModel.setGroupId(groupId);
        msgModel.setMsgSendOrRecv(GroupMessageModel.MSGSENDORRECV_RECV);
        msgModel.setMsgTime(DateUtil.getCurrentDateString());
        msgModel.setMsgStatus(GroupMessageModel.MSGSTATUS_UNREAD_NO_REPORT);
        msgModel.setMsgType(GroupMessageModel.MSGTYPE_SYSTEM);
        msgModel.setMsgContent(msgContent);
        msgModel.setMemberUserId(memberUserId);
        mGroupMessageDbAdapter.insertGroupMsg(userSysId,
                ConversationModel.CONVERSATIONTYPE_GROUP,
                msgModel);
    }
    
    /**
     * 生成会话展示
     * 
     * @param friendManagerModel friendManagerModel
     * @param context 上下文对象
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
    protected String getComponentId()
    {
        return GroupParams.FAST_COM_GROUP_ID;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void subNotify()
    {
        String componentId = getComponentId();
        //创建群组通知
        getEngineBridge().subNotify(componentId, GroupParams.FAST_GROUP_NTF_CREATE);
        
        // 获取群组配置信息
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_GET_CONFIG_INFO);
        
        //获取邀请加入群组通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_INVITE);
        
        //获取群组列表通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_GET_GOURP_LIST);
        
        // 删除群组通知（通知其他成员）
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_DESTROYED);
        
        // 成员退出通知（成员退出，所有者退出，成员被踢出）
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_REMOVED);
        
        // 邀请加入群组通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_INVITING);
        
        // 请求加入群组通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_APPLING);
        
        // 通知被踢出者（仅被踢出者收到）
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_KICKED);
        
        // 订阅成员信息通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_INFO);
        
        // 订阅 群组配置信息更新
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_CONFIG_INFO);
        
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_SUBMIT_CONFIG_INFO);
        // 订阅申请加入群被拒绝的响应
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_DECLINED);
        
        // 订阅管理员 （to管理员）同意别人申请加入群的响应
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_ACCEPT);
        
        // 被邀请者，拒绝接受加入群组邀请(通知管理员)
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_INVITE_DECLINED);
        
        // 被邀请者同意接受加入群组邀请(通知管理员)
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_INVITE_ACCEPT);
        
        // 拒绝别人加入我的群，通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_DECLINE);
        //获取群成员列表通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_GET_MEMBER_LIST);
        //申请加入群通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_JOIN_APPLY);
        //搜索群请求通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_SEARCH_GROUP);
        //删除群成员请求通知(成功或者失败)
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_REMOVE);
        //修改群昵称请求通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_CHANGE_NICK);
        //修改群消息接收策略或者描述
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_CHANGE_INFO);
        //申请退出群组的通知
        getEngineBridge().subNotify(componentId,
                GroupParams.FAST_GROUP_NTF_MEMBER_QUIT);
        //删除群组的通知
        getEngineBridge().subNotify(componentId, GroupParams.FAST_GROUP_NTF_DESTROY);
    }
}
