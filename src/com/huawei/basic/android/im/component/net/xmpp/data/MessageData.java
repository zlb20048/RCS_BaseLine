/*
 * 文件名: Message.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass.CommonMessageData;

/**
 * 消息模块数据结构
 *
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-18]
 */
public class MessageData
{
    
    /**
     * Message data base class, include from and to section
     * @author kuaidc
     *
     */
    public static class MessageBaseData
    {
        /*
        <im>
         <from>111@chinaunicom.com/woclient</from>
         <to>222@chinaunicom.com/woclient</to>
        </im>
        */

        /**
         * from
         */
        private String from;
        
        /**
         * to
         */
        private String to;
        
        /**
         * makeCmdData<BR>
         * @return CmdData
         */
        public String makeCmdData()
        {
            StringBuilder sb = new StringBuilder(128);
            
            sb.append("<im>");
            if (from != null)
            {
                sb.append("<from>").append(from).append("</from>");
            }
            
            if (to != null)
            {
                sb.append("<to>").append(to).append("</to>");
            }
            
            sb.append(getBodyString());
            
            sb.append("</im>");
            return sb.toString();
        }
        
        protected String getBodyString()
        {
            return "";
        }
        
        /**
         * setFrom
         * @param from the from to set
         */
        public void setFrom(String from)
        {
            this.from = from;
        }
        
        /**
         * setTo
         * @param to the to to set
         */
        public void setTo(String to)
        {
            this.to = to;
        }
    }
    
    /**
     * 发送状态报告。<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    public static class MessageReportCmdData extends MessageBaseData implements
            XmlCmdData
    {
        /*
        <im>
         <from>222@chinaunicom.com/woclient</from>
         <to>111@chinaunicom.com/woclient</to>
         <id>1<id>
         <report>received/read/recvandread</report>
        </im>
        */

        /**
         * 必选 消息ID
         * <p>
         * 用于匹配状态报告
         * </p>
         */
        private String id;
        
        /**
         * 必选 消息ID
         * <p>
         * 表示原始消息ID
         * 
         * 值[received/read/recvandread]
         * </p>
         */
        private String report;
        
        public void setId(String id)
        {
            this.id = id;
        }
        
        public void setReport(String report)
        {
            this.report = report;
        }
        
        /**
         * getBodyString<BR>
         * @return BodyString
         * @see com.huawei.basic.android.im.component.net.xmpp.data.
         * MessageData.MessageBaseData#getBodyString()
         */
        @Override
        protected String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            
            if (id != null)
            {
                sb.append("<id>").append(id).append("</id>");
            }
            if (report != null)
            {
                sb.append("<report>").append(report).append("</report>");
            }
            
            return sb.toString();
        }
    }
    
    /**
     * message命令及参数封装<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-12]
     */
    public static class MessageSendCmdData extends MessageBaseData implements
            XmlCmdData
    {
        
        /**
         * 消息报告
         */
        private String report;
        
        /**
         * 消息体
         */
        private CommonMessageData message;
        
        public void setReport(String report)
        {
            this.report = report;
        }
        
        public void setMessage(CommonMessageData message)
        {
            this.message = message;
        }
        
        /**
         * 获取对象的xml格式串<BR>
         * 
         * @return 对象的xml格式串
         * @see java.lang.Object#toString()
         */
        @Override
        protected String getBodyString()
        {
            // 非必要字段设置为空
            StringBuffer sb = new StringBuffer();
            
            if (message != null)
            {
                sb.append(message.getBodyString());
            }
            
            if (report != null)
            {
                sb.append("<report>").append(report).append("</report>");
            }
            
            return sb.toString();
        }
    }
    
    /**
     * 广播命令<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-15]
     */
    public static class BroadCastCmdData
    {
        
    }
    
}
