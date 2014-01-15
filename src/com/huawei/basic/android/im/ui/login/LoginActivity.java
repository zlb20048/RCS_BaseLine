/*
 * 文件名: LoginActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.FeaturesAction;
import com.huawei.basic.android.im.common.FusionAction.LoginAction;
import com.huawei.basic.android.im.common.FusionAction.MainTabction;
import com.huawei.basic.android.im.common.FusionAction.RegisterAction;
import com.huawei.basic.android.im.common.FusionAction.ResetPassWordAction;
import com.huawei.basic.android.im.common.FusionAction.SelectCountryAction;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionErrorInfo.LoginErrorCode;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.login.ILoginLogic;
import com.huawei.basic.android.im.logic.login.receiver.ConnectionChangedReceiver;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.DecodeUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 登录页面<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-6]
 */
public class LoginActivity extends BasicActivity implements OnClickListener
{
    /**
     * TAG
     */
    private static final String TAG = "LoginActivity";
    
    /**
     * 登录模块的业务逻辑处理对象
     */
    private ILoginLogic mLoginLogic;
    
    /**
     * 输入用户名
     */
    private EditText mstrInputPassportText;
    
    /**
     * 输入密码
     */
    private EditText mstrInputPasswordText;
    
    /**
     * 图形验证码的输入框
     */
    private EditText mVerifyCodeText;
    
    /**
     * 登录结果提示text
     */
    private EditText mLoginResultText;
    
    /**
     * 校验图形验证码结果提示text
     */
    private EditText mVerifyCodeResultText;
    
    /**
     * 验证码的区域
     */
    private View mVerifyCodeAreaView;
    
    /**
     * 图形验证码的显示框
     */
    private ImageView mVerifyCodeImage;
    
    /**
     * 标题栏中间文字
     */
    private TextView mTitleView;
    
    /**
     * 上次登录账号
     */
    private TextView mLastLoginName;
    
    /**
     * 国家名字
     */
    private TextView mCountryName;
    
    /**
     * 选择登录方式弹出框
     */
    private PopupWindow chooseLoginTypePopupWindow;
    
    /**
     * 选择登录方式弹出框内部View
     */
    private View loginTypeView;
    
    /**
     * 选择国家码区域
     */
    private View selectCountryView;
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 登录按钮
     */
    private Button loginButton;
    
    /**
     * 图形验证码页面
     */
    private LinearLayout verifyCodeLayout;
    
    /**
     * 登录页面
     */
    private LinearLayout loginLayout;
    
    /**
     * 标题栏右按钮
     */
    private Button rightButton;
    
    /**
     * 图形验证码确定按钮
     */
    private Button okButton;
    
    /**
     * 找回密码
     */
    private TextView mFindPwd;
    
    /**
     * 注册
     */
    private TextView mRegeister;
    
    /**
     * 国家名称
     */
    private String countryname;
    
    /**
     * 国家码
     */
    private String countrycode;
    
    /**
     * 登陆错误提示
     */
    private String mLoginError;
    
    /**
     * 是否是手机号登陆
     */
    private boolean isLoginByPhone;
    
    /**
     * 是否是登陆页面
     */
    private boolean isLoginLayout;
    
    /**
     * 
     * <BR> {@inheritDoc}
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
        
        // 登录
            case R.id.login_button:
                mLoginResultText.setVisibility(View.GONE);
                //禁止再次点击登录.
                loginButton.setEnabled(false);
                //                if (accountModel == null)
                //                {
                //                }
                //                else
                //                {
                //                    account = accountModel.getLoginAccount();
                //                }
                if (mstrInputPassportText.getVisibility() == View.VISIBLE)
                {
                    account = mstrInputPassportText.getText().toString().trim();
                }
                else
                {
                    SharedPreferences sp = getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                            Context.MODE_PRIVATE);
                    account = sp.getString(Common.KEY_USER_PASSPORT, "");
                }
                password = mstrInputPasswordText.getText().toString();
                
                if (checkLogin(account, password))
                {
                    mLoginLogic.login(account, password);
                    showProgressDialog(R.string.connecting);
                }
                //点击登录退出软键盘
                hideInputWindow(view);
                break;
            
            // 切换账号
            case R.id.right_button:
                showLoginDialog(R.string.login_user_number_hint,
                        R.string.login_user_other_hint,
                        new LoginTypeViewListener());
                break;
            
            //找回密码
            case R.id.findpwd:
                showLoginDialog(R.string.reset_password_from_phone,
                        R.string.reset_password_from_email,
                        new ResetPassWordListener());
                break;
            //注册
            case R.id.regeister:
                startActivity(new Intent(RegisterAction.ACTION));
                break;
            
            // 获取验证码
            case R.id.login_verify_code_image:
                //                String mInputPassportText;
                //                if (accountModel == null)
                //                {
                //                    mInputPassportText = mstrInputPassportText.getText()
                //                            .toString()
                //                            .trim();
                //                }
                //                else
                //                {
                //                    mInputPassportText = accountModel.getLoginAccount();
                //                }
                mLoginLogic.getVerifyCode(mstrInputPassportText.getText()
                        .toString()
                        .trim());
                break;
            // 选择国家码
            case R.id.countryname:
                startActivityForResult(new Intent(SelectCountryAction.ACTION),
                        LoginAction.REQUEST_CODE_SELECTCOUNTRY);
                break;
            case R.id.verify_code_login_button:
                // 用户输入的验证码
                String verifyCode = null;
                verifyCode = mVerifyCodeText.getText().toString();
                mLoginLogic.sendVerifyCodeImage(account, verifyCode);
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * <BR> {@inheritDoc}
     * 
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        currentActivtiy = this;
        // 初始化页面组件
        initView();
        
        FusionConfig.getInstance()
                .setUserStatus(LoginMessageType.STATUS_STAY_BY);
    }
    
    /**
     * <BR>
     * 
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    
    @Override
    protected void initLogics()
    {
        super.initLogics();
        mLoginLogic = (ILoginLogic) getLogicByInterfaceClass(ILoginLogic.class);
        //        mVoipLogic = (IVoipLogic) getLogicByInterfaceClass(IVoipLogic.class);
    }
    
    /**
     * 
     * <BR> {@inheritDoc}
     * 
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        super.handleStateMessage(msg);
        if (currentActivtiy != this)
        {
            return;
        }
        switch (msg.what)
        {
        
            case LoginMessageType.LOGIN:
                if (mstrInputPassportText.getVisibility() == View.VISIBLE)
                {
                    account = mstrInputPassportText.getText().toString().trim();
                }
                else
                {
                    SharedPreferences sp = getSharedPreferences(Common.SHARED_PREFERENCE_NAME,
                            Context.MODE_PRIVATE);
                    account = sp.getString(Common.KEY_USER_PASSPORT, "");
                }
                
                password = mstrInputPasswordText.getText().toString();
                
                if (checkLogin(account, password))
                {
                    mLoginLogic.login(account, password);
                }
                else
                {
                    closeProgressDialog();
                }
                break;
            // 登录成功
            case LoginMessageType.LOGIN_SUCCESS:
                
                FusionConfig.getInstance()
                        .setUserStatus(LoginMessageType.LOGIN_SUCCESS);
                //登录成功 登录按钮不可点击
                loginButton.setEnabled(false);
                //去判断是否自动登录voip
                //                mVoipLogic.autoLoginVoip();
                
                //本地保存系统id
                //                Logger.i(TAG, "SharedPreferences a "
                //                        + FusionConfig.getInstance()
                //                                .getAasResult()
                //                                .getUserSysId());
                
                //                Logger.i(TAG,
                //                        "SharedPreferences b "
                //                                + getSharedPreferences().getString(Common.KEY_USER_SYSID,
                //                                        null));
                boolean isFirst = mLoginLogic.saveAccount(this,
                        FusionConfig.getInstance()
                                .getAasResult()
                                .getUserSysId(),
                        FusionConfig.getInstance().getAasResult().getUserID(),
                        account,
                        password);
                Intent intent = new Intent();
                if (isFirst)
                {
                    // 页面跳转
                    intent.setAction(FeaturesAction.ACTION);
                }
                else
                {
                    intent.setAction(MainTabction.ACTION);
                }
                startActivity(intent);
                LoginMainActivity.finishActivity();
                finish();
                closeProgressDialog();
                mLoginLogic.afterLoginSuccessed(isFirst);
                break;
            // 登录失败
            case LoginMessageType.NEED_VERIFYCODE_ERROR:
                closeProgressDialog();
                loginButton.setEnabled(true);
                mVerifyCodeAreaView.setVisibility(View.VISIBLE);
                String info = String.valueOf(msg.obj);
                String text = FusionErrorInfo.getErrorInfo(this, info);
                mLoginError = text;
                mLoginResultText.setText(text);
                mLoginResultText.setVisibility(View.VISIBLE);
                //账号或密码错误
            case LoginMessageType.ACCOUNT_OR_PASSWORD_ERROR:
                closeProgressDialog();
                loginButton.setEnabled(true);
                //账号密码错误区分手机、ID、邮箱
                
                if (StringUtil.isMobile(account))
                {
                    mLoginError = getResources().getString(R.string.phone_error);
                }
                //邮箱或密码错误
                else if (StringUtil.isEmail(account))
                {
                    mLoginError = getResources().getString(R.string.email_error);
                }
                else
                {
                    mLoginError = getResources().getString(R.string.id_error);
                }
                mLoginResultText.setText(mLoginError);
                mLoginResultText.setVisibility(View.VISIBLE);
                break;
            case LoginMessageType.LOGIN_ERROR:
                closeProgressDialog();
                loginButton.setEnabled(true);
                String errorinfo = String.valueOf(msg.obj);
                String errortext = FusionErrorInfo.getLoginErrorInfo(this,
                        errorinfo);
                mLoginError = errortext;
                if (!StringUtil.isNullOrEmpty(errorinfo)
                        && StringUtil.isNumeric(errorinfo))
                {
                    Logger.i(TAG, "errorinfo:" + errorinfo);
                    // 提示为需要验证码，获取验证码。
                    if (LoginErrorCode.NEED_VERIFY_CODE.equals(errorinfo))
                    {
                        showIconDialog(R.string.prompt,
                                R.drawable.icon_warning,
                                R.string.error_code_200059504,
                                new DialogInterface.OnClickListener()
                                {
                                    
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        isLoginLayout = true;
                                        mLoginLogic.getVerifyCode(account);
                                        verifyCodeLayout.setVisibility(View.VISIBLE);
                                        loginLayout.setVisibility(View.GONE);
                                        
                                        rightButton.setVisibility(View.GONE);
                                        //标题栏
                                        mTitleView.setText(R.string.login_verify_code);
                                    }
                                    
                                });
                        return;
                    }
                    //账号密码错误
                    else if (LoginErrorCode.ACOUNT_ERROR_CODE.equals(errorinfo)
                            || LoginErrorCode.PASSWORD_ERROR_CODE.equals(errorinfo))
                    {
                        if (StringUtil.isMobile(account))
                        {
                            mLoginError = getResources().getString(R.string.phone_error);
                        }
                        //邮箱或密码错误
                        else if (StringUtil.isEmail(account))
                        {
                            mLoginError = getResources().getString(R.string.email_error);
                        }
                        else
                        {
                            mLoginError = getResources().getString(R.string.id_error);
                        }
                        mLoginResultText.setText(mLoginError);
                    }
                    else if (LoginErrorCode.KICK_OUT.equals(errorinfo))
                    {
                        quit();
                        Toast.makeText(this,
                                FusionErrorInfo.getLoginErrorInfo(this,
                                        (String) msg.obj),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                mLoginResultText.setText(errortext);
                mLoginResultText.setVisibility(View.VISIBLE);
                break;
            //获取图形验证码成功
            case LoginMessageType.GET_VERIFY_CODE_IMAGE_SUCCESS:
                closeProgressDialog();
                loginButton.setEnabled(true);
                byte[] bytes = (byte[]) msg.obj;
                mVerifyCodeImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes,
                        0,
                        bytes.length));
                break;
            //获取图形验证码失败
            case LoginMessageType.GET_VERIFY_CODE_IMAGE_FAILED:
                closeProgressDialog();
                loginButton.setEnabled(true);
                mVerifyCodeImage.setImageResource(R.drawable.error_verify_code);
                break;
            //校验图形验证码成功
            case LoginMessageType.SEND_VERIFY_CODE_IMAGE_SUCCESS:
                verifyCodeLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                
                rightButton.setVisibility(View.VISIBLE);
                //标题栏
                mTitleView.setText(R.string.app_name);
                break;
            //获取图形验证码失败
            case LoginMessageType.SEND_VERIFY_CODE_IMAGE_FAILED:
                // 提示为验证码错，聚焦到验证码框。
                String verifyerrorinfo = String.valueOf(msg.obj);
                String verifyerrortext = FusionErrorInfo.getLoginErrorInfo(this,
                        verifyerrorinfo);
                mLoginError = verifyerrortext;
                mVerifyCodeResultText.setText(verifyerrortext);
                mVerifyCodeResultText.setVisibility(View.VISIBLE);
                mVerifyCodeResultText.requestFocus();
                mVerifyCodeResultText.selectAll();
                mLoginLogic.getVerifyCode(account);
                
                break;
            default:
                break;
        }
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
        
        if (requestCode == LoginAction.REQUEST_CODE_SELECTCOUNTRY)
        {
            if (data != null)
            {
                countryname = data.getStringExtra(SelectCountryAction.COUNTRY_NAME);
                countrycode = data.getStringExtra(SelectCountryAction.COUNTRY_CODE);
                
                // 从intent获取到国家信息
                if (countryname != null && countrycode != null)
                {
                    mCountryName.setText(countryname + "(" + countrycode + ")");
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
     * 初始化页面组件、设置组件监听器 <BR>
     * 
     */
    private void initView()
    {
        //上次登录账号
        
        String passport = getSharedPreferences().getString(Common.KEY_USER_PASSPORT,
                "");
        String mPassword = getSharedPreferences().getString(Common.KEY_USER_PASSWORD,
                "");
        if (!StringUtil.isNullOrEmpty(mPassword))
        {
            mPassword = DecodeUtil.decrypt(passport, mPassword);
        }
        //        List<AccountModel> accounts = AccountDbAdapter.getInstance(this)
        //                .queryAllAccounts();
        //        if (null != accounts)
        //        {
        //            for (AccountModel account : accounts)
        //            {
        //                String passport = account.getLoginAccount();
        //                String password = account.getPassword();
        //                if (!StringUtil.isNullOrEmpty(passport)
        //                        && !StringUtil.isNullOrEmpty(password))
        //                {
        //                    pwdMap.put(passport, DecodeUtil.decrypt(passport, password));
        //                }
        //            }
        //        }
        //        String userSysID = getSharedPreferences().getString(Common.KEY_USER_SYSID,
        //                null);
        //        if (null != userSysID)
        //        {
        //            accountModel = AccountDbAdapter.getInstance(this)
        //                    .queryByUserSysId(userSysID);
        //        }
        //        accountModel = mLoginLogic.getLastLoginAccountModel(this);
        mstrInputPassportText = (EditText) findViewById(R.id.login_username);
        mLastLoginName = (TextView) findViewById(R.id.login_user_text);
        selectCountryView = findViewById(R.id.select_country);
        //        mCountryCode = (TextView) findViewById(R.id.countrycode);
        mCountryName = (TextView) findViewById(R.id.countryname);
        //首次登录账号输入框可见，非首次直接显示账号
        if (StringUtil.isNullOrEmpty(passport))
        {
            mstrInputPassportText.setVisibility(View.VISIBLE);
            mstrInputPassportText.setInputType(InputType.TYPE_CLASS_PHONE);
            mLastLoginName.setVisibility(View.GONE);
        }
        else
        {
            mLastLoginName.setVisibility(View.VISIBLE);
            mstrInputPassportText.setVisibility(View.GONE);
            mstrInputPassportText.setText(passport);
            selectCountryView.setVisibility(View.GONE);
            mLastLoginName.setText(getResources().getString(R.string.account_number)
                    + passport);
        }
        // 组件初始化
        
        mstrInputPasswordText = (EditText) findViewById(R.id.login_password);
        
        mTitleView = (TextView) findViewById(R.id.title);
        mLoginResultText = (EditText) findViewById(R.id.login_result);
        mVerifyCodeResultText = (EditText) findViewById(R.id.verify_code_result);
        mVerifyCodeText = (EditText) findViewById(R.id.login_verify_code);
        mVerifyCodeImage = (ImageView) findViewById(R.id.login_verify_code_image);
        mVerifyCodeAreaView = findViewById(R.id.login_verify_area);
        loginButton = (Button) findViewById(R.id.login_button);
        Button leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);
        
        //找回密码，注册
        mFindPwd = (TextView) findViewById(R.id.findpwd);
        mRegeister = (TextView) findViewById(R.id.regeister);
        
        //图形验证码页面
        verifyCodeLayout = (LinearLayout) findViewById(R.id.verify_code_layout);
        okButton = (Button) findViewById(R.id.verify_code_login_button);
        okButton.setOnClickListener(this);
        //登录页面
        loginLayout = (LinearLayout) findViewById(R.id.login_layout);
        
        // 设置可见性及显示文字
        leftButton.setVisibility(View.INVISIBLE);
        mTitleView.setVisibility(View.VISIBLE);
        mTitleView.setText(R.string.app_name);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setText(R.string.rapidly_passport);
        
        // 设置监听器
        mFindPwd.setOnClickListener(this);
        mRegeister.setOnClickListener(this);
        mLoginResultText.setOnClickListener(this);
        mVerifyCodeImage.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        mCountryName.setOnClickListener(this);
        
        loginButton.setEnabled(false);
        okButton.setEnabled(false);
        //        loginButton.setBackgroundResource(R.drawable.btn_green_disabled);
        // 设置输入框监听器
        mstrInputPassportText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                mLoginResultText.setVisibility(View.GONE);
                mstrInputPasswordText.setText(null);
                if (s.length() > 0
                        && mstrInputPasswordText.getText().length() > 0)
                {
                    loginButton.setEnabled(true);
                }
                else
                {
                    loginButton.setEnabled(false);
                }
            }
            
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
        });
        
        mstrInputPasswordText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                mLoginResultText.setVisibility(View.GONE);
                if (s.length() > 0
                        && mstrInputPassportText.getText().length() > 0)
                {
                    loginButton.setEnabled(true);
                }
                else
                {
                    loginButton.setEnabled(false);
                }
            }
            
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
        });
        
        //图形验证码输入框监听
        mVerifyCodeText.addTextChangedListener(new TextWatcher()
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
                mVerifyCodeResultText.setVisibility(View.GONE);
                if (s.length() > 0)
                {
                    okButton.setEnabled(true);
                }
                else
                {
                    okButton.setEnabled(false);
                }
                
            }
        });
    }
    
    /**
     * 
     * 弹出进度框<BR>
     * [功能详细描述]
     * 
     * @param proDialog
     *            对话框显示信息
     */
    @Override
    public void showProgressDialog(ProgressDialog proDialog)
    {
        proDialog.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event)
            {
                return keyCode == KeyEvent.KEYCODE_BACK
                        || keyCode == KeyEvent.KEYCODE_SEARCH;
            }
        });
        if (!isPaused())
        {
            proDialog.show();
            //            proDialog.getWindow()
            //                    .setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        }
    }
    
    /**
     * 
     * 登录前检查
     * 
     * @param account
     *            账号
     * @param passwd
     *            密码
     */
    private Boolean checkLogin(String mAccount, String passwd)
    {
        int netStatus = ConnectionChangedReceiver.checkNet(this);
        if (LoginMessageType.NET_STATUS_WAP == netStatus)
        {
            mLoginError = getResources().getString(R.string.check_wap_apn);
            mLoginResultText.setText(R.string.check_wap_apn);
            mLoginResultText.setVisibility(View.VISIBLE);
            loginButton.setEnabled(true);
        }
        else if (LoginMessageType.NET_STATUS_DISABLE == netStatus)
        {
            mLoginError = getResources().getString(R.string.check_network);
            mLoginResultText.setText(R.string.check_network);
            mLoginResultText.setVisibility(View.VISIBLE);
            loginButton.setEnabled(true);
        }
        //账号为空
        else if (StringUtil.isNullOrEmpty(mAccount))
        {
            mLoginError = getResources().getString(R.string.warn_input_user_name);
            mLoginResultText.setText(R.string.warn_input_user_name);
            mLoginResultText.setVisibility(View.VISIBLE);
            mstrInputPassportText.requestFocus();
            loginButton.setEnabled(true);
        }
        //密码为空
        else if (StringUtil.isNullOrEmpty(passwd))
        {
            mLoginError = getResources().getString(R.string.warn_input_pass_word);
            mLoginResultText.setText(R.string.warn_input_pass_word);
            mLoginResultText.setVisibility(View.VISIBLE);
            mstrInputPasswordText.requestFocus();
            loginButton.setEnabled(true);
        }
        //密码长度错误
        else if (passwd.length() < 6 || passwd.length() > 20)
        {
            mLoginError = getResources().getString(R.string.warn_input_correct_pass_word);
            mLoginResultText.setText(R.string.warn_input_correct_pass_word);
            mLoginResultText.setVisibility(View.VISIBLE);
            mstrInputPasswordText.requestFocus();
            loginButton.setEnabled(true);
        }
        
        //非首次登陆 不判断登陆的模式
        else if (mstrInputPassportText.getVisibility() == View.VISIBLE)
        {
            //只能输入手机号码
            if (selectCountryView.getVisibility() == View.VISIBLE)
            {
                return phoneError(mAccount);
            }
            //只能输入ID，邮箱
            if (selectCountryView.getVisibility() == View.GONE)
            {
                return emailOrIdError(mAccount);
            }
        }
        else
        {
            return true;
        }
        return false;
    }
    
    /**
     * 
     *检查手机号码
     * @param mAccount 帐号
     * @return 手机号码的检查
     */
    private boolean phoneError(String mAccount)
    {
        if (!(StringUtil.isNumeric(mAccount) || mAccount.trim().startsWith("+")))
        {
            mLoginError = getResources().getString(R.string.no_phone);
            mLoginResultText.setText(R.string.no_phone);
            mLoginResultText.setVisibility(View.VISIBLE);
            mstrInputPassportText.requestFocus();
            loginButton.setEnabled(true);
            return false;
        }
        return true;
    }
    
    /**
     * 
     *检查邮箱和id
     * @param mAccount 帐号
     * @return 邮箱和id的检查
     */
    private boolean emailOrIdError(String mAccount)
    {
        if (!(StringUtil.isEmail(mAccount) || (StringUtil.isNumeric(mAccount))))
        {
            mLoginError = getResources().getString(R.string.no_email_or_id);
            mLoginResultText.setText(R.string.no_email_or_id);
            mLoginResultText.setVisibility(View.VISIBLE);
            mstrInputPassportText.requestFocus();
            loginButton.setEnabled(true);
        }
        else
        {
            //邮箱地址不能超过50位
            if (StringUtil.isEmail(mAccount) && mAccount.length() > 50)
            {
                mLoginError = getResources().getString(R.string.warn_input_email_too_long);
                mLoginResultText.setText(R.string.warn_input_email_too_long);
                mLoginResultText.setVisibility(View.VISIBLE);
                mstrInputPassportText.requestFocus();
                loginButton.setEnabled(true);
            }
            //HiTalkID号码不能超过18位
            else if (StringUtil.isNumeric(mAccount) && mAccount.length() > 18)
            {
                mLoginError = getResources().getString(R.string.warn_input_woyouid_too_long);
                mLoginResultText.setText(R.string.warn_input_woyouid_too_long);
                mLoginResultText.setVisibility(View.VISIBLE);
                mstrInputPassportText.requestFocus();
                loginButton.setEnabled(true);
            }
            else
            {
                return true;
            }
        }
        return false;
        
    }
    
    /**
     * 
     * 选择登录模式/找回密码模式对话框
     */
    private void showLoginDialog(int firstString, int secondString,
            OnClickListener listener)
    {
        //隐藏软键盘
        hideInputWindow(getCurrentFocus());
        //多次点击取消上次弹出的。
        if (chooseLoginTypePopupWindow != null
                && chooseLoginTypePopupWindow.isShowing())
        {
            chooseLoginTypePopupWindow.dismiss();
        }
        LayoutInflater inflater = getLayoutInflater();
        loginTypeView = inflater.inflate(R.layout.login_type_window, null);
        
        Button firstButton = (Button) loginTypeView.findViewById(R.id.btn_number);
        firstButton.setOnClickListener(listener);
        firstButton.setText(firstString);
        Button secondButton = (Button) loginTypeView.findViewById(R.id.btn_other);
        secondButton.setOnClickListener(listener);
        secondButton.setText(secondString);
        
        loginTypeView.findViewById(R.id.btn_cancel)
                .setOnClickListener(listener);
        chooseLoginTypePopupWindow = new PopupWindow(loginTypeView,
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        chooseLoginTypePopupWindow.setFocusable(true);
        chooseLoginTypePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        chooseLoginTypePopupWindow.showAtLocation(loginTypeView,
                Gravity.BOTTOM,
                0,
                0);
    }
    
    /**
     * 
     * 
     * 选择登录模式监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Mar 21, 2012]
     */
    class LoginTypeViewListener implements OnClickListener
    {
        
        /**
         * [一句话功能简述]<BR>
         * [功能详细描述]
         * 
         * @param v
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        
        @Override
        public void onClick(View v)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            switch (v.getId())
            {
                case R.id.btn_number:
                    isLoginByPhone = false;
                    //手机号登录
                    mstrInputPassportText.setHint(R.string.login_user_number_hint);
                    mstrInputPassportText.setText(null);
                    mstrInputPassportText.setVisibility(View.VISIBLE);
                    mstrInputPassportText.requestFocus();
                    mstrInputPassportText.setInputType(InputType.TYPE_CLASS_PHONE);
                    
                    mstrInputPasswordText.setText(null);
                    mLastLoginName.setVisibility(View.GONE);
                    selectCountryView.setVisibility(View.VISIBLE);
                    chooseLoginTypePopupWindow.dismiss();
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    //错误提示框不可见
                    mLoginResultText.setVisibility(View.GONE);
                    break;
                case R.id.btn_other:
                    isLoginByPhone = true;
                    //ID,邮箱登录
                    mstrInputPassportText.setHint(R.string.login_user_other_hint);
                    mstrInputPassportText.setText(null);
                    mstrInputPassportText.setVisibility(View.VISIBLE);
                    mstrInputPassportText.requestFocus();
                    //                    mstrInputPassportText.setInputType(InputType.TYPE_NULL);
                    
                    mstrInputPasswordText.setText(null);
                    mLastLoginName.setVisibility(View.GONE);
                    selectCountryView.setVisibility(View.GONE);
                    //错误提示框不可见
                    mLoginResultText.setVisibility(View.GONE);
                    chooseLoginTypePopupWindow.dismiss();
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
                case R.id.btn_cancel:
                    //取消
                    chooseLoginTypePopupWindow.dismiss();
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
                default:
                    break;
            }
            
        }
    }
    
    /**
     * 
     * 
     * 选择找回密码监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Mar 21, 2012]
     */
    class ResetPassWordListener implements OnClickListener
    {
        
        /**
         * [一句话功能简述]<BR>
         * [功能详细描述]
         * 
         * @param v
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        
        @Override
        public void onClick(View v)
        {
            Intent intent;
            switch (v.getId())
            {
                case R.id.btn_number:
                    //通过手机号
                    intent = new Intent(ResetPassWordAction.ACTION);
                    intent.putExtra(ResetPassWordAction.IS_PHONE_OR_EMAIN, true);
                    startActivity(intent);
                    chooseLoginTypePopupWindow.dismiss();
                    break;
                case R.id.btn_other:
                    //通过邮箱
                    intent = new Intent(ResetPassWordAction.ACTION);
                    intent.putExtra(ResetPassWordAction.IS_PHONE_OR_EMAIN,
                            false);
                    startActivity(intent);
                    chooseLoginTypePopupWindow.dismiss();
                    break;
                case R.id.btn_cancel:
                    //取消
                    chooseLoginTypePopupWindow.dismiss();
                    break;
                default:
                    break;
            }
            
        }
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
        if (!isLoginLayout)
        {
            //标题
            mTitleView.setText(R.string.app_name);
            rightButton.setText(R.string.rapidly_passport);
            mLastLoginName.setText(getResources().getString(R.string.account_number)
                    + getSharedPreferences().getString(Common.KEY_USER_PASSPORT,
                            ""));
            //手机登陆
            if (null != countryname)
            {
                mCountryName.setText(countryname + "(" + countrycode + ")");
            }
            else
            {
                mCountryName.setText(R.string.country_normal_name);
            }
            //账号
            if (isLoginByPhone)
            {
                mstrInputPassportText.setHint(R.string.login_user_number_hint);
            }
            else
            {
                mstrInputPassportText.setHint(R.string.login_user_other_hint);
            }
            //密码
            mstrInputPasswordText.setHint(R.string.login_pwd_hint);
            //找回密码
            mFindPwd.setText(R.string.login_forgot_pwd);
            //注册
            mRegeister.setText(R.string.login_regeister);
            //登陆
            loginButton.setText(R.string.login);
            //登陆错误提示
            if (mLoginResultText.getVisibility() == View.VISIBLE)
            {
                mLoginResultText.setText(mLoginError);
            }
        }
        else
        {
            mTitleView.setText(R.string.login_verify_code);
            //图形验证码
            mVerifyCodeText.setHint(R.string.login_verify_code_hint);
            //图形验证码确认
            okButton.setText(R.string.confirm);
            if (mVerifyCodeResultText.getVisibility() == View.VISIBLE)
            {
                mVerifyCodeResultText.setText(mLoginError);
            }
        }
    }
    
    /**
     * 
     * 销毁当前activity
     **/
    static void finishActivity()
    {
        if (null != currentActivtiy)
        {
            currentActivtiy.finish();
        }
    }
}
