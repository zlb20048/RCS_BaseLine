/*
 * 文件名: IFriendLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.friend;

import java.util.ArrayList;

import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.ContactSectionModel;

/**
 * 好友模块的logic接口<BR>
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012] 
 */
public interface IFriendLogic
{
    
    /**
     * 添加分组入口<BR>
     * @param sectionName 组名
     * @param sectionNotes 分组备注
     * @param friendSysIdList 好友sysId列表
     */
    void addSection(String sectionName, String sectionNotes,
            ArrayList<String> friendSysIdList);
    
    /**
     * 分组添加成员入口<BR>
     * @param sectionId 分组ID
     * @param friendIdList 好友sysId列表
     */
    void addContactsToSection(String sectionId, ArrayList<String> friendIdList);
    
    /**
     * 删除分组入口<BR>
     * @param sectionId 分组ID
     */
    void deleteSection(String sectionId);
    
    /**
     * 获取好友列表入口<BR>
     * 首先根据时间戳发起联网请求，然后查询数据库，返回好友列表
     * @param isNeedHandleFriendManagerData 是否需要处理找朋友小助手数据
     * @param friendUserId 好友ID
     */
    void getAllContactList(boolean isNeedHandleFriendManagerData,
            String friendUserId);
    
    /**
     * 异步获取好友分组列表入口 <BR>
     * 分组中包括好友列表，通过Message的方式返回数据
     */
    void getContactSectionListWithFriendListAsyn();
    
    /**
     * 获取好友分组列表入口<BR>
     * 分组中不关联好友列表
     * @return 分组列表
     */
    ArrayList<ContactSectionModel> getContactSectionList();
    
    /**
     * 获取好友列表<BR>
     * @return 好友列表
     */
    ArrayList<ContactInfoModel> getContactInfoListFromDb();
    
    /**
     * 根据分组ID获取好友列表入口<BR>
     * @param sectionId 分组ID
     * @return 分组ID对用的好友列表
     */
    ArrayList<ContactInfoModel> getContactListBySectionId(String sectionId);
    
    /**
     * 根据好友的HitalkID获取本地好友详情入口<BR>
     * 
     * @param friendUserID 好友的HiTalkID
     * @return 好友的数据模型对象
     */
    ContactInfoModel getContactInfoByFriendUserId(String friendUserID);
    
    /**
     * 同步获取分组列表入口<BR>
     * 分组中包括好友列表数据
     * @return 分组列表
     */
    ArrayList<ContactInfoModel> getAllContactListFromDb();
    
    /**
     * 根据认识的人加载更多好友入口<BR>
     */
    void loadMoreFriendByKnownPerson();
    
    /**
     * 
     * 附近查找加载更多好友入口<BR>
     * [功能详细描述]
     */
    void loadMoreFriendByLocation();
    
    /**
     * 
     * 清除位置信息<BR>
     * [功能详细描述]
     */
    void removeLocationInfo();
    
    /**
     * 根据ID加载更多的好友入口<BR>
     * @param startIndex
     *      加载数据开始下标
     * @param recordCount
     *      记录条数
     * @param searchValue
     *      查询关键字
     */
    void loadMoreFriendById(int startIndex, int recordCount, String searchValue);
    
    /**
     * 根据详细信息加载更多好友入口<BR>
     * @param startIndex
     *      开始加载记录下标
     * @param recordCount
     *      返回记录条数
     * @param searchValue
     *      查询关键字
     */
    void loadMoreFriendByDetail(int startIndex, int recordCount,
            String searchValue);
    
    /**
     * 更新分组名称入口<BR>
     * @param sectionName 组名
     * @param sectionId 分组ID
     */
    void updateSectionName(String sectionName, String sectionId);
    
    /**
     * 把组员移除分组入口<BR>
     * @param sysIdList 要移除该分组的好友的SysId列表
     * @param sectionId 分组ID
     */
    void removeMemberFromSection(ArrayList<String> sysIdList, String sectionId);
    
    /**
     * 移动分组入口<BR>
     * @param accountId 账号
     * @param newSectionId 目标分组ID
     * @param oldSectionId 当前所在分组ID
     */
    void removeToSection(String accountId, String newSectionId,
            String oldSectionId);
    
    /**
     * 同步头像图标入口<BR>
     * @param contactInfo 联系人信息模型
     */
    void syncFaceIcon(ContactInfoModel contactInfo);
    
    /**
     * 更新服务器备注信息入口<BR>
     * @param contactInfoModel 数据库对象
     * @param friendMemoName 备注名
     * @param friendMemoPhone 电话
     * @param friendMemoEmail 邮件地址
     */
    void sendFriendMemo(ContactInfoModel contactInfoModel,
            String friendMemoName, String friendMemoPhone,
            String friendMemoEmail);
    
    /**
     * 添加分组时判断分组名是否存在入口
     * @param sectionName 组名
     * @return 是否存在
     */
    boolean sectionNameExist(String sectionName);
    
    /**
     * 添加通过hiTalkID监听数据库单条好友信息的监听入口<BR>
     * 
     * @param hiTalkID 好友的HitalkID
     */
    void registerObserverByID(String hiTalkID);
    
    /**
     * 
     * 移除通过hiTalkID移除数据库单条好友信息的监听入口<BR>
     * 
     * @param hiTalkID 好友的HitalkID
     */
    void unRegisterObserverByID(String hiTalkID);
    
    /**
     * 
     * 根据HiTalk详情信息获取入口<BR>
     * 
     * @param hiTalkID 好友的hiTalkID
     * @param contactType 联系人类型
     *          [通讯录 LOCAL_CONTACT = 0,
     *          HiTalk类型 HITALK_CONTACT = 1,
     *          好友类型 FRIEND_CONTACT = 2]
     */
    void updateContactDetails(String hiTalkID, int contactType);
    
    /**
     * 编辑分组名时判断编辑后的名字其他分组的名字是否相同入口
     * @param beforeSectionName 之前分组名
     * @param newSectionName 新分组名
     * @return 是否相同
     */
    boolean otherSectionNameExist(String beforeSectionName,
            String newSectionName);
    
    /**
     * 好友是否在好友列表中存在<BR>
     * @param friendSysId 好友的sysId
     * @return 是否存在
     */
    boolean isFriendExist(String friendSysId);
}
