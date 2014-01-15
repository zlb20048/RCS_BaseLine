/*
 * 文件名: GroupData.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: Kuaidc
 * 创建时间:2011-11-2
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import java.util.ArrayList;
import java.util.List;

import com.huawei.basic.android.im.component.net.xmpp.data.CommonXmppCmdGenerator.GroupConfigFieldData;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 *  群组功能模块说明<BR>
 *
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-11-2]
 */
public class GroupData
{
    /**
     * 群组命令基类
     * @author 周庆龙
     *
     */
    public static class GroupDataBase
    {
        /**
         * 消息发出者ID from
         */
        private String from = null;
        
        /**
         * 消息接收者ID to
         */
        private String to = null;
        
        /**
         * 设置From字段
         *
         * @param from 发送者jid
         */
        public void setFrom(String from)
        {
            this.from = from;
        }
        
        /**
         * 设置To字段
         *
         * @param to 接收者JID
         */
        public void setTo(String to)
        {
            this.to = to;
        }
        
        /**
         * 生成Group命令的根XML
         * @return String：生成过后的XML格式的字符串，
         * 包括根节点和From节点，如果存在TO，则包含To节点
         */
        public String makeCmdData()
        {
            StringBuilder cmdData = new StringBuilder();
            
            // 创建From和To字段并根据内容添加到CmdData的Xml内容中
            cmdData.append("<group>");
            
            // from和to在一些命令中是必选，在一些命令中是可选，
            //所以根据设置情况确定是否增加字段
            if (null != from && !"".equals(from.trim()))
            {
                cmdData.append("<from>").append(from).append("</from>");
            }
            
            if (null != to && !"".equals(to.trim()))
            {
                cmdData.append("<to>").append(to).append("</to>");
            }
            
            // 添加命令体内容到CmdData中，其子类自己实现对自身的封装
            cmdData.append(getBodyString());
            
            // 形成封闭的CmdData命令内容
            cmdData.append("</group>");
            
            return cmdData.toString();
        }
        
        /**
         * 留给子类实现各自不同的命令XML体内容
         *
         * @return String 格式化成XML后的字符串
         */
        protected String getBodyString()
        {
            return "";
        }
    }
    
    /**
     * 服务发现
     */
    public static class ServiceDiscoveryCmdData extends GroupDataBase
    {
    }
    
    /**
     * 群组创建
     */
    public static class CreateCmdData extends GroupDataBase
    {
        /**
         * groupType
         */
        private String groupType;
        
        /**
         * groupName
         */
        private String groupName;
        
        /**
         * groupNick
         */
        private String groupNick;
        
        /**
         * groupDesc
         */
        private String groupDesc;
        
        /**
         * groupLabel
         */
        private String groupLabel;
        
        /**
         * groupSort
         */
        private int groupSort;
        
        /**
         * 生成当前的xml字符串
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<apply");
            
            // 生成群组类型字段, 用户只能创建三种群:session, open, close,
            //如果为session类型,不填写fixtype
            //<apply grouptype='fixed'  fixtype=’close’>
            if ("session".equals(groupType))
            {
                bodyString.append(" grouptype='session'>");
            }
            else
            {
                bodyString.append(" grouptype='fixed' fixtype='")
                        .append(groupType)
                        .append("'>");
            }
            
            bodyString.append("<groupname>")
                    .append(StringUtil.encodeString(groupName))
                    .append("</groupname>");
            bodyString.append("<groupnick>")
                    .append(StringUtil.encodeString(groupNick))
                    .append("</groupnick>");
            bodyString.append("<groupsort>")
                    .append(groupSort)
                    .append("</groupsort>");
            
            if (groupDesc != null)
            {
                bodyString.append("<groupdesc>")
                        .append(StringUtil.encodeString(groupDesc))
                        .append("</groupdesc>");
            }
            
            if (groupLabel != null)
            {
                bodyString.append("<grouplabel>")
                        .append(StringUtil.encodeString(groupLabel))
                        .append("</grouplabel>");
            }
            
            bodyString.append("</apply>");
            
            return bodyString.toString();
        }
        
        /**
         * 设置群组类型
         * @param groupType 手机用户只能创建临时群，参数值是session
         */
        public void setGroupType(String groupType)
        {
            this.groupType = groupType;
        }
        
        /**
         * 设置群组名称
         * @param groupName the groupName to set
         */
        public void setGroupName(String groupName)
        {
            this.groupName = groupName;
        }
        
        /**
         * 设置群在中的昵称
         * @param groupNick the groupNick to set
         */
        public void setGroupNick(String groupNick)
        {
            this.groupNick = groupNick;
        }
        
        /**
         * 设置群描述
         * @param groupDesc the groupDesc to set
         */
        public void setGroupDesc(String groupDesc)
        {
            this.groupDesc = groupDesc;
        }
        
        /**
         * 设置群标签
         * @param groupLabel the groupLabel to set
         */
        public void setGroupLabel(String groupLabel)
        {
            this.groupLabel = groupLabel;
        }
        
        /**
         * 设置群分类
         * @param groupSort the groupSort to set
         */
        public void setGroupSort(int groupSort)
        {
            this.groupSort = groupSort;
        }
    }
    
    /**
     * 邀请加入群组
     */
    public static class MemberInviteCmdData extends GroupDataBase
    {
        /**
         * inviteItem
         */
        private List<String[]> inviteItem;
        
        /**
         * person
         */
        private PersonData person;
        
        /**
         * reason
         */
        private String reason;
        
        /**
         * 类构造函数，初始化成员变量
         */
        public MemberInviteCmdData()
        {
            person = new PersonData();
        }
        
        /**
         * 个人信息结构，包含Nick和Logo两个元素
         * @author kuaidc
         */
        public static class PersonData
        {
            /**
             * nick
             */
            private String nick;
            
            /**
             * logo
             */
            private String logo;
            
            /**
             * getNick
             * @return the nick
             */
            public String getNick()
            {
                return nick;
            }
            
            /**
             * setNick
             * @param nick the nick to set
             */
            public void setNick(String nick)
            {
                this.nick = nick;
            }
            
            /**
             * getLogo
             * @return the logo
             */
            public String getLogo()
            {
                return logo;
            }
            
            /**
             * setLogo
             * @param logo the logo to set
             */
            public void setLogo(String logo)
            {
                this.logo = logo;
            }
        }
        
        /**
         * getBodyString
         * @return body
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<invite>");
            
            for (String[] item : inviteItem)
            {
                bodyString.append("<item to='").append(item[0]);
                
                if (item[1] != null)
                {
                    bodyString.append("'><groupnick>")
                            .append(StringUtil.encodeString(item[1]))
                            .append("</groupnick></item>");
                }
                else
                {
                    bodyString.append("'/>");
                }
            }
            
            if (person != null)
            {
                bodyString.append("<person nick='")
                        .append(StringUtil.encodeString(person.nick))
                        .append("' logo='")
                        .append(StringUtil.encodeString(person.logo))
                        .append("'/>");
                
                bodyString.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            bodyString.append("</invite>");
            return bodyString.toString();
        }
        
        /**
         * 设置原因
         * @param reason the reason to set
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
        
        /**
         * 设置邀请者个人信息
         * @param nick the person to set
         * @param logo logo
         */
        public void setPerson(String nick, String logo)
        {
            person.nick = nick;
            person.logo = logo;
        }
        
        /**
         * 增加邀请者成员列表
         * @param itemList 传入参数是邀请者列表
         */
        public void addItem(List<String[]> itemList)
        {
            inviteItem = itemList;
        }
        
    }
    
    /**
     * 被邀请者同意加入群组
     */
    public static class MemberInviteAcceptCmdData extends GroupDataBase
    {
        /*
         * <group> <from>Bob@chinaunicom.com/woclient</from> <to>88888888@group.chinaunicom.com</to> <apply pcpolicy='2'
         * mobilepolicy='2'> <groupnick>Tiger</groupnick> <describe>I am good at running!</describe> </apply> </group>
         */
        /**
         * 接收消息策略默认为2: 接收并提示消息
         */
        private int pcPolicy = 2;
        
        /**
         * mobilePolicy
         */
        private int mobilePolicy = 2;
        
        /**
         * groupNick
         */
        private String groupNick;
        
        /**
         * describe
         */
        private String describe;
        
        /**
         * 生成同意命令的xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<apply pcpolicy='")
                    .append(pcPolicy)
                    .append("' mobilepolicy='")
                    .append(mobilePolicy)
                    .append("'>");
            bodyString.append("<groupnick>")
                    .append(StringUtil.encodeString(groupNick))
                    .append("</groupnick>");
            
            if (describe != null)
            {
                bodyString.append("<describe>")
                        .append(StringUtil.encodeString(describe))
                        .append("</describe>");
            }
            
            bodyString.append("</apply>");
            return bodyString.toString();
        }
        
        /**
         * 设置PC上的消息策略
         * @param pcpolicy PC终端群消息接收策略 1）自动弹出消息；
         * 2）接收并提示消息；3）接收不提示消息； 
         * 4）不提示消息只显示数目（屏蔽但是显示消息数目）；
         * 5）完全屏蔽群内消息；
         * 6）屏蔽群内图片；注：系统默认为2
         */
        public void setPCPolicy(int pcpolicy)
        {
            pcPolicy = pcpolicy;
        }
        
        /**
         * 设置手机上的消息策略
         * @param mobilePolicy the mobilePolicy to set Mobile终端群消息接收策略
         *  1）自动弹出消息；
         *  2）接收并提示消息；
         *  3）接收不提示消息；
         *  4）不提示消息只显示数目（屏蔽但是显示消息数目）；
         *  5）完全屏蔽群内消息； 6）屏蔽群内图片；注：系统默认为2
         */
        public void setMobilePolicy(int mobilePolicy)
        {
            this.mobilePolicy = mobilePolicy;
        }
        
        /**
         * 昵称
         * @param groupNick the groupNick to set
         */
        public void setGroupNick(String groupNick)
        {
            this.groupNick = groupNick;
        }
        
        /**
         * 设置描述
         * @param describe the describe to set
         */
        public void setDescribe(String describe)
        {
            this.describe = describe;
        }
        
    }
    
    /**
     * 被邀请者拒绝加入群组
     */
    public static class MemberInviteDeclineCmdData extends GroupDataBase
    {
        /*
         * <group> <from>Jim@chinaunicom.com/woclient</from> <to>88888888@group.chinaunicom.com</to> <decline
         * to='Bob@chinaunicom.com'> <reason>Sorry, I'm too busy right now.</reason> </decline> </group>
         */
        /**
         * declineTo
         */
        private String declineTo;
        
        /**
         * reason
         */
        private String reason;
        
        /**
         * 生成邀请拒绝的xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<decline to='").append(declineTo).append("'>");
            
            if (reason != null)
            {
                bodyString.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            bodyString.append("</decline>");
            return bodyString.toString();
        }
        
        /**
         * 设置拒绝对象jid
         * @param declineTo the declineTo to set
         */
        public void setDeclineTo(String declineTo)
        {
            this.declineTo = declineTo;
        }
        
        /**
         * 设置拒绝原因
         * @param reason the reason to set
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
    }
    
    /**
     * 申请加入群组
     */
    public static class MemberJoinApplyCmdData extends GroupDataBase
    {
        /**
         * PCPolicy
         */
        private int pCPolicy = 2;
        
        /**
         * mobilePolicy
         */
        private int mobilePolicy = 2;
        
        /**
         * nick
         */
        private String nick;
        
        /**
         * describe
         */
        private String describe;
        
        /**
         * reason
         */
        private String reason;
        
        /**
         * 生成申请加入群组xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<apply pcpolicy='")
                    .append(pCPolicy)
                    .append("' mobilepolicy='")
                    .append(mobilePolicy)
                    .append("'>");
            
            if (describe != null)
            {
                bodyString.append("<describe>")
                        .append(StringUtil.encodeString(describe))
                        .append("</describe>");
            }
            
            if (reason != null)
            {
                bodyString.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            bodyString.append("<nick>")
                    .append(StringUtil.encodeString(nick))
                    .append("</nick>");
            bodyString.append("</apply>");
            return bodyString.toString();
        }
        
        /**
         * 设置PC上的消息策略
         * @param pcpolicy the pCPolicy to set
         */
        public void setPCPolicy(int pcpolicy)
        {
            pCPolicy = pcpolicy;
        }
        
        /**
         * 设置手机上的消息策略
         * @param mobilePolicy the mobilePolicy to set
         */
        public void setMobilePolicy(int mobilePolicy)
        {
            this.mobilePolicy = mobilePolicy;
        }
        
        /**
         * 设置群组昵称
         * @param pNick the groupNick to set
         */
        public void setGroupNick(String pNick)
        {
            this.nick = pNick;
        }
        
        /**
         * 设置描述信息
         * @param describe the describe to set
         */
        public void setDescribe(String describe)
        {
            this.describe = describe;
        }
        
        /**
         * 设置原因
         * @param reason the reason to set
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
        
    }
    
    /**
     * 拒绝他人加入群组
     */
    public static class MemberJoinDeclineCmdData extends GroupDataBase
    {
        /*
         * <group> <from>Jim@chinaunicom.com/woclient</from> <to>88888888@group.chinaunicom.com</to> <decline
         * to='1000003@chinaunicom.com/woclient'> <reason>Sorry, I'm busy now.</reason> </decline> </group>
         */
        /**
         * declineTo
         */
        private String declineTo;
        
        /**
         * reason
         */
        private String reason;
        
        /**
         * 生成拒绝他人命令的xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<decline to='").append(declineTo).append("'>");
            
            if (reason != null)
            {
                bodyString.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            bodyString.append("</decline>");
            return bodyString.toString();
        }
        
        /**
         * 设置拒绝对象
         * @param declineTo the declineTo to set
         */
        public void setDeclineTo(String declineTo)
        {
            this.declineTo = declineTo;
        }
        
        /**
         * 设置拒绝原因
         * @param reason the reason to set
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
    }
    
    /**
     * 同意他人加入群组
     */
    public static class MemberJoinAcceptCmdData extends GroupDataBase
    {
        /**
         * 授予申请者的群组岗位 member
         */
        private String affiliation;
        
        /**
         * 申请者的JID
         */
        private String jid;
        
        /**
         * 生成同意他人加入群组的xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query>");
            bodyString.append("<item affiliation='")
                    .append(affiliation)
                    .append("' ");
            bodyString.append("jid='").append(jid).append("'/>");
            bodyString.append("</query>");
            return bodyString.toString();
        }
        
        /**
         * 设置岗位
         * @param affiliation the affiliation to set
         */
        public void setAffiliation(String affiliation)
        {
            this.affiliation = affiliation;
        }
        
        /**
         * 设置被设置者的jid
         * @param jid the jid to set
         */
        public void setJid(String jid)
        {
            this.jid = jid;
        }
    }
    
    /**
     * 组员退出
     */
    public static class MemberQuitCmdData extends GroupDataBase
    {
    }
    
    /**
     * 踢出成员
     */
    public static class MemberRemoveCmdData extends GroupDataBase
    {
        
        /**
         * 被踢者在群组中的群组成员全JID
         */
        private String memberJid;
        
        /**
         * 踢出群组原因
         */
        private String reason;
        
        /**
         * affiliation
         */
        private String affiliation;
        
        /**
         * 生成踢出成员命令的xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query>");
            bodyString.append("<item memberjid='")
                    .append(memberJid)
                    .append("' affiliation='")
                    .append(affiliation)
                    .append("'>");
            
            if (reason != null)
            {
                bodyString.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            bodyString.append("</item></query>");
            return bodyString.toString();
        }
        
        /**
         * 设置成员jid
         * @param memberJid the memberJid to set
         */
        public void setMemberJid(String memberJid)
        {
            this.memberJid = memberJid;
        }
        
        /**
         * 设置原因
         * @param reason the reason to set
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
        
        /**
         * 设置岗位
         * @param affiliation 岗位名称，踢出成员时，设置为none
         */
        public void setAffiliation(String affiliation)
        {
            this.affiliation = affiliation;
        }
    }
    
    /**
     * 删除群组
     */
    public static class DestroyCmdData extends GroupDataBase
    {
        /**
         * reason
         */
        private String reason = "";
        
        /**
         * 生成删除群组的xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query><destroy>");
            
            if (reason != null)
            {
                bodyString.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            bodyString.append("</destroy></query>");
            return bodyString.toString();
        }
        
        /**
         * 设置原因
         * @param reason the reason to set
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
        
    }
    
    /**
     * 获取所属群组列表
     */
    public static class GetGroupListCmdData extends GroupDataBase
    {
        /**
         * 指明查询者的群组岗位 可以为owner/member/admin/none 
         * 若不指定则返回该查询者创建和加入的所有群组列表
         */
        private String affiliation;
        
        /**
         * 指明查询者的群组岗位
         * 可以为owner/member/admin/none
         * 若不指定则返回该查询者创建和加入的所有群组列表
         * @param affiliation 岗位
         */
        public void setAffiliation(String affiliation)
        {
            this.affiliation = affiliation;
        }
        
        /**
         * 生成获取群组列表命令的xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query><item");
            
            if (affiliation != null)
            {
                bodyString.append(" affiliation='")
                        .append(this.affiliation)
                        .append("'");
            }
            
            bodyString.append("/></query>");
            return bodyString.toString();
        }
    }
    
    /**
     * 搜索群组
     */
    public static class SearchGroupCmdData extends GroupDataBase
    {
        /**
         * 搜索类型，分：精确搜索，模糊搜索，手机综合搜索，分类搜索四种
         */
        private String searchType;
        
        /**
         * 搜索关键字
         */
        private String searchKey;
        
        /**
         * 分页序号
         */
        private int pageID;
        
        /**
         * 分页大小
         */
        private int pageSize;
        
        /**
         * 生成xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query>");
            bodyString.append("<item cndtype='")
                    .append(searchType)
                    .append("' cndvalue='");
            bodyString.append(StringUtil.encodeString(searchKey))
                    .append("' pageid='")
                    .append(pageID);
            bodyString.append("' pagesize='").append(pageSize).append("'/>");
            bodyString.append("</query>");
            return bodyString.toString();
        }
        
        /**
         * 设置搜索群组的方式
         * @param searchType mobile手机综合,exact精确,fuzzy模糊,sort分类
         */
        public void setSearchType(String searchType)
        {
            this.searchType = searchType;
        }
        
        /**
         * 设置搜索关键字
         * @param searchKey the searchKey to set
         */
        public void setSearchKey(String searchKey)
        {
            this.searchKey = searchKey;
        }
        
        /**
         * 设置搜索开始页id
         * @param pageID the pageID to set
         */
        public void setPageID(int pageID)
        {
            this.pageID = pageID;
        }
        
        /**
         * 设置搜索页大小
         * @param pageSize the pageSize to set
         */
        public void setPageSize(int pageSize)
        {
            this.pageSize = pageSize;
        }
    }
    
    /**
     * 获取成员列表
     */
    public static class MemberGetMemberListCmdData extends GroupDataBase
    {
        /**
         * pageID
         */
        private int pageID;
        
        /**
         * pageSize
         */
        private int pageSize;
        
        /**
         * 生成xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query>");
            bodyString.append("<item pageid='").append(pageID);
            bodyString.append("' pagesize='").append(pageSize).append("'/>");
            bodyString.append("</query>");
            return bodyString.toString();
        }
        
        /**
         * 设置起始页id
         * @param pageID the pageID to set
         */
        public void setPageID(int pageID)
        {
            this.pageID = pageID;
        }
        
        /**
         * 设置分页大小
         * @param pageSize the pageSize to set
         */
        public void setPageSize(int pageSize)
        {
            this.pageSize = pageSize;
        }
    }
    
    /**
     * 分配管理员权限、转让群组
     */
    public static class MemberSetAffiliationCmdData extends GroupDataBase
    {
        
        /**
         * 分配的权限，有owner和admin两种
         */
        private String affiliation;
        
        /**
         * 被授权人JID
         */
        private String jid;
        
        /**
         * reason
         */
        private String reason;
        
        /**
         * 生成xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query>");
            bodyString.append("<item affiliation='").append(affiliation);
            bodyString.append("' jid='").append(jid).append("'>");
            
            if (reason != null)
            {
                bodyString.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            bodyString.append("</item></query>");
            return bodyString.toString();
        }
        
        /**
         * 设置岗位
         * @param affiliation 权限:管理员admin, 群主owner,普通成员member
         */
        public void setAffiliation(String affiliation)
        {
            this.affiliation = affiliation;
        }
        
        /**
         * 设置jid
         * @param jid the jid to set
         */
        public void setJid(String jid)
        {
            this.jid = jid;
        }
        
        /**
         * 设置原因
         * @param reason the reason to set
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
    }
    
    /**
     * 获取群组配置
     */
    public static class GetConfigInfoCmdData extends GroupDataBase
    {
    }
    
    /**
     * 更新群组配置
     */
    public static class SubmitConfigInfoCmdData extends GroupDataBase
    {
        /**
         * fieldList
         */
        private List<GroupConfigFieldData> fieldList;
        
        /**
         * 生成xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query><x>");
            
            if (fieldList != null)
            {
                int listSize = fieldList.size();
                
                for (int i = 0; i < listSize; ++i)
                {
                    bodyString.append(fieldList.get(i).getBodyString());
                }
            }
            
            bodyString.append("</x></query>");
            return bodyString.toString();
        }
        
        /**
         * 设置list
         * @param list 配置信息列表
         */
        public void setFieldList(List<GroupConfigFieldData> list)
        {
            fieldList = list;
        }
        
        /**
         * 增加单个个Field到当前列表
         *
         * @param field field
         */
        public void addField(GroupConfigFieldData field)
        {
            if (fieldList == null)
            {
                fieldList = new ArrayList<GroupConfigFieldData>();
            }
            
            fieldList.add(field);
        }
    }
    
    /**
     * 群组成员更改昵称
     */
    public static class MemberChangeNickCmdData extends GroupDataBase
    {
        /**
         * groupNick
         */
        private String groupNick;
        
        /**
         * 生成xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<groupnick>")
                    .append(StringUtil.encodeString(groupNick))
                    .append("</groupnick>");
            return bodyString.toString();
        }
        
        /**
         * 设置昵称
         * @param groupNick the groupNick to set
         */
        public void setGroupNick(String groupNick)
        {
            this.groupNick = groupNick;
        }
        
    }
    
    /**
     * 群组成员更改信息
     */
    public static class MemberChangeInfoCmdData extends GroupDataBase
    {
        /**
         * PCPolicy
         */
        private int pCPolicy = 2;
        
        /**
         * mobilePolicy
         */
        private int mobilePolicy = 2;
        
        /**
         * logo
         */
        private String logo;
        
        /**
         * describe
         */
        private String describe;
        
        /**
         * 生成xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query>");
            bodyString.append("<item ");
            
            if (describe != null)
            {
                bodyString.append(" desc='")
                        .append(StringUtil.encodeString(describe))
                        .append("'");
            }
            
            if (logo != null)
            {
                bodyString.append(" logo='")
                        .append(StringUtil.encodeString(logo))
                        .append("' ");
            }
            
            bodyString.append("pcpolicy='")
                    .append(pCPolicy)
                    .append("' mobilepolicy='")
                    .append(mobilePolicy)
                    .append("'/>");
            bodyString.append("</query>");
            
            return bodyString.toString();
        }
        
        /**
         * 设置PC消息策略
         * @param pcpolicy the pCPolicy to set
         */
        public void setPCPolicy(int pcpolicy)
        {
            pCPolicy = pcpolicy;
        }
        
        /**
         * 设置手机消息策略
         * @param mobilePolicy the mobilePolicy to set
         */
        public void setMobilePolicy(int mobilePolicy)
        {
            this.mobilePolicy = mobilePolicy;
        }
        
        /**
         * 设置用户头像url
         * @param logo 用户头像地址
         */
        public void setLogo(String logo)
        {
            this.logo = logo;
        }
        
        /**
         * 设置描述
         * @param describe the describe to set
         */
        public void setDescribe(String describe)
        {
            this.describe = describe;
        }
    }
    
    /**
     * 获取成员呈现信息
     */
    public static class MemberGetPresenceCmdData extends GroupDataBase
    {
        /**
         * 需查询的群组成员列表，内容填写成员的成员全JID
         */
        private List<String> itemList;
        
        /**
         * 生成xml
         * @return BodyString
         */
        @Override
        protected String getBodyString()
        {
            StringBuilder bodyString = new StringBuilder();
            bodyString.append("<query>");
            
            for (String item : itemList)
            {
                bodyString.append("<item memberjid='")
                        .append(item)
                        .append("'/>");
            }
            
            bodyString.append("</query>");
            return bodyString.toString();
        }
        
        /**
         * 增加邀请者成员列表
         *
         * @param pItemList 传入参数是邀请者列表
         */
        public void addItem(List<String> pItemList)
        {
            this.itemList = pItemList;
        }
    }
    
    /**
     * 群组发送消息
     */
    public static class MessageSendCmdData extends GroupDataBase
    {
        /**
         * message
         */
        private MessageCommonClass.CommonMessageData message;
        
        /**
         * 生成xml
         * @return getBodyString
         */
        @Override
        protected String getBodyString()
        {
            return message.getBodyString();
        }
        
        /**
         * 设置消息
         * @param message 消息详情
         */
        public void setMessage(MessageCommonClass.CommonMessageData message)
        {
            this.message = message;
        }
    }
    
}
