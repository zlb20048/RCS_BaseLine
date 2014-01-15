/*
 * 文件名: ImageItem.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: fanniu
 * 创建时间:2012-4-16
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.ui.im.ProgressModel;

/**
 * 图片发送与接收Item<BR>
 * @author fanniu
 * @version [RCS Client V100R001C03, 2012-4-16] 
 */
public class ImageItem extends BaseMsgItem
{
    
    /**
     * 图片内容     
     */
    private ImageView mMessagImageView;
    
    /**
     * 上传蒙版
     */
    private TextView mSmallImgMask;
    
    /**
     * 取消上传按钮
     */
    private ImageView mCancleUpload;
    
    /**
     * 构造方法
     * @param holderEventListener HolderEventListener
     */
    public ImageItem(HolderEventListener holderEventListener)
    {
        super(holderEventListener);
    }
    
    /**
     * 获得子视图<BR>
     * @param context context
     * @return View
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#getSubView(android.content.Context)
     */
    @Override
    protected View getSubView(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.im_msg_item_image_paopao, null);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#initSubView(android.view.View)
     */
    @Override
    protected void initSubView(Context context, View view)
    {
        mMessagImageView = (ImageView) view.findViewById(R.id.html_image);
        mSmallImgMask = (TextView) view.findViewById(R.id.small_image_mask);
        mCancleUpload = (ImageView) view.findViewById(R.id.cancle_upload);
    }
    
    /**
     * [一句话功能简述]<BR>
     * @param msg BaseMessageModel
     * @param context Context
     * @param cursor Cursor
     * @param progress ProgressModel
     *  @param isPlaying isPlaying
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#updateView(com.huawei.basic.android.im.logic.model.BaseMessageModel, android.content.Context, android.database.Cursor, com.huawei.basic.android.im.ui.im.ProgressModel)
     */
    @Override
    protected void updateView(final BaseMessageModel msg, Context context,
            Cursor cursor, ProgressModel progress, boolean isPlaying)
    {
        //1.设置小图
        String smallImgPath = msg.getMediaIndex().getMediaSmallPath();
        Bitmap bitmap = getHolderEventListener().getBitmap(smallImgPath);
        if (null != bitmap)
        {
            mMessagImageView.setImageBitmap(bitmap);
        }
        else
        {
            mMessagImageView.setImageResource(R.drawable.receive_default_image);
        }
        
        mMessagImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (msg.getMsgStatus() != BaseMessageModel.MSGSTATUS_PREPARE_SEND
                        && msg.getMsgStatus() != BaseMessageModel.MSGSTATUS_BLOCK)
                {
                    getHolderEventListener().onImageClick(msg);
                    getHolderEventListener().setMsgAsReaded(msg);
                }
            }
        });
        
        // 显示进度
        if (progress != null)
        {
            //2.上传进度的显示
            mSmallImgMask.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = mSmallImgMask.getLayoutParams();
            double lastHeight = mMessagImageView.getHeight()
                    - (mMessagImageView.getHeight() * progress.getPercent() * 0.01);
            
            params.height = (int) lastHeight;
            params.width = mMessagImageView.getWidth();
            mCancleUpload.setVisibility(View.VISIBLE);
            mSmallImgMask.setLayoutParams(params);
        }
        else
        {
            mSmallImgMask.setVisibility(View.GONE);
            mCancleUpload.setVisibility(View.GONE);
        }
        
    }
}
