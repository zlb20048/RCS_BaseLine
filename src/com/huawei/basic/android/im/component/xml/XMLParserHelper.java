/*
 * 文件名: XMLParserHelper.java
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 
 * XML解析工具类<BR>
 * [功能详细描述]
 * @author 盛兴亚
 * @version [RCS Client V100R001C03, 2012-2-15]
 */
public final class XMLParserHelper
{
    /**
     * XML解析工具入口类
     * 
     * @param data 输入，包含XML报文
     * @param handler 相应的handler类，为SAXHandler类的子类
     * @return BaseInfo类的子类，调用处需要强转
     */
    public static Object xmlParser(final byte[] data, final DataHandler handler)
    {
        InputStream inputStream = null;
        inputStream = new ByteArrayInputStream(data);
        return fileXmlParser(inputStream, handler);
    }
    
    /**
     * 
     * XML解析本地文件工具
     * @param aStream 输入，包含XML报文
     * @param handler 相应的handler类，为SAXHandler类的子类
     * @return BaseInfo类的子类，调用处需要强转
     */
    public static Object fileXmlParser(InputStream aStream,
            final DataHandler handler)
    {
        SAXParserFactory saxParserfactory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        XMLReader xmlReader = null;
        InputStream inputStream = null;
        try
        {
            saxParser = saxParserfactory.newSAXParser();
            xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            InputStreamReader strInStream = new InputStreamReader(aStream,
                    "UTF-8");
            xmlReader.parse(new InputSource(strInStream));
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != inputStream)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return handler.getInfo();
        
    }
}
