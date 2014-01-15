/*
 * 文件名: PresenceCommonClass.java
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

import com.huawei.basic.android.im.utils.StringUtil;

/**
 * presence组件中用到的公共类<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-11-7]
 */
public class PresenceCommonClass
{
    
    /**
     * • away：表示实体资源临时离开；
     * • chat：表示实体资源希望进行聊天； 
     * • dnd：表示实体资源状态为忙且不希望被打扰；
     * • busy：表示实体资源状态为忙； 
     * • xa：表示实体资源离开一段时间； 
     * • invisible：表示实体资源希望隐身，接收者终端需要直接忽略本Presence信息
     * ，并显示好友状态为offline（如果用户希望在登录时直接隐身 ，
     * 需要在初始发布时的Presence消息中指明show为invisible）； 
     * •online：表示实体资源在线且available，用户初始发布时的默认状态； 
     * •offline：表示实体资源离线且unavailabe，用户下线后的默认状态； 
     * •hibernate：用户设备进行休眠状态（非PC设备不再接收Presence状态信息
     * ）当终端从该状态切回正常状态时，
     * 终端应该将本地的原有所有好友Presence信息全部清空，
     * 因为这些信息已经在休眠期间失效了；
     */
    public static enum Show
    {
        /**
         * AWAY
         */
        AWAY("away"), 
        /**
         * CHAT 
         */
        CHAT("chat"),
        /**
         * DND
         */
        DND("dnd"), 
        /**
         * BUSY
         */
        BUSY("busy"), 
        /**
         * XA
         */
        XA("xa"), 
        /**
         * INVISIBLE
         */
        INVISIBLE("invisible"), 
        /**
         * ONLINE
         */
        ONLINE("online"), 
        /**
         * OFFLINE
         */
        OFFLINE("offline"), 
        /**
         * HIBERNATE
         */
        HIBERNATE("hibernate");
        
        private final String value;
        
        /**
         * 构造方法
         * @param value value
         */
        Show(String value)
        {
            this.value = value;
        }
        
        public String getValue()
        {
            return value;
        }
        
    };
    
    /**
     * person<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "person", strict = false)
    public static class PersonData
    {
        /**
         * 可选 昵称
         */
        @Attribute(name = "nick", required = false)
        private String nick;
        
        /**
         * 可选 logo
         */
        @Attribute(name = "logo", required = false)
        private String logo;
        
        @Attribute(name = "sign", required = false)
        private String sign;
        
        public void setNick(String nick)
        {
            this.nick = nick;
        }
        
        public void setLogo(String logo)
        {
            this.logo = logo;
        }
        
        public String getSign()
        {
            return sign;
        }
        
        public void setSign(String sign)
        {
            this.sign = sign;
        }
        
        public String getNick()
        {
            return nick;
        }
        
        public String getLogo()
        {
            return logo;
        }
        
        /**
         * 获取字段组成的命令字符串
         * 
         * @return BodyString
         */
        public String getBodyString()
        {
            StringBuilder sb = new StringBuilder(128);
            sb.append("<person");
            if (nick != null)
            {
                sb.append(" nick = '")
                        .append(StringUtil.encodeString(nick))
                        .append("'");
            }
            if (logo != null)
            {
                sb.append(" logo = '")
                        .append(StringUtil.encodeString(logo))
                        .append("'");
            }
            
            if (sign != null)
            {
                sb.append(" sign = '")
                        .append(StringUtil.encodeString(sign))
                        .append("'");
            }
            sb.append("/>");
            return sb.toString();
        }
        
    }
    
    /**
     * 设备<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    
    @Root(name = "device", strict = false)
    public static class DeviceData
    {
        /**
         * 设备类型<BR>
         * 
         * @author 周庆龙
         * @version [RCS Client V100R001C03, 2011-11-7]
         */
        public static enum Type
        {
            /**
             * 操作系统
             */
            SYMBIAN("SYMBIAN"), IPHONE("IPHONE"), ANDROID("Android"), PC("pc");
            
            private final String value;
            
            Type(String value)
            {
                this.value = value;
            }
            
            public String getValue()
            {
                return value;
            }
        }
        
        /**
         * 终端设备类型类型
         */
        @Attribute(name = "type", required = false)
        private String type;
        
        /**
         * 音频能力
         */
        @Element(name = "audio", required = false)
        private String audio;
        
        /**
         * 视频能力
         */
        @Element(name = "video", required = false)
        private String video;
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public void setAudio(String audio)
        {
            this.audio = audio;
        }
        
        public void setVideo(String video)
        {
            this.video = video;
        }
        
        public String getType()
        {
            return type;
        }
        
        public String getAudio()
        {
            return audio;
        }
        
        public String getVideo()
        {
            return video;
        }
        
        /**
         * getBodyString<BR>
         * @return BodyString
         */
        public String getBodyString()
        {
            StringBuilder sb = new StringBuilder(128);
            
            sb.append("<device");
            if (type != null)
            {
                sb.append(" type = '").append(type).append("'");
            }
            sb.append(">");
            
            if (audio != null)
            {
                sb.append("<audio>").append(audio).append("</audio>");
            }
            if (video != null)
            {
                sb.append("<video>").append(video).append("</video>");
            }
            
            sb.append("</device>");
            
            return sb.toString();
        }
        
    }
    
    /**
     * error<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    public static class ErrorData
    {
        @Attribute(name = "type", required = true)
        private String type;
        
        @Element(name = "internal-server-error", required = false)
        private InternalServerError internalServerError;
        
        @Element(name = "not-allowed", required = false)
        private NotAllowed notAllowed;
        
        @Element(name = "validation-needed", required = false)
        private ValidationNeeded validationNeeded;
        
        @Element(name = "mobile-needed", required = false)
        private MobileNeeded mobileNeeded;
        
        @Element(name = "quota-exceeded", required = false)
        private QuotaExceeded quotaExceeded;
        
        public MobileNeeded getMobileNeeded()
        {
            return mobileNeeded;
        }
        
        public void setMobileNeeded(MobileNeeded mobileNeeded)
        {
            this.mobileNeeded = mobileNeeded;
        }
        
        public QuotaExceeded getQuotaExceeded()
        {
            return quotaExceeded;
        }
        
        public void setQuotaExceeded(QuotaExceeded quotaExceeded)
        {
            this.quotaExceeded = quotaExceeded;
        }
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public InternalServerError getInternalServerError()
        {
            return internalServerError;
        }
        
        public void setInternalServerError(
                InternalServerError internalServerError)
        {
            this.internalServerError = internalServerError;
        }
        
        public NotAllowed getNotAllowed()
        {
            return notAllowed;
        }
        
        public void setNotAllowed(NotAllowed notAllowed)
        {
            this.notAllowed = notAllowed;
        }
        
        public ValidationNeeded getValidationNeeded()
        {
            return validationNeeded;
        }
        
        public void setValidationNeeded(ValidationNeeded validationNeeded)
        {
            this.validationNeeded = validationNeeded;
        }
        
    }
    
    /**
     * InternalServerError<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class InternalServerError
    {
        @Attribute(name = "xmlns", required = false)
        private String xmlns;
        
        public String getXmlns()
        {
            return xmlns;
        }
        
        public void setXmlns(String xmlns)
        {
            this.xmlns = xmlns;
        }
        
    }
    
    /**
     * NotAllowed<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class NotAllowed extends InternalServerError
    {
        
    }
    
    /**
     * ValidationNeeded<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class ValidationNeeded extends InternalServerError
    {
        
    }
    
    /**
     * MobileNeeded<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class MobileNeeded extends InternalServerError
    {
    }
    
    /**
     * QuotaExceeded<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class QuotaExceeded extends InternalServerError
    {
    }
    
}
