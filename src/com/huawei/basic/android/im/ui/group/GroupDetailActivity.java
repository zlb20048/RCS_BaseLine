/*
 * 文件名: GroupDetailsActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 群详情页面
 * 创建人: gaihe
 * 创建时间:2012-3-10
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.InputReasonAction;
import com.huawei.basic.android.im.common.FusionAction.GroupMemberListAction;
import com.huawei.basic.android.im.common.FusionAction.SetHeadUtilAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.GroupMessageType;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.BasicDialog;
import com.huawei.basic.android.im.ui.basic.LimitedEditText;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.UriUtil;

/**
 * 群详情页面<BR>
 * [功能详细描述]
 * @author gaihe
 * @version [RCS Client V100R001C03, 2012-3-10] 
 */
public class GroupDetailActivity extends BasicActivity implements
        OnClickListener
{
    private static final int REQUEST_CODE = 0x11111111;
    
    /**
     * 批量读取头像 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * 群LOGO
     */
    private ImageView groupLogoImg;
    
    /**
     * 群名称
     */
    private TextView groupNameText;
    
    /**
     * 群id
     */
    private TextView groupIdText;
    
    /**
     * 申请加入群组按钮
     */
    private View applyJoinGroupBtn;
    
    /**
     * 加入会话按钮
     */
    private View joinConversationBtn;
    
    /**
     * 同意拒绝加入群
     */
    private View agreeOrRefuse;
    
    /**
     * 同意加入按钮
     */
    private View agreeBtn;
    
    /**
     * 拒绝按钮
     */
    private View refuseBtn;
    
    /**
     * 群类型整行
     */
    private View groupTypeRow;
    
    /**
     * 群类型箭头
     */
    private View groupTypePointer;
    
    /**
     * 群类型
     */
    private TextView groupTypeText;
    
    /**
     * 群标签整行
     */
    private View groupLabelRow;
    
    /**
     * 群标签箭头
     */
    private View groupLabelPointer;
    
    /**
     * 群标签
     */
    private TextView groupLableText;
    
    /**
     * 群主名称
     */
    private TextView groupOwnerText;
    
    /**
     * 身份验证整行
     */
    private View groupValidateRow;
    
    /**
     * 身份验证
     */
    private TextView groupValidateText;
    
    /**
     * 群昵称
     */
    private TextView groupCardText;
    
    /**
     * 群昵称整行
     */
    private View groupCardTextRow;
    
    /**
     * 群昵称整个布局
     */
    private View groupCardTextTotalRow;
    
    /**
     * 查看群成员
     */
    private View groupMemberRow;
    
    /**
     * 成员个数
     */
    private TextView groupMemberCount;
    
    /**
     * 查看群成员整行
     */
    private View groupMemberTotalRow;
    
    /**
     * 接收群消息
     */
    private View groupMessageReceiveRow;
    
    /**
     * 群消息接收改变
     */
    private TextView groupMessageReceive;
    
    /**
     * 接收群消息整个布局
     */
    private View groupMessageReceiveTotalRow;
    
    /**
     * 群简介整行
     */
    private View groupIntroductionRow;
    
    /**
     * 群简介箭头
     */
    private View groupIntroductionPointer;
    
    /**
     * 群简介文字
     */
    private TextView groupIntroductionText;
    
    /**
     * 退出群组按钮
     */
    private Button exitBtn;
    
    /**
     * 头像设置popView
     */
    private View popView;
    
    /**
     * 头像设置弹出框
     */
    private PopupWindow popupWindow;
    
    /**
     * 群组Jid
     */
    private String groupJid;
    
    /**
     * 用户的系统id
     */
    private String mUserSysId;
    
    /**
     * 用户id
     */
    private String mUserId;
    
    /**
     * 处理群组业务逻辑的对象
     */
    private IGroupLogic mGroupLogic;
    
    /**
     * 群详情对象
     */
    private GroupInfoModel mGroupInfoModel;
    
    /**
     * 群成员对象
     */
    private GroupMemberModel mGroupMemberModel;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail);
        initView();
        //头像下载器
        mPhotoLoader = new PhotoLoader(this,
                R.drawable.default_group_head_icon, 66, 66,
                PhotoLoader.SOURCE_TYPE_GROUP, null);
        //获得登陆成功后用户的系统id
        mUserSysId = FusionConfig.getInstance().getAasResult().getUserSysId();
        //获得登陆成功后的用户id
        mUserId = FusionConfig.getInstance().getAasResult().getUserID();
        //获得跳转传递过来的GroupInfoModel对象
        mGroupInfoModel = (GroupInfoModel) getIntent().getSerializableExtra(FusionAction.GroupDetailAction.EXTRA_MODEL);
        if (null != mGroupInfoModel)
        {
            groupJid = mGroupInfoModel.getGroupId();
        }
        else
        {
            //获得跳转传递过来的groupJid
            groupJid = getIntent().getStringExtra(FusionAction.GroupDetailAction.EXTRA_GROUP_ID);
            mGroupInfoModel = mGroupLogic.getGroupInfoModelFromDB(mUserSysId,
                    groupJid);
        }
        //如果创建者昵称为空，则需要联网获取一下群组配置
        if (null != mGroupInfoModel
                && StringUtil.isNullOrEmpty(mGroupInfoModel.getGroupOwnerNick()))
        {
            mGroupLogic.getConfigInfo(groupJid);
        }
        setViewValue();
        mGroupLogic.registerGroupMemberByIdObserver(groupJid);
        mGroupLogic.registerGroupInfoByIdObserver(groupJid);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mGroupLogic.unregisterGroupMemberByIdObserver(groupJid);
        mGroupLogic.unregisterGroupInfoByIdObserver(groupJid);
    }
    
    /**
     * 
     * 初始化组件<BR>
     * 找到各个组件并设置必要的监听
     */
    private void initView()
    {
        //返回按钮
        Button backBtn = (Button) findViewById(R.id.left_button);
        //群详情
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(R.string.view_group_detail);
        //群图像
        groupLogoImg = (ImageView) findViewById(R.id.group_image);
        //群组名
        groupNameText = (TextView) findViewById(R.id.group_name);
        //群组id
        groupIdText = (TextView) findViewById(R.id.group_id);
        //申请加入群按钮
        applyJoinGroupBtn = findViewById(R.id.add_button_group);
        //进入群会话按钮
        joinConversationBtn = findViewById(R.id.join_conversation);
        //同意、拒绝邀请按钮
        agreeOrRefuse = findViewById(R.id.invite_manage_button_group);
        agreeBtn = findViewById(R.id.agree_friend_layout);
        refuseBtn = findViewById(R.id.refuse_friend_layout);
        //群类型
        groupTypeRow = findViewById(R.id.group_type_row);
        groupTypePointer = findViewById(R.id.group_type_pointer);
        groupTypeText = (TextView) findViewById(R.id.group_type);
        //群标签
        groupLabelRow = findViewById(R.id.group_label_row);
        groupLabelPointer = findViewById(R.id.group_label_pointer);
        groupLableText = (TextView) findViewById(R.id.group_label);
        //创建者
        groupOwnerText = (TextView) findViewById(R.id.group_owner);
        //身份验证
        groupValidateRow = findViewById(R.id.group_validate_row);
        groupValidateText = (TextView) findViewById(R.id.group_validate);
        //昵称
        groupCardText = (TextView) findViewById(R.id.group_card);
        groupCardTextRow = findViewById(R.id.group_card_row);
        groupCardTextTotalRow = findViewById(R.id.group_card_total_row);
        //查看群成员
        groupMemberRow = findViewById(R.id.group_member_row);
        groupMemberTotalRow = findViewById(R.id.group_member_total_row);
        groupMemberCount = (TextView) findViewById(R.id.group_member_count);
        //接受群消息
        groupMessageReceive = (TextView) findViewById(R.id.group_choose_receive);
        groupMessageReceiveRow = findViewById(R.id.group_choose_receive_row);
        groupMessageReceiveTotalRow = findViewById(R.id.group_choose_receive_total_row);
        //群简介
        groupIntroductionRow = findViewById(R.id.group_introduction_row);
        groupIntroductionPointer = findViewById(R.id.group_introduction_pointer);
        groupIntroductionText = (TextView) findViewById(R.id.group_introduction);
        //关闭按钮
        exitBtn = (Button) findViewById(R.id.group_exit_button);
        
        //设置头像设置弹出框
        LayoutInflater inflater = getLayoutInflater();
        popView = inflater.inflate(R.layout.popwindow, null);
        //        popView.findViewById(R.id.frist_btn).setOnClickListener(this);
        popView.findViewById(R.id.second_btn).setOnClickListener(this);
        popView.findViewById(R.id.third_btn).setOnClickListener(this);
        popView.findViewById(R.id.forth_btn).setOnClickListener(this);
        popupWindow = new PopupWindow(popView, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        
        backBtn.setOnClickListener(this);
        groupLogoImg.setOnClickListener(this);
        applyJoinGroupBtn.setOnClickListener(this);
        joinConversationBtn.setOnClickListener(this);
        agreeBtn.setOnClickListener(this);
        refuseBtn.setOnClickListener(this);
        groupTypeRow.setOnClickListener(this);
        groupLabelRow.setOnClickListener(this);
        groupValidateRow.setOnClickListener(this);
        groupCardTextRow.setOnClickListener(this);
        groupMemberRow.setOnClickListener(this);
        groupMessageReceiveRow.setOnClickListener(this);
        groupIntroductionRow.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        popView.setOnClickListener(this);
    }
    
    /**
     * 
     * 展示各组件的值<BR>
     * 根据不同的情况显示组件及其对应的值
     */
    private void setViewValue()
    {
        groupIdText.setText(UriUtil.getHitalkIdFromJid(groupJid));
        mGroupMemberModel = mGroupLogic.getGroupMemberModelFromDB(mUserSysId,
                groupJid,
                mUserId);
        //判断是否加入过群
        if (mGroupLogic.hasJoined(groupJid))
        {
            applyJoinGroupBtn.setVisibility(View.GONE);
            agreeOrRefuse.setVisibility(View.GONE);
            joinConversationBtn.setVisibility(View.VISIBLE);
            groupCardTextTotalRow.setVisibility(View.VISIBLE);
            groupMemberTotalRow.setVisibility(View.VISIBLE);
            groupMessageReceiveTotalRow.setVisibility(View.VISIBLE);
            //设置群昵称
            if (null != mGroupMemberModel)
            {
                groupCardText.setText(mGroupMemberModel.getMemberNick());
            }
            mGroupInfoModel = mGroupLogic.getGroupInfoModelFromDB(mUserSysId,
                    groupJid);
            //设置接收群消息策略
            groupMessageReceive.setText(mGroupInfoModel.getRecvRolicy() == GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT ? R.string.group_message_accept
                    : R.string.group_message_refuse);
            exitBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            // 如果邀请过，显示同意拒绝按钮，否则显示加入按钮
            if (mGroupLogic.hasBeInvited(groupJid))
            {
                applyJoinGroupBtn.setVisibility(View.GONE);
                agreeOrRefuse.setVisibility(View.VISIBLE);
            }
            else
            {
                applyJoinGroupBtn.setVisibility(View.VISIBLE);
                agreeOrRefuse.setVisibility(View.GONE);
            }
            joinConversationBtn.setVisibility(View.GONE);
            groupCardTextTotalRow.setVisibility(View.GONE);
            groupMemberTotalRow.setVisibility(View.GONE);
            groupMessageReceiveTotalRow.setVisibility(View.GONE);
            exitBtn.setVisibility(View.GONE);
        }
        
        if (null != mGroupInfoModel)
        {
            groupNameText.setText(mGroupInfoModel.getGroupName());
            mPhotoLoader.loadPhoto(groupLogoImg, mGroupInfoModel.getFaceUrl());
            //设置群类型
            if (mGroupInfoModel.getGroupSort() > 0)
            {
                groupTypeText.setText(mGroupLogic.getCategroyType(mGroupInfoModel.getGroupSort()));
            }
            groupLableText.setText(mGroupInfoModel.getGroupLabel());
            // 群组创建者昵称
            groupOwnerText.setText(mGroupInfoModel.getGroupOwnerNick());
            // 群组身份验证
            if (mGroupInfoModel.getGroupType() == GroupInfoModel.GROUPTYPE_LIMITED
                    || mGroupInfoModel.getGroupType() == GroupInfoModel.GROUPTYPE_OPENED)
            {
                groupValidateText.setText(mGroupLogic.getValidate(mGroupInfoModel.getGroupType()));
            }
            groupMemberCount.setText(String.format(getResources().getString(R.string.count),
                    mGroupInfoModel.getMemberCount()));
            // 设置群简介
            groupIntroductionText.setText(mGroupInfoModel.getGroupDesc());
            // 如果不是群主，需要对点击事件进行处理
            if (!GroupMemberModel.AFFILIATION_OWNER.equals(mGroupInfoModel.getAffiliation()))
            {
                groupLogoImg.setClickable(false);
                popView.setClickable(false);
                groupTypeRow.setClickable(false);
                groupTypePointer.setVisibility(View.GONE);
                groupTypeText.setPadding(0,
                        0,
                        (int) (30 * getResources().getDisplayMetrics().density + 0.5f),
                        0);
                groupLabelRow.setClickable(false);
                groupLabelPointer.setVisibility(View.GONE);
                groupLableText.setPadding(0,
                        0,
                        (int) (30 * getResources().getDisplayMetrics().density + 0.5f),
                        0);
                groupValidateRow.setVisibility(View.GONE);
                groupIntroductionRow.setClickable(false);
                groupIntroductionPointer.setVisibility(View.GONE);
                groupIntroductionText.setPadding(0,
                        0,
                        (int) (30 * getResources().getDisplayMetrics().density + 0.5f),
                        0);
            }
            else
            {
                groupValidateRow.setVisibility(View.VISIBLE);
                exitBtn.setText(R.string.group_close);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view)
    {
        if (popupWindow.isShowing())
        {
            popupWindow.dismiss();
        }
        Intent intent = new Intent();
        switch (view.getId())
        {
            case R.id.left_button:
                finish();
                break;
            case R.id.group_image:
                popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 0);
                break;
            // 系统头像设置,跳转到系统头像选择界面
            //            case R.id.frist_btn:
            //                intent.setAction(SetHeadUtilAction.ACTION);
            //                intent.putExtra(SetHeadUtilAction.EXTRA_MODE,
            //                        SetHeadUtilAction.MODE_SYSTEM);
            //                intent.putExtra(SetHeadUtilAction.EXTRA_SYSTEM_HEAD_MODE,
            //                        SetSystemHeadAction.MODE_GROUP);
            //                startActivityForResult(intent, SetHeadUtilAction.MODE_SYSTEM);
            //                break;
            
            case R.id.second_btn:
                // 相册选择头像
                intent.setAction(SetHeadUtilAction.ACTION);
                intent.putExtra(SetHeadUtilAction.EXTRA_MODE,
                        SetHeadUtilAction.MODE_FILE);
                startActivityForResult(intent, SetHeadUtilAction.MODE_FILE);
                break;
            // 拍照设置头像
            case R.id.third_btn:
                intent.setAction(SetHeadUtilAction.ACTION);
                intent.putExtra(SetHeadUtilAction.EXTRA_MODE,
                        SetHeadUtilAction.MODE_CAMERA);
                startActivityForResult(intent, SetHeadUtilAction.MODE_CAMERA);
                break;
            case R.id.forth_btn:
                popupWindow.dismiss();
                break;
            
            case R.id.group_member_row:
                intent.setAction(GroupMemberListAction.ACTION_GROUP_MEMBER_LIST);
                intent.putExtra(GroupMemberListAction.EXTRA_GROUP_ID, groupJid);
                startActivity(intent);
                break;
            case R.id.group_type_row:
                new BasicDialog.Builder(this).setTitle(R.string.group_select_type)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.group_catagroy_title),
                                mGroupInfoModel.getGroupSort() > 0 ? mGroupInfoModel.getGroupSort() - 1
                                        : 0,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        if (mGroupInfoModel.getGroupSort() != which + 1)
                                        {
                                            mGroupInfoModel.setGroupSort(which + 1);
                                            mGroupLogic.submitConfigInfo(mGroupInfoModel);
                                        }
                                        dialog.dismiss();
                                    }
                                })
                        .create()
                        .show();
                break;
            case R.id.group_label_row:
                final LimitedEditText editText = new LimitedEditText(this);
                editText.setMaxCharLength(120);
                editText.setText(groupLableText.getText());
                showTextEditDialog(R.string.group_label_dialog,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                // 判断修改后的标签是否为空，并且和之前的不同
                                final String text = editText.getText()
                                        .toString();
                                if ((null == mGroupInfoModel.getGroupLabel() && !StringUtil.isNullOrEmpty(text))
                                        || (null != mGroupInfoModel.getGroupLabel() && !mGroupInfoModel.getGroupLabel()
                                                .equals(text)))
                                {
                                    mGroupInfoModel.setGroupLabel(text);
                                    mGroupLogic.submitConfigInfo(mGroupInfoModel);
                                }
                            }
                        },
                        editText);
                break;
            case R.id.group_validate_row:
                new BasicDialog.Builder(this).setTitle(R.string.group_validate_dialog)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.group_validate_title),
                                mGroupInfoModel.getGroupType() - 1,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        if (mGroupInfoModel.getGroupType() != which + 1)
                                        {
                                            mGroupInfoModel.setGroupType(which + 1);
                                            mGroupLogic.submitConfigInfo(mGroupInfoModel);
                                        }
                                        dialog.dismiss();
                                    }
                                })
                        .create()
                        .show();
                break;
            case R.id.group_introduction_row:
                final LimitedEditText edittext = new LimitedEditText(this);
                edittext.setMaxCharLength(100);
                edittext.setText(groupIntroductionText.getText());
                showTextEditDialog(R.string.group_introduction,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                final String text = edittext.getText()
                                        .toString();
                                if (!StringUtil.isNullOrEmpty(text)
                                        && !text.equals(mGroupInfoModel.getGroupDesc()))
                                {
                                    mGroupInfoModel.setGroupDesc(text);
                                    mGroupLogic.submitConfigInfo(mGroupInfoModel);
                                }
                            }
                        },
                        edittext);
                break;
            //更改接受群消息设置
            case R.id.group_choose_receive_row:
                new BasicDialog.Builder(this).setTitle(R.string.group_message)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.group_message_receive_title),
                                mGroupInfoModel.getRecvRolicy() == GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT ? 0
                                        : 1,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        if (mGroupInfoModel.getRecvRolicy() != (which == 0 ? GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT
                                                : GroupInfoModel.RECVPOLICY_REFUSE))
                                        {
                                            mGroupInfoModel.setRecvRolicy(which == 0 ? GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT
                                                    : GroupInfoModel.RECVPOLICY_REFUSE);
                                            mGroupLogic.changeMemberInfo(mGroupMemberModel.getMemberDesc(),
                                                    mGroupMemberModel.getMemberFaceUrl(),
                                                    mGroupInfoModel.getRecvRolicy(),
                                                    mGroupInfoModel.getRecvRolicy(),
                                                    groupJid,
                                                    mGroupInfoModel);
                                        }
                                        dialog.dismiss();
                                    }
                                })
                        .create()
                        .show();
                break;
            // 进入群组聊天页面
            case R.id.join_conversation:
                Intent chatIntent = new Intent(
                        FusionAction.MultiChatAction.ACTION);
                chatIntent.putExtra(FusionAction.MultiChatAction.EXTRA_GROUP_ID,
                        mGroupInfoModel.getGroupId());
                chatIntent.putExtra(FusionAction.MultiChatAction.EXTRA_GROUP_NAME,
                        mGroupInfoModel.getGroupName());
                startActivity(chatIntent);
                break;
            //申请加入群
            case R.id.add_button_group:
                // 如果是受限群，需要填写申请理由
                if (GroupInfoModel.GROUP_TYPE_CLOSE.equals(mGroupInfoModel.getGroupTypeString()))
                {
                    intent.setAction(InputReasonAction.ACTION);
                    intent.putExtra(InputReasonAction.EXTRA_MODE,
                            InputReasonAction.MODE_REASON);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else
                {
                    mGroupLogic.joinGroup(null, mGroupInfoModel);
                }
                break;
            //同意加入群邀请
            case R.id.agree_friend_layout:
                mGroupLogic.acceptInvite(groupJid);
                break;
            //拒绝加入群邀请
            case R.id.refuse_friend_layout:
                mGroupLogic.declineInvite(groupJid,
                        getResources().getString(R.string.group_invite_refuse),
                        mGroupInfoModel.getGroupOwnerUserId());
                break;
            //更新群昵称
            case R.id.group_card_row:
                final LimitedEditText edit = new LimitedEditText(this);
                edit.setMaxCharLength(20);
                edit.setText(groupCardText.getText());
                showTextEditDialog(R.string.modify_group_card,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                if (null == mGroupMemberModel)
                                {
                                    return;
                                }
                                final String text = edit.getText().toString();
                                if (StringUtil.isNullOrEmpty(text))
                                {
                                    showToast(R.string.membernick_notnull);
                                }
                                else if (text.equals(mGroupMemberModel.getMemberNick()))
                                {
                                    showToast(R.string.membernick_notchange);
                                }
                                else
                                {
                                    mGroupMemberModel.setMemberNick(text);
                                    mGroupLogic.changeMemberNick(mGroupInfoModel,
                                            mGroupMemberModel);
                                }
                            }
                        },
                        edit);
                break;
            //退出群组或者删除群组
            case R.id.group_exit_button:
                showConfirmDialog(mGroupLogic.isOwner(groupJid) ? R.string.group_close_info
                        : R.string.group_exit_info,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                // 如果非创建者则退出群，如果是创建者则关闭群
                                if (!mGroupLogic.isOwner(groupJid))
                                {
                                    mGroupLogic.quitGroup(groupJid);
                                }
                                else
                                {
                                    mGroupLogic.destroyGroup(groupJid, null);
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (REQUEST_CODE == requestCode)
            {
                mGroupLogic.joinGroup(data.getStringExtra(InputReasonAction.OPERATE_RESULT),
                        mGroupInfoModel);
            }
            else
            {
                String url = data.getStringExtra(SetHeadUtilAction.EXTRA_URL);
                byte[] photoByte = data.getByteArrayExtra(SetHeadUtilAction.EXTRA_BYTES);
                mGroupInfoModel.setFaceUrl(url);
                mGroupInfoModel.setFaceBytes(photoByte);
                mGroupLogic.submitConfigInfo(mGroupInfoModel);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed()
    {
        //如果弹出框有显示，需要先关闭弹出框
        if (popupWindow.isShowing())
        {
            popupWindow.dismiss();
        }
        else
        {
            super.onBackPressed();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        int what = msg.what;
        Object obj = msg.obj;
        switch (what)
        {
            case GroupMessageType.GROUP_MEMBER_DB_ONE_RECORD_CHANGED:
            case GroupMessageType.GROUPINFO_DB_ONE_RECORD_CHANGED:
                if (null != obj && ((String) obj).equals(groupJid))
                {
                    GroupInfoModel gim = mGroupLogic.getGroupInfoModelFromDB(mUserSysId,
                            groupJid);
                    if (null != gim)
                    {
                        mGroupInfoModel = gim;
                    }
                    setViewValue();
                }
                break;
            case GroupMessageType.SUBMIT_CONFIGINFO_SUCCESS:
            case GroupMessageType.CHANGE_MEMBERINFO_SUCCESS:
            case GroupMessageType.CHANGE_MEMBERNICK_SUCCESS:
                showToast(R.string.groupinfo_modify_success);
                break;
            case GroupMessageType.SUBMIT_CONFIGINFO_FAILED:
            case GroupMessageType.CHANGE_MEMBERINFO_FAILED:
            case GroupMessageType.CHANGE_MEMBERNICK_FAILED:
                if (null != obj)
                {
                    showToast((String) obj);
                }
                else
                {
                    showToast(R.string.groupinfo_modify_failed);
                }
                break;
            //删除群成功
            case GroupMessageType.GROUP_DESTROY_SUCCESS:
                if (groupJid.equals((String) obj))
                {
                    showToast(R.string.group_destroy_success);
                    finish();
                }
                break;
            case GroupMessageType.GROUP_QUIT_SUCCESS:
                if (groupJid.equals((String) obj))
                {
                    showToast(R.string.group_quit_success);
                    finish();
                }
                break;
            case GroupMessageType.GROUP_DESTROYED_SUCCESS:
                if (groupJid.equals((String) obj))
                {
                    if (isPaused())
                    {
                        finish();
                    }
                    else
                    {
                        showOnlyConfirmDialog(R.string.group_destroyed_success,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        finish();
                                    }
                                });
                    }
                }
                break;
            //删除群失败
            case GroupMessageType.GROUP_DESTROY_FAILED:
                if (null != obj)
                {
                    showToast((String) obj);
                }
                else
                {
                    showToast(String.format(getResources().getString(R.string.group_destroy_failed),
                            groupNameText.getText().toString()));
                }
                break;
            //退出群成功
            case GroupMessageType.MEMBER_REMOVED_FROM_GROUP:
                if (!isPaused())
                {
                    //如果是当前用户，说明当前用户退出群
                    if (mUserId.equals((String) obj))
                    {
                        showToast(R.string.group_quit_success);
                        finish();
                    }
                    else
                    {
                        showToast(R.string.group_quited_succsee);
                    }
                }
                break;
            //退出群失败
            case GroupMessageType.GROUP_QUIT_FAILED:
                if (null != obj)
                {
                    showToast((String) obj);
                }
                else
                {
                    showToast(String.format(getResources().getString(R.string.group_quit_failed),
                            groupNameText.getText().toString()));
                }
                break;
            //被踢时在群详情界面
            case GroupMessageType.MEMBER_KICKED_FROM_GROUP:
                if (!isPaused())
                {
                    setViewValue();
                    if (groupJid.equals((String) obj))
                    {
                        showOnlyConfirmDialog(R.string.group_kicked,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                    }
                                });
                    }
                }
                else
                {
                    finish();
                }
                break;
            //加入群成功
            case GroupMessageType.JOIN_GROUP_SUCCESS:
                if (mUserId.equals((String) obj))
                {
                    showToast(R.string.group_join_success);
                }
                else
                {
                    showToast(R.string.group_joined_success);
                }
                break;
            //加入群失败
            case GroupMessageType.JOIN_GROUP_FAILED:
                if (null != obj)
                {
                    showToast((String) obj);
                }
                else
                {
                    showToast(String.format(getResources().getString(R.string.group_join_failed),
                            groupNameText.getText().toString()));
                }
                break;
            case GroupMessageType.DECLINE_INVITE_MEMBER_SUCCESS:
                finish();
                break;
            case GroupMessageType.ACCEPT_INVITE_MEMBER_FAILED:
            case GroupMessageType.DECLINE_INVITE_MEMBER_FAILED:
                if (null != obj)
                {
                    showToast((String) obj);
                }
                break;
            //请求加入群消息发送成功
            case GroupMessageType.REQUEST_MESSAGE_SEND_SUCCESS:
                // 如果是受限群，提示"验证消息已发送"
                if (GroupInfoModel.GROUP_TYPE_CLOSE.equals(mGroupInfoModel.getGroupTypeString()))
                {
                    showToast(R.string.group_send_join_reason);
                }
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initLogics()
    {
        mGroupLogic = (IGroupLogic) getLogicByInterfaceClass(IGroupLogic.class);
    }
    
}
