/*
 * 文件名: ResetPasswordActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Feb 25, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.login;

import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.LoginAction;
import com.huawei.basic.android.im.common.FusionAction.ResetPassWordAction;
import com.huawei.basic.android.im.common.FusionAction.SelectCountryAction;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionErrorInfo.LoginErrorCode;
import com.huawei.basic.android.im.common.FusionMessageType.RegisterMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.logic.adapter.http.UserManager;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.MyPopupWindow;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 重置密码（找回密码）界面展示
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Feb 25, 2012]
 */
public class ResetPasswordActivity extends BasicActivity implements
        OnClickListener
{
    
    /**
     * 返回按钮
     */
    private Button backButton;
    
    /**
     * 下一步或确认按钮
     */
    private Button nextButton;
    
    /**
     * 电话号码输入框
     */
    private EditText phoneNumberEdit;
    
    /**
     * 验证码输入框
     */
    private EditText verifyCodeEdit;
    
    /**
     * 新密码输入框
     */
    private EditText newPasswordEdit;
    
    /**
     * 显示密码标识
     */
    private CheckBox showPasswordBox;
    
    /**
     * title信息
     */
    private TextView titleText;
    
    /**
     * 重新获取验证码
     */
    private TextView getVerifyCodeText;
    
    /**
     * 填写电话号码所在布局
     */
    private LinearLayout phoneNumberLayout;
    
    /**
     * 重置密码所在布局
     */
    private LinearLayout resetPasswordLayout;
    
    /**
     * 密码强度图标
     */
    private ImageView passwordLowImg;
    
    /**
     * 手机号码
     */
    private String mobile;
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 是否是重置密码界面标识
     */
    private boolean isResetPasswordLayout;
    
    /**
     * popupWindow
     */
    private MyPopupWindow popupWindow;
    
    /**
     * 设置逻辑接口
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 倒计时线程
     */
    private Thread threadReset;
    
    /**
     * 倒计时类型
     */
    private TimeControlRunnable runnable;
    
    /**
     * 倒计时时间
     */
    private int delayTime = FusionCode.DelayTime.DELAYTIME;
    
    /**
     * 是邮箱找回还是手机找回密码
     */
    private boolean isPhoneOrEmail;
    
    /**
     * 国家名字
     */
    private TextView mCountryName;
    
    /**
     * 选择国家码区域
     */
    private View selectCountryView;
    
    /**
     * 国家名称
     */
    private String countryname;
    
    /**
     * 国家码
     */
    private String countrycode;
    
    /**
     * 倒计时文字
     */
    private String timeText;
    
    /**
     * 
     * 启动
     * 
     * @param savedInstanceState
     *            savedInstanceState
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        isPhoneOrEmail = getIntent().getBooleanExtra(ResetPassWordAction.IS_PHONE_OR_EMAIN,
                false);
        initView();
    }
    
    /**
     * 
     * 初始化界面控件
     */
    private void initView()
    {
        backButton = (Button) findViewById(R.id.left_button);
        titleText = (TextView) findViewById(R.id.title);
        
        nextButton = (Button) findViewById(R.id.right_button);
        
        phoneNumberEdit = (EditText) findViewById(R.id.phone_number);
        verifyCodeEdit = (EditText) findViewById(R.id.verify_code);
        newPasswordEdit = (EditText) findViewById(R.id.new_password);
        showPasswordBox = (CheckBox) findViewById(R.id.show_password);
        getVerifyCodeText = (TextView) findViewById(R.id.get_verifycode);
        
        phoneNumberLayout = (LinearLayout) findViewById(R.id.reset_phone_number_layout);
        resetPasswordLayout = (LinearLayout) findViewById(R.id.reset_layout);
        
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setText(R.string.next);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(getResources().getString(R.string.login_find_pwd));
        backButton.setText(getResources().getString(R.string.back));
        
        passwordLowImg = (ImageView) findViewById(R.id.password_icon);
        
        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        showPasswordBox.setOnClickListener(this);
        getVerifyCodeText.setOnClickListener(this);
        
        if (StringUtil.isNullOrEmpty(phoneNumberEdit.getText().toString()))
        {
            //进入页面后默认不可点击
            nextButton.setEnabled(false);
        }
        else
        {
            nextButton.setEnabled(true);
        }
        
        //选择国家码
        selectCountryView = findViewById(R.id.select_country);
        //        mCountryCode = (TextView) findViewById(R.id.countrycode);
        mCountryName = (TextView) findViewById(R.id.countryname);
        mCountryName.setOnClickListener(this);
        
        //手机找回需要国家码，邮箱地址不需要
        if (isPhoneOrEmail)
        {
            selectCountryView.setVisibility(View.VISIBLE);
            phoneNumberEdit.setHint(R.string.login_user_number_hint);
            phoneNumberEdit.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        else
        {
            selectCountryView.setVisibility(View.GONE);
            phoneNumberEdit.setHint(R.string.email);
        }
        
        //手机号码、邮箱输入框监听
        phoneNumberEdit.addTextChangedListener(new TextWatcher()
        {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
                
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
                //输入框不为空
                if (null != s && s.toString().trim().length() > 0)
                {
                    nextButton.setEnabled(true);
                }
                else
                {
                    nextButton.setEnabled(false);
                }
            }
        });
        //验证码监听
        verifyCodeEdit.addTextChangedListener(new TextWatcher()
        {
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
                
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0 && newPasswordEdit.getText().length() > 0)
                {
                    nextButton.setEnabled(true);
                }
                else
                {
                    nextButton.setEnabled(false);
                }
            }
            
        });
        //密码框监听
        newPasswordEdit.addTextChangedListener(new TextWatcher()
        {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
                
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0 && verifyCodeEdit.getText().length() > 0)
                {
                    nextButton.setEnabled(true);
                }
                else
                {
                    nextButton.setEnabled(false);
                }
                
                String password = s.toString().trim();
                //密码长度小于6
                if (password.length() < 6)
                {
                    passwordLowImg.setImageResource(R.drawable.intensity_of_password_0);
                    
                    return;
                }
                //检测密码强度
                int strong = StringUtil.checkStrong(password);
                //密码强度低
                if (strong == 1)
                {
                    passwordLowImg.setImageResource(R.drawable.intensity_of_password_1);
                }
                //密码强度中
                else if (strong == 2)
                {
                    passwordLowImg.setImageResource(R.drawable.intensity_of_password_2);
                }
                //密码强度高
                else if (strong == 3)
                {
                    passwordLowImg.setImageResource(R.drawable.intensity_of_password_3);
                }
            }
        });
    }
    
    /**
     * 初始化逻辑对象
     * 
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics()
     */
    
    @Override
    protected void initLogics()
    {
        mSettingsLogic = (ISettingsLogic) super.getLogicByInterfaceClass(ISettingsLogic.class);
    }
    
    /**
     * 
     * 按钮点击事件处理
     * 
     * @param v
     *            点击按钮
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            // 返回按钮点击事件监听
            case R.id.left_button:
                //是否是重置密码界面标识
                if (isResetPasswordLayout)
                {
                    showIconDialog(R.string.prompt,
                            R.drawable.call_title_circle,
                            R.string.confirm_exit,
                            new DialogInterface.OnClickListener()
                            {
                                
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    if (null != runnable)
                                    {
                                        runnable.cancel();
                                    }
                                    phoneNumberLayout.setVisibility(View.VISIBLE);
                                    resetPasswordLayout.setVisibility(View.GONE);
                                    nextButton.setText(R.string.next);
                                    nextButton.setEnabled(true);
                                    isResetPasswordLayout = false;
                                }
                                
                            });
                }
                else
                {
                    finish();
                }
                break;
            // 下一步按钮点击事件监听
            case R.id.right_button:
                if (!isResetPasswordLayout)
                {
                    mobile = "";
                    email = "";
                    
                    String resetInfo = phoneNumberEdit.getText().toString();
                    //输入为空
                    if (StringUtil.isNullOrEmpty(resetInfo))
                    {
                        popupWindow = new MyPopupWindow(this);
                        popupWindow.show(phoneNumberEdit,
                                getResources().getString(R.string.mobile_or_email_not_null));
                        
                        return;
                    }
                    popupWindow = new MyPopupWindow(this);
                    //区分手机号码和邮箱错误的提示
                    if (isPhoneOrEmail)
                    {
                        //输入为手机号码
                        if (StringUtil.isMobile(resetInfo))
                        {
                            mobile = resetInfo.substring(resetInfo.length() - 11);
                            mSettingsLogic.resetPasswordFromNumber(mobile);
                            nextButton.setClickable(false);
                            
                        }
                        else
                        {
                            popupWindow.show(phoneNumberEdit,
                                    getResources().getString(R.string.input_right_mobile));
                        }
                    }
                    else
                    {
                        //输入为邮箱
                        if (StringUtil.isEmail(resetInfo))
                        {
                            email = resetInfo;
                            mSettingsLogic.resetPasswordFromEmail(email);
                        }
                        else
                        {
                            popupWindow.show(phoneNumberEdit,
                                    getResources().getString(R.string.input_right_email));
                        }
                    }
                    
                }
                else
                {
                    String verifyCode = verifyCodeEdit.getText().toString();
                    String password = newPasswordEdit.getText().toString();
                    //验证码为空或者验证码长度小于6提示
                    if (StringUtil.isNullOrEmpty(verifyCode)
                            || verifyCode.length() < 6)
                    {
                        popupWindow = new MyPopupWindow(this);
                        popupWindow.show(verifyCodeEdit,
                                getResources().getString(R.string.verify_code_length));
                        
                        return;
                    }
                    //密码为空
                    else if (StringUtil.isNullOrEmpty(password))
                    {
                        popupWindow = new MyPopupWindow(this);
                        popupWindow.show(newPasswordEdit,
                                getResources().getString(R.string.password_not_null));
                        
                        return;
                    }
                    //密码长度小于最小长于
                    else if (password.length() < RegisterActivity.PS_VERIFYCODE_MIN_LENGTH)
                    {
                        popupWindow = new MyPopupWindow(this);
                        popupWindow.show(newPasswordEdit,
                                getResources().getString(R.string.password_length));
                        
                        return;
                    }
                    //重置密码
                    else
                    {
                        mSettingsLogic.resetPasswordFromNumberAndVerify(mobile,

                        verifyCode, password);
                    }
                    break;
                }
                break;
            // 显示密码勾选框
            case R.id.show_password:
                if (showPasswordBox.isChecked())
                {
                    newPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    newPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.get_verifycode:
                mSettingsLogic.resetPasswordFromNumberWithType(mobile, "2");
                getVerifyCodeText.setTextColor(R.color.agray);
                getVerifyCodeText.setClickable(false);
                break;
            // 选择国家码
            case R.id.countryname:
                startActivityForResult(new Intent(SelectCountryAction.ACTION),
                        ResetPassWordAction.REQUEST_CODE_SELECTCOUNTRY);
                break;
            default:
                break;
        }
    }
    
    /**
     * 通过 重载父类的handleStateMessage方法， 可以 实现消息处理
     * 
     * @param msg
     *            Message
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        super.handleStateMessage(msg);
        String retCode;
        switch (msg.what)
        {
            case SettingsMessageType.RESET_PASSWORD:
                retCode = (String) msg.obj;
                // 手机注册
                if (!StringUtil.isNullOrEmpty(mobile))
                {
                    if (StringUtil.equals(retCode, LoginErrorCode.SUCCESS))
                    {
                        showToast(R.string.reset_password_success);
                        startActivity(new Intent(LoginAction.ACTION));
                        finish();
                    }
                    else if (StringUtil.equals(retCode, LoginErrorCode.INCORRECT_VERIFY_CODE))
                    {
                        // 验证码错误
                        //                        popupWindow = new MyPopupWindow(this);
                        //                        popupWindow.show(verifyCodeEdit,
                        //                                getResources().getString(R.string.error_code_209005011));
                        
                        showOnlyConfirmIconDialog(R.string.prompt,
                                R.drawable.call_title_circle,
                                R.string.verification_code_error,
                                new DialogInterface.OnClickListener()
                                {
                                    
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        verifyCodeEdit.setText(null);
                                        
                                    }
                                });
                    }
                    else if (StringUtil.equals(retCode, LoginErrorCode.TRY_AGAIN))
                    {
                        // 重置过于频繁
                        showToast(R.string.error_code_209005008);
                    }
                    else
                    {
                        showToast(R.string.reset_password_faild);
                    }
                    delayTime = FusionCode.DelayTime.DELAYTIME;
                    if (null != runnable)
                    {
                        runnable.cancel();
                        runnable = null;
                    }
                    getVerifyCodeText.setClickable(true);
                    getVerifyCodeText.setTextColor(getResources().getColorStateList(R.drawable.verifycode_bg));
                    getVerifyCodeText.setText(getString(R.string.register_fresh_code3));
                    
                }
                // 邮箱注册
                else
                {
                    
                    if (StringUtil.equals(retCode, LoginErrorCode.SUCCESS))
                    {
                        showToast(R.string.send_email);
                        finish();
                    }
                    // 邮箱未绑定提示
                    else if (StringUtil.equals(retCode, LoginErrorCode.EMAIL_NOT_BOUNDED))
                    {
                        popupWindow = new MyPopupWindow(this);
                        popupWindow.show(phoneNumberEdit,
                                getResources().getString(R.string.error_code_209005005));
                    }
                    else if (StringUtil.equals(retCode, LoginErrorCode.EMAIL_SEND_FAILED))
                    {
                        // 邮件发送失败
                        showToast(R.string.error_code_209005015);
                    }
                    else if (StringUtil.equals(retCode, LoginErrorCode.TRY_AGAIN))
                    {
                        // 重置过于频繁，请稍后再试
                        showToast(R.string.error_code_209005008);
                    }
                    else
                    {
                        showToast(R.string.reset_password_faild);
                    }
                }
                break;
            case SettingsMessageType.GET_MSISDN_VERIFY_CODE:
                //获取手机验证码
                //                retCode = (String) msg.obj;
                //                if (StringUtil.equals(retCode, "0"))
                //                {
                //                    showOnlyConfirmIconDialog(R.string.prompt,
                //                            R.drawable.call_title_circle,
                //                            R.string.send_phone,
                //                            new DialogInterface.OnClickListener()
                //                            {
                //                                
                //                                @Override
                //                                public void onClick(DialogInterface dialog,
                //                                        int which)
                //                                {
                //                                    phoneNumberLayout.setVisibility(View.GONE);
                //                                    resetPasswordLayout.setVisibility(View.VISIBLE);
                //                                    nextButton.setText(R.string.confirm);
                //                                    isResetPasswordLayout = true;
                //                                }
                //                                
                //                            });
                //                }
                //                else
                //                {
                //                    showToast(R.string.get_verify_code_faild);
                //                }
                
                nextButton.setClickable(true);
                //演示版本从服务器返回中直接获取验证码 edited by zhanggj 20120509
                @SuppressWarnings("unchecked")
                Map<String, Object> params = (Map<String, Object>) msg.obj;
                if (params != null)
                {
                    retCode = (String) params.get(UserManager.RET_CODE);
                    String verifyCode = (String) params.get(UserManager.VERIFY_CODE);
                    if (StringUtil.equals(retCode, LoginErrorCode.SUCCESS))
                    {
                        verifyCodeEdit.setText(verifyCode);
                        showOnlyConfirmIconDialog(R.string.prompt,
                                R.drawable.call_title_circle,
                                R.string.send_phone,
                                new DialogInterface.OnClickListener()
                                {
                                    
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        newPasswordEdit.setText(null);
                                        newPasswordEdit.requestFocus();
                                        phoneNumberLayout.setVisibility(View.GONE);
                                        resetPasswordLayout.setVisibility(View.VISIBLE);
                                        nextButton.setText(R.string.confirm);
                                        isResetPasswordLayout = true;
                                    }
                                    
                                });
                    }
                    else
                    {
                        showToast(R.string.get_verify_code_faild);
                    }
                    
                }
                startRunnable();
                
                break;
            case SettingsMessageType.CHECK_MOBILE_BIND:
                nextButton.setClickable(true);
                //检查手机是否绑定
                retCode = (String) msg.obj;
                if (StringUtil.equals(retCode, LoginErrorCode.SUCCESS))
                {
                    // 手机号码未绑定
                    popupWindow = new MyPopupWindow(this);
                    popupWindow.show(phoneNumberEdit,
                            getResources().getString(R.string.mobile_not_bound));
                }
                else if (StringUtil.equals(retCode, LoginErrorCode.PHONE_IS_BOUNDED))
                {
                    nextButton.setClickable(false);
                    mSettingsLogic.resetPasswordFromNumberWithType(mobile, "2");
                }
                else
                {
                    // 未知错误
                    showToast(R.string.error_code_209009001);
                }
                
                break;
            case RegisterMessageType.GET_VERIFYCODE_TIMER:
                getVerifyDelay((Integer) msg.obj);
                break;
            case RegisterMessageType.CONNECT_FAILED:
                nextButton.setClickable(true);
                showToast(FusionErrorInfo.getErrorInfo(this,
                        String.valueOf(msg.obj)));
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * 按键监听事件
     * 
     * @param keyCode
     *            按键码
     * @param event
     *            按键事件
     * @return true or false
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (isResetPasswordLayout)
            {
                showIconDialog(R.string.prompt,
                        R.drawable.call_title_circle,
                        R.string.confirm_exit,
                        new DialogInterface.OnClickListener()
                        {
                            
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                if (null != runnable)
                                {
                                    runnable.cancel();
                                }
                                phoneNumberLayout.setVisibility(View.VISIBLE);
                                resetPasswordLayout.setVisibility(View.GONE);
                                nextButton.setText(R.string.next);
                                nextButton.setEnabled(true);
                                
                                isResetPasswordLayout = false;
                            }
                            
                        });
            }
            else
            {
                finish();
            }
            
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * 从INTENT中获取数据<BR>
     * 如果是从注册成功页面跳转至登录页面，则从INTENT中可以获取登录账号及密码，直接进行登录 <BR> {@inheritDoc}
     * 
     * @see android.app.Activity#onActivityResult(int, int,
     *      android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        // 如果是找回密码页面返回
        if (requestCode == ResetPassWordAction.REQUEST_CODE_SELECTCOUNTRY)
        {
            if (data != null)
            {
                countryname = data.getStringExtra(SelectCountryAction.COUNTRY_NAME);
                countrycode = data.getStringExtra(SelectCountryAction.COUNTRY_CODE);
                
                // 从intent获取到国家信息
                if (countryname != null && countrycode != null)
                {
                    mCountryName.setText(countryname + "(" + countrycode + ")");
                    //                    mCountryCode.setText(countryname+"("+countrycode+")");
                }
            }
        }
    }
    
    /**
     * 
     * Activity销毁处理方法
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
    
    /**
     * 重新获取验证码延时 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param time
     *            计时器
     */
    private void getVerifyDelay(int time)
    {
        // 重新获取验证码
        String addTime = String.format("%02d", time);
        String info = getResources().getString(R.string.register_fresh_code,
                addTime);
        int index = info.indexOf(addTime);
        SpannableString titles = new SpannableString(info);
        titles.setSpan(new ForegroundColorSpan(
                getResources().getColor(R.color.orange)),
                index,
                index + addTime.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        timeText = titles.toString();
        getVerifyCodeText.setText(titles);
        getVerifyCodeText.setTextColor(getResources().getColor(R.color.agray));
        getVerifyCodeText.setClickable(false);
        if (time <= 0)
        {
            getVerifyCodeText.setClickable(true);
            getVerifyCodeText.setTextColor(getResources().getColorStateList(R.drawable.verifycode_bg));
            timeText = getResources().getString(R.string.register_fresh_code3);
            getVerifyCodeText.setText(getString(R.string.register_fresh_code3));
        }
    }
    
    /**
     * 
     * 开始倒计时
     */
    private void startRunnable()
    {
        //重置倒计时
        delayTime = FusionCode.DelayTime.DELAYTIME;
        if (null != runnable)
        {
            runnable.cancel();
            runnable = null;
        }
        runnable = new TimeControlRunnable(
                RegisterMessageType.GET_VERIFYCODE_TIMER);
        threadReset = new Thread(runnable);
        threadReset.start();
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * 计时器类型(立即登录、获取验证码)
     * 
     * @version [RCS Client V100R001C03, 2012-3-5]
     */
    private class TimeControlRunnable implements Runnable
    {
        
        //是否终止线程
        private boolean running;
        
        //总时间
        private int messageId;
        
        public TimeControlRunnable(int messageId)
        {
            this.messageId = messageId;
        }
        
        /**
         * 
         * 停止倒计时
         */
        public void cancel()
        {
            running = false;
        }
        
        @Override
        public void run()
        {
            running = true;
            while (delayTime-- >= 0 && running)
            {
                Message msg = new Message();
                msg.what = messageId;
                msg.obj = delayTime;
                ResetPasswordActivity.this.getHandler().sendMessage(msg);
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 返回一个boolean表示展示该页面是否需要登录成功
     * 
     * @return boolean 是否是登录后的页面
     */
    @Override
    protected boolean needLogin()
    {
        return false;
    }
    
    /**
     * 
     * 语言切换
     * 
     * @param newConfig
     *            newConfig
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (!isResetPasswordLayout)
        {
            //返回按钮
            backButton.setText(getResources().getString(R.string.back));
            //下一步
            nextButton.setText(R.string.next);
            //标题
            titleText.setText(getResources().getString(R.string.login_find_pwd));
            if (isPhoneOrEmail)
            {
                //国家码
                if (null != countryname)
                {
                    mCountryName.setText(countryname + "(" + countrycode + ")");
                }
                else
                {
                    mCountryName.setText(R.string.country_normal_name);
                }
                phoneNumberEdit.setHint(R.string.login_user_number_hint);
            }
            else
            {
                phoneNumberEdit.setHint(R.string.email);
            }
        }
        else
        {
            //返回按钮
            backButton.setText(getResources().getString(R.string.back));
            //下一步
            nextButton.setText(R.string.confirm);
            //标题
            titleText.setText(getResources().getString(R.string.login_find_pwd));
            //验证码
            verifyCodeEdit.setHint(R.string.input_verify_code);
            //密码
            newPasswordEdit.setHint(R.string.input_password);
            //密码强度
            ((TextView) (findViewById(R.id.password_strength_text))).setText(R.string.password_strength);
            //倒计时
            getVerifyCodeText.setText(timeText);
        }
    }
}
