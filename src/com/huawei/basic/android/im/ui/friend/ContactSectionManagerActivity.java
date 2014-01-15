/*
 * 文件名: ContactSectionManagerActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.ChooseMemberAction;
import com.huawei.basic.android.im.common.FusionAction.ContactSectionManagerAction;
import com.huawei.basic.android.im.common.FusionAction.ContactSectionNameAction;
import com.huawei.basic.android.im.common.FusionMessageType.FriendMessageType;
import com.huawei.basic.android.im.logic.friend.IFriendLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 好友分组的管理UI<BR>
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-13]
 */
public class ContactSectionManagerActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * debug tag
     */
    private static final String TAG = "ContactSectionManagerActivity";
    
    /**
     * 请求添加分组
     */
    private static final int REQUEST_CODE_REMOVE_SECTION_MEMBER = 0x00000001;
    
    /**
     * 请求修改分组名
     */
    private static final int REQUEST_CODE_UPDATE_SECTION_NAME = 0x00000002;
    
    /**
     * 请求添加成员到分组
     */
    private static final int REQUEST_CODE_ADD_MEMBER_TO_SECTION = 0x00000003;
    
    /**
     * 分组ID
     */
    private String mContactSectionId;
    
    /**
     * 分组名称 
     */
    private String mContactSectionName;
    
    /**
     * 逻辑接口
     */
    private IFriendLogic mFriendLogic;
    
    /**
     * 页面组件的按键监听处理<BR>
     * @param v View
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            
            case R.id.left_button:
                onBackPressed();
                break;
            //编辑组名
            case R.id.edit_contact_section_name:
            {
                Log.d(TAG, "onClick ---> edit contact section name");
                Intent intent = new Intent(
                        FusionAction.ContactSectionNameAction.ACTION);
                intent.putExtra(ContactSectionNameAction.EXTRA_EXTRANCE_TYPE,
                        ContactSectionNameAction.TYPE_EDIT_SECTION);
                intent.putExtra(FusionAction.ContactSectionNameAction.EXTRA_SECTION_NAME,
                        mContactSectionName);
                intent.putExtra(FusionAction.ContactSectionManagerAction.EXTRA_SECTION_ID,
                        mContactSectionId);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_SECTION_NAME);
                break;
            }
            case R.id.add_member:
            {
                Log.d(TAG, "onClick ---> add member");
                Intent intent = new Intent(ChooseMemberAction.ACTION);
                //添加联系人到分组
                intent.putExtra(ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                        ChooseMemberAction.TYPE.ADD_CONTACT_TO_SECTION);
                intent.putExtra(FusionAction.ContactSectionManagerAction.EXTRA_SECTION_ID,
                        mContactSectionId);
                startActivityForResult(intent,
                        REQUEST_CODE_ADD_MEMBER_TO_SECTION);
                break;
            }
                //移除成员入口
            case R.id.remove_member:
            {
                Log.d(TAG, "onClick ---> remove member");
                Intent intent = new Intent();
                intent.setAction(ChooseMemberAction.ACTION);
                intent.putExtra(ChooseMemberAction.EXTRA_SECTION_ID,
                        mContactSectionId);
                intent.putExtra(ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                        ChooseMemberAction.TYPE.REMOVE_MEMBER);
                startActivityForResult(intent,
                        REQUEST_CODE_REMOVE_SECTION_MEMBER);
                break;
            }
                //群发消息入口
            case R.id.fan_out_message:
            {
                Log.d(TAG, "onClick ---> fan out message");
                break;
            }
            case R.id.delete_contact_section:
            {
                Log.d(TAG, "onClick ---> delete contact section");
                
                super.showMessageDialog(R.string.sure_delete_section,
                        0,
                        0,
                        R.string.done,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                mFriendLogic.deleteSection(mContactSectionId);
                            }
                        },
                        R.string.cancel,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                dialog.dismiss();
                            }
                        });
                break;
            }
            default:
                break;
        }
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param msg msg
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            case FriendMessageType.REQUEST_DELETE_SECTION:
                finish();
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 
     * onCreate()<BR>
     * [功能详细描述]
     * 
     * @param savedInstanceState Bundle
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfriend_contact_section_manager);
        
        Intent intent = getIntent();
        
        //获取分组ID
        mContactSectionId = intent.getStringExtra(ContactSectionManagerAction.EXTRA_SECTION_ID);
        //获取分组名称
        mContactSectionName = intent.getStringExtra(ContactSectionManagerAction.EXTRA_SECTION_NAME);
        
        initView();
    }
    
    /**
     * 
     * 初始化页面组件<BR>
     * [功能详细描述]
     */
    private void initView()
    {
        
        // 标题
        TextView title = (TextView) findViewById(R.id.title);
        // 标题栏左按钮
        Button leftButton = (Button) findViewById(R.id.left_button);
        // 标题栏右按钮
        Button rightButton = (Button) findViewById(R.id.right_button);
        // 显示组名layout
        LinearLayout editName = (LinearLayout) findViewById(R.id.edit_contact_section_name);
        // 组名
        TextView sectionName = (TextView) findViewById(R.id.contact_section_name);
        // 添加成员按钮
        LinearLayout addMember = (LinearLayout) findViewById(R.id.add_member);
        // 移除成员按钮
        LinearLayout removeMember = (LinearLayout) findViewById(R.id.remove_member);
        // 群发消息按钮
        LinearLayout fanoutMessage = (LinearLayout) findViewById(R.id.fan_out_message);
        // 删除分组按钮
        Button deleteContactSection = (Button) findViewById(R.id.delete_contact_section);
        
        // 设置标题
        title.setText(mContactSectionName);
        // 无右按钮
        rightButton.setVisibility(View.INVISIBLE);
        // 分组名
        sectionName.setText(mContactSectionName);
        
        // 设置监听器
        leftButton.setOnClickListener(this);
        editName.setOnClickListener(this);
        addMember.setOnClickListener(this);
        removeMember.setOnClickListener(this);
        fanoutMessage.setOnClickListener(this);
        deleteContactSection.setOnClickListener(this);
        
    }
    
    /**
     * 初始化逻辑<BR>
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mFriendLogic = (IFriendLogic) this.getLogicByInterfaceClass(IFriendLogic.class);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
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
            case REQUEST_CODE_ADD_MEMBER_TO_SECTION:
            case REQUEST_CODE_REMOVE_SECTION_MEMBER:
            case REQUEST_CODE_UPDATE_SECTION_NAME:
                if (resultCode == RESULT_OK)
                {
                    finish();
                }
                break;
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
}
