/*
 * 文件名: LocationModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: RaulXiao
 * 创建时间:Apr 17, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.location;

import android.location.Location;

/**
 * 
 * 定位信息实体类<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Apr 17, 2012]
 */
public class LocationInfo
{
    /**
     * 位置经纬度
     */
    private Location location;
    
    /**
     * 具体的位置信息
     */
    private String addressInfo;
    
    public Location getLocation()
    {
        return location;
    }
    
    public void setLocation(Location location)
    {
        this.location = location;
    }
    
    public String getAddressInfo()
    {
        return addressInfo;
    }
    
    public void setAddressInfo(String addressInfo)
    {
        this.addressInfo = addressInfo;
    }
    
}
