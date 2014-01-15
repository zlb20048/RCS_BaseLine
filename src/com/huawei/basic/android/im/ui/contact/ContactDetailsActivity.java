package com.huawei.basic.android.im.ui.contact;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.InputReasonAction;
import com.huawei.basic.android.im.common.FusionAction.ContactDetailAction;
import com.huawei.basic.android.im.common.FusionAction.PhotoAction;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.ContactDetailsMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.ContactMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendHelperMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.adapter.db.FaceThumbnailDbAdapter;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.logic.friend.IFriendHelperLogic;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.FriendManagerModel;
import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.ContactCodeUtil;
import com.huawei.basic.android.im.ui.voip.VoipBasicActivity;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 好友详情<BR>
 * 
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-2-11]
 */
public class ContactDetailsActivity extends VoipBasicActivity implements
        View.OnClickListener
{
    
    /**
     * 请求添加好友
     */
    private static final int REQ_FOR_INPUT_REASON = 100;
    
    /**
     * 定义TAG
     */
    private static final String TAG = "ContactDetailsActivity";
    
    /**
     * 防止频繁点击屏幕
     */
    private static final int TOUCH_SCREEN_DELAY = 1000;
    
    /**
     * 联系人类型：好友、HiTalk、通讯录
     */
    private int mContactType;
    
    /**
     * 记录上次点击时间
     */
    private long mLastTouchTime = 0l;
    
    /**
     * 好友详情数据封装类
     */
    private ContactInfoModel mContactInfoModel;
    
    /**
     * 找朋友小助手数据封装类
     */
    private FriendManagerModel mFriendManagerModel;
    
    /**
     * 小助手进入
     */
    private boolean isMyFriend;
    
    /**
     * 通讯录传入mContactId
     */
    private String mContactId;
    
    /**
     * 群小助手mGroupJid
     */
    private String mGroupJid;
    
    /**
     * 批量读取头像 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 子业务类型
     */
    private int mSubService;
    
    /**
     * 加群还是加好友标志
     */
    private int mFriendhelpSubserviceType;
    
    private int mFriendhelpSubserviceStatus;
    
    /**
     * title bar 相关控件
     */
    private Button mBackButton;
    
    private TextView mTitle;
    
    /**
     * 头像相关控件
     */
    private ImageView mHeadImage;
    
    private TextView mFriendName;
    
    private TextView mContactName;
    
    private TextView mFriendMemoName;
    
    private ImageView mSexImage;
    
    private View mHiTalkIDGroup;
    
    private View mBackupGroup;
    
    private View mContactGroup;
    
    private TextView mHiTalkView;
    
    /**
     * 验证信息
     */
    private TextView mFriendselfreason;
    
    private View mFriendselfreasonLayout;
    
    /**
     * 会话按钮
     */
    private View mTalkLayout;
    
    /**
     * 好友Button list
     */
    private View mFriendButtonGroup;
    
    private View mFriendDetails;
    
    private View mEditButton;
    
    private View mDelButton;
    
    private View mJoinBlackButton;
    
    /**
     * 陌生人button list
     */
    private View mNotFriendButtonGroup;
    
    private View mAddButtonGroup;
    
    private View mNotFriendDetails;
    
    /**
     * 管理请求button list
     */
    private View mFriendManageButtonGroup;
    
    private View mAgreeButton;
    
    private View mRefuseButton;
    
    private View mManageDetails;
    
    /**
     * 普通联系人的页面控件
     */
    private View mInviteFriend;
    
    private LinearLayout mContentView;
    
    /**
     * 好友的个人签名
     */
    private TextView mSelfSignature;
    
    private View mSignatureLayout;
    
    /**
     * 地区
     */
    private TextView mCountry;
    
    private TextView mCompany;
    
    private TextView mSchool;
    
    /**
     * 好友电话，email,姓名
     */
    private View mFriendPhoneLayout;
    
    private View mPhoneLine;
    
    private TextView mPhoneNumber;
    
    private View mPhoneDivider;
    
    private View mEmailLine;
    
    private TextView mEmailAdd;
    
    private ImageView mFriendPhonecall;
    
    /**
     * 好友电话备注 邮箱
     */
    private View mMemoLayout;
    
    private View mMemophoneLayout;
    
    private TextView mFriendMemophone;
    
    private View mMemoemailLayout;
    
    private TextView mFriendMemoemail;
    
    private ImageView mFriendmemoPhonecall;
    
    private ImageView mFriendmemoPhonesms;
    
    /**
     * 好友hiTalkID<Br>
     */
    private String mHiTalkID;
    
    /**
     * 逻辑功能实现
     */
    private IFriendLogic mFriendLogic = null;
    
    private IFriendHelperLogic mFriendHelperLogic = null;
    
    private IContactLogic mContactLogic = null;
    
    private IGroupLogic mGroupLogic = null;
    
    /**
     * 
     * 触发事件<BR>
     * 
     * @param v 点击的view
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        String number = null;
        long now = System.currentTimeMillis();
        //控制不能频繁点击屏幕
        if ((now - mLastTouchTime) < TOUCH_SCREEN_DELAY)
        {
            return;
        }
        mLastTouchTime = now;
        
        switch (v.getId())
        {
            // 点击头像
            case R.id.head_image:
                if (null == mContactId && null != mContactInfoModel)
                {
                    Logger.d(TAG, "展示大图");
                    openPhoto();
                }
                break;
            // 返回按钮
            case R.id.left_button:
                unregisterObserver();
                finish();
                break;
            
            // 好友操作按钮 Friend
            case R.id.friend_talk_layout:
                if (null != mContactInfoModel)
                {
                    Intent chatIntent = new Intent(
                            FusionAction.SingleChatAction.ACTION);
                    chatIntent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_NICK_NAME,
                            mContactInfoModel.getNickName());
                    chatIntent.putExtra(FusionAction.SingleChatAction.EXTRA_FRIEND_USER_ID,
                            mContactInfoModel.getFriendUserId());
                    startActivity(chatIntent);
                }
                break;
            // 添加备注
            case R.id.edit_friend_layout:
                editMemoDialog();
                break;
            // 删除好友
            case R.id.del_friend_button:
                deleteFriendAction();
                break;
            // 添加好友
            case R.id.add_button_group:
                addFriendAction();
                break;
            // 验证通过
            case R.id.agree_friend_layout:
                dealGroupApply(true);
                break;
            // 拒绝
            case R.id.refuse_friend_layout:
                dealGroupApply(false);
                unregisterObserver();
                finish();
                break;
            // 拨打电话
            case R.id.phone_number:
                number = mPhoneNumber.getText().toString();
                this.phoneCall(number);
                break;
            case R.id.friend_phone_call:
                number = mPhoneNumber.getText().toString();
                this.phoneCall(number);
                break;
            case R.id.friendmemo_phone_call:
                number = mFriendMemophone.getText().toString();
                this.phoneCall(number);
                break;
            case R.id.friend_memophone_layout:
                number = mFriendMemophone.getText().toString();
                this.phoneCall(number);
                break;
            // 发送email
            case R.id.email_line:
                number = mEmailAdd.getText().toString();
                phoneEmail(number);
                break;
            case R.id.friend_memoemail_layout:
                number = mFriendMemoemail.getText().toString();
                phoneEmail(number);
                break;
            // 邀请好友
            case R.id.invite_friends:
                doInvite();
                break;
            // 加入黑名单
            case R.id.join_blacklist_layout:
                Toast.makeText(this, "黑名单功能尚未提供，敬请期待！", Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }
    
    /**
     * 
     * 发起邀请好友<BR>
     */
    private void doInvite()
    {
        if (null == mContactId)
        {
            return;
        }
        // 获取联系人信息
        PhoneContactIndexModel phoneContactIndex = mContactLogic.getLocalContactProfile(mContactId);
        
        if (null != phoneContactIndex)
        {
            // 发起邀请请求
            InviteUtil.invite(this,
                    mContactLogic,
                    phoneContactIndex,
                    FusionConfig.getInstance().getAasResult().getUserID());
        }
    }
    
    /**
     * 
     * 群主处理群申请<BR>
     * @param flag flag
     */
    private void dealGroupApply(boolean flag)
    {
        // 群主受理待加入成员
        if (mFriendhelpSubserviceType == FriendManagerModel.SUBSERVICE_GROUP_WAITTING)
        {
            mGroupLogic.acceptJoin(mGroupJid, mHiTalkID, null);
        }
        else
        {
            mFriendHelperLogic.doAuth(mHiTalkID, flag);
        }
    }
    
    /**
     * 
     * 查看大图Activity<BR>
     * 
     */
    private void openPhoto()
    {
        FaceThumbnailModel ftm = FaceThumbnailDbAdapter.getInstance(this)
                .queryByFaceId(mContactInfoModel.getFriendUserId());
        
        Intent intent = new Intent();
        intent.setAction(PhotoAction.ACTION);
        if (null != ftm)
        {
            intent.putExtra(PhotoAction.PHOTO_LARGE, ftm.getFaceBytes());
        }
        intent.putExtra(PhotoAction.PHOTO_LARGE_URL,
                mContactInfoModel.getFaceUrl());
        startActivity(intent);
    }
    
    @Override
    protected boolean isPrivateHandler()
    {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 页面传递参数：联系人类型
        mContactType = getIntent().getIntExtra(ContactDetailAction.BUNDLE_CONTACT_MODE,
                ContactDetailAction.HITALK_CONTACT);
        // 页面传递参数：好友ID
        mHiTalkID = getIntent().getStringExtra(ContactDetailAction.BUNDLE_FRIEND_HITALK_ID);
        
        // 页面传递参数：通讯录contactId
        mContactId = getIntent().getStringExtra(ContactDetailAction.BUNDLE_FRIEND_LOCAL_ID);
        
        // 页面传递参数：群组ID
        mGroupJid = getIntent().getStringExtra(ContactDetailAction.BUNDLE_FRIEND_GROUP_ID);
        
        // 页面传递参数：SubService
        mSubService = getIntent().getIntExtra(ContactDetailAction.BUNDLE_FRIEND_SERVICE,
                FriendManagerModel.SUBSERVICE_GROUP_WAITTING);
        
        // 注册监听
        mFriendLogic.registerObserverByID(mHiTalkID);
        mFriendHelperLogic.registerObserverByID(mHiTalkID);
        
        // 数据库查询好友及小助手信息
        initFriendRelativeInfo();
        // 初始化界面UI
        initDetailUI();
    }
    
    /**
     * 
     * 通过 重载父类的handleStateMessage方法实现消息处理<BR>
     * [功能详细描述]
     * 
     * @param msg
     *            msg
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        ContactInfoModel contactInfoModel = null;
        
        // 服务器获取好友
        switch (msg.what)
        {
            // 详细信息返回
            case ContactDetailsMessageType.GET_MULTI_FRIENDS_DETAILS:
                Logger.i(TAG, "从服务器获取好友信息成功！");
                contactInfoModel = (ContactInfoModel) msg.obj;
                // 判断服务器端好友信息是否更改
                if (null != contactInfoModel)
                {
                    Logger.i(TAG,
                            "contactInfoModel : " + contactInfoModel.toString());
                    mContactInfoModel = contactInfoModel;
                    
                    // 刷新页面前通过数据库查询做好友判断
                    if (!isMyFriend)
                    {
                        isMyFriend = null != mFriendLogic.getContactInfoByFriendUserId(mHiTalkID);
                    }
                    // 刷新详情页面
                    setContentViewValue();
                }
                break;
            case ContactDetailsMessageType.GET_FRIENDS_DETAILS_FALSE:
                Logger.i(TAG, "从服务器获取好友信息失败！");
                Toast.makeText(this,
                        R.string.friend_detail_get_fail,
                        Toast.LENGTH_LONG).show();
                break;
            // 更新备注信息
            case ContactDetailsMessageType.UPDATE_FRIEND_MEMO:
                Logger.i(TAG, "更新备注信息！");
                contactInfoModel = (ContactInfoModel) msg.obj;
                if (null != contactInfoModel)
                {
                    // 需要把备注信息更新到mContactInfoModel去
                    mContactInfoModel.setMemoName(contactInfoModel.getMemoName());
                    mContactInfoModel.setMemoPhones(contactInfoModel.getMemoPhones());
                    mContactInfoModel.setMemoEmails(contactInfoModel.getMemoEmails());
                    // 刷新页面备注栏
                    setCommonViewValue();
                }
                break;
            // 验证通过，服务器成功
            case FriendHelperMessageType.REQUEST_DO_AUTH_SUCCESS:
                // 状态置为“好友”
                mContactType = ContactDetailAction.FRIEND_CONTACT;
                // 重新服务器获取好友信息
                updateContactDetails();
                break;
            // 删除好友成功
            case FriendHelperMessageType.DELETE_FRIEND_SUCCESS:
                Toast.makeText(this,
                        R.string.friend_del_success,
                        Toast.LENGTH_LONG).show();
                unregisterObserver();
                finish();
                break;
            // 被删好友
            case FriendHelperMessageType.BE_DELETED:
                if (mHiTalkID.equals(msg.obj))
                {
                    mContactType = ContactDetailAction.HITALK_CONTACT;
                    isMyFriend = false;
                    // 重新服务器获取好友信息:主要因为隐私策略(手机、邮箱等内容是否显示)是由服务器推送信息判断
                    updateContactDetails();
                    Toast.makeText(this,
                            R.string.friend_removed,
                            Toast.LENGTH_LONG).show();
                }
                break;
            // 加好友成功 刷新页面
            case FriendHelperMessageType.NEW_FRIEND_ADDED:
                if (mHiTalkID.equals(msg.obj))
                {
                    // 状态置为“好友”
                    mContactType = ContactDetailAction.FRIEND_CONTACT;
                    // 重新服务器获取好友信息
                    updateContactDetails();
                }
                break;
            // 监听到通讯录信息变更，刷新界面
            case ContactMessageType.GET_ALL_CONTACT_LIST:
                if (null == this.mContactId)
                {
                    break;
                }
                // 获取所有联系人，在listview中展示
                List<PhoneContactIndexModel> contacts = null;
                if (null != msg.obj)
                {
                    contacts = (List<PhoneContactIndexModel>) msg.obj;
                }
                getContactListForView(contacts);
                break;
            // 监听到小助手信息变更(插入、更新)，刷新界面
            case FriendHelperMessageType.FRIENDHELPER_CHANGED:
                if (null != mHiTalkID)
                {
                    // 重新数据库获取好友及小助手信息
                    initFriendRelativeInfo();
                    // 详情UI赋值
                    setContentViewValue();
                }
                break;
            // 监听到好友信息(插入、删除)，刷新界面
            case FriendMessageType.CONTACTINFO_CHANGED:
                if (null != mHiTalkID)
                {
                    // 重新数据库获取好友及小助手信息
                    initFriendRelativeInfo();
                    // 详情UI赋值
                    setContentViewValue();
                }
                break;
            // 拒绝好友请求命令执行失败
            case FriendMessageType.REFUSE_FRIEND_APPLY_FAIL:
                Toast.makeText(this, R.string.request_fail, Toast.LENGTH_LONG)
                        .show();
                break;
            case FriendMessageType.INVITE_FRIEND_MESSAGE:
                // 展示toast
                Toast.makeText(this, (String) msg.obj, Toast.LENGTH_SHORT)
                        .show();
                break;
            // 展示软键盘
            case ContactDetailsMessageType.SHOW_SOFT_INPUT:
                ((InputMethodManager) getSystemService(ContactDetailsActivity.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            default:
                break;
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ContactDetailAction.EDIT_REQUEST_CODE)
        {
            Logger.d(TAG, "onActivityResult ----> EDIT_REQUEST_CODE");
        }
        else if (requestCode == REQ_FOR_INPUT_REASON)
        {
            // 输验证信息界面反馈数据
            if (resultCode == RESULT_OK && null != data)
            {
                // 加好友附带请求信息
                String operateResult = data.getStringExtra(InputReasonAction.OPERATE_RESULT);
                // 发送加好友请求
                mFriendHelperLogic.addFriend(mHiTalkID,
                        mContactInfoModel.getNickName(),
                        operateResult,
                        mContactInfoModel.getFaceUrl());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mFriendLogic = (IFriendLogic) getLogicByInterfaceClass(IFriendLogic.class);
        mFriendHelperLogic = (IFriendHelperLogic) getLogicByInterfaceClass(IFriendHelperLogic.class);
        mContactLogic = (IContactLogic) getLogicByInterfaceClass(IContactLogic.class);
        mGroupLogic = (IGroupLogic) getLogicByInterfaceClass(IGroupLogic.class);
    }
    
    /**
     * onPause方法 Activity的生命周期
     * 关闭软键盘
     * @see android.app.ActivityGroup#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        // 关闭
        mPhotoLoader.pause();
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
    protected void onDestroy()
    {
        mPhotoLoader.stop();
        super.onDestroy();
    }
    
    /**
     * 
     * 初始化界面UI<BR>
     * 
     */
    private void initDetailUI()
    {
        // 查看通讯录详情
        if (mContactType == ContactDetailAction.LOCAL_CONTACT)
        {
            setContentView(R.layout.contact_baseline_local);
            
            // 头像下载器
            mPhotoLoader = new PhotoLoader(this,
                    R.drawable.default_contact_icon, 60, 60,
                    PhotoLoader.SOURCE_TYPE_CONTACT, null);
            
            // 初始化通讯录进入详情UI
            initContactLoaclDetails();
            
            // 通讯录进入详情UI界面赋值
            setLocalViewValue(mContactId);
        }
        else
        {
            // 头像下载器
            mPhotoLoader = new PhotoLoader(this,
                    R.drawable.default_contact_icon, 60, 60,
                    PhotoLoader.SOURCE_TYPE_FRIEND, null);
            
            // 如果是自己的账号，则跳转到个人资料页面
            if (mHiTalkID.equals(FusionConfig.getInstance()
                    .getAasResult()
                    .getUserID()))
            {
                Intent intent = new Intent();
                intent.setAction(SettingsAction.ACTION_ACTIVITY_PRIVATE_PROFILE_SETTING);
                startActivity(intent);
                unregisterObserver();
                finish();
                return;
            }
            
            setContentView(R.layout.contact_baseline);
            
            // 初始化详情UI
            initContentView();
            // 详情UI赋值
            setContentViewValue();
            // 服务器获取信息
            updateContactDetails();
        }
        
        // 初始化返回按钮
        mBackButton = (Button) findViewById(R.id.left_button);
        mBackButton.setOnClickListener(this);
    }
    
    /**
     * 
     * 数据库查询好友及小助手信息<BR>
     * [功能详细描述]
     */
    private void initFriendRelativeInfo()
    {
        // 查询数据库中已存好友信息
        ContactInfoModel contactInfoModel = mFriendLogic.getContactInfoByFriendUserId(mHiTalkID);
        
        mContactInfoModel = null == contactInfoModel ? mContactInfoModel
                : contactInfoModel;
        
        // 查询数据库中找朋友小助手信息
        mFriendManagerModel = mFriendHelperLogic.getFriendManagerFromDB(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId(),
                mSubService,
                mHiTalkID,
                mGroupJid);
        
        // 好友判断:根据找朋友小助手表状态与好友信息
        if (mFriendManagerModel != null
                && (mFriendManagerModel.getSubService() == FriendManagerModel.SUBSERVICE_BE_ADD || mFriendManagerModel.getSubService() == FriendManagerModel.SUBSERVICE_ADD_FRIEND)
                && mFriendManagerModel.getStatus() == FriendManagerModel.STATUS_AGREE)
        {
            isMyFriend = true;
        }
        else
        {
            isMyFriend = null != contactInfoModel;
        }
        
        if (null != mFriendManagerModel)
        {
            // 找朋友小助手：是申请加入群的好友才会传这个参数，其余都为默认
            mFriendhelpSubserviceType = mFriendManagerModel.getSubService();
            
            // 找朋友小助手状态:同意、拒绝
            mFriendhelpSubserviceStatus = mFriendManagerModel.getStatus();
        }
    }
    
    /**
     * 
     * 初始化通讯录进入UI<BR>
     * [功能详细描述]
     */
    private void initContactLoaclDetails()
    {
        initHeadView();
        mHiTalkIDGroup.setVisibility(View.GONE);
        mBackupGroup.setVisibility(View.GONE);
        mContactGroup.setVisibility(View.GONE);
        
        mInviteFriend = findViewById(R.id.invite_friends);
        mInviteFriend.setOnClickListener(this);
        mContentView = (LinearLayout) findViewById(R.id.content_view);
    }
    
    /**
     * 
     * 通讯录详情UI<BR>
     * 
     * [功能详细描述]
     */
    private void setLocalViewValue(String contactLocalId)
    {
        PhoneContactIndexModel phoneContactIndexModel = mContactLogic.getLocalContactProfile(contactLocalId);
        
        if (null == phoneContactIndexModel)
        {
            return;
        }
        
        //加载头像数据
        mPhotoLoader.loadPhoto(mHeadImage, phoneContactIndexModel.getPhotoId());
        // 赋值联系人名称
        mFriendName.setText(phoneContactIndexModel.getDisplayName());
        // 初始化界面前，先清空ContentView
        mContentView.removeAllViews();
        
        // 通讯录详情电话号码展示
        if (null != phoneContactIndexModel.getPhoneNumbers()
                && phoneContactIndexModel.getPhoneNumbers().size() > 0)
        {
            // 有手机号码，需要展示邀请按钮
            setLocalPhoneView(R.string.phone_1,
                    phoneContactIndexModel.getPhoneNumbers());
            mContentView.setVisibility(View.VISIBLE);
            mInviteFriend.setVisibility(View.VISIBLE);
        }
        
        // 通讯录详情 EmailAddrs展示控制
        if (null != phoneContactIndexModel.getEmailAddrs()
                && phoneContactIndexModel.getEmailAddrs().size() > 0)
        {
            setLocalEmail(R.string.email_1,
                    phoneContactIndexModel.getEmailAddrs());
            mContentView.setVisibility(View.VISIBLE);
        }
        
        // 邀请按钮展示控制
        if (null == mContactInfoModel
                && null != phoneContactIndexModel.getPhoneNumbers()
                && phoneContactIndexModel.getPhoneNumbers().size() > 0)
        {
            mInviteFriend.setVisibility(View.VISIBLE);
        }
        else
        {
            // 无手机号码，隐藏邀请按钮
            mInviteFriend.setVisibility(View.GONE);
        }
    }
    
    /**
     * 
     * 详情界面电话号码展示<BR>
     * 系统存在电话号码则展示，否则不展示
     * @param phoneId R文件对应ID
     * @param phones 数据对象
     */
    private void setLocalPhoneView(int phoneId, List<List<String>> phones)
    {
        if (null != phones && phones.size() > 0)
        {
            // 循环加载mContentView
            for (int i = 0; i < phones.size(); i++)
            {
                LinearLayout phoneLayout = (LinearLayout) View.inflate(this,
                        R.layout.component_detail_number,
                        null);
                TextView numberTitle = (TextView) phoneLayout.findViewById(R.id.number_title);
                TextView numberText = (TextView) phoneLayout.findViewById(R.id.number);
                ImageView phoneImag = (ImageView) phoneLayout.findViewById(R.id.phone_pointer);
                ImageView smsImag = (ImageView) phoneLayout.findViewById(R.id.sms_pointer);
                smsImag.setVisibility(View.GONE);
                // 设置展示类型
                String phoneType = phones.get(i).get(1);
                // 展示电话类型
                String phoneTitle = StringUtil.isNullOrEmpty(phoneType) ? getResources().getString(phoneId)
                        : ContactCodeUtil.getPhoneInfo(ContactDetailsActivity.this,
                                phoneType);
                numberTitle.setText(phoneTitle);
                // list.get(i)中包括电话号码、号码类型
                final String phoneNumber = StringUtil.getString(phones.get(i)
                        .get(0))
                        .replaceAll("(-|^\\+86)", "")
                        .trim();
                
                numberText.setText(phoneNumber);
                // 系统电话实现
                OnClickListener callListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        phoneCall(phoneNumber);
                    }
                };
                phoneImag.setOnClickListener(callListener);
                numberText.setOnClickListener(callListener);
                
                // 系统发短信实现
                OnClickListener smsListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        phoneSMS(phoneNumber);
                    }
                };
                smsImag.setOnClickListener(smsListener);
                
                mContentView.addView(phoneLayout);
            }
        }
        
    }
    
    /**
     * 
     * 通讯录详情 邮件信息展示<BR>
     * @param emailId 邮件地址R文件对象
     * @param emails 具体数据
     */
    private void setLocalEmail(int emailId, List<List<String>> emails)
    {
        if (null != emails && emails.size() > 0)
        {
            // 循环加载mContentView
            for (int i = 0; i < emails.size(); i++)
            {
                LinearLayout linearLayout = (LinearLayout) View.inflate(this,
                        R.layout.component_detail_number,
                        null);
                TextView numberTitle = (TextView) linearLayout.findViewById(R.id.number_title);
                ImageView phonePointer = (ImageView) linearLayout.findViewById(R.id.phone_pointer);
                ImageView smsPointer = (ImageView) linearLayout.findViewById(R.id.sms_pointer);
                smsPointer.setVisibility(View.GONE);
                
                TextView number = (TextView) linearLayout.findViewById(R.id.number);
                // 获取邮箱类型
                String emailType = emails.get(i).get(1);
                // 展示邮箱类型
                String emailTitle = StringUtil.isNullOrEmpty(emailType) ? getResources().getString(emailId)
                        : ContactCodeUtil.getEmailInfo(ContactDetailsActivity.this,
                                emailType);
                numberTitle.setText(emailTitle);
                
                // list.get(i)中包括email地址、email类型
                final String emailAdd = emails.get(i).get(0);
                number.setText(emailAdd);
                phonePointer.setImageResource(R.drawable.icon_massage);
                linearLayout.setOnClickListener(new View.OnClickListener()
                {
                    
                    @Override
                    public void onClick(View v)
                    {
                        phoneEmail(emailAdd);
                    }
                });
                mContentView.addView(linearLayout);
                
            }
        }
        
    }
    
    /**
     * 
     * 好友展示邮箱地址与电话<BR>
     * 是好友同时存在邮箱地址或电话则展示
     */
    private void setFriendViewValue()
    {
        String phoneNumber = null;
        String emailAddress = null;
        
        if (null != mContactInfoModel)
        {
            phoneNumber = mContactInfoModel.getPrimaryMobile();
            emailAddress = mContactInfoModel.getPrimaryEmail();
        }
        
        // 先判断手机号码和邮箱地址是否有一个不为空
        if (!StringUtil.isNullOrEmpty(phoneNumber)
                || !StringUtil.isNullOrEmpty(emailAddress))
        {
            mFriendPhoneLayout.setVisibility(View.VISIBLE);
            boolean hasNumber = false;
            boolean hasEmail = false;
            // 判断是否展示详情手机号码
            if (!StringUtil.isNullOrEmpty(phoneNumber))
            {
                mPhoneLine.setVisibility(View.VISIBLE);
                mPhoneNumber.setText(phoneNumber);
                hasNumber = true;
            }
            else
            {
                mPhoneLine.setVisibility(View.GONE);
            }
            // 判断是否展示详情电话
            if (!StringUtil.isNullOrEmpty(emailAddress))
            {
                mEmailLine.setVisibility(View.VISIBLE);
                mEmailAdd.setText(emailAddress);
                hasEmail = true;
            }
            else
            {
                mEmailLine.setVisibility(View.GONE);
            }
            if (!hasNumber)
            {
                mPhoneDivider.setVisibility(View.GONE);
                mEmailLine.setBackgroundResource(R.drawable.setting_item_bg_all);
            }
            else
            {
                if (hasEmail)
                {
                    mPhoneLine.setBackgroundResource(R.drawable.setting_item_bg_top);
                    mEmailLine.setBackgroundResource(R.drawable.setting_item_bg_bottom);
                }
                else
                {
                    mPhoneDivider.setVisibility(View.GONE);
                    mPhoneLine.setBackgroundResource(R.drawable.setting_item_bg_all);
                }
            }
        }
        else
        {
            mFriendPhoneLayout.setVisibility(View.GONE);
        }
        
    }
    
    /**
     * 需刷新模块UI控件
     */
    private void setCommonViewValue()
    {
        // 判空处理
        if (null == mContactInfoModel)
        {
            return;
        }
        // 头像名称模块初始化
        setHeadViewValue();
        
        int gender = mContactInfoModel.getGender();
        if (gender == 0)
        {
            mSexImage.setVisibility(View.GONE);
        }
        else
        {
            mSexImage.setVisibility(View.VISIBLE);
            if (gender == 1)
            {
                mSexImage.setImageResource(R.drawable.setting_female);
            }
            else
            {
                mSexImage.setImageResource(R.drawable.setting_male);
            }
        }
        
        if (!StringUtil.isNullOrEmpty(mContactInfoModel.getSignature()))
        {
            mSelfSignature.setVisibility(View.VISIBLE);
            mSignatureLayout.setVisibility(View.VISIBLE);
            mSelfSignature.setText(mContactInfoModel.getSignature());
        }
        else
        {
            mSignatureLayout.setVisibility(View.GONE);
            mSelfSignature.setVisibility(View.GONE);
        }
        String country = getCoutry(mContactInfoModel);
        mCountry.setText(country);
        mCompany.setText(mContactInfoModel.getCompany());
        mSchool.setText(mContactInfoModel.getSchool());
        
        //加载头像数据
        mPhotoLoader.loadPhoto(mHeadImage, mContactInfoModel.getFaceUrl());
    }
    
    /**
     * 好友详情名字模块展示
     * 
     * 1、通讯录 && 非HiTalk用户 ：通讯录联系人名称 2、通讯录 && HiTalk && 非好友： 第一行：HiTalk昵称 第二行：ID
     * 第三行：联系人姓名 3、通讯录 && 好友： 第一行：HiTalk昵称 第二行：ID 第三行：联系人姓名 第四行：备注名
     */
    private void setHeadViewValue()
    {
        if (isMyFriend)
        {
            mTitle.setText(R.string.friend_detail);
        }
        else
        {
            mTitle.setText(R.string.details);
        }
        // HiTalk昵称
        mFriendName.setText(mContactInfoModel.getNickName());
        // HiTalk编号
        mHiTalkView.setText(mContactInfoModel.getFriendUserId());
        // 联系人姓名
        mContactGroup.setVisibility(View.GONE);
        if (null != mContactId)
        {
            PhoneContactIndexModel phoneContactIndexModel = mContactLogic.getLocalContactProfile(mContactId);
            if (null != phoneContactIndexModel
                    && null != phoneContactIndexModel.getPhoneNumbers()
                    && phoneContactIndexModel.getPhoneNumbers().size() > 0)
            {
                mContactGroup.setVisibility(View.VISIBLE);
                mContactName.setText(null == phoneContactIndexModel.getDisplayName() ? ""
                        : phoneContactIndexModel.getDisplayName());
            }
        }
        
        mBackupGroup.setVisibility(View.GONE);
        if (!StringUtil.isNullOrEmpty(mContactInfoModel.getMemoName()))
        {
            mBackupGroup.setVisibility(View.VISIBLE);
            mFriendMemoName.setText(mContactInfoModel.getMemoName());
        }
        else
        {
            mBackupGroup.setVisibility(View.GONE);
        }
    }
    
    /**
     * 
     * 从服务器获取HiTalk信息<BR>
     */
    private void updateContactDetails()
    {
        mFriendLogic.updateContactDetails(mHiTalkID, mContactType);
        
    }
    
    /**
     * 
     * 获取到联系人信息变动，刷新界面<BR>
     * 
     * @param contacts
     *            联系人列表
     */
    private void getContactListForView(List<PhoneContactIndexModel> contacts)
    {
        // 通讯录进入详情UI界面赋值
        if (null != mContactId && null != contacts && contacts.size() > 0)
        {
            Logger.d(TAG, " === contactId:" + mContactId);
            
            for (PhoneContactIndexModel phoneContactIndexModel : contacts)
            {
                if (!mContactId.equals(phoneContactIndexModel.getContactLUID()))
                {
                    continue;
                }
                
                if (null != phoneContactIndexModel
                        && null != phoneContactIndexModel.getContactUserId())
                {
                    mContactType = ContactDetailAction.HITALK_CONTACT;
                    mHiTalkID = phoneContactIndexModel.getContactUserId();
                    initFriendRelativeInfo();
                }
                else
                {
                    // 判断通讯录信息变更前界面是否为通讯录界面
                    mContactType = ContactDetailAction.LOCAL_CONTACT;
                    mContactInfoModel = null;
                }
                initDetailUI();
                
                return;
            }
        }
        else
        {
            // 判断通讯录信息变更前界面是否为通讯录界面
            mContactType = ContactDetailAction.LOCAL_CONTACT;
            mContactInfoModel = null;
            initDetailUI();
        }
    }
    
    /**
     * 
     * 关闭监听<BR>
     * [功能详细描述]
     */
    private void unregisterObserver()
    {
        mFriendLogic.unRegisterObserverByID(mHiTalkID);
        mFriendHelperLogic.unRegisterObserverByID(mHiTalkID);
    }
    
    /**
     * 初始化头像/昵称模块View<BR>
     */
    private void initHeadView()
    {
        mHeadImage = (ImageView) findViewById(R.id.head_image);
        
        // 点击查看大图
        mHeadImage.setOnClickListener(this);
        
        // 第一行：HiTalk昵称
        mFriendName = (TextView) findViewById(R.id.friend_name);
        
        // 第二行：ID
        mHiTalkIDGroup = (View) findViewById(R.id.hitalk_id_group);
        mHiTalkView = (TextView) findViewById(R.id.hitalk_id);
        
        // 第三行：联系人姓名
        mContactGroup = (View) findViewById(R.id.contact_group);
        mContactName = (TextView) findViewById(R.id.contact_name);
        
        // 第四行：备注名
        mBackupGroup = (View) findViewById(R.id.backup_group);
        mFriendMemoName = (TextView) findViewById(R.id.backup_name);
    }
    
    /**
     * 初始化控件<BR>
     */
    private void initContentView()
    {
        // 初始化头像相关控件
        initHeadView();
        mSexImage = (ImageView) findViewById(R.id.sex_image);
        // 初始化title
        mTitle = (TextView) findViewById(R.id.title);
        mHiTalkView.setText(mHiTalkID);
        
        // 初始化验证信息layout
        mFriendselfreason = (TextView) findViewById(R.id.friend_self_reason);
        
        mFriendselfreasonLayout = (View) findViewById(R.id.friend_self_reason_layout);
        
        // 初始化会话按钮
        mTalkLayout = findViewById(R.id.friend_talk_layout);
        mTalkLayout.setOnClickListener(this);
        
        // 初始化好友Button list
        mFriendButtonGroup = findViewById(R.id.friend_button_group);
        mFriendDetails = findViewById(R.id.friend_details_layout);
        mEditButton = findViewById(R.id.edit_friend_layout);
        mDelButton = findViewById(R.id.del_friend_button);
        mJoinBlackButton = findViewById(R.id.join_blacklist_layout);
        mFriendDetails.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
        mDelButton.setOnClickListener(this);
        mJoinBlackButton.setOnClickListener(this);
        
        // 初始化陌生人button list
        mNotFriendButtonGroup = findViewById(R.id.not_friend_button_group);
        mAddButtonGroup = findViewById(R.id.add_button_group);
        mNotFriendDetails = findViewById(R.id.not_friend_details_layout);
        mAddButtonGroup.setOnClickListener(this);
        mNotFriendDetails.setOnClickListener(this);
        
        // 初始化管理请求button list
        mFriendManageButtonGroup = findViewById(R.id.friend_manage_button_group);
        mAgreeButton = findViewById(R.id.agree_friend_layout);
        mRefuseButton = findViewById(R.id.refuse_friend_layout);
        mManageDetails = findViewById(R.id.friend_manage_details_layout);
        mAgreeButton.setOnClickListener(this);
        mRefuseButton.setOnClickListener(this);
        mManageDetails.setOnClickListener(this);
        
        // 初始化手机号码和邮箱
        mFriendPhoneLayout = findViewById(R.id.phone_email_layout);
        mPhoneLine = findViewById(R.id.phone_line);
        mPhoneNumber = (TextView) findViewById(R.id.phone_number);
        mPhoneDivider = findViewById(R.id.phone_number_divider);
        mEmailLine = findViewById(R.id.email_line);
        mEmailAdd = (TextView) findViewById(R.id.email_add);
        mFriendPhonecall = (ImageView) findViewById(R.id.friend_phone_call);
        mPhoneNumber.setOnClickListener(this);
        mFriendPhonecall.setOnClickListener(this);
        mPhoneLine.setOnClickListener(this);
        mEmailLine.setOnClickListener(this);
        mContentView = (LinearLayout) findViewById(R.id.content_view);
        
        // 初始化备注信息
        mMemoLayout = findViewById(R.id.memo_layout);
        mMemophoneLayout = findViewById(R.id.friend_memophone_layout);
        mFriendMemophone = (TextView) findViewById(R.id.friend_memophone);
        mMemoemailLayout = findViewById(R.id.friend_memoemail_layout);
        mFriendMemoemail = (TextView) findViewById(R.id.friend_memoemail);
        
        mFriendmemoPhonecall = (ImageView) findViewById(R.id.friendmemo_phone_call);
        mFriendmemoPhonesms = (ImageView) findViewById(R.id.friendmemo_phone_sms);
        mFriendmemoPhonecall.setOnClickListener(this);
        mFriendmemoPhonesms.setOnClickListener(this);
        mMemophoneLayout.setOnClickListener(this);
        mMemoemailLayout.setOnClickListener(this);
        
        // 初始化个性签名layout
        mSelfSignature = (TextView) findViewById(R.id.friend_self_signature);
        mSignatureLayout = (View) findViewById(R.id.friend_self_signature_layout);
        
        // 地区
        mCountry = (TextView) findViewById(R.id.country);
        mCompany = (TextView) findViewById(R.id.company);
        mSchool = (TextView) findViewById(R.id.school);
    }
    
    /**
     * 控件展示<BR>
     */
    private void setContentViewValue()
    {
        // 页面传入参数：判断是否好友
        if (isMyFriend
                && (null == mGroupJid || (null != mGroupJid && (mFriendhelpSubserviceStatus == FriendManagerModel.STATUS_REFUSE || mFriendhelpSubserviceStatus == FriendManagerModel.STATUS_AGREE))))
        {
            mContactType = ContactDetailAction.FRIEND_CONTACT;
            mFriendselfreason.setVisibility(View.GONE);
            mNotFriendButtonGroup.setVisibility(View.GONE);
            mFriendManageButtonGroup.setVisibility(View.GONE);
            mFriendPhoneLayout.setVisibility(View.GONE);
            mTalkLayout.setVisibility(View.VISIBLE);
            mFriendButtonGroup.setVisibility(View.VISIBLE);
            mFriendselfreasonLayout.setVisibility(View.GONE);
            
            setCommonViewValue();
            // 邮箱地址及电话号码展示
            setFriendViewValue();
        }
        else
        {
            Logger.i(TAG, "=====不是好友关系，进行小助手判断======");
            mFriendPhoneLayout.setVisibility(View.GONE);
            mFriendButtonGroup.setVisibility(View.GONE);
            mMemoLayout.setVisibility(View.GONE);
            mTalkLayout.setVisibility(View.GONE);
            mBackupGroup.setVisibility(View.GONE);
            
            // 验证信息：被加好友或者群主受理待加入成员,同时状态不为拒绝/同意
            if ((mFriendhelpSubserviceType == FriendManagerModel.SUBSERVICE_BE_ADD || mFriendhelpSubserviceType == FriendManagerModel.SUBSERVICE_GROUP_WAITTING)
                    && mFriendhelpSubserviceStatus != FriendManagerModel.STATUS_REFUSE
                    && mFriendhelpSubserviceStatus != FriendManagerModel.STATUS_AGREE)
            {
                mNotFriendButtonGroup.setVisibility(View.GONE);
                mFriendManageButtonGroup.setVisibility(View.VISIBLE);
                
                if (!StringUtil.isNullOrEmpty(mFriendManagerModel.getReason()))
                {
                    mFriendselfreasonLayout.setVisibility(View.VISIBLE);
                    mFriendselfreason.setVisibility(View.VISIBLE);
                    mFriendselfreason.setText(mFriendManagerModel.getReason());
                }
                else
                {
                    mFriendselfreason.setVisibility(View.GONE);
                }
            }
            // 普通系统资料查看
            else
            {
                Logger.i(TAG, "=====普通关系资料查看======");
                mFriendManageButtonGroup.setVisibility(View.GONE);
                mFriendselfreason.setVisibility(View.GONE);
                mNotFriendButtonGroup.setVisibility(View.VISIBLE);
            }
            
            setCommonViewValue();
            // 邮箱地址及电话号码展示
            setFriendViewValue();
            
            if (null != mContactInfoModel)
            {
                // 功能按钮设置可点击
                mAddButtonGroup.setClickable(true);
            }
            else
            {
                // 功能按钮设置为不可点击
                mAddButtonGroup.setClickable(false);
            }
        }
        
        // 通讯录进入HiTalk详情,需要展示通讯录电话号码
        if (null != mContactId)
        {
            PhoneContactIndexModel mPhoneContactIndexModel = mContactLogic.getLocalContactProfile(mContactId);
            // 初始化界面前，先清空ContentView
            mContentView.removeAllViews();
            
            if (null == mPhoneContactIndexModel
                    || (null != mPhoneContactIndexModel
                            && null == mPhoneContactIndexModel.getPhoneNumbers() && null == mPhoneContactIndexModel.getEmailAddrs()))
            {
                mContentView.setVisibility(View.GONE);
                return;
            }
            mContentView.setVisibility(View.VISIBLE);
            mFriendPhoneLayout.setVisibility(View.GONE);
            
            // Hitalk电话展示
            if (null != mContactInfoModel
                    && null != mContactInfoModel.getPrimaryMobile())
            {
                List<List<String>> phoneLists = new ArrayList<List<String>>();
                List<String> phoneList = new ArrayList<String>();
                phoneList.add(mContactInfoModel.getPrimaryMobile());
                phoneList.add("");
                phoneLists.add(phoneList);
                setLocalPhoneView(R.string.phone_hitalk, phoneLists);
            }
            // Hitalk邮箱展示
            if (null != mContactInfoModel
                    && null != mContactInfoModel.getPrimaryEmail())
            {
                List<List<String>> emailLists = new ArrayList<List<String>>();
                List<String> emailList = new ArrayList<String>();
                emailList.add(mContactInfoModel.getPrimaryEmail());
                emailList.add("");
                emailLists.add(emailList);
                setLocalEmail(R.string.email_hitalk, emailLists);
            }
            // 通讯录详情电话号码展示
            if (null != mPhoneContactIndexModel.getPhoneNumbers()
                    && mPhoneContactIndexModel.getPhoneNumbers().size() > 0)
            {
                setLocalPhoneView(R.string.phone_1,
                        mPhoneContactIndexModel.getPhoneNumbers());
            }
            // 通讯录详情 EmailAddrs展示控制
            if (null != mPhoneContactIndexModel.getEmailAddrs()
                    && mPhoneContactIndexModel.getEmailAddrs().size() > 0)
            {
                setLocalEmail(R.string.email_1,
                        mPhoneContactIndexModel.getEmailAddrs());
            }
            
            // 去掉最后一根分割线，然后给不同的item设置不同的背景
            int count = mContentView.getChildCount();
            if (count > 0)
            {
                mContentView.setVisibility(View.VISIBLE);
                View view = null;
                if (count == 1)
                {
                    view = mContentView.getChildAt(0);
                    view.setBackgroundResource(R.drawable.setting_item_bg_all);
                }
                else
                {
                    view = mContentView.getChildAt(0);
                    view.setBackgroundResource(R.drawable.setting_item_bg_top);
                    for (int i = 1; i < count - 1; i++)
                    {
                        view = mContentView.getChildAt(i);
                        view.setBackgroundResource(R.drawable.setting_item_bg_mid);
                    }
                    view = mContentView.getChildAt(count - 1);
                    view.setBackgroundResource(R.drawable.setting_item_bg_bottom);
                }
                View dividerView = view.findViewById(R.id.divider_image);
                if (null != dividerView)
                {
                    dividerView.setVisibility(View.GONE);
                }
            }
            else
            {
                mContentView.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * [跳转到系统拨打电话页面]<BR>
     * 
     * @param phoneNumber
     *            phoneNumber
     */
    private void phoneCall(final String phoneNumber)
    {
        // 弹出选择呼叫或者短信的对话框，由于样式未给，暂不做
        Logger.i(TAG, "====呼叫======" + phoneNumber.replace("+", "00"));
        if (!StringUtil.isNullOrEmpty(phoneNumber))
        {
            // 可选择voip电话功能
            showChooseCallType(phoneNumber, false);
        }
    }
    
    /**
     * 跳转到发邮件页面<BR>
     * 
     * @param phoneNumber
     *            phoneNumber
     */
    private void phoneEmail(final String phonenumber)
    {
        Logger.i(TAG, "====发EMAIL======" + phonenumber);
        String number = phonenumber;
        if (!"".equals(number) || null != number)
        {
            Uri emailToUri = Uri.parse("mailto:" + number);
            Intent intent = new Intent(Intent.ACTION_SENDTO, emailToUri);
            try
            {
                startActivity(intent);
            }
            catch (Exception e)
            {
                Logger.e(TAG, "备注邮件 调用系统邮件失败 " + e.toString());
            }
        }
    }
    
    /**
     * [跳转到发短信页面]<BR>
     * 
     * @param phoneNumber
     *            phoneNumber
     */
    private void phoneSMS(final String phoneNumber)
    {
        // 弹出选择呼叫或者短信的对话框，由于样式未给，暂不做
        Logger.i(TAG, "====发短信======" + phoneNumber);
        String number = phoneNumber;
        if (!"".equals(number) && number != null)
        {
            Uri uri = Uri.parse("smsto:" + number.replace("+", "00"));
            Intent smsintent = new Intent(Intent.ACTION_SENDTO, uri);
            smsintent.putExtra("sms_body", "");
            
            try
            {
                startActivity(smsintent);
            }
            catch (Exception e)
            {
                Logger.e(TAG, "调用系统法短信失败 " + e.toString());
            }
        }
    }
    
    /**
     * 添加好友<BR>
     * 需要判断隐私设置
     */
    private void addFriendAction()
    {
        
        // 对方设置不允许加好友
        if (mContactInfoModel.getFriendPrivacy() == 3)
        {
            Toast.makeText(this,
                    this.getResources()
                            .getString(R.string.add_friend_not_allowed),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 对方被加好友需要验证
        else if (mContactInfoModel.getAutoConfirmFriend() != 2)
        {
            // 跳转到输入验证理由界面
            Intent intent = new Intent();
            intent.setAction(InputReasonAction.ACTION);
            intent.putExtra(InputReasonAction.EXTRA_MODE,
                    InputReasonAction.MODE_REASON);
            startActivityForResult(intent, REQ_FOR_INPUT_REASON);
        }
        else
        {
            mFriendHelperLogic.addFriend(mHiTalkID,
                    mContactInfoModel.getNickName(),
                    "",
                    mContactInfoModel.getFaceUrl());
        }
    }
    
    /**
     * 删除好友操作<BR>
     */
    private void deleteFriendAction()
    {
        String mesRes = String.format(getResources().getString(R.string.del_friend_choose),
                null == mContactInfoModel.getNickName() ? mHiTalkID
                        : mContactInfoModel.getNickName());
        
        showConfirmDialog(mesRes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                Logger.i(TAG, "确定删除");
                // 调用mFriendHelperLogic.delFriend(hiTalkID);
                mFriendHelperLogic.deleteFriend(mHiTalkID);
            }
        });
    }
    
    /**
     * 设置地区
     */
    private String getCoutry(ContactInfoModel contactInfoModel)
    {
        String provinceStr = contactInfoModel.getProvince();
        String cityStr = contactInfoModel.getCity();
        
        String sReturn = "";
        if (StringUtil.isNullOrEmpty(provinceStr)
                && StringUtil.isNullOrEmpty(cityStr))
        {
            sReturn = getResources().getString(R.string.setting_CHINA);
        }
        else if (StringUtil.isNullOrEmpty(provinceStr)
                && !StringUtil.isNullOrEmpty(cityStr))
        {
            sReturn = cityStr;
        }
        else if (!StringUtil.isNullOrEmpty(provinceStr)
                && StringUtil.isNullOrEmpty(cityStr))
        {
            sReturn = provinceStr;
        }
        else
        {
            sReturn = provinceStr + " " + cityStr;
        }
        
        return sReturn;
    }
    
    /**
     * 
     * 备注弹出框<BR>
     * 
     * @param editTexttitle
     */
    private void editMemoDialog()
    {
        final LimitedEditText editSign = new LimitedEditText(this);
        editSign.setText(mContactInfoModel.getMemoName());
        editSign.setMaxCharLength(20);
        showTextEditDialog(R.string.friend_memo_name,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // 发送备注信息
                        Logger.d(TAG, "=====更改备注信息======");
                        
                        String sInput = editSign.getText().toString();
                        if (null == mContactInfoModel
                                && mContactInfoModel.getMemoName() == sInput)
                        {
                            return;
                        }
                        mFriendLogic.sendFriendMemo(mContactInfoModel,
                                sInput,
                                null,
                                null);
                    }
                },
                editSign);
        // 控制弹出软件盘
        getHandler().sendEmptyMessage(ContactDetailsMessageType.SHOW_SOFT_INPUT);
        
    }
    
    /**
     * 重载返回按钮
     */
    @Override
    public void onBackPressed()
    {
        if (closeChooseCallType(false))
        {
            return;
        }
        super.onBackPressed();
    }
}