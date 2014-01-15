/*
 * 文件名: GroupLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 群组相关的Logic 处理类
 * 创建人: tjzhang
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.group;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.GroupListAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupInfoColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams.GroupParams;
import com.huawei.basic.android.im.component.net.xmpp.data.CommonXmppCmdGenerator.GroupConfigFieldData;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification.CreateNtfData.Item;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification.GetGroupListNtfData.GroupListItemNtf;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification.MemberGetMemberListNtfData.MemberItemNtf;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.component.service.app.IXmppServiceListener;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FriendManagerDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMemberDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMessageDbAdapter;
import com.huawei.basic.android.im.logic.adapter.http.ContactInfoManager;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.HanziToPinyin;
import com.huawei.basic.android.im.utils.MessageUtils;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.SystemFacesUtil;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * 群组logic<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public class GroupLogic extends BaseLogic implements IGroupLogic,
        IXmppServiceListener
{
    private static final String TAG = "GroupLogic";
    
    /**
     * 用户创建群组最大的数量
     */
    private static final int MAX_CREATE_GROUP = 10;
    
    /**
     * 一次获取群成员的pageID
     */
    private static final int GROUP_MEMBER_PAGE_ID = 1;
    
    /**
     * 一次获取群成员的page size
     */
    private static final int GROUP_MEMBER_PAGE_SIZE = 200;
    
    /**
     * 关键字/id搜索群组的value
     */
    private static final String MOBILE_TYPE = "mobile";
    
    /**
     * 分类搜索群组的value
     */
    private static final String CATEGROY_TYPE = "sort";
    
    private Context mContext;
    
    private IServiceSender mServiceSender;
    
    private ContactInfoDbAdapter mContactInfoDbAdapter;
    
    private GroupInfoDbAdapter mGroupInfoDbAdapter;
    
    private GroupMemberDbAdapter mGroupMemberDbAdapter;
    
    private GroupMessageDbAdapter mGroupMessageDbAdapter;
    
    private FriendManagerDbAdapter mFriendManagerDbAdapter;
    
    private FaceThumbnailDbAdapter mFaceThumbnailDbAdapter;
    
    private GroupXmppSender mGroupXmppSender;
    
    /**
     * 构造方法
     * @param context context
     * @param serviceSender serviceSender
     */
    public GroupLogic(Context context, IServiceSender serviceSender)
    {
        mContext = context;
        mServiceSender = serviceSender;
        mServiceSender.addXmppServiceListener(this);
        mContactInfoDbAdapter = ContactInfoDbAdapter.getInstance(mContext);
        mGroupInfoDbAdapter = GroupInfoDbAdapter.getInstance(mContext);
        mGroupMemberDbAdapter = GroupMemberDbAdapter.getInstance(mContext);
        mGroupMessageDbAdapter = GroupMessageDbAdapter.getInstance(mContext);
        mFriendManagerDbAdapter = FriendManagerDbAdapter.getInstance(mContext);
        mFaceThumbnailDbAdapter = FaceThumbnailDbAdapter.getInstance(mContext);
        mGroupXmppSender = new GroupXmppSender(serviceSender);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<GroupInfoModel> getGroupListFormDB(int mode)
    {
        
        List<GroupInfoModel> list = mGroupInfoDbAdapter.queryAll(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        Logger.d(TAG, "get group list, size : "
                + (list != null ? list.size() : 0));
        if (list != null && list.size() > 0)
        {
            int size = list.size();
            GroupInfoModel gim = null;
            for (int i = size - 1; i >= 0; i--)
            {
                gim = list.get(i);
                // 先拿出是padding状态的群组，因为暂时不具有权限操作
                if (GroupMemberModel.AFFILIATION_NONE.equals(gim.getAffiliation()))
                {
                    list.remove(i);
                    continue;
                }
                // 然后根据是群还是聊吧 去掉不需要的群信息
                boolean isSession = GroupInfoModel.GROUP_TYPE_SESSION.equals(gim.getGroupTypeString());
                if ((mode == GroupListAction.GROUP_MODE && isSession)
                        || (mode == GroupListAction.CHAT_BAR_MODE && !isSession))
                {
                    list.remove(i);
                }
            }
            Logger.d(TAG, "after filter list, size : " + list.size());
        }
        return list;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortGroup(List<GroupInfoModel> list)
    {
        
        if (list != null)
        {
            ArrayList<GroupInfoModel> myGroupList = new ArrayList<GroupInfoModel>();
            ArrayList<GroupInfoModel> myOtherGroupList = new ArrayList<GroupInfoModel>();
            for (GroupInfoModel model : list)
            {
                if (GroupMemberModel.AFFILIATION_OWNER.equals(model.getAffiliation()))
                {
                    myGroupList.add(model);
                }
                else
                {
                    myOtherGroupList.add(model);
                }
            }
            GroupComparator comparator = new GroupComparator();
            try
            {
                Collections.sort(myGroupList, comparator);
                Collections.sort(myOtherGroupList, comparator);
                list.clear();
                list.addAll(myGroupList);
                list.addAll(myOtherGroupList);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortMember(List<GroupMemberModel> list)
    {
        
        if (list != null)
        {
            GroupMemberComparator comparator = new GroupMemberComparator();
            try
            {
                Collections.sort(list, comparator);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            GroupMemberModel ownerModel = null;
            GroupMemberModel model = null;
            for (int i = list.size() - 1; i >= 0; i--)
            {
                model = list.get(i);
                if (GroupMemberModel.AFFILIATION_OWNER.equals(model.getAffiliation()))
                {
                    ownerModel = model;
                    list.remove(i);
                }
                else if (GroupMemberModel.AFFILIATION_NONE.equals(model.getAffiliation()))
                {
                    list.remove(i);
                }
            }
            if (ownerModel != null)
            {
                list.add(0, ownerModel);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContactInfoModel> filterContact(
            List<GroupMemberModel> beforeHadList)
    {
        List<ContactInfoModel> list = mContactInfoDbAdapter.queryAllWithFaceUrl(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        if (list != null && beforeHadList != null)
        {
            int size = list.size();
            int beforeSize = beforeHadList.size();
            for (int i = size - 1; i >= 0; i--)
            {
                ContactInfoModel allInfo = list.get(i);
                for (int j = 0; j < beforeSize; j++)
                {
                    GroupMemberModel existInfo = beforeHadList.get(j);
                    
                    if (StringUtil.equals(allInfo.getFriendUserId(),
                            UriUtil.getHitalkIdFromJid(existInfo.getMemberUserId()))
                            && !GroupMemberModel.AFFILIATION_NONE.equals(existInfo.getAffiliation()))
                    {
                        list.remove(i);
                        break;
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
    public List<String> getCategroyTitles()
    {
        return Arrays.asList(mContext.getResources()
                .getStringArray(R.array.group_catagroy_title));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCategroyType(int mode)
    {
        return getCategroyTitles().get(mode - 1);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValidate(int mode)
    {
        return Arrays.asList(mContext.getResources()
                .getStringArray(R.array.group_validate_title)).get(mode - 1);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getGroupListFromXmpp()
    {
        int result = mGroupXmppSender.getGroupList(FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        handleXmppResult(result,
                null,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.GET_GROUP_LIST_FAILED);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createGroup(GroupInfoModel model, final int from)
    {
        createGroupByIds(null, model, from);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createGroupByIds(final String[] ids, GroupInfoModel model,
            final int from)
    {
        
        Logger.d(TAG, "=====创建群组=====");
        ContactInfoModel contactInfoModel = mContactInfoDbAdapter.queryByFriendSysIdWithPrivate(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                FusionConfig.getInstance().getAasResult().getUserSysId());
        //生成群组创建者的昵称
        String groupOwnerNick = contactInfoModel != null ? contactInfoModel.getDisplayName()
                : FusionConfig.getInstance().getAasResult().getUserID();
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_CREATE)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    
                    String groupId = handleCreateGroup(data, from);
                    if (ids != null && groupId != null)
                    {
                        inviteMember(ids,
                                groupId,
                                GroupMessageType.INVITE_MEMBER_FROM_CONVERSATION);
                    }
                    
                }
                
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.createGroup(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(), model, groupOwnerNick);
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                from == GroupMessageType.CREATE_GROUP_FROM_CONVERSATION ? GroupMessageType.CREATE_GROUP_FAILED_FROM_CONVERSATION
                        : GroupMessageType.CREATE_GROUP_FAILED_FROM_GROUP);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptInvite(String groupId)
    {
        Logger.d(TAG, "=====同意邀请加入群=====");
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        ContactInfoModel cim = mContactInfoDbAdapter.queryByFriendSysIdWithPrivate(userSysId,
                userSysId);
        int result = mGroupXmppSender.acceptInvite(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(),
                groupId,
                "",
                cim.getDisplayName(),
                GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT,
                GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT);
        handleXmppResult(result,
                null,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.ACCEPT_INVITE_MEMBER_FAILED);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void declineInvite(String to, String reason, String declineTo)
    {
        Logger.d(TAG, "=====拒绝邀请加入群=====");
        
        int result = mGroupXmppSender.declineInvite(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(), to, reason, declineTo);
        if (XmppResultCode.Base.FAST_ERR_SUCCESS == result)
        {
            int[] subServices = new int[] { FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY };
            String userSysId = FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId();
            String friendUserId = FusionConfig.getInstance()
                    .getAasResult()
                    .getUserID();
            
            FriendManagerModel model = mFriendManagerDbAdapter.queryByFriendUserIdAndGroupId(userSysId,
                    subServices,
                    friendUserId,
                    to);
            
            if (model != null)
            {
                model.setStatus(FriendManagerModel.STATUS_REFUSE);
                model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(null));
                mFriendManagerDbAdapter.updateByFriendUserIdAndGroupId(friendUserId,
                        userSysId,
                        subServices,
                        to,
                        model,
                        generateConversationString(mContext, model),
                        false);
            }
            sendEmptyMessage(GroupMessageType.DECLINE_INVITE_MEMBER_SUCCESS);
        }
        else
        {
            //            Toast.makeText(mContext,
            //                    mContext.getResources().getString(R.string.request_fail),
            //                    Toast.LENGTH_SHORT).show();
            handleXmppResult(result,
                    null,
                    GroupMessageType.INVALID_GROUP_MESSAGE,
                    GroupMessageType.DECLINE_INVITE_MEMBER_FAILED);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void inviteMember(String[] ids, String groupId, final int from)
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        //需要先把ids组成成id + nickname的list
        List<String[]> memberIdList = new ArrayList<String[]>();
        int length = ids.length;
        ContactInfoModel model = null;
        for (int i = 0; i < length; i++)
        {
            model = mContactInfoDbAdapter.queryByFriendUserIdNoUnion(userSysId,
                    ids[i]);
            memberIdList.add(new String[] {
                    UriUtil.buildXmppJidNoWo(model.getFriendUserId()),
                    model.getNickName() });
        }
        
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_MEMBER_INVITE)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handleInviteMember(data, from);
                    
                }
                
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        
        //获取邀请人的昵称和头像url
        model = mContactInfoDbAdapter.queryByFriendSysIdWithPrivate(userSysId,
                userSysId);
        int result = mGroupXmppSender.inviteMember(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(),
                groupId,
                model != null ? model.getNickName() : "",
                model != null ? model.getFaceUrl() : "",
                memberIdList,
                mContext.getResources().getString(R.string.group_invite_reason));
        //TODO 由于服务器暂时没有邀请成功的响应，这里在命令执行成功后就提示邀请成功，以后要去掉
        handleXmppResult(result,
                listener,
                from == GroupMessageType.INVITE_MEMBER_FROM_CONVERSATION ? GroupMessageType.INVITE_MEMBER_SUCCESS_FROM_CONVERSATION
                        : GroupMessageType.INVITE_MEMBER_SUCCESS_FROM_GROUP,
                from == GroupMessageType.INVITE_MEMBER_FROM_CONVERSATION ? GroupMessageType.INVITE_MEMBER_FAILED_FROM_CONVERSATION
                        : GroupMessageType.INVITE_MEMBER_FAILED_FROM_GROUP);
    }
    
    /**
     * {@inheritDoc}
     */
    public void joinGroup(String reason, GroupInfoModel gim)
    {
        Logger.d(TAG, "======申请加入群=====");
        
        //当前登录用户的sysId
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        
        //当前登录用户的hitalk id
        String friendUserId = FusionConfig.getInstance()
                .getAasResult()
                .getUserID();
        
        int[] subServices = new int[] {
                FriendManagerModel.SUBSERVICE_GROUP_APPLY,
                FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY };
        
        //判断群详情表是否需要插入数据
        boolean isInsert = false;
        GroupInfoModel groupModel = mGroupInfoDbAdapter.queryByGroupJidNoUnion(userSysId,
                gim.getGroupId());
        if (null == groupModel)
        {
            groupModel = new GroupInfoModel();
            isInsert = true;
        }
        groupModel.setGroupId(gim.getGroupId());
        groupModel.setFaceUrl(gim.getFaceUrl());
        groupModel.setGroupName(gim.getGroupName());
        groupModel.setGroupLabel(gim.getGroupLabel());
        groupModel.setGroupDesc(gim.getGroupDesc());
        groupModel.setGroupSort(gim.getGroupSort());
        groupModel.setGroupOwnerNick(gim.getGroupOwnerNick());
        groupModel.setGroupType(gim.getGroupType());
        groupModel.setAffiliation(GroupMemberModel.AFFILIATION_NONE);
        if (isInsert)
        {
            mGroupInfoDbAdapter.insertGroupInfo(userSysId, groupModel);
        }
        else
        {
            mGroupInfoDbAdapter.updateByGroupJid(userSysId,
                    gim.getGroupId(),
                    groupModel);
        }
        //是否需要插入数据
        boolean needInsert = false;
        
        FriendManagerModel model = mFriendManagerDbAdapter.queryByFriendUserIdAndGroupId(userSysId,
                subServices,
                friendUserId,
                gim.getGroupId());
        
        if (null == model)
        {
            model = new FriendManagerModel();
            
            needInsert = true;
        }
        
        model.setSubService(FriendManagerModel.SUBSERVICE_GROUP_APPLY);
        model.setFriendUserId(friendUserId);
        model.setStatus(FriendManagerModel.STATUS_WAITTING);
        model.setGroupName(gim.getGroupName());
        model.setFaceUrl(gim.getFaceUrl());
        model.setOperateTime(DateUtil.getFormatTimeStringForFriendManager(null));
        model.setGroupId(gim.getGroupId());
        
        if (needInsert)
        {
            mFriendManagerDbAdapter.insert(userSysId,
                    model,
                    generateConversationString(mContext, model),
                    false);
        }
        else
        {
            //更新数据库
            mFriendManagerDbAdapter.updateByFriendUserIdAndGroupId(friendUserId,
                    userSysId,
                    subServices,
                    gim.getGroupId(),
                    model,
                    generateConversationString(mContext, model),
                    false);
        }
        
        ContactInfoModel cim = mContactInfoDbAdapter.queryByFriendSysIdWithPrivate(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                FusionConfig.getInstance().getAasResult().getUserSysId());
        
        int result = mGroupXmppSender.joinGroup(cim.getFriendUserId(),
                gim.getGroupId(),
                cim.getDescription(),
                cim.getDisplayName(),
                GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT,
                GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT,
                reason);
        handleXmppResult(result,
                null,
                GroupMessageType.REQUEST_MESSAGE_SEND_SUCCESS,
                GroupMessageType.JOIN_GROUP_FAILED);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptJoin(String groupId, String userId, String affiliation)
    {
        Logger.d(TAG, "=====同意加入群申请=====");
        Logger.d(TAG, "groupJid:" + groupId + " userId :" + groupId
                + "affiliation:" + affiliation);
        mGroupXmppSender.acceptJoin(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(),
                groupId,
                UriUtil.buildXmppJid(userId),
                GroupMemberModel.AFFILIATION_MEMBER);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void declineJoin(String groupJid, String joiningUserJid,
            String reason)
    {
        Logger.d(TAG, "=====拒绝加入群申请=====");
        //        Logger.d(TAG, "小助手拒绝成功,服务器没有返回码流,客户端无法处理拒绝的情况,这边直接更新数据位拒绝");
        
        int result = mGroupXmppSender.declineJoin(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(), groupJid, joiningUserJid, reason);
        
        if (XmppResultCode.Base.FAST_ERR_SUCCESS == result)
        {
            int[] subServices = new int[] { FriendManagerModel.SUBSERVICE_GROUP_WAITTING };
            String userSysId = FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId();
            
            FriendManagerModel model = mFriendManagerDbAdapter.queryByFriendUserIdAndGroupId(userSysId,
                    subServices,
                    joiningUserJid,
                    groupJid);
            
            if (model != null)
            {
                model.setStatus(FriendManagerModel.STATUS_REFUSE);
                mFriendManagerDbAdapter.updateByFriendUserIdAndGroupId(joiningUserJid,
                        userSysId,
                        subServices,
                        groupJid,
                        model,
                        generateConversationString(mContext, model),
                        false);
            }
        }
        else
        {
            Toast.makeText(mContext,
                    mContext.getResources().getString(R.string.request_fail),
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void searchGroupByCategory(int pageID, int pageSize, String searchKey)
    {
        Logger.d(TAG, "=====根据群分类搜索群=====");
        int result = mGroupXmppSender.searchGroupList(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(),
                pageID,
                pageSize,
                searchKey,
                CATEGROY_TYPE);
        handleXmppResult(result,
                null,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.SEARCH_GROUP_FAILED);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void searchGroupByKey(int pageID, int pageSize, String searchKey)
    {
        Logger.d(TAG, "=====根据关键字/ID 搜索群=====");
        int result = mGroupXmppSender.searchGroupList(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(),
                pageID,
                pageSize,
                searchKey,
                MOBILE_TYPE);
        handleXmppResult(result,
                null,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.SEARCH_GROUP_FAILED);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMemberListCount(String groupId)
    {
        
        List<GroupMemberModel> memberList = mGroupMemberDbAdapter.queryByGroupIdNoUnion(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                groupId);
        if (memberList == null || memberList.size() < 1)
        {
            return 0;
        }
        
        int count = 0;
        for (GroupMemberModel member : memberList)
        {
            if (member.getAffiliation().equals(GroupInfoModel.CHATTYPE_NONE))
            {
                continue;
            }
            
            count++;
        }
        
        return count;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasGroupReachMaxNumber()
    {
        List<GroupInfoModel> lists = mGroupInfoDbAdapter.queryAll(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        // 需要算出自己创建的，并且是群组的数目
        int total = 0;
        if (lists != null)
        {
            for (GroupInfoModel gim : lists)
            {
                if (gim.getGroupType() > GroupInfoModel.GROUPTYPE_NVN
                        && GroupMemberModel.AFFILIATION_OWNER.equals(gim.getAffiliation()))
                {
                    total++;
                }
            }
        }
        if (total >= MAX_CREATE_GROUP)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerGroupInfoObserver()
    {
        registerObserver(URIField.GROUPINFO_URI, new ContentObserver(
                new Handler())
        {
            public void onChange(boolean selfChange)
            {
                Logger.d(TAG, "=====群组信息表发生变化=====");
                sendEmptyMessage(GroupMessageType.GROUPINFO_DB_CHANGED);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerGroupInfoByIdObserver(final String groupId)
    {
        
        registerObserver(Uri.withAppendedPath(URIField.GROUPINFO_WITH_GROUPID_URI,
                groupId),
                new ContentObserver(new Handler())
                {
                    public void onChange(boolean selfChange)
                    {
                        Logger.d(TAG, "=====群组信息表的一条记录发生变化=====" + groupId);
                        sendMessage(GroupMessageType.GROUPINFO_DB_ONE_RECORD_CHANGED,
                                groupId);
                    }
                });
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterGroupInfoObserver()
    {
        unRegisterObserver(URIField.GROUPINFO_URI);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterGroupInfoByIdObserver(String groupId)
    {
        unRegisterObserver(Uri.withAppendedPath(URIField.GROUPINFO_WITH_GROUPID_URI,
                groupId));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendXmppMessage(int messageType, String result)
    {
        switch (messageType)
        {
            case GroupMessageType.GET_GROUP_LIST_SUCCESS:
                break;
            case GroupMessageType.GET_GROUP_LIST_FAILED:
                break;
            case GroupMessageType.GET_MEMBER_LIST_SUCCESS:
                sendEmptyMessage(GroupMessageType.GET_MEMBER_LIST_SUCCESS);
                break;
            case GroupMessageType.GET_MEMBER_LIST_FAILED:
                sendEmptyMessage(GroupMessageType.GET_MEMBER_LIST_FAILED);
                break;
            case GroupMessageType.MEMBER_REMOVED_FROM_GROUP:
                Logger.d(result, "====sendXmppMessage====");
                sendMessage(GroupMessageType.MEMBER_REMOVED_FROM_GROUP, result);
                break;
            case GroupMessageType.MEMBER_KICKED_FROM_GROUP:
                sendMessage(GroupMessageType.MEMBER_KICKED_FROM_GROUP, result);
                break;
            case GroupMessageType.MEMBER_NICKNAME_CHANGED:
                sendMessage(GroupMessageType.MEMBER_NICKNAME_CHANGED, result);
                break;
            case GroupMessageType.MEMBER_ADDED_TO_GROUP:
                sendMessage(GroupMessageType.MEMBER_ADDED_TO_GROUP, result);
                break;
            case GroupMessageType.NEED_GET_MEMBER_LIST:
                getMemberListFromXmpp(result,
                        GROUP_MEMBER_PAGE_ID,
                        GROUP_MEMBER_PAGE_SIZE);
                break;
            case GroupMessageType.JOIN_GROUP_FAILED:
                sendMessage(GroupMessageType.JOIN_GROUP_FAILED, result);
                break;
            case GroupMessageType.JOIN_GROUP_SUCCESS:
                sendMessage(GroupMessageType.JOIN_GROUP_SUCCESS, result);
                break;
            case GroupMessageType.GROUP_DESTROYED_SUCCESS:
                sendMessage(GroupMessageType.GROUP_DESTROYED_SUCCESS, result);
                break;
            default:
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void xmppCallback(String componentID, int notifyID, String data)
    {
        switch (notifyID)
        {
            case GroupParams.FAST_GROUP_NTF_GET_GOURP_LIST:
                handleGetGroupList(data);
                break;
            case GroupParams.FAST_GROUP_NTF_MEMBER_GET_MEMBER_LIST:
                handleGetMemberList(data);
                break;
            case GroupParams.FAST_GROUP_NTF_SEARCH_GROUP:
                handleSearchGroupList(data);
                break;
            default:
                break;
        }
        
    }
    
    /**
     * 
     * 解析创建群组返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     * @param from 从哪个页面创建的
     * @return groupId 创建群组获取到的群组id
     */
    private String handleCreateGroup(String data, int from)
    {
        // 解析数据
        GroupNotification.CreateNtfData ntfData = parseData(GroupNotification.CreateNtfData.class,
                data);
        if (ntfData == null)
        {
            sendEmptyMessage(from == GroupMessageType.CREATE_GROUP_FROM_CONVERSATION ? GroupMessageType.CREATE_GROUP_FAILED_FROM_CONVERSATION
                    : GroupMessageType.CREATE_GROUP_FAILED_FROM_GROUP);
            return null;
        }
        // 先获取返回错误码
        int errorCode = ntfData.getErrorCode();
        // 如果没有错误，则解析服务器返回内容
        if (errorCode != XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            sendMessage(from == GroupMessageType.CREATE_GROUP_FROM_CONVERSATION ? GroupMessageType.CREATE_GROUP_FAILED_FROM_CONVERSATION
                    : GroupMessageType.CREATE_GROUP_FAILED_FROM_GROUP,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(errorCode)));
            return null;
        }
        GroupInfoModel newGroup = buildGroupFromNotification(ntfData);
        if (newGroup == null)
        {
            sendEmptyMessage(from == GroupMessageType.CREATE_GROUP_FROM_CONVERSATION ? GroupMessageType.CREATE_GROUP_FAILED_FROM_CONVERSATION
                    : GroupMessageType.CREATE_GROUP_FAILED_FROM_GROUP);
            return null;
        }
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        newGroup.setRecvRolicy(GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT);
        // 保存数据库
        if (mGroupInfoDbAdapter.queryByGroupJidNoUnion(userSysId,
                newGroup.getGroupId()) != null)
        {
            mGroupInfoDbAdapter.updateByGroupJid(userSysId,
                    newGroup.getGroupId(),
                    newGroup);
        }
        else
        {
            mGroupInfoDbAdapter.insertGroupInfo(userSysId, newGroup);
        }
        
        // 把自己的信息存到成员表中
        ContactInfoModel cim = mContactInfoDbAdapter.queryByFriendSysIdWithPrivate(userSysId,
                userSysId);
        GroupMemberModel groupMemberModel = new GroupMemberModel();
        groupMemberModel.setAffiliation(GroupMemberModel.AFFILIATION_OWNER);
        groupMemberModel.setGroupId(newGroup.getGroupId());
        groupMemberModel.setMemberFaceUrl(cim.getFaceUrl());
        groupMemberModel.setMemberUserId(cim.getFriendUserId());
        groupMemberModel.setMemberNick(cim.getDisplayName());
        mGroupMemberDbAdapter.insertGroupMember(cim.getFriendSysId(),
                groupMemberModel);
        
        // 插入新建聊吧消息,如果是群，则不处理
//        if (GroupInfoModel.GROUP_TYPE_SESSION.equals(newGroup.getGroupTypeString()))
//        {
//            Logger.d(TAG,
//                    "handleCreateGroup--------->--在消息表中插入消息: 聊吧：XXXXXXXXXXXXXXXXX");
//            insertSystemMsg(userSysId,
//                    newGroup.getGroupId(),
//                    userId,
//                    newGroup.getGroupName());
//        }
        sendMessage(from == GroupMessageType.CREATE_GROUP_FROM_CONVERSATION ? GroupMessageType.CREATE_GROUP_SUCCESS_FROM_CONVERSATION
                : GroupMessageType.CREATE_GROUP_SUCCESS_FROM_GROUP,
                newGroup);
        return newGroup.getGroupId();
    }
    
    /**
     * 
     * 解析邀请群成员返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     * @param from 从哪个页面邀请的
     */
    private void handleInviteMember(String data, int from)
    {
        Logger.d(TAG, "=====邀请者收到 邀请成员加入响应=====");
        GroupNotification.MemberInviteNtfData ntfData = parseData(GroupNotification.MemberInviteNtfData.class,
                data);
        if (ntfData == null)
        {
            sendEmptyMessage(from == GroupMessageType.INVITE_MEMBER_FROM_CONVERSATION ? GroupMessageType.INVITE_MEMBER_FAILED_FROM_CONVERSATION
                    : GroupMessageType.INVITE_MEMBER_FAILED_FROM_GROUP);
            return;
        }
        // 先获取返回错误码
        int errorCode = ntfData.getErrorCode();
        // 如果没有错误，则解析服务器返回内容
        if (errorCode != XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            sendMessage(from == GroupMessageType.INVITE_MEMBER_FROM_CONVERSATION ? GroupMessageType.INVITE_MEMBER_FAILED_FROM_CONVERSATION
                    : GroupMessageType.INVITE_MEMBER_FAILED_FROM_GROUP,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(errorCode)));
            return;
        }
        sendEmptyMessage(from == GroupMessageType.INVITE_MEMBER_FROM_CONVERSATION ? GroupMessageType.INVITE_MEMBER_SUCCESS_FROM_CONVERSATION
                : GroupMessageType.INVITE_MEMBER_SUCCESS_FROM_GROUP);
        
    }
    
    /**
     * 
     * 处理群管理员删除群成员返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     * @param from 从哪个页面删除的
     */
    private void handleRemoveMember(String data, int from)
    {
        Logger.d(TAG, "=====处理删除成员响应=====");
        GroupNotification.MemberRemoveNtfData ntfData = parseData(GroupNotification.MemberRemoveNtfData.class,
                data);
        if (ntfData == null)
        {
            Logger.d(TAG, "=====ntfData is null=====");
            sendEmptyMessage(from == GroupMessageType.REMOVE_MEMBER_FROM_CONVERSATION ? GroupMessageType.REMOVE_MEMBER_FAILED_FROM_CONVERSATION
                    : GroupMessageType.REMOVE_MEMBER_FAILED_FROM_GROUP);
            return;
        }
        // 先获取返回错误码
        int errorCode = ntfData.getErrorCode();
        // 如果没有错误，则解析服务器返回内容
        if (errorCode != XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            Logger.d(TAG, "=====errorCode is not success=====");
            sendMessage(from == GroupMessageType.REMOVE_MEMBER_FROM_CONVERSATION ? GroupMessageType.REMOVE_MEMBER_FAILED_FROM_CONVERSATION
                    : GroupMessageType.REMOVE_MEMBER_FAILED_FROM_GROUP,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(errorCode)));
            return;
        }
        Logger.d(TAG, "=====errorCode is success=====");
        
        //subservices
        int[] subServices = new int[] {
                FriendManagerModel.SUBSERVICE_GROUP_WAITTING,
                FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER,
                FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF };
        
        //删除小助手相关的数据
        //获取成员的id
        String memberUserId = UriUtil.getGroupMemberIdFromJid(ntfData.getQuery()
                .getItem()
                .getMemberJid());
        
        //删除小助手中的数据
        mFriendManagerDbAdapter.deleteByFriendUserIdAndSubservices(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                subServices,
                memberUserId);
        
        sendEmptyMessage(from == GroupMessageType.REMOVE_MEMBER_FROM_CONVERSATION ? GroupMessageType.REMOVE_MEMBER_SUCCESS_FROM_CONVERSATION
                : GroupMessageType.REMOVE_MEMBER_SUCCESS_FROM_GROUP);
        
    }
    
    /**
     * 
     * 处理获取群列表返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     */
    private void handleGetGroupList(String data)
    {
        Logger.d(TAG, "======获取群列表请求响应=====");
        
        // 获取数据，封装成UI层需要的数据，并保存数据库
        GroupNotification.GetGroupListNtfData ntfData = parseData(GroupNotification.GetGroupListNtfData.class,
                data);
        if (ntfData == null)
        {
            sendEmptyMessage(GroupMessageType.GET_GROUP_LIST_FAILED);
            return;
        }
        // 先获取返回错误码
        int errorCode = ntfData.getErrorCode();
        // 如果没有错误，则解析服务器返回内容
        if (errorCode != XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            sendMessage(GroupMessageType.GET_GROUP_LIST_FAILED,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(errorCode)));
            return;
        }
        
        List<GroupInfoModel> groupList = null;
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        List<GroupInfoModel> savedList = mGroupInfoDbAdapter.queryAll(userSysId);
        groupList = buildGroupListFromNotification(ntfData);
        
        // 保存数据库
        if (groupList != null)
        {
            if (savedList != null)
            {
                boolean finded = false;
                // 和数据库中现有的进行比较，判断需要更新还是插入
                for (GroupInfoModel savedGim : savedList)
                {
                    finded = false;
                    for (GroupInfoModel newGim : groupList)
                    {
                        if (savedGim.getGroupId().equals(newGim.getGroupId()))
                        {
                            // 找到相同的，说明需要更新
                            mGroupInfoDbAdapter.updateByGroupJid(userSysId,
                                    newGim.getGroupId(),
                                    newGim);
                            groupList.remove(newGim);
                            finded = true;
                            break;
                        }
                    }
                    // 没有找到相同的，并且affiliation不是none
                    // 不是，则需要将该群的affiliation置为none，用于标示这个该用户不属于该群
                    if (!finded
                            && !GroupMemberModel.AFFILIATION_NONE.equals(savedGim.getAffiliation()))
                    {
                        //将这个群的affiliation置为none
                        mGroupInfoDbAdapter.updateGroupInfoToInvalid(userSysId,
                                savedGim.getGroupId());
                        //                        mGroupInfoDbAdapter.deleteByGroupJid(userSysId,
                        //                                savedGim.getGroupId());
                        //                        mFriendManagerDbAdapter.deleteByGroupId(userSysId,
                        //                                savedGim.getGroupId());
                        continue;
                    }
                }
            }
            
            mGroupInfoDbAdapter.applyInsertGroupInfo(userSysId, groupList);
        }
        else
        {
            if (savedList != null)
            {
                // 将所有群的affiliation置为none
                for (GroupInfoModel savedGim : savedList)
                {
                    mGroupInfoDbAdapter.updateGroupInfoToInvalid(userSysId,
                            savedGim.getGroupId());
                    //                    mGroupInfoDbAdapter.deleteByGroupJid(userSysId,
                    //                            savedGim.getGroupId());
                    //                    mFriendManagerDbAdapter.deleteByGroupId(userSysId,
                    //                            savedGim.getGroupId());
                }
            }
        }
        //处理完群列表数据，需要获取群成员（按新需求这个方法不会获取多人会话的成员列表）
        getAllGroupMemberList();
        sendEmptyMessage(GroupMessageType.GET_GROUP_LIST_SUCCESS);
        
    }
    
    /**
     * 
     * 获取所有非临时群组的群成员列表<BR>
     * 聊吧群成员不在这里获取，群成员只要获取一次，就不会主动获取
     */
    private void getAllGroupMemberList()
    {
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        List<GroupInfoModel> groupList = mGroupInfoDbAdapter.queryAll(userSysId);
        if (null != groupList)
        {
            for (GroupInfoModel model : groupList)
            {
                //1.不是聊吧
                //2.是群成员
                //3.没有获取过群成员列表
                if (!GroupInfoModel.GROUP_TYPE_SESSION.equals(model.getGroupTypeString())
                        && !GroupMemberModel.AFFILIATION_NONE.equals(model.getAffiliation())
                        && mGroupMemberDbAdapter.queryByGroupIdNoUnion(userSysId,
                                model.getGroupId()) == null)
                {
                    getMemberListFromXmpp(model.getGroupId(),
                            GROUP_MEMBER_PAGE_ID,
                            GROUP_MEMBER_PAGE_SIZE);
                }
            }
        }
        
    }
    
    /**
     * 
     * 处理群获得群成员返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     */
    private void handleGetMemberList(String data)
    {
        Logger.d(TAG, "======获取到群成员列表======");
        // 获取数据，封装成UI层需要的数据，并保存数据库
        GroupNotification.MemberGetMemberListNtfData ntfData = parseData(GroupNotification.MemberGetMemberListNtfData.class,
                data);
        if (ntfData == null)
        {
            sendEmptyMessage(GroupMessageType.GET_MEMBER_LIST_FAILED);
            return;
        }
        
        // 先获取返回错误码
        int errorCode = ntfData.getErrorCode();
        // 如果没有错误，则解析服务器返回内容
        if (errorCode != XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            sendMessage(GroupMessageType.GET_MEMBER_LIST_FAILED,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(errorCode)));
            return;
        }
        String groupId = ntfData.getFrom();
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        List<GroupMemberModel> savedList = mGroupMemberDbAdapter.queryByGroupIdNoUnion(userSysId,
                groupId);
        List<GroupMemberModel> memberList = null;
        memberList = buildMemberListFromNotification(ntfData);
        // 保存数据库
        if (memberList != null)
        {
            if (savedList != null)
            {
                boolean finded = false;
                // 和数据库中现有的进行比较，判断需要更新，插入还是删除
                for (GroupMemberModel savedMember : savedList)
                {
                    finded = false;
                    for (GroupMemberModel newGmm : memberList)
                    {
                        String memberId = newGmm.getMemberUserId();
                        String savedMemberId = savedMember.getMemberUserId();
                        if (savedMemberId.equals(memberId))
                        {
                            if (!savedMember.equals(newGmm))
                            {
                                // 找到相同的成员，且成员信息有更新，说明需要更新
                                mGroupMemberDbAdapter.updateByGroupIdAndMemberUserId(userSysId,
                                        groupId,
                                        memberId,
                                        newGmm);
                            }
                            memberList.remove(newGmm);
                            finded = true;
                            break;
                        }
                    }
                    
                    // 没有找到相同的,需要从数据库中删除
                    if (!finded)
                    {
                        mGroupMemberDbAdapter.deleteMemberByMemberUserId(userSysId,
                                groupId,
                                savedMember.getMemberUserId());
                    }
                }
            }
            
            // 批量插入新的数据
            mGroupMemberDbAdapter.applyInsertGroupMember(userSysId,
                    groupId,
                    memberList);
        }
        //最后需要联网拉取头像
        getGroupMembersFace(groupId);
        
        sendEmptyMessage(GroupMessageType.GET_MEMBER_LIST_SUCCESS);
    }
    
    /**
     * 
     * 获取群组内所有成员的头像<BR>
     * [功能详细描述] 
     * @param groupId 群组Id
     */
    private void getGroupMembersFace(String groupId)
    {
        
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        List<GroupMemberModel> list = mGroupMemberDbAdapter.queryByGroupId(userSysId,
                groupId);
        Logger.d(TAG, "=====获取群组成员头像====" + groupId);
        if (list != null && list.size() > 0)
        {
            //需要先联网获取到这些成员的详细资料，然后根据详细资料拿到成员的头像URL
            List<String> ids = new ArrayList<String>();
            for (GroupMemberModel gmm : list)
            {
                ids.add(gmm.getMemberUserId());
            }
            if (ids.size() > 0)
            {
                HashMap<String, Object> sendData = new HashMap<String, Object>();
                sendData.put("List", ids);
                new ContactInfoManager().sendDetail(sendData,
                        new IHttpListener()
                        {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void onResult(int action, Response response)
                            {
                                //联网获取到成员资料，然后循环对头像进行更新
                                ArrayList<Object> list = (ArrayList<Object>) response.getObj();
                                if (null != list && list.size() > 0)
                                {
                                    List<ContactInfoModel> result = (List<ContactInfoModel>) list.get(0);
                                    if (result != null && result.size() > 0)
                                    {
                                        for (final ContactInfoModel cim : result)
                                        {
                                            // 先判断是否有头像url
                                            if (cim.getFaceUrl() != null)
                                            {
                                                final String faceId = cim.getFriendUserId();
                                                final String faceUrl = cim.getFaceUrl();
                                                FaceManager.updateFace(mContext,
                                                        faceId,
                                                        faceUrl);
                                                // 判断是否是自定义头像
                                                if (!SystemFacesUtil.isSystemFaceUrl(faceUrl)
                                                        && null == mFaceThumbnailDbAdapter.queryByFaceId(faceId))
                                                {
                                                    FaceThumbnailModel ftm = mFaceThumbnailDbAdapter.queryByFaceId(faceId);
                                                    if (null == ftm
                                                            || null == ftm.getFaceBytes())
                                                    {
                                                        Logger.d(TAG,
                                                                "=====联网获取头像====="
                                                                        + faceUrl);
                                                        new FaceManager().loadFaceIcon(faceId,
                                                                faceUrl,
                                                                new IHttpListener()
                                                                {
                                                                    
                                                                    @Override
                                                                    public void onResult(
                                                                            int action,
                                                                            Response response)
                                                                    {
                                                                        
                                                                        if (response.getResponseCode() == ResponseCode.Succeed
                                                                                && response.getByteData() != null
                                                                                && response.getByteData().length > 0)
                                                                        {
                                                                            
                                                                            if (mFaceThumbnailDbAdapter.queryByFaceId(faceId) != null)
                                                                            {
                                                                                mFaceThumbnailDbAdapter.updateByFaceId(faceId,
                                                                                        new FaceThumbnailModel(
                                                                                                faceId,
                                                                                                faceUrl,
                                                                                                response.getByteData()));
                                                                            }
                                                                            else
                                                                            {
                                                                                mFaceThumbnailDbAdapter.insertFaceThumbnail(new FaceThumbnailModel(
                                                                                        faceId,
                                                                                        faceUrl,
                                                                                        response.getByteData()));
                                                                            }
                                                                            
                                                                        }
                                                                        
                                                                    }
                                                                    
                                                                    @Override
                                                                    public void onProgress(
                                                                            boolean isInProgress)
                                                                    {
                                                                        
                                                                    }
                                                                });
                                                        
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                mFaceThumbnailDbAdapter.deleteByFaceId(cim.getFriendUserId());
                                            }
                                        }
                                    }
                                }
                                
                            }
                            
                            @Override
                            public void onProgress(boolean isInProgress)
                            {
                                // TODO Auto-generated method stub
                                
                            }
                        });
            }
            
        }
        
    }
    
    /**
     * 
     * 处理搜索群返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     */
    private void handleSearchGroupList(String data)
    {
        Logger.d(TAG, "=====搜索群组请求响应=====");
        GroupNotification.SearchGroupNtfData ntfData = parseData(GroupNotification.SearchGroupNtfData.class,
                data);
        
        if (ntfData == null)
        {
            sendEmptyMessage(GroupMessageType.SEARCH_GROUP_FAILED);
            return;
        }
        // 先获取返回错误码
        int errorCode = ntfData.getErrorCode();
        // 如果没有错误，则解析服务器返回内容
        if (errorCode != XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            sendMessage(GroupMessageType.SEARCH_GROUP_FAILED,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(errorCode)));
            return;
        }
        
        List<GroupInfoModel> groupList = null;
        groupList = buildGroupListFromNotification(ntfData);
        
        sendMessage(GroupMessageType.SEARCH_GROUP_SUCCESS, groupList);
    }
    
    /**
     * 
     * 根据XMPP模块解析的Notification对象转成GroupInfoModel对象<BR>
     * [功能详细描述]
     * @param ntfData 创建群组响应的XMPP解析Notification
     * @return 群组对象GroupInfoModel
     */
    private GroupInfoModel buildGroupFromNotification(
            GroupNotification.CreateNtfData ntfData)
    {
        GroupInfoModel group = null;
        if (ntfData != null)
        {
            group = new GroupInfoModel();
            // 群组JID '88888888@group.im.wo.com.cn/1000001' 需要截掉后面的id号
            group.setGroupId(UriUtil.getGroupJidFromJid(ntfData.getFrom()));
            Item item = ntfData.getxNtf().getItem();
            group.setAffiliation(item.getAffiliation());
            group.setGroupDesc(item.getGroupdesc());
            group.setGroupLabel(item.getGrouplabel());
            group.setGroupName(item.getGroupname());
            group.setGroupOwnerNick(item.getGroupnick());
            if (item.getGroupsort() != null)
            {
                group.setGroupSort(Integer.parseInt(item.getGroupsort()));
            }
            group.setGroupTypeStr(item.getGroupType());
            if (item.getMaxgroupsize() != null)
            {
                group.setMaxMembers(Integer.parseInt(item.getMaxgroupsize()));
            }
        }
        return group;
    }
    
    /**
     * 
     * 根据XMPP模块解析的Notification对象转成GroupInfoModel集合<BR>
     * [功能详细描述]
     * @param ntfData 获取群列表响应的XMPP解析Notification
     * @return 群组集合
     */
    private List<GroupInfoModel> buildGroupListFromNotification(
            GroupNotification.GetGroupListNtfData ntfData)
    {
        List<GroupInfoModel> groupList = null;
        if (ntfData != null)
        {
            if (ntfData.getErrorCode() != XmppResultCode.Base.FAST_ERR_SUCCESS)
            {
                // 搜索群组失败了，需要界面提示
                return null;
            }
            
            GroupNotification.GetGroupListNtfData.QueryNtf queryNtf = ntfData.getQuery();
            
            if (queryNtf != null)
            {
                List<GroupListItemNtf> itemList = queryNtf.getItemList();
                if (itemList != null)
                {
                    groupList = new ArrayList<GroupInfoModel>();
                    for (GroupListItemNtf itemNtf : itemList)
                    {
                        GroupInfoModel group = new GroupInfoModel();
                        groupList.add(group);
                        
                        // 群组JID
                        group.setGroupId(UriUtil.getGroupJidFromJid(itemNtf.getJid()));
                        
                        // 群组头像URL
                        String logo = itemNtf.getLogo();
                        group.setFaceUrl(logo);
                        FaceManager.updateFace(mContext,
                                group.getGroupId(),
                                group.getFaceUrl());
                        group.setAffiliation(itemNtf.getAffiliation());
                        group.setGroupName(itemNtf.getName());
                        group.setGroupDesc(itemNtf.getDesc());
                        group.setGroupLabel(itemNtf.getLabel());
                        group.setGroupTypeStr(itemNtf.getType());
                        if (itemNtf.getOwner() != null)
                        {
                            group.setGroupOwnerNick(itemNtf.getOwner()
                                    .getNick());
                        }
                        
                        if (itemNtf.getSort() != null)
                        {
                            group.setGroupSort(Integer.parseInt(itemNtf.getSort()));
                        }
                        
                        if (itemNtf.getMobilepolicy() != null)
                        {
                            group.setRecvRolicy(Integer.parseInt(itemNtf.getMobilepolicy()));
                        }
                        // TODO:群组成员在群组内的唯一昵称
                        itemNtf.getNick();
                        itemNtf.getPcpolicy();
                        itemNtf.getShow();
                        itemNtf.getStatus();
                        itemNtf.getInviter();
                    }
                }
            }
        }
        return groupList;
    }
    
    /**
     * 
     * 根据XMPP模块解析的Notification对象转成GroupMemberModel集合<BR>
     * [功能详细描述]
     * @param ntfData 获取群成员列表响应的XMPP解析Notification
     * @return 群成员集合
     */
    private List<GroupMemberModel> buildMemberListFromNotification(
            GroupNotification.MemberGetMemberListNtfData ntfData)
    {
        List<GroupMemberModel> list = null;
        if (ntfData != null)
        {
            if (ntfData.getErrorCode() != XmppResultCode.Base.FAST_ERR_SUCCESS)
            {
                return null;
            }
            String from = ntfData.getFrom();
            GroupNotification.MemberGetMemberListNtfData.QueryNtf queryNtf = ntfData.getQuery();
            List<MemberItemNtf> itemList = queryNtf.getItemList();
            if (itemList != null)
            {
                list = new ArrayList<GroupMemberModel>();
                GroupMemberModel gmm = null;
                for (MemberItemNtf itemNtf : itemList)
                {
                    gmm = new GroupMemberModel();
                    gmm.setMemberNick(itemNtf.getGroupNick());
                    gmm.setMemberId(itemNtf.getMemberJid());
                    gmm.setGroupId(UriUtil.getGroupJidFromJid(from));
                    gmm.setAffiliation(itemNtf.getAffiliation());
                    gmm.setMemberUserId(UriUtil.getHitalkIdFromJid(itemNtf.getJid()));
                    
                    list.add(gmm);
                }
            }
        }
        return list;
        
    }
    
    /**
     * 
     * 插入系统消息<BR>
     * [功能详细描述]
     * 
     * @param groupId groupId
     * @param memberUserId memberUserId
     * @param msgContent msgContent
     */
    @SuppressWarnings("unused")
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
     * 
     * 群组比较器<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-10]
     */
    private class GroupComparator implements Comparator<GroupInfoModel>,
            Serializable
    {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(GroupInfoModel groupOne, GroupInfoModel groupTwo)
        {
            Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
            String pyOne = groupOne.getGroupName() == null ? ""
                    : HanziToPinyin.getInstance()
                            .getSimpleSortKey(groupOne.getGroupName());
            String pyTwo = groupTwo.getGroupName() == null ? ""
                    : HanziToPinyin.getInstance()
                            .getSimpleSortKey(groupTwo.getGroupName());
            CollationKey first = (CollationKey) (cmp.getCollationKey(pyOne) == null ? ""
                    : cmp.getCollationKey(pyOne));
            CollationKey two = (CollationKey) (cmp.getCollationKey(pyTwo) == null ? ""
                    : cmp.getCollationKey(pyTwo));
            return cmp.compare(first.getSourceString(), two.getSourceString());
        }
        
    }
    
    /**
     * 
     * 群成员比较器<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-10]
     */
    private static class GroupMemberComparator implements
            Comparator<GroupMemberModel>, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(GroupMemberModel groupOne, GroupMemberModel groupTwo)
        {
            Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
            String pyOne = groupOne.getMemberNick() == null ? ""
                    : HanziToPinyin.getInstance()
                            .getSimpleSortKey(groupOne.getMemberNick());
            String pyTwo = groupTwo.getMemberNick() == null ? ""
                    : HanziToPinyin.getInstance()
                            .getSimpleSortKey(groupTwo.getMemberNick());
            CollationKey first = (CollationKey) (cmp.getCollationKey(pyOne) == null ? ""
                    : cmp.getCollationKey(pyOne));
            CollationKey two = (CollationKey) (cmp.getCollationKey(pyTwo) == null ? ""
                    : cmp.getCollationKey(pyTwo));
            return cmp.compare(first.getSourceString(), two.getSourceString());
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<GroupMemberModel> getMemberListFromDB(String groupId)
    {
        List<GroupMemberModel> list = mGroupMemberDbAdapter.queryByGroupId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                groupId);
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getMemberListFromXmpp(String groupId, int pageID, int pageSize)
    {
        int result = mGroupXmppSender.getMemberList(groupId, pageID, pageSize);
        handleXmppResult(result,
                null,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.GET_MEMBER_LIST_FAILED);
    }
    
    /**
     * 
     * 由于在登录时不获取多人会话的成员信息，需要在用户进入某个多人会话的聊天界面时获取一遍该多人会话的成员信息<BR>
     * [功能详细描述]
     * @param groupId 群组的groupJid
     */
    public void getMemberListForMultiChat(String groupId)
    {
        GroupInfoModel groupInfoModel = mGroupInfoDbAdapter.queryByGroupJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                groupId);
        //1.是聊吧
        //2.是该聊吧的成员
        //3.没有获取过成员信息
        if (GroupInfoModel.GROUP_TYPE_SESSION.equals(groupInfoModel.getGroupTypeString())
                && !GroupMemberModel.AFFILIATION_NONE.equals(groupInfoModel.getAffiliation())
                && null == mGroupMemberDbAdapter.queryByGroupIdNoUnion(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        groupId))
        {
            getMemberListFromXmpp(groupInfoModel.getGroupId(),
                    GROUP_MEMBER_PAGE_ID,
                    GROUP_MEMBER_PAGE_SIZE);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOwner(String groupId)
    {
        GroupInfoModel mGroupInfoModel = mGroupInfoDbAdapter.queryByGroupJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                groupId);
        if (mGroupInfoModel != null)
        {
            boolean isOwner;
            isOwner = GroupMemberModel.AFFILIATION_OWNER.equals(mGroupInfoModel.getAffiliation());
            return isOwner;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasJoined(String groupID)
    {
        GroupInfoModel gim = getGroupInfoModelFromDB(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(), groupID);
        return gim != null
                && !GroupMemberModel.AFFILIATION_NONE.equals(gim.getAffiliation());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBeInvited(String groupID)
    {
        FriendManagerModel friendManagerModel = mFriendManagerDbAdapter.queryByGroupIdNoUnion(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                groupID,
                FusionConfig.getInstance().getAasResult().getUserID());
        return friendManagerModel != null
                && FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY == friendManagerModel.getSubService()
                && FriendManagerModel.STATUS_WAITTING <= friendManagerModel.getStatus();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GroupInfoModel getGroupInfoModelFromDB(String mUserSysId,
            String groupId)
    {
        return mGroupInfoDbAdapter.queryByGroupJid(mUserSysId, groupId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GroupMemberModel getGroupMemberModelFromDB(String userSysId,
            String groupId, String memberUserId)
    {
        return mGroupMemberDbAdapter.queryByMemberUserIdNoUnion(userSysId,
                groupId,
                memberUserId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContactInfoModel> getContactListForRemove(String groupId)
    {
        List<GroupMemberModel> gmmList = getMemberListFromDB(groupId);
        sortMember(gmmList);
        List<ContactInfoModel> cimList = new ArrayList<ContactInfoModel>();
        if (gmmList != null)
        {
            int size = gmmList.size();
            for (int i = 0; i < size; i++)
            {
                // 群的拥有者是不能被自己删除的
                if (GroupMemberModel.AFFILIATION_OWNER.equals(gmmList.get(i)
                        .getAffiliation()))
                {
                    continue;
                }
                ContactInfoModel cim = new ContactInfoModel();
                cim.setFriendUserId(gmmList.get(i).getMemberId());
                cim.setFriendSysId(gmmList.get(i).getMemberId());
                cim.setNickName(gmmList.get(i).getMemberNick());
                cim.setFaceUrl(gmmList.get(i).getMemberFaceUrl());
                cim.setFaceBytes(gmmList.get(i).getMemberFaceBytes());
                cimList.add(cim);
            }
        }
        return cimList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContactInfoModel> getContactListForAdd(String groupId)
    {
        List<GroupMemberModel> gmmList = getMemberListFromDB(groupId);
        List<ContactInfoModel> cimList = filterContact(gmmList);
        return cimList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMember(String memberId, String groupId, final int from)
    {
        Logger.d(TAG, "removeMember");
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_MEMBER_REMOVE)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handleRemoveMember(data, from);
                }
                
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.removeMember(FusionConfig.getInstance()
                .getAasResult()
                .getUserID(),
                groupId,
                memberId,
                GroupMemberModel.AFFILIATION_NONE,
                mContext.getResources().getString(R.string.group_remove_reason));
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                from == GroupMessageType.REMOVE_MEMBER_FROM_CONVERSATION ? GroupMessageType.REMOVE_MEMBER_FAILED_FROM_CONVERSATION
                        : GroupMessageType.REMOVE_MEMBER_FAILED_FROM_GROUP);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void submitConfigInfo(final GroupInfoModel group)
    {
        Logger.i(TAG, "=====submitConfigInfo=====");
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_SUBMIT_CONFIG_INFO)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handleSubmitConfigInfo(data, group);
                }
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.submitConfigInfo(group);
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.SUBMIT_CONFIGINFO_FAILED);
    }
    
    /**
     * 
     * 解析提交群组信息返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     * @param group 新的的群组信息
     */
    private void handleSubmitConfigInfo(String data, GroupInfoModel group)
    {
        try
        {
            GroupNotification.SubmitConfigInfoNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.SubmitConfigInfoNtfData.class,
                    data);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + data);
                sendEmptyMessage(GroupMessageType.SUBMIT_CONFIGINFO_FAILED);
                return;
            }
            if (ntfData.getErrorCode() != 0)
            {
                Logger.d(TAG,
                        "handleSubmitConfigInfo, 错误码: "
                                + ntfData.getErrorCode());
                sendMessage(GroupMessageType.SUBMIT_CONFIGINFO_FAILED,
                        FusionErrorInfo.getXmppErrInfo(mContext,
                                String.valueOf(ntfData.getErrorCode())));
                return;
            }
            else
            {
                ContentValues values = new ContentValues();
                values.put(GroupInfoColumns.GROUP_NAME, group.getGroupName());
                values.put(GroupInfoColumns.GROUP_LABEL, group.getGroupLabel());
                values.put(GroupInfoColumns.GROUP_DESC, group.getGroupDesc());
                values.put(GroupInfoColumns.GROUP_SORT, group.getGroupSort());
                values.put(GroupInfoColumns.GROUP_TYPE, group.getGroupType());
                // 更新头像
                FaceThumbnailModel ftm = new FaceThumbnailModel();
                if (null != group.getFaceBytes())
                {
                    ftm.setFaceId(group.getGroupId());
                    ftm.setFaceBytes(group.getFaceBytes());
                    ftm.setFaceUrl(group.getFaceUrl());
                    mFaceThumbnailDbAdapter.updateOrInsert(ftm);
                }
                else if (null == group.getFaceUrl())
                {
                    mFaceThumbnailDbAdapter.deleteByFaceId(group.getGroupId());
                }
                FaceManager.updateFace(mContext,
                        group.getGroupId(),
                        group.getFaceUrl());
                
                mGroupInfoDbAdapter.updateByGroupJid(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(), group.getGroupId(), values);
                sendEmptyMessage(GroupMessageType.SUBMIT_CONFIGINFO_SUCCESS);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void changeMemberInfo(String describe, String logo,
            int mobilePolicy, int pcpolicy, String to,
            final GroupInfoModel model)
    {
        Logger.i(TAG, "=====changeMemberInfo=====");
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_MEMBER_CHANGE_INFO)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handleChangeMemberInfo(data, model);
                }
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.changeMemberInfo(describe,
                logo,
                mobilePolicy,
                pcpolicy,
                to);
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.CHANGE_MEMBERINFO_FAILED);
    }
    
    /**
     * 
     * 解析更改群成员配置信息返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     * @param model 新的成员配置信息
     */
    private void handleChangeMemberInfo(String data, GroupInfoModel model)
    {
        try
        {
            GroupNotification.MemberChangeInfoNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.MemberChangeInfoNtfData.class,
                    data);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + data);
                sendEmptyMessage(GroupMessageType.CHANGE_MEMBERINFO_FAILED);
                return;
            }
            if (ntfData.getErrorCode() != 0)
            {
                Logger.d(TAG,
                        "handleChangeMemberInfo, 错误码: "
                                + ntfData.getErrorCode());
                sendMessage(GroupMessageType.CHANGE_MEMBERINFO_FAILED,
                        FusionErrorInfo.getXmppErrInfo(mContext,
                                String.valueOf(ntfData.getErrorCode())));
                return;
            }
            else
            {
                mGroupInfoDbAdapter.updateByGroupJid(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(), model.getGroupId(), model);
                sendEmptyMessage(GroupMessageType.CHANGE_MEMBERINFO_SUCCESS);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void changeMemberNick(final GroupInfoModel info,
            final GroupMemberModel member)
    {
        Logger.i(TAG, "=====changeMemberNick=====");
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_MEMBER_CHANGE_NICK)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handleChangeMemberNick(data, info, member);
                }
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.changeMemberNick(info.getGroupId(),
                member.getMemberNick());
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.CHANGE_MEMBERNICK_FAILED);
    }
    
    /**
     * 
     * 解析更改群昵称返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     * @param info 所在群组群信息
     * @param member 新的成员信息
     */
    private void handleChangeMemberNick(String content, GroupInfoModel info,
            GroupMemberModel member)
    {
        try
        {
            GroupNotification.MemberChangeNickNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.MemberChangeNickNtfData.class,
                    content);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + content);
                sendEmptyMessage(GroupMessageType.CHANGE_MEMBERNICK_FAILED);
                return;
            }
            if (ntfData.getErrorCode() != 0)
            {
                Logger.d(TAG,
                        "handleChangeMemberNick, 错误码: "
                                + ntfData.getErrorCode());
                sendMessage(GroupMessageType.CHANGE_MEMBERNICK_FAILED,
                        FusionErrorInfo.getXmppErrInfo(mContext,
                                String.valueOf(ntfData.getErrorCode())));
                return;
            }
            else
            {
                mGroupMemberDbAdapter.updateByGroupIdAndMemberUserId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        info.getGroupId(),
                        member.getMemberUserId(),
                        member);
                if (GroupMemberModel.AFFILIATION_OWNER.equals(member.getAffiliation()))
                {
                    info.setGroupOwnerNick(member.getMemberNick());
                    mGroupInfoDbAdapter.updateByGroupJid(FusionConfig.getInstance()
                            .getAasResult()
                            .getUserSysId(),
                            info.getGroupId(),
                            info);
                }
                sendEmptyMessage(GroupMessageType.CHANGE_MEMBERNICK_SUCCESS);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerGroupmemberObserver()
    {
        registerObserver(URIField.GROUPMEMBER_URI, new ContentObserver(
                new Handler())
        {
            public void onChange(boolean selfChange)
            {
                Logger.d(TAG, "=====群组成员表发生变化=====");
                sendEmptyMessage(GroupMessageType.GROUP_MEMBER_DB_CHANGED);
            }
        });
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerGroupMemberByIdObserver(final String groupId)
    {
        registerObserver(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                groupId),
                new ContentObserver(new Handler())
                {
                    public void onChange(boolean selfChange)
                    {
                        Logger.d(TAG, "=====成员信息表的一条记录发生变化=====" + groupId);
                        sendMessage(GroupMessageType.GROUP_MEMBER_DB_ONE_RECORD_CHANGED,
                                groupId);
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterGroupmemberObserver()
    {
        unRegisterObserver(URIField.GROUPMEMBER_URI);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterGroupMemberByIdObserver(String groupId)
    {
        unRegisterObserver(Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                groupId));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void quitGroup(String groupJid)
    {
        Logger.i(TAG, "=====quitGroup=====");
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_MEMBER_QUIT)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handQuitGroup(data);
                }
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.quitGroup(groupJid);
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.GROUP_QUIT_FAILED);
    }
    
    /**
     * 
     * 处理退出群返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     */
    private void handQuitGroup(String data)
    {
        try
        {
            GroupNotification.MemberQuitNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.MemberQuitNtfData.class,
                    data);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + data);
                sendEmptyMessage(GroupMessageType.GROUP_QUIT_FAILED);
                return;
            }
            if (ntfData.getErrorCode() != 0)
            {
                Logger.d(TAG, "handQuitGroup, 错误码: " + ntfData.getErrorCode());
                sendMessage(GroupMessageType.GROUP_QUIT_FAILED,
                        FusionErrorInfo.getXmppErrInfo(mContext,
                                String.valueOf(ntfData.getErrorCode())));
                return;
            }
            //            //主动退出群，删除群信息，级联删除群会话
            //            mGroupInfoDbAdapter.deleteByGroupJid(FusionConfig.getInstance()
            //                    .getAasResult()
            //                    .getUserSysId(), UriUtil.getGroupJidFromJid(groupId));
            //            //删除找朋友小助手相关的信息
            //            mFriendManagerDbAdapter.deleteByGroupId(FusionConfig.getInstance()
            //                    .getAasResult()
            //                    .getUserSysId(), UriUtil.getGroupJidFromJid(groupId));
            //            sendMessage(GroupMessageType.GROUP_QUIT_SUCCESS,
            //                    UriUtil.getGroupJidFromJid(groupId));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyGroup(String groupJid, String reason)
    {
        Logger.i(TAG, "=====destroyGroup=====");
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_DESTROY)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handleDestroyGroup(data);
                }
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.destroyGroup(groupJid, reason);
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.GROUP_DESTROY_FAILED);
    }
    
    /**
     * 
     * 处理解散群返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     */
    private void handleDestroyGroup(String content)
    {
        try
        {
            GroupNotification.DestroyNtfData ntfData = new XmlParser().parseXmlString(GroupNotification.DestroyNtfData.class,
                    content);
            if (ntfData == null)
            {
                Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + content);
                sendEmptyMessage(GroupMessageType.GROUP_DESTROY_FAILED);
                return;
            }
            if (ntfData.getErrorCode() != 0)
            {
                Logger.d(TAG,
                        "handleDestoryGroup, 错误码: " + ntfData.getErrorCode());
                sendMessage(GroupMessageType.GROUP_DESTROY_FAILED,
                        FusionErrorInfo.getXmppErrInfo(mContext,
                                String.valueOf(ntfData.getErrorCode())));
                return;
            }
            //            //主动删除群，删除群信息，级联删除群会话
            //            mGroupInfoDbAdapter.deleteByGroupJid(FusionConfig.getInstance()
            //                    .getAasResult()
            //                    .getUserSysId(),
            //                    UriUtil.getGroupJidFromJid(ntfData.getFrom()));
            //            //删除找朋友小助手相关的信息
            //            mFriendManagerDbAdapter.deleteByGroupId(FusionConfig.getInstance()
            //                    .getAasResult()
            //                    .getUserSysId(),
            //                    UriUtil.getGroupJidFromJid(ntfData.getFrom()));
            
            //将这个群的affiliation置为none
            mGroupInfoDbAdapter.updateGroupInfoToInvalid(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(),
                    UriUtil.getGroupJidFromJid(ntfData.getFrom()));
            sendMessage(GroupMessageType.GROUP_DESTROY_SUCCESS,
                    UriUtil.getGroupJidFromJid(ntfData.getFrom()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public List<GroupMemberModel> queryByGroupId(String userSysId,
            String groupId)
    {
        return mGroupMemberDbAdapter.queryByGroupId(userSysId, groupId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getConfigInfo(String groupJid)
    {
        Logger.d(TAG, "=====根据群组Jid查询群组配置信息=====");
        IXmppServiceListener listener = new IXmppServiceListener()
        {
            @Override
            public void xmppCallback(String componentID, int notifyID,
                    String data)
            {
                if (notifyID == GroupParams.FAST_GROUP_NTF_GET_CONFIG_INFO)
                {
                    mServiceSender.removeXmppServiceListener(this);
                    handleGetConfigInfo(data);
                }
            }
            
            @Override
            public void sendXmppMessage(int messageType, String result)
            {
                
            }
        };
        int result = mGroupXmppSender.getConfigInfo(groupJid);
        handleXmppResult(result,
                listener,
                GroupMessageType.INVALID_GROUP_MESSAGE,
                GroupMessageType.GET_CONFIG_INFO_FAILED);
    }
    
    /**
     * 
     * 处理获取群配置返回的数据<BR>
     * [功能详细描述]
     * @param data 返回的数据
     */
    private void handleGetConfigInfo(String content)
    {
        GroupNotification.GetConfigInfoNtfData ntfData = parseData(GroupNotification.GetConfigInfoNtfData.class,
                content);
        if (ntfData == null)
        {
            Logger.d(TAG, "解析返回的xml字符串失败, ntfData为空, 字符串为: " + content);
            sendEmptyMessage(GroupMessageType.GET_CONFIG_INFO_FAILED);
            return;
        }
        if (ntfData.getErrorCode() != 0)
        {
            Logger.d(TAG, "handleGetConfigInfo, 错误码: " + ntfData.getErrorCode());
            sendMessage(GroupMessageType.GET_CONFIG_INFO_FAILED,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(ntfData.getErrorCode())));
        }
        GroupInfoModel groupInfoModel = buildConfigInfoFromNotification(ntfData);
        //获取到群组配置后，需要更新数据库
        String userSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        if (null != groupInfoModel
                && mGroupInfoDbAdapter.queryByGroupJid(userSysId,
                        groupInfoModel.getGroupId()) != null)
        {
            // 目前只修改群组名称，标签，类型，简介，权限
            ContentValues values = new ContentValues();
            values.put(GroupInfoColumns.GROUP_NAME,
                    groupInfoModel.getGroupName());
            values.put(GroupInfoColumns.GROUP_LABEL,
                    groupInfoModel.getGroupLabel());
            values.put(GroupInfoColumns.GROUP_DESC,
                    groupInfoModel.getGroupDesc());
            values.put(GroupInfoColumns.GROUP_SORT,
                    groupInfoModel.getGroupSort());
            values.put(GroupInfoColumns.GROUP_TYPE,
                    groupInfoModel.getGroupType());
            values.put(GroupInfoColumns.GROUP_OWNERNICK,
                    groupInfoModel.getGroupOwnerNick());
            // 更新头像
            FaceManager.updateFace(mContext,
                    groupInfoModel.getGroupId(),
                    groupInfoModel.getFaceUrl());
            mGroupInfoDbAdapter.updateByGroupJid(userSysId,
                    groupInfoModel.getGroupId(),
                    values);
        }
        else
        {
            mGroupInfoDbAdapter.insertGroupInfo(userSysId, groupInfoModel);
        }
        // 发送消息返回UI
        sendMessage(GroupMessageType.GET_CONFIG_INFO_SUCCESS, groupInfoModel);
    }
    
    /**
     * 
     * 根据XMPP模块解析的Notification对象转成GroupInfoModel对象<BR>
     * [功能详细描述]
     * @param data 获取群组配置信息响应的XMPP解析Notification
     * @return 群组详情对象
     */
    private GroupInfoModel buildConfigInfoFromNotification(
            GroupNotification.GetConfigInfoNtfData data)
    {
        GroupInfoModel gim = null;
        if (data != null && data.getErrorCode() == 0)
        {
            gim = new GroupInfoModel();
            List<GroupConfigFieldData> fieldNtfList = data.getQuery()
                    .getElementX()
                    .getField();
            String groupType = null;
            String groupFixtype = null;
            for (GroupConfigFieldData fieldNtf : fieldNtfList)
            {
                if ("group#groupconfig_groupid".equals(fieldNtf.getVar()))
                {
                    // gim.setGroupId(UriUtil.buildXmppJid(fieldNtf.getValue()));
                    //gim.setGroupId(UriUtil.getHitalkIdFromJid(fieldNtf.getValue()));
                    gim.setGroupId(data.getFrom());
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
                    gim.setGroupBulletin(fieldNtf.getValue());
                }
                else if ("group#groupconfig_chattype".equals(fieldNtf.getVar()))
                {
                    gim.setChatType(fieldNtf.getValue());
                }
                else if ("group#groupconfig_groupdesc".equals(fieldNtf.getVar()))
                {
                    gim.setGroupDesc(fieldNtf.getValue());
                }
                else if ("group#groupconfig_grouplabel".equals(fieldNtf.getVar()))
                {
                    gim.setGroupLabel(fieldNtf.getValue());
                }
                else if ("group#groupconfig_grouplogo".equals(fieldNtf.getVar()))
                {
                    String logo = fieldNtf.getVar();
                    if (logo != null)
                    {
                        // 网络头像
                        if (logo.contains("http"))
                        {
                            gim.setFaceUrl(logo);
                        }
                        // 系统头像
                        else
                        {
                            int start = logo.lastIndexOf("/");
                            int end = logo.lastIndexOf(".");
                            if (start > 0 && end > start)
                            {
                                gim.setFaceUrl(logo.substring(start + 1, end));
                            }
                        }
                    }
                }
                else if ("group#groupconfig_groupname".equals(fieldNtf.getVar()))
                {
                    gim.setGroupName(fieldNtf.getValue());
                }
                else if ("group#groupconfig_groupsort".equals(fieldNtf.getVar()))
                {
                    if (fieldNtf.getValue() != null)
                    {
                        gim.setGroupSort(Integer.parseInt(fieldNtf.getValue()));
                    }
                }
                else if ("group#groupconfig_ownernick".equals(fieldNtf.getVar()))
                {
                    gim.setGroupOwnerNick(fieldNtf.getValue());
                }
            }
            // 如果是固定类型群，则从fixtype中获取内容
            if ("fixed".equals(groupType))
            {
                gim.setGroupTypeStr(groupFixtype);
            }
            else
            {
                gim.setGroupTypeStr(groupType);
            }
        }
        return gim;
    }
    
    /**
     * 
     * 对执行xmpp命令结果的处理<BR>
     * [功能详细描述]
     * @param result 执行命令结果
     * @param successMsg 成功时发送的消息
     * @param failedMsg 失败时发送的消息
     */
    private void handleXmppResult(int result, IXmppServiceListener listener,
            int successMsg, int failedMsg)
    {
        if (result == XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            if (null != listener)
            {
                mServiceSender.addXmppServiceListener(listener);
            }
            sendEmptyMessage(successMsg);
        }
        else
        {
            sendMessage(failedMsg,
                    FusionErrorInfo.getXmppErrInfo(mContext,
                            String.valueOf(result)));
        }
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
     * 
     * 解析XMPP服务器推送过来的字符串消息<BR>
     * 
     * @param type 解析类class
     * @param data 推送消息字符串
     * @return 返回对应的解析类对象
     */
    private <T> T parseData(Class<? extends T> type, String data)
    {
        try
        {
            return new XmlParser().parseXmlString(type, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.e(TAG, "xmpp xml parse failed!");
            return null;
        }
    }
}
