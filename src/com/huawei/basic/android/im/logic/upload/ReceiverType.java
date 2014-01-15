/*
 * 文件名: ReceiverType.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-4-11
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.upload;

/**
 * 接收消息的用户账号类型，该字段仅用于广播消息附件上传。<BR>
 * 取值如下：<BR>
 * 1：用户账号（即沃友ID）。在发送点对点消息及群发消息时使用。<BR>
 * 2：群组ID。在发送多人会话及群组消息时使用<BR>
 * 3：所有好友。在发送好友广播时使用（该字段预留，目前暂未使用。）<BR>
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-11]
 */
public interface ReceiverType
{
    /**
     * 1：用户账号（即沃友ID）。在发送点对点消息及群发消息时使用。
     */
    int SINGLE_FRIEND = 1;
    
    /**
     * 2：群组ID。在发送多人会话及群组消息时使用
     */
    int GROUP = 2;
    
    /**
     * 3：所有好友。在发送好友广播时使用
     */
    int ALL_FRIEND = 3;
}
