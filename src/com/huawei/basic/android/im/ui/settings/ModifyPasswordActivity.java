/*
 * 文件名: ModifyPasswordActivity.java
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

import java.util.Map;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionErrorInfo;
import com.huawei.basic.android.im.common.FusionMessageType.SettingsMessageType;
import com.huawei.basic.android.im.logic.settings.ISettingsLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.MyPopupWindow;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 修改密码界面
 * @author meiyue
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class ModifyPasswordActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * 密码最小长度
     */
    private static final int PASSWORD_MIN_LENGTH = 0x00000006;
    
    /**
     * 响应码
     */
    private static final String RET_CODE = "ret_code";
    
    /**
     * 原密码输入框
     */
    private EditText mOldPasswordEdit;
    
    /**
     * 新密码输入框
     */
    private EditText mNewPasswordEdit;
    
    /**
     * 是否显示密码图标
     */
    private ImageView mShowPasswordImage;
    
    /**
     * 密码强度图标
     */
    private ImageView mPasswordLowImg;
    
    /**
     * popupWindow
     */
    private MyPopupWindow mPopupWindow;
    
    /**
     * 逻辑对象
     */
    private ISettingsLogic mSettingsLogic;
    
    /**
     * 是否显示密码
     */
    private boolean mIsShow = false;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);
        initView();
    }
    
    /**
     * 初始化修改密码界面
     */
    private void initView()
    {
        Button backButton = (Button) findViewById(R.id.left_button);
        backButton.setText(R.string.back);
        backButton.setOnClickListener(this);
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(R.string.set_password);
        Button completeButton = (Button) findViewById(R.id.right_button);
        completeButton.setVisibility(View.VISIBLE);
        completeButton.setText(R.string.finish);
        completeButton.setOnClickListener(this);
        
        mOldPasswordEdit = (EditText) findViewById(R.id.old_password);
        mOldPasswordEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        mPasswordLowImg = (ImageView) findViewById(R.id.password_icon);
        mNewPasswordEdit = (EditText) findViewById(R.id.new_password);
        //设置密码强度改变监听事件
        mNewPasswordEdit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                String password = s.toString().trim();
                if (password.length() < 6)
                {
                    mPasswordLowImg.setImageResource(R.drawable.intensity_of_password_0);
                    return;
                }
                
                int strong = StringUtil.checkStrong(password);
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
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
        mShowPasswordImage = (ImageView) findViewById(R.id.show_password);
        mShowPasswordImage.setOnClickListener(this);
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
        // 返回按钮处理事件
            case R.id.left_button:
                finish();
                break;
            // 下一步/完成按钮处理事件
            case R.id.right_button:
                String oldPassword = mOldPasswordEdit.getText()
                        .toString()
                        .trim();
                String newPassword = mNewPasswordEdit.getText()
                        .toString()
                        .trim();
                //旧密码为空时提示
                if (StringUtil.isNullOrEmpty(oldPassword))
                {
                    mPopupWindow = new MyPopupWindow(this);
                    mPopupWindow.show(mOldPasswordEdit,
                            getResources().getString(R.string.input_old_password_not_null));
                }
                else if (StringUtil.isNullOrEmpty(newPassword))
                {
                    //新密码为空时提示
                    mPopupWindow = new MyPopupWindow(this);
                    mPopupWindow.show(mNewPasswordEdit,
                            getResources().getString(R.string.password_not_null));
                }
                else if (newPassword.length() < PASSWORD_MIN_LENGTH)
                {
                    //新密码长度小于最小长度时提示
                    mPopupWindow = new MyPopupWindow(this);
                    mPopupWindow.show(mNewPasswordEdit,
                            getResources().getString(R.string.password_length));
                }
                else if (StringUtil.equals(oldPassword, newPassword))
                {
                    //新密码和旧密码相同时提示
                    mPopupWindow = new MyPopupWindow(this);
                    mPopupWindow.show(mNewPasswordEdit,
                            getResources().getString(R.string.modify_password_not_equals));
                }
                else
                {
                    //发送修改密码请求
                    mSettingsLogic.sendModifyPassword(oldPassword, newPassword);
                }
                break;
            case R.id.show_password:
                mIsShow = !mIsShow;
                if (mIsShow)
                {
                    mShowPasswordImage.setBackgroundResource(R.drawable.icon_eye_selected);
                    mNewPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    mShowPasswordImage.setBackgroundResource(R.drawable.icon_eye_normal);
                    mNewPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mNewPasswordEdit.setSelection(mNewPasswordEdit.getText()
                        .toString()
                        .length());
                break;
            default:
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mSettingsLogic = (ISettingsLogic) getLogicByInterfaceClass(ISettingsLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
        //修改密码返回
            case SettingsMessageType.MODIFY_PASSWORD:
                Map<String, Object> responseMap = (Map<String, Object>) msg.obj;
                if (null != responseMap)
                {
                    String retCode = (String) responseMap.get(RET_CODE);
                    if (StringUtil.equals(retCode, Common.RETCODE_SUCCESS))
                    {
                        // 修改密码成功
                        showToast(R.string.modify_password_success);
                        finish();
                    }
                    else
                    {
                        showToast(FusionErrorInfo.getErrorInfo(this, retCode));
                    }
                    //                    if (StringUtil.equals(retCode, "0"))
                    //                    {
                    //                        // 修改密码成功
                    //                        showToast(R.string.modify_password_success);
                    //                        finish();
                    //                        
                    //                    }
                    //                    
                    //                    else if (StringUtil.equals(retCode, "209006004"))
                    //                    {
                    //                        // 原密码错误
                    //                        showToast(R.string.error_code_209006004);
                    //                    }
                    //                    else if (StringUtil.equals(retCode, "209006005"))
                    //                    {
                    //                        // 系统错误
                    //                        showToast(R.string.error_code_209006005);
                    //                    }
                    //                    else
                    //                    {
                    //                        showToast(R.string.modify_password_faild);
                    //                    }
                }
                else
                {
                    //网络异常
                    showToast(R.string.network_exception_modify_password_faild);
                }
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
}
