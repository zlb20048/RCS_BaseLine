/*
 * 文件名: ContactSectionNameActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: Lidan
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ChooseMemberAction;
import com.huawei.basic.android.im.common.FusionAction.ContactSectionNameAction;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 编辑分组名称<BR>
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-14] 
 */
public class ContactSectionNameActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * debug tag
     */
    private static final String TAG = "ContactSectionNameActivity";
    
    /**
     * 组名输入框
     */
    private EditText mInputSectionName;
    
    /**
     * 当前停留的操作入口（0 添加分组 ，1 编辑组名）
     */
    private int mCurrentEntrance = -1;
    
    /**
     * 好友逻辑接口
     */
    private IFriendLogic mFriendLogic;
    
    /**
     * 页面加载后默认显示的分组名
     */
    private String mSectionName;
    
    /**
     * onCreate<BR>
     * @param savedInstanceState savedInstanceState
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfriend_create_contact_section);
        openInputWindow();
        initView();
        
    }
    
    /**
     * onClick<BR>
     * @param v v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        // 返回
        if (v.getId() == R.id.left_button)
        {
            onBackPressed();
        }
        
        // 下一步
        else if (v.getId() == R.id.right_button)
        {
            Editable text = mInputSectionName.getText();
            if (text != null && text.length() > 0)
            {
                Log.e(TAG, "text.toString()" + text.toString());
                //当前的入口为添加分组
                if (mCurrentEntrance == ContactSectionNameAction.TYPE_ADD_SECTION)
                {
                    //判断分组名是否存在
                    if (mFriendLogic.sectionNameExist(text.toString().trim()))
                    {
                        showToast(R.string.same_section_exist);
                    }
                    else
                    {
                        Log.e(TAG,
                                "text.toString()"
                                        + text.toString()
                                        + "FLAG====="
                                        + mFriendLogic.sectionNameExist(text.toString()));
                        Intent intent = new Intent(ChooseMemberAction.ACTION);
                        intent.putExtra(ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                                ChooseMemberAction.TYPE.ADD_SECTION);
                        intent.putExtra(ChooseMemberAction.EXTRA_SECTION_NAME,
                                text.toString());
                        startActivityForResult(intent,
                                FriendMessageType.REQUEST_TO_ADD_SECTION);
                    }
                }
                //当前的入口为编辑组名
                else
                {
                    Logger.d(TAG, "onClick -------> 编辑组名入口");
                    //更新分组名
                    String newSectionName = mInputSectionName.getText()
                            .toString()
                            .trim();
                    
                    if (newSectionName.equals(mSectionName))
                    {
                        Logger.d(TAG, "组名没有发生变化，不用发送请求");
                        showToast(R.string.please_input_new_section_name);
                        return;
                    }
                    Logger.e(TAG,
                            "before sectionName===="
                                    + mSectionName
                                    + "new SectionName"
                                    + newSectionName
                                    + "FLAG====="
                                    + mFriendLogic.otherSectionNameExist(mSectionName,
                                            newSectionName.trim()));
                    if (mFriendLogic.otherSectionNameExist(mSectionName,
                            newSectionName.trim()))
                    {
                        showToast(R.string.same_section_exist);
                    }
                    else
                    {
                        String sectionId = getIntent().getStringExtra(FusionAction.ContactSectionManagerAction.EXTRA_SECTION_ID);
                        mFriendLogic.updateSectionName(newSectionName,
                                sectionId);
                    }
                }
            }
            else
            {
                showToast(R.string.section_name_not_be_empty);
            }
        }
    }
    
    /**
     * 初始化页面组件<BR>
     * 1.获取页面组件对象；2.设置显示内容及监听器
     */
    private void initView()
    {
        mInputSectionName = (EditText) findViewById(R.id.input_section_name);
        //默认分组的        
        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        Button titleLeftBtn = (Button) findViewById(R.id.left_button);
        Button titleRightBtn = (Button) findViewById(R.id.right_button);
        titleRightBtn.setVisibility(View.VISIBLE);
        
        Intent intent = getIntent();
        // 说明：标题栏及左右按钮文字设置
        mCurrentEntrance = intent.getIntExtra(ContactSectionNameAction.EXTRA_EXTRANCE_TYPE,
                ContactSectionNameAction.TYPE_ADD_SECTION);
        Logger.d(TAG, "mCurrentEntrance=" + mCurrentEntrance);
        
        if (mCurrentEntrance == ContactSectionNameAction.TYPE_ADD_SECTION)
        {
            Logger.d(TAG, "initView ------> 当前入口为增加分组");
            title.setText(R.string.add_contact_section);
            titleRightBtn.setText(R.string.next);
        }
        else
        {
            Logger.d(TAG, "initView ------> 当前入口编辑分组名");
            mSectionName = intent.getStringExtra(ContactSectionNameAction.EXTRA_SECTION_NAME)
                    .trim();
            title.setText(R.string.edit_contact_section_name);
            titleRightBtn.setText(R.string.finish);
            if (mSectionName != null)
            {
                mInputSectionName.setText(mSectionName);
                
                //设置光标位置
                mInputSectionName.setSelection(mSectionName.length());
            }
        }
        
        // 设置按钮监听器
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn.setOnClickListener(this);
    }
    
    /**
     * 请求结果<BR>
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data data
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case FriendMessageType.REQUEST_TO_ADD_SECTION:
                if (resultCode == RESULT_OK)
                {
                    finish();
                }
                break;
            case FriendMessageType.REQUEST_UPDATE_SECTION_NAME:
                setResult(RESULT_OK);
                finish();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 初始化逻辑<BR>
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics()
     */
    
    @Override
    protected void initLogics()
    {
        mFriendLogic = (IFriendLogic) getLogicByInterfaceClass(IFriendLogic.class);
    }
    
    /**
     * 
     * 处理消息<BR>
     * [功能详细描述]
     * @param msg msg
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            case FriendMessageType.REQUEST_UPDATE_SECTION_NAME:
                //            case FriendMessageType.REQUEST_DELETE_SECTION:
                setResult(RESULT_OK);
                finish();
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 
     * 打开软键盘
     * [功能详细描述]
     */
    protected void openInputWindow()
    {
        //如果直接把它放到onCreate()里面会发现根本没用，因为Activity启动时需要一点时间来初始化
        //加载页面后一秒内显示键盘，SHOW_FORCED强制显示
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                InputMethodManager imm = (InputMethodManager) ContactSectionNameActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
            
        },
                500);
        
    }
}
