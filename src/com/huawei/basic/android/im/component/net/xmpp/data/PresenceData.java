/*
 * 文件名: PresenceData.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass.DeviceData;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass.PersonData;
import com.huawei.basic.android.im.component.net.xmpp.data.PresenceCommonClass.Show;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * presence组件的参数<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-18]
 */
public class PresenceData
{
    /**
     * PresenceBaseData<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class PresenceBaseData
    {
        /**
         * from
         */
        private String from;
        
        /**
         * to
         */
        private String to;
        
        public void setFrom(String pFrom)
        {
            this.from = pFrom;
        }
        
        public void setTo(String pTo)
        {
            this.to = pTo;
        }
        
        /**
         * 生成当前类的Body内容字符串
         * 
         * @return BodyString
         */
        public String getBodyString()
        {
            return "";
        }
        
        /**
         * makeCmdData<BR>
         * @return CmdData
         */
        public String makeCmdData()
        {
            StringBuilder sb = new StringBuilder(128);
            sb.append("<ps>");
            if (from != null)
            {
                sb.append("<from>").append(from).append("</from>");
            }
            
            if (to != null)
            {
                sb.append("<to>").append(to).append("</to>");
            }
            
            sb.append(getBodyString());
            sb.append("</ps>");
            
            return sb.toString();
        }
    }
    
    /**
     * 增加好友<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    public static class FriendAddCmdData extends PresenceBaseData
    {
        // <ps>
        // <from>juliet@chinaunicom.com/woclient</from>
        // <to>contact@chinaunicom.com</to>
        // <presence>
        // 参考联通XMPP规范
        // </presence>
        // </ps>
        
        /**
         * 用户的个人信息子集元素
         */
        private PersonData person = null;
        
        /**
         * 可选 希望携带的验证信息
         */
        private String reason = null;
        
        /**
         * 用户的个人信息子集元素<BR>
         * 
         * @param pPerson pPerson
         */
        public void setPerson(PersonData pPerson)
        {
            this.person = pPerson;
        }
        
        /**
         * 设置希望携带的验证信息<BR>
         * 
         * @param reason 希望携带的验证信息
         */
        public void setReason(String reason)
        {
            this.reason = reason;
        }
        
        /**
         * 获取xml格式的串<BR>
         * 
         * @return xml格式的串
         */
        @Override
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<presence>");
            if (person != null)
            {
                sb.append(person.getBodyString());
            }
            if (reason != null)
            {
                sb.append("<reason>")
                        .append(StringUtil.encodeString(reason))
                        .append("</reason>");
            }
            
            sb.append("</presence>");
            
            return sb.toString();
        }
    }
    
    /**
     * 同意或者拒绝订阅请求<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    public static class FriendAddingConfirmCmdData extends PresenceBaseData
    {
        
        /**
         * 是否订阅<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-7]
         */
        public static enum Type
        {
            /**
             * TYPE（同意拒绝）
             */
            SUBSCRIBED("subscribed"), UNSUBSCRIBED("unsubscribed");
            
            /**
             * value
             */
            private final String value;
            
            Type(String value)
            {
                this.value = value;
            }
            
            public String getValue()
            {
                return value;
            }
        };
        
        /**
         * type
         */
        private Type type;
        
        public void setType(Type type)
        {
            this.type = type;
        }
        
        /**
         * 获得对象的xml格式串<BR>
         * 
         * @return 对象的xml格式串
         * @see java.lang.Object#toString()
         */
        @Override
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<presence ");
            if (type != null)
            {
                sb.append(" type = '").append(type.getValue()).append("'");
            }
            sb.append("/>");
            
            return sb.toString();
            
        }
        
    }
    
    /**
     * 删除好友<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    public static class FriendRemoveCmdData extends PresenceBaseData
    {
    }
    
    /**
     * 发布状态<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    public static class PublishCmdData extends PresenceBaseData
    {
        /**
         * 可选 device
         */
        private DeviceData device = null;
        
        /**
         * 状态及显示，包括命令优先级
         */
        private Show show;
        
        /**
         * status
         */
        private String status;
        
        /**
         * priority
         */
        private String priority;
        
        /**
         * person
         */
        private PersonData person;
        
        public void setPreson(PersonData pPerson)
        {
            this.person = pPerson;
        }
        
        public void setDevice(DeviceData device)
        {
            this.device = device;
        }
        
        public void setStatus(String status)
        {
            this.status = status;
        }
        
        public void setShow(Show show)
        {
            this.show = show;
        }
        
        public void setPriority(String priority)
        {
            this.priority = priority;
        }
        
        /**
         * 获取xml格式的串<BR>
         * 
         * @return xml格式的串
         * @see java.lang.Object#toString()
         */
        @Override
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<presence>");
            
            if (show != null)
            {
                // Show 字段
                sb.append("<show>").append(show.getValue()).append("</show>");
            }
            if (status != null)
            {
                sb.append("<status>")
                        .append(StringUtil.encodeString(status))
                        .append("</status>");
            }
            if (priority != null)
            {
                sb.append("<priority>").append(priority).append("</priority>");
            }
            if (device != null)
            {
                sb.append(device.getBodyString());
            }
            if (person != null)
            {
                sb.append(person.getBodyString());
            }
            
            sb.append("</presence>");
            
            return sb.toString();
        }
    }
    
    /**
     * 发布离线消息<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    public static class UnavailableCmdData extends PresenceBaseData
    {
    }
}
