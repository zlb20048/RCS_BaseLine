/*
 * 文件名: FindFriendActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionMessageType.ConversationMessageType;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.ui.contact.ContactBaseActivity;

/**查找好友的界面<BR>
 * 查找好友的主界面，可以根据三种查找类型跳转到各自的查找界面[上传通讯暂时就这样写，将来需要优化]
 * @author 刘鲁宁
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 16, 2012] 
 */
public class FindFriendActivity extends ContactBaseActivity
{
    /**
     * TAG
     */
    private static final String TAG = "FindFriendActivity";
    
    /**
     * 根据ID查找好友按钮
     */
    private View mFindByIdView;
    
    /**
     * 根据详细信息查找好友按钮
     */
    private View mFindByDetailsView;
    
    /**
     * 根据认识的人查找好友按钮
     */
    private View mFindMaybeKnownView;
    
    /**
     * 查找附近的人
     */
    private View mFindByLocation;
    
    /**
     * 查看通讯录好友 
     */
    private View mCheckMobileContacts;
    
    /**
     * 因为通讯录上次会时不时的发一个FINISH消息，需要做一个标记，防止不是这边界面的FINISH消息导致其也会处理信息
     */
    private boolean mNeedHandleContactFinish = false;
    
    /**
     * Activity生命周期入口
     * @param savedInstanceState 
     *      传入的bundle对象
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_find_friend);
        //需要退出菜单
        mFindByIdView = (View) findViewById(R.id.find_by_id);
        mFindByIdView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * 点击根据ID查找好友按钮事件处理<BR>
             * @param v 被点击的控件对象
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             */
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(FusionAction.FindFriendResultListAction.ACTION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra(FusionAction.FindFriendResultListAction.EXTRA_MODE,
                        FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_ID);
                startActivity(intent);
            }
            
        });
        mFindByDetailsView = (View) findViewById(R.id.find_by_details);
        mFindByDetailsView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * 点击根据详细信息查找好友按钮事件处理<BR>
             * @param v 被点击的控件对象
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             */
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(FusionAction.FindFriendResultListAction.ACTION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra(FusionAction.FindFriendResultListAction.EXTRA_MODE,
                        FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_DETAIL);
                startActivity(intent);
            }
            
        });
        mFindMaybeKnownView = (View) findViewById(R.id.find_maybe_known);
        mFindMaybeKnownView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * 点击根据认识的人查找好友按钮事件处理<BR>
             * @param v 被点击的控件对象
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             */
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(FusionAction.FindFriendResultListAction.ACTION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra(FusionAction.FindFriendResultListAction.EXTRA_MODE,
                        FusionAction.FindFriendResultListAction.MODE.MODE_FIND_BY_MAYBE_KNOWN);
                startActivity(intent);
            }
        });
        mFindByLocation = (View) findViewById(R.id.find_by_location);
        mFindByLocation.setOnClickListener(new View.OnClickListener()
        {
            /**
             * 
             * 附近查找的点击事件处理<BR>
             * @param v被点击的控件对象
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             */
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(FusionAction.CheckAroundAction.ACTION);
                startActivity(intent);
            }
        });
        mCheckMobileContacts = (View) findViewById(R.id.check_mobile_contacts);
        mCheckMobileContacts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!getContactLogic().hasUploaded())
                {
                    Logger.i(TAG, "通讯录未上传");
                    mNeedHandleContactFinish = true;
                    FindFriendActivity.this.uploadFirstContact(R.string.check_mobile_contacts_upload_contacts_info);
                }
                else
                {
                    startCheckMobileActivity();
                }
            }
        });
    }
    
    /**
     * onResume<BR>
     * @see android.app.ActivityGroup#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        super.hideInputWindow(mFindMaybeKnownView);
    }
    
    @Override
    protected boolean isNeedMenu()
    {
        return true;
    }
    
    /**
     * 底端返回时不关闭页面
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        super.handleStateMessage(msg);
        switch (msg.what)
        {
            case ConversationMessageType.UPLOAD_CONTACTS_FINISH:
                if (mNeedHandleContactFinish)
                {
                    mNeedHandleContactFinish = false;
                    startCheckMobileActivity();
                }
                break;
        }
    }
    
    /**
     * 跳转到查看电话簿好友Activity
     */
    private void startCheckMobileActivity()
    {
        Intent intent = new Intent(FusionAction.CheckMobileContacts.ACTION);
        startActivity(intent);
    }
    
}
