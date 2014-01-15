package com.huawei.basic.android.im.logic.notification.bean;

/**
 * 
 * IM通知栏的实体类<BR>
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-5-21]
 */
public class IMNotificationBean
{
    /**
     * 消息内容
     */
    private String msgContent;
    
    /**
     * 好友昵称
     */
    private String nickName;
    
    /**
     * 消息类型
     */
    private int msgType;
    
    /**
     * 媒体类型
     */
    private int mediaType;
    
    /**
     * 未读消息数
     */
    private int unreadMsgCount;
    
    /**
     * 好友会话ID
     */
    private String sessionId;
    
    public String getMsgContent()
    {
        return msgContent;
    }
    
    public void setMsgContent(String msgContent)
    {
        this.msgContent = msgContent;
    }
    
    public String getNickName()
    {
        return nickName;
    }
    
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }
    
    public int getMsgType()
    {
        return msgType;
    }
    
    public void setMsgType(int msgType)
    {
        this.msgType = msgType;
    }
    
    public int getMediaType()
    {
        return mediaType;
    }
    
    public void setMediaType(int mediaType)
    {
        this.mediaType = mediaType;
    }
    
    public int getUnreadMsgCount()
    {
        return unreadMsgCount;
    }
    
    public void setUnreadMsgCount(int unreadMsgCount)
    {
        this.unreadMsgCount = unreadMsgCount;
    }
    
    public String getSessionId()
    {
        return sessionId;
    }
    
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }
}