/*
 * 文件名: BaseNotification.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import org.simpleframework.xml.Element;

/**
 * 响应通知类基类<BR>
 * 在此基类中，处理所有相同的标签，如error-code, jid 等等
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-14] 
 */
public abstract class BaseNotification
{
    /**
     * 发起者
     */
    @Element(name = "from", required = false)
    private String from;

    /**
     * 接受者
     */
    @Element(name = "to", required = false)
    private String to;

    @Element(name = "id", required = false)
    private String id;

    /**
     * error code
     */
    @Element(name = "error-code", required = false)
    private int errorCode = 0;

    /**
     * 服务器单词拼错
     */
    @Element(name = "error-reson", required = false)
    private String errorReason;
    
    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(int statusCode)
    {
        this.errorCode = statusCode;
    }

    public String getErrorReason()
    {
        return errorReason;
    }

    public void setErrorReason(String errorReason)
    {
        this.errorReason = errorReason;
    }
}
