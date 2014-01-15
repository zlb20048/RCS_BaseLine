/*
 * 文件名: FusionCode.java
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
 * 客户端请求码定义的聚合 //TODO:梳理下
 * 
 * @author deanye
 * @version [RCS Client V100R001C03, 2012-2-14]
 */
public class FusionCode
{
    /**
     * 一些公共信息
     */
    public interface Common
    {
        /**
         * HTTP请求成功返回码
         */
        int RESULT_CODE_SUCCESS = 0;
        
        /**
         * 请求成功返回码
         */
        String RETCODE_SUCCESS = "0";
        
        /**
         * 此MessageType的基数
         */
        int BASE = 0x11000000;
        
        /**
         * 程序保存shared preferences的名字
         */
        String SHARED_PREFERENCE_NAME = "HiTalk";
        
        /**
         * 系统账号字段
         */
        String KEY_USER_SYSID = "userSysId";
        
        /**
         * 账号字段
         */
        String KEY_USER_ID = "userId";
        
        /**
         * 用户账号：根据当前用户的登陆类型改变，邮箱/手机号/用户ID
         */
        String KEY_USER_PASSPORT = "passPort";
        
        /**
         * 用户密码
         */
        String KEY_USER_PASSWORD = "passWord";
        
        /**
         * 是否成功登录
         */
        String KEY_ISLOGIN = "isLogin";
        
        /**
         * 保存在shared preferences中的好友列表视图类型key
         */
        String KEY_VIEW_TYPE = "myfriends_is_shown_by_AZ";
        
        /**
         * 是否首次进入好友列表页面
         */
        String KEY_FIRST_ENTER = "myfriends_first_enter";
        
        /**
         * http连接失败
         */
        String KEY_FAILED = "Failed";
        
        /**
         * http鉴权失败
         */
        String KEY_AUTHERROR = "AuthError";
        
        /**
         * http网络错误
         */
        String KEY_NETWORKERROR = "NetworkError";
        
        /**
         * http请求参数错误
         */
        String KEY_PARAMERROR = "ParamError";
        
        /**
         * http连接超时
         */
        String KEY_TIMEOUT = "Timeout";
        
        /**
         * 首次登陆发送给小秘书的消息
         */
        String MESSAGE_TO_SECRETARY = "first login";
        
        /**
         * 总记录条数，多余50条就删除
         */
        int PAGE_RECORD_COUNT = 50;
        
        /**
         * 通话记录界面记录起始数据
         */
        int PAGE_START_INDEX = 0;
        
        /**
         * 标示是否显示上传位置的提示框
         */
        String SHARE_UPLOAD_LOCATION = "show_upload_location";
        
        /**
         * 标示是否显示GPS的提示框
         */
        String SHARE_GPS_LOCATION = "show_gps_location";
        
        /**
         * 是否创建了VOIP铃音文件
         */
        String KEY_EXISTS_VOIP_RING_FILE = "exists_ring_file";
    }
    
    /**
     * 
     * XMPP服务器业务中一些比较特殊的标识约定如下：
     * 沃友系统：im.wo.com.cn，以系统角色发给用户的消息中的主叫地址均是im.wo.com.cn，比如系统公告等；
     * 沃友小秘书：10010@im.wo.com.cn； 云邮：mail.im.wo.com.cn，主要用于云邮新邮件通知接口，作为主叫地址。
     * 
     * @author 杨凡
     * @version [RCS Client V100R001C03, 2012-4-18]
     */
    public interface XmppConfig
    {
        /**
         * 小秘书JID
         */
        String SECRETARY_JID = "10010@im.wo.com.cn";
        
        /**
         * 小秘书ID
         */
        String SECRETARY_ID = "10010";
        
        /**
         * 系统公告的JID
         */
        String SYSTEM_ANNOUNCEMENT_JID = "im.wo.com.cn";
        
        /**
         * 云邮JID
         */
        String CLOUD_EMAIL_JID = "mail.im.wo.com.cn";
        
        /**
         * 群组服务JID
         */
        String GROUP_JID = "group.im.wo.com.cn";
        
        /**
         * 小秘书系统插件的ID
         */
        String SECRETARY_PLUGIN_ID = "1002";
        
        /**
         * 好友列表中已经存在的错误码
         */
        int FRIEND_LIST_EXISTED = 10001;
    }
    
    /**
     * 
     * 倒计时常量
     * @author tlmao
     * @version [RCS Client V100R001C03, May 26, 2012]
     */
    public interface DelayTime
    {
        /**
         * 倒计时时间
         */
         int DELAYTIME = 60;
         
         /**
          * 立即登录 延时
          */
         int LOGIN_DELAY = 6;
    }
}
