/*
 * 文件名: ConversationLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 会话界面逻辑操作
 * 创建人: 周雪松
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.im;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.ConversationMessageType;
import com.huawei.basic.android.im.component.database.URIField;
import com.huawei.basic.android.im.component.service.app.IServiceSender;
import com.huawei.basic.android.im.framework.logic.BaseLogic;
import com.huawei.basic.android.im.logic.adapter.db.ContactInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.ConversationDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.FriendManagerDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.GroupInfoDbAdapter;
import com.huawei.basic.android.im.logic.adapter.db.MediaIndexDbAdapter;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * 
 * 会话模块的逻辑实现
 * @author xuesongzhou
 * @version [RCS Client V100R001C03, Feb 23, 2012]
 */
public class ConversationLogic extends BaseLogic implements IConversationLogic
{
    /**
     * 标签:用于打印日志
     */
    public static final String TAG = "ConversationLogic";
    
    /**
     * 上下文内容对象
     */
    private Context mContext;
    
    /**
     * 消息会话数据操作适配器
     */
    private ConversationDbAdapter mConversationDbAdapter;
    
    /**
     * 个人/好友信息表数据库操作适配器<BR>
     */
    private ContactInfoDbAdapter mContactInfoDbAdapter;
    
    private MediaIndexDbAdapter mMediaIndexDbAdapter;
    
    private FriendManagerDbAdapter mFriendManagerAdapter;
    /**会话操作逻辑构造方法
     * @param context 内容对象
     * @param serviceSender  与service交互的对象
     */
    public ConversationLogic(Context context, IServiceSender serviceSender)
    {
        this.mContext = context;
        this.mConversationDbAdapter = ConversationDbAdapter.getInstance(context);
        this.mContactInfoDbAdapter = ContactInfoDbAdapter.getInstance(context);
        this.mMediaIndexDbAdapter = MediaIndexDbAdapter.getInstance(context);
        this.mFriendManagerAdapter = FriendManagerDbAdapter.getInstance(context);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Uri[] getObserverUris()
    {
        return new Uri[] { URIField.CONVERSATION_URI,
                URIField.CONVERSATION_FRIEND_URI };
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onChangeByUri(boolean selfChange, Uri uri)
    {
        if (uri == URIField.CONVERSATION_URI
                || uri == URIField.CONVERSATION_FRIEND_URI)
        {
            sendEmptyMessage(ConversationMessageType.CONVERSATION_DB_CHANGED);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConversationModel> loadAllConversations()
    {
        //UserSysId从用户登录以后的全局对象中获取
        List<ConversationModel> list = mConversationDbAdapter.queryAllConversation(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConversationModel> loadConversationsByLimit(int limit)
    {
        List<ConversationModel> list = mConversationDbAdapter.queryAllConversationForPage(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                limit);
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getUnReadCount()
    {
        return mConversationDbAdapter.queryAllUnreadMsgNum(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
    }
    
    /**
     * {@inheritDoc}
     */
    public int delete(ConversationModel conversationModel)
    {
        Log.d("ConversationListLogic", "delete()");
        int result = -1;
        //TODO 根据类别，执行不同的删除【程序中有具体的类别选择】
        if (null == conversationModel)
        {
            Log.d("ConversationListLogic",
                    "delete()--->删除失败，传入的conversationModel为null");
            return -1;
        }
        //TODO:会话类别为初始话登录的时候没在这边处理
        //会话类别为1VN，群发消息
        if (ConversationModel.CONVERSATIONTYPE_1VN == conversationModel.getConversationType())
        {
            result = mConversationDbAdapter.deleteByMsgId(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(),
                    conversationModel.getLastMsgId());
        }
        //用户删除向导信息
        else if (ConversationModel.CONVERSATIONTYPE_INIT_TIPS == conversationModel.getConversationType())
        {
            result = mConversationDbAdapter.deleteByMsgId(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(),
                    conversationModel.getLastMsgId());
        }
        else if (ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER == conversationModel.getConversationType())
        {
            //删除插件的会话记录，需清空插件的记录内容
            mFriendManagerAdapter.deleteByFriendSysId(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId());
            result = mConversationDbAdapter.deleteByConversationId(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(),
                    conversationModel.getConversationId(),
                    conversationModel.getConversationType());
        }
        else
        {
            result = mConversationDbAdapter.deleteByConversationId(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserSysId(),
                    conversationModel.getConversationId(),
                    conversationModel.getConversationType());
            
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GroupInfoModel createConversationByFriendUserIds(
            final String[] friendUserIds)
    {
        String curUserSysId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        //查询ContactInfo表中当前用户的所有信息
        final ContactInfoModel currentUserInfo = mContactInfoDbAdapter.queryByFriendSysIdWithPrivate(curUserSysId,
                curUserSysId);
        //字符串，存放聊天人昵称
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtil.trim(currentUserInfo.getNickName(), 7));
        final List<String[]> chatMemberLists = new ArrayList<String[]>();
        for (int i = 0; i < friendUserIds.length; i++)
        {
            ContactInfoModel friendUserInfo = mContactInfoDbAdapter.queryByFriendUserIdNoUnion(curUserSysId,
                    friendUserIds[i]);
            builder.append(",")
                    .append(StringUtil.trim(friendUserInfo.getNickName(), 7));
            chatMemberLists.add(new String[] {
                    UriUtil.buildXmppJidNoWo(friendUserInfo.getFriendUserId()),
                    friendUserInfo.getNickName() });
        }
        //如果群组长度>20截取字符串，该字符串在界面显示
        String groupName = StringUtil.subString(builder.toString(), 20);
        // 创建一个GroupInfoModel并设置对应的参数，以供调用命令时使用
        final GroupInfoModel groupInfoModel = new GroupInfoModel();
        
        //群组名称
        groupInfoModel.setGroupName(groupName);
        //群组类别:会话
        groupInfoModel.setGroupType(GroupInfoModel.GROUPTYPE_NVN);
        //设定群组的分类索引,这边设定为不分类
        groupInfoModel.setGroupSort(0);
        
        return groupInfoModel;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeString(int status)
    {
        String msgStatus = "";
        switch (status)
        {
        //取值:1 待发送[界面:正在发送]
            case BaseMessageModel.MSGSTATUS_PREPARE_SEND:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_sending);
                break;
            //取值:2 已发送[界面:已发送]
            case BaseMessageModel.MSGSTATUS_SENDED:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_sent);
                break;
            //取值:3 已送达未读[界面:已送达]
            case BaseMessageModel.MSGSTATUS_SEND_UNREAD:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_sent_unread);
                break;
            //取值:4  已读 [界面:已读]
            case BaseMessageModel.MSGSTATUS_READED:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_sent_read);
                break;
            //取值:10 未读,需发送阅读报告[界面:已接收未读]
            case BaseMessageModel.MSGSTATUS_UNREAD_NEED_REPORT:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_received_unread);
                //msgStatus = "";
                break;
            //取值:11 未读,无需发送阅读报告[界面:已接收未读]
            case BaseMessageModel.MSGSTATUS_UNREAD_NO_REPORT:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_received_unread);
                //msgStatus = "";
                break;
            //取值:12 已读，需发送阅读报告[界面:已读]
            case BaseMessageModel.MSGSTATUS_READED_NEED_REPORT:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_received_read);
                //msgStatus = "";
                break;
            //取值:13 已读，无需发送阅读报告[界面:已读]
            case BaseMessageModel.MSGSTATUS_READED_NO_REPORT:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_received_read);
                //msgStatus = "";
                break;
            //取值:100 阻塞状态(多媒体消息正在上传附件，不处理)[界面:阻塞状态]
            case BaseMessageModel.MSGSTATUS_BLOCK:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_media_block);
                //msgStatus = "";
                break;
            //取值:101 发送失败[界面:发送失败]
            case BaseMessageModel.MSGSTATUS_SEND_FAIL:
                msgStatus = mContext.getResources()
                        .getString(R.string.message_send_failed);
                break;
            default:
                msgStatus = "";
                break;
        }
        return msgStatus;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageTypeString(String msgId)
    {
        MediaIndexModel mediaIndexModel = mMediaIndexDbAdapter.queryByMsgId(msgId);
        int mediaType;
        String typeString = "";
        if (null == mediaIndexModel)
        {
            //TODO:没有查到，这边要处理下
            return null;
        }
        mediaType = mediaIndexModel.getMediaType();
        switch (mediaType)
        {
            case MediaIndexModel.MEDIATYPE_EMOJI:
                typeString = mContext.getResources()
                        .getString(R.string.mediatype_emoji);
                break;
            case MediaIndexModel.MEDIATYPE_IMG:
                typeString = mContext.getResources()
                        .getString(R.string.mediatype_img);
                break;
            case MediaIndexModel.MEDIATYPE_AUDIO:
                typeString = mContext.getResources()
                        .getString(R.string.mediatype_audio);
                break;
            case MediaIndexModel.MEDIATYPE_VIDEO:
                typeString = mContext.getResources()
                        .getString(R.string.mediatype_video);
                break;
            case MediaIndexModel.MEDIATYPE_LOCATION:
                typeString = mContext.getResources()
                        .getString(R.string.mediatype_location);
                break;
            default:
                break;
        }
        return typeString;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getContactInfoByJID(String jid, int type)
    {
        switch (type)
        {
            case ConversationModel.CONVERSATIONTYPE_1V1:
                return ContactInfoDbAdapter.getInstance(mContext)
                        .queryByFriendUserIdWithPrivate(FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId(),
                                jid);
            case ConversationModel.CONVERSATIONTYPE_1VN:
                break;
            case ConversationModel.CONVERSATIONTYPE_GROUP:
                return GroupInfoDbAdapter.getInstance(mContext)
                        .queryByGroupJid(FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId(),
                                jid);
            default:
                break;
        
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConversationModel> sort(List<ConversationModel> list)
    {
        //执行到这边表示不需要排序，没有或只有1个元素
        if (null == list || list.size() <= 1)
        {
            return list;
        }
        //初始话的要放在前面
        List<ConversationModel> highPriorityList = new ArrayList<ConversationModel>();
        ConversationModel conversationModel;
        
        for (int i = list.size() - 1; i >= 0; i--)
        {
            conversationModel = list.get(i);
            
            if (null != conversationModel
                    && conversationModel.getUnReadmsg() > 0)
            {
                //小秘书置顶
                if (ConversationModel.CONVERSATIONTYPE_SECRET == conversationModel.getConversationType())
                {
                    highPriorityList.add(0, conversationModel);
                    list.remove(i);
                    
                }
                //找朋友小助手第二
                else if (ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER == conversationModel.getConversationType())
                {
                    highPriorityList.add(conversationModel);
                    list.remove(i);
                    
                }
            }
        }
        
        if (highPriorityList.size() > 0)
        {
            list.addAll(0, highPriorityList);
        }
        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAllConversation()
    {
        mConversationDbAdapter.clearAllData(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
    }
    
}
