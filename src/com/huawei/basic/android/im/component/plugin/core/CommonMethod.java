/*
 * 文件名: CommonMethod.java
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
 * [一句话功能简述]<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Apr 23, 2012] 
 */
@Root(name = "common-method", strict = false)
public abstract class CommonMethod extends BaseMethod
{
    /**
     * 调用<BR>
     */
    public abstract void invoke();
}
