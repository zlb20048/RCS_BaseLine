/*
 * 文件名: ImXmppMng.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.impl.xmpp;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.download.http.DownloadHttpTask;
import com.huawei.basic.android.im.component.download.http.RcsDownloadHttpTask;
import com.huawei.basic.android.im.component.load.TaskManagerFactory;
import com.huawei.basic.android.im.component.load.task.ITask;
import com.huawei.basic.android.im.component.load.task.ITaskManager;
import com.huawei.basic.android.im.component.load.task.ITaskStatusListener;
import com.huawei.basic.android.im.component.load.task.TaskException;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams;
import com.huawei.basic.android.im.component.net.xmpp.data.BaseParams.GroupParams;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification;
import com.huawei.basic.android.im.component.net.xmpp.data.GroupNotification.MessageReceivedNtfData;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass.CommonMessageData;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass.Request;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageData.MessageReportCmdData;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageNotification;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageNotification.MessageReceivedNtf;
import com.huawei.basic.android.im.component.net.xmpp.data.XmppResultCode;
import com.huawei.basic.android.im.component.notification.NotificationEntity;
import com.huawei.basic.android.im.component.notification.NotificationEntityManager;
import com.huawei.basic.android.im.component.service.impl.IObserver;
import com.huawei.basic.android.im.logic.adapter.db.ConversationDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupMessageDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.MediaIndexDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.MessageDbAdapter;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.logic.notification.IMNotificationEntity;
import com.huawei.basic.android.im.logic.notification.bean.IMNotificationBean;
import com.huawei.basic.android.im.ui.im.SingleChatActivity;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.MessageUtils;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;
import com.huawei.basic.android.im.utils.UriUtil.FromType;
import com.huawei.fast.IEngineBridge;

/**
 * 聊天模块XMPP管理器<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-14]
 */
public class MsgXmppMng extends XmppMng
{
    
    /**
     * 延迟发送时间
     */
    private static final long SEND_DELIVER_REPORT_DELAY_TIME = 2000L;
    
    /**
     * 延迟更新消息状态时间
     */
    private static final long UPDATE_BY_NTF_DELAY_TIME = 200L;
    
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
     * 消息数据库适配器
     */
    private MessageDbAdapter mMsgAdapter;
    
    /**
     * 群消息数据库适配器
     */
    private GroupMessageDbAdapter mGroupMsgAdapter;
    
    /**
     * 多媒体文件索引数据操作适配器
     */
    private MediaIndexDbAdapter mMediaIndexDbAdapter;
    
    /**
     * 会话表数据库操作适配器
     */
    private ConversationDbAdapter mConversationDbAdapter;
    
    /**
     * 送达报告队列
     */
    private MessengerQueue mMsgQueue;
    
    /**
     * 送达报告消息信息存储map
     */
    private Map<String, String[]> mPendingDeliveryRptRequests = new WeakHashMap<String, String[]>();
    
    /**
     * 根据返回的notify消息更新数据库中信息map
     */
    private Map<String, String[]> mPendingUpdateByNtfRequests = new WeakHashMap<String, String[]>();
    
    private HandlerThread mMsgThread;
    
    /**
     * 通知栏对象
     */
    private NotificationEntity notificationEntity;
    
    private String mCurrentFriendUserId;
    
    /**
     * 下载、上传任务管理器对象，用于下载和上传
     */
    private ITaskManager mTaskManager;
    
    /**
     * 构造方法
     * 
     * @param engineBridge
     *            IEngineBridge
     * @param observer
     *            IObserver
     */
    public MsgXmppMng(IEngineBridge engineBridge, IObserver observer)
    {
        super(engineBridge, observer);
        
        // 获取消息数据库适配器对象
        mMsgAdapter = MessageDbAdapter.getInstance(getObserver().getContext());
        mGroupMsgAdapter = GroupMessageDbAdapter.getInstance(getObserver().getContext());
        mMediaIndexDbAdapter = MediaIndexDbAdapter.getInstance(getObserver().getContext());
        mConversationDbAdapter = ConversationDbAdapter.getInstance(getObserver().getContext());
        
        // 送达报告线程
        mMsgThread = new HandlerThread("MsgXmppMng");
        mMsgThread.start();
        mMsgQueue = new MessengerQueue(mMsgThread.getLooper());
        TaskManagerFactory.init(getObserver().getContext());
        mTaskManager = TaskManagerFactory.getTaskManager();
        
        registIMNotificationReceiver();
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * 
     * @see com.huawei.basic.android.im.component.service.impl.xmpp.XmppMng#matched(java.lang.String,
     *      int)
     */
    
    @Override
    public boolean matched(String componentId, int notifyID)
    {
        
        // 如果是群组的消息订阅回调，使用MsgXmppMng来处理
        if (GroupParams.FAST_COM_GROUP_ID.equals(componentId))
        {
            if (notifyID == GroupParams.FAST_GROUP_NTF_MESSAGE_RECEIVED
                    || notifyID == GroupParams.FAST_GROUP_NTF_MESSAGE_SEND_ERROR
                    || notifyID == GroupParams.FAST_GROUP_NTF_MESSAGE_SEND)
            {
                return true;
            }
        }
        return super.matched(componentId, notifyID);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * 
     * @see com.huawei.basic.android.im.component.service.impl.xmpp.XmppMng#handleNotification(int,
     *      java.lang.String)
     */
    
    @Override
    public void handleNotification(String componentID, int notifyId, String data)
    {
        if (getComponentId().equals(componentID))
        {
            switch (notifyId)
            {
                /*
                 * 收到文件传输通知。
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_INVITING:
                    break;
                
                /*
                 * 文件传输请求被接受通知
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_ACCEPTED:
                    break;
                /*
                 * 文件传输进度通知
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_STATUS:
                    break;
                
                /*
                 * 文件传输关闭通知（原因结束、中断）
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_CLOSED:
                    break;
                
                /*
                 * 收到及时消息。及时消息的类型有message的type属性决定：
                 * chat为文本和多媒体消息，sm为短信，im-usage为我有小秘书消息
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_RECEIVED:
                    handleMsgReceived(data);
                    break;
                
                /*
                 * 收到递送报告
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_REPORTED:
                    handleMsgReported(data);
                    break;
                
                /*
                 * 收到广播通知
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_BROADCAST:
                    break;
                
                /*
                 * 收到评论通知
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_COMMENT:
                    break;
                
                /*
                 * 收到邮件通知
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_EMAIL:
                    break;
                
                /*
                 * 发送消息返回通知
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_SEND:
                    handleMessageNtfSend(data);
                    break;
                
                /*
                 * 系统公告通知
                 */
                case BaseParams.MessageParams.FAST_MESSAGE_NTF_SYS:
                    break;
                
                default:
                    break;
            }
        }
        else if (GroupParams.FAST_COM_GROUP_ID.equals(componentID))
        {
            switch (notifyId)
            {
                // 订阅群组消息
                case GroupParams.FAST_GROUP_NTF_MESSAGE_RECEIVED:
                    handleGroupMsgReceived(data);
                    break;
                // 接口文档中没有定义这个消息
                case GroupParams.FAST_GROUP_NTF_MESSAGE_SEND_ERROR:
                    break;
                case GroupParams.FAST_GROUP_NTF_MESSAGE_SEND:
                    handleGroupMessageNtfSend(data);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * 
     * @see com.huawei.basic.android.im.component.service.impl.xmpp.XmppMng#getComponentId()
     */
    
    @Override
    protected String getComponentId()
    {
        return BaseParams.MessageParams.FAST_COM_MESSAGE_ID;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * 
     * @see com.huawei.basic.android.im.component.service.impl.xmpp.XmppMng#subNotify()
     */
    
    @Override
    protected void subNotify()
    {
        
        /*
         * 收到文件传输通知。
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_INVITING);
        
        /*
         * 文件传输请求被接受通知
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_ACCEPTED);
        
        /*
         * 文件传输进度通知
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_STATUS);
        
        /*
         * 文件传输关闭通知（原因结束、中断）
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_FILE_CLOSED);
        
        /*
         * 收到及时消息。及时消息的类型有message的type属性决定： chat为文本和多媒体消息，sm为短信，im-usage为我有小秘书消息
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_RECEIVED);
        
        /*
         * 收到递送报告
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_REPORTED);
        
        /*
         * 收到广播通知
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_BROADCAST);
        
        /*
         * 收到评论通知
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_COMMENT);
        
        /*
         * 收到邮件通知
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_EMAIL);
        
        /*
         * 发送消息返回通知
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_SEND);
        
        /*
         * 系统公告通知
         */
        getEngineBridge().subNotify(getComponentId(),
                BaseParams.MessageParams.FAST_MESSAGE_NTF_SYS);
        
        // 订阅群组消息
        // 备注：下面的notifyID有可能与消息模块的notifyID有冲突
        getEngineBridge().subNotify(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_NTF_MESSAGE_RECEIVED);
        getEngineBridge().subNotify(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_NTF_MESSAGE_SEND_ERROR);
        getEngineBridge().subNotify(GroupParams.FAST_COM_GROUP_ID,
                GroupParams.FAST_GROUP_NTF_MESSAGE_SEND);
    }
    
    /**
     * 
     * 收到消息的处理<BR>
     * 
     * @param data
     *            收到的消息
     */
    private void handleMsgReceived(String data)
    {
        Logger.i(TAG, "Receive a message");
        MessageNotification.MessageReceivedNtf ntf = parseData(MessageNotification.MessageReceivedNtf.class,
                data);
        
        // 如果接收消息对象或是其中的消息体对象为空，直接返回
        if (ntf == null || ntf.getMessage() == null)
        {
            Logger.w(TAG,
                    "handleMsgReceived failed, MessageReceivedNtf is null");
            return;
            
        }
        String jid = ntf.getFrom();
        
        // 系统公告的jid统一转化为小秘书的jid
        if (FusionCode.XmppConfig.SYSTEM_ANNOUNCEMENT_JID.equalsIgnoreCase(jid)
                || IMChatType.SYS.equalsIgnoreCase(ntf.getMessage().getType()))
        {
            jid = FusionCode.XmppConfig.SECRETARY_JID;
            ntf.setFrom(jid);
        }
        Logger.i(TAG,
                "Finish inserting the receiving message messageSquence = "
                        + ntf.getId());
        
        // 如果为小秘书或无request消息体则设置报告发送类型为不发送任何报告
        if (null != ntf.getMessage().getRequest()
                && !FusionCode.XmppConfig.SECRETARY_JID.equalsIgnoreCase(jid))
        {
            String reportType = ntf.getMessage().getRequest().getType();
            
            if (reportType == null)
            {
                reportType = Request.TYPE_DELIVERY;
            }
            
            //如果报告类型为只需要送达报告，马上发送报告给服务器
            if (Request.TYPE_DELIVERY.equals(reportType))
            {
                //发送报告，并记录日志
                Logger.i(TAG,
                        "FAST_MESSAGE_CMD_REPORT has been invoked. msgSequence= "
                                + ntf.getId()
                                + " and iRet="
                                + sendReport(ntf.getId(),
                                        jid,
                                        IMRespReportType.RECEIVED));
            }
            if (Request.TYPE_ALL.equals(reportType))
            {
                
                // 参数加入参数队列
                String[] deliveryReportParams = new String[] {
                        this.getObserver().getUserSysID(), jid,
                        IMRespReportType.RECEIVED };
                mPendingDeliveryRptRequests.put(ntf.getId(),
                        deliveryReportParams);
                //延迟两秒钟再判断是否要发送送达报告
                mMsgQueue.sendMessageDelayed(mMsgQueue.obtainMessage(MessengerQueue.SEND_DELIVER_REPORT,
                        ntf.getId()),
                        SEND_DELIVER_REPORT_DELAY_TIME);
            }
            
        }
        
        // 保存到数据库
        insertReceiveMessage(ntf);
        Logger.i(TAG, "处理收到的消息，保存到数据库");
        // 弹出通知栏
        showNotification(UriUtil.getHitalkIdFromJid(ntf.getFrom()));
        // 将我发给这个帐号的所有消息变成已读//TODO:接收到消息的时候为什么要把发送出去的消息置为已读？ edit by yangfan.
        mMsgAdapter.changeMsgStatusByConversationId(this.getObserver()
                .getUserID(), jid, MessageModel.MSGSTATUS_READED);
    }
    
    /**
     * 显示通知栏<BR>
     * 
     * @param friendUserId
     *            好友会话ID，通知栏跳转时候用到
     */
    private void showNotification(String friendUserId)
    {
        Logger.d(TAG, "showNotification***********");
        KeyguardManager mKeyguardManager = (KeyguardManager) getObserver().getContext()
                .getSystemService(Context.KEYGUARD_SERVICE);
        ActivityManager am = (ActivityManager) getObserver().getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        // //如果在锁屏界面
        if (mKeyguardManager.inKeyguardRestrictedInputMode())
        {
            // 展示通知栏
            generateNotificationData(friendUserId, "");
        }
        else
        { // 非锁屏界面
          // 判断是否是会话详情界面
            ComponentName name = am.getRunningTasks(1).get(0).topActivity;
            if (!StringUtil.equals(name.getClassName(),
                    SingleChatActivity.class.getName()))
            {
                // 如果不是会话详情界面，展示通知栏
                generateNotificationData(friendUserId, "");
            }
            else
            {
                // 如果是会话详情界面 判断当前mCurrentFriendUserId
                if (!StringUtil.equals(mCurrentFriendUserId, friendUserId))
                {
                    // 如果会话详情friendUserId 与 当前收到的消息的friendUserId不同，展示通知栏
                    generateNotificationData(friendUserId, mCurrentFriendUserId);
                }
            }
        }
    }
    
    /**
     * 组织通知栏数据，并展示通知栏
     * @param friendUserId 收到消息的好友会话ID
     * @param currentFriendUserId 当前会话界面好友ID
     */
    private void generateNotificationData(String friendUserId,
            String currentFriendUserId)
    {
        HashMap<String, IMNotificationBean> map = mConversationDbAdapter.getImSingleNotification(this.getObserver()
                .getUserSysID(),
                currentFriendUserId);
        if (map != null && map.size() > 0)
        {
            int friendCount = map.size();
            IMNotificationBean bean = map.get(friendUserId);
            Intent intent = null;
            if (friendCount > 1)
            {
                // 如果是多人条消息，设置会话ID为-1，跳转到会话列表界面
                intent = new Intent(FusionAction.MainTabction.ACTION);
                intent.putExtra(IMNotificationEntity.FLAG_NOTIFICATION_TYPE,
                        IMNotificationEntity.NOTIFICATION_IM_SINGLE);
                //                intent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_ID,
                //                        "-1");
            }
            else if (friendCount == 1)
            {
                // 如果是单人条消息，传递好友会话ID，跳转到单人聊天界面
                intent = new Intent(FusionAction.SingleChatAction.ACTION);
                intent.putExtra(IMNotificationEntity.FLAG_NOTIFICATION_TYPE,
                        IMNotificationEntity.NOTIFICATION_IM_SINGLE);
                intent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_ID,
                        friendUserId);
            }
            if (null != notificationEntity)
            {
                // 先清除掉通知，目的是为了让通知可以在接受到新的消息的时候在通知栏顶部刷新
                cancelNotification();
            }
            SharedPreferences sp = getObserver().getContext()
                    .getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                            Context.MODE_PRIVATE);
            boolean isLogin = sp.getBoolean(Common.KEY_ISLOGIN, false);
            if (isLogin)
            {
                Logger.d(TAG,
                        "generateNotificationData***********   friendUserId:"
                                + friendUserId);
                if (null != bean)
                {
                    notificationEntity = new IMNotificationEntity(
                            bean.getNickName(), bean.getMsgContent(),
                            friendCount, bean.getUnreadMsgCount(),
                            bean.getMsgType(), bean.getMediaType(), intent,
                            getObserver().isSoundOpen());
                    NotificationEntityManager.getInstance()
                            .showNewNotification(notificationEntity);
                }
            }
        }
    }
    
    /**
     * 
     * 清除通知栏<BR>
     */
    private void cancelNotification()
    {
        if (null != notificationEntity)
        {
            NotificationEntityManager.getInstance()
                    .cancelNotification(notificationEntity);
        }
    }
    
    /**
     * 注册notification的广播监听<BR>
     * 主要为了实现，当进入到会话详情页面的时候清除通知栏
     */
    private void registIMNotificationReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(IMNotificationEntity.NOTIFICAITON_ACTION_IM_SINGLE);
        getObserver().getContext().registerReceiver(new BroadcastReceiver()
        {
            
            @Override
            public void onReceive(Context context, Intent intent)
            {
                mCurrentFriendUserId = intent.getStringExtra(IMNotificationEntity.NOTIFICATION_CURRENT_FRIENDUSERID);
                Logger.d(TAG, mCurrentFriendUserId);
                cancelNotification();
            }
        },
                filter);
    }
    
    /**
     * 
     * 收到状态报告的处理<BR>
     * 
     * @param data
     *            接收到的消息
     */
    private void handleMsgReported(String data)
    {
        Logger.i(TAG, "Receive a message report notification");
        
        // 获取状态报告通知类
        MessageNotification.MessageReportedNtf ntf = parseData(MessageNotification.MessageReportedNtf.class,
                data);
        // 发送消息返回通知类为空，则直接返回
        if (ntf == null)
        {
            Logger.w(TAG,
                    "handleMsgReported failed, MessageReportedNtf is null");
            return;
        }
        
        String msgSequence = ntf.getId();
        if (StringUtil.isNullOrEmpty(msgSequence))
        {
            Logger.w(TAG, "Receive report but msgsequence is null");
            return;
        }
        
        // 定义消息状态
        int iStatus = -1;
        
        // 获取消息报告
        String msgReport = ntf.getReport();
        
        // 从消息报告中获取消息状态
        if (IMRespReportType.RECEIVED.equalsIgnoreCase(msgReport))
        {
            // 从数据库中查询出该条信息的状态码，避免已读状态被覆盖
            iStatus = mMsgAdapter.getMessageStateBySequenceId(this.getObserver()
                    .getUserSysID(),
                    msgSequence);
            
            if (iStatus == MessageModel.MSGSTATUS_READED)
            {
                Logger.w(TAG, "Message " + msgSequence + " may be readed!");
                return;
            }
            iStatus = MessageModel.MSGSTATUS_SEND_UNREAD;
        }
        else if (IMRespReportType.READ.equalsIgnoreCase(msgReport)
                || IMRespReportType.RECVANDREAD.equalsIgnoreCase(msgReport))
        {
            iStatus = MessageModel.MSGSTATUS_READED;
        }
        
        // 如果获取状态失败，直接返回
        if (iStatus == -1)
        {
            Logger.w(TAG, "Invalid report status: " + msgReport);
            return;
        }
        else
        {
            
            // 更新数据库状态
            updateMessageStatus(ntf.getId(),
                    this.getObserver().getUserSysID(),
                    UriUtil.getHitalkIdFromJid(ntf.getFrom()),
                    iStatus);
        }
    }
    
    /**
     * 
     * 收到发送消息返回通知的处理<BR>
     * 
     * @param data
     *            接收到的消息
     */
    private void handleMessageNtfSend(String data)
    {
        Logger.i(TAG, "Receive a message sended notification");
        
        // 获取发送消息返回通知类
        MessageNotification.MessageSendNtf ntf = parseData(MessageNotification.MessageSendNtf.class,
                data);
        // 发送消息返回通知类为空，则直接返回
        if (ntf == null)
        {
            Logger.w(TAG, "handleMessageSend failed, MessageSendNtf is null");
            return;
        }
        
        // 设置消息状态
        int iMsgStatus;
        int errCode = ntf.getErrorCode();
        if (errCode == XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            iMsgStatus = MessageModel.MSGSTATUS_SENDED;
        }
        else
        {
            iMsgStatus = MessageModel.MSGSTATUS_SEND_FAIL;
        }
        
        // 如果数据库中状态为准备发送，则更新数据库消息状态
        int result = updateMessageStatus(ntf.getId(),
                this.getObserver().getUserSysID(),
                UriUtil.getHitalkIdFromJid(ntf.getFrom()),
                iMsgStatus);
        
        // 如果更新返回结果为0条，可能是因为该条数据还未更新msgSeqence字段，所以间隔时间再次更新
        if (result == 0)
        {
            
            Logger.d(TAG, "handleMessageNtfSend update result = 0, So after "
                    + UPDATE_BY_NTF_DELAY_TIME
                    + "Millis update again. msgSeqence=" + ntf.getId()
                    + " updateStatus=" + iMsgStatus);
            // 更新数据库
            String[] updateStatusByNtfParams = new String[] {
                    this.getObserver().getUserSysID(),
                    UriUtil.getHitalkIdFromJid(ntf.getFrom()),
                    String.valueOf(iMsgStatus) };
            mMsgQueue.sendMessageDelayed(mMsgQueue.obtainMessage(MessengerQueue.UPDATE_STATUS_BY_NTF,
                    ntf.getId()),
                    UPDATE_BY_NTF_DELAY_TIME);
            mPendingUpdateByNtfRequests.put(ntf.getId(),
                    updateStatusByNtfParams);
        }
        
    }
    
    /**
     * 
     * 收到群组消息的处理<BR>
     * 
     * @param data
     *            接收到的消息
     */
    private void handleGroupMsgReceived(String data)
    {
        Logger.i(TAG, "Receive a group message");
        GroupNotification.MessageReceivedNtfData ntf = parseData(GroupNotification.MessageReceivedNtfData.class,
                data);
        if (ntf == null || ntf.getMessage() == null || ntf.getErrorCode() != 0)
        {
            Logger.w(TAG,
                    "handleGroupMsgReceived failed, MessageReceivedNtfData is null");
            return;
        }
        
        // 非群公开消息处理
        if (GroupChatType.PRIVATE.equals(ntf.getMessage().getType()))
        {
            Logger.w(TAG, "Private group message is unable to handle so far.");
            return;
        }
        
        // 保存到数据库
        insertReceiveGroupMessage(ntf);
    }
    
    /**
     * 
     * 向数据库插入收到群消息发送状态
     * 
     * @param data
     *            接收到的消息
     */
    private void handleGroupMessageNtfSend(String data)
    {
        Logger.i(TAG, "Receive a group message sended notification");
        GroupNotification.MessageSendNtfData ntf = parseData(GroupNotification.MessageSendNtfData.class,
                data);
        
        if (ntf == null)
        {
            Logger.w(TAG,
                    "handleXmppGroupMessageNtfSend failed, handleXmppGroupMessageNtfSend is null");
        }
        
        // 获得消息序列
        String msgSequence = ntf.getId();
        if (StringUtil.isNullOrEmpty(msgSequence))
        {
            Logger.w(TAG,
                    "Receive group msg send notify but msgsequence is null");
            return;
        }
        
        // 获取返回消息状态
        int iMsgStatus;
        int errCode = ntf.getErrorCode();
        if (errCode == XmppResultCode.Base.FAST_ERR_SUCCESS)
        {
            iMsgStatus = MessageModel.MSGSTATUS_SENDED;
        }
        else
        {
            iMsgStatus = MessageModel.MSGSTATUS_SEND_FAIL;
        }
        
        // 更新数据库消息状态
        int result = mGroupMsgAdapter.updateByMsgSequence(this.getObserver()
                .getUserSysID(),
                UriUtil.getGroupJidFromJid(ntf.getFrom()),
                msgSequence,
                iMsgStatus);
        // 如果更新返回结果为0条，可能是因为该条数据还未更新msgSeqence字段，所以间隔时间再次更新
        
        if (result == 0)
        {
            Logger.d(TAG,
                    "handleGroupMessageNtfSend update result = 0, So after "
                            + UPDATE_BY_NTF_DELAY_TIME
                            + "Millis update again. msgSeqence=" + ntf.getId()
                            + " updateStatus=" + iMsgStatus);
            // 更新数据库
            String[] updateStatusByNtfParams = new String[] {
                    this.getObserver().getUserSysID(),
                    UriUtil.getHitalkIdFromJid(ntf.getFrom()),
                    String.valueOf(iMsgStatus) };
            
            mPendingUpdateByNtfRequests.put(ntf.getId(),
                    updateStatusByNtfParams);
            mMsgQueue.sendMessageDelayed(mMsgQueue.obtainMessage(MessengerQueue.UPDATE_GROUP_STATUS_BY_NTF,
                    ntf.getId()),
                    UPDATE_BY_NTF_DELAY_TIME);
        }
        
    }
    
    /**
     * 
     * 向数据库插入收到消息
     * 
     * @param ntf
     *            MessageReceivedNtf 接收到的消息
     * @param type
     *            String 发送方要求的报告的类型
     */
    private void insertReceiveMessage(MessageReceivedNtf ntf)
    {
        CommonMessageData msg = ntf.getMessage();
        
        MessageModel msgModel = new MessageModel();
        msgModel.setMsgId(MessageUtils.generateMsgId());
        msgModel.setFriendUserId(UriUtil.getHitalkIdFromJid(ntf.getFrom()));
        msgModel.setMsgSendOrRecv(MessageModel.MSGSENDORRECV_RECV);
        msgModel.setMsgSequence(ntf.getId());
        msgModel.setUserSysId(this.getObserver().getUserSysID());
        
        // 获取发送方要求的报告的类型
        if (null == ntf.getMessage().getRequest()
                || null == ntf.getMessage().getRequest().getType()
                || Request.TYPE_DELIVERY.equalsIgnoreCase(ntf.getMessage()
                        .getRequest()
                        .getType()))
        {
            // 若request为空或只需要发送送达报告的消息，不需要阅读报告
            msgModel.setMsgStatus(MessageModel.MSGSTATUS_UNREAD_NO_REPORT);
        }
        else
        {
            // 需要阅读报告
            msgModel.setMsgStatus(MessageModel.MSGSTATUS_UNREAD_NEED_REPORT);
        }
        
        // 文本内容
        String msgContent = msg.getBody();
        
        // 修改图文混排方式-开始
        if (msgContent != null && msgContent.trim().length() != 0)
        {
            msgContent = msgContent.trim();
        }
        // 修改图文混排方式-结束
        
        // 设置model中的消息内容
        msgModel.setMsgContent(msgContent);
        
        // 处理多媒体消息
        MediaIndexModel media = resolveMediaInfo(msg, msgModel.getMsgId());
        // 如果为多媒体文件则获取多媒体文件细节
        if (media != null)
        {
            media.setMsgId(msgModel.getMsgId());
            msgModel.setMediaIndex(media);
            msgModel.setMsgType(MessageModel.MSGTYPE_MEDIA);
        }
        else
        {
            msgModel.setMsgType(MessageModel.MSGTYPE_TEXT);
            // 如果消息为空时，处理为未知消息 added by pierce.
            if (msgModel.getMsgContent() == null
                    || msgContent.trim().length() == 0)
            {
                msgModel.setMsgContent(getObserver().getString(R.string.unknown_msg));
            }
            
        }
        
        // 处理时延时间
        MessageCommonClass.Delay delay = msg.getDelay();
        Date delayTime = null;
        if (delay != null && !StringUtil.isNullOrEmpty(delay.getStamp()))
        {
            try
            {
                delayTime = DateUtil.getDelayTime(delay.getStamp());
            }
            catch (Exception e)
            {
                Logger.e(TAG, "parse delay time error");
            }
        }
        if (delayTime != null)
        {
            msgModel.setMsgTime(DateUtil.TIMESTAMP_DF.format(delayTime));
        }
        else
        {
            msgModel.setMsgTime(DateUtil.TIMESTAMP_DF.format(new Date()));
        }
        
        // 判断是否收到的是小秘书类型
        if (ntf.getFrom().equalsIgnoreCase(FusionCode.XmppConfig.SECRETARY_JID))
        {
            mMsgAdapter.insert(ConversationModel.CONVERSATIONTYPE_SECRET,
                    msgModel);
        }
        else
        {
            // 信息插入数据库
            mMsgAdapter.insert(ConversationModel.CONVERSATIONTYPE_1V1, msgModel);
        }
        
    }
    
    /**
     * 根据返回通知更新消息状态
     * 
     * @param userSysId
     *            用户的系统id
     * @param friendUserId
     *            好友id
     * @param mSequence
     *            消息序列
     * @param status
     *            消息状态
     */
    private int updateMessageStatus(String mSequence, String userSysId,
            String friendUserId, int status)
    {
        
        if (StringUtil.isNullOrEmpty(mSequence))
        {
            Logger.w(TAG, "Receive msg send notify but msgsequence is null");
            return -1;
        }
        
        // 更新数据库消息状态
        return mMsgAdapter.updateByMsgSequence(userSysId,
                mSequence,
                friendUserId,
                status);
    }
    
    /**
     * 向数据库插入收到群消息
     * 
     * @param ntf
     *            接收到群消息
     */
    private void insertReceiveGroupMessage(MessageReceivedNtfData ntf)
    {
        CommonMessageData msg = ntf.getMessage();
        GroupMessageModel msgModel = new GroupMessageModel();
        msgModel.setMsgId(MessageUtils.generateMsgId());
        msgModel.setGroupId(UriUtil.getGroupJidFromJid(ntf.getFrom()));
        msgModel.setMemberUserId(UriUtil.getGroupMemberIdFromJid(ntf.getFrom()));
        msgModel.setMsgSendOrRecv(GroupMessageModel.MSGSENDORRECV_RECV);
        msgModel.setMsgSequence(ntf.getId());
        msgModel.setMsgStatus(GroupMessageModel.MSGSTATUS_UNREAD_NO_REPORT);
        msgModel.setUserSysId(this.getObserver().getUserSysID());
        String showName = ntf.getMessage().getFgnick();
        if (StringUtil.isNullOrEmpty(showName))
        {
            showName = msgModel.getMemberUserId();
        }
        msgModel.setMemberNick(showName);
        
        // 处理多媒体消息
        MediaIndexModel media = resolveMediaInfo(msg, msgModel.getMsgId());
        if (media != null)
        {
            media.setMsgId(msgModel.getMsgId());
            msgModel.setMediaIndex(media);
            msgModel.setMsgType(MessageModel.MSGTYPE_MEDIA);
        }
        else
        {
            msgModel.setMsgType(MessageModel.MSGTYPE_TEXT);
        }
        
        // 处理离线消息
        MessageCommonClass.Delay delay = ntf.getMessage().getDelay();
        Date delayTime = null;
        if (delay != null && !StringUtil.isNullOrEmpty(delay.getStamp()))
        {
            try
            {
                delayTime = DateUtil.getDelayTime(delay.getStamp());
            }
            catch (Exception e)
            {
                Logger.e(TAG, "parse delay time error");
            }
        }
        if (delayTime != null)
        {
            msgModel.setMsgTime(DateUtil.TIMESTAMP_DF.format(delayTime));
        }
        else
        {
            msgModel.setMsgTime(DateUtil.TIMESTAMP_DF.format(new Date()));
        }
        
        // 文本内容
        String msgContent = null;
        msgContent = msg.getBody();
        
        // 修改图文混排方式
        // TODO:
        
        // 设置信息内容
        msgModel.setMsgContent(msgContent);
        
        // 插入数据库
        mGroupMsgAdapter.insertGroupMsg(this.getObserver().getUserSysID(),
                ConversationModel.CONVERSATIONTYPE_GROUP,
                msgModel);
    }
    
    /**
     * 解析并下载媒体信息
     * 
     * @param message
     *            消息对象
     * @return 媒体信息
     */
    private MediaIndexModel resolveMediaInfo(CommonMessageData message,
            String msgId)
    {
        MediaIndexModel media = null;
        if (message == null)
        {
            Logger.w(TAG,
                    "resolveMediaInfo failed. Because CommonMessageData is null.");
            return null;
        }
        // 处理音频多媒体信息
        MessageCommonClass.Audio audio = message.getAudio();
        if (audio != null && !StringUtil.isNullOrEmpty(audio.getSrc()))
        {
            media = new MediaIndexModel();
            media.setMediaType(MediaIndexModel.MEDIATYPE_AUDIO);
            
            media.setMediaURL(audio.getSrc() + DownloadFileType.ORIGINAL);
            media.setMediaSize(audio.getSize());
            String strPlayTime = audio.getPlaytime();
            int playTime = 0;
            if (strPlayTime != null)
            {
                try
                {
                    playTime = Integer.parseInt(strPlayTime);
                }
                catch (NumberFormatException ex)
                {
                    Logger.e(TAG, "Invalid playtime: " + strPlayTime);
                }
            }
            media.setPlayTime(playTime);
            // 获取音频下载文件夹
            String savePath = UriUtil.getLocalStorageDir(UriUtil.getHitalkIdFromJid(message.getTo()),
                    FromType.RECEIVE,
                    UriUtil.LocalDirType.VOICE);
            // 如果文件夹不存在直接返回
            if (savePath == null)
            {
                Logger.e(TAG, "Download path is null");
                return null;
            }
            // 调用下载方法
            String mediaPath = downloadMedia(media.getMediaURL(),
                    savePath,
                    msgId,
                    MediaIndexColumns.MEDIA_PATH);
            if (null != mediaPath)
            {
                media.setMediaPath(mediaPath);
            }
            
        }
        
        // 处理图片多媒体消息
        MessageCommonClass.Image image = message.getImage();
        if (image != null && !StringUtil.isNullOrEmpty(image.getSrc()))
        {
            media = new MediaIndexModel();
            media.setMediaType(MediaIndexModel.MEDIATYPE_IMG);
            media.setMediaURL(image.getSrc() + DownloadFileType.ORIGINAL);
            media.setMediaSize(image.getSize());
            media.setMediaSmallURL(image.getSrc()
                    + DownloadFileType.SMALL_THUMBNAILS);
            
            // 获取图片存储路径
            String savePath = UriUtil.getLocalStorageDir(UriUtil.getHitalkIdFromJid(message.getTo()),
                    FromType.RECEIVE,
                    UriUtil.LocalDirType.THUMB_NAIL);
            // 如果文件夹不存在直接返回
            if (savePath == null)
            {
                Logger.e(TAG, "Download path is null");
                return null;
            }
            // 调用下载图片小缩略图
            String smallMediaPath = downloadMedia(media.getMediaSmallURL(),
                    savePath,
                    msgId,
                    MediaIndexColumns.MEDIA_SMALL_PATH);
            if (null != smallMediaPath)
            {
                media.setMediaSmallPath(smallMediaPath);
            }
            
        }
        
        // 处理视频多媒体消息
        MessageCommonClass.Video video = message.getVideo();
        if (video != null && !StringUtil.isNullOrEmpty(video.getSrc()))
        {
            media = new MediaIndexModel();
            media.setMediaType(MediaIndexModel.MEDIATYPE_VIDEO);
            media.setMediaURL(video.getSrc() + DownloadFileType.ORIGINAL);
            media.setMediaSize(video.getSize());
            String strPlayTime = video.getPlaytime();
            int playTime = 0;
            if (strPlayTime != null)
            {
                try
                {
                    playTime = Integer.parseInt(strPlayTime);
                }
                catch (NumberFormatException ex)
                {
                    Logger.e(TAG, "Invalid playtime: " + strPlayTime);
                }
            }
            media.setPlayTime(playTime);
            media.setMediaSmallURL(video.getThumbnail()
                    + DownloadFileType.SMALL_THUMBNAILS);
            
            // 获取下载路径
            String savePath = UriUtil.getLocalStorageDir(UriUtil.getHitalkIdFromJid(message.getTo()),
                    FromType.RECEIVE,
                    UriUtil.LocalDirType.THUMB_NAIL);
            // 如果文件夹不存在直接返回
            if (savePath == null)
            {
                Logger.e(TAG, "Download path is null");
                return null;
            }
            // 下载多媒体消息缩略图
            String smallMediaPath = downloadMedia(media.getMediaSmallURL(),
                    savePath,
                    msgId,
                    MediaIndexColumns.MEDIA_SMALL_PATH);
            if (null != smallMediaPath)
            {
                media.setMediaSmallPath(smallMediaPath);
            }
        }
        
        // 处理贴图信息
        MessageCommonClass.Emoji emoji = message.getEmoji();
        if (emoji != null && !StringUtil.isNullOrEmpty(emoji.getTtid()))
        {
            media = new MediaIndexModel();
            media.setMediaType(MediaIndexModel.MEDIATYPE_EMOJI);
            media.setMediaAlt(emoji.getAlt());
            media.setMediaPath(emoji.getTtid());
            media.setMediaSmallPath(emoji.getTtid());
        }
        //处理location
        MessageCommonClass.Location location = message.getLocation();
        if (location != null)
        {
            media = new MediaIndexModel();
            media.setMediaType(MediaIndexModel.MEDIATYPE_LOCATION);
            media.setMediaAlt(location.getDesc());
            media.setLocationLon(location.getLo());
            media.setLocationLat(location.getLa());
            media.setMediaURL(buildLocationDlUrl(location.getLo(),
                    location.getLa()));
        }
        return media;
    }
    
    /**
     * 展示小图，视频缩略图和音频文件的接口
     * 
     * @param msgId
     *            消息id
     */
    private String downloadMedia(String url, String downloadPath,
            final String msgId, final String columnsName)
    {
        // 如果url为空有则不进行下载
        if (null == url)
        {
            Logger.e(TAG, "url is null in method downloadMedia");
            return null;
        }
        
        // 下载源文件
        RcsDownloadHttpTask downloadTask = new RcsDownloadHttpTask();
        downloadTask.setStoreDir(downloadPath);
        downloadTask.setDownloadUrl(url);
        final ITaskStatusListener mTaskStatusListener = new ITaskStatusListener()
        {
            @Override
            public void onChangeStatus(ITask loadTask)
            {
                
                if (!(loadTask instanceof DownloadHttpTask))
                {
                    return;
                }
                switch (loadTask.getStatus())
                {
                    case ITask.TASK_STATUS_NEW:
                        Logger.d(TAG, "[DOWNLOAD]NEW TASK, NAME:" + msgId);
                        break;
                    
                    case ITask.TASK_STATUS_RUNNING:
                        Logger.d(TAG, "[DOWNLOAD]TASK IS RUNNING, NAME:"
                                + msgId);
                        break;
                    
                    case ITask.TASK_STATUS_PROCESS:
                        Logger.d(TAG, "[DOWNLOAD]RETURN TASK PROCESS, NAME:"
                                + msgId);
                        break;
                    
                    case ITask.TASK_STATUS_WARTING:
                        Logger.d(TAG, "[DOWNLOAD]TASK IS WAITING, NAME:"
                                + msgId);
                        break;
                    
                    case ITask.TASK_STATUS_FINISHED:
                        Logger.d(TAG, "[DOWNLOAD]TASK IS FINISHED, NAME:"
                                + msgId);
                        DownloadHttpTask dlHttpTask = (DownloadHttpTask) loadTask;
                        String filepath = dlHttpTask.getStorePath();
                        
                        // file路径更新到数据库中
                        ContentValues mediaPathCV = new ContentValues();
                        mediaPathCV.put(columnsName, filepath);
                        mMediaIndexDbAdapter.update(msgId, mediaPathCV);
                        break;
                    
                    case ITask.TASK_STATUS_DELETED:
                        Logger.d(TAG, "[DOWNLOAD]TASK IS DELETED, NAME:"
                                + msgId);
                        break;
                    
                    case ITask.TASK_STATUS_ERROR:
                        Logger.d(TAG, "[DOWNLOAD]TASK MET ERROR, NAME:" + msgId);
                        // 查询数据库中下载失败次数，在原来的失败次数上+1
                        int tryTime = mMediaIndexDbAdapter.queryByMsgId(msgId)
                                .getDownloadTryTimes() + 1;
                        
                        // 更新数据库下载次数
                        ContentValues tryTimesCV = new ContentValues();
                        tryTimesCV.put(MediaIndexColumns.DOWNLOAD_TRY_TIMES,
                                tryTime);
                        mMediaIndexDbAdapter.update(msgId, tryTimesCV);
                        
                        break;
                    
                    case ITask.TASK_STATUS_STOPPED:
                        Logger.d(TAG, "[DOWNLOAD]TASK IS STOPED, NAME:" + msgId);
                        break;
                    
                    default:
                        break;
                }
                
            }
            
        };
        downloadTask.addOwnerStatusListener(mTaskStatusListener);
        downloadTask.setBackground(true);
        try
        {
            mTaskManager.createTask(downloadTask);
            mTaskManager.startTask(downloadTask.getId());
        }
        catch (TaskException e)
        {
            Logger.d(TAG, "[DOWNLOAD]TASK exists, NAME:" + msgId);
            // 如下载任务已存在，不再进行下载
            if (TaskException.TASK_IS_EXIST == e.getCode())
            {
                // 获取任务
                downloadTask = (RcsDownloadHttpTask) mTaskManager.findTaskById(downloadTask.getId());
                // 如果下载任务已经完成，则更新数据库
                if (ITask.TASK_STATUS_FINISHED == downloadTask.getStatus())
                {
                    return downloadTask.getStorePath();
                }
                // 如果没有完成，则添加监听到任务中
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
        return null;
        
    }
    
    /**
     * 获取下载的URL<BR>
     * [功能详细描述]
     * @param longitude
     * @param latitude
     * @return
     */
    private String buildLocationDlUrl(String longitude, String latitude)
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
     * 暂时定义为消息处理队列 用来处理需要延迟处理的消息
     */
    private class MessengerQueue extends Handler
    {
        public static final int UPDATE_STATUS_BY_NTF = 101;
        
        public static final int UPDATE_GROUP_STATUS_BY_NTF = 102;
        
        public static final int SEND_DELIVER_REPORT = 202;
        
        private MessengerQueue(Looper looper)
        {
            super(looper);
        }
        
        @Override
        public void handleMessage(android.os.Message msg)
        {
            int type = msg.what;
            String msgSequence = (String) msg.obj;
            
            switch (type)
            {
                case SEND_DELIVER_REPORT:
                    sendDeliverReport(msgSequence);
                    break;
                case UPDATE_STATUS_BY_NTF:
                    updateStatusByNtf(msgSequence);
                    break;
                case UPDATE_GROUP_STATUS_BY_NTF:
                    updateGroupStatusByNtf(msgSequence);
                    break;
                default:
                    break;
            }
        }
        
        /**
         * 
         * 根据NTF状态更改1v1消息状态<BR>
         * 
         * @param msgSequence
         *            String 消息序列
         */
        private void updateStatusByNtf(String msgSequence)
        {
            String[] updateStatusByNtfParams = mPendingUpdateByNtfRequests.remove(msgSequence);
            if (null == updateStatusByNtfParams
                    || updateStatusByNtfParams.length < 3)
            {
                Logger.e(TAG,
                        "updateStatusByNtfParams is null or length <3 in updateStatusByNtf");
                return;
            }
            
            // 从数据库中查询出该条信息的状态码，只更新消息状态为正在发送状态的消息
            int iStatus = mMsgAdapter.getMessageStateBySequenceId(updateStatusByNtfParams[0],
                    msgSequence);
            if (iStatus == MessageModel.MSGSTATUS_PREPARE_SEND)
            {
                updateMessageStatus(msgSequence,
                        updateStatusByNtfParams[0],
                        updateStatusByNtfParams[1],
                        Integer.parseInt(updateStatusByNtfParams[2]));
                Logger.d(TAG,
                        "update the 1v1Message status to MSGSTATUS_SENDED after "
                                + UPDATE_BY_NTF_DELAY_TIME
                                + "Millis msgSequence=" + msgSequence);
            }
            
        }
        
        /**
         * 
         * 根据NTF状态更改1vN消息状态<BR>
         * 
         * @param msgSequence
         *            String 消息序列
         */
        private void updateGroupStatusByNtf(String msgSequence)
        {
            String[] updateStatusByNtfParams = mPendingUpdateByNtfRequests.remove(msgSequence);
            
            if (null == updateStatusByNtfParams
                    || updateStatusByNtfParams.length < 3)
            {
                Logger.e(TAG,
                        "updateStatusByNtfParams is null or length < 3 in updateStatusByNtf");
                return;
            }
            
            // 更新数据库消息状态
            mGroupMsgAdapter.updateByMsgSequence(updateStatusByNtfParams[0],
                    updateStatusByNtfParams[1],
                    msgSequence,
                    Integer.parseInt(updateStatusByNtfParams[2]));
            
            Logger.i(TAG, "update the groupMessage 1vNMessage status to "
                    + updateStatusByNtfParams[2] + " after "
                    + UPDATE_BY_NTF_DELAY_TIME + "Millis msgSequence="
                    + msgSequence);
        }
        
        /**
         * 
         * 发送送达报告<BR>
         * 
         * @param msgSequence
         *            String 消息序列
         */
        private void sendDeliverReport(String msgSequence)
        {
            String[] deliveryReportParams = mPendingDeliveryRptRequests.remove(msgSequence);
            if (deliveryReportParams == null || deliveryReportParams.length < 2)
            {
                return;
            }
            
            // 查询数据库，看看是否有消息为已读状态
            int iStatus = mMsgAdapter.getMessageStateBySequenceId(deliveryReportParams[0],
                    msgSequence);
            // 如果为已读则 不发送送达报告
            if (iStatus < 0
                    || iStatus == MessageModel.MSGSTATUS_READED_NO_REPORT
                    || iStatus == MessageModel.MSGSTATUS_READED_NEED_REPORT)
            {
                Logger.i(TAG,
                        "ReadedReport has been sent or query status < 0. So do not send the deliveredReport. msgSequence="
                                + msgSequence);
            }
            else
            {
                Logger.i(TAG,
                        "FAST_MESSAGE_CMD_REPORT has been invoked. msgSequence= "
                                + msgSequence
                                + " and iRet="
                                + sendReport(msgSequence,
                                        deliveryReportParams[1],
                                        deliveryReportParams[2]));
            }
        }
    }
    
    /**
     * 发送报告给服务器<BR>
     * 发送送达报告，或月度报告给xmpp服务器
     * @param msgSequence String 消息id
     * @param toJid String 接收方
     * @param reportType String 发送报告类型
     * @return String 返回结果
     */
    
    private String sendReport(String msgSequence, String toJid,
            String reportType)
    {
        MessageReportCmdData report = new MessageReportCmdData();
        report.setId(msgSequence);
        report.setTo(toJid);
        report.setReport(reportType);
        String retData = getEngineBridge().executeCommand(BaseParams.MessageParams.FAST_COM_MESSAGE_ID,
                BaseParams.MessageParams.FAST_MESSAGE_CMD_REPORT,
                report.makeCmdData());
        return StringUtil.getXmlValue(retData, "ret");
    }
    
    /**
     * IM发送方要求的报告的类型
     * 
     * @return
     */
    public interface IMRespReportType
    {
        
        /**
         * 已收到
         */
        String RECEIVED = "received";
        
        /**
         * 已读
         */
        String READ = "read";
        
        /**
         * 已收到且已读
         */
        String RECVANDREAD = "recvandread";
    }
    
    /**
     * 群组消息类型
     */
    
    public interface GroupChatType
    {
        
        /**
         * 群内私信
         */
        String PRIVATE = "im-gp-private";
        
        /**
         * 群组公开消息
         */
        String PUBLIC = "im-gp-public";
        
    }
    
    /**
     * 
     * IM消息类型<BR>
     * 
     * @author liying 00124251
     * @version [RCS Client V100R001C03, Apr 17, 2012]
     */
    public interface IMChatType
    {
        
        /**
         * IM消息
         */
        String CHAT = "chat";
        
        /**
         * 短信
         */
        String SM = "sm";
        
        /**
         * 短信转IM
         */
        String IM_SM = "im-sm";
        
        /**
         * 第一次登录请求服务器下发业务使用指导时使用
         */
        String USAGE = "im-usage";
        
        /**
         * 系统公告
         */
        String SYS = "im-sys";
        
        /**
         * 位置分享
         */
        String LOC_SHARE = "im-locshare";
    }
    
    /**
     * 下载多媒体文件类型
     * 提交到ND得到的下载文件的URL,需要在最后端补充‘&type=x’才可以获取具体的文件。这里统一在接收方进行添加type的操作
     * type=2表示获取文件内容，type=0表示获取小缩略图，type=1表示获取大缩略图
     */
    
    public interface DownloadFileType
    {
        
        /**
         * 小缩略图
         */
        String SMALL_THUMBNAILS = "&type=0";
        
        /**
         * 大缩略图
         */
        String BIG_THUMBNAILS = "&type=1";
        
        /**
         * 原文件
         */
        String ORIGINAL = "&type=2";
    }
    
}
