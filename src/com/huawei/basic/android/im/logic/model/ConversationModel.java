/*
 * 文件名: ConversationModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周雪松
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;
import java.util.List;

/**
 * 消息会话信息<BR>
 * 
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-2-22]
 */
public class ConversationModel implements Serializable
{
    /**
     * 消息内容类型 ：文本（含图片表情符号）
     */
    public static final int MSGTYPE_TEXT = 1;
    
    /**
     * 消息内容类型 ：多媒体
     */
    public static final int MSGTYPE_MEDIA = 2;
    
    /**
     * 消息内容类型 :系统提示
     */
    public static final int MSGTYPE_SYSTEM = 3;
    
    /**
     * 会话类型：一对一聊天
     */
    public static final int CONVERSATIONTYPE_1V1 = 1;
    
    /**
     * 会话类型：临时群/群组
     */
    public static final int CONVERSATIONTYPE_GROUP = 2;
    
    /**
     * 会话类型：群发消息
     */
    public static final int CONVERSATIONTYPE_1VN = 3;
    
    /**
     * 会话类型：群内私聊
     */
    public static final int CONVERSATIONTYPE_GROUP_INNER = 4;
    
    /**
     * 会话类型：小秘书
     */
    public static final int CONVERSATIONTYPE_SECRET = 5;
    
    /**
     * 会话类型：找朋友小助手
     */
    public static final int CONVERSATIONTYPE_FRIEND_MANAGER = 6;
    
    /**
     * 超箱小助手
     */
    public static final int CONVERSATIONTYPE_SUPER_MAIL = 7;
    
    /**
     * 首次登陆的时候需要给出用户几条提示信息
     */
    public static final int CONVERSATIONTYPE_INIT_TIPS = 8;
    
    /**
     * 首次登录时需要播放一条超邮小助手的信息
     */
    //public static final int CONVERSATIONTYPE_MAIL_INIT_TIPS = 9;
    
    /**
     * 群解散通知信息
     */
    public static final int CONVERSATION_GROUP_DESTROYED = 10;
    
    /**
     * 会话ID：如何建立你的圈子的id
     */
    public static final String ID_VALUE_BE_FOUND = "-1";
    
    /**
     * 会话ID：如何找到好友的id
     */
    public static final String ID_VALUE_FIND_FRIEND = "-2";
    
    /**
     * 会话ID：如何找到群的id
     */
    public static final String ID_VALUE_FIND_GROUP = "-3";
    
    /**
     * 会话ID：会话表中找朋友小助手ID
     */
    public static final String ID_VALUE_FRIEND_MANAGER = "-4";
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 会话ID，可以是<br>
     * 好友/群成员JID；<br>
     * 群发消ID(短信和IM都可以，迭代三设计)； <br>
     * 临时组JID 群组JID；<br>
     * 小秘书JID：（待定，10010，系统公告的id不需要在此表展现）<br>
     * 找朋友小助手JID：（待定）
     */
    private String conversationId;
    
    /**
     * 会话类型： <br>
     * 1：单人会话（对应好友ID） <br>
     * 2、群组会话（对应群组ID） <br>
     * 3：群发消息（混合消息或短信） <br>
     * 4：群内私聊（对应群成员ID） <br>
     * 5：小秘书会话 <br>
     * 6：找朋友小助手 <br>
     * 7：超箱小助手
     */
    private int conversationType;
    
    /**
     * 群组ID， IDType=4时，有效
     */
    private String groupId;
    
    /**
     * 最近发送/接收消息时间，毫秒级别UTC时间戳
     */
    private String lastTime;
    
    /**
     * 发送接收消息时，客户端存储时生成的唯一标识：UUID；用于与媒体资源表的对应
     */
    private String lastMsgId;
    
    /**
     * 消息序号，发送消息时由FAST生成 接收时为发送方生成的。
     */
    private String lastMsgSequence;
    
    /**
     * 最后一次，最近发送/接收消息内容类型： <br>
     * 1：文本（含图片表情符号） <br>
     * 2：多媒体
     */
    private int lastMsgType;
    
    /**
     * 最后一次，文本消息内容（图文混排的文本也存放在这里），
     * 注：如果是多媒体消息，需要在多媒体消息表内查询详情
     */
    private String lastMsgContent;
    
    /**
     * 当发送时，发送的消息阅读状态： <br>
     * 1：待发送 <br>
     * 2：已发送 <br>
     * 3：已送达未读 <br>
     * 4：对方已读 <br>
     * 当接收时，收到的消息状态 <br>
     * 5：已接收未读 <br>
     * 6：自己已读 <br>
     * 100: 阻塞状态(多媒体消息正在上传附件，不处理) <br>
     * 101：发送失败
     */
    private int lastMsgStatus;
    
    /**
     * 当前会话未读消息条数
     */
    private int unReadmsg;
    
    /**
     * 群发短信，接收方们的昵称聚合，例如：小李，王大伟，李刚
     */
    private List<String> receiversName;
    
    /**
     * 用户系统标识， 用于数据迁移
     */
    private String userSysId;
    
    /**
     * 构造方法
     */
    public ConversationModel()
    {
    }
    
    public String getConversationId()
    {
        return conversationId;
    }
    
    public void setConversationId(String conversationId)
    {
        this.conversationId = conversationId;
    }
    
    public int getConversationType()
    {
        return conversationType;
    }
    
    public void setConversationType(int conversationType)
    {
        this.conversationType = conversationType;
    }
    
    public String getLastTime()
    {
        return lastTime;
    }
    
    public void setLastTime(String lastTime)
    {
        this.lastTime = lastTime;
    }
    
    public String getLastMsgId()
    {
        return lastMsgId;
    }
    
    public void setLastMsgId(String lastMsgId)
    {
        this.lastMsgId = lastMsgId;
    }
    
    public String getLastMsgSequence()
    {
        return lastMsgSequence;
    }
    
    public void setLastMsgSequence(String lastMsgSequence)
    {
        this.lastMsgSequence = lastMsgSequence;
    }
    
    public int getLastMsgType()
    {
        return lastMsgType;
    }
    
    public void setLastMsgType(int lastMsgType)
    {
        this.lastMsgType = lastMsgType;
    }
    
    public String getLastMsgContent()
    {
        return lastMsgContent;
    }
    
    public void setLastMsgContent(String lastMsgContent)
    {
        this.lastMsgContent = lastMsgContent;
    }
    
    public int getLastMsgStatus()
    {
        return lastMsgStatus;
    }
    
    public void setLastMsgStatus(int lastMsgStatus)
    {
        this.lastMsgStatus = lastMsgStatus;
    }
    
    public int getUnReadmsg()
    {
        return unReadmsg;
    }
    
    public void setUnReadmsg(int unreadmsg)
    {
        this.unReadmsg = unreadmsg;
    }
    
    public List<String> getReceiversName()
    {
        return receiversName;
    }
    
    public void setReceiversName(List<String> receiversName)
    {
        this.receiversName = receiversName;
    }
    
    public String getGroupId()
    {
        return groupId;
    }
    
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }
    
    public String getUserSysId()
    {
        return userSysId;
    }
    
    public void setUserSysId(String userSysId)
    {
        this.userSysId = userSysId;
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
        sb.append(" conversationId:").append(conversationId);
        sb.append(" conversationType:").append(conversationType);
        sb.append(" groupId:").append(groupId);
        sb.append(" lastTime:").append(lastTime);
        sb.append(" lastMsgId:").append(lastMsgId);
        sb.append(" lastMsgSequnce").append(lastMsgSequence);
        sb.append(" lastMsgType:").append(lastMsgType);
        sb.append(" lastMsgContent:").append(lastMsgContent);
        sb.append(" lastMsgStatus:").append(lastMsgStatus);
        sb.append(" unReadmsg:").append(unReadmsg);
        sb.append(" receiversName:").append(receiversName);
        return sb.toString();
    }
    
}
