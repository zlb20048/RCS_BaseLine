/*
 * 文件名: IImLogic.java
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

import java.util.List;

import android.graphics.drawable.Drawable;

import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.ui.im.BaseMsgCursorWrapper;
import com.huawei.basic.android.im.ui.im.ProgressModel;
import com.huawei.basic.android.im.utils.UriUtil.FromType;

/**
 * 聊天的逻辑处理接口定义<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-12] 
 */
public interface IImLogic
{
    /**
     * 
     * 注册1V1聊天的数据库监听<BR>
     * 1V1需要监听该好友对应的消息表以及该好友的用户信息表
     * @param friendUserID 聊天对象user id
     */
    void register1V1DataObserver(String friendUserID);
    
    /**
     * 
     * 注册1VN聊天的数据库监听<BR>
     * 1VN需要监听该群组对应的群组消息表以及该群组成员的成员信息表
     * @param groupID 群组ID
     */
    void register1VNDataObserver(String groupID);
    
    /**
     * 
     * 注销1V1聊天的数据库监听<BR>
     * 与{@link #register1V1DataObserver(String)}对应
     * @param friendUserID 聊天对象user id
     */
    void unregister1V1DataObserver(String friendUserID);
    
    /**
     * 
     * 注销1VN聊天的数据库监听<BR>
     * 与{@link #register1VNDataObserver(String)}对应
     * @param groupID 群组ID
     */
    void unregister1VNDataObserver(String groupID);
    
    /**
     * 获取1V1聊天消息表的Cursor
     * <BR>
     * @param friendUserID 好友ID
     * @return  Cursor
     */
    BaseMsgCursorWrapper get1V1MsgList(String friendUserID);
    
    /**
     * 获取1VN聊天消息表的Cursor<BR>
     * [功能详细描述]
     * @param groupId 群组ID
     * @return Cursor
     */
    BaseMsgCursorWrapper get1VNMsgList(String groupId);
    
    /**
     * 
     * 发送消息给小秘书<BR>
     */
    void sendToSecretary();
    
    /**
     * 
     * 发送1V1消息（简单文本消息）<BR>
     * 
     * @param to 发往方
     * @param textContent 消息内容
     */
    void send1V1Message(String to, String textContent);
    
    /**
     * 
     * 发送1V1消息（携带媒体附件）<BR>
     * 
     * @param to 发往方
     * @param textContent 消息内容
     * @param mediaIndex 媒体附件信息
     */
    void send1V1Message(String to, String textContent,
            MediaIndexModel mediaIndex);
    
    /**
     * 
     * 发送1VN消息（简单文本消息）<BR>
     * 
     * @param to 发往方
     * @param textContent 消息内容
     */
    void send1VNMessage(String to, String textContent);
    
    /**
     * 
     * 发送1VN消息（携带媒体附件）<BR>
     * 
     * @param to 发往方
     * @param textContent 消息内容
     * @param mediaIndex 媒体附件信息
     */
    void send1VNMessage(String to, String textContent,
            MediaIndexModel mediaIndex);
    
    /**
     * 
     * 获取与指定好友的未读音频消息id列表<BR>
     * 
     * @param friendUserId 好友用户id
     * @return 未读音频消息id列表
     */
    List<String> get1V1UnreadAudioMsgIds(String friendUserId);
    
    /**
     * 
     * 获取与指定好友的未读音频消息id列表<BR>
     * 
     * @param groupId 群组id
     * @return 未读音频消息id列表
     */
    List<String> get1VNUnreadAudioMsgIds(String groupId);
    
    /**
     * 获取头像
     * <BR>
     * 
     * @param userID 用户id
     * @return Drawable
     */
    Drawable getFace(String userID);
    
    /**
     * 获取自己的头像
     * <BR>
     * 
     * @return Drawable
     */
    Drawable getMyFace();
    
    /**
     * 获取群组成员的昵称
     * <BR>
     * 
     * @param memberUserID 成员user id
     * @param groupID 成员所在群组的群组ID
     * @return String
     */
    String getGroupMemberNickName(String memberUserID, String groupID);
    
    /**
     * 获取自己的昵称
     * <BR>
     * 
     * @return String
     */
    String getMyNickName();
    
    /**
     * 
     * 进入1v1聊天页面后，把接收到的该好友的所有消息置为已读<BR>
     * @param friendUserId 好友id
     */
    void setAll1V1MsgAsReaded(String friendUserId);
    
    /**
     * 
     * 进入1vN聊天页面后，把接收到的该群组的所有消息置为已读<BR>
     * @param groupId 群组id
     */
    void setAll1VNMsgAsReaded(String groupId);
    
    /**
     * 将1v1消息设为已读<BR>
     * @param msg MessageModel
     */
    void set1V1MsgAsReaded(MessageModel msg);
    
    /**
     * 将1vN消息设为已读<BR>
     * @param msg GroupMessageModel
     */
    void set1VNMsgAsReaded(GroupMessageModel msg);
    
    /**
     * * 1v1消息重发<BR>
     * @param msg MessageModel
     */
    void resend1V1Message(MessageModel msg);
    
    /**
     * 1vN消息重发<BR>
     * @param msg GroupMessageModel
     */
    void resend1VNMessage(GroupMessageModel msg);
    
    /**
     * 
     * 清除1v1消息<BR>
     * @param friendUserID friendUserId
     */
    void clear1V1Message(String friendUserID);
    
    /**
     * 清除1VN消息<BR>
     * @param groupID groupId
     */
    void clear1VNMessage(String groupID);
    
    /**
     * 
     * 删除指定msdID的1V1聊天消息<BR>
     * 
     * @param msgId 消息id
     */
    void delete1V1Message(String msgId);
    
    /**
     * 
     * 删除指定msdID的1VN聊天消息<BR>
     * 
     * @param msgId 消息id
     */
    void delete1VNMessage(String msgId);
    
    /**
     * 
     * 转发一条1V1消息到多个好友<BR>
     * 该消息以1V1方式发送给多个好友
     * 
     * @param msgId 消息id
     * @param friendUserIds 要发往的用户id数组
     */
    void transfer1V1Message(String msgId, String[] friendUserIds);
    
    /**
     * 
     * 转发一条1VN消息<BR>
     * 该消息以1V1方式发送给多个好友
     * @param msgId 消息id
     * @param friendUserIds 要发往的用户id数组
     */
    void transfer1VNMessage(String msgId, String[] friendUserIds);
    
    /**
     * 获取联系人信息<BR>
     * @param friendUserId 好友UserID
     * @return contactInfoModel
     */
    ContactInfoModel getContactInfoModel(String friendUserId);
    
    /**
     * 
     * 获取音频保存路径<BR>
     * @return String 音频保存的路径
     */
    String getAudioFilePath();
    
    /**
     * 
     * 获取图片保存路径<BR>
     * @param fromType FromType 图片的来源类型
     * @return 图片保存路径
     */
    String getImageFilePath(FromType fromType);
  
    /**
     * 
     * 获取视频保存路径<BR>
     * @param fromType FromType 图片的来源类型
     * @return 视频保存路径
     */
    String getVideoFilePath(FromType fromType);
    
    
    /**
     * 
     * 判断SD卡是否存在<BR>
     * @return boolean true存在
     */
    boolean sdCardExist();
    
    /**
     * 下载媒体原文件的接口
     * @param msgId 下载多媒体的消息id
     * @param fromType FromType 消息来源类型
     * @return ProgressModel
     */
    ProgressModel downloadMedia(String msgId, FromType fromType);
    
    /**
     * 多媒体文件下载暂停接口
     * @param msgId 下载多媒体的消息id
     */
    void pauseDownload(String msgId);
    
    /**
     * 
     * 暂停的任务继续下载<BR>
     * @param msgId String 下载多媒体的消息id
     */
    void continueDownload(String msgId);
    
    /**
     * 多媒体文件下载停止接口
     * @param msgId String 下载多媒体的消息id
     */
    void stopDownload(String msgId);
    
    /**
     * 多媒体文件上传停止接口
     * @param msgId String 上传的多媒体的消息id
     */
    void stopUpload(String msgId);
    
    /**
     * 
     * 判断GPS是否开启<BR>
     * @return boolean
     */
    boolean isGPSEnabled();
    
    /**
     * 根据经纬度构造地图完整下载地址
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @return 完整下载地址
     */
    String buildLocationDlUrl(double longitude, double latitude);
    
    /**
     * 开启GPS
     * @throws Exception Exception
     */
    void toggleGPS() throws Exception;
    
}