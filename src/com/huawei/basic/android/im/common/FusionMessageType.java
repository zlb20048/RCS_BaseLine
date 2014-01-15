/*
 * 文件名: FusionMessageType.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2012-3-8
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.common;

/**
 * 消息类型定义<BR>
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2012-3-8]
 */
public interface FusionMessageType
{
    
    /**
     * 常用消息处理定义的聚合
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-14]
     */
    public interface Base
    {
        /**
         * 此MessageType的基数
         */
        final int BASE = 0x10000000;
        
        /**
         * 跳转登陆界面的消息
         */
        int MSGTYPE_TIMER_LOGIN = BASE + 1;
        
        /**
         * 跳转主界面的消息
         */
        int MSGTYPE_TIMER_MAINTAB = BASE + 2;
        
        /**
         * 更新界面的消息
         */
        int MSGTYPE_TIMER_UPDATE = BASE + 3;
        
        /**
         * 正在登录
         */
        int DEF_MSGTYPE_INLOGIN_BREAK = BASE + 4;
        
        /**
         * 跳转首次登陆界面的消息
         */
        int MSGTYPE_TIMER_LOGIN_MAIN = BASE + 5;
        
    }
    
    /**
     * 好友模块消息码<BR>
     * 
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 14, 2012]
     */
    public interface FriendMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x20000000;
        
        /**
         * 请求好友列表，联网或者读取本地数据库
         */
        int REQUEST_FOR_CONTACT_LIST = BASE + 1;
        
        /**
         * 增加分组
         */
        int REQUEST_TO_ADD_SECTION = BASE + 2;
        
        /**
         * 获取所有找朋友小助手数据列表
         */
        int GET_ALL_FRIEND_HELPER_LIST = BASE + 3;
        
        /**
         * 删除分组
         */
        int REQUEST_DELETE_SECTION = BASE + 4;
        
        /**
         * 更新分组名
         */
        int REQUEST_UPDATE_SECTION_NAME = BASE + 5;
        
        /**
         * 从数据库获取好友列表
         */
        int GET_CONTACT_LIST_FROM_DB = BASE + 6;
        
        /**
         * 获取默认分组的好友列表
         */
        int GET_CONTACT_LIST_BY_SECTION_ID_FROM_DB = BASE + 7;
        
        /**
         * 请求给分组添加成员
         */
        int REQUEST_ADD_CONTACT_TO_SECTION = BASE + 8;
        
        /**
         * 请求移出分组
         */
        int REQUEST_REMOVE_CONTACT_FROM_SECTION = BASE + 9;
        
        /**
         * 请求移动分组
         */
        int REQUEST_REMOVE_CONTACT_TO_SECTION = BASE + 10;
        
        /**
         * 请求更多的好友
         */
        int REQUEST_FRIEND_LOAD_MORE_FRIEND = BASE + 11;
        
        /**
         * 请求认识的人
         */
        int REQUEST_FRIEND_MAYBE_KNOWN_PERSON = BASE + 12;
        
        /**
         * 查询好友失败
         */
        int REQUEST_FIND_FRIEND_ERROR = BASE + 13;
        
        /**
         * 修改备注成功
         */
        int REQUEST_TO_MODIFY_MEMO_SUCCESS = BASE + 14;
        
        /**
         * 好友详情表变更
         */
        int CONTACTINFO_CHANGED = BASE + 15;
        
        /**
         * 联网错误
         */
        int RESPONSE_ERROR = BASE + 16;
        
        /**
         * 异步加载头像
         */
        int DEF_FRIEND_SYNC_FACE_ICON = BASE + 17;
        
        /**
         * 拒绝好友请求命令执行失败
         */
        int REFUSE_FRIEND_APPLY_FAIL = BASE + 18;
        
        /**
         * 定位请求
         */
        int REQUEST_LOCATION = BASE + 19;
        
        /**
         * 定位成功
         */
        int LOCATION_SUCCESS = BASE + 20;
        
        /**
         * 附近查找成功
         */
        int FRIEND_BY_LOCATION_SUCCESS = BASE + 21;
        
        /**
         * 附近查找失败
         */
        int FRIEND_BY_LOCATION_FAILED = BASE + 22;
        
        /**
         *  清除位置信息
         */
        int REQUEST_REMOVE_LOCATION = BASE + 23;
        
        /**
         * 邀请信息返回提示
         */
        int INVITE_FRIEND_MESSAGE = BASE + 24;
        
        /**
         * 是否在好友列表展示
         */
        int SHOW_ON_CONTACTS_LIST = BASE + 25;
        
        /**
         * 查看可能认识的人出错
         */
        int FIND_MAYBE_KNOWN_FRIEND_FAIL = BASE + 26;
        
        /**
         *  未知错误
         */
        int REMOVE_LOCATION_ERROR = BASE + 27;
        
        /**
         *  参数校验失败或缺少必选参数
         */
        int REMOVE_LOCATION_PARAMETER = BASE + 28;
        
        /**
         *  指定用户实体不存在
         */
        int REMOVE_LOCATION_NO_USER = BASE + 29;
        
        /**
         * 请求失败
         */
        int REQUEST_ERROR = BASE + 30;
        
        /**
         * 插入
         */
        int CONTACT_INFO_INSERT = BASE + 31;
        
        /**
         * 删除
         */
        int CONTACT_INFO_DELETE = BASE + 32;
    }
    
    /**
     * 注册模块
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-2-20]
     */
    public interface RegisterMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x30000000;
        
        /**
         * 成功注册账号
         */
        int REGISTE_ACCOUNT_SUCCESS = BASE + 1;
        
        /**
         * 注册账号
         */
        int REGISTER_ACCOUNT_FAILED = BASE + 2;
        
        /**
         * 检测手机号未绑定
         */
        int CHECK_MOBILE_BIND_SUCCESS = BASE + 3;
        
        /**
         * 检测手机号已经绑定
         */
        int CHECK_MOBILE_BIND_FAILED = BASE + 4;
        
        /**
         * 检测email未绑定
         */
        int CHECK_EMAIL_BIND_SUCCESS = BASE + 5;
        
        /**
         * 检测email已经绑定
         */
        int CHECK_EMAIL_BIND_FAILED = BASE + 6;
        
        /**
         * 成功获取手机验证码
         */
        int GET_MSISDN_VERIFY_CODE_SUCCESS = BASE + 7;
        
        /**
         * 获取邮箱验证码成功
         */
        int GET_EMAIL_VERIFY_CODE_SUCCESS = BASE + 8;
        
        /**
         * 没有获取验证码
         */
        int GET_VERIFY_CODE_FAILED = BASE + 9;
        
        /**
         * 检测验证码正确
         */
        int CHECK_VERIFY_CODE_SUCCESS = BASE + 10;
        
        /**
         * 检测验证码错误
         */
        int CHECK_VERIFY_CODE_FAILED = BASE + 11;
        
        /**
         * 检测手机验证码是否正确
         */
        int CHECK_MSISDN_VERIFY_CODE = BASE + 12;
        
        /**
         * 检测邮箱验证码是否正确
         */
        int CHECK_EMAIL_VERIFY_CODE = BASE + 13;
        
        /**
         * 联网失败时的action标识
         */
        int CONNECT_FAILED = BASE + 14;
        
        /**
         * 检测手机号或邮箱已经绑定
         */
        int CHECK_BIND_FAILED = BASE + 15;
        
        /**
         * 是否 获取验证码 计时器
         */
        int GET_VERIFYCODE_TIMER = BASE + 16;
        
        /**
         * 是否 立即登录 计时器
         */
        int LOGIN_NOW_TIMER = BASE + 17;
        
        /**
         * 获取短信验证码的消息
         */
        int GET_MSISDN_VERIFY_CODE_MESSAGE = BASE + 18;
        
    }
    
    /**
     * 找朋友小助手<BR>
     * 
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 22, 2012]
     */
    public interface FriendHelperMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x40000000;
        
        /**
         * 获取全部找朋友小助手数据
         */
        int GET_ALL_FRIEND_HELPER_LIST = BASE + 1;
        
        /**
         * 新增好友
         */
        int NEW_FRIEND_ADDED = BASE + 2;
        
        /**
         * 删除好友成功
         */
        int DELETE_FRIEND_SUCCESS = BASE + 3;
        
        /**
         * 被好友删除
         */
        int BE_DELETED = BASE + 4;
        
        /**
         * 验证好友信息服务返回成功
         */
        int REQUEST_DO_AUTH_SUCCESS = BASE + 5;
        
        /**
         * 删除好友请求失败
         */
        int DELETE_FRIEND_FAIL = BASE + 6;
        
        /**
         * 找朋友小助手表变更
         */
        int FRIENDHELPER_CHANGED = BASE + 7;
        
        /**
         * 找朋友小助手数据记录发生变化
         */
        int FRIENDHELPER_LIST_CHANGED = BASE + 8;
        
        /**
         * 好友发布状态报告
         */
        int FRIENDHELPER_PRESENCE = BASE + 9;
        
    }
    
    /**
     * 通讯录模块消息码 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @author 邵培培
     * @version [RCS Client V100R001C03, 2012-2-23]
     */
    public interface ContactMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x50000000;
        
        /**
         * 获取全部通讯录数据
         */
        int GET_ALL_CONTACT_LIST = BASE + 1;
        
        /**
         * 获取通讯录数据变化
         */
        int CONTACTS_CHANGE = BASE + 2;
    }
    
    /**
     * 
     * 详情模块消息码 [功能详细描述]
     * 
     * @author 马波
     * @version [RCS Client V100R001C03, 2012-2-22]
     */
    public interface ContactDetailsMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x60000000;
        
        /**
         * 根据传过来的id list 批量获取好友详情
         */
        int GET_MULTI_FRIENDS_DETAILS = BASE + 1;
        
        /**
         * 获取好友信息失败
         */
        int GET_FRIENDS_DETAILS_FALSE = BASE + 2;
        
        /**
         * 获取好友备注
         */
        int GET_FRIEND_MEMOS = BASE + 3;
        
        /**
         * 更新好友备注
         */
        int UPDATE_FRIEND_MEMO = BASE + 4;
        
        /**
         * 更新好友备注失败
         */
        int UPDATE_FRIEND_MEMO_FAIL = BASE + 5;
        
        /**
         * 邀请好友请求发送
         */
        int INVITE_FRIEND_SEND = BASE + 6;
        
        /**
         * 展示软键盘
         */
        int SHOW_SOFT_INPUT = BASE + 7;
        
    }
    
    /**
     * 设置模块消息码 [一句话功能简述]<BR>
     * 
     * @author mizhou
     * @version [RCS Client V100R001C03, 2012-2-23]
     */
    public interface SettingsMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x70000000;
        
        /**
         * 检测验证码是否正确
         */
        int CHECK_VERIFY_CODE = BASE + 1;
        
        /**
         * 注册账号
         */
        int REGISTE_ACCOUNT = BASE + 2;
        
        /**
         * 绑定手机号
         */
        int BIND_PHONE = BASE + 3;
        
        /**
         * 绑定邮箱
         */
        int BIND_EMAIL = BASE + 4;
        
        /**
         * 解绑手机号或邮箱
         */
        int UNBIND = BASE + 5;
        
        /**
         * 检测手机验证码是否正确
         */
        int CHECK_MSISDN_VERIFY_CODE = BASE + 6;
        
        /**
         * 检测邮箱验证码是否正确
         */
        int CHECK_EMAIL_VERIFY_CODE = BASE + 7;
        
        /**
         * 检测手机号是否已绑定
         */
        int CHECK_MOBILE_BIND = BASE + 8;
        
        /**
         * 检测email是否绑定
         */
        int CHECK_EMAIL_BIND = BASE + 9;
        
        /**
         * 获取手机验证码
         */
        int GET_MSISDN_VERIFY_CODE = BASE + 10;
        
        /**
         * 获取邮箱验证码
         */
        int GET_EMAIL_VERIFY_CODE = BASE + 11;
        
        /**
         * 修改密码。
         */
        int MODIFY_PASSWORD = BASE + 12;
        
        /**
         * 重置密码。
         */
        int RESET_PASSWORD = BASE + 13;
        
        /**
         * 检测手机号绑定失败
         */
        int BIND_FAILED = BASE + 14;
        
        /**
         * 验证码检测失败
         */
        int CHECK_VERIFY_CODE_FAILED = BASE + 15;
        
        /**
         * 注册返回id号失败
         */
        int REGISTER_ACCOUNT_FAILED = BASE + 16;
        
        /**
         * 获取验证码失败
         */
        int GET_VERIFY_CODE_FAILED = BASE + 17;
        
        /**
         * 联网失败时的action标识
         */
        int CONNECT_FAILED = BASE + 18;
        
        /**
         * 更新个人资料成功
         */
        int MSG_TYPE_UPDATE_MYPROFILE_SUCCEED = BASE + 19;
        
        /**
         * 更新个人资料失败
         */
        int MSG_TYPE_UPDATE_MYPROFILE_FAILED = BASE + 20;
        
        /**
         * 更新我的个性签名。
         */
        int MSG_TYPE_UPDATE_SIGNATURE_SUCCEED = BASE + 21;
        
        /**
         * 更新我的个性签名失败。
         */
        int MSG_TYPE_UPDATE_SIGNATURE_FAILED = BASE + 22;
        
        /**
         * 更新版本成功
         */
        int MSG_TYPE_CHECK_UPDATE_VERSION_SUCCEED = BASE + 23;
        
        /**
         * 更新版本失败
         */
        int MSG_TYPE_CHECK_UPDATE_VERSION_FAILED = BASE + 24;
        
        /**
         * 更新隐私策略成功
         */
        int UPDATE_MYPROFILE_PRIVACY_SUCCEED = BASE + 25;
        
        /**
         * 更新隐私策略失败
         */
        int UPDATE_MYPROFILE_PRIVACY_FAILED = BASE + 26;
        
        /**
         * 更新隐私资料成功
         */
        int UPDATE_PRIVACY_MATERIAL_SUCCEED = BASE + 27;
        
        /**
         * 更新隐私资料失败
         */
        int UPDATE_PRIVACY_MATERIAL_FAILED = BASE + 28;
        
        /**
         * 获取我的个人信息成功。
         */
        int GET_MYPROFILE_SUCCEED = BASE + 29;
        
        /**
         * 获取我的个人信息失败。
         */
        int GET_MYPROFILE_FAILED = BASE + 30;
        
        /**
         * 获取应用成功
         */
        int GET_APP_SUCCESS = BASE + 31;
        
        /**
         * 获取应用失败
         */
        int GET_APP_FAILED = BASE + 32;
        
        /**
         * 删除我的应用成功
         */
        int DELETE_APP_SUCCESS = BASE + 33;
        
        /**
         * 删除我的应用成功
         */
        int DELETE_APP_FAILED = BASE + 34;
        
        /**
         * 应用已超数额
         */
        int EXCESS_MAXNUM = BASE + 35;
        
        /**
         * 应用数为0
         */
        int NO_APP = BASE + 36;
        
        /**
         * 应用数未超额，可以添加
         */
        int CAN_INSERT = BASE + 37;
        
        /**
         * 头像上传成功的
         */
        int FACE_UPLOAD_SUCCESS = BASE + 38;
        
        /**
         * 头像上传失败
         */
        int FACE_UPLOAD_FAILED = BASE + 39;
        
        /**
         * 联系人数据库改变的消息
         */
        int CONTACTINFO_DB_CHANGED = BASE + 40;
        
        /**
         * 添加好友联网失败
         */
        int ADD_FRIENDS_CONNECT_FAILED = BASE + 41;
        
        /**
         * 资料开放联网失败
         */
        int OPEN_PROFILE_CONNECT_FAILED = BASE + 42;
    }
    
    /**
     * 聊天<BR>
     * [功能详细描述]
     * 
     * @author fanniu
     * @version [RCS Client V100R001C03, 2012-3-13]
     */
    public interface ChatMessageType
    {
        
        /**
         * 此MessageType的基数
         */
        final int BASE = 0x80000000;
        
        /**
         * 显示底部按钮栏
         */
        int SHOW_BOTTOM_BAR = BASE + 5;
        
        /**
         * 隐藏底部按钮栏
         */
        int HIDE_BOTTOM_BAR = BASE + 6;
        
        /**
         * 记录音频录制的时间
         */
        int RECORD_TIME = BASE + 7;
        
        /**
         * 聊天页面聊天信息变更后刷新页面
         */
        int MSGTYPE_MSG_REFRESH = BASE + 20;
        
        /**
         * 与好友聊天页面好友信息变更刷新页面
         */
        int MSGTYPE_FRIEND_INFO_REFRESH = BASE + 21;
        
        /**
         * 群聊页面群组成员信息变更刷新页面
         */
        int MSGTYPE_MEMBER_INFO_REFRESH = BASE + 22;
        
        /**
         * 群组信息改变时刷新界面
         */
        int MSGTYPE_GROUP_INFO_REFRESH = BASE + 23;
        
        /**
         * 多媒体消息表变更时刷新页面
         */
        int MSGTYPE_MEDIA_INDEX_REFRESH = BASE + 24;
        
        /**
         * 传递音频的振幅，用于改变界面话筒的变化
         */
        int SOUND_AMPLITUDE = BASE + 25;
    }
    
    /**
     * huihua <BR>
     * [功能详细描述]
     * 
     * @author 刘鲁宁
     * @version [RCS Client V100R001C03, Mar 2, 2012]
     */
    public interface ConversationMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x90000000;
        
        /**
         * 会话表数据发生变化
         */
        int CONVERSATION_DB_CHANGED = BASE + 1;
        
        /**
         * 通讯录上传进度
         */
        //        int UPLOAD_PROGRESS_SUCCESS = BASE + 2;
        
        /**
         * 开始进度条
         */
        //        int UPLOAD_PROGRESS_BEGIN = BASE + 3;
        
        /**
         * 上传失败
         */
        int UPLOAD_PROGRESS_FAIL = BASE + 4;
        
        /**
         * 开始上传
         */
        int UPLOAD_CONTACTS_RUNNING = BASE + 5;
        
        /**
         * 取消上传
         */
        int UPLOAD_CONTACTS_CANCEL = BASE + 6;
        
        /**
         * 上传完毕
         */
        int UPLOAD_CONTACTS_FINISH = BASE + 7;
        
        /**
         * 继续上传
         */
        int UPLOAD_CONTACTS_RESUM = BASE + 8;
        
        /**
         * 停止上传
         */
        int UPLOAD_CONTACTS_STOPPED = BASE + 9;
        
    }
    
    /**
     * 登录模块消息类型定义 <BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-6]
     */
    public interface LoginMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x12000000;
        
        /**
         * 登录成功，login的HTTP请求及XMPP注册都成功时才算登录成功；并且在成功时返回的message.
         * obj表示是否需要展示上传通讯录DIALOG.
         */
        int LOGIN_SUCCESS = BASE + 1;
        
        //        /**
        //         * 登录HTTP请求失败
        //         */
        //        int LOGIN_HTTP_FAILED = BASE + 2;
        //        
        //        /**
        //         * 登录后XMPP注册失败
        //         */
        //        int LOGIN_XMPP_FAILED = BASE + 3;
        
        /**
         * 登出
         */
        int LOGOUT = BASE + 4;
        
        /**
         * 获取验证码成功
         */
        int GET_VERIFY_CODE_IMAGE_SUCCESS = BASE + 5;
        
        /**
         * 获取验证码失败
         */
        int GET_VERIFY_CODE_IMAGE_FAILED = BASE + 6;
        
        /**
         * 刷新TOKEN
         */
        int REFRESH_TOKEN = BASE + 7;
        
        /**
         * 上线
         */
        int DEF_MSGTYPE_ONLINE = BASE + 8;
        
        //        /**
        //         * xmpp登录错误
        //         */
        //        int DEF_MSGTYPE_XMPP_LOGIN_FAILED = BASE + 9;
        //        
        //        /**
        //         * 用户错误
        //         */
        //        int DEF_MSGTYPE_FAIL = BASE + 10;
        //        
        //        /**
        //         * 系统错误
        //         */
        //        int DEF_MSGTYPE_ERROR = BASE + 11;
        
        /**
         * 需要图形验证码
         */
        int NEED_VERIFYCODE_ERROR = BASE + 12;
        
        /**
         * 验证码错误
         */
        int VERIFYCODE_ERROR = BASE + 13;
        
        /**
         * 其他错误
         */
        int LOGIN_ERROR = BASE + 14;
        
        /**
         * 登出失败
         */
        int LOGOUT_FAIL = BASE + 15;
        
        /**
         * 被踢
         */
        int KICK_OUT = BASE + 16;
        
        /**
         * // * http连接失败 //
         */
        //        int HTTP_FAILED = BASE + 17;
        //        
        //        /**
        //         * http鉴权失败
        //         */
        //        int HTTP_AUTHERROR = BASE + 18;
        //        
        //        /**
        //         * http网络错误
        //         */
        //        int HTTP_NETWORKERROR = BASE + 19;
        //        
        //        /**
        //         * http请求参数错误
        //         */
        //        int HTTP_PARAMERROR = BASE + 20;
        //        
        //        /**
        //         * http连接超时
        //         */
        //        int HTTP_TIMEOUT = BASE + 21;
        /**
         * 账号或者密码错误
         */
        int ACCOUNT_OR_PASSWORD_ERROR = BASE + 22;
        
        /**
         * 状态：正在登陆
         */
        int STATUS_LOGINING = BASE + 23;
        
        /**
         * 状态：掉线
         */
        int STATUS_OFFLINE = BASE + 24;
        
        /**
         * 状态：在线
         */
        int STATUS_ONLINE = BASE + 25;
        
        /**
         * 状态：稍后重试
         */
        int STATUS_BREAK = BASE + 26;
        
        /**
         * 网络状态：开机
         */
        int STATUS_BOOT = BASE + 27;
        
        /**
         * 网络状态：等待
         */
        int STATUS_STAY_BY = BASE + 28;
        
        /**
         * 网络状态：有网络
         */
        int NET_STATUS_ENABLE = BASE + 29;
        
        /**
         * 网络状态：无网络
         */
        int NET_STATUS_DISABLE = BASE + 30;
        
        /**
         * 网络状态：wap
         */
        int NET_STATUS_WAP = BASE + 31;
        
        /**
         * 状态
         */
        int STATUS = BASE + 32;
        
        /**
         * 登录后流程第一操作
         */
        int BEGIN_AFTER_LOGIN = BASE + 33;
        
        /**
         * 网络状态
         */
        int NET_STATUS = BASE + 64;
        
        /**
         * 登录
         */
        int LOGIN = BASE + 128;
        
        /**
         * 校验验证码成功
         */
        int SEND_VERIFY_CODE_IMAGE_SUCCESS = BASE + 34;
        
        /**
         * 校验验证码失败
         */
        int SEND_VERIFY_CODE_IMAGE_FAILED = BASE + 35;
        
    }
    
    /**
     * 群组模块相关消息码 [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-9]
     */
    public interface GroupMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x15000000;
        
        /**
         * 获取群组列表成功消息
         */
        int GET_GROUP_LIST_SUCCESS = BASE + 1;
        
        /**
         * 获取群组列表失败消息
         */
        int GET_GROUP_LIST_FAILED = BASE + 2;
        
        /**
         * 创建群组成功消息
         */
        int CREATE_GROUP_SUCCESS = BASE + 3;
        
        /**
         * 创建群组失败消息
         */
        int CREATE_GROUP_FAILED = BASE + 4;
        
        /**
         * 获取群组成员列表成功消息
         */
        int GET_MEMBER_LIST_SUCCESS = BASE + 5;
        
        /**
         * 获取群组成员列表失败消息
         */
        int GET_MEMBER_LIST_FAILED = BASE + 6;
        
        /**
         * 申请加入群成功消息
         */
        int JOIN_GROUP_SUCCESS = BASE + 7;
        
        /**
         * 申请加入群失败消息
         */
        int JOIN_GROUP_FAILED = BASE + 8;
        
        /**
         * 搜索群成功消息
         */
        int SEARCH_GROUP_SUCCESS = BASE + 9;
        
        /**
         * 搜索群失败消息
         */
        int SEARCH_GROUP_FAILED = BASE + 10;
        
        /**
         * 修改群组配置成功消息
         */
        int SUBMIT_CONFIGINFO_SUCCESS = BASE + 11;
        
        /**
         * 修改成功配置失败消息
         */
        int SUBMIT_CONFIGINFO_FAILED = BASE + 12;
        
        /**
         * 更改成员信息成功消息
         */
        int CHANGE_MEMBERINFO_SUCCESS = BASE + 13;
        
        /**
         * 更改成员信息失败消息
         */
        int CHANGE_MEMBERINFO_FAILED = BASE + 14;
        
        /**
         * 更改群昵称成功消息
         */
        int CHANGE_MEMBERNICK_SUCCESS = BASE + 15;
        
        /**
         * 更改群昵称成功消息
         */
        int CHANGE_MEMBERNICK_FAILED = BASE + 16;
        
        /**
         * 删除群组成功消息
         */
        int GROUP_DESTROY_SUCCESS = BASE + 17;
        
        /**
         * 删除群组失败消息
         */
        int GROUP_DESTROY_FAILED = BASE + 18;
        
        /**
         * 退出群组失败消息
         */
        int GROUP_QUIT_FAILED = BASE + 19;
        
        /**
         * 从服务器获取群组配置成功消息
         */
        int GET_CONFIG_INFO_SUCCESS = BASE + 20;
        
        /**
         * 从服务器获取群组配置失败消息
         */
        int GET_CONFIG_INFO_FAILED = BASE + 21;
        
        /**
         * 群组被解散成功消息
         */
        int GROUP_DESTROYED_SUCCESS = BASE + 22;
        
        /**
         * 同意群邀请成功消息
         */
        int ACCEPT_INVITE_MEMBER_SUCCESS = BASE + 23;
        
        /**
         * 同意群邀请失败消息
         */
        int ACCEPT_INVITE_MEMBER_FAILED = BASE + 24;
        
        /**
         * 拒绝群邀请成功消息
         */
        int DECLINE_INVITE_MEMBER_SUCCESS = BASE + 25;
        
        /**
         * 拒绝群邀请失败消息
         */
        int DECLINE_INVITE_MEMBER_FAILED = BASE + 26;
        
        /**
         * 退出群组失败消息
         */
        int GROUP_QUIT_SUCCESS = BASE + 27;
        
        /**
         * 群组信息表发生改变消息
         */
        int GROUPINFO_DB_CHANGED = BASE + 200;
        
        /**
         * 群组成员表发生变化
         */
        int GROUP_MEMBER_DB_CHANGED = BASE + 201;
        
        /**
         * 群组信息表中的某一条记录发生变化
         */
        int GROUPINFO_DB_ONE_RECORD_CHANGED = BASE + 202;
        
        /**
         * 成员信息表中的某一条记录发生变化
         */
        int GROUP_MEMBER_DB_ONE_RECORD_CHANGED = BASE + 204;
        
        /**
         * 从会话页面创建群组
         */
        int CREATE_GROUP_FROM_CONVERSATION = BASE + 300;
        
        /**
         * 从群组页面创建群组
         */
        int CREATE_GROUP_FROM_GROUP = BASE + 301;
        
        /**
         * 群组页面创建群组成功消息
         */
        int CREATE_GROUP_SUCCESS_FROM_GROUP = BASE + 302;
        
        /**
         * 群组页面创建群组失败消息
         */
        int CREATE_GROUP_FAILED_FROM_GROUP = BASE + 303;
        
        /**
         * 会话页面创建群组成功消息
         */
        int CREATE_GROUP_SUCCESS_FROM_CONVERSATION = BASE + 304;
        
        /**
         * 会话创建群组失败消息
         */
        int CREATE_GROUP_FAILED_FROM_CONVERSATION = BASE + 305;
        
        /**
         * 从会话页面邀请成员加入群组
         */
        int INVITE_MEMBER_FROM_CONVERSATION = BASE + 306;
        
        /**
         * 从群组相关页面邀请成员加入群组
         */
        int INVITE_MEMBER_FROM_GROUP = BASE + 307;
        
        /**
         * 从会话页面邀请成员加入群组成功
         */
        int INVITE_MEMBER_SUCCESS_FROM_CONVERSATION = BASE + 308;
        
        /**
         * 从会话页面邀请成员加入群组失败
         */
        int INVITE_MEMBER_FAILED_FROM_CONVERSATION = BASE + 309;
        
        /**
         * 从群组相关页面邀请成员加入群组成功
         */
        int INVITE_MEMBER_SUCCESS_FROM_GROUP = BASE + 310;
        
        /**
         * 从群组相关页面邀请成员加入群组失败
         */
        int INVITE_MEMBER_FAILED_FROM_GROUP = BASE + 311;
        
        /**
         * 从会话页面删除成员
         */
        int REMOVE_MEMBER_FROM_CONVERSATION = BASE + 312;
        
        /**
         * 从群组相关页面删除成员
         */
        int REMOVE_MEMBER_FROM_GROUP = BASE + 313;
        
        /**
         * 从会话页面删除成员成功
         */
        int REMOVE_MEMBER_SUCCESS_FROM_CONVERSATION = BASE + 314;
        
        /**
         * 从会话页面删除成员失败
         */
        int REMOVE_MEMBER_FAILED_FROM_CONVERSATION = BASE + 315;
        
        /**
         * 从群组相关页面删除成员成功
         */
        int REMOVE_MEMBER_SUCCESS_FROM_GROUP = BASE + 316;
        
        /**
         * 从群组相关页面删除成员失败
         */
        int REMOVE_MEMBER_FAILED_FROM_GROUP = BASE + 317;
        
        /**
         * 有人从群里退出
         */
        int MEMBER_REMOVED_FROM_GROUP = BASE + 318;
        
        /**
         * 被踢出群
         */
        int MEMBER_KICKED_FROM_GROUP = BASE + 319;
        
        /**
         * 有人从会话退出
         */
        int MEMBER_REMOVED_FROM_CONVERSATION = BASE + 320;
        
        /**
         * 被踢出会话
         */
        int MEMBER_KICKED_FROM_CONVERSATION = BASE + 321;
        
        /**
         * 成员修改昵称
         */
        int MEMBER_NICKNAME_CHANGED = BASE + 322;
        
        /**
         * 成员加入群
         */
        int MEMBER_ADDED_TO_GROUP = BASE + 323;
        
        /**
         * 需要获取群成员列表
         */
        int NEED_GET_MEMBER_LIST = BASE + 324;
        
        /**
         * 请求加入群命令执行成功
         */
        int REQUEST_MESSAGE_SEND_SUCCESS = BASE + 325;
        
        /**
         * 无效的群组消息
         */
        int INVALID_GROUP_MESSAGE = BASE + 1000;
    }
    
    //    /**
    //     * 
    //     * 重新登录
    //     * 
    //     * @author tlmao
    //     * @version [RCS Client V100R001C03, Mar 15, 2012]
    //     */
    //    public interface ReLoginMessageType
    //    {
    //        /**
    //         * 此MessageType的基数
    //         */
    //        int BASE = 0x16000000;
    //        
    //        /**
    //         * 重新登录成功
    //         */
    //        int RELOGIN_SUCCESS = BASE + 1;
    //        
    //        /**
    //         * 重新登录失败
    //         */
    //        int RELOGIN_FAILED = BASE + 2;
    //        
    //        /**
    //         * 重新登录密码修改错误
    //         */
    //        int RELOGIN_PASSWORD_ERROR = BASE + 3;
    //        
    //        /**
    //         * 开始重新登录
    //         */
    //        int RELOGIN_BEGIN = BASE + 4;
    //        
    //    }
    
    /**
     * 绑定voip账号的MessageType
     * 
     * @author 王媛媛
     * @version [RCS Client V100R001C03, 2012-3-18]
     */
    public interface VOIPMessageType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x17000000;
        
        /**
         * 获得所有通话记录
         */
        int COMM_GET_ALL_COMM_LOG = BASE + 1;
        
        /**
         * voip账号绑定成功
         */
        
        int VOIP_BIND_SUCCESS = BASE + 2;
        
        /**
         * voip账号绑定失败
         */
        int VOIP_BIND_FAILED = BASE + 3;
        
        /**
         * voip账号解绑成功
         */
        int VOIP_UNBIND_SUCCESS = BASE + 4;
        
        /**
         * voip账号解绑失败
         */
        int VOIP_UNBIND_FAILED = BASE + 5;
        
        /**
         * 显示voip账号绑定界面按钮
         */
        int VOIP_SHOW_BIND_BUTTON = BASE + 6;
        
        /**
         * 显示voip解绑按钮
         */
        int VOIP_SHOW_UNBIND_BUTTON = BASE + 7;
        
        /**
         * 正在呼叫
         */
        int VOIP_CALL_STATE_RINGING = BASE + 8;
        
        /**
         * 正在通话
         */
        int VOIP_CALL_STATE_TALKING = BASE + 9;
        
        /**
         * 通话结束
         */
        int VOIP_CALL_STATE_CLOSE = BASE + 10;
        
        /**
         * fast组件没有初始化
         */
        int VOIP_UNINIT_SDK = BASE + 11;
        
        /**
         * 获得0条通话记录
         */
        int COMM_GET_NO_COMM_LOG = BASE + 12;
        
        /**
         * 得到通话记录详情
         */
        int COMM_GET_COMM_LOG_DETAIL = BASE + 13;
        
        /**
         * 长按删除通讯记录
         */
        int VOIP_DELETE_COMM_LOG = BASE + 14;
        
        /**
         * 被叫振铃
         */
        int VOIP_IN_CALL_STATE_ALERTING = BASE + 15;
        
        /**
         * 来电接听通话中
         */
        int VOIP_IN_CALL_STATE_TALKING = BASE + 16;
        
        /**
         * 来电通话结束
         */
        int VOIP_IN_CALL_STATE_CLOSE = BASE + 17;
        
        /**
         * 解除绑定时密码错误
         */
        int VOIP_PS_ERROR = BASE + 18;
        
        /**
         * 改变未读通话记录总数的消息
         */
        int VOIP_CHANGE_COMM_LOG_UNREAD_TOTAL = BASE + 19;
        
        /**
         * 刷新通话详情
         */
        int VOIP_CALL_AGAIN = BASE + 20;
        
        /**
         * 账号或密码错误
         */
        int VOIP_ACCOUNT_PS_ERROR = BASE + 21;
        
        /**
         * 添加到通讯录
         */
        int VOIP_ADD_CANTACT = BASE + 22;
        
        /**
         * 二次拨号
         */
        int VOIP_REDIAL = BASE + 23;
        
        /**
         * 绑定解绑时监听消息
         */
        int VOIP_BIND_UNBIND = BASE + 24;
        
        /**
         * 通话计时
         */
        int VOIP_CALLING_COUNT_TIME = BASE + 25;
        
        /**
         * 挂断电话后显示时间
         */
        int VOIP_DISPLAY_TIME = BASE + 26;
        
        /**
         * 挂断电话后不显示时间
         */
        int VOIP_UNDISPLAY_TIME = BASE + 27;
        
        /**
         * 绑定voip时网络异常
         */
        int NTE_ERROR_VOIP_BIND_FAILED = BASE + 28;
        
        /**
         * 已经播出电话
         */
        int VOIP_CALL_OUT = BASE + 29;
        
    }
    
    /**
     * 下载状态类型
     * @author liying00124251
     * @version [RCS Client V100R001C03, 2012-3-29]
     */
    public interface DownloadType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x18000000;
        
        /**
         * 下载开始
         */
        int DOWNLOAD_START = BASE + 1;
        
        /**
         * 下载暂停
         */
        int DOWNLOAD_PAUSE = BASE + 2;
        
        /**
         * 下载停止
         */
        int DOWNLOAD_STOP = BASE + 3;
        
        /**
         * 下载结束 
         */
        int DOWNLOAD_FINISH = BASE + 4;
        
        /**
         * 下载失败
         */
        int DOWNLOAD_FAILED = BASE + 5;
        
        /**
         * 正在下载
         */
        int DOWNLOADING = BASE + 6;
        
    }
    
    /**
     * 上传状态类型
     * @author liying00124251
     * @version [RCS Client V100R001C03, 2012-3-29]
     */
    public interface UPloadType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x19000000;
        
        /**
         * 上传暂停
         */
        int UPLOAD_START = BASE + 1;
        
        /**
         * 上传暂停
         */
        int UPLOAD_PAUSE = BASE + 2;
        
        /**
         * 上传停止
         */
        int UPLOAD_STOP = BASE + 3;
        
        /**
         * 上传结束
         */
        int UPLOAD_FINISH = BASE + 4;
        
        /**
         * 上传失败
         */
        int UPLOAD_FAILED = BASE + 5;
        
        /**
         * 正在下载
         */
        int UPLOADING = BASE + 6;
    }
    
    /**
     * 
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @author 马波
     * @version [RCS Client V100R001C03, 2012-5-3]
     */
    public interface ContactUploadType
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x21000000;
        
        /**
         * 上传开始
         */
        int UPLOAD_START = BASE + 1;
        
        /**
         * 上传暂停
         */
        int UPLOAD_PAUSE = BASE + 2;
        
        /**
         * 上传停止
         */
        int UPLOAD_STOP = BASE + 3;
        
        /**
         * 上传结束
         */
        int UPLOAD_FINISH = BASE + 4;
        
        /**
         * 上传失败
         */
        int UPLOAD_FAILED = BASE + 5;
        
        /**
         * 正在上传
         */
        int UPLOADING = BASE + 6;
    }
}
