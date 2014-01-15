/*
 * 文件名: MultiChatActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ChatbarMemberAction;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionAction.GroupDetailAction;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.GroupMessageColumns;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.logic.model.GroupMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;

/**
 * 多人聊天<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-9]
 */
public class MultiChatActivity extends BaseChatActivity
{
    /**
     * TAG
     */
    private static final String TAG = "MultiChatActivity";
    
    /**
     * 菜单相关
     */
    private static final int MENU_CLEAR_MESSAGE = 0;
    
    /**
     * 退出菜单标志
     */
    private static final int MENU_EXIT_CHAT = 1;
    
    /**
     * 当前群组Id
     */
    private String mGroupID;
    
    /**
     * 群组类型
     */
    private int mGroupType;
    
    /**
     * 群组成员的头像Map
     */
    private HashMap<String, Drawable> mMemberFaceMap;
    
    /**
     * 群成员的昵称
     */
    private HashMap<String, String> mMemberNickMap;
    
    /**
     * 群成员信息
     */
    private GroupInfoModel mGroupInfoModel;
    
    /**
     * 发送消息<BR>
     * @param textCnt
     *            文本内容
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#send(java.lang.String)
     */
    @Override
    public void send(String textCnt)
    {
        getImLogic().send1VNMessage(mGroupID, textCnt);
        
    }
    
    /**
     * 发送媒体消息<BR>
     * @param textCnt 文本消息
     * @param media MediaIndexModel
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#sendMediaMsg(java.lang.String, com.huawei.basic.android.im.logic.model.MediaIndexModel)
     */
    @Override
    public void sendMediaMsg(String textCnt, MediaIndexModel media)
    {
        getImLogic().send1VNMessage(mGroupID, textCnt, media);
        
    }
    
    /**
     * 用户头像点击事件 <BR>
     * @param msg BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#onUserPhotoClick(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    @Override
    public void onUserPhotoClick(BaseMessageModel msg)
    {
        if (msg.getMsgSendOrRecv() == BaseMessageModel.MSGSENDORRECV_SEND)
        {
            Intent intent = new Intent(
                    SettingsAction.ACTION_ACTIVITY_PRIVATE_PROFILE_SETTING);
            startActivity(intent);
        }
        else
        {
            GroupMessageModel groupMsg = (GroupMessageModel) msg;
            Intent toDetail = new Intent(
                    FusionAction.ContactDetailAction.ACTION);
            toDetail.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                    groupMsg.getMemberUserId());
            startActivity(toDetail);
        }
    }
    
    /**
     * 创建可选菜单<BR>
     * 
     * @param menu
     *            menu
     * @return super.onCreateOptionsMenu(menu)
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(Menu.NONE,
                MENU_CLEAR_MESSAGE,
                Menu.NONE,
                getResources().getString(R.string.clear_message_record))
                .setIcon(getResources().getDrawable(R.drawable.menu_exit_icon));
        if (!getGroupLogic().isOwner(mGroupID))
        {
            menu.add(Menu.NONE,
                    MENU_EXIT_CHAT,
                    Menu.NONE,
                    getResources().getString(R.string.exit_chat))
                    .setIcon(getResources().getDrawable(R.drawable.menu_exit_icon));
        }
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#onRightButtonClick()
     */
    
    @Override
    public void onRightButtonClick()
    {
        if (mGroupType == GroupInfoModel.GROUPTYPE_NVN)
        {
            // 聊吧
            Intent intent = new Intent(
                    ChatbarMemberAction.ACTION_CHATBAR_MEMBER);
            intent.putExtra(ChatbarMemberAction.EXTRA_GROUP_ID, mGroupID);
            startActivity(intent);
        }
        else
        {
            // 群详情
            Intent intent = new Intent(GroupDetailAction.ACTION_GROUP_DETAIL);
            intent.putExtra(ChatbarMemberAction.EXTRA_GROUP_ID, mGroupID);
            startActivity(intent);
        }
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity.BaseMsgAdapter#setMsgAsReaded(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    
    @Override
    public void setMsgAsReaded(BaseMessageModel msg)
    {
        // 如果消息未读，将消息设置为已读
        if (msg.getMsgStatus() == BaseMessageModel.MSGSTATUS_UNREAD_NO_REPORT)
        {
            // 把已阅读的消息状态置为已读
            getImLogic().set1VNMsgAsReaded((GroupMessageModel) msg);
        }
    }
    
    /**
     * 可选菜单选项事件<BR>
     * 
     * @param item item
     * @return true true
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        if (item.getItemId() == MENU_CLEAR_MESSAGE)
        {
            showConfirmDialog(getResources().getString(R.string.dialog_clear_group_message),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getImLogic().clear1VNMessage(mGroupID);
                        }
                    });
        }
        
        else if (item.getItemId() == MENU_EXIT_CHAT)
        {
            showConfirmDialog(getResources().getString(R.string.dialog_exit_group),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getGroupLogic().quitGroup(mGroupID);
                            finish();
                        }
                    });
        }
        
        return true;
    }
    
    
    /**
     * 删除单条消息<BR>
     * @param msgID 消息ID
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#deleteMsg(java.lang.String)
     */
    @Override
    protected void deleteMsg(String msgID)
    {
        getImLogic().delete1VNMessage(msgID);
    }
    
    /**
     * 发送多人消息<BR>
     * @param msgID msgID
     * @param friendUserIds 好友userID
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#transferMsg(java.lang.String, java.lang.String[])
     */
    @Override
    protected void transferMsg(String msgID, String[] friendUserIds)
    {
        getImLogic().transfer1VNMessage(msgID, friendUserIds);
    }
    
    /**
     * 重发事件监听<BR>
     * 
     * @param msg
     *            BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#resendMsg(java.lang.String)
     */
    @Override
    protected void resendMsg(BaseMessageModel msg)
    {
        getImLogic().resend1VNMessage((GroupMessageModel) msg);
    }
    
    /**
     * 初始化数据<BR>
     * TODO：封装Cursor，写一个Cursor的子类，在子类中提供
     * getMessageModel()的方法，返回基类BaseMessageModel供BaseMsgItem使用。
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#initData()
     */
    @Override
    protected void initData()
    {
        mGroupID = getIntent().getStringExtra(FusionAction.MultiChatAction.EXTRA_GROUP_ID);
        Logger.d(TAG, "group id is : " + mGroupID);
        // 将所有消息设置为已读
        getImLogic().setAll1VNMsgAsReaded(mGroupID);
        
        mGroupInfoModel = getGroupLogic().getGroupInfoModelFromDB(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                mGroupID);
        
        //判断是否在当前群
        if (mGroupInfoModel != null
                && mGroupInfoModel.getAffiliation()
                        .equals(GroupMemberModel.AFFILIATION_NONE))
        {
            setButtonUnAvailable();
        }
        if (mGroupInfoModel != null)
        {
            mGroupType = mGroupInfoModel.getGroupType();
        }
        
        String groupTitle;
        if (mGroupType == GroupInfoModel.GROUPTYPE_NVN)
        {
            //聊吧
            //            setRightButton(getResources().getString(R.string.default_add));
            
            Logger.d(TAG, "group member count is : "
                    + getGroupLogic().getMemberListCount(mGroupID));
            groupTitle = mGroupInfoModel.getGroupName();
        }
        else
        {
            //群
            //            setRightButton(getResources().getString(R.string.group_detail));
            groupTitle = getIntent().getStringExtra(FusionAction.MultiChatAction.EXTRA_GROUP_NAME);
            if (null == groupTitle)
            {
                groupTitle = mGroupInfoModel.getGroupName();
            }
        }
        setTitle(groupTitle);
        setTitleCount("(" + mGroupInfoModel.getMemberCount() + ")");
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#getUnreadAudioMsgIds()
     */
    
    @Override
    protected List<String> getUnreadAudioMsgIds()
    {
        return getImLogic().get1VNUnreadAudioMsgIds(mGroupID);
    }
    
    /**
     * 
     * 构建群组聊天的Adapter<BR> 
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#buildMsgAdapter()
     */
    @Override
    protected BaseMsgAdapter buildMsgAdapter(int curMsgCnt)
    {
        BaseMsgCursorWrapper cursor = getImLogic().get1VNMsgList(mGroupID);
        cursor.setCount(curMsgCnt);
        return new MultiMsgAdapter(this, cursor, true);
    }
    
    /**
     * 注册群组聊天的数据库监听<BR> 
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#registerObserver()
     */
    @Override
    protected void registerObserver()
    {
        getImLogic().register1VNDataObserver(mGroupID);
    }
    
    /**
     * 注销群组聊天的数据库监听<BR> 
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#unregisterObserver()
     */
    @Override
    protected void unregisterObserver()
    {
        getImLogic().unregister1VNDataObserver(mGroupID);
    }
    
    /**
     * 
     * 清空会话表中未读消息树<BR>
     * BUG 310的临时解决方案
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        getImLogic().setAll1VNMsgAsReaded(mGroupID);
    }
    
    /**
     * 处理消息<BR> 
     * {@inheritDoc}
     * @param msg Message
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        super.handleStateMessage(msg);
        int what = msg.what;
        switch (what)
        {
            
            // 群组成员信息发生变更，刷新页面
            case FusionMessageType.ChatMessageType.MSGTYPE_MEMBER_INFO_REFRESH:
                if (mMemberFaceMap != null)
                {
                    mMemberFaceMap.clear();
                    mMemberFaceMap = null;
                }
                if (mMemberNickMap != null)
                {
                    mMemberNickMap.clear();
                    mMemberNickMap = null;
                }
                
                mGroupInfoModel = getGroupLogic().getGroupInfoModelFromDB(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        mGroupID);
                if (null != mGroupInfoModel)
                {
                    setTitleCount("(" + mGroupInfoModel.getMemberCount() + ")");
                }
                
                getMsgAdapter().refreshMsg();
                break;
            //成员被踢出，关闭聊天界面
            case GroupMessageType.MEMBER_KICKED_FROM_GROUP:
                if (((String) msg.obj).equals(mGroupID))
                {
                    if (isPaused())
                    {
                        finish();
                        
                    }
                    else
                    {
                        showOnlyConfirmDialog(R.string.group_kicked,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        setButtonUnAvailable();
                                    }
                                });
                    }
                }
                break;
            //退出群成功
            case GroupMessageType.MEMBER_REMOVED_FROM_GROUP:

                //如果是当前用户，说明当前用户退出群
                if (((String) msg.obj).equals(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID()))
                {
                    setButtonUnAvailable();
                }
                break;
            
            //群主解散群
            case GroupMessageType.GROUP_DESTROYED_SUCCESS:
                if (((String) msg.obj).equals(mGroupID))
                {
                    if (isPaused())
                    {
                        finish();
                    }
                    else
                    {
                        if (mGroupType == GroupInfoModel.GROUPTYPE_NVN)
                        {
                            showOnlyConfirmDialog(R.string.chatbar_destroyed_success,
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which)
                                        {
                                            setButtonUnAvailable();
                                        }
                                    });
                        }
                        else
                        {
                            showOnlyConfirmDialog(R.string.group_destroyed_success,
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which)
                                        {
                                            setButtonUnAvailable();
                                        }
                                    });
                        }
                    }
                }
                break;
            //解散群成功
            case GroupMessageType.GROUP_DESTROY_SUCCESS:
                if (((String) msg.obj).equals(mGroupID))
                {
                    setButtonUnAvailable();
                }
                break;
            //群组信息改变时
            case FusionMessageType.ChatMessageType.MSGTYPE_GROUP_INFO_REFRESH:

                GroupInfoModel groupInfo = getGroupLogic().getGroupInfoModelFromDB(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        mGroupID);
                
                if (null != groupInfo)
                {
                    setTitle(groupInfo.getGroupName());
                    setTitleCount("(" + mGroupInfoModel.getMemberCount() + ")");
                }
                
                break;
            
            default:
                break;
        }
        
    }
    
    /**
     * <BR>
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-15]
     */
    private class MultiMsgAdapter extends BaseMsgAdapter
    {
        /**
         * 构造简方法
         * 
         * @param context Context
         * @param c  Cursor
         * @param autoRequery boolean
         */
        public MultiMsgAdapter(Context context, BaseMsgCursorWrapper c,
                boolean autoRequery)
        {
            super(context, c, autoRequery);
        }
        
        /**
         * <BR> 
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.adapter.BaseMsgAdapter#getTypeSendOrReceive(android.database.Cursor)
         */
        
        @Override
        protected int getTypeSendOrReceive(Cursor cursor)
        {
            int sendOrReceive = cursor.getInt(cursor.getColumnIndex(GroupMessageColumns.MSG_SENDORRECV));
            return sendOrReceive;
        }
        
        /**
         * <BR> 
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.adapter.BaseMsgAdapter#getMsgType(android.database.Cursor)
         */
        
        @Override
        protected int getMsgType(Cursor cursor)
        {
            int msgType = cursor.getInt(cursor.getColumnIndex(GroupMessageColumns.MSG_TYPE));
            return msgType;
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.BaseChatActivity.BaseMsgAdapter#getFace(com.huawei.basic.android.im.logic.model.BaseMessageModel)
         */
        
        @Override
        protected Drawable getFace(BaseMessageModel msg)
        {
            Drawable memberFace = null;
            GroupMessageModel groupMsg = (GroupMessageModel) msg;
            if (mMemberFaceMap == null)
            {
                mMemberFaceMap = new HashMap<String, Drawable>();
            }
            String memberUserId = groupMsg.getMemberUserId();
            if (mMemberFaceMap.get(memberUserId) == null)
            {
                memberFace = getImLogic().getFace(memberUserId);
                if (memberFace != null)
                {
                    mMemberFaceMap.put(memberUserId, memberFace);
                }
            }
            else
            {
                memberFace = mMemberFaceMap.get(memberUserId);
            }
            return memberFace;
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.BaseChatActivity.BaseMsgAdapter#getDisplayName(com.huawei.basic.android.im.logic.model.BaseMessageModel)
         */
        
        @Override
        protected String getDisplayName(BaseMessageModel msg)
        {
            String displayName = null;
            GroupMessageModel groupMsg = (GroupMessageModel) msg;
            // 昵称操作
            if (mMemberNickMap == null)
            {
                mMemberNickMap = new HashMap<String, String>();
            }
            String memberUserID = groupMsg.getMemberUserId();
            if (mMemberNickMap.get(memberUserID) == null)
            {
                displayName = getImLogic().getGroupMemberNickName(memberUserID,
                        groupMsg.getGroupId());
                if (displayName != null)
                {
                    mMemberNickMap.put(memberUserID, displayName);
                }
            }
            else
            {
                displayName = mMemberNickMap.get(memberUserID);
            }
            return displayName;
        }
        
    }
}
