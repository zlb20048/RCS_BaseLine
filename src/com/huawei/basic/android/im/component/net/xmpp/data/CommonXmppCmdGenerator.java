/*
 * 文件名: XmppCommonClass.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import com.huawei.basic.android.im.utils.StringUtil;

/**
 * XMPP命令生成器<BR>
 * 通过属性名、属性值生成XMPP命令XML字符串
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, 2011-11-7]
 */
public class CommonXmppCmdGenerator
{
    
    /**
     * from节点<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-6]
     */
    public static class FromCmdElement
    {
        /**
         * from
         */
        private String from;
        
        /**
         * getFrom<BR>
         * @return from
         */
        public String getFrom()
        {
            return from;
        }
        
        /**
         * setFrom<BR>
         * @param from from
         */
        public void setFrom(String from)
        {
            this.from = from;
        }
        
        /**
         * 生成当前类的Body内容字符串
         * @return  BodyString
         */
        public String getBodyString()
        {
            if (from != null)
            {
                return "<from>" + from + "</from>";
            }
            
            return "";
        }
        
        /**
         * makeCmdData<BR>
         * @return  CmdData
         */
        public String makeCmdData()
        {
            StringBuilder sb = new StringBuilder(128);
            sb.append("<ps>");
            sb.append(getBodyString());
            sb.append("</ps>");
            
            return sb.toString();
        }
    }
    
    /**
     * 包含from和to节点<BR>
     * 
     * @author 周庆龙
     * @version [RCS Client V100R001C03, 2011-11-6]
     */
    public static class FromAndToCmdElement extends FromCmdElement
    {
        /**
         * to
         */
        private String to;
        
        /**
         * setTo<BR>
         * @param to to
         */
        public void setTo(String to)
        {
            this.to = to;
        }
        
        /**
         * getBodyString<BR>
         * @return  BodyString
         * @see com.huawei.basic.android.im.component.net.xmpp.data.
         * CommonXmppCmdGenerator.FromCmdElement#getBodyString()
         */
        @Override
        public String getBodyString()
        {
            StringBuilder sb = new StringBuilder(64);
            sb.append(super.getBodyString());
            
            if (to != null)
            {
                sb.append("<to>").append(to).append("</to>");
            }
            
            return sb.toString();
        }
        
    }
    
    /**
     * Group config section's field data<BR>
     * @author qlzhou
     * @version [RCS Client V100R001C03, Mar 1, 2012]
     */
    public static class GroupConfigFieldData
    {
        /**
         * label
         */
        @Attribute(name = "label", required = false)
        private String label;
        
        /**
         * var
         */
        @Attribute(name = "var", required = false)
        private String var;
        
        /**
         * value
         */
        @Element(name = "value", required = false)
        private String value;
        
        /**
         * type
         */
        @Attribute(name = "type", required = false)
        private String type;
        
        /**
         * 需要一个空的构造函数给simpleframework使用
         */
        public GroupConfigFieldData()
        {
        }
        
        /**
         * GroupConfigFieldData
         * @param var var
         * @param value value
         */
        public GroupConfigFieldData(String var, String value)
        {
            this.var = var;
            this.value = value;
        }
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public String getLabel()
        {
            return label;
        }
        
        public void setLabel(String label)
        {
            this.label = label;
        }
        
        public String getVar()
        {
            return var;
        }
        
        public void setVar(String var)
        {
            this.var = var;
        }
        
        public String getValue()
        {
            return value;
        }
        
        public void setValue(String value)
        {
            this.value = value;
        }
        
        /**
         * get all field element and attribute make the xml data
         * 
         * @return xml data string
         */
        public String getBodyString()
        {
            StringBuilder sb = new StringBuilder(64);
            
            sb.append("<field var='").append(var).append("'>");
            if (value != null)
            {
                sb.append("<value>")
                        .append(StringUtil.encodeString(value))
                        .append("</value>");
            }
            sb.append("</field>");
            return sb.toString();
        }
    }
}
