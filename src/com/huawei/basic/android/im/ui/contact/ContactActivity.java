package com.huawei.basic.android.im.ui.contact;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionMessageType.ContactMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.logic.model.BaseContactModel;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.ui.basic.BaseContactUtil;
import com.huawei.basic.android.im.ui.basic.QuickActivity;
import com.huawei.basic.android.im.ui.basic.QuickAdapter;
import com.huawei.basic.android.im.utils.Match;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 通讯录页面<BR>
 * [功能详细描述]
 * @author 邵培培
 * @version [RCS Client V100R001C03, 2012-2-15] 
 */
public class ContactActivity extends QuickActivity implements
        OnItemClickListener
{
    /**
     * 删除好友
     */
    public static final int MENU_DELETE_FRIEND = 1;
    
    /**
     * debug TAG
     */
    private static final String TAG = "ContactActivity";
    
    /**
     * IContactLogic的引用
     */
    private IContactLogic mContactLogic;
    
    /**
     * 通讯录列表List
     */
    private List<PhoneContactIndexModel> mContacts;
    
    /**
     * 通讯录列表的Adapter
     */
    private ContactListAdapter mAdapter;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.component_quick);
        // 进度条展示
        showProgressDialog(R.string.connecting);
        // 需要退出菜单
        mAdapter = new ContactListAdapter();
        // 获取展示数据
        mContactLogic.getPhoneContactData();
        mContactLogic.registerContactsObserver();
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
     * 
     * handleStateMessage<BR>
     * [功能详细描述]
     * @param msg msg
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            // 获取页面展示数据message
            case ContactMessageType.GET_ALL_CONTACT_LIST:
                Logger.e(TAG, "=========GET_ALL_CONTACT_LIST============");
                // 获取所有联系人，在listview中展示
                // 不判断msg.obj是否为null,因为用户清空联系人,列表也需相应改变
                mContacts = (List<PhoneContactIndexModel>) msg.obj;
                initView();
                break;
        }
        super.handleStateMessage(msg);
    }
    
    private void initView()
    {
        updateView(mContacts);
        // 注册ContextMenu监听,用于响应长按事件
        registerForContextMenu(getListView());
        
        getListView().setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> vParent, View vItem,
                    int iItemIndex, long lItemId)
            {
                PhoneContactIndexModel contactBean = (PhoneContactIndexModel) mAdapter.getDisplayList()
                        .get(iItemIndex);
                
                //打开通讯录详情页面
                Intent intent = new Intent();
                intent.setAction(ContactDetailAction.ACTION);
                // 好友编号
                String hitalkId = contactBean.getContactUserId();
                // 通讯录ID
                String contactId = contactBean.getContactLUID();
                // 联系人类型:通讯录、Hitalk用户
                int modeType = StringUtil.isNullOrEmpty(contactBean.getContactUserId()) ? ContactDetailAction.LOCAL_CONTACT
                        : ContactDetailAction.HITALK_CONTACT;
                // 加载传递数据
                intent.putExtra(ContactDetailAction.BUNDLE_CONTACT_MODE,
                        modeType);
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                        hitalkId);
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_LOCAL_ID,
                        contactId);
                Logger.i(TAG, "hitalkId:" + hitalkId + " && localId:"
                        + contactId);
                startActivity(intent);
            }
        });
        // 关闭进度条
        closeProgressDialog();
    }
    
    @Override
    protected int getQuickBarResId()
    {
        return R.drawable.quick_bar_has_search;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        // 获取触发位置
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        // 长按弹出框内容
        if (null != mAdapter.getDisplayList()
                && (mAdapter.getDisplayList().get(position) instanceof PhoneContactIndexModel))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0, MENU_DELETE_FRIEND, 0, R.string.delete_conversation);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean onContextItemSelected(MenuItem item)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        final PhoneContactIndexModel phoneContactIndexModel = (PhoneContactIndexModel) mAdapter.getDisplayList()
                .get(position);
        final String contactId = phoneContactIndexModel.getContactLUID();
        String displayNameString = phoneContactIndexModel.getDisplayName();
        if (null != mAdapter.getDisplayList()
                && (mAdapter.getDisplayList().get(position) instanceof PhoneContactIndexModel))
        {
            showPromptDialog(String.format(getResources().getString(R.string.delete_contact_warning),
                    displayNameString),
                    new android.content.DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mContactLogic.deleteContactById(contactId);
                            mContacts.remove(phoneContactIndexModel);
                            updateView(mContacts);
                        }
                    },
                    true);
        }
        return super.onContextItemSelected(item);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        super.hideInputWindow(mAdapter.getSearchEditText());
        super.onResume();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        mContactLogic.unRegisterContactsObserver();
        super.onDestroy();
    }
    
    /**
     * 
     * 列表排序<BR>
     * 对列表进行排序以及插入A-Z等快捷索引的String，生成用于Adapter进行处理的list<BR>
     * @param contactList BaseContactModel列表
     * @return 用于Adapter进行处理的list
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Object> generateDisplayList(List<?> contactList)
    {
        return BaseContactUtil.contactListForDisplay((List<? extends BaseContactModel>) contactList);
    }
    
    /**
     * 获取Adapter<BR>
     * @return QuickAdapter
     * @see com.huawei.basic.android.im.ui.basic.QuickActivity#getQuickAdapter()
     */
    @Override
    protected QuickAdapter getQuickAdapter()
    {
        return mAdapter;
    }
    
    /**
     *  联系人按字母顺序排序的adapter
     * @author 邵培培
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    private class ContactListAdapter extends QuickAdapter
    {
        /**
         * 
         * 获取当前item的view<BR>
         * [功能详细描述]
         * 
         * @param position item位置
         * @param convertView item view
         * @param parent item parent
         * @return view 返回需要的view
         * @see android.widget.Adapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        @Override
        public View getItemView(int position, View convertView, ViewGroup parent)
        {
            final PhoneContactIndexModel contactBean = (PhoneContactIndexModel) getDisplayList().get(position);
            
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = LinearLayout.inflate(getBelongedActivity(),
                        R.layout.contacts_list_row,
                        null);
                holder = new ViewHolder();
                convertView.findViewById(R.id.child);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.phoneView = (TextView) convertView.findViewById(R.id.phone_num);
                holder.addStatus = (TextView) convertView.findViewById(R.id.add_status);
                
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(contactBean.getDisplayName());
            
            // 判断是否HiTalk
            if (contactBean.isHiTalk())
            {
                holder.addStatus.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.addStatus.setVisibility(View.GONE);
            }
            // 判断是否有手机号码，有就显示HiTalk标记
            if (null != contactBean.getPhoneNumbers()
                    && contactBean.getPhoneNumbers().size() > 0)
            {
                // 页面展示时,展示第一个电话就行
                holder.phoneView.setText(contactBean.getPhoneNumbers()
                        .get(0)
                        .get(0));
            }
            else
            {
                holder.phoneView.setText(null);
            }
            
            return convertView;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatched(Object obj, String key)
        {
            if (obj instanceof PhoneContactIndexModel)
            {
                // 可匹配通讯录中的姓名和简拼
                PhoneContactIndexModel contact = (PhoneContactIndexModel) obj;
                List<String> values = new ArrayList<String>();
                // 号码匹配
                if (null != contact.getPhoneNumbers())
                {
                    for (List<String> number : contact.getPhoneNumbers())
                    {
                        // 判断联系人号码存在
                        if (!StringUtil.isNullOrEmpty(number.get(0)))
                        {
                            values.add(number.get(0));
                        }
                    }
                }
                // 名称匹配
                if (!StringUtil.isNullOrEmpty(contact.getDisplayName()))
                {
                    values.add(contact.getDisplayName());
                }
                // 拼音匹配
                if (!StringUtil.isNullOrEmpty(contact.getSpellName()))
                {
                    values.add(contact.getSpellName());
                }
                // 拼音简称匹配
                if (!StringUtil.isNullOrEmpty(contact.getInitialName()))
                {
                    values.add(contact.getInitialName());
                }
                
                int size = values.size();
                String[] strs = new String[size];
                
                for (int i = 0; i < size; i++)
                {
                    strs[i] = values.get(i);
                }
                
                return Match.match(key, strs);
            }
            return false;
        }
        
    }
    
    /**
     * 
     *  存放view对象<BR>
     * [功能详细描述]
     * @author 邵培培
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    private static class ViewHolder
    {
        private TextView name;
        
        private TextView phoneView;
        
        private TextView addStatus;
        
    }
    
    /**
     * 初始化逻辑接口<BR>
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mContactLogic = (IContactLogic) getLogicByInterfaceClass(IContactLogic.class);
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
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        
    }
    
}
