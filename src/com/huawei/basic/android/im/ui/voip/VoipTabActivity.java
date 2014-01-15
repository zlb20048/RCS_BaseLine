/*
 * 文件名: VoipTabActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: Voip tab界面 包括拨号盘 通讯记录
 * 创建人: zhoumi
 * 创建时间:2012-3-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.voip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.VoipAction;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic;
import com.huawei.basic.android.im.ui.basic.BasicTabActivity;

/**
 * Voip主Tab界面
 * @author zhoumi
 * @version [RCS Client V100R001C03, 2012-3-14]
 */
public class VoipTabActivity extends BasicTabActivity
{
    /**
     * 记录Tab中小圈圈中最大记录条数，如果超过这个记录，显示
     * MAX_RECORD_COUNT+,这边显示 99+
     */
    private static final int MAX_RECORD_COUNT = 99;
    /**
     * TabActivity的TabHost对象
     */
    private TabHost mTabHost;
    
    /**
     * 绘制TAB的按扭  拨号盘
     */
    private RelativeLayout layoutNumberPad;
    
    /**
     * 绘制TAB的按扭  通讯录
     */
    private RelativeLayout layoutCommunicationLog;
    
    /**
     * 通话记录逻辑处理类
     */
    private ICommunicationLogLogic mCommunicationLogLogic;
    
    /**
     * 通话记录TextView
     */
    private TextView mViewCommunicationLog;
    
    /**
     * 通话记录text
     */
    private String mCommLogtext;
    
    /**
     * 通话记录未读的TextView
     */
    private TextView mConversationUnReadNumber;
    
  
    
    /**
     * activity 生命周期入口
     * @param savedInstanceState Bundle
     * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voip_base_tab);
        findView();
        initTabHost();
        getViewVaules();
    }
    
    /**
     *  重载父类的handleStateMessage方法， 可以实现消息处理
     *   
     * @param msg Message
     * @see com.huawei.basic.android.im.framework.ui.BaseTabActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        //消息类型
        int type = msg.what;
        
        switch (type)
        {
            case FusionMessageType.VOIPMessageType.VOIP_CHANGE_COMM_LOG_UNREAD_TOTAL:
                getViewVaules();
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 初始化tabHost
     */
    private void initTabHost()
    {
        //获得tabhost对象
        mTabHost = getTabHost();
        mTabHost.setup();
        
        //拨号盘界面
        Intent numberPad = new Intent(VoipAction.ACTION_VOIP_NUMBER_PAD);
        mTabHost.addTab(mTabHost.newTabSpec("voip_number_pad")
                .setIndicator(layoutNumberPad)
                .setContent(numberPad));
        
        //通话记录界面
        Intent contacIntent = new Intent(VoipAction.ACTION_VOIP_COMM_LOG);
        mTabHost.addTab(mTabHost.newTabSpec("voip_communication_log")
                .setIndicator(layoutCommunicationLog)
                .setContent(contacIntent));
        mTabHost.setCurrentTabByTag("voip_number_pad");
    }
    
    /**
     * 
     * 获得界面的必要的一些对象和元素
     */
    private void findView()
    {
        // 绘制 拨号盘 按钮
        
        layoutNumberPad = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.voip_tab_view, null);
        TextView viewtNumberPad = (TextView) layoutNumberPad.findViewById(R.id.tab_view);
        viewtNumberPad.setText(R.string.voip_main_tab_number_pad);
        viewtNumberPad.setTextColor(Color.WHITE);
        viewtNumberPad.setBackgroundDrawable(getResources().getDrawable(R.drawable.voip_tab_num_pad));
        // 绘制通话记录按钮
        layoutCommunicationLog = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.voip_tab_view, null);
        mViewCommunicationLog = (TextView) layoutCommunicationLog.findViewById(R.id.tab_view);
        mConversationUnReadNumber = (TextView) findViewById(R.id.tab_comm_unread);
        mCommLogtext = getResources().getText(R.string.voip_main_tab_communication_log)
                .toString();
        int total = mCommunicationLogLogic.getUnreadTotal();
        showUnReadCount(total);
        mViewCommunicationLog.setText(mCommLogtext);
        
        mViewCommunicationLog.setTextColor(Color.WHITE);
        mViewCommunicationLog.setBackgroundDrawable(getResources().getDrawable(R.drawable.voip_tab_com_log));
        
    }
    
    /**
     * 刷新未读数
     */
    private void getViewVaules()
    {
        int total = mCommunicationLogLogic.getUnreadTotal();
        showUnReadCount(total);
    }

    /**
     * 显示未读数目
     * @param total
     */
    private void showUnReadCount(int total)
    {
        if (total > 0)
        {
            //显示未读数
            mConversationUnReadNumber.setVisibility(View.VISIBLE);
            if (total > MAX_RECORD_COUNT)
            {
                //大于99条，显示99+
                mConversationUnReadNumber.setText(MAX_RECORD_COUNT + "+");
            }
            else
            {
                //显示具体的多少条
                mConversationUnReadNumber.setText(String.valueOf(total));
            }
        }
        
        else
        {
            //不显示
            mConversationUnReadNumber.setVisibility(View.GONE);
        }
        
    }
    
    /**
     * 
     *  {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicTabActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        // TODO Auto-generated method stub
        mCommunicationLogLogic = (ICommunicationLogLogic) super.getLogicByInterfaceClass(ICommunicationLogLogic.class);
    }
    
}
