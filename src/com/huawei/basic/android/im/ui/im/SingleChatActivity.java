/*

 * 文件名: SingleChatActivity.java
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
import com.huawei.basic.android.im.common.FusionAction.ChooseMemberAction;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionAction.GroupSearchAction;
import com.huawei.basic.android.im.common.FusionAction.PluginDetailAction;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionAction.SingleChatAction;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendHelperMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MessageColumns;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.logic.notification.IMNotificationEntity;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 1v1聊天<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public class SingleChatActivity extends BaseChatActivity
{
    /**
     * DEBUG_TAG
     */
    private static final String TAG = "SingleChatActivity";
    
    /**
     * menu
     */
    private static final int MENU_CLEAR_MESSAGE = 1;
    
    /**
     * 页面传递参数：好友ID
     */
    private String mFriendUserId;
    
    /**
     * 好友头像
     */
    private Drawable mFriendFace;
    
    /**
     * 好友昵称
     */
    private String mFriendNickName;
    
    /**
     * 联系人信息
     */
    private ContactInfoModel mContactInfoModel;
    
    /**
     * 发送文本消息<BR>
     * @param textCnt   消息内容
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#send(java.lang.String)
     */
    @Override
    public void send(String textCnt)
    {
        getImLogic().send1V1Message(mFriendUserId, textCnt);
    }
    
    /**
     * 
     * 发送多媒体消息<BR>
     * @param textCnt 文本内容
     * @param media 多媒体消息体
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#sendMediaMsg(java.lang.String, com.huawei.basic.android.im.logic.model.MediaIndexModel)
     */
    @Override
    public void sendMediaMsg(String textCnt, MediaIndexModel media)
    {
        getImLogic().send1V1Message(mFriendUserId, textCnt, media);
    }
    
    /**
     * 文本点击事件<BR>
     * @param msgModel BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#onTextClick(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    
    @Override
    public void onTextClick(BaseMessageModel baseModel)
    {
        MessageModel msgModel = (MessageModel) baseModel;
        if (msgModel.getMsgContent() != null
                && msgModel.getMsgSendOrRecv() == BaseMessageModel.MSGSENDORRECV_RECV
                && msgModel.getFriendUserId()
                        .equals(FusionCode.XmppConfig.SECRETARY_ID))
        {
            if (msgModel.getMsgContent()
                    .equals(getResources().getString(R.string.hitalk_person_info)))
            {
                
                //设置个人资料
                Intent intent = new Intent();
                intent.setAction(SettingsAction.ACTION_ACTIVITY_PRIVATE_PROFILE_SETTING);
                startActivity(intent);
            }
            else if (msgModel.getMsgContent()
                    .equals(getResources().getString(R.string.hitalk_person_group)))
            {
                
                //如何加入群
                Intent intent = new Intent();
                intent.setAction(GroupSearchAction.ACTION_GROUP_SEARCH);
                startActivity(intent);
            }
        }
        
    }
    
    /**
     * 
     * 用户头像点击事件<BR>
     * @param msg 消息体
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
            MessageModel msgModel = (MessageModel) msg;
            if (msgModel.getFriendUserId()
                    .equals(FusionCode.XmppConfig.SECRETARY_ID))
            {
                //如果是小秘书头像，跳转到小秘书系统插件
                Intent toPlugin = new Intent(PluginDetailAction.ACTION);
                toPlugin.putExtra(PluginDetailAction.EXTRA_PLUGIN_ID,
                        FusionCode.XmppConfig.SECRETARY_PLUGIN_ID);
                startActivity(toPlugin);
            }
            else
            {
                Intent toDetail = new Intent(
                        FusionAction.ContactDetailAction.ACTION);
                toDetail.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                        mFriendUserId);
                startActivity(toDetail);
            }
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
        getImLogic().set1V1MsgAsReaded((MessageModel) msg);
    }
    
    /**
     * 创建菜单
     * @param menu Menu
     * @return boolean
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(Menu.NONE,
                MENU_CLEAR_MESSAGE,
                Menu.NONE,
                getResources().getString(R.string.clear_message_record));
        menu.getItem(0)
                .setIcon(getResources().getDrawable(R.drawable.menu_exit_icon));
        
        return true;
    }
    
    /**
     * 跳转到选择好友界面<BR>
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#onRightButtonClick(java.lang.String)
     */
    @Override
    public void onRightButtonClick()
    {
        Intent intent = new Intent();
        //设定Action,标识要跳转的界面
        intent.setAction(FusionAction.ChooseMemberAction.ACTION);
        intent.putExtra(FusionAction.ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                ChooseMemberAction.TYPE.DELETE_CURRENT_FRIEND);
        intent.putExtra(FusionAction.ChooseMemberAction.EXTRA_CURRENT_FRIEND_ID,
                mFriendUserId);
        SingleChatActivity.this.startActivityForResult(intent,
                SingleChatAction.REQUEST_CODE_TO_CHOOSE_MEMBER_FOR_CHAT);
    }
    
    /**
     * 
     * Menu菜单的选项被选中<BR>
     * @param item MenuItem
     * @return boolean
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        if (item.getItemId() == MENU_CLEAR_MESSAGE)
        {
            showConfirmDialog(getResources().getQuantityString(R.plurals.dialog_content,
                    1,
                    mFriendNickName),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getImLogic().clear1V1Message(mFriendUserId);
                        }
                    });
        }
        
        return true;
    }
    
    /**
     * 1v1删除单条消息<BR>
     * @param msgID 消息ID
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#deleteMsg(java.lang.String)
     */
    @Override
    protected void deleteMsg(String msgID)
    {
        getImLogic().delete1V1Message(msgID);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void transferMsg(String msgID, String[] friendUserIds)
    {
        getImLogic().transfer1V1Message(msgID, friendUserIds);
    }
    

    /**
     * 重发事件监听<BR>
     * @param msg BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#resendMsg(java.lang.String)
     */
    @Override
    protected void resendMsg(BaseMessageModel msg)
    {
        getImLogic().resend1V1Message((MessageModel) msg);
    }
    
    /**
     * 初始化1V1聊天的数据<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#initData()
     */
    @Override
    protected void initData()
    {
        mFriendUserId = getIntent().getStringExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_ID);
        
        Logger.d(TAG, "friend user id : " + mFriendUserId);
        
        mFriendNickName = getIntent().getStringExtra(SingleChatAction.EXTRA_FRIEND_USER_NICK_NAME);
        
        mContactInfoModel = getImLogic().getContactInfoModel(mFriendUserId);
        
        if (mContactInfoModel != null)
        {
            //判断备注名是否为空
            if (!StringUtil.isNullOrEmpty(mContactInfoModel.getMemoName()))
            {
                mFriendNickName = mContactInfoModel.getMemoName();
            }
            else
            {
                mFriendNickName = mContactInfoModel.getNickName();
            }
        }
        
        // 不是好友关系，右侧按钮灰化
        else
        {
            setmRightButton();
        }
        if (null == mFriendNickName)
        {
            mFriendNickName = "";
        }
        setTitle(mFriendNickName);
        //        setRightButton(getResources().getString(R.string.default_add));
        if (mFriendUserId != null
                && mFriendUserId.equals(FusionCode.XmppConfig.SECRETARY_ID))
        {
            //小秘书头像
            mFriendFace = getResources().getDrawable(R.drawable.icon_secretary);
            setmRightButton();
        }
        else
        {
            mFriendFace = getImLogic().getFace(mFriendUserId);
        }
        getImLogic().setAll1V1MsgAsReaded(mFriendUserId);
    }
    
    /**
     * 创建1V1聊天的Adapter<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#buildMsgAdapter()
     */
    @Override
    protected BaseMsgAdapter buildMsgAdapter(int curMsgCnt)
    {
        BaseMsgCursorWrapper cursor = getImLogic().get1V1MsgList(mFriendUserId);
        cursor.setCount(curMsgCnt);
        return new SingleMsgAdapter(this, cursor, true);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#getUnreadAudioMsgIds()
     */
    
    @Override
    protected List<String> getUnreadAudioMsgIds()
    {
        return getImLogic().get1V1UnreadAudioMsgIds(mFriendUserId);
    }
    
    /**
     * 注册1V1聊天的数据库监听<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#registerObserver()
     */
    @Override
    protected void registerObserver()
    {
        getImLogic().register1V1DataObserver(mFriendUserId);
    }
    
    /**
     * 注销1V1聊天的数据库监听<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#unregisterObserver()
     */
    @Override
    protected void unregisterObserver()
    {
        getImLogic().unregister1V1DataObserver(mFriendUserId);
    }
    
    /**
     * 处理消息<BR>
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
            // 好友信息发生变更，刷新页面
            case FusionMessageType.ChatMessageType.MSGTYPE_FRIEND_INFO_REFRESH:
                getMsgAdapter().refreshMsg();
                break;
            //被好友删除
            case FriendHelperMessageType.BE_DELETED:
                if (((String) msg.obj).equals(mFriendUserId))
                {
                    showOnlyConfirmDialog(R.string.friend_removed,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    finish();
                                }
                            });
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * <BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-13] 
     */
    private class SingleMsgAdapter extends BaseMsgAdapter
    {
        /**
         * 
         * 构造方法
         * @param context Context
         * @param c Cursor
         * @param autoRequery boolean
         */
        public SingleMsgAdapter(Context context, BaseMsgCursorWrapper c,
                boolean autoRequery)
        {
            super(context, c, autoRequery);
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.BaseChatActivity.BaseMsgAdapter#getFace(com.huawei.basic.android.im.logic.model.BaseMessageModel)
         */
        
        @Override
        protected Drawable getFace(BaseMessageModel msg)
        {
            // TODO Auto-generated method stub
            return mFriendFace;
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.BaseChatActivity.BaseMsgAdapter#getDisplayName(com.huawei.basic.android.im.logic.model.BaseMessageModel)
         */
        
        @Override
        protected String getDisplayName(BaseMessageModel msg)
        {
            return mFriendNickName;
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.adapter.BaseMsgAdapter#getTypeSendOrReceive(android.database.Cursor)
         */
        
        @Override
        protected int getTypeSendOrReceive(Cursor cursor)
        {
            return cursor.getInt(cursor.getColumnIndex(MessageColumns.MSG_SENDORRECV));
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see com.huawei.basic.android.im.ui.im.adapter.BaseMsgAdapter#getMsgType(android.database.Cursor)
         */
        
        @Override
        protected int getMsgType(Cursor cursor)
        {
            return cursor.getInt(cursor.getColumnIndex(MessageColumns.MSG_TYPE));
        }
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param intent 数据
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#onActivityResult(int, int, android.content.Intent)
     */
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (RESULT_OK == resultCode)
        {
            //从选择成员的界面返回
            if (SingleChatAction.REQUEST_CODE_TO_CHOOSE_MEMBER_FOR_CHAT == requestCode)
            {
                finish();
            }
        }
    }
    
    /**
     * 
     * onResume方法<BR>
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        //发送广播，取消通知栏
        Intent intent = new Intent(
                IMNotificationEntity.NOTIFICAITON_ACTION_IM_SINGLE);
        intent.putExtra(IMNotificationEntity.NOTIFICATION_CURRENT_FRIENDUSERID,
                mFriendUserId);
        this.sendBroadcast(intent);
    }
}
