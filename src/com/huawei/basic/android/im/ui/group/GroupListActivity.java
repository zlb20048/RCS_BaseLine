/*
 * 文件名: GroupListActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 我的群页面
 * 创建人: tjzhang
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.group;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.GroupCreateAction;
import com.huawei.basic.android.im.common.FusionAction.GroupListAction;
import com.huawei.basic.android.im.common.FusionAction.GroupSearchAction;
import com.huawei.basic.android.im.common.FusionAction.MultiChatAction;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;

/**
 * 我的群列表页面<BR>
 * [功能详细描述]
 * 
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-9]
 */
public class GroupListActivity extends BasicActivity implements OnClickListener
{
    
    private static final String TAG = "GroupListActivity";
    
    /**
     * 标示菜单itemid的常量
     */
    private static final int MENU_DESTROY_GROUP = 0;
    
    private static final int MENU_QIUT_GROUP = 1;
    
    private int mode;
    
    private GroupListAdapter mGroupListAdapter;
    
    private ListView mListView;
    
    private View mNoGroupTips;
    
    private Context mContext;
    
    private IGroupLogic mGroupLogic;
    
    /**
     * 批量读取头像 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 
     * Activity生命周期入口
     * 
     * @param savedInstanceState
     *            bundle
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group);
        mContext = this;
        mode = getIntent().getIntExtra(GroupListAction.EXTRA_GROUP_MODE,
                GroupListAction.GROUP_MODE);
        Logger.d(TAG, "===mode===" + mode);
        initView();
        
        //头像下载器
        mPhotoLoader = new PhotoLoader(this,
                R.drawable.default_group_head_icon, 52, 52,
                PhotoLoader.SOURCE_TYPE_GROUP, null);
        mGroupListAdapter = new GroupListAdapter();
        mListView.setAdapter(mGroupListAdapter);
        getDataForUI();
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> vParent, View vItem,
                    int iItemIndex, long lItemId)
            {
                GroupInfoModel model = (GroupInfoModel) mGroupListAdapter.getDataSrc()
                        .get(iItemIndex);
                Intent intent = new Intent(MultiChatAction.ACTION);
                intent.putExtra(MultiChatAction.EXTRA_GROUP_ID,
                        model.getGroupId());
                mContext.startActivity(intent);
            }
        });
        //列表滑动时暂停加载头像
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
        mGroupLogic.registerGroupInfoObserver();
        mGroupLogic.registerGroupmemberObserver();
        
    }
    
    private void initView()
    {
        Button backButton = (Button) findViewById(R.id.left_button);
        backButton.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        if (mode == GroupListAction.CHAT_BAR_MODE)
        {
            title.setText(R.string.chat_ba_title);
        }
        else
        {
            backButton.setText(R.string.group_create);
            title.setText(R.string.group_title);
            Button searchButton = (Button) findViewById(R.id.right_button);
            searchButton.setText(R.string.search_group_title);
            searchButton.setVisibility(View.VISIBLE);
            searchButton.setOnClickListener(this);
        }
        mListView = (ListView) findViewById(R.id.group_list);
        mNoGroupTips = findViewById(R.id.no_group_tips);
        registerForContextMenu(mListView);
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
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @see com.chinaunicom.woyou.ui.basic.BasicActivity#onResume()
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
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @see com.chinaunicom.woyou.ui.basic.BasicActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mGroupLogic.unregisterGroupInfoObserver();
        mGroupLogic.unregisterGroupmemberObserver();
        mPhotoLoader.stop();
    }
    
    private void getDataForUI()
    {
        //先从数据库中获取群组列表
        List<GroupInfoModel> list = mGroupLogic.getGroupListFormDB(mode);
        //然后对群组进行排序
        mGroupLogic.sortGroup(list);
        //生成UI要显示的list
        mGroupListAdapter.setData(addHeadToList(list));
        // 获取到数据，刷新
        mGroupListAdapter.notifyDataSetChanged();
    }
    
    /**
     * 
     * 生成放到adapter中展示的List<BR>
     * 把list根据角色生成不同的标题
     * 
     * @param list
     *            list
     * @return List<Object> 包含头的list
     */
    private List<Object> addHeadToList(List<GroupInfoModel> list)
    {
        List<Object> afterList = null;
        if (list != null)
        {
            afterList = new ArrayList<Object>();
            String ownhead = mode == GroupListAction.GROUP_MODE ? getResources().getString(R.string.group_owner_title)
                    : getResources().getString(R.string.chat_bar_owner_title);
            String memberHead = mode == GroupListAction.GROUP_MODE ? getResources().getString(R.string.group_member_title)
                    : getResources().getString(R.string.chat_bar_member_title);
            for (GroupInfoModel gim : list)
            {
                if (GroupMemberModel.AFFILIATION_OWNER.equals(gim.getAffiliation()))
                {
                    if (!afterList.contains(ownhead))
                    {
                        afterList.add(ownhead);
                    }
                }
                else
                {
                    if (!afterList.contains(memberHead))
                    {
                        afterList.add(memberHead);
                    }
                }
                afterList.add(gim);
            }
        }
        
        return afterList;
    }
    
    /**
     * 
     * 群成员列表的adapter<BR>
     * [功能详细描述]
     * 
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-9]
     */
    private class GroupListAdapter extends BaseListAdapter
    {
        
        private static final int VIEW_TYPE_COUNT = 2;
        
        private static final int TYPE_HEAD = 0;
        
        private static final int TYPE_CONTENT = 1;
        
        @Override
        public void notifyDataSetChanged()
        {
            super.notifyDataSetChanged();
            if (getCount() > 0)
            {
                mNoGroupTips.setVisibility(View.GONE);
            }
            else
            {
                mNoGroupTips.setVisibility(View.VISIBLE);
            }
        }
        
        @Override
        public int getItemViewType(int position)
        {
            return getDataSrc().get(position) instanceof String ? TYPE_HEAD
                    : TYPE_CONTENT;
        }
        
        @Override
        public int getViewTypeCount()
        {
            return VIEW_TYPE_COUNT;
        }
        
        @Override
        public boolean areAllItemsEnabled()
        {
            return false;
        }
        
        @Override
        public boolean isEnabled(int position)
        {
            return getDataSrc().get(position) instanceof String ? false : true;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (getDataSrc().get(position) instanceof String)
            {
                TextView initialTV;
                if (convertView == null)
                {
                    convertView = LinearLayout.inflate(mContext,
                            R.layout.component_contact_initial_letter_item,
                            null);
                    initialTV = (TextView) convertView.findViewById(R.id.group);
                    convertView.setTag(initialTV);
                }
                else
                {
                    initialTV = (TextView) convertView.getTag();
                }
                initialTV.setText((String) getDataSrc().get(position));
                
            }
            else
            {
                ViewHolder holder = null;
                if (convertView == null)
                {
                    convertView = LinearLayout.inflate(mContext,
                            R.layout.group_list_row,
                            null);
                    holder = new ViewHolder();
                    holder.headImage = (ImageView) convertView.findViewById(R.id.group_head);
                    holder.name = (TextView) convertView.findViewById(R.id.group_name);
                    holder.memberNumber = (TextView) convertView.findViewById(R.id.group_member_number);
                    //                    holder.unreadNumber = (TextView) convertView.findViewById(R.id.group_unread_number);
                    holder.description = (TextView) convertView.findViewById(R.id.group_description);
                    //                    holder.chatBatTime = (TextView) convertView.findViewById(R.id.group_popular_title);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (ViewHolder) convertView.getTag();
                }
                final GroupInfoModel model = (GroupInfoModel) getDataSrc().get(position);
                holder.name.setText(model.getGroupName());
                if (mode == GroupListAction.GROUP_MODE)
                {
                    holder.description.setText(model.getGroupDesc());
                    int memberCount = model.getMemberCount();
                    if (memberCount > 0)
                    {
                        holder.memberNumber.setVisibility(View.VISIBLE);
                        holder.memberNumber.setText(String.format(getResources().getString(R.string.count),
                                memberCount));
                    }
                    else
                    {
                        holder.memberNumber.setVisibility(View.GONE);
                    }
                    //加载头像数据
                    mPhotoLoader.loadPhoto(holder.headImage, model.getFaceUrl());
                }
                else
                {
                    holder.description.setVisibility(View.GONE);
                    //                    holder.chatBatTime.setText(MessageUtils.getFormatTime(mContext,
                    //                            model.getLastUpdate()));
                    //                    FaceManager.showCharBarFace(holder.headImage,
                    //                            model.getGroupId(),
                    //                            mContext,
                    //                            HEAD_WIDTH,
                    //                            HEAD_HEIGHT);
                    
                }
                //                int unRead = model.getUnReadMsg();
                //                if (unRead > 0)
                //                {
                //                    holder.unreadNumber.setVisibility(View.VISIBLE);
                //                    holder.unreadNumber.setText(String.valueOf(unRead));
                //                }
                //                else
                //                {
                //                    holder.unreadNumber.setVisibility(View.GONE);
                //                }
            }
            return convertView;
        }
    }
    
    /**
     * 
     * adapter 的holder<BR>
     * [功能详细描述]
     * 
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-9]
     */
    private static class ViewHolder
    {
        private ImageView headImage;
        
        private TextView name;
        
        private TextView memberNumber;
        
        //        private TextView unreadNumber;
        
        private TextView description;
        
        //        private TextView chatBatTime;
        
    }
    
    /**
     * 
     * 底端返回时不关闭页面
     * 
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param v
     *            v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                startActivity(new Intent(GroupCreateAction.ACTION_GROUP_CREATE));
                break;
            case R.id.right_button:
                Intent intent = new Intent(
                        GroupSearchAction.ACTION_GROUP_SEARCH);
                intent.putExtra(GroupSearchAction.EXTRA_MODE,
                        GroupSearchAction.MODE_SEARCH_GROUP_BY_CATEGROY);
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
    protected void handleStateMessage(Message msg)
    {
        int what = msg.what;
        switch (what)
        {
            case GroupMessageType.GROUP_MEMBER_DB_CHANGED:
            case GroupMessageType.GROUPINFO_DB_CHANGED:
                if (isPaused())
                {
                    setNeedUpdate(true);
                }
                else
                {
                    getDataForUI();
                }
                break;
            //解散群失败
            case GroupMessageType.GROUP_DESTROY_FAILED:
                if (null != msg.obj)
                {
                    showToast((String) msg.obj);
                }
                else
                {
                    showToast(R.string.group_destroy_failed);
                }
                break;
            //退出群失败
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
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 是否需要Menu菜单（退出程序）
     * 
     * @return 是否需要Menu菜单
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#isNeedMenu()
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
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        GroupInfoModel gim = (GroupInfoModel) mGroupListAdapter.getDataSrc()
                .get(info.position);
        //是这个群的群主
        if (mGroupLogic.isOwner(gim.getGroupId()))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0, MENU_DESTROY_GROUP, 0, R.string.destroy_group);
        }
        //不是群主
        else
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0, MENU_QIUT_GROUP, 0, R.string.quit_group);
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
        //如果是删除群
        if (item.getItemId() == MENU_DESTROY_GROUP)
        {
            final GroupInfoModel gim = (GroupInfoModel) mGroupListAdapter.getItem(info.position);
            showConfirmDialog(R.string.group_close_info,
                    new DialogInterface.OnClickListener()
                    {
                        
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mGroupLogic.destroyGroup(gim.getGroupId(), null);
                        }
                    });
        }
        //如果是退出群
        if (item.getItemId() == MENU_QIUT_GROUP)
        {
            final GroupInfoModel gim = (GroupInfoModel) mGroupListAdapter.getItem(info.position);
            showConfirmDialog(R.string.group_exit_info,
                    new DialogInterface.OnClickListener()
                    {
                        
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mGroupLogic.quitGroup(gim.getGroupId());
                        }
                    });
        }
        
        return true;
    }
}
