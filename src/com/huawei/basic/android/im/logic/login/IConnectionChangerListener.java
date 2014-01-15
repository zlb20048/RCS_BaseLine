/*
 * 文件名: IConnectionChangerListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Mar 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.login;

/**
 * 监听网络变更
 * @author tlmao
 * @version [RCS Client V100R001C03, Mar 16, 2012] 
 */
public interface IConnectionChangerListener
{
    /**
     * 
     * 当前网络是WIFI回调<BR>
     * [功能详细描述]
     */
    void wifiCalback();
    
    /**
     * 
     * 当前网络是NET回调<BR>
     * [功能详细描述]
     */
    void netCallback();
    
    /**
     * 
     * 当前网络是WAP回调<BR>
     * [功能详细描述]
     */
    void wapCallback();
    
    /**
     * 
     * 当前无网络回调<BR>
     * [功能详细描述]
     */
    void noNetworkCallback();
    
}
