/*
 * 文件名: FeaturesActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionAction;
import com.huawei.basic.android.im.ui.basic.BasicActivity;

/**
 * 客户端注册完成后登录，软件特性介绍<BR>
 * [功能详细描述]
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Feb 16, 2012]
 */
public class FeaturesActivity extends BasicActivity implements
        OnGestureListener, OnTouchListener
{
    
    /**
     * 移动的最小距离
     */
    private static final int FLING_MIN_DISTANCE = 100;
    
    /**
     * ViewFlipper
     */
    private ViewFlipper mFlipper;
    
    /**
     * 手势监听
     */
    private GestureDetector mDetector;
    
    /**
     * 小块块
     */
    private ImageView[] mSwitch;
    
    // /**
    // * 小块块的图像
    // */
    // private int[] mSwitchImageResid;
    
    /**
     * 
     * Activity生命周期入口
     * 
     * @param savedInstanceState
     *            Bundle
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.features_welcome);
        // 关闭软键盘
        this.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mFlipper = (ViewFlipper) findViewById(R.id.flipper);
        mDetector = new GestureDetector(this);
        mFlipper.setOnTouchListener(this);
        mFlipper.setLongClickable(true);
        mFlipper.getChildCount();
        mFlipper.indexOfChild(mFlipper.getCurrentView());
        mSwitch = new ImageView[] {
                (ImageView) findViewById(R.id.switch_imageview_1),
                (ImageView) findViewById(R.id.switch_imageview_2),
                (ImageView) findViewById(R.id.switch_imageview_3),
                (ImageView) findViewById(R.id.switch_imageview_4) };
    }
    
    /**
     * 
     * 按下手势
     * 
     * @param e
     *            移动事件
     * @return 手势结果
     * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
     */
    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }
    
    /**
     * 
     * 滑动手势
     * 
     * @param e1
     *            起始落点
     * @param e2
     *            终止落点
     * @param velocityX
     *            x坐标
     * @param velocityY
     *            y坐标
     * @return 滑动结果
     * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent,
     *      android.view.MotionEvent, float, float)
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {
        
        // 左滑
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE)
        {
            if (mFlipper.getDisplayedChild() < mFlipper.getChildCount() - 1)
            {
                mFlipper.setInAnimation(this, R.anim.right_in);
                mFlipper.setOutAnimation(this, R.anim.left_out);
                mFlipper.showNext();
            }
            else
            {
                // 到达最后一个见面再次滑动即进入主界面并关闭本界面
                Intent intent = new Intent(FusionAction.MainTabction.ACTION);
                startActivity(intent);
                finish();
            }
        }
        // 右滑
        else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE)
        {
            if (mFlipper.getDisplayedChild() > 0)
            {
                mFlipper.setInAnimation(this, R.anim.left_in);
                mFlipper.setOutAnimation(this, R.anim.right_out);
                mFlipper.showPrevious();
            }
        }
        for (int i = 0; i < mFlipper.getChildCount(); i++)
        {
            if (i == mFlipper.getDisplayedChild())
            {
                mSwitch[i].setImageResource(R.drawable.features_welcome_p2);
            }
            else
            {
                mSwitch[i].setImageResource(R.drawable.features_welcome_p1);
            }
        }
        return false;
    }
    
    /**
     * 
     * 长按手势
     * 
     * @param e
     *            长按事件
     * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
     */
    @Override
    public void onLongPress(MotionEvent e)
    {
        
    }
    
    /**
     * 
     * 滚动手势
     * 
     * @param e1
     *            起始落点
     * @param e2
     *            结束落点
     * @param distanceX
     *            x坐标
     * @param distanceY
     *            y坐标
     * @return 移动结果
     * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent,
     *      android.view.MotionEvent, float, float)
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY)
    {
        return false;
    }
    
    /**
     * ShowPress
     * 
     * @param e
     *            移动事件
     * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
     */
    @Override
    public void onShowPress(MotionEvent e)
    {
        
    }
    
    /**
     * 
     * 单击手势事件
     * 
     * @param e
     *            手势事件
     * @return 单击结果
     * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return false;
    }
    
    /**
     * 
     * 界面触摸事件，将触摸事件交给手势监听
     * 
     * @param v
     *            view
     * @param event
     *            移动类型
     * @return true or false
     * @see android.view.View.OnTouchListener#onTouch(android.view.View,
     *      android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return mDetector.onTouchEvent(event);
    }
    
    /**
     * 
     * 按键事件处理
     * 
     * @param keyCode
     *            按键码
     * @param event
     *            按键事件
     * @return 按键结果
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //        if (keyCode == KeyEvent.KEYCODE_BACK)
        //        {
        //            Intent intent = new Intent(this, MainTabActivity.class);
        //            startActivity(intent);
        //            
        //            finish();
        //            return false;
        //        }
        //        
        //        return super.onKeyDown(keyCode, event);
        return false;
    }
}
