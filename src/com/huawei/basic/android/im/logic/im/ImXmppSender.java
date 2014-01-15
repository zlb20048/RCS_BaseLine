/*
 * 文件名: ImXmppSender.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-12
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.im;

import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseRetData;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupData;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageData;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageData.MessageReportCmdData;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * IM模块XMPP消息发送<BR>
 * 负责组装XMPP消息，并通过执行executeCommand()将消息发送出去
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-12] 
 */
public class ImXmppSender
{
    private static final String TAG = "ImXmppSender";
    
    /**
     * 视频缩略图上传失败时数据库记录的值
     */
    private static final String VIDEO_THUMB_UPLOAD_FAILED_FLAG = "FAILED";
    
    /**
     * 与service交互的成员变量
     */
    private IServiceSender mServiceSender;
    
    /**
     * 
     * 构造函数，需要传进IServiceSender 对象
     * @param sender IServiceSender对象
     */
    public ImXmppSender(IServiceSender sender)
    {
        mServiceSender = sender;
    }
    
    /**
     * 发送状态报告
     * @param msgFastId msgFastId
     * @param accountTo accountTo
     * @param status status
     * @return int 状态报告发送结果
     */
    public int sendReport(String msgFastId, String accountTo, String status)
    {
        MessageReportCmdData report = new MessageReportCmdData();
        report.setId(msgFastId);
        report.setTo(UriUtil.buildXmppJid(accountTo));
        report.setReport(status);
        String str = report.makeCmdData();
        Logger.i(TAG, str);
        String retData = mServiceSender.executeCommand(BaseParams.MessageParams.FAST_COM_MESSAGE_ID,
                BaseParams.MessageParams.FAST_MESSAGE_CMD_REPORT,
                str);
        int iRet = getReportResultCode(retData);
        return iRet;
    }
    
    /**
     * 1对1消息发送
     * @param msgModel 1V1消息对象
     * @return String 执行结果
     */
    public String send1V1Message(MessageModel msgModel)
    {
        // 判空操作
        if (msgModel == null)
        {
            Logger.d(TAG, "msgModel is null.");
            return null;
        }
        
        // 1.将message组装成xmpp发送消息的参数
        String data = makeCmdData(msgModel);
        
        Logger.d(TAG, "data : " + data);
        
        // 2.调用IServiceSender的executeCommand()发送消息
        String retData = mServiceSender.executeCommand(BaseParams.MessageParams.FAST_COM_MESSAGE_ID,
                BaseParams.MessageParams.FAST_MESSAGE_CMD_SEND,
                data);
        
        if (retData != null)
        {
            // 调用发送命令成功（消息是否发送成功，需要服务器返回FAST_MESSAGE_NTF_SEND确认）
            if (getSendResultCode(retData) == 0)
            {
                String msgSequence = StringUtil.getXmlValue(retData, "id");
                return msgSequence;
            }
        }
        return null;
        
    }
    
    /**
     * 群消息发送
     * @param groupMsgModel 1VN消息对象
     * @return 发送成功时返回msgSequence,失败时返回null
     */
    public String send1VNMessage(GroupMessageModel groupMsgModel)
    {
        // 判空操作
        if (groupMsgModel != null)
        {
            
            // 将message组装成xmpp发送消息的参数
            String data = makeCmdData(groupMsgModel);
            
            // 调用IServiceSender的executeCommand()发送消息
            String retData = mServiceSender.executeCommand(BaseParams.GroupParams.FAST_COM_GROUP_ID,
                    BaseParams.GroupParams.FAST_GROUP_CMD_MESSAGE_SEND,
                    data);
            if (retData != null)
            {
                // 调用发送命令成功（消息是否发送成功，需要服务器返回FAST_MESSAGE_NTF_SEND确认）
                if (getSendResultCode(retData) == 0)
                {
                    String msgSequence = StringUtil.getXmlValue(retData, "id");
                    return msgSequence;
                }
            }
        }
        return null;
    }
    
    /**
     * 将message组装成xmpp发送消息的参数
     * @param baseMsg 消息对象
     * @return 返回xmpp发送消息的参数
     */
    private String makeCmdData(BaseMessageModel baseMsg)
    {
        String ret = null;
        
        MessageCommonClass.CommonMessageData message = getCommonMessageData();
        // 文本内容
        String msgContent = null;
        // 多媒体信息
        MediaIndexModel media = null;
        //判空操作
        if (baseMsg != null)
        {
            msgContent = baseMsg.getMsgContent();
            media = baseMsg.getMediaIndex();
        }
        if (media != null)
        {
            switch (media.getMediaType())
            {//如果多媒体是音频
                case MediaIndexModel.MEDIATYPE_AUDIO:
                    MessageCommonClass.Audio audio = getAudio();
                    audio.setSrc(media.getMediaURL());
                    audio.setPlaytime(String.valueOf(media.getPlayTime()));
                    audio.setSize(media.getMediaSize());
                    message.setAudio(audio);
                    break;
                //如果多媒体是视频
                case MediaIndexModel.MEDIATYPE_VIDEO:
                    MessageCommonClass.Video video = getVideo();
                    video.setSrc(media.getMediaURL());
                    video.setSize(media.getMediaSize());
                    video.setPlaytime(String.valueOf(media.getPlayTime()));
                    
                    // 处理视频缩略图
                    String mediaSmallUrl = media.getMediaSmallURL();
                    if (!StringUtil.isNullOrEmpty(mediaSmallUrl)
                            && !StringUtil.equals(VIDEO_THUMB_UPLOAD_FAILED_FLAG,
                                    mediaSmallUrl))
                    {
                        video.setThumbnail(mediaSmallUrl);
                    }
                    
                    message.setVideo(video);
                    break;
                //如果多媒体是图片
                case MediaIndexModel.MEDIATYPE_IMG:
                    MessageCommonClass.Image image = getImage();
                    image.setSrc(media.getMediaURL());
                    image.setSize(media.getMediaSize());
                    message.setImage(image);
                    break;
                //如果多媒体信息是贴图
                case MediaIndexModel.MEDIATYPE_EMOJI:
                    MessageCommonClass.Emoji emoji = getEmoji();
                    emoji.setAlt(media.getMediaAlt());
                    emoji.setTtid(media.getMediaPath());
                    message.setEmoji(emoji);
                    break;
                //如果多媒体消息是我的位置
                case MediaIndexModel.MEDIATYPE_LOCATION:
                    MessageCommonClass.Location location = getLocation();
                    location.setLa(media.getLocationLat());
                    location.setLo(media.getLocationLon());
                    location.setDesc(media.getMediaAlt());
                    message.setLocation(location);
                    break;
                default:
                    break;
            }
        }
        if (msgContent == null)
        {
            msgContent = "";
        }
        
        message.setBody(msgContent);
        //如果 baseMsg 是  MessageModel类的实例
        if (baseMsg instanceof MessageModel && baseMsg != null)
        {
            MessageModel msgModel = (MessageModel) baseMsg;
            MessageData.MessageSendCmdData mscd = getMessageSendCmdData();
            
            // 设置消息接收人
            mscd.setTo(UriUtil.buildXmppJid(msgModel.getFriendUserId()));
            // 如果是小秘书消息
            /*
             * 4、首次登录，自动发给小秘书的消息内容是：first login，消息类型：im-usage 
             * 5、可以正常发IM消息给小秘书，类型与给一个固定的JID发送IM消息（消息格式与一对一会话一样） 消息类型：chat。
             */
            if (FusionCode.XmppConfig.SECRETARY_ID.equals(msgModel.getFriendUserId())
                    && FusionCode.Common.MESSAGE_TO_SECRETARY.equals(msgContent))
            {
                message.setType("im-usage");
            }
            else
            {
                message.setType("chat");
            }
            mscd.setMessage(message);
            
            // 设置要求报告类型
            mscd.setReport("all");
            
            ret = mscd.makeCmdData();
        }
        //如果 baseMsg 是  GroupMessageModel类的实例
        else if (baseMsg instanceof GroupMessageModel && baseMsg != null)
        {
            GroupMessageModel groupMsgModel = (GroupMessageModel) baseMsg;
            GroupData.MessageSendCmdData mscd = getGroupMessageSendCmdData();
            
            // 设置消息接收人
            mscd.setTo(UriUtil.buildXmppGroupJID(groupMsgModel.getGroupId()));
            String from = UriUtil.buildXmppJid(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserID());
            if (!StringUtil.isNullOrEmpty(from))
            {
                mscd.setFrom(from);
            }
            
            message.setType("im-gp-public");
            
            mscd.setMessage(message);
            //将消息组装成xml形式的字符串
            ret = mscd.makeCmdData();
        }
        
        return ret;
    }
    
    /**
     *获取消息基类的对象 MessageCommonClass的内部类对象CommonMessageData
     * 
     */
    private MessageCommonClass.CommonMessageData getCommonMessageData()
    {
        return new MessageCommonClass.CommonMessageData();
    }
    
    /**
     *获取消息基类 MessageCommonClass的内部类对象Audio
     * 
     */
    private MessageCommonClass.Audio getAudio()
    {
        return new MessageCommonClass.Audio();
    }
    
    /**
     *获取消息基类 MessageCommonClass的内部类对象Video
     * 
     */
    private MessageCommonClass.Video getVideo()
    {
        return new MessageCommonClass.Video();
    }
    
    /**
     *获取消息基类 MessageCommonClass的内部类对象Image
     * 
     */
    private MessageCommonClass.Image getImage()
    {
        return new MessageCommonClass.Image();
    }
    
    /**
     *获取消息基类 MessageCommonClass的内部类对象Image
     * 
     */
    private MessageCommonClass.Emoji getEmoji()
    {
        return new MessageCommonClass.Emoji();
    }
    
    /**
     *获取消息基类 MessageCommonClass的内部类对象Location
     * 
     */
    private MessageCommonClass.Location getLocation()
    {
        return new MessageCommonClass.Location();
    }
    
    /**
     *获取消息数据结构类 MessageData的内部类对象MessageSendCmdData
     * 
     */
    private MessageData.MessageSendCmdData getMessageSendCmdData()
    {
        return new MessageData.MessageSendCmdData();
    }
    
    /**
     *获取群组消息类 GroupData的内部类对象MessageSendCmdData
     * 
     */
    private GroupData.MessageSendCmdData getGroupMessageSendCmdData()
    {
        return new GroupData.MessageSendCmdData();
    }
    
    /**
     * 
     * 解析执行命令返回值<BR>
     * 
     * @param resultString 结果字符串
     * @return 成功标志
     */
    private int getSendResultCode(String resultString)
    {
        BaseRetData.MessageSend messageData = null;
        try
        {
            messageData = new XmlParser().parseXmlString(BaseRetData.MessageSend.class,
                    resultString);
        }
        catch (Exception e)
        {
            Logger.e(TAG, " parseXmlString 解析错误！");
        }
        if (null != messageData)
        {
            return messageData.getRet();
        }
        return -1;
    }
    
    /**
     * 
     * 解析执行命令返回值<BR>
     * 
     * @param resultString
     * @return 成功标志
     */
    private int getReportResultCode(String resultString)
    {
        BaseRetData.MessageReport messageData = null;
        try
        {
            messageData = new XmlParser().parseXmlString(BaseRetData.MessageReport.class,
                    resultString);
        }
        catch (Exception e)
        {
            Logger.e(TAG, " parseXmlString 解析错误！");
        }
        if (null != messageData)
        {
            return messageData.getRet();
        }
        return -1;
    }
}
