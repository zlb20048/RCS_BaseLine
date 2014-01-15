/*
 * 文件名: IConversationLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 会话界面逻辑操作
 * 创建人: 周雪松
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.im;

import java.util.List;

import com.huawei.basic.android.im.logic.model.ConversationModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;

/**
 * 
 * [一句话功能简述]<BR>
 * 会话模块接口
 * @author xuesongzhou
 * @version [RCS Client V100R001C03, Feb 23, 2012]
 */
public interface IConversationLogic
{
    
    /**
     * 加载所有的会话<BR>
     * [功能详细描述]
     * @return List<ConversationModel>
     */
    List<ConversationModel> loadAllConversations();
    
    
    /**
     * 加载给定数量的会话<BR>
     * 从会话表中加载给定数量的会话,会话条数取值范围
     * @param limit 给定的数量 limit>0
     * @return 实际加载的会话条数 0 <= 实际加载的条数 <= limit
     */
    List<ConversationModel> loadConversationsByLimit(int limit);
    /**
     * 
     * 获取会话列表未读消息数量<BR>
     * [功能详细描述]
     * @return 未读消息数量
     */
    int getUnReadCount();
    
    /**
     * 
     * 根据信息状态标识返回对应的字符串<BR>
     * [功能详细描述]
     * 
     * @param status 信息状态
     * @return string 字符串
     */
    String getTypeString(int status);
    
    /**
     * 
     * 根据消息id返回消息类型<BR>
     * [功能详细描述]
     * 
     * @param msgId msgId
     * @return string 字符串
     */
    String getMessageTypeString(String msgId);
    
    /**
     * 
     * 根据JID以及会话类型查找对应的对象<BR>
     * [功能详细描述]
     * 
     * @param jid jid
     * @param type 会话类型：<br>
     *            一对一聊天：1 <br>
     *            群聊：2 <br>
     *            多人会话(讨论组)：3 <br>
     *            群发消息：4
     * 
     * @return Object
     * 1V1聊天:返回聊天人的信息
     * 群组:群组对象
     */
    Object getContactInfoByJID(String jid, int type);
    
    /**
     * 
     * 删除给定的会话对象<BR>
     * @param conversationModel 会话对象
     * @return 删除记录的条数,返回值<0表示删除失败
     */
    int delete(ConversationModel conversationModel);
    
    /**
     * 根据Ids信息生成群组model<BR>
     * [功能详细描述]
     * @param friendIds 创建聊吧时的好友ids
     * @return GroupInfoModel
     */
    GroupInfoModel createConversationByFriendUserIds(String[] friendIds);
    
    /**
     * 
     * 把查到的会话重新排序<BR>
     * 根据查到的会话重新排序，在会话界面要求如果小秘书或找朋友小助手中未读消息>0要置顶
     * @param list 要排序的对象
     * @return list 排序过以后的结果
     */
    List<ConversationModel> sort(List<ConversationModel> list);
    
    /**
     * 
     * 清除所有会话<BR>
     * [功能详细描述]
     */
    void clearAllConversation();
}
