/*
 * 文件名: VoipBasicActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周谧
 * 创建时间:5 24, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.voip;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Intents;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionAction.VoipAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.VOIPMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.logic.voip.VoipLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * VOIP基类
 * 
 * @author 周谧
 * @version [RCS Client V100R001C03, 2012-5-24]
 */
public abstract class VoipBasicActivity extends BasicActivity
{
    /**
     * TAG
     */
    private static final String TAG = "BasicActivity";
    
    /**
     * 选择呼叫方式弹出框
     */
    private PopupWindow choosecallTypePopupWindow;
    
    /**
     * 选择呼叫方式弹出框内部View
     */
    private View popView;
    
    /**
     * voip呼叫的电话
     */
    private String mPhoneNumber;
    
    /**
     * voip呼叫的电话
     */
    private IVoipLogic mVoipLogic;
    
    /**
     * 是否有Popwindow弹出
     */
    private boolean isShowPopwindow;
    
    /**
     * 
     * 启动
     * 
     * @param savedInstanceState
     *            Bundle
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mVoipLogic = (VoipLogic) super.getLogicByInterfaceClass(IVoipLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause()
    {
        closeChooseCallType(true);
        super.onPause();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        if (isShowPopwindow)
        {
            showChooseCallType(mPhoneNumber, true);
        }
    }
    
    /**
     * 选择拨打方式 原生电话和VOIP
     * @param phoneNumber phoneNumber
     * @param isResume Activity的状态
     */
    protected void showChooseCallType(String phoneNumber, boolean isResume)
    {
        if (!isResume)
        {
            isShowPopwindow = true;
        }
        
        if (isPaused())
        {
            return;
        }
        
        if (null != choosecallTypePopupWindow && choosecallTypePopupWindow.isShowing())
        {
            return;
        }
        
        LayoutInflater inflater = getLayoutInflater();
        popView = inflater.inflate(R.layout.voip_call_type, null);
        popView.findViewById(R.id.call_type_layout_normal)
                .setOnClickListener(popViewListener);
        popView.findViewById(R.id.call_type_layout_voip)
                .setOnClickListener(popViewListener);
        choosecallTypePopupWindow = new PopupWindow(popView,
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        choosecallTypePopupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
        mPhoneNumber = phoneNumber;
    }
    
    /**
     * 关闭拨号方式的popowindow
     * @param isResume Activity的状态
     * @return 关闭是否成功
     */
    protected boolean closeChooseCallType(boolean isResume)
    {
        if (!isResume)
        {
            isShowPopwindow = false;
        }
        if (choosecallTypePopupWindow != null)
        {
            choosecallTypePopupWindow.dismiss();
            choosecallTypePopupWindow = null;
            return true;
        }
        return false;
    }
    
    /**
     * 添加到本地通讯录
     * @param phoneNumber phoneNumber
     */
    protected void addToLocalContact(String phoneNumber)
    {
        LayoutInflater inflater = getLayoutInflater();
        popView = inflater.inflate(R.layout.voip_add_to_contact, null);
        popView.findViewById(R.id.btn_add).setOnClickListener(popViewListener);
        popView.findViewById(R.id.btn_cancel)
                .setOnClickListener(popViewListener);
        choosecallTypePopupWindow = new PopupWindow(popView,
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        choosecallTypePopupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
        mPhoneNumber = phoneNumber;
    }
    
    /**
     *  选择呼叫模式监听
     */
    protected View.OnClickListener popViewListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                //普通通话
                case R.id.call_type_layout_normal:
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + mPhoneNumber));
                    //添加异常处理,针对一些定制android系统(Coolpad 8870)可以禁止访问系统权限
                    try
                    {
                        startActivity(intent);
                    }
                    catch (SecurityException ex)
                    {
                        Logger.d(TAG, "call_type_layout_normal", ex);
                    }
                    mVoipLogic.addLocalContactCommunicationLog(mPhoneNumber);
                    getHandler().sendEmptyMessage(VOIPMessageType.VOIP_CALL_OUT);
                    break;
                //音频通话
                case R.id.call_type_layout_voip:
                    call(mPhoneNumber);
                    break;
                //添加到通讯录
                case R.id.btn_add:
                    intent = new Intent(
                            Intent.ACTION_INSERT,
                            Uri.withAppendedPath(Uri.parse("content://com.android.contacts"),
                                    "contacts"));
                    intent.putExtra(Intents.Insert.PHONE, mPhoneNumber);
                    //添加异常处理,针对一些定制android系统(Coolpad 8870)可以禁止访问系统权限
                    try
                    {
                        startActivity(intent);
                    }
                    catch (SecurityException ex)
                    {
                        Logger.d(TAG, "btn_add", ex);
                    }
                    break;
                default:
                    break;
            }
            isShowPopwindow = false;
            choosecallTypePopupWindow.dismiss();
            choosecallTypePopupWindow = null;
        }
    };
    
    /**
     * 拨打电话
     * 
     * @param phoneNumber
     *            电话号码
     */
    protected void call(String phoneNumber)
    {
        //没有网络时提醒
        if (LoginMessageType.NET_STATUS_DISABLE == FusionConfig.getInstance()
                .getUserStatus())
        {
            showToast(R.string.check_network);
            return;
        }
        //查询数据库是否绑定VOIP帐号
        if (null != mVoipLogic.queryVoipAccount())
        {
            //判断voip是否登录
            if (mVoipLogic.isLogin())
            {
                Intent intent = new Intent(VoipAction.ACTION_VOIP_CALLING);
                intent.putExtra(VoipAction.EXTRA_PHONE_NUMBER, phoneNumber);
                intent.putExtra(VoipAction.EXTRA_IS_CALL_OUT, true);
                startActivity(intent);
                getHandler().sendEmptyMessage(VOIPMessageType.VOIP_CALL_OUT);
            }
            else
            {
                showToast(R.string.voip_login_toast);
            }
        }
        else
        {
            //提示绑定voip帐号
            showPromptDialog(getString(R.string.voip_prompt_bind_account),
                    R.string.prompt,
                    R.string.voip_create_bind_account,
                    R.string.cancel,
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent = new Intent(
                                    SettingsAction.ACTION_ACTIVITY_BIND_VOIP);
                            startActivity(intent);
                        }
                    }, true);
        }
    }
    
}
