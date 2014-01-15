/*
 * 文件名: ILoginServiceListener.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.app;

import com.huawei.basic.android.im.logic.model.AASResult;

/**
 * 登录模块service监听器<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-14] 
 */
public interface ILoginServiceListener
{
    /**
     * 
     * LOGIN模块发送消息<BR>
     * 
     * @param messageType 消息类型
     * @param result 结果
     */
    void sendLoginMessage(int messageType, String result);
    
    /**
     * 
     * 登录模块的成功回调函数<BR>
     * 
     * @param aasResult AAS鉴权结果
     */
    void loginSuccessCallback(AASResult aasResult);
}
