/*
 * 文件名: BaseParams.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

/**
 * 封装命令，组件以及一些相关的id<BR>
 *
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-18]
 */
public interface BaseParams
{
    /**
     * presence 组件的组件id,命令id,通知id<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-12]
     */
    public interface PresenceParams
    {
        
        /**
         * presence 组件的id
         */
        String FAST_COM_PRESENCE_ID = "{6FFFB8F1-7469-4246-B57C-E2B7B1049E31}";
        
        /**
         * presence命令基数
         */
        int FAST_PRESENCE_CMD_BASEID = 200;
        
        /**
         * presence无效命令
         */
        int FAST_PRESENCE_CMD_NONE = FAST_PRESENCE_CMD_BASEID;
        
        /**
         * 用户通过界面修改了个人信息中的昵称、签名或头像URI
         * <p>
         * 程序将用户修改的个人信息通知到XMPP服务器。  
         * XMPP服务器将用户发布的更新Presence信息广播给用户的联系人列表中
         * 已经订阅了该用户的联系人。
         * </p>
         */
        int FAST_PRESENCE_CMD_PUBLISH = FAST_PRESENCE_CMD_BASEID + 1;
        
        /**
         * 发布离线无效状态
         */
        int FAST_PRESENCE_CMD_UNAVAILABLE = FAST_PRESENCE_CMD_BASEID + 2;
        
        /**
         * 客户端发起添加好友的请求
         */
        int FAST_PRESENCE_CMD_FRIEND_ADD = FAST_PRESENCE_CMD_BASEID + 3;
        
        /**
         * 客户端向XMPP服务器发送同意或者拒绝订阅请求
         */
        int FAST_PRESENCE_CMD_FRIEND_ADDING_CONFIRM = FAST_PRESENCE_CMD_BASEID + 4;
        
        /**
         * 客户端发起删除好友的请求
         */
        int FAST_PRESENCE_CMD_FRIEND_REMOVE = FAST_PRESENCE_CMD_BASEID + 5;
        
        /**
         * 命令总数
         */
        int FAST_PRESENCE_CMD_COUNT = FAST_PRESENCE_CMD_BASEID + 99;
        
        /**
         * presence组件消息基数
         */
        int FAST_PRESENCE_NTF_BASEID = 0x00000FFF;
        
        /**
         * 收到呈现信息通知
         */
        int FAST_PRESENCE_NTF_PRESENCE = FAST_PRESENCE_NTF_BASEID + 1;
        
        /**
         * 好友离线通知
         */
        int FAST_PRESENCE_NTF_UNAVAILABLE = FAST_PRESENCE_NTF_BASEID + 2;
        
        /**
         * 添加好友响应
         */
        int FAST_PRESENCE_NTF_FRIEND_ADD = FAST_PRESENCE_NTF_BASEID + 3;
        
        /**
         * 收到添加好友请求通知（被订阅通知，需返回确认消息）
         */
        int FAST_PRESENCE_NTF_FRIEND_ADDING = FAST_PRESENCE_NTF_BASEID + 4;
        
        /**
         * 收到已经被添加为好友通知（被订阅通知），同时通知发起订阅者和被订阅者。
         */
        int FAST_PRESENCE_NTF_FRIEND_ADDED = FAST_PRESENCE_NTF_BASEID + 5;
        
        /**
         * 收到添加好友被拒绝通知。仅通知订阅发起方。
         */
        int FAST_PRESENCE_NTF_FRIEND_ADD_DECLINED = FAST_PRESENCE_NTF_BASEID + 6;
        
        /**
         * 确认是否同意被加为好友响应。
         */
        int FAST_PRESENCE_NTF_FRIEND_ADDING_CONFIRM = FAST_PRESENCE_NTF_BASEID + 7;
        
        /**
         * 删除好友请求响应（结果）
         */
        int FAST_PRESENCE_NTF_FRIEND_REMOVE = FAST_PRESENCE_NTF_BASEID + 8;
        
        /**
         * 通知好友被删除（通知被删除者）
         */
        int FAST_PRESENCE_NTF_FRIEND_REMOVED = FAST_PRESENCE_NTF_BASEID + 9;
        
    }
    
    /**
     * 消息组件的id,通知id,和命令id<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-12]
     */
    public interface MessageParams
    {
        /**
         * 消息组件id
         */
        String FAST_COM_MESSAGE_ID = "{470DDA2C-3EAB-43db-AEFC-9169069B485C}";
        
        /**
         * 消息命令基数
         */
        int FAST_MESSAGE_CMD_BASEID = 300;
        
        /**
         * 无效消息命令
         */
        int FAST_MESSAGE_CMD_NONE = FAST_MESSAGE_CMD_BASEID;
        
        /**
         * 发送文件命令
         */
        int FAST_MESSAGE_CMD_FILE_SEND = FAST_MESSAGE_CMD_BASEID + 1;
        
        /**
         * 接收文件命令
         */
        int FAST_MESSAGE_CMD_FILE_ACCEPT = FAST_MESSAGE_CMD_BASEID + 2;
        
        /**
         * 拒绝接收文件命令
         */
        int FAST_MESSAGE_CMD_FILE_REJECT = FAST_MESSAGE_CMD_BASEID + 3;
        
        /**
         * 取消发送文件命令
         */
        int FAST_MESSAGE_CMD_FILE_CANCEL = FAST_MESSAGE_CMD_BASEID + 4;
        
        /**
         * 发送广播
         */
        int FAST_MESSAGE_CMD_BROADCAST = FAST_MESSAGE_CMD_BASEID + 11;
        
        /**
         * 发送消息
         */
        int FAST_MESSAGE_CMD_SEND = FAST_MESSAGE_CMD_BASEID + 12;
        
        /**
         * 发送状态报告
         */
        int FAST_MESSAGE_CMD_REPORT = FAST_MESSAGE_CMD_BASEID + 13;
        
        /**
         * 收到服务器消息,需要发回执给服务器,目前仅需要PC客户端使用
         */
        int FAST_MESSAGE_CMD_RESPONSE = FAST_MESSAGE_CMD_BASEID + 14;
        
        /**
         * 命令总数
         */
        int FAST_MESSAGE_CMD_COUNT = FAST_MESSAGE_CMD_BASEID + 99;
        
        /**
         * message通知通知id
         */
        int FAST_MESSAGE_NTF_BASEID = 0x00000FFF;
        
        /**
         * 无效通知
         */
        int FAST_MESSAGE_NTF_NONE = FAST_MESSAGE_NTF_BASEID;
        
        /**
         * 收到文件传输通知。
         */
        int FAST_MESSAGE_NTF_FILE_INVITING = FAST_MESSAGE_NTF_BASEID + 1;
        
        /**
         * 文件传输请求被接受通知
         */
        int FAST_MESSAGE_NTF_FILE_ACCEPTED = FAST_MESSAGE_NTF_BASEID + 2;
        
        /**
         * 文件传输进度通知
         */
        int FAST_MESSAGE_NTF_FILE_STATUS = FAST_MESSAGE_NTF_BASEID + 3;
        
        /**
         * 文件传输关闭通知（原因结束、中断）
         */
        int FAST_MESSAGE_NTF_FILE_CLOSED = FAST_MESSAGE_NTF_BASEID + 4;
        
        /**
         * 收到及时消息。及时消息的类型有message的type属性决定：
         * chat为文本和多媒体消息，sm为短信，im-usage为我有小秘书消息
         */
        int FAST_MESSAGE_NTF_RECEIVED = FAST_MESSAGE_NTF_BASEID + 11;
        
        /**
         * 收到递送报告
         */
        int FAST_MESSAGE_NTF_REPORTED = FAST_MESSAGE_NTF_BASEID + 12;
        
        /**
         * 收到广播通知
         */
        int FAST_MESSAGE_NTF_BROADCAST = FAST_MESSAGE_NTF_BASEID + 13;
        
        /**
         * 收到评论通知
         */
        int FAST_MESSAGE_NTF_COMMENT = FAST_MESSAGE_NTF_BASEID + 14;
        
        /**
         * 收到邮件通知
         */
        int FAST_MESSAGE_NTF_EMAIL = FAST_MESSAGE_NTF_BASEID + 15;
        
        /**
         * 发送消息返回通知
         */
        int FAST_MESSAGE_NTF_SEND = FAST_MESSAGE_NTF_BASEID + 16;
        
        /**
         * 系统公告通知
         */
        int FAST_MESSAGE_NTF_SYS = FAST_MESSAGE_NTF_BASEID + 17;
    }
    
    /**
     * 群组组件id，命令id 和通知id<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-12]
     */
    public interface GroupParams
    {
        /**
         * 群组组件id
         */
        String FAST_COM_GROUP_ID = "{32B8580E-E3D7-4a7a-8919-342A67928489}";
        
        /**
         * 群组命令基数
         */
        int FAST_GROUP_CMD_BASEID = 400;
        
        /**
         * 无效群组命令
         */
        int FAST_GROUP_CMD_NONE = FAST_GROUP_CMD_BASEID;
        
        /**
         * 服务发现
         */
        int FAST_GROUP_CMD_SERVICE_DISCOVERY = FAST_GROUP_CMD_BASEID + 1;
        
        /**
         * 群组创建
         */
        int FAST_GROUP_CMD_CREATE = FAST_GROUP_CMD_BASEID + 2;
        
        /**
         * 邀请加入群组
         */
        int FAST_GROUP_CMD_MEMBER_INVITE = FAST_GROUP_CMD_BASEID + 5;
        
        /**
         * 被邀请者同意加入群组
         */
        int FAST_GROUP_CMD_MEMBER_INVITE_ACCEPT = FAST_GROUP_CMD_BASEID + 6;
        
        /**
         * 被邀请者拒绝加入群组
         */
        int FAST_GROUP_CMD_MEMBER_INVITE_DECLINE = FAST_GROUP_CMD_BASEID + 7;
        
        /**
         * 申请加入群组
         */
        int FAST_GROUP_CMD_MEMBER_JOIN_APPLY = FAST_GROUP_CMD_BASEID + 10;
        
        /**
         * 拒绝他人加入群组
         */
        int FAST_GROUP_CMD_MEMBER_JOIN_DECLINE = FAST_GROUP_CMD_BASEID + 11;
        
        /**
         * 同意他人加入群组
         */
        int FAST_GROUP_CMD_MEMBER_JOIN_ACCEPT = FAST_GROUP_CMD_BASEID + 12;
        
        /**
         * 组员退出
         */
        int FAST_GROUP_CMD_MEMBER_QUIT = FAST_GROUP_CMD_BASEID + 13;
        
        /**
         * 踢出成员
         */
        int FAST_GROUP_CMD_MEMBER_REMOVE = FAST_GROUP_CMD_BASEID + 14;
        
        /**
         * 删除群组
         */
        int FAST_GROUP_CMD_DESTROY = FAST_GROUP_CMD_BASEID + 20;
        
        /**
         * 获取群组列表
         */
        int FAST_GROUP_CMD_GET_GROUP_LIST = FAST_GROUP_CMD_BASEID + 21;
        
        /**
         * 查找群组
         */
        int FAST_GROUP_CMD_SEARCH_GROUP = FAST_GROUP_CMD_BASEID + 22;
        
        /**
         * 获取成员列表
         */
        int FAST_GROUP_CMD_MEMBER_GET_MEMBER_LIST = FAST_GROUP_CMD_BASEID + 23;
        
        /**
         * 分配管理员权限、转让群组
         */
        int FAST_GROUP_CMD_MEMBER_SET_AFFILIATION = FAST_GROUP_CMD_BASEID + 24;
        
        /**
         * 获取群组配置
         */
        int FAST_GROUP_CMD_GET_CONFIG_INFO = FAST_GROUP_CMD_BASEID + 25;
        
        /**
         * 更新群组配置
         */
        int FAST_GROUP_CMD_SUBMIT_CONFIG_INFO = FAST_GROUP_CMD_BASEID + 26;
        
        /**
         * 群组成员更改昵称
         */
        int FAST_GROUP_CMD_MEMBER_CHANGE_NICK = FAST_GROUP_CMD_BASEID + 30;
        
        /**
         * 群组成员更改信息
         */
        int FAST_GROUP_CMD_MEMBER_CHANGE_INFO = FAST_GROUP_CMD_BASEID + 31;
        
        /**
         * 获取成员呈现信息
         */
        int FAST_GROUP_CMD_MEMBER_GET_PRESENCE = FAST_GROUP_CMD_BASEID + 32;
        
        /**
         * 群组发送消息
         */
        int FAST_GROUP_CMD_MESSAGE_SEND = FAST_GROUP_CMD_BASEID + 33;
        
        /**
         * 命令总数
         */
        int FAST_GROUP_CMD_COUNT = FAST_GROUP_CMD_BASEID + 99;
        
        /**
         * 组件通知基数
         */
        int FAST_GROUP_NTF_BASEID = 0x00000FFF;
        
        /**
         * 无效群组通知
         */
        int FAST_GROUP_NTF_NONE = FAST_GROUP_NTF_BASEID;
        
        /**
         * 发现群组业务响应
         */
        int FAST_GROUP_NTF_SERVICE_DISCORVERY = FAST_GROUP_NTF_BASEID + 1;
        
        /**
         * 创建群组响应
         */
        int FAST_GROUP_NTF_CREATE = FAST_GROUP_NTF_BASEID + 2;
        
        /**
         * 邀请加入群组通知
         */
        int FAST_GROUP_NTF_MEMBER_INVITING = FAST_GROUP_NTF_BASEID + 5;
        
        /**
         * 被邀请者，拒绝接受加入群组邀请(通知管理员)
         */
        int FAST_GROUP_NTF_MEMBER_INVITE_DECLINED = FAST_GROUP_NTF_BASEID + 6;
        
        /**
         * 请求加入群组通知（加入受限群组时，通知管理员）
         */
        int FAST_GROUP_NTF_MEMBER_JOIN_APPLING = FAST_GROUP_NTF_BASEID + 10;
        
        /**
         * 申请加入群组者，收到被拒绝加入群组通知
         */
        int FAST_GROUP_NTF_MEMBER_JOIN_DECLINED = FAST_GROUP_NTF_BASEID + 11;
        
        /**
         * 管理员同意申请人加入请求的响应
         */
        int FAST_GROUP_NTF_MEMBER_JOIN_ACCEPT = FAST_GROUP_NTF_BASEID + 12;
        
        /**
         * 成员退出通知（成员退出，所有者退出，成员被踢出）
         */
        int FAST_GROUP_NTF_MEMBER_REMOVED = FAST_GROUP_NTF_BASEID + 15;
        
        /**
         * 踢出成员响应（仅发起踢人者收到）
         */
        int FAST_GROUP_NTF_MEMBER_REMOVE = FAST_GROUP_NTF_BASEID + 16;
        
        /**
         * 通知被提出者（仅被踢出者收到）
         */
        int FAST_GROUP_NTF_MEMBER_KICKED = FAST_GROUP_NTF_BASEID + 17;
        
        /**
         * 删除群组响应（仅群组删除者收到）
         */
        int FAST_GROUP_NTF_DESTROY = FAST_GROUP_NTF_BASEID + 20;
        
        /**
         * 删除群组通知（通知其他成员）
         */
        int FAST_GROUP_NTF_DESTROYED = FAST_GROUP_NTF_BASEID + 21;
        
        /**
         * 获取所属群组列表响应
         */
        int FAST_GROUP_NTF_GET_GOURP_LIST = FAST_GROUP_NTF_BASEID + 22;
        
        /**
         * 搜索群组响应
         */
        int FAST_GROUP_NTF_SEARCH_GROUP = FAST_GROUP_NTF_BASEID + 23;
        
        /**
         * 获取成员列表响应
         */
        int FAST_GROUP_NTF_MEMBER_GET_MEMBER_LIST = FAST_GROUP_NTF_BASEID + 25;
        
        /**
         * 设置成员岗位响应（分配管理员权限、转让群组）需所有者权限
         */
        int FAST_GROUP_NTF_MEMBER_SET_AFFILIATION = FAST_GROUP_NTF_BASEID + 26;
        
        /**
         * 获取群组配置信息响应
         */
        int FAST_GROUP_NTF_GET_CONFIG_INFO = FAST_GROUP_NTF_BASEID + 27;
        
        /**
         * 更新群组配置响应
         */
        int FAST_GROUP_NTF_SUBMIT_CONFIG_INFO = FAST_GROUP_NTF_BASEID + 28;
        
        /**
         * 昵称修改通知（仅通知修改昵称发起者）
         */
        int FAST_GROUP_NTF_MEMBER_CHANGE_NICK = FAST_GROUP_NTF_BASEID + 30;
        
        /**
         * 群组成员更改信息响应
         */
        int FAST_GROUP_NTF_MEMBER_CHANGE_INFO = FAST_GROUP_NTF_BASEID + 31;
        
        /**
         * 获取成员呈现信息响应
         */
        int FAST_GROUP_NTF_MEMBER_GET_PRESENCE = FAST_GROUP_NTF_BASEID + 32;
        
        /**
         * 发送消息失败
         */
        int FAST_GROUP_NTF_MESSAGE_SEND_ERROR = FAST_GROUP_NTF_BASEID + 35;
        
        /**
         * 收到群消息
         */
        int FAST_GROUP_NTF_MESSAGE_RECEIVED = FAST_GROUP_NTF_BASEID + 36;
        
        /**
         * 成员信息通知（新成员加入，指派管理员，所有者变更、修改了成员信息）
         */
        int FAST_GROUP_NTF_MEMBER_INFO = FAST_GROUP_NTF_BASEID + 40;
        
        /**
         * 群配置更新通知
         */
        int FAST_GROUP_NTF_CONFIG_INFO = FAST_GROUP_NTF_BASEID + 41;
        
        /**
         * 成员退出通知
         */
        int FAST_GROUP_NTF_MEMBER_QUIT = FAST_GROUP_NTF_BASEID + 42;
        
        /**
         * 群消息
         */
        int FAST_GROUP_NTF_MESSAGE_SEND = FAST_GROUP_CMD_BASEID + 43;
        
        /**
         * 申请加入群
         */
        int FAST_GROUP_NTF_MEMBER_JOIN_APPLY = FAST_GROUP_NTF_BASEID + 44;
        
        /**
         * 被邀请加入群
         */
        int FAST_GROUP_NTF_MEMBER_INVITE = FAST_GROUP_NTF_BASEID + 45;
        
        /**
         * 邀请加入群，对方同意
         */
        int FAST_GROUP_NTF_MEMBER_INVITE_ACCEPT = FAST_GROUP_NTF_BASEID + 46;
        
        /**
         * 邀请加入群，对方拒绝
         */
        int FAST_GROUP_NTF_MEMBER_INVITE_DECLINE = FAST_GROUP_NTF_BASEID + 47;
        
        /**
         * 管理员拒绝成员加入群
         */
        int FAST_GROUP_NTF_MEMBER_JOIN_DECLINE = FAST_GROUP_NTF_BASEID + 48;
        
    }
    
    /**
     * 定义注册相关的组件id,命令id,和消息id<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-12]
     */
    public interface RegisterParams
    {
        
        /**
         * 组件id
         */
        String FAST_COM_REGISTER_ID = "{EA31BB94-1DCA-402F-AFFA-A18566E584A5}";
        
        /**
         * 注册命令基数
         */
        int FAST_REGISTER_CMD_BASEID = 100;
        
        /**
         * 无效注册命令
         */
        int FAST_REGISTER_CMD_NONE = FAST_REGISTER_CMD_BASEID;
        
        /**
         * 连接命令ID
         */
        int FAST_REGISTER_CMD_REGISTER = FAST_REGISTER_CMD_BASEID + 1;
        
        /**
         * 断开连接命令ID
         */
        int FAST_REGISTER_CMD_DEREGISTER = FAST_REGISTER_CMD_BASEID + 2;
        
        /**
         * 网络状态
         */
        int FAST_REGISTER_CMD_NETSTATE = FAST_REGISTER_CMD_BASEID + 6;
        
        /**
         * 命令总数
         */
        int FAST_REGISTER_CMD_COUNT = FAST_REGISTER_CMD_BASEID + 99;
        
        /**
         * 注册消息基数
         */
        int FAST_REGISTER_NTF_BASEID = 0x00000FFF;
        
        /**
         * 无效注册消息
         */
        int FAST_REGISTER_NTF_NONE = FAST_REGISTER_NTF_BASEID;
        
        /**
         * 注册命令结果通知消息
         */
        int FAST_REGISTER_NTF_REGISTER = FAST_REGISTER_NTF_BASEID + 1;
        
        /**
         * 注销命令结果通知消息
         */
        int FAST_REGISTER_NTF_DEREGISTER = FAST_REGISTER_NTF_BASEID + 2;
        
        /**
         * 网络状态通知
         */
        int FAST_REGISTER_NTF_NETSTATE = FAST_REGISTER_NTF_BASEID + 6;
    }
    
    /**
     * xmppService组件<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-12]
     */
    public interface XmppServiceParams
    {
        /**
         * xmppService组件id
         */
        String FAST_COM_XMPPSERVICE_ID = "{589DBC58-96E4-416b-B946-5B51322788F9}";
        
        /**
         * xmppService组件命令基数
         */
        int FAST_XMPPSERVICE_CMD_BASEID = 0;
        
        /**
         * 无效命令
         */
        int FAST_XMPPSERVICE_CMD_NONE = FAST_XMPPSERVICE_CMD_BASEID;
        
        /**
         * 暂未实现
         */
        int FAST_XMPPSERVICE_CMD_CONFIG = FAST_XMPPSERVICE_CMD_BASEID + 1;
        
        /**
         * 通知基数
         */
        int FAST_XMPPSERVICE_NTF_BASEID = 0x00000FFF;
        
        /**
         * 无效通知
         */
        int FAST_XMPPSERVICE_NTF_NONE = FAST_XMPPSERVICE_NTF_BASEID;
    }
    
    /**
     * 系统命令<BR>
     * 系统命令（不能与其它组件的功能ID重复，不支持参数）
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-12]
     */
    public interface SystemCommand
    {
        /**
         * 系统命令基数
         */
        int FAST_SYSTEM_CMD_BASEID = 0x0000FFFF;
        
        /**
         * 无效命令
         */
        int FAST_SYSTEM_CMD_NONE = FAST_SYSTEM_CMD_BASEID;
        
        /**
         * 初始化命令
         */
        int FAST_SYSTEM_CMD_INIT = FAST_SYSTEM_CMD_BASEID + 1;
        
        /**
         * 去初始化命令
         */
        int FAST_SYSTEM_CMD_UNINIT = FAST_SYSTEM_CMD_BASEID + 2;
        
        /**
         * 激活命令
         */
        int FAST_SYSTEM_CMD_ACTIVE = FAST_SYSTEM_CMD_BASEID + 3;
        
        /**
         * 去激活命令
         */
        int FAST_SYSTEM_CMD_INACTIVE = FAST_SYSTEM_CMD_BASEID + 4;
        
        /**
         * 日志开关
         */
        int FAST_SYSTEM_CMD_LOGSWITCH = FAST_SYSTEM_CMD_BASEID + 5;
        
    }
}
