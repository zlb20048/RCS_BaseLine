/*
 * 文件名: UserConfigModel.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 张仙
 * 创建时间:Feb 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.model;

import java.io.Serializable;

/**
 * 用户全局配置信息<BR>
 * 
 * @author 张仙
 * @version [RCS Client_Handset V100R001C04SPC002, Feb 15, 2012] 
 */
public class UserConfigModel implements Serializable
{
    /**
     * 用户个人资料key值
     */
    public static final String PRIVATE_PROFILE = "PrivateProfile";
    
    /**
     * 用户对外授权规则key值
     */
    public static final String PRIVACY = "Privacy";
    
    /**
     * 用户被添加好友控制策略key值
     */
    public static final String FRIEND_PRIVACY = "FriendPrivacy";
    
    /**
     * 用户被加好友确认策略key值
     */
    public static final String AUTO_CONFIRM_FRIEND = "AutoConfirmFriend";
    
    /**
     * 消息接收策略取值
     */
    public static final String PC_RECEIVE_POLICY = "PC_Receiving_Policy";
    
    /**
     * 免打扰设置的状态
     */
    public static final String UNDISTURB_POLICY_STATUS = "UndisturbPolicyStatus";
    
    /**
     * 免打扰设置的时间
     */
    public static final String UNDISTURB_POLICY_TIME = "UndisturbPolicyTime";
    
    /**
     * 免打扰设置的更新时间点（时间戳？）
     */
    public static final String UNDISTURB_POLICY_UPDATETIME = "UndisturbPolicyUpdateTime";
    
    /**
     * 系统应用版本号
     */
    public static final String SYS_APP_INFO_VERSION = "SysAppInfoVersion";
    
    /**
     * 离线消息转短信策略
     */
    public static final String RECEIVING_POLICY = "PcReceivingPolicy";
    
    /**
     * 广播消息接收设置策略
     */
    public static final String BROADCAST_POLICY = "BroadcastPolicy";
    
    /**
     * 声音提示设置
     */
    public static final String VOICE_TIPS = "VoiceTips";
    
    /**
     * 用户系统唯一标识符
     */
    public static final String USERSYSID = "UserSysId";
    
    /**
     * 用户账号
     */
    public static final String ACCOUNT = "account";
    
    /**
     * 获取好友列表时间戳
     */
    public static final String GET_FRIEND_LIST_TIMESTAMP = "get_friend_list_timestamp";
    
    /**
     *  通讯录是否上传全局设置
     */
    public static final String IS_UPLOAD_CONTACTS = "is_upload_contacts";
    
    /**
     * 上传通讯录 
     */
    public static final String IS_UPLOAD_CONTACTS_YES = "yes";
    
    /**
     * 不上传通讯录
     */
    public static final String IS_UPLOAD_CONTACTS_NO = "no";
    
    /**
     * 群发短信能力. <br>
     * 0: 没有  <br>
     * 1: 有
     * 
     */
    public static final String TOGETHER_SEND_SM_ABILITY = "TogetherSendSmAbility";
    
    /**
     * 群发短信能力: 有
     */
    public static final String TOGETHER_SEND_SM_ABILITY_YES = "1";
    
    /**
     * 群发短信能力: 没有
     */
    public static final String TOGETHER_SEND_SM_ABILITY_NO = "0";
    
    /**
     * 是否上传手机通讯录
     */
    public static final String IS_UPLOAD_PHONE_CONTACT = "isUploadPhoneContact";
    
    /**
     * 是否上传手机通讯录: 上传
     */
    public static final String IS_UPLOAD_PHONE_CONTACT_YES = "1";
    
    /**
     * 是否上传手机通讯录： 不上传
     */
    public static final String IS_UPLOAD_PHONE_CONTACT_NO = "0";
    
    /**
     * 是否在好友界面展示通讯录
     */
    public static final String IS_SHOW_PLUGINS_ON_CONTACTS = "show_plugins_on_contacts";
    
    /**
     * 是
     */
    public static final String IS_SHOW_PLUGINS_ON_CONTACTS_YES = "1";
    
    /**
     * 否
     */
    public static final String IS_SHOW_PLUGINS_ON_CONTACTS_NO = "0";
    
    /**
     * 开启提示音
     */
    public static final String OPEN_VOICE = "0";
    
    /**
     * 关闭提示音
     */
    public static final String CLOSE_VOICE = "1";
    
    /**
     * 应用时间戳
     */
    public static final String APP_TIMESTAMP = "app_timestamp";
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 配置项的名称，例如：addrbookver
     */
    private String key;
    
    /**
     * 配置项的值，例如：V1.2
     */
    private String value;
    
    /**
     * 默认构造方法
     */
    public UserConfigModel()
    {
        super();
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
}
