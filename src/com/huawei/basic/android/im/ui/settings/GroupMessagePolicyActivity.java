/*
 * 文件名: MessageTipsSettingsActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhoumi
 * 创建时间:2012-4-6
 * 
 * 修改人：hegai
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.logic.adapter.db.GroupInfoDbAdapter;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.login.receiver.ConnectionChangedReceiver;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PhotoLoader;

/**
 * 群消息接收策略设置界面
 * @author zhoumi
 * @version [RCS Client V100R001C03, 2012-4-6]
 */
public class GroupMessagePolicyActivity extends BasicActivity implements
        OnClickListener
{
    /**
     * 接收全部群消息选项卡
     */
    private ImageView mMainCheckedTV;
    
    /**
     * 群策略列表
     */
    private ListView mGroupListView;
    
    /**
     * 头像加载器
     */
    private PhotoLoader mPhotoLoader;
    
    /**
     * groupInfoModels表
     */
    private List<GroupInfoModel> groupInfoModels = new ArrayList<GroupInfoModel>();
    
    /**
     * 自定义适配器
     */
    private MyAdapter mAdapter;
    
    /**
     * 用于从数据库获取信息，会随着点击事件而改变，并最终与（数据库获取的）checks进行比对
     */
    private List<Boolean> checks = new ArrayList<Boolean>();
    
    /**
     * 处理群组业务逻辑的对象
     */
    private IGroupLogic mGroupLogic;
    
    /**
     * 全部群接收标识位
     */
    private boolean mReceiveAll;
    
    /**
     * 单个群接收标识位
     */
    private boolean mReceive;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_group_message);
        initView();
        setViewValues();
    }
    
    /**
     * 初始化控件
     */
    private void initView()
    {
        findViewById(R.id.left_button).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.set_groupmessagepolicy);
        mMainCheckedTV = (ImageView) findViewById(R.id.setting_voice_ctv_all);
        mMainCheckedTV.setOnClickListener(this);
        mGroupListView = (ListView) findViewById(R.id.setting_group_message_listview);
        mPhotoLoader = new PhotoLoader(this, R.drawable.default_contact_icon,
                52, 52, PhotoLoader.SOURCE_TYPE_GROUP, null);
    }
    
    /**
     * 初始化界面
     */
    private void setViewValues()
    {
        groupInfoModels = getDataFromDB();
        mAdapter = new MyAdapter();
        mAdapter.setData(groupInfoModels);
        mGroupListView.setAdapter(mAdapter);
        
        /**
         * 设置成多选模式
         */
        mGroupListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        /**
         * 获取策略对应的显示
         */
        if (null == groupInfoModels || groupInfoModels.size() < 1)
        {
            // 如果没有群或者没有有权限的群，则将设置选项灰化，不让用户操作
            mMainCheckedTV.setBackgroundResource(R.drawable.button_switch_off);
            mMainCheckedTV.setEnabled(false);
            mGroupListView.setVisibility(View.INVISIBLE);
        }
        else
        {
            mReceiveAll = true;
            for (int i = 0; i < groupInfoModels.size(); i++)
            {
                if (GroupInfoModel.RECVPOLICY_REFUSE == groupInfoModels.get(i)
                        .getRecvRolicy())
                {
                    checks.add(false);
                    mReceiveAll = false;
                }
                else
                {
                    checks.add(true);
                }
            }
            if (mReceiveAll)
            {
                mMainCheckedTV.setBackgroundResource(R.drawable.button_switch_on);
            }
            else
            {
                mMainCheckedTV.setBackgroundResource(R.drawable.button_switch_off);
            }
        }
    }
    
    /**
     * 获得GroupInfoModel集合
     * @return GroupInfoModel集合
     */
    private List<GroupInfoModel> getDataFromDB()
    {
        String userId = FusionConfig.getInstance()
                .getAasResult()
                .getUserSysId();
        List<GroupInfoModel> groupInfoModelList = GroupInfoDbAdapter.getInstance(this)
                .queryAll(userId);
        /**
         * 删除临时群
         */
        if (null != groupInfoModelList)
        {
            for (int j = groupInfoModelList.size() - 1; j >= 0; j--)
            {
                /**
                 * 获取这个群的类型
                 */
                String groupTypeStr = groupInfoModelList.get(j)
                        .getGroupTypeString();
                /**
                 * 获取这个群的权限
                 */
                String groupAffiliation = groupInfoModelList.get(j)
                        .getAffiliation();
                if (GroupInfoModel.GROUP_TYPE_SESSION.equals(groupTypeStr)
                        || GroupMemberModel.AFFILIATION_NONE.equals(groupAffiliation))
                {
                    groupInfoModelList.remove(j);
                }
            }
        }
        return groupInfoModelList;
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
            case R.id.left_button:
                finish();
                break;
            case R.id.setting_voice_ctv_all:
                mReceiveAll = !mReceiveAll;
                if (mReceiveAll)
                {
                    mMainCheckedTV.setBackgroundResource(R.drawable.button_switch_on);
                }
                else
                {
                    mMainCheckedTV.setBackgroundResource(R.drawable.button_switch_off);
                }
                if (null != checks)
                {
                    for (int i = checks.size() - 1; i >= 0; i--)
                    {
                        checks.set(i, mReceiveAll);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 提交修改后的群组策略信息到服务器，并修改本地数据库
     * @param index 初始提交的序列号，从0开始
     * @param hasChange 是否有修改的标识，初始为false
     */
    private void updateGroupMessagePolicy(int index, boolean hasChange)
    {
        int groupCount = checks.size();
        while (index < groupCount)
        {
            // 从群列表中取出一个群组进行处理
            GroupInfoModel group = groupInfoModels.get(index);
            int newPolicy = GroupInfoModel.RECVPOLICY_REFUSE;
            if (checks.get(index))
            {
                newPolicy = GroupInfoModel.RECVPOLICY_ACCEPT_PROMPT;
            }
            index++;
            if (group.getRecvRolicy() != newPolicy)
            {
                //先判断是否有网络，如果没有网络，直接提示失败，并且不修改本地数据库
                if (LoginMessageType.NET_STATUS_DISABLE == ConnectionChangedReceiver.checkNet(this))
                {
                    showToast(R.string.setting_update_group_message_receiver_setting_failure);
                    return;
                }
                // 有修改，则先设置修改后的值到群组中
                group.setRecvRolicy(newPolicy);
                mGroupLogic.changeMemberInfo(null,
                        null,
                        group.getRecvRolicy(),
                        group.getRecvRolicy(),
                        group.getGroupId(),
                        group);
                //                hasChange = true;
            }
        }
        //        if (index >= groupCount)
        //        {
        //            if (hasChange)
        //            {
        //                // 如果有改变，则提示用户修改成功
        //                showToast(R.string.setting_update_group_message_receiver_setting_success);
        //            }
        //            return;
        //        }
    }
    
    /**
     * 用于展示GroupInfoModel的Adapter
     * @author hegai
     * @version [RCS Client V100R001C03, 2012-4-6]
     */
    private class MyAdapter extends BaseAdapter
    {
        /**
         * groupList
         */
        private List<GroupInfoModel> groupList;
        
        /**
         * 初始化List
         * @param models models
         */
        public void setData(List<GroupInfoModel> models)
        {
            groupList = models;
        }
        
        /**
         * 获取list的大小
         * @return  list的大小
         */
        public int getCount()
        {
            return groupList != null ? groupList.size() : 0;
        }
        
        /**
         * 获取对应的群详情对象
         * @param position   行下标
         * @return 对应的群详情对象
         */
        @Override
        public Object getItem(int position)
        {
            return groupList != null ? groupList.get(position) : null;
        }
        
        /**
         * 获取行下标
         * @param position  行下标
         * @return 0
         */
        @Override
        public long getItemId(int position)
        {
            return 0;
        }
        
        /**
         * 获取行视图
         * @param position  行下标
         * @param convertView 当前视图
         * @param parent  父视图
         * @return 行视图
         */
        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent)
        {
            if (null != groupList)
            {
                convertView = RelativeLayout.inflate(GroupMessagePolicyActivity.this,
                        R.layout.vlist,
                        null);
                ImageView groupImage = (ImageView) convertView.findViewById(R.id.setting_policy_group_logo);
                GroupInfoModel gim = (GroupInfoModel) groupList.get(position);
                mPhotoLoader.loadPhoto(groupImage, gim.getFaceUrl());
                TextView textView = (TextView) convertView.findViewById(R.id.setting_policy_group_name);
                textView.setText(gim.getGroupName());
                final ImageView imageView = (ImageView) convertView.findViewById(R.id.setting_voice_ctv);
                mReceive = checks.get(position);
                if (mReceive)
                {
                    imageView.setBackgroundResource(R.drawable.button_switch_on);
                }
                else
                {
                    imageView.setBackgroundResource(R.drawable.button_switch_off);
                }
                imageView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mReceive = !mReceive;
                        if (mReceive)
                        {
                            imageView.setBackgroundResource(R.drawable.button_switch_on);
                        }
                        else
                        {
                            imageView.setBackgroundResource(R.drawable.button_switch_off);
                        }
                        checks.set(position, mReceive);
                        if (checks.contains(false))
                        {
                            mMainCheckedTV.setBackgroundResource(R.drawable.button_switch_off);
                        }
                        else
                        {
                            mMainCheckedTV.setBackgroundResource(R.drawable.button_switch_on);
                        }
                    }
                });
            }
            return convertView;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void finish()
    {
        if (null != checks)
        {
            updateGroupMessagePolicy(0, false);
        }
        super.finish();
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
