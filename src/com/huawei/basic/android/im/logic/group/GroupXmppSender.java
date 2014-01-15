/*
 * 文件名: GroupXmppSender.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 主要是根据需要生成发送请求的字符串，并调用sender执行executeCommand
 * 创建人: tjzhang
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.group;

import java.util.List;

import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams.GroupParams;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseRetData;
import com.huawei.basic.android.im.component.net.xmpp.data.CommonXmppCmdGenerator.GroupConfigFieldData;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupData;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * 生成发送请求字符串，然后执行executeCommand<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public class GroupXmppSender
{
    private static final String TAG = "GroupXmppSender";
    
    private IServiceSender mXmppSender;
    
    /**
     * 
     * 构造函数，需要传进IServiceSender 对象
     * @param sender IServiceSender对象
     */
    public GroupXmppSender(IServiceSender sender)
    {
        mXmppSender = sender;
    }
    
    /**
     * 
     * 从xmppServer获取群组列表<BR>
     * @param from 发送者的jid
     * 
     * @return 执行结果 0 标示成功
     */
    public int getGroupList(String from)
    {
        Logger.d(TAG, "getGroupList");
        String resultString = mXmppSender.executeCommand(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_CMD_GET_GROUP_LIST,
                getGroupListXml(from));
        
        return getResultCode(resultString);
    }
    
    /**|
     * 
     * 从xmpp获取成员列表<BR>
     * [功能详细描述]
     * @param groupId 群组ID
     * @param pageID pageID
     * @param pageSize pageSize
     * @return 执行结果 0标示成功
     */
    public int getMemberList(String groupId, int pageID, int pageSize)
    {
        Logger.d(TAG, "getMemberList");
        String resultString = mXmppSender.executeCommand(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_CMD_MEMBER_GET_MEMBER_LIST,
                getMemberListXml(UriUtil.buildXmppGroupJID(groupId),
                        pageID,
                        pageSize));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 组装创建群组命令，并发送请求<BR>
     * [功能详细描述]
     * @param from 发送者的jid
     * @param model GroupInfoModel
     * @param groupNickName groupOwnerNick 群组创建者的昵称
     * @return 执行结果 0 标示成功
     */
    public int createGroup(String from, GroupInfoModel model,
            String groupNickName)
    {
        Logger.d(TAG, "createGroup");
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_CREATE,
                createGroupXml(from, model, groupNickName));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 邀请加入群组<BR>
     * [功能详细描述]
     * 
     * @param from 发送者的jid
     * @param groupJid 群组jid
     * @param userNick nick
     * @param logo logo
     * @param memberIdList 被邀请的用户的id集合
     * @param reason 邀请原因
     * @return 执行结果，0为成功
     */
    public int inviteMember(String from, String groupJid, String userNick,
            String logo, List<String[]> memberIdList, String reason)
    {
        Logger.d(TAG, "inviteMember");
        
        // 注：在邀请被同意后，服务器会返回“新成员加入”的响应，在GroupServiceManager中已经订阅和处理
        
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_INVITE,
                inviteMemberXml(from,
                        groupJid,
                        reason,
                        userNick,
                        logo,
                        memberIdList));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param memberId 要删除的群成员的jid
     * @param from 删除者的jid
     * @param affiliation 角色
     * @param reason 删除原因
     * @param to 群id
     * @return 执行结果，0为成功
     */
    public int removeMember(String from, String to, String memberId,
            String affiliation, String reason)
    {
        Logger.d(TAG, "removeMember");
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_REMOVE,
                removeMemberXml(from, to, memberId, affiliation, reason));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 接受群主邀请加入群的请求<BR>
     * [功能详细描述]
     * @param from from
     * @param to to
     * @param describe describe
     * @param groupNick groupNick
     * @param mobilePolicy mobilePolicy
     * @param pcpolicy pcpolicy
     * @return 执行结果，0为成功
     */
    public int acceptInvite(String from, String to, String describe,
            String groupNick, int mobilePolicy, int pcpolicy)
    {
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_INVITE_ACCEPT,
                acceptInviteXml(from,
                        UriUtil.buildXmppGroupJID(to),
                        describe,
                        groupNick,
                        mobilePolicy,
                        pcpolicy));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 拒绝邀请加入群的请求<BR>
     * [功能详细描述]
     * @param from from
     * @param to 群组ID
     * @param reason 拒绝原因
     * @param declineTo 被拒绝者的ID
     * @return 执行结果，0为成功
     */
    public int declineInvite(String from, String to, String reason,
            String declineTo)
    {
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_INVITE_DECLINE,
                declineInviteXml(from, to, reason, declineTo));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 申请加入群请求<BR>
     * [功能详细描述]
     * @param from from
     * @param to to 
     * @param describe describe
     * @param nick 昵称
     * @param mobilePolicy mobilePolicy
     * @param pcpolicy pcpolicy
     * @param reason 申请原因
     * @return 执行结果，0为成功
     */
    public int joinGroup(String from, String to, String describe, String nick,
            int mobilePolicy, int pcpolicy, String reason)
    {
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_JOIN_APPLY,
                joinGroupXml(from,
                        to,
                        describe,
                        nick,
                        mobilePolicy,
                        pcpolicy,
                        reason));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 获取群组配置信息<BR>
     * [功能详细描述]
     * @param to 【必选】：群组jid<Br>
     * @return 执行结果，0为成功
     */
    public int getConfigInfo(String to)
    {
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_GET_CONFIG_INFO,
                getConfigInfoXml(to));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 同意加入群组请求<BR>
     * [功能详细描述]
     * @param from from
     * @param groupId 群组Id
     * @param userId 被同意的用户Id
     * @param affiliation 在群组中的角色
     * @return 执行结果，0为成功
     */
    public int acceptJoin(String from, String groupId, String userId,
            String affiliation)
    {
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_JOIN_ACCEPT,
                acceptJoinXml(from, groupId, userId, affiliation));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 拒绝加入群申请的请求<BR>
     * [功能详细描述]
     * @param from from
     * @param groupJid 申请加入的群的id
     * @param joiningUserJid 申请者的id
     * @param reason 拒绝的原因
     * @return 执行结果，0为成功
     */
    public int declineJoin(String from, String groupJid, String joiningUserJid,
            String reason)
    {
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_JOIN_DECLINE,
                declineJoinXml(from, groupJid, joiningUserJid, reason));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 搜索群组<BR>
     * [功能详细描述]
     * 
     * @param from from
     * @param pageID pageID
     * @param pageSize pageSize
     * @param searchKey searchKey
     * @param searchType searchType
     * @return 执行结果，0为成功
     */
    public int searchGroupList(String from, int pageID, int pageSize,
            String searchKey, String searchType)
    {
        String resultString = mXmppSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_SEARCH_GROUP,
                searchGroupListXml(from,
                        pageID,
                        pageSize,
                        searchKey,
                        searchType));
        return getResultCode(resultString);
    }
    
    /**
     * 执行更新群组配置信息的命令
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param group 群组对象
     * @return int 返回执行结果
     */
    public int submitConfigInfo(GroupInfoModel group)
    {
        String resultString = mXmppSender.executeCommand(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_CMD_SUBMIT_CONFIG_INFO,
                submitConfigInfoXml(group));
        return getResultCode(resultString);
    }
    
    /**
     * 更新群组成员的信息
     * [一句话功能简述]<BR>
     * [功能详细描述]
     *@param describe describe
     * @param logo logo
     * @param mobilePolicy mobilePolicy
     * @param pcpolicy pcpolicy
     * @param to pcpolicy
     * @return String 执行结果，0为成功
     */
    public int changeMemberInfo(String describe, String logo, int mobilePolicy,
            int pcpolicy, String to)
    {
        String resultString = mXmppSender.executeCommand(GroupParams.FAST_COM_GROUP_ID,
                BaseParams.GroupParams.FAST_GROUP_CMD_MEMBER_CHANGE_INFO,
                changeMemberInfoXml(describe,
                        logo,
                        mobilePolicy,
                        pcpolicy,
                        UriUtil.buildXmppGroupJID(to)));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupJid 群组JID
     * @param nick 昵称
     * @return 返回执行结果
     */
    public int changeMemberNick(String groupJid, String nick)
    {
        String resultString = mXmppSender.executeCommand(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_CMD_MEMBER_CHANGE_NICK,
                changeMemberNickXml(UriUtil.buildXmppGroupJID(groupJid), nick));
        
        return getResultCode(resultString);
    }
    
    /**
     * 申请退出群
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupJid 群组Jid
     * @return 执行结果，0为成功
     */
    public int quitGroup(String groupJid)
    {
        String resultString = mXmppSender.executeCommand(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_CMD_MEMBER_QUIT,
                quitGroupXml(UriUtil.buildXmppGroupJID(groupJid)));
        
        return getResultCode(resultString);
    }
    
    /**
     * 关闭并解散群
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupJid 群组Jid
     * @param reason 删除群组的原因
     * @return 执行结果，0为成功
     */
    public int destroyGroup(String groupJid, String reason)
    {
        String resultString = mXmppSender.executeCommand(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_CMD_DESTROY,
                deleteGroupXml(UriUtil.buildXmppGroupJID(groupJid), reason));
        
        return getResultCode(resultString);
    }
    
    /**
     * 
     * 获取群组列表时封装请求XML字符串<BR>
     * @param from 发送者的jid(~/@~)
     * @return 请求XML字符串
     */
    private String getGroupListXml(String from)
    {
        GroupData.GetGroupListCmdData getGroupListData = new GroupData.GetGroupListCmdData();
        getGroupListData.setFrom(UriUtil.buildXmppJid(from));
        getGroupListData.setTo(FusionCode.XmppConfig.GROUP_JID);
        return getGroupListData.makeCmdData();
    }
    
    /**
     * 
     * 获取群成员列表时发送的字符串<BR>
     * [功能详细描述]
     * @param groupId 群组id
     * @param pageId   pageId
     * @param pageSize pageSize
     * @return 请求XML字符串
     */
    private String getMemberListXml(String groupId, int pageId, int pageSize)
    {
        GroupData.MemberGetMemberListCmdData data = new GroupData.MemberGetMemberListCmdData();
        data.setFrom(UriUtil.buildXmppJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserID()));
        data.setTo(groupId);
        data.setPageID(pageId);
        data.setPageSize(pageSize);
        return data.makeCmdData();
    }
    
    /**
     * 
     * 获取创建群组字符串<BR>
     * [功能详细描述]
     * @param from from 发送者的jid(~/@~)
     * @param group GroupInfoModel
     * @param groupOwnerNick 群组创建者的昵称
     * @return 请求XML字符串
     */
    private String createGroupXml(String from, GroupInfoModel group,
            String groupOwnerNick)
    {
        GroupData.CreateCmdData groupData = new GroupData.CreateCmdData();
        
        groupData.setFrom(UriUtil.buildXmppJid(from));
        groupData.setTo(FusionCode.XmppConfig.GROUP_JID);
        groupData.setGroupDesc(group.getGroupDesc());
        groupData.setGroupLabel(group.getGroupLabel());
        groupData.setGroupName(group.getGroupName());
        groupData.setGroupNick(groupOwnerNick);
        groupData.setGroupSort(group.getGroupSort());
        groupData.setGroupType(group.getGroupTypeString());
        
        return groupData.makeCmdData();
    }
    
    private String inviteMemberXml(String from, String groupJid, String reason,
            String nick, String logo, List<String[]> itemList)
    {
        GroupData.MemberInviteCmdData cmdData = new GroupData.MemberInviteCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(from));
        cmdData.setTo(groupJid);
        cmdData.setReason(reason);
        cmdData.setPerson(nick, logo);
        cmdData.addItem(itemList);
        return cmdData.makeCmdData();
    }
    
    /**
     * 
     * 从群组中删除成员<BR>
     * [功能详细描述]
     * @param from 发送者jid
     * @param groupJid 群组jid
     * @param reason 删除原因
     * @param affiliation 角色
     * @return 执行结果，"0"为成功
     */
    private String removeMemberXml(String from, String groupJid,
            String memberJid, String affiliation, String reason)
    {
        GroupData.MemberRemoveCmdData cmdData = new GroupData.MemberRemoveCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(from));
        cmdData.setTo(groupJid);
        cmdData.setReason(reason);
        cmdData.setAffiliation(affiliation);
        cmdData.setMemberJid(memberJid);
        return cmdData.makeCmdData();
    }
    
    private String acceptInviteXml(String from, String to, String describe,
            String groupNick, int mobilePolicy, int pcpolicy)
    {
        GroupData.MemberInviteAcceptCmdData cmdData = new GroupData.MemberInviteAcceptCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(from));
        cmdData.setTo(to);
        cmdData.setDescribe(describe);
        cmdData.setGroupNick(groupNick);
        cmdData.setMobilePolicy(mobilePolicy);
        cmdData.setPCPolicy(pcpolicy);
        return cmdData.makeCmdData();
    }
    
    private String declineInviteXml(String from, String to, String reason,
            String declineTo)
    {
        GroupData.MemberInviteDeclineCmdData cmdData = new GroupData.MemberInviteDeclineCmdData();
        cmdData.setFrom(from);
        cmdData.setTo(to);
        cmdData.setReason(reason);
        cmdData.setDeclineTo(UriUtil.buildXmppJidNoWo(declineTo));
        return cmdData.makeCmdData();
    }
    
    private String joinGroupXml(String from, String to, String describe,
            String nick, int mobilePolicy, int pcpolicy, String reason)
    {
        GroupData.MemberJoinApplyCmdData cmdData = new GroupData.MemberJoinApplyCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(from));
        cmdData.setTo(to);
        cmdData.setDescribe(describe);
        cmdData.setGroupNick(nick);
        cmdData.setMobilePolicy(mobilePolicy);
        cmdData.setPCPolicy(pcpolicy);
        cmdData.setReason(reason);
        return cmdData.makeCmdData();
    }
    
    /**
     * 
     * 生成获取群组配置请求的XML<BR>
     * [功能详细描述]
     * @param to 群组Jid
     * @return 生成的xml文件
     */
    private String getConfigInfoXml(String to)
    {
        GroupData.GetConfigInfoCmdData data = new GroupData.GetConfigInfoCmdData();
        data.setFrom(UriUtil.buildXmppJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserID()));
        data.setTo(to);
        return data.makeCmdData();
    }
    
    private String acceptJoinXml(String from, String groupId, String userId,
            String affiliation)
    {
        GroupData.MemberJoinAcceptCmdData cmdData = new GroupData.MemberJoinAcceptCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(from));
        cmdData.setTo(groupId);
        cmdData.setJid(UriUtil.buildXmppJidNoWo(userId));
        cmdData.setAffiliation(affiliation);
        return cmdData.makeCmdData();
    }
    
    private String declineJoinXml(String from, String groupJid,
            String joiningUserJid, String reason)
    {
        GroupData.MemberJoinDeclineCmdData cmdData = new GroupData.MemberJoinDeclineCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(from));
        cmdData.setTo(groupJid);
        cmdData.setDeclineTo(UriUtil.buildXmppJid(joiningUserJid));
        cmdData.setReason(reason);
        return cmdData.makeCmdData();
    }
    
    private String searchGroupListXml(String from, int pageID, int pageSize,
            String searchKey, String searchType)
    {
        GroupData.SearchGroupCmdData searchData = new GroupData.SearchGroupCmdData();
        searchData.setFrom(UriUtil.buildXmppJid(from));
        searchData.setPageID(pageID);
        searchData.setPageSize(pageSize);
        searchData.setSearchKey(searchKey);
        searchData.setSearchType(searchType);
        searchData.setTo(FusionCode.XmppConfig.GROUP_JID);
        return searchData.makeCmdData();
    }
    
    /**
     * 
     * 生成更新群组配置请求的XML<BR>
     * [功能详细描述]
     * @param group 群组对象
     * @return 请求的xml
     */
    private String submitConfigInfoXml(GroupInfoModel group)
    {
        GroupData.SubmitConfigInfoCmdData data = new GroupData.SubmitConfigInfoCmdData();
        data.setFrom(UriUtil.buildXmppJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserID()));
        data.setTo(UriUtil.buildXmppGroupJID(group.getGroupId()));
        data.addField(new GroupConfigFieldData("FORM_TYPE",
                "http://jabber.org/protocol/group#groupconfig"));
        if (!"session".equals(group.getGroupTypeString()))
        {
            // 如果不是Session类型的群，则先增加fixed字段，再增加type字段， Session不上报GroupType字段
            data.addField(new GroupConfigFieldData(
                    "group#groupconfig_grouptype", "fixed"));
            data.addField(new GroupConfigFieldData("group#groupconfig_fixtype",
                    group.getGroupTypeString()));
        }
        data.addField(new GroupConfigFieldData(
                "group#groupconfig_groupbulletin", group.getGroupBulletin()));
        data.addField(new GroupConfigFieldData("group#groupconfig_chattype",
                group.getChatType()));
        data.addField(new GroupConfigFieldData("group#groupconfig_groupdesc",
                group.getGroupDesc()));
        data.addField(new GroupConfigFieldData("group#groupconfig_grouplabel",
                group.getGroupLabel()));
        data.addField(new GroupConfigFieldData("group#groupconfig_grouplogo",
                group.getFaceUrl()));
        data.addField(new GroupConfigFieldData("group#groupconfig_groupname",
                group.getGroupName()));
        data.addField(new GroupConfigFieldData("group#groupconfig_groupsort",
                String.valueOf(group.getGroupSort())));
        return data.makeCmdData();
    }
    
    /**
     * 生成更新群组成员信息的xml
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param describe describe
     * @param logo logo
     * @param mobilePolicy mobilePolicy
     * @param pcpolicy pcpolicy
     * @param to pcpolicy
     * @return String 生成的xml文件
     */
    private String changeMemberInfoXml(String describe, String logo,
            int mobilePolicy, int pcpolicy, String to)
    {
        GroupData.MemberChangeInfoCmdData cmdData = new GroupData.MemberChangeInfoCmdData();
        cmdData.setDescribe(describe);
        cmdData.setFrom(UriUtil.buildXmppJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserID()));
        cmdData.setLogo(logo);
        cmdData.setMobilePolicy(mobilePolicy);
        cmdData.setPCPolicy(pcpolicy);
        cmdData.setTo(to);
        return cmdData.makeCmdData();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupJid 群组JID
     * @param nick 昵称
     * @return String 返回请求修改昵称的xml
     */
    private String changeMemberNickXml(String groupJid, String nick)
    {
        GroupData.MemberChangeNickCmdData cmdData = new GroupData.MemberChangeNickCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserID()));
        cmdData.setGroupNick(nick);
        cmdData.setTo(groupJid);
        return cmdData.makeCmdData();
    }
    
    /**
     * 生成请求退去群组的xml
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupJid 群组Jid
     * @return 所生成的xml
     */
    private String quitGroupXml(String groupJid)
    {
        GroupData.MemberQuitCmdData cmdData = new GroupData.MemberQuitCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserID()));
        cmdData.setTo(groupJid);
        return cmdData.makeCmdData();
    }
    
    /**
     * 组装删除群组请求的xml字符串<BR>
     * [功能详细描述]
     * @param strTo xml中to字段
     * @param reason 删除群组的原因
     * @return 删除群组请求的xml字符串
     */
    private String deleteGroupXml(String strTo, String reason)
    {
        GroupData.DestroyCmdData deleteGroupData = new GroupData.DestroyCmdData();
        deleteGroupData.setFrom(UriUtil.buildXmppJid(FusionConfig.getInstance()
                .getAasResult()
                .getUserID()));
        deleteGroupData.setTo(strTo);
        deleteGroupData.setReason(reason);
        return deleteGroupData.makeCmdData();
    }
    
    /**
     * 
     * 解析执行命令返回值<BR>
     * [功能详细描述]
     * @param resultString
     * @return 成功标志
     */
    private int getResultCode(String resultString)
    {
        BaseRetData.Group groupData = null;
        try
        {
            groupData = new XmlParser().parseXmlString(BaseRetData.Group.class,
                    resultString);
        }
        catch (Exception e)
        {
            Logger.e(TAG, " parseXmlString 解析错误!");
        }
        if (null != groupData)
        {
            return groupData.getRet();
        }
        return -1;
    }
}
