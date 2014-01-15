/*
 * 文件名: MessageModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 27, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

/**
 * 1v1聊天信息记录表<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Feb 27, 2012] 
 */
public class MessageModel extends BaseMessageModel
{
    /**
     * serial 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 好友HiTalk ID
     */
    private String friendUserId;
    
    public String getFriendUserId()
    {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId)
    {
        this.friendUserId = friendUserId;
    }
}
