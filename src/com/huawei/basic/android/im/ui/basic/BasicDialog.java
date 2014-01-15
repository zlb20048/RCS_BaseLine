/*
 * 文件名: BasicDialog.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Apr 23, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huawei.basic.android.R;

/**
 * 自定义Dialog
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Apr 23, 2012]
 */
public class BasicDialog extends Dialog

{
    /**
     * 
     * [构造简要说明]
     * 
     * @param context
     *            context
     */
    public BasicDialog(Context context)
    {
        super(context);
    }
    
    /**
     * 
     * [构造简要说明]
     * 
     * @param context
     *            context
     * @param theme
     *            theme
     */
    public BasicDialog(Context context, int theme)
    {
        super(context, theme);
    }
    
    /**
     * 
     * 屏蔽menu
     * 
     * @param keyCode
     *            keyCode
     * @param event
     *            event
     * @return true
     * @see android.app.Dialog#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            super.openOptionsMenu();
        }
        else
        {
            super.onKeyDown(keyCode, event);
        }
        return true;
    }
    
    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder
    {
        
        /**
         * 上下文
         */
        private Context context;
        
        /**
         * 标题
         */
        private String mTitle;
        
        /**
         * 消息体
         */
        private String mgeText;
        
        /**
         * 确定按钮文字
         */
        private String mPositiveButtonText;
        
        /**
         * 取消按钮文字
         */
        private String mNegativeButtonText;
        
        /**
         * 消息体布局
         */
        private View contentView;
        
        /**
         * 提示图标
         */
        private int icon;
        
        /**
         * 列表数组
         */
        private String[] mStrArray;
        
        /**
         * 列表数组默认选择
         */
        private int mDefaltValue = -1;
        
        /**
         * (原来的代码是默认关闭的，现在由于邀请好友不是默认关闭的 ，
         * 所以加上这个属性，如果不单独设置这个属性对原来的代码没有任何影响，周庆龙修改) 点击确认和取消按钮时是否默认关闭对话框
         */
        private boolean autoClosed = true;
        
        /**
         * 列表按钮
         */
        private DialogInterface.OnClickListener mSingleChoiceClickListener;
        
        /**
         * 列表按钮
         */
        private DialogInterface.OnClickListener mChoiceClickListener;
        
        /**
         * 确定按钮监听
         */
        private DialogInterface.OnClickListener positiveButtonClickListener;
        
        /**
         * 取消按钮监听
         */
        private DialogInterface.OnClickListener negativeButtonClickListener;
        
        /**
         * 构造函数
         * 
         * @param context
         *            context
         */
        public Builder(Context context)
        {
            this.context = context;
        }
        
        /**
         * 设置消息体
         * 
         * @param message
         *            message
         * @return Builder
         */
        public Builder setMessage(String message)
        {
            this.mgeText = message;
            return this;
        }
        
        /**
         * 设置消息体
         * 
         * @param message message
         * @return Builder
         */
        public Builder setMessage(int message)
        {
            this.mgeText = (String) context.getText(message);
            return this;
        }
        
        /**
         * 设置标题
         * 
         * @param title title
         * @return Builder
         */
        public Builder setTitle(int title)
        {
            this.mTitle = (String) context.getText(title);
            return this;
        }
        
        /**
         * 设置标题
         * 
         * @param title title
         * @return Builder
         */
        public Builder setTitle(String title)
        {
            this.mTitle = title;
            return this;
        }
        
        /**
         * 设置标题图标
         * 
         * @param iconId
         *            iconId
         * @return Builder
         */
        public Builder setIcon(int iconId)
        {
            this.icon = iconId;
            return this;
        }
        
        /**
         * 设置自定义布局
         * 
         * @param v
         *            View
         * @return Builder
         */
        public Builder setContentView(View v)
        {
            this.contentView = v;
            return this;
        }
        
        /**
         * 
         * 设置自定义布局
         * 
         * @param strArray
         *            strArray
         * @param listener
         *            listener
         * @return Builder
         */
        public Builder setItems(String[] strArray,
                DialogInterface.OnClickListener listener)
        {
            this.mStrArray = strArray;
            this.mChoiceClickListener = listener;
            return this;
        }
        
        /**
         * 设置确定按钮
         * 
         * @param positiveButtonText
         *            positiveButtonText
         * @param listener
         *            listener
         * @return Builder
         */
        public Builder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listener)
        {
            this.mPositiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }
        
        /**
         * 设置确定按钮
         * 
         * @param positiveButtonText
         *            positiveButtonText
         * @param listener
         *            listener
         * @return Builder
         */
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener)
        {
            this.mPositiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }
        
        /**
         * 设置取消按钮
         * 
         * @param negativeButtonText
         *            negativeButtonText
         * @param listener
         *            listener
         * @return Builder
         */
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener)
        {
            this.mNegativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }
        
        /**
         * 设置取消按钮
         * 
         * @param negativeButtonText
         *            negativeButtonText
         * @param listener
         *            listener
         * @return Builder
         */
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener)
        {
            this.mNegativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
        
        /**
         * 
         * 设置单项选择列表
         * 
         * @param strArray
         *            strArray
         * @param defaltValue
         *            defaltValue
         * @param listener
         *            listener
         * @return Builder
         */
        public Builder setSingleChoiceItems(String[] strArray, int defaltValue,
                DialogInterface.OnClickListener listener)
        {
            this.mStrArray = strArray;
            this.mDefaltValue = defaltValue;
            this.mSingleChoiceClickListener = listener;
            return this;
        }
        
        /**
         * 
         * 创建 dialog
         * 
         * @return BasicDialog
         */
        public BasicDialog create()
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final BasicDialog dialog = new BasicDialog(context,
                    R.style.Translucent_NoTitle);
            View layout = inflater.inflate(R.layout.dialog, null);
            //            dialog.addContentView(layout, new LayoutParams(
            //                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            // 设置标题
            ((TextView) layout.findViewById(R.id.title)).setText(mTitle);
            // 设置提示图标
            ((ImageView) layout.findViewById(R.id.icon)).setImageResource(icon);
            // 设置确定按钮
            if (mPositiveButtonText != null)
            {
                //按钮父类布局可见
                layout.findViewById(R.id.button_layout)
                        .setVisibility(View.VISIBLE);
                //确定按钮可见
                ((Button) layout.findViewById(R.id.positiveButton)).setVisibility(View.VISIBLE);
                ((Button) layout.findViewById(R.id.positiveButton)).setText(mPositiveButtonText);
                
                ((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        if (positiveButtonClickListener != null)
                        {
                            positiveButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                        if (autoClosed)
                        {
                            dialog.dismiss();
                        }
                    }
                });
            }
            
            // 设置取消按鈕
            if (mNegativeButtonText != null)
            {
                //按钮父类布局可见
                layout.findViewById(R.id.button_layout)
                        .setVisibility(View.VISIBLE);
                //确定按钮可见
                ((Button) layout.findViewById(R.id.negativeButton)).setVisibility(View.VISIBLE);
                ((Button) layout.findViewById(R.id.negativeButton)).setText(mNegativeButtonText);
                
                ((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        if (negativeButtonClickListener != null)
                        {
                            negativeButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                        if (autoClosed)
                        {
                            dialog.dismiss();
                        }
                    }
                });
                
            }
            
            //如果消息体没有 设置为不可见
            if (mgeText != null)
            {
                ((LinearLayout) layout.findViewById(R.id.content)).setVisibility(View.VISIBLE);
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                
                TextView messageText = new TextView(context);
                messageText.setText(mgeText);
                messageText.setTextSize(16);
                messageText.setTextColor(R.color.dialog_bg);
                ((LinearLayout) layout.findViewById(R.id.content)).addView(messageText,
                        new LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
            }
            
            //如果自定义布局没有 设置为不可见
            if (contentView != null)
            {
                //设置自定义的布局
                ((LinearLayout) layout.findViewById(R.id.content)).setVisibility(View.VISIBLE);
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView,
                        new LayoutParams(LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));
            }
            
            //如果自定义布局为ListView没有 设置为不可见
            if (null != mStrArray && null == mPositiveButtonText
                    && null == mNegativeButtonText)
            {
                ((LinearLayout) layout.findViewById(R.id.list_content)).setVisibility(View.VISIBLE);
                ListView mSingleChoice = new ListView(context);
                ArrayAdapter<String> adapter;
                if (mDefaltValue == -1)
                {
                    adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_list_item_1, mStrArray);
                    mSingleChoice.setAdapter(adapter);
                    mSingleChoice.setOnItemClickListener(new OnItemClickListener()
                    {
                        
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                View view, int position, long id)
                        {
                            if (null != mChoiceClickListener)
                            {
                                mChoiceClickListener.onClick(dialog, position);
                            }
                            
                        }
                    });
                }
                //带有单选按钮的ListView
                else
                {
                    adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_list_item_single_choice,
                            mStrArray);
                    mSingleChoice.setAdapter(adapter);
                    //设置默认选择的单选框
                    mSingleChoice.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    mSingleChoice.setItemChecked(mDefaltValue, true);
                    
                    mSingleChoice.setOnItemClickListener(new OnItemClickListener()
                    {
                        
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                View view, int position, long id)
                        {
                            if (null != mSingleChoiceClickListener)
                            {
                                mSingleChoiceClickListener.onClick(dialog,
                                        position);
                                dialog.dismiss();
                            }
                            
                        }
                    });
                }
                ((LinearLayout) layout.findViewById(R.id.list_content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.list_content)).addView(mSingleChoice,
                        new LayoutParams(LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }
        
        public void setAutoClosed(boolean autoClosed)
        {
            this.autoClosed = autoClosed;
        }
        
        public boolean isAutoClosed()
        {
            return autoClosed;
        }
    }
    
}
