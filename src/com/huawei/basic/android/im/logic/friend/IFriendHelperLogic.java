/*
 * 文件名: IFriendHelperLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: qlzhou
 * 创建时间:Feb 22, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.friend;

import java.util.ArrayList;

import com.huawei.basic.android.im.logic.model.FriendManagerModel;

/**
 * 找朋友小助手逻辑接口<BR>
 * @author qlzhou
 * @version [RCS Client V100R001C03, Feb 22, 2012] 
 */
public interface IFriendHelperLogic
{
    /**
     * 加好友<BR>
     * @param friendUserId 接受者的hitalkId
     * @param nickName 接受者的昵称
     * @param reason 验证信息
     * @param faceUrl 好友的头像
     */
    void addFriend(String friendUserId, String nickName, String reason,
            String faceUrl);
    
    /**
     * 验证好友<BR>
     * @param friendUserId 接受者的hitalkId
     * @param isAgree 同意拒绝
     */
    void doAuth(String friendUserId, boolean isAgree);
    
    /**
     * 删除好友<BR>
     * @param friendUserId 接受者的hitalkId
     */
    void deleteFriend(String friendUserId);
    
    /**
     * 
     * 找朋友小助手数据本地查询<BR>
     * 群组小助手用
     * @param userSysId 用户sysid
     * @param subService subService
     * @param friendUserId friendUserId
     * @param groupId groupId
     * @return FriendManagerModel
     */
    FriendManagerModel getFriendManagerFromDB(String userSysId, int subService,
            String friendUserId, String groupId);
    
    /**
     * 删除找朋友小助手记录<BR>
     * @param friendUserId hitalkId
     * @param subService 数据类型
     * @param groupJid 群组的jid
     * @return 删除结果
     */
    boolean deleteFriendManagerByFriendUserId(String friendUserId,
            int subService, String groupJid);
    
    /**
     * 
     * 通过hiTalkID监听数据库单条好友信息<BR>
     * 
     * @param hiTalkID hiTalkID
     */
    void registerObserverByID(final String hiTalkID);
    
    /**
     * 
     * 通过hiTalkID移除数据库单条好友信息监听<BR>
     * 
     * @param hiTalkID hiTalkID
     */
    void unRegisterObserverByID(String hiTalkID);
    
    /**
     * 分页查询数据，分页大小为20条，第一页的page = 1<BR>
     * @param page 页数
     * @return 数据列表
     */
    ArrayList<FriendManagerModel> queryByPage(int page);
    
    /**
     * 清空当前用户未读的找朋友小助手信息<BR>
     * @return 是否成功
     */
    boolean clearFriendManagerUnreadMessages();
    
    /**
     * 清空找朋友小助手信息
     * @return 是否清空成功
     */
    boolean clearFriendManagerMessages();
}
