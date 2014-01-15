/*
 * 文件名: BindVoip.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 王媛媛
 * 创建时间:2012-3-15
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.voip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 绑定VOIP账号  登录sip服务器
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-15] 
 */
public class BindVoipActivity extends BasicActivity

{
    
    /**
     * VOIP逻辑处理对象
     */
    private IVoipLogic mBindVoipLogic;
    
    /**
     * 返回按钮
     */
    private Button mBackButton;
    
    /**
     * 绑定VOIP  登录sip服务器
     */
    private Button mBindButton;
    
    /**
     * 删除并取消VOIP绑定
     */
    private Button mDeleteBindButton;
    
    /**
     * 标题：绑定VOIP 
     */
    private TextView mVoipTitleTextView;
    
    /**
     * VOIP账号输入框
     */
    private EditText mAccountEditText;
    
    /**
     * VOIP密码输入框
     */
    private EditText mPasswdEditText;
    
    /**
     * 显示绑定的按钮界面
     */
    private LinearLayout mBindLinearLayout;
    
    /**
     * 绑定成功后，显示删除并取消绑定的按钮界面
     */
    private LinearLayout mDeletebindLinearLayout;
    
    /**
     * 显示解绑的按钮界面
     */
    private LinearLayout mUnbindLinearLayout;
    
    /**
     * 显示描述信息
     */
    private LinearLayout mVoipTextLinearLayout;
    
    /**
     * 显示 “您的VOIP账号：”的TextView
     */
    private TextView mVoipAccountTextView;
    
    /**
     * 取消绑定输入密码框
     */
    private EditText mUnbindPSEditText;
    
    /**
     * voip账号
     */
    private String mAor;
    
    /**
     * 取消绑定按钮
     */
    private Button mUnBindButton;
    
    /**
     * 输入框监听
     */
    private TextWatcher textWatcher = new TextWatcher()
    {
        
        @Override
        public void afterTextChanged(Editable s)
        {
            if (s.length() > 0)
            {
                mUnBindButton.setEnabled(true);
                mUnbindPSEditText.setSelection(mUnbindPSEditText.getText()
                        .length());
            }
            else
            {
                mUnBindButton.setEnabled(false);
            }
            
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            // TODO Auto-generated method stub
            
        }
        
    };
    
    /**
     * 
     * 设置返回键
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra(FusionAction.VoipAction.EXTRAL_VOIP_AOR, mAor);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
    
    /**
     * Activity生命周期开始
     * @param savedInstanceState savedInstanceState
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.voip_bind);
        //初始化UI控件
        initView();
        
        if (null == mBindVoipLogic.queryVoipAccount()
                && mBindLinearLayout.getVisibility() == View.VISIBLE)
        {
            //如果为绑定成功或为登录成功sip服务器，则显示软键盘 ，设置光标位置在mAccountEditText
            //  mAccountEditText.requestFocus();
            
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                    InputMethodManager.SHOW_IMPLICIT);
        }
        else
        {
            mDeletebindLinearLayout.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        
        mBindVoipLogic.checkVoipLogin();
    }
    
    /**
     * 屏蔽menu键长按
     *  
     * @param event KeyEvent
     * @return boolean
     * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getRepeatCount() > 0
                && event.getKeyCode() == KeyEvent.KEYCODE_MENU)
        {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    
    /**
     * 
     *  {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        
        super.onDestroy();
        closeProgressDialog();
    }

    /**
     * {@inheritDoc}
     * @param keyCode  int 
     * @param event keyEvent
     * @return boolean
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
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
     * 
     * 通过 重载父类的handleStateMessage方法， 可以 实现消息处理
     * @param msg   待处理的消息
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        
        int type = msg.what;
        Object obj = msg.obj;
        switch (type)
        {
            
            //绑定voip失败，仍然显示绑定按钮
            case FusionMessageType.VOIPMessageType.VOIP_SHOW_BIND_BUTTON:
                closeProgressDialog();
                mBindLinearLayout.setVisibility(View.VISIBLE);
                mDeletebindLinearLayout.setVisibility(View.GONE);
                mUnbindLinearLayout.setVisibility(View.GONE);
                mVoipTitleTextView.setText(getString(R.string.bindTitle));
                
                break;
            
            //绑定voip成功则显示解绑按钮，隐藏绑定按钮
            case FusionMessageType.VOIPMessageType.VOIP_SHOW_UNBIND_BUTTON:

               
                closeProgressDialog();
                mAor = String.valueOf(obj);
                //显示VOIP账号
                mVoipAccountTextView.setText(getString(R.string.VOIP_account_title)
                        + mAor);
                mBindLinearLayout.setVisibility(View.GONE);
                mVoipTitleTextView.setText(getString(R.string.bindTitle));
                mDeletebindLinearLayout.setVisibility(View.VISIBLE);
                mVoipTextLinearLayout.setVisibility(View.VISIBLE);
                
                break;
            
            //voip绑定成功
            case FusionMessageType.VOIPMessageType.VOIP_BIND_SUCCESS:

                closeProgressDialog();
                mAor = String.valueOf(obj);
                
                //显示VOIP账号
                mVoipAccountTextView.setText(getString(R.string.VOIP_account_title)
                        + mAor);
                
                //绑定成功显示删除并取消绑定 设置标题为取消绑定VOIP
                mBindLinearLayout.setVisibility(View.GONE);
                mDeletebindLinearLayout.setVisibility(View.VISIBLE);
                mUnbindLinearLayout.setVisibility(View.GONE);
                mVoipTextLinearLayout.setVisibility(View.VISIBLE);
                mVoipTitleTextView.setText(getString(R.string.bindTitle));
                showOnlyConfirmDialog(R.string.voip_bind_success);
//                mAccountEditText.setFocusableInTouchMode(true);
//                mPasswdEditText.setFocusableInTouchMode(true);
                
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                
                break;
            
            //绑定失败
            case FusionMessageType.VOIPMessageType.VOIP_BIND_FAILED:
                closeProgressDialog();
//                mAccountEditText.setFocusableInTouchMode(true);
//                mPasswdEditText.setFocusableInTouchMode(true);
                mAccountEditText.requestFocus();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.SHOW_IMPLICIT);
                showToast(R.string.voip_bind_failed);
                break;
            case FusionMessageType.VOIPMessageType.NTE_ERROR_VOIP_BIND_FAILED:
//                mAccountEditText.setFocusableInTouchMode(true);
//                mPasswdEditText.setFocusableInTouchMode(true);
                closeProgressDialog();
                mAccountEditText.requestFocus();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.SHOW_IMPLICIT);
                showToast(R.string.voip_net_error);
                break;
            //检测本地数据库密码，密码错误
            case FusionMessageType.VOIPMessageType.VOIP_PS_ERROR:
                mUnbindPSEditText.setText("");
                mUnbindPSEditText.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                showToast(R.string.voip_ps_error);
                break;
            
            case FusionMessageType.VOIPMessageType.VOIP_ACCOUNT_PS_ERROR:
                closeProgressDialog();
                mPasswdEditText.setText("");
//                mAccountEditText.setFocusableInTouchMode(true);
//                mPasswdEditText.setFocusableInTouchMode(true);
                mPasswdEditText.requestFocus();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.SHOW_IMPLICIT);
                showToast(R.string.voip_accountorps_error);
                break;
            //voip 解绑 成功
            case FusionMessageType.VOIPMessageType.VOIP_UNBIND_SUCCESS:
                mAor = null;
                
                //解绑成功显示绑定界面，设置标题为绑定VOIP
                mBindLinearLayout.setVisibility(View.VISIBLE);
                mDeletebindLinearLayout.setVisibility(View.GONE);
                mUnbindLinearLayout.setVisibility(View.GONE);
                mVoipTextLinearLayout.setVisibility(View.VISIBLE);
                mVoipTitleTextView.setText(getString(R.string.bindTitle));
//                mAccountEditText.setFocusableInTouchMode(true);
//                mPasswdEditText.setFocusableInTouchMode(true);
                mAccountEditText.setText("");
                mPasswdEditText.setText("");
                //提示解绑成功
                showOnlyConfirmDialog(R.string.voip_unbind_success);
                
                mAccountEditText.requestFocus();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.SHOW_IMPLICIT);
                break;
           
            //解绑 失败
            case FusionMessageType.VOIPMessageType.VOIP_UNBIND_FAILED:

                showToast(R.string.voip_unbind_failed);
                break;
            
            //fast 组件未初始化
            case FusionMessageType.VOIPMessageType.VOIP_UNINIT_SDK:
                showToast(R.string.voip_uninit_sdk);
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 初始化Logic层对象
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mBindVoipLogic = (IVoipLogic) super.getLogicByInterfaceClass(IVoipLogic.class);
    }
    
    /**
     * 初始化View控件
     */
    private void initView()
    {
        
        //设置显示绑定VOIP标题
        mVoipTitleTextView = (TextView) findViewById(R.id.title);
        mVoipTitleTextView.setText(R.string.bindTitle);
        mVoipTitleTextView.setVisibility(View.VISIBLE);
        //图片和“请设置您的voip账号...”的LinearLayout
        mVoipTextLinearLayout = (LinearLayout) findViewById(R.id.voip_bindtext);
        //账号输入框
        mAccountEditText = (EditText) findViewById(R.id.bind_voip_account_edittext);
        // 密码输入框
        mPasswdEditText = (EditText) findViewById(R.id.bind_voip_ps_edittext);
       //“请设置您的voip账号...”的TextView
        mVoipAccountTextView = (TextView) findViewById(R.id.voip_account_textView);
        
        mBindLinearLayout = (LinearLayout) findViewById(R.id.bind_Voip_LinearLayout);
        mDeletebindLinearLayout = (LinearLayout) findViewById(R.id.delete_Voip_LinearLayout);
        mUnbindLinearLayout = (LinearLayout) findViewById(R.id.unbind_Voip_LinearLayout);
        mBindLinearLayout.setVisibility(View.VISIBLE);
        mDeletebindLinearLayout.setVisibility(View.GONE);
        mUnbindLinearLayout.setVisibility(View.GONE);
       
        mUnbindPSEditText = (EditText) findViewById(R.id.unbind_voip_ps_edittext);
        mUnBindButton = (Button) findViewById(R.id.btn_voip_unbind);
        mUnBindButton.setEnabled(false);
        
        //绑定VOIP
        mBindButton = (Button) findViewById(R.id.btn_voip_bind);
        mBindButton.setEnabled(false);
        
        //设置监听器
        mAccountEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0 && mPasswdEditText.getText().length() > 0)
                {
                    mBindButton.setEnabled(true);
                    mAccountEditText.setSelection(mAccountEditText.getText()
                            .toString()
                            .length());
                }
                else
                {
                    mBindButton.setEnabled(false);
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
        mPasswdEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0 && mAccountEditText.getText().length() > 0)
                {
                    mBindButton.setEnabled(true);
                    mPasswdEditText.setSelection(mPasswdEditText.getText()
                            .toString()
                            .length());
                }
                else
                {
                    mBindButton.setEnabled(false);
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
        //设置取消voip填写密码输入框监听 
        mUnbindPSEditText.addTextChangedListener(textWatcher);
        
        mBindButton.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                showProgressDialog(R.string.connecting);
                String aor = mAccountEditText.getText().toString().trim();
                String passwd = mPasswdEditText.getText().toString().trim();
                
                if (!StringUtil.isNullOrEmpty(aor)
                        && !StringUtil.isNullOrEmpty(passwd))
                {
                    
                    //mAccountEditText.setFocusableInTouchMode(false);
                    //mPasswdEditText.setFocusableInTouchMode(false);
                    //调用绑定LOGIC层绑定VOIP账号登录sip服务器
                    mBindVoipLogic.bindVoip(aor, passwd);
                   
                    
                }
                else
                {
                    showToast(R.string.voip_accountorps_notNUll);
                }
                
            }
        });
        
        //删除并取消VOIP账号
        mDeleteBindButton = (Button) findViewById(R.id.btn_delete_voip_bind);
        
        mDeleteBindButton.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                showConfirmDialog(R.string.voip_deletebind_dialog,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int whichButton)
                            {
                                mUnbindPSEditText.setText("");
                                //显示取消绑定界面 
                                unBind();
                                
                            }
                        });
                
            }
        });
        
        //返回按钮
        mBackButton = (Button) findViewById(R.id.left_button);
        mBackButton.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra(FusionAction.VoipAction.EXTRAL_VOIP_AOR, mAor);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    
    /**
     * 显示取消绑定界面 
     *  
     */
    private void unBind()
    {
        
        mUnbindPSEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //设置标题
        mVoipTitleTextView.setText(R.string.unbindTitle);
        mBindLinearLayout.setVisibility(View.GONE);
        findViewById(R.id.right_button).setVisibility(View.GONE);
        mDeletebindLinearLayout.setVisibility(View.GONE);
        mUnbindLinearLayout.setVisibility(View.VISIBLE);
        mVoipTextLinearLayout.setVisibility(View.GONE);
        //取消绑定
        mUnBindButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //获取密码
                String ps = mUnbindPSEditText.getText().toString().trim();
                if (!StringUtil.isNullOrEmpty(ps))
                {
                    //校验填写的密码和数据库中查询的密码是否相同
                    mBindVoipLogic.checkPS(ps);
                }
            }
        });
        
    }
    
}
