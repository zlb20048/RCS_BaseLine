/*
 * 文件名: VoipAccount.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: Voip账号bean
 * 创建人: 刘鲁宁
 * 创建时间:Mar 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model.voip;

import java.util.Date;

/**
 * Voip账号bean
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 15, 2012] 
 */
public class VoipAccount
{
    /**
     * id
     */
    private int id;
    
    /**
     * 用户的sysId
     */
    private String ownerUserId;
    
    /**
     * voip账号名
     */
    private String account;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 创建时间
     */
    private Date createdDate;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getOwnerUserId()
    {
        return ownerUserId;
    }
    
    public void setOwnerUserId(String ownerUserId)
    {
        this.ownerUserId = ownerUserId;
    }
    
    public String getAccount()
    {
        return account;
    }
    
    public void setAccount(String account)
    {
        this.account = account;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public Date getCreatedDate()
    {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }
    
}
