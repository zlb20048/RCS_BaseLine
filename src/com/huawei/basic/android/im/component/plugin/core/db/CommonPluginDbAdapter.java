/*
 * 文件名: CommonPluginDbAdapter.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 21, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core.db;

import android.content.Context;

/**
 * 通用数据库操作<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 21, 2012] 
 */
public class CommonPluginDbAdapter extends BasePluginDbAdapter
{
    
    private static CommonPluginDbAdapter sCommonPluginDbAdapter;
    
    /**
     * 获取CommonPluginDbAdapter的实例对象<BR>
     * @return CommonPluginDbAdapter的实例
     */
    public static CommonPluginDbAdapter getInstance()
    {
        if (null == sCommonPluginDbAdapter)
        {
            sCommonPluginDbAdapter = new CommonPluginDbAdapter();
        }
        return sCommonPluginDbAdapter;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Context context)
    {
        this.setmContext(context);
        this.setmCr(context.getContentResolver());
    }
    
}
