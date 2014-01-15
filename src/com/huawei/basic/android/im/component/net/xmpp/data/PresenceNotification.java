/*
 * 文件名: PresenceNotification.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass.DeviceData;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass.PersonData;

/**
 * presence组件的通知<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-12]
 */
public class PresenceNotification
{
    /**
     * presence 呈现<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class PresenceNtf extends BaseNotification
    {

//        <ps>
//        <from>juliet@chinaunicom.com/woclient</from>
//        <presence>
//          <show>away</show>
//          <status>be right back</status>
//          <priority>0</priority>
//          <person nick='Rabbit' logo='person/logo/f5dc54e4a6d7f8e876d6a5dsf5.png'/>
//        </presence>
//      </ps>


        /**
         * 包含device节点
         */
        @Element(name = "presence", required = false)
        private InnerPresenceNtf presence;

        public InnerPresenceNtf getPresence()
        {
            return presence;
        }

        public void setPresence(InnerPresenceNtf presence)
        {
            this.presence = presence;
        }

        /**
         * InnerPresenceNtf
         * @author k00127978
         *
         */
        @Root(strict = false)
        public static class InnerPresenceNtf
        {
            @Attribute(name = "type", required = false)
            private String type;
            
            /**
             * device
             */
            @Element(name = "device", required = false)
            private DeviceData device;

            @Element(name = "show", required = false)
            private String show;

            @Element(name = "status", required = false)
            private String status;

            @Element(name = "priority", required = false)
            private String priority;
            /**
             * person
             */
            @Element(name = "person", required = false)
            private PersonData person;


            public void setPerson(PersonData person)
            {
                this.person = person;
            }

            public PersonData getPerson()
            {
                return person;
            }

            public DeviceData getDevice()
            {
                return device;
            }

            public void setDevice(DeviceData device)
            {
                this.device = device;
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

            public String getPriority()
            {
                return priority;
            }

            public void setPriority(String priority)
            {
                this.priority = priority;
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
    }

    /**
     * 添加好友响应<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class FriendAddNtf extends BaseNotification
    {
//        <ps>
//        <from>contact@chinaunicom.com</from>
//        <to>juliet@chinaunicom.com/woclient</to>
//        <error-code>0</error-code>
//        <error-reason>validation-needed</error-reason>
//        </ps>

    }

    /**
     * friend adding <BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class FriendAddingNtf extends BaseNotification
    {
        /**
         * presence
         */
        @Element(name = "presence", required = false)
        private InnerPresenceNtf presence;

        public InnerPresenceNtf getPresence()
        {
            return presence;
        }

        public void setPresence(InnerPresenceNtf presence)
        {
            this.presence = presence;
        }

        /**
         * [InnerPresenceNtf]<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-7]
         */
        @Root(strict = false)
        public static class InnerPresenceNtf
        {
            /**
             * person
             */
            @Element(name = "person", required = false)
            private PersonData person;

            /**
             * reason
             */
            @Element(name = "reason", required = false)
            private String reason;

            public PersonData getPerson()
            {
                return person;
            }

            public void setPerson(PersonData person)
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
    }

    /**
     * 通知好友添加成功<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class FriendAddedNtf extends BaseNotification
    {

    }

    /**
     * 拒绝添加好友<BR>
     * 收到添加好友被拒绝通知。仅通知订阅发起方。
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class FriendAddDeclinedNtf extends BaseNotification
    {
    }

    /**
     * 确认是否同意被加为好友响应。<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class FriendAddingConfirmNtf extends BaseNotification
    {
//        <ps>
//        <from>juliet@chinaunicom.com/woclient</from>
//        <to>contact@chinaunicom.com</to>
//        <error-code>0</error-code>
//        <error-reason>internal-server-error</error-reason>
//        </ps>

    }

    /**
     * 删除好友请求响应（结果）<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class FriendRemoveNtf extends BaseNotification
    {
//        <ps>
//        <from>juliet@chinaunicom.com/woclient</from>
//        <to>contact@chinaunicom.com</to>
//        <error-code>0</error-code>
//        <error-reason>internal-server-error</error-reason>
//        </ps>

    }

    /**
     * 通知好友被删除（通知被删除者）<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "ps", strict = false)
    public static class FriendRemovedNtf extends BaseNotification
    {

    }

}
