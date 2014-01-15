/*
 * 文件名: ConversationListActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 会话界面
 * 创建人: deanye
 * 创建时间:2012-2-14
 * 
 * 修改人：周雪松
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ContactListAction;
import com.huawei.basic.android.im.common.FusionAction.GroupSearchAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.ConversationMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.im.IConversationLogic;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.MessageModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.ui.contact.ContactBaseActivity;
import com.huawei.basic.android.im.utils.DateUtil;

/**
 * 会话界面,显示会话列表
 * 
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public class ConversationListActivity extends ContactBaseActivity implements
        OnClickListener, OnItemClickListener
{
    /**
     * TAG:用于打印Log
     */
    private static final String TAG = "ConversationListActivity";
    
    /**
     * 标示菜单itemid的常量
     */
    private static final int MENU_DELETE_CONVERSATION = 0;
    
    private static final int MENU_CLEAR_ALL_CONVERSATION = 10;
    
    /**
     * 一次加载的最多的会话条数
     */
    private static final int PAGE_SIZE = 50;
    
    /**
     * 给定最大数量，用在气泡上，当气泡中的数据>99显示99+
     */
    private static final int MAX_COUNT = 99;
    
    /**
     * 设定初始查找的记录
     */
    private int mLimit = PAGE_SIZE;
    
    private int mLastItem;
    
    /**
     * 判断数据库中是否还有会话列表没有被加载到listView中
     */
    private boolean mHasMoreConversation = true;
    
    /**
     * 会话操作逻辑对象
     */
    private IConversationLogic mConversationLogic;
    
    /**
     * 存放所有会话对象的界面组件
     */
    private ListView mListView;
    
    /**
     * 多人会话头像加载器
     */
    private PhotoLoader mChatBarPhotoLoad;
    
    /**
     * 好友头像加载器
     */
    private PhotoLoader mFriendPhotoLoader;
    
    /**
     * 群头像加载器
     */
    private PhotoLoader mGrouppPhotoLoader;
    
    /**
     * 会话列表适配器，和mListView联合使用
     */
    private ConversationListAdapter mConversationListAdapter;
    
    /**
     * 配置信息
     */
    private UserConfigModel mUserConfigModel;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.conversation_list);
        initView();
        
        mChatBarPhotoLoad = new PhotoLoader(this,
                R.drawable.default_chatbar_icon, 52, 52,
                PhotoLoader.SOURCE_TYPE_CHAT_BAR, null);
        
        mFriendPhotoLoader = new PhotoLoader(this,
                R.drawable.default_contact_icon, 52, 52,
                PhotoLoader.SOURCE_TYPE_FRIEND, null);
        
        mGrouppPhotoLoader = new PhotoLoader(this,
                R.drawable.default_group_head_icon, 52, 52,
                PhotoLoader.SOURCE_TYPE_GROUP, null);
        
        mUserConfigModel = getContactLogic().queryUserConfig(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                UserConfigModel.IS_UPLOAD_CONTACTS);
        
        if (null == mUserConfigModel)
        {
            uploadFirstContact(null);
        }
        // 判断是否需上传通讯录
        else if (ContactListAction.AGREE_UPLOAD_CONTACTS.equals(mUserConfigModel.getValue()))
        {
            getContactLogic().beginUpload(false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isNeedMenu()
    {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        
        menu.add(Menu.NONE,
                MENU_CLEAR_ALL_CONVERSATION,
                Menu.NONE,
                R.string.clear_data).setIcon(R.drawable.menu_exit_icon);
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == MENU_CLEAR_ALL_CONVERSATION)
        {
            showConfirmDialog(R.string.clear_data_alert,
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mConversationLogic.clearAllConversation();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 初始化界面中所有View对象，并且添加事件监听器
     */
    private void initView()
    {
        // 显示共有多少条会话
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.tab_talkLabel);
        // "群发按钮"，没用到，设为隐藏
        Button btnGroupSend = (Button) findViewById(R.id.left_button);
        btnGroupSend.setVisibility(View.INVISIBLE);
        btnGroupSend.setOnClickListener(this);
        View chooseMemberView = findViewById(R.id.right_layout);
        chooseMemberView.setOnClickListener(this);
        // 初始化存放会话列表的ListView
        mListView = (ListView) this.findViewById(R.id.conversation_list);
        //添加ListView拖动时的监听器
        mListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
                mLastItem = firstVisibleItem + visibleItemCount;
            }
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                if (mLastItem == mConversationListAdapter.getCount()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE)
                {
                    //用户拖动时，如果数据库中还有会话就继续加载
                    if (mHasMoreConversation)
                    {
                        //请求数据库时查询的记录条数，实际返回的结果<=mLimit
                        mLimit += PAGE_SIZE;
                        updateUI();
                    }
                }
            }
            
        });
        mConversationListAdapter = new ConversationListAdapter();
        mListView.setAdapter(mConversationListAdapter);
        mListView.setOnItemClickListener(this);
        // 在列表视图中注册上下文菜单
        registerForContextMenu(mListView);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mChatBarPhotoLoad.stop();
        mFriendPhotoLoader.stop();
        mGrouppPhotoLoader.stop();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        super.initLogics();
        mConversationLogic = (IConversationLogic) super.getLogicByInterfaceClass(IConversationLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mChatBarPhotoLoad.clear();
        updateUI();
    }
    
    /**
     * 
     * 获取List数据，并更新UI<BR>
     * [功能详细描述]
     */
    private void updateUI()
    {
        // 查找该用户的会话
        //List<ConversationModel> list = mConversationLogic.loadAllConversations();
        List<ConversationModel> list = null;
        list = mConversationLogic.loadConversationsByLimit(mLimit);
        if (null != list && list.size() == mLimit)
        {
            mHasMoreConversation = true;
        }
        else
        {
            mHasMoreConversation = false;
        }
        //Logger.d(TAG, "mHasMoreConversation:" + mHasMoreConversation);
        // 要求小秘书或找朋友小助手如果会话信息>0，排在前面
        list = mConversationLogic.sort(list);
        mConversationListAdapter.setData(list);
        mConversationListAdapter.notifyDataSetChanged();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.operation);
        menu.add(0, MENU_DELETE_CONVERSATION, 0, R.string.delete_conversation);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info;
        
        try
        {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        }
        catch (ClassCastException e)
        {
            return false;
        }
        
        if (item.getItemId() == MENU_DELETE_CONVERSATION)
        {
            final ConversationModel cm = (ConversationModel) mConversationListAdapter.getItem(info.position);
            mConversationLogic.delete(cm);
        }
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        closeProgressDialog();
        
        int what = msg.what;
        switch (what)
        {
            case GroupMessageType.GET_GROUP_LIST_SUCCESS:
            case FriendMessageType.REQUEST_FOR_CONTACT_LIST:
            case ConversationMessageType.CONVERSATION_DB_CHANGED:
                if (!isPaused())
                {
                    updateUI();
                }
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 
     * 会话列表适配器类，管理每条会话信息的显示<BR>
     * [功能详细描述]
     * 
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-10]
     */
    private class ConversationListAdapter extends BaseListAdapter
    {
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder = null;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(ConversationListActivity.this,
                        R.layout.conversation_list_row,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.headImage = (ImageView) convertView.findViewById(R.id.conversation_head);
                viewHolder.unreadNumber = (TextView) convertView.findViewById(R.id.conversation_number);
                viewHolder.name = (TextView) convertView.findViewById(R.id.conversation_name);
                viewHolder.groupNumber = (TextView) convertView.findViewById(R.id.conversation_group_number);
                viewHolder.content = (TextView) convertView.findViewById(R.id.conversation_content);
                viewHolder.msgType = (TextView) convertView.findViewById(R.id.conversation_lastmessage_type);
                viewHolder.time = (TextView) convertView.findViewById(R.id.conversation_time);
                
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ConversationModel cb = (ConversationModel) getDataSrc().get(position);
            
            // 判断会话的未读消息条数
            // 如果未读消息条数>0，要在界面显示气泡，在气泡中显示未读消息的条数
            if (cb.getUnReadmsg() > 0)
            {
                viewHolder.unreadNumber.setVisibility(View.VISIBLE);
                if (cb.getUnReadmsg() <= MAX_COUNT)
                {
                    viewHolder.unreadNumber.setText(String.valueOf(cb.getUnReadmsg()));
                }
                else
                {
                    viewHolder.unreadNumber.setText(String.valueOf(MAX_COUNT)
                            + "+");
                }
                
            }
            // 未读消息=0，表示已经读过，这边不显示
            else
            {
                viewHolder.unreadNumber.setVisibility(View.GONE);
            }
            // 显示时间
            viewHolder.time.setText(DateUtil.getFormatTimeString(ConversationListActivity.this,
                    cb.getLastTime()));
            // 显示会话状态
            //            viewHolder.status.setText(mConversationLogic.getTypeString(cb.getLastMsgStatus()));
            // 根据消息类别显示消息内容 1：文本消息 2:媒体消息 3:已送达未读 4:对方已读 5:已接收未读 6:自己已读
            // 100:阻塞状态(多媒体消息正在上传附件，不处理)101:发送失败
            
            // 这边根据是文本消息还是还是多媒体消息，显示消息的内容
            // 获得消息类别：1:文本含表情符 2:多媒体
            int msgType = cb.getLastMsgType();
            if (msgType == MessageModel.MSGTYPE_TEXT
                    || msgType == MessageModel.MSGTYPE_SYSTEM)
            {
                viewHolder.content.setVisibility(View.VISIBLE);
                viewHolder.msgType.setVisibility(View.GONE);
                viewHolder.content.setText(cb.getLastMsgContent() == null ? null
                        : Html.fromHtml(cb.getLastMsgContent()));
            }
            else if (msgType == MessageModel.MSGTYPE_MEDIA)
            {
                viewHolder.content.setVisibility(View.GONE);
                viewHolder.msgType.setVisibility(View.VISIBLE);
                viewHolder.msgType.setText(mConversationLogic.getMessageTypeString(cb.getLastMsgId()));
            }
            
            // 设定默认名称
            viewHolder.name.setText("");
            //设定默认成员数目
            viewHolder.groupNumber.setText("");
            // 设定默认头像
            viewHolder.headImage.setImageResource(R.drawable.default_contact_icon);
            viewHolder.headImage.setTag(PhotoLoader.NOT_AVAIABLE_VIEW);
            
            // 获取会话类别:[1:单人会话 2:群组会话 3:群发消息 4:群内私聊 5:小秘书会话 6:找朋友小助手 7:超箱小助手
            // 8:用户首次登录时给定的会话]
            int conversationType = cb.getConversationType();
            int titleId = 0;
            int headId = R.drawable.default_contact_icon;
            switch (conversationType)
            {
                // 会话类别:HiTalk小秘书
                case ConversationModel.CONVERSATIONTYPE_SECRET:
                    titleId = R.string.hi_talk_sec;
                    headId = R.drawable.icon_secretary;
                    break;
                // HiTalk找朋友小助手
                case ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER:
                    titleId = R.string.find_friend_sec;
                    headId = R.drawable.icon_find_friend;
                    break;
                // 用户第一次登录时放入会话表中的3条会话
                case ConversationModel.CONVERSATIONTYPE_INIT_TIPS:
                {
                    if (ConversationModel.ID_VALUE_BE_FOUND.equals(cb.getConversationId()))
                    {
                        titleId = R.string.init_tips_be_found;
                        headId = R.drawable.icon_contact;
                    }
                    else if (ConversationModel.ID_VALUE_FIND_FRIEND.equals(cb.getConversationId()))
                    {
                        titleId = R.string.init_tips_find_friend;
                        headId = R.drawable.icon_find_friend;
                    }
                    else if (ConversationModel.ID_VALUE_FIND_GROUP.equals(cb.getConversationId()))
                    {
                        titleId = R.string.init_tips_find_group;
                        headId = R.drawable.icon_app;
                    }
                    break;
                }
                default:
                {
                    // 判断是单人会话，群组会话还是群内私聊
                    Object obj = mConversationLogic.getContactInfoByJID(cb.getConversationId(),
                            cb.getConversationType());
                    // 表示不是单人会话，就是群组会话或群内私聊天
                    if (null != obj)
                    {
                        setDefualtViewValue(viewHolder, obj);
                    }
                    return convertView;
                }
            }
            // 设定头像图片
            viewHolder.headImage.setImageResource(headId);
            // 设定显示的内容
            viewHolder.name.setText(titleId);
            return convertView;
        }
        
        /**
         * 
         * 设置好友或者群/多人会话的ui显示<BR>
         * [功能详细描述]
         * @param viewHolder viewHolder
         * @param obj 获取到的好友或者群组对象
         */
        private void setDefualtViewValue(ViewHolder viewHolder, Object obj)
        {
            // 表示会话类型为单人会话
            if (obj instanceof ContactInfoModel)
            {
                ContactInfoModel contactInfoModel = (ContactInfoModel) obj;
                viewHolder.name.setText(contactInfoModel.getDisplayName());
                viewHolder.headImage.setTag(PhotoLoader.SOURCE_TYPE_FRIEND);
                mFriendPhotoLoader.loadPhoto(viewHolder.headImage,
                        contactInfoModel.getFaceUrl());
            }
            // 表示为群组会话/群内私聊
            else if (obj instanceof GroupInfoModel)
            {
                GroupInfoModel groupInfoModel = (GroupInfoModel) obj;
                viewHolder.name.setText(groupInfoModel.getGroupName());
                //群成员或者聊吧成员至少有一个
                int size = groupInfoModel.getMemberCount() > 0 ? groupInfoModel.getMemberCount()
                        : 1;
                viewHolder.groupNumber.setText(String.format(getResources().getString(R.string.count),
                        size));
                if (GroupInfoModel.GROUP_TYPE_SESSION.equals(groupInfoModel.getGroupTypeString()))
                {
                    viewHolder.headImage.setTag(PhotoLoader.SOURCE_TYPE_CHAT_BAR);
                    mChatBarPhotoLoad.loadPhoto(viewHolder.headImage,
                            groupInfoModel.getGroupId());
                }
                else
                {
                    viewHolder.headImage.setTag(PhotoLoader.SOURCE_TYPE_GROUP);
                    mGrouppPhotoLoader.loadPhoto(viewHolder.headImage,
                            groupInfoModel.getFaceUrl());
                }
            }
        }
    }
    
    /**
     * 
     * 
     * 会话列表占位符
     * 
     * @author xuesongzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    private static class ViewHolder
    {
        // 显示会话列表中的头像
        private ImageView headImage;
        
        // 显示未读消息条数
        private TextView unreadNumber;
        
        // 显示会话名称
        private TextView name;
        
        private TextView groupNumber;
        
        // 显示会话内容
        private TextView content;
        
        // 显示会话的消息类别
        private TextView msgType;
        
        // 显示会话时间
        private TextView time;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.right_layout:
                Intent intent = new Intent();
                // 设定Action,标识要跳转的界面
                intent.setAction(FusionAction.ChooseMemberAction.ACTION);
                intent.putExtra(FusionAction.ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                        FusionAction.ChooseMemberAction.TYPE.REQUEST_ALL_FRIEND);
                startActivity(intent);
                break;
            default:
                break;
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        final ConversationModel cb = (ConversationModel) mConversationListAdapter.getDataSrc()
                .get(position);
        // 获取会话类别
        int conversationType = cb.getConversationType();
        Logger.d(TAG, "=====conversationType=====" + conversationType);
        Intent intent = new Intent();
        switch (conversationType)
        {
            // 1V1 聊天
            case ConversationModel.CONVERSATIONTYPE_1V1:
            {
                intent.setAction(FusionAction.SingleChatAction.ACTION);
                intent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_ID,
                        cb.getConversationId());
                break;
            }
                // 小秘书
            case ConversationModel.CONVERSATIONTYPE_SECRET:
            {
                intent.setAction(FusionAction.SingleChatAction.ACTION);
                intent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_ID,
                        cb.getConversationId());
                intent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_NICK_NAME,
                        getResources().getString(R.string.hi_talk_sec));
                break;
            }
                // 群聊或者多人会话
            case ConversationModel.CONVERSATIONTYPE_GROUP:
            {
                intent.setAction(FusionAction.MultiChatAction.ACTION);
                intent.putExtra(FusionAction.MultiChatAction.EXTRA_GROUP_ID,
                        cb.getConversationId());
                break;
            }
                // 找朋友小助手
            case ConversationModel.CONVERSATIONTYPE_FRIEND_MANAGER:
            {
                intent.setAction(FusionAction.FindFriendHelperAction.ACTION);
                break;
            }
                // 首次登录的提示语
            case ConversationModel.CONVERSATIONTYPE_INIT_TIPS:
            {
                // 建立自己的圈子
                if (ConversationModel.ID_VALUE_BE_FOUND.equals(cb.getConversationId()))
                {
                    intent.setAction(FusionAction.SettingsAction.ACTION_ACTIVITY_PRIVATE_PROFILE_SETTING);
                }
                // 找好友
                else if (ConversationModel.ID_VALUE_FIND_FRIEND.equals(cb.getConversationId()))
                {
                    intent.setAction(FusionAction.FindFriendHelperAction.ACTION);
                }
                // 找群组
                else if (ConversationModel.ID_VALUE_FIND_GROUP.equals(cb.getConversationId()))
                {
                    intent.setAction(GroupSearchAction.ACTION_GROUP_SEARCH);
                }
                mConversationLogic.delete(cb);
                
                break;
            }
        }
        if (null != intent.getAction())
        {
            startActivity(intent);
        }
    }
}
