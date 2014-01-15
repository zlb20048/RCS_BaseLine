/*
 * 文件名: LoginAdapterListener.java
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
 * 登录监听器
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Mar 13, 2012] 
 */
public interface ICallLoginListener
{
    
    /**
     * 登录成功回调
     * @param regId
     *      注册id
     * @param uri
     *      注册成功返回的Uri
     *      
     */
    void onLoginSuccessful(int regId, String uri);
    
    /**
     * 登录失败回调
     * @param regId
     *        注册id
     * @param errorCode
     *        失败的错误码
     */
    void onLoginFailure(int regId, int errorCode);
    
}
