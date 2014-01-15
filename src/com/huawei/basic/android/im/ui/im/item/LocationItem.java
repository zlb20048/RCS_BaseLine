/*
 * 文件名: LocationItem.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: lidan
 * 创建时间:2012-5-11
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.ui.im.ProgressModel;

/**
 * 地图位置显示Item<BR>
 * @author lidan
 * @version [RCS Client V100R001C03, 2012-5-11] 
 */
public class LocationItem extends BaseMsgItem
{
    /**
     * TAG
     */
    private static final String TAG = "LocationItem";
    
    /**
     * 地图图片     
     */
    private ImageView mLocationIV;
    
    /**
     * 位置文本
     */
    private TextView mLocationTV;
    
    /**
     * 放置地图图片的Layout
     */
    private LinearLayout mapLayout;
    
    /**
     * 地图下载进度Layout
     */
    private LinearLayout mPBLayout;
    
    /**
     * 显示地图下载进度文字
     */
    private TextView mPbTV;
    
    /**
     * 构造方法
     * @param holderEventListener HolderEventListener
     */
    public LocationItem(HolderEventListener holderEventListener)
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
        return inflater.inflate(R.layout.im_msg_item_location_paopao, null);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#initSubView(android.view.View)
     */
    @Override
    protected void initSubView(Context context, View view)
    {
        mLocationIV = (ImageView) view.findViewById(R.id.location_image);
        mLocationTV = (TextView) view.findViewById(R.id.location_text);
        mapLayout = (LinearLayout) view.findViewById(R.id.map_layout);
        mPBLayout = (LinearLayout) view.findViewById(R.id.pb_layout);
        mPbTV = (TextView) view.findViewById(R.id.pb_text);
    }
    
    /**
     * 更新视图<BR>
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
        getHolderEventListener().setMsgAsReaded(msg);
        Logger.i(TAG, "msg===" + msg.getMediaIndex().toString());
        // 通过msg获取到地图图片，位置信息展示
        Bitmap bm = null;
        String lo = msg.getMediaIndex().getLocationLon();
        String la = msg.getMediaIndex().getLocationLat();
        String desc = msg.getMediaIndex().getMediaAlt();
        Logger.i(TAG, "LO=" + lo + "la" + la + "desc" + desc);
        // 根据数据库是否有path，判断
        //下载图片
        if (msg.getMediaIndex().getMediaPath() == null)
        {
            Logger.i(TAG, "下载Map图片");
            getHolderEventListener().downLocationImage(msg);
            Logger.i(TAG, "progress==" + progress);
            if (progress != null)
            {
                mPBLayout.setVisibility(View.VISIBLE);
                mPbTV.setText(progress.getPercent() + "%");
            }
            else
            {
                mPBLayout.setVisibility(View.GONE);
            }
        }
        // 本地有path。直接生成bitmap 显示
        else
        {
            mPBLayout.setVisibility(View.GONE);
            bm = getHolderEventListener().getBitmap(msg.getMediaIndex()
                    .getMediaPath());
            if (bm != null)
            {
                mLocationIV.setImageBitmap(bm);
            }
            else
            {
                mLocationIV.setImageResource(R.drawable.no_map);
            }
            
        }
        if (desc == null)
        {
            mLocationTV.setText(R.string.postion_info);
        }
        else
        {
            mLocationTV.setText(desc);
        }
        mapLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logger.i(TAG, "MAP LOCATION CLICK.........");
                getHolderEventListener().onLocationImageClick(msg);
            }
        });
        
    }
}
