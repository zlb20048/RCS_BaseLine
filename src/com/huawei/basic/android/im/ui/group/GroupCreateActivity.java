/*
 * 文件名: GroupCreateActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 创建群组页面
 * 创建人: tjzhang
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.group;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.BasicDialog;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 群组群组页面<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public class GroupCreateActivity extends BasicActivity implements
        OnClickListener
{
    private EditText mGroupName;
    
    private TextView mGroupType;
    
    // 群类型，默认设置为1
    private int groupType = 1;
    
    private TextView mGroupValidate;
    
    // 群验证，默认设置为1，表示需要验证(close)，2代表不需要验证(open)
    private int groupValidate = 1;
    
    private EditText mGroupLabel;
    
    private EditText mGroupDes;
    
    private GroupInfoModel mGroupInfoModel;
    
    private IGroupLogic mGroupLogic;
    
    private boolean mProgressDialogIsShow;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_create);
        initView();
    }
    
    private void initView()
    {
        // 设置title相关显示
        Button cancleButton = (Button) findViewById(R.id.left_button);
        cancleButton.setOnClickListener(this);
        cancleButton.setText(R.string.cancel);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.group_create);
        Button finishButton = (Button) findViewById(R.id.right_button);
        finishButton.setText(R.string.finish_btn);
        finishButton.setVisibility(View.VISIBLE);
        finishButton.setOnClickListener(this);
        
        // 群名称
        mGroupName = (EditText) findViewById(R.id.group_name_edit);
        
        // 群类型
        View groupTypeRow = findViewById(R.id.group_type_row);
        groupTypeRow.setOnClickListener(this);
        mGroupType = (TextView) findViewById(R.id.group_type_text);
        mGroupType.setText(mGroupLogic.getCategroyType(groupType));
        
        // 群验证
        View groupValidateRow = findViewById(R.id.group_validate_row);
        groupValidateRow.setOnClickListener(this);
        mGroupValidate = (TextView) findViewById(R.id.group_validate_text);
        mGroupValidate.setText(mGroupLogic.getValidate(groupValidate));
        
        // 群标签
        mGroupLabel = (EditText) findViewById(R.id.group_lable_edit);
        // 群简介
        mGroupDes = (EditText) findViewById(R.id.group_introduction_edit);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        if (mProgressDialogIsShow)
        {
            return;
        }
        // 响应任何button事件，先关闭输入框
        hideInputWindow(mGroupName);
        switch (v.getId())
        {
            case R.id.left_button:
                finish();
                break;
            case R.id.right_button:
                //                if (!ConnectionChangedReceiver.isNet())
                //                {
                //                    showToast(R.string.group_create_fail_bynet);
                //                    return;
                //                }
                // 需要先判断自己创建的群组的上限是否已经达到最大值
                if (mGroupLogic.hasGroupReachMaxNumber())
                {
                    showToast(R.string.group_over_max);
                }
                else
                {
                    String groupName = mGroupName.getText().toString();
                    
                    if (!StringUtil.isNullOrEmpty(groupName))
                    {
                        mProgressDialogIsShow = true;
                        showProgressDialog(R.string.group_creating);
                        mGroupInfoModel = new GroupInfoModel();
                        mGroupInfoModel.setGroupName(groupName);
                        mGroupInfoModel.setGroupSort(groupType);
                        mGroupInfoModel.setGroupType(groupValidate);
                        mGroupInfoModel.setGroupLabel(mGroupLabel.getText()
                                .toString());
                        mGroupInfoModel.setGroupDesc(mGroupDes.getText()
                                .toString());
                        // 发送创建群的请求
                        mGroupLogic.createGroup(mGroupInfoModel,
                                GroupMessageType.CREATE_GROUP_FROM_GROUP);
                    }
                    else
                    {
                        showToast(R.string.group_name_null);
                    }
                    
                }
                break;
            case R.id.group_type_row:
                new BasicDialog.Builder(this).setTitle(R.string.group_select_type)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.group_catagroy_title),
                                groupType - 1,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        groupType = which + 1;
                                        mGroupType.setText(mGroupLogic.getCategroyType(groupType));
                                        dialog.dismiss();
                                    }
                                })
                        .create()
                        .show();
                break;
            case R.id.group_validate_row:
                new BasicDialog.Builder(this).setTitle(R.string.group_validate_dialog)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.group_validate_title),
                                groupValidate - 1,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        groupValidate = which + 1;
                                        mGroupValidate.setText(mGroupLogic.getValidate(groupValidate));
                                        dialog.dismiss();
                                    }
                                })
                        .create()
                        .show();
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
        mGroupLogic = (IGroupLogic) getLogicByInterfaceClass(IGroupLogic.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        closeProgressDialog();
        int what = msg.what;
        Object obj = msg.obj;
        switch (what)
        {
            case GroupMessageType.CREATE_GROUP_SUCCESS_FROM_GROUP:
                showToast(String.format(getResources().getString(R.string.group_create_success),
                        mGroupName.getText().toString()));
                finish();
                break;
            case GroupMessageType.CREATE_GROUP_FAILED_FROM_GROUP:
                // 如果创建群失败给出失败提示
                if (null != obj)
                {
                    showToast((String) obj);
                }
                else
                {
                    showToast(String.format(getResources().getString(R.string.group_create_failed),
                            mGroupName.getText().toString()));
                }
                mProgressDialogIsShow = false;
                break;
            default:
                break;
        }
    }
    
}
