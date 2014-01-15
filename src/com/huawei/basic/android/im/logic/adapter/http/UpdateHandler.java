package com.huawei.basic.android.im.logic.adapter.http;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.logic.model.CheckUpdateInfoModel;

/**
 * 
 * [功能详细描述]
 * @author lidan
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public class UpdateHandler extends DefaultHandler
{
    /**
     * Tag
     */
    private static final String TAG = "UpdateHandler";
    
    /**
     * 解析类型标识符
     */
    private int mType;
    
    /**
     * 起始标签
     */
    private String startXml = "";
    
    /**
     * 存储解析内容的对象
     */
    private CheckUpdateInfoModel checkUpdateModel;
    
    /**
     * 
     * 空构造
     */
    public UpdateHandler()
    {
    }
    
    /**
     * 
     * 构造函数
     * 
     * @param mType http内容类型
     */
    public UpdateHandler(int mType)
    {
        this.mType = mType;
        checkUpdateModel = new CheckUpdateInfoModel();
    }
    
    /**
     * 
     * 获取解析对象 [功能详细描述]
     * 
     * @return CheckUpdateInfoModel
     */
    public CheckUpdateInfoModel getCheckUpdateModel()
    {
        return checkUpdateModel;
    }
    
    /**
     * 
     * set对象方法 [功能详细描述]
     * 
     * @param checkUpdateModel 检查升级获得的对象
     */
    public void setCheckUpdateModel(CheckUpdateInfoModel checkUpdateModel)
    {
        this.checkUpdateModel = checkUpdateModel;
    }
    
    /**
     * 
     * 文档开始 [功能详细描述]
     * 
     * @throws SAXException 异常
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException
    {
        
        super.startDocument();
    }
    
    /**
     * 
     * 标签开始 [功能详细描述]
     * 
     * @param uri uri
     * @param localName localName
     * @param qName qName
     * @param attributes attribute
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        Logger.d(TAG, "Begin startElement method mType=" + mType);
        startXml = localName;
        
        // if (mType == 5)
        // {
        // }
        // else
        if (mType == 6)
        {
            
            if (startXml.equals("component"))
            {
                checkUpdateModel.getChangeLog().setComponentName(attributes.getValue(0));
                checkUpdateModel.getChangeLog().setComponentNersion(attributes.getValue(1));
            }
            else if (startXml.equals("default-language"))
            {
                checkUpdateModel.getChangeLog().setDefaultLanguageName(attributes.getValue(0));
            }
            else if (startXml.equals("language"))
            {
                checkUpdateModel.getChangeLog().setLanguageName(attributes.getValue(0));
                checkUpdateModel.getChangeLog().setLanguageCode(attributes.getValue(1));
            }
        }
    }
    
    /**
     * 
     * 标签结束 [功能详细描述]
     * 
     * @param uri URI
     * @param localName 标签名
     * @param qName Q名
     * @throws SAXException SAX异常
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        startXml = null;
        return;
    }
    
    /**
     * 
     * 属性内容 [功能详细描述]
     * 
     * @param ch 字符数组
     * @param start 起始下标
     * @param length 结束下标
     * @throws SAXException SAX标签
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        
        if (startXml != null)
        {
            String data = new String(ch, start, length);
            switch (mType)
            {
                case 3:
                    if (startXml.equals("name"))
                    {
                        checkUpdateModel.getFilelist().setName(data);
                    }
                    else if (startXml.equals("compress"))
                    {
                        checkUpdateModel.getFilelist().setCompress(data);
                    }
                    else if (startXml.equals("spath"))
                    {
                        checkUpdateModel.getFilelist().setSpath(data);
                    }
                    else if (startXml.equals("dpath"))
                    {
                        checkUpdateModel.getFilelist().setDpath(data);
                    }
                    else if (startXml.equals("operation"))
                    {
                        checkUpdateModel.getFilelist().setOperation(data);
                    }
                    else if (startXml.equals("md5"))
                    {
                        checkUpdateModel.getFilelist().setMd5(data);
                    }
                    else if (startXml.equals("size"))
                    {
                        checkUpdateModel.getFilelist().setSize(data);
                    }
                    break;
                case 6:
                    if (startXml.equals("default-language"))
                    {
                        checkUpdateModel.getChangeLog().setDefaultLanguageCode(data);
                    }
                    else if (startXml.equals("feature"))
                    {
                        checkUpdateModel.getChangeLog().addFeatures(data);
                    }
                    
                    break;
                case 5:
                    if (startXml.equals("status"))
                    {
                        checkUpdateModel.setStatus(data);
                    }
                    else if (startXml.equals("name"))
                    {
                        checkUpdateModel.setName(data);
                    }
                    else if (startXml.equals("version"))
                    {
                        checkUpdateModel.setVersion(data);
                    }
                    else if (startXml.equals("versionID"))
                    {
                        checkUpdateModel.setVersionID(data);
                    }
                    else if (startXml.equals("description"))
                    {
                        checkUpdateModel.setDescription(data);
                    }
                    else if (startXml.equals("url"))
                    {
                        checkUpdateModel.setUrl(data);
                    }
                    else if (startXml.equals("forcedupdate"))
                    {
                        checkUpdateModel.setForceupdate(data);
                    }
                    break;
                default:
                    break;
            }
        }
        
    }
}
