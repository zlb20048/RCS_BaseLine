/*
 * 文件名: ChooseMemberActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: Lidan
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ChooseMemberAction;
import com.huawei.basic.android.im.common.FusionAction.ContactSectionManagerAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.im.IConversationLogic;
import com.huawei.basic.android.im.logic.model.BaseContactModel;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ContactSectionModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.ui.basic.BaseContactUtil;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.ui.basic.QuickActivity;
import com.huawei.basic.android.im.ui.basic.QuickAdapter;
import com.huawei.basic.android.im.utils.Match;

/**
 * 选择成员<BR>
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-14] 
 */
public class ChooseMemberActivity extends QuickActivity implements
        OnClickListener
{
    /**
     * 头像的默认宽度，子类可根据需要重新定义
     */
    protected static final int HEAD_WIDTH = 52;
    
    /**
     * 头像的默认高度，子类可根据需要重新定义
     */
    protected static final int HEAD_HEIGHT = 52;
    
    /**
     * DEBUG TAG
     */
    private static final String TAG = "ChooseMemberActivity";
    
    /**
     * 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 标题栏右侧全选按钮
     */
    private Button mTitleRightBtn;
    
    /**
     * 存放选择联系人头像的LinearLayout
     */
    private LinearLayout mChoosededMemberLL;
    
    /**
     * 包裹mChoosededMemberLL的HorizontalScrollView
     */
    private HorizontalScrollView mChoosedMemberHSV;
    
    /**
     * 标志mChoosedMemberHSV是否被初始化
     */
    private boolean mChoosedMemberHSVisInit;
    
    /**
     * 选中的个数
     */
    private int mChooseCount;
    
    /**
     * 聊吧的最大人数
     */
    private int mChatbarMemberMax = 20;
    
    /**
     * 还可以选多少人
     */
    private int mChooseLeft;
    
    /**
     * 存放选择的联系人
     */
    private List<ContactInfoModel> mChoosedMember;
    
    /**
     * 联系人列表适配器
     */
    private ContactInfoAdapter mAdapter;
    
    /**
     * 当前入口
     */
    private int mCurrentEntrance = -1;
    
    /**
     * 分组名称
     */
    private String mSectionName;
    
    /**
     * 分组ID
     */
    private String mSectionId;
    
    private String mCurrentFriendId;
    
    /**
     * 好友逻辑处理类
     */
    private IFriendLogic mFriendLogic;
    
    /**
     * 群逻辑处理类
     */
    private IGroupLogic mGroupLogic;
    
    /**
     * 会话逻辑处理类
     */
    private IConversationLogic mConversationLogic;
    
    /**
     * 标题
     */
    private TextView mTitleView;
    
    /**
     * 标志是否有progressDialog在显示
     */
    private boolean mProgressDialogFlag;
    
    /**
     * onCreate<BR>
     * @param savedInstanceState savedInstanceState
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.myfriend_add_member_to_section);
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                HEAD_WIDTH, HEAD_HEIGHT, PhotoLoader.SOURCE_TYPE_FRIEND, null);
        
        //默认是从添加群组入口
        mCurrentEntrance = getIntent().getIntExtra(ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                ChooseMemberAction.TYPE.ADD_SECTION);
        initView();
        switch (mCurrentEntrance)
        {
        //添加分组入口进入时
            case ChooseMemberAction.TYPE.ADD_SECTION:
            {
                mSectionName = getIntent().getStringExtra(ChooseMemberAction.EXTRA_SECTION_NAME);
                //默认分组ID，建议在Model中定义
                ArrayList<ContactInfoModel> contactInfoList = mFriendLogic.getContactListBySectionId(ContactSectionModel.DEFAULT_SECTION_ID);
                updateView(contactInfoList);
                break;
            }
            //添加联系人到分组
            case ChooseMemberAction.TYPE.ADD_CONTACT_TO_SECTION:
            {
                mSectionId = getIntent().getStringExtra(ContactSectionManagerAction.EXTRA_SECTION_ID);
                //默认分组ID，建议在Model中定义
                ArrayList<ContactInfoModel> contactInfoList = mFriendLogic.getContactListBySectionId(ContactSectionModel.DEFAULT_SECTION_ID);
                updateView(contactInfoList);
                break;
            }
            //移除成员
            case ChooseMemberAction.TYPE.REMOVE_MEMBER:
            {
                mSectionId = getIntent().getStringExtra(ChooseMemberAction.EXTRA_SECTION_ID);
                ArrayList<ContactInfoModel> contactInfoList = mFriendLogic.getContactListBySectionId(mSectionId);
                updateView(contactInfoList);
                break;
            }
            //请求选中的好友列表
            case ChooseMemberAction.TYPE.REQUEST_FOR_FRIEND_ID_LIST:
            {
                mFriendLogic.getContactSectionListWithFriendListAsyn();
                break;
            }
            //添加群成员列表
            case ChooseMemberAction.TYPE.ADD_GROUP_MEMBER:
            {
                String groupId = getIntent().getStringExtra(ChooseMemberAction.EXTRA_GROUP_ID);
                List<ContactInfoModel> contactInfoliList = mGroupLogic.getContactListForAdd(groupId);
                updateView(contactInfoliList);
                initSelectedMemberHSV();
                GroupInfoModel gim = mGroupLogic.getGroupInfoModelFromDB(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        groupId);
                if (GroupInfoModel.GROUPTYPE_NVN == gim.getGroupType())
                {
                    mChooseLeft = mChatbarMemberMax - gim.getMemberCount();
                }
                else
                {
                    mChooseLeft = Integer.MAX_VALUE;
                }
                break;
            }
            //删除当前聊天好友和自己后的列表
            case ChooseMemberAction.TYPE.DELETE_CURRENT_FRIEND:
                mCurrentFriendId = getIntent().getStringExtra(ChooseMemberAction.EXTRA_CURRENT_FRIEND_ID);
                List<ContactInfoModel> friendList = mFriendLogic.getContactInfoListFromDb();
                if (friendList != null && mCurrentFriendId != null)
                {
                    for (ContactInfoModel contactInfo : friendList)
                    {
                        if (contactInfo.getFriendUserId()
                                .equals(mCurrentFriendId))
                        {
                            friendList.remove(contactInfo);
                            break;
                        }
                    }
                }
                updateView(friendList);
                initSelectedMemberHSV();
                mChooseLeft = mChatbarMemberMax - 2;
                break;
            //获取用来创建多人会话或一对一聊天的好友列表
            case ChooseMemberAction.TYPE.REQUEST_ALL_FRIEND:
                List<ContactInfoModel> allFriendList = mFriendLogic.getContactInfoListFromDb();
                updateView(allFriendList);
                initSelectedMemberHSV();
                mChooseLeft = mChatbarMemberMax - 1;
                break;
        }
    }
    
    /**
     * 处理信息<BR>
     * @param msg msg
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        Object obj = msg.obj;
        switch (msg.what)
        {
        //创建聊吧成功,然后需要邀请成员加入到聊吧中
            case GroupMessageType.CREATE_GROUP_SUCCESS_FROM_CONVERSATION:
                GroupInfoModel groupInfoModel = (GroupInfoModel) obj;
                Logger.d(TAG, "groupInfoModel = " + groupInfoModel.toString());
                Intent multiChatIntent = new Intent(
                        FusionAction.MultiChatAction.ACTION);
                multiChatIntent.putExtra(FusionAction.MultiChatAction.EXTRA_GROUP_ID,
                        groupInfoModel.getGroupId());
                multiChatIntent.putExtra(FusionAction.MultiChatAction.EXTRA_GROUP_NAME,
                        groupInfoModel.getGroupName());
                startActivity(multiChatIntent);
                if (ChooseMemberAction.TYPE.DELETE_CURRENT_FRIEND == mCurrentEntrance)
                {
                    //销毁单人聊天界面
                    Intent intent = getIntent();
                    ChooseMemberActivity.this.setResult(Activity.RESULT_OK,
                            intent);
                }
                finish();
                closeProgressDialog();
                mProgressDialogFlag = false;
                break;
            //创建聊吧失败，给出失败提示
            case GroupMessageType.CREATE_GROUP_FAILED_FROM_CONVERSATION:
                closeProgressDialog();
                mProgressDialogFlag = false;
                if (null != obj)
                {
                    showToast((String) obj);
                }
                else
                {
                    showToast(R.string.create_chatbar_failed);
                }
                break;
            //邀请成员成功
            case GroupMessageType.INVITE_MEMBER_SUCCESS_FROM_CONVERSATION:
                break;
            //邀请成员失败，给出失败提示
            case GroupMessageType.INVITE_MEMBER_FAILED_FROM_CONVERSATION:
                if (null != obj)
                {
                    showToast((String) obj);
                }
                else
                {
                    showToast(R.string.invite_members_to_failed);
                }
                break;
            case FriendMessageType.REQUEST_TO_ADD_SECTION:
            case FriendMessageType.REQUEST_ADD_CONTACT_TO_SECTION:
            case FriendMessageType.REQUEST_REMOVE_CONTACT_FROM_SECTION:
            {
                Logger.i(TAG, "handleStateMessage -------------> 添加联系人到分组收到响应");
                setResult(RESULT_OK);
                finish();
                break;
            }
            case FriendMessageType.GET_CONTACT_LIST_FROM_DB:
            {
                if (msg.obj != null)
                {
                    ArrayList<ContactInfoModel> list = new ArrayList<ContactInfoModel>();
                    for (ContactSectionModel model : (ArrayList<ContactSectionModel>) msg.obj)
                    {
                        if (model.getFriendList() != null)
                        {
                            list.addAll(model.getFriendList());
                        }
                    }
                    updateView(list);
                }
                break;
            }
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * onClick<BR>
     * @param v v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        if (mProgressDialogFlag)
        {
            return;
        }
        
        // 返回
        if (v.getId() == R.id.left_button)
        {
            //收起软键盘
            hideInputWindow(v);
            onBackPressed();
        }
        
        // 完成
        else if (v.getId() == R.id.right_button)
        {
            switch (mCurrentEntrance)
            {
                case ChooseMemberAction.TYPE.ADD_SECTION:
                {
                    mFriendLogic.addSection(mSectionName,
                            null,
                            mAdapter.getChoosedSysIdList());
                    break;
                }
                case ChooseMemberAction.TYPE.REQUEST_FOR_FRIEND_ID_LIST:
                {
                    //对数据进行操作，返回选中的数据
                    Intent intent = getIntent();
                    intent.putExtra(ChooseMemberAction.RESULT_CHOOSED_USER_ID_LIST,
                            mAdapter.getChoosedFriendUserIds());
                    ChooseMemberActivity.this.setResult(Activity.RESULT_OK,
                            intent);
                    ChooseMemberActivity.this.finish();
                    break;
                }
                case ChooseMemberAction.TYPE.MANAGE_SECTION:
                    break;
                //添加联系人到分组
                case ChooseMemberAction.TYPE.ADD_CONTACT_TO_SECTION:
                    Logger.e("TAG", "进入方式" + "TYPE_ADD_CONTACT_TO_SECTION"
                            + "mChooseCount===" + mChooseCount);
                    //如果选择的成员为0不发送请求
                    if (mChooseCount <= 0)
                    {
                        showToast(R.string.section_empty);
                    }
                    else
                    {
                        mFriendLogic.addContactsToSection(mSectionId,
                                mAdapter.getChoosedSysIdList());
                    }
                    break;
                case ChooseMemberAction.TYPE.REMOVE_MEMBER:
                {
                    if (mChooseCount <= 0)
                    {
                        showToast(R.string.section_empty);
                    }
                    else
                    {
                        mFriendLogic.removeMemberFromSection(mAdapter.getChoosedSysIdList(),
                                mSectionId);
                    }
                    break;
                }
                case ChooseMemberAction.TYPE.ADD_GROUP_MEMBER:
                {
                    if (mChooseCount <= 0)
                    {
                        showToast(R.string.section_empty);
                    }
                    else
                    {
                        Intent intent = getIntent();
                        intent.putExtra(ChooseMemberAction.RESULT_CHOOSED_USER_ID_LIST,
                                mAdapter.getChoosedFriendUserIds());
                        ChooseMemberActivity.this.setResult(Activity.RESULT_OK,
                                intent);
                        ChooseMemberActivity.this.finish();
                    }
                    break;
                }
                case ChooseMemberAction.TYPE.DELETE_CURRENT_FRIEND:
                {
                    if (mChooseCount <= 0)
                    {
                        showToast(R.string.section_empty);
                    }
                    else
                    {
                        //获取选中的用户的friendUserId
                        String[] savedIds = mAdapter.getChoosedFriendUserIds();
                        if (null == savedIds || 0 == savedIds.length)
                        {
                            return;
                        }
                        String[] addFriendSavedIds = new String[savedIds.length + 1];
                        System.arraycopy(savedIds,
                                0,
                                addFriendSavedIds,
                                0,
                                savedIds.length);
                        addFriendSavedIds[savedIds.length] = mCurrentFriendId;
                        //选择好友数量为多个，发起群聊
                        showProgressDialog(R.string.create_chat_bar);
                        mProgressDialogFlag = true;
                        GroupInfoModel model = mConversationLogic.createConversationByFriendUserIds(addFriendSavedIds);
                        mGroupLogic.createGroupByIds(addFriendSavedIds,
                                model,
                                GroupMessageType.CREATE_GROUP_FROM_CONVERSATION);
                    }
                    break;
                }
                case ChooseMemberAction.TYPE.REQUEST_ALL_FRIEND:
                {
                    if (mChooseCount <= 0)
                    {
                        showToast(R.string.section_empty);
                    }
                    else
                    {
                        //获取选中的用户的friendUserId
                        String[] savedIds = mAdapter.getChoosedFriendUserIds();
                        //选择好友的数量为1，发起1对1会话
                        if (1 == savedIds.length)
                        {
                            Intent singleChatIntent = new Intent(
                                    FusionAction.SingleChatAction.ACTION);
                            singleChatIntent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_ID,
                                    savedIds[0]);
                            startActivity(singleChatIntent);
                            finish();
                        }
                        //选择好友数量为多个，发起群聊
                        else
                        {
                            if (mGroupLogic.hasGroupReachMaxNumber())
                            {
                                showToast(R.string.group_over_max);
                            }
                            else
                            {
                                //Logger.d(TAG, "onActivityResult(),选择好友为多个，发起群聊");
                                showProgressDialog(R.string.create_chat_bar);
                                mProgressDialogFlag = true;
                                GroupInfoModel model = mConversationLogic.createConversationByFriendUserIds(savedIds);
                                mGroupLogic.createGroupByIds(savedIds,
                                        model,
                                        GroupMessageType.CREATE_GROUP_FROM_CONVERSATION);
                            }
                        }
                    }
                    break;
                }
            }
        }
        
        // 全选
        //        else if (v.getId() == R.id.right_button)
        //        {
        //            // 按钮显示“全选”时，勾选全部好友
        //            if (getString(R.string.choose_all).equals(mTitleRightBtn.getText()))
        //            {
        //                mTitleRightBtn.setText(R.string.dischoose_all);
        //                mAdapter.chooseAll(true);
        //            }
        //            
        //            // 按钮显示“全不选”时，去除选中全部好友
        //            else
        //            {
        //                mTitleRightBtn.setText(R.string.choose_all);
        //                mAdapter.chooseAll(false);
        //            }
        //            
        //        }
        
    }
    
    /**
     * 
     * 初始化页面组件<BR>
     * [功能详细描述]
     */
    private void initView()
    {
        // 获取组件对象引用
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setVisibility(View.VISIBLE);
        Button titleLeftBtn = (Button) findViewById(R.id.left_button);
        mTitleRightBtn = (Button) findViewById(R.id.right_button);
        mTitleRightBtn.setText(R.string.done);
        mTitleRightBtn.setVisibility(View.VISIBLE);
        mAdapter = new ContactInfoAdapter();
        // 设置显示文字
        setTitleText();
        
        // 设置按钮监听器
        titleLeftBtn.setOnClickListener(this);
        mTitleRightBtn.setOnClickListener(this);
    }
    
    /**
     * 
     * 初始化底部的头像bar<BR>
     * [功能详细描述]
     */
    private void initSelectedMemberHSV()
    {
        mChoosedMemberHSV = (HorizontalScrollView) findViewById(R.id.selected_member_hsv);
        mChoosedMemberHSV.setVisibility(View.VISIBLE);
        mChoosededMemberLL = (LinearLayout) findViewById(R.id.selected_member_ll);
        mChoosedMember = new LinkedList<ContactInfoModel>();
        mChoosedMemberHSVisInit = true;
    }
    
    /**
     * 设置选中状态<BR>
     */
    private void setTitleText()
    {
        mTitleView.setText(getResources().getQuantityString(R.plurals.choose_member_title,
                1,
                mChooseCount));
        if (mChooseCount == 0)
        {
            mTitleRightBtn.setEnabled(false);
        }
        else
        {
            mTitleRightBtn.setEnabled(true);
        }
    }
    
    /**
     * 好友列表Adapter<BR>
     * @author lidan
     * @version [RCS Client V100R001C03, Feb 16, 2012]
     */
    private class ContactInfoAdapter extends QuickAdapter
    {
        /**
         * 缓存列表成员是否被选中的状态
         */
        private final Map<Object, Boolean> mChooseMap;
        
        /**
         * [构造简要说明]
         */
        public ContactInfoAdapter()
        {
            super();
            mChooseMap = new HashMap<Object, Boolean>();
        }
        
        /**
         * 
         * contact info对应的好友是否被选中<BR>
         * [功能详细描述]
         * 
         * @param contactInfo ContactInfoModel
         * @return 选中返回true，反之返回false
         */
        public boolean isChoosed(ContactInfoModel contactInfo)
        {
            return mChooseMap.containsKey(contactInfo)
                    && mChooseMap.get(contactInfo);
        }
        
        /**
         * 取消某个选项的选中<BR>
         * [功能详细描述]
         */
        public void disChoose(ContactInfoModel contactInfo)
        {
            mChooseMap.put(contactInfo, false);
            mChooseCount--;
            mChooseLeft++;
            setTitleText();
            notifyDataSetChanged();
        }
        
        @Override
        public View getItemView(int position, View convertView, ViewGroup parent)
        {
            final ContactInfoModel contactInfo = (ContactInfoModel) getDisplayList().get(position);
            final ContactInfoViewHolder holder;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(ChooseMemberActivity.this,
                        R.layout.myfriend_friend_items,
                        null);
                convertView.findViewById(R.id.friend_item)
                        .setVisibility(View.GONE);
                holder = new ContactInfoViewHolder();
                holder.itemView = convertView.findViewById(R.id.item);
                holder.photoIv = (ImageView) convertView.findViewById(R.id.photo);
                holder.displayNameTv = (TextView) convertView.findViewById(R.id.display_name);
                holder.signatureTv = (TextView) convertView.findViewById(R.id.friend_signature);
                holder.chooseCheckBox = (ImageView) convertView.findViewById(R.id.friend_choose);
                holder.idItemView = convertView.findViewById(R.id.friend_item);
                
                convertView.setTag(holder);
            }
            else
            {
                holder = (ContactInfoViewHolder) convertView.getTag();
            }
            holder.displayNameTv.setText(contactInfo.getDisplayName());
            holder.signatureTv.setText(contactInfo.getSignature());
            holder.idItemView.setVisibility(View.GONE);
            holder.chooseCheckBox.setVisibility(View.VISIBLE);
            
            holder.chooseCheckBox.setImageResource(isChoosed(contactInfo) ? R.drawable.checkbox_selected
                    : R.drawable.checkbox_normal);
            //加载头像
            mPhotoLoader.loadPhoto(holder.photoIv, contactInfo.getFaceUrl());
            
            holder.itemView.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    boolean chooseStatus = !isChoosed(contactInfo);
                    if ((chooseStatus && mChooseLeft > 0) || !chooseStatus)
                    {
                        holder.chooseCheckBox.setImageResource(chooseStatus ? R.drawable.checkbox_selected
                                : R.drawable.checkbox_normal);
                        mChooseMap.put(contactInfo, chooseStatus);
                        mChooseCount = chooseStatus ? ++mChooseCount
                                : --mChooseCount;
                        mChooseLeft = chooseStatus ? --mChooseLeft
                                : ++mChooseLeft;
                        //如果初始化了，因为有些界面不需要这个组件
                        if (mChoosedMemberHSVisInit)
                        {
                            if (chooseStatus)
                            {
                                addMemberToBar(contactInfo);
                            }
                            else
                            {
                                removeMemberFromBar(contactInfo);
                            }
                        }
                        setTitleText();
                    }
                    else
                    {
                        showToast(R.string.member_reach_maximum);
                    }
                }
            });
            return convertView;
        }
        
        /**
         * 比配搜索结果<BR>
         * @param obj obj
         * @param key key
         * @return  是否匹配
         * @see com.huawei.basic.android.im.ui.basic.QuickAdapter#isMatched(java.lang.Object, java.lang.String)
         */
        @Override
        public boolean isMatched(Object obj, String key)
        {
            
            if (obj instanceof ContactInfoModel)
            {
                ContactInfoModel contact = (ContactInfoModel) obj;
                return Match.match(key,
                        contact.getInitialName(),
                        contact.getSpellName(),
                        contact.getFriendUserId());
            }
            return false;
            
        }
        
        /**
         * 获取选中的SysID列表，采用String串，中间用‘,’隔开，
         * 减小Activity之间的传递数据量<BR>
         * @return 返回选中的ID List
         */
        public String[] getChoosedFriendUserIds()
        {
            List<String> list = new ArrayList<String>();
            for (Object key : mChooseMap.keySet())
            {
                boolean isChecked = mChooseMap.get(key);
                if (isChecked)
                {
                    ContactInfoModel contactInfoModel = (ContactInfoModel) key;
                    list.add(contactInfoModel.getFriendUserId());
                }
            }
            return list.toArray(new String[list.size()]);
        }
        
        /**
         * 获取选中的好友列表<BR>
         * @return 选中好友列表
         */
        public ArrayList<String> getChoosedSysIdList()
        {
            ArrayList<String> choosedList = null;
            if (mChooseMap != null)
            {
                choosedList = new ArrayList<String>();
                for (Object object : mChooseMap.keySet())
                {
                    Logger.e(TAG,
                            "mChooseMap.get((ContactInfoModel) object)==="
                                    + mChooseMap.get((ContactInfoModel) object));
                    //判断选择数组都为已选择
                    if (mChooseMap.get((ContactInfoModel) object))
                    {
                        choosedList.add(((ContactInfoModel) object).getFriendSysId());
                    }
                }
            }
            return choosedList;
        }
        
    }
    
    /**
     * 好友ViewHolder<BR>
     * 好友或系统插件的组件都定义在这里
     * @author lidan
     * @version [RCS Client V100R001C03, Feb 16, 2012]
     */
    private class ContactInfoViewHolder
    {
        
        /**
         * 整行(item)
         */
        private View itemView;
        
        /**
         * 头像(photo)
         */
        private ImageView photoIv;
        
        /**
         * 昵称(display_name)
         */
        private TextView displayNameTv;
        
        /**
         * 签名(friend_signature)
         */
        private TextView signatureTv;
        
        /**
         * id与功能性图标（sms）等整个一列的view
         */
        private View idItemView;
        
        /**
         * 选择好友的check box.
         */
        private ImageView chooseCheckBox;
        
    }
    
    //将选择的成员的头像添加到底部bar
    private void addMemberToBar(final ContactInfoModel contactInfoModel)
    {
        mChoosedMember.add(contactInfoModel);
        View view = LinearLayout.inflate(ChooseMemberActivity.this,
                R.layout.selected_member_hsv_item,
                null);
        ImageView selectedMemberIV = (ImageView) view.findViewById(R.id.selected_member_photo);
        mPhotoLoader.loadPhoto(selectedMemberIV, contactInfoModel.getFaceUrl());
        selectedMemberIV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                removeMemberFromBar(contactInfoModel);
                mAdapter.disChoose(contactInfoModel);
            }
        });
        mChoosededMemberLL.addView(view, mChoosededMemberLL.getChildCount() - 1);
        mChoosedMemberHSV.fullScroll(View.FOCUS_RIGHT);
    }
    
    //从底部bar中删除选中的成员
    private void removeMemberFromBar(ContactInfoModel contactInfoModel)
    {
        mChoosededMemberLL.removeViewAt(mChoosedMember.indexOf(contactInfoModel));
        mChoosedMember.remove(contactInfoModel);
    }
    
    /**
     * getQuickAdapter<BR>
     * @return mAdapter
     * @see com.huawei.basic.android.im.ui.basic.QuickActivity#getQuickAdapter()
     */
    @Override
    protected QuickAdapter getQuickAdapter()
    {
        return mAdapter;
    }
    
    /**
     * 根据ContactInfoModel列表生成displayList<BR>
     * @param contactList contactList
     * @return displayList 
     * @see com.demo.friend.ui.basic.QuickActivity#generateDisplayList(java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Object> generateDisplayList(List<?> contactList)
    {
        return BaseContactUtil.contactListForDisplay((List<? extends BaseContactModel>) contactList);
    }
    
    /**
     * 初始化逻辑接口<BR>
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mFriendLogic = (IFriendLogic) getLogicByInterfaceClass(IFriendLogic.class);
        mGroupLogic = (IGroupLogic) getLogicByInterfaceClass(IGroupLogic.class);
        mConversationLogic = (IConversationLogic) getLogicByInterfaceClass(IConversationLogic.class);
    }
    
    /**
     * 是否含有搜索<BR>
     * @return 是否搜索
     * @see com.huawei.basic.android.im.ui.basic.QuickActivity#hasSearch()
     */
    @Override
    protected boolean hasSearch()
    {
        return true;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param view view
     * @param scrollState scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (scrollState == OnScrollListener.SCROLL_STATE_FLING)
        {
            mPhotoLoader.pause();
        }
        else
        {
            mPhotoLoader.resume();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause()
    {
        mPhotoLoader.pause();
        super.onPause();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        mPhotoLoader.resume();
        super.onResume();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop()
    {
        mPhotoLoader.stop();
        super.onStop();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        mPhotoLoader.stop();
        super.onDestroy();
    }
}
