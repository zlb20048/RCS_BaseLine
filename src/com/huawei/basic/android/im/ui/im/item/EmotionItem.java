/*
 * 文件名: EmotionItem.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2012-4-24
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im.item;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.ui.im.ProgressModel;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author zhaozeyang
 * @version [RCS Client V100R001C03, 2012-4-24] 
 */
public class EmotionItem extends ImageItem
{
    
    /**
     * 图片内容
     */
    private ImageView mMessagImageView;
    
    /**
     * [构造简要说明]
     * @param holderEventListener holderEventListener
     */
    public EmotionItem(HolderEventListener holderEventListener)
    {
        super(holderEventListener);
    }
    
    /**
     * 初始化子视图<BR>
     * @param context context
     * @param view view
     * @see com.huawei.basic.android.im.ui.im.item.ImageItem#initSubView(android.content.Context, android.view.View)
     */
    @Override
    protected void initSubView(Context context, View view)
    {
        mMessagImageView = (ImageView) view.findViewById(R.id.html_image);
    }
    
    /**
     *更新视图<BR>
     * @param msg BaseMessageModel
     * @param context context
     * @param cursor cursor
     * @param progress ProgressModel
     * @param isPlaying boolean
     * @see com.huawei.basic.android.im.ui.im.item.ImageItem#updateView(com.huawei.basic.android.im.logic.model.BaseMessageModel, android.content.Context, android.database.Cursor, com.huawei.basic.android.im.ui.im.ProgressModel, boolean)
     */
    @Override
    protected void updateView(BaseMessageModel msg, Context context,
            Cursor cursor, ProgressModel progress, boolean isPlaying)
    {
        mMessagImageView.setImageBitmap(getHolderEventListener().getEmojBitmap(msg.getMediaIndex()
                .getMediaSmallPath()));
        getHolderEventListener().setMsgAsReaded(msg);
    }
    
}
