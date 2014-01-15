/*
 * 文件名: ChatbarMemberActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: fengdai
 * 创建时间:Mar 21, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.group;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ChatbarMemberAction;
import com.huawei.basic.android.im.common.FusionAction.ChatbarNameModifyAction;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.logic.group.GroupLogic;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.CustomGridView;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 聊吧成员管理界面<BR>
 * [功能详细描述]
 * @author fengdai
 * @version [RCS Client V100R001C03, Mar 21, 2012] 
 */
public class ChatbarMemberActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * 跳转到选择好友界面添加成员标识
     */
    private static final int REQUEST_ADD_CODE = 0x00000001;
    
    /**
     * 跳转到修改聊吧名称界面
     */
    private static final int REQUEST_MODIFY_CODE = 0x00000002;
    
    /**
     * 一次获取群成员的数目
     */
    private static final int PAGE_SIZE = 200;
    
    /**
     * 获取群成员的页面id
     */
    private int mPageId = 1;
    
    /**
     * 聊吧成员列表
     */
    private CustomGridView mMemberGridView;
    
    /**
     * 用于显示聊吧名称的TextView
     */
    private TextView mChatbarName;
    
    /**
     * 适配器
     */
    private ChatbarMemberAdapter mChatbarMemberAdapter;
    
    /**
     * 群组ID(聊吧ID)
     */
    private String mGroupId;
    
    /**
     * 是否是聊吧创建者标识
     */
    private boolean mIsOwner;
    
    /**
     * 标志是否想删人，当长按GridView中的头像后为true
     */
    private boolean mIsWantDeleteMember = false;
    
    /**
     * 处理群组业务逻辑的对象
     */
    private IGroupLogic mGroupLogic;
    
    private GroupInfoModel mGroupInfoModel;
    
    /**
     * 用来记录GridView是否触摸按下
     */
    private boolean mTouchDown = false;
    
    /**
     * 用来记录GridView是否触摸抬起
     */
    private boolean mTouchUp = false;
    
    /**
     * 批量读取头像 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbar_member);
        //头像下载器
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                52, 52, PhotoLoader.SOURCE_TYPE_FRIEND, null);
        mGroupId = getIntent().getStringExtra(ChatbarMemberAction.EXTRA_GROUP_ID);
        mGroupInfoModel = mGroupLogic.getGroupInfoModelFromDB(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                mGroupId);
        mIsOwner = mGroupLogic.isOwner(mGroupId);
        intiView();
        mChatbarMemberAdapter = new ChatbarMemberAdapter();
        mMemberGridView.setAdapter(mChatbarMemberAdapter);
        getDataForUI();
        mGroupLogic.registerGroupInfoByIdObserver(mGroupId);
        mGroupLogic.getMemberListFromXmpp(mGroupId, mPageId, PAGE_SIZE);
    }
    
    /**
     * 
     * 初始化界面控件<BR>
     * 
     */
    private void intiView()
    {
        Button backButton = (Button) findViewById(R.id.left_button);
        backButton.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.view_chatbar_member));
        View chatbarNameRow = (View) findViewById(R.id.chatbar_name_row);
        ImageView chatbarNamePointer = (ImageView) findViewById(R.id.chatbar_name_pointer);
        Button exitChatBarButton = (Button) findViewById(R.id.bottom_button);
        exitChatBarButton.setOnClickListener(this);
        if (mIsOwner)
        {
            exitChatBarButton.setText(R.string.chatbar_close);
            chatbarNameRow.setOnClickListener(this);
        }
        else
        {
            exitChatBarButton.setText(R.string.chatbar_exit);
            //如果不是吧主，不能编辑聊吧名称
            chatbarNameRow.setClickable(false);
            chatbarNamePointer.setVisibility(View.INVISIBLE);
        }
        mMemberGridView = (CustomGridView) findViewById(R.id.members);
        mMemberGridView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                //点击GridView的空白部分取消删除
                if (mIsWantDeleteMember)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            mTouchDown = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if (mTouchDown)
                            {
                                mTouchUp = true;
                            }
                            break;
                        default:
                            break;
                    }
                    //取消删除
                    if (mTouchDown && mTouchUp)
                    {
                        mIsWantDeleteMember = false;
                        mTouchDown = false;
                        mTouchUp = false;
                        //刷新GridView
                        mChatbarMemberAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        });
        mChatbarName = (TextView) findViewById(R.id.chatbar_name_tv);
        mChatbarName.setText(mGroupInfoModel.getGroupName());
    }
    
    /**
     * 
     * 从数据库获取UI显示用的数据<BR>
     * 
     */
    private void getDataForUI()
    {
        List<GroupMemberModel> list = mGroupLogic.getMemberListFromDB(mGroupId);
        mGroupLogic.sortMember(list);
        mChatbarMemberAdapter.setData(list);
        mChatbarMemberAdapter.notifyDataSetChanged();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        int what = msg.what;
        switch (what)
        {
            case GroupMessageType.GET_MEMBER_LIST_SUCCESS:
                refresh();
                break;
            case GroupMessageType.REMOVE_MEMBER_SUCCESS_FROM_GROUP:
                showToast(R.string.delete_success);
                break;
            case GroupMessageType.REMOVE_MEMBER_FAILED_FROM_GROUP:
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                else
                {
                    showToast(R.string.delete_fail);
                }
                break;
            case GroupMessageType.MEMBER_REMOVED_FROM_GROUP:
                //如果是自己退出聊吧
                if (((String) msg.obj).equals(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID()))
                {
                    showToast(R.string.group_quit_success);
                    finish();
                }
                else
                {
                    refresh();
                }
                break;
            case GroupMessageType.MEMBER_KICKED_FROM_GROUP:
                if (((String) msg.obj).equals(mGroupId))
                {
                    showOnlyConfirmDialog(R.string.group_kicked,
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
            case GroupMessageType.MEMBER_NICKNAME_CHANGED:
                if (((String) msg.obj).equals(mGroupId))
                {
                    refresh();
                }
                break;
            case GroupMessageType.MEMBER_ADDED_TO_GROUP:
                if (((String) msg.obj).equals(mGroupId))
                {
                    refresh();
                }
                break;
            case GroupMessageType.INVITE_MEMBER_FAILED_FROM_GROUP:
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                else
                {
                    showToast(R.string.invite_failed);
                }
                break;
            case GroupMessageType.GROUPINFO_DB_ONE_RECORD_CHANGED:
                GroupInfoModel gim = mGroupLogic.getGroupInfoModelFromDB(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(),
                        mGroupId);
                if (null != gim)
                {
                    mChatbarName.setText(gim.getGroupName());
                    showToast(R.string.groupinfo_modify_success);
                }
                break;
            case GroupMessageType.GROUP_DESTROY_SUCCESS:
                showToast(R.string.chatbar_destroy_success);
                finish();
                break;
            case GroupMessageType.GROUP_QUIT_SUCCESS:
                finish();
                break;
            //解散群失败
            case GroupMessageType.GROUP_DESTROY_FAILED:
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                else
                {
                    showToast(R.string.chatbar_destroy_failed);
                }
                break;
            case GroupMessageType.GROUP_DESTROYED_SUCCESS:
                if (((String) msg.obj).equals(mGroupId))
                {
                    showOnlyConfirmDialog(R.string.chatbar_destroyed_success,
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
            case GroupMessageType.GET_MEMBER_LIST_FAILED:
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                break;
            case GroupMessageType.GROUP_QUIT_FAILED:
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                else
                {
                    showToast(R.string.group_quit_failed);
                }
                break;
            //更改聊吧名称失败
            case GroupMessageType.SUBMIT_CONFIGINFO_FAILED:
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                break;
            //邀请成功
            case GroupMessageType.INVITE_MEMBER_SUCCESS_FROM_GROUP:
                showToast(R.string.invite_members_to_success);
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        if (isNeedUpdate())
        {
            getDataForUI();
            setNeedUpdate(false);
        }
        mPhotoLoader.resume();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        mPhotoLoader.pause();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mGroupLogic.unregisterGroupInfoByIdObserver(mGroupId);
        mPhotoLoader.stop();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent)
    {
        if (Activity.RESULT_CANCELED == resultCode)
        {
            return;
        }
        if (REQUEST_ADD_CODE == requestCode)
        {
            String[] savedIds = intent.getExtras()
                    .getStringArray(FusionAction.ChooseMemberAction.RESULT_CHOOSED_USER_ID_LIST);
            if (null == savedIds || 0 == savedIds.length)
            {
                return;
            }
            mGroupLogic.inviteMember(savedIds,
                    mGroupId,
                    GroupMessageType.INVITE_MEMBER_FROM_GROUP);
        }
        else if (REQUEST_MODIFY_CODE == requestCode)
        {
            String text = intent.getStringExtra(ChatbarNameModifyAction.EXTRA_CHATBAR_NAME);
            if (!StringUtil.isNullOrEmpty(text)
                    && !text.equals(mGroupInfoModel.getGroupName()))
            {
                mGroupInfoModel.setGroupName(text);
                mGroupLogic.submitConfigInfo(mGroupInfoModel);
            }
        }
    }
    
    /**
     * 聊吧gridView的adapter<BR>
     * [功能详细描述]
     * @author fengdai
     * @version [RCS Client V100R001C03, Mar 21, 2012]
     */
    private class ChatbarMemberAdapter extends BaseListAdapter
    {
        @Override
        public int getCount()
        {
            //假如是吧主，需要增加一个添加成员的图标到GridView
            return mIsOwner ? super.getCount() + 1 : super.getCount();
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (null == convertView)
            {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ChatbarMemberActivity.this)
                        .inflate(R.layout.chatbar_member_item, null);
                holder.memberGroup = convertView.findViewById(R.id.chatbar_member_group);
                holder.headImg = (ImageView) convertView.findViewById(R.id.member_head);
                holder.deleteMember = (ImageView) convertView.findViewById(R.id.member_delete);
                holder.addView = convertView.findViewById(R.id.chatbar_add_member);
                holder.nameText = (TextView) convertView.findViewById(R.id.member_name);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            
            // 默认为GONE
            holder.deleteMember.setVisibility(View.GONE);
            
            // 如果是吧主，吧主的最后一个GridView元素为添加成员图标
            if (mIsOwner && position == getCount() - 1)
            {
                holder.memberGroup.setVisibility(View.GONE);
                // 该图标下没有名字
                holder.nameText.setVisibility(View.INVISIBLE);
                //在不删人的情况下，该图标显示，否则不可见
                if (!mIsWantDeleteMember)
                {
                    holder.addView.setVisibility(View.VISIBLE);
                    holder.addView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 转到添加成员界面
                            Intent intent = new Intent(
                                    FusionAction.ChooseMemberAction.ACTION);
                            intent.putExtra(FusionAction.ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                                    FusionAction.ChooseMemberAction.TYPE.ADD_GROUP_MEMBER);
                            intent.putExtra(FusionAction.ChooseMemberAction.EXTRA_GROUP_ID,
                                    mGroupId);
                            startActivityForResult(intent, REQUEST_ADD_CODE);
                        }
                    });
                }
                else
                {
                    holder.addView.setVisibility(View.GONE);
                }
            }
            else
            {
                final GroupMemberModel gmm = (GroupMemberModel) getItem(position);
                holder.memberGroup.setVisibility(View.VISIBLE);
                holder.addView.setVisibility(View.GONE);
                holder.nameText.setVisibility(View.VISIBLE);
                holder.nameText.setText(gmm.getMemberNick());
                
                //加载头像数据
                mPhotoLoader.loadPhoto(holder.headImg, gmm.getMemberFaceUrl());
                
                if (mIsOwner)
                {
                    if (mIsWantDeleteMember)
                    {
                        //删人，删除图片为可见
                        holder.deleteMember.setVisibility(View.VISIBLE);
                        holder.deleteMember.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            { // 处理删除操作
                                mGroupLogic.removeMember(gmm.getMemberId(),
                                        mGroupId,
                                        GroupMessageType.REMOVE_MEMBER_FROM_GROUP);
                            }
                        });
                    }
                    holder.headImg.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            mIsWantDeleteMember = true;
                            //刷新GridView
                            mChatbarMemberAdapter.notifyDataSetChanged();
                            return true;
                        }
                    });
                }
                
                if (GroupMemberModel.AFFILIATION_OWNER.equals(gmm.getAffiliation()))
                {
                    //吧主是无法删除的
                    holder.deleteMember.setVisibility(View.GONE);
                }
                
                holder.headImg.setOnClickListener(new View.OnClickListener()
                {
                    
                    @Override
                    public void onClick(View v)
                    {
                        // 跳转到成员详情
                        Intent intent = new Intent(
                                FusionAction.ContactDetailAction.ACTION);
                        intent.putExtra(ContactDetailAction.BUNDLE_CONTACT_MODE,
                                ContactDetailAction.HITALK_CONTACT);
                        intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                                gmm.getMemberUserId());
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }
    }
    
    /**
     * 定义成员信息展示所需控件<BR>
     * [功能详细描述]
     * @author fengdai
     * @version [RCS Client V100R001C03, Mar 21, 2012]
     */
    private class ViewHolder
    {
        /**
         * 成员头像，群主标识，删除的布局
         */
        private View memberGroup;
        
        /**
         * 成员头像
         */
        private ImageView headImg;
        
        /**
         * 删除图片
         */
        private ImageView deleteMember;
        
        /**
         * 群主添加好友的布局
         */
        private View addView;
        
        /**
         * 成员名称
         */
        private TextView nameText;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        //退出
            case R.id.left_button:
                onBackPressed();
                break;
            //编辑聊吧名称
            case R.id.chatbar_name_row:
                Intent intent = new Intent();
                intent.setAction(ChatbarNameModifyAction.ACTION_CHATBAR_NAME_MODIFY);
                intent.putExtra(ChatbarNameModifyAction.EXTRA_CHATBAR_NAME_OLD,
                        mGroupInfoModel.getGroupName());
                startActivityForResult(intent, REQUEST_MODIFY_CODE);
                break;
            // 退出聊吧或者关闭聊吧
            case R.id.bottom_button:
                showConfirmDialog(mIsOwner ? R.string.close_chatbar_info
                        : R.string.exit_chatbar_info,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                // 处理退出聊吧操作
                                // 如果非创建者则退出聊吧，如果是创建者则关闭聊吧
                                if (!mIsOwner)
                                {
                                    // 发送退出聊吧请求
                                    mGroupLogic.quitGroup(mGroupId);
                                }
                                else
                                {
                                    // 关闭聊吧请求
                                    mGroupLogic.destroyGroup(mGroupId, null);
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mGroupLogic = (GroupLogic) getLogicByInterfaceClass(IGroupLogic.class);
    }
    
    /**
     * 
     * 刷新界面<BR>
     * [功能详细描述]
     */
    private void refresh()
    {
        if (isPaused())
        {
            setNeedUpdate(true);
        }
        else
        {
            getDataForUI();
        }
    }
    
}
