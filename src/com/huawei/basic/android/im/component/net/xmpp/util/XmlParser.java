/*
 * 文件名: XmlParser.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 周庆龙
 * 创建时间:2011-11-6
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.xmpp.util;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * xml解析工具<BR>
 * 
 * @author 周庆龙
 * @version [RCS Client V100R001C03, 2011-10-12]
 */
public class XmlParser
{
    /**
     * TAG
     */
//    private static String TAG = "XmlParser";
    
    /**
     * 解析工具
     * <p>
     * simple xml 解析工具
     * </p>
     */
    private Serializer mSerializer = null;

    /**
     * 构造解析器
     */
    public XmlParser()
    {
        mSerializer = new Persister();
    }


    /**
     * 把字符串解析成对象<BR>
     * 
     * @param <T> 对象的class
     * @param type 对象类型
     * @param source 需要解析 的字符串
     * @return 解析后的对象
     * @throws Exception 需要解析的字符串不符合已定义的规范异常
     */
    public <T> T parseXmlString(Class<? extends T> type, String source)
        throws Exception
    {
        return mSerializer.read(type,
            source);
    }

    /**
     * 对象解析成xml串<BR>
     * 
     * @author 周庆龙
     * @param source 数据源
     * @return 解析后的串
     */
    public String getXmlString(Object source)
    {
        return source == null ? "" : source.toString();
    }
    
}
