/*
 * 文件名:MainTabActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 基础的界面框架 含五个功能TAB
 * 创建人: deanye
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.FriendTabAction;
import com.huawei.basic.android.im.common.FusionAction.GroupListAction;
import com.huawei.basic.android.im.common.FusionAction.LoginAction;
import com.huawei.basic.android.im.common.FusionAction.SettingsAction;
import com.huawei.basic.android.im.common.FusionAction.VoipAction;
import com.huawei.basic.android.im.common.FusionCode.Common;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.ConversationMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.LoginMessageType;
import com.huawei.basic.android.im.component.notification.NotificationEntity;
import com.huawei.basic.android.im.logic.im.IConversationLogic;
import com.huawei.basic.android.im.logic.notification.IMNotificationEntity;
import com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic;
import com.huawei.basic.android.im.ui.basic.BasicTabActivity;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 
 * 基础的界面框架 含五个功能TAB 会话 \广播\好友\群\更多
 * 
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public class MainTabActivity extends BasicTabActivity implements
        TabHost.OnTabChangeListener
{
    /**
     * 记录Tab中小圈圈中最大记录条数，如果超过这个记录，显示 MAX_RECORD_COUNT+,这边显示 99+
     */
    public static final int MAX_RECORD_COUNT = 99;
    
    /**
     * tab id talk
     */
    private static final int TAB_TALK = 0;
    
    /**
     * tab id friend
     */
    private static final int TAB_FRIEND = 1;
    
    /**
     * tab id voip
     */
    private static final int TAB_VOIP = 2;
    
    /**
     * tab id group
     */
    private static final int TAB_GROUP = 3;
    
    /**
     * tab id more
     */
    private static final int TAB_MORE = 4;
    
    /**
     * 通话记录未读数的TextView
     */
    private TextView mCommunicationLogUnreadNumber;
    
    /**
     * 通话记录未读的TextView
     */
    private TextView mConversationUnReadNumber;
    
    /**
     * 自绘制下面的五个TAB的按扭界面
     */
    private RelativeLayout layoutTabTalk;
    
    private RelativeLayout layoutTabFriend;
    
    private RelativeLayout layoutTabGroup;
    
    private RelativeLayout layoutTabMore;
    
    private RelativeLayout layoutTabVoip;
    
    private IConversationLogic mConversationLogic;
    
    /**
     * 通话记录逻辑处理类
     */
    private ICommunicationLogLogic mCommunicationLogLogic;
    
    /**
     * TabActivity的TabHost对象
     */
    private TabHost mTabHost;
    
    /**
     * Activity生命周期入口 [功能详细描述]
     * 
     * @param savedInstanceState
     *            bundle
     * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //隐藏软键盘
        this.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        if (!getSharedPreferences(Common.SHARED_PREFERENCE_NAME, MODE_PRIVATE).getBoolean(Common.KEY_ISLOGIN,
                false))
        {
            if (LoginMessageType.STATUS_STAY_BY != FusionConfig.getInstance()
                    .getUserStatus())
            {
                startActivity(new Intent(LoginAction.ACTION));
            }
            finish();
            return;
        }
        setContentView(R.layout.basic_tab);
        
        // 获得界面的必要的一些对象和元素
        findView();
        
        // 初始化TAB数据
        initTabHost(getIntent());
        
        // 设置现实 tab 为好友界面
        //TODO 暂时先跳转好友界面
        mTabHost.setCurrentTab(TAB_TALK);
        //更新通话记录未读数
        // updateCommunicationLogTabUnread();
        //读取未读会话条数，显示
        updateConversationUnread();
        
    }
    
    /**
     * 更新通话记tab上的未读信息
     * 
     */
    private void updateCommunicationLogTabUnread()
    {
        //获取未读总数
        int total = mCommunicationLogLogic.getUnreadTotal();
        //判断未读数是大于零如果大于零则显示否则不显示
        if (total > 0)
        {
            mCommunicationLogUnreadNumber.setVisibility(View.VISIBLE);
            if (total < 10)
            {
                mCommunicationLogUnreadNumber.setText(String.valueOf(total));
            }
            else if (total >= 10 && total <= MAX_RECORD_COUNT)
            {
                mCommunicationLogUnreadNumber.setMinWidth((int) (26 * getResources().getDisplayMetrics().density + 0.5f));
                mCommunicationLogUnreadNumber.setText(String.valueOf(total));
            }
            else if (total > MAX_RECORD_COUNT)
            {
                mCommunicationLogUnreadNumber.setMinWidth((int) (28 * getResources().getDisplayMetrics().density + 0.5f));
                mCommunicationLogUnreadNumber.setText(MAX_RECORD_COUNT + "+");
            }
        }
        else
        {
            mCommunicationLogUnreadNumber.setVisibility(View.GONE);
        }
    }
    
    /**
     * 获取该客户端对应的用户所有的未读会话的数量
     */
    private void updateConversationUnread()
    {
        int count = mConversationLogic.getUnReadCount();
        if (count > 0)
        {
            mConversationUnReadNumber.setVisibility(View.VISIBLE);
            //需求要求如果图片中记录条数>99显示99+
            //            if (count > MAX_RECORD_COUNT)
            //            {
            //                mConversationUnReadNumber.setText(MAX_RECORD_COUNT + "+");
            //            }
            if (count < 10)
            {
                mConversationUnReadNumber.setText(String.valueOf(count));
            }
            else if (count >= 10 && count <= MAX_RECORD_COUNT)
            {
                mConversationUnReadNumber.setMinWidth((int) (26 * getResources().getDisplayMetrics().density + 0.5f));
                mConversationUnReadNumber.setText(String.valueOf(count));
            }
            else if (count > MAX_RECORD_COUNT)
            {
                mConversationUnReadNumber.setMinWidth((int) (28 * getResources().getDisplayMetrics().density + 0.5f));
                mConversationUnReadNumber.setText(MAX_RECORD_COUNT + "+");
            }
        }
        else
        {
            mConversationUnReadNumber.setVisibility(View.GONE);
        }
    }
    
    /**
     * 通话记录为读记录条数的显示与另外两处同步
     * 
     * @see android.app.ActivityGroup#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        //更新通话记录未读数
        updateCommunicationLogTabUnread();
        
    }
    
    /**
     * 
     * 初始化TAB数据
     */
    private void initTabHost(Intent intent)
    {
        // 取得TabHost对象
        mTabHost = getTabHost();
        mTabHost.setOnTabChangedListener(this);
        
        // 设置跳转会话界面的intent
        Intent talkIntent = new Intent(
                FusionAction.ACTION_ACTIVITY_CONVERSATION);
        // 设置<会话>标签，setIndicator()此方法用来设置标签和View
        mTabHost.addTab(mTabHost.newTabSpec("tab_talk")
                .setIndicator(layoutTabTalk)
                .setContent(talkIntent.putExtra("TAB_NAME", TAB_TALK)));
        mConversationUnReadNumber = (TextView) layoutTabTalk.findViewById(R.id.tab_talk_unread);
        
        // 设置跳转好友界面的intent
        Intent friendintent = new Intent(FriendTabAction.ACTION);
        // 设置<好友>标签，setIndicator()此方法用来设置标签和View
        mTabHost.addTab(mTabHost.newTabSpec("tab_friend")
                .setIndicator(layoutTabFriend)
                .setContent(friendintent.putExtra("TAB_NAME", TAB_FRIEND)));
        
        // 设置跳转VOIP界面的intent
        Intent voipintent = new Intent(VoipAction.ACTION_VOIP_MAIN_TAB);
        // 设置<更多>标签，setIndicator()此方法用来设置标签和View
        mTabHost.addTab(mTabHost.newTabSpec("tab_voip")
                .setIndicator(layoutTabVoip)
                .setContent(voipintent.putExtra("TAB_NAME", TAB_VOIP)));
        
        mCommunicationLogUnreadNumber = (TextView) layoutTabVoip.findViewById(R.id.tab_talk_unread);
        
        // 设置跳转群界面的intent
        Intent groupintent = new Intent(GroupListAction.ACTION_GROUP_LIST);
        // 设置<群>标签，setIndicator()此方法用来设置标签和View
        mTabHost.addTab(mTabHost.newTabSpec("tab_group")
                .setIndicator(layoutTabGroup)
                .setContent(groupintent.putExtra("TAB_NAME", TAB_GROUP)));
        
        // 设置跳转更多界面的intent
        Intent moreintent = new Intent(SettingsAction.ACTION_ACTIVITY_SETTINGS);
        // 设置<更多>标签，setIndicator()此方法用来设置标签和View
        mTabHost.addTab(mTabHost.newTabSpec("tab_more")
                .setIndicator(layoutTabMore)
                .setContent(moreintent.putExtra("TAB_NAME", TAB_MORE)));
        
    }
    
    /**
     * [一句话功能简述]<BR>
     * 重写此方法处理通知栏跳转过来时候的参数传递
     * 
     * @param intent
     *            Intent
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        if (null != intent)
        {
            //如果是通知栏跳转过来的，则跳到第一个页面
            if (StringUtil.equals(intent.getStringExtra(NotificationEntity.FLAG_NOTIFICATION_TYPE),
                    IMNotificationEntity.NOTIFICATION_IM_SINGLE))
            {
                mTabHost.setCurrentTab(0);
            }
        }
        super.onNewIntent(intent);
    }
    
    /**
     * 
     * 获得界面的必要的一些对象和元素
     */
    private void findView()
    {
        // 绘制<会话>按钮的view
        layoutTabTalk = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.basic_tab_view, null);
        TextView viewTabTalk = (TextView) layoutTabTalk.findViewById(R.id.tab_btn_view);
        viewTabTalk.setText(R.string.tab_talkLabel);
        
        // 绘制<好友>按钮的view
        layoutTabFriend = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.basic_tab_view, null);
        TextView viewTabFriend = (TextView) layoutTabFriend.findViewById(R.id.tab_btn_view);
        TextView imageTabFriend = (TextView) layoutTabFriend.findViewById(R.id.tab_imagebtn_view);
        viewTabFriend.setText(R.string.tab_friendLabel);
        imageTabFriend.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_tab_friend_picture));
        
        // 绘制<群>按钮的view
        layoutTabGroup = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.basic_tab_view, null);
        TextView viewTabGroup = (TextView) layoutTabGroup.findViewById(R.id.tab_btn_view);
        TextView imageTabGroup = (TextView) layoutTabGroup.findViewById(R.id.tab_imagebtn_view);
        viewTabGroup.setText(R.string.tab_groupLabel);
        imageTabGroup.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_tab_group_picture));
        
        // 绘制<更多>按钮的view
        layoutTabMore = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.basic_tab_view, null);
        TextView viewTabMore = (TextView) layoutTabMore.findViewById(R.id.tab_btn_view);
        TextView imageTabMore = (TextView) layoutTabMore.findViewById(R.id.tab_imagebtn_view);
        viewTabMore.setText(R.string.tab_moreLabel);
        imageTabMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_tab_more_picture));
        
        // 绘制<voip>按钮的view
        layoutTabVoip = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.basic_tab_view, null);
        TextView viewTabVoip = (TextView) layoutTabVoip.findViewById(R.id.tab_btn_view);
        TextView imageTabVoip = (TextView) layoutTabVoip.findViewById(R.id.tab_imagebtn_view);
        viewTabVoip.setText(R.string.tab_voipLabel);
        imageTabVoip.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_tab_voip_picture));
    }
    
    /**
     * 
     * 标签切换事件处理<BR>
     * setOnTabChangedListener是从一个标签切换到另外一个标签会触发的事件
     * 
     * @param tabId
     *            tab id
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    @Override
    public void onTabChanged(String tabId)
    {
        if (!tabId.equals("tab_talk"))
        {
            this.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    }
    
    /**
     * 
     * 返回键进入后台
     * 
     * @param event
     *            点击事件
     * @return dispatchKeyEvent
     * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
                0);
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && mTabHost.getCurrentTab() != TAB_VOIP
                && mTabHost.getCurrentTab() != TAB_MORE)
        {
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        int what = msg.what;
        switch (what)
        {
            case ConversationMessageType.CONVERSATION_DB_CHANGED:
                updateConversationUnread();
                break;
            case FusionMessageType.VOIPMessageType.VOIP_CALL_AGAIN:
                // 更新通话记录未读数
                updateCommunicationLogTabUnread();
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
        mConversationLogic = (IConversationLogic) super.getLogicByInterfaceClass(IConversationLogic.class);
        mCommunicationLogLogic = (ICommunicationLogLogic) super.getLogicByInterfaceClass(ICommunicationLogLogic.class);
    }
}
