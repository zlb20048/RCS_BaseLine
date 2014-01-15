/*
 * 文件名: Request.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: admin
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.huawei.basic.android.im.utils.StringUtil;

/**
 * 发送请求时封装的请求对象
 */
public class Request
{
    
    /**
     * 
     * 请求方法类型<BR>
     */
    public enum RequestMethod
    {
        /**
         * get请求
         */
        GET,
        
        /**
         * post请求
         */
        POST,
        
        /**
         * PUT
         */
        PUT,
        
        /**
         * DELETE
         */
        DELETE
    }
    
    /**
     * 
     * 请求数据的格式<BR>
     */
    public enum ContentType
    {
        /**
         * 请求数据的格式为xml
         */
        XML,
        
        /**
         * 请求数据的格式为json
         */
        JSON
    }
    
    /**
     * 请求的url
     */
    private String url;
    
    /**
     * 发送时携带的请求体
     */
    private String body;
    
    /**
     * 请求类型，默认为get请求
     */
    private RequestMethod requestMethod = RequestMethod.GET;
    
    /**
     * 请求数据的格式，默认为xml格式封装
     */
    private ContentType contentType = ContentType.XML;
    
    /**
     * 请求附带的request property.
     */
    private List<NameValuePair> requestProperties;
    
    /**
     * need to set the response data's type to 'byte' instead of 'String'
     */
    private boolean needByte;
    
    /**
     * 获取请求url
     * 
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }
    
    /**
     * 设置请求url
     * 
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = StringUtil.fixUrl(url);
    }
    
    /**
     * 获取请求消息体
     * 
     * @return the body
     */
    public String getBody()
    {
        return body;
    }
    
    /**
     * 设置请求消息体
     * 
     * @param body the body to set
     */
    public void setBody(String body)
    {
        this.body = body;
    }
    
    /**
     * 获取request method
     * 
     * @return the requestMethod
     */
    public RequestMethod getRequestMethod()
    {
        return requestMethod;
    }
    
    /**
     * 设置request method
     * 
     * @param requestMethod the requestMethod to set
     */
    public void setRequestMethod(RequestMethod requestMethod)
    {
        this.requestMethod = requestMethod;
    }
    
    /**
     * 获取请求消息体的格式类型
     * 
     * @return the contentType
     */
    public ContentType getContentType()
    {
        return contentType;
    }
    
    /**
     * 设置请求消息体的格式类型
     * 
     * @param contentType the contentType to set
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }
    
    /**
     * 获取request property
     * 
     * @return the requestProperties
     */
    public List<NameValuePair> getRequestProperties()
    {
        return requestProperties;
    }
    
    /**
     * 设置request property
     * 
     * @param requestProperties the requestProperties to set
     */
    public void setRequestProperties(List<NameValuePair> requestProperties)
    {
        this.requestProperties = requestProperties;
    }
    
    /**
     * 
     * 添加一个Request Property
     * @param key the request header field to be set.
     * @param value the new value of the specified property.
     */
    public void addRequestProperty(String key, String value)
    {
        if (requestProperties == null)
        {
            requestProperties = new ArrayList<NameValuePair>();
        }
        if (key != null && value != null)
        {
            requestProperties.add(new BasicNameValuePair(key, value));
        }
    }
    
    /**
     * 
     * 该请求是否需要返回字节数组
     * @return the needBytes
     */
    public boolean isNeedByte()
    {
        return needByte;
    }
    
    /**
     * 设置该请求获取的数据类型是否为字节数组
     * @param needByte the needBytes to set
     */
    public void setNeedByte(boolean needByte)
    {
        this.needByte = needByte;
    }
    
}
