/*
 * 文件名: PreviewActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: fanniu
 * 创建时间:2012-4-5
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.CropImageAction;
import com.huawei.basic.android.im.common.FusionAction.RecordVideoAction;
import com.huawei.basic.android.im.common.FusionMessageType;
import com.huawei.basic.android.im.logic.im.IImLogic;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.UriUtil.FromType;

/**
 * 图片预览, 下载，视频下载Activity<BR>
 * @author fanniu
 * @version [RCS Client V100R001C03, 2012-4-5] 
 */
public class DownloadActivity extends BasicActivity implements OnClickListener
{
    
    //    /**
    //     * DEBUG_TAG
    //     */
    //    private static final String TAG = "DownloadActivity";
    
    /**
     * 返回按钮
     */
    private Button mBackBtn;
    
    /**
     * 预览标题
     */
    private TextView mTitle;
    
    /**
     * 蒙版图片
     */
    private TextView mMaskImage;
    
    /**
     * 下载时的默认图片
     */
    private ImageView mDefaultImg;
    
    /**
     * ImLogic
     */
    private IImLogic mImLogic;
    
    /**
     * MessageID
     */
    private String mMsgId;
    
    /**
     * 媒体类型
     */
    private int mMediaType;
    
    /**
     * title_name
     */
    private String mPreviewTitle;
    
    /**
     * 下载按钮
     */
    private ImageView mDownloadBtn;
    
    /**
     * 下载按钮标志
     */
    private boolean mIsDownloading = true;
    
    /**
     * 图片文件大小
     */
    private TextView mImageSize;
    
    /**
     * 蒙版初始高度
     */
    private int mHeight;
    
    /**
     * 单击事件<BR>
     * @param v View
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        //返回
            case R.id.left_button:
                
                finish();
                break;
            case R.id.download_icon:
                
                if (mIsDownloading)
                {
                    //暂停下载
                    mImLogic.pauseDownload(mMsgId);
                    mDownloadBtn.setImageResource(R.drawable.icon_go);
                    mIsDownloading = false;
                }
                else
                {
                    //开始下载
                    mImLogic.continueDownload(mMsgId);
                    mDownloadBtn.setImageResource(R.drawable.icon_stop);
                    mIsDownloading = true;
                }
                
                break;
            default:
                break;
        }
    }
    
    /**
     * [一句话功能简述]<BR>
     * @param savedInstanceState savedInstanceState
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.im_download);
        
        initData();
        
        initView();
        
        //Message 判空
        if (null != mMsgId)
        {
            ProgressModel progress = mImLogic.downloadMedia(mMsgId,
                    FromType.RECEIVE);
            if (progress != null)
            {
                updateProgress(progress);
            }
        }
        
    }
    
    /**
     * init logic<BR>
     * @see com.huawei.basic.android.im.ui.basic.BasicActivity#initLogics()
     */
    @Override
    protected void initLogics()
    {
        mImLogic = (IImLogic) super.getLogicByInterfaceClass(IImLogic.class);
    }
    
    /**
     * 下载图片消息处理界面<BR>
     * @param msg Message
     * @see com.huawei.basic.android.im.framework.ui.BaseActivity#handleStateMessage(android.os.Message)
     */
    @Override
    protected void handleStateMessage(Message msg)
    {
        super.handleStateMessage(msg);
        
        switch (msg.what)
        {
        //下载时的蒙版效果
            case FusionMessageType.DownloadType.DOWNLOADING:
                ProgressModel precess = (ProgressModel) msg.obj;
                if (precess.getId() != null && precess.getId().equals(mMsgId))
                {
                    updateProgress(precess);
                }
                break;
            //下载完成后跳转到预览界面
            case FusionMessageType.DownloadType.DOWNLOAD_FINISH:
                String[] retStrs = (String[]) msg.obj;
                if (retStrs[0] != null && retStrs[0].equals(mMsgId))
                {
                    String downFilePath = retStrs[1];
                    Intent backChat = getIntent();
                    Intent intent = new Intent();
                    if (FusionAction.DownloadAction.DownloadMediaType.IMG == mMediaType)
                    {
                        intent.setAction(FusionAction.CropImageAction.ACTION);
                        intent.putExtra(FusionAction.CropImageAction.EXTRA_PATH,
                                downFilePath);
                        intent.putExtra(CropImageAction.EXTRA_MODE,
                                CropImageAction.MODE_SAVE);
                        //设置文件保存路径
                        intent.putExtra(CropImageAction.SAVE_PATH,
                                mImLogic.getImageFilePath(FromType.RECEIVE));
                    }
                    else
                    {
                        intent.setAction(FusionAction.RecordVideoAction.ACTION);
                        intent.putExtra(RecordVideoAction.MEDIA_PATH,
                                downFilePath);
                        intent.putExtra(RecordVideoAction.IMAGE_THUMB_PATH,
                                backChat.getStringExtra(RecordVideoAction.IMAGE_THUMB_PATH));
                        
                        intent.putExtra(RecordVideoAction.NEW_PATH,
                                mImLogic.getVideoFilePath(FromType.RECEIVE));
                    }
                    startActivity(intent);
                    finish();
                }
                break;
            case FusionMessageType.DownloadType.DOWNLOAD_FAILED:
                // TODO:错误提示有待确认
                showToast(R.string.download_failed);
                break;
            default:
                break;
        }
        
    }
    
    /**
     * 初始化相关数据<BR>
     */
    private void initData()
    {
        mMsgId = getIntent().getStringExtra(FusionAction.DownloadAction.EXTRA_MSG_ID);
        mMediaType = getIntent().getIntExtra(FusionAction.DownloadAction.EXTRA_MEDIA_TYPE,
                FusionAction.DownloadAction.DownloadMediaType.IMG);
        mPreviewTitle = getIntent().getStringExtra(FusionAction.DownloadAction.EXTRA_TITLE_NAME);
    }
    
    /**
     * 初始化视图<BR>
     */
    private void initView()
    {
        
        // 1.获取组件对象
        mDefaultImg = (ImageView) findViewById(R.id.default_img);
        mBackBtn = (Button) findViewById(R.id.left_button);
        mTitle = (TextView) findViewById(R.id.title);
        mMaskImage = (TextView) findViewById(R.id.image_mask);
        mImageSize = (TextView) findViewById(R.id.image_size);
        mDownloadBtn = (ImageView) findViewById(R.id.download_icon);
        
        mTitle.setText(getResources().getString(R.string.preview_title)
                + mPreviewTitle);
        if (mMediaType == FusionAction.DownloadAction.DownloadMediaType.IMG)
        {
            mDefaultImg.setImageResource(R.drawable.download_default_image);
        }
        else
        {
            mDefaultImg.setImageResource(R.drawable.download_default_video);
        }
        
        // 2.设置组件监听
        mDownloadBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
    }
    
    /**
     * 
     * 更新页面上进度显示(蒙版效果及进度文本显示)
     * @param progress 当前下载进度
     */
    private void updateProgress(ProgressModel progress)
    {
        
        if (mHeight == 0)
        {
            mHeight = mMaskImage.getLayoutParams().height;
        }
        double lastHeight = mHeight - (mHeight * progress.getPercent() * 0.01);
        
        ViewGroup.LayoutParams params = mMaskImage.getLayoutParams();
        params.height = (int) lastHeight;
        mMaskImage.setLayoutParams(params);
        
        if (lastHeight == 0)
        {
            mMaskImage.setVisibility(View.GONE);
        }
        mImageSize.setText(progress.getFinished() / 1024 + "K/"
                + progress.getTotal() / 1024 + "K");
        
    }
}