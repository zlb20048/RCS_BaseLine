/*
 * 文件名: TextHolder.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im.item;

import android.content.Context;

import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.ui.basic.ChatTextParser;
import com.huawei.basic.android.im.ui.im.ProgressModel;

/**
 * 文本类型消息展示HOLDER
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public class TextItem extends BaseMsgItem
{
    
    /**
     * Message内容
     */
    private TextView mMessagTextView;
    
    /**
     * 构造方法
     * @param holderEventListener HolderEventListener
     */
    public TextItem(HolderEventListener holderEventListener)
    {
        super(holderEventListener);
    }
    
    /**
     * 更新视图<BR>
     * @param msg BaseMessageModel
     * @param context context
     * @param cursor cursor
     * @param progress ProgressModel
     * @param isPlaying boolean
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#updateView(com.huawei.basic.android.im.logic.model.BaseMessageModel, android.content.Context, android.database.Cursor, com.huawei.basic.android.im.ui.im.ProgressModel, boolean)
     */
    @Override
    public void updateView(final BaseMessageModel msg, Context context,
            Cursor cursor, ProgressModel progress, boolean isPlaying)
    {
        //设置消息内容
        String msgContent = msg.getMsgContent();
        
        // 如果是小秘书特性引导，设置字体突出显示
        if (context.getResources()
                .getString(R.string.hitalk_person_info)
                .equals(msgContent)
                || context.getResources()
                        .getString(R.string.hitalk_person_group)
                        .equals(msgContent))
        {
            mMessagTextView.setText(Html.fromHtml(msgContent));
        }
        else
        {
            mMessagTextView.setText(ChatTextParser.getInstance(context)
                    .parseText(msgContent, context));
        }
        getHolderEventListener().setMsgAsReaded(msg);
        //处理TextView不响应长按事件
        mMessagTextView.setOnLongClickListener(null);
        mMessagTextView.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                getHolderEventListener().onTextClick(msg);
            }
        });
    }
    
    /**
     * 获取子视图<BR>
     * @param context context
     * @return View
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#getSubView(android.content.Context)
     */
    @Override
    protected View getSubView(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.im_msg_item_text_paopao, null);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#initSubView(android.view.View)
     */
    
    @Override
    protected void initSubView(Context context, View view)
    {
        mMessagTextView = (TextView) view.findViewById(R.id.msg_body);
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#msgCanBeCopy()
     */
    
    @Override
    protected boolean msgCanBeCopy()
    {
        return true;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#msgCanBeTransfer()
     */
    
    @Override
    protected boolean msgCanBeTransfer()
    {
        return true;
    }
    
}
