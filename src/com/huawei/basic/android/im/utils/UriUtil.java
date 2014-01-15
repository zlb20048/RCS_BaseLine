/*
 * 文件名: UriUtil.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Feb 16, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.utils;

import java.io.File;

import android.os.Environment;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author tlmao
 * @version [RCS Client V100R001C03, Feb 16, 2012]
 */
public final class UriUtil
{
    
    private static final String TAG = "UriUtil";
    
    private static final String DOMAIN = "im.wo.com.cn";
    
    private static final String WOCLIENT = DOMAIN + "/woclient";
    
    private static final String FORMAT_JID_COMPLETE = "%s@" + WOCLIENT;
    
    private static final String GROUP = "group." + DOMAIN;
    
    private static final String FORMAT_JID_SIMPLE = "%s@" + DOMAIN;
    
    private static final String FORMAT_GROUP_JID_COMPLETE = "%s@" + GROUP;
    
    private static final String FORMAT_MY_GROUP_JID_SIMPLE = "%s@" + GROUP
            + "/%s";
    
    private static final String FORMAT_SM_JID_SIMPLE = "%s@" + DOMAIN
            + "/msisdn";
    
    private static final String APP_NAME = "hitalk";
    
    /**
     * 群发JID
     */
    private static final String TOGETHER_SEND_JID = "multicast." + DOMAIN;
    
    private UriUtil()
    {
    }
    
    /**
     * 
     * 创建xmpp jid
     * 
     * @param hitalkAccount
     *            账号
     * @return xmppJID
     */
    public static String buildXmppJid(String hitalkAccount)
    {
        if (StringUtil.isNullOrEmpty(hitalkAccount))
        {
            Logger.e(TAG, "buildXmppJid error: hitalkAccount is null");
            return "";
        }
        
        return buildJid(hitalkAccount, FORMAT_JID_COMPLETE);
    }
    
    /**
     * 获取ip地址和端口<BR>
     * 
     * @param httpUrl
     *            标准地址串 格式 http://192.168.9.104:5222
     * @return strs数组 1.ip 2.port
     */
    public static String[] resolveHttpUrl(String httpUrl)
    {
        if (httpUrl == null)
        {
            Logger.e(TAG, "httpUrl is null");
            throw new NullPointerException("httpUrl is null");
        }
        String[] strs = new String[2];
        
        try
        {
            String ip = httpUrl.substring(7, httpUrl.lastIndexOf(":"));
            strs[0] = ip;
            String port = httpUrl.substring(httpUrl.lastIndexOf(":") + 1);
            strs[1] = port;
        }
        catch (Exception e)
        {
            Logger.e(TAG, "wrong http url form");
            Logger.e(TAG, "http :" + httpUrl);
            Logger.e(TAG, "stand url: http://192.168.9.104:5222");
        }
        return strs;
    }
    
    /**
     * 获取群发聊天、群发消息的接收者ID
     * 
     * @return 群发jid
     */
    public static String getTogetherImJid()
    {
        return TOGETHER_SEND_JID;
    }
    
    /**
     * 
     * 创建xmpp jid
     * 
     * @param hitalkAccount
     *            账号
     * @return jid
     */
    public static String buildXmppJidNoWo(String hitalkAccount)
    {
        if (StringUtil.isNullOrEmpty(hitalkAccount))
        {
            Logger.w(TAG, "buildXmppJidNoWo error: hitalkAccount is null");
            return "";
        }
        
        return buildJid(hitalkAccount, FORMAT_JID_SIMPLE);
    }
    
    /**
     * 
     * 创建xmpp jid
     * 
     * @param hitalkAccount
     *            账号
     * @return jid
     */
    public static String buildSmXmppJid(String hitalkAccount)
    {
        if (StringUtil.isNullOrEmpty(hitalkAccount))
        {
            Logger.w(TAG, "buildSmXmppJid error: hitalkAccount is null");
            return "";
        }
        
        return buildJid(hitalkAccount, FORMAT_SM_JID_SIMPLE);
    }
    
    /**
     * 
     * 创建我的群组 jid
     * 
     * @param groupId
     *            群组id
     * @param userAccount
     *            账号
     * @return jid
     */
    public static String buildMyXmppGroupJid(String groupId, String userAccount)
    {
        if (StringUtil.isNullOrEmpty(groupId)
                || StringUtil.isNullOrEmpty(userAccount))
        {
            Logger.w(TAG,
                    "buildMyXmppGroupJid error: groupId or userAccount is null");
            return "";
        }
        
        if (groupId.contains("@"))
        {
            if (groupId.contains("/"))
            {
                return groupId;
            }
            else
            {
                return groupId + "/" + userAccount;
            }
        }
        return String.format(FORMAT_MY_GROUP_JID_SIMPLE, groupId, userAccount);
    }
    
    /**
     * 
     * 创建群 jid
     * 
     * @param groupId
     *            群组id
     * @return jid
     */
    public static String buildXmppGroupJID(String groupId)
    {
        if (StringUtil.isNullOrEmpty(groupId))
        {
            Logger.w(TAG, "buildXmppGroupJID error: groupId is null");
            return "";
        }
        
        return buildJid(groupId, FORMAT_GROUP_JID_COMPLETE);
    }
    
    /**
     * 
     * 通过HitalkId获取jid
     * 
     * @param jid
     *            jid
     * @return jid
     */
    public static String getHitalkIdFromJid(String jid)
    {
        if (jid != null && jid.indexOf("@") != -1)
        {
            return jid.substring(0, jid.indexOf("@"));
        }
        return jid;
    }
    /**
     * 
     *通过GroupJid 获取 jid
     * 
     * @param jid
     *            jid
     * @return jid
     */
    public static String getGroupJidFromJid(String jid)
    {
        if (jid != null && jid.indexOf('/') != -1)
        {
            return jid.substring(0, jid.indexOf('/'));
        }
        return jid;
    }
    /**
     * 
     *通过GroupMemberId 获取jid
     * 
     * @param jid
     *            jid
     * @return jid
     */
    public static String getGroupMemberIdFromJid(String jid)
    {
        if (jid != null && jid.indexOf('/') != -1)
        {
            return jid.substring(jid.indexOf('/') + 1);
        }
        return jid;
    }
    
    /**
     * 本地文件夹类型
     */
    public enum LocalDirType
    {
        /**
         * 图片
         */
        IMAGE("image"),
        /**
         * 语音
         */
        VOICE("voice"),
        /**
         * 视频
         */
        VIDEO("video"),
        /**
         * 缩略图
         */
        THUMB_NAIL("thumbnail"),
        /**
         * 下载
         */
        DOWNLOAD("download"),
        /**
         * 头像
         */
        FACE("face"),
        /**
         * VoIP相关的录音文件
         */
        VOIP_RECORD("voip/record"),
        /**
         * 其他临时需要处理的文件
         */
        TEMP("temp"),
        /**
         * 系统相册的目录
         */
        DCIM("DCIM/hitalk"),
        /**
         * 升级文件
         */
        UPGRADE("upgrade"),
        /**
         * 日志文件
         */
        LOG("log"),
        /**
         * 新浪微博
         */
        MBLOG_SINA("mblog/sina"),
        /**
         * im
         */
        IM("im"),
        /**
         * sns
         */
        SNS("sns"),
        /**
         * email
         */
        EMAIL("email");
        
        private String value;
        
        private LocalDirType(String value)
        {
            this.value = value;
        }
        
        public String getValue()
        {
            return value;
        }
    }
    
    /**
     * 来源类型
     */
    public enum FromType
    {
        /**
         * 发送
         */
        SEND("send"),
        /**
         * 接收
         */
        RECEIVE("receive"),
        /**
         * 第三方
         */
        Third("3rd");
        
        private String value;
        
        private FromType(String value)
        {
            this.value = value;
        }
        
        public String getValue()
        {
            return value;
        }
    }
    
    /**
     * 获取本地存储目录
     * 
     * @param userAccount
     *            沃友Id
     * @param fromType
     *            业务类型
     * @param dirType
     *            目录类型
     * @return 本地存储目录
     */
    public static String getLocalStorageDir(String userAccount,
            FromType fromType, LocalDirType dirType)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(Environment.getExternalStorageDirectory().getPath());
        buffer.append("/");
        buffer.append(APP_NAME);
        buffer.append("/");
        if (null != userAccount)
        {
            buffer.append(String.valueOf(userAccount));
            buffer.append("/");
        }
        if (null != fromType)
        {
            buffer.append(fromType.getValue());
            buffer.append("/");
        }
        if (null != dirType)
        {
            buffer.append(dirType.getValue());
            buffer.append("/");
        }
        //如果目录不存在，则创建新目录
        File storeDir = FileUtil.getFileByPath(buffer.toString());
        
        if (null == storeDir)
        {
            return null;
        }
        
        if (!storeDir.exists())
        {
            if (!storeDir.mkdirs())
            {
                return null;
            }
        }
        
        return buffer.toString();
    }
    
    /**
     * 
     * [用于取得系统相册的目录]<BR>
     * [功能详细描述]
     * 
     * @return 本地存储目录
     */
    public static String getDcimStorgeDir()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(Environment.getExternalStorageDirectory().getPath());
        buffer.append("/");
        buffer.append(LocalDirType.DCIM.getValue());
        buffer.append("/");
        return buffer.toString();
    }
    
    private static String buildJid(String src, String format)
    {
        int idx = src.indexOf("@");
        String id = src;
        if (idx >= 0)
        {
            id = src.substring(0, idx);
        }
        
        return String.format(format, id);
    }
}
