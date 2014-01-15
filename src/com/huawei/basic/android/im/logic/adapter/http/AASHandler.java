/*
 * 文件名: AasParser.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨凡
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.logic.adapter.http;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.AASResult;
import com.huawei.basic.android.im.utils.DecodeUtil;

/**
 * 
 * 登录服务器返回内容解析器<BR>
 * [功能详细描述]
 * 
 * @author 杨凡
 * @version [RCS Client V100R001C03, Oct 20, 2011]
 */
public class AASHandler extends DefaultHandler
{
    private String strXmlTag = null;
    
    private AASResult aasResult;
    
    /**
     * 
     * [解密字符串]<BR>
     * [功能详细描述]
     * 
     * @param inStrToDecode 加密字段
     * @param inStrKey 解密Key
     * @return 解密字符串
     */
    public String doDecode(String inStrToDecode, String inStrKey)
    {
        return DecodeUtil.decryptAES(inStrToDecode,
                DecodeUtil.sha256Encode(inStrKey).substring(0, 16));
    }
    
    /**
     * 
     * [解析结果]<BR>
     * [功能详细描述]
     * 
     * @param inAasResult AAS结果
     * @param inStrToParse 返回值
     * @return 成功标记
     */
    public boolean doParse(AASResult inAasResult, String inStrToParse)
    {
        
        Logger.i("AASHandler", "aas result : \n" + inStrToParse);
        try
        {
            this.aasResult = inAasResult;
            StringReader read = new StringReader(inStrToParse);
            InputSource source = new InputSource(read);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(this);
            xr.parse(source);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * 
     * [XML开始]<BR>
     * [功能详细描述]
     * 
     * @throws SAXException XML异常
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    public void startDocument() throws SAXException
    {
    }
    
    /**
     * 
     * [XML结束]<BR>
     * [功能详细描述]
     * 
     * @throws SAXException XML异常
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
    }
    
    /**
     * 
     * [XML节点开始]<BR>
     * [功能详细描述]
     * 
     * @param namespaceURI 名空间
     * @param localName 名
     * @param qName 名
     * @param atts 属性
     * @throws SAXException XML异常
     */
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException
    {
        strXmlTag = localName;
    }
    
    /**
     * 
     * [XML节点结束]<BR>
     * [功能详细描述]
     * 
     * @param namespaceURI 名空间
     * @param localName 名
     * @param qName 名
     * @throws SAXException XML异常
     */
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException
    {
        strXmlTag = null;
    }
    
    /**
     * 
     * [XML值]<BR>
     * [功能详细描述]
     * 
     * @param ch 字符
     * @param start 起始
     * @param length 长度
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
    {
        if (strXmlTag != null)
        {
            String data = new String(ch, start, length);
            
            if (strXmlTag.equals("return"))
            {
                this.aasResult.setResult(data);
            }
            else if (strXmlTag.equals("desc"))
            {
                this.aasResult.setDesc(data);
            }
            else if (strXmlTag.equals("userid"))
            {
                this.aasResult.setUserSysId(data);
            }
            else if (strXmlTag.equals("loginid"))
            {
                this.aasResult.setLoginid(data);
            }
            else if (strXmlTag.equals("token"))
            {
                this.aasResult.setToken(data);
            }
            else if (strXmlTag.equals("expiretime"))
            {
                this.aasResult.setExpiretime(data);
            }
            else if (strXmlTag.equals("user"))
            {
                this.aasResult.setUserID(data);
            }
            else if (strXmlTag.equals("nduid"))
            {
                this.aasResult.setNduid(data);
            }
            else if (strXmlTag.equals("lang"))
            {
                this.aasResult.setLanguage(data);
            }
            else if (strXmlTag.equals("loginfirsttime"))
            {
                this.aasResult.setLoginfirsttime(data);
            }
            else if (strXmlTag.equals("cabsyncmlurl"))
            {
                this.aasResult.setCabsyncmlurl(data);
            }
            //            else if (strXmlTag.equals("portalurl"))
            //            {
            //                this.aasResult.setPortalurl(data);//TODO:服务器返回的portalurl不可用，缺少后面的“/Portal/servlet”
            //            }
            else if (strXmlTag.equals("xmppaddr"))
            {
                this.aasResult.setXmppaddr(data);
            }
            else if (strXmlTag.equals("rifurl"))
            {
                this.aasResult.setRifurl(data);
            }
            else if (strXmlTag.equals("boshurl"))
            {
                this.aasResult.setBoshurl(data);
            }
            else if (strXmlTag.equals("svnuser"))
            {
                this.aasResult.setSvnuser(data);
            }
            else if (strXmlTag.equals("svnpwd"))
            {
                this.aasResult.setSvnpwd(data);
            }
            else if (strXmlTag.equals("svnlist"))
            {
                this.aasResult.setSvnlist(data);
            }
            else if (strXmlTag.equals("cabgroupurl"))
            {
                this.aasResult.setCabgroupurl(data);
            }
            else if (strXmlTag.equals("liveupdateurl"))
            {
                this.aasResult.setLiveupdateurl(data);
            }
            else if (strXmlTag.equals("popup_mini_window"))
            {
                this.aasResult.setPopupminiwindow(data);
            }
            // 邮箱的地址
            else if (strXmlTag.equals("emailurl"))
            {
                aasResult.setEmailurl(data);
            }
            // 微博服务器的地址
            else if (strXmlTag.equals("mbplaturl"))
            {
                aasResult.setMbplaturl(data);
            }
        }
    }
}
