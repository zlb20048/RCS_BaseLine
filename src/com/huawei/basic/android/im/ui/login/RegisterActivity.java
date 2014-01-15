/*
 * 文件名: ds.java 
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved. 
 * 描 述:[该类的简要描述] 
 * 创建人: 王媛媛
 * 创建时间:2012-2-15 
 * 修改人： 
 * 修改时间: 
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.login;

import java.util.Stack;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.FeaturesAction;
import com.huawei.basic.android.im.common.FusionAction.LoginAction;
import com.huawei.basic.android.im.common.FusionAction.RegisterAction;
import com.huawei.basic.android.im.common.FusionAction.SelectCountryAction;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionErrorInfo.LoginErrorCode;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.RegisterMessageType;
import com.huawei.basic.android.im.logic.login.ILoginLogic;
import com.huawei.basic.android.im.logic.login.IRegisterLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.MyPopupWindow;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 注册界面
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class RegisterActivity extends BasicActivity
{
    /**
     * 密码最小长度
     */
    public static final int PS_VERIFYCODE_MIN_LENGTH = 6;
    
    //    /**
    //     * TAG
    //     */
    //    private static final String TAG = "RegisterActivity";
    
    /**
     * 顶部的返回按钮 标题 下一步按钮
     */
    private Button mBackButton;
    
    /**
     * 标题
     */
    private TextView mTitleText;
    
    /**
     * 短信验证码校验成功后服务器返回的凭据
     */
    private String mCred;
    
    /**
     * 手机号码/Email输入框
     */
    private EditText mBoundInfoEdit;
    
    /**
     * email 输入框
     */
    private EditText mEmailInfoEdit;
    
    /**
     * 输入手机号或邮箱时的text
     */
    private TextView mRegisterBoundText;
    
    /**
     * 验证码输入框
     */
    private EditText mVerifyCodeEdit;
    
    /**
     * 重新获取验证码
     */
    private TextView mGetMSISDNVerifyCodeText;
    
    /**
     * 昵称输入框
     */
    private EditText mNickNameEdit;
    
    /**
     * 密码输入框
     */
    private EditText mPasswordEdit;
    
    /**
     * 是否显示密码勾选框
     */
    private CheckBox mShowPasswordBox;
    
    /**
     * 密码强度 弱
     */
    private ImageView mPasswordLowImg;
    
    /**
     * 注册后显示注册账号ＩＤ
     */
    private TextView mAccountText;
    
    /**
     * 注册成功后的信息提示
     */
    //    private TextView mSuccessInfoText;
    
    /**
     * 自动延迟登录 s
     */
    private TextView mDelayInfoText;
    
    /**
     * 填写的绑定信息（手机号或是邮箱号）
     */
    private String mBindInfo;
    
    /**
     * 验证码
     */
    private String mVerifyCode;
    
    /**
     * 密码
     */
    private String mPassword;
    
    /**
     * PopupWindow提示框
     */
    private MyPopupWindow mPopupWindow;
    
    /**
     * 注册成功后返回的account
     */
    private String mAccount;
    
    /**
     * 注册业务逻辑处理对象
     */
    private IRegisterLogic mRegisterLogic;
    
    /**
     * 点击各个按钮及TextView的监听器
     */
    private ButtonClickListener mButtonClickListener = new ButtonClickListener();
    
    /**
     * layout 栈
     */
    private Stack<Integer> mViewIdStack;
    
    /**
     * 计时器接口
     */
    private TimeControlRunnable mRunnable = null;
    
    /**
     * 登录模块的业务逻辑处理对象
     */
    private ILoginLogic mLoginLogic;
    
    /**
     * 立即登陆
     */
    private Button mLoginButton;
    
    /**
     * 填写注册信息界面的下一步按钮
     */
    private Button mNextBtnInfo;
    
    /**
     * 邮箱注册的入口
     */
    private TextView mRegisterEmail;
    
    /**
     * 国家名称textview
     */
    private TextView mCountryNameTV;
    
    /**
     * 手机注册勾选框
     */
    private CheckBox phoneCheckBox;
    
    /**
     * 邮箱注册勾选框
     */
    private CheckBox emailCheckBox;
    
    /**
     * 是否为手机注册
     */
    private boolean mIsPhoneReg = true;
    
    /**
     * 填写注册信息下一步按钮是否可用的标志
     */
    private boolean mFlag = true;
    
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
     * 生命周期入口
     * 
     * @param savedInstanceState
     *            Bundle
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeruser);
        
        // 初始化控件
        initView();
    }
    
    /**
     * 手机按键响应事件 该方法用来捕捉手机键盘被按下的事件
     * 
     * @param keyCode
     *            int
     * @param event
     *            KeyEvent
     * @return boolean
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (getCurrentViewId() == R.id.register_layout_verify_code)
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
                                back();
                            }
                            
                        });
            }
            else
            {
                back();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * 界面触摸事件处理，如果提示框处于显示状态下触摸屏幕则将其关闭
     * 
     * @param event
     *            MotionEvent
     * @return boolean
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        
        if (mPopupWindow != null && mPopupWindow.isShow())
        {
            mPopupWindow.dismiss();
        }
        return super.onTouchEvent(event);
    }
    
    /**
     * 
     * 通过 重载父类的handleStateMessage方法， 可以 实现消息处理
     * 
     * @param msg
     *            待处理的消息
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    
    @Override
    protected void handleStateMessage(Message msg)
    {
        if (currentActivtiy != this)
        {
            return;
        }
        Object obj = msg.obj;
        switch (msg.what)
        {
            
            //检测手机未绑定
            case RegisterMessageType.CHECK_MOBILE_BIND_SUCCESS:
                //按钮可点击
                findViewById(R.id.reg_next1).setClickable(true);
                mRegisterBoundText.setText(R.string.register_bound_msg);
                next(R.id.register_layout_verify_code);
                getVerifyDelay(0);
                break;
            
            // 检测邮箱未绑定
            case RegisterMessageType.CHECK_EMAIL_BIND_SUCCESS:
                mRegisterBoundText.setText(R.string.register_bound_email_msg);
                next(R.id.register_layout_verify_code);
                
                break;
            
            //手机或是邮箱已经绑定
            case RegisterMessageType.CHECK_BIND_FAILED:

                //按钮可点击
                findViewById(R.id.reg_next1).setClickable(true);
                
                if (StringUtil.equals(String.valueOf(obj), LoginErrorCode.PHONE_IS_BOUNDED))
                {
                    // 错误描述
                    showMessageWhenPop(mBoundInfoEdit, String.valueOf(obj));
                }
                else if (StringUtil.equals(String.valueOf(obj), LoginErrorCode.EMAIL_IS_BOUNDED))
                {
                    showMessageWhenPop(mEmailInfoEdit, String.valueOf(obj));
                }
                
                break;
            
            // 获取手机验证码
            case RegisterMessageType.GET_MSISDN_VERIFY_CODE_SUCCESS:
                Toast.makeText(this,
                        R.string.send_verify_code,
                        Toast.LENGTH_SHORT).show();
                
                //演示版本从服务器返回中直接获取验证码 added by zhanggj 20120509
                mVerifyCodeEdit.setText((String) obj);
                
                mRunnable = new TimeControlRunnable(
                        RegisterMessageType.GET_VERIFYCODE_TIMER);
                new Thread(mRunnable).start();
                break;
            //读取本机短信验证码
            case RegisterMessageType.GET_MSISDN_VERIFY_CODE_MESSAGE:

                //读取本机短信验证码
                if (null != obj)
                {
                    mVerifyCodeEdit.setText((String) obj);
                    mVerifyCodeEdit.setSelection(obj.toString().length());
                }
                break;
            //获取邮箱验证码
            case RegisterMessageType.GET_EMAIL_VERIFY_CODE_SUCCESS:
                Toast.makeText(this,
                        R.string.send_email_verify_code,
                        Toast.LENGTH_SHORT).show();
                
                //演示版本从服务器返回中直接获取验证码 added by zhanggj 20120509
                mVerifyCodeEdit.setText((String) obj);
                
                mRunnable = new TimeControlRunnable(
                        RegisterMessageType.GET_VERIFYCODE_TIMER);
                new Thread(mRunnable).start();
                break;
            case RegisterMessageType.GET_VERIFY_CODE_FAILED:

                getVerifyDelay(0);
                // 错误描述
                showMessageWhenPop(mVerifyCodeEdit, String.valueOf(obj));
                
                break;
            
            //检测验证码正确
            case RegisterMessageType.CHECK_VERIFY_CODE_SUCCESS:
                findViewById(R.id.reg_next2).setClickable(true);
                // 得到验证码凭证
                mCred = (String) obj;
                next(R.id.register_layout_info);
                
                //停止获取验证码的计时器
                if (null != mRunnable)
                {
                    mRunnable.cancel();
                }
                break;
            
            //检测验证码错误
            case RegisterMessageType.CHECK_VERIFY_CODE_FAILED:
                findViewById(R.id.reg_next2).setClickable(true);
                showOnlyConfirmIconDialog(R.string.prompt,
                        R.drawable.call_title_circle,
                        R.string.verification_code_error,
                        new DialogInterface.OnClickListener()
                        {
                            
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                if (null != mRunnable)
                                {
                                    //停止
                                    mRunnable.cancel();
                                }
                                
                                //将倒计时的时间隐藏
                                getVerifyDelay(0);
                                mVerifyCodeEdit.setText(null);
                                
                            }
                        });
                
                // 错误描述
                //                showMessageWhenPop(mVerifyCodeEdit, String.valueOf(obj))
                
                break;
            
            // 处理用户完成注册的消息
            case RegisterMessageType.REGISTE_ACCOUNT_SUCCESS:
                findViewById(R.id.reg_next3).setClickable(true);
                // 注册完成
                mFlag = false;
                
                mAccount = (String) obj;
                registerAccountOver(mAccount);
                break;
            case RegisterMessageType.REGISTER_ACCOUNT_FAILED:
                findViewById(R.id.reg_next3).setClickable(true);
                mFlag = true;
                errorDescription(String.valueOf(obj));
                break;
            
            // 是否 重新获取验证码延时
            case RegisterMessageType.GET_VERIFYCODE_TIMER:
                getVerifyDelay((Integer) msg.obj);
                break;
            
            // 立即登陆
            case RegisterMessageType.LOGIN_NOW_TIMER:
                loginNowDelay((Integer) msg.obj);
                break;
            
            // 连接服务器失败
            case RegisterMessageType.CONNECT_FAILED:
                //按钮可点击
                findViewById(R.id.reg_next1).setClickable(true);
                findViewById(R.id.reg_next2).setClickable(true);
                findViewById(R.id.reg_next3).setClickable(true);
                if (null != mRunnable)
                {
                    
                    //检测验证码时联网出错则需要停止计时器
                    
                    mRunnable.cancel();
                    //将倒计时的时间隐藏
                    getVerifyDelay(0);
                }
                showToast(FusionErrorInfo.getErrorInfo(this,
                        String.valueOf(obj)));
                //                showMessageWhenPop(mVerifyCodeEdit, String.valueOf(obj));
                break;
            // 登录成功
            case LoginMessageType.LOGIN_SUCCESS:
                FusionConfig.getInstance()
                        .setUserStatus(LoginMessageType.LOGIN_SUCCESS);
                //去判断是否自动登录voip
                //                mVoipLogic.autoLoginVoip();
                
                //本地保存系统id
                //                Logger.i(TAG, "SharedPreferences a "
                //                        + FusionConfig.getInstance()
                //                                .getAasResult()
                //                                .getUserSysId());
                mLoginLogic.saveAccount(this, FusionConfig.getInstance()
                        .getAasResult()
                        .getUserSysId(), FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID(), mAccount, mPassword);
                // 页面跳转
                closeProgressDialog();
                if (mPopupWindow != null && mPopupWindow.isShow())
                {
                    mPopupWindow.dismiss();
                }
                Intent intent = new Intent();
                intent.setAction(FeaturesAction.ACTION);
                startActivity(intent);
                LoginActivity.finishActivity();
                finish();
                mLoginLogic.afterLoginSuccessed(true);
                break;
            // 登录失败
            case LoginMessageType.NEED_VERIFYCODE_ERROR:
                //验证码错误
            case LoginMessageType.VERIFYCODE_ERROR:
                //http连接失败
            case LoginMessageType.LOGIN_ERROR:
                //账号或密码错误
            case LoginMessageType.ACCOUNT_OR_PASSWORD_ERROR:
                closeProgressDialog();
                startActivity(new Intent(LoginAction.ACTION));
                finish();
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * 返回信息
     * 
     * @param requestCode
     *            requestCode
     * @param resultCode
     *            resultCode
     * @param data
     *            返回的数据
     * @see android.app.Activity#onActivityResult(int, int,
     *      android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RegisterAction.REQUEST_CODE_SELECTCOUNTRY)
        {
            if (data != null)
            {
                countryname = data.getStringExtra(SelectCountryAction.COUNTRY_NAME);
                countrycode = data.getStringExtra(SelectCountryAction.COUNTRY_CODE);
                
                // 从intent获取到国家信息
                if (countryname != null && countrycode != null)
                {
                    mCountryNameTV.setText(countryname + "(" + countrycode
                            + ")");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 构造函数
     * 
     * 获取注册接口
     */
    @Override
    protected void initLogics()
    {
        mLoginLogic = (ILoginLogic) getLogicByInterfaceClass(ILoginLogic.class);
        // logic层对象
        mRegisterLogic = (IRegisterLogic) super.getLogicByInterfaceClass(IRegisterLogic.class);
    }
    
    /**
     * 服务器返回错误码描述
     * 
     * @param retCode
     */
    private void errorDescription(String resultCode)
    {
        // hideInputWindow(getCurrentFocus());
        Toast.makeText(this,
                FusionErrorInfo.getErrorInfo(this, resultCode),
                Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 初始化界面VIEW对象
     */
    private void initView()
    {
        mViewIdStack = new Stack<Integer>();
        mBackButton = (Button) findViewById(R.id.left_button);
        mTitleText = (TextView) findViewById(R.id.title);
        
        mNextBtnInfo = (Button) findViewById(R.id.reg_next3);
        // 手机号码/Email输入框
        mBoundInfoEdit = (EditText) findViewById(R.id.register_phone_number);
        mEmailInfoEdit = (EditText) findViewById(R.id.register_email_bindInfo);
        mRegisterBoundText = (TextView) findViewById(R.id.register_bound_textview);
        mRegisterEmail = (TextView) findViewById(R.id.register_email);
        
        // 验证码输入框
        mVerifyCodeEdit = (EditText) findViewById(R.id.register_verify_code);
        mGetMSISDNVerifyCodeText = (TextView) findViewById(R.id.register_re_get_verify_code);
        mNickNameEdit = (EditText) findViewById(R.id.register_nick_name);
        mPasswordEdit = (EditText) findViewById(R.id.register_password);
        mShowPasswordBox = (CheckBox) findViewById(R.id.show_password);
        mPasswordLowImg = (ImageView) findViewById(R.id.password_icon);
        mAccountText = (TextView) findViewById(R.id.register_account);
        //        mSuccessInfoText = (TextView) findViewById(R.id.register_success_info);
        mDelayInfoText = (TextView) findViewById(R.id.delay_login_info);
        mLoginButton = (Button) findViewById(R.id.register_login_button);
        // 设置初始标题
        mTitleText.setVisibility(View.VISIBLE);
        mTitleText.setText(getString(R.string.register));
        //国家信息view
        mCountryNameTV = (TextView) findViewById(R.id.reg_countryname);
        //        mCountryCodeTV = (TextView) findViewById(R.id.reg_countrycode);
        // 点击各个按钮及TextView的响应事件
        mBackButton.setOnClickListener(mButtonClickListener);
        
        //填写手机号或邮箱的下一步按钮
        findViewById(R.id.reg_next1).setOnClickListener(mButtonClickListener);
        findViewById(R.id.reg_next11).setOnClickListener(mButtonClickListener);
        
        //填写验证码的下一步按钮
        findViewById(R.id.reg_next2).setOnClickListener(mButtonClickListener);
        
        //填写用户信息的下一步按钮
        mNextBtnInfo.setOnClickListener(mButtonClickListener);
        mRegisterEmail.setOnClickListener(mButtonClickListener);
        findViewById(R.id.hitalk_protocol).setOnClickListener(mButtonClickListener);
        findViewById(R.id.hitalk_protocol2).setOnClickListener(mButtonClickListener);
        
        //选择国家码
        mCountryNameTV.setOnClickListener(mButtonClickListener);
        
        mGetMSISDNVerifyCodeText.setOnClickListener(mButtonClickListener);
        
        mShowPasswordBox.setOnClickListener(mButtonClickListener);
        mLoginButton.setOnClickListener(mButtonClickListener);
        
        //是否阅读协议
        phoneCheckBox = (CheckBox) findViewById(R.id.show_register_read);
        emailCheckBox = (CheckBox) findViewById(R.id.show_register_read2);
        
        phoneCheckBox.setOnCheckedChangeListener(new PhoneCheckBoxListener());
        emailCheckBox.setOnCheckedChangeListener(new EmailCheckBoxListener());
        // 监听密码输入框变化事件
        mPasswordEdit.addTextChangedListener(new TextWatcherListener());
        
        //手机注册“继续”按钮灰化
        findViewById(R.id.reg_next1).setClickable(false);
        findViewById(R.id.reg_next1).setEnabled(false);
        mBoundInfoEdit.addTextChangedListener(new PhoneWatcherListener());
        
        //邮箱注册“继续”按钮灰化
        findViewById(R.id.reg_next11).setClickable(false);
        findViewById(R.id.reg_next11).setEnabled(false);
        mEmailInfoEdit.addTextChangedListener(new EmailWatcherListener());
        
        //填写验证码“继续”按钮灰化
        findViewById(R.id.reg_next2).setClickable(false);
        findViewById(R.id.reg_next2).setEnabled(false);
        mVerifyCodeEdit.addTextChangedListener(new VerifyCodeWatcherListener());
        
        //填写注册信息按钮灰化
        findViewById(R.id.reg_next3).setClickable(false);
        findViewById(R.id.reg_next3).setEnabled(false);
        mNickNameEdit.addTextChangedListener(new NickNameWatcherListener());
        mPasswordEdit.addTextChangedListener(new PasswordWatcherListener());
        next(R.id.register_layout_bound);
    }
    
    /**
     * 下一步
     * 
     * @param myCurrentView
     */
    private void nextButton()
    {
        switch (getCurrentViewId())
        {
            
            // 3.1 点击“手机注册”页面的下一步按钮
            case R.id.register_layout_bound:
                mBindInfo = mBoundInfoEdit.getText().toString().trim();
                nextOnRegisterBound(mBindInfo);
                break;
            //email注册
            case R.id.register_email_layout_bound:
                mBindInfo = mEmailInfoEdit.getText().toString().trim();
                nextOnRegisterBound(mBindInfo);
                break;
            // 3.2 点击“填写验证码”页面的下一步按钮
            case R.id.register_layout_verify_code:
                mVerifyCode = mVerifyCodeEdit.getText().toString().trim();
                if (StringUtil.isNullOrEmpty(mVerifyCode)
                        || mVerifyCode.length() < PS_VERIFYCODE_MIN_LENGTH)
                {
                    showMessageWhenPop(mVerifyCodeEdit,
                            R.string.verify_code_length);
                }
                else
                {
                    findViewById(R.id.reg_next2).setClickable(false);
                    mRegisterLogic.checkVerifyCode(mVerifyCode, mBindInfo);
                }
                break;
            
            // 3.3 点击“填写注册信息”页面的下一步按钮
            case R.id.register_layout_info:
                String nickNameStr = mNickNameEdit.getText().toString().trim();
                String passwordStr = mPasswordEdit.getText().toString().trim();
                
                // 注册信息页面 下一步
                nextOnRegisterInfo(nickNameStr, passwordStr);
                
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * 注册绑定页面 下一步
     * 
     * @param boundInfo
     *            用户输入信息
     */
    private void nextOnRegisterBound(String boundInfo)
    {
        switch (getCurrentViewId())
        {
            // 3.1 点击“手机注册”页面的下一步按钮
            case R.id.register_layout_bound:
                if (phoneCheckBox.isChecked())
                {
                    if (StringUtil.isMobile(boundInfo))
                    {
                        //按钮不可点击
                        findViewById(R.id.reg_next1).setClickable(false);
                        // 发送检测手机号码是否已绑定请求
                        mRegisterLogic.checkMobileBind(boundInfo);
                    }
                    else
                    {
                        showMessageWhenPop(mBoundInfoEdit,
                                R.string.input_right_mobile);
                    }
                }
                else
                {
                    showToast(R.string.read_info);
                }
                break;
            //email注册
            case R.id.register_email_layout_bound:
                if (phoneCheckBox.isChecked())
                {
                    if (StringUtil.isEmail(boundInfo))
                    {
                        
                        // 发送检测邮箱是否已绑定请求
                        mRegisterLogic.checkEmailBind(boundInfo);
                    }
                    else
                    {
                        showMessageWhenPop(mEmailInfoEdit,
                                R.string.input_right_email);
                    }
                }
                else
                {
                    showToast(R.string.read_info);
                }
                break;
            
            default:
                break;
        }
        
    }
    
    /**
     * 注册信息页面 下一步
     * 
     * @param nick
     *            昵称
     * @param psword
     *            密码
     */
    
    private void nextOnRegisterInfo(String nick, String psword)
    {
        
        //用户名和密码不能为空
        if (StringUtil.isNullOrEmpty(nick))
        {
            showMessageWhenPop(mNickNameEdit, R.string.nick_name_not_null);
        }
        else if (StringUtil.isNullOrEmpty(psword))
        {
            showMessageWhenPop(mPasswordEdit, R.string.password_not_null);
        }
        else if (psword.length() < PS_VERIFYCODE_MIN_LENGTH)
        {
            
            //密码长度不能小于六位
            showMessageWhenPop(mPasswordEdit, R.string.password_length);
        }
        else
        {
            findViewById(R.id.reg_next3).setClickable(false);
            mPassword = psword;
            
            //注册账号
            mRegisterLogic.registeAccount(nick, mPassword, mBindInfo, mCred);
        }
    }
    
    /**
     * 显示软键盘
     * 
     * @param editText
     *            编辑框
     */
    private void showSoftKeyBoard(EditText editText)
    {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editText,
                0);
    }
    
    /**
     * 
     * 设置焦点<BR>
     * [功能详细描述]
     * 
     * @param viewId
     *            各个布局
     */
    private void setFocus(int viewId)
    {
        switch (viewId)
        {
            case R.id.register_layout_bound:
                //手机注册
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                mBoundInfoEdit.requestFocus();
                showSoftKeyBoard(mBoundInfoEdit);
                break;
            case R.id.register_email_layout_bound:
                //邮箱注册
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                mEmailInfoEdit.requestFocus();
                showSoftKeyBoard(mEmailInfoEdit);
                break;
            case R.id.deal_layout:
                //注册协议界面
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                hideInputWindow(findViewById(R.id.deal_layout));
                break;
            case R.id.register_layout_verify_code:
                // 填写验证码 
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                mVerifyCodeEdit.requestFocus();
                showSoftKeyBoard(mVerifyCodeEdit);
                break;
            case R.id.register_layout_info:
                //填写注册信息
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                mNickNameEdit.requestFocus();
                showSoftKeyBoard(mNickNameEdit);
                break;
            case R.id.register_success_layout:
                //注册成功界面 
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                hideInputWindow(findViewById(R.id.register_success_layout));
                break;
            default:
                break;
        }
    }
    
    /**
     * 数据为空.出错信息 提示
     * 
     * @param view
     *            View
     * @param i
     *            INT
     */
    private void showMessageWhenPop(View view, int resId)
    {
        if (null == mPopupWindow)
        {
            mPopupWindow = new MyPopupWindow(this);
        }
        mPopupWindow.show(view, getString(resId));
    }
    
    /**
     * 数据为空.出错信息 提示
     * 
     * @param view
     *            View
     * @param errorInfo
     *            String
     */
    private void showMessageWhenPop(View view, String errorCode)
    {
        if (null == mPopupWindow)
        {
            mPopupWindow = new MyPopupWindow(this);
        }
        mPopupWindow.show(view, FusionErrorInfo.getErrorInfo(this, errorCode));
        
    }
    
    /**
     * 注册完成
     * 
     * @param accountParam
     *            注册账号
     */
    private void registerAccountOver(String accountParam)
    {
        
        //hideInputWindow(getCurrentFocus());
        next(R.id.register_success_layout);
        
        TextView nickname = (TextView) findViewById(R.id.register_nickname);
        nickname.setText(mNickNameEdit.getText());
        mAccountText.setText(accountParam);
        
        //        if (StringUtil.isMobile(mBindInfo))
        //        {
        //            mSuccessInfoText.setText(getString(R.string.mobile_register_success_info));
        //        }
        //        else if (StringUtil.isEmail(mBindInfo))
        //        {
        //            mSuccessInfoText.setText(getString(R.string.email_register_success_info));
        //        }
        mRunnable = new TimeControlRunnable(RegisterMessageType.LOGIN_NOW_TIMER);
        
        new Thread(mRunnable).start();
    }
    
    /**
     * 重新获取验证码延时
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
        mGetMSISDNVerifyCodeText.setText(titles);
        mGetMSISDNVerifyCodeText.setTextColor(getResources().getColor(R.color.agray));
        mGetMSISDNVerifyCodeText.setClickable(false);
        
        if (time <= 0)
        {
            mGetMSISDNVerifyCodeText.setClickable(true);
            mGetMSISDNVerifyCodeText.setTextColor(getResources().getColorStateList(R.drawable.verifycode_bg));
            timeText = getResources().getString(R.string.register_fresh_code3);
            mGetMSISDNVerifyCodeText.setText(getString(R.string.register_fresh_code3));
        }
        
    }
    
    /**
     * 立即登录
     * 
     */
    private void loginNow()
    {
        mRunnable.cancel();
        mAccount = mAccountText.getText().toString();
        mLoginLogic.login(mAccount, mPassword);
        showProgressDialog(R.string.connecting);
        
    }
    
    /**
     * 立即登录 延时
     * 
     * @param time
     *            计时器
     */
    private void loginNowDelay(int time)
    {
        
        String info = getResources().getString(R.string.delay_login,
                String.valueOf(time));
        int index = info.indexOf(String.valueOf(time));
        SpannableString titles = new SpannableString(info);
        titles.setSpan(new ForegroundColorSpan(
                getResources().getColor(R.color.orange)),
                index,
                index + String.valueOf(time).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        mDelayInfoText.setText(titles);
        if (time == 0)
        {
            // 立即登录
            loginNow();
        }
    }
    
    /**
     * 
     * 更新标题栏 更新标题栏标题、左右按钮文字
     * 
     * @param currentUI
     *            当前页面id
     */
    private void updateTitle(Integer currentUI)
    {
        
        switch (currentUI)
        {
            case R.id.deal_layout:
                mBackButton.setText(R.string.back);
                mTitleText.setText(R.string.deal_title);
                ((TextView) (findViewById(R.id.tv_deal))).setText(R.string.agreement_info);
                break;
            
            // 1.2 绑定手机号码
            case R.id.register_layout_bound:

                mIsPhoneReg = true;
                //                mBoundInfoEdit.requestFocus();
                mRegisterEmail.setVisibility(View.VISIBLE);
                findViewById(R.id.reg_countryCodeLayout).setVisibility(View.VISIBLE);
                
                mBackButton.setText(R.string.back);
                mTitleText.setText(getString(R.string.register));
                //国家码
                if (null != countryname)
                {
                    mCountryNameTV.setText(countryname + "(" + countrycode
                            + ")");
                }
                else
                {
                    mCountryNameTV.setText(R.string.country_normal_name);
                }
                mBoundInfoEdit.setHint(R.string.login_user_number_hint);
                phoneCheckBox.setHint(R.string.register_read);
                ((TextView) (findViewById(R.id.hitalk_protocol))).setText(R.string.deal_title);
                mRegisterEmail.setText(R.string.register_email);
                ((Button) findViewById(R.id.reg_next1)).setText(R.string.register_continue);
                break;
            //绑定邮箱
            case R.id.register_email_layout_bound:
                mIsPhoneReg = false;
                //                mEmailInfoEdit.requestFocus();
                mRegisterEmail.setVisibility(View.INVISIBLE);
                findViewById(R.id.reg_countryCodeLayout).setVisibility(View.GONE);
                
                mBackButton.setText(R.string.back);
                mTitleText.setText(R.string.register_email);
                mEmailInfoEdit.setHint(R.string.email);
                emailCheckBox.setHint(R.string.register_read);
                ((TextView) (findViewById(R.id.hitalk_protocol2))).setText(R.string.deal_title);
                ((Button) findViewById(R.id.reg_next11)).setText(R.string.register_continue);
                break;
            // 1.3 填写验证码界面
            case R.id.register_layout_verify_code:
                //                mVerifyCodeEdit.requestFocus();
                mBackButton.setText(R.string.back);
                if (mIsPhoneReg)
                {
                    mTitleText.setText(R.string.register_phone_title);
                }
                else
                {
                    mTitleText.setText(R.string.register_email);
                }
                mRegisterBoundText.setText(R.string.register_bound_msg);
                mVerifyCodeEdit.setHint(R.string.input_verify_code);
                mGetMSISDNVerifyCodeText.setText(timeText);
                ((Button) findViewById(R.id.reg_next2)).setText(R.string.register_continue);
                break;
            
            // 1.4 填写注册信息界面
            case R.id.register_layout_info:
                //密码设为可见
                mShowPasswordBox.setChecked(true);
                mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mNextBtnInfo.setClickable(mFlag);
                //                mNickNameEdit.requestFocus();
                mBackButton.setText(R.string.back);
                mTitleText.setText(R.string.register_write_msg);
                mNickNameEdit.setHint(R.string.input_nick_name);
                mPasswordEdit.setHint(R.string.input_password);
                ((TextView) (findViewById(R.id.password_strength_text))).setText(R.string.password_strength);
                ((Button) findViewById(R.id.reg_next3)).setText(R.string.register_continue);
                break;
            case R.id.register_success_layout:
                mBackButton.setVisibility(View.INVISIBLE);
                mTitleText.setText(R.string.register_success);
                ((TextView) (findViewById(R.id.register_remember))).setText(R.string.register_remember_id);
                ((TextView) (findViewById(R.id.begin_hitalk_text))).setText(R.string.begin_hitalk);
                mLoginButton.setText(R.string.register_login);
                ((TextView) (findViewById(R.id.delay_login_info))).setText(R.string.delay_login);
                break;
            default:
                break;
        }
    }
    
    /**
     * 返回
     */
    private void back()
    {
        //注册成功页面屏蔽back键
        if (getCurrentViewId() == R.id.register_success_layout)
        {
            return;
        }
        
        mVerifyCodeEdit.setText(null);
        mNickNameEdit.setText(null);
        mPasswordEdit.setText(null);
        if (null != mRunnable)
        {
            
            //检测验证码页面返回时，需要停止计时器
            mRunnable.cancel();
            
            //将倒计时的时间隐藏
            getVerifyDelay(0);
        }
        int currentViewId = mViewIdStack.pop();
        
        // 当前View隐藏
        findViewById(currentViewId).setVisibility(View.GONE);
        if (mViewIdStack.size() > 0)
        {
            
            // 进入下一个view
            findViewById(mViewIdStack.peek()).setVisibility(View.VISIBLE);
            
            setFocus(mViewIdStack.peek());
            // 设置该view标题
            updateTitle(mViewIdStack.peek());
            //所有输入框置空
            mBoundInfoEdit.setText(null);
            mEmailInfoEdit.setText(null);
            mVerifyCodeEdit.setText(null);
            mNickNameEdit.setText(null);
            mPasswordEdit.setText(null);
        }
        else
        {
            finish();
        }
        
    }
    
    /**
     * 下一步
     * 
     * @param viewId
     *            INT
     */
    private void next(int viewId)
    {
        if (null != mRunnable)
        {
            
            //检测验证码页面返回时，需要停止计时器
            mRunnable.cancel();
            
            //将倒计时的时间隐藏
            getVerifyDelay(0);
        }
        if (mViewIdStack.size() > 0)
        {
            findViewById(mViewIdStack.peek()).setVisibility(View.GONE);
        }
        mViewIdStack.add(viewId);
        findViewById(viewId).setVisibility(View.VISIBLE);
        //        switch (viewId)
        //        {
        //            case value:
        //                
        //                break;
        //            
        //            default:
        //                break;
        //        }
        //        findViewById(viewId).requestFocus();
        //        if (viewId == PRO_VIEW_ID || viewId == SING_UP_OVER_VIEW_ID)
        //        {
        //            hideInputWindow(findViewById(viewId));
        //        }
        //        else
        //        {
        //            
        //            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //        }
        setFocus(viewId);
        // 设置该view标题
        updateTitle(viewId);
        
    }
    
    /**
     * 得到当前view
     * 
     * @return 当前VIEW ID.
     */
    private int getCurrentViewId()
    {
        return mViewIdStack.peek();
    }
    
    /**
     * 
     * 各个按钮响应事件 监听器
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-10]
     */
    private class ButtonClickListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                
                // 1.响应返回按钮
                case R.id.left_button:
                    if (getCurrentViewId() == R.id.register_layout_verify_code)
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
                                        back();
                                    }
                                    
                                });
                    }
                    else
                    {
                        back();
                    }
                    
                    break;
                
                // 2.响应查看注册协议界面
                case R.id.hitalk_protocol:
                case R.id.hitalk_protocol2:
                    next(R.id.deal_layout);
                    break;
                
                // 3.点击下一步按钮
                
                case R.id.reg_next1:
                case R.id.reg_next11:
                case R.id.reg_next2:
                    nextButton();
                    break;
                case R.id.reg_next3:
                    nextButton();
                    break;
                // 4.响应重新获取验证码事件
                case R.id.register_re_get_verify_code:
                    mVerifyCodeEdit.setText(null);
                    mRegisterLogic.getVerifyCode(mBindInfo);
                    break;
                
                // 5.响应显示密码勾选框事件
                case R.id.show_password:
                    if (mShowPasswordBox.isChecked())
                    {
                        mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    else
                    {
                        mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    mPasswordEdit.setSelection(mPasswordEdit.getText()
                            .toString()
                            .length());
                    break;
                
                // 6.响应立即登录按钮事件
                case R.id.register_login_button:

                    // 立即登录按钮响应事件
                    loginNow();
                    break;
                
                // 7.点击邮箱注册
                case R.id.register_email:
                    next(R.id.register_email_layout_bound);
                    break;
                // 选择国家码
                case R.id.reg_countryname:
                    startActivityForResult(new Intent(
                            SelectCountryAction.ACTION),
                            RegisterAction.REQUEST_CODE_SELECTCOUNTRY);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * 监听密码变化的输入框
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-10]
     */
    private class TextWatcherListener implements TextWatcher
    {
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            
        }
        
        /**
         * 监听文本变化之后
         * 
         * @param s
         *            Editable
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        public void afterTextChanged(Editable s)
        {
            // 密码
            mPassword = s.toString().trim();
            
            //密码长度小于6
            if (mPassword.length() < 6)
            {
                mPasswordLowImg.setImageResource(R.drawable.intensity_of_password_0);
                
                return;
            }
            //检测密码强度
            int strong = StringUtil.checkStrong(mPassword);
            //密码强度低
            if (strong == 1)
            {
                mPasswordLowImg.setImageResource(R.drawable.intensity_of_password_1);
            }
            //密码强度中
            else if (strong == 2)
            {
                mPasswordLowImg.setImageResource(R.drawable.intensity_of_password_2);
            }
            //密码强度高
            else if (strong == 3)
            {
                mPasswordLowImg.setImageResource(R.drawable.intensity_of_password_3);
            }
        }
        
        /**
         * 文本变化之前监听
         * 
         * @param s
         *            CharSequence
         * @param start
         *            INT
         * @param count
         *            INT
         * @param after
         *            INT
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            
        }
    }
    
    /**
     * 
     * 计时器类型(立即登录、获取验证码)
     * 
     * @version [RCS Client V100R001C03, 2012-2-23]
     */
    private class TimeControlRunnable implements Runnable
    {
        /**
         * 总时间
         */
        private int messageId;
        
        /**
         * 是否终止线程
         */
        private boolean running;
        
        public TimeControlRunnable(int messageId)
        {
            this.messageId = messageId;
        }
        
        public void cancel()
        {
            running = false;
        }
        
        @Override
        public void run()
        {
            running = true;
            
            int delayTime = 0;
            
            // 设置总时间
            if (messageId == RegisterMessageType.LOGIN_NOW_TIMER)
            {
                delayTime = FusionCode.DelayTime.LOGIN_DELAY;
            }
            else if (messageId == RegisterMessageType.GET_VERIFYCODE_TIMER)
            {
                delayTime = FusionCode.DelayTime.DELAYTIME;
            }
            while (delayTime-- > 0 && running)
            {
                Message msg = new Message();
                msg.what = messageId;
                msg.obj = delayTime;
                RegisterActivity.this.getHandler().sendMessage(msg);
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
     * 手机注册“继续”按钮监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    private class PhoneWatcherListener implements TextWatcher
    {
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            
            if (s.length() > 0 && phoneCheckBox.isChecked())
            {
                findViewById(R.id.reg_next1).setClickable(true);
                findViewById(R.id.reg_next1).setEnabled(true);
            }
            else
            {
                findViewById(R.id.reg_next1).setClickable(false);
                findViewById(R.id.reg_next1).setEnabled(false);
            }
        }
        
        /**
         * 监听文本变化之后
         * 
         * @param s
         *            Editable
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        public void afterTextChanged(Editable s)
        {
            
        }
        
        /**
         * 文本变化之前监听
         * 
         * @param s
         *            CharSequence
         * @param start
         *            INT
         * @param count
         *            INT
         * @param after
         *            INT
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            
        }
    }
    
    /**
     * 
     * Email注册“继续”按钮监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    private class EmailWatcherListener implements TextWatcher
    {
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            
            if (s.length() > 0 && emailCheckBox.isChecked())
            {
                findViewById(R.id.reg_next11).setClickable(true);
                findViewById(R.id.reg_next11).setEnabled(true);
            }
            else
            {
                findViewById(R.id.reg_next11).setClickable(false);
                findViewById(R.id.reg_next11).setEnabled(false);
            }
        }
        
        /**
         * 监听文本变化之后
         * 
         * @param s
         *            Editable
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        public void afterTextChanged(Editable s)
        {
            
        }
        
        /**
         * 文本变化之前监听
         * 
         * @param s
         *            CharSequence
         * @param start
         *            INT
         * @param count
         *            INT
         * @param after
         *            INT
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            
        }
    }
    
    /**
     * 
     * 填写验证码注册“继续”按钮监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    private class VerifyCodeWatcherListener implements TextWatcher
    {
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            
            if (s.length() > 0)
            {
                findViewById(R.id.reg_next2).setClickable(true);
                findViewById(R.id.reg_next2).setEnabled(true);
            }
            else
            {
                findViewById(R.id.reg_next2).setClickable(false);
                findViewById(R.id.reg_next2).setEnabled(false);
            }
        }
        
        /**
         * 监听文本变化之后
         * 
         * @param s
         *            Editable
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        public void afterTextChanged(Editable s)
        {
            
        }
        
        /**
         * 文本变化之前监听
         * 
         * @param s
         *            CharSequence
         * @param start
         *            INT
         * @param count
         *            INT
         * @param after
         *            INT
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            
        }
    }
    
    /**
     * 
     * 填写注册信息用户昵称监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    private class NickNameWatcherListener implements TextWatcher
    {
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            
            if (s.length() > 0 && mPasswordEdit.getText().length() > 0)
            {
                findViewById(R.id.reg_next3).setClickable(true);
                findViewById(R.id.reg_next3).setEnabled(true);
            }
            else
            {
                findViewById(R.id.reg_next3).setClickable(false);
                findViewById(R.id.reg_next3).setEnabled(false);
            }
        }
        
        /**
         * 监听文本变化之后
         * 
         * @param s
         *            Editable
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        public void afterTextChanged(Editable s)
        {
            
        }
        
        /**
         * 文本变化之前监听
         * 
         * @param s
         *            CharSequence
         * @param start
         *            INT
         * @param count
         *            INT
         * @param after
         *            INT
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            
        }
    }
    
    /**
     * 
     * 填写注册信息用户密码监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Apr 16, 2012]
     */
    private class PasswordWatcherListener implements TextWatcher
    {
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            
            if (s.length() > 0 && mNickNameEdit.getText().length() > 0)
            {
                findViewById(R.id.reg_next3).setClickable(true);
                findViewById(R.id.reg_next3).setEnabled(true);
            }
            else
            {
                findViewById(R.id.reg_next3).setClickable(false);
                findViewById(R.id.reg_next3).setEnabled(false);
            }
        }
        
        /**
         * 监听文本变化之后
         * 
         * @param s
         *            Editable
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        public void afterTextChanged(Editable s)
        {
            
        }
        
        /**
         * 文本变化之前监听
         * 
         * @param s
         *            CharSequence
         * @param start
         *            INT
         * @param count
         *            INT
         * @param after
         *            INT
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         *      int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            
        }
    }
    
    /**
     * 
     * 手机注册 阅读协议选择框监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Apr 26, 2012]
     */
    private class PhoneCheckBoxListener implements OnCheckedChangeListener
    {
        
        /**
         * 是否勾选
         * 
         * @param buttonView
         *            buttonView
         * @param isChecked
         *            isChecked
         * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton,
         *      boolean)
         */
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked)
        {
            if (isChecked && mBoundInfoEdit.getText().length() > 0)
            {
                findViewById(R.id.reg_next1).setClickable(true);
                findViewById(R.id.reg_next1).setEnabled(true);
            }
            else
            {
                findViewById(R.id.reg_next1).setClickable(false);
                findViewById(R.id.reg_next1).setEnabled(false);
            }
        }
        
    }
    
    /**
     * 
     * Email注册 阅读协议选择框监听
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Apr 26, 2012]
     */
    private class EmailCheckBoxListener implements OnCheckedChangeListener
    {
        
        /**
         * 是否勾选
         * 
         * @param buttonView
         *            buttonView
         * @param isChecked
         *            isChecked
         * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton,
         *      boolean)
         */
        
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked)
        {
            if (isChecked && mEmailInfoEdit.getText().length() > 0)
            {
                findViewById(R.id.reg_next11).setClickable(true);
                findViewById(R.id.reg_next11).setEnabled(true);
            }
            else
            {
                findViewById(R.id.reg_next11).setClickable(false);
                findViewById(R.id.reg_next11).setEnabled(false);
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
        switch (getCurrentViewId())
        {
            case R.id.register_layout_bound:
                //手机注册
                mBackButton.setText(R.string.back);
                mTitleText.setText(getString(R.string.register));
                //国家码
                if (null != countryname)
                {
                    mCountryNameTV.setText(countryname + "(" + countrycode
                            + ")");
                }
                else
                {
                    mCountryNameTV.setText(R.string.country_normal_name);
                }
                mBoundInfoEdit.setHint(R.string.login_user_number_hint);
                phoneCheckBox.setHint(R.string.register_read);
                ((TextView) (findViewById(R.id.hitalk_protocol))).setText(R.string.deal_title);
                mRegisterEmail.setText(R.string.register_email);
                ((Button) findViewById(R.id.reg_next1)).setText(R.string.register_continue);
                break;
            case R.id.register_email_layout_bound:
                //邮箱注册
                mBackButton.setText(R.string.back);
                mTitleText.setText(R.string.register_email);
                mEmailInfoEdit.setHint(R.string.email);
                emailCheckBox.setHint(R.string.register_read);
                ((TextView) (findViewById(R.id.hitalk_protocol2))).setText(R.string.deal_title);
                ((Button) findViewById(R.id.reg_next11)).setText(R.string.register_continue);
                break;
            case R.id.deal_layout:
                //注册协议界面
                mBackButton.setText(R.string.back);
                mTitleText.setText(R.string.deal_title);
                ((TextView) (findViewById(R.id.tv_deal))).setText(R.string.agreement_info);
                break;
            case R.id.register_layout_verify_code:
                // 填写验证码 
                mBackButton.setText(R.string.back);
                if (mIsPhoneReg)
                {
                    mTitleText.setText(R.string.register_phone_title);
                }
                else
                {
                    mTitleText.setText(R.string.register_email);
                }
                mRegisterBoundText.setText(R.string.register_bound_msg);
                mVerifyCodeEdit.setHint(R.string.input_verify_code);
                mGetMSISDNVerifyCodeText.setText(timeText);
                ((Button) findViewById(R.id.reg_next2)).setText(R.string.register_continue);
                break;
            case R.id.register_layout_info:
                //填写注册信息
                mBackButton.setText(R.string.back);
                mTitleText.setText(R.string.register_write_msg);
                mNickNameEdit.setHint(R.string.input_nick_name);
                mPasswordEdit.setHint(R.string.input_password);
                ((TextView) (findViewById(R.id.password_strength_text))).setText(R.string.password_strength);
                ((Button) findViewById(R.id.reg_next3)).setText(R.string.register_continue);
                break;
            case R.id.register_success_layout:
                //注册成功界面 
                mTitleText.setText(R.string.register_success);
                ((TextView) (findViewById(R.id.register_remember))).setText(R.string.register_remember_id);
                ((TextView) (findViewById(R.id.begin_hitalk_text))).setText(R.string.begin_hitalk);
                mLoginButton.setText(R.string.register_login);
                ((TextView) (findViewById(R.id.delay_login_info))).setText(R.string.delay_login);
                break;
            default:
                break;
        }
    }
    
}
