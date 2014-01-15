/*
 * 文件名: VoipNumberPadActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 拨号盘界面
 * 创建人: zhoumi
 * 创建时间:2012-3-13
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.voip;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionMessageType.VOIPMessageType;
import com.huawei.basic.android.im.ui.basic.VoipNumberPadUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/***
 * 输入拨打对象的打电话界面 Detailed description
 * 
 * @author zhoumi
 * @version [RCS Client V100R001C03, 2012-3-13]
 */
public class VoipNumberPadActivity extends VoipBasicActivity implements
        View.OnClickListener
{
    /**
     * 显示要拨打的电话
     */
    private EditText mInputEdt;
    
    /**
     * 删除按钮
     */
    private ImageButton mCallBtnDelete;
    
    /**
     * 添加到本地电话簿
     */
    private ImageButton mAddToLocal;
    
    /**
     * 语音按钮
     */
    private ImageButton mCallBtnAudio;
    
    /**
     * 输入框监听
     */
    private TextWatcher editWatcher = new TextWatcher()
    {
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            if (!StringUtil.isNullOrEmpty(s.toString()))
            {
                mCallBtnDelete.setEnabled(true);
                mAddToLocal.setEnabled(true);
                mCallBtnAudio.setEnabled(true);
            }
            else
            {
                mCallBtnDelete.setEnabled(false);
                mAddToLocal.setEnabled(false);
                mCallBtnAudio.setEnabled(false);
            }
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void afterTextChanged(Editable s)
        {
            // TODO Auto-generated method stub
            
        }
    };
    
    /**
     * Called when the activity is starting
     * 
     * @param savedInstanceState
     *            savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voip_phone_number_pad);
        //初始化界面
        initView();
        new VoipNumberPadUtil(this, mInputEdt, null);
    }
    
    /**
     * onClick实现
     * 
     * @param v
     *            View
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_call_video:
                addToLocalContact(mInputEdt.getText().toString().trim());
                break;
            case R.id.btn_call_voice:
                String phoneNumber = mInputEdt.getText().toString().trim();
                //判断是否输入电话号码
                if (StringUtil.isNullOrEmpty(phoneNumber))
                {
                    showToast(R.string.voip_input_number_toast);
                    return;
                }
                showChooseCallType(phoneNumber, false);
                break;
            case R.id.btn_call_del:
                backSelection();
                break;
            default:
                break;
        }
    }
    
    /**
     * onActivityResult
     * 
     * @param requestCode
     *            requestCode
     * @param resultCode
     *            resultCode
     * @param data
     *            data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 是否需要Menu菜单（退出程序）
     * 
     * @return 是否需要Menu菜单
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#isNeedMenu()
     */
    @Override
    protected boolean isNeedMenu()
    {
        return true;
    }
    
    /**
     * 初始化界面
     */
    private void initView()
    {
        //清空按钮
        mCallBtnDelete = (ImageButton) findViewById(R.id.btn_call_del);
        mCallBtnDelete.setEnabled(false);
        mCallBtnDelete.setOnClickListener(this);
        //长按清空
        mCallBtnDelete.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                deleteNumber();
                return true;
            }
        });
        
        //添加到本地联系人
        mAddToLocal = (ImageButton) findViewById(R.id.btn_call_video);
        mAddToLocal.setOnClickListener(this);
        //语音按钮
        mCallBtnAudio = (ImageButton) findViewById(R.id.btn_call_voice);
        mCallBtnAudio.setOnClickListener(this);
        mInputEdt = (EditText) findViewById(R.id.edtPhoneInput);
        mInputEdt.setInputType(InputType.TYPE_NULL);
        mInputEdt.setClickable(false);
        mInputEdt.setLongClickable(false);
        mInputEdt.addTextChangedListener(editWatcher);
        mInputEdt.setText("");
    }
    
    /**
     * 删除显示框里的号码
     */
    private void deleteNumber()
    {
        mInputEdt.setText("");
        mCallBtnDelete.setPressed(false);
    }
    
    /**
     * 退格
     */
    private void backSelection()
    {
        //获得长度
        int length = mInputEdt.getText().length();
        if (length > 0)
        {
            String input = mInputEdt.getText().toString();
            //设置号码
            mInputEdt.setText(input.subSequence(0, length - 1));
            mInputEdt.setSelection(length - 1);
        }
    }
    
    /**
     * 返回键
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        //如果有弹出框  取消弹出框
        if (closeChooseCallType(false))
        {
            return;
        }
        //切换到后台
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
                0);
        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }
    
    /**
     * 处理逻辑返回
     * 
     * @param msg
     *            msg
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            //接通电话后清除输入的号码
            case VOIPMessageType.VOIP_CALL_OUT:
                deleteNumber();
                break;
            default:
                break;
        }
    }
}
