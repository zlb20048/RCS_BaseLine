/*
 * 文件名: VoipCallingActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 通话界面
 * 创建人: zhoumi
 * 创建时间:2012-3-13
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.voip;

import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.VoipAction;
import com.huawei.basic.android.im.common.FusionMessageType.VOIPMessageType;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.FaceThumbnailModel;
import com.huawei.basic.android.im.logic.voip.ICommunicationLogLogic;
import com.huawei.basic.android.im.logic.voip.IVoipLogic;
import com.huawei.basic.android.im.logic.voip.CommunicationLogLogic.PhoneContact;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.CheckButton;
import com.huawei.basic.android.im.ui.basic.VoipNumberPadUtil;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.ImageUtil;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.fast.voip.FastVoIPConstant;

/***
 * 拨打电话的等待通话界面/来电话的通话界面
 * 
 * @author zhoumi
 * @version [RCS Client V100R001C03, 2012-3-13]
 */
public class VoipCallingActivity extends BasicActivity implements
        OnClickListener
{
    
    /**
     * 通话状态 -正在呼叫，通话中，通话结束
     */
    private TextView mCallState;
    
    /**
     * 通话时间
     */
    private TextView mCallTime;
    
    /**
     * 当前的显示时间的控件
     */
    private TextView mCurrentTimeView;
    
    /**
     * 联系人的头像
     */
    private ImageView mContactIcon;
    
    /**
     * 联系人的名字
     */
    private TextView mContactName;
    
    /**
     * 联系人的号码
     */
    private TextView mContactNumber;
    
    /**
     * 录音按钮
     */
    private CheckButton mRecordBtn;
    
    /**
     * 静音按钮
     */
    private CheckButton mMuteBtn;
    
    /**
     * 键盘按钮
     */
    private CheckButton mKeyBoardBtn;
    
    /**
     * 扬声器按钮
     */
    private CheckButton mSpeakerBtn;
    
    /**
     * 接听电话按钮
     */
    private LinearLayout mOperatAnswerBtn;
    
    /**
     * 挂断电话按钮
     */
    private LinearLayout mOperatHangUpBtn;
    
    /**
     * 拒绝接听按钮
     */
    private LinearLayout mOperatRefuseBtn;
    
    /**
     * 隐藏键盘按钮
     */
    private ImageButton moperateDisplayKeypadBtn;
    
    /**
     * 电话号码
     */
    private String mPhoneNumber;
    
    /**
     * 电话号码
     */
    private String mContactDisplayName = "";
    
    /**
     * 开始通话的起始时间
     */
    private long mTalkingStartTime;
    
    /**
     * 逻辑对象
     */
    private IVoipLogic mVoipLogic;
    
    /**
     * 头像数据对象
     */
    private FaceThumbnailModel mFaceThumbnailModel;
    
    /**
     * 判断是呼出电话还是呼入
     */
    private boolean isOutCall;
    
    /**
     * 通话记录逻辑处理类对象
     */
    private ICommunicationLogLogic mCommunicationLogic;
    
    /**
     * 更新时间的线程
     */
    private DisplayTimeThread mDisplayTimeThread;
    
    /**
     * 键盘控制
     */
    private VoipNumberPadUtil mNumberPadUtil;
    
    /**
     * 通知栏图片
     */
    private Bitmap mNotificationIcon;
    
    /**
     * 记录当前的状态
     */
    private int mCurrentState = 0;
    
    /**
     * 更新时间的线程
     * 
     * @author mizhou
     * 
     */
    private class DisplayTimeThread extends Thread
    {
        
        /**
         * 线程运行
         */
        private boolean isRun = true;
        
        /**
         * 时间闪烁
         */
        private boolean isDisplay = true;
        
        /**
         * 通话过程
         */
        private boolean isCalling = true;
        
        /**
         * 关闭通话过程
         */
        public void closeCall()
        {
            isCalling = false;
        }
        
        /**
         * 关闭线程
         */
        public void clear()
        {
            isRun = false;
        }
        
        public boolean isRun()
        {
            return isRun;
        }
        
        public void initThread()
        {
            isRun = true;
            isDisplay = true;
            isCalling = true;
        }
        
        @Override
        public void run()
        {
            Date startTime = new Date();
            long time = 0;
            int flickerNum = 0;
            while (isRun)
            {
                Message msg = new Message();
                //通话过程时间
                if (isCalling)
                {
                    Date curDate = new Date(System.currentTimeMillis());
                    time = curDate.getTime() - startTime.getTime();
                    msg.what = VOIPMessageType.VOIP_CALLING_COUNT_TIME;
                }
                else
                {
                    //显示时间
                    if (isDisplay)
                    {
                        msg.what = VOIPMessageType.VOIP_DISPLAY_TIME;
                        isDisplay = false;
                    }
                    //隐藏时间
                    else
                    {
                        msg.what = VOIPMessageType.VOIP_UNDISPLAY_TIME;
                        isDisplay = true;
                    }
                    
                    //闪烁2次关闭
                    if (flickerNum++ > 4)
                    {
                        clear();
                        VoipCallingActivity.this.finish();
                        return;
                    }
                }
                //发送消息
                msg.obj = DateUtil.VOIP_TALKING_TIME.format(time);
                VoipCallingActivity.this.getHandler().sendMessage(msg);
                //睡眠
                try
                {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    };
    
    /**
     * 二次拨号按钮
     */
    private Handler reDialHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg.what == VOIPMessageType.VOIP_REDIAL)
            {
                mVoipLogic.redial((String) msg.obj);
            }
        };
    };
    
    /**
     * onCreate
     * 
     * @param savedInstanceState
     *            savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.voip_calling);
        initCallInfo(getIntent());
    }
    
    /**
     * 初始化拨打信息
     */
    private void initCallInfo(Intent intent)
    {
        initView();
        mPhoneNumber = intent.getStringExtra(VoipAction.EXTRA_PHONE_NUMBER);
        isOutCall = intent.getBooleanExtra(VoipAction.EXTRA_IS_CALL_OUT, true);
        initContactInfo();
        
        if (isOutCall)
        {
            //设置拨打
            setDataByState(VOIPMessageType.VOIP_CALL_STATE_RINGING);
            call(mContactDisplayName,
                    mPhoneNumber,
                    FastVoIPConstant.AUDIOTYPE,
                    mFaceThumbnailModel);
        }
        else
        {
            //设置来电界面
            setDataByState(VOIPMessageType.VOIP_IN_CALL_STATE_ALERTING);
        }
        
        if (null == mNumberPadUtil)
        {
            mNumberPadUtil = new VoipNumberPadUtil(this,
                    (TextView) findViewById(R.id.contact_name_top),
                    reDialHandler);
        }
    }
    
    /**
     * 点击事件
     * 
     * @param v
     *            View
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            //挂断电话
            case R.id.operating_hang_up:
                mVoipLogic.closeVoip(false);
                break;
            //拒绝接听
            case R.id.operating_refuse:
                mVoipLogic.closeVoip(true);
                break;
            case R.id.operating_answer:
                mVoipLogic.answerVoip();
                break;
            //隐藏键盘
            case R.id.operating_displaykeypad:
                findViewById(R.id.nomal_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.display_keypad).setVisibility(View.GONE);
                moperateDisplayKeypadBtn.setVisibility(View.GONE);
                mKeyBoardBtn.drawDrawable(R.drawable.voip_icon_dispad_on,
                        R.drawable.voip_icon_dispad_off);
                String tempTimeString = mCurrentTimeView.getText().toString();
                mCurrentTimeView = mCallTime;
                mCurrentTimeView.setText(tempTimeString);
                break;
            //免提
            case R.id.speaker:
                mSpeakerBtn.drawDrawable(R.drawable.voip_icon_speaker_on,
                        R.drawable.voip_icon_speaker_off);
                if (!mVoipLogic.isOpenSpeaker())
                {
                    mVoipLogic.openSpeaker();
                }
                else
                {
                    mVoipLogic.closeSpeaker();
                }
                break;
            //录音
            case R.id.record:
                mRecordBtn.drawDrawable(R.drawable.voip_icon_record_on,
                        R.drawable.voip_icon_record_off);
                break;
            //静音
            case R.id.mute:
                mMuteBtn.drawDrawable(R.drawable.voip_icon_mute_on,
                        R.drawable.voip_icon_mute_off);
                if (!mVoipLogic.isMute())
                {
                    mVoipLogic.openMute();
                }
                else
                {
                    mVoipLogic.closeMute();
                }
                break;
            //键盘
            case R.id.keyboard:
                mKeyBoardBtn.drawDrawable(R.drawable.voip_icon_dispad_on,
                        R.drawable.voip_icon_dispad_off);
                if (mKeyBoardBtn.isChecked())
                {
                    findViewById(R.id.nomal_layout).setVisibility(View.GONE);
                    findViewById(R.id.display_keypad).setVisibility(View.VISIBLE);
                    moperateDisplayKeypadBtn.setVisibility(View.VISIBLE);
                    tempTimeString = mCurrentTimeView.getText().toString();
                    mCurrentTimeView = (TextView) findViewById(R.id.call_time_top);
                    mCurrentTimeView.setText(tempTimeString);
                    moperateDisplayKeypadBtn.setEnabled(true);
                    mNumberPadUtil.setBtnEnabled(true);
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 按back键时切后台
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
     * 初始化逻辑对象
     */
    @Override
    protected void initLogics()
    {
        mVoipLogic = (IVoipLogic) super.getLogicByInterfaceClass(IVoipLogic.class);
        mCommunicationLogic = (ICommunicationLogLogic) super.getLogicByInterfaceClass(ICommunicationLogLogic.class);
    }
    
    /**
     * 处理逻辑返回
     * 
     * @param msg
     *            msg
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        switch (msg.what)
        {
            //正在呼叫
            case VOIPMessageType.VOIP_CALL_STATE_RINGING:
                setDataByState(VOIPMessageType.VOIP_CALL_STATE_RINGING);
                break;
            //正在通话
            case VOIPMessageType.VOIP_CALL_STATE_TALKING:
            case VOIPMessageType.VOIP_IN_CALL_STATE_TALKING:
                setDataByState(VOIPMessageType.VOIP_CALL_STATE_TALKING);
                mDisplayTimeThread = new DisplayTimeThread();
                mDisplayTimeThread.start();
                mDisplayTimeThread.initThread();
                break;
            //通话结束
            case VOIPMessageType.VOIP_CALL_STATE_CLOSE:
            case VOIPMessageType.VOIP_IN_CALL_STATE_CLOSE:
                setDataByState(VOIPMessageType.VOIP_CALL_STATE_CLOSE);
                if (getTalkingTime())
                {
                    mDisplayTimeThread.closeCall();
                }
                else
                {
                    finish();
                }
                break;
            //来电振铃
            case VOIPMessageType.VOIP_IN_CALL_STATE_ALERTING:
                setDataByState(VOIPMessageType.VOIP_IN_CALL_STATE_ALERTING);
                break;
            //监听本地通讯录数据库变化数据库
            case VOIPMessageType.VOIP_ADD_CANTACT:
                if (!initPhoneContact())
                {
                    mContactDisplayName = mPhoneNumber;
                    ImageUtil.showFaceOnBackground(mContactIcon,
                            null,
                            null,
                            R.drawable.voip_comm_img_unknow,
                            mContactIcon.getWidth(),
                            mContactIcon.getHeight());
                }
                mContactName.setText(mContactDisplayName);
                mVoipLogic.updateNotificationData(mNotificationIcon,
                        mContactDisplayName);
                break;
            //通话中计时
            case VOIPMessageType.VOIP_CALLING_COUNT_TIME:
                String talkingTime = (String) msg.obj;
                //通话中
                mCurrentTimeView.setVisibility(View.VISIBLE);
                mCurrentTimeView.setText(talkingTime);
                break;
            //结束电话后，显示闪烁的时间
            case VOIPMessageType.VOIP_DISPLAY_TIME:
                mCurrentTimeView.setVisibility(View.VISIBLE);
                break;
            //结束电话后，不显示闪烁的时间
            case VOIPMessageType.VOIP_UNDISPLAY_TIME:
                mCurrentTimeView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
    
    /**
     * 查询本地通讯录
     */
    private boolean initPhoneContact()
    {
        //查询本地通讯录
        PhoneContact contact = mCommunicationLogic.getPhoneContacts(this,
                mPhoneNumber);
        
        if (null != contact)
        {
            mContactDisplayName = contact.getContactName();
            ImageUtil.showFaceOnBackground(mContactIcon,
                    null,
                    contact.getFaceData(),
                    R.drawable.voip_comm_img_unknow,
                    mContactIcon.getWidth(),
                    mContactIcon.getHeight());
            if (null != contact.getFaceData())
            {
                mNotificationIcon = BitmapFactory.decodeByteArray(contact.getFaceData(),
                        0,
                        contact.getFaceData().length);
            }
            else
            {
                mNotificationIcon = null;
            }
            if (mContactDisplayName.equals(mPhoneNumber))
            {
                //如果是陌生人则把原来现实号码的控件隐藏
                mContactNumber.setVisibility(View.GONE);
            }
            else
            {
                mContactNumber.setVisibility(View.VISIBLE);
            }
            return true;
        }
        else
        {
            mNotificationIcon = null;
        }
        //如果是陌生人则把原来现实号码的控件隐藏
        mContactNumber.setVisibility(View.GONE);
        return false;
    }
    
    /**
     * 如果是好友，初始化好友信息
     */
    private void initContactInfo()
    {
        mContactDisplayName = mPhoneNumber;
        //查询本地通讯录
        if (initPhoneContact())
        {
            return;
        }
        
        //查询Hitalk好友,看有没有备注名
        
        ContactInfoModel mContactmInfoModel = mVoipLogic.getContactInfoModelByPhone(mPhoneNumber);
        if (null != mContactmInfoModel)
        {
            //好友的名字
            mContactDisplayName = StringUtil.isNullOrEmpty(mContactmInfoModel.getMemoName()) ? mContactmInfoModel.getDisplayName()
                    : mContactmInfoModel.getMemoName();
            showFace(mContactmInfoModel);
            return;
        }
    }
    
    /**
     * 设置头像
     * 
     * @param mContactmInfoModel
     *            好友对象
     */
    private void showFace(ContactInfoModel mContactmInfoModel)
    {
        // 展示数据库中头像
        mFaceThumbnailModel = mVoipLogic.getFaceThumbnailModel(mContactmInfoModel.getFriendUserId());
        if (null != mFaceThumbnailModel
                && null != mFaceThumbnailModel.getFaceUrl())
        {
            ImageUtil.showFaceOnBackground(mContactIcon,
                    mFaceThumbnailModel.getFaceUrl(),
                    mFaceThumbnailModel.getFaceBytes(),
                    R.drawable.voip_comm_img_unknow,
                    mContactIcon.getWidth(),
                    mContactIcon.getHeight());
            if (null != mFaceThumbnailModel.getFaceBytes())
            {
                mNotificationIcon = BitmapFactory.decodeByteArray(mFaceThumbnailModel.getFaceBytes(),
                        0,
                        mFaceThumbnailModel.getFaceBytes().length);
            }
            
        }
    }
    
    /***
     * 初始化界面
     */
    private void initView()
    {
        mCallState = (TextView) findViewById(R.id.call_state);
        mCallTime = (TextView) findViewById(R.id.call_time);
        mContactIcon = (ImageView) findViewById(R.id.contact_icon);
        mContactName = (TextView) findViewById(R.id.contact_name);
        mContactNumber = (TextView) findViewById(R.id.contact_number);
        mOperatAnswerBtn = (LinearLayout) findViewById(R.id.operating_answer);
        mOperatAnswerBtn.setOnClickListener(this);
        mOperatHangUpBtn = (LinearLayout) findViewById(R.id.operating_hang_up);
        mOperatHangUpBtn.setOnClickListener(this);
        mOperatRefuseBtn = (LinearLayout) findViewById(R.id.operating_refuse);
        mOperatRefuseBtn.setOnClickListener(this);
        mRecordBtn = (CheckButton) findViewById(R.id.record);
        mRecordBtn.setOnClickListener(this);
        mMuteBtn = (CheckButton) findViewById(R.id.mute);
        mMuteBtn.setOnClickListener(this);
        mKeyBoardBtn = (CheckButton) findViewById(R.id.keyboard);
        mKeyBoardBtn.setOnClickListener(this);
        mSpeakerBtn = (CheckButton) findViewById(R.id.speaker);
        mSpeakerBtn.setOnClickListener(this);
        moperateDisplayKeypadBtn = (ImageButton) findViewById(R.id.operating_displaykeypad);
        moperateDisplayKeypadBtn.setOnClickListener(this);
        
        findViewById(R.id.display_keypad).setVisibility(View.GONE);
        findViewById(R.id.nomal_layout).setVisibility(View.VISIBLE);
        mTalkingStartTime = 0;
    }
    
    /**
     * 根据呼叫的类型和状态设置控件内容
     * 
     * @param callType
     *            呼叫的类型和状态
     */
    private void setDataByState(int callState)
    {
        switch (callState)
        {
            //正在呼叫
            case VOIPMessageType.VOIP_CALL_STATE_RINGING:
                findViewById(R.id.operating_hangup).setVisibility(View.VISIBLE);
                mCallTime.setVisibility(View.GONE);
                mOperatAnswerBtn.setVisibility(View.GONE);
                mOperatRefuseBtn.setVisibility(View.GONE);
                mCallState.setText(getString(R.string.voip_call_state_calling));
                mContactName.setText(mContactDisplayName);
                mContactNumber.setText(mPhoneNumber);
                mRecordBtn.setEnabled(false, R.drawable.voip_icon_record_enable);
                mMuteBtn.setEnabled(false, R.drawable.voip_icon_mute_enable);
                mKeyBoardBtn.setEnabled(false,
                        R.drawable.voip_icon_dispad_enable);
                break;
            //正在通话
            case VOIPMessageType.VOIP_CALL_STATE_TALKING:
                findViewById(R.id.operating_hangup).setVisibility(View.VISIBLE);
                mCallTime.setVisibility(View.VISIBLE);
                mCurrentTimeView = mCallTime;
                mOperatHangUpBtn.setVisibility(View.VISIBLE);
                mOperatAnswerBtn.setVisibility(View.GONE);
                mOperatRefuseBtn.setVisibility(View.GONE);
                mRecordBtn.setVisibility(View.VISIBLE);
                mMuteBtn.setVisibility(View.VISIBLE);
                mKeyBoardBtn.setVisibility(View.VISIBLE);
                mSpeakerBtn.setVisibility(View.VISIBLE);
                mCallState.setVisibility(View.GONE);
                mContactName.setText(mContactDisplayName);
                mContactNumber.setText(mPhoneNumber);
                mTalkingStartTime = System.currentTimeMillis();
                if (!mSpeakerBtn.isChecked())
                {
                    mSpeakerBtn.setEnabled(true,
                            R.drawable.voip_icon_speaker_off);
                }
                mMuteBtn.setEnabled(true, R.drawable.voip_icon_mute_off);
                mKeyBoardBtn.setEnabled(true, R.drawable.voip_icon_dispad_off);
                mRecordBtn.setEnabled(false, R.drawable.voip_icon_record_enable);
                mOperatHangUpBtn.setEnabled(true);
                mCurrentState = VOIPMessageType.VOIP_CALL_STATE_TALKING;
                //添加通知栏
                mVoipLogic.showNewNotification(mContactDisplayName,
                        mNotificationIcon,
                        mContactDisplayName,
                        new Date(),
                        getIntent());
                break;
            //通话结束
            case VOIPMessageType.VOIP_CALL_STATE_CLOSE:
                mCallState.setVisibility(View.VISIBLE);
                mCallState.setText(getString(R.string.voip_call_state_talk_end));
                mContactNumber.setText(mPhoneNumber);
                mContactIcon.setEnabled(false);
                mContactName.setEnabled(false);
                mRecordBtn.setEnabled(false, R.drawable.voip_icon_record_enable);
                mSpeakerBtn.setEnabled(false,
                        R.drawable.voip_icon_speaker_enable);
                mMuteBtn.setEnabled(false, R.drawable.voip_icon_mute_enable);
                mKeyBoardBtn.setEnabled(false,
                        R.drawable.voip_icon_dispad_enable);
                mOperatHangUpBtn.setEnabled(false);
                mOperatAnswerBtn.setEnabled(false);
                mOperatRefuseBtn.setEnabled(false);
                moperateDisplayKeypadBtn.setEnabled(false);
                findViewById(R.id.end_cover).setVisibility(View.VISIBLE);
                mNumberPadUtil.setBtnEnabled(false);
                mCurrentState = VOIPMessageType.VOIP_CALL_STATE_CLOSE;
                //取消通知栏
                mVoipLogic.destroyNotification();
                if (null != mNotificationIcon)
                {
                    mNotificationIcon.recycle();
                    mNotificationIcon = null;
                }
                break;
            //来电振铃
            case VOIPMessageType.VOIP_IN_CALL_STATE_ALERTING:
                findViewById(R.id.operating_hangup).setVisibility(View.GONE);
                mCallState.setText(getString(R.string.voip_in_call_alerting));
                mContactIcon.setVisibility(View.VISIBLE);
                mContactName.setVisibility(View.VISIBLE);
                mRecordBtn.setVisibility(View.GONE);
                mSpeakerBtn.setVisibility(View.GONE);
                mMuteBtn.setVisibility(View.GONE);
                mKeyBoardBtn.setVisibility(View.GONE);
                mOperatHangUpBtn.setVisibility(View.GONE);
                mOperatAnswerBtn.setVisibility(View.VISIBLE);
                mOperatRefuseBtn.setVisibility(View.VISIBLE);
                mCallTime.setVisibility(View.GONE);
                mContactName.setText(mContactDisplayName);
                mContactNumber.setText(mPhoneNumber);
                mOperatAnswerBtn.setEnabled(true);
                mOperatRefuseBtn.setEnabled(true);
                findViewById(R.id.end_cover).setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
    
    /**
     * 判断是否接通电话
     * 
     * @return 接通
     */
    private boolean getTalkingTime()
    {
        return mTalkingStartTime > 0;
    }
    
    /**
     * 拨打电话
     * 
     * @param displayName
     *            昵称
     * @param phoneNum
     *            号码
     * @param type
     *            呼叫类型
     */
    private void call(String displayName, String phoneNum, String type,
            FaceThumbnailModel faceModel)
    {
        mVoipLogic.callVoip(displayName,
                phoneNum.replace("+", "00"),
                type,
                faceModel);
    }
    
    /**
     * 这个Activity是singleTop的所以在接到来电的时候需要重新初始化来电信息
     * 
     * @param intent
     *            intent
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (mCurrentState == VOIPMessageType.VOIP_CALL_STATE_CLOSE)
        {
            if (mDisplayTimeThread.isRun())
            {
                mDisplayTimeThread.closeCall();
                mDisplayTimeThread.clear();
                mDisplayTimeThread = null;
            }
            initCallInfo(intent);
        }
    }
}
