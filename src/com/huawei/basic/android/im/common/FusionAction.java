/*
 * 文件名: FusionAction.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: deanye
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.common;

/**
 * 所有UI跳转界面的action定义
 * 
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public interface FusionAction
{
    
    /**
     * 登陆
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface LoginAction
    {
        /**
         * 跳转登陆界面的消息
         */
        String ACTION = "com.huawei.basic.android.im.LOGIN";
        
        /**
         * 请求码 用于标识请求来源
         */
        int REQUEST_CODE = 0x10000001;
        
        /**
         * 登录与注册约定的request code.
         */
        int REQUEST_CODE_REGISTER = 0x00000002;
        
        /**
         * 登录与国家码选择的request code.
         */
        int REQUEST_CODE_SELECTCOUNTRY = 0x00000003;
        
        /**
         * 
         * TODO 范例 定义KEY
         */
        String EXTRA_LOGIN = "login";
        
        /**
         * 注册账号
         */
        String EXTRA_ACCOUNT = "account";
        
        /**
         * 
         * 注册密码
         * 
         */
        String EXTRA_PASSWORD = "password";
        
        /**
         * 
         * 注册类型
         * 
         */
        String EXTRA_REGISTER_TYPE = "register_type";
        
        /**
         * 上传通讯录标志
         */
        String EXTRA_AGREE_UPLOAD_CONTACTS = "agree_update_contacts";
        
        /**
         * 是否被踢出的标识
         */
        String EXTRA_IS_KICK_OUT = "kick_out";
        
        /**
         * 
         * 注册类型，手机号 or 邮箱<BR>
         */
        interface RegisterType
        {
            /**
             * 手机注册
             */
            int MOBILE = 0;
            
            /**
             * 邮箱注册
             */
            int EMAIL = 1;
        }
        
    }
    
    /**
     * 
     * 首次登录页面
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Mar 20, 2012]
     */
    public interface LoginMainAction
    {
        /**
         * 跳转登陆界面的消息
         */
        String ACTION = "com.huawei.basic.android.im.loginmain";
        
    }
    
    /**
     * 
     * 选择国家页面
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Mar 20, 2012]
     */
    public interface SelectCountryAction
    {
        /**
         * 跳转国家码界面的消息
         */
        String ACTION = "com.huawei.basic.android.im.SELECTCOUNTRY";
        
        /**
         * 国家名
         */
        String COUNTRY_NAME = "countryname";
        
        /**
         * 国家码
         */
        String COUNTRY_CODE = "countrycode";
    }
    
    /**
     * 注册
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface RegisterAction
    {
        /**
         * 跳转注册 界面的消息
         */
        String ACTION = "com.huawei.basic.android.im.REGISTER";
        
        /**
         * 手机号码正则验证
         */
        String MOBILEREGULATION = "(\\+86|86|0086)?(13[0-9]|15[0-35-9]|14[57]|18[02356789])\\d{8}";
        
        /**
         * 是否同时上传联系人key值
         */
        String AGREE_UPLOAD_CONTACTS = "agree_upload_contacts";
        
        /**
         * 注册时国家码选择的request code.
         */
        int REQUEST_CODE_SELECTCOUNTRY = 0x00000004;
        
    }
    
    /**
     * 好友主界面
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface FriendTabAction
    {
        /**
         * 好友主界面
         */
        String ACTION = "com.huawei.basic.android.im.FRIENDTAB";
    }
    
    /**
     * 我的好友界面
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface MyFriendAction
    {
        /**
         * 我的好友界面
         */
        String ACTION = "com.huawei.basic.android.im.MY_FRIEND";
        
    }
    
    /**
     * 选择成员Action<BR>
     * 
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    public interface ChooseMemberAction
    {
        /**
         * 选择成员
         */
        String ACTION = "com.huawei.basic.android.im.CHOOSE_MEMBER";
        
        /**
         * 入口类型
         */
        String EXTRA_ENTRANCE_TYPE = "extra_entrance_type";
        
        /**
         * 群组ID参数
         */
        String EXTRA_SECTION_ID = "extra_section_id";
        
        /**
         * 群组名称
         */
        String EXTRA_SECTION_NAME = "extra_section_name";
        
        /**
         * 选中的好友列表（String的方式，“,”隔开）
         */
        String RESULT_CHOOSED_USER_ID_LIST = "choosed_sys_id_list";
        
        /**
         * 群组ID
         */
        String EXTRA_GROUP_ID = "extra_group_id";
        
        /**
         * 当前聊天的好友ID
         */
        String EXTRA_CURRENT_FRIEND_ID = "extra_current_friend_id";
        
        /**
         * 
         * 请求类型 [功能详细描述]
         * 
         * @author i0324
         * @version [RCS Client_Handset V100R001C04SPC002, Mar 3, 2012]
         */
        public interface TYPE
        {
            /**
             * 增加群组入口
             */
            int ADD_SECTION = 0;
            
            /**
             * 群组管理入口
             */
            int MANAGE_SECTION = 1;
            
            /**
             * 请求选中的好友列表
             */
            int REQUEST_FOR_FRIEND_ID_LIST = 2;
            
            /**
             * 添加联系人到群组
             */
            int ADD_CONTACT_TO_SECTION = 3;
            
            /**
             * 移除成员
             */
            int REMOVE_MEMBER = 4;
            
            /**
             * 添加群组成员
             */
            int ADD_GROUP_MEMBER = 5;
            
            /**
             * 删除群组成员
             */
            int DELETE_GROUP_MEMBER = 6;
            
            /**
             * 删除当前聊天好友
             */
            int DELETE_CURRENT_FRIEND = 7;
            
            /**
             * 请求所有的好友列表
             */
            int REQUEST_ALL_FRIEND = 8;
            
            /**
             * 获取好友列表
             */
            int GET_ALL_LIST = 9;
        }
    }
    
    /**
     * 重置密码<BR>
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    public interface ResetPassWordAction
    {
        /**
         * 此MessageType的基数
         */
        int BASE = 0x10000000;
        
        /**
         * 输入组名
         */
        String ACTION = "com.huawei.basic.android.im.RESETPASSWPRD";
        
        /**
         * 是手机号码还是邮箱地址，手机：true,邮箱：false
         */
        String IS_PHONE_OR_EMAIN = "isphoneoremail";
        
        /**
         * 找回密码与国家码选择的request code.
         */
        int REQUEST_CODE_SELECTCOUNTRY = BASE + 1;
    }
    
    /**
     * 登录向导<BR>
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    public interface FeaturesAction
    {
        /**
         * 输入组名
         */
        String ACTION = "com.huawei.basic.android.im.FEATURES";
    }
    
    /**
     * MianTab<BR>
     * 
     * @author tlmao
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    public interface MainTabction
    {
        /**
         * 输入组名
         */
        String ACTION = "com.huawei.basic.android.im.MAINTAB";
    }
    
    /**
     * 群组名称编辑入口<BR>
     * 
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    public interface ContactSectionNameAction
    {
        /**
         * 输入组名
         */
        String ACTION = "com.huawei.basic.android.im.CONTACT_SECTION_NAME";
        
        /**
         * 入口类型
         */
        String EXTRA_EXTRANCE_TYPE = "entrance_type";
        
        /**
         * 组名
         */
        String EXTRA_SECTION_NAME = "section_name";
        
        /**
         * 增加群组
         */
        int TYPE_ADD_SECTION = 0;
        
        /**
         * 编辑群组
         */
        int TYPE_EDIT_SECTION = 1;
    }
    
    /**
     * 好友分组管理<BR>
     * 
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 17, 2012]
     */
    public interface ContactSectionManagerAction
    {
        /**
         * 好友分组管理
         */
        String ACTION = "com.huawei.basic.android.im.CONTACT_SECTION_MANAGER";
        
        /**
         * 分组名称
         */
        String EXTRA_SECTION_NAME = "setion_name";
        
        /**
         * 分组id
         */
        String EXTRA_SECTION_ID = "section_id";
        
        /**
         * 分组中的好友list
         */
        String EXRA_SECTION_FRIENDLIST = "section_friendlist";
    }
    
    /**
     * 找朋友小助手<BR>
     * 
     * @author qlzhou
     * @version [RCS Client V100R001C03, Feb 20, 2012]
     */
    public interface FindFriendHelperAction
    {
        /**
         * 找朋友小助手Action
         */
        String ACTION = "com.huawei.basic.android.im.FIND_FRIEND_HELPER";
    }
    
    /**
     * 查找好友界面
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface FindFriendAction
    {
        /**
         * 查找好友界面
         */
        String ACTION = "com.huawei.basic.android.im.FINDFRIEND";
        
    }
    
    /**
     * 查找好友界面
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface FindFriendResultListAction
    {
        /**
         * 根据ID查找好友界面
         */
        String ACTION = "com.huawei.basic.android.im.FINDFRIENDRESULTLIST";
        
        /**
         * 查找好友类型
         */
        String EXTRA_MODE = "find_mode";
        
        /**
         * 
         * 查找好友类型
         * 
         * @author deanye
         * @version [RCS Client V100R001C03, 2012-2-20]
         */
        public interface MODE
        {
            /**
             * 根据ID查找
             */
            int MODE_FIND_BY_ID = 1;
            
            /**
             * 通过个人资料查找三
             */
            int MODE_FIND_BY_DETAIL = 2;
            
            /**
             * 可能认识的人
             */
            int MODE_FIND_BY_MAYBE_KNOWN = 3;
            
            /**
             * 查找附近的人
             */
            int MODE_FIND_NEAR = 4;
            
            /**
             * 查看电话簿好友
             */
            int MODE_CHECK_MOBILE_FRIENDS = 5;
        }
    }
    
    /**
     * 查看附近的人跳转
     * 
     * @author deanye
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface CheckAroundAction
    {
        /**
         * 查看附近的人跳转
         */
        String ACTION = "com.huawei.basic.im.ui.friend.CheckAroundActivity";
        
    }
    
    /**
     * 查看电话簿好友<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 25, 2012]
     */
    public interface CheckMobileContacts
    {
        /**
         * 查看电话簿好友
         */
        String ACTION = "com.huawei.basic.im.ui.friend.CHECKMOBILECONTACTSACTIVITY";
    }
    
    /**
     * 联系人详情界面
     * 
     * @author 马波
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface ContactDetailAction
    {
        /**
           * STRANGER_CONTACT
           */
        public static final int STRANGER_CONTACT = 3;
        
        /**
         * 根据ID查找好友界面
         */
        String ACTION = "com.huawei.basic.android.im.CONTACTDETAILS";
        
        /**
         * 入口类型
         */
        String BUNDLE_ENTRANCE_TYPE = "bundle_entrance_type";
        
        /**
         * hiTalkId
         */
        String BUNDLE_FRIEND_HITALK_ID = "friend_hitalk_id";
        
        /**
         * 是HiTalk的sysId
         */
        String BUNDLE_FRIEND_LOCAL_ID = "friend_local_id";
        
        /**
         * 群组groupID
         */
        String BUNDLE_FRIEND_GROUP_ID = "friend_group_id";
        
        /**
         * 小助手subService
         */
        String BUNDLE_FRIEND_SERVICE = "friend_group_service";
        
        /**
         * 进入此activity必选参数，标识好友类型：联系人，沃友，好友
         */
        String BUNDLE_CONTACT_MODE = "contact_mode";
        
        /**
         * 是否是好友
         */
        String BUNDLE_IS_FRIEND = "bundle_is_friend";
        
        /**
         * 状态
         */
        String BUNDLE_STATUS = "bundle_status";
        
        /**
         * 子好友类型：联系人类型
         */
        int LOCAL_CONTACT = 0;
        
        /**
         * 子好友类型：HiTalk类型
         */
        int HITALK_CONTACT = 1;
        
        /**
         * 子好友类型：好友类型
         */
        int FRIEND_CONTACT = 2;
        
        /**
         * 入口类型为 找朋友小助手
         */
        int TYPE_FRIEND_HELPER = 4;
        
        /**
         * 请求code,activity 返回时使用
         */
        int EDIT_REQUEST_CODE = 1;
        
        /**
         * 标识类型：加好友还是加群
         */
        String BUNDLE_FRIENDHELPER_SUBSERVICE = "friendhelp_subservice";
        
    }
    
    /**
     * 加好友验证信息界面
     * 
     * @author 马波
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface InputReasonAction
    {
        /**
         * 模式
         */
        String EXTRA_MODE = "EXTRA_MODE";
        
        /**
         * 个性签名
         */
        int MODE_SIGNATURE = 0;
        
        /**
         * 加人请求
         */
        int MODE_REASON = 1;
        
        /**
         * 显示在EditText中的内容
         */
        String EXTRA_CONTENT = "EXTRA_CONTENT";
        
        /**
         * 加好友验证信息录入界面
         */
        String ACTION = "com.huawei.basic.android.im.ADDFRIENDREASON";
        
        /**
         * 加好友验证消息
         */
        String OPERATE_RESULT = "OPERATE_RESULT";
        
    }
    
    /**
     * 查看大图
     * 
     * @author 马波
     * @version [RCS Client V100R001C03, 2012-4-23]
     */
    public interface PhotoAction
    {
        /**
         * 展示大图
         */
        public static final String PHOTO_LARGE = "photo_large";
        
        /**
         * 展示大图的url地址
         */
        public static final String PHOTO_LARGE_URL = "photo_large_url";
        
        /**
         * 根据ID查找好友界面
         */
        String ACTION = "com.huawei.basic.android.im.PHOTO";
    }
    
    /**
     * 
     * 一对一聊天的ACTION及参数定义<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface SingleChatAction extends ChatAction
    {
        /**
         * 进入一对一聊天页面
         */
        String ACTION = "com.huawei.basic.android.im.SINGLE_CHAT";
        
        /**
         * 好友的user id(必选)
         */
        String EXTRA_FRIEND_USER_ID = "friend_user_id";
        
        /**
         * 好友昵称(可选)
         */
        String EXTRA_FRIEND_USER_NICK_NAME = "friend_user_nick_name";
        
        /**
         * 在一对一聊天页面，点击右上角按钮创建群聊时，跳转到选择好友页面的request code.
         */
        int REQUEST_CODE_TO_CHOOSE_MEMBER_FOR_CHAT = 0x00000003;
    }
    
    /**
     * 一对多聊天的ACTION及参数定义 <BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-3-22]
     */
    public interface MultiChatAction extends ChatAction
    {
        /**
         * 进入一对多聊天页面
         */
        String ACTION = "com.huawei.basic.android.im.MULTI_CHAT";
        
        /**
         * 对应群聊的群组id(必选)
         */
        String EXTRA_GROUP_ID = "group_id";
        
        /**
         * 对应群聊的群组名(可选)
         */
        String EXTRA_GROUP_NAME = "group_name";
    }
    
    /**
     * 图片预览界面<BR>
     * @author fanniu
     * @version [RCS Client V100R001C03, 2012-4-17]
     */
    public interface DownloadAction
    {
        /**
         * 进入下载页面
         */
        String ACTION = "com.huawei.basic.android.im.DOWNLOAD";
        
        /**
         * 图片/视频下载后的本地路径
         */
        String EXTRA_PATH = "path";
        
        /**
         * 图片消息ID
         */
        String EXTRA_MSG_ID = "msg_id";
        
        /**
         * 媒体类型
         */
        String EXTRA_MEDIA_TYPE = "media_type";
        
        /**
         * 下载标题
         */
        String EXTRA_TITLE_NAME = "title_name";
        
        /**
         * 下载页面的要下载的多媒体类型
         * <BR>
         * 
         * @author 杨凡
         * @version [RCS Client V100R001C03, 2012-4-26]
         */
        interface DownloadMediaType
        {
            /**
             * 图片
             */
            int IMG = 0;
            
            /**
             * 视频
             */
            int VIDEO = 1;
        }
    }
    
    /**
     * 
     * 聊天<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-2-27]
     */
    public interface ChatAction
    {
        /**
         * 聊天界面与选择表情页面约定的request code.
         */
        int REQUEST_CODE_EMOTION = 0x00000001;
        
        /**
         * 转发消息时，跳转到选择好友页面的request code
         */
        int REQUEST_CODE_TO_CHOOSE_MEMBER_FOR_TRANSFER = 0x00000002;
        
        /**
         * 录制视频时跳转到录制视频页面的request code定义
         */
        int REQUEST_CODE_RECORD_VIDEO = 0x10000003;
        
        /**
         * 图片选择时跳转到Gallery页面的request code
         */
        int REQUEST_CODE_SELECT_PICTURE = 0x10000004;
        
        /**
         * 发送图片时跳转到Chat页面的request code
         */
        int REQUEST_CODE_SEND_IMAGE = 0x10000005;
        
        /**
         * 下载图片时跳转回Chat页面的request code
         */
        int REQUEST_CODE_DOWNLOAD_IMAGE = 0x10000006;
        
        /**
         * 下载图片时跳转回Chat页面的request code
         */
        int REQUEST_CODE_DOWNLOAD_VIDEO = 0x10000007;
        
        /**
         * 跳转系统拍照页面的request code
         */
        int REQUEST_CODE_CAMERA = 0X10000008;
        
        /**
         * 选择的表情占位符
         */
        String EXTRA_EMOTION_STR = "emotion_str";
        
    }
    
    /**
     * 
     * 选择表情<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-2-27]
     */
    public interface PickEmotionAction
    {
        /**
         * 跳转到选择表情页面的ACTION
         */
        String ACTION = "com.huawei.basic.android.im.PICK_EMOTION";
    }
    
    /**
     * 
     * 录制视频<BR>
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-4-5]
     */
    public interface RecordVideoAction
    {
        /**
         * 跳转到录制视频页面的ACTION
         */
        String ACTION = "com.huawei.basic.android.RECORD_VIDEO";
        
        /**
         * 播放或者录制的类型
         */
        String RECORD = "record";
        
        /**
         * 媒体文件的路径
         */
        String MEDIA_PATH = "media_path";
        
        /**
         * 媒体文件的路径
         */
        String NEW_PATH = "new_path";
        
        /**
         * 图像缩略图的路径
         */
        String IMAGE_THUMB_PATH = "image_thumb_path";
        
        /**
         * 视频的播放时长
         */
        String VIDEO_DURATION = "video_duration";
        
        /**
         * 视频的文件大小
         */
        String FILE_SIZE = "file_size";
    }
    
    /**
     * 
     * 设置界面<BR>
     * 
     * @author 周谧
     * @version [RCS Client V100R001C03, 2012-2-27]
     */
    public interface SettingsAction
    {
        /**
         * 标识个人电话
         */
        String FLAG_USER_NAME = "name";
        
        /**
         * 标识个人电话
         */
        String FLAG_USER_PHONE = "phone";
        
        /**
         * 标识个人邮箱
         */
        String FLAG_USER_EMAIL = "email";
        
        /**
         * 绑定邮箱界面
         */
        String ACTION_ACTIVITY_BIND_EMAIL = "com.huawei.basic.android.im.BindEmailActivity";
        
        /**
         * 绑定手机界面
         */
        String ACTION_ACTIVITY_BIND_PHONE = "com.huawei.basic.android.im.BindPhoneActivity";
        
        /**
         *我的账号界面
         */
        String ACTION_ACTIVITY_MY_ACCOUNT = "com.huawei.basic.android.im.MyAccountActivity";
        
        /**
         * 设置界面
         */
        String ACTION_ACTIVITY_SETTINGS = "com.huawei.basic.android.im.SETTINGS";
        
        /**
         * 个人资料界面
         */
        String ACTION_ACTIVITY_PRIVATE_PROFILE_SETTING = "com.huawei.basic.android.im.settings."
                + "PrivateProfileSettingsActivity";
        
        /**
         * 个人资料选择地区界面
         */
        String ACTION_ACTIVITY_REGION_LIST = "com.huawei.basic.android.im.settings.AllRegionsExpandableList";
        
        /**
         * 设置——隐私设置界面
         */
        String ACTION_ACTIVITY_PRIVACY_SETTINGS = "com.huawei.basic.android.im.PRIVACYSETTINGS";
        
        /**
         * 设置——关于||意见反馈
         */
        String ACTION_ACTIVITY_ABOUT_FEEDBACK = "com.huawei.basic.android.im.ABOUTFEEDBACK";
        
        /**
         * 消息设置——群消息接收策略设置
         */
        String ACTION_ACTIVITY_GROUP_MESSAGE = "com.huawei.basic.android.im.GROUPMESSAGE";
        
        /**
         * 设置——消息设置
         */
        String ACTION_ACTIVITY_MESSAGE_TIP_SSETTINGS = "com.huawei.basic.android.im.MESSAGETIPSSETTINGS";
        
        /**
         * 设置——修改密码
         */
        String ACTION_ACTIVITY_MODIFY_PASSWORD = "com.huawei.basic.android.im.MODIFYPASSWORD";
        
        /**
         * 消息设置——广播消息接收设置||免打扰设置
         */
        String ACTION_ACTIVITY_UNDISTURB_SETTINGS = "com.huawei.basic.android.im.UNDISTURBSETTINGS";
        
        /**
         * VOIP绑定
         */
        String ACTION_ACTIVITY_BIND_VOIP = "com.huawei.basic.android.im.BIND_VOIP";
        
        /**
         * 应用界面
         */
        String ACTION_ACTIVITY_APPINFO = "com.huawei.basic.android.im.settings.AppListActivity";
        
        /**
         * 添加应用界面
         */
        String ACTION_ACTIVITY_ADD_APP = "com.huawei.basic.android.im.settings.AddApplyActivity";
        
        /**
         * 应用介绍页面
         */
        String ACTION_WEBVIEW_APP = "com.huawei.basic.android.im.ui.settings.AppWebView";
        
        /**
         * 群消息策略
         */
        String ACTION_ACTIVITY_GROUPMESSAGEPOLICY = "com.huawei.basic.android.im.settings.GroupMessagePolicy";
        
        /**
         * 标志个人资料
         */
        String FLAG_USER_PROFILE = "user_profile";
        
        /**
         * 从设置界面进入的标志
         */
        String FLAG_FROM_SET = "from_set";
        
        /**
         * 进入反馈还是关于
         */
        String EXTRA_ABOUT_TYPE = "extra_type";
        
        /**
         * 进入关于
         */
        String EXTRA_VALUE_ABOUT = "value_about";
        
        /**
         * 进入反馈
         */
        String EXTRA_VALUE_FEEDBACK = "value_feedback";
        
        /**
         * 绑定的类型
         */
        String EXTRA_BIND_TYPE = "bind_type";
        
        /**
         * 手机
         */
        int EXTRA_VALUE_BINDPHONE = 1;
        
        /**
         * 邮箱
         */
        int EXTRA_VALUE_BINDMAIL = 2;
        
        /**
         * 应用图标URL
         */
        String APP_ICON_URL = "app_url";
        
        /**
         * 应用名称
         */
        String APP_ICON_NAME = "app_name";
    }
    
    /**
     * 根据详细资料查找好友界面
     */
    String ACTION_ACTIVITY_FINDFRIENDBYDETAILS = "com.huawei.basic.android.im.FINDFRIENDBYDETAILS";
    
    /**
     * 根据认识的人查找好友界面
     */
    String ACTION_ACTIVITY_FINDFRIENDMAYBEKNOWN = "com.huawei.basic.android.im.FINDFRIENDMAYBEKNOWN";
    
    /**
     * 会话界面
     */
    String ACTION_ACTIVITY_CONVERSATION = "com.huawei.basic.android.im.CONVERSATION";
    
    /**
     * 
     * 通讯录界面:ContactActivity<BR>
     * [功能详细描述]
     * 
     * @author 马波
     * @version [RCS Client V100R001C03, 2012-3-2]
     */
    public interface ContactListAction
    {
        /**
         * 通讯录列表
         */
        
        String ACTION_ACTIVITY_CONTACTS = "com.huawei.basic.android.im.CONTACTS";
        
        /**
         * 同意上传通讯录
         */
        String AGREE_UPLOAD_CONTACTS = "yes";
        
        /**
         * 不同意上传通讯录
         */
        String DISAGREE_UPLOAD_CONTACTS = "no";
    }
    
    /**
     * 我的群列表页面:GroupListActivity<BR>
     * [功能详细描述]
     * 
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-9]
     */
    public interface GroupListAction
    {
        /**
         * 群列表页面
         */
        String ACTION_GROUP_LIST = "com.huawei.basic.android.im.GROUPLIST";
        
        /**
         * 查看列表的类型：群 or 聊吧
         */
        String EXTRA_GROUP_MODE = "group_mode";
        
        /**
         * 群模式
         */
        int GROUP_MODE = 1;
        
        /**
         * 聊吧模式
         */
        int CHAT_BAR_MODE = 2;
    }
    
    /**
     * 
     * 创建群组页面<BR>
     * [功能详细描述]
     * 
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-10]
     */
    public interface GroupCreateAction
    {
        /**
         * 创建群组页面ACTION
         */
        String ACTION_GROUP_CREATE = "com.huawei.basic.android.im.GROUPCREATE";
    }
    
    /**
     * 
     * 修改聊吧名称页面<BR>
     * [功能详细描述]
     * @author fengdai
     * @version [RCS Client V100R001C03, Apr 23, 2012]
     */
    public interface ChatbarNameModifyAction
    {
        /**
         * 修改聊吧名称页面ACTION
         */
        String ACTION_CHATBAR_NAME_MODIFY = "com.huawei.basic.android.group.CHATBARNAMEMODIFY";
        
        /**
         * 聊吧名称
         */
        String EXTRA_CHATBAR_NAME = "chatbar_name";
        
        /**
         * 旧聊吧名称
         */
        String EXTRA_CHATBAR_NAME_OLD = "chatbar_name_old";
    }
    
    /**
     * 
     * voip相关action<BR>
     * [功能详细描述]
     * 
     * @author zhoumi
     * @version [RCS Client V100R001C03, 2012-3-10]
     */
    public interface VoipAction
    {
        /**
         * voip主tab页面
         */
        String ACTION_VOIP_MAIN_TAB = "com.huawei.basic.android.im.ui.voip.VoipTabActivity";
        
        /**
         * voip拨号界面
         */
        String ACTION_VOIP_NUMBER_PAD = "com.huawei.basic.android.im.ui.voip.VoipNumberPadActivity";
        
        /**
         * voip通话界面
         */
        String ACTION_VOIP_CALLING = "com.huawei.basic.android.im.ui.voip.VoipCallingActivity";
        
        /**
         * 通话记录界面的action
         */
        String ACTION_VOIP_COMM_LOG = "com.huawei.basic.android.im.COMMUNICATIONLOG";
        
        /**
         * 通话记录详情界面的action
         */
        String ACTION_VOIP_COMM_DETAIL = "com.huawei.basic.android.im.COMMUNICATIONLOG_DETAIL";
        
        /**
         * 通话记录详情 传入的对方的urI
         */
        String EXTRA_DETAIL_URI = "remoteUri";
        
        /**
         * 通话记录详情 传入的对方的PHONE
         */
        String EXTRA_DETAIL_PHONE_NUM = "remotePhoneNum";
        
        /**
         * 电话号码
         */
        String EXTRA_PHONE_NUMBER = "phone_number";
        
        /**
         * HItalk好友对象
         */
        String EXTRA_CONTACT_INFO_MODEL = "contact_info_model";
        
        /**
         * 来电，去电标志
         */
        String EXTRA_IS_CALL_OUT = "is_call_out";
        
        /**
         * voip账号
         */
        String EXTRAL_VOIP_AOR = "aor";
    }
    
    /**
     * 
     * 查看群成员列表界面
     * 
     * @author fengdai
     * @version [RCS Client V100R001C03, 2012-3-12]
     */
    public interface GroupMemberListAction
    {
        /**
         * 群成员列表
         */
        String ACTION_GROUP_MEMBER_LIST = "com.huawei.basic.android.im.GROUPMEMBERLIST";
        
        /**
         * 获取群ID
         */
        String EXTRA_GROUP_ID = "group_id";
    }
    
    /**
     * 
     * 聊吧成员界面<BR>
     * [功能详细描述]
     * @author fengdai
     * @version [RCS Client V100R001C03, Mar 21, 2012]
     */
    public interface ChatbarMemberAction
    {
        /**
         * 聊吧成员界面
         */
        String ACTION_CHATBAR_MEMBER = "com.huawei.basic.android.im.CHATBARMEMBER";
        
        /**
         * 获取群ID(聊吧ID)
         */
        String EXTRA_GROUP_ID = "group_id";
    }
    
    /**
     * 展示群详情页面ACTION [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @author gaihe
     * @version [RCS Client V100R001C03, 2012-3-13]
     */
    public interface GroupDetailAction
    {
        /**
         * 显示群组详情页面ACTION
         */
        String ACTION_GROUP_DETAIL = "com.huawei.basic.android.im.GROUPDETAIL";
        
        /**
         * 传入model的绑定参数
         */
        String EXTRA_MODEL = "group_info_model";
        
        /**
         * 传入id的绑定参数
         */
        String EXTRA_GROUP_ID = "group_id";
    }
    
    /**
     * 群搜索页面的action [一句话功能简述]<BR>
     * [功能详细描述]
     * 
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-14]
     */
    public interface GroupSearchAction
    {
        /**
         * 显示群搜索页面的action
         */
        String ACTION_GROUP_SEARCH = "com.huawei.basic.android.im.GROUPSEARCH";
        
        /**
         * 搜索的模式
         */
        String EXTRA_MODE = "search_mode";
        
        /**
         * 分类查找的title
         */
        String EXTRA_TITLE = "categroy_title";
        
        /**
         * 模糊查找的search key
         */
        String EXTRA_SEARCH_KEY = "group_search_key";
        
        /**
         * 分类查找的对应分类标识
         */
        String EXTRA_CATEGROY_MODE = "categroy_mode";
        
        /**
         * 根据群ID/关键字查找群
         */
        int MODE_SEARCH_GROUP_BY_KEY = 1;
        
        /**
         * 根据分类查找
         */
        int MODE_SEARCH_GROUP_BY_CATEGROY = 2;
        
        /**
         * 根据选择的分类获得的结果
         */
        int MODE_SEARCH_GROUP_BY_CATEGROY_RESULT = 3;
    }
    
    /**
     * 一些公共信息
     */
    public interface Common
    {
        /**
         * 程序保存shared preferences的名字
         */
        String SHARED_PREFERENCE_NAME = "com.huawei.rcs";
    }
    
    /**
     * 
     * 设置头像的工具activity<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-27]
     */
    public interface SetHeadUtilAction
    {
        /**
         * 跳转的action
         */
        String ACTION = "com.huawei.basic.android.im.ui.settings.SETHEADUTIL";
        
        /**
         * 需要的头像mode
         */
        String EXTRA_MODE = "head_mode";
        
        /**
         * 系统头像需要传进的mode
         */
        String EXTRA_SYSTEM_HEAD_MODE = "system_head_mode";
        
        /**
         * 页面返回的头像URL
         */
        String EXTRA_URL = "head_url";
        
        /**
         * 头像的字节数组
         */
        String EXTRA_BYTES = "head_bytes";
        
        /**
         * 系统头像Mode
         */
        int MODE_SYSTEM = 1;
        
        /**
         * 从文件中选择头像
         */
        int MODE_FILE = 2;
        
        /**
         * 拍照选择头像
         */
        int MODE_CAMERA = 3;
        
    }
    
    /**
     * 
     * 设置系统头像的activity<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [RCS Client V100R001C03, 2012-3-27]
     */
    public interface SetSystemHeadAction
    {
        /**
         * 跳转的action
         */
        String ACTION = "com.huawei.basic.android.im.ui.settings.SETSYSTEMHEAD";
        
        /**
         * 系统头像的Mode
         */
        String EXTRA_MODE = "system_head_mode";
        
        /**
         * 系统头像页面返回的系统头像URL
         */
        String EXTRA_HEAD_URL = "system_head_url";
        
        /**
         * 个人/好友模式
         */
        int MODE_PERSON = 1;
        
        /**
         * 群组模式
         */
        int MODE_GROUP = 2;
    }
    
    /**
     * 
     * 下载图片，图片预览，图片裁剪页面<BR>
     * [功能详细描述]
     * @author tjzhang
     * @version [ME MTVClient_Handset V100R001C04SPC002, 2012-4-20]
     */
    public interface CropImageAction
    {
        /**
         * 跳转的action
         */
        String ACTION = "com.huawei.basic.android.im.ui.basic.image.CROPIMAGE";
        
        /**
         * 页面的模式:1.裁剪模式2.下载查看模式
         */
        String EXTRA_MODE = "ui_mode";
        
        /**
         * 图片的路径
         */
        String EXTRA_PATH = "image_path";


        /**
         * 保存图片存放路径
         */
        String SAVE_PATH = "save_path";
        
        /**
         * 原图压缩后的小图路径
         */
        String EXTRA_SMALL_IMAGE_PATH = "small_image_path";
        
        /**
         * 裁剪模式
         */
        int MODE_CROP = 1;
        
        /**
         * 发送模式
         */
        int MODE_SEND_VIEW = 2;
        
        /**
         * 查看模式
         */
        int MODE_VIEW = 3;
        
        /**
         * 保存模式
         */
        int MODE_SAVE = 4;
        
    }
    
    /**
     * SERVICE
     * 
     * @author qinyangwang
     * @version [RCS Client V100R001C03, 2012-2-16]
     */
    public interface ServiceAction
    {
        /**
         * SERVICE的ACTION NAME.
         */
        String SERVICE_ACTION = "com.huawei.im.service";
        
        /**
         * SERVICE的ACTION NAME.
         */
        String CONNECTIVITY = "connectivity";
        
        /**
         * SERVICE的restar 广播.
         */
        String SERVICE_RECREATE = "com.huawei.im.service.recreate";
    }
    
    /**
     * 插件列表<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 18, 2012]
     */
    public interface PluginListAction
    {
        /**
         * 跳转插件列表的消息d
         */
        String ACTION = "com.huawei.basic.android.im.ui.plugin.PLUGIN";
    }
    
    /**
     * 插件详情<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Apr 18, 2012]
     */
    public interface PluginDetailAction
    {
        /**
         * 跳转插件详情的消息
         */
        String ACTION = "com.huawei.basic.android.im.ui.plugin.PLUGIN_DETAIL";
        
        /**
         * plugin_id 的key
         */
        String EXTRA_PLUGIN_ID = "plugin_id";
        
        /**
         * plugin的key
         */
        String EXTRA_PLUGIN = "plugin";
    }
}
