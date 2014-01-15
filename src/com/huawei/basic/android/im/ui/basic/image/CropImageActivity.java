/*
 * 文件名: CropImageActivity1.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 图片操作页面
 * 创建人: tjzhang
 * 创建时间:2012-4-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.common.FusionAction.CropImageAction;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.ui.basic.BasicActivity;
import com.huawei.basic.android.im.utils.FileUtil;
import com.huawei.basic.android.im.utils.ImageUtil;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 图片查看，下载，剪切页面<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-4-20] 
 */
public class CropImageActivity extends BasicActivity implements
        View.OnClickListener, OnTouchListener
{
    private static final String TAG = "CropImageActivity";
    
    private static final long SIZE_MB = 1 * 1024 * 1024;
    
    /**
    * 图片路径
    */
    private String mImagePath;
    
    /**
     * 点击保存按钮时图片的存放路径,路径中包含文件名称
     */
    private String mNewPath;
    
    private int mode;
    
    private Reference<Bitmap> mBitmapRef;
    
    private CropZoomView mCropZoomView;
    
    private ZoomState mZoomState;
    
    //根据这个判断图片是否被加载
    private boolean imageLoadStatus;
    
    /**
     * 控制区域
     */
    private View mControlBar;
    
    /**
     * title_bar
     */
    private View mTitleBar;
    
    /**
     * 父视图
     */
    private View mRootLayout;
    
    /**
     * 是否显示
     */
    private boolean mIsVisible = true;
    
    /**
     * 发送或接收按钮
     */
    private Button mBtnSendOrSave;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.crop_image);
        
        mImagePath = getIntent().getStringExtra(CropImageAction.EXTRA_PATH);
        mode = getIntent().getIntExtra(CropImageAction.EXTRA_MODE,
                CropImageAction.MODE_CROP);
        initView();
        imageLoadStatus = loadImage(mImagePath);
    }
    
    private void initView()
    {
        //1.声明所使用的控件
        mBtnSendOrSave = (Button) findViewById(R.id.right_button);
        TextView title = (TextView) findViewById(R.id.title);
        mCropZoomView = (CropZoomView) findViewById(R.id.cropZoomView);
        mBtnSendOrSave.setText(getResources().getString(R.string.default_save));
        mControlBar = findViewById(R.id.pic_operation_controls);
        mTitleBar = findViewById(R.id.crop_image_title);
        mRootLayout = findViewById(R.id.root_view);
        mBtnSendOrSave.setVisibility(View.VISIBLE);
        mBtnSendOrSave.setOnClickListener(this);
        
        //2.判断模式
        //裁剪模式
        if (mode == CropImageAction.MODE_CROP)
        {
            title.setText(R.string.cut_image);
            mCropZoomView.setNeedShowCropArea(true);
        }
        //发送模式
        else if (mode == CropImageAction.MODE_SEND_VIEW)
        {
            title.setText(getResources().getString(R.string.preview_image));
            mBtnSendOrSave.setText(R.string.send);
            mRootLayout.setOnTouchListener(this);
        }
        //查看模式
        else if (mode == CropImageAction.MODE_VIEW)
        {
            title.setText(getResources().getString(R.string.preview_image));
            mBtnSendOrSave.setVisibility(View.INVISIBLE);
            mRootLayout.setOnTouchListener(this);
        }
        //保存模式
        else if (mode == CropImageAction.MODE_SAVE)
        {
            //设置保存路径
            mNewPath = getIntent().getStringExtra(CropImageAction.SAVE_PATH);
            title.setText(getResources().getString(R.string.preview_image));
            mBtnSendOrSave.setText(R.string.default_save);
            mRootLayout.setOnTouchListener(this);
        }
        
        findViewById(R.id.left_button).setOnClickListener(this);
        findViewById(R.id.btnRotation).setOnClickListener(this);
        findViewById(R.id.btnZoomIn).setOnClickListener(this);
        findViewById(R.id.btnZoomOut).setOnClickListener(this);
    }
    
    /**
     * 
     * 根据给定路径加载图片到CropZoomView组件中
     * @param path 图片路径
     * @return 加载图片到CropZoomView中是否成功
     */
    private boolean loadImage(String path)
    {
        boolean isLoad = false;
        if (!(new File(path).exists()))
        {
            showOnlyConfirmDialog(R.string.image_not_exist,
                    new OnClickListener()
                    {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }
                    });
        }
        if (StringUtil.isNullOrEmpty(path))
        {
            Logger.d(TAG, "图片路径为空,加载图片失败...");
            return isLoad;
        }
        Bitmap bitmap = getBitmap(path);
        if (null == bitmap)
        {
            return isLoad;
        }
        //执行到这边表示图片加载成功
        mBitmapRef = new WeakReference<Bitmap>(bitmap);
        //把图片放入CropZoomView中
        mCropZoomView.setImage(bitmap);
        
        //初始化zoomState
        mZoomState = new ZoomState();
        mZoomState.setPanX(0.5f);
        mZoomState.setPanY(0.5f);
        mZoomState.setZoom(2.4f);
        mCropZoomView.setZoomState(mZoomState);
        if (mode == CropImageAction.MODE_CROP)
        {
            CropZoomViewOnTouchListener cropZoomViewOnTouchListener = new CropZoomViewOnTouchListener();
            cropZoomViewOnTouchListener.setZoomState(mZoomState);
            mCropZoomView.setOnTouchListener(cropZoomViewOnTouchListener);
        }
        mCropZoomView.setVisibility(View.VISIBLE);
        Logger.d(TAG, "loadImage()中加载图片成功...");
        isLoad = true;
        return isLoad;
    }
    
    /**
     * 给定图片路径，获得bitmap，图片根据大小进行decode,
     * 根据图片大小，如果图片<屏幕宽度和高度，1次decode,
     * 否则2次decode
     * @param path 图片路径
     * @return bitmap 图片对象 null:表示路径为空或decode图片失败
     */
    private Bitmap getBitmap(String path)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        
        BitmapFactory.decodeFile(path, options);
        //获得图片的宽度和高度
        int imgWidth = options.outHeight;
        int imgHeight = options.outHeight;
        //存储屏幕宽度和高度
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        Logger.d(TAG, "screenWidth:" + screenWidth + ",screenHeight:"
                + screenHeight);
        Logger.d(TAG, "imgWidth:" + imgWidth + ",imgHeight:" + imgHeight);
        
        int inSampleSize = 1;
        while (imgWidth > screenWidth || imgHeight > screenHeight)
        {
            imgWidth = imgWidth >> 1;
            imgHeight = imgHeight >> 1;
            inSampleSize = inSampleSize << 1;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Log.d(TAG, "getBitmap(),inSampleSize:" + inSampleSize);
        return BitmapFactory.decodeFile(path, options);
    }
    
    /**
    * 
    * 旋转图片<BR>
    * [功能详细描述]
    */
    private void rotateImage()
    {
        Log.d(TAG, "rotateImage()执行旋转图片");
        if (imageLoadStatus && null != mCropZoomView
                && null != mCropZoomView.getBitmap())
        {
            Bitmap resizeBitmap = ImageUtil.rotateBitmap(mCropZoomView.getBitmap(),
                    90);
            mCropZoomView.setImage(resizeBitmap);
        }
    }
    
    /**
    * 
    * 缩小图片<BR>
    * [功能详细描述]
    */
    private void zoomIn()
    {
        Log.d(TAG, "zoomIn()");
        if (imageLoadStatus && null != mZoomState)
        {
            float z = mZoomState.getZoom() + 0.25f;
            mZoomState.setZoom(z);
            mZoomState.notifyObservers();
        }
        
    }
    
    /**
    * 
    * 放大图片<BR>
    * [功能详细描述]
    */
    private void zoomOut()
    {
        Log.d(TAG, "zoomOut()");
        if (imageLoadStatus && null != mZoomState)
        {
            float z = mZoomState.getZoom() - 0.25f;
            if (z > 0)
            {
                mZoomState.setZoom(z);
                mZoomState.notifyObservers();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (null != mBitmapRef)
        {
            Bitmap bitmap = mBitmapRef.get();
            if (bitmap != null && !bitmap.isRecycled())
            {
                //GC回收bitmap对象
                bitmap.recycle();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.left_button:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.right_button:
                if (mode == CropImageAction.MODE_CROP)
                {
                    crop();
                }
                else if (mode == CropImageAction.MODE_SEND_VIEW)
                {
                    send();
                }
                else if (mode == CropImageAction.MODE_SAVE)
                {
                    saveImage(mNewPath, mImagePath);
                    
                }
                break;
            case R.id.btnRotation:
                rotateImage();
                break;
            case R.id.btnZoomIn:
                zoomIn();
                break;
            case R.id.btnZoomOut:
                zoomOut();
                break;
            default:
                break;
        }
        
    }
    
    /**
     * 
     * 点击保存按钮，把图片保存在新的目录下<BR>
     * 
     * @param newPath
     * @param ImagePath
     */
    private void saveImage(String newPath, String imagePath)
    {
        if (null == newPath || null == imagePath)
        {
            Logger.e(TAG, "saveImage failed. imagePath=" + imagePath
                    + " newPath=" + newPath);
            return;
            
        }
        if (FileUtil.copyFile(new File(imagePath), new File(newPath)))
        {
            showToast(getResources().getString(R.string.saved_path) + newPath);
        }
        
    }
    
    private void send()
    {
        //根据源图生成小图
        Bitmap bitmap = ImageUtil.getFitBitmap(mImagePath);
        //生成小图文件
        String thumbPicPath = ImageUtil.saveBitmap(bitmap);
        
        File thumbPic = new File(thumbPicPath);
        File srcPic = new File(mImagePath);
        
        Intent backToChat = new Intent();
        if (srcPic.length() > SIZE_MB)
        {
            if (thumbPic.length() > SIZE_MB)
            {
                //大图小图都>1MB
                showToast(R.string.image_is_bigger);
                mBtnSendOrSave.setEnabled(false);
            }
            else
            {
                //压缩过的图小于1MB,2个都发送小图
                backToChat.putExtra(FusionAction.CropImageAction.EXTRA_PATH,
                        ImageUtil.saveBitmap(bitmap));
                backToChat.putExtra(FusionAction.CropImageAction.EXTRA_SMALL_IMAGE_PATH,
                        thumbPicPath);
                setResult(RESULT_OK, backToChat);
                finish();
            }
        }
        else
        {
            backToChat.putExtra(FusionAction.CropImageAction.EXTRA_PATH,
                    mImagePath);
            backToChat.putExtra(FusionAction.CropImageAction.EXTRA_SMALL_IMAGE_PATH,
                    thumbPicPath);
            setResult(RESULT_OK, backToChat);
            finish();
        }
        
        if (null != bitmap && !bitmap.isRecycled())
        {
            bitmap.isRecycled();
            bitmap = null;
        }
    }
    
    private void crop()
    {
        Bitmap bitmap = mCropZoomView.cropImage();
        if (bitmap != null)
        {
            Intent intent = new Intent();
            ByteArrayOutputStream baos = null;
            try
            {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                if (!bitmap.isRecycled())
                {
                    bitmap.recycle();
                    bitmap = null;
                }
                byte[] data = baos.toByteArray();
                intent.putExtra("data", data);
                baos.close();
                baos = null;
                setResult(RESULT_OK, intent);
                finish();
            }
            catch (Exception e)
            {
                Logger.e(TAG, "crop()发生异常:" + e.toString());
            }
        }
    }
    
    /** 
     * onTouch事件<BR>
     * @param v View
     * @param event MotionEvent
     * @return super.onTouchEvent(event)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        mControlBar.setVisibility(mIsVisible ? View.INVISIBLE : View.VISIBLE);
        mTitleBar.setVisibility(mIsVisible ? View.INVISIBLE : View.VISIBLE);
        mIsVisible = !mIsVisible;
        return super.onTouchEvent(event);
    }
    
}
