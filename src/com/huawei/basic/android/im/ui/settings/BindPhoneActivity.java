/*
 * 文件名: BindphoneActivity.java
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

import java.util.Map;

import android.content.DialogInterface;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.logic.adapter.http.UserManager;
import com.huawei.basic.android.im.logic.contact.IContactLogic;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.utils.MyPopupWindow;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 *绑定、解绑手机界面
 * @author gaihe
 * @version [RCS Client V100R001C03, 2012-4-21] 
 */
public class BindPhoneActivity extends BasicActivity implements OnClickListener
{
    /**
     * 绑定类型标识
     */
    private static final String BINDSIGN = "0";
    
    /**
     * 解绑类型标识
     */
    private static final String UNBINDSIGN = "1";
    
    /**
     * 解绑手机类型
     */
    private static final String UNBINDPHONETYPE = "0";
    
    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;
    
    /**
     * 标题文字
     */
    private TextView mTitle;
    
    /**
     * 电话
     */
    private String mPhone;
    
    /**
     * 编辑框
     */
    private LimitedEditText mEditText;
    
    /**
     *绑定手机
     */
    private String toBoundphone;
    
    /**
     * 获取验证码的按钮
     */
    private Button mButton;
    
    /**
     * 获取验证码的提示
     */
    private TextView mVerificationCodeTip;
    
    /**
     * 绑定、解绑按钮
     */
    private Button mRightButton;
    
    /**
     * 已绑定的布局
     */
    private LinearLayout mLinearLayout;
    
    /**
     * 已绑定的手机号码
     */
    private TextView mBoundphone;
    
    /**
     * 上传通讯录的按钮
     */
    private Button mUploadContactsButton;
    
    /**
     * 上传通讯录的提示
     */
    private TextView mUploadContactsTip;
    
    /**
     * 设置逻辑接口
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * IContactLogic的引用
     */
    private IContactLogic mContactLogic;
    
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
        setContentView(R.layout.setting_bind_phone);
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
        ImageView imageView = (ImageView) findViewById(R.id.bind_phone_iv);
        imageView.setImageResource(R.drawable.setting_bind_phone);
        TextView bindInfo = (TextView) findViewById(R.id.bind_phone_info);
        bindInfo.setText(R.string.bind_phone_info);
        mEditText = (LimitedEditText) findViewById(R.id.bind_phone_edit);
        mLinearLayout = (LinearLayout) findViewById(R.id.bound_phone);
        mBoundphone = (TextView) findViewById(R.id.your_phone_content);
        mButton = (Button) findViewById(R.id.bind_phone_btn);
        mButton.setOnClickListener(this);
        mVerificationCodeTip = (TextView) findViewById(R.id.verification_code_tip);
        mUploadContactsButton = (Button) findViewById(R.id.upload_contacts_btn);
        mUploadContactsButton.setOnClickListener(this);
        mUploadContactsTip = (TextView) findViewById(R.id.upload_contacts_tip);
    }
    
    /**
     * 初始化界面
     */
    private void setViewValues()
    {
        mPhone = (String) getIntent().getSerializableExtra(SettingsAction.FLAG_USER_PHONE);
        if (StringUtil.isNullOrEmpty(mPhone))
        {
            setBindphoneView();
        }
        else
        {
            //            if (mPhone.contains("+86"))
            //            {
            //                mPhone = mPhone.substring(3);
            //            }
            mPhone = StringUtil.fixPortalPhoneNumber(mPhone);
            setUnbindphoneView();
        }
    }
    
    /**
     * 设置绑定手机界面
     */
    private void setBindphoneView()
    {
        mTitle.setText(R.string.bind_phone_title);
        mEditText.setHint(R.string.unbind_phone_tilte);
        mEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mLinearLayout.setVisibility(View.GONE);
        mButton.setEnabled(false);
        mButton.setText(R.string.bind_phone);
        mEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3)
            {
                String phoneNumber = arg0.toString().trim();
                if (phoneNumber.startsWith("+86"))
                {
                    mEditText.setMaxCharLength(14);
                }
                else if (phoneNumber.startsWith("86"))
                {
                    mEditText.setMaxCharLength(13);
                }
                else if (phoneNumber.startsWith("0086"))
                {
                    mEditText.setMaxCharLength(15);
                }
                else
                {
                    mEditText.setMaxCharLength(11);
                }
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
        if (mContactLogic.hasUploaded())
        {
            mUploadContactsButton.setText(R.string.stop_uploading_address_book);
            mUploadContactsTip.setVisibility(View.GONE);
        }
        else
        {
            mUploadContactsButton.setText(R.string.upload_mobile_contacts);
            mUploadContactsTip.setText(R.string.upload_info);
        }
    }
    
    /**
     * 设置解绑手机界面
     */
    private void setUnbindphoneView()
    {
        mTitle.setText(R.string.unbind_phone_tilte);
        mEditText.setVisibility(View.GONE);
        mBoundphone.setText(mPhone);
        mButton.setBackgroundResource(R.drawable.btn_red);
        mButton.setText(R.string.delete_and_unbind);
        if (mContactLogic.hasUploaded())
        {
            mUploadContactsButton.setText(R.string.stop_uploading_address_book);
            mUploadContactsTip.setVisibility(View.GONE);
        }
        else
        {
            mUploadContactsButton.setText(R.string.upload_mobile_contacts);
            mUploadContactsTip.setText(R.string.upload_info);
        }
    }
    
    /**
     * 设置输入验证码界面
     */
    /*
     * 演示版本从服务器返回中直接获取验证码 edited by zhanggj 20120509
    private void inputVerificationCodeView()
    {
        mRightButton.setVisibility(View.VISIBLE);
        mRightButton.setOnClickListener(this);
        bindInfo.setText(R.string.bind_phone_info);
        editText.setVisibility(View.VISIBLE);
        editText.setText("");
        editText.setHint(R.string.verify_code);
        editText.requestFocus();
        linearLayout.setVisibility(View.GONE);
        mUploadContactsButton.setVisibility(View.GONE);
        mUploadContactsTip.setVisibility(View.GONE);
        startTime = System.currentTimeMillis();
        handler = new Handler();
        ticker = new Runnable()
        {
            public void run()
            {
                long second = 60 - (System.currentTimeMillis() - startTime) / 1000;
                if (second < 0)
                {
                    mButton.setVisibility(View.VISIBLE);
                    mButton.setText(R.string.resend_verify_code);
                    mButton.setEnabled(true);
                    editText.setVisibility(View.GONE);
                    mVerificationCodeTip.setVisibility(View.GONE);
                }
                else
                {
                    String secondString = getResources().getString(R.string.verify_code_input_details,
                            Integer.parseInt(Long.toString(second)));
                    mButton.setVisibility(View.GONE);
                    mVerificationCodeTip.setVisibility(View.VISIBLE);
                    mVerificationCodeTip.setText(secondString);
                    handler.postDelayed(ticker, 1000);
                }
            }
        };
        ticker.run();
    }
    */
    
    /**
     * 设置输入验证码界面
     * @param verifyCode 验证码
     * 演示版本从服务器返回中直接获取验证码 edited by zhanggj 20120509
     */
    private void inputVerificationCodeView(String verifyCode)
    {
        mRightButton.setVisibility(View.VISIBLE);
        mRightButton.setOnClickListener(this);
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setText(verifyCode);
        mEditText.setHint(R.string.verify_code);
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
                    mRightButton.setEnabled(true);
                }
                else
                {
                    mRightButton.setEnabled(false);
                }
            }
        });
        mLinearLayout.setVisibility(View.GONE);
        mUploadContactsButton.setVisibility(View.GONE);
        mUploadContactsTip.setVisibility(View.GONE);
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
                    mButton.setText(R.string.resend_verify_code);
                    mButton.setEnabled(true);
                    mRightButton.setVisibility(View.GONE);
                    mEditText.setVisibility(View.GONE);
                    mVerificationCodeTip.setVisibility(View.GONE);
                }
                else
                {
                    String secondString = getResources().getString(R.string.verify_code_input_details,
                            Integer.parseInt(Long.toString(second)));
                    mButton.setVisibility(View.GONE);
                    mVerificationCodeTip.setVisibility(View.VISIBLE);
                    mVerificationCodeTip.setText(secondString);
                    mHandler.postDelayed(mTicker, 1000);
                }
            }
        };
        mTicker.run();
    }
    
    /**
     * 绑定手机
     */
    private void bindPhone()
    {
        String mEditContent = mEditText.getText().toString().trim();
        if (mEditContent.length() < CODE_LENGTH)
        {
            PopupWindow popupWindow = new MyPopupWindow(this);
            ((MyPopupWindow) popupWindow).show(mEditText,
                    getResources().getString(R.string.code_length));
            return;
        }
        else if (!StringUtil.isNullOrEmpty(mEditContent))
        {
            if (StringUtil.isNullOrEmpty(mPhone))
            {
                String bindPhone = "+86"
                        + StringUtil.fixPortalPhoneNumber(toBoundphone);
                mSettingsLogic.bindPhone(bindPhone, mEditContent);
                return;
            }
            else
            {
                mSettingsLogic.unBindPhone(UNBINDPHONETYPE, mEditContent);
                return;
            }
        }
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
                finish();
                break;
            case R.id.bind_phone_btn:
                if (StringUtil.isNullOrEmpty(mPhone))
                {
                    if (mEditText.getVisibility() == View.VISIBLE)
                    {
                        toBoundphone = mEditText.getText().toString().trim();
                    }
                    if (StringUtil.isMobile(toBoundphone))
                    {
                        //请求绑定验证码
                        mSettingsLogic.getMsisdnVerifyCode(StringUtil.fixPortalPhoneNumber(toBoundphone),
                                BINDSIGN);
                    }
                    else
                    {
                        showToast(R.string.input_right_mobile);
                        return;
                    }
                }
                else
                {
                    showConfirmDialog(R.string.unbind_tip,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    //请求解绑验证码
                                    mSettingsLogic.getMsisdnVerifyCode(StringUtil.fixPortalPhoneNumber(mPhone),
                                            UNBINDSIGN);
                                }
                            });
                }
                break;
            case R.id.right_button:
                bindPhone();
                break;
            case R.id.upload_contacts_btn:
                if (mContactLogic.hasUploaded())
                {
                    showConfirmDialog(R.string.stop_upload_contact,
                            new DialogInterface.OnClickListener()
                            {
                                
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    mContactLogic.updateUploadFlag(false);
                                    mContactLogic.deleteUploadedContacts();
                                    mUploadContactsButton.setText(R.string.upload_mobile_contacts);
                                    mUploadContactsTip.setVisibility(View.VISIBLE);
                                    mUploadContactsTip.setText(R.string.upload_info);
                                }
                            });
                }
                else
                {
                    mContactLogic.updateUploadFlag(true);
                    mContactLogic.beginUpload(true);
                    mUploadContactsButton.setText(R.string.stop_uploading_address_book);
                    mUploadContactsTip.setVisibility(View.GONE);
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
            case SettingsMessageType.GET_MSISDN_VERIFY_CODE:
                //                String code = (String) msg.obj;
                //                if ("0".equals(code))
                //                {
                //                    if (StringUtil.isNullOrEmpty(phone))
                //                    {
                //                        showOnlyConfirmDialog(R.string.bind_verify_tip,
                //                                new DialogInterface.OnClickListener()
                //                                {
                //                                    @Override
                //                                    public void onClick(DialogInterface dialog,
                //                                            int which)
                //                                    {
                //                                        inputVerificationCodeView();
                //                                    }
                //                                });
                //                    }
                //                    else
                //                    {
                //                        showOnlyConfirmDialog(R.string.unbind_verify_tip,
                //                                new DialogInterface.OnClickListener()
                //                                {
                //                                    @Override
                //                                    public void onClick(DialogInterface dialog,
                //                                            int which)
                //                                    {
                //                                        inputVerificationCodeView();
                //                                    }
                //                                });
                //                    }
                //                }
                //                else
                //                {
                //                    showErrorToast(code);
                //                }
                //演示版本从服务器返回中直接获取验证码 edited by zhanggj 20120509
                @SuppressWarnings("unchecked")
                Map<String, Object> params = (Map<String, Object>) msg.obj;
                if (null != params)
                {
                    String code = (String) params.get(UserManager.RET_CODE);
                    final String verifyCode = (String) params.get(UserManager.VERIFY_CODE);
                    if (Common.RETCODE_SUCCESS.equals(code))
                    {
                        if (StringUtil.isNullOrEmpty(mPhone))
                        {
                            showOnlyConfirmDialog(R.string.bind_verify_tip,
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which)
                                        {
                                            inputVerificationCodeView(verifyCode);
                                        }
                                    });
                        }
                        else
                        {
                            showOnlyConfirmDialog(R.string.unbind_verify_tip,
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which)
                                        {
                                            inputVerificationCodeView(verifyCode);
                                        }
                                    });
                        }
                    }
                    else
                    {
                        showToast(FusionErrorInfo.getErrorInfo(this, code));
                    }
                }
                break;
            case SettingsMessageType.CONNECT_FAILED:
                String errorCode = (String) msg.obj;
                showToast(FusionErrorInfo.getErrorInfo(this, errorCode));
                break;
            case SettingsMessageType.BIND_PHONE:
                String bind = (String) msg.obj;
                if (Common.RETCODE_SUCCESS.equals(bind))
                {
                    mSettingsLogic.updateContactPhone(toBoundphone);
                    showToast(R.string.bind_succeed);
                    finish();
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
                    mSettingsLogic.updateContactPhone("");
                    mSettingsLogic.unbindAccount(mPhone);
                    showToast(R.string.unbind_succeed);
                    finish();
                }
                else
                {
                    showToast(FusionErrorInfo.getErrorInfo(this, unbind));
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
        mSettingsLogic = (ISettingsLogic) super.getLogicByInterfaceClass(ISettingsLogic.class);
        mContactLogic = (IContactLogic) getLogicByInterfaceClass(IContactLogic.class);
    }
}
