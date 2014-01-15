/*
 * 文件名: GroupMemberModel.java
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

import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 临时群/群成员 信息<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-21]
 */
public class GroupMemberModel implements Serializable
{
    /**
     * 群组成员岗位, owner:群组创建者
     */
    public static final String AFFILIATION_OWNER = "owner";

    /**
     * 群组成员岗位, admin:群组创建者
     */
    public static final String AFFILIATION_ADMIN = "admin";

    /**
     * 群组成员岗位, member:群组创建者
     */
    public static final String AFFILIATION_MEMBER = "member";

    /**
     * 群组成员岗位, none:群组创建者
     */
    public static final String AFFILIATION_NONE = "none";

    /**
     * 当affiliation取值为none时，
     * 该属性指明显该Pending状态的成员是处于邀请还是申请的过程中.<br>
     * invite: 该成员处于已邀请但未确认过程中
     */
    public static final String STATUS_INVITE = "invite";

    /**
     * 当affiliation取值为none时，
     * 该属性指明显该Pending状态的成员是处于邀请还是申请的过程中.<br>
     * apply: 该成员处于已申请但未批准过程中
     */
    public static final String STATUS_APPLY = "apply";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 群成员在系统的IM通信ID（即ID）
     */
    private String memberUserId;

    /**
     * 群组分配的成员ID
     */
    private String memberId;

    /**
     * 群组成员岗位 <br>
     * owner:群组创建者 <br>
     * admin:群组管理员 <br>
     * member：群组普通正式成员(Active状态) <br>
     * none：处于Pending状态的非正式群组成员
     */
    private String affiliation;

    /**
     * 用户在群组中的昵称
     */
    private String memberNick;

    /**
     * 用户在群组中的个人描述
     */
    private String memberDesc;

    /**
     * 成员头像（个性化和系统配置） <br>
     * 0，本地缓存的个性化的群头像
     *（若是自定义头像，则取 groupID + userID 命名的图片）； <br>
     * 001-999，系统群头像，
     */
    // private String memberFace;

    /**
     * 成员头像URL
     */
    private String memberFaceUrl;

    /**
     * 成员头像数据
     */
    private byte[] memberFaceBytes;

    /**
     * 当affiliation取值为none时，
     * 该属性指明显该Pending状态的成员是处于邀请还是申请的过程中：
     * invite：该成员处于已邀请但未确认过程中； <br>
     * apply：该成员处于已申请但未批准过程中；
     */
    private String status;

    /**
     * 构造方法
     */
    public GroupMemberModel()
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

    public String getMemberUserId()
    {
        return memberUserId;
    }

    public void setMemberUserId(String memberUserId)
    {
        this.memberUserId = memberUserId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getAffiliation()
    {
        return affiliation;
    }

    public void setAffiliation(String affiliation)
    {
        this.affiliation = affiliation;
    }

    public String getMemberNick()
    {
        return StringUtil.isNullOrEmpty(memberNick) ? memberUserId : memberNick;
    }

    public void setMemberNick(String memberNick)
    {
        this.memberNick = memberNick;
    }

    public String getMemberDesc()
    {
        return memberDesc;
    }

    public void setMemberDesc(String memberDesc)
    {
        this.memberDesc = memberDesc;
    }

    public String getMemberFaceUrl()
    {
        return memberFaceUrl;
    }

    public void setMemberFaceUrl(String memberFaceUrl)
    {
        this.memberFaceUrl = memberFaceUrl;
    }

    public byte[] getMemberFaceBytes()
    {
        return memberFaceBytes;
    }

    public void setMemberFaceBytes(byte[] memberFaceBytes)
    {
        this.memberFaceBytes = memberFaceBytes;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" groupId:").append(groupId);
        sb.append(" memberId:").append(memberId);
        sb.append(" memberUserId:").append(memberUserId);
        sb.append(" affiliation:").append(affiliation);
        sb.append(" memberNick:").append(memberNick);
        sb.append(" memberDesc:").append(memberDesc);
        // sb.append(" memberFace:").append(memberFace);
        sb.append(" memberFaceUrl:").append(memberFaceUrl);
        sb.append(" memberFaceBytes:").append(memberFaceBytes);
        sb.append(" status:").append(status);
        return sb.toString();
    }

}
