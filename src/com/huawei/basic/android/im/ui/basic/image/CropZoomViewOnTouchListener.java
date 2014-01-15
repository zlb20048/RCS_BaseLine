package com.huawei.basic.android.im.ui.basic.image;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 
 * cropview touch 监听[一句话功能简述]<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-4-23]
 */
public class CropZoomViewOnTouchListener implements OnTouchListener
{
    private static final String TAG = "CropZoomViewOnTouchListener";
    
    private static final int MOVE = 1;
    
    private float mX;
    
    private float mY;
    
    private ZoomState mZoomState;
    
    
    public void setZoomState(ZoomState zoomState)
    {
        this.mZoomState = zoomState;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int count;
        boolean status = true;
        count = event.getPointerCount();
        if (MOVE == count)
        {
            status = move(v, event);
        }
        
        return status;
    }
    
    private boolean move(View v, MotionEvent event)
    {
        Logger.d(TAG, "move");
        final float x;
        final float y;
        final int action;
        
        action = event.getAction();
        x = event.getX();
        y = event.getY();
        
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mX = x;
                mY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final float dx = (x - mX) / v.getWidth();
                final float dy = (y - mY) / v.getHeight();
                float moveX = mZoomState.getPanX() - dx;
                float moveY = mZoomState.getPanY() - dy;
                mZoomState.setPanX(moveX);
                mZoomState.setPanY(moveY);
                mZoomState.notifyObservers();
                mX = x;
                mY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }
    
}
