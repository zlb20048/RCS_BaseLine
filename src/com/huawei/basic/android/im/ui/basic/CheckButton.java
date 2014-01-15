/*
 * 文件名: VoipCallingActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 响应点击的Button
 * 创建人: zhoumi
 * 创建时间:2012-3-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

package com.huawei.basic.android.im.ui.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 响应点击的Button
 * 
 * @author zhoumi
 * @version [RCS Client V100R001C03, 2012-3-20]
 */
public class CheckButton extends Button
{
    /**
     * 是否被点击
     */
    private boolean isChecked = false;
    
    /**
     * [构造简要说明]
     * 
     * @param context context
     */
    public CheckButton(Context context)
    {
        super(context);
    }
    
    /**
     * [构造简要说明]
     * 
     * @param context context
     * @param attrs attrs
     */
    public CheckButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    /**
     * [构造简要说明]
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public CheckButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    /**
     * 是否点击
     * @return 是否点击
     */
    public boolean isChecked()
    {
        return isChecked;
    }
    
    /**
     * 设置点击事件
     */
    public void setChecked()
    {
        isChecked = !isChecked;
    }
    
    /**
     * 设置按钮的Enabled属性
     * @param enabled 是否Enabled
     * @param drawableId 资源Id
     */
    public void setEnabled(boolean enabled, int drawableId)
    {
        setEnabled(enabled);
        setBackgroundResource(drawableId);
    }
    
    /**
     * 切换点击/被点击时的Drawable
     * @param isCheckedDrawableId 被点击时的Drawable
     * @param unCheckedDrawable 未被点击时的Drawable
     */
    public void drawDrawable(int isCheckedDrawableId, int unCheckedDrawable)
    {
        setChecked();
        int drawableId = isChecked ? isCheckedDrawableId : unCheckedDrawable;
        setBackgroundResource(drawableId);
    }
}
