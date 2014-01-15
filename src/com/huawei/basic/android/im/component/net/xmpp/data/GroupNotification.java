/*
 * 文件名: GroupNotification.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import com.huawei.basic.android.im.component.log.Logger;

/**
 * 群组消息响应通知类<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-11-2]
 */
public class GroupNotification
{
    
    /**
     * 发现群组业务响应
     */
    @Root(name = "group", strict = false)
    public static class ServiceDiscorveryNtfData extends BaseNotification
    {
        
        @Element(name = "query", required = false)
        private QueryNtf query;
        
        public QueryNtf getQuery()
        {
            return query;
        }
        
        public void setQuery(QueryNtf query)
        {
            this.query = query;
        }
        
        /**
         * query<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-3]
         */
        @Root(name = "query")
        public static class QueryNtf
        {
            /**
             * xmlns
             */
            @Attribute(name = "xmlns")
            private String xmlns;
            
            /**
             * identity
             */
            @Element(name = "identity", required = false)
            private IdentityNtf identity;
            
            /**
             * feature
             */
            @Element(name = "feature", required = false)
            private FeatureNtf feature;
            
            public String getXmlns()
            {
                return xmlns;
            }
            
            public void setXmlns(String xmlns)
            {
                this.xmlns = xmlns;
            }
            
            public IdentityNtf getIdentity()
            {
                return identity;
            }
            
            public void setIdentity(IdentityNtf identity)
            {
                this.identity = identity;
            }
            
            public FeatureNtf getFeature()
            {
                return feature;
            }
            
            public void setFeature(FeatureNtf feature)
            {
                this.feature = feature;
            }
            
        }
        
        /**
         * identity<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-3]
         */
        public static class IdentityNtf
        {
            
            /**
             * category
             */
            @Attribute(name = "category", required = false)
            private String category;
            
            /**
             * name
             */
            @Attribute(name = "name", required = false)
            private String name;
            
            /**
             * type
             */
            @Attribute(name = "type", required = false)
            private String type;
            
            public String getCategory()
            {
                return category;
            }
            
            public void setCategory(String category)
            {
                this.category = category;
            }
            
            public String getName()
            {
                return name;
            }
            
            public void setName(String name)
            {
                this.name = name;
            }
            
            public String getType()
            {
                return type;
            }
            
            public void setType(String type)
            {
                this.type = type;
            }
            
        }
        
        /**
         * feature<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-3]
         */
        @Root(name = "feature")
        public static class FeatureNtf
        {
            /**
             * var
             */
            @Attribute(name = "var", required = false)
            private String var;
            
            public String getVar()
            {
                return var;
            }
            
            public void setVar(String var)
            {
                this.var = var;
            }
            
        }
    }
    
    /**
     * 创建群组响应
     */
    @Root(name = "group", strict = false)
    public static class CreateNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com/1000001</from>
        // <to>1000001@chinaunicom.com/woclient</to>
        // <x xmlns='http://jabber.org/protocol/group#user'>
        // <item affiliation='owner' id='create1' grouptype='close'
        // maxgroupsize='12'>
        // <groupname>footballteam</groupname>
        // <groupnick>Sweetcat</groupnick>
        // <groupdesc>The place for all football team member! </groupdesc>
        // <groupsort>3</groupsort>
        // <grouplabel>football,team</grouplabel>
        // </item>
        // <status code='110'/>
        // <status code='201'/>
        // </x>
        // </group>
        
        @Element(name = "x", required = false)
        @Namespace(reference = "http://jabber.org/protocol/group#user")
        private XNtf xNtf;
        
        /**
         * 获取x结构
         * 
         * @return XNtf
         */
        public XNtf getxNtf()
        {
            return xNtf;
        }
        
        /**
         * 设置x结构
         * 
         * @param pXNtf 解析到的XNtf
         */
        public void setxNtf(XNtf pXNtf)
        {
            this.xNtf = pXNtf;
        }
        
        /**
         * XNtf<BR>
         * @author qlzhou
         * @version [RCS Client V100R001C03, Mar 1, 2012]
         */
        @Root(strict = false)
        public static class XNtf
        {
            /**
             * item
             */
            @Element(name = "item", required = false)
            private Item item;
            
            @ElementList(entry = "status", required = false, inline = true)
            private List<StatusCode> status;
            
            /**
             * 设置状态
             * 
             * @param pStatus status
             */
            public void setStatus(List<StatusCode> pStatus)
            {
                this.status = pStatus;
            }
            
            /**
             * 获取状态码
             * 
             * @return 状态码列表
             */
            public List<StatusCode> getStatus()
            {
                return this.status;
            }
            
            /**
             * 获取Item
             * 
             * @return Item
             */
            public Item getItem()
            {
                return item;
            }
            
            /**
             * 设置Item
             * @param item item
             */
            public void setItem(Item item)
            {
                this.item = item;
            }
            
            /**
             * 状态码解析类
             * 
             * @author k00127978
             * 
             */
            private static class StatusCode
            {
                @Attribute(name = "code")
                private int code;
                
                /**
                 * 获取状态码
                 * 
                 * @return int
                 */
                public int getCode()
                {
                    return code;
                }
            }
        }
        
        /**
         * 获取状态码，提供给外面使用的最终的接口
         * 
         * @return int
         */
        private int getStatusCode()
        {
            // 获取X节点失败，直接返回-1
            if (getxNtf() == null)
            {
                return -1;
            }
            
            List<XNtf.StatusCode> status = getxNtf().getStatus();
            
            // 检查status列表是否为空，是否存在值
            if (status == null || status.size() == 0)
            {
                return -1;
            }
            
            // 如果只有一个结果，直接返回
            if (status.size() == 1)
            {
                return status.get(0).getCode();
            }
            
            // 如果超过一个，则检查里面是否有201
            for (int i = 0; i < status.size(); ++i)
            {
                int code = status.get(i).getCode();
                Logger.d("GroupNotification", "key = " + code);
                
                if (code == 201 || code == 110)
                {
                    // 如果201或110存在，直接返回0
                    return 0;
                }
            }
            
            return -1;
        }
        
        /**
         * 获取错误码,经过处理后的,如果成功返回0
         * @return 错误码
         */
        @Override
        public int getErrorCode()
        {
            int ret = super.getErrorCode();
            
            if (0 != ret)
            {
                // 如果获取错误码的结果为非0，直接返回错误码
                return ret;
            }
            
            // 否则返回当前的状态码结果
            return getStatusCode();
        }
        
        /**
         * item<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-3]
         */
        @Root(strict = false)
        public static class Item
        {
            /**
             * affiliation
             */
            @Attribute(name = "affiliation", required = false)
            private String affiliation;
            
            /**
             * id
             */
            @Attribute(name = "id", required = false)
            private String id;
            
            /**
             * grouptype
             */
            @Attribute(name = "grouptype", required = false)
            private String groupType;
            
            /**
             * grouptype
             */
            @Attribute(name = "fixtype", required = false)
            private String fixType;
            
            /**
             * maxgroupsize
             */
            @Attribute(name = "maxgroupsize", required = false)
            private String maxgroupsize;
            
            /**
             * groupname
             */
            @Element(name = "groupname", required = false)
            private String groupname;
            
            /**
             * groupnick
             */
            @Element(name = "groupnick", required = false)
            private String groupnick;
            
            /**
             * groupdesc
             */
            @Element(name = "groupdesc", required = false)
            private String groupdesc;
            
            /**
             * grouplabel
             */
            @Element(name = "grouplabel", required = false)
            private String grouplabel;
            
            /**
             * describe
             */
            @Element(name = "describe", required = false)
            private String describe;
            
            /**
             * groupsort
             */
            @Element(name = "groupsort", required = false)
            private String groupsort;
            
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            public String getAffiliation()
            {
                return affiliation;
            }
            
            public void setAffiliation(String affiliation)
            {
                this.affiliation = affiliation;
            }
            
            public String getId()
            {
                return id;
            }
            
            public void setId(String id)
            {
                this.id = id;
            }
            
            /**
             * getGroupType<BR>
             * @return GroupType
             */
            public String getGroupType()
            {
                // 如果是固定群组，则直接返回fixed类型
                if ("fixed".equals(groupType))
                {
                    return fixType;
                }
                return groupType;
            }
            
            public void setGroupType(String groupType)
            {
                this.groupType = groupType;
            }
            
            public String getFixType()
            {
                return fixType;
            }
            
            public void setFixType(String fixType)
            {
                this.fixType = fixType;
            }
            
            public String getMaxgroupsize()
            {
                return maxgroupsize;
            }
            
            public void setMaxgroupsize(String maxgroupsize)
            {
                this.maxgroupsize = maxgroupsize;
            }
            
            public String getGroupname()
            {
                return groupname;
            }
            
            public void setGroupname(String groupname)
            {
                this.groupname = groupname;
            }
            
            public String getGroupnick()
            {
                return groupnick;
            }
            
            public void setGroupnick(String groupnick)
            {
                this.groupnick = groupnick;
            }
            
            public String getGroupdesc()
            {
                return groupdesc;
            }
            
            public void setGroupdesc(String groupdesc)
            {
                this.groupdesc = groupdesc;
            }
            
            public String getGrouplabel()
            {
                return grouplabel;
            }
            
            public void setGrouplabel(String grouplabel)
            {
                this.grouplabel = grouplabel;
            }
            
            public String getDescribe()
            {
                return describe;
            }
            
            public void setDescribe(String describe)
            {
                this.describe = describe;
            }
            
            public String getGroupsort()
            {
                return groupsort;
            }
            
            public void setGroupsort(String groupsort)
            {
                this.groupsort = groupsort;
            }
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
        }
        
    }
    
    /**
     * 邀请加入群组通知
     */
    @Root(name = "group", strict = false)
    public static class MemberInvitingNtfData extends BaseNotification
    {
        /**
         * invite
         */
        @Element(name = "invite", required = false)
        private InviteNtf invite;
        
        public InviteNtf getInvite()
        {
            return invite;
        }
        
        public void setInvite(InviteNtf invite)
        {
            this.invite = invite;
        }
        
        /**
         * invite<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-3]
         */
        @Root(strict = false)
        public static class InviteNtf
        {
            /**
             * from
             */
            @Attribute(name = "from", required = false)
            private String from;
            
            @Attribute(name = "grouptype", required = false)
            private String grouptype;
            
            /**
             * groupname
             */
            @Element(name = "groupname", required = false)
            private String groupname;
            
            /**
             * groupdesc
             */
            @Element(name = "groupdesc", required = false)
            private String groupdesc;
            
            /**
             * grouplogo
             */
            @Element(name = "grouplogo", required = false)
            private String grouplogo;
            
            /**
             * person
             */
            @Element(name = "person", required = false)
            private PersonNtf person;
            
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            public String getFrom()
            {
                return from;
            }
            
            public void setFrom(String from)
            {
                this.from = from;
            }
            
            /**
             * 
             * [一句话功能简述]<BR>
             * [功能详细描述]
             * @return 群组类型
             */
            public String getGrouptype()
            {
                //TODO 由于服务器只下发fixed字段来标识是群组类型，这边默认返回close类型
                if ("fixed".equals(grouptype))
                {
                    return "close";
                }
                return grouptype;
            }
            
            public void setGrouptype(String grouptype)
            {
                this.grouptype = grouptype;
            }
            
            public String getGroupname()
            {
                return groupname;
            }
            
            public void setGroupname(String groupname)
            {
                this.groupname = groupname;
            }
            
            public String getGroupdesc()
            {
                return groupdesc;
            }
            
            public void setGroupdesc(String groupdesc)
            {
                this.groupdesc = groupdesc;
            }
            
            public String getGrouplogo()
            {
                return grouplogo;
            }
            
            public void setGrouplogo(String grouplogo)
            {
                this.grouplogo = grouplogo;
            }
            
            public PersonNtf getPerson()
            {
                return person;
            }
            
            public void setPerson(PersonNtf person)
            {
                this.person = person;
            }
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
            
        }
        
        /**
         * person<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        @Root(strict = false)
        public static class PersonNtf
        {
            /**
             * nick
             */
            @Attribute(name = "nick", required = false)
            private String nick;
            
            /**
             * logo
             */
            @Attribute(name = "logo", required = false)
            private String logo;
            
            public String getNick()
            {
                return nick;
            }
            
            public void setNick(String nick)
            {
                this.nick = nick;
            }
            
            public String getLogo()
            {
                return logo;
            }
            
            public void setLogo(String logo)
            {
                this.logo = logo;
            }
            
        }
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>Jim@chinaunicom.com</to>
        // <invite from='Bob@chinaunicom.com/woclient'>
        // <groupname>footballteam</groupname>
        // <groupdesc> The place for all football team member!</groupdesc>
        // <grouplogo>group/logo/5as6df47f8e8e7c7d5f.png</grouplogo>
        // <person nick='Rabbit'
        // logo='person/logo/f5dc54e4a6d7f8e876d6a5dsf5.png'/>
        // <reason>Hi, this is a good place!</reason>
        // </invite>
        // </group>
    }
    
    /**
     * 被邀请者，拒绝接受加入群组邀请(通知管理员)
     */
    @Root(name = "group", strict = false)
    public static class MemberInviteDeclinedNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>Bob@chinaunicom.com/woclient</to>
        // <decline from='Jim@chinaunicom.com' action='invite'>
        // <reason>Sorry, I'm too busy right now.</reason>
        // </decline>
        // </group>
        
        /**
         * decline
         */
        @Element(name = "decline", required = false)
        private DeclineNtf decline;
        
        public DeclineNtf getDecline()
        {
            return decline;
        }
        
        public void setDecline(DeclineNtf decline)
        {
            this.decline = decline;
        }
        
        /**
         * 拒绝通知结构
         * 
         * @author k00127978
         * 
         */
        public static class DeclineNtf
        {
            /**
             * from
             */
            @Attribute(name = "from", required = false)
            private String from;
            
            /**
             * action
             */
            @Attribute(name = "action", required = false)
            private String action;
            
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            public String getFrom()
            {
                return from;
            }
            
            public void setFrom(String from)
            {
                this.from = from;
            }
            
            public String getAction()
            {
                return action;
            }
            
            public void setAction(String action)
            {
                this.action = action;
            }
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
            
        }
    }
    
    /**
     * 请求加入群组通知（加入受限群组时，通知管理员）
     */
    @Root(name = "group", strict = false)
    public static class MemberJoinApplingNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>1000001@chinaunicom.com</to>
        // <apply from='1000003@chinaunicom.com/woclient' pcpolicy='2'
        // mobilepolicy="2">
        // <describe>I am good at defending!</describe>
        // <reason>I need parterner!</reason>
        // <nick>Babydog</nick>
        // </apply>
        // </group>
        
        /**
         * apply
         */
        @Element(name = "apply", required = false)
        private ApplyNtf apply;
        
        public ApplyNtf getApply()
        {
            return apply;
        }
        
        public void setApply(ApplyNtf apply)
        {
            this.apply = apply;
        }
        
        /**
         * apply
         */
        @Root(name = "apply", strict = false)
        public static class ApplyNtf
        {
            /**
             * from
             */
            @Attribute(name = "from", required = false)
            private String from;
            
            /**
             * pcpolicy
             */
            @Attribute(name = "pcpolicy", required = false)
            private String pcpolicy;
            
            @Attribute(name = "mobilepolicy", required = false)
            private String mobilePolicy;
            
            /**
             * describe
             */
            @Element(name = "describe", required = false)
            private String describe;
            
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            /**
             * nick
             */
            @Element(name = "nick", required = false)
            private String nick;
            
            public String getFrom()
            {
                return from;
            }
            
            public void setFrom(String from)
            {
                this.from = from;
            }
            
            public String getPcpolicy()
            {
                return pcpolicy;
            }
            
            public void setPcpolicy(String pcpolicy)
            {
                this.pcpolicy = pcpolicy;
            }
            
            public String getMobilePolicy()
            {
                return mobilePolicy;
            }
            
            public void setMobilePolicy(String mobilePolicy)
            {
                this.mobilePolicy = mobilePolicy;
            }
            
            public String getDescribe()
            {
                return describe;
            }
            
            public void setDescribe(String describe)
            {
                this.describe = describe;
            }
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
            
            public String getNick()
            {
                return nick;
            }
            
            public void setNick(String nick)
            {
                this.nick = nick;
            }
            
        }
    }
    
    /**
     * 申请加入群组者，收到被拒绝加入群组通知
     */
    @Root(name = "group", strict = false)
    public static class MemberJoinDeclinedNtfData extends
            MemberInviteDeclinedNtfData
    {
    }
    
    /**
     * 管理员同意申请人加入请求的响应
     */
    @Root(name = "group", strict = false)
    public static class MemberJoinAcceptNtfData extends BaseNotification
    {
        // <group>
        // <from>1000001@chinaunicom.com/woclient</from>
        // <to>88888888@group.chinaunicom.com</to>
        // <status-code>0</status-code>>
        // </group>
        
    }
    
    /**
     * Remove Item 's structure
     * 
     * @author Kuaidc
     */
    @Root(name = "item", strict = false)
    public static class RemoveItem
    {
        @Element(name = "actor", required = false)
        private Actor actor;
        
        @Element(name = "reason", required = false)
        private String reason;
        
        @Attribute(name = "memberjid", required = false)
        private String memberJid;
        
        public String getMemberJid()
        {
            return memberJid;
        }
        
        public void setMemberJid(String memberJid)
        {
            this.memberJid = memberJid;
        }
        
        /**
         * 获取Actor的Jid
         * 
         * @return 实施行为者的Jid
         */
        public String getActorJid()
        {
            return actor.getJid();
        }
        
        public Actor getActor()
        {
            return actor;
        }
        
        public void setActor(Actor actor)
        {
            this.actor = actor;
        }
        
        public String getReason()
        {
            return reason;
        }
        
        public void setReason(String reason)
        {
            this.reason = reason;
        }
        
        /**
         * Actor Element
         * 
         * @author kuaidc
         * 
         */
        @Root(name = "actor", strict = false)
        public static class Actor
        {
            @Attribute(name = "jid")
            private String jid;
            
            public String getJid()
            {
                return jid;
            }
            
            public void setJid(String jid)
            {
                this.jid = jid;
            }
        }
    }
    
    /**
     * 成员退出通知（成员退出，所有者退出，成员被踢出）
     */
    @Root(name = "group", strict = false)
    public static class MemberRemovedNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com/1000003</from>
        // <to>1000002@chinaunicom.com/woclient</to>
        // <status-code>307</status-code>
        // <item affiliation='none'/>
        // </group>
        
        /**
         * 其中的item中的affiliation='none'对业务没有影响，这里不保存此信息
         */
    }
    
    /**
     * 踢出成员响应（仅发起踢人者收到）
     */
    @Root(name = "group", strict = false)
    public static class MemberRemoveNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>Bob@chinaunicom.com/woclient</to>
        // <status-code>0</status-code>
        // <query>
        // <item memberjid='88888888@group.chinaunicom.com/1000003'
        // affiliation='none'>
        // <reason>You are boring!</reason>
        // </item>
        // </query>
        // </group>
        
        /**
         * query
         */
        @Element(name = "query", required = false)
        private QueryNtf query;
        
        public QueryNtf getQuery()
        {
            return query;
        }
        
        public void setQuery(QueryNtf query)
        {
            this.query = query;
        }
        
        /**
         * QueryNtf
         * 
         * @author k00127978
         * 
         */
        @Root(name = "query")
        public static class QueryNtf
        {
            /**
             * item
             */
            @Element(name = "item", required = false)
            private RemoveItem item;
            
            public RemoveItem getItem()
            {
                return item;
            }
            
            public void setItem(RemoveItem item)
            {
                this.item = item;
            }
        }
    }
    
    /**
     * 通知被提出者（仅被踢出者收到）
     */
    @Root(name = "group", strict = false)
    public static class MemberKickedNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>1000003@chinaunicom.com/woclient</to>
        // <item affiliation='none'>
        // <actor jid='1000001@chinaunicom.com'/>
        // <reason>You are boring!</reason>
        // </item>
        // </group>
        
        /**
         * item
         */
        @Element(name = "item", required = false)
        private RemoveItem item;
        
        public RemoveItem getItem()
        {
            return item;
        }
        
        public void setItem(RemoveItem item)
        {
            this.item = item;
        }
    }
    
    /**
     * 删除群组响应（仅群组删除者收到）
     */
    @Root(name = "group", strict = false)
    public static class DestroyNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>1000001@group.chinaunicom.com</to>
        // <status-code>0</status-code>
        // </group>
    }
    
    /**
     * 删除群组通知（通知其他成员）
     */
    @Root(name = "group", strict = false)
    public static class DestroyedNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>1000002@group.chinaunicom.com</to>
        // <x xmlns='http://jabber.org/protocol/group#user'>
        // <item affiliation='none'/>
        // <destroy >
        // <reason>Team is dismissed.</reason>
        // </destroy>
        // </x>
        // </group>
        
        /**
         * x
         */
        @Element(name = "x", required = false)
        @Namespace(reference = "http://jabber.org/protocol/group#user")
        private XNtf x;
        
        public XNtf getX()
        {
            return x;
        }
        
        public void setX(XNtf x)
        {
            this.x = x;
        }
        
        /**
         * XNtf<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        public static class XNtf
        {
            /**
             * item
             */
            @Element(name = "item", required = false)
            private ItemNtf item;
            
            /**
             * destory
             */
            @Element(name = "destroy", required = false)
            private DestoryNtf destory;
            
            public ItemNtf getItem()
            {
                return item;
            }
            
            public void setItem(ItemNtf item)
            {
                this.item = item;
            }
            
            public DestoryNtf getDestory()
            {
                return destory;
            }
            
            public void setDestory(DestoryNtf destory)
            {
                this.destory = destory;
            }
            
        }
        
        /**
         * item<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        @Root(name = "item")
        public static class ItemNtf
        {
            /**
             * affiliation
             */
            @Attribute(name = "affiliation")
            private String affiliation;
            
            public String getAffiliation()
            {
                return affiliation;
            }
            
            public void setAffiliation(String affiliation)
            {
                this.affiliation = affiliation;
            }
            
        }
        
        /**
         * destory<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        @Root(name = "destory")
        public static class DestoryNtf
        {
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
            
        }
    }
    
    /**
     * 获取所属群组列表响应
     */
    @Root(name = "group", strict = false)
    public static class GetGroupListNtfData extends BaseNotification
    {
        // <group>
        // <from>group.chinaunicom.com</from>
        // <to>Bob@chinaunicom.com/woclient</to>
        // <error-code>0</error-code>
        // <query xmlns='http://jabber.org/protocol/group#items' type='group'>
        // <item jid='12345678@ group.chinaunicom.com' name='healthteam'
        // type='open' sort='1' logo='group/logo/a9d9f8c7d6e6fads5f4asdf46.png'
        // affiliation='owner' pcpolicy='2' mobilepolicy='2'>
        // <desc>The place for doctor!</desc>
        // <owner logo=’ group / logo / a9d9f8c7d6e6fads5f4a.png '
        // jid=’1000001@chinaunicom.com’>
        // <nick>Tiger</nick>
        // </owner>
        // </item>
        // <item jid='88888888@group.chinaunicom.com' sort='3'
        // name='footballteam'
        // type='close' logo='
        // group/logo/a9d9f8c7d6e6fadse5d55sadf56a5f4asdf46.png '
        // affiliation='member' pcpolicy='2' mobilepolicy='2'>
        // <desc> The place for all football team member!</desc>
        // <owner logo=’ group / logo /
        // a9d9f8c7d6e6fadse5d55sadf56a5f4.png ' jid=’1000001@chinaunicom.com’>
        // <nick>Tiger</nick>
        // </owner>
        // </item>
        // <item jid='1234321@group.chinaunicom.com' sort='3'
        // name='footballteam1'
        // type='close' logo='
        // group/logo/a9d9f8c7d6e6fadse5d55sadf56a5f4asdf46.png'
        // affiliation='none' proceeding='invite' pcpolicy='2' mobilepolicy='2'>
        // <inviter jid='1000002@chinaunicom.com'
        // logo='person/logo/asd3f2e3f3c3asd22df3ad3v3.png'>
        // <nick>Babydog</nick>
        // <reason>Join us!</reason>
        // </inviter>
        // <desc> The place for all football team member!</desc>
        // <owner logo=’ group / logo / a9d9f8c7e6fads5f4a.png '
        // jid=’1000001@chinaunicom.com’>
        // <nick>Tiger</nick>
        // </owner>
        // </item>
        // </query>
        // </group>
        
        /**
         * query
         */
        @Element(name = "query", required = false)
        @Namespace(reference = "http://jabber.org/protocol/group#items")
        private QueryNtf query;
        
        public QueryNtf getQuery()
        {
            return query;
        }
        
        public void setQuery(QueryNtf query)
        {
            this.query = query;
        }
        
        /**
         * query<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        public static class QueryNtf
        {
            /**
             * type
             */
            @Attribute(name = "type", required = false)
            private String type;
            
            /**
             * item
             */
            @ElementList(inline = true, entry = "item", required = false)
            private List<GroupListItemNtf> itemList;
            
            public String getType()
            {
                return type;
            }
            
            public void setType(String type)
            {
                this.type = type;
            }
            
            public List<GroupListItemNtf> getItemList()
            {
                return itemList;
            }
            
            public void setItemList(List<GroupListItemNtf> itemList)
            {
                this.itemList = itemList;
            }
            
        }
        
        // 群组item信息
        // <item label="group-1 Label" affiliation="none"
        // jid="1028401@group.chinaunicom.com" type="open" sort="3" pcpolicy="2"
        // mobilepolicy="2" proceeding="invite">
        // <groupname>group-1 Name</groupname>
        // <groupdesc>changee</groupdesc>
        // <owner logo="null" jid="100628@chinaunicom.com">
        // <nick>猪</nick>
        // </owner>
        // <inviter logo="null" jid="100628@chinaunicom.com">
        // <nick>猪</nick>
        // <reason>加入群玩玩呗~</reason>
        // </inviter>
        // </item>
        
        /**
         * item
         */
        @Root(name = "item", strict = false)
        public static class GroupListItemNtf
        {
            /**
             * jid
             */
            @Attribute(name = "jid", required = false)
            private String jid;
            
            /**
             * affiliation
             */
            @Attribute(name = "affiliation", required = false)
            private String affiliation;
            
            /**
             * pcpolicy
             */
            @Attribute(name = "pcpolicy", required = false)
            private String pcpolicy;
            
            /**
             * mobilepolicy
             */
            @Attribute(name = "mobilepolicy", required = false)
            private String mobilepolicy;
            
            /**
             * memberjid 用户的组内唯一JID
             */
            @Attribute(name = "memberjid", required = false)
            private String memberjid;
            
            /**
             * type
             */
            @Attribute(name = "type", required = false)
            private String type;
            
            @Attribute(name = "fixtype", required = false)
            private String fixtype;
            
            /**
             * sort
             */
            @Attribute(name = "sort", required = false)
            private String sort;
            
            /**
             * label
             */
            @Attribute(name = "label", required = false)
            private String label;
            
            /**
             * logo
             */
            @Attribute(name = "logo", required = false)
            private String logo;
            
            /**
             * proceeding
             */
            @Attribute(name = "proceeding", required = false)
            private String proceeding;
            
            /**
             * desc
             */
            @Element(name = "groupdesc", required = false)
            private String desc;
            
            /**
             * name
             */
            @Element(name = "groupname", required = false)
            private String name;
            
            /**
             * nick
             */
            @Element(name = "groupnick", required = false)
            private String nick;
            
            /**
             * inviter
             */
            @Element(name = "inviter", required = false)
            private InviterNtf inviter;
            
            /**
             * show
             */
            @Element(name = "show", required = false)
            private String show;
            
            /**
             * status
             */
            @Element(name = "status", required = false)
            private String status;
            
            /**
             * owner
             */
            @Element(name = "owner", required = false)
            private Owner owner;
            
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            public String getJid()
            {
                return jid;
            }
            
            public void setJid(String jid)
            {
                this.jid = jid;
            }
            
            public String getAffiliation()
            {
                return affiliation;
            }
            
            public void setAffiliation(String affiliation)
            {
                this.affiliation = affiliation;
            }
            
            public String getPcpolicy()
            {
                return pcpolicy;
            }
            
            public void setPcpolicy(String pcpolicy)
            {
                this.pcpolicy = pcpolicy;
            }
            
            public String getMobilepolicy()
            {
                return mobilepolicy;
            }
            
            public void setMobilepolicy(String mobilepolicy)
            {
                this.mobilepolicy = mobilepolicy;
            }
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
            
            public String getShow()
            {
                return show;
            }
            
            public void setShow(String show)
            {
                this.show = show;
            }
            
            public String getStatus()
            {
                return status;
            }
            
            public void setStatus(String status)
            {
                this.status = status;
            }
            
            public String getNick()
            {
                return nick;
            }
            
            public void setNick(String nick)
            {
                this.nick = nick;
            }
            
            public String getProceeding()
            {
                return proceeding;
            }
            
            public void setProceeding(String proceeding)
            {
                this.proceeding = proceeding;
            }
            
            public String getName()
            {
                return name;
            }
            
            public void setName(String name)
            {
                this.name = name;
            }
            
            /**
             * getType<BR>
             * @return type
             */
            public String getType()
            {
                // 如果是固定群组，则直接返回fixed类型
                if ("fixed".equals(type))
                {
                    return fixtype;
                }
                
                return type;
            }
            
            public void setType(String type)
            {
                this.type = type;
            }
            
            public String getDesc()
            {
                return desc;
            }
            
            public void setDesc(String desc)
            {
                this.desc = desc;
            }
            
            public String getSort()
            {
                return sort;
            }
            
            public void setSort(String sort)
            {
                this.sort = sort;
            }
            
            public String getLogo()
            {
                return logo;
            }
            
            public void setLogo(String logo)
            {
                this.logo = logo;
            }
            
            public InviterNtf getInviter()
            {
                return inviter;
            }
            
            public void setInviter(InviterNtf inviter)
            {
                this.inviter = inviter;
            }
            
            /**
             * get the label
             * 
             * @return the label
             */
            public String getLabel()
            {
                return label;
            }
            
            public void setLabel(String label)
            {
                this.label = label;
            }
            
            public Owner getOwner()
            {
                return owner;
            }
            
            public void setOwner(Owner owner)
            {
                this.owner = owner;
            }
            
            public String getMemberjid()
            {
                return memberjid;
            }
            
            public void setMemberjid(String memberjid)
            {
                this.memberjid = memberjid;
            }
            
            public void setFixtype(String fixtype)
            {
                this.fixtype = fixtype;
            }
            
        }
        
        // <inviter jid='1000002@chinaunicom.com' reason='Join us!'
        // // logo='person/logo/asd3f2e3f3c3asd22df3ad3v3.png'
        // // nick='Tiger'/>
        
        /**
         * inviter
         */
        @Root(name = "inviter", strict = false)
        public static class InviterNtf
        {
            /**
             * jid
             */
            @Attribute(name = "jid", required = false)
            private String jid;
            
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            /**
             * logo
             */
            @Attribute(name = "logo", required = false)
            private String logo;
            
            /**
             * nick
             */
            @Element(name = "nick", required = false)
            private String nick;
            
            public String getJid()
            {
                return jid;
            }
            
            public void setJid(String jid)
            {
                this.jid = jid;
            }
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
            
            public String getLogo()
            {
                return logo;
            }
            
            public void setLogo(String logo)
            {
                this.logo = logo;
            }
            
            public String getNick()
            {
                return nick;
            }
            
            public void setNick(String nick)
            {
                this.nick = nick;
            }
        }
        
        /**
         * group Owner class
         * 
         * @author kuaidc
         * 
         */
        @Root(strict = false)
        public static class Owner
        {
            @Attribute(name = "logo", required = false)
            private String logo;
            
            @Attribute(name = "jid", required = false)
            private String jid;
            
            @Element(name = "nick", required = false)
            private String nick;
            
            public String getLogo()
            {
                return logo;
            }
            
            public void setLogo(String logo)
            {
                this.logo = logo;
            }
            
            public String getJid()
            {
                return jid;
            }
            
            public void setJid(String jid)
            {
                this.jid = jid;
            }
            
            public String getNick()
            {
                return nick;
            }
            
            public void setNick(String nick)
            {
                this.nick = nick;
            }
        }
    }
    
    /**
     * 搜索群组响应
     */
    @Root(name = "group", strict = false)
    public static class SearchGroupNtfData extends GetGroupListNtfData
    {
        // <group>
        // <from>Bob@chinaunicom.com/woclient</from>
        // <to>group.chinaunicom.com</to>
        // <status-code>0</status-code>
        // <query>
        // <item jid='12345678@group.chinaunicom.com' name='healthteam'
        // type='open' desc='The place for doctor!' sort='1' logo='
        // group/logo/a9d9f8c7d6e6fads5f4asdf46.png'/>
        // <item jid='88888888@group.chinaunicom.com'name='footballteam'
        // type='close' desc=' The place for all football team member!' sort='3'
        // logo=' group/logo/b6d5fa9d9f8c7d6e6fads5f4asdf46.png'/>
        // </query>
        // </group>
    }
    
    /**
     * 获取成员列表响应
     */
    @Root(name = "group", strict = false)
    public static class MemberGetMemberListNtfData extends BaseNotification
    {
        /**
         * query
         */
        @Element(name = "query", required = false)
        @Namespace(reference = "http://jabber.org/protocol/group#items")
        private QueryNtf query;
        
        public QueryNtf getQuery()
        {
            return query;
        }
        
        public void setQuery(QueryNtf query)
        {
            this.query = query;
        }
        
        /**
         * query<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        @Root(strict = false)
        public static class QueryNtf
        {
            // /**
            // * type
            // */
            // @Attribute(name = "type", required = false)
            // private String type;
            // public String getType()
            // {
            // return type;
            // }
            //
            // public void setType(String type)
            // {
            // this.type = type;
            // }
            
            /**
             * item
             */
            @ElementList(inline = true, entry = "item", required = false)
            private List<MemberItemNtf> itemList;
            
            public List<MemberItemNtf> getItemList()
            {
                return itemList;
            }
            
            public void setItemList(List<MemberItemNtf> itemList)
            {
                this.itemList = itemList;
            }
            
        }
        
        /**
         * MemberItem
         * 
         * @author k00127978
         * 
         */
        @Root(strict = false)
        public static class MemberItemNtf
        {
            // 成员信息
            // <item memberjid="1165301@group.chinaunicom.com/111159"
            // affiliation="member" jid="111159@chinaunicom.com" logo="">
            // <desc>desc</desc>
            // <groupnick>backstree boy </groupnick>
            // <show>online</show>
            // <status>justlogin</status>
            // </item>
            
            /**
             * jid
             */
            @Attribute(name = "jid", required = false)
            private String jid;
            
            /**
             * affiliation
             */
            @Attribute(name = "affiliation", required = false)
            private String affiliation;
            
            /**
             * memberJid
             */
            @Attribute(name = "memberjid", required = false)
            private String memberJid;
            
            @Attribute(name = "logo", required = false)
            private String logo;
            
            @Element(name = "desc", required = false)
            private String desc;
            
            /**
             * groupnick
             */
            @Element(name = "groupnick", required = false)
            private String groupNick;
            
            /**
             * show
             */
            @Element(name = "show", required = false)
            private String show;
            
            /**
             * status
             */
            @Element(name = "status", required = false)
            private String status;
            
            public String getJid()
            {
                return jid;
            }
            
            public void setJid(String jid)
            {
                this.jid = jid;
            }
            
            public String getAffiliation()
            {
                return affiliation;
            }
            
            public void setAffiliation(String affiliation)
            {
                this.affiliation = affiliation;
            }
            
            public String getMemberJid()
            {
                return memberJid;
            }
            
            public void setMemberJid(String memberJid)
            {
                this.memberJid = memberJid;
            }
            
            public String getGroupNick()
            {
                return groupNick;
            }
            
            public void setGroupNick(String groupNick)
            {
                this.groupNick = groupNick;
            }
            
            public String getShow()
            {
                return show;
            }
            
            public void setShow(String show)
            {
                this.show = show;
            }
            
            public String getStatus()
            {
                return status;
            }
            
            public void setStatus(String status)
            {
                this.status = status;
            }
            
            public String getLogo()
            {
                return logo;
            }
            
            public void setLogo(String logo)
            {
                this.logo = logo;
            }
            
            public String getDesc()
            {
                return desc;
            }
            
            public void setDesc(String desc)
            {
                this.desc = desc;
            }
            
        }
    }
    
    /**
     * 设置成员岗位响应（分配管理员权限、转让群组）需所有者权限
     */
    @Root(name = "group", strict = false)
    public static class MemberSetAffiliationNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>1000001@chinaunicom.com/woclient</to>
        // <status-code>0</status-code>
        // <query>
        // <item affiliation='owner/admin' jid='Jim@chinaunicom.com'>
        // <reason>New owner!</reason>
        // </item>
        // </query>
        // </group>
        
        /**
         * query
         */
        @ElementList(name = "query", inline = false, entry = "item")
        private List<ItemNtf> query;
        
        public List<ItemNtf> getQuery()
        {
            return query;
        }
        
        public void setQuery(List<ItemNtf> query)
        {
            this.query = query;
        }
        
        /**
         * item<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        @Root(name = "item")
        public static class ItemNtf
        {
            /**
             * affiliation
             */
            @Attribute(name = "affiliation", required = false)
            private String affiliation;
            
            /**
             * jid
             */
            @Attribute(name = "jid", required = false)
            private String jid;
            
            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;
            
            public String getAffiliation()
            {
                return affiliation;
            }
            
            public void setAffiliation(String affiliation)
            {
                this.affiliation = affiliation;
            }
            
            public String getJid()
            {
                return jid;
            }
            
            public void setJid(String jid)
            {
                this.jid = jid;
            }
            
            public String getReason()
            {
                return reason;
            }
            
            public void setReason(String reason)
            {
                this.reason = reason;
            }
            
        }
    }
    
    /**
     * 获取群组配置信息响应
     */
    @Root(name = "group", strict = false)
    public static class GetConfigInfoNtfData extends BaseNotification
    {
        // <query xmlns="http://jabber.org/protocol/group#owner">
        // <x xmlns="jabber:x:data" type="form">
        // <field type="hidden" var="FORM_TYPE">
        // <value>http://jabber.org/protocol/group#groupconfig</value>
        // </field>
        // <field label="Group ID" type="text-single"
        // var="group#groupconfig_groupid">
        // <value>1023101</value>
        // </field>
        // <field label="Group Type" type="text-single"
        // var="group#groupconfig_grouptype">
        // <value>close</value>
        // </field>
        // <field label="Bulletin of Group" type="text-single"
        // var="group#groupconfig_groupbulletin">
        // <value />
        // </field>
        // <field label="Group Message Switch" type="text-single"
        // var="group#groupconfig_chattype">
        // <value>both</value>
        // </field>
        // <field label="Description of Group" type="text-single"
        // var="group#groupconfig_groupdesc">
        // <value>groupDesc</value>
        // </field>
        // <field label="Group Label" type="text-single"
        // var="group#groupconfig_grouplabel">
        // <value>groupLabel</value>
        // </field>
        // <field label="Group Logo" type="text-single"
        // var="group#groupconfig_grouplogo">
        // <value />
        // </field>
        // <field label="Group Name" type="text-single"
        // var="group#groupconfig_groupname">
        // <value>groupName</value>
        // </field>
        // <field label="Group owner receive messages or not" type="text-single"
        // var="group#groupconfig_ownerchat">
        // <value>1</value>
        // </field>
        // <field label="Group Sort" type="text-single"
        // var="group#groupconfig_groupsort">
        // <value>3</value>
        // </field>
        // </x>
        // </query>
        
        /**
         * query
         */
        @Element(name = "query", required = false)
        @Namespace(reference = "http://jabber.org/protocol/group#owner")
        private QueryNtf query;
        
        public QueryNtf getQuery()
        {
            return query;
        }
        
        public void setQuery(QueryNtf query)
        {
            this.query = query;
        }
        
        /**
         * query
         */
        public static class QueryNtf
        {
            @Element(name = "x", required = false)
            @Namespace(reference = "jabber:x:data")
            private ElementXNtf elementX;
            
            public ElementXNtf getElementX()
            {
                return elementX;
            }
            
            public void setElementX(ElementXNtf elementX)
            {
                this.elementX = elementX;
            }
        }
        
        /**
         * x
         */
        public static class ElementXNtf
        {
            /**
             * type attribute, the value is "form"
             */
            @Attribute(name = "type", required = false)
            private String type;
            
            /**
             * field
             */
            @ElementList(inline = true, entry = "field", required = false)
            private List<CommonXmppCmdGenerator.GroupConfigFieldData> field;
            
            public String getType()
            {
                return type;
            }
            
            public void setType(String type)
            {
                this.type = type;
            }
            
            public List<CommonXmppCmdGenerator.GroupConfigFieldData> getField()
            {
                return field;
            }
            
            public void setField(
                    List<CommonXmppCmdGenerator.GroupConfigFieldData> field)
            {
                this.field = field;
            }
        }
        
    }
    
    /**
     * 更新群组配置响应
     */
    @Root(name = "group", strict = false)
    public static class SubmitConfigInfoNtfData extends BaseNotification
    {
        /**
         * 不需要做特殊处理，只有From、To、Status-Code三项
         */
    }
    
    /**
     * 昵称修改通知（仅通知修改昵称发起者）
     */
    @Root(name = "group", strict = false)
    public static class MemberChangeNickNtfData extends BaseNotification
    {
        /**
         * reason
         */
        @Element(name = "reason", required = false)
        private String reason;
        
        public String getReason()
        {
            return reason;
        }
        
        public void setReason(String reason)
        {
            this.reason = reason;
        }
        
    }
    
    /**
     * 群组成员更改信息响应
     */
    @Root(name = "group", strict = false)
    public static class MemberChangeInfoNtfData extends BaseNotification
    {
        /**
         * 不需要做特殊处理，只有From、To、Status-Code三项
         */
    }
    
    /**
     * 获取成员呈现信息响应
     */
    @Root(name = "group", strict = false)
    public static class MemberGetPresenceNtfData extends BaseNotification
    {
        // <group>
        // <from>Bob@chinaunicom.com/woclient</from>
        // <to>88888888@group.chinaunicom.com</to>
        // <status-code>0</status-code>
        // <query xmlns='http://jabber.org/protocol/group#items'
        // type='presence'>
        // <item memberjid='88888888@group.chinaunicom.com/1000001'>
        // <show>online</show>
        // <status>I am online!</status>
        // </item>
        // <item memberjid='88888888@group.chinaunicom.com/1000002'>
        // <show>online</show>
        // <status>I am online!</status>
        // </item>
        // <item memberjid='88888888@group.chinaunicom.com/1000003'>
        // <show>online</show>
        // <status>I am online!</status>
        // </item>
        // </query>
        // </group>
        
        /**
         * query
         */
        @Element(name = "query", required = false)
        @Namespace(reference = "http://jabber.org/protocol/group#items")
        private QueryNtf query;
        
        public QueryNtf getQuery()
        {
            return query;
        }
        
        public void setQuery(QueryNtf query)
        {
            this.query = query;
        }
        
        /**
         * query
         */
        @Root(name = "query")
        public static class QueryNtf
        {
            /**
             * type
             */
            @Attribute(name = "type", required = false)
            private String type;
            
            /**
             * itemList
             */
            @ElementList(inline = true, entry = "item", required = false)
            private List<ItemNtf> itemList;
            
            public List<ItemNtf> getItemList()
            {
                return itemList;
            }
            
            public void setItemList(List<ItemNtf> itemList)
            {
                this.itemList = itemList;
            }
            
            public String getType()
            {
                return type;
            }
            
            public void setType(String type)
            {
                this.type = type;
            }
            
        }
        
        /**
         * item<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-4]
         */
        @Root(name = "item")
        public static class ItemNtf
        {
            /**
             * memberjid
             */
            @Attribute(name = "memberjid", required = false)
            private String memberjid;
            
            /**
             * show
             */
            @Element(name = "show", required = false)
            private String show;
            
            /**
             * status
             */
            @Element(name = "status", required = false)
            private String status;
            
            public String getMemberjid()
            {
                return memberjid;
            }
            
            public void setMemberjid(String memberjid)
            {
                this.memberjid = memberjid;
            }
            
            public String getShow()
            {
                return show;
            }
            
            public void setShow(String show)
            {
                this.show = show;
            }
            
            public String getStatus()
            {
                return status;
            }
            
            public void setStatus(String status)
            {
                this.status = status;
            }
            
        }
    }
    
    /**
     * 收到群消息
     */
    @Root(name = "group", strict = false)
    public static class MessageReceivedNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com/1000002</from>
        // <id>1</id>
        // <message type='im-gp-private/im-gp-public'>
        // <body>I'll go home.</body>
        // <delay xmlns=‘urn:xmpp:delay’ from=‘xmpp.com’
        // stamp=‘2011-08-02T14:56:59Z’>
        // Offline Storage
        // </delay>
        // </message>
        // </group>
        
        /**
         * message
         */
        @Element(name = "message", required = false)
        private MessageNtf message;
        
        public MessageNtf getMessage()
        {
            return message;
        }
        
        public void setMessage(MessageNtf message)
        {
            this.message = message;
        }
        
        /**
         * message
         */
        @Root(name = "message", strict = false)
        public static class MessageNtf extends
                MessageCommonClass.CommonMessageData
        {
            
            @Element(name = "fgnick", required = false)
            private String fgnick;
            
            public String getFgnick()
            {
                return fgnick;
            }
            
            public void setFgnick(String fgnick)
            {
                this.fgnick = fgnick;
            }
            
        }
        
        /**
         * DelayNtf
         * 
         * @author k00127978
         * 
         */
        public static class DelayNtf
        {
            @Attribute(name = "from", required = false)
            private String from;
            
            @Attribute(name = "stamp", required = false)
            private String stamp;
            
            @Text(required = false)
            private String value;
            
            public String getFrom()
            {
                return from;
            }
            
            public void setFrom(String from)
            {
                this.from = from;
            }
            
            public String getStamp()
            {
                return stamp;
            }
            
            public void setStamp(String stamp)
            {
                this.stamp = stamp;
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
    }
    
    /**
     * 成员信息通知（新成员加入，指派管理员，所有者变更、修改了成员信息）
     */
    @Root(name = "group", strict = false)
    public static class MemberInfoNtfData extends BaseNotification
    {
        // 有新成员加入示例：
        // <group>
        // <from>88888888@group.chinaunicom.com/1000003</from>
        // <to>1000002@chinaunicom.com/woclient</to>
        // <x xmlns='http://jabber.org/protocol/group#user'>
        // <item affiliation='member' jid=1000003@chinaunicom.com'
        // desc='I am good at running!'>
        // <groupnick>Tigase</groupnick>
        // </item>
        // </x>
        // </group>
        //
        // 加入新的群组示例：
        // <group>
        // <from>88888888@group.chinaunicom.com/1000003</from>
        // <to>1000002@chinaunicom.com/woclient</to>
        // <x xmlns='http://jabber.org/protocol/group#user'>
        // <item affiliation='member' jid='1000003@chinaunicom.com'>
        // <groupnick>Babydog</groupnick>
        // <groupname>footballteam</groupname>
        // <groupdesc> The place for all football team member!</groupdesc>
        // <grouplogo>group/logo/5as6df47f8e8e7c7d5f.png</grouplogo>
        // </item>
        // </x>
        // </group>
        /**
         * x
         */
        @Element(name = "x", required = false)
        private XNtf x;
        
        public XNtf getX()
        {
            return x;
        }
        
        public void setX(XNtf x)
        {
            this.x = x;
        }
        
        /**
         * x
         */
        @Root(name = "x")
        public static class XNtf
        {
            /**
             * item
             */
            
            @ElementList(inline = true, entry = "item")
            private List<ItemNtf> itemList;
            
            @Element(name = "groupnick", required = false)
            private String groupNick;
            
            @Element(name = "status", required = false)
            private Status status;
            
            /**
             * 这个Item是经过处理的,如果成员个数超过1个,
             * 则此Item为Null,从ItemList中取数据
             * @return ItemNtf
             */
            public ItemNtf getItem()
            {
                if (itemList != null && itemList.size() == 1)
                {
                    return itemList.get(0);
                }
                else
                {
                    return null;
                }
            }
            
            public List<ItemNtf> getItemList()
            {
                return itemList;
            }
            
            public void setItemList(List<ItemNtf> itemList)
            {
                this.itemList = itemList;
            }
            
            public String getGroupNick()
            {
                return groupNick;
            }
            
            public void setGroupNick(String groupNick)
            {
                this.groupNick = groupNick;
            }
            
            /**
             * getStatusCode<BR>
             * @return statusCode
             */
            public int getStatusCode()
            {
                int code = 0;
                if (status != null)
                {
                    code = status.getCode();
                }
                return code;
            }
            
            public void setStatus(Status status)
            {
                this.status = status;
            }
            
            /**
             * Status
             * 
             * @author k00127978
             * 
             */
            public static class Status
            {
                @Attribute(name = "code", required = false)
                private int code;
                
                public int getCode()
                {
                    return code;
                }
                
                public void setCode(int code)
                {
                    this.code = code;
                }
                
            }
            
        }
        
        /**
         * ItemNtf
         * 
         * @author k00127978
         * 
         */
        @Root(strict = false)
        public static class ItemNtf
        {
            /**
             * jid
             */
            @Attribute(name = "jid", required = false)
            private String jid;
            
            /**
             * nick
             */
            @Attribute(name = "nick", required = false)
            private String nick;
            
            /**
             * affiliation
             */
            @Attribute(name = "affiliation", required = false)
            private String affiliation;
            
            @Attribute(name = "grouptype", required = false)
            private String groupType;
            
            /**
             * desc
             */
            @Attribute(name = "desc", required = false)
            @Element(name = "desc", required = false)
            private String desc;
            
            /**
             * groupname
             */
            @Element(name = "groupname", required = false)
            private String groupName;
            
            /**
             * groupnick
             */
            @Element(name = "groupnick", required = false)
            private String groupnick;
            
            /**
             * groupdesc
             */
            @Element(name = "groupdesc", required = false)
            private String groupdesc;
            
            public String getJid()
            {
                return jid;
            }
            
            public void setJid(String jid)
            {
                this.jid = jid;
            }
            
            public String getNick()
            {
                return nick;
            }
            
            public void setNick(String nick)
            {
                this.nick = nick;
            }
            
            public String getGroupName()
            {
                return groupName;
            }
            
            public void setGroupName(String groupName)
            {
                this.groupName = groupName;
            }
            
            public String getAffiliation()
            {
                return affiliation;
            }
            
            public void setAffiliation(String affiliation)
            {
                this.affiliation = affiliation;
            }
            
            /**
             * getGroupType<BR>
             * @return GroupType
             */
            public String getGroupType()
            {
                return groupType;
            }
            
            public void setGroupType(String groupType)
            {
                this.groupType = groupType;
            }
            
            public String getDesc()
            {
                return desc;
            }
            
            public void setDesc(String desc)
            {
                this.desc = desc;
            }
            
            public String getGroupnick()
            {
                return groupnick;
            }
            
            public void setGroupnick(String groupnick)
            {
                this.groupnick = groupnick;
            }
            
            public String getGroupdesc()
            {
                return groupdesc;
            }
            
            public void setGroupdesc(String groupdesc)
            {
                this.groupdesc = groupdesc;
            }
        }
    }
    
    /**
     * 群配置更新通知
     */
    @Root(name = "group", strict = false)
    public static class ConfigInfoNtfData extends BaseNotification
    {
        // <group>
        // <from>88888888@group.chinaunicom.com</from>
        // <to>Tom@chinaunicom.com/woclient</to>
        // <x xmlns='jabber:x:data' type='form'>
        // <title>Configuration for "footballteam" Group</title>
        // <instructions>
        // Your group footballteam has been created!
        // The default configuration is as follows:
        // - Up to 50 members
        // - Only open for member
        // - Group is persistent
        // To accept the default configuration, click OK. To
        // select a different configuration, please complete
        // this form.
        // </instructions>
        // <field type='hidden' var='FORM_TYPE'>
        // <value>http://jabber.org/protocol/group#groupconfig</value>
        // </field>
        // <field label='Group ID?' type='text-single' var='group#groupconfig
        // _groupid'>
        // <value>88888888</value>
        // </field>
        // <field label='Group Name' type='text-single' var='group#groupconfig
        // _groupname'/>
        // <value>footballteam</value>
        // </field>
        // <field label='Description of Group' type='text-single'
        // var='group#groupconfig_groupdesc'/>
        // <value>The place of football player</value>
        // </field>
        // <field label='Bulletin of Group' type='text-single'
        // var='group#groupconfig_groupbulletin'/>
        // <value>Come on, friends!</value>
        // </field>
        // <field var='group# groupconfig _grouptype'>
        // <value>close</value>
        // </field>
        // <field var='group# groupconfig _grouplabel'>
        // <value>football,team</value>
        // </field>
        // <field label='Group Sort' type='text-single'
        // var='group#groupconfig_groupsort'>
        // <value>3</value>
        // </field>
        // <field label='Group owner receive messages or not' type='text-single'
        // var='group#groupconfig_ownerchat'>
        // <value>1</value>
        // </field>
        // <field label='Group Message Switch' type='text-single'
        // var='group#groupconfig_chattype'>
        // <value>both</value>
        // </field>
        // <field label=' Group LOGO' type=' text-single' var='group#groupconfig
        // _grouplogo'>
        // <value>group/logo/a6f6d6sc66ef66d65a4df4e4.png</value>
        // </field>
        // </x>
        // </group>
        
        /**
         * x
         */
        @Element(name = "x", required = false)
        @Namespace(reference = "jabber:x:data")
        private XNtf x;
        
        public XNtf getX()
        {
            return x;
        }
        
        public void setX(XNtf x)
        {
            this.x = x;
        }
        
        /**
         * XNtf
         * 
         * @author k00127978
         */
        public static class XNtf
        {
            /**
             * type
             */
            @Attribute(name = "type", required = false)
            private String type;
            
            /**
             * title
             */
            @Element(name = "title", required = false)
            private String title;
            
            /**
             * instructions
             */
            @Element(name = "instructions", required = false)
            private String instructions;
            
            /**
             * fieldList
             */
            @ElementList(inline = true, entry = "field")
            private List<CommonXmppCmdGenerator.GroupConfigFieldData> fieldList;
            
            public String getType()
            {
                return type;
            }
            
            public void setType(String type)
            {
                this.type = type;
            }
            
            public String getTitle()
            {
                return title;
            }
            
            public void setTitle(String title)
            {
                this.title = title;
            }
            
            public String getInstructions()
            {
                return instructions;
            }
            
            public void setInstructions(String instructions)
            {
                this.instructions = instructions;
            }
            
            public List<CommonXmppCmdGenerator.GroupConfigFieldData> getFieldList()
            {
                return fieldList;
            }
            
            public void setFieldList(
                    List<CommonXmppCmdGenerator.GroupConfigFieldData> fieldList)
            {
                this.fieldList = fieldList;
            }
        }
    }
    
    /**
     * 成员退出通知
     * 
     * @author Administrator
     */
    @Root(name = "group", strict = false)
    public static class MemberQuitNtfData extends BaseNotification
    {
    }
    
    /**
     * 消息发送通知
     * 
     * @author k00127978
     * 
     */
    @Root(name = "group", strict = false)
    public static class MessageSendNtfData extends BaseNotification
    {
    }
    
    /**
     * 成员加入命令通知
     * 
     * @author k00127978
     * 
     */
    @Root(name = "group", strict = false)
    public static class MemberJoinApplyNtfData extends BaseNotification
    {
    }
    
    /**
     * 邀请发送命令通知
     * 
     * @author k00127978
     * 
     */
    @Root(name = "group", strict = false)
    public static class MemberInviteNtfData extends BaseNotification
    {
    }
    
    /**
     * 同意邀请命令通知
     * 
     * @author k00127978
     * 
     */
    @Root(name = "group", strict = false)
    public static class MemberInviteAcceptNtfData extends BaseNotification
    {
    }
    
    /**
     * 拒绝邀请命令通知
     * 
     * @author k00127978
     * 
     */
    @Root(name = "group", strict = false)
    public static class MemberInviteDeclineNtfData extends BaseNotification
    {
    }
}
