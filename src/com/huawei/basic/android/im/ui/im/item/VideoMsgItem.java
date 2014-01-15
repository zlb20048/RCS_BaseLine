/*
 * 文件名: VideoMsgItem.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-4-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im.item;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.ui.im.ProgressModel;

/**
 * 视频消息ITEM<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-9] 
 */
public class VideoMsgItem extends BaseMsgItem
{
    /**
     * TAG
     */
    private static final String TAG = "VideoMsgItem";
    
    /**
     * 视频消息的视频第一帧图片显示
     */
    private ImageView mMsgIV;
    /**
     * 视频蒙版
     */
    private View mMsgIvMask;
    
    private View mMsgIvButton;
    /**
     * 取消上传的ImageView
     */
    private ImageView mCancelUpload;
    
    /**
     * 上传进度
     */
    //    private TextView mImgUploadProgress;
    
    /**
     * [构造简要说明]
     * @param holderEventListener holderEventListener
     */
    public VideoMsgItem(HolderEventListener holderEventListener)
    {
        super(holderEventListener);
    }
    
    /**
     * 获取子视图<BR>
     * @param context context
     * @return View
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#getSubView(android.content.Context)
     */
    @Override
    protected View getSubView(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.im_msg_item_video_paopao, null);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#initSubView(android.view.View)
     */
    
    @Override
    protected void initSubView(Context context, View view)
    {
        mMsgIV = (ImageView) view.findViewById(R.id.html_image);
        mMsgIvButton = view.findViewById(R.id.html_button);
        mMsgIvMask = view.findViewById(R.id.html_image_mask);
        mCancelUpload=(ImageView) view.findViewById(R.id.cancle_vedio_upload);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#updateView(android.content.Context, android.database.Cursor)
     */
    
    @Override
    protected void updateView(final BaseMessageModel msg, Context context,
            Cursor cursor, ProgressModel progress, boolean isPlaying)
    {
        Bitmap bitmap = null;
        String thumbPath = msg.getMediaIndex().getMediaSmallPath();
        Logger.d(TAG, "video thumbPath is : " + thumbPath);
        if (thumbPath != null)
        {
            bitmap = getHolderEventListener().getBitmap(thumbPath);
        }
        
        if (bitmap != null)
        {
            mMsgIV.setImageBitmap(bitmap);
        }
        else
        {
            mMsgIV.setImageResource(R.drawable.receive_default_vedio);
        }
        
        int width = mMsgIV.getWidth();
        int height = mMsgIV.getHeight();
        
        mMsgIvButton.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (msg.getMsgStatus() != BaseMessageModel.MSGSTATUS_PREPARE_SEND
                        && msg.getMsgStatus() != BaseMessageModel.MSGSTATUS_BLOCK)
                {
                    getHolderEventListener().onVideoClick(msg);
                    getHolderEventListener().setMsgAsReaded(msg);
                }
            }
        });
        if (progress != null)
        {
            int total = (int) progress.getTotal();
            int finished = (int) progress.getFinished();
            mMsgIvMask.setVisibility(View.VISIBLE);
            mCancelUpload.setVisibility(View.VISIBLE);
            setImageSize(mMsgIvMask, width, height * (total - finished) / total);
        }
        else
        {
            mMsgIvMask.setVisibility(View.GONE);
            mCancelUpload.setVisibility(View.GONE);
        }
    }
    
    private void setImageSize(View view, int width, int height)
    {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
    
}
