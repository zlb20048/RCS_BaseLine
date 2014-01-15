/*
 * 文件名: MessageCommonClass.java
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
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import com.huawei.basic.android.im.utils.StringUtil;

/**
 * message common classes<BR>
 *
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-11-7]
 */
public class MessageCommonClass
{
    /**
     * IM模块消息体的封装<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-1]
     */
    @Root(strict = false)
    public static class CommonMessageData
    {
        
        @Attribute(name = "id", required = false)
        private String id;
        
        /**
         * 消息类型，群发即时消息（文本消息/多媒体消息）
         * 固定填为chat；群发短信固定填为sm 群组消息时，
         * 公共消息填写：im-gp-public
         * 私人消息填写：im-gp-private
         */
        @Attribute(name = "type", required = false)
        private String type = "chat";
        
        @Attribute(name = "from", required = false)
        private String from;
        
        @Attribute(name = "to", required = false)
        private String to;
        
        @Element(name = "report", required = false)
        private String report;
        
        /**
         * 可选 文本消息
         */
        @Element(name = "body", required = false)
        private String body;
        
        /**
         * request可选
         */
        @Element(name = "request", required = false)
        private Request request;
        
        /**
         * delay 可选
         */
        @Element(name = "delay", required = false)
        private Delay delay;
        
        /**
         * 可选 用于携带接收方从服务器下属音频文件的信息
         */
        @Element(name = "audio", required = false)
        private Audio audio;
        
        /**
         * 可选 用于携带接收方从服务器下属视频文件的信息
         */
        @Element(name = "video", required = false)
        private Video video;
        
        /**
         * 可选 用于携带接收方从服务器下属图片文件信息
         */
        @Element(name = "image", required = false)
        private Image image;
        
        /**
         * 可选 用于携带贴图信息
         */
        @Element(name = "emoji", required = false)
        private Emoji emoji;
        
        /**
         * 可选 用于携带位置信息
         */
        @Element(name = "location", required = false)
        private Location location;
        
        /**
         * 可选 UTF-8编码的纯文本消息内容，
         * 使用多媒体消息的文本部分及图片的alt描述拼接而成。
         * 当接收方终端无法显示多媒体消息时，
         * 可以此作为替代内容显示。
         */
        // @Element(name = "html", required = false)
        private Html html;
        
        /**
         * 接受者列表
         */
        private List<Address> addresses;
        
        public Delay getDelay()
        {
            return delay;
        }
        
        public void setDelay(Delay delay)
        {
            this.delay = delay;
        }
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public Request getRequest()
        {
            return request;
        }
        
        public void setRequest(Request request)
        {
            this.request = request;
        }
        
        public String getBody()
        {
            return body;
        }
        
        public void setBody(String body)
        {
            this.body = body;
        }
        
        public List<Address> getAddresses()
        {
            return addresses;
        }
        
        public void setAddresses(List<Address> addresses)
        {
            this.addresses = addresses;
        }
        
        public Audio getAudio()
        {
            return audio;
        }
        
        public void setAudio(Audio audio)
        {
            this.audio = audio;
        }
        
        public Video getVideo()
        {
            return video;
        }
        
        public void setVideo(Video video)
        {
            this.video = video;
        }
        
        public Image getImage()
        {
            return image;
        }
        
        public void setImage(Image image)
        {
            this.image = image;
        }
        
        public Emoji getEmoji()
        {
            return emoji;
        }
        
        public void setEmoji(Emoji emoji)
        {
            this.emoji = emoji;
        }
        
        public Location getLocation()
        {
            return location;
        }
        
        public void setLocation(Location location)
        {
            this.location = location;
        }
        
        public Html getHtml()
        {
            return html;
        }
        
        public void setHtml(Html html)
        {
            this.html = html;
        }
        
        public String getId()
        {
            return id;
        }
        
        public void setId(String id)
        {
            this.id = id;
        }
        
        public String getFrom()
        {
            return from;
        }
        
        public void setFrom(String from)
        {
            this.from = from;
        }
        
        public String getTo()
        {
            return to;
        }
        
        public void setTo(String to)
        {
            this.to = to;
        }
        
        public String getReport()
        {
            return report;
        }
        
        public void setReport(String report)
        {
            this.report = report;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            // 生成消息体xml 串
            sb.append("<message");
            
            if (type != null)
            {
                sb.append(" type = '" + type + "'");
            }
            sb.append(">");
            
            // if (request != null)
            // {
            // sb.append(request.getBodyString());
            // }
            if (body != null)
            {
                sb.append("<body>")
                        .append(StringUtil.encodeString(body))
                        .append("</body>");
            }
            if (audio != null)
            {
                sb.append(audio.getBodyString());
            }
            if (video != null)
            {
                sb.append(video.getBodyString());
            }
            if (image != null)
            {
                sb.append(image.getBodyString());
            }
            if (emoji != null)
            {
                sb.append(emoji.getBodyString());
            }
            if (location != null)
            {
                sb.append(location.getBodyString());
            }
            if (html != null)
            {
                sb.append(html.getBodyString());
            }
            if (addresses != null)
            {
                // 收件人列表
                sb.append("<addresses>");
                for (Address address : addresses)
                {
                    sb.append(address.getBodyString());
                }
                sb.append("</addresses>");
            }
            sb.append("</message>");
            
            return sb.toString();
        }
    }
    
    /**
     * item对象<BR>
     * [功能详细描述]
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-10-13]
     */
    public static class Address
    {
        /**
         * 类型
         */
        private String type;
        
        /**
         * 接收者jid
         */
        private String jid;
        
        /**
         * 描述
         */
        private String desc;
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public String getJid()
        {
            return jid;
        }
        
        public void setJid(String jid)
        {
            this.jid = jid;
        }
        
        public String getDesc()
        {
            return desc;
        }
        
        public void setDesc(String desc)
        {
            this.desc = desc;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            
            sb.append("<address type = ");
            sb.append(type == null ? "''" : "'" + type + "'");
            // jid
            sb.append(" jid = ");
            sb.append(jid == null ? "''" : "'" + jid + "'");
            
            // desc
            sb.append(" desc = ");
            sb.append(desc == null ? "''" : "'" + desc + "'");
            sb.append("/>");
            return sb.toString();
        }
    }
    
     
    /**
     * audio<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    public static class CommonMultiMediaData
    {
        /**
         * 必选 src
         */
        @Attribute(name = "src", required = false)
        private String src;
        
        /**
         * 可选 alt
         */
        @Attribute(name = "alt", required = false)
        private String alt;
        

        /**
         * 可选 size
         */
        @Attribute(name = "size", required = false)
        private String size;
        
        public String getSrc()
        {
            return src;
        }
        
        public void setSrc(String src)
        {
            this.src = src;
        }
        
        public String getAlt()
        {
            return alt;
        }
        
        public void setAlt(String alt)
        {
            this.alt = alt;
        }

        public String getSize()
        {
            return size;
        }
        
        public void setSize(String size)
        {
            this.size = size;
        }
        
    }
    
    /**
     * video<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "video", strict = false)
    public static class Video extends CommonMultiMediaData
    {
        /**
         * 可选 playtime
         */
        @Attribute(name = "playtime", required = false)
        private String playtime;
        
        @Attribute(name = "thumbnail", required = false)
        private String thumbnail;
        
        public String getPlaytime()
        {
            return playtime;
        }
        
        public void setPlaytime(String playtime)
        {
            this.playtime = playtime;
        }
        
        public String getThumbnail()
        {
            return thumbnail;
        }
        
        public void setThumbnail(String thumbnail)
        {
            this.thumbnail = thumbnail;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("<video");
            if (getSrc() != null)
            {
                sb.append(" src = '")
                        .append(StringUtil.encodeString(getSrc()))
                        .append("'");
            }
            if (getAlt() != null)
            {
                sb.append(" alt='");
                sb.append(getAlt());
                sb.append("'");
            }
            if (getSize() != null)
            {
                sb.append(" size='");
                sb.append(getSize());
                sb.append("'");
            }
            if (playtime != null)
            {
                sb.append(" playtime='");
                sb.append(playtime);
                sb.append("'");
            }
            if (thumbnail != null)
            {
                sb.append(" thumbnail='");
                sb.append(StringUtil.encodeString(thumbnail));
                sb.append("'");
            }
            sb.append("/>");
            return sb.toString();
        }
    }
    
    /**
     * audio<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "audio", strict = false)
    public static class Audio extends CommonMultiMediaData
    {
        
        /**
         * 可选 playtime
         */
        @Attribute(name = "playtime", required = false)
        private String playtime;
        
        public String getPlaytime()
        {
            return playtime;
        }
        
        public void setPlaytime(String playtime)
        {
            this.playtime = playtime;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("<audio");
            if (getSrc() != null)
            {
                sb.append(" src = '")
                        .append(StringUtil.encodeString(getSrc()))
                        .append("'");
            }
            if (getAlt() != null)
            {
                sb.append(" alt='");
                sb.append(getAlt());
                sb.append("'");
            }
            if (getSize() != null)
            {
                sb.append(" size='");
                sb.append(getSize());
                sb.append("'");
            }
            if (playtime != null)
            {
                sb.append(" playtime='");
                sb.append(playtime);
                sb.append("'");
            }
            sb.append("/>");
            return sb.toString();
        }
    }
    
    /**
     * image <BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "image", strict = false)
    public static class Image extends CommonMultiMediaData
    {
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("<image");
            if (getSrc() != null)
            {
                sb.append(" src = '")
                        .append(StringUtil.encodeString(getSrc()))
                        .append("'");
            }
            if (getAlt() != null)
            {
                sb.append(" alt='");
                sb.append(getAlt());
                sb.append("'");
            }
            if (getSize() != null)
            {
                sb.append(" size='");
                sb.append(getSize());
                sb.append("'");
            }
            sb.append("/>");
            return sb.toString();
        }
    }
    
    /**
     * image <BR>
     *
     * @author liying00124251
     * @version [RCS Client V100R001C03, 2012-4-23]
     */
    @Root(name = "emoji", strict = false)
    public static class Emoji extends CommonMultiMediaData
    {
        
        /**
         * 贴图的唯一标识，贴图必选
         */
        @Attribute(name = "ttid", required = false)
        private String ttid;
        
        public String getTtid()
        {
            return ttid;
        }
        
        public void setTtid(String ttid)
        {
            this.ttid = ttid;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("<emoji");
            if (getAlt() != null)
            {
                sb.append(" alt = '").append(getAlt()).append("'");
            }
            if (getTtid() != null)
            {
                sb.append(" ttid ='");
                sb.append(getTtid());
                sb.append("'");
            }
            sb.append("/>");
            return sb.toString();
        }
    }
    
    /**
     * 
     * 我的位置<BR>
     * @author lidan
     * @version [RCS Client V100R001C03, 2012-5-15]
     */

    @Root(name = "location", strict = false)
    public static class Location extends CommonMultiMediaData
    {
        /**
         * 经度
         */

        @Attribute(name = "lo", required = false)
        private String lo;
        
        /**
         * 纬度
         */
        @Attribute(name = "la", required = false)
        private String la;
        
        /**
         * 描述
         */
        @Attribute(name = "desc", required = false)
        private String desc;
        
        
        public String getLo()
        {
            return lo;
        }
        public void setLo(String lo)
        {
            this.lo = lo;
        }
        
        public String getLa()
        {
            return la;
        }
        
        public void setLa(String la)
        {
            this.la = la;
        }
        
        public String getDesc()
        {
            return desc;
        }
        
        public void setDesc(String desc)
        {
            this.desc = desc;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("<location");
            sb.append(" lo='").append(getLo()).append("'");
            sb.append(" la='").append(getLa()).append("'");
            if (null != getDesc())
            {
                sb.append(" desc='")
                        .append(StringUtil.encodeString(getDesc()))
                        .append("'");
            }
            sb.append("/>");
            return sb.toString();
        }
    }

    
    /**
     * [html]<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "html", strict = false)
    public static class Html
    {
        /**
         * body
         */
        private String body;
        
        public String getBody()
        {
            return body;
        }
        
        public void setBody(String body)
        {
            this.body = body;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            if (body != null)
            {
                return new StringBuffer().append("<html><body xmlns='http://www.w3.org/1999/xhtml'>")
                        .append(body)
                        .append("</body></html>")
                        .toString();
            }
            return "";
        }
    }
    
    /**
     * 发送方要求接收方返回指定类型的报告时，携带此元素。
     * 要求的报告类型，通过type属性携带 <BR>
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    public static class Request
    {
        /**
         * TYPE_ALL
         */
        public static final String TYPE_ALL = "all";
        
        /**
         * TYPE_DELIVERY
         */
        public static final String TYPE_DELIVERY = "delivery";
        
        /**
         * TYPE_READ
         */
        public static final String TYPE_READ = "read";
        
        /**
         * type 类型
         */
        @Attribute(name = "type")
        @Namespace(reference = "urn:xmpp:receipts")
        private String type;
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        /**
         * 转换成字符串
         *
         * @return 字符串
         */
        public String getBodyString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("<request");
            if (type != null)
            {
                sb.append(" type = '").append(type).append("'");
            }
            sb.append("/>");
            return sb.toString();
        }
        
    }
    
    /**
     * deley<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-7]
     */
    @Root(name = "delay", strict = false)
    public static class Delay
    {
        
        @Text(required = false)
        private String text;
        
        /**
         * from
         */
        @Attribute(name = "from", required = false)
        private String from;
        
        /**
         * stamp
         */
        @Attribute(name = "stamp", required = false)
        private String stamp;
        
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
        
        public String getText()
        {
            return text;
        }
        
        public void setText(String text)
        {
            this.text = text;
        }
    }
    
    /**
     * message in broadcast<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-3]
     */
    @Root(strict = false)
    public static class BroadcastMessageNtf
    {
        // <message>
        // <event xmlns='http://jabber.org/protocol/pubsub#event'>
        // <items node='friend_brdcst'>
        // <item>
        // <feed>
        // <feedID>12345</feedID>
        // <sndUsr>1000007</sndUsr>
        // <time>12345678</time>
        // <appID>11001</appID>
        // <feedClass>0</feedClass>
        // <txt><![CDATA[
        // <body>1111
        // picture 2222
        // 3333
        // </body>
        // <html xmlns='http://jabber.org/protocol/xhtml-im'>
        // <body xmlns='http://www.w3.org/1999/xhtml'>
        // <p>1111<br/>
        // <img alt='picture' src='picture URL'/>2222<br/>3333</p>
        // </body>
        // </html>
        // <audio src=’audio URL’>]]>
        // </txt>
        // <feedType>1</feedType>
        // <fwdCount>0</fwdCount>
        // <cmntCount>0</cmntCount>
        // <channel>6</channel>
        // <atTarget>0</atTarget>
        // </feed>
        // </item>
        // </items>
        // </event>
        // </message>
        
        /**
         * event
         */
        @Element(name = "event")
        @Namespace(reference = "http://jabber.org/protocol/pubsub#event")
        private BroadcastEvent event;
        
        public BroadcastEvent getEvent()
        {
            return event;
        }
        
        public void setEvent(BroadcastEvent event)
        {
            this.event = event;
        }
        
    }
    
    /**
     * 评论<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-17]
     */
    public static class CommentMessageNtf
    {
        /**
         * event
         */
        @Element(name = "event", required = false)
        private CommentEvent event;
        
        public CommentEvent getEvent()
        {
            return event;
        }
        
        public void setEvent(CommentEvent event)
        {
            this.event = event;
        }
        
    }
    
    /**
     * 评论接收信息<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-17]
     */
    public static class CommentItem
    {
        /**
         * cmnt
         */
        @Element(name = "cmnt", required = true)
        private Cmnt cmnt;
        
        public Cmnt getCmnt()
        {
            return cmnt;
        }
        
        public void setCmnt(Cmnt cmnt)
        {
            this.cmnt = cmnt;
        }
        
    }
    
    /**
     * 评论<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-17]
     */
    @Root(strict = false)
    public static class Cmnt
    {
        /**
         * 广播id
         */
        @Element(name = "feedID", required = true)
        private String feedID;
        
        /**
         * '@'
         */
        @Element(name = "atTarget", required = false)
        private String atTarget;
        
        public String getFeedID()
        {
            return feedID;
        }
        
        public void setFeedID(String feedID)
        {
            this.feedID = feedID;
        }
        
        public String getAtTarget()
        {
            return atTarget;
        }
        
        public void setAtTarget(String atTarget)
        {
            this.atTarget = atTarget;
        }
        
    }
    
    /**
     * item<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-3]
     */
    @Root(name = "item")
    public static class BroadcastItem
    {
        /**
         * feed
         */
        @Element(name = "feed", required = false)
        private Feed feed;
        
        public Feed getFeed()
        {
            return feed;
        }
        
        public void setFeed(Feed feed)
        {
            this.feed = feed;
        }
        
    }
    
    /**
     * 广播event<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-3]
     */
    @Root(name = "event", strict = false)
    @NamespaceList(value = {
            @Namespace(reference = "http://jabber.org/protocol/pubsub#event"),
            @Namespace(reference = "friend_brdcst") })
    public static class BroadcastEvent
    {
        /**
         * items
         */
        @ElementList(name = "items", required = false, inline = false)
        private List<BroadcastItem> items;
        
        public List<BroadcastItem> getItems()
        {
            return items;
        }
        
        public void setItems(List<BroadcastItem> items)
        {
            this.items = items;
        }
        
    }
    
    /**
     * 评论event<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-17]
     */
    @NamespaceList(value = {
            @Namespace(reference = "http://jabber.org/protocol/pubsub#event"),
            @Namespace(reference = "friend_brdcst_cmnt") })
    public static class CommentEvent
    {
        
        /**
         * items
         */
        @ElementList(name = "items", required = false, inline = false)
        private List<CommentItem> items;
        
        public List<CommentItem> getItems()
        {
            return items;
        }
        
        public void setItems(List<CommentItem> items)
        {
            this.items = items;
        }
        
    }
    
    /**
     * feed<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-3]
     */
    @Root(name = "feed", strict = false)
    public static class Feed
    {
        // <feed>
        // <feedID>12345</feedID>
        // <sndUsr>1000007</sndUsr>
        // <time>12345678</time>
        // <appID>11001</appID>
        // <feedClass>0</feedClass>
        // <txt><![CDATA[
        // <body>1111
        // picture 2222
        // 3333
        // </body>
        // <html xmlns='http://jabber.org/protocol/xhtml-im'>
        // <body xmlns='http://www.w3.org/1999/xhtml'>
        // <p>1111<br/>
        // <img alt='picture' src='picture URL'/>2222<br/>3333</p>
        // </body>
        // </html>
        // <audio src=’audio URL’>]]>
        // </txt>
        // <feedType>1</feedType>
        // <fwdCount>0</fwdCount>
        // <cmntCount>0</cmntCount>
        // <channel>6</channel>
        // <atTarget>0</atTarget>
        // </feed>
        
        /**
         * feedId
         */
        @Element(name = "feedID", required = false)
        private String feedId;
        
        /**
         * sndUsr
         */
        @Element(name = "sndUsr", required = false)
        private String sendUser;
        
        /**
         * time
         */
        @Element(name = "time", required = false)
        private String time;
        
        /**
         * appID
         */
        @Element(name = "appID", required = false)
        private String appID;
        
        /**
         * feedClass
         */
        @Element(name = "feedClass", required = false)
        private String feedClass;
        
        /**
         * txt
         */
        @Element(name = "txt", required = false)
        private String txt;
        
        /**
         * feedType
         */
        @Element(name = "feedType", required = false)
        private int feedType;
        
        /**
         * fwdCount
         */
        @Element(name = "fwdCount", required = false)
        private int forwardCount;
        
        /**
         * cmntCount
         */
        @Element(name = "cmntCount", required = false)
        private int commentCount;
        
        /**
         * channel
         */
        @Element(name = "channel", required = false)
        private String channel;
        
        /**
         * atTarget
         */
        @Element(name = "atTarget", required = false)
        private String atTarget;
        
        /**
         * originFeed
         */
        @Element(name = "originFeed", required = false)
        private Feed originFeed;
        
        @Element(name = "rcvUser", required = false)
        private String rcvUser;
        
        // 接收者列表
        public String getRcvUser()
        {
            return rcvUser;
        }
        
        public void setRcvUser(String rcvUser)
        {
            this.rcvUser = rcvUser;
        }
        
        public Feed getOriginFeed()
        {
            return originFeed;
        }
        
        public void setOriginFeed(Feed originFeed)
        {
            this.originFeed = originFeed;
        }
        
        public String getFeedId()
        {
            return feedId;
        }
        
        public void setFeedId(String feedId)
        {
            this.feedId = feedId;
        }
        
        public String getSendUser()
        {
            return sendUser;
        }
        
        public void setSendUser(String sendUser)
        {
            this.sendUser = sendUser;
        }
        
        public String getTime()
        {
            return time;
        }
        
        public void setTime(String time)
        {
            this.time = time;
        }
        
        public String getAppID()
        {
            return appID;
        }
        
        public void setAppID(String appID)
        {
            this.appID = appID;
        }
        
        public String getFeedClass()
        {
            return feedClass;
        }
        
        public void setFeedClass(String feedClass)
        {
            this.feedClass = feedClass;
        }
        
        public String getTxt()
        {
            return txt;
        }
        
        public void setTxt(String txt)
        {
            this.txt = txt;
        }
        
        public int getFeedType()
        {
            return feedType;
        }
        
        public void setFeedType(int feedType)
        {
            this.feedType = feedType;
        }
        
        public int getForwardCount()
        {
            return forwardCount;
        }
        
        public void setForwardCount(int forwardCount)
        {
            this.forwardCount = forwardCount;
        }
        
        public int getCommentCount()
        {
            return commentCount;
        }
        
        public void setCommentCount(int commentCount)
        {
            this.commentCount = commentCount;
        }
        
        public String getChannel()
        {
            return channel;
        }
        
        public void setChannel(String channel)
        {
            this.channel = channel;
        }
        
        public String getAtTarget()
        {
            return atTarget;
        }
        
        public void setAtTarget(String atTarget)
        {
            this.atTarget = atTarget;
        }
        
    }
    
    /**
     * FileNtf<BR>
     *
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-3]
     */
    @Root(name = "file")
    public static class FileNtf
    {
        /**
         * name
         */
        @Attribute(name = "name", required = false)
        private String name;
        
        /**
         * size
         */
        @Attribute(name = "size", required = false)
        private String size;
        
        /**
         * date
         */
        @Attribute(name = "date", required = false)
        private String date;
        
        /**
         * hash
         */
        @Attribute(name = "hash", required = false)
        private String hash;
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getSize()
        {
            return size;
        }
        
        public void setSize(String size)
        {
            this.size = size;
        }
        
        public String getDate()
        {
            return date;
        }
        
        public void setDate(String date)
        {
            this.date = date;
        }
        
        public String getHash()
        {
            return hash;
        }
        
        public void setHash(String hash)
        {
            this.hash = hash;
        }
        
    }
}