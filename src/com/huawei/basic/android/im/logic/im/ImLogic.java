/*
 * 文件名: ImLogic.java
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
import java.util.Locale;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.download.http.DownloadHttpTask;
import com.huawei.basic.android.im.component.download.http.RcsDownloadHttpTask;
import com.huawei.basic.android.im.component.load.TaskManagerFactory;
import com.huawei.basic.android.im.component.load.task.ITask;
import com.huawei.basic.android.im.component.load.task.ITaskManager;
import com.huawei.basic.android.im.component.load.task.ITaskStatusListener;
import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.component.service.impl.xmpp.MsgXmppMng;
import com.huawei.basic.android.im.component.upload.http.UploadContentInfo;
import com.huawei.basic.android.im.component.upload.http.UploadContentInfo.MimeType;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMemberDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMessageDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.MediaIndexDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.MessageDbAdapter;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.logic.upload.ContentUploader;
import com.huawei.basic.android.im.logic.upload.IUploadListener;
import com.huawei.basic.android.im.logic.upload.ReceiverType;
import com.huawei.basic.android.im.logic.upload.UploadFileForURLResponse;
import com.huawei.basic.android.im.logic.upload.UploadFileForURLResponse.UploadFileForURLResult;
import com.huawei.basic.android.im.logic.upload.UploadParam;
import com.huawei.basic.android.im.logic.upload.UploadType;
import com.huawei.basic.android.im.ui.im.BaseMsgCursorWrapper;
import com.huawei.basic.android.im.ui.im.GroupMsgCursorWrapper;
import com.huawei.basic.android.im.ui.im.MsgCursorWrapper;
import com.huawei.basic.android.im.ui.im.ProgressModel;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.FileUtil;
import com.huawei.basic.android.im.utils.MessageUtils;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.SystemFacesUtil;
import com.huawei.basic.android.im.utils.UriUtil;
import com.huawei.basic.android.im.utils.UriUtil.FromType;
import com.huawei.basic.android.im.utils.UriUtil.LocalDirType;

/**
 * 聊天的逻辑处理接口实现类<BR>
 * 本类实现发送消息的实现，负责创建消息实体，并保存更新到数据库，
 * 负责调用ImXmppSender将实体传递过去，由ImXmppSender封装XMPP请求体
 * 并调用ServiceSender发送消息
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-12] 
 */
public class ImLogic extends BaseLogic implements IImLogic
{
    /**
     * DEBUD_TAG
     */
    private static final String TAG = "ImLogic";
    
    /**
     * 调用递送报告命令成功后的返回值
     */
    private static final int SEND_REPORT_SUCCESS = 0;
    
    /**
     * 根据地图坐标点，获取缩略图的url
     */
    private static final String GOOGLE_MAP_IMG_URL = "http://maps.google.com/maps/api/staticmap?center=%s,%s"
            + "&zoom=13&size=%sx%s&sensor=false&maptype=roadmap&format=jpg&language=%s";
    
    /**
     * 地图上面有谷歌广告需要剪掉
     */
    private static final int CUT_HEIGHT = 40;
    
    /**
     * 系统 Context对象,用于对数据库操作
     */
    private Context mContext;
    
    private ImXmppSender mChatXmppSender;
    
    /**
     * 下载、上传任务管理器对象，用于下载和上传
     */
    private ITaskManager mTaskManager;
    
    /**
     * 下载状态监听器，所有经ImLogic发起的下载都使用此统一的Listener进行处理；<br>
     * 在调用下载创建下载任务时，将msgId作为Task的name，以便在此回调中进行识别
     */
    private ITaskStatusListener mTaskStatusListener = new ITaskStatusListener()
    {
        @Override
        public void onChangeStatus(ITask loadTask)
        {
            if (!(loadTask instanceof DownloadHttpTask))
            {
                return;
            }
            String msgId = loadTask.getName();
            switch (loadTask.getStatus())
            {
                case ITask.TASK_STATUS_NEW:
                    Logger.d(TAG, "[DOWNLOAD]NEW TASK, NAME:" + msgId);
                    break;
                
                case ITask.TASK_STATUS_RUNNING:
                    Logger.d(TAG, "[DOWNLOAD]TASK IS RUNNING, NAME:" + msgId);
                    sendEmptyMessage(FusionMessageType.DownloadType.DOWNLOAD_START);
                    break;
                
                case ITask.TASK_STATUS_PROCESS:
                    Logger.d(TAG, "[DOWNLOAD]RETURN TASK PROCESS, NAME:"
                            + msgId);
                    //通知页面 下载进度
                    sendMessage(FusionMessageType.DownloadType.DOWNLOADING,
                            new ProgressModel(msgId, loadTask.getCurrentSize(),
                                    loadTask.getTotalSize(),
                                    loadTask.getPercent()));
                    break;
                
                case ITask.TASK_STATUS_WARTING:
                    Logger.d(TAG, "[DOWNLOAD]TASK IS WAITING, NAME:" + msgId);
                    break;
                
                case ITask.TASK_STATUS_FINISHED:
                    Logger.d(TAG, "[DOWNLOAD]TASK IS FINISHED, NAME:" + msgId);
                    DownloadHttpTask dlHttpTask = (DownloadHttpTask) loadTask;
                    String filepath = dlHttpTask.getStorePath();
                    Logger.d(TAG, "filepath : " + filepath);
                    
                    //file路径更新到数据库中                   
                    ContentValues mediaPathCV = new ContentValues();
                    mediaPathCV.put(MediaIndexColumns.MEDIA_PATH, filepath);
                    MediaIndexDbAdapter.getInstance(mContext).update(msgId,
                            mediaPathCV);
                    
                    //把文件路径返回到页面上
                    // 返回msgid(用于区分不同任务)及保存路径
                    sendMessage(FusionMessageType.DownloadType.DOWNLOAD_FINISH,
                            new String[] { msgId, filepath });
                    break;
                
                case ITask.TASK_STATUS_DELETED:
                    Logger.d(TAG, "[DOWNLOAD]TASK IS DELETED, NAME:" + msgId);
                    break;
                
                case ITask.TASK_STATUS_ERROR:
                    Logger.d(TAG, "[DOWNLOAD]TASK MET ERROR, NAME:" + msgId);
                    //查询数据库中下载失败次数，在原来的失败次数上+1
                    int tryTime = MediaIndexDbAdapter.getInstance(mContext)
                            .queryByMsgId(msgId)
                            .getDownloadTryTimes() + 1;
                    
                    //更新数据库下载次数
                    ContentValues tryTimesCV = new ContentValues();
                    tryTimesCV.put(MediaIndexColumns.DOWNLOAD_TRY_TIMES,
                            tryTime);
                    MediaIndexDbAdapter.getInstance(mContext).update(msgId,
                            tryTimesCV);
                    
                    //发消息给页面，告诉它下载失败
                    sendMessage(FusionMessageType.DownloadType.DOWNLOAD_FAILED,
                            msgId);
                    break;
                
                case ITask.TASK_STATUS_STOPPED:
                    Logger.d(TAG, "[DOWNLOAD]TASK IS STOPED, NAME:" + msgId);
                    //发消息给页面，告诉它下载已经暂停
                    sendEmptyMessage(FusionMessageType.DownloadType.DOWNLOAD_PAUSE);
                    break;
                
                default:
                    break;
            }
            
        }
        
    };
    
    /**
     * 群组聊天消息数据库Uri
     */
    private Uri m1VNMsgUri;
    
    /**
     * 群组成员信息数据库Uri
     */
    private Uri m1VNMemberInfoUri;
    
    /**
     * 群组信息数据库Uri
     */
    private Uri m1VNInfoUri;
    
    /**
     * 与好友聊天消息数据库Uri
     */
    private Uri m1V1MsgUri;
    
    /**
     * 好友用户信息数据库Uri
     */
    private Uri m1V1FriendInfoUri;
    
    /**
     * 多媒体索引表数据库Uri
     */
    private Uri mMediaIndexUri;
    
    /**
     * 构造方法
     * @param context context
     * @param serviceSender serviceSender
     */
    public ImLogic(Context context, IServiceSender serviceSender)
    {
        mContext = context;
        mChatXmppSender = new ImXmppSender(serviceSender);
        TaskManagerFactory.init(context);
        mTaskManager = TaskManagerFactory.getTaskManager();
        mTaskManager.addTaskStatusListener(mTaskStatusListener);
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#register1V1DataObserver(java.lang.String)
     */
    @Override
    public void register1V1DataObserver(String friendUserID)
    {
        // 1V1需要监听该好友对应的消息表以及该好友的用户信息表
        // TODO: 根据friendUserID组装Uri值
        m1V1MsgUri = Uri.withAppendedPath(URIField.MESSAGE_WITH_FRIENDID_URI,
                friendUserID);
        //        m1V1FriendInfoUri = null;
        mMediaIndexUri = URIField.MEDIAINDEX_URI;
        registerObserver(m1V1MsgUri);
        registerObserver(mMediaIndexUri);
        //        registerObserver(m1V1FriendInfoUri);
    }
    
    /**
     * 监听该群组对应的群组消息表以及该群组成员的成员信息表
     *  @param groupID 群组id
     */
    @Override
    public void register1VNDataObserver(String groupID)
    {
        // 1VN需要监听该群组对应的群组消息表以及该群组成员的成员信息表
        // 根据groupID组装Uri值
        m1VNMsgUri = Uri.withAppendedPath(URIField.GROUPMESSAGE_WITH_GROUPID_URI,
                groupID);
        //仅监听该群组的成员信息变更
        m1VNMemberInfoUri = Uri.withAppendedPath(URIField.GROUPMEMBER_WITH_GROUPID_URI,
                groupID);
        //根据GroupID监听群组信息表
        m1VNInfoUri = Uri.withAppendedPath(URIField.GROUPINFO_WITH_GROUPID_URI,
                groupID);
        mMediaIndexUri = URIField.MEDIAINDEX_URI;
        registerObserver(m1VNMsgUri);
        registerObserver(m1VNMemberInfoUri);
        registerObserver(m1VNInfoUri);
        registerObserver(mMediaIndexUri);
    }
    
    /**
     * 取消监听该好友对应的消息表以及该好友的用户信息表
     *  @param friendUserID 聊天对象  user id
     */
    @Override
    public void unregister1V1DataObserver(String friendUserID)
    {
        unRegisterObserver(m1V1MsgUri);
        unRegisterObserver(m1V1FriendInfoUri);
        unRegisterObserver(mMediaIndexUri);
        m1V1MsgUri = null;
        m1V1FriendInfoUri = null;
        mMediaIndexUri = null;
    }
    
    /**
     * 取消监听该群组对应的群组消息表以及该群组成员的成员信息表
     *  @param groupID 群组id
     */
    @Override
    public void unregister1VNDataObserver(String groupID)
    {
        unRegisterObserver(m1VNMsgUri);
        unRegisterObserver(m1VNMemberInfoUri);
        unRegisterObserver(m1VNInfoUri);
        unRegisterObserver(mMediaIndexUri);
        
        m1VNMsgUri = null;
        m1VNMemberInfoUri = null;
        m1VNInfoUri = null;
        mMediaIndexUri = null;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#get1V1MsgList()
     */
    
    @Override
    public BaseMsgCursorWrapper get1V1MsgList(String friendUserID)
    {
        return new MsgCursorWrapper(MessageDbAdapter.getInstance(mContext)
                .queryAllByFriendUserIdWithCursor(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        friendUserID));
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#get1VNMsgList()
     */
    
    @Override
    public BaseMsgCursorWrapper get1VNMsgList(String groupId)
    {
        Logger.e(TAG, "userSysID===="
                + FusionConfig.getInstance().getAasResult().getUserSysId()
                + "groupID=====" + groupId);
        return new GroupMsgCursorWrapper(
                GroupMessageDbAdapter.getInstance(mContext)
                        .queryAllMessageByGroupId(FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId(),
                                groupId));
    }
    
    /**
     * 首次登录，自动发给小秘书的消息内容是：first login，消息类型：im-usage<br>
     * 注：首次发送小秘书，不保存消息到数据库
     * 
     * @see com.huawei.basic.android.im.logic.im.IImLogic#sendToSecretary(java.lang.String)
     */
    
    @Override
    public void sendToSecretary()
    {
        //插入小秘书固定引导信息
        insertSecretaryGuideMsg();
        // 首次登录发给小秘书的消息，不插入数据库
        // 构建MessageModel
        MessageModel msgModel = buildMessage(FusionCode.XmppConfig.SECRETARY_ID,
                FusionCode.Common.MESSAGE_TO_SECRETARY,
                null);
        
        if (msgModel != null)
        {
            // 调用ImXmppSender发送消息
            String msgSequence = mChatXmppSender.send1V1Message(msgModel);
            // 如果msgSequence不为空，说明调用命令成功，状态改为待发送
            if (msgSequence != null)
            {
                msgModel.setMsgSequence(msgSequence);
                msgModel.setMsgStatus(MessageModel.MSGSTATUS_PREPARE_SEND);
            }
            // 消息发送失败
            else
            {
                msgModel.setMsgStatus(MessageModel.MSGSTATUS_SEND_FAIL);
            }
        }
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IChatLogic#send1V1Message(java.lang.String, java.lang.String)
     */
    
    @Override
    public void send1V1Message(String to, String textContent)
    {
        // 构建MessageModel
        MessageModel msgModel = buildMessage(to, textContent, null);
        
        if (msgModel != null)
        {
            
            // 如果是小秘书消息
            if (FusionCode.XmppConfig.SECRETARY_ID.equals(to))
            {
                //将message保存数据库
                MessageDbAdapter.getInstance(mContext)
                        .insert(ConversationModel.CONVERSATIONTYPE_SECRET,
                                msgModel);
            }
            else
            {
                //将message保存数据库
                MessageDbAdapter.getInstance(mContext)
                        .insert(ConversationModel.CONVERSATIONTYPE_1V1,
                                msgModel);
            }
            
            // 调用发送消息并更新数据库状态
            sendMsgAndUpdateDB(msgModel);
        }
    }
    
    /**
     * 发送多媒体消息时，此方法仅负责将消息构造好并存入到数据库，具体的多媒体上传及发送消息到XMPP由MsgItem在展示时完成
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IChatLogic#
     * send1V1Message(java.lang.String, java.lang.String, com.huawei.basic.android.im.logic.model.MediaIndexModel)
     */
    
    @Override
    public void send1V1Message(String to, String textContent,
            MediaIndexModel mediaIndex)
    {
        // 构建MessageModel
        MessageModel msgModel = buildMessage(to, textContent, mediaIndex);
        
        if (msgModel != null)
        {
            // 如果是小秘书消息
            if (FusionCode.XmppConfig.SECRETARY_ID.equals(to))
            {
                //将message保存数据库
                MessageDbAdapter.getInstance(mContext)
                        .insert(ConversationModel.CONVERSATIONTYPE_SECRET,
                                msgModel);
            }
            else
            {
                //将message保存数据库
                MessageDbAdapter.getInstance(mContext)
                        .insert(ConversationModel.CONVERSATIONTYPE_1V1,
                                msgModel);
            }
            
            //多媒体文件处理
            switch (mediaIndex.getMediaType())
            {
            /*
             * 图片，上传
             */
                case MediaIndexModel.MEDIATYPE_IMG:
                    uploadFile(msgModel);
                    break;
                
                /*
                 * 视频文件，上传
                 */
                case MediaIndexModel.MEDIATYPE_VIDEO:
                    uploadFile(msgModel);
                    break;
                
                /*
                 * 音频文件，上传
                 */
                case MediaIndexModel.MEDIATYPE_AUDIO:
                    uploadFile(msgModel);
                    break;
                
                /*
                 * 贴图信息，直接发送
                 */
                case MediaIndexModel.MEDIATYPE_EMOJI:
                    sendMsgAndUpdateDB(msgModel);
                    break;
                /**
                 * 位置信息，直接发送   
                 */
                case MediaIndexModel.MEDIATYPE_LOCATION:
                    sendMsgAndUpdateDB(msgModel);
                    break;
                default:
                    Logger.w(TAG, "MediaIndexModel can not support");
                    break;
            }
        }
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IChatLogic#send1VNMessage(java.lang.String, java.lang.String)
     */
    
    @Override
    public void send1VNMessage(String to, String textContent)
    {
        // 构建MessageModel
        GroupMessageModel msgModel = buildGroupMessage(to, textContent, null);
        if (msgModel != null)
        {
            
            // 保存到数据库；此处判断会话类型
            GroupMessageDbAdapter.getInstance(mContext)
                    .insertGroupMsg(FusionConfig.getInstance()
                            .getAasResult()
                            .getUserSysId(),
                            ConversationModel.CONVERSATIONTYPE_GROUP,
                            msgModel);
            
            // 调用发送消息并更新数据库状态
            sendMsgAndUpdateDB(msgModel);
            
        }
        
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IChatLogic#
     * send1VNMessage(java.lang.String, java.lang.String, com.huawei.basic.android.im.logic.model.MediaIndexModel)
     */
    
    @Override
    public void send1VNMessage(String to, String textContent,
            MediaIndexModel mediaIndex)
    {
        // 构建MessageModel
        GroupMessageModel msgModel = buildGroupMessage(to,
                textContent,
                mediaIndex);
        if (msgModel != null)
        {
            
            // 保存到数据库；此处判断会话类型
            GroupMessageDbAdapter.getInstance(mContext)
                    .insertGroupMsg(FusionConfig.getInstance()
                            .getAasResult()
                            .getUserSysId(),
                            ConversationModel.CONVERSATIONTYPE_GROUP,
                            msgModel);
            
            //多媒体文件处理
            switch (mediaIndex.getMediaType())
            {
            /*
             * 图片，上传
             */
                case MediaIndexModel.MEDIATYPE_IMG:
                    uploadFile(msgModel);
                    break;
                
                /*
                 * 视频文件，上传
                 */
                case MediaIndexModel.MEDIATYPE_VIDEO:
                    uploadFile(msgModel);
                    break;
                
                /*
                 * 音频文件，上传
                 */
                case MediaIndexModel.MEDIATYPE_AUDIO:
                    uploadFile(msgModel);
                    break;
                
                /*
                 * 贴图消息，直接发送
                 */
                case MediaIndexModel.MEDIATYPE_EMOJI:
                    sendMsgAndUpdateDB(msgModel);
                    break;
                /**
                 * 位置信息
                 */
                case MediaIndexModel.MEDIATYPE_LOCATION:
                    Logger.i(TAG, "发送信息并更新数据库");
                    sendMsgAndUpdateDB(msgModel);
                    break;
                default:
                    Logger.w(TAG, "MediaIndexModel can not support");
                    break;
            }
        }
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#get1V1UnreadAudioMsgIds(java.lang.String)
     */
    
    @Override
    public List<String> get1V1UnreadAudioMsgIds(String friendUserId)
    {
        return MessageDbAdapter.getInstance(mContext)
                .getUnreadAudioMsgIds(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        friendUserId);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#get1VNUnreadAudioMsgIds(java.lang.String)
     */
    
    @Override
    public List<String> get1VNUnreadAudioMsgIds(String groupId)
    {
        return GroupMessageDbAdapter.getInstance(mContext)
                .getUnreadAudioMsgIds(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        groupId);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getFace(java.lang.String)
     */
    
    @Override
    public Drawable getFace(String userID)
    {
        Drawable drawble = null;
        FaceThumbnailModel ftm = FaceThumbnailDbAdapter.getInstance(mContext)
                .queryByFaceId(userID);
        if (ftm != null)
        {
            // 系统头像
            if (SystemFacesUtil.isSystemFaceUrl(ftm.getFaceUrl()))
            {
                drawble = mContext.getResources()
                        .getDrawable(SystemFacesUtil.getFaceImageResourceIdByFaceUrl(ftm.getFaceUrl()));
            }
            // 自定义头像
            else
            {
                
                byte[] faceBytes = ftm.getFaceBytes();
                // 自定义头像
                if (faceBytes != null)
                {
                    BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
                    mBitmapOptions.inPurgeable = true;
                    mBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(faceBytes,
                            0,
                            faceBytes.length,
                            mBitmapOptions);
                    drawble = new BitmapDrawable(bitmap);
                }
            }
        }
        return drawble;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getNick(java.lang.String)
     */
    
    @Override
    public String getGroupMemberNickName(String userID, String memberGroupId)
    {
        GroupMemberModel groupmemberModel = GroupMemberDbAdapter.getInstance(mContext)
                .queryByMemberUserId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        memberGroupId,
                        userID);
        if (groupmemberModel != null)
        {
            return groupmemberModel.getMemberNick();
        }
        return null;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getMyFace()
     */
    
    @Override
    public Drawable getMyFace()
    {
        return getFace(FusionConfig.getInstance().getAasResult().getUserID());
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getMyNickName()
     */
    @Override
    public String getMyNickName()
    {
        ContactInfoModel infoModel = ContactInfoDbAdapter.getInstance(mContext)
                .queryMyProfile(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId());
        if (infoModel != null)
        {
            return infoModel.getNickName();
        }
        return null;
    }
    
    /**
     * 进入1v1聊天页面后，把接收到的该好友的所有消息置为已读<BR>
     * @param friendUserId 好友id
     * @see com.huawei.basic.android.im.logic.im.IImLogic#setAll1V1MsgAsReaded(java.lang.String)
     */
    public void setAll1V1MsgAsReaded(final String friendUserId)
    {
        // 将数据库中所有与该好友的未读消息的状态更改为已读消息
        MessageDbAdapter.getInstance(mContext)
                .changeAllMsgToReaded(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        friendUserId);
        
        // 发送阅读报告
        new Thread(new Runnable()
        {
            
            @Override
            public void run()
            {
                //处理未读需要阅读报告MSGSTATUS_UNREAD_NEED_REPORT(10)的未读消息 
                List<MessageModel> needRptUnreadMgs = MessageDbAdapter.getInstance(mContext)
                        .queryAllMessageByMsgStatus(FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId(),
                                BaseMessageModel.MSGSTATUS_READED_NEED_REPORT,
                                friendUserId);
                if (needRptUnreadMgs != null && needRptUnreadMgs.size() > 0)
                {
                    for (MessageModel msg : needRptUnreadMgs)
                    {
                        //发送阅读报告
                        int ret = mChatXmppSender.sendReport(msg.getMsgSequence(),
                                msg.getFriendUserId(),
                                MsgXmppMng.IMRespReportType.READ);
                        
                        //如果调用成功 ,消息状态设置为MSGSTATUS_READED_NO_REPORT(12)
                        if (SEND_REPORT_SUCCESS == ret)
                        {
                            //更新数据库
                            updateMsgStatus(msg.getMsgSequence(),
                                    msg.getFriendUserId(),
                                    BaseMessageModel.MSGSTATUS_READED_NO_REPORT);
                        }
                    }
                }
            }
        }).start();
    }
    
    /**
     * 
     * 进入1vN聊天页面后，把接收到的该群组的所有消息置为已读<BR>
     * @param groupId 群组id
     * @see com.huawei.basic.android.im.logic.im.IImLogic#setAll1V1MsgAsReaded(java.lang.String)
     */
    public void setAll1VNMsgAsReaded(String groupId)
    {
        //返回数据库修改了多少条记录
        int result = GroupMessageDbAdapter.getInstance(mContext)
                .changeAllMsgToReaded(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        groupId);
        Logger.i(TAG, "Changing " + result
                + " Messages' status to readed. groupId is " + groupId);
    }
    
    /**
     * 处理查询到未读的消息，包括更改消息状态和是否发送递送报告<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#set1V1MsgAsReaded(java.lang.String)
     */
    @Override
    public void set1V1MsgAsReaded(MessageModel msg)
    {
        //model判空操作
        if (msg != null)
        {
            int msgStatus = msg.getMsgStatus();
            // 1.将所有消息设为已读
            //设置需要发送阅读报告消息为已读
            if (msgStatus == BaseMessageModel.MSGSTATUS_UNREAD_NEED_REPORT)
            {
                //发送阅读报告
                int ret = mChatXmppSender.sendReport(msg.getMsgSequence(),
                        msg.getFriendUserId(),
                        MsgXmppMng.IMRespReportType.READ);
                
                //如果调用成功 ,消息状态设置为MSGSTATUS_READED_NO_REPORT(12)
                if (SEND_REPORT_SUCCESS == ret)
                {
                    msgStatus = BaseMessageModel.MSGSTATUS_READED_NO_REPORT;
                }
                else
                {
                    //如果调用成功 ,消息状态设置为MSGSTATUS_READED_NEED_REPORT(11)
                    msgStatus = BaseMessageModel.MSGSTATUS_READED_NEED_REPORT;
                }
                
                //更新数据库
                updateMsgStatus(msg.getMsgSequence(),
                        msg.getFriendUserId(),
                        msgStatus);
            }
            //设置不需要发送阅读报告消息为已读
            else if (msgStatus == BaseMessageModel.MSGSTATUS_UNREAD_NO_REPORT)
            {
                msgStatus = BaseMessageModel.MSGSTATUS_READED_NO_REPORT;
                //更新数据库
                updateMsgStatus(msg.getMsgSequence(),
                        msg.getFriendUserId(),
                        msgStatus);
            }
        }
    }
    
    /**
     * 将1vN消息设为已读<BR>
     * @param msg GroupMessageModel 消息模型
     * @see com.huawei.basic.android.im.logic.im.IImLogic#set1VNMsgAsReaded(java.lang.String)
     */
    public void set1VNMsgAsReaded(GroupMessageModel msg)
    {
        GroupMessageDbAdapter.getInstance(mContext)
                .updateByMsgSequence(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        msg.getGroupId(),
                        msg.getMsgSequence(),
                        GroupMessageModel.MSGSTATUS_READED_NO_REPORT);
        
    }
    
    /**
     * 1v1消息重发<BR>
     * 重发消息时，从数据库获取该消息，并调用send方法，重新发送一条消息，原消息不做更改
     * @param msg MessageModel
     * @see com.huawei.basic.android.im.logic.im.IImLogic#resend1V1Message(java.lang.String)
     */
    @Override
    public void resend1V1Message(MessageModel msg)
    {
        // 发送媒体消息
        if (msg.getMediaIndex() != null)
        {
            send1V1Message(msg.getFriendUserId(),
                    msg.getMsgContent(),
                    msg.getMediaIndex());
        }
        
        // 发送普通消息
        else
        {
            send1V1Message(msg.getFriendUserId(), msg.getMsgContent());
        }
    }
    
    /**
     * 1VN消息重发<BR>
     * 重发消息时，从数据库获取该消息，并调用send方法，重新发送一条消息，原消息不做更改
     * @param msg MessageModel
     * @see com.huawei.basic.android.im.logic.im.IImLogic#resend1VNMessage(java.lang.String)
     */
    @Override
    public void resend1VNMessage(GroupMessageModel msg)
    {
        // 发送媒体消息
        if (msg.getMediaIndex() != null)
        {
            send1VNMessage(msg.getGroupId(),
                    msg.getMsgContent(),
                    msg.getMediaIndex());
        }
        
        // 发送普通消息
        else
        {
            send1VNMessage(msg.getGroupId(), msg.getMsgContent());
        }
    }
    
    /**
     * 1v1聊天删除单条消息<BR>
     * @param msgId 消息ID
     * @see com.huawei.basic.android.im.logic.im.IImLogic#delete1V1Message(java.lang.String)
     */
    @Override
    public void delete1V1Message(String msgId)
    {
        MessageDbAdapter.getInstance(mContext)
                .deleteByMsgId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        msgId);
    }
    
    /**
     * 1vN聊天删除单条消息<BR>
     * @param msgId 消息ID
     * @see com.huawei.basic.android.im.logic.im.IImLogic#delete1VNMessage(java.lang.String)
     */
    @Override
    public void delete1VNMessage(String msgId)
    {
        GroupMessageModel messageModel = GroupMessageDbAdapter.getInstance(mContext)
                .queryByMsgId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        msgId);
        
        GroupMessageDbAdapter.getInstance(mContext)
                .deleteByMsgId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        msgId,
                        messageModel.getMsgType(),
                        messageModel.getGroupId());
    }
    
    /**
     * 清除1V1消息<BR>
     * @param friendUserID 好友ID
     * @see com.huawei.basic.android.im.logic.im.IImLogic#clear1V1Message(java.lang.String)
     */
    @Override
    public void clear1V1Message(String friendUserID)
    {
        MessageDbAdapter.getInstance(mContext)
                .deleteByFriendUserId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        friendUserID);
    }
    
    /**
     * 清除1VN消息<BR>
     * @param groupID groupID
     * @see com.huawei.basic.android.im.logic.im.IImLogic#clear1VNMessage(java.lang.String)
     */
    @Override
    public void clear1VNMessage(String groupID)
    {
        GroupMessageDbAdapter.getInstance(mContext)
                .deleteByConversationId(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        groupID);
    }
    
    /**
     * 发送1V1message<BR>
     * @param msgId messageID
     * @param friendUserIds 好友UserID
     * @see com.huawei.basic.android.im.logic.im.IImLogic#transfer1V1Message(java.lang.String, java.lang.String[])
     */
    @Override
    public void transfer1V1Message(String msgId, String[] friendUserIds)
    {
        
        if (friendUserIds != null)
        {
            MessageModel message = MessageDbAdapter.getInstance(mContext)
                    .queryByMsgId(FusionConfig.getInstance()
                            .getAasResult()
                            .getUserSysId(),
                            msgId);
            for (String friendUserID : friendUserIds)
            {
                send1V1Message(friendUserID,
                        message.getMsgContent(),
                        message.getMediaIndex());
            }
        }
        
    }
    
    /**
     * 发送1VNmessage<BR>
     * @param msgId messageID
     * @param friendUserIds 好友UserID
     * @see com.huawei.basic.android.im.logic.im.IImLogic#transfer1VNMessage(java.lang.String, java.lang.String[])
     */
    @Override
    public void transfer1VNMessage(String msgId, String[] friendUserIds)
    {
        if (friendUserIds != null)
        {
            GroupMessageModel message = GroupMessageDbAdapter.getInstance(mContext)
                    .queryByMsgId(FusionConfig.getInstance()
                            .getAasResult()
                            .getUserSysId(),
                            msgId);
            for (String friendUserID : friendUserIds)
            {
                send1V1Message(friendUserID,
                        message.getMsgContent(),
                        message.getMediaIndex());
            }
        }
    }
    
    /**
     * 获得联系人信息<BR>
     * @param friendUserId 好友userID
     * @return contactInfoModel
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getContactInfoModel(java.lang.String)
     */
    @Override
    public ContactInfoModel getContactInfoModel(String friendUserId)
    {
        return ContactInfoDbAdapter.getInstance(mContext)
                .queryByFriendUserIdNoUnion(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        friendUserId);
    }
    
    /**
     * 展示媒体原文件的接口
     * @param msgId String 消息id
     * @param fromType FromType 消息来源类型
     * @return 如果该媒体已经在下载，则返回下载进度
     */
    @Override
    public ProgressModel downloadMedia(String msgId, FromType fromType)
    {
        //查看数据库中多媒体信息
        MediaIndexModel model = MediaIndexDbAdapter.getInstance(mContext)
                .queryByMsgId(msgId);
        ProgressModel progressModel = null;
        //判断数据库中是否有本地路径，如果有则不进行下载
        if (null == model.getMediaPath())
        {
            String url = model.getMediaURL();
            if (null == url)
            {
                Logger.e(TAG, "MediaURL is null in method downloadMedia");
                return null;
            }
            String downloadDir = getLocalDir(model.getMediaType(), fromType);
            if (null == downloadDir)
            {
                Logger.e(TAG, "Download path is null");
                return null;
            }
            //下载源文件
            RcsDownloadHttpTask downloadTask = new RcsDownloadHttpTask();
            downloadTask.setStoreDir(downloadDir);
            downloadTask.setDownloadUrl(url);
            
            // 将消息id设置为下载任务名，以便在回调中区分不同任务
            downloadTask.setName(msgId);
            try
            {
                mTaskManager.createTask(downloadTask);
                mTaskManager.startTask(downloadTask.getId());
            }
            catch (TaskException e)
            {
                //对下载任务已经存在，则不再进行下载
                if (TaskException.TASK_IS_EXIST == e.getCode())
                {
                    Logger.d(TAG, "[DOWNLOAD]TASK exists, NAME:" + msgId);
                    //获取任务
                    downloadTask = (RcsDownloadHttpTask) mTaskManager.findTaskById(downloadTask.getId());
                    
                    //构造progressModel
                    progressModel = new ProgressModel(msgId,
                            downloadTask.getCurrentSize(),
                            downloadTask.getTotalSize(),
                            downloadTask.getPercent());
                    
                    //如果下载任务已经完成，则更新数据库
                    if (ITask.TASK_STATUS_FINISHED == downloadTask.getStatus())
                    {
                        
                        Logger.d(TAG, "[DOWNLOAD]TASK IS FINISHED, NAME:"
                                + msgId);
                        String filepath = downloadTask.getStorePath();
                        Logger.d(TAG, "filepath : " + filepath);
                        
                        //file路径更新到数据库中                   
                        ContentValues mediaPathCV = new ContentValues();
                        mediaPathCV.put(MediaIndexColumns.MEDIA_PATH, filepath);
                        MediaIndexDbAdapter.getInstance(mContext).update(msgId,
                                mediaPathCV);
                        
                        //把文件路径返回到页面上
                        sendMessage(FusionMessageType.DownloadType.DOWNLOAD_FINISH,
                                new String[] { msgId, filepath });
                    }
                    //如果没有完成，则添加监听到任务中
                    else
                    {
                        downloadTask.addOwnerStatusListener(mTaskStatusListener);
                    }
                }
                else
                {
                    Logger.e(TAG, e.getMessage(), e);
                }
            }
        }
        return progressModel;
    }
    
    /**
     * 多媒体文件下载暂停接口
     * @param msgId String 消息id
     */
    @Override
    public void pauseDownload(String msgId)
    {
        List<ITask> taskList = mTaskManager.getDisplayTasks();
        if (taskList != null)
        {
            for (ITask task : taskList)
            {
                if (task.getName() != null && task.getName().equals(msgId))
                {
                    try
                    {
                        mTaskManager.stopTask(task.getId());
                    }
                    catch (TaskException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    
    /**
     * 
     * 暂停的任务继续下载<BR>
     * @param msgId String 消息id
     */
    @Override
    public void continueDownload(String msgId)
    {
        List<ITask> taskList = mTaskManager.getDisplayTasks();
        if (taskList != null)
        {
            for (ITask task : taskList)
            {
                if (task.getName() != null && task.getName().equals(msgId))
                {
                    try
                    {
                        mTaskManager.startTask(task.getId());
                    }
                    catch (TaskException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    
    /**
     * 多媒体文件下载停止接口
     * @param msgId String 消息id
     */
    @Override
    public void stopDownload(String msgId)
    {
    }
    
    /**
     * 多媒体文件上传停止接口
     * @param taskId 消息id
     */
    @Override
    public void stopUpload(String taskId)
    {
        // TODO:暂不实现
    }
    
    /**
     * 音频保存的路径
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getAudioFilePath()
     */
    
    @Override
    public String getAudioFilePath()
    {
        //获取音频目录
        String parentDir = getLocalDir(MediaIndexModel.MEDIATYPE_AUDIO, FromType.SEND);
        
        //如果目录为空，返回空
        if (null == parentDir)
        {
            return null;
        }
        
        return parentDir + DateUtil.getCurrentDateString() + ".amr";
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getImageFilePath()
     */
    @Override
    public String getImageFilePath(FromType fromType)
    {
        //获取图片目录
        String parentDir = getLocalDir(MediaIndexModel.MEDIATYPE_IMG, fromType);
        
        //如果目录为空，返回空
        if (parentDir == null)
        {
            return null;
        }
        
        return parentDir + DateUtil.getCurrentDateString() + ".jpg";
    }
   
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#getImageFilePath()
     */
    @Override
    public String getVideoFilePath(FromType fromType)
    {
        //获取图片目录
        String parentDir = getLocalDir(MediaIndexModel.MEDIATYPE_VIDEO, fromType);
        
        //如果目录为空，返回空
        if (parentDir == null)
        {
            return null;
        }
        
        return parentDir + DateUtil.getCurrentDateString() + ".mp4";
    }
    
    /**
     * 判断SDcard是否存在<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.logic.im.IImLogic#sdCardExist(android.content.Context)
     */
    
    @Override
    public boolean sdCardExist()
    {
        Logger.d(TAG,
                "getExternalStorageState : "
                        + Environment.getExternalStorageState());
        return Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())
                && FileUtil.isSuitableSizeForSDCard();
    }
    
    /**
     * 检查GPS是否已经开启
     *
     * @return 是否已经开启
     */
    public boolean isGPSEnabled()
    {
        return ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    /**
     * 获取下载的URL<BR>
     * [功能详细描述]
     * @param longitude 经纬度
     * @param latitude 经纬度
     * @return 根据经纬度构建下载URL字符串
     * @see com.huawei.basic.android.im.logic.im.IImLogic#buildDownloadURL(java.lang.String, com.huawei.basic.android.im.ui.im.item.BaseMsgItem.MsgType)
     */
    @Override
    public String buildLocationDlUrl(double longitude, double latitude)
    {
        StringBuilder buf = new StringBuilder(String.format(GOOGLE_MAP_IMG_URL,
                latitude,
                longitude,
                280,
                100 + CUT_HEIGHT,
                Locale.getDefault().getLanguage()));
        
        // 加入红色标记位
        buf.append("&markers=color:red%7c")
                .append(latitude)
                .append(',')
                .append(longitude);
        return buf.toString();
    }
    
    /**
     * 
     * 开启或关闭GPS
     * 这个方法是1个纯开关，如果当前是开启的，那么就会关闭它，反之亦然
     * 3是Widget中的图标按钮的序号
     * @throws Exception 发送广播时可能会抛出异常
     * @see com.huawei.basic.android.im.logic.im.IImLogic#toggleGPS()
     */
    @Override
    public void toggleGPS() throws Exception
    {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        PendingIntent.getBroadcast(mContext, 0, gpsIntent, 0).send();
    }
    
    /**
     * 
     * 当聊天的数据库相关表有更新时此方法会被调用<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.framework.logic.BaseLogic#onChangeByUri(boolean, android.net.Uri)
     */
    @Override
    protected void onChangeByUri(boolean selfChange, Uri uri)
    {
        super.onChangeByUri(selfChange, uri);
        Logger.d(TAG, "data changed");
        /*
         * 根据不同表的更新发送不同的Message到页面
         */
        // 好友信息变更
        if (uri.equals(m1V1FriendInfoUri))
        {
            sendEmptyMessage(FusionMessageType.ChatMessageType.MSGTYPE_FRIEND_INFO_REFRESH);
        }
        
        // 群组成员信息变更
        else if (uri.equals(m1VNMemberInfoUri))
        {
            sendEmptyMessage(FusionMessageType.ChatMessageType.MSGTYPE_MEMBER_INFO_REFRESH);
        }
        // 1V1/1VN聊天记录变更
        else if (uri.equals(m1V1MsgUri) || uri.equals(m1VNMsgUri))
        {
            sendEmptyMessage(FusionMessageType.ChatMessageType.MSGTYPE_MSG_REFRESH);
        }
        else if (uri.equals(m1VNInfoUri))
        {
            sendEmptyMessage(FusionMessageType.ChatMessageType.MSGTYPE_GROUP_INFO_REFRESH);
        }
        else if (uri.equals(mMediaIndexUri))
        {
            sendEmptyMessage(FusionMessageType.ChatMessageType.MSGTYPE_MEDIA_INDEX_REFRESH);
        }
    }
    
    /**
     * 插入小秘书固定提示<BR>
     * @see com.huawei.basic.android.im.logic.im.IImLogic#insertSecretaryGuideMsg()
     */
    
    private void insertSecretaryGuideMsg()
    {
        //插入小秘书固定引导进入新功能
        MessageModel secretaryMsgModelOne = new MessageModel();
        secretaryMsgModelOne.setMsgId(MessageUtils.generateMsgId());
        secretaryMsgModelOne.setFriendUserId(UriUtil.getHitalkIdFromJid(FusionCode.XmppConfig.SECRETARY_JID));
        secretaryMsgModelOne.setMsgSendOrRecv(MessageModel.MSGSENDORRECV_RECV);
        secretaryMsgModelOne.setMsgContent(mContext.getResources()
                .getString(R.string.hitalk_person_info));
        secretaryMsgModelOne.setUserSysId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        secretaryMsgModelOne.setMsgStatus(MessageModel.MSGSTATUS_UNREAD_NO_REPORT);
        secretaryMsgModelOne.setMsgType(MessageModel.MSGTYPE_TEXT);
        MessageDbAdapter.getInstance(mContext)
                .insert(ConversationModel.CONVERSATIONTYPE_SECRET,
                        secretaryMsgModelOne);
        
        MessageModel secretaryMsgModelTwo = new MessageModel();
        secretaryMsgModelTwo.setMsgId(MessageUtils.generateMsgId());
        secretaryMsgModelTwo.setFriendUserId(UriUtil.getHitalkIdFromJid(FusionCode.XmppConfig.SECRETARY_JID));
        secretaryMsgModelTwo.setMsgSendOrRecv(MessageModel.MSGSENDORRECV_RECV);
        secretaryMsgModelTwo.setMsgContent(mContext.getResources()
                .getString(R.string.hitalk_person_group));
        secretaryMsgModelTwo.setUserSysId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        secretaryMsgModelTwo.setMsgStatus(MessageModel.MSGSTATUS_UNREAD_NO_REPORT);
        secretaryMsgModelTwo.setMsgType(MessageModel.MSGTYPE_TEXT);
        MessageDbAdapter.getInstance(mContext)
                .insert(ConversationModel.CONVERSATIONTYPE_SECRET,
                        secretaryMsgModelTwo);
        
        Logger.i(TAG, "插入小秘书固定会话");
    }
    
    private GroupMessageModel buildGroupMessage(String to, String textContent,
            MediaIndexModel mediaIndex)
    {
        // 构建MessageModel
        if (mediaIndex == null && StringUtil.isNullOrEmpty(textContent))
        {
            Logger.w(TAG,
                    "MediaIndex and textContent are null in buildGroupMessage");
            return null;
        }
        GroupMessageModel msgModel = new GroupMessageModel();
        msgModel.setMsgId(MessageUtils.generateMsgId());
        
        //获取群组id
        msgModel.setGroupId(to);
        msgModel.setMsgSendOrRecv(MessageModel.MSGSENDORRECV_SEND);
        msgModel.setMsgTime(DateUtil.getCurrentDateString());
        msgModel.setMsgStatus(MessageModel.MSGSTATUS_PREPARE_SEND);
        // 发送者USER ID
        msgModel.setMemberUserId(FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        if (!StringUtil.isNullOrEmpty(textContent))
        {
            msgModel.setMsgContent(textContent);
        }
        if (mediaIndex != null)//TODO:没有设置多媒体的具体类型
        {
            msgModel.setMsgType(MessageModel.MSGTYPE_MEDIA);
            mediaIndex.setMsgId(msgModel.getMsgId());
            msgModel.setMediaIndex(mediaIndex);
        }
        else
        {
            msgModel.setMsgType(MessageModel.MSGTYPE_TEXT);
        }
        return msgModel;
    }
    
    private MessageModel buildMessage(String to, String textContent,
            MediaIndexModel mediaIndex)
    {
        // 构建MessageModel
        if (mediaIndex == null && StringUtil.isNullOrEmpty(textContent))
        {
            Logger.w(TAG, "MediaIndex and textContent are null in buildMessage");
            return null;
        }
        MessageModel msgModel = new MessageModel();
        msgModel.setUserSysId(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        msgModel.setMsgId(MessageUtils.generateMsgId());
        msgModel.setFriendUserId(to);
        msgModel.setMsgSendOrRecv(MessageModel.MSGSENDORRECV_SEND);
        msgModel.setMsgTime(DateUtil.getCurrentDateString());
        msgModel.setMsgStatus(MessageModel.MSGSTATUS_PREPARE_SEND);
        if (!StringUtil.isNullOrEmpty(textContent))
        {
            msgModel.setMsgContent(textContent);
        }
        if (mediaIndex != null)//TODO:没有设置多媒体的具体类型(在调用此方法之前就已经设置过了)
        {
            msgModel.setMsgType(MessageModel.MSGTYPE_MEDIA);
            mediaIndex.setMsgId(msgModel.getMsgId());
            msgModel.setMediaIndex(mediaIndex);
        }
        else
        {
            msgModel.setMsgType(MessageModel.MSGTYPE_TEXT);
        }
        
        //TODO:在构建多媒体消息时，在此处构建缩略图，并保存缩略图本地路径到数据库
        
        return msgModel;
    }
    
    /**
     * 
     * 文件上传<BR>
     *  
     * @param msg BaseMessageModel
     */
    private void uploadFile(final BaseMessageModel msg)
    {
        if (null == msg || null == msg.getMediaIndex()
                || null == msg.getMediaIndex().getMediaPath())
        {
            Logger.e(TAG, "file is null in method: uploadFile");
            return;
        }
        final String msgId = msg.getMsgId();
        UploadParam uploadParam = new UploadParam();
        uploadParam.setUserAccount(FusionConfig.getInstance()
                .getAasResult()
                .getUserID());
        uploadParam.setUploadType(UploadType.MESSAGE);
        if (msg instanceof MessageModel)
        {
            uploadParam.setReceiverType(ReceiverType.SINGLE_FRIEND);
            uploadParam.setReceiver(((MessageModel) msg).getFriendUserId());
        }
        else if (msg instanceof GroupMessageModel)
        {
            uploadParam.setReceiverType(ReceiverType.GROUP);
            uploadParam.setReceiver(((GroupMessageModel) msg).getGroupId());
        }
        
        IUploadListener uploadListener = new IUploadListener()
        {
            
            @Override
            public void onUploadFinish(UploadFileForURLResponse response)
            {
                sendMessage(FusionMessageType.UPloadType.UPLOAD_FINISH,
                        new ProgressModel(msgId, 0, 0, 100));
                List<UploadFileForURLResult> results = response.getUploadFileForURLResultList();
                if (results != null)
                {
                    String mediaSmallFileName = getFileName(msg.getMediaIndex()
                            .getMediaSmallPath());
                    String mediaFileName = getFileName(msg.getMediaIndex()
                            .getMediaPath());
                    for (UploadFileForURLResult result : results)
                    {
                        
                        if (mediaFileName != null
                                && mediaFileName.contains(result.getContentName()))
                        {
                            msg.getMediaIndex()
                                    .setMediaURL(result.getDownloadUrl());
                        }
                        else if (mediaSmallFileName != null
                                && mediaSmallFileName.contains(result.getContentName()))
                        {
                            msg.getMediaIndex()
                                    .setMediaSmallURL(result.getDownloadUrl());
                        }
                    }
                }
                
                if (msg.getMediaIndex().getMediaURL() != null)
                {
                    // 发送消息,并把消息状态更新到数据库
                    sendMsgAndUpdateDB(msg);
                }
                else
                {
                    Logger.e(TAG, "msg.mediaUrl is null, cancel send!");
                    msg.setMsgStatus(MessageModel.MSGSTATUS_SEND_FAIL);
                    if (msg instanceof MessageModel)
                    {
                        MessageDbAdapter.getInstance(mContext)
                                .updateMessageRecord((MessageModel) msg);
                    }
                    else if (msg instanceof GroupMessageModel)
                    {
                        // 保存到数据库；此处判断会话类型
                        GroupMessageDbAdapter.getInstance(mContext)
                                .updateGroupMessageRecord((GroupMessageModel) msg);
                    }
                }
                
            }
            
            @Override
            public void onUploadStart()
            {
                
            }
            
            @Override
            public void onUploadPause()
            {
                
            }
            
            @Override
            public void onUploadStop()
            {
                
            }
            
            @Override
            public void onUploadFail(String errorInfo)
            {
                Logger.d(TAG, "[error message]" + errorInfo);
                sendMessage(FusionMessageType.UPloadType.UPLOAD_FAILED, msgId);
                // 更新消息状态为失败
                msg.setMsgStatus(BaseMessageModel.MSGSTATUS_SEND_FAIL);
                if (msg instanceof MessageModel)
                {
                    MessageDbAdapter.getInstance(mContext)
                            .updateMessageRecord((MessageModel) msg);
                }
                else if (msg instanceof GroupMessageModel)
                {
                    GroupMessageDbAdapter.getInstance(mContext)
                            .updateGroupMessageRecord((GroupMessageModel) msg);
                }
            }
            
            @Override
            public void onUploadProgress(int finishedSize, int totalSize,
                    double progressPercent)
            {
                Logger.d(TAG, "progressPercent[" + progressPercent + "]");
                sendMessage(FusionMessageType.UPloadType.UPLOADING,
                        new ProgressModel(msgId, finishedSize, totalSize,
                                (int) progressPercent));
                
            }
        };
        //  如果为视频文件先上传缩略图，再上传源文件
        if (msg.getMediaIndex().getMediaType() == MediaIndexModel.MEDIATYPE_VIDEO)
        {
            String thumbnailPath = msg.getMediaIndex().getMediaSmallPath();
            if (thumbnailPath != null)
            {
                // 添加缩略图文件
                UploadContentInfo thumbnailFileInfo = new UploadContentInfo();
                thumbnailFileInfo.setContentName(thumbnailPath.substring(thumbnailPath.lastIndexOf("/") + 1));
                thumbnailFileInfo.setContentDesc("the thumbnail of the video file");
                thumbnailFileInfo.setFilePath(thumbnailPath);
                thumbnailFileInfo.setMimeType(MimeType.IMG);
                uploadParam.addUploadContentInfoList(thumbnailFileInfo);
            }
            
            String videoPath = msg.getMediaIndex().getMediaPath();
            // 添加视频文件
            UploadContentInfo videoFileInfo = new UploadContentInfo();
            videoFileInfo.setMainContent(true);
            videoFileInfo.setContentName(videoPath.substring(videoPath.lastIndexOf("/") + 1));
            videoFileInfo.setContentDesc("the source file of video");
            videoFileInfo.setFilePath(videoPath);
            videoFileInfo.setMimeType(MimeType.VIDEO);
            uploadParam.addUploadContentInfoList(videoFileInfo);
            
        }
        else if (msg.getMediaIndex().getMediaType() == MediaIndexModel.MEDIATYPE_AUDIO)
        {
            String audioPath = msg.getMediaIndex().getMediaPath();
            UploadContentInfo audioFileInfo = new UploadContentInfo();
            audioFileInfo.setMainContent(true);
            audioFileInfo.setContentName(audioPath.substring(audioPath.lastIndexOf("/") + 1));
            audioFileInfo.setContentDesc("the source file of audio");
            audioFileInfo.setFilePath(msg.getMediaIndex().getMediaPath());
            audioFileInfo.setMimeType(MimeType.AUDIO);
            uploadParam.addUploadContentInfoList(audioFileInfo);
        }
        else if (msg.getMediaIndex().getMediaType() == MediaIndexModel.MEDIATYPE_IMG)
        {
            String imgPath = msg.getMediaIndex().getMediaPath();
            UploadContentInfo imageFileInfo = new UploadContentInfo();
            imageFileInfo.setMainContent(true);
            imageFileInfo.setContentName(imgPath.substring(imgPath.lastIndexOf("/") + 1));
            imageFileInfo.setContentDesc("the source file of image");
            imageFileInfo.setFilePath(msg.getMediaIndex().getMediaPath());
            imageFileInfo.setMimeType(MimeType.IMG);
            uploadParam.addUploadContentInfoList(imageFileInfo);
        }
        else
        {
            //系统不识别的文件格式
            Logger.e(TAG, "MediaType is wrong in method uploadFile");
        }
        new ContentUploader().upload(uploadParam, uploadListener);
    }
    
    /**
     * 将消息设为已读<BR>
     * @param msgSequence String
     * @param msgStatus 消息状态
     * @param friendUserId FriendUserID
     */
    private void updateMsgStatus(String msgSequence, String friendUserId,
            int msgStatus)
    {
        //更新数据库
        MessageDbAdapter.getInstance(mContext)
                .updateByMsgSequence(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        msgSequence,
                        friendUserId,
                        msgStatus);
    }
    
    /**
     * 
     * 获取下载到本地文件夹的路径<BR>
     * @param mediaType
     * @return String 下载本地文件夹路径
     */
    private String getLocalDir(int mediaType, FromType formType)
    {
        String localDir;
        String userId = FusionConfig.getInstance().getAasResult().getUserID();
        switch (mediaType)
        {
        /*
         * 下载为图片（位置图片也保存在image下面）
         */
            case MediaIndexModel.MEDIATYPE_IMG:
            case MediaIndexModel.MEDIATYPE_LOCATION:
                localDir = UriUtil.getLocalStorageDir(userId,
                        formType,
                        LocalDirType.IMAGE);
                break;
            
            /*
             * 下载为视频文件
             */
            case MediaIndexModel.MEDIATYPE_VIDEO:
                localDir = UriUtil.getLocalStorageDir(userId,
                        formType,
                        LocalDirType.VIDEO);
                break;
            
            /*
             * 下载为音频文件
             */
            case MediaIndexModel.MEDIATYPE_AUDIO:
                localDir = UriUtil.getLocalStorageDir(userId,
                        formType,
                        LocalDirType.VOICE);
                break;
            
            /*
             * 默认为下载到download目录中
             */
            default:
                localDir = UriUtil.getLocalStorageDir(userId,
                        formType,
                        LocalDirType.DOWNLOAD);
                break;
        }
        
        return localDir;
    }
    
    /**
     * 
     * 发送消息，并把根据返回码，更新消息状态<BR>
     * @param msgModel MessageModel 消息体
     */
    private void sendMsgAndUpdateDB(BaseMessageModel msgModel)
    {
        // 调用ImXmppSender发送消息
        String msgSequence = null;
        if (msgModel instanceof MessageModel)
        {
            msgSequence = mChatXmppSender.send1V1Message((MessageModel) msgModel);
        }
        else if (msgModel instanceof GroupMessageModel)
        {
            // 保存到数据库；此处判断会话类型
            msgSequence = mChatXmppSender.send1VNMessage((GroupMessageModel) msgModel);
        }
        
        //发送成功则填入msgSequence字段
        if (msgSequence != null)
        {
            msgModel.setMsgSequence(msgSequence);
        }
        //发送失败，更新状态为失败
        else
        {
            msgModel.setMsgStatus(MessageModel.MSGSTATUS_SEND_FAIL);
        }
        
        if (msgModel instanceof MessageModel)
        {
            MessageDbAdapter.getInstance(mContext)
                    .updateMessageRecord((MessageModel) msgModel);
        }
        else if (msgModel instanceof GroupMessageModel)
        {
            // 保存到数据库；此处判断会话类型
            GroupMessageDbAdapter.getInstance(mContext)
                    .updateGroupMessageRecord((GroupMessageModel) msgModel);
        }
    }
    
    private String getFileName(String path)
    {
        if (path != null)
        {
            int index = path.lastIndexOf("/");
            if (index != -1)
            {
                return path.substring(index + 1);
            }
        }
        return null;
    }
}
