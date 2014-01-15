/*
 * 文件名: SafeViewFlipper.java
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
import android.widget.ViewFlipper;

/**
 * 
 * 切换视图时可能出现java.lang.IllegalArgumentException: Receiver not registered:
 * android.widget.ViewFlipper <BR>
 * 在MOTO milestone手机侧滑键盘时crash的处理
 * @author Lidan
 * @version [RCS Client V100R001C03, 2012-2-13]
 */
public class SafeViewFlipper extends ViewFlipper
{
    /**
     * 构造方法
     * [构造简要说明]
     * @param context context
     */
    public SafeViewFlipper(Context context)
    {
        
        super(context);
        
    }
    /**
     * 构造方法
     * [构造简要说明]
     * @param context context
     * @param attrs attrs
     */
    public SafeViewFlipper(Context context, AttributeSet attrs)
    {
        
        super(context, attrs);
        
    }
    /**
     * 一个窗口的分离操作  onDetachedFromWindow()
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @see android.widget.ViewFlipper#onDetachedFromWindow()
     */
    @Override
    public void onDetachedFromWindow()
    {
        
        try
        {
            
            super.onDetachedFromWindow();
            
        }
        
        catch (IllegalArgumentException e)
        {
            
            stopFlipping();
        }
        
    }
    
}
