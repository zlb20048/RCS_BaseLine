/*
 * 文件名: CommunicationDetail.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 王媛媛
 * 创建时间:2012-3-19
 * 
 * 修改人： 张国坚
 * 修改时间: 2012年3月24日
 * 修改内容：去除364行 holder.mCallStatusImageView.setImageResource(imgId);
 *           因为imgId初始值为-1,调用该方法会报错
 */
package com.huawei.basic.android.im.ui.voip;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * 通话记录详情类
 * 
 * @author 王媛媛
 * @version [RCS Client V100R001C03, 2012-3-19]
 */
public class CommunicationDetailActivity extends VoipBasicActivity
{
    /**
     * 总记录条数
     */
    private static final int PAGE_RECORD_COUNT = 20;
    
    /**
     * 通话记录界面记录起始数据
     */
    private static final int PAGE_START_INDEX = 0;
    
    /**
     * 标题：记录详情
     */
    private TextView mCommDetailTitleTextView;
    
    /**
     * 返回按钮
     */
    private Button mBackButton;
    
    /**
     * ListView控件
     */
    private ListView mListView;
    
    /**
     * 来电或去电号码
     */
    private String mRemotePhoneNum;
    
    /**
     * 来电或去电用户voip账号
     */
    private String mRemoteUri;
    
    /**
     * 通话记录逻辑处理对象
     */
    private ICommunicationLogLogic mCommunicationLogic;
        
    /**
     * 
     * 通话记录列表详情适配器对象
     * 
     */
    private CommLogDetailListAdapter mListAdapter = new CommLogDetailListAdapter();
    
    /**
     * 如果是陌生人则显示添加联系人按钮
     * 
     * 修改添加联系人按钮
     */
    //private TextView mVoipAddContactTV;
    private LinearLayout mVoipAddContactLayout;
    
    /**
     * 打电话按钮的linearLayout
     */
    private LinearLayout mCallLinearLayout;
    
    /**
     * 判断是voip账号还是手机号
     */
    private String mRemotePhoneOrRemoteUri;
    
    /**
     * 详情姓名 veiw
     */
    private TextView mCommDetailNameView;
    
    /**
     * 详情手机或voip号码view
     */
    private TextView mCommDetailPhoneView;
    
    /**
     * 详情头像 的view
     */
    private ImageView mCommDetailPhotoView;
       
    /**
     * 逻辑处理对象
     */
    private IVoipLogic mVoipLogic;
    
    
    /**
     * 是否为原生呼出
     */
    //private boolean mIsOrdinary = false;
    
    /**
     * Activity 生命周期开始方法
     * 
     * @param savedInstanceState
     *            Bundle
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication_log_detail);
        
        Intent intent = getIntent();
        mRemoteUri = intent.getStringExtra(FusionAction.VoipAction.EXTRA_DETAIL_URI);
        mRemotePhoneNum = intent.getStringExtra(FusionAction.VoipAction.EXTRA_DETAIL_PHONE_NUM);
        
        //根据来电或去电用户id查询该用户所有的通讯记录详情 
        //        mCommunicationLogic.loadCommunicationLogByRemoteUriOrRemotePhoneNum(mRemoteUri,
        //                mRemotePhoneNum,
        //                PAGE_START_INDEX,
        //                PAGE_RECORD_COUNT);
        
        //初始化控件
        initView(); 
        //初始化数据
        setViewValues();
        
    }
    
    /**
     * 查找该用户所有的通讯记录详情
     */
    private void setViewValues()
    {
        
        //重新查找该用户所有的通讯记录详情 
        mCommunicationLogic.loadCommunicationLogByRemoteUriOrRemotePhoneNum(mRemoteUri,
                mRemotePhoneNum,
                PAGE_START_INDEX,
                PAGE_RECORD_COUNT);
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
        finish();
    }
    
    
    /**
     * 获取Logic层对象方法
     * 
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        //获取通话记录逻辑处理对象
        mVoipLogic = (IVoipLogic) super.getLogicByInterfaceClass(IVoipLogic.class);
        mCommunicationLogic = (ICommunicationLogLogic) super.getLogicByInterfaceClass(ICommunicationLogLogic.class);
        
    }
    
    /**
     * 重载父类的handleStateMessage方法， 可以实现消息处理
     * 
     * @param msg
     *            Message
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#handleStateMessage(android.os.Message)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void handleStateMessage(Message msg)
    {
        
        //消息类型
        int type = msg.what;
        
        //消息对象
        Object obj = msg.obj;
        //通话记录
        CommunicationLog communicationLog = null;
        switch (type)
        {
            //获取所有的通话记录
            case FusionMessageType.VOIPMessageType.COMM_GET_COMM_LOG_DETAIL:

                //获取通话记录对象集合
                ArrayList<CommunicationLog> commLogList = (ArrayList<CommunicationLog>) obj;
                if (commLogList.size() == 0)
                {
                    //没有通话记录
                    showToast(R.string.no_comm_record);
                }
                else
                {
                    //得到该联系人信息
                    communicationLog = commLogList.get(0);
                    showThisContact(communicationLog);
                }
                //显示通话记录详情                
                mListAdapter.setData(commLogList);                
                //数据变化通知mListAdapter
                mListAdapter.notifyDataSetChanged();
                break;
            
            case FusionMessageType.VOIPMessageType.VOIP_CALL_AGAIN:

                //刷新记录详情界面：根据来电或去电用户电话号或VOIP账号查询该用户所有的通讯记录详情
                mCommunicationLogic.loadCommunicationLogByRemoteUriOrRemotePhoneNum(mRemoteUri,
                        mRemotePhoneNum,
                        PAGE_START_INDEX,
                        PAGE_RECORD_COUNT);
                
                //如果在某一联系人的通话详情界面，接收到该联系人的电话，则刷新未读通话记录数
                mCommunicationLogic.updateToIsReadByOwnerUserId(mRemoteUri,
                        mRemotePhoneNum);
                break;
            
            //添加联系人后重新设置
            case FusionMessageType.VOIPMessageType.VOIP_ADD_CANTACT:

                //重新设置界面显示
                if (null != communicationLog)
                {
                    showThisContact(communicationLog);
                }
                break;
            default:
                break;
        }
        super.handleStateMessage(msg);
    }
    
    /**
     * 初始化控件
     */
    private void initView()
    {
        
        //返回按钮
        mBackButton = (Button) findViewById(R.id.left_button);
        
        //显示标题：详情记录
        mCommDetailTitleTextView = (TextView) findViewById(R.id.title);
        mCommDetailTitleTextView.setVisibility(View.VISIBLE);
        mCommDetailTitleTextView.setText(R.string.comm_detail_title);
        
        //详情姓名、手机或voip号码、头像 的view
        mCommDetailNameView = (TextView) findViewById(R.id.comm_log_detail_name);
        mCommDetailPhoneView = (TextView) findViewById(R.id.comm_log_detail_phone);
        mCommDetailPhotoView = (ImageView) findViewById(R.id.comm_log_detail_photo);
        
        //如果是陌生人则显示添加联系人按钮 和此时的拨打电话按钮
        // mVoipAddContactLay=  (LinearLayout) findViewById(R.id.voip_add_contact_lay);
        // mVoipAddContactTV = (TextView) findViewById(R.id.voip_add_contact);
        mVoipAddContactLayout = (LinearLayout) findViewById(R.id.voip_add_contact_layout);
        
        //通话记录项控件
        mListView = (ListView) findViewById(R.id.comm_detail_ListView);
        
        //拨打电话 按钮及其响应的事件
        mCallLinearLayout = (LinearLayout) findViewById(R.id.comm_dial_image);
        
        mListView.setAdapter(mListAdapter);
        
      //返回按钮响应事件
        mBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        // 选择通话类型响应事件
        mCallLinearLayout.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                //选择通话类型：普通通话  VOIP语音通话  VOIP视频通话
                showChooseCallType(mRemotePhoneOrRemoteUri, false);
            }
        });
        
        //如果是陌生人则添加联系人到通讯录
        mVoipAddContactLayout.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                //添加到本地通讯录
                addToLocalContact(mRemotePhoneOrRemoteUri);
                
            }
        });
        
    }
    
    /**
     * 显示通话记录详情
     * 
     * @param communicationLog
     *            CommunicationLog
     */
    private void showThisContact(CommunicationLog communicationLog)
    {        
        boolean isContact = false;
        boolean isHiTalkFriend = false;
        // 获取来电或去电用户名称
        String commDetailName = communicationLog.getRemoteDisplayName();
        String faceUrl = communicationLog.getFaceUrl();
        byte[] faceData = communicationLog.getFaceData();
        
        //如果是手機号则显示手机号 否则显示voip账号
        if (null == mRemotePhoneNum)
        {
            mRemotePhoneOrRemoteUri = mRemoteUri;
        }
        else
        {
            mRemotePhoneOrRemoteUri = mRemotePhoneNum;
        }
        mCommDetailPhoneView.setText(mRemotePhoneOrRemoteUri);
        
        //根据voip账号或手机号 在本地通讯录获取该联系人，如果获取到，则显示该人的名称
        PhoneContact phoneContact = mCommunicationLogic.getPhoneContacts(CommunicationDetailActivity.this,
                mRemotePhoneOrRemoteUri);
        
        //如果HiTalk中不存在该联系人的信息则设置为本地联系人信息
        if (null != phoneContact)
        {
            //如果能在本地通讯录获取到该联系人的信息，则该联系人不是陌生人，就不需要添加联系人的按钮
            // mVoipAddContactTV.setVisibility(View.GONE);
            mVoipAddContactLayout.setVisibility(View.GONE);
            mCallLinearLayout.setVisibility(View.VISIBLE);
            commDetailName = phoneContact.getContactName();
            faceData = phoneContact.getFaceData();
            isContact = true;
        }
        else
        {
            //查询Hitalk好友,看有没有备注名
            ContactInfoModel mContactmInfoModel = mVoipLogic.getContactInfoModelByPhone(mRemotePhoneOrRemoteUri);
            if (null != mContactmInfoModel)
            {
                //好友的名字
                commDetailName = StringUtil.isNullOrEmpty(mContactmInfoModel.getMemoName()) ? mContactmInfoModel.getDisplayName()
                        : mContactmInfoModel.getMemoName();
                // 展示数据库中头像
                FaceThumbnailModel mFaceThumbnailModel = mVoipLogic.getFaceThumbnailModel(mContactmInfoModel.getFriendUserId());
                if (null != mFaceThumbnailModel
                        && null != mFaceThumbnailModel.getFaceUrl())
                {
                    
                    faceUrl = mFaceThumbnailModel.getFaceUrl();
                    faceData = mFaceThumbnailModel.getFaceBytes();
                    
                }
                isHiTalkFriend = true;
            }
            else
            {
                commDetailName = mRemotePhoneOrRemoteUri;
            }
        }
        
        //设置头像
        ImageUtil.showFace(mCommDetailPhotoView,
                faceUrl,
                faceData,
                R.drawable.voip_comm_img_unknow,
                72,
                72);
        
        if (StringUtil.isNullOrEmpty(commDetailName)
                || commDetailName.equals(mRemotePhoneOrRemoteUri))
        {
            //如果名字为空或和号码相同，则不显示名称
            mCommDetailNameView.setVisibility(View.GONE);
            //将电话号码显示字体未黑色
            mCommDetailPhoneView.setTextColor(Color.BLACK);
            mCommDetailPhoneView.setTextSize(16);
        }
        else
        {
            mCommDetailNameView.setVisibility(View.VISIBLE);
            //设置显示名称
            mCommDetailNameView.setText(commDetailName);
            
        }
        if (!isContact && !isHiTalkFriend)
        {
            ///mVoipAddContactTV.setVisibility(View.VISIBLE);
            mVoipAddContactLayout.setVisibility(View.VISIBLE);
            mCallLinearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            // mVoipAddContactTV.setVisibility(View.GONE);
            mVoipAddContactLayout.setVisibility(View.GONE);
            mCallLinearLayout.setVisibility(View.VISIBLE);
            mCallLinearLayout.setPadding(0, 0, 0, 0);
            mCallLinearLayout.setGravity(Gravity.CENTER);
        }
        
    }
    
    /**
     * 通话详情列表 适配器
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-19]
     */
    private class CommLogDetailListAdapter extends BaseListAdapter
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
         * @see com.huawei.basic.android.im.ui.basic.BaseListAdapter#getView
         *      (int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) CommunicationDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.communication_log_detail_item,
                        null);
                holder = new ViewHolder();
                
                //得到各个控件
                holder.mCallStatusImageView = (ImageView) convertView.findViewById(R.id.callStatusImage);
                holder.mCallStatusTextView = (TextView) convertView.findViewById(R.id.callStatusText);
                holder.mDateView = (TextView) convertView.findViewById(R.id.comm_detail_date);
                holder.mTalkingTimeView = (TextView) convertView.findViewById(R.id.comm_detail_talking_time);
                holder.mClearTimeView = (TextView) convertView.findViewById(R.id.comm_detail_time);
                convertView.setTag(holder);
            }
            else
            {
                //convertView回收一些布局供下面重构时使用
                holder = (ViewHolder) convertView.getTag();
            }
            CommunicationLog commLog = (CommunicationLog) getDataSrc().get(position);
            
            //判断通话的类型：已接来电、未接来电、已接去电、未接去电
            int imgId = -1;
            String commType = "";
            holder.mCallStatusTextView.setText(commType);
            switch (commLog.getType())
            {
                //已接来电 
                case CommunicationLog.TYPE_VOIP_CALL_IN_ALREADY:
                    commType = getString(R.string.comm_detail_dail_type1);
                    imgId = R.drawable.voip_commlog_icon_call_in;
                    break;
                //未接来电 
                case CommunicationLog.TYPE_VOIP_CALL_IN_MISSED:
                    commType = getString(R.string.comm_detail_dail_type2);
                    imgId = R.drawable.voip_commlog_icon_call_in_miss;
                    break;
                //拒接来电
                case CommunicationLog.TYPE_VOIP_CALL_IN_REFUSED:
                    commType = getString(R.string.comm_detail_dail_type3);
                    imgId = R.drawable.voip_commlog_icon_call_in_refuse;
                    break;
                //VOIP去电 
                case CommunicationLog.TYPE_VOIP_CALL_OUT:
                    commType = getString(R.string.comm_detail_dail_type4);
                    imgId = R.drawable.voip_commlog_icon_call_out;
                    break;
                //原生去电 
                case CommunicationLog.TYPE_ORDINARY_CALL_OUT:
                    commType = getString(R.string.comm_detail_dail_type5);
                    imgId = R.drawable.voip_commlog_icon_call_out;
                    break;
                default:
                    break;
            }
            if (imgId != -1)
            {
                holder.mCallStatusTextView.setText(commType);
                holder.mCallStatusImageView.setImageResource(imgId);
            }
            
            //显示通话的日期
            if (null != commLog.getCallTime())
            {
                
                holder.mDateView.setText(DateUtil.getCommunicationLogDetailTimeByDate(CommunicationDetailActivity.this,
                        commLog.getCallTime()));
            }
            
            //设置显示通话时长:
            //voip通话 
            //            if (!mIsOrdinary
            //                    && commLog.getType() == CommunicationLog.TYPE_ORDINARY_CALL_OUT)
            //            {
            //                holder.mTalkingTimeView.setText(DateUtil.getDiffTime2(commLog.getTalkTime()));
            //            }
            //            else
            //            {
            //                //原生通话
            //                holder.mTalkingTimeView.setText("");
            //            }
            //设置显示通话时长:
            String timString = commLog.getType() == CommunicationLog.TYPE_ORDINARY_CALL_OUT ? ""
                    : DateUtil.getDiffTime2(commLog.getTalkTime());
            //            if (commLog.getType() == CommunicationLog.TYPE_ORDINARY_CALL_OUT)
            //            {
            //                //原生通话
            //                holder.mTalkingTimeView.setText("");
            //            }
            //            else
            //            {
            //                //voip通话 
            //                holder.mTalkingTimeView.setText(DateUtil.getDiffTime2(commLog.getTalkTime()));
            //            }
            holder.mTalkingTimeView.setText(timString);
            
            //显示具体的通话时间
            if (null != commLog.getCallTime())
            {
                holder.mClearTimeView.setText(DateUtil.getFormatClearTimeByDate(CommunicationDetailActivity.this,
                        commLog.getCallTime()));
            }
            
            return convertView;
        }
               
    }
    
    /**
     * 自定义的View
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-19]
     */
    private class ViewHolder
    {
        private ImageView mCallStatusImageView;
        
        private TextView mCallStatusTextView;
        
        private TextView mDateView;
        
        private TextView mTalkingTimeView;
        
        private TextView mClearTimeView;
        
    }
}
