/*
 * 文件名: QuickBar.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * 快捷条<BR>
 * [功能详细描述]
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-13]
 */
public class QuickBar extends ImageButton
{
    
    /**
     * 
     * 对按钮的按键监听<BR>
     * [功能详细描述]
     * @author Lidan
     * @version [RCS Client V100R001C03, 2012-2-13]
     */
    public interface OnLetterPressListener
    {
        /**
         * 
         * 放开按钮<BR>
         * [功能详细描述]
         */
        void onPressUp();
        
        /**
         * 
         * 按下按钮<BR>
         * [功能详细描述]
         * 
         * @param letter 当前按到的字母
         */
        void onPressDown(String letter);
    }
    
    /**
     * 快捷条中所有字符
     */
    private String[] mLetterArray;
    
    /**
     * 按钮监听器
     */
    private OnLetterPressListener mOnLetterPressListener;
    
    /**
     * [构造简要说明]
     * 
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle int
     */
    public QuickBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    /**
     * [构造简要说明]
     * 
     * @param context Context
     * @param attrs AttributeSet
     */
    public QuickBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    /**
     * [构造简要说明]
     * 
     * @param context Context
     */
    public QuickBar(Context context)
    {
        super(context);
    }
    
    /**
     * 设置快捷条对应的字母数组 <BR>
     * [功能详细描述]
     * 
     * @param letterArray 数组
     */
    public void setLetterArray(String[] letterArray)
    {
        mLetterArray = letterArray;
    }
    
    /**
     * 
     * 设置按钮按下监听器<BR>
     * [功能详细描述]
     * 
     * @param onLetterPressListener OnLetterPressListener
     */
    public void setOnLetterPressListener(
            OnLetterPressListener onLetterPressListener)
    {
        mOnLetterPressListener = onLetterPressListener;
    }
    
    /**
     * 
     * 触摸事件监听处理<BR>
     * [功能详细描述]
     * 
     * @param event MotionEvent
     * @return boolean
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // 松开时
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (mOnLetterPressListener != null)
            {
                mOnLetterPressListener.onPressUp();
            }
        }
        
        // 按下和滑动时
        else if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_MOVE)
        {
            
            // 计算出当前选中的字母索引
            int letterIndex = calcIndex(event.getY());
            
            // 当前选中的字母
            if (letterIndex < mLetterArray.length && letterIndex > -1)
            {
                String selectedLetter = mLetterArray[letterIndex];
                if (mOnLetterPressListener != null)
                {
                    mOnLetterPressListener.onPressDown(selectedLetter);
                }
            }
        }
        return super.onTouchEvent(event);
    }
    
    /**
     * 
     * 计算当前y坐标下对应的字母索引<BR>
     * [功能详细描述]
     * 
     * @param currentY 当前y坐标
     * @return 对应的字母索引
     */
    private int calcIndex(float currentY)
    {
        int height = getHeight();
        int letterCount = mLetterArray.length;
        
        return (int) (currentY / height * letterCount);
    }
}
