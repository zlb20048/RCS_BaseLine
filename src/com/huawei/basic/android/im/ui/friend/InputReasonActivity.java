/*
 * 文件名: InputReasonActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: fengdai
 * 创建时间:Apr 25, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.InputReasonAction;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 输入理由 输入框<BR>
 * [功能详细描述]
 * @author fengdai
 * @version [RCS Client V100R001C03, Apr 25, 2012] 
 */
public class InputReasonActivity extends BasicActivity implements
        View.OnClickListener
{
    
    /**
     * 加人请求允许输入的字符数（一个汉字按两个字符计算）
     */
    private static final int MAX_CHAR_LENGTH_REASON = 50;
    
    /**
     * 个性签名允许输入的字符数（一个汉字按两个字符计算）
     */
    private static final int MAX_CHAR_LENGTH_SIGNATURE = 100;
    
    /**
     * 从哪个页面跳转来的
     */
    private int mode;
    
    /**
     * 显示在EditText中的内容
     */
    private String mContent;
    
    /**
     * 最大字符数
     */
    private int maxCharLengh;
    
    /**
     * 编辑框中还可以输入的字符的个数
     */
    private int mCount;
    
    /**
     * 后退按钮
     */
    private Button mBackBtn;
    
    /**
     * 确定按钮
     */
    private Button mSendBtn;
    
    /**
     * 标题栏的title
     */
    private TextView mTitle;
    
    /**
     * 编辑框
     */
    private LimitedEditText mEditText;
    
    /**
     * 录入信息
     */
    private String mOperateResult;
    
    /**
     * 清空编辑框的按钮
     */
    private ImageView mCancelBtn;
    
    /**
     * 显示编辑框中还可以输入的字符的个数
     */
    private TextView mCountTv;
    
    /**
     * 
     * 初始化方法
     * [功能详细描述]
     * @param savedInstanceState savedInstanceState
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_reason);
        mode = getIntent().getIntExtra(InputReasonAction.EXTRA_MODE,
                InputReasonAction.MODE_REASON);
        //根据不同界面的需要，设置最大字符数
        maxCharLengh = mode == InputReasonAction.MODE_REASON ? MAX_CHAR_LENGTH_REASON
                : MAX_CHAR_LENGTH_SIGNATURE;
        mContent = getIntent().getStringExtra(InputReasonAction.EXTRA_CONTENT);
        initViews();
    }
    
    /**
     * 
     * 初始化界面<BR>
     * [功能详细描述]
     */
    private void initViews()
    {
        mCount = maxCharLengh;
        
        mCountTv = (TextView) findViewById(R.id.count_last);
        mCountTv.setText(String.valueOf(mCount));
        
        mBackBtn = (Button) findViewById(R.id.left_button);
        mBackBtn.setText(getResources().getString(R.string.cancel));
        mBackBtn.setOnClickListener(this);
        
        mSendBtn = (Button) findViewById(R.id.right_button);
        mSendBtn.setVisibility(View.VISIBLE);
        mSendBtn.setText(mode == InputReasonAction.MODE_REASON ? getResources().getString(R.string.send)
                : getResources().getString(R.string.default_save));
        mSendBtn.setOnClickListener(this);
        
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(mode == InputReasonAction.MODE_REASON ? getResources().getString(R.string.friendhelper_input_reason)
                : getResources().getString(R.string.self_signature_title));
        
        mEditText = (LimitedEditText) findViewById(R.id.send_content);
        mEditText.setHint(mode == InputReasonAction.MODE_REASON ? getResources().getString(R.string.friendhelper_input_reason)
                : getResources().getString(R.string.signature));
        mEditText.setMaxCharLength(maxCharLengh);
        if (null != mContent)
        {
            mEditText.setText(mContent);
            mCount = maxCharLengh
                    - StringUtil.count2BytesChar(mContent.toString());
            mCountTv.setText(String.valueOf(mCount));
        }
        mEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.addTextChangedListener(new TextWatcher()
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
                mCount = maxCharLengh
                        - StringUtil.count2BytesChar(s.toString());
                mCountTv.setText(String.valueOf(mCount));
            }
        });
        
        mCancelBtn = (ImageView) findViewById(R.id.cancel);
        //按清空按钮，清空mEditText
        mCancelBtn.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mEditText.setText("");
            }
        });
        
    }
    
    /**
     * 
     * 触发事件<BR>
     * [功能详细描述]
     * @param v 点击的view
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            // 后退按钮事件监听
            case R.id.left_button:
                finish();
                break;
            case R.id.right_button:
                // 发送加好友请求
                mOperateResult = mEditText.getEditableText().toString();
                Intent intent = new Intent();
                intent.putExtra(InputReasonAction.OPERATE_RESULT,
                        mOperateResult);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
    
}
