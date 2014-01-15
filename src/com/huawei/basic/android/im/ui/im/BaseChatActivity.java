/*
 * 文件名: BaseChatActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.ClipboardManager;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.CropImageAction;
import com.huawei.basic.android.im.common.FusionAction.RecordVideoAction;
import com.huawei.basic.android.im.common.FusionCode;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.common.FusionMessageType.ChatMessageType;
import com.huawei.basic.android.im.component.database.DatabaseHelper.MediaIndexColumns;
import com.huawei.basic.android.im.component.location.LocationDataListener;
import com.huawei.basic.android.im.component.location.LocationInfo;
import com.huawei.basic.android.im.component.location.RCSLocationManager;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.group.IGroupLogic;
import com.huawei.basic.android.im.logic.im.IImLogic;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.logic.model.MediaIndexModel;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.PullToRefreshListView;
import com.huawei.basic.android.im.ui.basic.PullToRefreshListView.OnRefreshListener;
import com.huawei.basic.android.im.ui.basic.RichEditText;
import com.huawei.basic.android.im.ui.basic.emotion.EmotionManager;
import com.huawei.basic.android.im.ui.basic.emotion.EmotionWindow;
import com.huawei.basic.android.im.ui.basic.emotion.IPinupEmotionClickListener;
import com.huawei.basic.android.im.ui.basic.emotion.bean.EmojiBean;
import com.huawei.basic.android.im.ui.im.SoftKeyBoardDetectLinearLayout.SoftKeyBoardDetectListener;
import com.huawei.basic.android.im.ui.im.item.BaseMsgItem;
import com.huawei.basic.android.im.ui.im.item.HolderEventListener;
import com.huawei.basic.android.im.ui.im.item.ItemCreator;
import com.huawei.basic.android.im.ui.im.item.Switcher;
import com.huawei.basic.android.im.utils.StringUtil;
import com.huawei.basic.android.im.utils.TimeThread;
import com.huawei.basic.android.im.utils.UriUtil.FromType;

/**
 * 抽象的聊天基类<BR>
 * 负责初始化聊天页面，实现界面组件初始化，以及公有的UI逻辑处
 * 理，如选择表情、录音、录像、发送图片等功能
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public abstract class BaseChatActivity extends BasicActivity implements
        OnClickListener, OnTouchListener, SoftKeyBoardDetectListener,
        HolderEventListener, OnRefreshListener, IPinupEmotionClickListener,
        LocationDataListener
{
    /**
     * DEBUG TAG.
     */
    private static final String TAG = "BaseChatActivity";
    
    /**
     * 使用Google Maps浏览器地址格式化串
     */
    private static final String LOCATION_IN_GOOGLE_MAPS_URL = "http://maps.google.com/maps?q=loc:%s,%s";
    
    /**
     * 菜单项传递参数通过intent实现，这里定义msg消息体
     */
    private static final String EXTRA_MSG = "msg";
    
    /**
     * 菜单项传递参数通过intent实现，这里定义extra name
     */
    private static final String EXTRA_MSG_ID = "msg_id";
    
    /**
     * Gallery跳转的EXTRA
     */
    private static final String EXTRA_OUTPUT_FORMAT = "outputFormat";
    
    /**
     * Gallery跳转的type
     */
    private static final String TYPE_OF_GET_CONTENT = "image/*";
    
    /**
     * 页面显示消息条数，为18条。每次刷新时再多下拉18条
     */
    private static final int PER_MSG_COUNT = 18;
    
    /**
     * 当前显示的消息条数
     */
    private int mCurMsgCount = 0;
    
    /**
     * ImLogic
     */
    private IImLogic mImLogic;
    
    /**
     * GroupLogic
     */
    private IGroupLogic mGroupLogic;
    
    /**
     * message list view.
     */
    private PullToRefreshListView mMsgListView;
    
    /**
     * 按下说话提示窗口
     * */
    private PopupWindow mSoundRecorderWindow;
    
    /**
     * 选择表情按钮
     */
    private Button mPickEmotionBtn;
    
    /**
     * 发送按钮
     */
    private Button mSendBtn;
    
    /**
     * 录音按钮
     */
    private View mRecordBtn;
    
    /**
     * 聊天list view的Adapter对象
     */
    private BaseMsgAdapter mMsgAdapter;
    
    /**
     * 底部按钮栏
     */
    private View mBottomBar;
    
    /**
     * 顶部标题栏
     */
    private TextView mTitle;
    
    /**
     * 群组成员总数
     */
    private TextView mTitleCount;
    
    /**
     * 编辑框
     */
    private RichEditText mEditText;
    
    /**
     * 返回按钮
     */
    private Button mLeftButton;
    
    /**
     * 右边按钮（跳转到聊吧成员界面）
     */
    private LinearLayout mRightButton;
    
    /**
     * 发送视频
     */
    private View mVideoButton;
    
    /**
     * 发送图片
     */
    private View mImageButton;
    
    /**
     * 自己的头像
     */
    private Drawable mMyFace;
    
    /**
     * 选择表情时光标的开始位置
     */
    private int mChatTextSelectionStart;
    
    /**
     * 选择表情时光标的结束位置
     */
    private int mChatTextSelectionEnd;
    
    /**
     * 消息列表条目的各点击事件监听器对象
     */
    private HolderEventListener mHolderEventListener;
    
    /**
     * 音频录制对象
     */
    private MediaRecorder mMediaRecorder;
    
    /**
     * 要播放音频的绝对路径
     */
    private String mSoundFilePath;
    
    /**
     * 监听录制音频音量的进度条
     */
    private ImageView mAudioProgressBar;
    
    /**
     * 根据eable判断是否停止线程
     */
    private boolean mProgressBarEable = true;
    
    /**
     * 获取当前音量
     */
    private int mCurrentVoice;
    
    /**
     * 录制音频取消视图
     */
    private LinearLayout mRightView;
    
    /**
     * 贴图的缓存map
     */
    private SoftReference<Map<String, EmojiBean>> mEmojiMap;
    
    /**
     * 获取地理位置的diglog
     */
    private ProgressDialog mProgressDlg;
    
    /**
     * 时间计时线程
     */
    private TimeThread mTimeThread;
    
    /**
     * 取消发送音频文字
     */
    private TextView mCancelText;
    
    /**
     * 时间显示TextView
     */
    private TextView mTimeText;
    
    /**
     * 录制时显示剩余时间的view对象
     */
    private LinearLayout mTimeLayout;
    
    /**
     * 重发按钮是否可用，默认可用
     * 
     */
    private boolean mBtnAvailable = true;
    
    /**
     * 录制音频时麦克风的Image
     */
    private ImageView mMicImageView;
    
    /**
     * 音频子线程
     */
    //TODO:系统音量变化是否会发出广播，按广播接收方式进行处理
    private Runnable mAudioPbRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            while (mProgressBarEable)
            {
                try
                {
                    mCurrentVoice = mMediaRecorder.getMaxAmplitude();
                }
                catch (IllegalStateException e)
                {
                    mCurrentVoice = 0;
                }
                catch (Exception e)
                {
                    mCurrentVoice = 0;
                    Logger.e(TAG, " mMediaRecorder.getMaxAmplitude failed", e);
                }
                //得到当前音量
                if (mMediaRecorder != null)
                {
                    Message msg = new Message();
                    msg.what = FusionMessageType.ChatMessageType.SOUND_AMPLITUDE;
                    msg.obj = mCurrentVoice / 100;
                    getHandler().sendMessage(msg);
                }
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    Logger.e(TAG, "get current voice InterruptedException", e);
                }
            }
        }
    };
    
    /**
     * 缓存bitmap
     */
    private HashMap<String, Bitmap> mBitmapCache;
    
    /**
     * 未读音频消息ID列表
     */
    private List<String> mUnreadAudioMsgIds;
    
    /**
     * 正在播放的音频消息id
     */
    private String mPlayingAudioMsgId;
    
    /**
     * 是否正在播放
     */
    private boolean mIsPlaying;
    
    /**
     * 播放音乐类
     */
    private MediaPlayer mMediaPlayer;
    
    /**
     * 在进入系统拍照页面时需要生产图片保存路径，拍照页面返回时可能不会返回路径，所以需要临时保存一下。
     */
    private String mTmpImgPath;
    
    /**
     * 保存正在下载过程中的位置消息id集合
     */
    private List<String> mLocationDownList;
    
    /**
     * get msgAdapter
     * @return the msgAdapter
     */
    public BaseMsgAdapter getMsgAdapter()
    {
        return mMsgAdapter;
    }
    
    /**
     * get mImLogic
     * @return the mImLogic
     */
    public IImLogic getImLogic()
    {
        return mImLogic;
    }
    
    /**
     * get IGroupLogic<BR>
     * @return the mGroupLogic
     */
    public IGroupLogic getGroupLogic()
    {
        return mGroupLogic;
    }
    
    /**
     * 设置右边按钮不可见
     */
    public void setmRightButton()
    {
        mRightButton.setVisibility(View.INVISIBLE);
    }
    
    /**
     * 
     * 设置标题<BR>
     * 
     * @param titleName 标题名
     */
    public void setTitle(String titleName)
    {
        if (titleName != null && mTitle != null)
        {
            mTitle.setText(titleName);
        }
    }
    
    /**
     * 设置群组成员总数<BR>
     * @param count 群组成员总数
     */
    public void setTitleCount(String count)
    {
        mTitleCount.setVisibility(View.VISIBLE);
        mTitleCount.setText(count);
    }
    
    /**
     * 右上角按钮点击事件<BR>
     */
    public void onRightButtonClick()
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * 设置右边按钮<BR>
     * @param buttonName 按钮名称
     */
    public void setRightButton(String buttonName)
    {
        // TODO 由子类覆写该方法
    }
    
    /**
     * 
     * 单击事件处理<BR>
     * {@inheritDoc}
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
        //显示表情
            case R.id.pick_custom_face:
                //如果当前表情选择界面是展开的，则关闭
                if (EmotionWindow.getCurrInstance(this).getWindowStatus() == View.VISIBLE)
                {
                    EmotionWindow.getCurrInstance(this).closePopWindow();
                    EmotionWindow.getCurrInstance(this)
                            .setWindowStatus(View.GONE);
                    showBottomBar();
                    return;
                }
                //记录光标位置
                mChatTextSelectionStart = mEditText.getSelectionStart();
                mChatTextSelectionEnd = mEditText.getSelectionEnd();
                showEmotionWin();
                hideBottomBar();
                break;
            //发送消息
            case R.id.send:
                String content = mEditText.getText().toString().trim();
                if (content.length() > 0)
                {
                    mEditText.setText(null);
                    send(content);
                }
                else
                {
                    showToast(R.string.cant_not_send_empty);
                }
                break;
            //返回按钮
            case R.id.left_button:
                finish();
                break;
            case R.id.right_layout:
                onRightButtonClick();
                break;
            // 选择图片或视频  我的位置
            case R.id.left_image:
                Builder builder = new AlertDialog.Builder(this);
                String[] items = new String[] {
                        getString(R.string.menu_picture),
                        getString(R.string.menu_video),
                        getString(R.string.send_my_position) };
                builder.setTitle(R.string.operation);
                builder.setItems(items, new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // 弹出选择本地图片还是拍照的上下文菜单
                        if (which == 0)
                        {
                            Builder builder = new AlertDialog.Builder(
                                    BaseChatActivity.this);
                            String[] items = new String[] {
                                    getString(R.string.submenu_native_picture),
                                    getString(R.string.submenu_camera) };
                            builder.setTitle(R.string.operation);
                            builder.setItems(items,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(
                                                DialogInterface arg0, int arg1)
                                        {
                                            if (arg1 == 0)
                                            {
                                                Logger.d(TAG, "local album");
                                                Intent toGallery = new Intent(
                                                        Intent.ACTION_GET_CONTENT);
                                                toGallery.setType(TYPE_OF_GET_CONTENT);
                                                toGallery.putExtra(EXTRA_OUTPUT_FORMAT,
                                                        "JPEG");
                                                startActivityForResult(Intent.createChooser(toGallery,
                                                        ""),
                                                        FusionAction.ChatAction.REQUEST_CODE_SELECT_PICTURE);
                                            }
                                            else
                                            {
                                                Logger.d(TAG, "take a picture");
                                                //判断SD卡是否存在  和 判断sd卡是否有合适容量5M
                                                if (mImLogic.sdCardExist())
                                                {
                                                    //给刚要拍照的照片命名
                                                    String filePath = mImLogic.getImageFilePath(FromType.SEND);
                                                    mTmpImgPath = filePath;
                                                    File file = new File(
                                                            filePath);
                                                    Intent toSysCamera = new Intent(
                                                            "android.media.action.IMAGE_CAPTURE");
                                                    // Samsung的系统相机，版式是横板的,同时此activity不要设置单例模式
                                                    toSysCamera.putExtra(MediaStore.Images.Media.ORIENTATION,
                                                            0);
                                                    toSysCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                                                            Uri.fromFile(file));
                                                    // 调用系统拍照
                                                    startActivityForResult(toSysCamera,
                                                            FusionAction.ChatAction.REQUEST_CODE_CAMERA);
                                                }
                                                else
                                                {
                                                    //SD卡不存在
                                                    showToast(R.string.sd_not_exist);
                                                }
                                            }
                                        };
                                    });
                            builder.create().show();
                        }
                        
                        // 进入视频录制页面
                        else if (which == 1)
                        {
                            // 进入录制视频页面
                            Intent recordVideoIntent = new Intent(
                                    FusionAction.RecordVideoAction.ACTION);
                            recordVideoIntent.putExtra(FusionAction.RecordVideoAction.RECORD,
                                    true);
                            recordVideoIntent.putExtra(RecordVideoAction.NEW_PATH,
                                    mImLogic.getVideoFilePath(FromType.SEND));
                            startActivityForResult(recordVideoIntent,
                                    FusionAction.ChatAction.REQUEST_CODE_RECORD_VIDEO);
                        }
                        //获取我的位置
                        else
                        {
                            //GPS未打开                          
                            if (!mImLogic.isGPSEnabled())
                            {
                                //GPS未打开，并且选中不再提示，直接发送
                                if (BaseChatActivity.this.getSharedPreferences(FusionCode.Common.SHARED_PREFERENCE_NAME,
                                        Context.MODE_PRIVATE)
                                        .getBoolean(FusionCode.Common.SHARE_GPS_LOCATION,
                                                false))
                                {
                                    //无论是否勾选“下次不再提示”都要打开GPS
                                    try
                                    {
                                        mImLogic.toggleGPS();
                                    }
                                    catch (Exception e)
                                    {
                                        showToast(R.string.can_not_determine_location_info);
                                        cancelLocationWaitingDialog();
                                        Logger.e(TAG, "GPS Exception", e);
                                    }
                                    //GPS已打开，直接获取位置
                                    RCSLocationManager rcsLocationManager = new RCSLocationManager(
                                            BaseChatActivity.this);
                                    rcsLocationManager.getLocationInfo(BaseChatActivity.this,
                                            true);
                                    //显示进度
                                    showLocationWaitingDialog();
                                }
                                else
                                {
                                    
                                    //如果以勾选“下次不再提示”，不管是否开启GPS下次都不再提示
                                    //显示dialog 
                                    showOpenGpsDialog();
                                }
                                
                            }
                            else
                            {
                                //GPS已打开，直接获取位置
                                RCSLocationManager rcsLocationManager = new RCSLocationManager(
                                        BaseChatActivity.this);
                                rcsLocationManager.getLocationInfo(BaseChatActivity.this,
                                        true);
                                //显示进度
                                showLocationWaitingDialog();
                            }
                            
                        }
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            // 涂鸦 （暂未实现）TODO:
            case R.id.right_image:
                showToast(R.string.draw);
                break;
            default:
                break;
        }
        
    }
    
    /**
     * 发送消息<BR>
     * [功能详细描述]
     * @param textCnt 消息内容
     */
    public abstract void send(String textCnt);
    
    /**
     * 发送媒体消息<BR>
     * @param textCnt 文本消息
     * @param media MediaIndexModel
     */
    public abstract void sendMediaMsg(String textCnt, MediaIndexModel media);
    
    /**
     * onTouch事件<BR>
     * @param view view
     * @param event MotionEvent
     * @return super.onTouchEvent(event)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        //移动到的当前范围
        float rectX = event.getRawX();
        float rectY = event.getRawY();
        switch (view.getId())
        {
            case R.id.record_button:
                Logger.i(TAG, "event get action=" + event.getAction());
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        mRecordBtn.setPressed(true);
                        //判断SD卡是否存在  和 判断sd卡是否有合适容量5M
                        if (mImLogic.sdCardExist())
                        {
                            Logger.i(TAG, "show popupWindow");
                            mSoundRecorderWindow = new PopupWindow(
                                    getLayoutInflater().inflate(R.layout.media_ability,
                                            null), LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT);
                            mSoundRecorderWindow.showAtLocation(findViewById(R.id.im_parent),
                                    Gravity.CENTER,
                                    0,
                                    0);
                            initPopupWindow();
                            Logger.i(TAG, "mMediaRecorder==" + mMediaRecorder);
                            //开始录音
                            //被踢出时正在录制
                            beginRecord();
                        }
                        else
                        {
                            //SD卡不存在
                            showToast(R.string.sdcasd_no_size);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mRecordBtn.setPressed(true);
                        
                        // 获取BottomBar区域
                        // 获取bottom_bar视图的左上点位置
                        int[] bottomLocation = new int[2];
                        mBottomBar.getLocationOnScreen(bottomLocation);
                        
                        // 1.如果点击处在BottomBar的区域内，右边取消发送部分不显示
                        if (rectY > bottomLocation[1])
                        {
                            // 如果右边取消发送部分处于显示状态，则设置隐藏；如果处于未显示状态，则不做任何更改。
                            if (mRightView != null && mRightView.isShown())
                            {
                                mRightView.setVisibility(View.GONE);
                            }
                        }
                        
                        // 2.如果点击处在BottomBar的区域外，右边取消发送部分显示出来
                        else if (mRightView != null)
                        {
                            // 2.1 如果右侧区域处于未显示状态，设置显示
                            if (!mRightView.isShown())
                            {
                                mRightView.setVisibility(View.VISIBLE);
                                mSoundRecorderWindow.showAtLocation(findViewById(R.id.im_parent),
                                        Gravity.CENTER,
                                        0,
                                        0);
                            }
                            
                            // 2.2 再根据当前点击处是否处于右侧区域内，来判断右侧显示文字状态
                            //获取右边取消视图的左上点坐标位置（正值）
                            int[] rightLocation = new int[2];
                            mRightView.getLocationOnScreen(rightLocation);
                            //如果在右边移动到右边按钮上右边按钮换背景
                            if (rectY < bottomLocation[1]
                                    && rectY > rightLocation[0]
                                            + mRightView.getHeight())
                            {
                                mRightView.setBackgroundResource(R.drawable.bg_voice_right);
                                mCancelText.setVisibility(View.VISIBLE);
                            }
                            //获取取消视图的矩形面积
                            RectF rectF = new RectF(rightLocation[0],
                                    rightLocation[1], rightLocation[0]
                                            + mRightView.getWidth(),
                                    rightLocation[1] + mRightView.getHeight());
                            // A.如果在右侧区域，则显示取消发送的功能； 
                            if (rectF.contains(rectX, rectY))
                            {
                                mCancelText.setText(getResources().getString(R.string.send_cancel));
                                mRightView.setBackgroundResource(R.drawable.bg_voice_right);
                                mSoundRecorderWindow.showAtLocation(findViewById(R.id.im_parent),
                                        Gravity.CENTER,
                                        0,
                                        0);
                            }
                            // B.如果不在右侧区域，不做任何更改
                            else
                            {
                                mRightView.setBackgroundColor(Color.TRANSPARENT);
                                mCancelText.setText(getResources().getString(R.string.click_cancel));
                                mSoundRecorderWindow.showAtLocation(findViewById(R.id.im_parent),
                                        Gravity.CENTER,
                                        0,
                                        0);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // 停止计时
                        // POPUPWINDOW消失
                        boolean cancelSend = false;
                        //是否在取消发送的区域
                        if (mRightView != null && mRightView.isShown())
                        {
                            int[] rightLocation = new int[2];
                            mRightView.getLocationOnScreen(rightLocation);
                            RectF rectF = new RectF(rightLocation[0],
                                    rightLocation[1], rightLocation[0]
                                            + mRightView.getWidth(),
                                    rightLocation[1] + mRightView.getHeight());
                            if (rectF.contains(rectX, rectY))
                            {
                                Log.i(TAG, "执行取消发送");
                                // 执行取消发送，关闭window，直接跳出switch/case.
                                mSoundFilePath = null;
                                cancelSend = true;
                            }
                        }
                        if (mSoundRecorderWindow != null)
                        {
                            mSoundRecorderWindow.dismiss();
                        }
                        // 发送语音
                        
                        mRecordBtn.setPressed(false);
                        //结束录音
                        if (null == mMediaRecorder)
                        {
                            break;
                        }
                        endRecord();
                        
                        Logger.i(TAG, "end Record");
                        if (cancelSend)
                        {
                            break;
                        }
                        
                        if (mSoundFilePath != null)
                        {
                            MediaPlayer mp = MediaPlayer.create(this,
                                    Uri.parse(mSoundFilePath));
                            int duration = mp.getDuration();
                            int playTime = duration / 1000;
                            Logger.i(TAG, "duration=" + duration);
                            mp.release();
                            if (duration <= 1000)
                            {
                                showToast(R.string.short_record);
                                Log.i(TAG, "执行取消发送");
                                break;
                            }
                            else
                            {
                                MediaIndexModel audioMedia = new MediaIndexModel();
                                audioMedia.setMediaPath(mSoundFilePath);
                                audioMedia.setMediaType(MediaIndexModel.MEDIATYPE_AUDIO);
                                audioMedia.setPlayTime(playTime);
                                sendMediaMsg(null, audioMedia);
                            }
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.msg_history:
                if (EmotionWindow.getCurrInstance(this).getWindowStatus() == View.VISIBLE)
                {
                    EmotionWindow.getCurrInstance(this).closePopWindow();
                    showBottomBar();
                }
                break;
            case R.id.chatText:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        if (EmotionWindow.getCurrInstance(this)
                                .getWindowStatus() == View.VISIBLE)
                        {
                            EmotionWindow.getCurrInstance(this)
                                    .closePopWindow();
                            showBottomBar();
                        }
                        showSoftKeyBoard(mEditText);
                        break;
                }
                return false;
        }
        
        return true;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo)
    {
        /*
         * 说明：此方法中getTag()方法的key值与BaseMsgItem中setTag()方法中保持一致
         */
        BaseMessageModel msg = (BaseMessageModel) v.getTag();
        // 使用intent将msg传递到MenuItem中
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MSG, msg);
        Object canCopy = v.getTag(R.string.im_msg_menu_copy);
        if (canCopy != null && ((Boolean) canCopy))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0, R.string.im_msg_menu_copy, 0, R.string.im_msg_menu_copy)
                    .setIntent(intent);
        }
        Object canTransfer = v.getTag(R.string.im_msg_menu_transfer);
        if (canTransfer != null && ((Boolean) canTransfer))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0,
                    R.string.im_msg_menu_transfer,
                    0,
                    R.string.im_msg_menu_transfer).setIntent(intent);
        }
        Object canShare = v.getTag(R.string.im_msg_menu_share);
        if (canShare != null && ((Boolean) canShare))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0,
                    R.string.im_msg_menu_share,
                    0,
                    R.string.im_msg_menu_share).setIntent(intent);
        }
        Object canDelete = v.getTag(R.string.im_msg_menu_delete);
        if (canDelete != null && ((Boolean) canDelete))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0,
                    R.string.im_msg_menu_delete,
                    0,
                    R.string.im_msg_menu_delete).setIntent(intent);
        }
        
        // 该消息发送失败，需要提供重发的入口
        Object needResend = v.getTag(R.string.im_msg_menu_resend);
        //重发
        if (needResend != null && ((Boolean) needResend))
        {
            menu.setHeaderTitle(R.string.operation);
            menu.add(0,
                    R.string.im_msg_menu_resend,
                    0,
                    R.string.im_msg_menu_resend).setIntent(intent);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        Intent intent = item.getIntent();
        if (intent != null)
        {
            BaseMessageModel msg = (BaseMessageModel) intent.getSerializableExtra(EXTRA_MSG);
            Logger.d(TAG, "onContextItemSelected() msgID:" + msg.getMsgId());
            switch (item.getItemId())
            {
                case R.string.im_msg_menu_copy:
                    copyMsg(msg.getMsgContent());
                    break;
                
                case R.string.im_msg_menu_transfer:
                    transferMsg(msg.getMsgId());
                    break;
                
                case R.string.im_msg_menu_share:
                    
                    break;
                
                case R.string.im_msg_menu_delete:
                    Logger.d(TAG, "delete one msg, msg id : " + msg.getMsgId());
                    deleteMsg(msg.getMsgId());
                    break;
                //重发Menu
                case R.string.im_msg_menu_resend:
                    Logger.d(TAG, "resend msg, msg id : " + msg.getMsgId());
                    resendMsg(msg);
                    break;
                default:
                    break;
            }
        }
        return true;
    }
    
    /**
     * onKeyBoardShown<BR>
     * @param shown boolean
     * @see com.huawei.basic.android.im.ui.im.SoftKeyBoardDetectLinearLayout.SoftKeyBoardDetectListener#onKeyBoardShown(boolean)
     */
    @Override
    public void onKeyBoardShown(boolean shown)
    {
        if (shown)
        {
            getHandler().sendEmptyMessageDelayed(FusionMessageType.ChatMessageType.HIDE_BOTTOM_BAR,
                    10);
        }
        else
        {
            if (EmotionWindow.getCurrInstance(this).getWindowStatus() != View.VISIBLE)
            {
                getHandler().sendEmptyMessageDelayed(FusionMessageType.ChatMessageType.SHOW_BOTTOM_BAR,
                        10);
            }
        }
    }
    
    /**
     * 刷新消息<BR>
     * @see com.huawei.basic.android.im.ui.basic.PullToRefreshListView.OnRefreshListener#onRefresh()
     */
    @Override
    public void onRefresh()
    {
        int tmpCurMsgCount = mCurMsgCount;
        mCurMsgCount += PER_MSG_COUNT;
        refreshMsgList(false);
        int index = mCurMsgCount - tmpCurMsgCount;
        
        // 在刷新消息列表时展示刷出的消息的最后一条
        mMsgListView.setSelectionFromTop(index, 0);
        mMsgListView.onRefreshComplete();
    }
    
    /**
     * 判断窗口焦点是否改变<BR>
     * 滑动解锁问题发送方消息状态刷新问题解决点
     * @param hasFocus 是否获取焦点
     * @see android.app.Activity#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            refreshMsgList(false);
        }
    }
    
    /**
     * 
     * 设置button灰化处理<BR>
     */
    public void setButtonUnAvailable()
    {
        mBtnAvailable = false;
        mPickEmotionBtn.setEnabled(false);
        mEditText.setEnabled(false);
        mEditText.setFocusable(false);
        mSendBtn.setEnabled(false);
        mImageButton.setEnabled(false);
        mRecordBtn.setEnabled(false);
        mVideoButton.setEnabled(false);
        mRightButton.setEnabled(false);
        
    }
    
    /**
     * 
     * 获取位置后发送位置消息<BR>
     * 
     * @param location Location
     * @param address address
     */
    protected void onGetLocationDone(Location location, String address)
    {
        MediaIndexModel mediaModel = new MediaIndexModel();
        mediaModel.setLocationLon(Double.toString(location.getLongitude()));
        mediaModel.setLocationLat(Double.toString(location.getLatitude()));
        mediaModel.setMediaAlt(address);
        mediaModel.setMediaType(MediaIndexModel.MEDIATYPE_LOCATION);
        mediaModel.setMediaURL(getImLogic().buildLocationDlUrl(location.getLongitude(),
                location.getLatitude()));
        sendMediaMsg(null, mediaModel);
    }
    
    /**
     * 
     * 聊天页面使用统一的展示layout<BR>
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");
        setContentView(R.layout.im_main);
        initView();
        Logger.d(TAG, "initView");
        
        init();
        Logger.d(TAG, "initData");
        
        // 构造BaseMsgAdapter
        mCurMsgCount += PER_MSG_COUNT;
        mMsgAdapter = buildMsgAdapter(mCurMsgCount);
        
        // 设置Adapter
        mMsgListView.setAdapter(mMsgAdapter);
        
        //设置ListView底部显示在Activity底部
        mMsgListView.setSelection(mMsgListView.getCount() - 1);
        if (null != savedInstanceState)
        {
            mTmpImgPath = savedInstanceState.getString("saveData");
        }
        
    }
    
    /**
     * onNewIntent<BR>
     * 处理从群组选择单人聊天，再从单人聊天创建聊吧时的页面跳转问题
     * @param intent Intent
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    
    @Override
    protected void onNewIntent(Intent intent)
    {
        Logger.d(TAG, "onNewIntent");
        setContentView(R.layout.im_main);
        setIntent(intent);
        initView();
        Logger.d(TAG, "onNewIntent  initView");
        
        init();
        Logger.d(TAG, "onNewIntent  initData");
        
        // 构造BaseMsgAdapter
        mCurMsgCount += PER_MSG_COUNT;
        mMsgAdapter = buildMsgAdapter(mCurMsgCount);
        
        // 设置Adapter
        mMsgListView.setAdapter(mMsgAdapter);
        
        //设置ListView底部显示在Activity底部
        mMsgListView.setSelection(mMsgListView.getCount() - 1);
        super.onNewIntent(intent);
    }
    
    /**
     * 页面处于onPause停止播放音频<BR>
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onPause()
     */
    
    @Override
    protected void onPause()
    {
        super.onPause();
        stopPlayAudio();
        //如果正在录制时接收到电话等操作，界面切换到后台
        if (mSoundRecorderWindow != null && mSoundRecorderWindow.isShowing())
        {
            mSoundRecorderWindow.dismiss();
            endRecord();
        }
    }
    
    /**
     * 
     * 删除某条消息<BR>
     * 
     * @param msgID 消息id
     */
    protected abstract void deleteMsg(String msgID);
    
    /**
     * 
     * 转发一条消息<BR>
     * 
     * @param msgID 消息ID
     * @param friendUserIds 好友ID
     */
    protected abstract void transferMsg(String msgID, String[] friendUserIds);
    
    /**
     * 消息重发事件<BR>
     * @param msg BaseMessageModel
     */
    protected abstract void resendMsg(BaseMessageModel msg);
    
    /**
     * 消息复制到系统剪切板
     * <BR>
     * @param msgContent 消息内容
     */
    protected void copyMsg(String msgContent)
    {
        //获取剪贴板管理服务
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //将文本数据复制到剪贴板
        cm.setText(msgContent);
    }
    
    /**
     * 在销毁时注销监听<BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onDestroy()
     */
    
    @Override
    protected void onDestroy()
    {
        unregisterObserver();
        if (mMediaRecorder != null)
        {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mProgressBarEable = false;
        }
        if (mMediaPlayer != null)
        {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mTimeThread != null)
        {
            mTimeThread.cancel();
        }
        hideInputWindow(mLeftButton);
        // TODO:关闭游标
        super.onDestroy();
    }
    
    /**
    * 
    * 重写onActivityResult()方法<BR>
    * {@inheritDoc}
    * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
            // 选择表情页面返回
                case FusionAction.ChatAction.REQUEST_CODE_EMOTION:
                    if (data != null)
                    {
                        final String word = data.getStringExtra(FusionAction.ChatAction.EXTRA_EMOTION_STR);
                        Logger.d(TAG, word);
                        
                        Editable editable = mEditText.getEditableText();
                        
                        editable.replace(mChatTextSelectionStart,
                                mChatTextSelectionEnd,
                                EmotionManager.getInstance(this.getApplicationContext())
                                        .format(word));
                        
                    }
                    break;
                
                // 转发时选择好友页面返回
                case FusionAction.ChatAction.REQUEST_CODE_TO_CHOOSE_MEMBER_FOR_TRANSFER:
                    //获取选中的用户的friendUserId
                    if (data != null)
                    {
                        String[] savedIds = data.getStringArrayExtra(FusionAction.ChooseMemberAction.RESULT_CHOOSED_USER_ID_LIST);
                        String msgId = data.getStringExtra(EXTRA_MSG_ID);
                        transferMsg(msgId, savedIds);
                    }
                    break;
                //选择图片
                case FusionAction.ChatAction.REQUEST_CODE_SELECT_PICTURE:
                    // 1.获取图片的本地路径
                    if (null != data)
                    {
                        Uri uriImage = data.getData();
                        Cursor cursorImage = this.getContentResolver()
                                .query(uriImage, null, null, null, null);
                        String imgSelectedPath = null;
                        if (cursorImage != null)
                        {
                            if (cursorImage.moveToNext())
                            {
                                imgSelectedPath = cursorImage.getString(cursorImage.getColumnIndex("_data"));
                                cursorImage.close();
                            }
                        }
                        else
                        {
                            imgSelectedPath = uriImage.getPath();
                        }
                        Logger.d(TAG, "imgSelectedPath = " + imgSelectedPath);
                        if (null != imgSelectedPath)
                        {
                            if (imgSelectedPath.endsWith(".jpg")
                                    || imgSelectedPath.endsWith(".JPG")
                                    || imgSelectedPath.endsWith(".PNG")
                                    || imgSelectedPath.endsWith(".png"))
                            {
                                Intent toPreview = new Intent(
                                        FusionAction.CropImageAction.ACTION);
                                toPreview.putExtra(CropImageAction.EXTRA_PATH,
                                        imgSelectedPath);
                                toPreview.putExtra(CropImageAction.EXTRA_MODE,
                                        CropImageAction.MODE_SEND_VIEW);
                                startActivityForResult(toPreview,
                                        FusionAction.ChatAction.REQUEST_CODE_SEND_IMAGE);
                            }
                            else
                            {
                                showToast(R.string.image_format_incorrect);
                            }
                        }
                        else
                        {
                            Logger.e(TAG, "no image is choosed.");
                        }
                    }
                    
                    break;
                
                //图片发送
                case FusionAction.ChatAction.REQUEST_CODE_SEND_IMAGE:
                    
                    // 1.获取图片的大图及缩略图路径
                    String smallPath = data.getStringExtra(FusionAction.CropImageAction.EXTRA_SMALL_IMAGE_PATH);
                    Logger.d(TAG, "small path = " + smallPath);
                    String bigPath = data.getStringExtra(FusionAction.CropImageAction.EXTRA_PATH);
                    Logger.d(TAG, "big path = " + bigPath);
                    // 2.构建媒体对象
                    MediaIndexModel imageModel = new MediaIndexModel();
                    imageModel.setMediaSmallPath(smallPath);
                    imageModel.setMediaPath(bigPath);
                    imageModel.setMediaType(MediaIndexModel.MEDIATYPE_IMG);
                    
                    // 3.发送消息
                    sendMediaMsg(null, imageModel);
                    break;
                //到图片预览页面
                case FusionAction.ChatAction.REQUEST_CODE_DOWNLOAD_IMAGE:
                    
                    String downloadImgPath = data.getStringExtra(FusionAction.DownloadAction.EXTRA_PATH);
                    Intent toPreview = new Intent(
                            FusionAction.CropImageAction.ACTION);
                    toPreview.putExtra(FusionAction.CropImageAction.EXTRA_PATH,
                            downloadImgPath);
                    toPreview.putExtra(CropImageAction.EXTRA_MODE,
                            CropImageAction.MODE_SAVE);
                    //设置文件保存路径
                    toPreview.putExtra(CropImageAction.SAVE_PATH,
                            mImLogic.getImageFilePath(FromType.RECEIVE));
                    startActivity(toPreview);
                    
                    break;
                
                //发送视频
                case FusionAction.ChatAction.REQUEST_CODE_RECORD_VIDEO:
                    /** _data：文件的绝对路径 ，_display_name：文件名 , _size： 文件大小*/
                    String strVideoPath = data.getStringExtra(RecordVideoAction.MEDIA_PATH);
                    //                    showToast(strVideoPath);
                    MediaIndexModel media = new MediaIndexModel();
                    media.setMediaPath(strVideoPath);
                    media.setMediaType(MediaIndexModel.MEDIATYPE_VIDEO);
                    media.setMediaSmallPath(data.getStringExtra(RecordVideoAction.IMAGE_THUMB_PATH));
                    media.setPlayTime((int) (data.getLongExtra(RecordVideoAction.VIDEO_DURATION,
                            0) / 1000));
                    sendMediaMsg(null, media);
                    break;
                
                //到视频播放页面
                case FusionAction.ChatAction.REQUEST_CODE_DOWNLOAD_VIDEO:
                    Intent toVideoPlay = new Intent(
                            FusionAction.RecordVideoAction.ACTION);
                    toVideoPlay.putExtra(RecordVideoAction.MEDIA_PATH,
                            data.getStringExtra(FusionAction.DownloadAction.EXTRA_PATH));
                    toVideoPlay.putExtra(RecordVideoAction.IMAGE_THUMB_PATH,
                            data.getStringExtra(RecordVideoAction.IMAGE_THUMB_PATH));
                    toVideoPlay.putExtra(RecordVideoAction.NEW_PATH,
                            mImLogic.getVideoFilePath(FromType.RECEIVE));
                    startActivity(toVideoPlay);
                    break;
                
                // 系统拍照页面返回后的处理，进入图片预览页面
                case FusionAction.ChatAction.REQUEST_CODE_CAMERA:
                    String path = null;
                    // 机型适配（不同手机返回的地址不一样）
                    if (null != data && null != data.getData())
                    {
                        String[] projection = { MediaColumns.DATA };
                        Cursor cursor = managedQuery(data.getData(),
                                projection,
                                null,
                                null,
                                null);
                        int index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                        cursor.moveToFirst();
                        path = cursor.getString(index);
                    }
                    if (path == null)
                    {
                        path = mTmpImgPath;
                    }
                    if (path != null)
                    {
                        // 2.跳转到图片预览页面
                        startActivityForResult(new Intent(
                                FusionAction.CropImageAction.ACTION).putExtra(CropImageAction.EXTRA_PATH,
                                path)
                                .putExtra(CropImageAction.EXTRA_MODE,
                                        CropImageAction.MODE_SEND_VIEW),
                                FusionAction.ChatAction.REQUEST_CODE_SEND_IMAGE);
                    }
                    mTmpImgPath = null;
                    break;
                default:
                    break;
            }
        }
        
    }
    
    /**
     * 保存Activity状态，Moto机型适配<BR>
     * @param outState Bundle
     * @see android.app.ActivityGroup#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString("saveData", mTmpImgPath);
        super.onSaveInstanceState(outState);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    
    @Override
    protected void initLogics()
    {
        mImLogic = (IImLogic) super.getLogicByInterfaceClass(IImLogic.class);
        mGroupLogic = (IGroupLogic) super.getLogicByInterfaceClass(IGroupLogic.class);
    }
    
    /**
     * 初始化页面数据
     * <BR>
     *
     */
    protected abstract void initData();
    
    /**
     * 获取未读音频消息id列表
     * <BR>
     * 
     * @return 未读音频消息id列表
     */
    protected abstract List<String> getUnreadAudioMsgIds();
    
    /**
     * 
     * 获取BaseMsgAdapter<BR>
     * @param curMsgCnt curMsgCnt
     * @return 获取BaseMsgAdapter
     */
    protected abstract BaseMsgAdapter buildMsgAdapter(int curMsgCnt);
    
    /**
     * 
     * 注册数据库监听<BR>
     *
     */
    protected abstract void registerObserver();
    
    /**
     * 
     * 注销数据库监听<BR>
     *
     */
    protected abstract void unregisterObserver();
    
    /**
     * 软键盘隐藏事件监听<BR>
     * @param msg   软键盘隐藏消息
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        super.handleStateMessage(msg);
        int what = msg.what;
        switch (what)
        {
            case FusionMessageType.ChatMessageType.SOUND_AMPLITUDE:
                int i = (Integer) msg.obj;
                if (i < 100)
                {
                    mAudioProgressBar.setBackgroundResource(R.drawable.voice_1);
                    mMicImageView.setBackgroundResource(R.drawable.mic_one);
                }
                else if (100 < i && i < 200)
                {
                    mAudioProgressBar.setBackgroundResource(R.drawable.voice_2);
                    mMicImageView.setBackgroundResource(R.drawable.mic_two);
                }
                else if (200 < i && i < 300)
                {
                    mAudioProgressBar.setBackgroundResource(R.drawable.voice_3);
                    mMicImageView.setBackgroundResource(R.drawable.mic_three);
                }
                else if (300 < i && i < 500)
                {
                    mAudioProgressBar.setBackgroundResource(R.drawable.voice_4);
                    mMicImageView.setBackgroundResource(R.drawable.mic_four);
                }
                break;
            case ChatMessageType.RECORD_TIME:
                int remainTime = (Integer) msg.obj;
                Logger.i(TAG, "remainTime=" + remainTime);
                if (remainTime <= 10)
                {
                    mTimeLayout.setVisibility(View.VISIBLE);
                    mTimeText.setText(getResources().getString(R.string.record_time,
                            remainTime));
                }
                else
                {
                    mTimeLayout.setVisibility(View.GONE);
                }
                if (remainTime == 0)
                {
                    if (mSoundRecorderWindow != null
                            && mSoundRecorderWindow.isShowing()
                            && mMediaRecorder != null)
                    {
                        endRecord();
                        mSoundRecorderWindow.dismiss();
                        MediaPlayer mp = MediaPlayer.create(this,
                                Uri.parse(mSoundFilePath));
                        int duration = mp.getDuration();
                        int playTime = duration / 1000;
                        MediaIndexModel audioMedia = new MediaIndexModel();
                        audioMedia.setMediaPath(mSoundFilePath);
                        audioMedia.setMediaType(MediaIndexModel.MEDIATYPE_AUDIO);
                        audioMedia.setPlayTime(playTime);
                        sendMediaMsg(null, audioMedia);
                    }
                    
                }
                break;
            
            case FusionMessageType.ChatMessageType.SHOW_BOTTOM_BAR:
                showBottomBar();
                break;
            case FusionMessageType.ChatMessageType.HIDE_BOTTOM_BAR:
                hideBottomBar();
                break;
            
            // 刷新页面
            case FusionMessageType.ChatMessageType.MSGTYPE_MSG_REFRESH:
                Logger.d(TAG, "refreshMsgList from logic");
                if (mCurMsgCount < PER_MSG_COUNT)
                {
                    mCurMsgCount = PER_MSG_COUNT;
                }
                refreshMsgList(true);
                break;
            case FusionMessageType.ChatMessageType.MSGTYPE_MEDIA_INDEX_REFRESH:
                getMsgAdapter().refreshMsg();
                break;
            // 上传进度刷新
            case FusionMessageType.UPloadType.UPLOADING:
                mMsgAdapter.setProgressModel((ProgressModel) msg.obj);
                mMsgAdapter.notifyDataSetChanged();
                break;
            // 上传结束
            case FusionMessageType.UPloadType.UPLOAD_FINISH:
                mMsgAdapter.setProgressModel((ProgressModel) msg.obj);
                mMsgAdapter.notifyDataSetChanged();
                break;
            case FusionMessageType.UPloadType.UPLOAD_FAILED:
                String msgId = (String) msg.obj;
                mMsgAdapter.removeProgressModel(msgId);
                mMsgAdapter.notifyDataSetChanged();
                break;
            
            // 聊天页面下载只针对位置信息
            case FusionMessageType.DownloadType.DOWNLOADING:
                ProgressModel progress = (ProgressModel) msg.obj;
                if (mLocationDownList != null
                        && mLocationDownList.contains(progress.getId()))
                {
                    mMsgAdapter.setProgressModel(progress);
                    mMsgAdapter.notifyDataSetChanged();
                }
                break;
            case FusionMessageType.DownloadType.DOWNLOAD_FAILED:
                String locMsgId = (String) msg.obj;
                if (mLocationDownList != null
                        && mLocationDownList.contains(locMsgId))
                {
                    showToast(R.string.can_not_determine_location_info);
                    mLocationDownList.remove(locMsgId);
                    mMsgAdapter.removeProgressModel(locMsgId);
                    mMsgAdapter.notifyDataSetChanged();
                }
                break;
            //下载完成后移除progressModel
            case FusionMessageType.DownloadType.DOWNLOAD_FINISH:
                String[] retStrs = (String[]) msg.obj;
                if (mLocationDownList != null
                        && mLocationDownList.contains(retStrs[0]))
                {
                    mLocationDownList.remove(retStrs[0]);
                    mMsgAdapter.removeProgressModel(retStrs[0]);
                    mMsgAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * 初始化页面组件，设置组件监听器等<BR>
     *
     */
    private void initView()
    {
        // 组件对象初始化
        mTitleCount = (TextView) findViewById(R.id.title_member_count);
        mMsgListView = (PullToRefreshListView) findViewById(R.id.msg_history);
        mMsgListView.setRefreshEnableByCount(PER_MSG_COUNT);
        mPickEmotionBtn = (Button) findViewById(R.id.pick_custom_face);
        mSendBtn = (Button) findViewById(R.id.send);
        mRecordBtn = findViewById(R.id.record_button);
        mBottomBar = findViewById(R.id.bottom_bar);
        mEditText = (RichEditText) findViewById(R.id.chatText);
        mTitle = (TextView) findViewById(R.id.title);
        mLeftButton = (Button) findViewById(R.id.left_button);
        mRightButton = (LinearLayout) findViewById(R.id.right_layout);
        mRightButton.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.VISIBLE);
        mVideoButton = findViewById(R.id.right_image);
        mImageButton = findViewById(R.id.left_image);
        // 设置监听器 
        mPickEmotionBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mRecordBtn.setOnTouchListener(this);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mMsgListView.setOnRefreshListener(this);
        mVideoButton.setOnClickListener(this);
        mImageButton.setOnClickListener(this);
        mEditText.setOnTouchListener(this);
    }
    
    /**
     * 初始化popupWindow上的控件
     */
    private void initPopupWindow()
    {
        mMicImageView = (ImageView) mSoundRecorderWindow.getContentView()
                .findViewById(R.id.img_mic);
        mAudioProgressBar = (ImageView) mSoundRecorderWindow.getContentView()
                .findViewById(R.id.volumn_bar);
        LinearLayout mView = (LinearLayout) mSoundRecorderWindow.getContentView()
                .findViewById(R.id.media_container);
        mRightView = (LinearLayout) mSoundRecorderWindow.getContentView()
                .findViewById(R.id.right);
        mCancelText = (TextView) mRightView.findViewById(R.id.cancel_text);
        mView.setOnTouchListener(this);
        mTimeText = (TextView) mSoundRecorderWindow.getContentView()
                .findViewById(R.id.time);
        mTimeLayout = (LinearLayout) mSoundRecorderWindow.getContentView()
                .findViewById(R.id.time_layout);
    }
    
    private void init()
    {
        initData();
        
        // 进入聊天页面，获取未读音频消息id列表；但不会自动播放语音
        mUnreadAudioMsgIds = getUnreadAudioMsgIds();
        // 如果为空也要初始化
        if (mUnreadAudioMsgIds == null)
        {
            mUnreadAudioMsgIds = new ArrayList<String>();
        }
        
        mHolderEventListener = this;
        
        // 注册数据库变化监听器
        registerObserver();
        
        mMyFace = mImLogic.getMyFace();
    }
    
    /**
     * 
     * 用户点击“转发”菜单的响应<BR>
     * 
     * @param msgID
     */
    private void transferMsg(String msgID)
    {
        // 进入选择好友页面
        Intent intent = new Intent(FusionAction.ChooseMemberAction.ACTION);
        //设定Action,标识要跳转的界面
        intent.putExtra(FusionAction.ChooseMemberAction.EXTRA_ENTRANCE_TYPE,
                FusionAction.ChooseMemberAction.TYPE.REQUEST_FOR_FRIEND_ID_LIST);
        intent.putExtra(EXTRA_MSG_ID, msgID);
        startActivityForResult(intent,
                FusionAction.ChatAction.REQUEST_CODE_TO_CHOOSE_MEMBER_FOR_TRANSFER);
    }
    
    //    /**
    //     * 
    //     * 音频录制<BR>
    //     *
    //     */
    //    private void recordAudio()
    //    {
    //        
    //    }
    //    
    //    /**
    //     * 视频录制
    //     * <BR>
    //     *
    //     */
    //    private void recordVideo()
    //    {
    //        
    //    }
    //    
    //    /**
    //     * 
    //     * 拍照<BR>
    //     *
    //     */
    //    private void capturePhoto()
    //    {
    //        
    //    }
    //    
    /**
     * 当键盘显示时操作
     */
    private void showBottomBar()
    {
        if (mBottomBar != null)
        {
            mBottomBar.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 当键盘隐藏时操作
     * doKeyboardHidenAction
     */
    private void hideBottomBar()
    {
        if (mBottomBar != null)
        {
            mBottomBar.setVisibility(View.GONE);
        }
    }
    
    /**
     * 显示贴图表情窗口<BR>
     */
    protected void showEmotionWin()
    {
        EmotionWindow.getInstance(this, mEditText)
                .showEmotionWindow(EmotionWindow.IM_CHAT, this);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    /**
     * 显示软键盘
     * 
     * @param editText
     *            编辑框
     */
    private void showSoftKeyBoard(RichEditText editText)
    {
        editText.requestFocus();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editText,
                0);
    }
    
    /**
     * 根据贴图名称，找出贴图的Bitmap，同时设置到ImageView
     * 
     * @param filePath 贴图名称
     * @return 贴图Bitmap对象
     */
    protected Bitmap loadEmoji(String filePath)
    {
        try
        {
            if (mEmojiMap == null)
            {
                mEmojiMap = PictureManager.getInstance(getApplicationContext())
                        .getAllEmojis();
            }
            Map<String, EmojiBean> emojiCache = mEmojiMap.get();
            if (!StringUtil.isNullOrEmpty(filePath))
            {
                Bitmap bitmap = emojiCache.get(filePath).getEmoji();
                if (bitmap != null)
                {
                    return bitmap;
                }
                else
                {
                    //TODO:如果贴图不存在，是否要下载，待定
                    return null;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 刷新消息列表
     * <BR>
     * @param toBottom 是否刷到最下面
     */
    private void refreshMsgList(boolean toBottom)
    {
        getMsgAdapter().refreshMsg();
        if (toBottom)
        {
            //设置ListView底部显示在Activity底部
            // 设置显示到底部，不需要-1
            mMsgListView.setSelection(mMsgListView.getCount());
        }
    }
    
    /**
     * 
     * 开始录制<BR>
     * 
     */
    private void beginRecord()
    {
        //构建音频录制对象
        mMediaRecorder = new MediaRecorder();
        // 设置录音的来源为麦克风  
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置录制的声音输出格式    录制的音频文件格式为amr
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        // 设置声音的编码格式   TODO:什么编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置录音的输出文件路径  
        mSoundFilePath = mImLogic.getAudioFilePath();
        mMediaRecorder.setOutputFile(mSoundFilePath);
        Logger.i(TAG, "media output file========" + mSoundFilePath);
        
        // 做预期准备  
        try
        {
            mMediaRecorder.prepare();
        }
        catch (IllegalStateException e)
        {
            Logger.e(TAG,
                    "begin record prepare failuer IllegalStateException",
                    e);
            showToast(R.string.record_error);
            mMediaRecorder.release();
            return;
        }
        catch (IOException e)
        {
            Logger.e(TAG, "begin record prepare failuer IOException", e);
            showToast(R.string.record_error);
            mMediaRecorder.release();
            return;
        }
        // 开始录音  
        if (mSoundFilePath != null)
        {
            Logger.i(TAG, "begin Record");
            
            // 开始录制 
            mMediaRecorder.start();
            
            mProgressBarEable = true;
            
            // 启动线程刷新音量变化
            new Thread(mAudioPbRunnable).start();
            
            // 启动计时，录音时间不超过60s
            if (mTimeThread != null)
            {
                mTimeThread.cancel();
                mTimeThread = null;
            }
            mTimeThread = new TimeThread(getHandler(),
                    ChatMessageType.RECORD_TIME, 60);
            mTimeThread.start();
        }
    }
    
    /**
     * 
     * 结束录制<BR>
     * 
     */
    private void endRecord()
    {
        if (mMediaRecorder != null)
        {
            mMediaRecorder.stop();
            // 释放资源  
            mMediaRecorder.release();
            mMediaRecorder = null;
            mProgressBarEable = false;
        }
        if (mTimeThread != null)
        {
            mTimeThread.cancel();
            mTimeThread = null;
        }
    }
    
    /**
     * <BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-13] 
     */
    protected abstract class BaseMsgAdapter extends CursorAdapter
    {
        /**
         * TAG
         */
        private static final String TAG = "BaseMsgAdapter";
        
        /**
         * 多媒体消息上传进度信息
         * key为消息id
         */
        private Map<String, ProgressModel> mProgressMap = new HashMap<String, ProgressModel>();
        
        /**
         * 构造方法
         * @param context Context
         * @param c Cursor
         * @param autoRequery boolean
         */
        public BaseMsgAdapter(Context context, BaseMsgCursorWrapper c,
                boolean autoRequery)
        {
            super(context, c, autoRequery);
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
         */
        
        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            Logger.d(TAG, "pause flag is : " + isPaused());
            //onPause时停止刷新发送状态
            if (isPaused())
            {
                return;
            }
            
            //通过view获得msgItem
            BaseMsgItem msgItem = (BaseMsgItem) view.getTag();
            Logger.d(TAG, "(bind view)msgItem : " + msgItem);
            if (!mBtnAvailable)
            {
                msgItem.setResendUnable();
            }
            BaseMessageModel msg = ((BaseMsgCursorWrapper) cursor).parseMsgModel();
            Logger.d(TAG, "msg status = " + msg.getMsgStatus() + "msg.type:"
                    + msg.getMsgType() + " msg.content:" + msg.getMsgContent());
            // 刷新页面
            if (msgItem.getTypeSendOrReceive() == BaseMsgItem.SendOrReceive.SEND)
            {
                boolean isPlaying = msg.getMsgType() == BaseMessageModel.MSGTYPE_MEDIA
                        && msg.getMediaIndex().getMediaPath() != null
                        && msg.getMsgId().equals(mPlayingAudioMsgId);
                // 如果是发送的消息，设置头像为自己的头像
                msgItem.bindView(null,
                        mMyFace,
                        msg,
                        context,
                        cursor,
                        mProgressMap.get(msg.getMsgId()),
                        isPlaying);
            }
            else
            {
                boolean shouldPlay = msg.getMsgType() == BaseMessageModel.MSGTYPE_MEDIA
                        && msg.getMediaIndex().getMediaPath() != null
                        && msg.getMsgId().equals(mPlayingAudioMsgId);
                Logger.d(TAG, "mPlayingAudioMsgId : " + mPlayingAudioMsgId
                        + "; msgId : " + msg.getMsgId());
                msgItem.bindView(getDisplayName(msg),
                        getFace(msg),
                        msg,
                        context,
                        cursor,
                        mProgressMap.get(msg.getMsgId()),
                        shouldPlay);
                
                if (shouldPlay && !mIsPlaying)
                {
                    Logger.d(TAG, "start play audio");
                    startPlayAudio(msg);
                    setMsgAsReaded(msg);
                }
            }
        }
        
        /**
         * 
         * 设置多媒体消息进度<BR>
         * 
         * @param progress ProgressModel
         */
        void setProgressModel(ProgressModel progress)
        {
            // 如果完成，则移除
            if (progress.getPercent() == 100)
            {
                mProgressMap.remove(progress.getId());
            }
            else
            {
                mProgressMap.put(progress.getId(), progress);
            }
        }
        
        /**
         * 移除某条消息的进度条
         * <BR>
         * 
         * @param msgId 消息id
         */
        void removeProgressModel(String msgId)
        {
            mProgressMap.remove(msgId);
        }
        
        /**
         * 
         * 获取头像<BR>
         * 
         * @param msg BaseMessageModel
         * @return Drawable
         */
        protected abstract Drawable getFace(BaseMessageModel msg);
        
        /**
         * 
         * 获取显示名<BR>
         * 
         * @param msg BaseMessageModel
         * @return String
         */
        protected abstract String getDisplayName(BaseMessageModel msg);
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
         */
        
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
        {
            int type = getItemViewType(cursor);
            Logger.d(TAG, "new View , get type : " + type);
            BaseMsgItem msgItem = ItemCreator.creator(type,
                    mHolderEventListener);
            View v = msgItem.getView(context, cursor);
            v.setTag(msgItem);
            Logger.d(TAG, "view:" + v + ", msgItem:" + msgItem);
            return v;
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see android.widget.BaseAdapter#getItemViewType(int)
         */
        
        @Override
        public int getItemViewType(int position)
        {
            if (!getCursor().moveToPosition(position))
            {
                throw new IllegalStateException(
                        "couldn't move cursor to position " + position);
            }
            return getItemViewType(getCursor());
        }
        
        /**
         * <BR>
         * {@inheritDoc}
         * @see android.widget.BaseAdapter#getViewTypeCount()
         */
        
        @Override
        public int getViewTypeCount()
        {
            return ItemCreator.getItemTypeCount();
        }
        
        /**
         * 
         * 刷新消息页面（是刷新的唯一入口）<BR>
         *
         */
        public void refreshMsg()
        {
            // 重新获取Cursor
            BaseMsgCursorWrapper cursor = (BaseMsgCursorWrapper) getCursor();
            cursor.requery();
            
            // 设置显示条目
            mCurMsgCount = cursor.setCount(mCurMsgCount);
            
            // 如果所有消息都已经展示出来了，则不可刷新
            mMsgListView.setRefreshEnable(!cursor.sameAsSuper());
            
            // 获取未读音频消息
            List<String> tmpUnreadAudioMsgIds = getUnreadAudioMsgIds();
            if (tmpUnreadAudioMsgIds != null)
            {
                for (String msgId : tmpUnreadAudioMsgIds)
                {
                    if (!mUnreadAudioMsgIds.contains(msgId))
                    {
                        // 如果页面不可见（锁屏状态），不自动播放语音
                        if (mPlayingAudioMsgId == null && !isPaused())
                        {
                            mPlayingAudioMsgId = msgId;
                        }
                        mUnreadAudioMsgIds.add(msgId);
                    }
                }
            }
            Logger.d(TAG,
                    "refresh msg, get unread audio msg id that should be played : "
                            + mPlayingAudioMsgId);
        }
        
        /**
         * 刷新用户信息
         */
        public void refreshUserInfo()
        {
            //TODO
        }
        
        /**
         * 
         * 获取当前item的消息类型：发送或者接收<BR>
         * 
         * @param cursor Cursor
         * @return 是发送类型还是接收类型
         */
        protected abstract int getTypeSendOrReceive(Cursor cursor);
        
        /**
         * 获取当前Item的消息类型：文本、多媒体
         * <BR>
         * 
         * @param cursor Cursor
         * @return 消息类型
         */
        protected abstract int getMsgType(Cursor cursor);
        
        /**
         * 
         * 如果是媒体类型消息，返回具体媒体类型<BR>
         * 
         * @param cursor Cursor
         * @return 消息类型
         */
        protected int getSubMsgType(Cursor cursor)
        {
            return cursor.getInt(cursor.getColumnIndex(MediaIndexColumns.MEDIA_TYPE));
        }
        
        private int getItemViewType(Cursor cursor)
        {
            return ItemCreator.buildType(Switcher.switchTypeSendOrReceive(getTypeSendOrReceive(cursor)),
                    Switcher.switchMsgType(getMsgType(cursor),
                            getSubMsgType(cursor)));
        }
        
    }
    
    /**
     * 注册上下文菜单<BR>
     * @param view view
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#registerContextMenu(android.view.View)
     */
    @Override
    public void registerContextMenu(View view)
    {
        registerForContextMenu(view);
    }
    
    /**
     * 用户头像点击事件<BR>
     * @param msg BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#onUserPhotoClick(java.lang.String)
     */
    @Override
    public abstract void onUserPhotoClick(BaseMessageModel msg);
    
    /**
     * 文本消息点击事件<BR>
     * @param msgModel BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#onTextViewClick(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    
    @Override
    public void onTextClick(BaseMessageModel msgModel)
    {
        //TODO 子类实现方法
    }
    
    /**
     * 图片消息点击事件<BR>
     * @param msgModel BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#onImageClick(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    @Override
    public void onImageClick(BaseMessageModel msgModel)
    {
        
        // 1.获取消息的ID,大图路径
        String msgId = msgModel.getMsgId();
        String localImgPath = msgModel.getMediaIndex().getMediaPath();
        
        // 2.进入预览页面
        Logger.d(TAG, "msg ID is : " + msgId);
        Logger.d(TAG, "media path is : " + localImgPath);
        
        // 如果本地有大图，即大图路径不为空进入
        if (null != localImgPath)
        {
            //文件来源类型
            FromType fromType = FromType.RECEIVE;
            if (BaseMessageModel.MSGSENDORRECV_SEND == msgModel.getMsgSendOrRecv())
            {
                fromType = FromType.SEND;
            }
            
            Intent toPreview = new Intent(FusionAction.CropImageAction.ACTION);
            toPreview.putExtra(FusionAction.CropImageAction.EXTRA_PATH,
                    localImgPath);
            toPreview.putExtra(CropImageAction.EXTRA_MODE,
                    CropImageAction.MODE_SAVE);
            //设置文件保存路径
            toPreview.putExtra(CropImageAction.SAVE_PATH,
                    mImLogic.getImageFilePath(fromType));
            Logger.d(TAG, "message status = " + msgModel.getMsgStatus());
            startActivity(toPreview);
        }
        // 如果没大图，进入图片下载并展示
        else
        {
            Intent toDownload = new Intent(FusionAction.DownloadAction.ACTION);
            toDownload.putExtra(FusionAction.DownloadAction.EXTRA_MSG_ID, msgId);
            toDownload.putExtra(FusionAction.DownloadAction.EXTRA_MEDIA_TYPE,
                    FusionAction.DownloadAction.DownloadMediaType.IMG);
            toDownload.putExtra(FusionAction.DownloadAction.EXTRA_TITLE_NAME,
                    mMsgAdapter.getDisplayName(msgModel));
            startActivityForResult(toDownload,
                    FusionAction.ChatAction.REQUEST_CODE_DOWNLOAD_IMAGE);
        }
        
    }
    
    /**
     * 位置信息点击处理<BR>
     * @param msgModel BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#onLocationImageClick(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    
    @Override
    public void onLocationImageClick(BaseMessageModel msgModel)
    {
        Logger.i(TAG, "start go to google map");
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(String.format(LOCATION_IN_GOOGLE_MAPS_URL,
                    msgModel.getMediaIndex().getLocationLat(),
                    msgModel.getMediaIndex().getLocationLon())));
            startActivity(intent);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "go to google failure", e);
        }
        
    }
    
    /**
     * 视频点击事件<BR>
     * @param msg BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#onVideoClick(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    @Override
    public void onVideoClick(BaseMessageModel msg)
    {
        String filePath = msg.getMediaIndex().getMediaPath();
        if (null != filePath)
        {
            Intent intent = new Intent(FusionAction.RecordVideoAction.ACTION);
            intent.putExtra(RecordVideoAction.MEDIA_PATH, filePath);
            intent.putExtra(RecordVideoAction.IMAGE_THUMB_PATH,
                    msg.getMediaIndex().getMediaSmallPath());
            FromType fromType = FromType.RECEIVE;
            if (BaseMessageModel.MSGSENDORRECV_SEND == msg.getMsgSendOrRecv())
            {
                fromType = FromType.SEND;
            }
            
            intent.putExtra(RecordVideoAction.NEW_PATH,
                    mImLogic.getVideoFilePath(fromType));
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(FusionAction.DownloadAction.ACTION);
            intent.putExtra(FusionAction.DownloadAction.EXTRA_MSG_ID,
                    msg.getMsgId());
            intent.putExtra(FusionAction.DownloadAction.EXTRA_MEDIA_TYPE,
                    FusionAction.DownloadAction.DownloadMediaType.VIDEO);
            intent.putExtra(RecordVideoAction.IMAGE_THUMB_PATH,
                    msg.getMediaIndex().getMediaSmallPath());
            intent.putExtra(FusionAction.DownloadAction.EXTRA_TITLE_NAME,
                    mMsgAdapter.getDisplayName(msg));
            startActivityForResult(intent,
                    FusionAction.ChatAction.REQUEST_CODE_DOWNLOAD_VIDEO);
        }
    }
    
    /**
     * 
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#getBitmap(java.lang.String)
     */
    @Override
    public Bitmap getBitmap(String path)
    {
        if (null != path)
        {
            if (mBitmapCache == null)
            {
                mBitmapCache = new HashMap<String, Bitmap>();
            }
            
            Bitmap bitmap = mBitmapCache.get(path);
            
            if (bitmap == null)
            {
                bitmap = BitmapFactory.decodeFile(path);
                mBitmapCache.put(path, bitmap);
            }
            return bitmap;
        }
        return null;
    }
    
    /**
     * 获取贴图bitmap<BR>
     * @param path 贴图路径
     * @return Bitmap
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#getEmojBitmap(java.lang.String)
     */
    @Override
    public Bitmap getEmojBitmap(String path)
    {
        return loadEmoji(path);
    }
    
    /**
     * [一句话功能简述]<BR> 
     * @param keyCode keyCode
     * @param event KeyEvent
     * @return super.onKeyDown(keyCode, event)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                
                if (EmotionWindow.getCurrInstance(this).getWindowStatus() == View.VISIBLE)
                {
                    EmotionWindow.getCurrInstance(this).closePopWindow();
                    EmotionWindow.getCurrInstance(this)
                            .setWindowStatus(View.GONE);
                    showBottomBar();
                    return true;
                }
                
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * 开始播放音频<BR>
     * @param msg BaseMessageModel
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#startPlayAudio(com.huawei.basic.android.im.logic.model.BaseMessageModel)
     */
    @Override
    public void startPlayAudio(final BaseMessageModel msg)
    {
        String msgId = msg.getMsgId();
        
        // 如果当前正在播放语音，则先停止播放
        // 如果当前正在播放的消息与即将要播放的消息为同一条消息，则不需要调用stopPlayAudio()方法
        if (!msgId.equals(mPlayingAudioMsgId))
        {
            stopPlayAudio();
        }
        
        // 赋值当前正在播放的语音消息id
        mPlayingAudioMsgId = msgId;
        Logger.d(TAG, "mPlayingAudioMsgId : " + mPlayingAudioMsgId);
        mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(msg.getMediaIndex().getMediaPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mIsPlaying = true;
            // 更新视图
            mMsgAdapter.notifyDataSetChanged();
        }
        catch (IllegalArgumentException e)
        {
            Logger.e(TAG, "mediaplayer  IllegalArgumentException", e);
            mIsPlaying = false;
        }
        catch (IllegalStateException e)
        {
            Logger.e(TAG, "mediaplayer  IllegalStateException", e);
            mIsPlaying = false;
        }
        catch (IOException e)
        {
            Logger.e(TAG, "mediaplayer  IOException", e);
            mIsPlaying = false;
        }
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mIsPlaying = false;
                
                // 更新页面
                mMsgAdapter.notifyDataSetChanged();
                // 将该消息从未读列表中删除，并获取下一条要播放的音频消息id
                if (mPlayingAudioMsgId != null)
                {
                    int position = mUnreadAudioMsgIds.indexOf(mPlayingAudioMsgId);
                    if (position != -1)
                    {
                        mUnreadAudioMsgIds.remove(position);
                        // 获取下一条要播放的语音消息id
                        if (position < mUnreadAudioMsgIds.size())
                        {
                            mPlayingAudioMsgId = mUnreadAudioMsgIds.get(position);
                        }
                        else
                        {
                            mPlayingAudioMsgId = null;
                        }
                    }
                    else
                    {
                        mPlayingAudioMsgId = null;
                    }
                }
                Logger.d(TAG, "onComplete mPlayingAudioMsgId : "
                        + mPlayingAudioMsgId);
            }
        });
    }
    
    /**
     * 停止播放的时候，刷新视图
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#stopPlayAudio()
     */
    @Override
    public void stopPlayAudio()
    {
        // 如果正在播放，则停止播放
        if (mMediaPlayer != null)
        {
            mMediaPlayer.release();
            mIsPlaying = false;
            
            // 如果在未读消息列表中，则移除该消息
            if (mPlayingAudioMsgId != null)
            {
                if (mUnreadAudioMsgIds.contains(mPlayingAudioMsgId))
                {
                    mUnreadAudioMsgIds.remove(mPlayingAudioMsgId);
                }
                mPlayingAudioMsgId = null;
            }
            // 更新视图
            mMsgAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * 获取视频缩略图路径<BR>
     * @param cr ContentResolver
     * @param fileName fileName
     * @return 缩略图路径
     */
    public static String getVideoThumbnail(ContentResolver cr, String fileName)
    {
        //select condition.
        String whereClause = MediaStore.Video.Media.DATA + " = '" + fileName
                + "'";
        //colection of results.
        Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID },
                whereClause,
                null,
                null);
        if (cursor == null || cursor.getCount() == 0)
        {
            return null;
        }
        cursor.moveToFirst();
        //image id in image table.
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        if (videoId == null)
        {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);
        
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        whereClause = MediaStore.Video.Thumbnails.VIDEO_ID + " = '"
                + videoIdLong + "'";
        cursor = cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Thumbnails.DATA },
                whereClause,
                null,
                null);
        cursor.moveToFirst();
        Logger.d(TAG, "move to First : " + cursor.moveToFirst());
        Logger.d(TAG, "cursor.getCount() : " + cursor.getCount());
        String thumbnailsPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
        Logger.d(TAG, "thumbnailsPath : " + thumbnailsPath);
        return thumbnailsPath;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param mediaType 媒体类型
     * @param mediaPath 媒体路径
     * @param size 媒体大小
     * @param duration （视频和音频）播放时长
     * @param alt String
     * @see com.huawei.basic.android.im.ui.im.BaseChatActivity#onPinupEmotionClick(int, java.lang.String, java.lang.String, int, java.lang.String)
     */
    @Override
    public void onPinupEmotionClick(int mediaType, String mediaPath,
            String size, int duration, String alt)
    {
        MediaIndexModel imageModel = new MediaIndexModel();
        imageModel.setMediaPath(mediaPath);
        imageModel.setMediaSmallPath(mediaPath);
        imageModel.setMediaAlt(alt);
        imageModel.setMediaType(MediaIndexModel.MEDIATYPE_EMOJI);
        sendMediaMsg(null, imageModel);
    }
    
    /**
     * 获取位置的结果
     * @param result 位置信息
     * @see com.huawei.basic.android.im.component.location.LocationDataListener#onLocationResult(com.huawei.basic.android.im.component.location.LocationInfo)
     */
    @Override
    public void onLocationResult(LocationInfo result)
    {
        cancelLocationWaitingDialog();
        if (result == null)
        {
            return;
        }
        Location location = result.getLocation();
        if (location == null)
        {
            Logger.i(TAG, "未获取到位置信息");
            showToast(R.string.can_not_determine_location_info);
        }
        else
        {
            //发送一对一消息
            onGetLocationDone(location,
                    StringUtil.getString(result.getAddressInfo()));
        }
    }
    
    /**
     * 正在获取位置的过程中
     * @param show 是否显示获取位置过程的进度
     * @see com.huawei.basic.android.im.component.location.LocationDataListener#onLocationProgress(boolean)
     */
    @Override
    public void onLocationProgress(boolean show)
    {
    }
    
    /**
     * 
     * 提示是否打开GPS<BR>
     */
    public void showOpenGpsDialog()
    {
        if (!BaseChatActivity.this.getSharedPreferences(FusionCode.Common.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE)
                .getBoolean(FusionCode.Common.SHARE_GPS_LOCATION, false))
        {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.location_gps_needed,
                    null);
            final CheckBox showTips = (CheckBox) dialogView.findViewById(R.id.remember_my_choice);
            showTips.setChecked(false);
            //显示dialog
            showViewDialog(R.string.prompt,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Logger.i(TAG,
                                    "showTips.isChecked()"
                                            + showTips.isChecked());
                            //无论是否勾选“下次不再提示”都要打开GPS
                            try
                            {
                                mImLogic.toggleGPS();
                            }
                            catch (Exception e)
                            {
                                showToast(R.string.can_not_determine_location_info);
                                cancelLocationWaitingDialog();
                                Logger.e(TAG, "GPS Exception", e);
                            }
                            if (showTips.isChecked())
                            {
                                //保存数据
                                SharedPreferences.Editor editor = BaseChatActivity.this.getSharedPreferences(FusionCode.Common.SHARED_PREFERENCE_NAME,
                                        Context.MODE_PRIVATE)
                                        .edit();
                                editor.putBoolean(FusionCode.Common.SHARE_GPS_LOCATION,
                                        true);
                                editor.commit();
                                
                            }
                            //获取地理位置
                            RCSLocationManager rcsLocationManager = new RCSLocationManager(
                                    BaseChatActivity.this);
                            rcsLocationManager.getLocationInfo(BaseChatActivity.this,
                                    true);
                            //显示进度
                            showLocationWaitingDialog();
                        }
                    },
                    dialogView);
        }
        else
        {
            //否则直接打开GPS
            try
            {
                mImLogic.toggleGPS();
            }
            catch (Exception e)
            {
                showToast(R.string.can_not_determine_location_info);
                cancelLocationWaitingDialog();
                Logger.e(TAG, "GPS Exception", e);
            }
        }
    }
    
    /**
     * 
     * 显示正在获取地理位置的dialog<BR>
     */
    private void showLocationWaitingDialog()
    {
        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage(getString(R.string.get_location_information));
        mProgressDlg.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
            }
        });
        mProgressDlg.show();
    }
    
    /**
     * 
     * 取消获取地理位置的进度显示<BR>
     */
    public void cancelLocationWaitingDialog()
    {
        if (mProgressDlg != null)
        {
            if (mProgressDlg.isShowing())
            {
                mProgressDlg.dismiss();
            }
            mProgressDlg = null;
        }
    }
    
    /**
    * 下载地图信息<BR>
    * @param msgModel BaseMessageModel
    * @see com.huawei.basic.android.im.ui.im.item.HolderEventListener#downLocationImage(java.lang.String)
    */
    
    @Override
    public void downLocationImage(BaseMessageModel msgModel)
    {
        if (mLocationDownList == null)
        {
            mLocationDownList = new ArrayList<String>();
        }
        if (mLocationDownList.contains(msgModel.getMsgId()))
        {
            return;
        }
        else
        {
            FromType fromType = FromType.RECEIVE;
            //如果消息为发送消息
            if (BaseMessageModel.MSGSENDORRECV_SEND == msgModel.getMsgSendOrRecv())
            {
                fromType = FromType.SEND;
            }
            getImLogic().downloadMedia(msgModel.getMsgId(), fromType);
            mLocationDownList.add(msgModel.getMsgId());
        }
    }
}
