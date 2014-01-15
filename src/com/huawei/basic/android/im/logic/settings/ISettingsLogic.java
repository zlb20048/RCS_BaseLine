/*
 * 文件名: ISettingsLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.settings;

import java.util.ArrayList;

import com.huawei.basic.android.im.logic.model.ContactInfoModel;
import com.huawei.basic.android.im.logic.model.SysAppInfoModel;
import com.huawei.basic.android.im.logic.model.UserConfigModel;

/**
 * 设置模块的logic接口<BR>
 * 
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012]
 */
public interface ISettingsLogic
{
    
    /**
     * 查询个人资料
     * 
     * @param timeStamp
     *            时间戳
     * @param isForceUpdate
     *            是否强制更新
     */
    void sendRequestPrivateProfile(String timeStamp, boolean isForceUpdate);
    
    /**
     * 修改密码
     * 
     * @param oldPassword
     *            oldPassword
     * @param newPassword
     *            newPassword
     */
    void sendModifyPassword(String oldPassword, String newPassword);
    
    /**
     * 重置密码
     * 
     * @param number
     *            手机号
     */
    void resetPasswordFromNumber(String number);
    
    /**
     * 重置密码
     * 
     * @param number
     *            手机号
     * @param openType
     *            解绑类型
     */
    void resetPasswordFromNumberWithType(String number, String openType);
    
    /**
     * 
     * 重置密码
     * 
     * @param number
     *            号码
     * @param verifyCode
     *            验证码
     * @param password
     *            密码
     */
    void resetPasswordFromNumberAndVerify(String number, String verifyCode,
            String password);
    
    /**
     * 重置密码
     * 
     * @param email
     *            邮箱
     */
    void resetPasswordFromEmail(String email);
    
    /**
     * 更新个人资料
     * 
     * @param contactInfoModel
     *            个人资料信息
     */
    void sendUpdatePrivateProfile(ContactInfoModel contactInfoModel);
    
    /**
     * 更新个人资料
     * 
     * @param contactInfoModel
     *            个人资料信息
     */
    public void sendUpdateSignature(ContactInfoModel contactInfoModel);
    
    /**
     * 更新个人资料 privacy
     * 
     * @param privacy
     *            数据
     */
    void sendUpdateProfilePrivacy(String privacy);
    
    /**
     * 
     * 更新个人资料 [功能详细描述]
     * 
     * @param autoConfirmFriendStr
     *            autoConfirmFriendStr
     * @param friendPrivacyStr
     *            friendPrivacyStr
     */
    public void sendUpdateProfilePrivacy(String autoConfirmFriendStr,
            String friendPrivacyStr);
    
    /**
     * 检测更新 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param version
     *            version
     * @param width
     *            width
     * @param height
     *            height
     */
    void sendRequestCheckUpdate(String version, String width, String height);
    
    /**
     * 解绑定邮箱 [功能详细描述]
     * 
     * @param operType
     *            operType
     */
    void unBindMail(String operType);
    
    /**
     * 绑定邮箱
     * 
     * @param emailAdrr
     *            地址
     * @param nickName
     *            昵称
     */
    void bindMail(String emailAdrr, String nickName);
    
    /**
     * getMsisdnVerifyCode [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @param phoneNumber
     *            phoneNumber
     * @param operType
     *            operType
     */
    void getMsisdnVerifyCode(String phoneNumber, String operType);
    
    /**
     * 绑定电话
     * 
     * @param phoneNum
     *            号码
     * @param verifyCode
     *            验证码
     */
    void bindPhone(String phoneNum, String verifyCode);
    
    /**
     * 解绑电话
     * 
     * @param opreType
     *            解绑
     * @param verifyCode
     *            验证码
     */
    void unBindPhone(String opreType, String verifyCode);
    
    /**
     * 查询个人资料信息
     * 
     * @param userSysId
     *            用户系统ID
     * @return 个人资料对象
     */
    ContactInfoModel queryMyProfile(String userSysId);
    
    /**
     * 查询数据库操作
     * 
     * @param key
     *            key值
     * @return 个人配置对象
     */
    UserConfigModel configQueryByKey(String key);
    
    /**
     * 查询数据库有记录执行更新，没记录执行添加
     * 
     * @param userId
     *            userId
     * @param key
     *            key
     * @param value
     *            value
     */
    void addConfig(String userId, String key, String value);
    
    /**
     * 检测邮箱是否绑定
     * 
     * @param email
     *            邮箱
     * @see com.huawei.basic.android.im.logic.login.IRegisterLogic#checkEmailBind(java.lang.String)
     */
    void checkEmailBind(String email);
    
    /**
     * 
     * 从服务器获取应用
     */
    void getAppFromeServer();
    
    /**
     * 
     * 根据应用类型从数据库获取系统应用<BR>
     * [功能详细描述]
     * 
     * @param type
     *            应用类型
     * @return SysAppInfoModel列表
     */
    ArrayList<SysAppInfoModel> getSysAppInfoByType(int type);
    
    /**
     * 
     * 从数据库获取所有系统应用<BR>
     * [功能详细描述]
     * 
     * @return 应用列表
     */
    ArrayList<SysAppInfoModel> getAllSysAppInfo();
    
    /**
     * 
     * 从数据库获取我的应用<BR>
     * 
     * @return SysAppInfoModel列表
     */
    ArrayList<SysAppInfoModel> getMyAppInfoFromDB();
    
    /**
     * 
     * 删除我的应用<BR>
     * 
     * @param appId
     *            应用Id
     */
    void deleteByAppId(String appId);
    
    /**
     * 
     * 获取到待添加应用列表数据<BR>
     * [功能详细描述]
     * 
     * @return ArrayList<SysAppInfoModel> 列表
     */
    ArrayList<SysAppInfoModel> getAddedData();
    
    /**
     * 
     * 插入我的应用表<BR>
     * 
     * @param appId
     *            应用Id
     * @return long
     */
    long insertMyApp(String appId);
    
    /**
     * 
     * 添加应用事件<BR>
     * [功能详细描述]
     * 
     * @param mIds
     *            id
     */
    void addAppToDB(ArrayList<Integer> mIds);
    
    /**
     * 上传用户头像<BR>
     * 
     * @param fileName
     *            文件名称
     * @param photoBytes
     *            头像的byte数组
     */
    void uploadUserFace(String fileName, byte[] photoBytes);
    
    /**
     * 
     * 获取封装联系人对象所得的数据<BR>
     * [功能详细描述]
     * 
     * @param user
     *            联系人对象
     * @return 封装后所得的数据
     */
    String getCrcValue(ContactInfoModel user);
    
    /**
     * 
     * 给联系人信息表注册监听<BR>
     * [功能详细描述]
     */
    void registerContactInfoObserver();
    
    /**
     * 
     * 取消联系人信息表的监听<BR>
     * [功能详细描述]
     */
    void unregisterContactInfoObserver();
    
    /**
     * 
     * 更新个人邮箱<BR>
     * [功能详细描述]
     * 
     * @param email
     *            邮箱
     */
    void updateContactEmail(String email);
    
    /**
     * 
     * 更新个人电话<BR>
     * [功能详细描述]
     * 
     * @param phone
     *            电话
     */
    void updateContactPhone(String phone);
    
    /**
     * 
     * 解绑账号<BR>
     * [功能详细描述]
     * 
     * @param unbindInfo
     *            解绑信息
     * @return 成功失败
     */
    boolean unbindAccount(String unbindInfo);
    
    /**
     * 清空聊天记录<BR>
     */
    void clearAllData();
}
