/*
 * 文件名: IGroupLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 群组相关的Logic 对外提供的接口
 * 创建人: tjzhang
 * 创建时间:2012-3-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.group;

import java.util.List;
import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.GroupInfoModel;
import com.huawei.basic.android.im.logic.model.GroupMemberModel;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author tjzhang
 * @version [RCS Client V100R001C03, 2012-3-9] 
 */
public interface IGroupLogic
{
    /**
     * 
     * 根据模式从数据库中获取对应的群列表<BR>
     * [功能详细描述]
     * @param mode 群还是多人会话
     * @return 群list
     */
    List<GroupInfoModel> getGroupListFormDB(int mode);
    
    /**
     * 
     * 对群组进行排序<BR>
     * [功能详细描述]
     * @param list 群组List
     */
    void sortGroup(List<GroupInfoModel> list);
    
    /**
     * 
     * 过滤在List中已经存在的好友<BR>
     * [功能详细描述]
     * @param beforeHadList 已经存在的list
     * @return List<ContactInfoModel>
     */
    List<ContactInfoModel> filterContact(List<GroupMemberModel> beforeHadList);
    
    /**
     * 
     * 获取群分类搜索的标题<BR>
     * [功能详细描述]
     * @return 标题的List
     */
    List<String> getCategroyTitles();
    
    /**
     * 
     * 根据分类模式获取分类的类型<BR>
     * [功能详细描述]
     * @param mode 分类模式
     * @return String 分类类型字符串
     */
    String getCategroyType(int mode);
    
    /**
     * 
     * 根据群组类型获取验证标题<BR>
     * [功能详细描述]
     * @param mode 群组类型：受限还是开放的
     * @return String 验证标题字符串
     */
    String getValidate(int mode);
    
    /**
     * 对群成员进行排序
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param list 成员list
     */
    void sortMember(List<GroupMemberModel> list);
    
    /**
     * 从xmpp服务器获取群组列表数据<BR>
     * 
     */
    void getGroupListFromXmpp();
    
    /**
     * 
     * 创建群组<BR>
     * [功能详细描述]
     * @param model GroupInfoModel
     * @param from 从哪个页面调用此方法
     */
    void createGroup(GroupInfoModel model, int from);
    
    /**
     * 
     * 带上成员ids创建群组<BR>
     * [功能详细描述]
     * @param ids 创建聊吧时需要邀请的成员Ids
     * @param model 群组model
     * @param from 从哪个页面调用此方法
     */
    void createGroupByIds(String[] ids, GroupInfoModel model, int from);
    
    /**
     * 
     * 邀请好友加入加入群组或者多人会话<BR>
     * [功能详细描述]
     * @param ids 好友的ids
     * @param groupId 群组ID
     * @param from 从哪个页面调用此方法
     */
    void inviteMember(String[] ids, String groupId, int from);
    
    /**
     * 
     * 从群众或多人会话中删除成员<BR>
     * [功能详细描述]
     * @param memberId 被删除成员的id
     * @param groupId 群组ID
     * @param from 从哪个页面调用此方法
     */
    void removeMember(String memberId, String groupId, int from);
    
    /**
     * 
     * 接受群主邀请，加入该群<BR>
     * [功能详细描述]
     * @param to 群组ID
     */
    void acceptInvite(String to);
    
    /**
     * 
     * 拒绝邀请加入群<BR>
     * [功能详细描述]
     * @param to 拒绝的群组ID
     * @param reason 拒绝原因
     * @param declineTo 被拒绝人的id
     */
    void declineInvite(String to, String reason, String declineTo);
    
    /**
     * 
     * 申请加入群组<BR>
     * [功能详细描述]
     * @param reason 申请加入的原因
     * @param gim 群组详情对象
     */
    void joinGroup(String reason, GroupInfoModel gim);
    
    /**
     * 同意加入群申请
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupId 群组ID
     * @param joiningUserJid 申请userId
     * @param affiliation 在群组中的角色
     */
    void acceptJoin(String groupId, String joiningUserJid, String affiliation);
    
    /**
     * 
     * 拒绝加入群申请<BR>
     * [功能详细描述]
     * @param groupJid 群组ID
     * @param joiningUserJid 申请者的id
     * @param reason 拒绝的原因
     */
    void declineJoin(String groupJid, String joiningUserJid, String reason);
    
    /**
     * 
     * 根据分类搜索群组<BR>
     * [功能详细描述]
     * @param pageID pageID
     * @param pageSize  pageSize
     * @param searchKey 分类的key
     */
    void searchGroupByCategory(int pageID, int pageSize, String searchKey);
    
    /**
     * 
     * 根据输入关键字搜索群组<BR>
     * [功能详细描述]
     * @param pageID pageID
     * @param pageSize pageSize
     * @param searchKey 搜索关键字
     */
    void searchGroupByKey(int pageID, int pageSize, String searchKey);
    
    /**
     * 
     * 获取群组配置信息<BR>
     * [功能详细描述]
     * @param groupJid groupJid
     */
    void getConfigInfo(String groupJid);
    
    /**
     * 根据群组id获取群成员数目
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupId 群组ID
     * @return 成员数量
     */
    int getMemberListCount(String groupId);
    
    /**
     * 判断创建的群组是否已经达到上限
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return boolean
     */
    boolean hasGroupReachMaxNumber();
    
    /**
     * 
     * 注册监听群组信息表<BR>
     * [功能详细描述]
     */
    void registerGroupInfoObserver();
    
    /**
     * 
     * 根据群组ID注册监听群组信息表中的某一条记录<BR>
     * [功能详细描述]
     * @param groupId 群组ID
     */
    void registerGroupInfoByIdObserver(String groupId);
    
    /**
     * 
     * 注册监听群组成员表<BR>
     * [功能详细描述]
     */
    void registerGroupmemberObserver();
    
    /**
     * 
     * 根据用户系统ID注册监听用户信息表中的某一条记录<BR>
     * [功能详细描述]
     * @param userSysId 用户系统ID
     */
    void registerGroupMemberByIdObserver(String userSysId);
    
    /**
     * 取消注册群组信息表
     * [一句话功能简述]<BR>
     * [功能详细描述]
     */
    void unregisterGroupInfoObserver();
    
    /**
     * 
     * 根据群组ID取消注册监听群组信息表中的某一条记录<BR>
     * [功能详细描述]
     * @param groupId 群组Id
     */
    void unregisterGroupInfoByIdObserver(String groupId);
    
    /**
     * 
     * 取消注册监听群组成员表<BR>
     * [功能详细描述]
     */
    void unregisterGroupmemberObserver();
    
    /**
     * 
     * 根据用户系统ID取消注册监听用户信息表中的某一条记录<BR>
     * [功能详细描述]
     * @param userSysId 用户系统ID
     */
    void unregisterGroupMemberByIdObserver(String userSysId);
    
    /**
     * 从数据库获取成员列表数据<BR>
     * @param groupId 群组ID
     * @return 成员list
     */
    List<GroupMemberModel> getMemberListFromDB(String groupId);
    
    /**
     * 从xmpp获取成员列表
     * @param groupId 群组ID
     * @param pageID pageID
     * @param pageSize pageSize
     */
    void getMemberListFromXmpp(String groupId, int pageID, int pageSize);
    
    /**
     * 判断是否是群主
     * @param groupId 群组ID
     * @return 是否是owner
     */
    boolean isOwner(String groupId);
    
    /**
     * 判断是否已经加入群组
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupID 群组id
     * @return boolean 是否已经加入过
     */
    boolean hasJoined(String groupID);
    
    /**
     * 判断是否已经被群主邀请加入过，并且还未做处理
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupID 群组id
     * @return boolean 是否被邀请过
     */
    boolean hasBeInvited(String groupID);
    
    /**
     * 从数据库获取群组详情的对象
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param userSysId 用户的系统id
     * @param groupId 群组id
     * @return GroupInfoModel 群组详情的对象
     */
    GroupInfoModel getGroupInfoModelFromDB(String userSysId, String groupId);
    
    /**
     * 从数据库获取群组成员的对象
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param userSysId 用户的系统id
     * @param groupId 群组id
     * @param memberUserId 群成员的Id
     * @return GroupMemberModel 群组成员的对象
     */
    GroupMemberModel getGroupMemberModelFromDB(String userSysId,
            String groupId, String memberUserId);
    
    /**
     * 得到用于添加群成员的列表
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupId groupId
     * @return 联系人信息对象list
     */
    List<ContactInfoModel> getContactListForAdd(String groupId);
    
    /**
     * 得到用于删除群成员联系人列表
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupId groupId
     * @return 联系人信息对象list
     */
    List<ContactInfoModel> getContactListForRemove(String groupId);
    
    /**
     * 
     * 更新群组配置信息<BR>
     * [功能详细描述]
     * @param group 群组对象
     */
    void submitConfigInfo(GroupInfoModel group);
    
    /**
     * 在服务器更新群组成员的信息
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param describe describe
     * @param logo logo
     * @param mobilePolicy mobilePolicy
     * @param pcpolicy pcpolicy
     * @param to pcpolicy
     * @param model 群组详情对象
     */
    void changeMemberInfo(String describe, String logo, int mobilePolicy,
            int pcpolicy, String to, GroupInfoModel model);
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param info 群组详情对象
     * @param member 群组成员对象
     */
    void changeMemberNick(GroupInfoModel info, GroupMemberModel member);
    
    /**
     * 申请退出群
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupJid 群组Jid
     */
    void quitGroup(String groupJid);
    
    /**
     * 关闭并解散群
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param groupJid 群组Jid
     * @param reason 删除群组的原因
     */
    void destroyGroup(String groupJid, String reason);
    
    /**
     * 
     * 查询分组内的所有成员<BR>
     * Parameters:
     * @param userSysId 当前用户的系统标识
     * @param groupId 群组ID
     * @return 群组内所有成员
     */
    public List<GroupMemberModel> queryByGroupId(String userSysId,
            String groupId);
}
