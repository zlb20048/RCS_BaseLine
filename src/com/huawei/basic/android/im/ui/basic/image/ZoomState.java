package com.huawei.basic.android.im.ui.basic.image;

import java.util.Observable;

/**
 * 
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-4-23]
 */
public class ZoomState extends Observable
{
    /**
     * 缩放比例
     */
    private float mZoom;
    /**
     * 画笔横坐标
     */
    private float mPanX;
    /**
     * 画笔纵坐标
     */
    private float mPanY;
    
    public float getPanX()
    {
        return mPanX;
    }

    public float getPanY()
    {
        return mPanY;
    }

    public float getZoom()
    {
        return mZoom;
    }
    
    /**
     * 
     * [用于设置画笔横坐标]<BR>
     * [功能详细描述]
     * 
     * @param panX 画笔横坐标
     */
    public void setPanX(float panX)
    {
        if (panX != mPanX)
        {
            mPanX = panX;
            setChanged();
        }
    }

    /**
     * 
     * [用于设置画笔纵坐标]<BR>
     * [功能详细描述]
     * 
     * @param panY 画笔纵坐标
     */
    public void setPanY(float panY)
    {
        if (panY != mPanY)
        {
            mPanY = panY;
            setChanged();
        }
    }

    /**
     * 
     * [用于设置缩放的比例]<BR>
     * [功能详细描述]
     * 
     * @param zoom 缩放比例
     */
    public void setZoom(float zoom)
    {
        if (zoom != mZoom)
        {
            mZoom = zoom;
            setChanged();
        }
    }
    
    /**
     * 
     * [获取缩放比例]<BR>
     * [功能详细描述]
     * 
     * @param aspectQuotient 缩放比例横坐标
     * @return 缩放比例
     */
    public float getZoomX(float aspectQuotient)
    {
        return Math.min(mZoom,
            mZoom * aspectQuotient);
    }

    /**
     * 
     * [用于获取缩放的纵坐标]<BR>
     * [功能详细描述]
     * 
     * @param aspectQuotient 纵坐标
     * @return 纵坐标
     */
    public float getZoomY(float aspectQuotient)
    {
        return Math.min(mZoom,
            mZoom / aspectQuotient);
    }
}
