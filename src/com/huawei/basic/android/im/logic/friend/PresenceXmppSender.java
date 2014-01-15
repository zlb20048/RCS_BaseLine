/*
 * 文件名: PresenceXmppSender.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Mar 26, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.friend;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams.PresenceParams;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseRetData;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceData;
import com.huawei.basic.android.im.component.net.xmpp.util.XmlParser;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * 向Xmpp服务器发送Presence信息<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Mar 26, 2012] 
 */
public class PresenceXmppSender
{
    /**
     * TAG
     */
    private static final String TAG = "PresenceXmppSender";
    
    /**
     * 与service交互的发送接口定义
     */
    private IServiceSender mServiceSender;
    
    /**
     * 组件ID
     */
    private String mComponentId;
    
    /**
     * 构造方法，传入ServiceSender接口
     * @param xmppSender xmppSender
     */
    public PresenceXmppSender(IServiceSender xmppSender)
    {
        mServiceSender = xmppSender;
        mComponentId = BaseParams.PresenceParams.FAST_COM_PRESENCE_ID;
    }
    
    /**
     * 添加好友<BR>
     * @param from 发送者的hitalkId
     * @param faceUrl 发送者的头像URL
     * @param nickName 发送者的昵称
     * @param sinature 发送者的签名
     * @param to 接收者的hitalkId
     * @param reason 验证信息
     * @return 命令执行结果
     */
    public int addFriend(String from, String faceUrl, String nickName,
            String sinature, String to, String reason)
    {
        Logger.d(TAG, "addFriend -------->发出加好友申请");
        return getResultCode(mServiceSender.executeCommand(mComponentId,
                PresenceParams.FAST_PRESENCE_CMD_FRIEND_ADD,
                buildAddFriendXml(from, faceUrl, nickName, sinature, to, reason)));
    }
    
    /**
     * 处理好友请求<BR>
     * @param from 发起者
     * @param to 对应的人
     * @param isAgree 同意拒绝
     * @return 命令执行结果
     */
    public int doAuth(String from, String to, boolean isAgree)
    {
        Logger.d(TAG, "doAuth -------->处理好友请求");
        return getResultCode(mServiceSender.executeCommand(mComponentId,
                BaseParams.PresenceParams.FAST_PRESENCE_CMD_FRIEND_ADDING_CONFIRM,
                buildAuthXml(from, to, isAgree)));
    }
    
    /**
     * 删除好友的命令执行<BR>
     * @param from 发起者的hitalkId
     * @param to 接受者的hitalkId
     * @return 命令的执行结果
     */
    public int deleteFriend(String from, String to)
    {
        Logger.d(TAG, "deleteFriend ----------> 删除好友 ");
        return getResultCode(mServiceSender.executeCommand(mComponentId,
                BaseParams.PresenceParams.FAST_PRESENCE_CMD_FRIEND_REMOVE,
                buildDeleteFriendXml(from, to)));
    }
    
    /**
     * 用户修改头像签名昵称或在线状态是应该发表的presence<BR>
     * @param from presence的发起者
     * @param signature 签名
     * @param faceUrl 发起者的头像
     * @param nickName 昵称
     * @return 命令的执行结果
     */
    public int publishPresence(String from, String signature, String faceUrl,
            String nickName)
    {
        Logger.d(TAG, "publishPresence ----------> 发布在线状态 ");
        return getResultCode(mServiceSender.executeCommand(mComponentId,
                BaseParams.PresenceParams.FAST_PRESENCE_CMD_PUBLISH,
                buildPublishPresenceXml(from, signature, faceUrl, nickName)));
    }
    
    /**
     * 添加好友执行命令XML串组装<BR>
     * @param from 发送者的hitalkId
     * @param faceUrl 发送者的头像URL
     * @param nickName 发送者的昵称
     * @param sinature 发送者的签名
     * @param to 接收者的hitalkId
     * @param reason 验证信息
     * @param 组装后的XML串
     */
    private String buildAddFriendXml(String from, String faceUrl,
            String nickName, String sinature, String to, String reason)
    {
        PresenceData.FriendAddCmdData cmdObject = new PresenceData.FriendAddCmdData();
        cmdObject.setFrom(UriUtil.buildXmppJid(from));
        cmdObject.setTo(UriUtil.buildXmppJid(to));
        cmdObject.setReason(reason);
        
        //拼装命令字符串
        PresenceCommonClass.PersonData person = new PresenceCommonClass.PersonData();
        person.setNick(nickName);
        person.setSign(sinature);
        person.setLogo(faceUrl);
        cmdObject.setPerson(person);
        
        return cmdObject.makeCmdData();
    }
    
    /**
     * 生成执行命令需要的XMl串<BR>
     * @param from 发起者的hitalkId
     * @param to 接受者的hitalkId
     * @param reason 拒绝理由
     * @param isAgree isAgree
     * @return 执行命令需要的xml串
     */
    private String buildAuthXml(String from, String to, boolean isAgree)
    {
        PresenceData.FriendAddingConfirmCmdData authObject = new PresenceData.FriendAddingConfirmCmdData();
        
        authObject.setFrom(UriUtil.buildXmppJid(from));
        authObject.setTo(UriUtil.buildXmppJid(to));
        
        if (isAgree)
        {
            authObject.setType(PresenceData.FriendAddingConfirmCmdData.Type.SUBSCRIBED);
        }
        else
        {
            authObject.setType(PresenceData.FriendAddingConfirmCmdData.Type.UNSUBSCRIBED);
        }
        return authObject.makeCmdData();
    }
    
    /**
     * 生成执行命令的Xml串<BR>
     * @param from 发起者hitalkId
     * @param to 接受者的hitalkId
     * @return 生成的xml串
     */
    private String buildDeleteFriendXml(String from, String to)
    {
        PresenceData.FriendRemoveCmdData delObject = new PresenceData.FriendRemoveCmdData();
        delObject.setFrom(UriUtil.buildXmppJid(from));
        delObject.setTo(UriUtil.buildXmppJid(to));
        return delObject.makeCmdData();
    }
    
    /**
     * 生成用户修改头像签名昵称或在线状态是应该发表的presence的xml串<BR>
     * @param from presence的发起者
     * @param signature 签名
     * @param faceUrl 发起者的头像
     * @param nickName 昵称
     * @return 命令组装的字符串
     */
    private String buildPublishPresenceXml(String from, String signature,
            String faceUrl, String nickName)
    {
        PresenceData.PublishCmdData cmdData = new PresenceData.PublishCmdData();
        cmdData.setFrom(UriUtil.buildXmppJid(from));
        cmdData.setPriority("0");
        
        //个人的资料信息
        PresenceCommonClass.PersonData personData = new PresenceCommonClass.PersonData();
        personData.setLogo(faceUrl);
        personData.setNick(nickName);
        personData.setSign(signature);
        cmdData.setPreson(personData);
        
        //设置支持语音，视频能力，这里因为对手机客户端没有需求，所以默认谢写no
        PresenceCommonClass.DeviceData device = new PresenceCommonClass.DeviceData();
        device.setAudio("no");
        device.setVideo("no");
        device.setType(PresenceCommonClass.DeviceData.Type.ANDROID.getValue());
        cmdData.setDevice(device);
        return cmdData.makeCmdData();
    }
    
    /**
     * 根据执行命令的返回的string，提取错误码<BR>
     * @param resultString 执行命令返回的String
     * @return 错误码
     */
    private int getResultCode(String resultString)
    {
        BaseRetData.Presence prenceRetData = null;
        try
        {
            prenceRetData = new XmlParser().parseXmlString(BaseRetData.Presence.class,
                    resultString);
        }
        catch (Exception e)
        {
            Logger.e(TAG, " parseXmlString 解析错误！", e);
        }
        if (null != prenceRetData)
        {
            return prenceRetData.getRet();
        }
        return -1;
    }
}
