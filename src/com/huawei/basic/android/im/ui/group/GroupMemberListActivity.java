/*
 * 文件名: GroupMemberListActivity1.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 群成员列表页面
 * 创建人: fengdai
 * 创建时间:2012-3-13
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionAction.GroupMemberListAction;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;

/**
 * 群成员列表页面<BR>
 * [功能详细描述]
 * @author fengdai
 * @version [RCS Client V100R001C03, 2012-3-13] 
 */
public class GroupMemberListActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * DEBUG TAG
     */
    private static final String TAG = "GroupMemberListActiviy";
    
    /**
     * 一次获取群成员的数目
     */
    private static final int PAGE_SIZE = 200;
    
    /**
     * 跳转到选择好友界面添加成员标识
     */
    private static final int REQUEST_ADD_CODE = 0x00000001;
    
    /**
     * 标示菜单itemid的常量
     */
    private static final int MENU_DELETE_MEMBER = 0;
    
    /**
     * 获取群成员的页面id
     */
    private int mPageId = 1;
    
    private String mGroupId;
    
    private GroupMemberListAdapter mGroupMemberListAdapter;
    
    private ListView mListView;
    
    private IGroupLogic mGroupLogic;
    
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
        setContentView(R.layout.group_member_list);
        //头像下载器
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                52, 52, PhotoLoader.SOURCE_TYPE_FRIEND, null);
        mGroupId = getIntent().getStringExtra(GroupMemberListAction.EXTRA_GROUP_ID);
        
        Logger.d(TAG, "===groupId" + mGroupId);
        initView();
        mGroupMemberListAdapter = new GroupMemberListAdapter();
        mListView.setAdapter(mGroupMemberListAdapter);
        getDataForUI();
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id)
            {
                GroupMemberModel gmm = (GroupMemberModel) mGroupMemberListAdapter.getDataSrc()
                        .get(position);
                Intent intent = new Intent(
                        FusionAction.ContactDetailAction.ACTION);
                intent.putExtra(ContactDetailAction.BUNDLE_CONTACT_MODE,
                        ContactDetailAction.HITALK_CONTACT);
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                        gmm.getMemberUserId());
                startActivity(intent);
                
            }
        });
        mListView.setOnScrollListener(new OnScrollListener()
        {
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
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
            }
        });
        
    }
    
    /**
     * 
     * 初始化界面控件<BR>
     * [功能详细描述]
     */
    private void initView()
    {
        Button backButton = (Button) findViewById(R.id.left_button);
        backButton.setOnClickListener(this);
        View refreshRow = findViewById(R.id.right_layout);
        refreshRow.setOnClickListener(this);
        ImageView refreshButton = (ImageView) findViewById(R.id.right_button);
        refreshButton.setBackgroundResource(R.drawable.refresh);
        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.view_group_member));
        View addRow = findViewById(R.id.add_row);
        View addMemberLayout = findViewById(R.id.add_group_member);
        addMemberLayout.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.group_member_list);
        //如果不是群主，隐藏加人的视图
        if (!mGroupLogic.isOwner(mGroupId))
        {
            addRow.setVisibility(View.GONE);
        }
        else
        {
            //是群主，设置长按监听
            registerForContextMenu(mListView);
        }
    }
    
    private void getDataForUI()
    {
        List<GroupMemberModel> list = mGroupLogic.getMemberListFromDB(mGroupId);
        mGroupLogic.sortMember(list);
        mGroupMemberListAdapter.setData(list);
        mGroupMemberListAdapter.notifyDataSetChanged();
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
    protected void onDestroy()
    {
        super.onDestroy();
        mPhotoLoader.stop();
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
     * 
     * 成员list显示的adaper<BR>
     * [功能详细描述]
     * @author fengdai
     * @version [RCS Client V100R001C03, Mar 13, 2012]
     */
    private class GroupMemberListAdapter extends BaseListAdapter
    {
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(GroupMemberListActivity.this,
                        R.layout.myfriend_friend_items,
                        null);
                holder = new ViewHolder();
                holder.photo = (ImageView) convertView.findViewById(R.id.photo);
                holder.displayName = (TextView) convertView.findViewById(R.id.display_name);
                holder.signature = (TextView) convertView.findViewById(R.id.friend_signature);
                convertView.findViewById(R.id.friend_item)
                        .setVisibility(View.GONE);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            
            final GroupMemberModel gmm = (GroupMemberModel) getDataSrc().get(position);
            holder.displayName.setText(gmm.getMemberNick());
            holder.signature.setText(gmm.getMemberDesc());
            
            //加载头像数据
            mPhotoLoader.loadPhoto(holder.photo, gmm.getMemberFaceUrl());
            
            return convertView;
        }
        
    }
    
    /**
     * 
     * 缓存view<BR>
     * [功能详细描述]
     * @author fengdai
     * @version [RCS Client V100R001C03, Mar 13, 2012]
     */
    private class ViewHolder
    {
        /**
         * 成员头像
         */
        private ImageView photo;
        
        /**
         * 成员昵称
         */
        private TextView displayName;
        
        /**
         * 成员签名
         */
        private TextView signature;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            case R.id.right_layout:
                mGroupLogic.getMemberListFromXmpp(mGroupId, mPageId, PAGE_SIZE);
                showProgressDialog(R.string.refreshing);
                break;
            case R.id.add_group_member:
                Intent intentAdd = new Intent(
                        FusionAction.ChooseMemberAction.ACTION);
                intentAdd.putExtra(FusionAction.ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                        FusionAction.ChooseMemberAction.TYPE.ADD_GROUP_MEMBER);
                intentAdd.putExtra(FusionAction.ChooseMemberAction.EXTRA_GROUP_ID,
                        mGroupId);
                startActivityForResult(intentAdd, REQUEST_ADD_CODE);
                break;
            default:
                break;
        
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        if (info.position != 0)
        {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle(R.string.operation);
            menu.add(0, MENU_DELETE_MEMBER, 0, R.string.delete_group_member);
        }
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
        
        if (item.getItemId() == MENU_DELETE_MEMBER)
        {
            final GroupMemberModel gmm = (GroupMemberModel) mGroupMemberListAdapter.getItem(info.position);
            mGroupLogic.removeMember(gmm.getMemberId(),
                    mGroupId,
                    GroupMessageType.REMOVE_MEMBER_FROM_GROUP);
        }
        
        return true;
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
        String[] savedIds = intent.getExtras()
                .getStringArray(FusionAction.ChooseMemberAction.RESULT_CHOOSED_USER_ID_LIST);
        if (null == savedIds || 0 == savedIds.length)
        {
            return;
        }
        
        if (REQUEST_ADD_CODE == requestCode)
        {
            mGroupLogic.inviteMember(savedIds,
                    mGroupId,
                    GroupMessageType.INVITE_MEMBER_FROM_GROUP);
        }
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
        //获取群成员信息成功
            case GroupMessageType.GET_MEMBER_LIST_SUCCESS:
                refresh();
                closeProgressDialog();
                showToast(R.string.refresh_success);
                break;
            //删除群成员成功
            case GroupMessageType.REMOVE_MEMBER_SUCCESS_FROM_GROUP:
                showToast(R.string.delete_success);
                break;
            //删除群成员失败
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
            //有成员退出
            case GroupMessageType.MEMBER_REMOVED_FROM_GROUP:
                refresh();
                break;
            //被踢出群
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
            //有成员更改昵称
            case GroupMessageType.MEMBER_NICKNAME_CHANGED:
                if (((String) msg.obj).equals(mGroupId))
                {
                    refresh();
                }
                break;
            //有新成员加入
            case GroupMessageType.MEMBER_ADDED_TO_GROUP:
                if (((String) msg.obj).equals(mGroupId))
                {
                    refresh();
                }
                break;
            //邀请成员失败
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
            //获取成员列表失败
            case GroupMessageType.GET_MEMBER_LIST_FAILED:
                closeProgressDialog();
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                break;
            //邀请成功
            case GroupMessageType.INVITE_MEMBER_SUCCESS_FROM_GROUP:
                showToast(R.string.invite_success);
                break;
            //群被解散
            case GroupMessageType.GROUP_DESTROYED_SUCCESS:
                if (((String) msg.obj).equals(mGroupId))
                {
                    showOnlyConfirmDialog(R.string.group_destroyed_success,
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
        super.handleStateMessage(msg);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mGroupLogic = (IGroupLogic) getLogicByInterfaceClass(IGroupLogic.class);
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
