/*
 * 文件名: SystemEventHolder.java
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huawei.basic.android.R;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.BaseMessageModel;
import com.huawei.basic.android.im.ui.im.ProgressModel;

/**
 * <BR>
 * 系统提示
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public class SystemEventItem extends BaseMsgItem
{
    /**
     * debug tag
     */
    private static final String TAG = "SystemEventItem";
    
    /**
     * 系统提示
     */
    private TextView mSystemEvent;
    
    /**
     * 构造函数
     * [构造简要说明]
     * @param holderEventListener HolderEventListener
     */
    public SystemEventItem(HolderEventListener holderEventListener)
    {
        super(holderEventListener);
    }
    
    /**
     * 更新View<BR>
     * @param context 上下文
     * @param cursor Cursor
     * @param msg BaseMessageModel
     * @param progress ProgressModel
     * @param isPlaying boolean
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#updateView(com.huawei.basic.android.im.logic.model.BaseMessageModel, android.content.Context, android.database.Cursor, com.huawei.basic.android.im.ui.im.ProgressModel, boolean)
     */
    @Override
    public void updateView(final BaseMessageModel msg, Context context,
            Cursor cursor, ProgressModel progress, boolean isPlaying)
    {
        // 消息内容
        String msgContent = msg.getMsgContent();
        Logger.i(TAG, msgContent);
        mSystemEvent.setText(msgContent);
        getHolderEventListener().setMsgAsReaded(msg);
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
        return null;
    }
    
    /**
     * <BR>
     * {@inheritDoc}
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#initSubView(android.view.View)
     */
    
    @Override
    protected void initSubView(Context context, View view)
    {
        mSystemEvent = (TextView) view.findViewById(R.id.event);
    }
    
    /**
     * {@inheritDoc}
     * @param context Context
     * @param cursor Cursor
     * @return
     * @see com.huawei.basic.android.im.ui.im.item.BaseMsgItem#getView(android.content.Context, android.database.Cursor)
     */
    
    @Override
    public View getView(Context context, Cursor cursor)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View systemView = inflater.inflate(R.layout.im_msg_item_sys_event, null);
        setTimeLineV(systemView.findViewById(R.id.time_line));
        setTimeLineTV((TextView) systemView.findViewById(R.id.time_line_text));
        initSubView(context, systemView);
        return systemView;
    }
    
}
