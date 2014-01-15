/*
 * 文件名: CommunicationLogListActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 王媛媛
 * 创建时间:2012-3-15
 * 
 * 修改人： 张国坚
 * 修改时间: 2012年3月24日
 * 修改内容：去除307行 holder.mCallStatusImageView.setImageResource(imgId);
 *           因为imgId初始值为-1,调用该方法会报错
 */
package com.huawei.basic.android.im.ui.voip;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.model.voip.CommunicationLog;
import com.huawei.basic.android.im.logic.voip.CommunicationLogLogic.PhoneContact;
import com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.ui.basic.BaseListAdapter;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.ImageUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 通话记录类：通话记录列表
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-15]
 */
public class CommunicationLogListActivity extends VoipBasicActivity
{
    /**
     *清空通话记录菜单
     */
    private static final int CLEAR_COMM_LOGS = 0;
    
    /**
     * 菜单：删除
     */
    private static final int MENU_DELETE_COMM_LOG = 1;
    
    /**
     * 记录Tab中小圈圈中最大记录条数，如果超过这个记录，显示 MAX_RECORD_COUNT+,这边显示 99+
     */
    private static final int MAX_RECORD_COUNT = 99;
    
    /**
     * ListView控件
     */
    private ListView mListView;
    
    /**
     * 通话记录逻辑处理类对象
     */
    private ICommunicationLogLogic mCommunicationLogic;
    
    /**
     * 通话记录详情适配器
     */
    private CommLogListAdapter mListAdapter = new CommLogListAdapter();
    
    /**
     * 逻辑处理对象
     */
    private IVoipLogic mVoipLogic;
    
    
    /**
     * 
     * {@inheritDoc}
     * 
     * @param menu
     *            Menu
     * @return boolean
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreateOptionsMenu(android.view.Menu)
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE,
                CLEAR_COMM_LOGS,
                Menu.NONE,
                R.string.voip_clear_commlogs)
                .setIcon(R.drawable.menu_exit_icon);
        
        return true;
    }
    
    /**
     * 返回键
     */
    @Override
    public void onBackPressed()
    {
        //如果有弹出框  取消弹出框
        if (closeChooseCallType(false))
        {
            return;
        }
        //切换到后台
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
                0);
        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }
    
    /**
     * 点击menu键，提示是否清空通话记录
     * 
     * @param item
     *            MenuItem
     * @return boolean
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == CLEAR_COMM_LOGS)
        {
            if (mListAdapter.getCount() <= 0)
            {
                showToast(R.string.no_comm_record);
                return true;
            }
            showIconDialog(R.string.wake,
                    android.R.drawable.ic_dialog_alert,
                    R.string.voip_clear_commlogs_warn,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //清空通话记录
                            mCommunicationLogic.deleteAllCommunicationLogs();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Activity生命周期入口方法
     * 
     * @param savedInstanceState
     *            Bundle
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication_log_all);
        
        //初始化控件
        initView();
        //初始化数据
        getViewValues();
    }
    
    /**
     * 
     * 发送获取通话记录消息<BR>
     * 
     */
    private void getViewValues()
    {
        //发送获取通话记录消息
        mCommunicationLogic.getAllCommunicationLogs();
    }
    
    
    /**
     * 获取通话记录逻辑处理接口
     * 
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mVoipLogic = (IVoipLogic) super.getLogicByInterfaceClass(IVoipLogic.class);
        mCommunicationLogic = (ICommunicationLogLogic) super.getLogicByInterfaceClass(ICommunicationLogLogic.class);
    }
    
    /**
     * 
     * 通过 重载父类的handleStateMessage方法， 可以 实现消息处理
     * 
     * @param msg
     *            接收的消息对象
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#handleStateMessage(android.os.Message)
     */
    
    @Override
    protected void handleStateMessage(Message msg)
    {
        int type = msg.what;
        switch (type)
        {
            //获取所有的通话记录
            case FusionMessageType.VOIPMessageType.COMM_GET_ALL_COMM_LOG:
                List<CommunicationLog> commLogslist = mCommunicationLogic.getAllCommunicationLogsFromDB();
//                if (mCommLogslist.size() == 0 && mIsShowZero)
//                {
//                    showToast(R.string.no_comm_record);
//                }
                mListAdapter.setData(commLogslist);
                mListAdapter.notifyDataSetChanged();
                break;
            case FusionMessageType.VOIPMessageType.VOIP_ADD_CANTACT:
                //发送获取通话记录消息
                getViewValues();
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 是否需要Menu菜单（退出程序）
     * 
     * @return 是否需要Menu菜单
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#isNeedMenu()
     */
    @Override
    protected boolean isNeedMenu()
    {
        return true;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        if (null != mListAdapter.getDataSrc()
                && mListAdapter.getDataSrc().get(position) instanceof CommunicationLog)
        {
            CommunicationLog commLog = (CommunicationLog) mListAdapter.getDataSrc()
                    .get(position);           
            final String remotePhoneNum = commLog.getRemotePhoneNum();
            final String remoteUri = commLog.getRemoteUri();
            //根据该通话记录对象的voip账号或手机号删除该对象
            mCommunicationLogic.deleteByRemoteUriOrRemotePhoneNum(remoteUri,
                    remotePhoneNum);
        }
        return super.onContextItemSelected(item);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        if (null != mListAdapter.getDataSrc()
                && mListAdapter.getDataSrc().get(position) instanceof CommunicationLog)
        {
            menu.setHeaderTitle(R.string.voip_operation);
            menu.add(0, MENU_DELETE_COMM_LOG, 0, R.string.voip_delete_commlog);
            
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * 初始化控件
     */
    private void initView()
    {
        mListView = (ListView) findViewById(R.id.claListView);
        //通话记录列表项点击事件
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            
            /**
             * 点击ListViewItem的响应事件回调
             * 
             * @param vParent
             *            ListView对象
             * @param vItem
             *            其中的item
             * @param iItemIndex
             *            item的下标
             * @param lItemId
             *            item的id
             */
            @Override
            public void onItemClick(AdapterView<?> vParent, View vItem,
                    int iItemIndex, long lItemId)
            {
                CommunicationLog commLog = (CommunicationLog) mListAdapter.getItem(iItemIndex);
                String phoneOrremoteUri = null;
                String remotePhoneNum = commLog.getRemotePhoneNum();
                String remoteUri = commLog.getRemoteUri();
                //判断是voip账号还是手机号
//                if (null == remotePhoneNum)
//                {
//                    phoneOrremoteUri = remoteUri;
//                }
//                else
//                {
//                    phoneOrremoteUri = remotePhoneNum;
//                }
                phoneOrremoteUri = null == remotePhoneNum ? remoteUri : remotePhoneNum;
                //选择通话类型
                showChooseCallType(phoneOrremoteUri, false);
            }
        });
        registerForContextMenu(mListView);
        //长按单项删除
        
    }
    
    /**
     * 通话记录适配器
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-15]
     */
    private class CommLogListAdapter extends BaseListAdapter
    {
        /**
         * 
         * 重写父类的方法，向ListView提供每一个item所需要的view对象
         * 初始时ListView会从BaseAdapter中根据当前的屏幕布局实例化一定数量的view对象，
         * 同时ListView会将这些view对象缓存起来。 当向上滚动ListView时，原先位于最上面的list
         * item的view对象会被回收， 然后被用来构造新出现的最下面的list item。这个构造过程就是由getView()方法完成的。
         * 
         * @param position
         *            item的位置
         * @param convertView
         *            被缓存起来的listitem的view对象(初始化时缓存中没有view对象则convertView是null)
         * @param parent
         *            parent
         * @return view view
         * @see com.huawei.basic.android.im.ui.basic.
         *      BaseListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) CommunicationLogListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.communicationlog_list_item,
                        null);
                holder = new ViewHolder();
                
                //得到各个控件
                holder.mFaceImageView = (ImageView) convertView.findViewById(R.id.holder_FaceImageView);
                holder.mCallStatusView = (ImageView) convertView.findViewById(R.id.holder_call_status);
                holder.mCallNameView = (TextView) convertView.findViewById(R.id.holder_CallNameView);
                holder.mPhoneCodeView = (TextView) convertView.findViewById(R.id.holder_PhoneCode);
                holder.mTimeView = (TextView) convertView.findViewById(R.id.holder_Time);
                holder.mNextButton = (ImageView) convertView.findViewById(R.id.holder_ArrayImage);
                holder.mUnReadView = (TextView) convertView.findViewById(R.id.communication_unread_counts);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            
            // 得到每一项对应的通话 记录 
            final CommunicationLog commLog = (CommunicationLog) getDataSrc().get(position);
            
            //获取对方的姓名、手机号或voip账号
            String remoteName = commLog.getRemoteDisplayName();
            // 判断是voip账号还是手机号
            String mPhoneOrremoteUri;
            
            String remoteUri = commLog.getRemoteUri();
            String remotePhoneNum = commLog.getRemotePhoneNum();
            String faceUrl = commLog.getFaceUrl();
            byte[] faceData = commLog.getFaceData();
           
            //判断是voip账号还是手机号
            if (null == remotePhoneNum)
            {
                mPhoneOrremoteUri = remoteUri;
            }
            else
            {
                mPhoneOrremoteUri = remotePhoneNum;
            }
            //显示手机号或显示voip账号：
            
            holder.mPhoneCodeView.setText(mPhoneOrremoteUri);
            
            //根据voip账号或手机号 获取本地通讯录联系人，如果是本地通讯录中的联系人，则显示该人的名称
            PhoneContact phoneContact = mCommunicationLogic.getPhoneContacts(CommunicationLogListActivity.this,
                    mPhoneOrremoteUri);
            
            //如果HiTalk中不存在该联系人的信息则设置为本地联系人信息
            if (null != phoneContact)
            {
                remoteName = phoneContact.getContactName();
                faceData = phoneContact.getFaceData();
                
            }
            else
            {
                //查询Hitalk好友,看有没有备注名
                ContactInfoModel mContactmInfoModel = mVoipLogic.getContactInfoModelByPhone(mPhoneOrremoteUri);
                if (null != mContactmInfoModel)
                {
                    //好友的名字
                    remoteName = StringUtil.isNullOrEmpty(mContactmInfoModel.getMemoName()) ? mContactmInfoModel.getDisplayName()
                            : mContactmInfoModel.getMemoName();
                    // 展示数据库中头像
                    FaceThumbnailModel mFaceThumbnailModel = mVoipLogic.getFaceThumbnailModel(mContactmInfoModel.getFriendUserId());
                    if (null != mFaceThumbnailModel
                            && null != mFaceThumbnailModel.getFaceUrl())
                    {
                        faceUrl = mFaceThumbnailModel.getFaceUrl();
                        faceData = mFaceThumbnailModel.getFaceBytes();
                    }
                }
                else
                {
                    remoteName = mPhoneOrremoteUri;
                }
            }
            
            //设置头像
            ImageUtil.showFace(holder.mFaceImageView,
                    faceUrl,
                    faceData,
                    R.drawable.voip_comm_img_unknow,
                    72,
                    72);
            if (null == remoteName)
            {
                remoteName = mPhoneOrremoteUri;
            }
            //设置姓名
            holder.mCallNameView.setText(remoteName);
            int total = commLog.getUnreadAmout();
            //判断通话记录未读条数
            if (total > 0 && commLog.getIsUnread())
            {
                holder.mUnReadView.setVisibility(View.VISIBLE);
                if (total <= MAX_RECORD_COUNT)
                {
                    holder.mUnReadView.setText(getResources().getString(R.string.voip_comm_unread,
                            commLog.getUnreadAmout()));
                }
                else
                {
                    holder.mUnReadView.setText(getResources().getString(R.string.voip_comm_unread,
                            MAX_RECORD_COUNT + "+"));
                }
                
            }
            else
            {
                //未读消息=0，表示已经读过，不显示数目
                holder.mUnReadView.setVisibility(View.GONE);
            }
            
            //显示时间
            if (null != commLog.getCallTime())
            {
                holder.mTimeView.setText(DateUtil.getCommunicationTimeByDate(CommunicationLogListActivity.this,
                        commLog.getCallTime()));
            }
            //判断通话的类型：已接来电、未接来电、已接去电、未接去电
            int imgId = -1;
            switch (commLog.getType())
            {
                //已接来电 
                case CommunicationLog.TYPE_VOIP_CALL_IN_ALREADY:
                    imgId = R.drawable.voip_commlog_icon_call_in;
                    break;
                //未接来电 
                case CommunicationLog.TYPE_VOIP_CALL_IN_MISSED:
                    imgId = R.drawable.voip_commlog_icon_call_in_miss;
                    break;
                //拒接来电
                case CommunicationLog.TYPE_VOIP_CALL_IN_REFUSED:
                    imgId = R.drawable.voip_commlog_icon_call_in_refuse;
                    break;
                //VOIP去电 
                case CommunicationLog.TYPE_VOIP_CALL_OUT:
                    imgId = R.drawable.voip_commlog_icon_call_out;
                    break;
                //原生去电 
                case CommunicationLog.TYPE_ORDINARY_CALL_OUT:
                    imgId = R.drawable.voip_commlog_icon_call_out;
                    break;
                default:
                    break;
            }
            if (imgId != -1)
            {
                holder.mCallStatusView.setImageResource(imgId);
            }
            
            holder.mNextButton.setOnClickListener(new View.OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent();
                    intent.setAction(FusionAction.VoipAction.ACTION_VOIP_COMM_DETAIL);
                    intent.putExtra(FusionAction.VoipAction.EXTRA_DETAIL_URI,
                            commLog.getRemoteUri());
                    intent.putExtra(FusionAction.VoipAction.EXTRA_DETAIL_PHONE_NUM,
                            commLog.getRemotePhoneNum());
                    
                    //启动详情界面
                    startActivity(intent);
                    
                    //更新记录项为已读
                    mCommunicationLogic.updateToIsReadByOwnerUserId(commLog.getRemoteUri(),
                            commLog.getRemotePhoneNum());
                }
            });
            
            return convertView;
        }        
        
    }
    
    /**
     * 自定义的view
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-19]
     */
    private class ViewHolder
    {
        /**
         * 头像
         */
        private ImageView mFaceImageView;
        
        /**
         * 来电或去电用户 名称
         */
        private TextView mCallNameView;
        
        /**
         * 来电或去电号码
         */
        private TextView mPhoneCodeView;
        
        /**
         * 来电或去电时间
         */
        private TextView mTimeView;
        
        /**
         * 未读数
         */
        private TextView mUnReadView;
        
        /**
         * 来电去电类型：已接来电、未接来电、已接去电、未接去电
         */
        private ImageView mCallStatusView;
        
        /**
         * 跳转到记录详情的ImageVIew
         */
        private ImageView mNextButton;
        
    }
}
