/*
 * 文件名: LocationDataListener.java
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

/**
 * 
 * 定位信息数据结果监听<BR>
 * [功能详细描述]
 * @author raulxiao
 * @version [RCS Client V100R001C03, Apr 17, 2012]
 */
public interface LocationDataListener
{
    /**
     * 
     * 定位结果监听<BR>
     * [功能详细描述]
     * @param result 位置信息
     */
    void onLocationResult(LocationInfo result);
    
    /**
     * 
     * 进度条监听<BR>
     * [功能详细描述]
     * @param show boolean
     */
    void onLocationProgress(boolean show);
    
}
