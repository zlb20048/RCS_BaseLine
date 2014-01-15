/*
 * 文件名: CountryCodeHandler.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tlmao
 * 创建时间:Mar 20, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.huawei.basic.android.im.logic.model.CountryItemModel;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author tlmao
 * @version [RCS Client V100R001C03, Mar 20, 2012] 
 */
public class CountryCodeHandler extends DataHandler
{
    private CountryItemModel item;
    
    private List<CountryItemModel> itemList = new ArrayList<CountryItemModel>();
    
    private String mLocalName;
    
    /**
     * 开始解析某个XML片段的开始元素，子类需要重写该方法完成相应子类中info对象的解析
     * 
     * @param uri namespaceURI域名
     * @param localName 标签名
     * @param qName 标签的修饰前缀
     * @param attributes 标签所包含的属性列表
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        mLocalName = localName;
        if ("item".equals(mLocalName))
        {
            item = new CountryItemModel();
        }
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
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        String value = new String(ch, start, length);
        
        if (null == value || value.trim().length() == 0)
        {
            return;
        }
        
        if ("en".equals(mLocalName))
        {
            item.setEnName(value);
        }
        else if ("ch".equals(mLocalName))
        {
            item.setChName(value);
        }
        else if ("cc".equals(mLocalName))
        {
            item.setCountryCode(value);
        }
        
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
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        if ("item".equals(localName))
        {
            itemList.add(item);
            item = null;
        }
        
        mLocalName = "default";
    }
    
    /**
     * 
     * 获取列表
     * @return 国家码列表
     */
    public Object getResult()
    {
        return itemList;
    }
}
