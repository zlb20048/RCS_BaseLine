/*
 * 文件名: PrivacySettingsActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: meiyue
 * 创建时间:2012-2-27
 * 
 * 修改人：hegai
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.logic.model.UserConfigModel;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 个人隐私设置界面
 * @author meiyue
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class PrivacySettingsActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * 好友添加策略字段：允许所有
     */
    private static final int ALLOW_ALL = 1;
    
    /**
     * 好友添加策略字段：允许好友
     */
    private static final int ALLOW_CONTACTS = 2;
    
    /**
     * 好友添加策略字段：不允许
     */
    private static final int ALLOW_NONE = 3;
    
    /**
     * 自动添加策略字段：需验证
     */
    private static final int NEED_CONFIRM = 1;
    
    /**
     * 自动添加策略字段：不需验证
     */
    private static final int UNNEED_CONFIRM = 2;
    
    /**
     * 全公开
     */
    private static final int COMPLETELY_OPEN = 4;
    
    /**
     * 仅对好友公开
     */
    private static final int OPEN_FOR_FRIENDS = 3;
    
    /**
     * 不公开
     */
    private static final int OPEN_FOR_CONTACTS = 5;
    
    /**
     * 允许所有人加我为好友
     */
    private CheckedTextView mAllowEveryone;
    
    /**
     *加我为好友时需要验证
     */
    private CheckedTextView mNeedConfirm;
    
    /**
     * 只允许通讯录人员加我为好友
     */
    private CheckedTextView mAllowContacts;
    
    /**
     * 拒绝所有人加我为好友
     */
    private CheckedTextView mAllowNone;
    
    /**
     * 好友添加策略字段(进入时需要从服务器获取)
     */
    private String mFriendPrivacyStr;
    
    /**
     * 自动添加策略字段
     */
    private String mAutoConfirmFriendStr;
    
    /**
     * 逻辑对象
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * mCompletelyOpen
     */
    private CheckedTextView mCompletelyOpen;
    
    /**
     * mOpenForFriends
     */
    private CheckedTextView mOpenForFriends;
    
    /**
     * mOpenForContacts
     */
    private CheckedTextView mOpenForContacts;
    
    /**
     * 详细资料(手机号、Email)隐私策略
     */
    private String mPrivacyMaterial;
    
    /**
     * 添加好友 -1  0  1  
     */
    private int mAddFriend = -1;
    
    /**
     * 资料开放 -1  0  1  
     */
    private int mOpenProfile = -1;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_privacy_new);
        initView();
        setViewValues();
    }
    
    /**
     * 处理点击事件
     * @param v 点击视图
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                updataPrivacySetting();
                //                updateProfilePrivacy();
                break;
            case R.id.rb_addfriend_1:
                mAllowEveryone.setChecked(true);
                mNeedConfirm.setChecked(false);
                mAllowContacts.setChecked(false);
                mAllowNone.setChecked(false);
                break;
            case R.id.rb_addfriend_2:
                mAllowEveryone.setChecked(false);
                mNeedConfirm.setChecked(true);
                mAllowContacts.setChecked(false);
                mAllowNone.setChecked(false);
                break;
            case R.id.rb_addfriend_3:
                mAllowEveryone.setChecked(false);
                mNeedConfirm.setChecked(false);
                mAllowContacts.setChecked(true);
                mAllowNone.setChecked(false);
                break;
            case R.id.rb_addfriend_4:
                mAllowEveryone.setChecked(false);
                mNeedConfirm.setChecked(false);
                mAllowContacts.setChecked(false);
                mAllowNone.setChecked(true);
                break;
            case R.id.privacy_material_cb_one:
                mCompletelyOpen.setChecked(true);
                mOpenForFriends.setChecked(false);
                mOpenForContacts.setChecked(false);
                break;
            case R.id.privacy_material_cb_two:
                mCompletelyOpen.setChecked(false);
                mOpenForFriends.setChecked(true);
                mOpenForContacts.setChecked(false);
                break;
            case R.id.privacy_material_cb_three:
                mCompletelyOpen.setChecked(false);
                mOpenForFriends.setChecked(false);
                mOpenForContacts.setChecked(true);
                break;
            default:
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            case SettingsMessageType.UPDATE_MYPROFILE_PRIVACY_SUCCEED:
                mAddFriend = 1;
                if (mOpenProfile != 0)
                {
                    showToast(R.string.setting_person_information_success);
                    closeProgressDialog();
                    finish();
                }
                break;
            case SettingsMessageType.UPDATE_MYPROFILE_PRIVACY_FAILED:
                mAddFriend = 1;
                String addFriend = (String) msg.obj;
                if (mOpenProfile != 0)
                {
                    showToast(FusionErrorInfo.getErrorInfo(this, addFriend));
                    closeProgressDialog();
                    finish();
                }
                break;
            case SettingsMessageType.ADD_FRIENDS_CONNECT_FAILED:
                mAddFriend = 1;
                String addCode = (String) msg.obj;
                if (mOpenProfile != 0)
                {
                    showToast(FusionErrorInfo.getErrorInfo(this, addCode));
                    closeProgressDialog();
                    finish();
                }
                break;
            case SettingsMessageType.UPDATE_PRIVACY_MATERIAL_SUCCEED:
                mOpenProfile = 1;
                if (mAddFriend != 0)
                {
                    showToast(R.string.setting_person_information_success);
                    closeProgressDialog();
                    finish();
                }
                break;
            case SettingsMessageType.UPDATE_PRIVACY_MATERIAL_FAILED:
                mOpenProfile = 1;
                String openProfile = (String) msg.obj;
                if (mAddFriend != 0)
                {
                    showToast(FusionErrorInfo.getErrorInfo(this, openProfile));
                    closeProgressDialog();
                    finish();
                }
                break;
            case SettingsMessageType.OPEN_PROFILE_CONNECT_FAILED:
                mAddFriend = 1;
                String openCode = (String) msg.obj;
                if (mOpenProfile != 0)
                {
                    showToast(FusionErrorInfo.getErrorInfo(this, openCode));
                    closeProgressDialog();
                    finish();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 返回键按钮监听事件
     * @param keyCode keyCode
     * @param event event
     * @return 返回监听事件的boolean值
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                updataPrivacySetting();
                //                updateProfilePrivacy();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mSettingsLogic = (ISettingsLogic) getLogicByInterfaceClass(ISettingsLogic.class);
    }
    
    //    protected boolean isPrivateHandler()
    //    {
    //        return true;
    //    }
    
    /**
     * 初始化组件
     */
    private void initView()
    {
        findViewById(R.id.left_button).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.privacy);
        title.setVisibility(View.VISIBLE);
        
        mAllowEveryone = (CheckedTextView) findViewById(R.id.rb_addfriend_1);
        mAllowEveryone.setOnClickListener(this);
        mNeedConfirm = (CheckedTextView) findViewById(R.id.rb_addfriend_2);
        mNeedConfirm.setOnClickListener(this);
        mAllowContacts = (CheckedTextView) findViewById(R.id.rb_addfriend_3);
        mAllowContacts.setOnClickListener(this);
        mAllowNone = (CheckedTextView) findViewById(R.id.rb_addfriend_4);
        mAllowNone.setOnClickListener(this);
        
        mCompletelyOpen = (CheckedTextView) findViewById(R.id.privacy_material_cb_one);
        mCompletelyOpen.setOnClickListener(this);
        mOpenForFriends = (CheckedTextView) findViewById(R.id.privacy_material_cb_two);
        mOpenForFriends.setOnClickListener(this);
        mOpenForContacts = (CheckedTextView) findViewById(R.id.privacy_material_cb_three);
        mOpenForContacts.setOnClickListener(this);
    }
    
    /**
     * 初始化隐私界面
     */
    private void setViewValues()
    {
        UserConfigModel model1 = configQueryByKey(UserConfigModel.FRIEND_PRIVACY);
        UserConfigModel model2 = configQueryByKey(UserConfigModel.AUTO_CONFIRM_FRIEND);
        mFriendPrivacyStr = null == model1 ? String.valueOf(ALLOW_ALL)
                : model1.getValue();
        mAutoConfirmFriendStr = null == model2 ? String.valueOf(NEED_CONFIRM)
                : model2.getValue();
        setFriendPrivacy();
        UserConfigModel model3 = configQueryByKey(UserConfigModel.PRIVACY);
        mPrivacyMaterial = null == model3 ? String.valueOf(OPEN_FOR_FRIENDS)
                : model3.getValue();
        setPrivacyMaterial();
    }
    
    /**
     * 通过得到的mFriendPrivacyStr确定各组件选中状态
     */
    private void setFriendPrivacy()
    {
        switch ((int) Integer.parseInt(mFriendPrivacyStr))
        {
            case ALLOW_ALL:
                if (("" + UNNEED_CONFIRM).equals(mAutoConfirmFriendStr))
                {
                    mAllowEveryone.setChecked(true);
                    mNeedConfirm.setChecked(false);
                    mAllowContacts.setChecked(false);
                    mAllowNone.setChecked(false);
                }
                else
                {
                    mAllowEveryone.setChecked(false);
                    mNeedConfirm.setChecked(true);
                    mAllowContacts.setChecked(false);
                    mAllowNone.setChecked(false);
                }
                break;
            case ALLOW_CONTACTS:
                mAllowEveryone.setChecked(false);
                mNeedConfirm.setChecked(false);
                mAllowContacts.setChecked(true);
                mAllowNone.setChecked(false);
                break;
            case ALLOW_NONE:
                mAllowEveryone.setChecked(false);
                mNeedConfirm.setChecked(false);
                mAllowContacts.setChecked(false);
                mAllowNone.setChecked(true);
                break;
            default:
                break;
        }
    }
    
    /**
     * 通过得到的mPrivacyMaterial确定各组件选中状态
     */
    private void setPrivacyMaterial()
    {
        switch ((int) Integer.parseInt(mPrivacyMaterial))
        {
            case COMPLETELY_OPEN:
                mCompletelyOpen.setChecked(true);
                mOpenForFriends.setChecked(false);
                mOpenForContacts.setChecked(false);
                break;
            case OPEN_FOR_FRIENDS:
                mCompletelyOpen.setChecked(false);
                mOpenForFriends.setChecked(true);
                mOpenForContacts.setChecked(false);
                break;
            case OPEN_FOR_CONTACTS:
                mCompletelyOpen.setChecked(false);
                mOpenForFriends.setChecked(false);
                mOpenForContacts.setChecked(true);
                break;
            default:
                break;
        }
    }
    
    /**
     * 更新隐私设置
     */
    private void updataPrivacySetting()
    {
        //好友验证
        if (mAllowEveryone.isChecked())
        {
            mFriendPrivacyStr = "" + ALLOW_ALL;
            mAutoConfirmFriendStr = "" + UNNEED_CONFIRM;
        }
        else if (mNeedConfirm.isChecked())
        {
            mFriendPrivacyStr = "" + ALLOW_ALL;
            mAutoConfirmFriendStr = "" + NEED_CONFIRM;
        }
        else if (mAllowContacts.isChecked())
        {
            mFriendPrivacyStr = "" + ALLOW_CONTACTS;
            mAutoConfirmFriendStr = "" + UNNEED_CONFIRM;
        }
        else if (mAllowNone.isChecked())
        {
            mFriendPrivacyStr = "" + ALLOW_NONE;
            mAutoConfirmFriendStr = "" + UNNEED_CONFIRM;
        }
        //资料开放
        if (mCompletelyOpen.isChecked())
        {
            mPrivacyMaterial = "" + COMPLETELY_OPEN;
        }
        else if (mOpenForFriends.isChecked())
        {
            mPrivacyMaterial = "" + OPEN_FOR_FRIENDS;
        }
        else if (mOpenForContacts.isChecked())
        {
            mPrivacyMaterial = "" + OPEN_FOR_CONTACTS;
        }
        UserConfigModel model1 = configQueryByKey(UserConfigModel.FRIEND_PRIVACY);
        UserConfigModel model2 = configQueryByKey(UserConfigModel.AUTO_CONFIRM_FRIEND);
        UserConfigModel model3 = configQueryByKey(UserConfigModel.PRIVACY);
        if (null != model1 && null != model2 && null != model3)
        {
            if (!(mFriendPrivacyStr.equals(model1.getValue()) && mAutoConfirmFriendStr.equals(model2.getValue()))
                    || !mPrivacyMaterial.equals(model3.getValue()))
            {
                showProgressDialog(getString(R.string.setting_person_info_save_dialog));
                if (!(mFriendPrivacyStr.equals(model1.getValue()) && mAutoConfirmFriendStr.equals(model2.getValue())))
                {
                    mSettingsLogic.sendUpdateProfilePrivacy(mAutoConfirmFriendStr,
                            mFriendPrivacyStr);
                    mAddFriend = 0;
                }
                if (!mPrivacyMaterial.equals(model3.getValue()))
                {
                    mSettingsLogic.sendUpdateProfilePrivacy(mPrivacyMaterial);
                    mOpenProfile = 0;
                }
            }
            else
            {
                finish();
            }
        }
    }
    
    /**
     * 更新隐私资料
     */
    //    private void updateProfilePrivacy()
    //    {
    //        if (mCompletelyOpen.isChecked())
    //        {
    //            mPrivacyMaterial = "" + COMPLETELY_OPEN;
    //        }
    //        else if (mOpenForFriends.isChecked())
    //        {
    //            mPrivacyMaterial = "" + OPEN_FOR_FRIENDS;
    //        }
    //        else if (mOpenForContacts.isChecked())
    //        {
    //            mPrivacyMaterial = "" + OPEN_FOR_CONTACTS;
    //        }
    //        //查询数据库，判断状态是否改变，如果改变则发送更新请求
    //        UserConfigModel model = configQueryByKey(UserConfigModel.PRIVACY);
    //        if (null != model)
    //        {
    //            if (!mPrivacyMaterial.equals(model.getValue()))
    //            {
    //                showProgressDialog(getString(R.string.setting_person_info_save_dialog));
    //            }
    //            else
    //            {
    //                finish();
    //            }
    //        }
    //    }
    
    /**
     * 查询数据库操作
     * @param key  key值
     * @return 个人配置对象
     */
    private UserConfigModel configQueryByKey(String key)
    {
        return mSettingsLogic.configQueryByKey(key);
    }
}
