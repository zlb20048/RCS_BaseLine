/*
 * 文件名: RichEditText.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描 述: 富文本框实现
 * 创建人: 马波
 * 创建时间: 2012-03-05
 *
 */
package com.huawei.basic.android.im.ui.basic;

import java.util.regex.Matcher;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

/**
 * 
 * 富文本框实现<BR>
 * 
 * @author 马波
 * @version [RCS Client V100R001C03, 2012-2-25]
 */
public class RichEditText extends LimitedEditText
{
    /**
     * 文本观察者
     */
    private RichEditTextWatcher mTextWatcher;
    
    /**
     *  是否已装载文本观察器
     */
    private boolean mLoadedTextWatcher = false;
    
    /**
     * constructor()
     *
     * @param context context
     */
    public RichEditText(Context context)
    {
        super(context);
        loadTextWatcher();
    }
    
    /**
     * constructor()
     *
     * @param context context
     * @param attrs attrs
     */
    public RichEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        loadTextWatcher();
    }
    
    /**
     * constructor()
     *
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public RichEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        loadTextWatcher();
    }
    
    /**
     * 装载文本观察者
     */
    protected void loadTextWatcher()
    {
        //文本观察者已装载则直接返回
        if (mLoadedTextWatcher)
        {
            return;
        }
        //如果伪装在则重新装载
        if (mTextWatcher == null)
        {
            mTextWatcher = new RichEditTextWatcher(getContext());
        }
        //设置标志位true
        mLoadedTextWatcher = true;
        //添加监听
        addTextChangedListener(mTextWatcher);
    }
    
    /**
     * 卸载文本观察者
     */
    protected void unloadTextWatcher()
    {
        if (!mLoadedTextWatcher)
        {
            return;
        }
        if (mTextWatcher == null)
        {
            return;
        }
        removeTextChangedListener(mTextWatcher);
        mLoadedTextWatcher = false;
    }
    
    /**
     * 文本观察者<BR>
     * @author fanniu
     * @version [RCS Client V100R001C03, 2012-3-19]
     */
    private static class RichEditTextWatcher implements
            android.text.TextWatcher
    {
        /**
         * 表情标志
         */
        private boolean mIsExpression = false;
        
        /**
         * 文本开端和尾端
         */
        private int mStart, mEnd;
        
        /**
         * 附加标志
         */
        private boolean mReAppend = false;
        
        /**
         * 替换文本
         */
        private String mReplace;
        
        /**
         * 继续标志
         */
        private boolean willContinue = true;
        
        /**
         * 场景成员变量
         */
        private Context mContext;
        
        /**
         * [构造简要说明]
         * @param context context
         */
        private RichEditTextWatcher(Context context)
        {
            this.mContext = context;
        }
        
        /**
         * beforeTextChanged<BR>
         * @param charSequence  charSequence
         * @param start start
         * @param count count
         * @param after after
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start,
                int count, int after)
        {
            if (!willContinue)
            {
                return;
            }
            
            mIsExpression = false;
            if (count == 1 && after == 0)
            {
                String cut = String.valueOf(charSequence).substring(0,
                        start + 1);
                int index = cut.lastIndexOf('[');
                if (index > -1)
                {
                    String subCut = cut.substring(index);
                    if (ChatTextParser.getInstance(mContext).getEmotionPattern()
                            .matcher(subCut)
                            .matches())
                    {
                        // 为表情
                        mIsExpression = true;
                        mStart = index;
                        mEnd = start;
                    }
                }
            }
        }
        
        /**
         * onTextChanged<BR>
         * @param s s
         * @param start start
         * @param before    before
         * @param count count
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            if (!willContinue)
            {
                return;
            }
            mReAppend = false;
            if (count > 0)
            {
                mReplace = String.valueOf(s).substring(start, start + count);
                Matcher matcher = ChatTextParser.getInstance(mContext).getEmotionPattern()
                        .matcher(mReplace);
                if (matcher.find())
                {
                    mReAppend = true;
                    mStart = start;
                }
            }
        }
        
        /**
         * afterTextChanged<BR>
         * @param s s
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        @Override
        public void afterTextChanged(Editable s)
        {
            if (mIsExpression)
            {
                s.delete(mStart, mEnd);
                mIsExpression = false;
            }
            else if (mReAppend)
            {
                willContinue = false;
                mReAppend = false;
                s.delete(mStart, mStart + mReplace.length());
                s.insert(mStart, ChatTextParser.getInstance(mContext).parseText(mReplace, mContext));
                willContinue = true;
            }
        }
    }
    
    /**
     * 设置最大长度
     * @param maxCharLength 最大长度
     * @see com.huawei.basic.android.im.ui.basic.LimitedEditText#setMaxCharLength(int)
     */
    
    @Override
    public void setMaxCharLength(int maxCharLength)
    {
        // TODO Auto-generated method stub
        super.setMaxCharLength(maxCharLength);
    }
    
}
