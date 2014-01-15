/*
 * 文件名: UploadType.java
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
 * 
 * 上传的文件类型<BR>
 * 本次操作的具体功能。取值如下：<BR>
 * 1：上传头像文件（包括个人头像、应用头像）<BR>
 * 2：上传多媒体消息附件<BR>
 * 3：上传机器人自动回复消息的多媒体附件<BR>
 * 4：上传好友广播消息附件<BR>
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-4-11]
 */
public interface UploadType
{
    /**
     * 1：上传头像文件（包括个人头像、应用头像）
     */
    int FACE = 1;
    
    /**
     * 2：上传多媒体消息附件
     */
    int MESSAGE = 2;
    
    /**
     * 3：上传机器人自动回复消息的多媒体附件
     */
    int ROBOT = 3;
    
    /**
     * 4：上传好友广播消息附件
     */
    int BROADCAST = 4;
}
