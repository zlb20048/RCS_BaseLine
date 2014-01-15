/*
 * 文件名: SoftKeyBoardDetectLinearLayout.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: fanniu
 * 创建时间:2012-3-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 隐藏软键盘<BR>
 * @author fanniu
 * @version [RCS Client V100R001C03, 2012-3-14] 
 */
public class SoftKeyBoardDetectLinearLayout extends LinearLayout
{
    
    /**
     * 视图的原始高度
     */
    private int mOriginHeight = 0;
    
    /**
     * construction
     * @param context context
     */
    public SoftKeyBoardDetectLinearLayout(Context context)
    {
        super(context);
    }
    
    /**
     * 
     * construction
     * @param context context
     * @param attrs attrs
     */
    public SoftKeyBoardDetectLinearLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    /**
     * 屏幕高度变化处理<BR>
     * @param w width
     * @param h height
     * @param oldw oldWidth
     * @param oldh oldHeight
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        if (mOriginHeight == 0)
        {
            mOriginHeight = h;
        }
        super.onSizeChanged(w, h, oldw, oldh);
        if ((mOriginHeight != h && mOriginHeight != oldh) || h > mOriginHeight
                || oldh > mOriginHeight)
        {
            return;
        }
        Context context = getContext();
        if (context instanceof SoftKeyBoardDetectListener)
        {
            SoftKeyBoardDetectListener listener = (SoftKeyBoardDetectListener) context;
            if (h < oldh)
            {
                // 软键盘弹出
                listener.onKeyBoardShown(true);
            }
            else if (h > oldh)
            {
                // 软键盘隐藏
                listener.onKeyBoardShown(false);
            }
        }
        
    }
    
    /**
     * 软键盘检测监听器
     */
    public interface SoftKeyBoardDetectListener
    {
        
        /**
         * 检测到软键盘事件
         *
         * @param shown 软键盘是否显示
         */
        void onKeyBoardShown(boolean shown);
    }
    
}
