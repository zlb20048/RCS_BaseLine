/*
 * 文件名: MyFriendsActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: Lidan
 * 创建时间:2012-2-12
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionMessageType.ContactDetailsMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendHelperMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.plugin.core.BasePlugin;
import com.huawei.basic.android.im.logic.adapter.http.FaceManager;
import com.huawei.basic.android.im.logic.friend.IFriendHelperLogic;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ContactSectionModel;
import com.huawei.basic.android.im.logic.plugin.IPluginLogic;
import com.huawei.basic.android.im.ui.basic.BaseContactUtil;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.ui.basic.QuickActivity;
import com.huawei.basic.android.im.ui.basic.QuickAdapter;
import com.huawei.basic.android.im.utils.Match;
import com.huawei.basic.android.im.utils.PluginStringUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 好友显示界面<BR>
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-12] 
 */
public class MyFriendsActivity extends QuickActivity
{
    /**
     * 删除好友
     */
    public static final int MENU_DELETE_FRIEND = 1;
    
    /**
     * TAG
     */
    private static final String TAG = "MyFriendsActivity";
    
    /**
     * 好友逻辑对象
     */
    private IFriendLogic mFriendLogic = null;
    
    /**
     * 找朋友小助手逻辑处理
     */
    private IFriendHelperLogic mFriendHelperLogic = null;
    
    /**
     * 插件逻辑
     */
    private IPluginLogic mPluginLogic = null;
    
    /**
     * 系统插件列表
     */
    private List<BasePlugin> mSystemPluginList;
    
    /**
     * 好友列表Adapter
     */
    private ContactInfoAdapter mContactInfoAdapter;
    
    /**
     * 批量读取头像 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 分组列表
     */
    private ArrayList<ContactInfoModel> mContactInfoList;
    
    /**
     * 是否在好友列表展示插件
     */
    private boolean mShowOnContactsList;
    
    /**
     * onPause方法 Activity的生命周期
     * 关闭软键盘
     * @see android.app.ActivityGroup#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        //关闭键盘  
        mPhotoLoader.pause();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.component_quick);
        
        //是否显示
        mShowOnContactsList = mPluginLogic.getShowPluginsOnContacts();
        
        //初始化系统组件
        initSystemPluginList();
        
        //头像下载器
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                52, 52, PhotoLoader.SOURCE_TYPE_FRIEND, null);
        
        //初始化界面
        initView();
        
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
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        Logger.d(TAG, "handleStateMessage >>" + msg.what);
        switch (msg.what)
        {
            case FriendMessageType.RESPONSE_ERROR:
            {
                showToast(msg.obj.toString());
                break;
            }
            case FriendMessageType.REQUEST_FOR_CONTACT_LIST:
            {
                updateView((ArrayList<ContactSectionModel>) msg.obj);
                break;
            }
            case FriendHelperMessageType.BE_DELETED:
            case FriendHelperMessageType.FRIENDHELPER_PRESENCE:
            case FriendHelperMessageType.DELETE_FRIEND_SUCCESS:
            case ContactDetailsMessageType.UPDATE_FRIEND_MEMO:
            {
                updateView(mFriendLogic.getAllContactListFromDb());
                break;
            }
            case FriendMessageType.SHOW_ON_CONTACTS_LIST:
            {
                mShowOnContactsList = (Boolean) msg.obj;
                updateView(mContactInfoList);
                break;
            }
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 初始化view<BR>
     */
    private void initView()
    {
        //联系人Adapter
        mContactInfoAdapter = new ContactInfoAdapter();
        
        //因为每次进入都联网，导致屏幕有段时间是白屏，所以先刷新界面
        mContactInfoList = mFriendLogic.getAllContactListFromDb();
        updateView(mContactInfoList);
        
        getListView().setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> vParent, View vItem,
                    int iItemIndex, long lItemId)
            {
                //主要针对1091号bug单,monkey测试,虽然不知道为什么，这样可以规避那个log打出来的bug
                List<Object> list = mContactInfoAdapter.getDisplayList();
                if (null != list && list.size() > iItemIndex)
                {
                    clickItem(mContactInfoAdapter.getDisplayList()
                            .get(iItemIndex));
                }
            }
        });
        registerForContextMenu(getListView());
        
        //好友列表滑动式不暂停加载头像
        getListView().setOnScrollListener(new OnScrollListener()
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
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        if (null != mContactInfoAdapter.getDisplayList()
                && (mContactInfoAdapter.getDisplayList().get(position) instanceof ContactInfoModel))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0, MENU_DELETE_FRIEND, 0, R.string.del_friend);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean onContextItemSelected(MenuItem item)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        if (null != mContactInfoAdapter.getDisplayList()
                && (mContactInfoAdapter.getDisplayList().get(position) instanceof ContactInfoModel))
        {
            ContactInfoModel contactInfoModel = (ContactInfoModel) mContactInfoAdapter.getDisplayList()
                    .get(position);
            final String friendUserIdString = contactInfoModel.getFriendUserId();
            String displayNameString = contactInfoModel.getDisplayName();
            showPromptDialog(String.format(getResources().getString(R.string.delete_friend_warning),
                    displayNameString),
                    new android.content.DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mFriendHelperLogic.deleteFriend(friendUserIdString);
                        }
                    },
                    true);
        }
        return super.onContextItemSelected(item);
    }
    
    /**
     * listview item 点击处理
     * 列表点击事件处理<BR>
     * @param obj obj
     * @return true
     */
    public boolean clickItem(Object obj)
    {
        if (obj instanceof BasePlugin)
        {
            final BasePlugin sysPlugin = (BasePlugin) obj;
            sysPlugin.start();
        }
        else if (obj instanceof ContactInfoModel)
        {
            ContactInfoModel model = (ContactInfoModel) obj;
            //跳转到好友详情界面
            Intent intent = new Intent(ContactDetailAction.ACTION);
            intent.putExtra(ContactDetailAction.BUNDLE_CONTACT_MODE,
                    ContactDetailAction.FRIEND_CONTACT);
            intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                    model.getFriendUserId());
            intent.putExtra(ContactDetailAction.BUNDLE_IS_FRIEND, true);
            startActivity(intent);
        }
        
        return true;
    }
    
    /**
     * 
     * 我的好友列表Adapter<BR>
     * @author Lidan
     * @version [RCS Client V100R001C03, 2012-2-12]
     */
    private class ContactInfoAdapter extends QuickAdapter
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public View getItemView(int position, View convertView, ViewGroup parent)
        {
            return MyFriendsActivity.this.getFriendItemView(getDisplayList().get(position),
                    convertView,
                    parent);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatched(Object obj, String key)
        {
            if (obj instanceof ContactInfoModel)
            {
                ContactInfoModel contact = (ContactInfoModel) obj;
                return Match.match(key,
                        contact.getDisplayName(),
                        contact.getInitialName(),
                        contact.getSpellName(),
                        contact.getFriendUserId());
            }
            else if (obj instanceof BasePlugin)
            {
                BasePlugin sysPlugin = (BasePlugin) obj;
                return Match.match(key, sysPlugin.getName());
            }
            return false;
        }
    }
    
    /**
     * 用于list view中好友条目的显示<BR>
     * @param obj 好友对象或者系统插件对象
     * @param convertView View
     * @param parent ViewGroup
     * @return convertView
     */
    private View getFriendItemView(Object obj, View convertView,
            ViewGroup parent)
    {
        // 获取FriendViewHolder
        ContactInfoViewHolder friendViewHolder;
        if (convertView == null)
        {
            convertView = RelativeLayout.inflate(MyFriendsActivity.this,
                    R.layout.myfriend_friend_items,
                    null);
            friendViewHolder = new ContactInfoViewHolder();
            friendViewHolder.itemView = convertView.findViewById(R.id.item);
            friendViewHolder.userNameTv = (TextView) convertView.findViewById(R.id.user_name);
            friendViewHolder.idTv = (TextView) convertView.findViewById(R.id.friend_id);
            friendViewHolder.photoIv = (ImageView) convertView.findViewById(R.id.photo);
            friendViewHolder.displayNameTv = (TextView) convertView.findViewById(R.id.display_name);
            friendViewHolder.signatureTv = (TextView) convertView.findViewById(R.id.friend_signature);
            friendViewHolder.signatureTv.setSingleLine(true);
            friendViewHolder.idItemView = convertView.findViewById(R.id.friend_item);
            friendViewHolder.smsIv = (ImageView) convertView.findViewById(R.id.sms);
            friendViewHolder.smsIv.setVisibility(View.INVISIBLE);
            convertView.setTag(R.id.friend_id, friendViewHolder);
        }
        else
        {
            friendViewHolder = (ContactInfoViewHolder) convertView.getTag(R.id.friend_id);
        }
        if (obj instanceof BasePlugin)
        {
            final BasePlugin sysPlugin = (BasePlugin) obj;
            friendViewHolder.displayNameTv.setText(PluginStringUtil.getNameId(sysPlugin.getPluginId()));
            friendViewHolder.photoIv.setTag(PhotoLoader.NOT_AVAIABLE_VIEW);
            FaceManager.showFace(friendViewHolder.photoIv,
                    sysPlugin.getIconUrl(),
                    null,
                    R.drawable.icon,
                    50,
                    50);
            friendViewHolder.signatureTv.setVisibility(View.GONE);
            friendViewHolder.idItemView.setVisibility(View.GONE);
            friendViewHolder.userNameTv.setVisibility(View.GONE);
        }
        else
        {
            final ContactInfoModel contactInfo = (ContactInfoModel) obj;
            friendViewHolder.signatureTv.setVisibility(View.VISIBLE);
            friendViewHolder.idItemView.setVisibility(View.VISIBLE);
            friendViewHolder.userNameTv.setVisibility(View.VISIBLE);
            
            if (!StringUtil.isNullOrEmpty(contactInfo.getMemoName()))
            {
                friendViewHolder.displayNameTv.setText(contactInfo.getMemoName());
                friendViewHolder.userNameTv.setText("("
                        + contactInfo.getNickName() + ")");
            }
            else
            {
                friendViewHolder.displayNameTv.setText(contactInfo.getNickName());
                friendViewHolder.userNameTv.setText("");
            }
            
            friendViewHolder.signatureTv.setText(contactInfo.getSignature());
            friendViewHolder.idTv.setText(contactInfo.getFriendUserId());
            friendViewHolder.photoIv.setTag(null);
            //加载头像数据
            mPhotoLoader.loadPhoto(friendViewHolder.photoIv,
                    contactInfo.getFaceUrl());
        }
        return convertView;
    }
    
    /**
     * 
     * 好友ViewHolder<BR>
     * 好友或系统插件的组件都定义在这里
     * @author Lidan
     * @version [RCS Client V100R001C03, 2012-2-12]
     */
    private class ContactInfoViewHolder
    {
        /**
         * 整行(item)
         */
        @SuppressWarnings("unused")
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
         * id(friend_id)
         */
        private TextView idTv;
        
        /**
         * id与功能性图标（sms）等整个一列的view
         */
        private View idItemView;
        
        /**
         * 短免能力图标
         */
        private ImageView smsIv;
        
        /**
         * 展示用户名的地方
         */
        private TextView userNameTv;
        
    }
    
    /**
     * 
     * 更新UI，并且是唯一的入口，不可继承<BR>
     * 最原始的BaseContactModel列表
     * 
     * @param contactList 列表
     */
    protected void updateView(ArrayList<ContactInfoModel> contactList)
    {
        mContactInfoList = contactList;
        super.updateView(contactList);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mFriendLogic = (IFriendLogic) getLogicByInterfaceClass(IFriendLogic.class);
        mFriendHelperLogic = (IFriendHelperLogic) getLogicByInterfaceClass(IFriendHelperLogic.class);
        mPluginLogic = (IPluginLogic) getLogicByInterfaceClass(IPluginLogic.class);
    }
    
    /**
     * 初始化系统插件列表<BR>
     */
    private void initSystemPluginList()
    {
        mSystemPluginList = mPluginLogic.getPluginList();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        super.hideInputWindow(mContactInfoAdapter.getSearchEditText());
        mPhotoLoader.resume();
        super.onResume();
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected QuickAdapter getQuickAdapter()
    {
        return mContactInfoAdapter;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Object> generateDisplayList(List<?> contactInfoList)
    {
        @SuppressWarnings("unchecked")
        ArrayList<ContactInfoModel> contactInfoModelList = (ArrayList<ContactInfoModel>) contactInfoList;
        ArrayList<Object> contactListForDisplay = BaseContactUtil.contactListForDisplay(contactInfoModelList);
        if (contactListForDisplay == null)
        {
            contactListForDisplay = new ArrayList<Object>();
        }
        
        if (mShowOnContactsList)
        {
            if (!(null == mSystemPluginList || mSystemPluginList.size() < 1))
            {
                contactListForDisplay.addAll(0, mSystemPluginList);
                
                contactListForDisplay.add(0, getString(R.string.system_tools));
            }
        }
        return contactListForDisplay;
    }
    
}
