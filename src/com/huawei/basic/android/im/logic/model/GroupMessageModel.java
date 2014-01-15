/*
 * 文件名: GroupMessageModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 28, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

/**
 * 临时群/群消息<BR>
 * 
 * @author 张仙
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 28, 2012] 
 */
public class GroupMessageModel extends BaseMessageModel
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3L;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * HitalkID，消息发送者的HitalkID <br>
     * 0，表示群内的通知类型消息（加入群，离开群等）
     */
    private String memberUserId;
    
    /**
     * 群成员昵称
     */
    private String memberNick;

    /**
     * 构造方法
     */
    public GroupMessageModel()
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

    public String getMemberNick()
    {
        return memberNick;
    }

    public void setMemberNick(String memberNick)
    {
        this.memberNick = memberNick;
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
        sb.append(" msgId:").append(this.getMsgId());
        sb.append(" msgSequence:").append(this.getMsgSequence());
        sb.append(" groupId:").append(groupId);
        sb.append(" memberUserId:").append(memberUserId);
        sb.append(" memberNick:").append(memberNick);
        sb.append(" msgTime:").append(this.getMsgTime());
        sb.append(" msgType:").append(this.getMsgType());
        sb.append(" msgContent:").append(this.getMsgContent());
        sb.append(" msgStatus:").append(this.getMsgStatus());
        sb.append(" msgSendOrRecv:").append(this.getMsgSendOrRecv());
        sb.append(" mediaIndex:").append(this.getMediaIndex());
        return sb.toString();
    }
}
