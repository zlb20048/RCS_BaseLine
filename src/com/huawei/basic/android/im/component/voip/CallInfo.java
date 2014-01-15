/*
 * 文件名: CallInfo.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

/**
 * 通话用戶信息
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 15, 2012] 
 */
public class CallInfo
{
    /**
     * 电话号码
     */
    private String phoneNum;
    
    /**
     * voip的Uri
     */
    private String uri;
    
    /**
     * displayName
     */
    private String displayName;
    
    protected String getPhoneNum()
    {
        return phoneNum;
    }
    
    protected void setPhoneNum(String phoneNum)
    {
        this.phoneNum = phoneNum;
    }
    
    protected String getUri()
    {
        return uri;
    }
    
    protected void setUri(String uri)
    {
        this.uri = uri;
    }
    
    protected String getDisplayName()
    {
        return displayName;
    }
    
    protected void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
}
