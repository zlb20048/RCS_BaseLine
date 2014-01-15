/*
 * 文件名: MessageNotification.java
 * 版 权： Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-10-12
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass.BroadcastMessageNtf;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass.CommentMessageNtf;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass.CommonMessageData;
import com.huawei.basic.android.im.component.net.xmpp.data.MessageCommonClass.Delay;

/**
 * 消息通知
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-13]
 */
public class MessageNotification
{

    /**
     * MessageNotification Base Structrue
     * @author 周庆龙
     *
     */
    public static class MessageNotificationBase extends BaseNotification
    {
    }
    
    
    /**
     * 消息发送错误通知<BR>
     * 标记消息发送失败
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-3]
     */
    @Root(name = "im", strict = false)
    public static class MessageSendNtf extends MessageNotificationBase
    {
//        <im>
//        <from>juliet@chinaunicom.com/woclient</from>
//        <to>contact@chinaunicom.com</to>
//        <id>purplea7adefbc</id>
//        <error--code>0</error-code>
//        <error-reason> </error-reason>
//        </im>

    }

    /**
     * 接收消息<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "im", strict = false)
    public static class MessageReceivedNtf extends MessageNotificationBase
    {
        /**
         * report
         */
        @Element(name = "report", required = false)
        private String report;

        /**
         * message
         */
        @Element(name = "message", required = false)
        private CommonMessageData message;


        public String getReport()
        {
            return report;
        }

        public void setReport(String report)
        {
            this.report = report;
        }

        public CommonMessageData getMessage()
        {
            return message;
        }

        public void setMessage(CommonMessageData message)
        {
            this.message = message;
        }

    }

    /**
     * message reported<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "im", strict = false)
    public static class MessageReportedNtf extends MessageNotificationBase
    {
        /**
         * report
         */
        @Element(name = "report", required = false)
        private String report;

        /**
         * delay
         */
        @Element(name = "delay", required = false)
        @Namespace(reference = "urn:xmpp:delay")
        private Delay delay;

        public String getReport()
        {
            return report;
        }

        public void setReport(String report)
        {
            this.report = report;
        }

        public Delay getDelay()
        {
            return delay;
        }

        public void setDelay(Delay delay)
        {
            this.delay = delay;
        }

    }


    /**
     * parser broadcast<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-3]
     */
    @Root(name = "im", strict = false)
    public static class BroadcastNtf extends MessageNotificationBase
    {
//        <im>
//        <from>111@chinaunicom.com/woclient</from>
//        <to>222@chinaunicom.com/woclient</to>
//       <id>purplea7adefbc<id>
//        <message>
//         <event xmlns='http://jabber.org/protocol/pubsub#event'>
//          <items node='friend_brdcst'>
//           <item>
//             <feed>
//               <feedID>12345</feedID>
//                 <sndUsr>1000007</sndUsr>
//                 <time>12345678</time>
//                 <appID>11001</appID>
//                 <feedClass>0</feedClass>
//                 <txt><![CDATA[
//                   <body>1111
//                     picture 2222
//                     3333
//                   </body>
//                   <html xmlns='http://jabber.org/protocol/xhtml-im'>
//                     <body xmlns='http://www.w3.org/1999/xhtml'>
//                       <p>1111<br/>
//                       <img alt='picture' src='picture URL'/>2222<br/>3333</p>
//                     </body>
//                   </html>
//                   <audio src=’audio URL’>]]>
//                </txt>
//                <feedType>1</feedType>
//                <fwdCount>0</fwdCount>
//                <cmntCount>0</cmntCount>
//                <channel>6</channel>
//                <atTarget>0</atTarget>
//             </feed>
//           </item>
//          </items>
//         </event>
//        </message>
//       </im>

        /**
         * broadcast message
         */
        
        @Element(name = "message", required = false)
        private BroadcastMessageNtf message;

        public BroadcastMessageNtf getMessage()
        {
            return message;
        }

        public void setMessage(BroadcastMessageNtf message)
        {
            this.message = message;
        }
         
    }

    /**
     * comment <BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "im", strict = false)
    public static class CommentNtf extends MessageNotificationBase
    {
        /**
         * broadcast message
         */
        @Element(name = "message")
        private CommentMessageNtf message;

        public CommentMessageNtf getMessage()
        {
            return message;
        }

        public void setMessage(CommentMessageNtf message)
        {
            this.message = message;
        }

    }
    
    /**
     * EmailNtf<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    @Root(name = "im", strict = false)
    public static class EmailNtf extends MessageNotificationBase
    {
//        <im>
//        <from>cloudmail.chinaunicom.com </from>
//        <to>111@chinaunicom.com/woclient</to>
//        <id>purplea7adefbc<id>
//        <message>
//       <body>2,sender@gmail.com,receiver@gmail.com,邮件主题\,注意逗号,内容摘要\\注意斜线,0,12345
//        </body>
//        </message>
//       </im>
        
        @Element(name = "message", required = false)
        private EmailMessage message;
        
        public EmailMessage getMessage()
        {
            return message;
        }

        public void setMessage(EmailMessage message)
        {
            this.message = message;
        }

        /**
         * EmailMessage<BR>
         * @author qlzhou
         * @version [RCS Client V100R001C03, Mar 1, 2012]
         */
        @Root(strict = false)
        public static class EmailMessage
        {
            @Element(name = "body", required = false)
            private String emailBody;

            public String getEmailBody()
            {
                return emailBody;
            }

            public void setEmailBody(String emailBody)
            {
                this.emailBody = emailBody;
            }
        }
    }
}
