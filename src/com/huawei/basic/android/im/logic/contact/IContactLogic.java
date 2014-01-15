/*
 * 文件名: IContactLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.contact;

import java.util.List;
import java.util.Map;

import com.huawei.basic.android.im.logic.model.PhoneContactIndexModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;

/**
 * 通讯录模块的logic接口<BR>
 * [功能详细描述]
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012] 
 */
public interface IContactLogic
{
    
    /**
     * 通讯录上传操作
     * 全量和增量（包括新增、修改、删除）上传通讯录中联系人数据，第一次上传时为上传全部的新增联系人<BR>
     * 第一次上传既调用此方法
     * @param isFullUpload 
     *          全量 true 
     *          增量 false
     */
    void beginUpload(boolean isFullUpload);
    
    /**
     * 
     * 暂停上传<BR>
     * [功能详细描述]
     */
    void paushUpload();
    
    /**
     * 
     * 继续上传<BR>
     * [功能详细描述]
     */
    void resumUpload();
    
    /**
     * 
     * 取消上传<BR>
     * [功能详细描述]
     */
    void cancelUpload();
    
    /**
     * 
     *  获取手机通讯录里的联系人<BR>
     *  启动获取联系人线程
     */
    void getPhoneContactData();
    
    /**
     * 
     * 得到本地联系人详细描述<BR>
     * [功能详细描述]
     * @param id 联系人的Id
     * @return 本地联系人详细描述 PhoneContactIndexModel
     */
    PhoneContactIndexModel getLocalContactProfile(String id);
    
    /**
     * 
     * 删除手机通讯录记录<BR>
     * 
     * @param contactId contactId
     */
    void deleteContactById(String contactId);
    
    
    /**
     * 
     * 将通讯录是否上传标志插入数据库<BR>
     * 插入UserConfig表中
     * @param userSysID 用户sysid
     * @param colName 修改的列名
     * @param colValue 插入值
     * @return long 返回该数据的Id
     */
    long insertUploadFlag(String userSysID, String colName, String colValue);
    
    /**
     * 
     * 更改通讯录上传标志<BR>
     * @param flag 上传标志
     * @return 修改条数
     */
    int updateUploadFlag(boolean flag);
    
    /**
     * 
     * 插入或更改通讯录上传标志<BR>
     * @param flag 上传标志
     * @return 修改条数
     */
    long insertOrUpdateUploadFlag(boolean flag);
    
    /**
     * 
     * 查询数据库配置信息<BR>
     * 
     * @param userSysID userSysID
     * @param colName colName
     * @return UserConfigModel
     */
    UserConfigModel queryUserConfig(String userSysID, String colName);
    
    /**
     * 
     * 判断该用户是否需要上传通讯录<BR>
     * [功能详细描述]
     * @param userSysId 用户sysId
     * @return boolean 是否需要上传联系人
     */
    boolean needUploadContacts(String userSysId);
    
    /**
     * 
     * 是否需要提示联系人太多，上传会影响性能的提示<BR>
     * [功能详细描述]
     * @return boolean 
     */
    boolean needShowManyContactsTips();
    
    /**
     * 
     * 监听本地通讯录<BR>
     * 
     */
    void registerContactsObserver();
    
    /**
     * 
     * 移除本地通讯录监听<BR>
     * 
     */
    void unRegisterContactsObserver();
 
    /**
     * 
     * 邀请好友<BR>
     * @param sendData 邀请信息
     */
    void inviteFriend(Map<String, Object> sendData);
    
    /**
     * 是否已经上传过通讯录<BR>
     * @return true 上传过  false 未上传过
     */
    boolean hasUploaded();
    
    /**
     * 删除服务器上已上传的通讯录<BR>
     */
    void deleteUploadedContacts();

    /**
     * 获取非好友的电话簿联系人<BR>
     * @return List<PhoneContactIndexModel>
     */
    List<PhoneContactIndexModel>  getAddressBookContactsNotFriends();
}
