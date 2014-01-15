/*
 * 文件名: BindemailActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: gaihe
 * 创建时间:2012-4-21
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 绑定、解绑邮箱界面
 * @author gaihe
 * @version [RCS Client V100R001C03, 2012-4-21] 
 */
public class BindEmailActivity extends BasicActivity implements OnClickListener
{
    /**
     * 解绑类型标识
     */
    private static final String UNBINDSIGN = "1";
    
    /**
     * 我的账号界面传递过来的邮箱
     */
    private String mEmail;
    
    /**
     *  我的账号界面传递过来的姓名
     */
    private String mName;
    
    /**
     * 标题
     */
    private TextView mTitle;
    
    /**
     * 确定按钮
     */
    private Button mRightButton;
    
    /**
     * 编辑框
     */
    private LimitedEditText mEditText;
    
    /**
     * 编辑内容
     */
    private String mEditContent;
    
    /**
     * 绑定、解绑按钮
     */
    private Button mButton;
    
    /**
     * 邮箱链接提示
     */
    private TextView mEmailLinkTip;
    
    /**
     * 已绑定的布局
     */
    private LinearLayout mLinearLayout;
    
    /**
     * 已绑定的邮箱
     */
    private TextView mBoundEmail;
    
    /**
     * 设置逻辑接口
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * Handler对象
     */
    private Handler mHandler;
    
    /**
     * 获取验证码倒计时线程
     */
    private Runnable mTicker;
    
    /**
     * 获取验证码倒计时开始时间
     */
    private long mStartTime;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_bind_email);
        findView();
        setViewValues();
    }
    
    /**
     * 初始化组件
     */
    private void findView()
    {
        Button leftButton = (Button) findViewById(R.id.left_button);
        leftButton.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title);
        mRightButton = (Button) findViewById(R.id.right_button);
        mRightButton.setOnClickListener(this);
        ImageView imageView = (ImageView) findViewById(R.id.bind_email_iv);
        imageView.setImageResource(R.drawable.setting_bind_email);
        TextView textView = (TextView) findViewById(R.id.bind_email_info);
        textView.setText(R.string.bind_email_info);
        mEditText = (LimitedEditText) findViewById(R.id.bind_email_edit);
        mEditText.setMaxCharLength(50);
        mEmailLinkTip = (TextView) findViewById(R.id.email_link_tip);
        mLinearLayout = (LinearLayout) findViewById(R.id.bound_email);
        mBoundEmail = (TextView) findViewById(R.id.your_email_content);
        mButton = (Button) findViewById(R.id.bind_email_btn);
        mButton.setOnClickListener(this);
    }
    
    /**
     * 初始化界面
     */
    private void setViewValues()
    {
        mEmail = (String) getIntent().getSerializableExtra(SettingsAction.FLAG_USER_EMAIL);
        mName = (String) getIntent().getSerializableExtra(SettingsAction.FLAG_USER_NAME);
        if (StringUtil.isNullOrEmpty(mEmail))
        {
            setBindEmailView();
        }
        else
        {
            setUnbindEmailView();
        }
    }
    
    /**
     * 设置绑定邮箱页面
     */
    private void setBindEmailView()
    {
        mTitle.setText(getString(R.string.bind_email_title));
        mLinearLayout.setVisibility(View.GONE);
        mEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mButton.setEnabled(false);
        mEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3)
            {
            }
            
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3)
            {
            }
            
            @Override
            public void afterTextChanged(Editable arg0)
            {
                if (!StringUtil.isNullOrEmpty(mEditText.getText().toString()))
                {
                    mButton.setEnabled(true);
                }
                else
                {
                    mButton.setEnabled(false);
                }
            }
        });
    }
    
    /**
     * 设置解绑邮箱页面
     */
    private void setUnbindEmailView()
    {
        mTitle.setText(getString(R.string.unbind_email_title));
        mEditText.setVisibility(View.GONE);
        mBoundEmail.setText(mEmail);
        mButton.setBackgroundResource(R.drawable.btn_red);
        mButton.setText(R.string.delete_and_unbind);
    }
    
    /**
     * 设置获取邮箱链接的界面
     */
    private void setEmailLinkView()
    {
        mRightButton.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
        mStartTime = System.currentTimeMillis();
        mHandler = new Handler();
        mTicker = new Runnable()
        {
            public void run()
            {
                long second = 60 - (System.currentTimeMillis() - mStartTime) / 1000;
                if (second < 0)
                {
                    mButton.setVisibility(View.VISIBLE);
                    mButton.setText(R.string.resend_verify_email);
                    mButton.setEnabled(true);
                    mRightButton.setVisibility(View.GONE);
                    mEditText.setVisibility(View.GONE);
                    mEmailLinkTip.setVisibility(View.GONE);
                }
                else
                {
                    String secondString = getResources().getString(R.string.email_link_input_details,
                            Integer.parseInt(Long.toString(second)));
                    mButton.setVisibility(View.GONE);
                    mEmailLinkTip.setVisibility(View.VISIBLE);
                    mEmailLinkTip.setText(secondString);
                    mHandler.postDelayed(mTicker, 1000);
                }
            }
        };
        mTicker.run();
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
            case R.id.bind_email_btn:
                if (!StringUtil.isNullOrEmpty(mEmail))
                {
                    showConfirmDialog(R.string.unbind_tip_email,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    mSettingsLogic.unBindMail(UNBINDSIGN);
                                }
                            });
                }
                else
                {
                    mEditContent = mEditText.getText().toString().trim();
                    if (!StringUtil.isEmail(mEditContent))
                    {
                        showToast(R.string.input_right_email);
                        return;
                    }
                    else
                    {
                        mSettingsLogic.bindMail(mEditContent, mName);
                    }
                }
                break;
            case R.id.left_button:
            case R.id.right_button:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
    
    /**
     * 处理接收的消息
     * @param msg 消息
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            case SettingsMessageType.BIND_EMAIL:
                String bind = (String) msg.obj;
                if (Common.RETCODE_SUCCESS.equals(bind))
                {
                    showOnlyConfirmDialog(R.string.bound_mail_tip,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    setEmailLinkView();
                                }
                            });
                }
                else
                {
                    showToast(FusionErrorInfo.getErrorInfo(this, bind));
                }
                break;
            case SettingsMessageType.UNBIND:
                String unbind = (String) msg.obj;
                if (Common.RETCODE_SUCCESS.equals(unbind))
                {
                    showOnlyConfirmDialog(R.string.unbound_mail_tip,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    setEmailLinkView();
                                }
                            });
                }
                else
                {
                    showToast(FusionErrorInfo.getErrorInfo(this, unbind));
                }
                break;
            case SettingsMessageType.CONNECT_FAILED:
                String errorCode = (String) msg.obj;
                showToast(FusionErrorInfo.getErrorInfo(this, errorCode));
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
    }
}
