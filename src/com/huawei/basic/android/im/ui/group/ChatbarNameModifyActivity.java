/*
 * 文件名: ChatbarNameModifyActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: fengdai
 * 创建时间:Apr 23, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ChatbarNameModifyAction;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;

/**
 * 修改聊天页面<BR>
 * [功能详细描述]
 * @author fengdai
 * @version [RCS Client V100R001C03, Apr 23, 2012] 
 */
public class ChatbarNameModifyActivity extends Activity implements
        OnClickListener
{
    /**
     * 编辑聊吧名称的LimitedEditText
     */
    private LimitedEditText chatbarName;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_chatbar_name);
        initView();
    }
    
    private void initView()
    {
        chatbarName = (LimitedEditText) findViewById(R.id.chatbar_name);
        chatbarName.setText(getIntent().getStringExtra(ChatbarNameModifyAction.EXTRA_CHATBAR_NAME_OLD));
        //自动弹出软键盘
        chatbarName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        Button leftButton = (Button) findViewById(R.id.left_button);
        leftButton.setText(getResources().getString(R.string.cancel));
        leftButton.setOnClickListener(this);
        
        Button rightButton = (Button) findViewById(R.id.right_button);
        rightButton.setText(getResources().getString(R.string.default_save));
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setOnClickListener(this);
        
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.change_subject));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            case R.id.right_button:
                Intent intent = getIntent();
                intent.putExtra(ChatbarNameModifyAction.EXTRA_CHATBAR_NAME,
                        chatbarName.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
    
}
