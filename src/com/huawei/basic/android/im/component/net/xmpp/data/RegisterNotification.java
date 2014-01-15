/*
 * 文件名: RegisterNotification.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 注册通知<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-18]
 */
@Root(name = "reg", strict = false)
public class RegisterNotification extends BaseNotification
{
    
    @Element(name = "time-stamp", required = false)
    private String timeStamp;
    
    public String getTimeStamp()
    {
        return timeStamp;
    }
    
    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }
    
}
