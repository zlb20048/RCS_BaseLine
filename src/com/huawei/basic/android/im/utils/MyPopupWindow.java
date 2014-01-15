/*
 * 文件名: ds.java 
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved. 
 * 描 述: [该类的简要描述] 
 * 创建人: 王媛媛
 * 创建时间:2012-2-15 
 * 修改人： 
 * 修改时间: 
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.basic.android.R;

/**
 * 
 * 用户输入出错的提示 * 
 * @author i0332
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class MyPopupWindow extends PopupWindow
{
    /**
     * 提示信息偏移左边5个像素
     */
    public static final int PADDING_LEFT = 5;

    /**
     * 提示信息偏移上边5个像素
     */
    public static final int PADDING_TOP = 6;

    /**
     * 提示信息偏移右边5个像素
     */
    public static final int PADDING_RIGHT = 5;

    /**
     * 提示信息偏移下边0个像素
     */
    public static final int PADDING_BOTTOM = 0;

    /**
     * 偏移底下-22像素
     */
    public static final int POPUP_PADDING_BOTTOM = -11;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 
     * 获取上下文
     * 
     * @param context Context
     */
    public MyPopupWindow(Context context)
    {
        super(context);
        this.context = context;
    }

    /**
     * 
     * 显示提示框
     * 
     * @param view 显示提示框的view
     * @param info 提示框提示信息
     */
    public void show(View view, String info)
    {
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);

        TextView text = new TextView(context);
        text.setText(info);
        text.setTextColor(context.getResources().getColor(R.color.popupwidow));
        text.setPadding(PADDING_LEFT,
            PADDING_TOP,
            PADDING_RIGHT,
            PADDING_BOTTOM);

        setContentView(text);
        setBackgroundDrawable(context
            .getResources()
                .getDrawable(R.drawable.popup_bg));

        setTouchInterceptor(new OnTouchListener()
        {
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
            {
                if (paramMotionEvent.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        showAsDropDown(view,
            PADDING_BOTTOM,
            POPUP_PADDING_BOTTOM);
    }

    /**
     * 
     * 判断提示框是否是显示
     * 
     * @return 提示框显示
     */
    public boolean isShow()
    {
        return super.isShowing();
    }

    
    /**
     * 
     * 关闭提示框 
     * @see android.widget.PopupWindow#dismiss()
     */
    public void dismiss()
    {
        super.dismiss();
    }
}
