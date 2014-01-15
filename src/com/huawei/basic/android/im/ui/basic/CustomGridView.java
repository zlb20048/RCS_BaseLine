/*
 * 文件名: CustomGridView.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: raulxiao
 * 创建时间:Apr 5, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.ui.basic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 
 * GridView<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Apr 5, 2012]
 */
public class CustomGridView extends GridView
{
    /**
     * 
     * [构造简要说明]
     * @param context Context
     * @param attrs AttributeSet
     */
    public CustomGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
        
        getLayoutParams().height = getMeasuredHeight();
    }
}