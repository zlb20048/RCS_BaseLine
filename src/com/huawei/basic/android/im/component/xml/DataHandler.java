/*
 * 文件名: DataHandler.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 盛兴亚
 * 创建时间:Feb 15, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * 使用SAX工具解析XML时的中间Handler类，该类目的只是为了抽取出某些公共属性和方法<BR>
 * [功能详细描述]
 * @author 盛兴亚
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public abstract class DataHandler extends DefaultHandler
{
    
    /**
     * 当前的值
     */
    private String currentValue;
    
    /**
     * 当前解析到的结点正文，在characters方法中赋值
     */
    private StringBuffer currentBuffer = new StringBuffer();
    
    /**
     * 当前解析到的结点元素名称，需要在startElement方法中赋值
     */
    private String currentElement;
    
    /**
     * 解析完生产的对象
     */
    private Object baseInfo;
    
    /**
     * 构造函数
     */
    public DataHandler()
    {
        baseInfo = new Object();
    }
    
    public StringBuffer getCurrentBuffer()
    {
        return currentBuffer;
    }
    
    public void setCurrentBuffer(StringBuffer currentBuffer)
    {
        this.currentBuffer = currentBuffer;
    }
    
    public String getCurrentValue()
    {
        return currentValue;
    }
    
    public void setCurrentValue(String currentValue)
    {
        this.currentValue = currentValue;
    }
    
    public String getCurrentElement()
    {
        return currentElement;
    }
    
    public void setCurrentElement(String currentElement)
    {
        this.currentElement = currentElement;
    }
    
    /**
     * 当遇到标签中的字符串时，调用这个方法，它的参数是一个字符数组
     * 
     * @param ch 字符数组
     * @param start 起始位置
     * @param length 字符数组长度
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        // 取出目前节点对应的值
        currentBuffer.append(ch, start, length);
        currentValue = currentBuffer.toString();
        currentValue = new String(ch, start, length).trim();
        currentValue = currentValue.trim();
    }
    
    /**
     * 将当前值清除
     */
    public void reset()
    {
        if (null != currentBuffer)
        {
            currentBuffer.delete(0, currentBuffer.length());
            currentValue = "";
        }
    }
    
    /**
     * 开始解析某个XML片段的开始元素，子类需要重写该方法完成相应子类中info对象的解析
     * 
     * @param uri namespaceURI域名
     * @param localName 标签名
     * @param qName 标签的修饰前缀
     * @param attr 标签所包含的属性列表
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attr) throws SAXException
    {
        reset();
        // currentBuffer = new StringBuffer();
        // 开始解析某个XML片段的开始元素，子类需要重写该方法完成相应子类中info对象的解析
        // 一般是对currentElement属性的赋值，如果有Attributes值，需要在该方法里完成取值
        // 在该处注释是为了不覆盖该方法原来父类中的方法注释
    }
    
    /**
     * 开始解析到某个XML片段的结束元素，子类需要重写该方法完成相应子类中info对象的解析
     * 
     * @param uri namespaceURI域名
     * @param localName 标签名
     * @param qName 标签的修饰前缀
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        // 开始解析到某个XML片段的结束元素，子类需要重写该方法完成相应子类中info对象的解析
        // 一般是根据qName的值，将currentValue赋给info对象的某个属性，这里的info需要强转为相应的实际子类型再赋值
        // 在该处注释是为了不覆盖该方法原来父类中的方法注释
    }
    
    /**
     * 
     * 用于处理文档解析开始事件，该方法一般不需要重写
     * 
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    public void startDocument() throws SAXException
    {
        // 用于处理文档解析开始事件，该方法一般不需要重写
    }
    
    /**
     * 用于处理文档解析结束事件，该方法一般不需要重写
     * 
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
        // 用于处理文档解析结束事件，该方法一般不需要重写
    }
    
    /**
     * getInfo<BR>
     * [功能详细描述]
     * 
     * @return BaseInfo
     * @see com.huawei.softclient.util.xml.DataHandler#getInfo()
     */
    public Object getInfo()
    {
        return baseInfo;
    }
    
    public Object getBaseInfo()
    {
        return baseInfo;
    }
    
    public void setBaseInfo(Object baseInfo)
    {
        this.baseInfo = baseInfo;
    }
}
