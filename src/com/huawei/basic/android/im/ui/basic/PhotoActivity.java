
/*
 * 文件名: PhotoActivity.java 
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights
 * Reserved. 
 * 描 述: [该类的简要描述] 
 * 创建人: m00207912 
 * 创建时间:2012-4-23 
 * 修改人： 修改时间: 
 * 修改内容：[修改内容]
 */

package com.huawei.basic.android.im.ui.basic;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.utils.ImageUtil;
import com.huawei.basic.android.im.utils.SystemFacesUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 
 * 只需要传入头像的URL地址和的Facebytes，在入口处屏蔽不是的情况<BR>
 * 
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-4-23]
 */
public class PhotoActivity extends Activity
{
    /**
     * 定义TAG
     */
    public static final String TAG = "PhotoActivity";
    
    /**
     * 展示大图
     */
    public static final String PHOTO_LARGE = "photo_large";

    /**
     * 展示大图的url
     */
    public static final String PHOTO_LARGE_URL = "photo_large_url";

    private byte[] mFacebytes;

    private String mFaceUrl;

    private FrameLayout mFrameLayout;

    private ImageView mImageView;
    
    private ImageView mImg;

    private Bitmap mBitmap;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_large);
        // 初始化视图
        findview();
        // 插入数据
        setData();
    }

    /**
     * 初始化页面展示数据<BR>
     */
    private void setData()
    {
        mFaceUrl = getIntent().getStringExtra(PHOTO_LARGE_URL);
        mFacebytes = getIntent().getByteArrayExtra(PHOTO_LARGE);

        if (null != mFacebytes)
        {
            mBitmap = ImageUtil.bytes2Bimap(mFacebytes);
            if (null != mImageView)
            {
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setImageBitmap(mBitmap);
            }
        }
        else if (null != mFaceUrl && SystemFacesUtil.isSystemFaceUrl(mFaceUrl))
        {
            if (null != mImg)
            {
                mImg.setVisibility(View.VISIBLE);
                mImg.setImageResource(SystemFacesUtil
                    .getFaceImageResourceIdByFaceUrl(mFaceUrl));
            }
        }
        else
        {
            if (null != mImg)
            {
                mImg.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 初始化视图<BR>
     */
    private void findview()
    {
        mFrameLayout = (FrameLayout) findViewById(R.id.photo_large_ll);
        mImageView = (ImageView) findViewById(R.id.photo_large_img);
        mImg = (ImageView) findViewById(R.id.photo_large_img_sys);

        if (null != mFrameLayout)
        {
            mFrameLayout.setBackgroundColor(android.graphics.Color.BLACK);
            mFrameLayout.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish()
    {
        // 清空mBitmap数据
        if (null != mBitmap && !mBitmap.isRecycled())
        {
            mBitmap.recycle();
            mBitmap = null;
        }
        super.finish();
    }

}
