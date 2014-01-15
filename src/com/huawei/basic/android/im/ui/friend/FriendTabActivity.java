/*
 * 文件名: FriendTabActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: Lidan
 * 创建时间:2012-2-12
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.ContactListAction;
import com.huawei.basic.android.im.common.FusionAction.FindFriendAction;
import com.huawei.basic.android.im.common.FusionAction.MyFriendAction;

/**
 * 好友TabActivity<BR>
 * 点击好友tab后跳转三个TAB ：通讯录、我的好友、找朋友。
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-13]
 */
public class FriendTabActivity extends TabActivity
{
    /**
     * FriendTabActivity DEBUG TAG
     */
    public static final String TAG = "FriendTabActivity";
    
    /**
     * 我的好友的tab index
     */
    public static final int FRIEND_TAB_FRIEND_INDEX = 0;
    
    /**
     * 如果需要进入某个指定tab的需要传进来的参数
     */
    public static final String BUNDLE_TAB_INDEX = "friend_tab_index";
    
    /**
     * TabActivity的TabHost对象
     */
    private TabHost mTabHost;
    
    /**
     * 自绘制TAB的按扭界面
     */
    private RelativeLayout layoutContact;
    
    /**
     * 我的好友界面
     */
    private RelativeLayout layoutMyFriend;
    
    /**
     * 添加好友界面
     */
    private RelativeLayout layoutAddFriend;
    
    /**
     * 当前的TAB ID
     */
    private int mCurrentTabId = 1;
    
    /**
     * 
     * activity生命周期 入口
     * 
     * @param savedInstanceState bundle
     * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend_tab);
        
        mCurrentTabId = getIntent().getIntExtra(BUNDLE_TAB_INDEX,
                FRIEND_TAB_FRIEND_INDEX);
        findView();
        
        // 初始化TAB数据
        initTabHost();
        
    }
    
    /**
     * 
     * 初始化TAB数据
     */
    private void initTabHost()
    {
        // 取得TabHost对象
        mTabHost = getTabHost();
        mTabHost.setup();
        
        // 设置跳转我的好友界面的intent
        Intent myfriendintent = new Intent(MyFriendAction.ACTION);
        // 设置<我的好友>标签
        mTabHost.addTab(mTabHost.newTabSpec("friend_friend")
                .setIndicator(layoutMyFriend)
                .setContent(myfriendintent));
        
        // 设置跳转联系人界面的intent
        Intent contactintent = new Intent(
                ContactListAction.ACTION_ACTIVITY_CONTACTS);
        // 设置<联系人>标签，setIndicator()此方法用来设置标签和View
        mTabHost.addTab(mTabHost.newTabSpec("friend_contact")
                .setIndicator(layoutContact)
                .setContent(contactintent));
        
        // 设置跳转找朋友界面的intent
        Intent addfriendintent = new Intent();
        addfriendintent.setAction(FindFriendAction.ACTION);
        addfriendintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addfriendintent.addCategory("android.intent.category.DEFAULT");
        
        // 设置<添加好友>标签
        mTabHost.addTab(mTabHost.newTabSpec("friend_add_friend")
                .setIndicator(layoutAddFriend)
                .setContent(addfriendintent));
        
        mTabHost.setCurrentTab(mCurrentTabId);
    }
    
    /**
     * 
     * 获得界面的必要的一些对象和元素
     */
    private void findView()
    {
        // TODO Auto-generated method stub
        // 绘制<联系人>按钮的view
        layoutContact = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.friend_tab_view, null);
        TextView viewContact = (TextView) layoutContact.findViewById(R.id.tab_btn_view);
        viewContact.setText(R.string.btn_contacttext);
        viewContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_middle));
        
        // 绘制<我的好友>按钮的view
        layoutMyFriend = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.friend_tab_view, null);
        TextView viewMyFriend = (TextView) layoutMyFriend.findViewById(R.id.tab_btn_view);
        viewMyFriend.setText(R.string.btn_myfriendtext);
        viewMyFriend.setBackgroundDrawable(getResources().getDrawable(R.drawable.voip_tab_num_pad));
        
        // 绘制<找朋友>按钮的view
        layoutAddFriend = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.friend_tab_view, null);
        TextView viewAddFriend = (TextView) layoutAddFriend.findViewById(R.id.tab_btn_view);
        viewAddFriend.setText(R.string.btn_myaddfriendtext);
        viewContact.setTextColor(Color.WHITE);
        viewAddFriend.setBackgroundDrawable(getResources().getDrawable(R.drawable.voip_tab_com_log));
        
    }
    
}