/*
 * 文件名: MyAccountActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: gaihe
 * 创建时间:2012-4-12
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.voip.VoipAccount;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 我的账号界面
 * @author gaihe
 * @version [RCS Client V100R001C03, 2012-4-12] 
 */
public class MyAccountActivity extends BasicActivity implements OnClickListener
{
    /**
     * 绑定邮箱地址
     */
    private static final int BINDEMAIL = 2;
    
    /**
     * 绑定VOIP账号
     */
    private static final int BINDVOIP = 7;
    
    /**
     * 个人信息对象
     */
    private ContactInfoModel mUser;
    
    /**
     * 邮箱
     */
    //    private String email;
    
    /**
     * 设置逻辑接口
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 登录voip的Logic对象
     */
    private IVoipLogic mVoipLogic;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_my_account);
        findView();
        setViewValues();
        mSettingsLogic.registerContactInfoObserver();
    }
    
    /**
     * 初始化组件
     */
    private void findView()
    {
        findViewById(R.id.left_button).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.my_account);
        findViewById(R.id.bind_phone).setOnClickListener(this);
        findViewById(R.id.bind_email).setOnClickListener(this);
        findViewById(R.id.bind_voip).setOnClickListener(this);
    }
    
    /**
     * 初始化界面
     */
    private void setViewValues()
    {
        mUser = mSettingsLogic.queryMyProfile(FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId());
        if (null != mUser)
        {
            setPhoneNumber();
            setEmailAddress();
            setVoipAccount();
        }
    }
    
    /**
     * 设置组件文字显示
     * @param text text
     * @param content content
     */
    private void setText(TextView text, String content)
    {
        //        if (null != content)
        //        {
        //            content = content.trim();
        //        }
        if (!StringUtil.isNullOrEmpty(content))
        {
            content = content.trim();
            text.setText(content);
        }
        else
        {
            text.setText(getString(R.string.setting_unbind));
        }
    }
    
    /**
     * 设置电话号码是否绑定
     */
    private void setPhoneNumber()
    {
        String content = mUser.getPrimaryMobile();
        //        if (!StringUtil.isNullOrEmpty(content))
        //        {
        //            if (content.contains("+86"))
        //            {
        //                content = content.substring(3);
        //            }
        //            else if (content.contains("0086"))
        //            {
        //                content = content.substring(4);
        //            }
        //        }
        content = StringUtil.fixPortalPhoneNumber(content);
        setText((TextView) findViewById(R.id.bind_phone_content), content);
    }
    
    /**
     * 设置Email是否绑定
     */
    private void setEmailAddress()
    {
        String content = mUser.getPrimaryEmail();
        setText((TextView) findViewById(R.id.bind_email_content), content);
    }
    
    /**
     * 设置Voip帐号是否绑定
     */
    private void setVoipAccount()
    {
        VoipAccount voipAccount = mVoipLogic.queryVoipAccount();
        if (null != voipAccount)
        {
            setText((TextView) findViewById(R.id.bind_voip_content),
                    voipAccount.getAccount());
        }
        else
        {
            setText((TextView) findViewById(R.id.bind_voip_content),
                    this.getString(R.string.setting_unbind));
        }
    }
    
    /**
     * 处理点击事件
     * @param v 点击视图
     */
    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent();
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            // 绑定手机
            case R.id.bind_phone:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_BIND_PHONE);
                intent.putExtra(SettingsAction.FLAG_USER_PHONE,
                        mUser.getPrimaryMobile());
                startActivity(intent);
                break;
            //绑定邮箱
            case R.id.bind_email:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_BIND_EMAIL);
                intent.putExtra(SettingsAction.FLAG_USER_EMAIL,
                        mUser.getPrimaryEmail());
                intent.putExtra(SettingsAction.FLAG_USER_NAME,
                        mUser.getNickName());
                startActivityForResult(intent, BINDEMAIL);
                break;
            //绑定VOIP
            case R.id.bind_voip:
                intent.setAction(SettingsAction.ACTION_ACTIVITY_BIND_VOIP);
                startActivityForResult(intent, BINDVOIP);
                break;
        }
    }
    
    /**
     * 处理Activity跳转回来的信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case BINDEMAIL:
                if (resultCode == RESULT_OK)
                {
                    //                    email = (String) data.getSerializableExtra(SettingsAction.FLAG_USER_EMAIL);
                    //                    if (!StringUtil.isNullOrEmpty(email)
                    //                            && StringUtil.isEmail(email))
                    //                    {
                    //                        mSettingsLogic.checkEmailBind(email);
                    //                    }
                    mSettingsLogic.sendRequestPrivateProfile(null, true);
                }
                break;
            case BINDVOIP:
                TextView setVoipText = (TextView) findViewById(R.id.bind_voip_content);
                if (null != data)
                {
                    String aor = data.getStringExtra(FusionAction.VoipAction.EXTRAL_VOIP_AOR);
                    if (null != aor)
                    {
                        setVoipText.setText(aor);
                    }
                    else
                    {
                        setVoipText.setText(this.getString(R.string.setting_unbind));
                    }
                }
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
        switch (msg.what)
        {
            case SettingsMessageType.CONTACTINFO_DB_CHANGED:
            case FusionMessageType.VOIPMessageType.VOIP_BIND_UNBIND:
                setViewValues();
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
        mSettingsLogic = (ISettingsLogic) super.getLogicByInterfaceClass(ISettingsLogic.class);
        mVoipLogic = (IVoipLogic) getLogicByInterfaceClass(IVoipLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mSettingsLogic.unregisterContactInfoObserver();
    }
}
