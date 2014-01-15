/*
 * 文件名: SwitchMethod.java
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

import org.simpleframework.xml.Root;

/**
 * 开关方法<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 23, 2012] 
 */
@Root(name = "switch-method", strict = false)
public abstract class SwitchMethod extends BaseMethod
{
    /**
     * 获取开关当前的状态<BR>
     * @return 开关状态
     */
    public abstract boolean getStatus();
    
    /**
     * 改变状态<BR>
     * @param flag 标记位
     */
    public abstract void invoke(boolean flag);
}
