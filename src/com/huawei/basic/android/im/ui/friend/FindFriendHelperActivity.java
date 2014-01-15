/*
 * 文件名: FindFriendHelperActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 20, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionAction.GroupDetailAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.FriendHelperMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.notification.TextNotificationEntity;
import com.huawei.basic.android.im.logic.friend.IFriendHelperLogic;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 找朋友小助手界面<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Feb 20, 2012] 
 */
public class FindFriendHelperActivity extends BasicActivity
{
    /**
     * DEBUG TAG
     */
    private static final String TAG = "FindFriendHelperActivity";
    
    /**
     * 清空会话信息按钮
     */
    private static final int OPTION_MENU_CLEAR = 1;
    
    /**
     * 长按按钮
     */
    private static final int CONTEXT_MENU_DELETE_RECORD = 2;
    
    /**
     * 找朋友小助手 adapter
     */
    private FindFriendHelperAdapter mAdapter;
    
    /**
     * 找朋友小助手数据列表
     */
    private ListView mListView;
    
    /**
     * {@link IFriendHelperLogic}
     */
    private IFriendHelperLogic mFriendHelperLogic;
    
    /**
     * 上下文
     */
    private Context mContext;
    
    /**
     * 头像缓存
     * {@link PhotoLoader}
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 当前展示的最后一条记录
     */
    private int mCurrentLastItem = 0;
    
    /**
     * 当前的分页，第一页为 1
     */
    private int mCurrentPage = 1;
    
    /**
     * 群组逻辑接口
     * {@link IGroupLogic}
     */
    private IGroupLogic mGroupLogic;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friend_helper);
        mContext = this;
        //初始化头像加载器
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                52, 52, PhotoLoader.SOURCE_TYPE_FRIEND, null);
        //初始化界面
        initView();
        
        //清空会话中，找朋友小助手的未读信息
        mFriendHelperLogic.clearFriendManagerUnreadMessages();
    }
    
    /**
     * 初始化View<BR>
     */
    private void initView()
    {
        mListView = (ListView) findViewById(R.id.find_friend_helper_list);
        mAdapter = new FindFriendHelperAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.setData(mFriendHelperLogic.queryByPage(mCurrentPage));
        mAdapter.notifyDataSetChanged();
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
                //分页加载数据
                if (mCurrentLastItem == mAdapter.getCount())
                {
                    mCurrentPage++;
                    ArrayList<FriendManagerModel> list = mFriendHelperLogic.queryByPage(mCurrentPage);
                    if (null != list)
                    {
                        mAdapter.getFriendManagerList().addAll(list);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
                mCurrentLastItem = firstVisibleItem + visibleItemCount;
            }
        });
        //长按删除菜单
        registerForContextMenu(mListView);
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> praent, View view,
                    int position, long id)
            {
                FriendManagerModel model = (FriendManagerModel) mAdapter.getItem(position);
                if (model != null)
                {
                    switch (model.getSubService())
                    {
                        // 子业务类型：用户手动加的好友
                        case FriendManagerModel.SUBSERVICE_ADD_FRIEND:
                            // 用户加我为好友
                        case FriendManagerModel.SUBSERVICE_BE_ADD:
                            // 加好友成功
                        case FriendManagerModel.SUBSERVICE_FRIEND_COMMON:
                            // 群主受理待加入成员
                        case FriendManagerModel.SUBSERVICE_GROUP_WAITTING:
                            // 用户加入群，接收方为群主
                        case FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER:
                            openFriendDetails(model);
                            break;
                        // 申请加入群
                        case FriendManagerModel.SUBSERVICE_GROUP_APPLY:
                        case FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF:
                            // 用户收到群邀请
                        case FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY:
                            openGroupDetails(model);
                            break;
                    }
                }
            }
            
            /**
             * 进入好友详情<BR>
             * @param model 小助手对象
             */
            private void openFriendDetails(FriendManagerModel model)
            {
                Logger.d(TAG, "进入好友详情!");
                Intent intent = new Intent(ContactDetailAction.ACTION);
                //HiTalk ID
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                        model.getFriendUserId());
                //入口类型
                intent.putExtra(ContactDetailAction.BUNDLE_ENTRANCE_TYPE,
                        ContactDetailAction.TYPE_FRIEND_HELPER);
                // 小助手SubService
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_SERVICE,
                        model.getSubService());
                //不为空则传入分组ID
                if (null != model.getGroupId())
                {
                    intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_GROUP_ID,
                            model.getGroupId());
                }
                //跳转界面
                startActivity(intent);
            }
            
            /**
             * 进入群详情<BR>
             * @param model 小助手对象
             */
            private void openGroupDetails(FriendManagerModel model)
            {
                Logger.d(TAG, "进入群详情！");
                Intent intent = new Intent(
                        GroupDetailAction.ACTION_GROUP_DETAIL);
                //传入GroupId
                intent.putExtra(GroupDetailAction.EXTRA_GROUP_ID,
                        model.getGroupId());
                //跳转界面
                startActivity(intent);
            }
            
        });
        
        //标题栏
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText(R.string.find_friend_helper);
        textView.setVisibility(View.VISIBLE);
        
        //返回按钮
        findViewById(R.id.left_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logger.d(TAG, "onClick ------ > finish");
                finish();
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            case FriendHelperMessageType.FRIENDHELPER_LIST_CHANGED:
            {
                //先清空数据，然后重新加载
                mAdapter.getFriendManagerList().clear();
                mCurrentPage = 1;
                mPhotoLoader.clear();
                mAdapter.setData(mFriendHelperLogic.queryByPage(mCurrentPage));
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 初始化逻辑接口<BR>
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mFriendHelperLogic = (IFriendHelperLogic) this.getLogicByInterfaceClass(IFriendHelperLogic.class);
        mGroupLogic = (IGroupLogic) this.getLogicByInterfaceClass(IGroupLogic.class);
    }
    
    /**
     * 删除小助手记录 长按按钮<BR>
     * @param menu menu
     * @param v v
     * @param menuInfo  menuInfo
     * @see android.app.Activity#onCreateContextMenu
     * (android.view.ContextMenu, android.view.View, 
     * android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        menu.add(0, CONTEXT_MENU_DELETE_RECORD, 0, R.string.delete_this_record);
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * 长按时间处理<BR>
     * @param item item
     * @return 是否继续交予系统处理
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        
        if (item.getItemId() == CONTEXT_MENU_DELETE_RECORD)
        {
            if (info == null)
            {
                Logger.d(TAG, "onContextItemSelected ------------> 没有获取到数据!");
                super.onContextItemSelected(item);
            }
            if (null == mAdapter)
            {
                return super.onContextItemSelected(item);
            }
            FriendManagerModel friendManagerModel = (FriendManagerModel) mAdapter.getItem(info.position);
            
            ArrayList<FriendManagerModel> list = mAdapter.getFriendManagerList();
            if (!mFriendHelperLogic.deleteFriendManagerByFriendUserId(friendManagerModel.getFriendUserId(),
                    friendManagerModel.getSubService(),
                    friendManagerModel.getGroupId()))
            {
                showToast(R.string.delete_fail);
            }
            else
            {
                list.remove(friendManagerModel);
                mAdapter.setData(list);
                mAdapter.notifyDataSetChanged();
                showToast(R.string.delete_success);
            }
        }
        return super.onContextItemSelected(item);
    }
    
    /**
     * 找朋友小助手列表<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 20, 2012]
     */
    private class FindFriendHelperAdapter extends BaseAdapter
    {
        /**
         * DEBUG TAG
         */
        private static final String TAG = "FindFriendHelperAdapter";
        
        /**
         * 数据列表
         */
        private ArrayList<FriendManagerModel> mFriendManagerList;
        
        /**
         * Context
         */
        private Context mContext;
        
        /**
         * Adapter 构造方法
         */
        public FindFriendHelperAdapter(Context context)
        {
            this.mContext = context;
            mFriendManagerList = new ArrayList<FriendManagerModel>();
        }
        
        /**
         * 获取找朋友小助手列表<BR>
         * @return 找朋友小助手列表
         */
        public ArrayList<FriendManagerModel> getFriendManagerList()
        {
            return mFriendManagerList;
        }
        
        /**
         * 设置数据<BR>
         * @param list lists
         */
        public void setData(ArrayList<FriendManagerModel> list)
        {
            if (null != list)
            {
                mFriendManagerList.addAll(list);
            }
        }
        
        /**
         * 返回列表数据的数量<BR>
         * @return count
         */
        @Override
        public int getCount()
        {
            return mFriendManagerList != null ? mFriendManagerList.size() : 0;
        }
        
        /**
         * 获取某一条数据<BR>
         * @param position position
         * @return model
         */
        @Override
        public Object getItem(int position)
        {
            return mFriendManagerList == null ? null
                    : mFriendManagerList.get(position);
        }
        
        /**
         * 数据ID<BR>
         * @param position position
         * @return id
         */
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
        /**
         * 产生数据对象对应的View<BR>
         * @param position  position
         * @param convertView convertView
         * @param parent parent
         * @return view
         */
        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent)
        {
            ViewHolder viewHolder = null;
            final FriendManagerModel friendHelperBean = mFriendManagerList.get(position);
            if (null == convertView)
            {
                convertView = LinearLayout.inflate(mContext,
                        R.layout.find_friend_helper_list_row,
                        null);
                viewHolder = new ViewHolder();
                //                viewHolder.smalllogo = (ImageView) convertView.findViewById(R.id.small_logo_image);
                viewHolder.subserviceType = (TextView) convertView.findViewById(R.id.find_friend_helper_type);
                viewHolder.time = (TextView) convertView.findViewById(R.id.find_friend_helper_time);
                
                viewHolder.headImage = (ImageView) convertView.findViewById(R.id.find_friend_helper_image);
                viewHolder.nickName = (TextView) convertView.findViewById(R.id.find_friend_helper_nickname);
                viewHolder.status = (TextView) convertView.findViewById(R.id.find_friend_helper_status);
                viewHolder.groupname = (TextView) convertView.findViewById(R.id.find_friend_helper_groupname);
                viewHolder.helpergroup = (TextView) convertView.findViewById(R.id.find_friend_helper_group);
                viewHolder.addAgain = (Button) convertView.findViewById(R.id.add_friend_again_btn);
                viewHolder.addAgain.setText(R.string.friendmanager_add_again);
                viewHolder.reason = (TextView) convertView.findViewById(R.id.find_friend_helper_reason);
                viewHolder.groupNameBelow = (TextView) convertView.findViewById(R.id.find_friend_helper_groupname_below);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            //根据需求，昵称为空时显示 HitalkId 
            viewHolder.nickName.setText(StringUtil.isNullOrEmpty(friendHelperBean.getNickName()) ? friendHelperBean.getFriendUserId()
                    : friendHelperBean.getNickName());
            
            //群名为空时，显示群ID
            String displayGroupname = StringUtil.isNullOrEmpty(friendHelperBean.getGroupName()) ? friendHelperBean.getGroupId()
                    : friendHelperBean.getGroupName();
            viewHolder.groupname.setText(displayGroupname);
            viewHolder.groupNameBelow.setText(displayGroupname);
            
            //这里时间的显示方式可能会有变动
            viewHolder.time.setText(friendHelperBean.getOperateTime());
            
            //加载头像
            loadPhoto(friendHelperBean.getSubService(),
                    friendHelperBean.getFaceUrl(),
                    viewHolder.headImage);
            
            //根据不同的SubService显示不同UI
            switch (friendHelperBean.getSubService())
            {
                //添加别人为好友
                case FriendManagerModel.SUBSERVICE_ADD_FRIEND:
                {
                    viewHolder.groupname.setVisibility(View.GONE);
                    viewHolder.groupNameBelow.setVisibility(View.GONE);
                    viewHolder.helpergroup.setVisibility(View.GONE);
                    viewHolder.reason.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.VISIBLE);
                    viewHolder.subserviceType.setText(R.string.subservice_add_friend);
                    
                    // 设置加别人的图片
                    //                    viewHolder.smalllogo.setImageResource(R.drawable.ic_friend_add_other);
                    
                    switch (friendHelperBean.getStatus())
                    {
                        case FriendManagerModel.STATUS_AGREE:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                            viewHolder.status.setText(R.string.friendmanager_add_agree);
                            viewHolder.addAgain.setVisibility(View.GONE);
                            break;
                        case FriendManagerModel.STATUS_REFUSE:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_refuse_color));
                            viewHolder.status.setText(R.string.friendmanager_add_refuse);
                            viewHolder.addAgain.setVisibility(View.VISIBLE);
                            break;
                        case FriendManagerModel.STATUS_SENDDING:
                        case FriendManagerModel.STATUS_SEND_FAIL:
                        case FriendManagerModel.STATUS_WAITTING:
                            viewHolder.status.setText(R.string.friendmanager_add_wait);
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_waitting_color));
                            viewHolder.addAgain.setVisibility(View.VISIBLE);
                            viewHolder.status.setVisibility(View.VISIBLE);
                            break;
                        //下面这些都是暂时没用到的
                        case FriendManagerModel.STATUS_AGREE_SEND_SENDDING:
                        case FriendManagerModel.STATUS_REFUSE_SEND_SENDDING:
                        case FriendManagerModel.STATUS_AGREE_SEND_FAIL:
                        case FriendManagerModel.STATUS_REFUSE_SEND_FAIL:
                            break;
                        default:
                            break;
                    }
                    break;
                }
                    // 接收到加好友申请
                case FriendManagerModel.SUBSERVICE_BE_ADD:
                {
                    viewHolder.groupname.setVisibility(View.GONE);
                    viewHolder.groupNameBelow.setVisibility(View.GONE);
                    viewHolder.helpergroup.setVisibility(View.GONE);
                    viewHolder.addAgain.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.VISIBLE);
                    
                    // 设置为被加好友logo
                    //                    viewHolder.smalllogo.setBackgroundResource(R.drawable.ic_friend_add_other);
                    
                    viewHolder.subserviceType.setText(R.string.subservice_be_add);
                    switch (friendHelperBean.getStatus())
                    {
                        case FriendManagerModel.STATUS_AGREE:
                            viewHolder.reason.setVisibility(View.GONE);
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                            viewHolder.status.setText(R.string.friendmanager_be_add_agree);
                            break;
                        case FriendManagerModel.STATUS_REFUSE:
                            viewHolder.reason.setVisibility(View.GONE);
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_refuse_color));
                            viewHolder.status.setText(R.string.friendmanager_be_add_refuse);
                            break;
                        case FriendManagerModel.STATUS_SENDDING:
                        case FriendManagerModel.STATUS_SEND_FAIL:
                        case FriendManagerModel.STATUS_WAITTING:
                        {
                            // 有没有理由显示方式不一样
                            if (!StringUtil.isNullOrEmpty(friendHelperBean.getReason()))
                            {
                                viewHolder.reason.setVisibility(View.VISIBLE);
                                //TODO 需要做字符串转表情
                                viewHolder.reason.setText(friendHelperBean.getReason());
                                viewHolder.status.setText(R.string.friendmanager_auth_info);
                            }
                            else
                            {
                                viewHolder.reason.setVisibility(View.GONE);
                                viewHolder.status.setText(R.string.friendmanager_apply_for_adding_friend);
                            }
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_waitting_color));
                            break;
                        }
                        case FriendManagerModel.STATUS_AGREE_SEND_SENDDING:
                        case FriendManagerModel.STATUS_REFUSE_SEND_SENDDING:
                        case FriendManagerModel.STATUS_AGREE_SEND_FAIL:
                        case FriendManagerModel.STATUS_REFUSE_SEND_FAIL:
                            break;
                        default:
                            break;
                    }
                    break;
                    
                }
                    // 群成员申请加入群 设置图标为 加入群
                case FriendManagerModel.SUBSERVICE_GROUP_APPLY:
                {
                    //                    viewHolder.smalllogo.setBackgroundResource(R.drawable.ic_friend_add_group);
                    viewHolder.subserviceType.setText(R.string.subservice_group_apply);
                    viewHolder.groupname.setVisibility(View.VISIBLE);
                    viewHolder.groupNameBelow.setVisibility(View.GONE);
                    viewHolder.helpergroup.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.GONE);
                    viewHolder.reason.setVisibility(View.GONE);
                    switch (friendHelperBean.getStatus())
                    {
                        case FriendManagerModel.STATUS_AGREE:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                            viewHolder.status.setText(R.string.friendmanager_add_group_agree);
                            viewHolder.addAgain.setVisibility(View.GONE);
                            break;
                        case FriendManagerModel.STATUS_REFUSE:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_refuse_color));
                            viewHolder.status.setText(R.string.friendmanager_add_group_refuse);
                            viewHolder.addAgain.setVisibility(View.VISIBLE);
                            break;
                        case FriendManagerModel.STATUS_WAITTING:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_waitting_color));
                            viewHolder.status.setText(R.string.friendmanager_add_group_wait);
                            viewHolder.addAgain.setVisibility(View.VISIBLE);
                            break;
                        case FriendManagerModel.STATUS_SEND_FAIL:
                        case FriendManagerModel.STATUS_SENDDING:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_waitting_color));
                            viewHolder.status.setText(R.string.status_waitting);
                            viewHolder.addAgain.setVisibility(View.VISIBLE);
                            viewHolder.addAgain.setText(R.string.add_group_again);
                            break;
                        default:
                            break;
                    }
                    
                    break;
                }
                    // 申请加入我的群
                case FriendManagerModel.SUBSERVICE_GROUP_WAITTING:
                {
                    //                    viewHolder.smalllogo.setBackgroundResource(R.drawable.ic_friend_be_add_group);
                    viewHolder.subserviceType.setText(R.string.subservice_group_waitting);
                    viewHolder.addAgain.setVisibility(View.GONE);
                    viewHolder.groupname.setVisibility(View.VISIBLE);
                    viewHolder.groupNameBelow.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.VISIBLE);
                    viewHolder.helpergroup.setVisibility(View.VISIBLE);
                    switch (friendHelperBean.getStatus())
                    {
                        case FriendManagerModel.STATUS_AGREE:
                            viewHolder.reason.setVisibility(View.GONE);
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                            viewHolder.status.setText(R.string.friendmanager_be_add_group_agree);
                            break;
                        case FriendManagerModel.STATUS_REFUSE:
                            viewHolder.reason.setVisibility(View.GONE);
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_refuse_color));
                            viewHolder.status.setText(R.string.friendmanager_be_add_group_refuse);
                            break;
                        case FriendManagerModel.STATUS_WAITTING:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_waitting_color));
                            viewHolder.status.setText(R.string.friendmanager_auth_info);
                            viewHolder.reason.setVisibility(View.VISIBLE);
                            //TODO 表情处理,验证信息需要转支持表情
                            viewHolder.reason.setText(friendHelperBean.getReason());
                            break;
                        case FriendManagerModel.STATUS_SEND_FAIL:
                        case FriendManagerModel.STATUS_SENDDING:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_waitting_color));
                            viewHolder.status.setText(R.string.friendmanager_auth_info);
                            viewHolder.reason.setVisibility(View.VISIBLE);
                            viewHolder.reason.setText(friendHelperBean.getReason());
                            break;
                        default:
                            break;
                    }
                    break;
                }
                    // 沃友用户受到群邀请
                case FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY:
                {
                    //                    viewHolder.smalllogo.setBackgroundResource(R.drawable.ic_friend_group_default);
                    viewHolder.subserviceType.setText(R.string.subservice_group_be_invite);
                    viewHolder.addAgain.setVisibility(View.GONE);
                    viewHolder.groupname.setVisibility(View.VISIBLE);
                    viewHolder.groupNameBelow.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.GONE);
                    viewHolder.helpergroup.setVisibility(View.GONE);
                    viewHolder.status.setVisibility(View.VISIBLE);
                    switch (friendHelperBean.getStatus())
                    {
                        case FriendManagerModel.STATUS_AGREE:
                            viewHolder.reason.setVisibility(View.GONE);
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                            viewHolder.status.setText(R.string.friendmanager_invite_agree);
                            break;
                        case FriendManagerModel.STATUS_REFUSE:
                            viewHolder.reason.setVisibility(View.GONE);
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_refuse_color));
                            viewHolder.status.setText(R.string.friendmanager_invite_refuse);
                            break;
                        case FriendManagerModel.STATUS_SEND_FAIL:
                        case FriendManagerModel.STATUS_SENDDING:
                        case FriendManagerModel.STATUS_WAITTING:
                            viewHolder.status.setTextColor(getResources().getColor(R.color.status_waitting_color));
                            viewHolder.status.setText(R.string.friendmanager_invite_wait);
                            viewHolder.reason.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                    break;
                }
                    //邀请别人加入群
                case FriendManagerModel.SUBSERVICE_INVITE_REGISTER:
                {
                    //TODO 暂时不展示
                    break;
                }
                    // 群操作成功，接受者是群主,用于无法区分是组员申请加入的群的，
                    //还是被邀请加入群的
                case FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER:
                {
                    //                    viewHolder.smalllogo.setBackgroundResource(R.drawable.ic_friend_group_default);
                    viewHolder.subserviceType.setText(R.string.subservice_group_common);
                    viewHolder.addAgain.setVisibility(View.GONE);
                    viewHolder.groupname.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.VISIBLE);
                    viewHolder.helpergroup.setVisibility(View.GONE);
                    viewHolder.reason.setVisibility(View.GONE);
                    viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                    viewHolder.groupNameBelow.setVisibility(View.VISIBLE);
                    viewHolder.status.setText(mContext.getResources()
                            .getString(R.string.friendmanager_message_group_add_success));
                    break;
                }
                    // 群操作成功，接受者是组员，用于无法区分主动加入群组的和被邀请的
                case FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF:
                {
                    //                    viewHolder.smalllogo.setBackgroundResource(R.drawable.ic_friend_group_default);
                    viewHolder.subserviceType.setText(R.string.subservice_group_common);
                    viewHolder.addAgain.setVisibility(View.GONE);
                    viewHolder.groupname.setVisibility(View.VISIBLE);
                    viewHolder.nickName.setVisibility(View.GONE);
                    viewHolder.helpergroup.setVisibility(View.GONE);
                    viewHolder.reason.setVisibility(View.GONE);
                    viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                    viewHolder.groupNameBelow.setVisibility(View.GONE);
                    viewHolder.status.setText(mContext.getResources()
                            .getString(R.string.friendmanager_message_group_add_success_self));
                    break;
                }
                    // 好友操作成功,无法区分我加别人还是别人加我的
                case FriendManagerModel.SUBSERVICE_FRIEND_COMMON:
                {
                    //                    viewHolder.smalllogo.setBackgroundResource(R.drawable.ic_friend_default);
                    viewHolder.subserviceType.setText(R.string.subservice_friend_common);
                    viewHolder.addAgain.setVisibility(View.GONE);
                    viewHolder.groupname.setVisibility(View.VISIBLE);
                    viewHolder.nickName.setVisibility(View.VISIBLE);
                    viewHolder.helpergroup.setVisibility(View.GONE);
                    viewHolder.reason.setVisibility(View.GONE);
                    viewHolder.status.setTextColor(getResources().getColor(R.color.status_agree_color));
                    viewHolder.groupNameBelow.setVisibility(View.GONE);
                    viewHolder.status.setText(mContext.getResources()
                            .getString(R.string.friendmanager_add_agree));
                    break;
                }
                    
            }
            
            viewHolder.addAgain.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    FriendManagerModel model = (FriendManagerModel) getItem(position);
                    if (FriendManagerModel.SUBSERVICE_ADD_FRIEND == friendHelperBean.getSubService())
                    {
                        Logger.d(TAG, "onClick ------> 再次加好友!");
                        //调用XMPP 执行加好友命令
                        if (model != null)
                        {
                            mFriendHelperLogic.addFriend(model.getFriendUserId(),
                                    model.getNickName(),
                                    model.getReason(),
                                    model.getFaceUrl());
                        }
                    }
                    else if (FriendManagerModel.SUBSERVICE_GROUP_APPLY == friendHelperBean.getSubService())
                    {
                        Logger.d(TAG, "onClick ------> 再次加入群!");
                        GroupInfoModel gim = mGroupLogic.getGroupInfoModelFromDB(FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId(),
                                model.getGroupId());
                        if (null == gim)
                        {
                            gim = new GroupInfoModel();
                        }
                        gim.setGroupId(model.getGroupId());
                        mGroupLogic.joinGroup(model.getReason(), gim);
                    }
                }
            });
            return convertView;
        }
    }
    
    /**
     * ViewHolder 缓存View对象<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 20, 2012]
     */
    private static class ViewHolder
    {
        /**
         * 标记subservice的图标
         */
        //        private ImageView smalllogo;
        
        /**
         * 类型文字
         */
        private TextView subserviceType;
        
        /**
         * 时间
         */
        private TextView time;
        
        /**
         * 头像
         */
        private ImageView headImage;
        
        /**
         * 昵称
         */
        private TextView nickName;
        
        /**
         * 状态
         */
        private TextView status;
        
        /**
         * 理由
         */
        private TextView reason;
        
        /**
         * 群名称
         */
        private TextView groupname;
        
        /**
         * 群模块显示名称
         */
        private TextView helpergroup;
        
        /**
         * 再加一次
         */
        private Button addAgain;
        
        /**
         * 下面的群名
         */
        private TextView groupNameBelow;
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
        //发送广播清除通知栏
        Intent intent = new Intent(
                TextNotificationEntity.NOTIFICAITON_ACTION_IM_FRIENDHELPER);
        this.sendBroadcast(intent);
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
        //清空会话中，找朋友小助手的未读信息
        mFriendHelperLogic.clearFriendManagerUnreadMessages();
        super.onDestroy();
    }
    
    /**
     * 加载头像，有群头像和好友头像，默认头像展示是不同的，
     * 所以当faceurl为空时，根据不同的subservice，展示不同的默认头像<BR>
     * @param subService 类型
     * @param faceUrl 头像url
     * @param imageView 展示头像的imageView
     */
    private void loadPhoto(int subService, String faceUrl, ImageView imageView)
    {
        if (null == faceUrl)
        {
            switch (subService)
            {
                case FriendManagerModel.SUBSERVICE_ADD_FRIEND:
                case FriendManagerModel.SUBSERVICE_BE_ADD:
                case FriendManagerModel.SUBSERVICE_FRIEND_COMMON:
                case FriendManagerModel.SUBSERVICE_GROUP_COMMON_OWNER:
                case FriendManagerModel.SUBSERVICE_GROUP_WAITTING:
                    imageView.setImageResource(R.drawable.default_contact_icon);
                    break;
                case FriendManagerModel.SUBSERVICE_GET_GROUP_APPLY:
                case FriendManagerModel.SUBSERVICE_GROUP_COMMON_SELF:
                case FriendManagerModel.SUBSERVICE_GROUP_APPLY:
                    imageView.setImageResource(R.drawable.default_group_head_icon);
                    break;
            }
        }
        else
        {
            mPhotoLoader.loadPhoto(imageView, faceUrl);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Logger.e(TAG, "onCreateOptionsMenu");
        menu.add(Menu.NONE,
                OPTION_MENU_CLEAR,
                Menu.NONE,
                R.string.clear_message_record);
        menu.getItem(0).setIcon(R.drawable.menu_exit_icon);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Logger.e(TAG, "onOptionsItemSelected");
        if (item.getItemId() == OPTION_MENU_CLEAR)
        {
            showPromptDialog(mContext.getResources()
                    .getString(R.string.clear_friend_helper_messages),
                    new android.content.DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //清空找朋友小助手信息
                            mFriendHelperLogic.clearFriendManagerMessages();
                        }
                    },
                    true);
        }
        return false;
    }
    
}
