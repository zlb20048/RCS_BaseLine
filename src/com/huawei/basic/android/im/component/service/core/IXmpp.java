/*
 * 文件名: ILogin.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: IXmpp
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.service.core;

import com.huawei.basic.android.im.logic.model.AASResult;

/**
 * 存在于服务中的XMPP处理接口<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-2-10]
 */
public interface IXmpp
{
    
    /**
     * XMPP组件初始化
     * <BR>
     * 
     * @return 初始化结果
     */
    boolean init();
    
    /**
     * 在登录成功时注册XMPP相关服务
     * <BR>
     * 
     * @param aasresult AASResult
     */
    void register(AASResult aasresult);
    
    /**
     * 注销XMPP
     * @param userID 用户业务ID
     */
    void deregister(String userID);
    
    /**
     * 订阅 <BR>
     * 
     * @param comId 组件id
     * @param notifyId 订阅id
     * @return 订阅成功与否，0为成功
     */
    int subNotify(String comId, int notifyId);
    
    /**
     * 取消订阅 <BR>
     * 
     * @param comId 组件id
     * @param notifyId 订阅id
     * @return 取消订阅成功与否，0为成功
     */
    int unSubNotify(String comId, int notifyId);
    
    /**
     * 执行命令 <BR>
     * 
     * @param comId 组件id
     * @param cmdId 命令id
     * @param data 执行命令需上传的数据
     * @return 执行命令的结果，“0”为成功
     */
    String executeCommand(String comId, int cmdId, String data);
}
