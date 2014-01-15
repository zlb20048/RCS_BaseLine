package com.huawei.basic.android.im.ui.im;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction.RecordVideoAction;
import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.log.util.MemoryStatus;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.ui.basic.BasicDialog;
import com.huawei.basic.android.im.utils.DateUtil;
import com.huawei.basic.android.im.utils.FileUtil;
import com.huawei.basic.android.im.utils.UriUtil;
import com.huawei.basic.android.im.utils.UriUtil.FromType;
import com.huawei.basic.android.im.utils.UriUtil.LocalDirType;
import com.huawei.fast.VideoProtocalJni;

/**
 * 视频页面，主要用于视频的下载播放录制和预览
 * @author qinyangwang
 * @version [RCS Client V100R001C03, 2012-4-23]
 */
public class RecordVideoActivity extends BasicActivity implements
        SurfaceHolder.Callback, OnClickListener, Camera.PreviewCallback
{
    // 私有静态变量
    private static final String TAG = "VideoRecordActivity";
    
    private static final String TEMP_PATH = "temp_file_path";
    
    private static final String CURRENT_STATUS = "current_status";
    
    private static final String FILE_SAVE_ENABLED = "file_save_enabled";
    
    // 录制时间最大初始值
    private static final int RECORD_TIME_MAX_LIMIT = 60;
    
    /**
     * 录制剩余时间倒计时的七点
     */
    private static final int REMAIN_WARN = 10;
    
    /**
     * 音频采样率{ 44100, 22050, 11025, 8000 }
     */
    private static final int AUDIO_SAMPLE_RATE = 8000;
    
    // 视频压缩时设定其压缩质量
    private static final int VIDEO_MP4_QP = 30;
    
    private static final int RECORD_STATUS_CAMERA_ERR = 1;
    
    private static final int RECORD_STATUS_IDEL = 2;
    
    /**
     * 正在录制
     */
    private static final int RECORD_STATUS_RECORDING = 3;
    
    /**
     * 正在压缩
     */
    private static final int RECORD_STATUS_COMPRESSING = 4;
    
    private static final int RECORD_STATUS_COMPRESSED = 5;
    
    /**
     * 正在播放
     */
    private static final int RECORD_STATUS_PLAYING = 6;
    
    private static final int RECORD_STATUS_PLAY_PAUSE = 7;
    
    private static final int RECORD_STATUS_PLAY_FINISHED = 8;
    
    private static final int PLAY_STATUS_IDEL = 9;
    
    //    
    //    private static final int PLAY_STATUS_DOWNLOADING = 11;
    //    
    //    private static final int PLAY_STATUS_DOWNLOADED = 12;
    //    
    //    private static final int PLAY_STATUS_PLAYING = 13;
    //    
    //    private static final int PLAY_STATUS_PLAY_PAUSE = 14;
    //    
    //    private static final int PLAY_STATUS_PLAY_FINISHED = 15;
    
    private static final int VIDEO_PLAY_TIMER = 2012;
    
    private static final int VIDEO_PLAY_COMPLETED = 2013;
    
    private static final int VIDEO_COMPRESS_COMPLETED = 2014;
    
    private static final int VIDEO_RECORD_NO_DISKSPACE = 2015;
    
    private static final int GET_PREVIEW_PIC_FRAME_NUM = 2;
    
    private boolean recordView;
    
    private boolean mLandScreen;
    
    /**
     * 进度显示框
     */
    private BasicDialog mCompressDialog;
    
    private View mCompressDialogView;
    
    // 界面控件
    //    private Button backBtn;
    
    private Button sendVideoBtn;
    
    private Button saveVideoBtn;
    
    private View leftButton;
    
    private View rightButton;
    
    private ImageView recordControlBtn;
    
    private TextView title;
    
    private TextView recordStatusText;
    
    private TextView videoTimeText;
    
    private View centerView;
    
    private SurfaceView videoRecordSurface;
    
    private SurfaceView videoPlaySurface;
    
    private ImageView videoPreviewImg;
    
    private View videoPlayStartImgBtn;
    
    private TextView tipsText;
    
    private TextView tipsTime;
    
    private TextView tipsSize;
    
    private ImageView recordLightImg;
    
    private WAVAudioRecorder wavAudioRecorder;
    
    private CameraManager cameraManager;
    
    private VideoProtocalJni videoProtocal;
    
    private SurfaceHolder recordSurfaceHolder;
    
    private SurfaceHolder playSurfaceHolder;
    
    private MediaPlayer mMediaPlayer;
    
    private File audioFile;
    
    private File videoFile;
    
    private File destVideoFile;
    
    private String mNewVideoFilePath;
    
    private FileOutputStream fileStream;
    
    private boolean isFileInit;
    
    // 静态变量
    private Bitmap videoPreviewBitmap;
    
    private File videoPreViewPic;
    
    private String tempFileName;
    
    private int currentStatus;
    
    private int frameCount;
    
    private long startTime;
    
    private long timeCount;
    
    private int mCurSec;
    
    private long fileSize;
    
    private PowerManager mPowerManager;
    
    private PowerManager.WakeLock mWakeLocker;
    
    /**
     * 创建一个handler 接收消息改变页面信息
     * */
    private Handler progressHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            View compressDialogView = mCompressDialogView;
            int what = msg.what;
            switch (what)
            {
                case VideoProtocalJni.REFRUSH_PROGRESS:
                    int progress = (Integer) msg.obj;
                    
                    // 确定是否小于0,小于0时,压缩正在准备中,也显示0%
                    if (progress < 0)
                    {
                        progress = 0;
                    }
                    
                    if (progress > 100)
                    {
                        progress = 100;
                    }
                    if (null != compressDialogView)
                    {
                        ((TextView) compressDialogView.findViewById(R.id.compress_message)).setText(getString(R.string.compress_progress,
                                progress));
                    }
                    break;
                case VideoProtocalJni.COMPRESS_SUCCESS:
                    // 收到压缩完成消息后, 先执行一次停止压缩,释放内存
                    VideoProtocalJni.stopCompress();
                    if (null != compressDialogView)
                    {
                        fileSize = destVideoFile.length();
                        ((TextView) mCompressDialogView.findViewById(R.id.compress_message)).setText(getString(R.string.compress_to,
                                fileSize / 1024.0));
                        mCompressDialogView.findViewById(R.id.compress_progress)
                                .setVisibility(View.GONE);
                        mCompressDialogView.findViewById(R.id.compress_image)
                                .setVisibility(View.VISIBLE);
                        sendVideoBtn.setEnabled(true);
                        refreshView(RECORD_STATUS_COMPRESSED);
                        sendEmptyMessageDelayed(VIDEO_COMPRESS_COMPLETED, 2000);
                    }
                    break;
                case VideoProtocalJni.COMPRESS_ERROR:
                    showToast(R.string.video_compress_failed);
                    refreshView(RECORD_STATUS_IDEL);
                    break;
                case VIDEO_RECORD_NO_DISKSPACE:
                    showToast(R.string.video_record_no_diskspace_stop);
                    controlRecord();
                    break;
                case VIDEO_PLAY_TIMER:
                    setPlayTime(true);
                    break;
                case VIDEO_PLAY_COMPLETED:
                    removeMessages(VIDEO_PLAY_TIMER);
                    setPlayTime((int) timeCount);
                    refreshView(RECORD_STATUS_PLAY_FINISHED);
                    break;
                case VIDEO_COMPRESS_COMPLETED:
                    BasicDialog compressDialog = mCompressDialog;
                    if (null != compressDialog)
                    {
                        mCompressDialog.dismiss();
                        mCompressDialog = null;
                        mCompressDialogView = null;
                    }
                    break;
                default:
                    break;
            }
        }
    };
    
    /**
     * 
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate");
        
        Intent intent = getIntent();
        recordView = intent.getBooleanExtra(RecordVideoAction.RECORD, false);
        mLandScreen = recordView && Integer.parseInt(Build.VERSION.SDK) < 8;
        mNewVideoFilePath = intent.getStringExtra(RecordVideoAction.NEW_PATH);
        if (mLandScreen)
        {
            // 如果SDK版本低于8, 说明系统可能是2.0或2.1的系统,需要显示横屏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 强制为横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置成全屏模式
            setContentView(R.layout.video_record_land);
        }
        else
        {
            setContentView(R.layout.video_record);
        }
        
        // 将预览图片的File置为空，这是静态变量
        videoPreViewPic = null;
        
        // 先获取到当前屏幕的密度值,再初始化界面控件
        //        dip2pxScale = getResources().getDisplayMetrics().density;
        findView();
        initView(intent);
        
        if (null != savedInstanceState)
        {
            boolean saveEnabled = savedInstanceState.getBoolean(FILE_SAVE_ENABLED,
                    true);
            if (recordView)
            {
                tempFileName = savedInstanceState.getString(TEMP_PATH);
                if (null != tempFileName)
                {
                    isFileInit = false;
                    initFilePath();
                    fileSize = destVideoFile.length();
                }
                saveVideoBtn.setEnabled(saveEnabled);
                currentStatus = Math.min(RECORD_STATUS_COMPRESSED,
                        savedInstanceState.getInt(CURRENT_STATUS, currentStatus));
            }
            else
            {
                rightButton.setEnabled(saveEnabled);
            }
        }
        refreshView(currentStatus);
    }
    
    /**
     * 页面销毁<BR>
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (null != mWakeLocker)
        {
            mWakeLocker.release();
        }
    }
    
    /**
     * 
     * 取消锁屏事件<BR>
     */
    private void unLockScreen()
    {
        /**
         * 开启取消锁屏事件标识  默认为false(录制，压缩，播放,true)
         */
        //当前状态时正在录制，正在压缩，正在播放时，屏蔽自动锁屏
        if (currentStatus == RECORD_STATUS_RECORDING
                || currentStatus == RECORD_STATUS_COMPRESSING
                || currentStatus == RECORD_STATUS_PLAYING)
        {
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLocker = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                    TAG);
            mWakeLocker.acquire();
        }
    }
    
    private void findView()
    {
        leftButton = findViewById(R.id.left_button);
        leftButton.setOnClickListener(this);
        tipsText = (TextView) findViewById(R.id.tips_display_text);
        tipsTime = (TextView) findViewById(R.id.tips_time_text);
        tipsSize = (TextView) findViewById(R.id.tips_size_text);
        recordControlBtn = (ImageView) findViewById(R.id.record_control_btn);
        recordControlBtn.setOnClickListener(this);
        sendVideoBtn = (Button) findViewById(R.id.send_video_btn);
        sendVideoBtn.setOnClickListener(this);
        saveVideoBtn = (Button) findViewById(R.id.save_video_file_btn);
        saveVideoBtn.setOnClickListener(this);
        recordLightImg = (ImageView) findViewById(R.id.record_light_img);
        recordStatusText = (TextView) findViewById(R.id.record_status_text);
        videoTimeText = (TextView) findViewById(R.id.record_time_text);
        videoRecordSurface = (SurfaceView) findViewById(R.id.surfaceCamera);
        title = (TextView) findViewById(R.id.title);
        videoPlayStartImgBtn = findViewById(R.id.video_preview_btn_img);
        videoPlayStartImgBtn.setOnClickListener(this);
        videoPreviewImg = (ImageView) findViewById(R.id.vedioPreviewImage);
        videoPlaySurface = (SurfaceView) findViewById(R.id.surfaceMediaPlayer);
        videoPlaySurface.setOnClickListener(this);
        if (recordView)
        {
            rightButton = findViewById(R.id.right_layout);
        }
        else
        {
            rightButton = findViewById(R.id.title_right_button);
        }
        rightButton.setOnClickListener(this);
        centerView = findViewById(R.id.center);
    }
    
    /**
     * 初始化界面控件
     */
    private void initView(Intent intent)
    {
        mMediaPlayer = new MediaPlayer();
        playSurfaceHolder = videoPlaySurface.getHolder();
        playSurfaceHolder.addCallback(this);
        playSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (recordView)
        {
            currentStatus = RECORD_STATUS_IDEL;
            cameraManager = new CameraManager(mLandScreen);
            recordSurfaceHolder = videoRecordSurface.getHolder();
            recordSurfaceHolder.addCallback(this);
            recordSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            cameraManager.setSurfaceHolder(recordSurfaceHolder);
            cameraManager.openVideo();
            ImageView imge = (ImageView) findViewById(R.id.right_button);
            imge.setBackgroundDrawable(null);
            imge.setImageResource(R.drawable.icon_circulation);
            title.setText(R.string.video_record);
            videoProtocal = new VideoProtocalJni();
            videoProtocal.setHandler(progressHandler);
        }
        else
        {
            currentStatus = RECORD_STATUS_PLAYING;
            //            mMediaPlayer.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener()
            //            {
            //                
            //                @Override
            //                public void onVideoSizeChanged(MediaPlayer mp, int width,
            //                        int height)
            //                {
            //                    View center = findViewById(R.id.center);
            //                    LayoutParams params = center.getLayoutParams();
            //                    if ((params.width - params.height) * (width - height) < 0)
            //                    {
            //                        Log.i(TAG, "onVideoSizeChanged");
            //                        int temp = params.width;
            //                        params.width = params.height;
            //                        params.height = temp;
            //                        center.setLayoutParams(params);
            //                    }
            //                }
            //            });
            destVideoFile = new File(
                    intent.getStringExtra(RecordVideoAction.MEDIA_PATH));
            String imagePath = intent.getStringExtra(RecordVideoAction.IMAGE_THUMB_PATH);
            if (null != imagePath)
            {
                videoPreviewImg.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            }
            fileSize = destVideoFile.length();
            findViewById(R.id.right_layout_total).setVisibility(View.GONE);
            ((Button) rightButton).setText(R.string.default_save);
            rightButton.setVisibility(View.VISIBLE);
            videoRecordSurface.setVisibility(View.GONE);
            recordLightImg.setVisibility(View.GONE);
            recordStatusText.setVisibility(View.GONE);
            findViewById(R.id.layout_bottom).setVisibility(View.GONE);
            title.setText(R.string.video_preview);
            initMediaPlayer();
            tipsTime.setText(textTime((int) timeCount / 1000));
        }
        
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see android.app.ActivityGroup#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (recordView)
        {
            outState.putString(TEMP_PATH, tempFileName);
            outState.putInt(CURRENT_STATUS, currentStatus);
            outState.putBoolean(FILE_SAVE_ENABLED, saveVideoBtn.isEnabled());
        }
        else
        {
            outState.putBoolean(FILE_SAVE_ENABLED, rightButton.isEnabled());
        }
    }
    
    /**
     * 初始化文件路径
     *
     * @return 初始化成功,返回true,失败返回false
     */
    private boolean initFilePath()
    {
        //        // 需要先判断存储卡是否可用,如果不可用,需要提示用户
        //        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        //        {
        //            return false;
        //        }
        //        
        //        // 检查SdCard剩余空间
        //        StatFs sf = new StatFs(Environment.getExternalStorageDirectory()
        //                .getPath());
        //        long blockSize = sf.getBlockSize();
        //        long availCount = sf.getAvailableBlocks();
        //        
        //        // 将获取到的数值转换成MB
        //        long count = availCount * blockSize >> 20;
        
        if (!MemoryStatus.isExternalMemoryAvailable(10l << 20))
        {
            // 如果剩余空间不足10M,则需要提示用户剩余空间不足
            return false;
        }
        
        if (isFileInit)
        {
            // 已经初始化过后,则不再重新初始化文件
            return true;
        }
        
        // 组合成三个文件路径
        audioFile = new File(
                UriUtil.getLocalStorageDir(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID(), FromType.SEND, LocalDirType.VIDEO)
                        + tempFileName + ".pcm");
        videoFile = new File(
                UriUtil.getLocalStorageDir(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID(), FromType.SEND, LocalDirType.VIDEO)
                        + tempFileName + ".yuv");
        destVideoFile = new File(
                UriUtil.getLocalStorageDir(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID(), FromType.SEND, LocalDirType.VIDEO)
                        + tempFileName + ".mp4");
        videoPreViewPic = new File(
                UriUtil.getLocalStorageDir(FusionConfig.getInstance()
                        .getAasResult()
                        .getUserID(), FromType.SEND, LocalDirType.THUMB_NAIL)
                        + tempFileName + ".jpg");
        
        isFileInit = true;
        return true;
    }
    
    /**
     * 
     * 单击事件处理<BR>
     * {@inheritDoc}
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                
                // 未发送直接返回
                back2PrePage();
                break;
            case R.id.record_control_btn:
                // 点击控制按钮,进行录制或停止录制
                if (RECORD_STATUS_RECORDING < currentStatus)
                {
                    showConfirmDialog(R.string.video_will_lost_record,
                            new DialogInterface.OnClickListener()
                            {
                                
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    try
                                    {
                                        if (RECORD_STATUS_PLAYING == currentStatus)
                                        {
                                            mMediaPlayer.stop();
                                            progressHandler.removeMessages(VIDEO_PLAY_TIMER);
                                        }
                                        cameraManager.startPreview();
                                        controlRecord();
                                    }
                                    catch (IOException e)
                                    {
                                        Logger.e(TAG,
                                                "Open camera failed, the camera can't use. errmsg is: "
                                                        + e.getCause());
                                        refreshView(RECORD_STATUS_CAMERA_ERR);
                                    }
                                }
                            });
                    
                }
                else
                {
                    controlRecord();
                }
                break;
            
            case R.id.send_video_btn:
                sendVideoBtn.setEnabled(false);
                
                // 发送录制完成的视频
                // 如果视频牌播放状态，先停止
                if (RECORD_STATUS_PLAYING == currentStatus)
                {
                    mMediaPlayer.stop();
                }
                
                Intent intent = new Intent();
                intent.putExtra(RecordVideoAction.MEDIA_PATH,
                        destVideoFile.getPath());
                intent.putExtra(RecordVideoAction.IMAGE_THUMB_PATH,
                        videoPreViewPic.getPath());
                intent.putExtra(RecordVideoAction.VIDEO_DURATION, timeCount);
                setResult(RESULT_OK, intent);
                destVideoFile = null;
                videoPreViewPic = null;
                finish();
                
                break;
            case R.id.surfaceMediaPlayer:
                // 在录制完成后的预览状态,点击界面暂停播放
                refreshView(RECORD_STATUS_PLAY_PAUSE);
                break;
            case R.id.video_preview_btn_img:
                // 压缩完成后显示预览按钮,点击进行播放
                refreshView(RECORD_STATUS_PLAYING);
                break;
            case R.id.right_layout:
                try
                {
                    cameraManager.switchCamera();
                }
                catch (IOException e)
                {
                    Logger.e(TAG,
                            "Open camera failed, the camera can't use. errmsg is: "
                                    + e.getCause());
                    refreshView(RECORD_STATUS_CAMERA_ERR);
                }
                break;
            case R.id.title_right_button:
                rightButton.setEnabled(false);
                saveVideoToLocal(destVideoFile, mNewVideoFilePath);
                break;
            case R.id.save_video_file_btn:
                saveVideoBtn.setEnabled(false);
                saveVideoToLocal(destVideoFile, mNewVideoFilePath);
                break;
            default:
                break;
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        back2PrePage();
    }
    
    /**
     * 控制录制状态
     */
    private void controlRecord()
    {
        if (currentStatus == RECORD_STATUS_RECORDING)
        {
            // 已经处于录制当中，点击则停止录制
            stopRecord();
            mCompressDialogView = LayoutInflater.from(this)
                    .inflate(R.layout.video_compress_dialog, null);
            mCompressDialog = new BasicDialog.Builder(this).setTitle(R.string.compress)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setContentView(mCompressDialogView)
                    .create();
            ((TextView) mCompressDialogView.findViewById(R.id.compress_message)).setText(getString(R.string.compress_progress,
                    0));
            mCompressDialog.setOnCancelListener(new OnCancelListener()
            {
                
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    VideoProtocalJni.stopCompress();
                    mCompressDialog = null;
                    mCompressDialogView = null;
                    refreshView(RECORD_STATUS_IDEL);
                }
            });
            mCompressDialog.show();
            VideoProtocalJni.startCompressCut(audioFile.toString(),
                    videoFile.toString(),
                    destVideoFile.toString(),
                    frameCount,
                    timeCount,
                    cameraManager.getVideoScreenHeight(),
                    cameraManager.getVideoScreenWidth(),
                    cameraManager.getVideoScreenHeight(),
                    cameraManager.getVideoScreenWidth(),
                    cameraManager.getCameraOrientation(),
                    VIDEO_MP4_QP);
            
            refreshView(RECORD_STATUS_COMPRESSING);
            
            // 这里将Camera释放掉, 否则在一些手机上无法预览播放
            releaseRecorders();
        }
        else
        {
            
            // 根据当前时间生成文件名
            tempFileName = DateUtil.getCurrentDateString();
            if (!initFilePath())
            {
                // 如果初始化文件路径失败,则禁用录制按钮,并提示用户存储卡不可用或空间不足
                //recordControlBtn.setEnabled(false);
                //tipsText.setText(R.string.no_sdcard_tips_text);
                showToast(R.string.no_sdcard_tips);
                return;
            }
            
            if (currentStatus == RECORD_STATUS_PLAY_PAUSE
                    || currentStatus == RECORD_STATUS_PLAY_FINISHED)
            {
                // 这里要将播放器reset掉,否则在一些手机上无法打开Camera进行重新录制
                mMediaPlayer.reset();
            }
            
            // 删除已经录制的文件
            deleteRecordFiles();
            
            // 准备音频录制
            prepareAudio();
            // 开始录制
            startRecord();
        }
    }
    
    /**
     * 根据录制和播放状态刷新界面控件View
     *
     * @param recordStatus 状态标识
     */
    private void refreshView(int recordStatus)
    {
        Logger.d(TAG, "refreshView: stuatus = " + recordStatus);
        
        // 刷新当前状态
        currentStatus = recordStatus;
        unLockScreen();
        switch (recordStatus)
        {
            case RECORD_STATUS_RECORDING:
                // 开始进入录制状态
                // 先隐藏视频播放按钮图片和保存按钮
                videoPlayStartImgBtn.setVisibility(View.GONE);
                
                // 0. 显示视频录制的SurfaceView
                videoRecordSurface.setVisibility(View.VISIBLE);
                //videoRecordSurface.setKeepScreenOn(true);
                
                //                // 1. 控制按钮显示内容
                //                if (!isLandScreen)
                //                {
                //                    int leftPadding = (int) (dip2pxScale * 30 + 0.5f);
                //                    int rightPadding = (int) (dip2pxScale * 34 + 0.5);
                //                    
                //                    // 竖屏需要调整录制按钮大小,横屏不需要调整
                //                    recordControlBtn.setPadding(leftPadding, 0, rightPadding, 0);
                //                    //                    recordCtrlBtnText.setText(R.string.video_record_stop);
                //                }
                recordControlBtn.setImageResource(R.drawable.icon_suspension_select);
                recordControlBtn.setEnabled(true);
                //                recordCtrlBtnImg.setImageResource(R.drawable.features_welcome_p1);
                recordControlBtn.setEnabled(true);
                
                // 2. 视频预览界面头部状态文本及时间文本
                recordLightImg.setVisibility(View.VISIBLE);
                recordStatusText.setVisibility(View.VISIBLE);
                recordStatusText.setText(R.string.video_recordstatus_recording);
                
                // 5. 发送按钮隐藏
                sendVideoBtn.setVisibility(View.INVISIBLE);
                saveVideoBtn.setVisibility(View.INVISIBLE);
                
                // 6. 隐藏播放Surface和预览图片的View
                videoPlaySurface.setVisibility(View.GONE);
                videoPreviewImg.setVisibility(View.GONE);
                
                tipsText.setText(null);
                
                break;
            
            case RECORD_STATUS_COMPRESSING:
                recordLightImg.setVisibility(View.INVISIBLE);
                recordStatusText.setVisibility(View.INVISIBLE);
                videoPreviewImg.setImageBitmap(videoPreviewBitmap);
                videoPreviewImg.setVisibility(View.VISIBLE);
                
                // 录制结束,进入压缩中状态
                // 1. 控制按钮不可用
                //                recordControlBtn.setEnabled(false);
                // 2. 视频预览界面头部状态文本及时间文本隐藏
                //                recordLightImg.setVisibility(View.INVISIBLE);
                //                recordStatusText.setVisibility(View.GONE);
                //                recordTimeText.setVisibility(View.GONE);
                
                // 3. 压缩状态条及进度显示
                //                progressText.setText(String.format(getString(R.string.compress_progress),
                //                        0));
                //                ((LinearLayout) findViewById(R.id.progress_layout)).setVisibility(View.VISIBLE);
                break;
            
            case RECORD_STATUS_COMPRESSED:
                
                changeSize(false);
                
                title.setText(R.string.video_preview);
                
                tipsText.setText(null);
                
                rightButton.setEnabled(false);
                
                // 压缩完成状态
                // 1. 控制按钮改为重新录制,发送按钮显示
                //                if (!isLandScreen)
                //                {
                //                    int leftPadding = (int) (dip2pxScale * 20 + 0.5f);
                //                    int rightPadding = (int) (dip2pxScale * 24 + 0.5);
                //                    
                //                    // 竖屏需要调整录制按钮大小,横屏不需要调整
                //                    recordControlBtn.setPadding(leftPadding, 0, rightPadding, 0);
                ////                    recordCtrlBtnText.setText(R.string.video_re_record_btn);
                //                }
                recordControlBtn.setImageResource(R.drawable.icon_video_camera);
                recordControlBtn.setEnabled(true);
                //                recordCtrlBtnImg.setImageResource(R.drawable.features_welcome_p2);
                sendVideoBtn.setVisibility(View.VISIBLE);
                
                // 开放保存文件到本地的能力
                saveVideoBtn.setVisibility(View.VISIBLE);
                saveVideoBtn.setEnabled(true);
                
                // 2. 头部状态及时间文本, 隐藏状态,显示时间
                recordLightImg.setVisibility(View.INVISIBLE);
                recordStatusText.setVisibility(View.INVISIBLE);
                
                // 1. 隐藏录制视频的SurfaceView
                videoRecordSurface.setVisibility(View.INVISIBLE);
                
                // 2. 设置预览图片ImageView的图片源,并显示预览图片
                videoPreviewImg.setImageBitmap(videoPreviewBitmap);
                videoPlaySurface.setClickable(false);
                
                initMediaPlayer();
                break;
            case RECORD_STATUS_PLAYING:
                // 进入视频播放状态
                
                // 0. 允许切换前后摄像头
                //                rightButton.setEnabled(false);
                
                // 1. 立即开始播放
                recordControlBtn.setEnabled(false);
                
                mMediaPlayer.setDisplay(playSurfaceHolder);
                mMediaPlayer.start();
                setPlayTime(true);
                // 2. 如果初次进入播放状态,需要准备播放SurfaceView
                // C.隐藏预览图片
                videoPreviewImg.setVisibility(View.GONE);
                
                // 3. 将播放按钮隐藏
                videoPlayStartImgBtn.setVisibility(View.GONE);
                
                // 4. 播放界面能接收点击事件
                videoPlaySurface.setClickable(true);
                break;
            
            case RECORD_STATUS_PLAY_PAUSE:
                // 1. 先处理界面显示
                
                videoPlayStartImgBtn.setVisibility(View.VISIBLE);
                
                // 2. 让播放Surface不能响应点击事件, 并且允许设备关闭屏幕
                videoPlaySurface.setClickable(false);
                
                mMediaPlayer.pause();
                progressHandler.removeMessages(VIDEO_PLAY_TIMER);
                recordControlBtn.setEnabled(true);
                
                // 4. 允许切换前后摄像头
                //                rightButton.setEnabled(true);
                
                break;
            
            case RECORD_STATUS_PLAY_FINISHED:
                // 预览播放结束,重置播放器
                videoPlaySurface.setClickable(false);
                videoPlayStartImgBtn.setVisibility(View.VISIBLE);
                recordControlBtn.setEnabled(true);
                
                // 2. 允许界面关闭屏幕
                
                // 3. 允许切换前后摄像头
                //                rightButton.setEnabled(true);
                
                break;
            case RECORD_STATUS_IDEL:
                // 刚进入时状态
                changeSize(true);
                if (cameraManager.multiCamera())
                {
                    rightButton.setEnabled(true);
                }
                else
                {
                    rightButton.setEnabled(false);
                }
                videoRecordSurface.setVisibility(View.VISIBLE);
                
                // 1. 控制按钮显示内容
                //                if (!isLandScreen)
                //                {
                //                    int leftPadding = (int) (dip2pxScale * 30 + 0.5f);
                //                    int rightPadding = (int) (dip2pxScale * 34 + 0.5);
                //                    
                //                    // 竖屏需要调整录制按钮大小,横屏不需要调整
                //                    recordControlBtn.setPadding(leftPadding, 0, rightPadding, 0);
                ////                    recordCtrlBtnText.setText(R.string.video_start_record_btn);
                //                }
                
                recordControlBtn.setImageResource(R.drawable.icon_video_camera);
                recordControlBtn.setEnabled(true);
                
                // 2. 显示状态灯,状态字符及录制时间
                recordLightImg.setVisibility(View.GONE);
                recordStatusText.setVisibility(View.GONE);
                
                tipsText.setText(null);
                tipsSize.setText(null);
                tipsTime.setText(null);
                
                videoTimeText.setText(textTime(0));
                videoTimeText.setVisibility(View.VISIBLE);
                
                // 3.隐藏预览按钮和预览图片
                videoPlayStartImgBtn.setVisibility(View.GONE);
                videoPreviewImg.setVisibility(View.GONE);
                
                videoPlaySurface.setVisibility(View.GONE);
                // 4. 隐藏进度条及进度数字
                //                ((LinearLayout) findViewById(R.id.progress_layout)).setVisibility(View.GONE);
                
                // 5. 隐藏保存和发送按钮
                saveVideoBtn.setVisibility(View.INVISIBLE);
                sendVideoBtn.setVisibility(View.INVISIBLE);
                try
                {
                    cameraManager.startPreview();
                }
                catch (IOException e)
                {
                    Logger.e(TAG,
                            "Open camera failed, the camera can't use. errmsg is: "
                                    + e.getCause());
                    refreshView(RECORD_STATUS_CAMERA_ERR);
                }
                
                break;
            
            case RECORD_STATUS_CAMERA_ERR:
                // 1. 播放控制按钮灰化, 提示文本设置
                recordControlBtn.setEnabled(false);
                tipsText.setText(R.string.video_camera_open_failed);
                
                // 2. 隐藏其它按钮
                recordLightImg.setVisibility(View.INVISIBLE);
                recordStatusText.setVisibility(View.GONE);
                videoTimeText.setVisibility(View.GONE);
                
                if (cameraManager.multiCamera())
                {
                    rightButton.setEnabled(true);
                }
                else
                {
                    rightButton.setEnabled(false);
                }
                break;
            case PLAY_STATUS_IDEL:
                
                // 3. 显示中间的播放按钮,并响应事件
                initMediaPlayer();
                tipsTime.setText(textTime((int) timeCount / 1000));
                break;
            default:
                break;
        }
    }
    
    private void initMediaPlayer()
    {
        
        // 3. 显示中间的播放按钮,并响应事件
        videoPreviewImg.setVisibility(View.VISIBLE);
        videoPlayStartImgBtn.setVisibility(View.VISIBLE);
        videoPlayStartImgBtn.setClickable(true);
        videoPlaySurface.setVisibility(View.VISIBLE);
        mMediaPlayer.reset();
        mMediaPlayer.setDisplay(playSurfaceHolder);
        try
        {
            mMediaPlayer.setDataSource(destVideoFile.getAbsolutePath());
            mMediaPlayer.prepare();
        }
        catch (IllegalArgumentException e1)
        {
            e1.printStackTrace();
        }
        catch (IllegalStateException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                Logger.i(TAG, "mediaplayer play completed..");
                progressHandler.sendEmptyMessage(VIDEO_PLAY_COMPLETED);
            }
        });
        timeCount = mMediaPlayer.getDuration();
        tipsSize.setText(getString(R.string.video_size, fileSize / 1024.0));
        setPlayTime(false);
    }
    
    private void setPlayTime(boolean playing)
    {
        int sec = mMediaPlayer.getCurrentPosition();
        setPlayTime(sec);
        if (playing)
        {
            progressHandler.sendEmptyMessageDelayed(VIDEO_PLAY_TIMER,
                    1000 - sec % 1000);
        }
    }
    
    private void setPlayTime(int sec)
    {
        videoTimeText.setText(textTime(sec / 1000));
        if (recordView)
        {
            tipsTime.setText(textTime((int) timeCount / 1000 - sec / 1000));
        }
    }
    
    private String textTime(int sec)
    {
        return sec / 60 + ":" + String.format("%02d", sec % 60);
    }
    
    private void changeSize(boolean trans)
    {
        if (mLandScreen)
        {
            LayoutParams params = centerView.getLayoutParams();
            if (params.width > params.height != trans)
            {
                int temp = params.width;
                params.width = params.height;
                params.height = temp;
                centerView.setLayoutParams(params);
            }
        }
    }
    
    /**
     * 录制未发送，返回时做清理工作
     */
    private void back2PrePage()
    {
        if (RECORD_STATUS_RECORDING != currentStatus)
        {
            
            // 点击控制按钮,进行录制或停止录制
            if (RECORD_STATUS_RECORDING < currentStatus && recordView)
            {
                showConfirmDialog(R.string.video_will_lost,
                        new DialogInterface.OnClickListener()
                        {
                            
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        });
                
            }
            else
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
    
    /**
     * 删除录制的文件
     */
    private void deleteRecordFiles()
    {
        // 处理视频，如果已经录制，需要删除掉
        deleteFile(audioFile);
        deleteFile(videoFile);
        deleteFile(destVideoFile);
        deleteFile(videoPreViewPic);
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        Logger.d(TAG, "onPause, current status = " + currentStatus);
        super.onPause();
        // 1. 初始化状态,需要释放Camera,回来后进入IDEL状态
        // 2. 录制中状态,需要停止录制,不压缩,释放Camera,回来后进入IDEL状态
        // 3. 压缩中状态,停止压缩,释放Camera,回来后进入IDEL状态
        // 以上三种情况,需要删除已经录制的文件,并将发送按钮隐藏(如果不是第一次录)
        
        // 4. 压缩完成状态, 释放Camera,回来后进入预览截图状态,准备进入播放
        // 5. 播放完成状态,同上
        
        // 6. 播放中状态, 释放Camera, 暂停播放,进入暂停状态
        // 7. 播放暂停状态, 释放Camera
        
        switch (currentStatus)
        {
            case RECORD_STATUS_RECORDING:
                stopRecord();
            case RECORD_STATUS_IDEL:
                deleteRecordFiles();
                currentStatus = RECORD_STATUS_IDEL;
                break;
            //            case RECORD_STATUS_COMPRESSED:
            //            case RECORD_STATUS_PLAY_FINISHED:
            //                if (recordView)
            //                {
            //                    currentStatus = RECORD_STATUS_COMPRESSED;
            //                }
            //                else
            //                {
            //                    currentStatus = PLAY_STATUS_IDEL;
            //                }
            //                break;
            case RECORD_STATUS_PLAYING:
                currentStatus = RECORD_STATUS_PLAY_PAUSE;
                if (null != mMediaPlayer)
                {
                    progressHandler.removeMessages(VIDEO_PLAY_TIMER);
                    mMediaPlayer.pause();
                }
                break;
            case RECORD_STATUS_PLAY_PAUSE:
                // 暂停状态不需要做单独处理,仍保留此状态
                break;
            default:
                break;
        }
        
        if (recordView)
        {
            // 在暂停事件中立即释放摄像头
            releaseRecorders();
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        Logger.e(TAG, "onResume, current status = " + currentStatus);
        super.onResume();
        
        refreshView(currentStatus);
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#finish()
     */
    @Override
    public void finish()
    {
        progressHandler.removeMessages(VIDEO_PLAY_TIMER);
        if (recordView)
        {
            deleteRecordFiles();
            
            // 退出前释放Camera
            releaseRecorders();
        }
        if (null != mMediaPlayer)
        {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.finish();
    }
    
    /**
     * 删除文件,如果文件存在
     *
     * @param file 需要删除的文件对象
     */
    private void deleteFile(File file)
    {
        if (file != null && file.exists())
        {
            file.delete();
        }
    }
    
    /**
     * 
     * 保存视频文件到本地，目前没有使用，只是对已有文件的文件名进行显示<BR>
     * @param sourceFile
     * @param fromType
     */
    private void saveVideoToLocal(File sourceFile, String newPath)
    {
        if (null == sourceFile || null == newPath)
        {
            Logger.e(TAG, "saveImage failed. imagePath=" + sourceFile
                    + " newPath=" + newPath);
            return;
            
        }
        
        if (FileUtil.copyFile(sourceFile, new File(newPath)))
        {
            showToast(getString(R.string.video_save_success_toast, newPath));
        }
        
    }
    
    /**
     * 将压缩转换后的Bitmap保存到内存中, 以供预览界面显示
     * @param bitmap Bitmap
     */
    protected void setVideoPreviewBitmap(Bitmap bitmap)
    {
        videoPreviewBitmap = bitmap;
    }
    
    // ---------------------------------------------Surface控件事件----------------------------------------------
    
    /**
     * 
     * {@inheritDoc}
     * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Logger.i(TAG, "VideoSurface surfaceCreated");
        if (holder == recordSurfaceHolder
                && RECORD_STATUS_COMPRESSING > currentStatus)
        {
            // 创建时,打开摄像头设备
            try
            {
                cameraManager.startPreview();
            }
            catch (IOException e)
            {
                Logger.e(TAG,
                        "Open camera failed, the camera can't use. errmsg is: "
                                + e.getCause());
                refreshView(RECORD_STATUS_CAMERA_ERR);
            }
        }
        else if (holder == playSurfaceHolder
                && RECORD_STATUS_COMPRESSING < currentStatus)
        {
            initMediaPlayer();
            if (RECORD_STATUS_PLAYING == currentStatus)
            {
                refreshView(RECORD_STATUS_PLAYING);
            }
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height)
    {
        Logger.i(TAG, "VideoSurface surfaceChanged");
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Logger.i(TAG, "VideoSurface surfaceDestroyed");
    }
    
    private void prepareAudio()
    {
        // 准备音频录制
        if (wavAudioRecorder != null)
        {
            wavAudioRecorder.release();
            wavAudioRecorder = null;
        }
        
        wavAudioRecorder = new WAVAudioRecorder(true, AudioSource.MIC,
                AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        
        // 设置输出路径,准备录制音频,必须在Prepare之前设置
        wavAudioRecorder.setOutputFile(audioFile.toString());
        if (wavAudioRecorder.getState() == WAVAudioRecorder.State.INITIALIZING)
        {
            wavAudioRecorder.prepare();
        }
    }
    
    /**
     * 开始录制
     */
    private void startRecord()
    {
        changeSize(true);
        title.setText(R.string.video_record);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
        tipsText.setText(null);
        tipsSize.setText(null);
        tipsTime.setText(null);
        videoTimeText.setText(textTime(0));
        try
        {
            fileStream = new FileOutputStream(videoFile, false);
            cameraManager.setPreviewCallback(this);
        }
        catch (Exception e)
        {
            Logger.e(TAG, "fileOutStream create err: " + e.getCause());
            e.printStackTrace();
            showToast(R.string.video_start_record_failed);
            refreshView(RECORD_STATUS_CAMERA_ERR);
            return;
        }
        // 开始录制音频
        if (wavAudioRecorder.getState() == WAVAudioRecorder.State.READY)
        {
            wavAudioRecorder.start();
        }
        frameCount = 0;
        mCurSec = 0;
        startTime = System.currentTimeMillis();
        refreshView(RECORD_STATUS_RECORDING);
    }
    
    /**
     * 停止录制
     */
    private void stopRecord()
    {
        leftButton.setEnabled(true);
        if (RECORD_STATUS_RECORDING == currentStatus)
        {
            try
            {
                cameraManager.stopPreview();
                wavAudioRecorder.stop();
                // 停止录制音频和视频
                timeCount = System.currentTimeMillis() - startTime;
                
                //                Thread.sleep(100);
                
                fileStream.close();
                fileStream = null;
            }
            catch (Exception e)
            {
                Logger.e(TAG, "stopRecord failed: " + e.getCause());
            }
        }
    }
    
    private void releaseAudioRecorder()
    {
        if (wavAudioRecorder != null)
        {
            wavAudioRecorder.release();
            wavAudioRecorder = null;
        }
    }
    
    private void releaseRecorders()
    {
        cameraManager.releaseCamera();
        releaseAudioRecorder();
    }
    
    /**
     * 
     * {@inheritDoc}
     * @see android.hardware.Camera.PreviewCallback#onPreviewFrame(byte[], android.hardware.Camera)
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        try
        {
            if (null != fileStream)
            {
                frameCount++;
                fileStream.write(data);
                int curSec = (int) ((System.currentTimeMillis() - startTime) / 1000);
                if (curSec != mCurSec)
                {
                    int remain = RECORD_TIME_MAX_LIMIT - curSec;
                    videoTimeText.setText(textTime(curSec));
                    mCurSec = curSec;
                    if (REMAIN_WARN >= remain)
                    {
                        tipsText.setText(getResources().getQuantityString(R.plurals.video_record_remian,
                                Math.max(remain, 1),
                                remain));
                        if (0 >= remain)
                        {
                            controlRecord();
                        }
                    }
                }
                if (frameCount == GET_PREVIEW_PIC_FRAME_NUM)
                {
                    // 当数据传输至第2侦图像时,获取数据做为视频缩略图
                    new CreatePreviewPicThread(
                            cameraManager.getVideoScreenWidth(),
                            cameraManager.getVideoScreenHeight(),
                            cameraManager.getCameraOrientation(), data,
                            videoPreViewPic).start();
                }
            }
        }
        catch (IOException e)
        {
            Logger.e(TAG, "fileStream write err: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建预览图片的线程类
     * @author qinyangwang
     * @version [RCS Client V100R001C03, 2012-4-23]
     */
    private class CreatePreviewPicThread extends Thread
    {
        private byte[] mData;
        
        private File mDestFile;
        
        private int width;
        
        private int height;
        
        private int orientation;
        
        /**
         * 
         * [构造简要说明]
         * @param width 图片宽
         * @param height 图片高
         * @param orientation 图片角度
         * @param data 图片数据
         * @param bitmapFile 图片文件
         */
        public CreatePreviewPicThread(int width, int height, int orientation,
                byte[] data, File bitmapFile)
        {
            this.width = width;
            this.height = height;
            this.orientation = orientation;
            mData = data;
            mDestFile = bitmapFile;
        }
        
        /**
         * 
         * {@inheritDoc}
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
            FileOutputStream out = null;
            try
            {
                Bitmap bitmap = rotate(decodeYUV420SP(mData));
                
                // 判断是否正确获取到Bitmap
                if (bitmap == null)
                {
                    return;
                }
                
                // 将bitmap设置到Activity中,留待显示预览
                RecordVideoActivity.this.setVideoPreviewBitmap(bitmap);
                
                // 将Bitmap保存至文件
                out = new FileOutputStream(mDestFile);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out))
                {
                    out.flush();
                }
            }
            catch (Exception ex)
            {
                Logger.e(TAG,
                        "write byte to file failed. the msg is: "
                                + ex.getCause());
            }
            finally
            {
                FileUtil.closeStream(out);
            }
        }
        
        // //
        // -------------------------------------处理图像---------------------------------------
        /**
         * 从yuv格式的一帧图片,生成bitmap
         *
         * @param yuv420sp yuv格式的数据字节
         * @return 生成的bitmap, 在2.1以上机器上需要转90度才能使用
         */
        private Bitmap decodeYUV420SP(byte[] yuv420sp)
        {
            
            final int frameSize = width * height;
            int[] rgb = new int[frameSize];
            
            for (int j = 0, yp = 0; j < height; j++)
            {
                int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
                for (int i = 0; i < width; i++, yp++)
                {
                    int y = (0xff & ((int) yuv420sp[yp])) - 16;
                    if (y < 0)
                    {
                        y = 0;
                    }
                    if ((i & 1) == 0)
                    {
                        v = (0xff & yuv420sp[uvp++]) - 128;
                        u = (0xff & yuv420sp[uvp++]) - 128;
                    }
                    
                    int y1192 = 1192 * y;
                    int r = y1192 + 1634 * v;
                    int g = y1192 - 833 * v - 400 * u;
                    int b = y1192 + 2066 * u;
                    
                    r = getColorInt(r);
                    g = getColorInt(g);
                    b = getColorInt(b);
                    
                    rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                            | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                }
            }
            
            return Bitmap.createBitmap(rgb,
                    0,
                    width,
                    width,
                    height,
                    Config.RGB_565);
            
        }
        
        /**
         * 检验数字是否符合颜色值范围
         *
         * @param b 需要检查的原值
         * @return 校正后的值,保证在0和262143之间
         */
        private int getColorInt(int b)
        {
            if (b < 0)
            {
                b = 0;
            }
            else if (b > 262143)
            {
                b = 262143;
            }
            
            return b;
        }
        
        /**
         * 将Bitmap进行旋转,生成正常角度的图片
         *
         * @param b 原始图片
         * @param degrees 原始角度
         * @return 旋转后的图片
         */
        private Bitmap rotate(Bitmap b)
        {
            Bitmap bitmap = b;
            if (orientation != 0 && b != null)
            {
                Matrix m = new Matrix();
                m.setRotate(orientation,
                        (float) b.getWidth() / 2,
                        (float) b.getHeight() / 2);
                try
                {
                    bitmap = Bitmap.createBitmap(b,
                            0,
                            0,
                            b.getWidth(),
                            b.getHeight(),
                            m,
                            true);
                    
                }
                catch (OutOfMemoryError ex)
                {
                    Logger.e(TAG, "OutofMemoryErr");
                    ex.printStackTrace();
                }
                finally
                {
                    b.recycle();
                }
            }
            
            return bitmap;
        }
    }
    
}
