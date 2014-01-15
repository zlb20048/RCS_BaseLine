/*
 * 文件名: BaseMessageModel.java
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

/**
 * [一句话功能简述]<BR>
 * BaseMessageModel
 * @author xuesongzhou
 * @version [RCS Client V100R001C03, Feb 23, 2012]
 */
public class BaseMessageModel implements Serializable
{
    /**
     * 消息方向： 发送出去的消息
     */
    public static final int MSGSENDORRECV_SEND = 1;
    
    /**
     * 消息方向： 接收
     */
    public static final int MSGSENDORRECV_RECV = 2;
    
    /**
     * 消息阅读状态: 待发送
     */
    public static final int MSGSTATUS_PREPARE_SEND = 1;
    
    /**
     * 消息阅读状态: 已发送
     */
    public static final int MSGSTATUS_SENDED = 2;
    
    /**
     * 消息阅读状态: 已送达未读
     */
    public static final int MSGSTATUS_SEND_UNREAD = 3;
    
    /**
     * 消息阅读状态: 已读
     */
    public static final int MSGSTATUS_READED = 4;
    
    /**
     * 无状态
     */
    public static final int MSGSTATUS_NO_STATUS = 5;
    
    /**
     * 未读，需发送阅读报告
     */
    public static final int MSGSTATUS_UNREAD_NEED_REPORT = 10;
    
    /**
     * 未读，无需发送阅读报告
     */
    public static final int MSGSTATUS_UNREAD_NO_REPORT = 11;
    
    /**
     * 已读，需发送阅读报告
     */
    public static final int MSGSTATUS_READED_NEED_REPORT = 12;
    
    /**
     * 已读，无需发送阅读报告
     */
    public static final int MSGSTATUS_READED_NO_REPORT = 13;
    
    /**
     * 消息阅读状态: 阻塞状态(多媒体消息正在上传附件，不处理)
     */
    public static final int MSGSTATUS_BLOCK = 100;
    
    /**
     * 消息阅读状态: 发送失败
     */
    public static final int MSGSTATUS_SEND_FAIL = 101;
    
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
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 发送接收消息时，客户端存储时生成的唯一标识：UUID；用于与媒体资源表的对应
     */
    private String msgId;
    
    /**
     * 消息序号，发送消息时由FAST生成。接收时为发送方生成的。
     */
    private String msgSequence;
    
    /**
     * 消息发送/接收时间，毫秒级别UTC时间戳
     */
    private String msgTime;
    
    /**
     * 消息内容类型： <br>
     * 1：文本（含图片表情符号） <br>
     * 2：多媒体 <br>
     */
    private int msgType;
    
    /**
     * 文本消息内容（图文混排的文本也存放在这里），
     * 注：如果是多媒体消息，需要在多媒体消息表内查询详情
     */
    private String msgContent;
    
    /**
     * 消息方向： <br>
     * 1：发送出去的消息 <br>
     * 2：接收到的消息 <br>
     */
    private int msgSendOrRecv;
    
    /**
     * 当发送时，发送的消息阅读状态： <br>
     * 1：待发送 <br>
     * 2：已发送 <br>
     * 3：已送达未读 <br>
     * 4：已读 <br>
     * 当接收时，收到的消息状态 <br>
     * 10：未读,需发送阅读报告<br>
     * 11：未读,无需发送阅读报告 <br>
     * 12：已读，需发送阅读报告<br>
     * 13：已读，无需发送阅读报告<br>
     * 
     * 100: 阻塞状态(多媒体消息正在上传附件，不处理) <br>
     * 101： 发送失败
     */
    private int msgStatus;
    
    /**
     * 多媒体，主要用是查询消息时
     */
    private MediaIndexModel mediaIndex;
    /**
     * 用户系统标识，仅用于 "数据迁移"
     */
    private String userSysId;
    
    /**
     * 构造方法
     */
    public BaseMessageModel()
    {
        
    }
    

    public String getMsgId()
    {
        return msgId;
    }
    
    public void setMsgId(String msgId)
    {
        this.msgId = msgId;
    }
    
    public String getMsgSequence()
    {
        return msgSequence;
    }
    
    public void setMsgSequence(String msgSequence)
    {
        this.msgSequence = msgSequence;
    }
    
    public String getMsgTime()
    {
        return msgTime;
    }
    
    public void setMsgTime(String msgTime)
    {
        this.msgTime = msgTime;
    }
    
    public int getMsgType()
    {
        return msgType;
    }
    
    public void setMsgType(int msgType)
    {
        this.msgType = msgType;
    }
    
    public String getMsgContent()
    {
        return msgContent;
    }
    
    public void setMsgContent(String msgContent)
    {
        this.msgContent = msgContent;
    }
    
    public MediaIndexModel getMediaIndex()
    {
        return mediaIndex;
    }
    
    public void setMediaIndex(MediaIndexModel mediaIndex)
    {
        this.mediaIndex = mediaIndex;
    }
    
    public int getMsgSendOrRecv()
    {
        return msgSendOrRecv;
    }
    
    public void setMsgSendOrRecv(int msgSendOrRecv)
    {
        this.msgSendOrRecv = msgSendOrRecv;
    }
    
    public int getMsgStatus()
    {
        return msgStatus;
    }
    
    public void setMsgStatus(int msgStatus)
    {
        this.msgStatus = msgStatus;
    }
    
    public String getUserSysId()
    {
        return userSysId;
    }
    
    public void setUserSysId(String userSysId)
    {
        this.userSysId = userSysId;
    }
    
}
