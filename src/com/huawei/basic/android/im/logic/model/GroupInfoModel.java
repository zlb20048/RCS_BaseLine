/*
 * 文件名: GroupInfoModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qinyangwang
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;

/**
 * 临时群/群组信息<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-21]
 */
public class GroupInfoModel implements Serializable
{
    /**
     * 预留字段，暂未使用//群组成员（非Owner、Admin）的群组内消息的控制开关： <br>
     * 不允许任何群组内消息，包括一对一和一对多；
     */
    public static final String CHATTYPE_NONE = "none";

    /**
     * 预留字段，暂未使用//群组成员（非Owner、Admin）的群组内消息的控制开关： <br>
     * 不允许任何群组内消息，包括一对一和一对多；
     */
    public static final String CHATTYPE_CHAT = "chat";

    /**
     * 预留字段，暂未使用//群组成员（非Owner、Admin）的群组内消息的控制开关： <br>
     * 不允许任何群组内消息，包括一对一和一对多；
     */
    public static final String CHATTYPE_GROUPCHAT = "groupchat";

    /**
     * 预留字段，暂未使用//群组成员（非Owner、Admin）的群组内消息的控制开关： <br>
     * 同时允许一对一和一对多群组内消息；
     */
    public static final String CHATTYPE_BOTH = "both";

    /**
     * session:多人会话； close:受限固定群组； open:开放固定群组； preassign:预置群组
     */
    public static final String GROUP_TYPE_SESSION = "session",
        GROUP_TYPE_CLOSE = "close", GROUP_TYPE_OPEN = "open",
        GROUP_TYPE_PREASSIGN = "preassign";

    /**
     * 群组类型: 多人会话
     */
    public static final int GROUPTYPE_NVN = 0;

    /**
     * 群组类型: 固定群（受限）
     */
    public static final int GROUPTYPE_LIMITED = 1;

    /**
     * 群组类型: 固定群（开放）
     */
    public static final int GROUPTYPE_OPENED = 2;

    /**
     * 群组类型: 预置群（成员不能退出）
     */
    public static final int GROUPTYPE_SYSTEM = 3;

    /**
     * 预留字段，暂未使用//群组的Owner是否接收群组消息: 不接收群组消息
     */
    public static final int OWNERCHAT_REFUSE = 0;

    /**
     * 预留字段，暂未使用//群组的Owner是否接收群组消息: 接收群组消息
     */
    public static final int OWNERCHAT_ACCEPT = 1;

    /**
     * 群消息接收策略： 接收并提示消息
     */
    public static final int RECVPOLICY_ACCEPT_PROMPT = 2;
    
    /**
     * 群消息接收策略： 接收消息但不提示
     */
    public static final int RECVPOLICY_ACCEPT_NOT_PROMPT = 3;

    /**
     * 群消息接收策略：完全屏蔽群内消息
     */
    public static final int RECVPOLICY_REFUSE = 5;

    /**
     * 删除标记，0：正常
     */
    public static final int DELFLAG_NORMAL = 0;

    /**
     * 删除标记，1：服务器侧该群已删除
     */
    public static final int DELFLAG_DEL = 1;

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 群组描述
     */
    private String groupDesc;

    /**
     * 群头像（个性化和系统配置） <br>
     * 0，本地缓存的个性化的群头像（若是自定义头像，则取groupID命名的图片）； <br>
     * 001-999，系统群头像，
     */
    // private String face;

    /**
     * 个性化群头像存放服务器地址, anroid中 统一放入"头像表"
     */
    private String faceUrl;

    /**
     * 头像数据
     */
    private byte[] faceBytes;

    /**
     * 群组标签，内部按逗号分离，长度受限为300字节
     */
    private String groupLabel;

    /**
     * 【预留字段】，暂未使用//群组成员（非Owner、Admin）的群组内消息的控制开关： <br>
     * none：不允许任何群组内消息，包括一对一和一对多； <br>
     * chat：仅允许一对一群组内消息； <br>
     * groupchat：仅允许一对多群组内消息； <br>
     * both：同时允许一对一和一对多群组内消息； <br>
     * 对预置群组该配置项的取值根据业务场景的要求进行变化； <br>
     * 对于其他群组的取值固定为both；
     */
    private String chatType;

    /**
     * 群组的分类索引，当前的索引取值范围如下：
     * 0-20，其中0表示未分类，当用户创建群组不指定分类时取值为0
     */
    private int groupSort;

    /**
     * 群组类型 
     * 0：多人会话 1：固定群（受限） 2：固定群（开放） 3：预置群（成员不能退出）
     */
    private int groupType;

    /**
     * 群组公告
     */
    private String groupBulletin;

    /**
     * 群消息接收策略： <br>
     * 0 :自动弹出消息； <br>
     * 1）接收并提示消息； <br>
     * 2）接收不提示消息； <br>
     * 3）不提示消息只显示数目（屏蔽但是显示消息数目）； <br>
     * 4）完全屏蔽群内消息； <br>
     * 手机简化实现，只支持1 4两个值
     */
    private int recvRolicy;

    /**
     * 群组最大成员数
     */
    private int maxMembers;

    /**
     * 最后一次更新的UTC时间戳
     */
    private String lastUpdate;

    /**
     * 删除标记 <br>
     * 0 ：正常； <br>
     * 1 ：服务器侧该群已删除 <br>
     */
    private int delFlag;

    /**
     * 我在群组中的岗位 <br>
     * owner:群组创建者 <br>
     * admin:群组管理员 <br>
     * member：群组普通正式成员(Active状态) <br>
     * none：处于Pending状态的非正式群组成员
     */
    private String affiliation;

    /**
     * 当affiliation取值为none时，
     * 该属性指明显该Pending状态的成员是处于邀请还是申请的过程中：<br>
     * invite：该成员处于已邀请但未确认过程中；<br>
     * apply：该成员处于已申请但未批准过程中；
     */
    private String proceeding;

    /**
     * 群组中的人数
     */
    private int memberCount;

    /**
     * 未读消息数
     */
    private int unReadMsg;

    /**
     * 群的人气值
     */
    private String popularNumber;

    /**
     * 群主ID
     */
    private String groupOwnerUserId;

    /**
     * 群主的昵称
     */
    private String groupOwnerNick;

    /**
     * 群主头像(暂时没处理)
     */
    // private String groupOwnerFace;

    /**
     * 构造方法
     */
    public GroupInfoModel()
    {
        super();
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getGroupDesc()
    {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc)
    {
        this.groupDesc = groupDesc;
    }

    public String getFaceUrl()
    {
        return faceUrl;
    }

    public byte[] getFaceBytes()
    {
        return faceBytes;
    }

    public void setFaceBytes(byte[] faceBytes)
    {
        this.faceBytes = faceBytes;
    }

    public void setFaceUrl(String faceUrl)
    {
        this.faceUrl = faceUrl;
    }

    public String getGroupLabel()
    {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel)
    {
        this.groupLabel = groupLabel;
    }

    public String getChatType()
    {
        return chatType;
    }

    public void setChatType(String chatType)
    {
        this.chatType = chatType;
    }

    public int getGroupSort()
    {
        return groupSort;
    }

    public void setGroupSort(int groupSort)
    {
        this.groupSort = groupSort;
    }

    public int getGroupType()
    {
        return groupType;
    }

    public void setGroupType(int groupType)
    {
        this.groupType = groupType;
    }

    /**
     * 设置群组类型字符串<BR>
     * [功能详细描述]
     * 
     * @param type 类型
     */
    public void setGroupTypeStr(String type)
    {
        if (GROUP_TYPE_SESSION.equals(type))
        {
            groupType = GROUPTYPE_NVN;
        }
        else if (GROUP_TYPE_CLOSE.equals(type))
        {
            groupType = GROUPTYPE_LIMITED;
        }
        else if (GROUP_TYPE_OPEN.equals(type))
        {
            groupType = GROUPTYPE_OPENED;
        }
        else if (GROUP_TYPE_PREASSIGN.equals(type))
        {
            groupType = GROUPTYPE_SYSTEM;
        }
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @return String
     */
    public String getGroupTypeString()
    {
        String strType;
        switch (groupType)
        {
            case GROUPTYPE_NVN:
                strType = GROUP_TYPE_SESSION;
                break;
            case GROUPTYPE_LIMITED:
                strType = GROUP_TYPE_CLOSE;
                break;
            case GROUPTYPE_OPENED:
                strType = GROUP_TYPE_OPEN;
                break;
            case GROUPTYPE_SYSTEM:
                strType = GROUP_TYPE_PREASSIGN;
                break;
            default:
                strType = null;
                break;
        }

        return strType;
    }

    public String getGroupBulletin()
    {
        return groupBulletin;
    }

    public void setGroupBulletin(String groupBulletin)
    {
        this.groupBulletin = groupBulletin;
    }

    public String getProceeding()
    {
        return proceeding;
    }

    public void setProceeding(String proceeding)
    {
        this.proceeding = proceeding;
    }

    public String getGroupOwnerUserId()
    {
        return groupOwnerUserId;
    }

    public void setGroupOwnerUserId(String groupOwnerUserId)
    {
        this.groupOwnerUserId = groupOwnerUserId;
    }

    public int getRecvRolicy()
    {
        return recvRolicy;
    }

    public void setRecvRolicy(int recvRolicy)
    {
        this.recvRolicy = recvRolicy;
    }

    public int getMaxMembers()
    {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
    }

    public String getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public int getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(int delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getAffiliation()
    {
        return affiliation;
    }

    public void setAffiliation(String affiliation)
    {
        this.affiliation = affiliation;
    }

    public int getMemberCount()
    {
        return memberCount;
    }

    public void setMemberCount(int memberCount)
    {
        this.memberCount = memberCount;
    }

    public int getUnReadMsg()
    {
        return unReadMsg;
    }

    public void setUnReadMsg(int unReadMsg)
    {
        this.unReadMsg = unReadMsg;
    }

    public String getPopularNumber()
    {
        return popularNumber;
    }

    public void setPopularNumber(String popularNumber)
    {
        this.popularNumber = popularNumber;
    }

    public String getGroupOwnerNick()
    {
        return groupOwnerNick;
    }

    public void setGroupOwnerNick(String groupOwnerNick)
    {
        this.groupOwnerNick = groupOwnerNick;
    }

    /**
     * Override<BR>
     * 
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" groupId:").append(groupId);
        sb.append(" groupName:").append(groupName);
        sb.append(" groupDesc:").append(groupDesc);
        // sb.append(" face:").append(face);
        sb.append(" faceUrl:").append(faceUrl);
        sb.append(" faceBytes:").append(faceBytes);
        sb.append(" groupLabel:").append(groupLabel);
        sb.append(" groupSort:").append(groupSort);
        sb.append(" groupType:").append(groupType);
        sb.append(" groupBulletin:").append(groupBulletin);
        sb.append(" recvRolicy:").append(recvRolicy);
        sb.append(" maxMembers:").append(maxMembers);
        sb.append(" lastUpdate:").append(lastUpdate);
        sb.append(" delFlag:").append(delFlag);
        sb.append(" groupOwnerNick:").append(groupOwnerNick);
        sb.append(" memberCount:").append(memberCount);
        sb.append(" unReadMsg:").append(unReadMsg);
        sb.append(" groupOwnerUserId:").append(groupOwnerUserId);
        // sb.append(" groupOwnerFace:").append(groupOwnerFace);
        sb.append(" proceeding:").append(proceeding);
        return sb.toString();
    }

}
