/*
 * 文件名: BaseMethod.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Apr 23, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.plugin.core;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.content.Context;

/**
 * [一句话功能简述]<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 23, 2012] 
 */
@Root(name = "method")
public abstract class BaseMethod
{
    
    private Context context;
    
    @Element(name = "name", required = false)
    private String name;
    
    @Element(name = "desc", required = false)
    private String desc;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDesc()
    {
        return desc;
    }
    
    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public Context getContext()
    {
        return context;
    }
    
}
