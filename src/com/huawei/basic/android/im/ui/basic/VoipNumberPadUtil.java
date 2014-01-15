/*
 * 文件名: VoipCallingActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 通话界面
 * 创建人: zhoumi
 * 创建时间:2012-4-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.common.FusionMessageType.VOIPMessageType;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 号码盘工具类
 * 
 * @author zhoumi
 * @version [RCS Client V100R001C03, 2012-4-9]
 */
public class VoipNumberPadUtil
{
    /**
     * 0
     */
    private static final String ZERO = "0";
    
    /**
     * 1
     */
    private static final String ONE = "1";
    
    /**
     * 2
     */
    private static final String TWO = "2";
    
    /**
     * 3
     */
    private static final String THREE = "3";
    
    /**
     * 4
     */
    private static final String FOUR = "4";
    
    /**
     * 5
     */
    private static final String FIVE = "5";
    
    /**
     * 6
     */
    private static final String SIX = "6";
    
    /**
     * 7
     */
    private static final String SEVEN = "7";
    
    /**
     * 8
     */
    private static final String EIGHT = "8";
    
    /**
     * 9
     */
    private static final String NINE = "9";
    
    /**
     * #
     */
    private static final String JIN = "#";
    
    /**
     * 按钮*
     */
    private static final String STAR = "*";
    
    /**
     * 字符‘+’
     */
    private static final String ADD = "+";
    
    /**
     * 显示要拨打的号码
     */
    private TextView mInputEdt;
    
    /**
     * 所在的activity
     */
    private Activity mActivity;
    
    /**
     * 二次拨号按键输入
     */
    private Handler mKeyHandler;
    
    /**
     * 输入数字0的按钮
     */
    private ImageButton callBtnZero;
    
    /**
     * 输入数字1的按钮
     */
    private ImageButton callBtnOne;
    
    /**
     * 输入数字2的按钮
     */
    private ImageButton callBtnTwo;
    
    /**
     * 输入数字3的按钮
     */
    private ImageButton callBtnThree;
    
    /**
     * 输入数字4的按钮
     */
    private ImageButton callBtnFour;
    
    /**
     * 输入数字5的按钮
     */
    private ImageButton callBtnFive;
    
    /**
     * 输入数字6的按钮
     */
    private ImageButton callBtnSix;
    
    /**
     * 输入数字7的按钮
     */
    private ImageButton callBtnSeven;
    
    /**
     * 输入数字8的按钮
     */
    private ImageButton callBtnEight;
    
    /**
     * 输入数字9的按钮
     */
    private ImageButton callBtnNine;
    
    /**
     * 输入×按钮
     */
    private ImageButton callBtnStar;
    
    /**
     * #按钮
     */
    private ImageButton callBtnSharp;
    
    /**
     * 监听
     */
    private View.OnClickListener mClickListener = new OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_call_0:
                    appendString(ZERO);
                    break;
                case R.id.btn_call_1:
                    appendString(ONE);
                    break;
                case R.id.btn_call_2:
                    appendString(TWO);
                    break;
                case R.id.btn_call_3:
                    appendString(THREE);
                    break;
                case R.id.btn_call_4:
                    appendString(FOUR);
                    break;
                case R.id.btn_call_5:
                    appendString(FIVE);
                    break;
                case R.id.btn_call_6:
                    appendString(SIX);
                    break;
                case R.id.btn_call_7:
                    appendString(SEVEN);
                    break;
                case R.id.btn_call_8:
                    appendString(EIGHT);
                    break;
                case R.id.btn_call_9:
                    appendString(NINE);
                    break;
                case R.id.btn_call_star:
                    appendString(STAR);
                    break;
                case R.id.btn_call_jin:
                    appendString(JIN);
                    break;
                default:
                    break;
            }
        }
    };
    
    /**
     * 构造方法
     * 
     * @param activity
     *            所在的activity
     * @param inputEdt
     *            显示号码的控件
     * @param keyHandler
     *            二次拨号handler          
     */
    public VoipNumberPadUtil(Activity activity, TextView inputEdt,
            Handler keyHandler)
    {
        mActivity = activity;
        mInputEdt = inputEdt;
        mKeyHandler = keyHandler;
        initNumberButton();
    }
    
    /**
     * 初始化界面
     */
    private void initNumberButton()
    {
        //输入数字0的按钮
        callBtnZero = (ImageButton) mActivity.findViewById(R.id.btn_call_0);
        //短按是0
        callBtnZero.setOnClickListener(mClickListener);
        //长按是+
        callBtnZero.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                appendString(ADD);
                return true;
            }
        });
        
        //输入数字1的按钮
        callBtnOne = (ImageButton) mActivity.findViewById(R.id.btn_call_1);
        callBtnOne.setOnClickListener(mClickListener);
        //输入数字2的按钮
        callBtnTwo = (ImageButton) mActivity.findViewById(R.id.btn_call_2);
        callBtnTwo.setOnClickListener(mClickListener);
        //输入数字3的按钮
        callBtnThree = (ImageButton) mActivity.findViewById(R.id.btn_call_3);
        callBtnThree.setOnClickListener(mClickListener);
        //输入数字4的按钮
        callBtnFour = (ImageButton) mActivity.findViewById(R.id.btn_call_4);
        callBtnFour.setOnClickListener(mClickListener);
        //输入数字5的按钮
        callBtnFive = (ImageButton) mActivity.findViewById(R.id.btn_call_5);
        callBtnFive.setOnClickListener(mClickListener);
        //输入数字6的按钮
        callBtnSix = (ImageButton) mActivity.findViewById(R.id.btn_call_6);
        callBtnSix.setOnClickListener(mClickListener);
        //输入数字7的按钮
        callBtnSeven = (ImageButton) mActivity.findViewById(R.id.btn_call_7);
        callBtnSeven.setOnClickListener(mClickListener);
        //输入数字8的按钮
        callBtnEight = (ImageButton) mActivity.findViewById(R.id.btn_call_8);
        callBtnEight.setOnClickListener(mClickListener);
        //输入数字9的按钮
        callBtnNine = (ImageButton) mActivity.findViewById(R.id.btn_call_9);
        callBtnNine.setOnClickListener(mClickListener);
        //输入×按钮
        callBtnStar = (ImageButton) mActivity.findViewById(R.id.btn_call_star);
        callBtnStar.setOnClickListener(mClickListener);
        //#按钮
        callBtnSharp = (ImageButton) mActivity.findViewById(R.id.btn_call_jin);
        callBtnSharp.setOnClickListener(mClickListener);
    }
    
    /**
     * 在显示号码的框里增加显示
     * 
     * @param sub
     *            要增加的文字
     */
    private void appendString(String sub)
    {
        mInputEdt.append(sub);
        if (null != mKeyHandler)
        {
            Message msg = new Message();
            msg.what = VOIPMessageType.VOIP_REDIAL;
            sub = sub.replaceAll("\\*", "10").replaceAll("\\#", "11");
            msg.obj = sub;
            mKeyHandler.sendMessage(msg);
        }
    }
    
    /**
     * 设置按钮不可用
     * @param enabled 设置禁用
     */
    public void setBtnEnabled(boolean enabled)
    {
        callBtnEight.setEnabled(enabled);
        callBtnFive.setEnabled(enabled);
        callBtnFour.setEnabled(enabled);
        callBtnNine.setEnabled(enabled);
        callBtnOne.setEnabled(enabled);
        callBtnSeven.setEnabled(enabled);
        callBtnSharp.setEnabled(enabled);
        callBtnSix.setEnabled(enabled);
        callBtnStar.setEnabled(enabled);
        callBtnThree.setEnabled(enabled);
        callBtnTwo.setEnabled(enabled);
        callBtnZero.setEnabled(enabled);
    }
}
