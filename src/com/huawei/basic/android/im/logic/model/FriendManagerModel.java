/*
 * 文件名: FriendManagerModel.java
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
 * 
 * 正在加的好友(找朋友小助手)信息<BR>
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-20]
 */
public class FriendManagerModel implements Serializable
{

    /**
     * SERIAL_KEY
     */
    public static final String SERIAL_KEY = "FriendManagerModel";
    
    /**
     * 操作状态： 同意
     */
    public static final int STATUS_AGREE = 1;

    /**
     * 操作状态： 拒绝
     */
    public static final int STATUS_REFUSE = 2;

    /**
     * 操作状态： 等待处理
     */
    public static final int STATUS_WAITTING = 3;

    /**
     * 操作状态： 正在发送
     */
    public static final int STATUS_SENDDING = 4;

    /**
     * 操作状态：发送失败
     */
    public static final int STATUS_SEND_FAIL = 5;

    /**
     * 操作状态：同意发送中
     */
    public static final int STATUS_AGREE_SEND_SENDDING = 6;

    /**
     * 操作状态：拒绝发送中
     */
    public static final int STATUS_AGREE_SEND_FAIL = 7;

    /**
     * 操作状态：拒绝发送中
     */
    public static final int STATUS_REFUSE_SEND_SENDDING = 8;

    /**
     * 操作状态：拒绝发送失败
     */
    public static final int STATUS_REFUSE_SEND_FAIL = 9;

    /**
     * 操作状态 自动同意 sending
     */
    public static final int STATUS_AUTO_AGREE_SENDING = 10;

    /**
     * 子业务类型：系统自动匹配好友（双方号簿均有联系方式）
     */
    public static final int SUBSERVICE_SYSTEM_MATCH = 1;

    /**
     * 子业务类型：邀请方注册自动成为好友的
     */
    public static final int SUBSERVICE_INVITE_REGISTER = 2;

    /**
     * 子业务类型：用户手动加的好友
     */
    public static final int SUBSERVICE_ADD_FRIEND = 3;

    /**
     * 子业务类型：用户加我为好友
     */
    public static final int SUBSERVICE_BE_ADD = 4;

    /**
     * 通用的加好友成功
     */
    public static final int SUBSERVICE_FRIEND_COMMON = 5;

    /**
     * 子业务类型：群成员申请加入群
     */
    public static final int SUBSERVICE_GROUP_APPLY = 10;

    /**
     * 子业务类型：群主受理待加入成员
     */
    public static final int SUBSERVICE_GROUP_WAITTING = 11;

    /**
     * 子业务类型：用户收到群邀请
     */
    public static final int SUBSERVICE_GET_GROUP_APPLY = 12;

    /**
     * 通用的群组类型,接收方为群主
     */
    public static final int SUBSERVICE_GROUP_COMMON_OWNER = 13;
    
    /**
     * 通用的群组类型，接收方为成员
     */
    public static final int SUBSERVICE_GROUP_COMMON_SELF = 14;

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 子业务类型：<br>
     * 1：系统自动匹配好友（双方号簿均有联系方式） <br>
     * 2：邀请方注册自动成为好友的 <br>
     * 3：用户手动加的好友 <br>
     * 4：用户加我为好友 <br>
     * 10：群成员申请加入群 <br>
     * 11：群主受理待加入成员
     * 
     */
    private int subService;

    /**
     * 好友系统标识
     */
    private String friendSysId;

    /**
     * 客户端存储时生成的唯一标识：UUID； 为了找朋友小助手的信息和会话表进行关联
     */
    private String msgId;

    /**
     * 好友JID
     */
    private String friendUserId;

    /**
     * 好友昵称
     */
    private String nickName;

    /**
     * 好友的名
     */
    private String firstName;

    /**
     * 好友的中间名
     */
    private String middleName;

    /**
     * 好友的姓
     */
    private String lastName;

    /**
     * 好友签名
     */
    private String signature;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 业务状态（加好友、加群的过程态，状态可迁移）
     * 1：同意， 2：拒绝， 3：等待对方处理 4：正在发送 5：发送失败
     */
    private int status;

    /**
     * 操作的理由
     */
    private String reason;

    /**
     * 操作时间，毫秒级别UTC时间戳
     */
    private String operateTime;

    /**
     * 本地缓存的头像
     *（若是好友自定义头像，则取好友userid命名的图片，存放于此用户自己的文件夹下） 
     *自定义头像为0 系统头像，赋值（001-999）
     */
    // private String face;

    /**
     * 头像URL
     */
    private String faceUrl;

    /**
     * 头像数据
     */
    private byte[] faceBytes;

    /**
     * [构造简要说明]
     */
    public FriendManagerModel()
    {
        super();
    }

    public int getSubService()
    {
        return subService;
    }

    public void setSubService(int subService)
    {
        this.subService = subService;
    }

    public String getFriendSysId()
    {
        return friendSysId;
    }

    public void setFriendSysId(String friendSysId)
    {
        this.friendSysId = friendSysId;
    }

    public String getFriendUserId()
    {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId)
    {
        this.friendUserId = friendUserId;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
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

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getOperateTime()
    {
        return operateTime;
    }

    public void setOperateTime(String operateTime)
    {
        this.operateTime = operateTime;
    }

    public String getFaceUrl()
    {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl)
    {
        this.faceUrl = faceUrl;
    }

    public byte[] getFaceBytes()
    {
        return faceBytes;
    }

    public void setFaceBytes(byte[] faceBytes)
    {
        this.faceBytes = faceBytes;
    }

    public String getMsgId()
    {
        return msgId;
    }

    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
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
        sb.append(" subService:").append(subService);
        sb.append(" friendSysId:").append(friendSysId);
        sb.append(" friendUserId:").append(friendUserId);
        sb.append(" nickName:").append(nickName);
        sb.append(" firstName:").append(firstName);
        sb.append(" middleName:").append(middleName);
        sb.append(" lastName:").append(lastName);
        sb.append(" signature:").append(signature);
        sb.append(" groupId:").append(groupId);
        sb.append(" groupName:").append(groupName);
        sb.append(" status:").append(status);
        sb.append(" reason:").append(reason);
        sb.append(" operateTime:").append(operateTime);
        // sb.append(" face:").append(face);
        sb.append(" faceUrl:").append(faceUrl);
        sb.append(" faceBytes:").append(faceBytes);
        sb.append(" msgId:").append(msgId);
        return sb.toString();
    }
}
