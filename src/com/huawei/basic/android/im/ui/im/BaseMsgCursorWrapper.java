/*
 * 文件名: MsgCursorWrapper.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-24
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.im;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.huawei.basic.android.im.logic.model.BaseMessageModel;

/**
 * <BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-24] 
 */
public abstract class BaseMsgCursorWrapper extends CursorWrapper
{
    /**
     * count
     */
    private int mCount;
    
    /**
     * [构造简要说明]
     * @param cursor Cursor
     */
    public BaseMsgCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param position position
     * @return super.moveToPosition((super.getCount() - count) + position);
     * @see android.database.CursorWrapper#moveToPosition(int)
     */
    @Override
    public boolean moveToPosition(int position)
    {
        return super.moveToPosition((super.getCount() - mCount) + position);
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return count
     * @see android.database.CursorWrapper#getCount()
     */
    @Override
    public int getCount()
    {
        return mCount;
    }
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param count count
     * @return this.count
     */
    public int setCount(int count)
    {
        this.mCount = super.getCount() > count ? count : super.getCount();
        return this.mCount;
    }
    
    /**
     * 
     * 判断此Cursor持有的记录数是否与父类相同<BR>
     * 
     * @return 是否同父类
     */
    public boolean sameAsSuper()
    {
        return this.mCount == super.getCount();
    }
    
    //TODO:从cursor中解析出BaseMessageModel对象,可以写两个MsgCursorWrapper的子类，
    // 分别实现1V1的消息解析和1VN的消息解析
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return BaseMessageModel
     */
    public abstract BaseMessageModel parseMsgModel();
}
