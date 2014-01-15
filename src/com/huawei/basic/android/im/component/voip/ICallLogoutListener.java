/*
 * 文件名: LogoutAdapterListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Mar 13, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.voip;

/**
 * 登出监听器
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public interface ICallLogoutListener
{
    
    /**
     * 登出成功回调
     * @param regId
     *      注册id
     * @param uri
     *      登出成功返回的Uri
     */
    void onLogoutSuccessful(int regId, String uri);
    
    /**
     * 登出失败回调
     * @param regId
     *      注册id
     * @param errorcode
     *      失败的错误码
     */
    void onLogoutFailure(int regId, int errorcode);
    
}
