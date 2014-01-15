/*
 * 文件名: CountryItemModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周雪松
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

/**
 * 国家信息<BR>
 * 
 * @author 周雪松
 * @version [RCS Client V100R001C03, Mar 21, 2012]
 */
public class CountryItemModel extends BaseContactModel
{
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 国家英文名
     */
    private String enName;
    
    /**
     * 国家中文名
     */
    private String chName;
    
    /**
     * 国家码
     */
    private String countryCode;
    
    /**
     * 国家码各种名字的拼音首字母串，便于本地好友的快速搜索；例如：xl,ls,lgg
     */
    private String initialName;
    
    /**
     * 构造函数
     */
    public CountryItemModel()
    {
        super();
    }
    
    public String getInitialName()
    {
        return initialName;
    }
    
    public void setInitialName(String initialName)
    {
        this.initialName = initialName;
    }
    
    public String getEnName()
    {
        return enName;
    }
    
    public void setEnName(String enName)
    {
        this.enName = enName;
    }
    
    public String getChName()
    {
        return chName;
    }
    
    public void setChName(String chName)
    {
        this.chName = chName;
    }
    
    public String getCountryCode()
    {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }
    
}
