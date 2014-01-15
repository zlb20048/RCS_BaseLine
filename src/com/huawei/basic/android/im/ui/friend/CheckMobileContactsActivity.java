/*
 * 文件名: CheckMobileContacts.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 25, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionAction.InputReasonAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.ContactMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.logic.friend.IFriendHelperLogic;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.logic.model.BaseContactModel;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.ui.basic.BaseContactUtil;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.ui.basic.QuickActivity;
import com.huawei.basic.android.im.ui.basic.QuickAdapter;
import com.huawei.basic.android.im.ui.contact.InviteUtil;

/**
 * 查看通讯录好友<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 25, 2012] 
 */
public class CheckMobileContactsActivity extends QuickActivity
{
    private static final String TAG = "CheckMobileContactsActivity";
    
    private static final int REQ_FOR_INPUT_REASON = 1;
    
    private ContactsMobileAdapter mListAdapter;
    
    private IContactLogic mContactLogic;
    
    private TextView mTitleTx;
    
    private Button mBackBtn;
    
    private PhotoLoader mPhotoLoader;
    
    private IFriendHelperLogic mFriendHelperLogic;
    
    private IFriendLogic mFriendLogic;
    
    private ArrayList<PhoneContactIndexModel> mPhoneContactList;
    
    private PhoneContactIndexModel mCurrentContactIndexModel;
    
    private HashMap<String, Boolean> mMap = new HashMap<String, Boolean>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_mobile_contacts_list);
        initView();
        
        mPhotoLoader = new PhotoLoader(this.getApplicationContext(),
                R.drawable.default_contact_icon, 52, 52,
                PhotoLoader.SOURCE_TYPE_CONTACT, null);
        
        super.showProgressDialog(R.string.connecting);
        //获取列表
        mContactLogic.getPhoneContactData();
    }
    
    private void initView()
    {
        mListAdapter = new ContactsMobileAdapter();
        mTitleTx = (TextView) findViewById(R.id.title);
        mTitleTx.setText(R.string.check_mobile_contacts);
        mBackBtn = (Button) findViewById(R.id.left_button);
        mBackBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        updateView(new ArrayList<PhoneContactIndexModel>());
        
        getListView().setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> vParent, View vItem,
                    int iItemIndex, long lItemId)
            {
                PhoneContactIndexModel contactBean = null;
                Object obj = mListAdapter.getDisplayList().get(iItemIndex);
                
                if (obj instanceof PhoneContactIndexModel)
                {
                    contactBean = (PhoneContactIndexModel) obj;
                }
                else
                {
                    return;
                }
                
                //打开通讯录详情页面
                Intent intent = new Intent();
                intent.setAction(ContactDetailAction.ACTION);
                int mode = ContactDetailAction.LOCAL_CONTACT;
                String hitalkId = contactBean.getContactUserId();
                String localId = contactBean.getContactLUID();
                if (null != contactBean.getContactUserId())
                {
                    mode = ContactDetailAction.HITALK_CONTACT;
                }
                Logger.i(TAG, "mode:" + mode + "hitalkId:" + hitalkId
                        + "localId:" + localId);
                intent.putExtra(ContactDetailAction.BUNDLE_CONTACT_MODE, mode);
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID,
                        hitalkId);
                intent.putExtra(ContactDetailAction.BUNDLE_FRIEND_LOCAL_ID,
                        localId);
                startActivity(intent);
            }
        });
    }
    
    /**
     * ContactsMobileAdapter<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, May 9, 2012]
     */
    class ContactsMobileAdapter extends QuickAdapter
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public View getItemView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            final PhoneContactIndexModel phoneContactBean = (PhoneContactIndexModel) getDisplayList().get(position);
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) CheckMobileContactsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.check_mobile_contacts_row,
                        null);
                holder = new ViewHolder();
                holder.mImage = (ImageView) convertView.findViewById(R.id.head);
                holder.mNameView = (TextView) convertView.findViewById(R.id.name);
                holder.mPhoneNumberView = (TextView) convertView.findViewById(R.id.signature);
                holder.mPhoneNumberView.setVisibility(View.VISIBLE);
                holder.mAddView = (ImageView) convertView.findViewById(R.id.add_image);
                holder.mInviteButton = (Button) convertView.findViewById(R.id.invite_button);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            
            holder.mNameView.setText(phoneContactBean.getDisplayName());
            mPhotoLoader.loadPhoto(holder.mImage, phoneContactBean.getPhotoId());
            holder.mInviteButton.setText(R.string.invite);
            holder.mInviteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //邀请好友
                    InviteUtil.invite(CheckMobileContactsActivity.this,
                            mContactLogic,
                            phoneContactBean,
                            FusionConfig.getInstance()
                                    .getAasResult()
                                    .getUserID());
                }
            });
            holder.mInviteButton.setFocusable(false);
            
            holder.mAddView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //发送加好友请求
                    addFriendAction(phoneContactBean);
                }
            });
            
            if (null != phoneContactBean.getPhoneNumbers()
                    && phoneContactBean.getPhoneNumbers().size() >= 1)
            {
                holder.mPhoneNumberView.setText(phoneContactBean.getPhoneNumbers()
                        .get(0)
                        .get(0));
            }
            if (phoneContactBean.isHiTalk())
            {
                holder.mAddView.setVisibility(View.VISIBLE);
                holder.mInviteButton.setVisibility(View.GONE);
            }
            else
            {
                holder.mAddView.setVisibility(View.GONE);
                holder.mInviteButton.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMatched(Object obj, String key)
        {
            return false;
        }
    }
    
    /**
     * ViewHolder<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 26, 2012]
     */
    private static class ViewHolder
    {
        private ImageView mImage;
        
        private TextView mNameView;
        
        private TextView mPhoneNumberView;
        
        private ImageView mAddView;
        
        private Button mInviteButton;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mContactLogic = (IContactLogic) super.getLogicByInterfaceClass(IContactLogic.class);
        mFriendHelperLogic = (IFriendHelperLogic) super.getLogicByInterfaceClass(IFriendHelperLogic.class);
        mFriendLogic = (IFriendLogic) super.getLogicByInterfaceClass(IFriendLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        super.handleStateMessage(msg);
        switch (msg.what)
        {
            case ContactMessageType.GET_ALL_CONTACT_LIST:
            {
                mPhoneContactList = (ArrayList<PhoneContactIndexModel>) msg.obj;
                update();
                super.closeProgressDialog();
                break;
            }
            case FriendMessageType.CONTACT_INFO_INSERT:
            {
                update();
                break;
            }
            case FriendMessageType.CONTACT_INFO_DELETE:
            {
                update();
                break;
            }
            case FriendMessageType.INVITE_FRIEND_MESSAGE:
            {
                //展示toast
                showToast((String) msg.obj);
                break;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected QuickAdapter getQuickAdapter()
    {
        return mListAdapter;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasSearch()
    {
        return false;
    }
    
    private void update()
    {
        mMap.clear();
        List<PhoneContactIndexModel> listWithoutFriend = new ArrayList<PhoneContactIndexModel>();
        if (null != mPhoneContactList)
        {
            for (PhoneContactIndexModel model : mPhoneContactList)
            {
                if (!mFriendLogic.isFriendExist(model.getContactSysId()))
                {
                    listWithoutFriend.add(model);
                }
            }
        }
        updateView(listWithoutFriend);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Object> generateDisplayList(List<?> contactList)
    {
        return BaseContactUtil.contactListForDisplay((List<? extends BaseContactModel>) contactList);
    }
    
    private void addFriendAction(PhoneContactIndexModel phoneContactBean)
    {
        // 对方设置不允许加好友
        int friendPrivacy = phoneContactBean.getAddFriendPrivacy();
        if (PhoneContactIndexModel.ADDFRIENDPRIVACY_ALLOW_ALL == friendPrivacy
                || PhoneContactIndexModel.ADDFRIENDPRIVACY_ALLOW_CONTACT == friendPrivacy)
        {
            if (null == mMap.get(phoneContactBean.getContactUserId())
                    || !mMap.get(phoneContactBean.getContactUserId()))
            {
                mMap.put(phoneContactBean.getContactUserId(), true);
                mFriendHelperLogic.addFriend(phoneContactBean.getContactUserId(),
                        "",
                        "",
                        "");
            }
        }
        // 对方被加好友需要验证
        else if (PhoneContactIndexModel.ADDFRIENDPRIVACY_NEED_CONFIRM == friendPrivacy)
        {
            mCurrentContactIndexModel = phoneContactBean;
            // 跳转到输入验证理由界面
            Intent intent = new Intent();
            intent.setAction(InputReasonAction.ACTION);
            intent.putExtra(InputReasonAction.EXTRA_MODE,
                    InputReasonAction.MODE_REASON);
            startActivityForResult(intent, REQ_FOR_INPUT_REASON);
        }
        else
        {
            showToast(this.getString(R.string.add_friend_not_allowed));
        }
    }
    
    /**
     * startActivityForResult回调结果<BR>
     * @param requestCode 请求码
     * @param resultCode 返回码
     * @param data 传递的参数
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ_FOR_INPUT_REASON)
        {
            // 输验证信息界面反馈数据
            if (resultCode == RESULT_OK && null != data)
            {
                // 加好友附带请求信息
                String operateResult = data.getStringExtra(InputReasonAction.OPERATE_RESULT);
                // 发送加好友请求
                mFriendHelperLogic.addFriend(mCurrentContactIndexModel.getContactUserId(),
                        "",
                        operateResult,
                        "");
            }
        }
    }
    
    /**
     * 配置信息发生变化<BR>
     * @param newConfig 新的配置信息
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Logger.e(TAG, "onConfigurationChanged ->>> " + newConfig.locale);
        resetView();
        super.onConfigurationChanged(newConfig);
    }
    
    /**
     * 由于配置信息发生变化，重新刷新ui<BR>
     */
    private void resetView()
    {
        if (null != mTitleTx)
        {
            mTitleTx.setText(R.string.check_mobile_contacts);
        }
        if (null != mBackBtn)
        {
            mBackBtn.setText(R.string.back);
        }
        if (null != mListAdapter)
        {
            mListAdapter.notifyDataSetChanged();
        }
    }
}
